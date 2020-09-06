package com.stk123.spring.control;

import com.stk123.spring.jpa.entity.StkIndustryTypeEntity;
import com.stk123.spring.service.IndustryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * //webquoteklinepic.eastmoney.com/GetPic.aspx?token=&nid=1.600000&type=D&unitWidth=-6&ef=&formula=MACD&imageType=KXL&_=1599294174363
 */

@Controller
@RequestMapping("/k")
public class KControl {

    private static final Log log = LogFactory.getLog(KControl.class);

    @Autowired
    private IndustryService industryService;

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("")
    public String index(HttpServletRequest request){
        //log.info("id="+id);
        //ServletContext servletContext = request.getSession().getServletContext();
        //WebApplicationContext context = (WebApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        StkIndustryTypeEntity industryTypeEntity = industryService.findStkIndustryType(124618);
        log.info(industryTypeEntity.getName());

        viewAllRequestMapping(request);

        return "k";
    }

    @RequestMapping(value="/view/{id}")
    public String view2(@PathVariable("id")String id){
        log.info("id:"+id);
        return "k";
    }

    public void viewAllRequestMapping(HttpServletRequest request) {
        Set<String> noLoginUrlSet = new HashSet<>();
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();// 就是这个
        for (RequestMappingInfo rmi : handlerMethods.keySet()) {
            HandlerMethod handlerMethod = handlerMethods.get(rmi);
            //if (handlerMethod.hasMethodAnnotation(NoLogin.class)) {
            PatternsRequestCondition prc = rmi.getPatternsCondition();
            Set<String> patterns = prc.getPatterns();
            noLoginUrlSet.addAll(patterns);
            //}
        }
        for(String s : noLoginUrlSet){
            System.out.println(s);
        }
    }
}
