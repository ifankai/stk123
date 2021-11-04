package com.stk123.task.schedule;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.CommonHttpUtils;
import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.entity.*;
import com.stk123.model.core.Stock;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.enumeration.EnumPlace;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.*;
import com.stk123.service.StkConstant;
import com.stk123.service.core.*;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.TableTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StockTask extends AbstractTask {

    @Autowired
    private HttpService httpService;
    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private StockService stockService;
    @Autowired
    private FnService fnService;
    @Autowired
    private StkHolderRepository stkHolderRepository;
    @Autowired
    private StkIndustryTypeRepository stkIndustryTypeRepository;
    @Autowired
    private StkIndustryRepository stkIndustryRepository;
    @Autowired
    private StkOwnershipRepository stkOwnershipRepository;
    @Autowired
    private StkFnTypeRepository stkFnTypeRepository;
    @Autowired
    private KeywordService keywordService;

    private List<Stock> stocksCN = null;
    private List<Stock> stocksHK = null;
    private List<Stock> stocksUS = null;
    @Setter
    private String code;

    @Override
    public void register() {
        this.runByName("initCNIndustryEasymoney", this::initCNIndustryEasymoney);
        this.runByName("initCNHolder", this::initCNHolder);
        this.runByName("initCNFinance", this::initCNFinance);
        this.runByName("initCNMainProduct", this::initCNMainProduct);
        this.runByName("initCNNewStock", this::initCNNewStock);
        this.runByName("initHKBaseInfo", this::initHKBaseInfo);
        this.runByName("initUSBaseInfo", this::initUSBaseInfo);
        this.runByName("clear", this::clear);
    }

    public void clear(){
        if(stocksCN != null)stocksCN.clear();
        stocksCN = null;
        if(stocksHK != null)stocksHK.clear();
        stocksHK = null;
        if(stocksUS != null)stocksUS.clear();
        stocksUS = null;
    }

    public void initCNHolder() {
        log.info("initCNHolder start");
        initCNStocks();
        for(Stock stock : stocksCN) {
            log.info("initCNHolder:"+stock.getCode());
            try {
                String page = httpService.getString("http://basic.10jqka.com.cn/"+stock.getCode()+"/holder.html", "gbk");
                //System.out.println("page="+page);

                Node div = HtmlUtils.getNodeByAttribute(page, "GBK", "class", "data_tbody");
                if(div == null) continue;
                Node table1 = HtmlUtils.getNodeByAttribute(div, null, "class", "top_thead");
                List<Node> holderDates = HtmlUtils.getNodeListByTagNameAndAttribute(table1, "div", "class", "td_w");
                /*for(Node node : holderDates){
                    System.out.println(node.toPlainTextString());
                }*/
                Node table2 = HtmlUtils.getNodeByAttribute(div, null, "class", "tbody");
                List<List<String>> list = HtmlUtils.getListFromTable((TableTag) table2);
                /*for(List<String> row : list){
                    System.out.println(row);
                }*/
                Node headNode = HtmlUtils.getNodeByAttribute(page, null, "class", "tbody");
                List<Node> heads = HtmlUtils.getNodeListByTagName(headNode, "th");
                int h = 0;
                for(Node head : heads){
                    if(StringUtils.contains(head.toPlainTextString(), "变化")){
                        break;
                    }
                    h++;
                }

                for (int i = 0; i < holderDates.size(); i++) {
                    Node node = holderDates.get(i);
                    String holdDate = StringUtils.replace(node.toPlainTextString(), "-", "");
                    StkHolderEntity stkHolderEntity = stkHolderRepository.findByCodeAndFnDate(stock.getCode(), holdDate);

                    List<String> amounts = list.get(list.size()-1);
                    List<String> changes = list.get(h);
                    //System.out.println(amounts);
                    String amount = amounts.get(i);
                    String change = StringUtils.replace(changes.get(i), "%", "");
                    //System.out.println(i+"="+amount+"="+StringUtils.replace(amount,"万", ""));
                    if(stkHolderEntity == null) {
                        stkHolderEntity = new StkHolderEntity();
                        stkHolderEntity.setCode(stock.getCode());
                        stkHolderEntity.setFnDate(holdDate);
                    }
                    stkHolderEntity.setHoldingAmount(CommonUtils.getAmount(amount));
                    if(!"-".equals(change) && stkHolderEntity.getHolderChange() == null) {
                        stkHolderEntity.setHolderChange(StringUtils.isEmpty(change) ? null : Double.parseDouble(change));
                    }
                    if(stkHolderEntity.getTenOwnerChange() == null){
                        List<Object> objects = stkOwnershipRepository.findTenOnwerChangeByCodeAndFnDate(stock.getCode(), holdDate);
                        if(!objects.isEmpty()){
                            Object[] row = (Object[]) objects.get(0);
                            if(row[3] != null)
                                stkHolderEntity.setTenOwnerChange(Double.parseDouble(row[3].toString()));
                        }
                    }
                    stkHolderRepository.save(stkHolderEntity);
                    //System.out.println(stkHolderEntity);
                }

            } catch (Exception e) {
                log.error("initCNHolder", e);
                //System.out.println(stock.getCode());
                break;
            }
        }
        log.info("initCNHolder end");
    }

    public void initCNStocks(){
        if(stocksCN == null) {
            List<StockBasicProjection> list = null;
            if(code == null) {
                list =stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.STOCK);
            }else {
                list = stkRepository.findAllByCodes(ListUtils.createList(code));
            }
            stocksCN = stockService.buildStocksWithProjection(list);
        }
    }

    public void initHKStocks(){
        if(stocksHK == null) {
            List<StockBasicProjection> list = null;
            if(code == null) {
                list =stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.HK, EnumCate.STOCK);
            }else {
                list = stkRepository.findAllByCodes(ListUtils.createList(code));
            }
            stocksHK = stockService.buildStocksWithProjection(list);
        }
    }

    public void initUSStocks(){
        if(stocksUS == null) {
            List<StockBasicProjection> list = null;
            if(code == null) {
                list =stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.US, EnumCate.STOCK);
            }else {
                list = stkRepository.findAllByCodes(ListUtils.createList(code));
            }
            stocksUS = stockService.buildStocksWithProjection(list);
        }
    }

    public void initCNNewStock() {
        log.info("initCNNewStock start");
        String[] ss = getAllStocksCode().split(",");
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<ss.length;i++){
            sb.append(ss[i]+",");
            if((i+1) % 50 == 0 || i == ss.length-1){
                String page = httpService.getString("http://hq.sinajs.cn/list="+sb);
                //log.info(page);
                String[] str = page.split(";");
                for(int j=0;j<str.length;j++){
                    String s = str[j];
                    if(s.length() > 40){
                        String var = StringUtils.substringBefore(s, "=\"");
                        String code = StringUtils.substring(var, var.length()-6);
                        String name = StringUtils.substringBetween(s, "=\"", ",");
                        //log.info("code="+code+", name="+name);
                        if(code.length() != 6 && !ServiceUtils.isAllNumeric(code))continue;

                        Optional<StkEntity> stk = stkRepository.findById(code);
                        StkEntity stkEntity;
                        if(!stk.isPresent()){
                            stkEntity = new StkEntity();
                            stkEntity.setCode(code);
                            stkEntity.setName(name);
                            stkEntity.setInsertTime(new Date());
                            stkEntity.setMarket(EnumMarket.CN.getMarket());
                            stkEntity.setCate(EnumCate.STOCK.getCate());
                            stkEntity.setPlace(code.startsWith("6")?EnumPlace.SH.getPlace():EnumPlace.SZ.getPlace());
                            log.info("[新股]"+code+","+name);
                        }else{
                            stkEntity = stk.get();
                            stkEntity.setCode(code);
                            stkEntity.setName(name);
                        }
                        stkEntity = stkRepository.save(stkEntity);

                        try{
                            updateStockBasicInfo(stkEntity);
                        }catch(Exception e){
                            log.error("", e);
                            errorService.logErrorIfNoSimilarError(code, e);
                        }
                    }
                }
                sb = new StringBuffer();
            }
        }
        log.info("initCNNewStock end");
    }

    //http://quote.eastmoney.com/sz002572.html?from=beta
    //http://push2.eastmoney.com/api/qt/stock/get?ut=&invt=2&fltt=2&fields=f43,f57,f58,f169,f170,f46,f44,f51,f168,f47,f164,f163,f116,f60,f45,f52,f50,f48,f167,f117,f71,f161,f49,f530,f135,f136,f137,f138,f139,f141,f142,f144,f145,f147,f148,f140,f143,f146,f149,f55,f62,f162,f92,f173,f104,f105,f84,f85,f183,f184,f185,f186,f187,f188,f189,f190,f191,f192,f107,f111,f86,f177,f78,f110,f262,f263,f264,f267,f268,f250,f251,f252,f253,f254,f255,f256,f257,f258,f266,f269,f270,f271,f273,f274,f275,f127,f199,f128,f193,f196,f194,f195,f197,f80,f280,f281,f282,f284,f285,f286,f287,f292&secid=0.002572&cb=jQuery112409607445745814001_1597039350332&_=1597039350333
    //jQuery({"rc":0,"rt":4,"svr":182993475,"lt":1,"full":1,"data":{"f84":1364182800.0,"f189":19930827}});
    /**
     * http://push2.eastmoney.com/api/qt/stock/get?ut=&invt=2&fltt=2&fields=f84,f189&secid=0.002572&cb=jQuery&_=1597039350333
     * f84:  总股本
     * f189: 上市时间
     */
    public void updateStockBasicInfo(StkEntity stkEntity) throws Exception {
        String scode = null;
        if(stkEntity.getMarket() == 1) {
            scode = (EnumPlace.isSH(stkEntity.getPlace()) ? "1." : "0.") + stkEntity.getCode();
        }else if(stkEntity.getMarket() == 3){
            scode = "116."+stkEntity.getCode();
        }else if(stkEntity.getMarket() == 2){
            scode = "105."+stkEntity.getCode();
        }

        String url = "http://push2.eastmoney.com/api/qt/stock/get?ut=&invt=2&fltt=2&fields=f84,f189&secid="+ scode +"&cb=jQuery&_="+new Date().getTime();
        String page = httpService.getString(url);
        //System.out.println("updateCNStockBasicInfo:"+page);
        String json = StringUtils.substringBetween(page, "(", ")");
        ObjectMapper mapper = new ObjectMapper();
        Map map = mapper.readValue(json, HashMap.class);
        Map data = (Map)map.get("data");

        if(data != null){
            String listingDate = String.valueOf(data.get("f189"));
            Object f84 = data.get("f84");
            Double totalCapital = null;
            if(f84 instanceof Double) {
                totalCapital = ((Double) data.get("f84")) / 10000;
            }else if(f84 instanceof String && StringUtils.isNotEmpty((String)data.get("f84")) && !StringUtils.equals((String)data.get("f84"), "-")){
                totalCapital = Double.parseDouble((String)data.get("f84")) / 10000;
            }

            stkEntity.setListingDate(listingDate);
            stkEntity.setTotalCapital(totalCapital);
            stkRepository.save(stkEntity);
        }
    }

    private  String getAllStocksCode(){
        StringBuffer sb = new StringBuffer(1024);
        for(int i=0;i<=999;i++){
            sb.append("sh600"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sh601"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sh603"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sh605"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sh688"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sz000"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sz001"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sz002"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sz003"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        for(int i=0;i<=999;i++){
            sb.append("sz300"+StringUtils.leftPad(""+i, 3, '0')+",");
        }
        return sb.toString();
    }

    public void initCNIndustryEasymoney(){
        log.info("initCNIndustryEasymoney start");
        initCNIndustryEasymoney("59", "3", "70");
        initCNIndustryEasymoney("65", "2", "60");
        log.info("initCNIndustryEasymoney end");
    }

    //http://quote.eastmoney.com/center/boardlist.html#concept_board
    //http://quote.eastmoney.com/center/boardlist.html#industry_board
    public void initCNIndustryEasymoney(String type, String type2, String type3) {
        //http://59.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112407845542377068357_1615380136035&pn=1&pz=2000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:90+t:3+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152,f124,f107,f104,f105,f140,f141,f207,f208,f209,f222&_=1615380136036
        //http://65.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112408021918226266793_1633768399338&pn=1&pz=2000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:90+t:2+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152,f124,f107,f104,f105,f140,f141,f207,f208,f209,f222&_=1633768399354
        long time = new Date().getTime();
        String url = "http://"+type+".push2.eastmoney.com/api/qt/clist/get?cb=jQuery112407845542377068357_" + time +
                "&pn=1&pz=2000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:90+t:"+type2+"+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152,f124,f107,f104,f105,f140,f141,f207,f208,f209,f222" +
                "&_="+time;
        try {
            //System.out.println(url);
            String page = httpService.getString(url);
            String json = "{"+StringUtils.substringBetween(page, "({", "})")+"}";
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(json, HashMap.class);
            Map datas = (Map)map.get("data");
            List<Map> diff = (List)datas.get("diff");
            for(Map data : diff){
                String code = String.valueOf(data.get("f12"));
                String name = String.valueOf(data.get("f14"));
                log.info("code="+code+",name="+name);
                Optional<StkEntity> stk = stkRepository.findById(code);
                StkEntity stkEntity;
                if(!stk.isPresent()){
                    stkEntity = new StkEntity();
                    stkEntity.setCode(code);
                    stkEntity.setName(name);
                    stkEntity.setInsertTime(new Date());
                    stkEntity.setMarket(EnumMarket.CN.getMarket());
                    stkEntity.setCate(EnumCate.INDEX_eastmoney_gn.getCate());
                    stkEntity.setAddress("eastmoney_gn");
                    log.info("[新股(INDEX_eastmoney_gn)]"+code+","+name);
                }else{
                    stkEntity = stk.get();
                    stkEntity.setCode(code);
                    stkEntity.setName(name);
                }
                stkEntity = stkRepository.save(stkEntity);


                //update stk_industry_type stk_industry
                //http://70.push2.eastmoney.com/api/qt/clist/get?cb=jQuery11240011828891552190912_1622866172512&pn=1&pz=1000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=b:BK0896+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152,f45&_=1622866172522
                //http://60.push2.eastmoney.com/api/qt/clist/get?cb=jQuery1124024751182578362885_16337686791611&pn=1&pz=1000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=b:BK0464+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152,f45&_=1633768679162
                time = new Date().getTime();
                url = "http://"+type3+".push2.eastmoney.com/api/qt/clist/get?cb=jQuery11240011828891552190912_"+time
                        +"&pn=1&pz=1000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=b:"+code
                        +"+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152,f45&_="+time;
                page = httpService.getString(url);
                json = "{"+StringUtils.substringBetween(page, "({", "})")+"}";
                Map stkMap = mapper.readValue(json, HashMap.class);
                Map stkDatas = (Map)stkMap.get("data");
                List<Map> stkDiff = (List)stkDatas.get("diff");

                if(!stkDiff.isEmpty()) {

                    StkIndustryTypeEntity stkIndustryTypeEntity = new StkIndustryTypeEntity();
                    stkIndustryTypeEntity.setCode(code);
                    Optional<StkIndustryTypeEntity> sit = stkIndustryTypeRepository.findOne(Example.of(stkIndustryTypeEntity));
                    if (!sit.isPresent()) {
                        stkIndustryTypeEntity.setName(name);
                        stkIndustryTypeEntity.setSource("eastmoney_gn");
                        stkIndustryTypeRepository.save(stkIndustryTypeEntity);
                    } else {
                        stkIndustryTypeEntity = sit.get();
                        stkIndustryRepository.deleteAllByIndustry(stkIndustryTypeEntity.getId());
                    }

                    for(Map stkData : stkDiff){
                        StkIndustryEntity stkIndustryEntity = new StkIndustryEntity();
                        stkIndustryEntity.setCode(String.valueOf(stkData.get("f12")));
                        stkIndustryEntity.setIndustry(stkIndustryTypeEntity.getId());
                        stkIndustryRepository.save(stkIndustryEntity);
                    }
                }
            }
        } catch (IOException e) {
            log.error("initCNIndustryEasymoney", e);
        }

    }

    public void initCNFinance(){
        log.info("initCNFinance start");
        initCNStocks();
        ObjectMapper mapper = new ObjectMapper();

        List<StkFnTypeEntity> types = stkFnTypeRepository.findAllByMarketAndCodeIsNotNull(EnumMarket.CN.getMarket());
        for(Stock stock : stocksCN) {
            log.info("initCNFinance:"+stock.getCode());
            try {
                //http://f10.eastmoney.com/NewFinanceAnalysis/ZYZBAjaxNew?type=0&code=SH600107
                String url = "http://f10.eastmoney.com/NewFinanceAnalysis/ZYZBAjaxNew?type=0&code=" + stock.getCodeWithPlace();
                String json = httpService.getString(url);
                Map map = mapper.readValue(json, HashMap.class);
                List<Map> datas = (List)map.get("data");

                for(Map data : datas) {
                    String fnDateLong = (String) data.get("REPORT_DATE");
                    String fnDate = StringUtils.substring(fnDateLong, 0, 10).replaceAll("-", "");

                    for (StkFnTypeEntity type : types) {
                        StkFnDataEntity entity = fnService.find(StkFnDataEntity.class, new StkFnDataEntity.CompositeKey(stock.getCode(), type.getType(), fnDate));
                        if(entity == null) {
                            entity = new StkFnDataEntity();
                            entity.setCode(stock.getCode());
                            entity.setType(type.getType());
                            entity.setFnDate(fnDate);
                            entity.setInsertTime(new Date());
                        }else {
                            entity.setUpdateTime(new Date());
                        }
                        Object obj = data.get(type.getCode());
                        if (obj != null)
                            entity.setFnValue((Double) data.get(type.getCode()));
                        fnService.saveOrUpdate(entity);
                    }
                }

            }catch(Exception e){
                log.error("initCNFinance error:"+stock.getCode(), e);
            }
        }
        log.info("initCNFinance end");
    }

    public void initCNMainProduct(){
        initCNStocks();

        int codeIndex = CommonUtils.getIndexFromTempFile("task_stock_main_product.txt");
        if (codeIndex >= stocksCN.size()) {
            codeIndex = 0;
        }

        //for(Stock stock : stocksCN) {
        for(int i=codeIndex; i < stocksCN.size(); i++){
            Stock stock = stocksCN.get(i);
            log.info("initCNMainProduct:" + stock.getCode());
            try {
                String url = "http://www.iwencai.com/unifiedwap/unified-wap/v2/result/get-robot-data";
                String body = "question="+stock.getCode()+"&perpage=50&page=1&secondary_intent=&log_info=%7B%22input_type%22%3A%22click%22%7D&source=Ths_iwencai_Xuangu&version=2.0&query_area=&block_list=&add_info=%7B%22urp%22%3A%7B%22scene%22%3A1%2C%22company%22%3A1%2C%22business%22%3A1%7D%2C%22contentType%22%3A%22json%22%2C%22searchInfo%22%3Atrue%7D";
                Map headers = new HashMap();
                String cookie = CommonHttpUtils.getCookieByType(StkConstant.COOKIE_IWENCAI);
                headers.put("Cookie", cookie);
                //Map map = httpService.postMap(url, body, );
                String page = HttpUtils.post(url, null, body, headers,"utf-8", null);
                if(StringUtils.startsWith(page, "<html><body>")){
                    log.info("iwencai cookie error: "+stock.getCode());
                    EmailUtils.send("iwencai cookie error: "+stock.getCode(), page);
                    break;
                }
                ObjectMapper mapper = new ObjectMapper();
                Map map = mapper.readValue(page, Map.class);
                String products = null;
                try{
                    products = BeanUtils.getProperty(map, "data.answer.[0].txt.[0].content.components.[0].data.[0].主营产品名称");
                }catch (Exception e){
                    log.error("Parse 'data.answer.[0].txt.[0].content.components.[0].data.[0].主营产品名称' error", e);
                }
                //崂山啤酒||啤酒||汉斯啤酒||山水啤酒||啤麦||中高档啤酒||高档酒||糖酒
                log.info(stock.getCode()+":"+products);
                if(StringUtils.isEmpty(products)){
                    continue;
                }
                for(String product : StringUtils.split(products, "||")){
                    String prdt = StringUtils.trim(product);
                    if(StringUtils.isNotEmpty(prdt)) {
                        keywordService.addKeywordAndLink(prdt, stock.getCode(), StkConstant.KEYWORD_CODE_TYPE_STOCK, StkConstant.KEYWORD_LINK_TYPE_MAIN_PRODUCT);
                    }
                }
                CommonUtils.setIndexToTempFile("task_stock_main_product.txt", i+1);
            }catch(Exception e){
                log.error("initCNMainProduct error:"+stock.getCode(), e);
            }
        }
    }

    public void initHKBaseInfo(){
        initHKStocks();
        for(Stock stock : this.stocksHK){
            log.info("initHKBaseInfo:" + stock.getCode());
            try {
                StkEntity stkEntity = stkRepository.findById(stock.getCode()).orElse(null);
                if(stkEntity != null) {
                    this.updateStockBasicInfo(stkEntity);
                }
            }catch(Exception e){
                log.error("initHKBaseInfo error:"+stock.getCode(), e);
            }
        }
    }

    public void initUSBaseInfo(){
        initUSStocks();
        for(Stock stock : this.stocksUS){
            log.info("initUSBaseInfo:" + stock.getCode());
            try {
                // 已经实现相同功能： initUStkFromFinviz
                /*StkEntity stkEntity = stkRepository.findById(stock.getCode()).orElse(null);
                if(stkEntity != null) {
                    this.updateStockBasicInfo(stkEntity);
                }*/
            }catch(Exception e){
                log.error("initUSBaseInfo error:"+stock.getCode(), e);
            }
        }
    }
}
