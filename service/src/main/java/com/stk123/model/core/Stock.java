package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.CommonConstant;
import com.stk123.model.json.View;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.util.ServiceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.stk123.model.core.Stock.EnumPlace.SH;
import static com.stk123.model.core.Stock.EnumPlace.SZ;
import static com.stk123.model.core.Stock.EnumMarket.CN;
import static com.stk123.model.core.Stock.EnumMarket.HK;
import static com.stk123.model.core.Stock.EnumMarket.US;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @JsonView(View.Default.class)
    private String code;

    @JsonView(View.Default.class)
    private String name;

    @JsonView(View.Default.class)
    private EnumPlace place;// 1:sh, 2:sz

    @JsonView(View.Default.class)
    private EnumMarket market;// 1:A股, 2:美股, 3:港股

    @JsonView(View.Default.class)
    private EnumCate cate;

    private BarSeries barSeries;
    private BarSeries barSeriesWeek;
    private BarSeries barSeriesMonth;

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

    public Stock(String code, String name, EnumMarket market, EnumPlace place){
        this.code = code;
        this.name = name;
        this.market = market;
        this.place = place;
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
    public Stock buildBarSeries(BarSeries barSeries, boolean buildWeek, boolean buildMonth) {
        this.barSeries = barSeries;
        if(buildWeek)
            this.buildBarSeriesWeek();
        if(buildMonth)
            this.buildBarSeriesMonth();
        return this;
    }

    public Stock buildBarSeriesWeek() {
        if(this.barSeriesWeek == null) {
            this.barSeriesWeek = new BarSeries(false);
            Date a = null;
            Bar kw = null;
            for(Bar k : this.barSeries.getList()){
                Date kd = ServiceUtils.parseDate(k.getDate());
                Date monday = ((Calendar)DateUtils.iterator(kd, DateUtils.RANGE_WEEK_MONDAY).next()).getTime();
                if(a == null || monday.compareTo(a) != 0){
                    if(kw != null){
                        this.barSeriesWeek.add(kw);
                    }
                    kw = new Bar();
                    kw.setCode(k.getCode());
                    kw.setDate(k.getDate());
                    kw.setClose(k.getClose());
                    kw.setHigh(k.getHigh());
                    kw.setLow(k.getLow());
                    kw.setVolume(k.getVolume());
                    kw.setAmount(k.getAmount());
                }else{
                    kw.setOpen(k.getOpen());
                    kw.setHigh(Math.max(k.getHigh(), kw.getHigh()));
                    kw.setLow(Math.min(k.getLow(), kw.getLow()));
                    kw.setVolume(kw.getVolume()+k.getVolume());
                    kw.setAmount(kw.getAmount()+k.getAmount());
                }
                a = monday;
            }
            int i = 0;
            List<Bar> bars = this.barSeriesWeek.getList();
            for(Bar k : bars){
                if(i < bars.size()-1){
                    k.setBefore(bars.get(i+1));
                    k.setLastClose(k.before().getClose());
                    k.setChange(ServiceUtils.numberFormat((k.getClose() - k.getLastClose())/k.getLastClose()*100,2));
                }
                if(i > 0){
                    k.setAfter(bars.get(i-1));
                }
                i++;
            }
        }
        return this;
    }

    public Stock buildBarSeriesMonth(){
        if(this.barSeriesMonth == null) {
            this.barSeriesMonth = new BarSeries(false);
            int a = -1;
            Bar kw = null;
            for (Bar k : this.barSeries.getList()) {
                Date kd = ServiceUtils.parseDate(k.getDate());
                int month = kd.getMonth();
                if (a == -1 || month != a) {
                    if (kw != null) {
                        this.barSeriesMonth.add(kw);
                    }
                    kw = new Bar();
                    kw.setCode(k.getCode());
                    kw.setDate(k.getDate());
                    kw.setClose(k.getClose());
                    kw.setHigh(k.getHigh());
                    kw.setLow(k.getLow());
                    kw.setVolume(k.getVolume());
                    kw.setAmount(k.getAmount());
                } else {
                    kw.setOpen(k.getOpen());
                    kw.setHigh(Math.max(k.getHigh(), kw.getHigh()));
                    kw.setLow(Math.min(k.getLow(), kw.getLow()));
                    kw.setVolume(kw.getVolume() + k.getVolume());
                    kw.setAmount(kw.getAmount()+k.getAmount());
                }
                a = month;
            }
            int i = 0;
            List<Bar> bars = this.barSeriesMonth.getList();
            for (Bar k : bars) {
                if (i < bars.size() - 1) {
                    k.setBefore(bars.get(i + 1));
                    k.setLastClose(k.before().getClose());
                    k.setChange(ServiceUtils.numberFormat((k.getClose() - k.getLastClose())/k.getLastClose()*100,2));
                }
                if (i > 0) {
                    k.setAfter(bars.get(i - 1));
                }
                i++;
            }
        }
        return this;
    }

    @Override
    public int hashCode(){
        return this.code.hashCode();
    }
}
