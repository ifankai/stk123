package com.stk123.spring.control;

import com.stk123.spring.SpringUtils;
import com.stk123.spring.dto.checkpoints.CheckPoint;
import com.stk123.spring.dto.checkpoints.CheckResult;
import com.stk123.spring.service.CheckService;
import com.stk123.spring.support.checkpoints.CheckCashRevenueRatio;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/check")
@CommonsLog
public class CheckControl {

    @Autowired
    private CheckService checkService;

    @RequestMapping("")
    @ResponseBody
    public List<CheckPoint> getAllChecks(String code) {
        List<CheckPoint> cps = new ArrayList<CheckPoint>();
        cps.add(SpringUtils.getBean(CheckCashRevenueRatio.class).getCheckPoint()); //"现金收入比（是否大于50%）："
        return cps;
    }

    @RequestMapping("/{code}/{checkpoint}")
    @ResponseBody
    public CheckResult check(@PathVariable("code")String code, @PathVariable("checkpoint")String beanName) {
        return checkService.check(code, beanName);
    }
}
