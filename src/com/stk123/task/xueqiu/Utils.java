package com.stk123.task.schedule.xueqiu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.stk123.task.XueqiuUtils;
import com.stk123.tool.util.HtmlUtils;
import com.stk123.tool.util.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static List<Follower> getFollowers(String code, int gtFollowerCount) throws Exception {
        return Utils.getFollowers(code, gtFollowerCount, -1);
    }

    public static List<Follower> getFollowers(String code, int gtFollowerCount, int returnCount) throws Exception {
        String url = "https://xueqiu.com/S/"+code+"/follows?page=";
        int page = 1;
        List<Follower> result = new ArrayList<Follower>();
        while(true){
            String html = HttpUtils.get(url + (page++), null);
            String follows = StringUtils.substringBetween(html, "var follows={", "};");
            JSONObject jsonObject = JSON.parseObject("{"+follows+"}");
            List<Follower> followers = JSONObject.parseArray(jsonObject.getString("followers"), Follower.class);
            if(followers.isEmpty()){
                break;
            }
            for(Follower follower : followers){
                if(follower.getFollowers_count() >= gtFollowerCount){
                    result.add(follower);
                    if(returnCount > 0 && result.size() >= returnCount){
                        break;
                    }
                }
            }
            if(returnCount > 0 && result.size() >= returnCount){
                break;
            }
        }
        return result;
    }

    public static List<Portfolio> getPortfolios(String uid) throws Exception {
        String url = "https://stock.xueqiu.com/v5/stock/portfolio/stock/list.json?size=1000&category=3&uid="+uid+"&pid=-24";
        Map<String, String> requestHeaders = XueqiuUtils.getCookies();
        String html = HttpUtils.get(url,null, requestHeaders,"UTF-8");
        JSONObject jsonObject = JSON.parseObject(html);
        List<Portfolio> portfolios = JSONObject.parseArray(jsonObject.getJSONObject("data").getString("stocks"), Portfolio.class);

        //exclude closed portfolios
        List<String> codes = new ArrayList<String>();
        for(Portfolio portfolio : portfolios){
            codes.add(portfolio.getSymbol());
        }
        url = "https://xueqiu.com/cubes/quote.json?code="+StringUtils.join(codes,",");
        html = HttpUtils.get(url,null, requestHeaders,"UTF-8");
        JSONObject jsonObject2 = JSON.parseObject(html);
        List<Portfolio> result = new ArrayList<Portfolio>();
        for(Portfolio portfolio : portfolios){
            JSONObject symbol = jsonObject2.getJSONObject(portfolio.getSymbol());
            String closeAt = symbol.getString("closed_at");
            String market = symbol.getString("market");
            if(StringUtils.isEmpty(closeAt)){
                portfolio.setMarket(market);
                result.add(portfolio);
            }
        }
        return result;
    }

    public static Stock getPortfolioStockIfHas(String symbol, String code) throws Exception {
        List<Stock> stocks = getPortfolioStocks(symbol);
        for(Stock stock : stocks){
            if(StringUtils.contains(stock.getCode(), code)){
                return stock;
            }
        }
        return null;
    }

    public static List<Stock> getPortfolioStocks(String symbol) throws Exception {
        String url = "https://xueqiu.com/P/"+symbol;
        List<Stock> result = new ArrayList<Stock>();
        String html = HttpUtils.get(url,"UTF-8");
        if(StringUtils.contains(html,"cube-closed.png")){
            //System.out.println("已关停");
            return result;
        }
        Node div = HtmlUtils.getNodeByAttribute(html, null, "class", "weight-list");
        List<Node> stocks = HtmlUtils.getNodeListByTagNameAndAttribute(div,"a","class", "stock fn-clear no-tooltip");
        for(Node stock : stocks){
            if(stock == null)continue;
            Node name = HtmlUtils.getNodeByAttribute(stock, null,"class", "name");
            Node code = HtmlUtils.getNodeByAttribute(stock, null,"class", "price");
            Node weight = HtmlUtils.getNodeByAttribute(stock, null,"class", "stock-weight weight");
            Stock s = new Stock();
            s.setCode(code.toPlainTextString());
            s.setName(name.toPlainTextString());
            s.setWeight(weight.toPlainTextString());
            result.add(s);
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        List<Portfolio> portfolios = getPortfolios("9518372158");
        for (Portfolio portfolio : portfolios){
            System.out.println(portfolio.getName());
        }
    }
}
