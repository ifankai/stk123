package com.stk123.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
public enum EnumMarket {
    CN(1), HK(3), US(2);

    EnumMarket(Integer market){
        this.market = market;
    }
    @Getter
    private Integer market;

    private String klineTable;

    public String getKlineTable(){
        return klineTable == null ? this.klineTable = this.select("stk_kline", "stk_kline_hk", "stk_kline_us") : klineTable;
    }

    public boolean isCN(){
        return this.market.equals(CN.market);
    }

    /**
     * @param name 1|2|3|cn|us|hk
     */
    public static EnumMarket getMarket(String name){
        for(EnumMarket em : EnumMarket.values()){
            if(em.name().equalsIgnoreCase(name) || name.equals(em.getMarket().toString())){
                return em;
            }
        }
        return null;
    }
    public static EnumMarket getMarket(Integer market){
        if(market == null) return null;
        for(EnumMarket em : EnumMarket.values()){
            if(em.getMarket().intValue() == market.intValue()){
                return em;
            }
        }
        return null;
    }
    public <T> T select(T cn, T hk, T us){
        switch (this){
            case CN:
                return cn;
            case HK:
                return hk;
            case US:
                return us;
            default:
                return null;
        }
    }
    public String replaceKlineTable(String str){
        return StringUtils.replace(str, "stk_kline", this.getKlineTable());
    }
}
