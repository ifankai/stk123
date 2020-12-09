package com.stk123.service;

import com.stk123.common.util.JsonUtils;
import com.stk123.entity.StkKlineEntity;
import com.stk123.model.core.Stock;
import com.stk123.repository.StkKlineRepository;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BarService {

    @Autowired
    private StkKlineRepository stkKlineRepository;

    public StkKlineEntity saveIfNotExisting(StkKlineEntity stkKlineEntity) {
        Optional<StkKlineEntity> entity = stkKlineRepository.findById(new StkKlineEntity.CompositeKey(stkKlineEntity.getCode(),stkKlineEntity.getKlineDate()));
        if(!entity.isPresent()){
            return stkKlineRepository.save(stkKlineEntity);
        }
        return entity.get();
    }

    public void initKLines(Stock stock, int n) throws Exception {
        Date now = new Date();
        String startDate = ServiceUtils.formatDate(ServiceUtils.addDay(now, -n),ServiceUtils.sf_ymd2);

        String code = stock.getCode();
        if(stock.isMarketCN()){
            if(stock.getCate() == Stock.EnumCate.INDEX || stock.getCate() == Stock.EnumCate.STOCK){
                String tmp = null;
                if(code.length() == 6){
                    tmp = (stock.isPlaceSH()?"sse":"szse") + (code.equals("999999")?"000001":code);

                }else if(code.length() == 8){
                    tmp = (stock.isPlaceSH()?"sse":"ssz") + StringUtils.substring(code, 2, 8);
                }

                String page = HttpUtils.get("http://webstock.quote.hermes.hexun.com/a/kline?code="+tmp+"&start="+ServiceUtils.getToday()+"150000&number=-1000&type=5&callback=callback", null, "gb2312");
                //System.out.println(page);
                List<List> datas = JsonUtils.getList4Json("["+StringUtils.substringBetween(page, "[[", "]]")+"]]", ArrayList.class );
                for(List data:datas){
                    if(data == null)return;
                    String date = String.valueOf(data.get(0));
                    date = date.replaceAll("000000", "");
                    if(date.compareTo(ServiceUtils.getToday()) > 0)continue;
                    if(date.compareTo(startDate) < 0)continue;

                    StkKlineEntity stkKlineEntity = new StkKlineEntity();
                    stkKlineEntity.setCode(code);
                    stkKlineEntity.setKlineDate(StringUtils.substring(String.valueOf(data.get(0)), 0, 8));
                    stkKlineEntity.setOpen(Double.parseDouble(String.valueOf(data.get(2)))/100.0);
                    stkKlineEntity.setClose(Double.parseDouble(String.valueOf(data.get(3)))/100.0);
                    stkKlineEntity.setLastClose(Double.parseDouble(String.valueOf(data.get(1)))/100.0);
                    stkKlineEntity.setHigh(Double.parseDouble(String.valueOf(data.get(4)))/100.0);
                    stkKlineEntity.setLow(Double.parseDouble(String.valueOf(data.get(5)))/100.0);
                    stkKlineEntity.setVolumn(Double.parseDouble(String.valueOf(data.get(6)))/100.0);
                    stkKlineEntity.setAmount(Double.parseDouble(String.valueOf(data.get(7)))/100.0);
                    this.saveIfNotExisting(stkKlineEntity);

                }

            }

            //setCloseChange();
        }
    }



    //from finance.qq.com
    public void initKLine(Stock stock) throws Exception {
        String code = stock.getCode();
        Stock.EnumCate cate = stock.getCate();

        if(stock.isMarketCN()){
            boolean isSH = stock.isPlaceSH();
            String codeWithPlace = stock.getCodeWithPlace();
            String page = null;
            if(cate == Stock.EnumCate.STOCK){
                codeWithPlace = codeWithPlace.toLowerCase();
            }else if(cate == Stock.EnumCate.INDEX){
                if("SH999999".equals(code)){
                    codeWithPlace = "sh000001";
                }else{
                    if(code.length() == 8){
                        codeWithPlace = (isSH?"sh":"sz")+StringUtils.substring(code, 2, 8);
                    }else{
                        codeWithPlace = codeWithPlace.toLowerCase();
                    }
                }
            }else if(cate == Stock.EnumCate.INDEX_10jqka){
                //http://q.10jqka.com.cn/stock/gn/
                //http://q.10jqka.com.cn/gn/detail/code/308709/
                page = HttpUtils.get("http://d.10jqka.com.cn/v2/line/bk_"+code+"/01/last.js", null, "gbk");
                if("404".equals(page))return;
                String data = StringUtils.substringBetween(page, "data\":\"", "\"})");
                String[] ks = StringUtils.split(data, ";");
                if(ks != null){
                    for(String kk : ks){
                        String[] k = kk.split(",");

                        StkKlineEntity stkKlineEntity = new StkKlineEntity();
                        stkKlineEntity.setCode(code);
                        stkKlineEntity.setKlineDate(k[0]);
                        stkKlineEntity.setOpen(Double.parseDouble(k[1]));
                        stkKlineEntity.setClose(Double.parseDouble(k[4]));
                        //stkKlineEntity.setLastClose(Double.parseDouble(String.valueOf(data.get(1)))/100.0);
                        stkKlineEntity.setHigh(Double.parseDouble(k[2]));
                        stkKlineEntity.setLow(Double.parseDouble(k[3]));
                        stkKlineEntity.setVolumn(Double.parseDouble(k[5]));
                        stkKlineEntity.setAmount(Double.parseDouble(k[6])/100);
                        this.saveIfNotExisting(stkKlineEntity);


                   }
                }

                page = HttpUtils.get("http://d.10jqka.com.cn/v2/line/bk_"+code+"/01/today.js", null, "gbk");
                data = "{"+StringUtils.substringBetween(page, ":{", "}}")+"}";
                Map<String, String> map = (Map)JsonUtils.getObject4Json(data, Map.class, null);

                if(map != null){
                    String date = String.valueOf(map.get("1"));
                    StkKlineEntity stkKlineEntity = new StkKlineEntity();
                    stkKlineEntity.setCode(code);
                    stkKlineEntity.setKlineDate(date);
                    stkKlineEntity.setOpen(NumberUtils.createDouble(map.get("7")));
                    stkKlineEntity.setClose(NumberUtils.createDouble(map.get("11")));
                    //stkKlineEntity.setLastClose(Double.parseDouble(String.valueOf(data.get(1)))/100.0);
                    stkKlineEntity.setHigh(NumberUtils.createDouble(map.get("8")));
                    stkKlineEntity.setLow(NumberUtils.createDouble(map.get("9")));
                    stkKlineEntity.setVolumn(NumberUtils.createDouble(map.get("13")));
                    stkKlineEntity.setAmount(map.get("19") == null ? null : NumberUtils.toDouble(map.get("19"))/100);
                    this.saveIfNotExisting(stkKlineEntity);
                }

                return;
            }
            //腾讯股票接口 http://qt.gtimg.cn/&q=sh600600
            page = HttpUtils.get("http://qt.gtimg.cn/&q="+codeWithPlace, null, "");
            if(page != null && page.length() > 0){
                String str = StringUtils.substringBetween(page, "\"", "\"");
                //System.out.println(str);
                String[] ss = str.split("~");

                if(ss.length > 40){
                    String date = ss[30].substring(0, 8);
                    double close = Double.parseDouble(ss[3]);
                    double open = Double.parseDouble(ss[5]);
                    if(close == 0.0 || open == 0.0){//停牌
                        //System.out.println("停牌");
                        return;
                    }

                    double lastClose = Double.parseDouble(ss[4]);
                    double volume = Double.parseDouble(ss[6]) * 100;
                    double percentage = Double.parseDouble(ss[32]);
                    double high = Double.parseDouble(ss[33]);
                    double low = Double.parseDouble(ss[34]);
                    double amount = Double.parseDouble(ss[37]) * 10000;
                    double hsl = 0;
                    double pettm = 0;
                    double pbttm = 0;
                    double psttm = 0;
                    if(stock.getCate() == Stock.EnumCate.STOCK){
                        hsl = Double.parseDouble(ss[38]);
                        if(ss[39].length()>0){
                            pettm = Double.parseDouble(ss[39]);
                        }
                        if(ss[46].length()>0){
                            pbttm = Double.parseDouble(ss[46]); //sina的pb不对
                            /*StkKline kTmp = this.getK().getKline();
                            if(kTmp != null){
                                Double pbLast = kTmp.getPbTtm();
                                if(pbLast != null && Math.abs((pbLast-pbttm)/pbLast)>0.2 ){
                                    pbttm = this.getPB();
                                }
                            }*/
                        }
                        //psttm = this.getPSTTM();
                    }
                    //System.out.println("date="+date+",lastClose="+lastClose+",open="+open+",close="+close+",high="+high+",low="+low+",volume="+volume+",amount="+amount+",percentage="+percentage+",hsl="+hsl+",pettm="+pettm+",pbttm="+pbttm);

                    StkKlineEntity stkKlineEntity = new StkKlineEntity();
                    stkKlineEntity.setCode(code);
                    stkKlineEntity.setKlineDate(date);
                    stkKlineEntity.setOpen(open);
                    stkKlineEntity.setClose(close);
                    stkKlineEntity.setLastClose(lastClose);
                    stkKlineEntity.setHigh(high);
                    stkKlineEntity.setLow(low);
                    stkKlineEntity.setVolumn(volume);
                    stkKlineEntity.setAmount(amount);
                    stkKlineEntity.setHsl(hsl);
                    stkKlineEntity.setPeTtm(pettm);
                    stkKlineEntity.setPbTtm(pbttm);
                    stkKlineEntity.setPercentage(percentage);
                    //stkKlineEntity.setPsTtm(psttm);
                    this.saveIfNotExisting(stkKlineEntity);

                    //this.setCloseChange();

                }

            }
        }else if(stock.isMarketUS()){
            String tmpCode = code.replace(".", "");
            String page = HttpUtils.get("http://qt.gtimg.cn/&q=us"+tmpCode, null, "GBK");

            if(page != null && page.length() > 0){
                String str = StringUtils.substringBetween(page, "\"", "\"");
                //System.out.println(str);
                if(str == null)return;
                String[] ss = str.split("~");

                if(ss.length > 40){
                    double close = Double.parseDouble(ss[3]);
                    double open = Double.parseDouble(ss[5]);
                    if(close == 0.0 || open == 0.0){//停牌
                        //System.out.println("停牌");
                        return;
                    }
                    String date = StringUtils.replace(ss[30].substring(0, 10), "-", "");
                    double lastClose = Double.parseDouble(ss[4]);
                    double volume = Double.parseDouble(ss[6]);
                    double percentage = Double.parseDouble(ss[32]);
                    double high = Double.parseDouble(ss[33]);
                    double low = Double.parseDouble(ss[34]);
                    double amount = Double.parseDouble(ss[37]) * 10000;
                    double pettm = 0;
                    if(ss[39].length()>0 && ServiceUtils.isAllNumericOrDot(ss[39])){
                        pettm = Double.parseDouble(ss[39]);
                    }
                    //System.out.println("date="+date+",lastClose="+lastClose+",open="+open+",close="+close+",high="+high+",low="+low+",volume="+volume+",amount="+amount+",percentage="+percentage+",pettm="+pettm);

                    StkKlineEntity stkKlineEntity = new StkKlineEntity();
                    stkKlineEntity.setCode(code);
                    stkKlineEntity.setKlineDate(date);
                    stkKlineEntity.setOpen(open);
                    stkKlineEntity.setClose(close);
                    stkKlineEntity.setLastClose(lastClose);
                    stkKlineEntity.setHigh(high);
                    stkKlineEntity.setLow(low);
                    stkKlineEntity.setVolumn(volume);
                    stkKlineEntity.setAmount(amount);
                    //stkKlineEntity.setHsl(hsl);
                    stkKlineEntity.setPeTtm(pettm);
                    //stkKlineEntity.setPbTtm(pbttm);
                    stkKlineEntity.setPercentage(percentage);
                    //stkKlineEntity.setPsTtm(psttm);
                    this.saveIfNotExisting(stkKlineEntity);

                    //计算PB
                    /*Double bv = JdbcUtils.first(this.getConnection(), "select fn_value from stk_fn_data_us where type=4013 and code=? order by fn_date desc", params, Double.class);
                    Double pbttm = (bv == null || bv == 0)?null:close/bv;

                    Double salePerShare = JdbcUtils.first(this.getConnection(), "select fn_value from stk_fn_data_us where type=4005 and code=? order by fn_date desc", params, Double.class);
                    Double psttm = (salePerShare == null || salePerShare == 0)?null:close/salePerShare;*/

                 }
            }

        }else if(stock.isMarketHK()){
            String page = HttpUtils.get("http://qt.gtimg.cn/&q=hk"+code, null, "GBK");

            if(page != null && page.length() > 0){
                String str = StringUtils.substringBetween(page, "\"", "\"");
                //System.out.println(str);
                if(str == null)return;
                String[] ss = str.split("~");

                if(ss.length > 40){
                    double close = Double.parseDouble(ss[3]);
                    double open = Double.parseDouble(ss[5]);
                    if(close == 0.0 || open == 0.0){//停牌
                        //System.out.println("停牌");
                        return;
                    }
                    String date = StringUtils.replace(ss[30].substring(0, 10), "/", "");
                    double lastClose = Double.parseDouble(ss[4]);
                    double volume = Double.parseDouble(ss[6]);
                    double percentage = Double.parseDouble(ss[32]);
                    double high = Double.parseDouble(ss[33]);
                    double low = Double.parseDouble(ss[34]);
                    double amount = Double.parseDouble(ss[37]);
                    double pettm = 0;
                    if(ss[39].length()>0 && ServiceUtils.isAllNumericOrDot(ss[39])){
                        pettm = Double.parseDouble(ss[39]);
                    }
                    //System.out.println("date="+date+",lastClose="+lastClose+",open="+open+",close="+close+",high="+high+",low="+low+",volume="+volume+",amount="+amount+",percentage="+percentage+",pettm="+pettm);

                    StkKlineEntity stkKlineEntity = new StkKlineEntity();
                    stkKlineEntity.setCode(code);
                    stkKlineEntity.setKlineDate(date);
                    stkKlineEntity.setOpen(open);
                    stkKlineEntity.setClose(close);
                    stkKlineEntity.setLastClose(lastClose);
                    stkKlineEntity.setHigh(high);
                    stkKlineEntity.setLow(low);
                    stkKlineEntity.setVolumn(volume);
                    stkKlineEntity.setAmount(amount);
                    //stkKlineEntity.setHsl(hsl);
                    stkKlineEntity.setPeTtm(pettm);
                    //stkKlineEntity.setPbTtm(pbttm);
                    stkKlineEntity.setPercentage(percentage);
                    //stkKlineEntity.setPsTtm(psttm);
                    this.saveIfNotExisting(stkKlineEntity);

                }
            }
        }
    }



}
