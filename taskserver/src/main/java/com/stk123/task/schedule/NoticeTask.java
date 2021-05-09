package com.stk123.task.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.JsonUtils;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.Cninfo;
import com.stk123.service.XueqiuService;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.python.apache.xerces.dom.PSVIAttrNSImpl;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 实时查询巨潮资讯公告信息（http://www.cninfo.com.cn/new/commonUrl/pageOfSearch?url=disclosure/list/search&keywords=%E5%AD%A3%E5%BA%A6%E6%8A%A5%E5%91%8A#szse）
 * 然后再查询雪球这个公告的评价，对于包含积极词汇的公告及时发送email
 * 积极词汇：进步了，发展了
 */

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NoticeTask extends AbstractTask {

    public static List<Notice> NOTICES = Collections.synchronizedList(new LinkedList<>());

    public static List<String> POSITIVE_WORDS = new ArrayList<>();

    static {
        //non-regex
        String words = "进步了,发展了,静候佳音,应该问题不大,得到证明,数据好,福音,惊喜,期待";
        POSITIVE_WORDS.addAll(Arrays.asList(StringUtils.split(words, ",")));

        //regex
        POSITIVE_WORDS.add("护城河很(深|宽)");
        POSITIVE_WORDS.add("(((?!不看好).)*)(\\b看好)");
    }

    @Override
    public void register() {
        super.runByName("fetchNotice", this::fetchNotice);
        super.runByName("analyzeNotice", this::analyzeNotice);
    }

    public void fetchNotice() {
        try {
            Path path = Paths.get("./notice_cninfo_code.txt");
            if(!Files.exists(path)){
                Files.createFile(path);
            }
            String lastCode = new String(Files.readAllBytes(path));

            int pageNum = 1;
            boolean stopFlag = false;
            int i = 0;

            String category = "category_gddh_szsh;category_qyfpxzcs_szsh;category_yjdbg_szsh;category_bndbg_szsh;category_yjygjxz_szsh;category_ndbg_szsh;category_sjdbg_szsh;category_gqjl_szsh;category_zf_szsh;category_jj_szsh;category_pg_szsh;category_gqbd_szsh;category_kzzq_szsh";

            while(true) {
                String body = "pageNum="+pageNum+"&pageSize=30&column=szse&tabName=fulltext&plate=&stock=&searchkey=&secid=&category="+category+"&trade=&seDate=&sortName=time&sortType=desc&isHLtitle=true";
                String page = HttpUtils.post("http://www.cninfo.com.cn/new/hisAnnouncement/query", body, "UTF-8");
                //log.info(page);
                if ("404".equals(page)) {
                    return;
                }
                ObjectMapper mapper = new ObjectMapper();
                Cninfo.NoticeRoot root = mapper.readValue(page, Cninfo.NoticeRoot.class);

                Date DateBefore = ServiceUtils.addDay(new Date(), -1);

                for (Cninfo.Announcement item : root.getAnnouncements()) {
                    Date createDate = new Date(item.getAnnouncementTime());
                    if (createDate.before(DateBefore) || StringUtils.equals(lastCode, item.getSecCode())) {
                        stopFlag = true;
                        break;
                    }
                    //System.out.println(item);
                    if(i == 0){
                        Files.write(path, item.getSecCode().getBytes());
                    }

                    long cnt = NOTICES.stream().filter(notice -> notice.getCode().equals(item.getSecCode())).count();
                    if(cnt == 0){
                        Notice notice = new Notice();
                        notice.setCode(item.getSecCode());
                        notice.setFetchDate(new Date());
                        NOTICES.add(notice);
                    }

                    i++;
                }
                if(stopFlag){
                    break;
                }

                pageNum++;
            }
            log.info("NOTICES.size="+NOTICES.size());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("NoticeTask.fetchNotice error:", e);
        }
    }

    public void analyzeNotice() {
        for(Notice notice : NOTICES){
            try{
                Date twoHoursBefore = CommonUtils.addMinute(new Date(), -120);
                if(notice.getFetchDate().before(twoHoursBefore)){
                    Stock stock = Stock.build(notice.getCode());
                    String scode = stock.getCodeWithPlace();
                    Map<String, String> headerRequests = XueqiuService.getCookies();

                    List<Map> notices = new ArrayList<Map>();
                    Date now = new Date();
                    int pageNum = 1;
                    boolean clearCookie = false;
                    do{
                        String page = HttpUtils.get("https://xueqiu.com/statuses/stock_timeline.json?symbol_id="+scode+"&count=50&source=%E5%85%AC%E5%91%8A&page="+pageNum,null,headerRequests, "GBK");
                        if("400".equals(page) || "404".equals(page)){
                            if(!clearCookie){
                                XueqiuService.clearCookie();
                                clearCookie = true;
                                continue;
                            }
                            break;
                        }
                        Map m = JsonUtils.testJson(page);
                        List<Map> list = (List)m.get("list");
                        boolean flag = false;
                        for(Map n : list){
                            String createdAt = String.valueOf(n.get("created_at"));
                            Date date = new Date(Long.parseLong(createdAt));
                            if(date.before(ServiceUtils.addDay(now, -2))){
                                flag = true;
                                break;
                            }

                            int reply = Integer.parseInt(String.valueOf(n.get("reply_count")));
                            if(reply > 0){
                                int id = Integer.parseInt(String.valueOf(n.get("id")));

                                Map map = new HashMap();
                                map.put("url", "https://xueqiu.com"+n.get("target"));
                                map.put("count", reply);
                                map.put("createtime", ServiceUtils.formatDate(date));
                                map.put("description", n.get("description"));
                                //System.out.println(n.get("description"));
                                notices.add(map);
                            }
                        }
                        if(flag){
                            break;
                        }
                        if(pageNum++ >= 10)break;
                    }while(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("NoticeTask.analyzeNotice error:", e);
            }
        }
    }

    public static void main(String[] args) {
        String s = "最期待胰腺癌 看好的数据, 看公司语气，会有惊喜！[大笑],双抗护城河很深很宽, 不看好哈。";
        for(String reg : POSITIVE_WORDS){
            String str = CommonUtils.getMatchString(s, reg);
            System.out.println(reg+","+str);
        }
        List<String> matches = CommonUtils.getMatchStrings(s, POSITIVE_WORDS.stream().toArray(String[]::new));
        System.out.println(matches.size());

        new NoticeTask().fetchNotice();
    }
}

@Getter
@Setter
class Notice {
    private String code;
    private Date fetchDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notice notice = (Notice) o;
        return Objects.equals(code, notice.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}