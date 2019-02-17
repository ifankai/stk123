<%@page import="com.stk123.tool.util.JdbcUtils"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	StkContext sc = StkContext.getContext();
	Index index = sc.getIndex();
  	String stkNameCode = index.getName()+StkConstant.MARK_BLANK_SPACE+StkConstant.MARK_BRACKET_LEFT+index.getCode()+StkConstant.MARK_BRACKET_RIGHT;
  	pageContext.setAttribute(StkConstant.PAGE_TITLE, stkNameCode); 
%>
<%@include file="/common/header.jsp" %>
<style type="text/css">
a {
text-decoration:none;
color:#0055a2;
word-break:break-all;
outline:none;
}
#text_stk,#text_list{
color:#222;
line-height:1.5;
}
.table-stk tr{
color:#CC0033;
}
</style>
<body>
<%@include file="/common/nav.jsp" %>
<%
	boolean isAdmin = false;
	if(_user != null && _user.getStkUser().getId().intValue() == 1){
		isAdmin = true;
	}
%>
<input id="scode" type="hidden" value="<%=index.getCode() %>" />
<input id="sname" type="hidden" value="<%=index.getName() %>" />

<div class="container" role="main">
<div class="content">
  <div class="page-header">
    <h3><font color="red"><%=index.getName() %></font><%=StkConstant.MARK_BLANK_SPACE+StkConstant.MARK_BRACKET_LEFT+index.getCode()+StkConstant.MARK_BRACKET_RIGHT%></h3>
  </div>
  <div class="page-container">
    <div class="row stkbaseinfo">
      <article class="span10">
        <blockquote>
          <table><tr>
          <td width="42px"><span>收盘：</span></td><td><%=index.getK().getClose() %></td>
          <td width="60px"><span>总股本：</span></td><td><%=StkUtils.number2String(index.getStk().getTotalCapital()/10000,2) %>亿</td>
          <td width="60px"><span>总市值：</span></td><td><%=StkUtils.number2String(index.getTotalMarketValue(),2)%>亿</td>
          <td width="80px"><span>PE<sup>TTM</sup>/PE：</span></td><td><%=index.getK().getKline().getPeTtm() %>/<%=StkUtils.number2String(index.getPE(), 2) %></td>
          <td width="42px"><span>PB<sup>TTM</sup>：</span></td><td><%=StkUtils.number2String(index.getPB(),2) %></td>
          <td width="42px"><span>PS<sup>TTM</sup>：</span></td><td><%=index.getPSAsString() %></td>
          <td width="40px"><span>PR：</span></td><td><%=StkUtils.number2String(index.getPR(),2) %></td>
          <td width="42px"><span>地域：</span></td><td><%=index.getStk().getAddress() %></td>
          </tr></table>
        </blockquote>
        <blockquote>
        <table>
        	<tr><td width="40px"><span>简介：</span></td><td><%=WebUtils.display(index.getStock().getCompanyProfile(), 240, false) %></td></tr>
        </table>
        </blockquote>
      </article>
    </div>
    <div class="row stkbaseinfo">
      <article class="span5">
        <blockquote>
        <table>
	        <tr><td width="70px"><span>所属行业：</span></td>
	        <td>
		    <%
		      List<Industry> industries = index.getIndustry();
		      for(Industry ind : industries){
		        out.print("<a target='_blank' href='/industry?id="+ind.getType().getId()+"'>"+ind.getType().getName()+"</a>");
		      }
		    %>
		    <br><a target='_blank' href='http://stockhtm.finance.qq.com/sstock/quotpage/q/<%=index.getCode() %>.htm#6'>[QQ概念]</a>
	    	</td></tr>
	    </table>
	  </article>
	  <article class="span5">
	    <blockquote>
	    <table>
			<tr><td width="70px"><span>主营业务：</span></td>
			<td>
		    <%
		      List<Map> mbs = index.getMainBusiness();
		      for(Map mb : mbs){
		        out.print("<a target='_blank' href='/search?q="+mb.get("name")+"'>"+mb.get("name")+"</a>");
		      }
		    %>
		    <br><a target="_blank" href="http://www.iwencai.com/stockpick/search?tid=stockpick&qs=stockpick_diag&ts=1&w=<%=index.getCode()%>">[主营图谱]</a>
	    	</td></tr>
        </table>
        
        </blockquote>
      </article>
    </div>
    <div class="row stkbaseinfo">
      <article class="span10">
        <blockquote>
        <table>
	        <tr><td width="70px"><span>业绩预告：</span></td>
	        <%
	        List<String> ee = index.getEarningsForecastAsList();
	        if(ee != null && ee.size() > 2){
	        	out.print("<td width='80px'>"+ee.get(0)+"</td>");
	        	out.print("<td>"+ee.get(2)+"</td>");
	        }else{
	        	out.print("<td>&nbsp;</td>");
	        }
	        %></tr>
			<tr><td><span>盈利预期：</span></td>
			<td colspan="2"><%=WebUtils.display(index.getStk().getEarningExpect(),240,false) %></td></tr>
        </table>
        </blockquote>
        <blockquote>
        <table>
        	<tr><td width="75px"><span>相关股票1：</span></td><td id="relatedstk"><span class="loading-btn"></span></td></tr>
			<tr><td><span>相关股票2：</span></td><td id="relatedstk2"><span class="loading-btn"></span></td></tr>
        </table>
        </blockquote>
        <blockquote>
        <table>
	        <tr><td width="70px"><span>关键字：</span></td>
	        <td id="showkeyword"><span class="loading-btn"></span></td>
	        <input id="kwcode" type="hidden" />
			<input id="kwtype" type="hidden" value="1"/>
			<script type="text/javascript">$("#kwcode").val($('#scode').val());</script>
			<%
			if(isAdmin){
			%>			
			<td width="110px"><input class="input-small" id="keyword" type="text"/></td>
			<td><button class="btn" type="submit" onclick="addKeyword()">加关键字</button></td>
			<td><a target="_blank" href="http://www.100ppi.com/cindex/"><span>生意社</span></a>：</td>
			<td>
		    <%
		      Set<String> ppi = (Set<String>)sc.get("keyword_ppi");
		      out.print(ppi);
		    %>
		    </td>
			<%} %>
			</tr>
        </table>
        </blockquote>
      </article>
    </div>
  </div>
