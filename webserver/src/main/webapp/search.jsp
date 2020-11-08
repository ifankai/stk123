<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	String q = RequestUtils.getString(request, "q");
	pageContext.setAttribute(StkConstant.PAGE_TITLE, q+" - 搜索");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="/common/header.jsp" %>
<style type="text/css">
a {
  text-decoration: none;
  color: #0055a2;
}
</style>
<body>
<%@include file="/common/nav.jsp" %>
<script type="text/javascript">
$("#stkcode").val("<%=q%>");
$(function(){
	ajaxSearch(null,1);
});
</script>
<div class="container" role="main">
<div class="content">
	<div class="page-container">
		<div class="row">
		<p style="text-align:center;" id="loading"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
			<div id="search-list"></div> 
			<div id="search-pager" class="pagination pagination-right"></div>
    		<div id="search-right"></div>
		</div>
    </div>
</div> 
</div>
<%@include file="/common/footer.jsp" %>
</body>
</html>
