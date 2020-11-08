<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	StkContext sc = StkContext.getContext();
	Industry industrySelect = (Industry)sc.get("industry_select");
	if(industrySelect!=null){
		pageContext.setAttribute(StkConstant.PAGE_TITLE, industrySelect.getType().getName() + " - 行业分类");
	}else{
		pageContext.setAttribute(StkConstant.PAGE_TITLE, "行业分类");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="/common/header.jsp" %>
<link rel="stylesheet" type="text/css" href="/css/larger.css" />
<body>
<%@include file="/common/nav.jsp" %>
<script>$(".icon-table").parent().parent().addClass("active");</script>
<%@include file="/common/js_jstree.jsp" %>
<div class="container" role="main">
  <div class="content">
	<div id="cc" class="easyui-layout" style="height:840px;">
	  <div data-options="region:'west'" style="width:170px;">
  		<input type="hidden" id="industry_select_id" value="<%=industrySelect!=null?industrySelect.getType().getId():""%>"/>
  		<input type="text" id="industry_search" style="width:140px" placeholder="查询行业"/>
  		<div id="treeViewDiv"></div>
	  </div>
	  <div data-options="region:'center'" class="dataTables_wrapper">
		<table id="industry_dt" class="datatable table table-stk">
			<thead><tr>
            <% 
              List<StkDictionary> columns = StkService.getColumnNames(1000);
              for(StkDictionary column : columns){
            	  if(column.getParam3().equals(StkConstant.NUMBER_ONE)){
            		  String select = StkConstant.MARK_EMPTY;
            		  if(column.getKey().equals(StkConstant.JSON_NAME)){
            			  //TODO select = StkUtils.getQuartersAsSelect();
            		  }
            %>
              <th class="sorting"><%=column.getText()+select %></th>
            <%
            	  }
              } 
            %>
            </tr></thead>
		</table>
	  </div>
	</div>
 </div>
</div>
</body>
</html>
<%@include file="/common/js_datatables.jsp" %>
<%@include file="/common/js_easyui_jqueryui.jsp" %>