</div>
<script>
$(function() { 
  if($("#kwcode").length > 0){ 
	listRelatedStk('/stk.do?method=listRelatedStk&code='+$('#scode').val());
	listKeyword('/keyword.do?kwcode='+$("#kwcode").val()+'&kwtype='+$("#kwtype").val());
  }
});
</script>

<div class="content">
  <div class="page-header">
    <h3><span class="icon-globe"></span> 个股导航</h3>
  </div>
  <div class="page-container">
  <%String stkName = StringUtils.replace(index.getName(), " ",""); %>
  <%if(index.getMarket()==1){ %>
   <div class="row stkdaohang">
    <article class="span10">
       <blockquote>
        <p>
        <a target="_blank" href="http://www.windin.com/home/stock/html/<%=index.getCode() %>.<%=index.getLoc()==1?"SH":"SZ" %>.shtml">Windin</a>
        <a target="_blank" href="http://www.windin.com/Tools/NewsDetail.aspx?windcode=<%=index.getCode() %>.<%=index.getLoc()==1?"SH":"SZ" %>" style="font-size:14">Windin公司新闻</a>
        <a target="_blank" href="http://www.windin.com/Tools/IntelligenceDetail.aspx?windcode=<%=index.getCode() %>.<%=index.getLoc()==1?"SH":"SZ" %>" style="font-size:14">Windin公司情报</a>
        <a target="_blank" href="http://moer.jiemian.com/stockcode.htm?code=cn_<%=index.getCode() %>">摩尔经融</a>
        <a target="_blank" href="http://114.80.159.18/CorpEventsWeb/NewsEventAlert.aspx?windcode=<%=index.getCode() %>.<%=index.getLoc()==1?"SH":"SZ" %>&t=1">大事提醒</a>
        <a target="_blank" href="http://search.sina.com.cn/?q=<%=java.net.URLEncoder.encode(stkName) %>&range=title&c=news&sort=time">新浪新闻</a>
        <a target="_blank" href="http://news.baidu.com/ns?ct=0&rn=20&ie=utf-8&bs=<%=stkName %>&rsv_bp=1&sr=0&cl=2&f=8&prevct=0&word=<%=stkName %>&tn=newstitle&inputT=0">百度新闻</a>
        <a target="_blank" href="http://www.xueqiu.com/S/<%=(index.getMarket()==1?(index.getLoc()==1?"SH":"SZ"):"")+index.getCode() %>/GSJJ">公司网页</a>
        <a target="_blank" href="http://f9.eastmoney.com/soft/gp30.php?code=<%=index.getCode()+(index.getMarket()==1?(index.getLoc()==1?"01":"02"):"") %>" style="font-size:16">核心题材</a>
        
        <a target="_blank" href="http://data.eastmoney.com/rzrq/detail/<%=index.getCode() %>.html">融资融券</a>
        </p>
        <p>
        <a target="_blank" href="http://www.xueqiu.com/S/<%=(index.getMarket()==1?(index.getLoc()==1?"SH":"SZ"):"")+index.getCode() %>">雪球</a>
        <a target="_blank" href="http://guba.eastmoney.com/list,<%=index.getCode() %>.html">股吧</a>
        <a target="_blank" href="http://www.taoguba.com.cn/guba_<%=index.getLoc()==1?"sh":"sz" %><%=index.getCode() %>">淘股吧</a>
        <a target="_blank" href="http://s.weibo.com/wb/<%=stkName %>&xsort=time&Refer=wb_realtime">微博搜索</a>
        <a target="_blank" href="http://www.baidu.com/s?wd=<%=stkName %>%20site%3A(blog.sina.com.cn)&ie=utf-8&cl=3&t=12">新浪Blog</a>
        <a target="_blank" href="http://www.baidu.com/s?wd=<%=stkName %>%20site%3A(blog.eastmoney.com)&ie=utf-8&cl=3&t=12">东财Blog</a>
        <a target="_blank" href="http://www.baidu.com/s?wd=<%=stkName %>%20site%3A(xueqiu.com)&ie=utf-8&cl=3&t=12">雪球搜索</a>
        <a target="_blank" href="http://weixin.sogou.com/weixin?type=2&query=<%=stkName %>&ie=utf8&s_from=input&_sug_=y&_sug_type_=">微信搜索</a>
        <a target="_blank" href="https://www.qichacha.com/search?key=<%=stkName %>">企查查</a>
        </p>
       </blockquote>
    </article>
   </div>
   <div class="row stkdaohang">
    <article class="span5">
       <blockquote>
       <p>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_2001",stkName+" site:(cnstock.com)","上海证券报",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_2002",stkName+" site:(cs.com.cn)","中国证券报",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_2003",stkName+" site:(ccstock.cn)","证券日报",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_2004",stkName+" site:(cb.com.cn)","中国经营报",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_2005",stkName+" site:(21cbh.com)","21世纪报",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_2006",stkName+" site:(eeo.com.cn)","经济观察报",false) %>
       </p>
       </blockquote>
    </article>
    <article class="span5">
       <blockquote>
       <p>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_1001",stkName+" 超预期 | "+stkName+" 爆发","超预期/爆发",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_1010",stkName+" 高增长 | "+stkName+" 高成长","高增长/高成长",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_1","baidu_1020",stkName+" 提价 | "+stkName+" 强烈推荐","提价/强烈推荐",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_170",stkName+" 利好","利好预期",true) %>
       <%if(isAdmin){%><script type="text/javascript">baiduSearch("baidu_1");</script><%} %>
       </p>
       </blockquote>
    </article>
   </div>
   <div class="row stkdaohang">
    <article class="span5">
       <blockquote>
        <p>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_100",stkName+" 行业 | "+stkName+" 产业","行业/产业",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_101",stkName+" 景气","景气度",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_102",stkName+" 竞争对手","竞争对手",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_116",stkName+" 上游 | "+stkName+" 下游","上下游",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_110",stkName+" 龙头","龙头",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_112",stkName+" 新产品","新产品",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_114",stkName+" 产能","产能",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_200",stkName+" 转型","转型",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_210",stkName+" 管理层","管理层",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_117",stkName+" 出货量 | "+stkName+" 开工率","出货量/开工率",false, 16) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_120",stkName+" 订单 | "+stkName+" 中标 | "+stkName+" 合同","订单/中标/合同",true,16) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_190",stkName+" 市场占有率","市场占有率",false) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_180",stkName+" 核心技术","核心技术",true) %>
        </p>
      </blockquote>
    </article>
    <article class="span5">
       <blockquote>
        <p>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_130",stkName+" 重组 | "+stkName+" 并购 | "+stkName+" 收购","重组/并购/收购",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_160",stkName+" 股权激励","股权激励",true,20) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_140",stkName+" 回购 | "+stkName+" 增持","回购/增持",true) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_150",stkName+" 增发","增发",true,16) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_155",stkName+" 非公开发行","非公开发行",false,16) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_220",stkName+" 举牌","举牌",true,16) %>
        <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_230",stkName+" 央企改革","央企改革",true) %>
        <%if(isAdmin){%><script type="text/javascript">baiduSearch("baidu_2");</script><%} %>
        </p>
       </blockquote>
    </article>
   </div>
   <div class="row stkdaohang">
    <article class="span5">
       <blockquote>
        <p>
        <a target="_blank" href="http://www.baidu.com/s?wd=<%=index.getName() %> 招股说明书 site:(<%=index.getLoc()==1?"sse.com.cn":"szse.cn" %>)&ie=utf-8&cl=3&t=12&fr=news">招股说明书</a>
        <a target="_blank" href="http://rs.p5w.net/c/<%=index.getCode() %>.shtml">投资者互动平台</a>
        <a target="_blank" href="http://www.cninfo.com.cn/information/companyinfo.html?fulltext?<%=index.getCode() %>">巨潮公告</a>
        <a target="_blank" href="http://www.cninfo.com.cn/information/companyinfo.html?periodicalreport?<%=index.getCode() %>">定期报告</a>
        <a target="_blank" href="http://stockdata.stock.hexun.com/2008/ggqw.aspx?page=1&stockid=<%=index.getCode() %>">公告全文</a>
        <a target="_blank" href="http://data.eastmoney.com/report/hy,<%=index.getCode() %>_1.html">行业研报</a>
        <a target="_blank" href="http://vip.stock.finance.sina.com.cn/q/go.php/vReport_List/kind/search/index.phtml?symbol=<%=index.getCode() %>&orgname=&industry=&title=&t1=all">新浪研报</a>
        <a target="_blank" href="http://search.10jqka.com.cn/search?w=<%=index.getName() %>&tid=info&tr=2&ft=1&st=0&tr=1&qs=pf">同花顺研报</a>
        <a target="_blank" href="http://data.eastmoney.com/report/<%=index.getCode() %>.html">东方财富研报</a>
        <a target="_blank" href="http://www.baidu.com/s?wd=<%=index.getName() %> site:(microbell.com)&ie=utf-8&cl=3&t=12&fr=news" style="font-size: 16">迈博汇金研报</a>
        </p>
       </blockquote>
    </article>
    <article class="span5">
       <blockquote>
        <p>
        <a target="_blank" href="http://f9.eastmoney.com/<%=index.getLoc()==1?"sh":"sz" %><%=index.getCode() %>.html#jggd">盈利预测</a>
        <a target="_blank" href="http://vip.stock.finance.sina.com.cn/q/go.php/vFinanceAnalyze/kind/performance/index.phtml?symbol=<%=index.getCode() %>">业绩预告</a>
        <a target="_blank" href="http://f10.eastmoney.com/f10_v2/CompanyManagement.aspx?code=<%=index.getLoc()==1?"sh":"sz" %><%=index.getCode() %>#cgbd-0">高管持股</a>
        <a target="_blank" href="http://data.eastmoney.com/Stock/lhb/<%=index.getCode() %>.html">龙虎榜</a>
        <a target="_blank" href="http://data.eastmoney.com/zjlx/<%=index.getCode() %>.html">资金流入</a>
        <a target="_blank" href="http://vip.stock.finance.sina.com.cn/q/go.php/vInvestConsult/kind/xsjj/index.phtml?symbol=<%=index.getLoc()==1?"sh":"sz" %><%=index.getCode() %>">限售解禁</a>
        <a target="_blank" href="http://stockpage.10jqka.com.cn/<%=index.getCode() %>/holder/">十大股东</a>
        </p>
        <p>
        <a target="_blank" href="http://stockpage.10jqka.com.cn/<%=index.getCode() %>/holder/#holdernum">股东人数</a>
        <a target="_blank" href="http://f10.eastmoney.com/f10_v2/ShareholderResearch.aspx?code=<%=index.getLoc()==1?"sh":"sz" %><%=index.getCode() %>">股东人数2</a>       
        <a target="_blank" href="http://data.eastmoney.com/hsgtcg/StockHdStatistics.aspx?stock=<%=index.getCode() %>">沪深港通持股</a>
        </p>
       </blockquote>
    </article>
   </div>
  <%}else{ %>
   <div class="row stkdaohang">
    <article class="span10">
       <blockquote>
        <a target="_blank" href="http://www.xueqiu.com/S/<%=(index.getMarket()==1?(index.getLoc()==1?"SH":"SZ"):"")+index.getCode() %>">雪球</a>
        <a target="_blank" href="http://gu.qq.com/us<%=index.getCode() %>">QQ财经</a>
        <a target="_blank" href="http://www.finviz.com/quote.ashx?t=<%=index.getCode() %>&ty=c&ta=1&p=d&b=1">Finviz</a>        
   	   </blockquote>
    </article>
   </div>
  <%} %>
  </div>
