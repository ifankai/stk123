package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.json.View;
import com.stk123.repository.StkKlineRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/k")
@CommonsLog
public class KController {

    @Autowired
    private StkKlineRepository stkKlineRepository;

    /**
     * @return 返回格式： RequestResult:{..., data:["600600":[], "000001":[]]}
     * @throws Exception
     */
    @RequestMapping("/{code}")
    @ResponseBody
    @JsonView(View.Internal.class)
    public RequestResult<LinkedHashMap<String, List<StkKlineEntity>>> getKs(@PathVariable("code")String code,
                                                                             @RequestParam(value = "type", required = false, defaultValue = "1")int type,
                                                                             @RequestParam(value = "days", required = false, defaultValue = "100")int days,
                                                                             @RequestParam(value = "fromDate", required = false)String fromDate,
                                                                             @RequestParam(value = "toDate", required = false)String toDate) throws Exception {
        List<StkKlineEntity> ks = null;
        String[] codes = StringUtils.split(code, ",");
        LinkedHashMap<String, List<StkKlineEntity>> results = new LinkedHashMap<>();
        switch (type){
            case 1:
            case 2:
            case 3:
                for(String c : codes) {
                    ks = stkKlineRepository.queryTopNByCodeOrderByKlineDateDesc(days, c);
                    results.put(c, ks);
                }
        }
        return RequestResult.success(results);
    }

}
