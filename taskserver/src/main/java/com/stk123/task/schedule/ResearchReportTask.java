package com.stk123.task.schedule;

import com.stk123.common.util.EmailUtils;
import com.stk123.common.util.HtmlUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.Text;
import com.stk123.model.core.Stock;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.StkConstant;
import com.stk123.service.core.StockService;
import com.stk123.util.ExceptionUtils;
import com.stk123.util.HttpUtils;
import com.stk123.util.ServiceUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Header;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@Setter
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResearchReportTask extends AbstractTask {

    @Autowired
    private StkTextRepository stkTextRepository;
    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StockService stockService;

    private List<Stock> stocksCN = null;

    private String code;

    @Override
    public void register() {
        this.run(this::execute);
    }

    public void execute(){
        try{
            log.info("ResearchReportTask start...");
            //getReportByType("1", StkConstant.TEXT_SUB_TYPE_COMPANY_RESEARCH);
            getResearchReportFromHibor();
            getReportByType("2", StkConstant.TEXT_SUB_TYPE_INDUSTRY_RESEARCH);
            log.info("ResearchReportTask end...");
        } catch (Exception e) {
            log.error("ResearchReportTask", e);
            EmailUtils.send("ResearchReportTask Error", ExceptionUtils.getExceptionAsString(e));
        }
    }

    /**
     * @param type 1:公司调研  2:行业分析
     * @param subType select * from stk_dictionary where type=400
     */
    public void getReportByType(String type, int subType) throws Exception {
        int pageNum = 1;
        do{
            boolean flag = false;
            String page = HttpUtils.get("http://www.hibor.com.cn/elitelist_"+pageNum+"_"+type+".html", null, "gb2312");
            //System.out.println(page);
            List<Node> divNodes = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "tr", "class", "trContent");
            for(Node div : divNodes){
                //System.out.println(span.toHtml());
                Node titleNode = HtmlUtils.getNodeByTagName(div, "a");
                String titleLink = HtmlUtils.getAttribute(titleNode, "href");
                String s = HttpUtils.get("http://www.hibor.com.cn"+titleLink, null, "gb2312");
                Node contentNode = HtmlUtils.getNodeByAttribute(s, null, "class", "abstruct-info");
                String content = contentNode==null?null:contentNode.toHtml();
                //System.out.println(content);
                String title = ServiceUtils.wrapLink(HtmlUtils.getAttribute(titleNode, "title"), "http://www.hibor.com.cn"+titleLink) ;

                String code = null;
                if("1".equals(type)){
                    code = StringUtils.split(titleNode.toPlainTextString(), "-")[2];
                }

                List<Node> tds = HtmlUtils.getNodeListByTagName(div, "td");
                Node timeNode = tds.get(tds.size()-1);

                //System.out.println(pageNum+","+code+","+title+","+timeNode.toPlainTextString());
                long cnt = 0;
                StkTextEntity stkTextEntity = stkTextRepository.findFirstBySubTypeAndTitle(subType, title);
                if(stkTextEntity == null) {
                    stkTextEntity = new StkTextEntity();
                    stkTextEntity.setType(StkConstant.TEXT_TYPE_REPORT);
                    stkTextEntity.setSubType(subType);
                    stkTextEntity.setCodeType(StkConstant.TEXT_CODE_TYPE_STOCK);
                    stkTextEntity.setTitle(title);
                    stkTextEntity.setText(content);
                    stkTextEntity.setInsertTime(new Date());
                    stkTextEntity.setCreatedAt(ServiceUtils.sf_ymd9.parse(timeNode.toPlainTextString()));
                    stkTextEntity.setUserId(1L);
                    if (code != null) {
                        stkTextEntity.setCode(code);
                    }
                    stkTextRepository.save(stkTextEntity);
                }else {
                    flag = true;
                    break;
                }
            }
            if(flag)break;
            pageNum++;
            if(pageNum >= 2){
                break;
            }
        }while(true);

    }

    public void initCNStocks(){
        if(stocksCN == null) {
            List<StockBasicProjection> list = null;
            if(code == null) {
                list =stkRepository.findAllByMarketAndCateOrderByCode(EnumMarket.CN, EnumCate.STOCK);
            }else {
                list = stkRepository.findAllByCodes(ListUtils.createList(code));
            }
            stocksCN = stockService.buildStocksWithProjection(list);
        }
    }

    public void getResearchReportFromHibor() throws Exception {
        initCNStocks();
        for(Stock stock : stocksCN){
            log.info("ResearchReportFromHibor:{}", stock.getCode());
            Map<String, String> params = new HashMap<String, String>();
            params.put("gjc", StringUtils.replace(stock.getName(), " ", ""));
            params.put("sslb", "1");
            params.put("sjfw", "24");
            params.put("ys", "1");
            params.put("cxzd", "bt");
            params.put("px", "sj");
            Map<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put("X-Requested-With", "XMLHttpRequest");
            String cookie = HttpUtils.getCookieByType(StkConstant.COOKIE_HIBOR);
            requestHeaders.put("Cookie", cookie);
            List<Header> respHeaders = new ArrayList<Header>();
            String page = HttpUtils.post("http://www.hibor.com.cn/newweb/HuiSou/sa",params, requestHeaders, "UTF-8", respHeaders);
            //System.out.println(page);
            List<Node> items = HtmlUtils.getNodeListByTagNameAndAttribute(page, null, "div", "class", "result-dataitem");
            for(Node node : items){
                Node titleNode = HtmlUtils.getNodeByTagName(node, "a");
                String titleLink = HtmlUtils.getAttribute(titleNode, "href");
                String s = HttpUtils.get("http://www.hibor.com.cn"+titleLink, null, requestHeaders, "gb2312");
                Node contentNode = HtmlUtils.getNodeByAttribute(s, null, "class", "abstruct-info");
                String content = contentNode==null?null:contentNode.toHtml();
                //System.out.println(content);
                String title = ServiceUtils.wrapLink(titleNode.toPlainTextString(), "http://www.hibor.com.cn"+titleLink) ;
                //System.out.println(title+","+"http://www.hibor.com.cn"+titleLink);
                //System.out.println("sssssssssssssss"+s);
                Node timeNode = HtmlUtils.getNodeByAttribute(s, null, "class", "article-time");
                if(timeNode == null) continue;
                String time = StringUtils.replace(timeNode.toPlainTextString(), "日期：", "");

                StkTextEntity stkTextEntity = stkTextRepository.findFirstBySubTypeAndTitle(StkConstant.TEXT_SUB_TYPE_COMPANY_RESEARCH, title);
                if(stkTextEntity == null) {
                    stkTextEntity = new StkTextEntity();
                    stkTextEntity.setType(StkConstant.TEXT_TYPE_REPORT);
                    stkTextEntity.setSubType(StkConstant.TEXT_SUB_TYPE_COMPANY_RESEARCH);
                    stkTextEntity.setCodeType(StkConstant.TEXT_CODE_TYPE_STOCK);
                    stkTextEntity.setTitle(title);
                    stkTextEntity.setText(content);
                    stkTextEntity.setInsertTime(new Date());
                    stkTextEntity.setCreatedAt(ServiceUtils.sf_ymd9.parse(time));
                    stkTextEntity.setUserId(1L);
                    stkTextEntity.setCode(stock.getCode());
                    stkTextRepository.save(stkTextEntity);
                }
            }
        }
    }
}