</div>
<pre>
  1.股东人数是否减少。
  2.核心题材。
</pre>


<%
	List<StkFnType> fnTypes = sc.getFnTypes(index.getMarket());
if(index.getMarket()==1){ 
	List<List<StkFnDataCust>> fnDatas = index.getFnTable(fnTypes, 4);
%>
<div class="content" style="min-width:1240px;margin-left:-140px">
  <div class="page-header" style="position: relative;">
    <h3><span class="icon-list"></span> 
    	<a target="_blank" href="http://money.finance.sina.com.cn/corp/go.php/vFD_FinancialGuideLine/stockid/<%=index.getCode() %>/displaytype/4.phtml">
    	财务数据
    	</a></h3>
    <div style="position:absolute;left:150px;top:9px;" >
	    <!-- Nav tabs -->
	    <ul class="nav nav-tabs" role="tablist">
	      <li class="active"><a href="#finance-base" role="tab" data-toggle="tab">系列1</a></li>
	      <li><a href="#finance-other" role="tab" data-toggle="tab">系列2</a></li>
	    </ul>
	  </div>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" id="finance-base">
        <table class="datatable table table-stk">
          <thead><tr>
            <th><% if(isAdmin){out.print("["+index.isGrowthOrPotentialOrReversionName()+"]");} %></th>
			<%
				List<Integer> tabs = new ArrayList<Integer>();
				int i = 0;
				for(StkFnType fnType : fnTypes){
					if(fnType.getTab().intValue() == 1){
						tabs.add(i++);
						String dispName = fnType.getDispName()==null?fnType.getName():fnType.getDispName();
			%><th title="<%=dispName%>" <%=fnType.getColspan()!=null?"colspan="+fnType.getColspan():"" %>><%=dispName%></th><%
					}
				}
			%>
            </tr></thead>
          <tbody>
            <%
			  for(List<StkFnDataCust> fnData : fnDatas){
				  
				StkFnDataCust tmpFnData = (StkFnDataCust)StkUtils.getFirstNotNull(fnData);
				String style = "";
				if(tmpFnData != null && tmpFnData.getNumber() != 4) style = " style='color:#FF6666'";
			%>
				<tr<%=style %>>
				  <td><%=tmpFnData.getNumber()==4?StkUtils.formatDate(tmpFnData.getFnDate(),StkUtils.sf_ymd2,StkUtils.sf_yyyy_MM):("(Q"+tmpFnData.getNumber()+")") %></td>
				<%
				  int j = 0;
				  for(StkFnDataCust fn : fnData){
					  if(tabs.contains(j++)){
						  out.print("<td>"+(fn==null?"--":fn.getFnValueToString())+"</td>");
						  if(fn!=null && fn.getStkFnType()!=null && fn.getStkFnType().getColspan() != null){
							  out.print("<td>"+(StkUtils.numberFormat2Digits(fn.getFnDateByOneQuarter()))+"</td>");
						  }
					  }
				  } 
				%></tr>
			<%
			  } 
			%>
          </tbody>
        </table>
      </div>
      <div class="tab-pane" id="finance-other">
        <table class="datatable table table-stk">
          <thead><tr>
            <th><% if(isAdmin){out.print("["+index.isGrowthOrPotentialOrReversionName()+"]");} %></th>
			<%
				tabs = new ArrayList<Integer>();
				for(StkFnType fnType : fnTypes){
					if(fnType.getTab().intValue() == 2){
						tabs.add(i++);
						String dispName = fnType.getDispName()==null?fnType.getName():fnType.getDispName();
			%><th title="<%=dispName%>"><%=dispName%></th><%
					}
				}
			%>
            </tr></thead>
          <tbody>
            <%
			  for(List<StkFnDataCust> fnData : fnDatas){ 
				StkFnDataCust tmpFnData = (StkFnDataCust)StkUtils.getFirstNotNull(fnData);
				String style = "";
				if(tmpFnData != null && tmpFnData.getNumber() != 4) style = " style='color:#FF6666'";
			%>
				<tr<%=style %>>
				  <td><%=tmpFnData.getNumber()==4?StkUtils.formatDate(tmpFnData.getFnDate(),StkUtils.sf_ymd2,StkUtils.sf_yyyy_MM):("(Q"+tmpFnData.getNumber()+")") %></td>
				<%
				  int j = 0;
				  for(StkFnDataCust fn : fnData){
					  if(tabs.contains(j++)){
						  out.print("<td>"+(fn==null?"--":fn.getFnValueToString())+"</td>");
					  }
				  } 
				%></tr>
			<%} %>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
<pre>
<strong>财务数据 分析要点：</strong>
  1.毛利率是否在同行业里较高（大于30%~60%），毛利率是否同比增长。
  2.主营收入是否先于净利率增长：<%
  StkImportInfo ii = JdbcUtils.load(sc.getConnection(), "select * from stk_import_info where type=20 and code='"+index.getCode()+"' order by id desc", StkImportInfo.class);
  if(ii != null){
	out.print("<span style='color:red'>"+ii.getInfo()+" ["+ii.getInsertTime()+"]"+"</span>");	  
  }
  %>
  3.查看每股公积金是否>=3元(风生水起)。
  4.盈利预测：<%=EarningsForecast.getEarningsForecast(index.getCode())  %>  
  5.业绩拐点先行指标：经营活动现金流量净额 同比增长：<%
    StkFnDataCust fn = index.getFnDataLastestByType(StkConstant.FN_TYPE_CN_JYHDXJLLJE);
    if(fn != null){
    	Double rate = fn.getRateOfYear(false);
    	if(rate != null){
  			double d = StkUtils.numberFormat(rate,2);
			out.print(d >= 100?"<span style='color:red'>"+d+"%</span>":d+"%");
    	}
    }
  %> (参考案例：安洁科技[002635]-2016Q1经营活动现金流量净额开始大幅增长。也有除外的，如订单类公司：神雾环保 [300156]，利亚德 [300296]，他们之所以牛是因为订单多，营收暴增。)
  6.销售净利率是否>5%(零售行业除外)，最好在15%以上；ROE是否>8% (来自 - 《投资的本源》 P31)；经营性净现金流与净利润比>70% (P69)
  7.杜邦分析：净资产收益率(ROE) = 销售净利润率    *    资产周转率    *   权益乘数 
                           = (净利润/主营收入) * (主营收入/总资产) * (总资产/净资产)  (来自 - 《投资的本源》 P73)
    参考文档：<a target="_blank" href="https://note.youdao.com/web/#/file/recent/note/73F3E3712CE7492BBAE3539E12D62666/">浅析“杜邦分析法”</a>
  8.商誉是否太大。并购重组会产生商誉，具体参见：<a target="_blank" href="https://note.youdao.com/web/#/file/recent/note/3D9A0D55DC1A470BB5D8F4A879C477C5/">警惕商誉的恶之花</a>
  9.案例分析：<a target="_blank" href="https://note.youdao.com/web/#/file/recent/note/e36b07f35fd55c2864a8cdb175cc3637/">三安光电</a>
  10.大股东质押比例不能太高，<a target="_blank" href="http://data.eastmoney.com/gpzy/pledgeRatio.aspx">查询质押比例</a>。
</pre>
<%}else if(index.getMarket()==2){ 
	//com.stk123.tool.util.collection.Table tab = index.getFnTable(fnTypes);
	List<Map> tab = index.getFnTableForUS();
%>
<div class="content" style="min-width:1240px;margin-left:-140px">
  <div class="page-header" style="position: relative;">
    <h3><span class="icon-list"></span> 
    <a target="_blank" href="http://f10.eastmoney.com/usf10/dtgjzb.aspx?code=<%=index.getCode() %>">财务数据</a>
    <a target="_blank" href="https://finance.yahoo.com/quote/<%=index.getCode() %>/key-statistics?p=<%=index.getCode() %>">Yahoo财经</a>
    <a target="_blank" href="https://seekingalpha.com/symbol/<%=index.getCode() %>/financials/balance-sheet?figure_type=quarterly">Seekingalpha</a>
    </h3>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" id="finance-base">
        <table class="datatable table table-stk">
          <thead><tr>
            <th></th>
			<%
				int i=0;
				Iterator iter = tab.get(0).entrySet().iterator(); 
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next(); 
					if(i++<=1)continue;
					Object key = entry.getKey();					
			%><th title="<%=key%>"><%=key%></th><%
				}
			%>
            </tr></thead>
            
            <%
			  for(Map row : tab){
				  int j=0;
			%>
				<tr>
			<%
				iter = row.entrySet().iterator(); 			
				while (iter.hasNext()) {					
					Map.Entry entry = (Map.Entry) iter.next();
					if(j++==0)continue;
					Object fn = entry.getValue();
					out.print("<td>"+(fn==null||"%".equals(fn)?"--":String.valueOf(fn))+"</td>");
				}			  
			%></tr>
			<%} %>
          <tbody>
            
          </tbody>
        </table>
      </div>
      
    </div>
  </div>
