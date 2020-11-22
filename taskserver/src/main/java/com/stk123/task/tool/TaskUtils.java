package com.stk123.task.tool;

import com.stk123.common.CommonConstant;
import com.stk123.common.html.HtmlTd;
import com.stk123.model.Index;
import com.stk123.model.bo.StkEarningsNotice;
import com.stk123.model.bo.StkImportInfo;
import com.stk123.model.bo.StkRestricted;
import com.stk123.model.bo.cust.StkFnDataCust;
import com.stk123.util.ServiceUtils;
import com.stk123.service.XueqiuService;
import com.stk123.task.schedule.InitialKLine;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class TaskUtils extends ServiceUtils {

    public static String createHtmlTable(String today,List<Index> indexs) throws Exception {
        if(indexs != null){
            Collections.sort(indexs, new Comparator<Index>(){
                @Override
                public int compare(Index arg0, Index arg1) {
                    try {
                        int market = arg0.getMarket();
                        if(market == 2){
                            int n1 = StringUtils.containsIgnoreCase(arg0.getName(), "etf")?1:0;
                            int n2 = StringUtils.containsIgnoreCase(arg1.getName(), "etf")?1:0;
                            return n1-n2;
                        }
                        return (int)(arg0.getMarketValue() - arg1.getMarketValue());
                    } catch (Exception e) {
                        return 0;
                    }
                }});
        }
        List<List> datas = new ArrayList<List>();
        for(Index index : indexs){
            List list = new ArrayList();
            list.add(index);
            datas.add(list);
        }
        return TaskUtils.createHtmlTable(today, datas, null);
    }

    public static String createHtmlTable(String today, List<List> datas, List<String> addtitle) throws Exception {
        if(datas == null || datas.size() == 0){
            return "";
        }
        String date10 = ServiceUtils.formatDate(ServiceUtils.addDay(ServiceUtils.sf_ymd2.parse(today),-10), ServiceUtils.sf_ymd2);
        int market = 1;
        if(datas.size() > 0){
            Index index = (Index)datas.get(0).get(0);
            market = index.getMarket();
        }
        List<String> titles = new ArrayList<String>();
        titles.add("股票(红色为雪球自选股)");
        titles.add("热度");
        if(market == 1){
            titles.add("毛利率");
            titles.add("资金流向");
            titles.add("业绩预告");
            //titles.add("新闻");
            titles.add("限售解禁");
            titles.add("重组并购");
            titles.add("非公定增");
            titles.add("增持员持");
            titles.add("拐点");
        }else{
            titles.add("ETF");
        }
        titles.add("新增");
        titles.add("删除");
        if(addtitle != null){
            for(String title : addtitle){
                titles.add(title);
            }
        }
        //titles.add("日K线");
        //titles.add("月K线");
        List<Index> indexs = new ArrayList<Index>();
        List<Index> indexsNew = new ArrayList<Index>();
        List<Index> indexsNewEr = new ArrayList<Index>();
        List<Index> indexsGROSS_MARGIN = new ArrayList<Index>();//毛利大的
        List<Index> indexsJLRZZL = new ArrayList<Index>();//净利润增长大的
        List<Index> indexsRestriced = new ArrayList<Index>();
        List<Index> indexsBaiduHot = new ArrayList<Index>();

        List<List<HtmlTd>> data = new ArrayList<List<HtmlTd>>();

        Map alignR = new HashMap();
        alignR.put("align","right");

        for(List element : datas){
            List<HtmlTd> row = new ArrayList<HtmlTd>();
            for(int i=0;i<element.size();i++){
                if(i == 0){
                    Index index = (Index)element.get(i);
                    indexs.add(index);

                    if(market == 1){
                        boolean xqFollowStk = XueqiuService.existingXueqiuFollowStk("全部", index.getCode());
                        if(xqFollowStk && InitialKLine.addToCareStks){
                            InitialKLine.careStks.add(index.getCode());
                        }
                        String cd = xqFollowStk ? "<span style=\"color:red\">"+index.getCode()+"</span>" : index.getCode();
                        row.add(HtmlTd.getInstance(index.getXueqiuLinkByName()+"[<a target='_blank' href='http://"+CommonConstant.HOST_PORT+"/stk?s="+index.getCode()+"'>"+ cd +"</a>]"+"["+ServiceUtils.number2String(index.getTotalMarketValue(),2)+"亿]"+index.isGrowthOrPotentialOrReversionName().substring(0, 1)));
                    }else{
                        row.add(HtmlTd.getInstance("["+ServiceUtils.wrapCodeLink(index.getCode())+"]"+index.getName()));
                    }
                    row.add(HtmlTd.getInstance(index.getStk().getHot().toString(),alignR));
                    if(market == 1){
                        StkFnDataCust fn = index.getFnDataLastestByType(index.FN_GROSS_MARGIN);
                        Double gm = null;
                        if(fn != null){
                            gm = fn.getFnValue();
                        }
                        if(gm != null && gm.doubleValue() >= 50){
                            indexsGROSS_MARGIN.add(index);
                        }
                        //毛利率
                        row.add(HtmlTd.getInstance(ServiceUtils.numberFormat2Digits(gm)+"%", alignR));
                        //资金流向
                        //row.add(HtmlTd.getInstance(StkUtils.numberFormat2Digits(index.getCapitalFlowPercent(today))+"%", alignR));
                        row.add(HtmlTd.getInstance(index.getCapitalFlowImageOnMainAndSuper(10)));
                        if(index.NumberOfCapitalFlowPostive >= 6 && InitialKLine.addToFlowStks){
                            InitialKLine.flowStks.add(index);
                        }
                        List<String> nextEF = null;
                        try{
                            nextEF = index.getEarningsForecastAsList();
                        }catch(Exception e){}
                        if(nextEF != null && nextEF.size() > 3){
                            fn = index.getFnDataLastestByType(index.FN_JLRZZL);
                            if(fn!=null && fn.getFnDate().compareTo(StringUtils.replace(nextEF.get(0), "-", "")) > 0){
                                Double jlr = fn.getFnValue();
                                if(jlr != null && jlr.doubleValue() >= 40){
                                    indexsJLRZZL.add(index);
                                }
                                row.add(HtmlTd.getInstance(ServiceUtils.formatDate(fn.getFnDate())+"["+ServiceUtils.numberFormat2Digits(jlr) +"%]"));
                            }else{
                                double ef = ServiceUtils.percentigeGreatThan(nextEF.get(2));
                                if(ef >= 50){
                                    indexsJLRZZL.add(index);
                                }
                                row.add(HtmlTd.getInstance(nextEF.get(0)+"["+nextEF.get(2) +"]*"));
                            }
                        }else{
                            row.add(HtmlTd.getInstance(""));
                        }
                        //新闻热度
						/*int hot = 0;//baiduSearch(index.getName());
						row.add(HtmlTd.getInstance(String.valueOf(hot), alignR));
						if(hot >= 16){
							indexsBaiduHot.add(index);
						}*/
                        Date start = ServiceUtils.addDay(now, -500);
                        StkRestricted sr = index.isRestrictedDateBetween(start, ServiceUtils.addDay(now, -50));
                        boolean hadReduced = false;
                        String percent = null;
                        List<StkImportInfo> infos = null;
                        if(sr != null){
                            //查看有没有减持过
                            infos = index.getImportInfoAfterDate(190, start);
                            for(StkImportInfo info : infos){
                                if(info.getTitle().contains("减持") && !info.getTitle().contains("不减持")){
                                    hadReduced = true;
                                    break;
                                }
                            }
                            percent = ServiceUtils.number2String(sr.getBanMarketValue()/index.getMarketValue(),2);
                        }
                        row.add(HtmlTd.getInstance((sr != null && !hadReduced)?"未减持("+percent+"%)":(sr != null && hadReduced)?"限售有减("+percent+"%)":""));
                        if(sr != null && !hadReduced && percent != null && Double.parseDouble(percent) >= 0.10){
                            indexsRestriced.add(index);
                        }
                        //重组并购
                        start = ServiceUtils.addDay(now, -180);
                        infos = index.getImportInfoAfterDate(140, start);
                        row.add(HtmlTd.getInstance(infos.size() > 0?"重组并购":""));
                        //非公定增
                        infos = index.getImportInfoAfterDate(150, start);
                        row.add(HtmlTd.getInstance(infos.size() > 0?"非公定增":""));
                        //增持员工持股
                        infos = index.getImportInfoAfterDate(120, start);
                        row.add(HtmlTd.getInstance(infos.size() > 0?"增持员持":""));
                        //拐点
                        infos = index.getImportInfoAfterDate(210, start);
                        row.add(HtmlTd.getInstance(infos.size() > 0?"拐点":""));

                    }else{
                        row.add(HtmlTd.getInstance(StringUtils.containsIgnoreCase(index.getName(), "etf")?"ETF":""));
                    }
                    row.add(HtmlTd.getInstance(index.isNew?"新增":""));
                    row.add(HtmlTd.getInstance(index.isDeleted?"删除":""));

                    index.isDeleted = false;
                    if(index.isNew){
                        indexsNew.add(index);
                        StkEarningsNotice en = index.getPerformanceNoticeLatest(ServiceUtils.formatDate(ServiceUtils.addDay(now, -360),ServiceUtils.sf_yyyyMMdd));
                        if(en != null && en.getErLow()!=null && en.getErLow() >= 20){
                            indexsNewEr.add(index);
                        }
                    }
                    index.isNew = false;
                }else{
                    row.add(HtmlTd.getInstance(String.valueOf(element.get(i))));
                }
            }
			/*for(int i=0;i<element.size();i++){
				if(i == 0){
					Index index = (Index)element.get(i);
					row.add(StkUtils.createDailyKLine(index));
					row.add(StkUtils.createMonthlyKLine(index));
				}
			}*/
            data.add(row);
        }
        return ServiceUtils.createTable(titles, data)
                +(indexsNew.size()>0?"<br>(新加入标的):<br>"+ServiceUtils.join(indexsNew, ","):"")
                +(indexsNewEr.size()>0?"<br>(新加入且业绩预告大于20%标的):<br>"+ServiceUtils.join(indexsNewEr, ","):"")
                +(indexsGROSS_MARGIN.size()>0?"<br>(毛利>=50%标的):<br>"+ServiceUtils.join(indexsGROSS_MARGIN, ","):"")
                +(indexsJLRZZL.size()>0?"<br>(净利润增长>=40%标的):<br>"+ServiceUtils.join(indexsJLRZZL, ","):"")
                +(indexsRestriced.size()>0?"<br>(解禁未减持(数量>=0.1%)标的):<br>"+ServiceUtils.join(indexsRestriced, ","):"")
                +(indexsBaiduHot.size()>0?"<br>(百度新闻热点标的):<br>"+ServiceUtils.join(indexsBaiduHot, ","):"")

                +((indexsGROSS_MARGIN.size()+indexsJLRZZL.size()+indexsRestriced.size()+indexsBaiduHot.size())>0?
                "<br><br>(有亮点标的):<br>"+
                        ServiceUtils.join(combine(indexsGROSS_MARGIN,indexsJLRZZL,indexsRestriced,indexsBaiduHot), ","):"")
                +"<br><br>(全部标的)<br>"+ ServiceUtils.join(indexs, ",");
    }

}
