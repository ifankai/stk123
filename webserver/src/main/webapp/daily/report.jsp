<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	StkContext sc = StkContext.getContext();
	List<Index> indexs = (List<Index>)sc.get("stk_reports");
	String flag = sc.getRequest().getParameter("k");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>每日创新高</title>
<%@include file="/common/top.jsp" %>
</head>
<body>
<div align="center" >
<%@include file="/common/navbar.jsp" %>
<div id="center2" style="width: 98%;padding-top: 33px">
<table width="100%" class="rank">
<%
for(Index index : indexs){
%>
<tr>
<td><a target="_blank" href="/stk.do?s=<%=index.getCode()%>"><b><%=index.getName() %></b></a>(<%=index.getCode() %>)</td>
</tr>
<%if(flag == null){ %>
<tr>
<td><div id="chartstk_<%=index.getCode() %>" style="width:98%; height:700px;"></div></td>
</tr>
<script>
$.ajax({
  url:'/chartstk.do?s=<%=index.getCode()%>',
  success: function (data) {
	  var chartData = JSON.parse(data, function (key, value) {
	        var a;  
	        if (typeof value === 'string') { 
	        	if(key == 'date'){
	        		var a = /^(\d{4})(\d{2})(\d{2})$/.exec(value);
	        		return new Date(a[1],--a[2],a[3],0,0,0);
	        	}
	        }  
	        return value;  
	    });
	  createStockChart("chartstk_<%=index.getCode() %>",chartData);
  }
});
</script>
<%
}
}
%>
</table>
</div>
</div>
</body>
</html>