</div>
<pre>
<strong>财务数据 观察要点：</strong>
  1.营运资金 = 流动资产 - 负债。 当 折扣率 = 市值/营运资金 < 0.7时，考虑买入。
    施洛斯：总的来说，我们(格雷厄姆公司)的做法是：买入价格低于营运资金的公司，以营运资金的三分之二买入，等股价上涨到等于每股营运资金，我们的投资就赚了50%。
</pre>
<%} %>


<%@include file="/common/js_datatables.jsp" %>
<div class="content">
  <div class="page-header">
    <h3><span class="icon-list"></span> 数据统计 <a href="https://www.lixinger.com/analytics/company/sz/<%=index.getCode() %>/detail/fundamental/value" target="_black">理性人数据</a> <a href="https://guorn.com/stock/history?his=1&ticker=<%=index.getCode() %>,0.M.%E8%82%A1%E7%A5%A8%E6%AF%8F%E6%97%A5%E6%8C%87%E6%A0%87_%E5%B8%82%E7%9B%88%E7%8E%87.0,1" target="_black">果仁网数据</a></h3>
  </div>
  <div class="page-container" style="position:relative;">
   	  <table id="stkValueHistory" class="datatable table table-stk">
        <thead>
           <tr>
             <th rowspan="2">年份</th><th colspan="2">股价</th><th colspan="2">总市值</th>
             <th colspan="2">PE</th><th colspan="2">PB</th><th colspan="2">PEG</th>
           </tr>
           <tr>
             <th>最低价</th><th>最高价</th><th>最低市值</th><th>最高市值</th>
             <th>最低PE</th><th>最高PE</th><th>最低PB</th><th>最高PB</th><th>最低PEG</th><th>最高PEG</th>
           </tr>
        </thead>
      </table>
   	  <script type="text/javascript">$(function() {stkValueHistory($('#scode').val())});</script>
  </div>
