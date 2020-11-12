package com.stk123.spring.control;

import com.stk123.model.Index;
import com.stk123.model.K;
import com.stk123.spring.dto.StkDto;
import com.stk123.spring.service.IndexService;
import com.stk123.spring.service.IndustryService;
import com.stk123.tool.util.StkUtils;
import com.stk123.task.XueqiuUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

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

    @Autowired
    protected DataSource ds;

    @RequestMapping("")
    public String index(HttpServletRequest request){
        //log.info("id="+id);
        //ServletContext servletContext = request.getSession().getServletContext();
        //WebApplicationContext context = (WebApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        /*StkIndustryTypeEntity industryTypeEntity = industryService.findStkIndustryType(124618);
        log.info(industryTypeEntity.getName());
        viewAllRequestMapping(request);*/

        return "k";
    }

    /**
     * http://webquoteklinepic.eastmoney.com/GetPic.aspx?token=&nid=1.600000&type=D&unitWidth=-6&ef=&formula=MACD&imageType=KXL&_=1599294174363
     */
    @RequestMapping("/show/{codes}")
    @ResponseBody
    public List<StkDto> show(@PathVariable("codes")String codes){
        //log.info("codes:"+codes);
        List<StkDto> list = new ArrayList<StkDto>();
        if(!StringUtils.isEmpty(codes)) {
            String[] cs = StringUtils.split(StringUtils.replace(codes," ", ""), ",");
            List<StkDto> stks = indexService.findStkByCode(Arrays.asList(cs));
            for(StkDto stk : stks){
                String url = null;
                if(StringUtils.length(stk.getCode()) == 5){
                    url = "http://webquoteklinepic.eastmoney.com/GetPic.aspx?imageType=KXL&nid=116."+stk.getCode()+"&token=&type=%s&unitWidth=-6&ef=&formula=MACD";
                }else {
                    String code = (Index.getLocation(stk.getCode()) == Index.SH ? 1 : 0) + "." + stk.getCode();
                    url = "http://webquoteklinepic.eastmoney.com/GetPic.aspx?token=&nid=" + code + "&type=%s&unitWidth=-6&ef=&formula=MACD&imageType=KXL&_=" + StkUtils.now.getTime();
                }
                stk.setDailyUrl(String.format(url, "D"));
                stk.setWeekUrl(String.format(url, "W"));
                stk.setMonthUrl(String.format(url, "M"));
                stk.setNameAndCodeLink(StkUtils.wrapCodeAndName(stk.getCode(), stk.getName()));
                list.add(stk);
            }
        }
        return list;
    }

    @RequestMapping("/xueqiu/{name}")
    @ResponseBody
    public List<StkDto> xueqiu(@PathVariable("name")String name) throws Exception {
        Set<String> codes = XueqiuUtils.getFollowStks(name);
        return show(StringUtils.join(codes, ","));
    }

    /**
     * fromDate must after toDate
     */
    @RequestMapping("/{code}")
    @ResponseBody
    public List<K> getKs(@PathVariable("code")String code,
                         @RequestParam(value = "type", required = false, defaultValue = "1")int type,
                         @RequestParam(value = "days", required = false, defaultValue = "0")int days,
                         @RequestParam(value = "fromDate", required = false)String fromDate,
                         @RequestParam(value = "toDate", required = false)String toDate) throws Exception {
        Connection conn = null;
        try {
            conn = ds.getConnection();
            Index index = new Index(conn, code);
            List<K> ks = null;
            switch (type){
                case 1: ks = index.getKs(); break;
                case 2: ks = index.getKsWeekly(true); break;
                case 3: ks = index.getKsMonthly(true); break;
            }
            if(StringUtils.isEmpty(fromDate)) {
                return ks.subList(0, days);
            }else{
                int fromIndex = index.indexOf(fromDate);
                int toIndex = fromIndex + days;
                if(!StringUtils.isEmpty(toDate)) {
                    toIndex = index.indexOf(toDate);
                }
                return ks.subList(fromIndex, toIndex);
            }
        }finally {
            conn.close();
        }
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
