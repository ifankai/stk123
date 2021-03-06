package com.stk123.model.core;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.CommonConstant;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.entity.*;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.enumeration.EnumPeriod;
import com.stk123.model.enumeration.EnumPlace;
import com.stk123.model.json.View;
import com.stk123.model.projection.IndustryProjection;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockProjection;
import com.stk123.model.strategy.Strategy;
import com.stk123.repository.*;
import com.stk123.service.StkConstant;
import com.stk123.service.core.BarService;
import com.stk123.service.core.DictService;
import com.stk123.service.core.StockService;
import com.stk123.service.support.SpringApplicationContext;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.Data;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.stk123.model.enumeration.EnumMarket.CN;
import static com.stk123.model.enumeration.EnumMarket.HK;
import static com.stk123.model.enumeration.EnumMarket.US;
import static com.stk123.model.enumeration.EnumPlace.SH;
import static com.stk123.model.enumeration.EnumPlace.SZ;

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
    @Autowired
    private StkIndustryTypeRepository stkIndustryTypeRepository;
    @Autowired
    private StkOwnershipRepository stkOwnershipRepository;
    @Autowired
    private DictService dictService;
    @Autowired
    private StkHolderRepository stkHolderRepository;
    @Autowired
    private StkNewsRepository stkNewsRepository;



    @JsonView({View.Default.class, View.Score.class})
    private String code;

    @JsonView(View.Default.class)
    private String name;

    @JsonView(View.Default.class)
    private EnumPlace place;// 1:sh, 2:sz

    @JsonView(View.Default.class)
    private EnumMarket market;// 1:A股, 2:美股, 3:港股

    @JsonView(View.Default.class)
    private EnumCate cate;

    //存放临时数据
    private Map data = new HashMap();

    private Double totalCapital; //总股本
    private Double marketCap; //总市值

    private StockProjection stock;

    private List<IndustryProjection> industries; //行业
    private List<Stock> bks; //板块 eastmoney_gn
    private List<Stock> stocks; //板块包含的所有股票

    private StkHolderEntity holder; //最新股东人数,人均持股金额
    private List<StkOwnershipEntity> owners; //十大流通股股东
    private List<StkNewsEntity> news;


    private BarSeries barSeries;
    private BarSeries barSeriesWeek;
    private BarSeries barSeriesMonth;

    //是否包含今天最新的k线价格，用于交易时间实时监控
    private boolean isIncludeRealtimeBar = false;
    private boolean isIncludeRealtimeBarDone = false;

    public static Integer BarSeriesRowsDefault = 750;
    public Integer BarSeriesRows = BarSeriesRowsDefault;

    //相对强弱指标
    @JsonView(View.Score.class)
    private Map<String, Rps> rps = new HashMap<>();

    //评级
    private Rating rating;


    //////////////////////////////////////////////////////////////////////////////////////

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
        setCate();
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
        if(this.market == CN && this.place == null
                && (this.cate == EnumCate.STOCK || this.cate == EnumCate.INDEX)){
            setPlace();
        }
        this.totalCapital = stockBasicProjection.getTotalCapital();
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
        //return this.place == null ? this.code : (loadIfNull(this.cate) && this.isCateStock() ? (this.place.name() + this.code) : this.code);
        return this.place == null ? this.code : (this.place.name() + this.code);
    }

    public String getNameAndCode(){
        loadIfNull(this.name);
        return (this.name==null?this.code:this.name) + "["+ this.getCodeWithPlace() +"]";
    }
    public String getNameAndCodeWithLink(){
        loadIfNull(this.name);
        if(this.isMarketCN() && this.isCateIndexEastmoneyGn()){
            return CommonUtils.wrapLink(this.getName(), "https://quote.eastmoney.com/bk/90."+this.getCode()+".html")
                    + "["+ CommonUtils.wrapLink(this.getCode(), "http://81.68.255.181:8089/bk/"+this.getCode()) +"]";
        }
        return CommonUtils.wrapLink((this.name==null?this.code:this.name), "https://xueqiu.com/S/"+this.getCodeWithPlace())
                + "["+ CommonUtils.wrapLink(this.getCodeWithPlace(), "http://81.68.255.181:8088/stk?s="+this.getCode()) +"]";
    }
    public String getNameAndCodeWithLinkAndBold(){
        return "<b>"+this.getNameAndCodeWithLink()+"</b>";
    }

    public boolean isMarketCN(){
        return this.market == CN;
    }
    public boolean isMarketUS(){
        return this.market == US;
    }
    public boolean isMarketHK(){
        return this.market == HK;
    }
    public boolean isCateStock() {
        return this.cate == EnumCate.STOCK;
    }
    public boolean isCateIndexEastmoneyGn() {
        return this.cate == EnumCate.INDEX_eastmoney_gn;
    }
    public boolean isPlaceSH(){
        return this.place == SH;
    }

    private void setPlace(){
        if(place != null) return;
        if(this.market == CN){
            if(this.code.length() == 8){//01000010 or SH000010
                if(this.code.startsWith(CommonConstant.NUMBER_01) || this.code.startsWith(SH.name())){
                    this.place = SH;
                }else{
                    this.place = SZ;
                }
                if(this.code.startsWith(SH.name()) || this.code.startsWith(SZ.name())){
                    this.code = this.code.substring(2, 8);
                }
            }else{
                this.place = getCity(code);
            }
        }
    }
    private void setCate() {
        if(cate != null) return;
        if (this.market == CN) {
            if(this.code.startsWith("399")){
                this.cate = EnumCate.INDEX;
            }else {
                this.cate = EnumCate.STOCK;
            }
        }else {
            this.cate = EnumCate.STOCK;
        }
    }

    //input:SH000010 return:000010
    public static String getCodeWithoutPlace(String codeWithPlace){
        if(codeWithPlace.length() == 8){
            if(codeWithPlace.startsWith(SH.name()) || codeWithPlace.startsWith(SZ.name())){
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
    public synchronized BarSeries getBarSeries(EnumPeriod period){
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
        for (String aStr : str) {
            String s = aStr;
            if (this.isMarketCN() && s.length() > 40) {
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
                k.setChange((k.getClose() - k.getLastClose()) / k.getLastClose() * 100);
                k.setDate(org.apache.commons.lang.StringUtils.replace(ss[30], "-", ""));

                this.getBarSeries().addToFirst(k);
                //System.out.println(this.getBarSeries().getFirst());
                return this;
            } else if (this.isMarketHK() && s.length() > 12) {
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

    //用于bk
    public void initStocks(){
        if(this.stocks == null)
            this.stocks = new ArrayList<>();
    }
    //用于bk
    public void addStock(Stock stock){
        if(this.stocks == null){
            this.stocks = new ArrayList<>();
            this.stocks.add(stock);
        }else{
            if(!this.stocks.contains(stock)){
                this.stocks.add(stock);
            }
        }
    }
    //用于bk
    public List<Stock> getStocks(){
        if(this.stocks == null){
            StkIndustryTypeEntity stkIndustryTypeEntity = stkIndustryTypeRepository.findByCode(this.getCode());
            List<StkIndustryEntity> list = stkIndustryRepository.findAllByIndustry(stkIndustryTypeEntity.getId());
            this.stocks = stockService.buildStocks(list.stream().map(StkIndustryEntity::getCode).collect(Collectors.toList()));
        }
        return this.stocks;
    }

    //用于stock
    public void initBks(){
        if(bks == null)
            this.bks = new ArrayList<>();
    }
    //用于stock
    public void addBk(Stock bk){
        if(this.bks == null){
            this.bks = new ArrayList<>();
            this.bks.add(bk);
        }else{
            if(!this.bks.contains(bk)){
                this.bks.add(bk);
            }
        }
    }
    //用于stock
    public List<Stock> getBks(){
        if(this.bks == null){
            if(this.industries == null){
                this.industries = stkIndustryRepository.findAllByCode(this.getCode());
            }
            this.bks = stockService.buildStocks(this.industries.stream().map(IndustryProjection::getBkCode).collect(Collectors.toList()));
        }
        return this.bks;
    }

    public static Map<Integer, Bar> TURNING_POINTS = Collections.synchronizedMap(new HashMap<>());
    /**
     * 转折点，板块站上5日均线最多的日子
     */
    public synchronized Bar getTurningPoint(int days){
        if(TURNING_POINTS.get(days) != null){
            return TURNING_POINTS.get(days);
        }
        List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(CN, EnumCate.INDEX_eastmoney_gn);
        List<Stock> stocks = list.stream().map(Stock::build).collect(Collectors.toList());
        Bar b = this.getBar().getHighestBar(days, bar -> {
           List<Bar> bars = stocks.stream().map(stock1 -> stock1.getBar()!=null ? stock1.getBar().before(bar.getDate()) : null).filter(Objects::nonNull).collect(Collectors.toList());
           long cnt = bars.stream().filter(bar1 -> bar1.getClose() > bar1.getMA(5, Bar.EnumValue.C) && bar1.before().getClose() < bar1.before().getMA(5, Bar.EnumValue.C)).count();
           return (double) cnt;
        });
        TURNING_POINTS.put(days, b);
        return b;
    }

    private String getBarImage(String period){
        String xueqiu = "https://xueqiu.com/S/"+this.getCodeWithPlace();
        if(this.isMarketCN()) {
            if(this.isCateIndexEastmoneyGn()){
                xueqiu = "https://xueqiu.com/k?q="+this.getName()+"#/stock";
                return CommonUtils.wrapLink("<img src='http://webquoteklinepic.eastmoney.com/GetPic.aspx?token=&nid=90."+this.getCodeWithPlace()+"&type="+period+"&unitWidth=-6&ef=&formula=MACD&imageType=KXL&_="+new Date().getTime()+"' />", xueqiu);
            }
            // http://webquoteklinepic.eastmoney.com/GetPic.aspx?nid=0.002020&UnitWidth=-6&imageType=KXL&EF=&Formula=MACD&AT=1&&type=W&token=&_=
            return CommonUtils.wrapLink("<img src='http://webquoteklinepic.eastmoney.com/GetPic.aspx?nid="+(this.isPlaceSH()?"1":"0")+"."+this.getCode()+"&UnitWidth=-6&imageType=KXL&EF=&Formula=MACD&AT=1&&type="+period+"&token=&_="+new Date().getTime()+"' />", xueqiu);
            //return CommonUtils.wrapLink("<img src='http://image.sinajs.cn/newchart/"+period+"/n/" + this.getCodeWithPlace().toLowerCase() + ".gif' />", xueqiu);
        }else if(this.isMarketHK()){
            //http://webquoteklinepic.eastmoney.com/GetPic.aspx?nid=116.01812&UnitWidth=-6&imageType=KXL&EF=&Formula=MACD&AT=&&type=D&token=
            return CommonUtils.wrapLink("<img src='http://webquoteklinepic.eastmoney.com/GetPic.aspx?nid=116."+this.getCode()+"&UnitWidth=-6&imageType=KXL&EF=&Formula=MACD&AT=&&type="+period+"&token=' />", xueqiu);
        }else if(this.isMarketUS()){
            //http://webquoteklinepic.eastmoney.com/GetPic.aspx?token=&nid=105.JD&type=&unitWidth=-6&ef=&formula=RSI&imageType=KXL&_=1625985559783
            return CommonUtils.wrapLink("<img src='http://webquoteklinepic.eastmoney.com/GetPic.aspx?token=&nid=105."+this.getCode()+"&type="+period+"&unitWidth=-6&ef=&formula=MACD&imageType=KXL&_="+new Date().getTime()+"' />", xueqiu);
        }
        return "";
    }
    public String getDayBarImage(){
        return getBarImage("D");
    }
    public String getWeekBarImage(){
        return getBarImage("W");
    }
    public String getMonthBarImage(){
        return getBarImage("M");
    }

    public Double getMarketCap(){
        if(this.marketCap != null) return this.marketCap;
        if(this.totalCapital != null && this.getBar() != null){
            this.marketCap = totalCapital * this.getBar().getClose() / 10000;
        }else{
            this.marketCap = Double.MIN_VALUE;
        }
        return this.marketCap;
    }

    public void setRpsValue(String rpsCode, Double rpsValue){
        Rps rps = getRps(rpsCode);
        rps.setValue(rpsValue);
    }
    public void setRpsOrder(String rpsCode, Integer rpsOrder){
        Rps rps = getRps(rpsCode);
        rps.setOrder(rpsOrder);
    }
    public void setRpsPercentile(String rpsCode, Double rpsPercentile){
        Rps rps = getRps(rpsCode);
        rps.setPercentile(rpsPercentile);
    }
    public Rps getRps(String rpsCode){
        return rps.get(rpsCode);
    }
    public Rps createRps(String rpsCode, List<Strategy> strategies){
        Rps rps = getRps(rpsCode);
        if (rps == null) {
            rps = new Rps(rpsCode, strategies);
            this.rps.put(rpsCode, rps);
        }
        return rps;
    }
    public Stock getBkByMaxRps(String rpsCode){
        return getBks().stream().filter(Objects::nonNull).max(Comparator.comparingDouble(stk -> stk.getRps(rpsCode).getPercentile())).orElse(null);
    }
    public Rps getMaxRpsInAllBk(String rpsCode){
        return getBkByMaxRps(rpsCode).getRps(rpsCode);
    }

    //用于bk
    public List<Stock> getGreatestStocksInBkByRps(int topN, String rpsCode){
        List<Stock> stocks = stockService.calcRps(this.getStocks(), rpsCode);
        return ListUtils.greatest(stocks, topN, stock1 -> stock1.getRps(rpsCode).getPercentile());
    }

    //用于stock
    public String getBkInfo(){
        if(!getBks().isEmpty()){
            Stock bk = this.getBkByMaxRps(Rps.CODE_BK_60);
            Rps rps = bk.getRps(Rps.CODE_BK_60);
            //List<Stock> top5a = rps.getPercentile()>=90?bk.getGreatestStocksInBkByRps(Rps.CODE_BK_STOCKS_SCORE_30, 5):null;

            Stock bk2 = this.getBkByMaxRps(Rps.CODE_BK_STOCKS_SCORE_30);
            Rps rps2 = bk2.getRps(Rps.CODE_BK_STOCKS_SCORE_30);
            //List<Stock> top5b = rps2.getPercentile()>=90?(List<Stock>)bk2.getData().get("top5"):null;

            //final int[] a = {1}, b = {1};
            return "<br/>"+bk.getNameAndCodeWithLink()+bk.getStocksInfo(15,false, Rps.CODE_STOCK_SCORE_20)+
                   "<br/>"+rps.getName()+":"+CommonUtils.numberFormat2Digits(rps.getPercentile())+
                    //(top5a==null?"":("<br/>"+StringUtils.join(top5a.stream().map(stock->(a[0]++)+"."+stock.getNameAndCodeWithLink()).collect(Collectors.toList()), "<br/>"))+CommonUtils.k("查看",top5a.stream().map(Stock::getCodeWithPlace).collect(Collectors.toList())))+
                   "<br/>"+bk2.getNameAndCodeWithLink()+bk2.getStocksInfo(15,false, Rps.CODE_STOCK_SCORE_20)+
                   "<br/>"+rps2.getName()+"["+rps2.getValue()+"]:"+CommonUtils.numberFormat2Digits(rps2.getPercentile());
                    //(top5b==null?"":("<br/>"+StringUtils.join(top5b.stream().map(stock->(b[0]++)+"."+stock.getNameAndCodeWithLink()).collect(Collectors.toList()), "<br/>"))+CommonUtils.k("查看",top5b.stream().map(Stock::getCodeWithPlace).collect(Collectors.toList())));
        }
        return "";
    }

    //用于bk
    public String getStocksInfo(int topN, boolean displayAllStocks, String rpsCode){
        List<Stock> stocks = this.getGreatestStocksInBkByRps(topN, rpsCode);
        final int[] a = {1};

        String info = displayAllStocks ? StringUtils.join(stocks.stream().map(stock->(a[0]++)+"."+stock.getNameAndCodeWithLink()+"["+CommonUtils.numberFormat2Digits(stock.getRps(rpsCode).getPercentile())+"("+stock.getRps(rpsCode).getRpsStrategies().stream().map(rs -> CommonUtils.numberFormat0Digits(stock.getRps(rs.getCode()).getPercentile())).collect(Collectors.toList())+")]").collect(Collectors.toList()), "<br/>") : "";
        return info + CommonUtils.k("查看", stocks.stream().map(Stock::getCode).collect(Collectors.toList()));
    }

    public static void main(String[] args) {
        Rating rating = new Rating();
        rating.addScore("jsm");
        rating.addScore("jsm","bar1", () -> 11);
        rating.addScore("jsm","bar2", () -> 12);
        rating.addScore("jsm","bar3", () -> 13);
        rating.addScore("jbm");
        rating.addScore("jbm","bk", () -> 21);
        rating.addScore("jbm","holder", () -> 22);
        rating.addScore("jbm","owners", () -> 23);
        rating.addScore("jbm","news", () -> 24);
        rating.addScore("jbm", "fn");
        rating.addScore("fn", "fn1", () -> 31);
        rating.setInclude("fn1");
        rating.calculate();
        System.out.println(rating.toHtml());
        System.out.println(rating.toMap());
    }

    @Deprecated
    public int getScore(String date){
        int days = 30;
        Bar bar = this.getBar().before(date);
        int score = bar.getScore(days);
        score += bar.getScore(days/2);
        score += bar.getScore(days/3);
        return score;
    }

    //评分
    public int getScore(){
        return this.getRating().getScore();
    }

    /**
     * @TODO 评级
     * 1. 十大流通股里有基金、证券投资公司的加一颗星，有社保，港股通资金的加一颗星，有著名基金，私募如高毅等加一颗星
     * 2. 人均持股金额大于50w的加一颗星，散户个数下降一定比例的加一颗星，十大流通股持股比例环比提供5%的加一颗星
     * 3. 所属行业有rps大于90的加三颗星
     * 4. 有股权激励，龙头，大订单，涨价等新闻的各加1颗星
     * 5. 财务方面数据，如毛利率，主营收入，净利润，现金流优秀的个加一颗星
     * 6. 。。。
     */
    public Rating getRating(){
        if(rating != null){
            return this.rating;
        }
        this.rating = new Rating();
        int days = 30;
        rating.addScore("jsm");
        rating.addScore("jsm","bar1", () -> this.getBar().getScore(days));
        rating.addScore("jsm","bar2", () -> this.getBar().getScore(days/2));
        rating.addScore("jsm","bar3", () -> this.getBar().getScore(days/3));
        rating.addScore("jbm");
        rating.addScore("jbm","bk", () -> this.getScoreByBk());
        rating.addScore("jbm","holder", () -> this.getScoreByHolder());
        rating.addScore("jbm","owners", this::getScoreByOwners);
        rating.addScore("jbm","news", this::getScoreByNews);
        rating.calculate();
        return rating;
    }



    public int getScoreByBk(){
        if(this.bks != null){
            Stock bk = this.getBkByMaxRps(Rps.CODE_BK_60);
            if(bk == null) return 0;
            Rps rps = bk.getRps(Rps.CODE_BK_60);
            if(rps != null){
                if(rps.getPercentile() >= 90){ //板块rps强度大于90百分位，则加10分
                    return 15;
                }else if(rps.getPercentile() >= 80){
                    return 10;
                }
            }
        }
        return 0;
    }

    public int getScoreByHolder(){
        int score = 0;
        if(this.getHolder() != null){
            StkHolderEntity stkHolderEntity = this.getHolder();
            if(stkHolderEntity.getHoldingAmount() != null){
                if(stkHolderEntity.getHoldingAmount() >= 500000){ //人均持股金额大于50w，加10
                    score += 10;
                }else if(stkHolderEntity.getHoldingAmount() >= 300000){ //人均持股金额大于30w，加5
                    score += 5;
                }else if(stkHolderEntity.getHoldingAmount() < 100000){ //人均持股金额小于10w，减5
                    score += -5;
                }
            }
            if(stkHolderEntity.getHolderChange() != null){
                if(stkHolderEntity.getHolderChange() < -10){
                    score += 5;
                }
                if(stkHolderEntity.getHolderChange() > 15){
                    score += -5;
                }
            }
            if(stkHolderEntity.getTenOwnerChange() != null){
                Double ownerChange = stkHolderEntity.getTenOwnerChange();
                if(ownerChange > 0 && ownerChange < 100){
                    if(ownerChange >= 10) score += 10;
                    else score += ownerChange;
                }
            }
        }
        return score;
    }

    public int getScoreByOwners(){
        int score = 0;
        List<StkOwnershipEntity> os = this.getOwners();
        int cnt = (int) os.stream().filter(owner -> StringUtils.contains(owner.getOrgName(), "证券投资基金")).count();
        if(cnt > 0 && cnt <= 3){
            score += 3;
        }else if(cnt > 3 && cnt <= 5){
            score += cnt;
        }else if(cnt > 5){
            score += 5;
        }
        for(StkOwnershipEntity owner : this.getOwners()){
            if(StringUtils.contains(owner.getOrgName(), "香港中央结算") ||
               StringUtils.contains(owner.getOrgName(), "中央汇金资产") ||
               StringUtils.contains(owner.getOrgName(), "中国证券金融股份") ||
               StringUtils.contains(owner.getOrgName(), "社保基金" ) ||
               StringUtils.contains(owner.getOrgName(), "养老保险基金")
                    ){
                score += 5;
                break;
            }
        }
        Collection<StkDictionaryEntity> dicts = dictService.getDictionaryByTypes(StkConstant.DICT_NIUSAN, StkConstant.DICT_FAMOUS_FUNDS);
        for(StkOwnershipEntity owner : this.getOwners()){
            if(dicts.stream().anyMatch(dict -> StringUtils.contains(owner.getOrgName(), dict.getText()))){
                score += 5;
                break;
            }
        }
        return score;
    }

    public int getScoreByNews(){
        int score = 0;
        List<StkNewsEntity> news = this.getNews();
        long cnt = news.stream().map(n -> n.getType()).distinct().filter(
                type -> type == StkConstant.NEWS_TYPE_220 //	高成长
                || type == StkConstant.NEWS_TYPE_240 //	龙头
                || type == StkConstant.NEWS_TYPE_250 //	业绩大幅增长
                || type == StkConstant.NEWS_TYPE_130 //	股权激励
        ).count();
        score += cnt * 5;
        return score;
    }

    public List<StkOwnershipEntity> getOwners(){
        if(owners == null){
            owners = stkOwnershipRepository.findAllByCodeAndFnDateIsMax(Collections.singletonList(this.getCode()));
        }
        return this.owners;
    }

    public StkHolderEntity getHolder(){
        if(this.holder == null){
            holder = stkHolderRepository.findByCodeAndFnDateIsMax(this.getCode());
        }
        return this.holder;
    }

    public List<StkNewsEntity> getNews(int days){
        if(this.news == null){
            news = stkNewsRepository.findAllByCodeInAndInfoCreateTimeAfterOrderByInsertTimeDesc(Collections.singletonList(this.getCode()), CommonUtils.addDay(new Date(), days));
        }
        return news;
    }
    public List<StkNewsEntity> getNews(){
        return this.getNews(-180);
    }

    @Override
    public int hashCode(){
        return this.code.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Stock && this.getCodeWithPlace().equals(((Stock) obj).getCodeWithPlace());
    }

    @Override
    public String toString() {
        return "Stock{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