</div>  
<pre>
<strong>数据统计 观察要点：</strong>
  1.原则上 PEG<0.8 才能买入（陌上花香）。
</pre>

<%if(index.getMarket() == 1){ %>
<div class="content">
  <div class="page-header" style="position: relative;">
    <h3 style="line-height: 59px;"><span class="icon-tasks"></span> <a target="_blank" href="http://www.windin.com/Tools/NewsDetail.aspx?windcode=<%=index.getCode() %>.<%=index.getLoc()==1?"SH":"SZ" %>">新闻</a></h3>
    <div style="position: absolute;left:100px;top:9px;" >
    <ul class="nav nav-tabs" role="tablist">
      <li class="active"><a href="#news-all" role="tab" data-toggle="tab">全部</a></li>
<%
	List<StkImportInfoType> types = News.getTypes();
	for(StkImportInfoType newType : types){
%>      
      <li><a href="#news-<%=newType.getType() %>" id="news-tab-<%=newType.getType() %>" data="<%=newType.getType()%>" role="tab" data-toggle="tab"><%=newType.getName() %></a></li>
<%
	}
%>      
    </ul>
  </div>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" id="news-all">
      	<div id="news-list-all">
			<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
		</div>
		<div class="pagination pagination-right" id="pager-news-all"></div>
      </div>
<%
	for(StkImportInfoType newType : types){
%>
      <div class="tab-pane" id="news-<%=newType.getType()%>" >
      	<div id="news-list-<%=newType.getType() %>" style="height:100%;overflow:auto;text-align:left;">
      		<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
      	</div>
		<div class="pagination pagination-right" id="pager-news-<%=newType.getType() %>"></div>
	  </div>
<%
	}
%>
    </div>
  </div>
</div>
<%@include file="/common/stk_care_modal.jsp" %>
<script type="text/javascript">
$(function() {
  listNews('all',1);
  $("a[id^='news-tab-']").one('click', function(){listNews($(this).attr('data'),1);});
  
  rightMenu('关注','right-menu-care','care');
  $("#care-btn").bind("click",care);
});
</script>
<pre>
  1.是否有股权激励：<%
  StkImportInfo info = JdbcUtils.load(sc.getConnection(), "select * from stk_import_info where type=5 and code='"+index.getCode()+"' order by id desc", StkImportInfo.class);
  if(info != null){
	out.print("<span style='color:red'>"+info.getInfo()+" ["+StkUtils.formatDate(info.getInsertTime())+"]"+"</span>");	  
  }
  %> - <a target="_blank" href='http://www.iwencai.com/search?typed=0&preParams=&ts=1&f=1&qs=result_channel&selfsectsn=&querytype=&searchfilter=&tid=info&w=<%=index.getName()%>%20股权激励'>问财搜索</a> <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_160",stkName+" 股权激励","百度搜索",true,20) %> 
  2.高管是否增持，公司是否有回购: <%=WebUtils.getBaiduNewsSearch("baidu_2","baidu_140",stkName+" 回购 | "+stkName+" 增持","搜索回购/增持",true) %>
  3.员工持股计划：<%
  List<StkMonitor> ms = index.getMonitor(2);
  out.print(WebUtils.createTableOfStkMonitor(index, ms, "员工持股"));
  %>
  4.非公开发行：<%
  out.print(WebUtils.createTableOfStkMonitor(index, ms, "非公开发行"));
  %>
  5.订单金额占主营收入：<%
  info = JdbcUtils.load(sc.getConnection(), "select * from stk_import_info where type=1 and code="+index.getCode()+" order by id desc", StkImportInfo.class);
  if(info != null){
	out.print("<span style='color:red'>"+info.getInfo()+" ["+StkUtils.formatDate(info.getInsertTime())+"]"+"</span>");	  
  }
  %>
  6.募集资金：<%
  info = JdbcUtils.load(sc.getConnection(), "select * from stk_import_info where type=21 and code="+index.getCode()+" order by id desc", StkImportInfo.class);
  if(info != null){
	out.print("<span style='color:red'>"+info.getInfo()+" ["+StkUtils.formatDate(info.getInsertTime())+"]"+"</span>");	  
  }
  %>
  7.牛散：<%out.print(WebUtils.createTableOfStkImportInfo(sc.getConnection(),index, 3));%>
  8.资金流：<%out.print(index.getCapitalFlowImageOnMainAndSuper(60, 800, 50));%>
  9.互动易：<a target="_blank" href="http://ircs.p5w.net/ircs/interaction/queryQuestionByGszz.do?condition.stockcode=<%=index.getCode() %>&condition.status=3&condition.dateFrom=<%=StkUtils.formatDate(StkUtils.addDay(StkUtils.getToday(),-30), StkUtils.sf_ymd) %>&condition.dateTo=<%=StkUtils.formatDate(StkUtils.getToday()) %>">投资者互动平台</a>
</pre>
<%} %>

