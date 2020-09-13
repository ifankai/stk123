<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.stk123.web.WebUtils" %>
<%@page import="com.stk123.model.User" %>
<%
  User _user = WebUtils.getUser(session);
%>
<header class="navbar navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <a class="brand" href="/">小智慧365</a>
      <div class="search">
        <div class="input-append">
          <input size="10px" type="text" id="stkcode" placeholder="搜索 股票/行业/指标/文档" onclick="suggest(this.value,event,this.id,false,getStks);" onkeyup="suggest(this.value,event,this.id,false,getStks);" onkeydown="return tabfix(this.value,event);" onblur="clearSuggest();"/>
          <span class="icon-search"></span>
        </div>
        <p class="nodisplay"><input class="nodisplayd" type="text" id="keyIndex" /></p>  
        <p class="nodisplay"><input class="nodisplayd" type="text" id="sortIndex" /></p> 
        <div id="results"></div>
      </div>
      <div class="nav-collapse navigation">
<%//if(_user != null){%>
        <ul class="nav" role="navigation">
          <li class="divider-vertical"></li>
          <li><a target="_blank" href="/article"><span class="icon-edit"></span> 文档编辑</a></li>
          <li><a target="_blank" href="/data"><span class="icon-picture"></span> 数据指标</a></li>
          <li><a target="_blank" href="/industry"><span class="icon-table"></span> 行业分类</a></li>
          <li><a target="_blank" href="/main"><span class="icon-globe"></span> 每日指标</a></li>
          <li class="searching">
          	<a target="_blank" href="/earning"><span class="icon-globe icon-earning"></span> 业绩预告</a>
          	<div class="btn-group" style="padding:0px">
          	<ul class="dropdown-menu">
			<a href="/screener?from=us" target="_blank">美股筛选器</a>
			<a href="/screener?from=cn" target="_blank">A股筛选器</a>
			</ul>
			</div>
          </li>
        </ul>
<%//}else{out.println("&nbsp;");}%>
      </div> 
<%//if(_user != null){%>
      <div class="nav-collapse user">
        <div class="user-info pull-right">
          <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
              <div><strong><%=_user!=null?_user.getStkUser().getNickname():"" %></strong></div>
              <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
              <li><a href=""><span class="icon-cogs"></span> 个人资料</a></li>
<%//if(_user.getStkUser().getId()==1){ %>
			  <li><a target="_blank" href="/main"><span class="icon-book"></span> 每日指标</a></li>
			  <li><a target="_blank" href="/search?q=%E4%B8%80%E5%B9%B4%E6%9C%9F"><span class="icon-book"></span> 一年期牛基</a></li>
			  <li><a target="_blank" href="/search?q=%E6%88%90%E7%AB%8B%E4%BB%A5%E6%9D%A5%E7%89%9B%E5%9F%BA"><span class="icon-book"></span> 成立以来牛基</a></li>
			  
<%//} %>
			  <li><a target="_blank" href="http://data.eastmoney.com/report/ylyc.html"><span class="icon-book"></span> 东方财富-盈利预测</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/bbsj/201506/yjyg.html"><span class="icon-book"></span> 东方财富-业绩预告</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/IF/Data/Contract.html"><span class="icon-book"></span> 东方财富-期指持仓</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/rzrq/sh.html"><span class="icon-book"></span> 东方财富-融资融券</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/bkzj/hgt.html"><span class="icon-book"></span> 东方财富-沪股通资金流向</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/bkzj/jlr.html"><span class="icon-book"></span> 东方财富-行业板块资金流向</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/cjsj/bankTransfer.html"><span class="icon-book"></span> 东方财富-银证转账</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/xg/xg/default.html"><span class="icon-book"></span> 东方财富-新股申购</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/executive/gdzjc.html"><span class="icon-book"></span> 东方财富-股东增减持</a></li>
			  <li><a target="_blank" href="http://data.eastmoney.com/gpzy/pledgeRatio.aspx"><span class="icon-book"></span> 东方财富-上市公司质押比例</a></li>
			  <li><a target="_blank" href="http://www.cninfo.com.cn/disclosure/prbookinfo.jsp?desc=0&&order=2&&&market=szmb&&stockCode=&&sjplrq="><span class="icon-book"></span> 定期报告预约披露时间表</a></li>
			  
			  <li><a target="_blank" href="/dailyreport/<%=StkUtils.getToday() %>/index.html"><span class="icon-book"></span> 策略模型</a></li>
			  <li><a target="_blank" href="https://www.jisilu.cn/data/indicator/"><span class="icon-book"></span> 市场的估值</a></li>
              <li><a href="javascript:logout()"><span class="icon-signout"></span> 退出</a></li>
            </ul>
          </div>
        </div>
      </div>
<%//}%>
    </div>
  </div>
</header>
<script>
$(function() { 
  $(".searching").mouseover(function(){
	  $(".searching .btn-group").addClass("open");
  });
  $(".searching").mouseout(function(){
	  $(".searching .btn-group").removeClass("open");
  });
});
</script>