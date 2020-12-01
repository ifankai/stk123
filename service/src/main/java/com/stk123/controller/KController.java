package com.stk123.controller;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
import com.stk123.repository.StkKlineRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/k")
@CommonsLog
public class KController {

    @Autowired
    private StkKlineRepository stkKlineRepository;

    @RequestMapping("/{code}")
    @ResponseBody
    public RequestResult<List<StkKlineEntity>> getKs(@PathVariable("code")String code,
                                                     @RequestParam(value = "type", required = false, defaultValue = "1")int type,
                                                     @RequestParam(value = "days", required = false, defaultValue = "100")int days,
                                                     @RequestParam(value = "fromDate", required = false)String fromDate,
                                                     @RequestParam(value = "toDate", required = false)String toDate) throws Exception {
        List<StkKlineEntity> ks = null;
        switch (type){
            case 1:
            case 2:
            case 3:
                ks = stkKlineRepository.queryTopNByCodeOrderByKlineDateDesc(code, days);
        }
        return RequestResult.success(ks);
    }

}