<div class="content">
  <div class="page-header">
    <h3><span class="icon-tasks"></span> 公告</h3>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active">
      	<div id="notice-list">
			<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
		</div>
		<div class="pagination pagination-right" id="pager-notice"></div>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
$(function() {
  listNotice('',1);
});
</script>

<div class="content" style="min-width:1240px;margin-left:-140px">
  <div class="page-header">
    <div style="float: left;">
      <h3><span class="icon-tasks"></span> 雪球帖子</h3>
    </div>
    <div class="controls" style="float:right;">
		<div class="input-append">
			<button class="btn" onclick="listXueqiuArticle();">查询</button>
		</div>
	</div>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active">
      	<table class="xueqiuTable table table-striped table-bordered" id="stk-care">
          <thead><tr>
            <% 
              List<StkDictionary> columns = StkService.getColumnNames(1006);
              for(StkDictionary column : columns){
            %>
              	<th width="<%=column.getParam3()%>%"><%=column.getText() %></th>
            <%
              } 
            %>
            </tr></thead>
          
        </table>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
function listXueqiuArticle() {
  $('.xueqiuTable').DataTable().destroy();
  $('.xueqiuTable').dataTable( {
    "language": datatable_lang,
    "order": [],
    "ajax":"/stk?method=getXueqiuArticle&code="+$("#scode").val()
  });
};
</script>

