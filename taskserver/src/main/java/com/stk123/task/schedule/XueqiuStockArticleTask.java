package com.stk123.task.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.Arrays;
import com.stk123.common.util.ChineseUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.constant.TextConstant;
import com.stk123.model.core.Stock;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XueqiuStockArticleTask extends AbstractTask {

    private static List<StockProjection> stocks;

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
        this.run(this::execute);
    }

    public void execute() {
        try {
            if (code != null) {
                StockProjection stockProjection = stkRepository.getByCode(code);
                List<StkTextEntity> list = getArticles(stockProjection);
                return;
            }

            if(stocks == null){
                stocks = getStocks();
            }

            int codeIndex = 0;
            Path path = Paths.get("./temp/task_xueqiu_article.txt");
            if(!Files.exists(path)){
                Files.createFile(path);
            }else {
                String idx = new String(Files.readAllBytes(path));
                codeIndex = Integer.parseInt(idx);
            }

            if (codeIndex >= stocks.size()) {
                stocks = getStocks();
                codeIndex = 0;
            }
            Files.write(path, String.valueOf(codeIndex+5).getBytes());

            for (int i = 0; i < 5 && codeIndex < stocks.size(); i++) {
                StockProjection stk = stocks.get(codeIndex++);
                List<StkTextEntity> list = getArticles(stk);
                log.info("XueqiuStockArticleTask=" + stk.getCode() + "[" + list.size() + "]");
                Thread.sleep(1000 * 12);
            }

        } catch (Exception e) {
            log.error("XueqiuStockArticleTask", e);
        }
    }

    public synchronized List<StockProjection> getStocks(){
        List<StockProjection> list = stkRepository.findAllByMarketAndCateAndHotGreaterThan(
                (List<Integer>) Arrays.toCollection(new int[]{EnumMarket.CN.getMarket(), EnumMarket.HK.getMarket()}),
                EnumCate.STOCK.getCate(), 1000);
        List<StockProjection> stks = list.stream().filter(stk -> {
            if (stk.getMarket() == EnumMarket.CN.getMarket() && stk.getHot() >= 3000 && !StringUtils.contains(stk.getName(), "ST")) {
                return true;
            } else if (stk.getMarket() == EnumMarket.HK.getMarket()) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return stks.stream().sorted(Comparator.comparing(e -> StringUtils.reverse(e.getCode()))).collect(Collectors.toList());
    }

    //https://xueqiu.com/statuses/search.json?count=10&comment=0&symbol=SH603611&hl=0&source=all&sort=alpha&page=1&_=1507209904103
    public List<StkTextEntity> getArticles(StockProjection stk) throws Exception {
        String code = stk.getCode();
        String name = stk.getName();
        Stock stock = Stock.build(stk);
        List<StkTextEntity> results = new ArrayList<>();

        List<XueqiuPost> list = getList(stock);
        for (XueqiuPost post : list) {
            long createdAt = post.getCreated_at();
            if (ServiceUtils.addDay(new Date(), -2).getTime() <= createdAt && ServiceUtils.addDay(new Date(), 1).getTime() >= createdAt) {
                boolean flag = false;
                String title = post.getTitle();
                String text = post.getText();

                //post.getUser().getId() == 0 公告
                boolean isNotice = false;
                if (post.getUser().getId() == 0) {
                    flag = true;
                    isNotice = true;
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
                    StkTextEntity stkTextEntity = null;

                    if(isNotice){
                        if(replyCount >= 5){
                            stkTextEntity = save(code, post);
                        }

                    }else{
                        if(replyCount >=5 && replyCount < 10){
                            if(post.getLike_count() >= 10){
                                stkTextEntity = save(code, post);
                            }else {
                                Date createAt = CommonUtils.addDay(new Date(), -30); //30天內都沒有评论
                                int cnt = stkTextRepository.countAllByCodeAndCreatedAtGreaterThanEqual(code, createAt);
                                if (cnt == 0) {
                                    stkTextEntity = save(code, post);
                                }
                            }
                        }
                        else if(replyCount >= 10){
                            stkTextEntity = save(code, post);
                        }
                    }

                    if(stkTextEntity != null){
                        results.add(stkTextEntity);
                    }

                }
            }
        }

        return results;
    }

    public StkTextEntity save(String code, XueqiuPost post) {
        if (stkTextRepository.existsByCodeAndPostId(code, post.getId())){
            return null;
        }
        StkTextEntity stkTextEntity = new StkTextEntity();
        stkTextEntity.setUserId(post.getUser_id());
        stkTextEntity.setUserName(post.getUser().getScreen_name());
        stkTextEntity.setCode(code);
        stkTextEntity.setCreatedAt(new Date(post.getCreated_at()));
        stkTextEntity.setPostId(post.getId());
        stkTextEntity.setTitle(post.getTitle());
        stkTextEntity.setText(post.getText());
        stkTextEntity.setTextDesc(post.getDescription());
        stkTextEntity.setType(TextConstant.TYPE_XUEQIU);
        stkTextEntity.setInsertTime(new Date());
        stkTextEntity.setCodeType(TextConstant.CODE_TYPE_STK);
        stkTextEntity.setFollowersCount(post.getUser().getFollowers_count());
        stkTextEntity.setReplyCount(post.getReply_count());
        stkTextEntity.setUserAvatar(StringUtils.split(post.getUser().getProfile_image_url(), ",")[1]);
        if(post.getUser().getId() == 0){
            stkTextEntity.setSubType(TextConstant.SUB_TYPE_XUEQIU_NOTICE);
        }else{
            stkTextEntity.setSubType(TextConstant.SUB_TYPE_XUEQIU_DEFAUTL);
        }

        return textService.save(stkTextEntity);
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
        //System.out.println(page);
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
