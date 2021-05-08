package com.stk123.task.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.CommonUtils;
import com.stk123.model.dto.Cninfo;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
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

/**
 * 实时查询巨潮资讯公告信息（http://www.cninfo.com.cn/new/commonUrl/pageOfSearch?url=disclosure/list/search&keywords=%E5%AD%A3%E5%BA%A6%E6%8A%A5%E5%91%8A#szse）
 * 然后再查询雪球这个公告的评价，对于包含积极词汇的公告及时发送email
 * 积极词汇：进步了，发展了
 */

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NoticeTask extends AbstractTask {

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
        super.runAnyway(this::execute);
    }

    public void execute() {
        try {
            Path path = Paths.get("./notice_cninfo_code.txt");
            if(!Files.exists(path)){
                Files.createFile(path);
            }
            String lastCode = new String(Files.readAllBytes(path));

            int pageNum = 1;
            boolean stopFlag = false;
            Set<String> codes = new LinkedHashSet<>();
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
                    codes.add(item.getSecCode());
                    i++;
                }
                if(stopFlag){
                    break;
                }

                pageNum++;
            }
            log.info("codes.size="+codes.size());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("NoticeTask error:", e);
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

        new NoticeTask().execute();
    }
}
