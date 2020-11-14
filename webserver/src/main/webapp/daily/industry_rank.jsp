<%@ page import="com.stk123.service.ServiceUtils" %>
<%@ page import="com.stk123.task.schedule.InitialKLine" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	StkContext sc = StkContext.getContext();
	Connection conn = sc.getConnection();
	Industry industrySelect = (Industry)sc.get("industry_select");
	
	List<StkKlineRankIndustry> ranks = (List<StkKlineRankIndustry>)sc.get("industry_rank");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><%=industrySelect!=null?industrySelect.getType().getName():"所有行业" %></title>
<%@include file="/common/top.jsp" %>
</head>
<body>
<div align="center" >
<%@include file="/common/navbar.jsp" %>
<div id="center2" style="width: 98%;padding-top: 33px">
<table width="100%" class="rank">
<%
  int i = 0;
  Index sh = new Index(conn, "999999");
  for(StkKlineRankIndustry rank : ranks){
	  int day = rank.getRankDays().intValue();
%>  
  <tr <%if(day==2 || day == 10 || day == 30 || day ==120)out.print("bgcolor='#E3EFFF'"); %>>
    <%if(i++ % 5 == 0)out.print("<td rowspan='5'>"+day+"</td>"); %>
    <td><%=rank.getRank() %></td>
    <td><%=rank.getStkIndustryType().getName()%></td>
    <%for(int k : InitialKLine.RANK_STK_DAYS){ %>
      <td align="right">
      <%
        int no = 0;
        if(k == day){
        	no = rank.getRank();
        } else {
        	no = Industry.getIndustryRank(conn, rank.getIndustryId(), rank.getRankDate(), k).getRank();
        }
        
        if(no <= 10){
    		out.print("<font color='red'>"+no+"</font>");
    	}else{
    		out.print(no);
    	}
      %></td>
    <%} %>
	
    <td>
    <%
      int k = 0;
      for(StkKlineRankIndustryStock stk : rank.getStkKlineRankIndustryStock()){
    	  out.print("<a target='_blank' href='/stk.do?s="+stk.getCode()+"' title='"+ServiceUtils.numberFormat(stk.getChangePercent()*100,2)+"%'>"+Index.getName(stk.getCode())+"</a>&nbsp;");
    	  if(k++ >= 5)break;
      }
    %>
    </td>
  </tr>
<%
  }
%>  
</table>
</div>
</div>
</body>
</html>