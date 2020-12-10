package com.stk123.model.core;

import com.stk123.common.CommonConstant;
import com.stk123.model.projection.StockBasicProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import static com.stk123.model.core.Stock.EnumPlace.SH;
import static com.stk123.model.core.Stock.EnumPlace.SZ;
import static com.stk123.model.core.Stock.EnumMarket.CN;
import static com.stk123.model.core.Stock.EnumMarket.HK;
import static com.stk123.model.core.Stock.EnumMarket.US;

@Data
public class Stock {

    @AllArgsConstructor
    public enum EnumMarket {
        CN(1), HK(3), US(2);

        @Getter
        private Integer market;

        /**
         * @param name 1|2|3|cn|us|hk
         * @return
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
    }

    @AllArgsConstructor
    public enum EnumPlace {
        SH(1), SZ(2);

        @Getter
        private Integer place;

        public static EnumPlace getPlace(Integer place) {
            if(place == null) return null;
            for(EnumPlace em : EnumPlace.values()){
                if(em.getPlace().intValue() == place.intValue()){
                    return em;
                }
            }
            return null;
        }
    }

    @AllArgsConstructor
    public enum EnumCate {
        STOCK(1), INDEX(2), FUND(3), INDEX_10jqka(4);

        @Getter
        private Integer cate;

        public static EnumCate getCate(Integer cate) {
            if(cate == null) return null;
            for(EnumCate em : EnumCate.values()){
                if(em.getCate().intValue() == cate.intValue()){
                    return em;
                }
            }
            return null;
        }
    }

    private String code;
    private String name;
    private EnumPlace place;// 1:sh, 2:sz
    private EnumMarket market;// 1:A股, 2:美股, 3:港股
    private EnumCate cate;

    private BarSeries barSeries;

    public Stock() {}

    public Stock(StockBasicProjection stockBasicProjection) {
        this.code = stockBasicProjection.getCode();
        this.name = stockBasicProjection.getName();
        this.market = EnumMarket.getMarket(stockBasicProjection.getMarket());
        this.cate = EnumCate.getCate(stockBasicProjection.getCate());
        this.place = EnumPlace.getPlace(stockBasicProjection.getPlace());
        if(this.market == EnumMarket.CN && this.place == null){
            setPlace();
        }
    }

    public Stock(String code, String name){
        this(code, name, null);
    }

    public Stock(String code, String name, BarSeries barSeries) {
        this.code = code;
        this.name = name;

        boolean isAllNumber = StringUtils.isNumeric(code);

        if(code.length() == 5 && isAllNumber){
            this.market = HK;
        }else{
            this.market = isAllNumber ? CN : US;
        }

        setPlace();
        this.barSeries = barSeries;
    }

    /**
     * @return SH600000
     */
    public String getCodeWithPlace(){
        return this.place == null ? this.code : this.place.name() + this.code;
    }

    public boolean isMarketCN(){
        return this.market == EnumMarket.CN;
    }
    public boolean isMarketUS(){
        return this.market == EnumMarket.US;
    }
    public boolean isMarketHK(){
        return this.market == EnumMarket.HK;
    }
    public boolean isPlaceSH(){
        return this.place == EnumPlace.SH;
    }

    void setPlace(){
        if(this.market == CN){
            if(this.code.length() == 8){//01000010 : sh 000010
                if(this.code.startsWith(CommonConstant.NUMBER_01)){
                    this.place = SH;
                }else{
                    this.place = SZ;
                }
            }else{
                this.place = getCity(code);
            }
        }
    }


    public static EnumPlace getCity(String code){
        if(code.startsWith(CommonConstant.NUMBER_SIX) || code.startsWith(CommonConstant.NUMBER_99)){
            return SH;
        }else{
            return SZ;
        }
    }

    public Stock buildBarSeries(BarSeries barSeries) {
        this.barSeries = barSeries;
        return this;
    }

    @Override
    public int hashCode(){
        return this.code.hashCode();
    }
}
