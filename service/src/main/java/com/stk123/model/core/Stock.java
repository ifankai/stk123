package com.stk123.model.core;

import com.stk123.common.CommonConstant;
import com.stk123.entity.StkKlineEntity;
import com.stk123.model.Index;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Objects;

import static com.stk123.model.core.Stock.EnumCity.SH;
import static com.stk123.model.core.Stock.EnumCity.SZ;
import static com.stk123.model.core.Stock.EnumMarket.CN;
import static com.stk123.model.core.Stock.EnumMarket.HK;
import static com.stk123.model.core.Stock.EnumMarket.US;

@Data
public class Stock {

    public enum EnumMarket {
        CN(1), HK(3), US(2);

        private Integer market;

        EnumMarket(Integer market){
            this.market = market;
        }

        public Integer getMarket(){
            return this.market;
        }

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
            for(EnumMarket em : EnumMarket.values()){
                if(em.getMarket().intValue() == market.intValue()){
                    return em;
                }
            }
            return null;
        }
    }

    public enum EnumCity {
        SH, SZ
    }

    public enum EnumCate {
        STOCK(1), INDEX(2), FUND(3), INDEX_10jqka(4);

        private Integer cate;

        EnumCate(Integer cate){
            this.cate = cate;
        }

        public Integer getCate(){return this.cate;}

        public static EnumCate getCate(Integer cate) {
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
    private EnumCity city;// 1:sh, 2:sz
    private EnumMarket market;// 1:A股, 2:美股, 3:港股

    private BarSeries barSeries;

    public Stock() {}

    public Stock(String code, String name, BarSeries barSeries) {
        this.code = code;
        this.name = name;

        boolean isAllNumber = StringUtils.isNumeric(code);

        if(code.length() == 5 && isAllNumber){
            this.market = HK;
        }else{
            this.market = isAllNumber ? CN : US;
        }

        if(this.market == CN){
            if(code.length() == 8){//01000010 : sh 000010
                if(code.startsWith(CommonConstant.NUMBER_01)){
                    this.city = SH;
                }else{
                    this.city = SZ;
                }
            }else{
                this.city = getCity(code);
            }
        }
        this.barSeries = barSeries;
    }

    public Stock(String code, String name) {
        this(code, name, null);
    }

    public static EnumCity getCity(String code){
        if(code.startsWith(CommonConstant.NUMBER_SIX) || code.startsWith(CommonConstant.NUMBER_99)){
            return SH;
        }else{
            return SZ;
        }
    }
}
