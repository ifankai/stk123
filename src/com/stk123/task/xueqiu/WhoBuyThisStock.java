package com.stk123.task.xueqiu;

import org.h2.util.StringUtils;

import java.util.List;

public class WhoBuyThisStock {

    public static void main(String[] args) throws Exception {
        String code = "MDGL";
        String market = "us";
        List<Follower> followers = Utils.getFollowers(code, 3000);
        int i = 0;
        for (Follower f: followers ) {
            List<Portfolio> portfolios = Utils.getPortfolios(String.valueOf(f.getId()));
            for(Portfolio portfolio : portfolios){
                try {
                    if(StringUtils.equals(portfolio.getMarket(), market)) {
                        Stock stock = Utils.getPortfolioStockIfHas(portfolio.getSymbol(), code);
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
