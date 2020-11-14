package com.stk123.task.schedule.xueqiu;


import com.stk123.model.xueqiu.Follower;
import com.stk123.model.xueqiu.Portfolio;
import com.stk123.model.xueqiu.Stock;
import com.stk123.service.XueqiuService;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class WhoBuyThisStock {

    public static void main(String[] args) throws Exception {
        String code = "MDGL";
        String market = "us";
        List<Follower> followers = XueqiuService.getFollowers(code, 3000);
        int i = 0;
        for (Follower f: followers ) {
            List<Portfolio> portfolios = XueqiuService.getPortfolios(String.valueOf(f.getId()));
            for(Portfolio portfolio : portfolios){
                try {
                    if(StringUtils.equals(portfolio.getMarket(), market)) {
                        Stock stock = XueqiuService.getPortfolioStockIfHas(portfolio.getSymbol(), code);
                        if (stock != null) {
                            System.out.print(f.getScreen_name() + "(" + stock.getWeight() + "), ");
                            System.out.println("https://xueqiu.com/P/" + portfolio.getSymbol());
                        }
                        if (i++ % 8 == 0) {
                            Thread.sleep(1000 * 20);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


}