<div class="content">
  <div class="page-header">
    <h3><span class="icon-tasks"></span> 投资者关系</h3>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active">
      	<div id="invest-list">
			<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
		</div>
		<div class="pagination pagination-right" id="pager-invest"></div>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
$(function() {
  listInvest('stk',1);
});
</script>
<pre>
<strong>投资者关系 观察要点：</strong>
  1.查看公司现有股东情况，如果当前没有几家基金或机构，那不排除基金随后就开始建仓的可能。
</pre>

<%@include file="/common/js_ckeditor.jsp" %>
<div class="content">
  <div class="page-header" style="position: relative;">
    <h3><span class="icon-edit"></span> 文档编辑</h3>
    <div style="position: absolute;left:150px;top:9px;" >
	    <ul class="nav nav-tabs" role="tablist">
	      <li class="active"><a href="#info-stk" role="tab" data-toggle="tab">个股文档</a></li>
	      <li><a href="#info-related" role="tab" data-toggle="tab" id="tab-related">相关文档</a></li>
	    </ul>
    </div>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" id="info-stk">
			<div id="editor-container">
				<div class="editor-title"><input id="text-title" tabindex="1" type="text" placeholder="点击这里输入标题" class="title"></div>
				<div id="text-main">
					<textarea class="text-box" id="text" ></textarea>
				</div>
				<div class="text-btns">
					<button id="btn-save" class="btn">保 存</button>
					<button class="btn" onclick="cancelText()">取 消</button>
					<button class="btn" id="long-text-btn" onclick="longText()">长 文</button>
					<input type="hidden" value="1" id="text_type"/>
					<input type="hidden" value="" id="text_id"/>
					<input type="hidden" value="1" id="text_ctype"/>
					<input type="hidden" value="" id="text_code" />
					<input type="hidden" value="0" id="disp-order"/>
				</div>
			</div>
		    <div id="text_stk">
		    <p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
		    </div>
		  	<div class="pagination pagination-right" id="pager1"></div>
	  </div>
      <div class="tab-pane" id="info-related_tmp">
      	<div id="text_list_tmp" style="height:100%;overflow:auto;text-align:left;">
      	<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
      	</div> 
		<script type="text/javascript">$("#text_code").val($('#scode').val());</script>
		<div class="pagination pagination-right" id="pager2_tmp"></div>
	  </div>
   </div>
      <!-- 临时把“相关文档”一下都查询出来 -->
      <div class="tab-pane" id="info-related">
      	<div id="text_list" style="height:100%;overflow:auto;text-align:left;">
      	<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
      	</div> 
		<script type="text/javascript">$("#text_code").val($('#scode').val());</script>
		<div class="pagination pagination-right" id="pager2"></div>
	  </div>
  </div>
