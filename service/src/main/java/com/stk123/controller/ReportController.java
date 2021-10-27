package com.stk123.controller;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.CommonUtils;
import com.stk123.entity.StkKlineEntity;
import com.stk123.entity.StkPeEntity;
import com.stk123.entity.StkReportDetailEntity;
import com.stk123.entity.StkReportHeaderEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.json.View;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkPeRepository;
import com.stk123.repository.StkReportDetailRepository;
import com.stk123.repository.StkReportHeaderRepository;
import com.stk123.service.core.ReportService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rpt")
@CommonsLog
public class ReportController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private StkReportDetailRepository stkReportDetailRepository;
    @Autowired
    private StkReportHeaderRepository stkReportHeaderRepository;
    @Autowired
    private StkPeRepository stkPeRepository;
    @Autowired
    private StkKlineRepository stkKlineRepository;

    @GetMapping({"","/","/{reportDate}"})
    @JsonView(View.All.class)
    public RequestResult report(@PathVariable(value = "reportDate", required = false)String reportDate){
        Date rptDate = new Date();
        if(StringUtils.isNotEmpty(reportDate)){
            String date = StringUtils.replace(reportDate, "-", "");
            rptDate = CommonUtils.parseDate(date);
        }
        Map map = reportService.findReportAsMap(CommonUtils.formatDate(rptDate, CommonUtils.sf_ymd2));
        String finalReportDate = (String) map.get("reportDate");
        Map checkedMap = reportService.findReportCheckedAsMap(finalReportDate,
                (List<StkReportDetailEntity>)map.get("currentAllStocksRpsA"),
                (List<StkReportDetailEntity>)map.get("currentAllStocksRpsH"),
                (List<StkReportDetailEntity>)map.get("currentAllStocksRpsU"));
        map.putAll(checkedMap);
        return RequestResult.success(map);
    }

    @PostMapping({"/detail/checked/{id}"})
    public RequestResult reportDetailChecked(@PathVariable(value = "id")String id){
        stkReportDetailRepository.updateCheckedById(Integer.parseInt(id), new Date());
        return RequestResult.success();
    }

    @DeleteMapping({"/detail/checked/{id}"})
    public RequestResult reportDetailCheckedDetlete(@PathVariable(value = "id")String id){
        StkReportDetailEntity stkReportDetailEntity = stkReportDetailRepository.findById(Integer.parseInt(id)).get();
        stkReportDetailEntity.setCheckedTime(null);
        stkReportDetailRepository.save(stkReportDetailEntity);
        return RequestResult.success();
    }

    @PostMapping({"/header/checked/{type}/{reportDate}"})
    public RequestResult reportHeaderChecked(@PathVariable(value = "type")String type, @PathVariable(value = "reportDate")String reportDate){
        stkReportHeaderRepository.updateCheckedByTypeAndReportDate(type, reportDate, new Date());
        return RequestResult.success();
    }

    @DeleteMapping({"/header/checked/{type}/{reportDate}"})
    public RequestResult reportHeaderCheckedDetlete(@PathVariable(value = "type")String type, @PathVariable(value = "reportDate")String reportDate){
        List<StkReportHeaderEntity> headers = stkReportHeaderRepository.findAllByTypeAndReportDate(type, reportDate);
        for(StkReportHeaderEntity stkReportHeaderEntity : headers){
            stkReportHeaderEntity.setCheckedTime(null);
            stkReportHeaderRepository.save(stkReportHeaderEntity);
        }
        return RequestResult.success();
    }

    @GetMapping({"/monitor"})
    public RequestResult monitor(){
        String date = CommonUtils.addDay2String(new Date(), -365);
        List<StkPeEntity> entities = stkPeRepository.findAllByReportDateGreaterThanOrderByReportDateDesc(date);
        List<StkKlineEntity> ks = stkKlineRepository.findAllByCodeAndKlineDateAfterOrderByKlineDateDesc("999999", date);
        Map map = new HashMap(){{
            put("monitor", entities);
            put("ks", ks);
        }};
        return RequestResult.success(map);
    }
}
