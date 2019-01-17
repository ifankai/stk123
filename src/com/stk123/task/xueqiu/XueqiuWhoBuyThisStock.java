package com.stk123.task.xueqiu;

import java.util.List;

public class XueqiuWhoBuyThisStock {

    public static void main(String[] args) throws Exception {
        String code = "EDIT";
        List<Follower> followers = Utils.getFollowers(code, 1000);
        int i = 0;
        for (Follower f: followers ) {
            List<Portfolio> portfolios = Utils.getPortfolios(String.valueOf(f.getId()));
            for(Portfolio portfolio : portfolios){
                Stock stock = Utils.getPortfolioStockIfHas(portfolio.getSymbol(), code);
                if(stock != null){
                    System.out.print(f.getScreen_name()+"("+stock.getWeight()+"), ");
                    System.out.println("https://xueqiu.com/P/"+portfolio.getSymbol());
                }
                if(i++ % 6 == 0){
                    Thread.sleep(1000 * 20);
                }
            }
        }
    }


}