</div>

<div class="content">
  <div class="page-container">
		<div id="chartstk" style="width:100%; height:600px;"></div>
<%if(_user != null && _user.getStkUser().getId()==1 && StkConstant.IS_DEV){ %>		
		<table width="98%" border="1">
		<tr>
		  <td width="330">股票
		    <select id="kparam1">
		      <%=StkDict.htmlOptions(StkDict.MONITOR_K_PARAM1) %>
		    </select>数值
		    <select id="kparam2">
		      <%=StkDict.htmlOptions(StkDict.MONITOR_K_PARAM2,"2") %>
		    </select>
		    <input type="text" id="kparam3" size="8" value=""/>
		    <input type="button" onclick="kmonitorcreate()" value="监控"/>
		  </td>
		  <td id="kmonitorshow"></td>
		</tr>
		</table>
<%} %>		
  </div>
</div>  
  
</div>
<%@include file="/common/footer.jsp" %>
</body>
</html>
<script type="text/javascript">
var editor;

$( document ).ready( function() {
	editor = editorCreateFull('text',100);
} );

$(function() {
  $("#btn-save").bind("click",function(){
	$(this).prepend("<span class=\"loading-btn\"></span>");
	addText();
  });
  
  if($("#text_code").length > 0){ 
	listStkText('text_stk',1);
	listRelatedText('text_list',1);
  }
  $("#tab-related").one('click', function(){listRelatedText('text_list',1);});
});

</script>
<%@include file="/common/js_amcharts.jsp" %>
<script type="text/javascript" src="/js/jquery.contextmenu.r2.js"></script>
