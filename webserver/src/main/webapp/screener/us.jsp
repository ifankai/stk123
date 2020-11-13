<%@ page import="com.stk123.common.CommonConstant" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	pageContext.setAttribute(CommonConstant.PAGE_TITLE, "美股筛选器");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="/common/header.jsp" %>
<link rel="stylesheet" type="text/css" href="/css/larger.css" />
<style type="text/css">
.earningTable tr td{
  text-overflow:ellipsis;
  word-break:keep-all; 
  white-space:nowrap;
  text-align: right; 
}
.earningTable tbody tr.selected {
  background-color: #B0BED9;
}
</style>
<body>
<%@include file="/common/nav.jsp" %>
<script>$(".icon-earning").parent().parent().addClass("active");</script>
<%@include file="/common/js_datatables.jsp" %>
<script src="/js/bootstrap-datepicker.js"></script>
<div class="container" id="chart-container">
<div class="content">
  <!-- Page header -->
  <div class="page-header" style="position: relative;">
    <form id="screenerForm" type="post">
    <input type="hidden" name="method" value="searchForUS"/>
    <div style="float: left;">
    <h3><span class="icon-tasks"></span> 美股筛选器</h3>
    </div>
    <div class="controls" style="float:right;">
		<div class="input-append">
			<b>实际业绩:</b>
			PE(TTM)范围<input type="text" name="peFrom" style="width: 30px"/>
			~<input type="text" name="peTo" style="width: 30px"/>
			
			PB(TTM)范围<input type="text" name="pbFrom" style="width: 30px"/>
			~<input type="text" name="pbTo" style="width: 30px"/>
			
			PS(TTM)范围<input type="text" name="psFrom" style="width: 30px"/>
			~<input type="text" name="psTo" style="width: 30px"/>
			
			净利润增速范围<input type="text" name="netProfitGrowthRateFrom" style="width: 30px"/>
			~<input type="text" name="netProfitGrowthRateTo" style="width: 30px"/>
			
			毛利率范围<input type="text" name="grossProfitMarginFrom" style="width: 30px"/>
			~<input type="text" name="grossProfitMarginTo" style="width: 30px"/>
			
			<input type="button" class="btn" onclick="search();" value="查询"/>
		</div>
		<div class="input-append">
			<button class="btn" id="deleteStk">添加关注</button>
			<textarea id="deletestk" style="width:900px"></textarea>
			<input type="hidden" id="selectedstk">
		    <button class="btn" id="btn-search-2" onclick="search(true, true);">查询关注</button>
		</div>
		<div class="input-append">
			<button class="btn" id="addcondition" onclick="search(true, false);">保存查询条件</button>
			<textarea id="comments" style="width:900px"></textarea>
		</div>
	</div>
	</form>
  </div>
  <!-- Page container -->
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active">
        <table class="earningTable table table-striped" id="earningTable">
            <thead>
	            <tr>	     
	            <th>股票</th>
	            <th>行业</th>
	            <th>财务季度</th>
	            <th>市值</th>
	            <th>ROE</th>
	            <th>PE</th>
	            <th>PB</th>
	            <th>PS</th>
	            <th>Peg</th>
	            <th>毛利率</th>
	            <th>净利润增长率</th>
	            <th>负债率</th>
	            <th>研发占比</th>
	            </tr>
            </thead>
            <tfoot>
	            <tr>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="0"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="1"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="2"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="3"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="4"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="5"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="6"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="7"/></th>
				<th><input type="text" style="width:80%" class="column_filter" data-column="8"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="9"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="10"/></th>
	            <th><input type="text" style="width:80%" class="column_filter" data-column="11"/></th>
				<th><input type="text" style="width:80%" class="column_filter" data-column="12"/></th>
	            </tr>
            </tfoot>
            
          
        </table>
      </div>
    </div>
    
  </div>
  <!-- /Page container -->
</div>
<pre>
业绩拐点先行指标：经营活动现金流量净额 是否同比增长，参考案例：安洁科技[002635]-2016Q1经营活动现金流量净额开始大幅增长。
正则表达式：
  1.数值30.00到50.00：^[345]\d{1}\.  
  2.不为0：[^0](\s|\S)*  
  3.数值大于30：^[3-9]\d{1}|\d{3,}\d
</pre>
</div>
</body>
</html>

<script>
function search(){
	$('.earningTable').DataTable().destroy();
	$('.earningTable').dataTable( {
	      "language": datatable_lang,
	      //"order": [0],
	      //"bSort": false,
	      "aLengthMenu": [[15, 30, 50, -1], [15, 30, 50, "All"]],
	      "iDisplayLength": 15,
	      "ajax":"/screener?method=searchForUS&"+$('#screenerForm').serialize(),
	      "initComplete": function(settings, json) {
	    	   $('#earningTable td .content-summary').hover( function (){
	    		   $(this).parent().prop('title', ($(this).parent().find('.content-detail-inner').text()));
	    	   });
  		   }
	    });
}

function filterColumn (val, i ) {
	$('.earningTable').DataTable().column( i ).search(
        val,
        true,
        true
    ).draw();
}
$(document).ready(function() {
	$('.datepicker').datepicker();
	
	$('.earningTable').dataTable({
		"language": datatable_lang,
		"aLengthMenu": [[15, 30, 50, -1], [15, 30, 50, "All"]],
	    "iDisplayLength": 15
	});
    
    $('input.column_filter').on( 'keyup click', function () {
        filterColumn($(this).val(), $(this).attr('data-column') );
    } );
    
    $('#earningTable').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            //$(this).removeClass('selected');
        }
        else {
        	$('#earningTable').DataTable().$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            //$('#selectedstk').val($(this).children('td:first').text());
        }
    } );
    
});

</script>