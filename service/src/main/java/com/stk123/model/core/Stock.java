package com.stk123.model.core;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.CommonConstant;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.BeanUtils;
import com.stk123.model.json.View;
import com.stk123.model.projection.IndustryProjection;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockProjection;
import com.stk123.repository.StkIndustryRepository;
import com.stk123.repository.StkRepository;
import com.stk123.service.core.BarService;
import com.stk123.service.core.StockService;
import com.stk123.service.support.SpringApplicationContext;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.stk123.model.core.Stock.EnumMarket.*;
import static com.stk123.model.core.Stock.EnumPlace.SH;
import static com.stk123.model.core.Stock.EnumPlace.SZ;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
@CommonsLog
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Stock {

    @Autowired
    private BarService barService;
    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StkIndustryRepository stkIndustryRepository;
    @Autowired
    private StockService stockService;

    @AllArgsConstructor
    public enum EnumMarket {
        CN(1), HK(3), US(2);

        EnumMarket(Integer market){
            this.market = market;
        }
        @Getter
        private Integer market;
        @Getter
        private String klineTable;

        public String getKlineTable(){
            return klineTable == null ? this.klineTable = this.select("stk_kline", "stk_kline_hk", "stk_kline_us") : klineTable;
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

        public static boolean isSH(Integer place){
            return Objects.equals(SH.place, place);
        }
    }

    @AllArgsConstructor
    public enum EnumCate {
        STOCK(1), INDEX(2), FUND(3), INDEX_10jqka(4), INDEX_eastmoney_gn(5);

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

    private StockProjection stock;
    private List<IndustryProjection> industries; //行业

    private BarSeries barSeries;
    private BarSeries barSeriesWeek;
    private BarSeries barSeriesMonth;

    //是否包含今天最新的k线价格，用于交易时间实时监控
    private boolean isIncludeRealtimeBar = false;
    private boolean isIncludeRealtimeBarDone = false;

    public Integer BarSeriesRows = 750;

    /**
     * static field
     */



    private Stock() {}

    public static Stock build(){
        return SpringApplicationContext.getBean(Stock.class);
    }
    public static Stock build(String code){
        Stock stock = Stock.build();
        return stock.set(code, null);
    }
    //code, eg: SH600600, 600600, 02002, BIDU
    public static Stock build(String code, String name){
        Stock stock = Stock.build();
        return stock.set(code, name);
    }
    public static Stock build(String code, String name, EnumMarket market, EnumPlace place){
        Stock stock = Stock.build();
        return stock.set(code, name, market, place);
    }
    public static Stock build(StockBasicProjection stockBasicProjection) {
        Stock stock = Stock.build();
        return stock.set(stockBasicProjection);
    }
    public static Stock build(StockProjection stockProjection) {
        Stock stock = Stock.build();
        return stock.set(stockProjection);
    }

    /**
     * 能适应各种类型的code, eg: SH600600, 600600, 02002, BIDU
     * @param code
     * @param name
     * @return
     */
    private Stock set(String code, String name) {
        this.code = code;
        this.name = name;

        boolean isAllNumber = StringUtils.isNumeric(code);

        if(code.length() == 5 && isAllNumber){
            this.market = HK;
        }else{
            boolean isAllAlpha = StringUtils.isAlpha(code);
            this.market = isAllAlpha ? US : CN;
        }

        setPlace();
        return this;
    }
    private Stock set(String code, String name, EnumMarket market, EnumPlace place){
        this.code = code;
        this.name = name;
        this.market = market;
        this.place = place;
        return this;
    }
    private Stock set(StockProjection stockProjection) {
        this.stock = stockProjection;
        return set((StockBasicProjection)stockProjection);
    }
    private Stock set(StockBasicProjection stockBasicProjection) {
        this.code = stockBasicProjection.getCode();
        this.name = stockBasicProjection.getName();
        this.market = EnumMarket.getMarket(stockBasicProjection.getMarket());
        this.cate = EnumCate.getCate(stockBasicProjection.getCate());
        this.place = EnumPlace.getPlace(stockBasicProjection.getPlace());
        if(this.market == EnumMarket.CN && this.place == null){
            setPlace();
        }
        return this;
    }

    private boolean loadIfNull(Object o) {
        if(stock == null && ObjectUtil.isEmpty(o)){
            stock = stkRepository.findByCode(code);
            set(stock);
        }
        return true;
    }


    /**
     * @return SH600000, 03323, APPL
     */
    public String getCodeWithPlace(){
        return this.place == null ? this.code : (loadIfNull(this.cate) && this.isCateStock() ? (this.place.name() + this.code) : this.code);
    }

    public String getNameAndCode(){
        loadIfNull(this.name);
        return (this.name==null?this.code:this.name) + "["+ this.getCodeWithPlace() +"]";
    }
    public String getNameAndCodeWithLink(){
        if(this.isMarketCN() && this.isCateIndexEastmoneyGn()){
            return CommonUtils.wrapLink(this.getNameAndCode(), "https://quote.eastmoney.com/bk/90."+this.getCode()+".html");
        }
        return CommonUtils.wrapLink(this.getNameAndCode(), "https://xueqiu.com/S/"+this.getCodeWithPlace());
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
    public boolean isCateStock() {
        return this.cate == EnumCate.STOCK;
    }
    public boolean isCateIndexEastmoneyGn() {
        return this.cate == EnumCate.INDEX_eastmoney_gn;
    }
    public boolean isPlaceSH(){
        return this.place == EnumPlace.SH;
    }

    private void setPlace(){
        if(this.market == CN){
            if(this.code.length() == 8){//01000010 or SH000010
                if(this.code.startsWith(CommonConstant.NUMBER_01) || this.code.startsWith(EnumPlace.SH.name())){
                    this.place = SH;
                }else{
                    this.place = SZ;
                }
                if(this.code.startsWith(EnumPlace.SH.name()) || this.code.startsWith(EnumPlace.SZ.name())){
                    this.code = this.code.substring(2, 8);
                }
            }else{
                this.place = getCity(code);
            }
        }
    }

    //input:SH000010 return:000010
    public static String getCodeWithoutPlace(String codeWithPlace){
        if(codeWithPlace.length() == 8){
            if(codeWithPlace.startsWith(EnumPlace.SH.name()) || codeWithPlace.startsWith(EnumPlace.SZ.name())){
                String code = codeWithPlace.substring(2, 8);
                if(StringUtils.isNumeric(code)){
                    return code;
                }
            }
        }
        return codeWithPlace;
    }


    public static EnumPlace getCity(String code){
        if(code.startsWith(CommonConstant.NUMBER_SIX) || code.startsWith(CommonConstant.NUMBER_99)){
            return SH;
        }else{
            return SZ;
        }
    }

    public Bar getBar(){
        return this.getBarSeries().getFirst();
    }
    public BarSeries getBarSeries(){
        return this.getBarSeries(this.BarSeriesRows);
    }
    public synchronized BarSeries getBarSeries(Integer rows){
        if(this.barSeries != null){
            return this.barSeries;
        }
        this.barSeries = barService.queryTopNByCodeOrderByKlineDateDesc(this.code, this.market, rows);
        if (isIncludeRealtimeBar) {
            buildBarRealTime();
        }
        return this.barSeries;
    }
    public synchronized BarSeries getBarSeries(BarSeries.EnumPeriod period){
        return period.select(this.getBarSeries(), this.getBarSeriesWeek(), this.getBarSeriesMonth());
        /*switch (period) {
            case W:
            case WEEK:
                return this.getBarSeriesWeek();
            case M:
            case MONTH:
                return this.getBarSeriesMonth();
            default:
                return this.getBarSeries();
        }*/
    }


    public synchronized BarSeries getBarSeriesWeek() {
        if(this.barSeriesWeek != null)
            return this.barSeriesWeek;
        else{
            this.barSeriesWeek = new BarSeries(false);
            Date a = null;
            Bar kw = null;
            for(Bar k : this.getBarSeries().getList()){
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
        return this.barSeriesWeek;
    }

    public synchronized BarSeries getBarSeriesMonth(){
        if(this.barSeriesMonth != null)
            return this.barSeriesMonth;
        else{
            this.barSeriesMonth = new BarSeries(false);
            int a = -1;
            Bar kw = null;
            for (Bar k : this.getBarSeries().getList()) {
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
        return this.barSeriesMonth;
    }

    public synchronized Stock buildBarRealTime() {
        if(isIncludeRealtimeBarDone) return this;
        isIncludeRealtimeBarDone = true;
        if(this.isMarketUS()) {
            return this;
        }
        String scode = this.getCode();
        if(this.isMarketCN()){
            scode = this.getCodeWithPlace().toLowerCase();
        }else if(this.isMarketHK()){
            scode = "hk"+this.getCode();
        }
        String page = null;
        try {
            page = HttpUtils.get("http://hq.sinajs.cn/list="+scode, null, "GBK");
        } catch (Exception e) {
            log.error("setBarRealTime", e);
            return this;
        }
        log.info("buildBarRealTime:"+page);
        String[] str = page.split(";");
        for(int j=0;j<str.length;j++){
            String s = str[j];
            if(this.isMarketCN() && s.length() > 40){
                s = org.apache.commons.lang.StringUtils.substringBetween(s, "\"", "\"");
                String[] ss = s.split(",");
                Bar k = new Bar();
                k.setCode(code);
                k.setOpen(Double.parseDouble(ss[1]));
                k.setLastClose(Double.parseDouble(ss[2]));
                k.setClose(Double.parseDouble(ss[3]));
                k.setHigh(Double.parseDouble(ss[4]));
                k.setLow(Double.parseDouble(ss[5]));
                k.setVolume(Double.parseDouble(ss[8]));
                k.setAmount(Double.parseDouble(ss[9]));
                k.setChange((k.getClose()-k.getLastClose())/k.getLastClose()*100);
                k.setDate(org.apache.commons.lang.StringUtils.replace(ss[30], "-", ""));

                this.getBarSeries().addToFirst(k);
                //System.out.println(this.getBarSeries().getFirst());
                return this;
            }else if(this.isMarketHK() && s.length() > 12){
                s = org.apache.commons.lang.StringUtils.substringBetween(s, "\"", "\"");
                String[] ss = s.split(",");
                Bar k = new Bar();
                k.setCode(code);
                k.setOpen(Double.parseDouble(ss[2]));
                k.setLastClose(Double.parseDouble(ss[3]));
                k.setClose(Double.parseDouble(ss[6]));
                k.setHigh(Double.parseDouble(ss[4]));
                k.setLow(Double.parseDouble(ss[5]));
                k.setVolume(Double.parseDouble(ss[12]));
                k.setAmount(Double.parseDouble(ss[11]));
                k.setChange(Double.parseDouble(ss[8]));
                k.setDate(org.apache.commons.lang.StringUtils.replace(ss[17], "/", ""));

                this.getBarSeries().addToFirst(k);
                //System.out.println(this.getBarSeries().getFirst());
                return this;
            }
        }
        return this;
    }


    public synchronized List<IndustryProjection> getIndustries(){
        if(industries == null){
            return this.industries = stkIndustryRepository.findAllByCode(this.getCode());
        }
        return this.industries;
    }

    private static Map<Integer, Bar> TURNING_POINTS = Collections.synchronizedMap(new HashMap<>());
    /**
     * 转折点，板块站上5日均线最多的日子
     */
    public synchronized Bar getTurningPoint(int days){
        if(TURNING_POINTS.get(days) != null){
            return TURNING_POINTS.get(days);
        }
        List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.INDEX_eastmoney_gn);
        List<Stock> stocks = list.stream().map(projection -> Stock.build(projection)).collect(Collectors.toList());
        Bar b = this.getBar().getHighestBar(days, bar -> {
           List<Bar> bars = stocks.stream().map(stock1 -> stock1.getBar().before(bar.getDate())).collect(Collectors.toList());
           long cnt = bars.stream().filter(bar1 -> bar1.getClose() > bar1.getMA(5, Bar.EnumValue.C) && bar1.before().getClose() < bar1.before().getMA(5, Bar.EnumValue.C)).count();
           return Double.valueOf(cnt);
        });
        TURNING_POINTS.put(days, b);
        return b;
    }

    @Override
    public int hashCode(){
        return this.code.hashCode();
    }
}
