package com.stk123.task.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.EmailUtils;
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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 实时查询巨潮资讯公告信息（http://www.cninfo.com.cn/new/commonUrl/pageOfSearch?url=disclosure/list/search&keywords=%E5%AD%A3%E5%BA%A6%E6%8A%A5%E5%91%8A#szse）
 * 然后再查询雪球这个公告的评价，对于包含积极词汇的公告及时发送email
 * 积极词汇：进步了，发展了
 */

@CommonsLog
@Service
@Setter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NoticeTask extends AbstractTask {

    private String triggerBymanual;

    /**
     * add方法在添加元素的时候，若超出了度列的长度会直接抛出异常
     * put方法，若向队尾添加元素的时候发现队列已经满了会发生阻塞一直等待空间，以加入元素。
     * offer方法在添加元素时，如果发现队列已满无法添加的话，会直接返回false。
     *
     * poll: 若队列为空，返回null。
     * remove:若队列为空，抛出NoSuchElementException异常。
     * take:若队列为空，发生阻塞，等待有元素。
     */
    public static LinkedBlockingQueue<Notice> NOTICES = new LinkedBlockingQueue<>();

    public static List<String> POSITIVE_WORDS = new ArrayList<>();

    static {
        //non-regex
        String words = "进步了,发展了,静候佳音,问题不大,数据好,福音,惊喜,期待,蜕变,成功,靠谱,厉害,很有诚意的" +
                "终于来了,牛逼,比较牛,利好,优秀,可喜可贺,稳步扩张,安心睡大觉,一步一个脚印,超预期,爆发,工作务实," +
                "稳妥,卓有成效,信赖,非常好,欣喜,有吸引力";
        POSITIVE_WORDS.addAll(Arrays.asList(StringUtils.split(words, ",")));

        //regex
        POSITIVE_WORDS.add("护城河很(深|宽)");
        POSITIVE_WORDS.add("(((?!不看好).)*)(\\b看好)");
        POSITIVE_WORDS.add("效率.{0,5}(可以|提升)");
        POSITIVE_WORDS.add("得到(证明|承认)");
        POSITIVE_WORDS.add("大股东.{0,4}看好公司");
        POSITIVE_WORDS.add("保持.{0,4}增长");
        POSITIVE_WORDS.add("上.{0,5}动力");
    }

    @Override
    public void register() {
        super.runByName("fetchNotice", this::fetchNotice);
        super.runByName("analyzeNotice", this::analyzeNotice);
    }

    //5分钟跑一次
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
                        NOTICES.put(notice);
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

    //持续跑
    public void analyzeNotice() {
        log.info("analyzeNotice.size="+NOTICES.size());
        List<Notice> noticeList = new ArrayList<>();
        Map<String, String> headerRequests = XueqiuService.getCookies();

        Iterator<Notice> it = NOTICES.iterator();
        while (it.hasNext()) {
            Notice notice = it.next();
            log.info("notice:"+notice.getCode());
            try{
                Date twoHoursBefore = CommonUtils.addHour(new Date(), -2);
                Date oneHoursBefore = CommonUtils.addHour(new Date(), -1);
                if(notice.getFetchDate().before(twoHoursBefore) || triggerBymanual != null){
                    if(notice.getXqCreateDate() != null && notice.getXqCreateDate().after(twoHoursBefore)
                            && notice.getXqFetchDate() != null && notice.getXqFetchDate().after(oneHoursBefore)){
                        continue;
                    }
                    notice.setXqFetchDate(new Date());

                    Stock stock = Stock.build(notice.getCode());
                    String scode = stock.getCodeWithPlace();
                    log.info("notice:"+scode);

                    int pageNum = 1;
                    int countPerPage = 100;
                    int totalReplyCount = 0;

                    do{
                        String url = "https://xueqiu.com/statuses/stock_timeline.json?symbol_id="+scode+"&count="+countPerPage+"&source=%E5%85%AC%E5%91%8A&page="+pageNum;
                        String json = HttpUtils.get(url,null,headerRequests, "GBK");
                        if("400".equals(json) || "404".equals(json)){
                            log.info("404:"+url);
                            log.info("[remove]404：" + scode);
                            NOTICES.remove(notice);
                            break;
                        }
                        Map root = JsonUtils.testJson(json);
                        List<Map> ns = (List)root.get("list");
                        boolean flag = false;
                        for(Map n : ns){
                            String createdAt = String.valueOf(n.get("created_at"));
                            Date date = new Date(Long.parseLong(createdAt));

                            if(date.before(ServiceUtils.addHour(new Date(), -24))){
                                flag = true;
                                Thread.sleep(12 * 1000);
                                break;
                            }

                            if(notice.getXqCreateDate() == null || notice.getXqCreateDate().after(date)) {
                                notice.setXqCreateDate(date);
                            }

                            int reply = Integer.parseInt(String.valueOf(n.get("reply_count")));
                            totalReplyCount += reply;
                            if(reply > 0){
                                int id = Integer.parseInt(String.valueOf(n.get("id")));
                                int page = 1;
                                int matchCount = 0;
                                do{
                                    //https://xueqiu.com/statuses/comments.json?id=179083274&count=20&page=1&reply=true&asc=false&type=status&split=true
                                    url = "https://xueqiu.com/statuses/comments.json?id="+id+"&count="+countPerPage+"&page="+page+"&reply=true&asc=false&type=status&split=true";
                                    json = HttpUtils.get(url,null,headerRequests, "GBK");
                                    if("400".equals(json) || "404".equals(json)){
                                        log.info("404:"+url);
                                        break;
                                    }

                                    root = JsonUtils.testJson(json);
                                    List<Map> comments = (List)root.get("comments");

                                    for(Map comment : comments){
                                        String text = String.valueOf(comment.get("text"));
                                        List<String> matches = CommonUtils.getMatchStrings(text, POSITIVE_WORDS.toArray(new String[0]));
                                        matchCount += matches.size();
                                    }

                                    if(comments.size() < countPerPage){
                                        break;
                                    }

                                    page++;
                                    //Thread.sleep(1000);
                                }while(true);

                                if(matchCount >= 3){
                                    notice.setXqUrl("https://xueqiu.com/S/"+scode+"/"+id);
                                    notice.setXqTitle(String.valueOf(n.get("description")));
                                    //noticeList.add(notice);
                                    log.info("[remove]有积极评论发邮件：" + scode);
                                    NOTICES.remove(notice);
                                    EmailUtils.send("[公告]"+notice.getXqTitle(), scode + CommonUtils.wrapLink(notice.getXqTitle(), notice.getXqUrl()));
                                    continue;
                                }
                            }
                        }
                        if(flag)break;
                        if(pageNum++ >= 3)break;

                        Thread.sleep(12 * 1000);
                    }while(true);


                    if(notice.getXqCreateDate() != null) {

                        if(totalReplyCount > 20){
                            log.info("[remove]评论数大于20条任然没有积极评论：" + scode);
                            NOTICES.remove(notice);
                            continue;
                        }

                        if(stock.isMarketCN()) {
                            if (totalReplyCount <= 0 &&
                                    notice.getXqCreateDate().before(CommonUtils.addHour(new Date(), -2))) {
                                log.info("[remove]2小时内没有任何评论：" + scode);
                                NOTICES.remove(notice);
                                continue;
                            }

                            if (notice.getXqCreateDate().before(CommonUtils.addHour(new Date(), -3)) &&
                                    totalReplyCount <= 5) {
                                log.info("[remove]3小时内评论数小于5条：" + scode);
                                NOTICES.remove(notice);
                                continue;
                            }

                            if (notice.getXqCreateDate().before(CommonUtils.addHour(new Date(), -5))) {
                                log.info("[remove]5小时后删除：" + scode);
                                NOTICES.remove(notice);
                                continue;
                            }
                        }
                        if(stock.isMarketHK()) {
                            if (totalReplyCount <= 0 &&
                                    notice.getXqCreateDate().before(CommonUtils.addHour(new Date(), -18))) {
                                log.info("[remove]18小时内没有任何评论：" + scode);
                                NOTICES.remove(notice);
                                continue;
                            }

                            if (notice.getXqCreateDate().before(CommonUtils.addHour(new Date(), -24))) {
                                log.info("[remove]24小时后删除：" + scode);
                                NOTICES.remove(notice);
                                continue;
                            }
                        }
                    }

                    if(notice.getFetchDate().before(CommonUtils.addHour(new Date(), -36))){
                        log.info("[remove]抓取36小时后删除：" + scode);
                        NOTICES.remove(notice);
                        continue;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("NoticeTask.analyzeNotice error:", e);
            }
        }
        log.info("analyzeNotice.size="+NOTICES.size());
    }

    public static void main(String[] args) {
        String s = "最期待胰腺癌 看好的数据, 看公司语气，会有惊喜！[大笑],双抗护城河很深很宽, 不看好哈。效率都是可以的";
        for(String reg : POSITIVE_WORDS){
            String str = CommonUtils.getMatchString(s, reg);
            System.out.println(reg+","+str);
        }
        List<String> matches = CommonUtils.getMatchStrings(s, POSITIVE_WORDS.stream().toArray(String[]::new));
        System.out.println(matches.size());

        //new NoticeTask().fetchNotice();
    }
}

@Getter
@Setter
class Notice {
    private String code;
    private Date fetchDate;

    private Date xqFetchDate;
    private Date xqCreateDate;
    private String xqUrl;
    private String xqTitle;

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