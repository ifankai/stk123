package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.CommonUtils;
import com.stk123.model.RequestResult;
import com.stk123.model.json.View;
import com.stk123.repository.StkReportHeaderRepository;
import com.stk123.service.core.ReportService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/rpt")
@CommonsLog
public class ReportController {

    @Autowired
    private ReportService reportService;

    @RequestMapping({"","/","/{reportDate}"})
    @JsonView(View.All.class)
    public RequestResult report(@PathVariable(value = "reportDate", required = false)String reportDate){
        Date rptDate = new Date();
        if(StringUtils.isNotEmpty(reportDate)){
            String date = StringUtils.replace(reportDate, "-", "");
            rptDate = CommonUtils.parseDate(date);
        }
        Map map = reportService.findReportAsMap(CommonUtils.formatDate(rptDate, CommonUtils.sf_ymd2));
        return RequestResult.success(map);
    }
}
