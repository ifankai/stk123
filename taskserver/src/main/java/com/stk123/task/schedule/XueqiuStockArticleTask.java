package com.stk123.task.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.util.Arrays;
import com.stk123.common.util.ChineseUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.Text;
import com.stk123.model.constant.TextConstant;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockProjection;
import com.stk123.model.xueqiu.XueqiuPost;
import com.stk123.model.xueqiu.XueqiuPostRoot;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.XueqiuService;
import com.stk123.service.core.TextService;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XueqiuStockArticleTask extends AbstractTask {

    private static List<StockProjection> stocks;
    private static int codeIndex = 0;
    public static List<StkTextEntity> results = new ArrayList();
    private static Set<Long> ids = new HashSet<Long>();

    @Setter
    private String code;

    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private TextService textService;
    @Autowired
    private StkTextRepository stkTextRepository;

    @Override
    public void register() {
        this.runAnyway(this::execute);
    }

    public void execute() {
        try {
            if (code != null) {
                StockProjection stockProjection = stkRepository.findByCode(code);
                List<StkTextEntity> list = getArticles(stockProjection);
                return;
            }

            if (stocks == null) {
                List<StockProjection> list = stkRepository.findAllByMarketAndCateAndHotGreaterThan(
                        (List<Integer>) Arrays.toCollection(new int[]{Stock.EnumMarket.CN.getMarket(), Stock.EnumMarket.HK.getMarket()}),
                        Stock.EnumCate.STOCK.getCate(), 1000);
                List<StockProjection> stks = list.stream().filter(stk -> {
                    if (stk.getMarket() == Stock.EnumMarket.CN.getMarket() && stk.getHot() >= 3000 && !StringUtils.contains(stk.getName(), "ST")) {
                        return true;
                    } else if (stk.getMarket() == Stock.EnumMarket.HK.getMarket()) {
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                stocks = stks.stream().sorted(Comparator.comparing(e -> StringUtils.reverse(e.getCode()))).collect(Collectors.toList());
                if (new Date().getDate() % 2 == 0) {
                    Collections.reverse(stocks);
                }
            }

            if (codeIndex >= stocks.size()) {
                return;
            }

            for (int i = 0; i < 5 && codeIndex < stocks.size(); i++) {
                StockProjection stk = stocks.get(codeIndex++);
                log.info("XueqiuStockArticleTask=" + stk.getCode() + "[" + results.size() + "]");
                List<StkTextEntity> list = getArticles(stk);
                if (list.size() > 0) {
                    results.addAll(list);
                    if (results.size() >= 20) {
                        //EmailUtils.send("雪球个股长文", StringUtils.join(results, "<br><br>"));
                        results.clear();
                    }
                }
                //System.out.println(results);
                Thread.sleep(1000 * 12);
            }
            if (codeIndex >= stocks.size()) {
                if (results.size() > 0) {
                    //EmailUtils.send("雪球个股长文2", StringUtils.join(results, "<br><br>"));
                    results.clear();
                }
                stocks = null;
                codeIndex = 0;
                ids.clear();
                return;
            }
        } catch (Exception e) {
            log.error("XueqiuStockArticleTask", e);
        } finally {
            if (codeIndex >= stocks.size()) {
                if (results.size() > 0) {
                    //EmailUtils.send("雪球个股长文end", StringUtils.join(results, "<br><br>"));
                }
            }
        }
    }

    //https://xueqiu.com/statuses/search.json?count=10&comment=0&symbol=SH603611&hl=0&source=all&sort=alpha&page=1&_=1507209904103
    public List<StkTextEntity> getArticles(StockProjection stk) throws Exception {
        String code = stk.getCode();
        String name = stk.getName();
        Stock stock = Stock.build(stk);

        List<XueqiuPost> list = getList(stock);
        for (XueqiuPost post : list) {
            long createdAt = post.getCreated_at();
            if (ServiceUtils.addDay(new Date(), -2).getTime() <= createdAt && ServiceUtils.addDay(new Date(), 0).getTime() >= createdAt) {
                boolean flag = false;
                String title = post.getTitle();
                String text = post.getText();

                //post.getUser().getId() == 0 公告
                if (post.getUser().getId() == 0) {
                    flag = true;
                } else {
                    if (ChineseUtils.length(HtmlUtils.removeHTML(text)) < 100) {//内容长度控制，太短的排除掉，100=50个中文
                        continue;
                    }

                    if (StringUtils.length(text) > 300 && (text.contains(name) || text.contains(StringUtils.replace(name, " ", "")))) {
                        flag = true;
                    } else if (!StringUtils.isEmpty(title) && (title.contains(name) || title.contains(StringUtils.replace(name, " ", "")))) {
                        flag = true;
                    }
                    if (post.getUser().getFollowers_count() < 100) { //排除粉丝数小于100
                        continue;
                    }
                }

                if (flag) {
                    int replyCount = post.getReply_count();
                    if (replyCount >= 10) {

                        if (!ids.contains(post.getId())) {
                            if (stkTextRepository.existingByPostId(post.getId()) > 0) continue;

                            StkTextEntity stkTextEntity = new StkTextEntity();
                            stkTextEntity.setUserId(post.getUser_id());
                            stkTextEntity.setUserName(post.getUser().getScreen_name());
                            stkTextEntity.setCode(code);
                            stkTextEntity.setCreatedAt(new Date(post.getCreated_at()));
                            stkTextEntity.setPostId(post.getId());
                            stkTextEntity.setTitle(title);
                            stkTextEntity.setText(text);
                            stkTextEntity.setTextDesc(post.getDescription());
                            stkTextEntity.setType(TextConstant.TYPE_XUEQIU);
                            stkTextEntity.setSubType(TextConstant.SUB_TYPE_XUEQIU_DEFAUTL);
                            stkTextEntity.setInsertTime(new Date());
                            stkTextEntity.setCodeType(TextConstant.CODE_TYPE_STK);
                            stkTextEntity.setFollowersCount(post.getUser().getFollowers_count());
                            stkTextEntity.setReplyCount(post.getReply_count());
                            stkTextEntity.setUserAvatar(StringUtils.split(post.getUser().getProfile_image_url(), ",")[1]);
                            if(post.getUser().getId() == 0){
                                stkTextEntity.setSubType(Text.SUB_TYPE_XUEQIU_NOTICE);
                            }

                            results.add(stkTextEntity);
                            ids.add(post.getId());
                            //stkTextRepository.save(stkTextEntity);
                            try {
                                textService.save(stkTextEntity);
                            }catch (Exception e){
                                log.info(stkTextEntity);
                                log.error("",e);
                            }
                        }

                    }
                }
            }
        }

        return results;
    }

    public List<XueqiuPost> getList(Stock stock) throws Exception {
        String scode = stock.getCodeWithPlace();
        Map<String, String> requestHeaders = XueqiuService.getCookies();
        List<XueqiuPost> results = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            String url = "https://xueqiu.com/query/v1/symbol/search/status?count=100&comment=0&symbol=" + scode + "&hl=0&source=all&sort=&page=" + i + "&q=";
            String page = HttpUtils.get(url, null, requestHeaders, "gb2312");
            //		System.out.println(page);
            if ("400".equals(page)) {
                log.info("XueqiuStockArticleTask 400");
                return results;
            }

            boolean flag = false;
            ObjectMapper objectMapper = new ObjectMapper();
            XueqiuPostRoot xueqiuPostRoot = objectMapper.readValue(page, XueqiuPostRoot.class);
            for (XueqiuPost post : xueqiuPostRoot.getList()) {
                long createdAt = post.getCreated_at();
                if (ServiceUtils.addDay(new Date(), -2).getTime() <= createdAt) {
                    results.add(post);
                } else {
                    flag = true;
                    break;
                }
            }
            if (flag) break;
        }

        //热帖
        String url = "https://xueqiu.com/statuses/search.json?count=100&comment=0&symbol=" + scode + "&hl=0&source=all&sort=alpha&page=1&q=&type=11";
        String page = HttpUtils.get(url, null, requestHeaders, "gb2312");
        //		System.out.println(page);
        if ("400".equals(page)) {
            return results;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        XueqiuPostRoot xueqiuPostRoot = objectMapper.readValue(page, XueqiuPostRoot.class);
        for (XueqiuPost post : xueqiuPostRoot.getList()) {
            long createdAt = post.getCreated_at();
            if (ServiceUtils.addDay(new Date(), -10).getTime() <= createdAt) {
                results.add(post);
            }
        }

        return results;
    }
}
