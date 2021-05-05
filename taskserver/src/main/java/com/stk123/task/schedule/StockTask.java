package com.stk123.task.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.common.CommonUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.entity.StkEntity;
import com.stk123.entity.StkHolderEntity;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.StkHolderRepository;
import com.stk123.repository.StkRepository;
import com.stk123.service.core.ErrorService;
import com.stk123.service.core.HttpService;
import com.stk123.service.core.StockService;
import com.stk123.util.ServiceUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.tags.TableTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
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
    private StkHolderRepository stkHolderRepository;

    private List<Stock> stocksCN = null;

    @Override
    public void register() {
        this.runByName("initCNNewStock", this::initCNNewStock);
        this.runByName("initCNIndustryEasymoney", this::initCNIndustryEasymoney);
        this.runByName("initCNHolder", this::initCNHolder);
    }

    public void initCNHolder() {
        initCNStocks();
        for(Stock stock : stocksCN) {
            System.out.println(stock.getCode());
            String page = httpService.getString("http://basic.10jqka.com.cn/"+stock.getCode()+"/holder.html", "gbk");
            //System.out.println("page="+page);
            try {
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
                    stkHolderRepository.save(stkHolderEntity);
                    //System.out.println(stkHolderEntity);
                }
            } catch (Exception e) {
                log.error("", e);
                //System.out.println(stock.getCode());
                break;
            }
        }
    }

    public void initCNStocks(){
        if(stocksCN == null) {
            List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(Stock.EnumMarket.CN, Stock.EnumCate.STOCK);
            //List<StockBasicProjection> list = stkRepository.findAllByCodes(ListUtils.createList("600600"));
            stocksCN = stockService.buildStocksWithProjection(list);
        }
    }

    public void initCNNewStock() {
        String[] ss = getAllStocksCode().split(",");
        List params = new ArrayList();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<ss.length;i++){
            sb.append(ss[i]+",");
            if((i+1) % 50 == 0 || i == ss.length-1){
                String page = httpService.getString("http://hq.sinajs.cn/list="+sb);
                log.info(page);
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
                            stkEntity.setMarket(Stock.EnumMarket.CN.getMarket());
                            stkEntity.setCate(Stock.EnumCate.STOCK.getCate());
                            stkEntity.setPlace(code.startsWith("6")?Stock.EnumPlace.SH.getPlace():Stock.EnumPlace.SZ.getPlace());
                            log.info("[新股]"+code+","+name);
                        }else{
                            stkEntity = stk.get();
                            stkEntity.setCode(code);
                            stkEntity.setName(name);
                        }
                        stkEntity = stkRepository.save(stkEntity);

                        try{
                            updateCNStockBasicInfo(stkEntity);
                        }catch(Exception e){
                            log.error("", e);
                            errorService.logErrorIfNoSimilarError(code, e);
                        }
                    }
                }
                sb = new StringBuffer();
            }
        }

    }

    //http://quote.eastmoney.com/sz002572.html?from=beta
    //http://push2.eastmoney.com/api/qt/stock/get?ut=&invt=2&fltt=2&fields=f43,f57,f58,f169,f170,f46,f44,f51,f168,f47,f164,f163,f116,f60,f45,f52,f50,f48,f167,f117,f71,f161,f49,f530,f135,f136,f137,f138,f139,f141,f142,f144,f145,f147,f148,f140,f143,f146,f149,f55,f62,f162,f92,f173,f104,f105,f84,f85,f183,f184,f185,f186,f187,f188,f189,f190,f191,f192,f107,f111,f86,f177,f78,f110,f262,f263,f264,f267,f268,f250,f251,f252,f253,f254,f255,f256,f257,f258,f266,f269,f270,f271,f273,f274,f275,f127,f199,f128,f193,f196,f194,f195,f197,f80,f280,f281,f282,f284,f285,f286,f287,f292&secid=0.002572&cb=jQuery112409607445745814001_1597039350332&_=1597039350333
    //jQuery({"rc":0,"rt":4,"svr":182993475,"lt":1,"full":1,"data":{"f84":1364182800.0,"f189":19930827}});
    /**
     * http://push2.eastmoney.com/api/qt/stock/get?ut=&invt=2&fltt=2&fields=f84,f189&secid=0.002572&cb=jQuery&_=1597039350333
     * f84:  总股本
     * f189: 上市时间
     */
    public void updateCNStockBasicInfo(StkEntity stkEntity) throws Exception {
        String scode = (Stock.EnumPlace.isSH(stkEntity.getPlace())?"1.":"0.") + stkEntity.getCode();
        String url = "http://push2.eastmoney.com/api/qt/stock/get?ut=&invt=2&fltt=2&fields=f84,f189&secid="+ scode +"&cb=jQuery&_="+new Date().getTime();
        String page = httpService.getString(url);
        System.out.println("updateCNStockBasicInfo:"+page);
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


    //http://quote.eastmoney.com/center/boardlist.html#concept_board
    public void initCNIndustryEasymoney() {
        //http://59.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112407845542377068357_1615380136035&pn=1&pz=2000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:90+t:3+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152,f124,f107,f104,f105,f140,f141,f207,f208,f209,f222&_=1615380136036
        long time = new Date().getTime();
        String url = "http://59.push2.eastmoney.com/api/qt/clist/get?cb=jQuery112407845542377068357_" + time +
                "&pn=1&pz=2000&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2&fid=f3&fs=m:90+t:3+f:!50&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f26,f22,f33,f11,f62,f128,f136,f115,f152,f124,f107,f104,f105,f140,f141,f207,f208,f209,f222" +
                "&_="+time;
        try {
            String page = httpService.getString(url);
            String json = "{"+StringUtils.substringBetween(page, "({", "})")+"}";
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(json, HashMap.class);
            Map datas = (Map)map.get("data");
            List<Map> diff = (List)datas.get("diff");
            for(Map data : diff){

                String code = String.valueOf(data.get("f12"));
                String name = String.valueOf(data.get("f14"));
                Optional<StkEntity> stk = stkRepository.findById(code);
                StkEntity stkEntity;
                if(!stk.isPresent()){
                    stkEntity = new StkEntity();
                    stkEntity.setCode(code);
                    stkEntity.setName(name);
                    stkEntity.setInsertTime(new Date());
                    stkEntity.setMarket(Stock.EnumMarket.CN.getMarket());
                    stkEntity.setCate(Stock.EnumCate.INDEX_eastmoney_gn.getCate());
                    stkEntity.setAddress("eastmoney_gn");
                    log.info("[新股(INDEX_eastmoney_gn)]"+code+","+name);
                }else{
                    stkEntity = stk.get();
                    stkEntity.setCode(code);
                    stkEntity.setName(name);
                }
                stkEntity = stkRepository.save(stkEntity);

            }
        } catch (IOException e) {
            log.error("initCNIndustryEasymoney", e);
        }

    }
}
