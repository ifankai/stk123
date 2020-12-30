package com.stk123.task.schedule;

import com.stk123.common.util.JdbcUtils;
import com.stk123.entity.StkEntity;
import com.stk123.model.bo.Stk;
import com.stk123.model.core.Stock;
import com.stk123.repository.StkRepository;
import com.stk123.service.core.HttpService;
import com.stk123.util.ExceptionUtils;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CommonsLog
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StockTask extends AbstractTask {

    @Autowired
    private HttpService httpService;
    @Autowired
    private StkRepository stkRepository;

    @Override
    public void register() {
        this.register("initCNNewStock", this::initCNNewStock);
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
                        log.info("code="+code+", name="+name);
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
                        stkRepository.save(stkEntity);

                        try{
                            updateCNNewStockInfo(code);
                        }catch(Exception e){
                            //ExceptionUtils.insertLog(conn, code, e);
                        }
                    }
                }
                sb = new StringBuffer();
            }
        }

    }

    public void updateCNNewStockInfo(String code){

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
}
