package com.stk123.spring.control;

import com.stk123.model.Index;
import com.stk123.spring.dto.StkDto;
import com.stk123.spring.jpa.entity.StkIndustryTypeEntity;
import com.stk123.spring.service.IndexService;
import com.stk123.spring.service.IndustryService;
import com.stk123.task.StkUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * //webquoteklinepic.eastmoney.com/GetPic.aspx?token=&nid=1.600000&type=D&unitWidth=-6&ef=&formula=MACD&imageType=KXL&_=1599294174363
 */

@Controller
@RequestMapping("/k")
@CommonsLog
public class KControl {

    //private static final Log log = LogFactory.getLog(KControl.class);

    @Autowired
    private IndustryService industryService;

    @Autowired
    private IndexService indexService;

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

    @RequestMapping("/show/{codes}")
    @ResponseBody
    public List<StkDto> show(@PathVariable("codes")String codes){
        List<StkDto> list = new ArrayList<StkDto>();
        if(!StringUtils.isEmpty(codes)) {
            String[] cs = StringUtils.split(StringUtils.replace(codes," ", ""), ",");
            List<StkDto> stks = indexService.findStkByCode(Arrays.asList(cs));
            for(StkDto stk : stks){
                String code = (Index.getLocation(stk.getCode()) == Index.SH ? 1 : 0) + "." + stk.getCode();
                String url = "http://webquoteklinepic.eastmoney.com/GetPic.aspx?token=&nid="+code+"&type=%s&unitWidth=-6&ef=&formula=MACD&imageType=KXL&_="+StkUtils.now.getTime();
                stk.setKDUrl(String.format(url, "D"));
                stk.setKWUrl(String.format(url, "K"));
                stk.setKMUrl(String.format(url, "M"));
                stk.setNameAndCodeLink(StkUtils.wrapCodeAndName(stk.getCode(), stk.getName()));
            }
        }
        return list;
    }

    @RequestMapping(value="/view/{id}")
    public String view(@PathVariable("id")String id){
        log.info("id:"+id);
        return "k";
    }

    public void viewAllRequestMapping(HttpServletRequest request) {
        Set<String> noLoginUrlSet = new HashSet<String>();
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
