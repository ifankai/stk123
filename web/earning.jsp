<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	pageContext.setAttribute(StkConstant.PAGE_TITLE, "业绩预告");
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
    <div style="float: left;">
    <h3><span class="icon-tasks"></span> 业绩预告</h3>
    </div>
    <div class="controls" style="float:right;">
    <%
	    String now = StkUtils.getToday();
		String prev = StkUtils.getPrevQuarter(now);
		String next = StkUtils.getNextQuarter(now);
    %>
		<div class="input-append">
			<b>业绩预告:</b>
			季报日期  <select id="fndate" style="width:95px">
						<option value="">--All--</option>
						<option value="<%=prev %>"><%=StkUtils.formatDate(prev) %></option>
						<option value="<%=next %>"><%=StkUtils.formatDate(next) %></option>
					</select>
			业绩预告低点>=<input type="text" id="erlow" style="width: 30px"/>
			业绩预告低点<=<input type="text" id="erlow2" style="width: 30px"/>
			高点>=<input type="text" id="erhigh" style="width: 30px"/>
			
			业绩预估后PE范围<input type="text" id="pelow" style="width: 30px"/>
			~<input type="text" id="pehigh" style="width: 30px"/>
			市值范围<input type="text" id="mvlow" style="width: 30px"/>
			~<input type="text" id="mvhigh" style="width: 30px"/>
		</div>
		<div class="input-append">
			业绩预告公告日期 <input class="datepicker" id="noticefrom" value="<%=StkUtils.getDate(-180, StkUtils.sf_ymd14) %>" type="text" style="width:70px"><span class="add-on"><i class="icon-calendar"></i></span>
			到 <input class="datepicker" id="noticeto" value="<%=StkUtils.getDate(30, StkUtils.sf_ymd14) %>" type="text" style="width:70px"><span class="add-on"><i class="icon-calendar"></i></span>
			查询股票
			<input type="text" id="querystk" style="width:80px"/>
			<button class="btn" id="btn-search" onclick="search(true, false);">查询</button>&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
		<div class="input-append">
			<b>实际业绩:</b>
			PE(TTM)范围<input type="text" id="realpelow" style="width: 30px"/>
			~<input type="text" id="realpehigh" style="width: 30px"/>
			PB(TTM)范围<input type="text" id="realpblow" style="width: 30px"/>
			~<input type="text" id="realpbhigh" style="width: 30px"/>
			净利润增速范围<input type="text" id="realnetlow" style="width: 30px"/>
			~<input type="text" id="realnethigh" style="width: 30px"/>
			经营现金流范围<input type="text" id="cashlow" style="width: 30px"/>
			~<input type="text" id="cashhigh" style="width: 30px"/>
			<button class="btn" id="btn-search-3" onclick="search2(true, false, true);">查询</button>
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
  </div>
  <!-- Page container -->
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active">
      <% 
          List<StkDictionary> columns = StkService.getColumnNames(1005);
          
      %>
        <table class="earningTable table table-striped" id="earningTable">
            <thead>
	            <tr>
	            <%for(StkDictionary column : columns){ %>
	            <th><%=column.getText() %></th>
	            <%}%>
	            </tr>
            </thead>
            <tfoot>
	            <tr>
	            <%
	              int n = 0;
	              for(StkDictionary column : columns){
	            %>
	              	<th><input type="text" style="width:<%=column.getParam3()%>px" class="column_filter" data-column="<%=n++%>"/></th>
	            <%
	              } 
	            %>
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

function filterColumn (val, i ) {
	$('.earningTable').DataTable().column( i ).search(
        val,
        true,
        true
    ).draw();
}
$(document).ready(function() {
	$('.datepicker').datepicker();
	
    searchEarning(false, true, false);
    
    $('input.column_filter').on( 'keyup click', function () {
        filterColumn($(this).val(), $(this).attr('data-column') );
    } );
    
    $('#earningTable tbody').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            //$(this).removeClass('selected');
        }
        else {
        	$('#earningTable').DataTable().$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            $('#selectedstk').val($(this).children('td:first').text());
        }
    } );
 
    $('#deleteStk').click( function () {
    	$('#earningTable').DataTable().row('.selected').remove().draw( false );
    	var orginal = $('#deletestk').val();
    	if(orginal == ''){
    		$('#deletestk').val($('#selectedstk').val());
    	}else{
    		var s = $('#selectedstk').val();
    		if(orginal.indexOf(s) < 0){
    			$('#deletestk').val($('#deletestk').val() + ',' + $('#selectedstk').val())
    		}
    	}
    } );
    
    <%
    StkContext sc = StkContext.getContext();
	Map<String, Object> params = (Map)sc.get("searchparams");
    if(params != null){
	    for(Map.Entry<String, Object> e : params.entrySet()){
	    	out.println("$('#"+e.getKey()+"').val('"+e.getValue()+"')");
	    }
    }
    %>
    
});
function searchEarning(ajax,queryCareStkOnly, real){
	$('.earningTable').DataTable().destroy();
	$('.earningTable').dataTable( {
	      "language": datatable_lang,
	      //"order": [0],
	      //"bSort": false,
	      "aLengthMenu": [[15, 30, 50, -1], [15, 30, 50, "All"]],
	      "iDisplayLength": 15,
	      "ajax":"/earning?method=getEarningNotice&ajax="+ajax+
	    		  "&fndate="+$('#fndate').val()+
	    		  "&erlow="+$('#erlow').val()+
	    		  "&erlow2="+$('#erlow2').val()+
	    		  "&erhigh="+$('#erhigh').val()+
	    		  "&pelow="+$('#pelow').val()+
	    		  "&pehigh="+$('#pehigh').val()+
	    		  "&deletestk="+$('#deletestk').val()+
	    		  "&mvlow="+$('#mvlow').val()+
	    		  "&mvhigh="+$('#mvhigh').val()+
	    		  "&noticefrom="+$('#noticefrom').val()+
	    		  "&noticeto="+$('#noticeto').val()+
	    		  "&querystk="+$('#querystk').val()+
	    		  "&realpelow="+$('#realpelow').val()+
	    		  "&realpehigh="+$('#realpehigh').val()+
	    		  "&realpblow="+$('#realpblow').val()+
	    		  "&realpbhigh="+$('#realpbhigh').val()+
	    		  "&realnetlow="+$('#realnetlow').val()+
	    		  "&realnethigh="+$('#realnethigh').val()+
	    		  "&cashlow="+$('#cashlow').val()+
	    		  "&cashhigh="+$('#cashhigh').val()+
	    		  "&comments="+$('#comments').val()+
	    		  "&querycarestkonly="+queryCareStkOnly+	    		  
	    		  "&real="+real,
	       "initComplete": function(settings, json) {
	    	   $('#earningTable tbody td .content-summary').hover( function (){
	    		   $(this).parent().prop('title', ($(this).parent().find('.content-detail-inner').text()));
	    	   });
  		   }
	    });
}
function search2(ajax, queryCareStkOnly, real){
	$("#btn-search").prepend("<span class=\"loading-btn\"></span>");
	$("#btn-search").prop("disabled", true);
	searchEarning(ajax, queryCareStkOnly, real);
	$("#btn-search").prop("disabled", false);
	$("#btn-search .loading-btn").remove();
}

function search(ajax, queryCareStkOnly){
	searchEarning(ajax, queryCareStkOnly, false);
}

</script>