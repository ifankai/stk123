<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	pageContext.setAttribute(CommonConstant.PAGE_TITLE, "A股筛选器");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="/common/header.jsp" %>
<link rel="stylesheet" type="text/css" href="/css/larger.css" />
<link rel="stylesheet" type="text/css" href="/js/easyui/themes/bootstrap/easyui.css">
<style type="text/css">
.earningTable tr td{
  text-overflow:ellipsis;
  word-break:keep-all; 
  white-space:nowrap;
  text-align: right; 
}
.earningTable tbody tr:hover
{ 
  background-color:#FAFAD2;
}
.earningTable tbody tr.selected {
  background-color: #B0BED9;
}
.earningTable .dataTables_empty{
  text-align: center; 
}
.table th, .table td {
    padding: 6px 0;
}
select, textarea, input[type="text"], input[type="password"], input[type="datetime"], input[type="datetime-local"], input[type="date"], input[type="month"], input[type="time"], input[type="week"], input[type="number"], input[type="email"], input[type="url"], input[type="search"], input[type="tel"], input[type="color"] {
	padding:2px;
}
.content .page-container {
    margin: 5px 20px 5px;
}
.content .page-container .dataTables_wrapper table.table {
    margin-bottom: 5px;
}
.s_table{
	border: 2px solid #ddd;
	font-size:12px;
}
.s_table td{
	border: 1px solid #ddd;
}
.s_table td.selected{
	background-color:yellow;
} 
</style>
<body>
<%@include file="/common/nav.jsp" %>
<script>$(".icon-earning").parent().parent().addClass("active");</script>
<%@include file="/common/js_datatables.jsp" %>
<script src="/js/bootstrap-datepicker.js"></script>
<script src="/js/easyui/jquery.easyui.min.js"></script>
<div class="container">
<div class="content">
  <!-- Page header -->
  <div class="page-header" style="position: relative;">
    <div style="float: left;">
    <!-- h4><span class="icon-tasks"></span> A股筛选器</h4 -->
    <div>
    	<select multiple="multiple" id="conditionSelect" size="7" style="width:180px;margin-bottom:0px;" onchange="select()">
    	</select>
    </div>
    </div>
    <form id="screenerForm" type="post">
    <div class="controls" style="float:right;">
		<div class="input-append">
			<b>实际业绩:</b>
			PE(TTM)<input type="text" name="peFrom" style="width: 25px"/>
			~<input type="text" name="peTo" style="width: 25px"/>
			&nbsp;
			PB(TTM)<input type="text" name="pbFrom" style="width: 20px"/>
			~<input type="text" name="pbTo" style="width: 20px"/>
			&nbsp;
			PS(TTM)<input type="text" name="psFrom" style="width: 20px"/>
			~<input type="text" name="psTo" style="width: 20px"/>
			&nbsp;
			净利润增速<input type="text" name="netProfitGrowthRateFrom" style="width: 30px"/>
			~<input type="text" name="netProfitGrowthRateTo" style="width: 30px"/>
			&nbsp;
			营收增速<input type="text" name="revenueGrowthRateFrom" style="width: 30px"/>
			~<input type="text" name="revenueGrowthRateTo" style="width: 30px"/>
			&nbsp;
			毛利率<input type="text" name="grossProfitMarginFrom" style="width: 24px"/>
			~<input type="text" name="grossProfitMarginTo" style="width: 24px"/>
			&nbsp;
			总市值<input type="text" name="marketCapFrom" style="width: 30px"/>
			~<input type="text" name="marketCapTo" style="width: 30px"/>
		</div>
		<div class="input-append">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			
			负债率<input type="text" name="debtRateFrom" style="width: 24px"/>
			~<input type="text" name="debtRateTo" style="width: 24px"/>
			&nbsp;
			上市天数<input type="text" name="listingDaysFrom" style="width: 30px"/>
			~<input type="text" name="listingDaysTo" style="width: 30px"/>
			&nbsp;
			行业
			<input class="easyui-combobox" name="industry" id="industry" style="padding:3px;width:180px"
			data-options="multiple:true,
				url: '/screener?from=cn&method=getIndustry',
				method: 'get',
				valueField:'value',
				textField:'text',
				groupField:'group',
				filter: function(q, row){
					var opts = $(this).combobox('options');                                                      
					return row[opts.textField].indexOf(q) >-1;
				}">
			&nbsp;
			策略
			<input class="easyui-combobox" name="strategy" id="strategy" style="padding:3px;width:400px" 
			data-options="multiple:true,
				url: '/screener?from=cn&method=getStrategy',
				method: 'get',
				valueField:'value',
				textField:'text',
				groupField:'group',
				filter: function(q, row){
					var opts = $(this).combobox('options');                                                      
					return row[opts.textField].indexOf(q) >-1;
				}">
		</div>
		<div class="input-append">
			<b>预告业绩:</b>
			预告业绩低点<input type="text" name="erLowFrom" style="width: 25px"/>
			~<input type="text" name="erLowTo" style="width: 25px"/>
			&nbsp;
			预告业绩高点<input type="text" name="erHighFrom" style="width: 25px"/>
			~<input type="text" name="erHighTo" style="width: 25px"/>
			&nbsp;
			预告PE<input type="text" name="erPeFrom" style="width: 25px"/>
			~<input type="text" name="erPeTo" style="width: 25px"/>
		    <input type="button" class="btn" onclick="clearCondition();" value="清空"/>
		    <input type="button" class="btn" onclick="deleteCondition();" value="删除"/>
		    &nbsp;
			应用策略
			<input class="easyui-combobox" name="astrategy" id="astrategy" style="padding:3px;width:300px"
			data-options="multiple:true,
				url: '/screener?from=cn&method=getAvailableStrategy',
				method: 'get',
				valueField:'value',
				textField:'text',
				groupField:'group',
				filter: function(q, row){
					var opts = $(this).combobox('options');                                                      
					return row[opts.textField].indexOf(q) >-1;
				}">
			<input type="button" class="btn" onclick="doStrategy();" value="应用"/>
		</div>
		<div class="input-append">
			<b>股票代码:</b>
			<textarea rows="2" name="codes" style="width:350px;margin-bottom:0px;"/></textarea>
		    <input type="button" class="btn" onclick="search();" value="查询"/>
		    <input type="button" class="btn" onclick="extract();" value="提取"/>
		    <button type="button" class="btn" data-toggle="modal" data-target="#myModal">保存</button>
		    &nbsp;备注
		    <textarea rows="2" name="comment" style="width:400px;margin-bottom:0px;"/></textarea>							
		</div>
	</div>
	</form>
  </div>
  
  
  <div class="modal" id="myModal" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display:none;">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span class="sr-only">x</span></button>
        <h4 class="modal-title" id="myModalLabel" style="color:#d43f3f">保存查询条件</h4>
      </div>
      <div class="modal-body form-horizontal" id="regForm">
		<fieldset>
			<div class="control-group" id="nickname-group">
				<label class="control-label" for="condition">名称：</label>
				<div class="controls">
					<input id="condition" class="input-large" type="text" placeholder="名称">
					<input id="id" type="hidden">
				</div>
			</div>			
		</fieldset>
      </div>
      <div class="modal-footer">
     	<button type="button" class="btn btn-primary" id="reg-btn" onclick="save();">保存</button>
       	<button type="button" class="btn btn-default" id="cancel-btn" data-dismiss="modal">取消</button>
      </div>
    </div>
  </div>
</div>
  
  <!-- Page container -->
  <div class="page-container">
    <div class="tab-content" >
      <div style="position: absolute;left:350px;top:215px;">
      	<div style="position:relative;float:left;" id="mv_refresh_time"></div>
      	<div style="position:relative;float:left;top:-5px;left:10px;">
      		<button type="button" id="mvRefreshBtn" class="btn" onclick="mvRefresh();">刷新物化视图</button>
      	</div>
      </div>
      <div class="tab-pane active">
        <table class="earningTable table table-striped" id="earningTable">
            <thead>
	            <tr>	       
	            <th id="column-stk">股票</th>
	            <th>自定义</th>
	            <!-- th>上市天数</th-->
	            <th id="column-industry">行业</th>
	            <th>市值</th>
	            <th title="ROE(TTM)">ROE</th>
	            <th title="(er_date)">预告季度</th>
	            <th title="(er_low/er_high)">业绩预告</th>
	            <th title="业绩预告均值">均值</th>
	            <th>PEG</th>
	            <th title="业绩预告2年净利润季度新高">预告净高</th>
	            <th>PE</th>
	            <th title="PE(YOY)/PE(TTM)">静PE/动PE</th>
	            <th>预告PE</th>
	            <th>PE/预告PE</th>
	            <th title="预测今年PE(forecast_pe_this_year)">今年PE</th>
	            <th title="预测明年PE(forecast_pe_next_year)">明年PE</th>
	            <th>PB</th>
	            <th>PS</th>
	            <th>PE分位</th>
	            <th>PB分位</th>
	            <th>PS分位</th>
	            <th title="(ntile)">总分位</th>
	            <th title="(gross_profit_margin)">毛利率</th>
	            <th title="(Sale_Profit_Margin)">净利率</th>
	            <th title="(Revenue_growth_rate)">营收同比</th>
	            <th title="2年营收季度新高">2年营高</th>
	            <th title="(net_profit_Growth_rate)">净利润同比</th>
	            <th title="2年净利润季度新高">2年净高</th>
	            <th title="经营现金净流量与净利润的比率(cash_net_profit_rate)">经现/净利</th>
	            <th title="(debt_rate)">负债率</th>
	            <th>研发占比</th>
	            <th>财务季度</th>
	            <th>10日涨</th>
	            <th>20日涨</th>
	            <th>30日涨</th>
	            <th>60日涨</th>
	            <th>120日涨</th>	            
	            </tr>
            </thead>
            <tfoot>
	            <tr>
	            <%for(int i=0;i<=36;i++){%>
			       <th><input type="text" style="width:80%" class="column_filter" data-column="<%=i%>"/></th>
	            <%}%>
	            </tr>
            </tfoot>
            
          
        </table>
      </div>
    </div>
    
    <div class="content">
	  <div class="page-container">
	   <div class="row">
		<ul style="list-style:disc;">
			<li><b>主营行业集中在：</b><br><font id="industryAnaylse2"></font></li>
			<li><b>行业集中在：</b><br><font id="industryAnaylse"></font></li>
			<li><b>最近1个月资金流入行业：</b><br><font id="industryMoneyFlow"></font></li>
			<li><b>股票：</b><br><font id="scodes"></font></li>
		</ul>
	   </div>
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
<%@include file="/common/js_contextmenu.jsp" %>
<script type="text/javascript">
function search(m){
	$('.earningTable').DataTable().destroy();
	$('.earningTable').dataTable( {
	      "language": datatable_lang,
	      "order": [],
	      "aLengthMenu": [[15, 30, 50, 100, -1], [15, 30, 50, 100, "All"]],
	      "iDisplayLength": 15,
	      "ajax":"/screener?method="+(m==undefined?"searchForCN":m)+"&"+$('#screenerForm').serialize(),
	      "rowCallback": function( row, data ) {
	      	  $(row).addClass('r-menu-close');
	      },
	      "initComplete": function(settings, json) {
	    	   $('#earningTable td .content-summary').hover( function (){
	    		   $(this).parent().prop('title', ($(this).parent().find('.content-detail-inner').text()));
	    	   });
	    	   showKline('.earningTable', '#column-stk');
	    	   $('#industryAnaylse').html(json.industryAnaylse);
	    	   $('#industryAnaylse2').html(json.industryAnaylse2);
	    	   $('#scodes').html("'"+json.codes+"'")
	    	   
	    	   rightMenu('删除','right-menu-delete','delete');
	    	   $('.r-menu-close').contextMenu('right-menu-delete', {
	    	      bindings: {
	    	        'delete': function(t) {
	    	        	confirmDialog(50,260,'确定要删除标签 <b>'+$(t).find('td:first').html()+'</b> 吗？',deleteRow,t);
	    	        }
	    	      },      
	    	      shadow: false
	    	   });
  		   }
	    });
}

function deleteRow(id){	
	var code = $(id).find('td:first > a').attr('data-code');
	var codes = $("textarea[name='codes']").val();
	$("textarea[name='codes']").val(codes.replace(code,''));
	$("textarea[name='codes']").val($("textarea[name='codes']").val().replace(',,',','));
	
	$('.earningTable').DataTable().row(id).remove().draw();
	return true;
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
		"aLengthMenu": [[15, 30, 50, 100, -1], [15, 30, 50, 100, "All"]],
	    "iDisplayLength": 15
	});
    
    $('input.column_filter').on( 'keyup click', function () {
        filterColumn($(this).val(), $(this).attr('data-column') );
    } );
    
    $('#earningTable').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
        }
        else {
        	$('#earningTable').DataTable().$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    } );

    list(null);
    
    getMVRefreshTime();
    
    window.setInterval("getMVRefreshTime()", 1000*60*5);
    
    getMoneyFlow();
});

function getMoneyFlow(){
	$.ajax({
		  type: 'get',
		  url: '/screener?method=getMoneyFlow',
		  dataType: 'json',
	      success: function (data) {
			if(data){
				var result = eval(data);
				var html = "<table class='s_table'>";
				for(var i=0; i<=30; i++){
					if(i == 0){
						html += "<tr>";
						for(var j=0; j<result.length; j++){
						    var cond = result[j];
				    		html += "<td style='text-align:center'>"+cond[i].flow_date+"</td>"
						}
						html += "</tr>"
				    }
					html += "<tr>";	
					for(var j=0; j<result.length; j++){
						//if(i == 10)$("#industryMoneyFlow").append('<br>');
					    var cond = result[j];
					    
		               	html += "<td><a href='javascript:setIndustry(\""+cond[i].name+"\");'>"+cond[i].name+"</a>["+cond[i].hot+"]</td>";
					
					}
					html += "</tr>";
				}
				html += "</table>";
				$("#industryMoneyFlow").append(html);
				
				$(".s_table td").mouseover(function(){
					var text = $(this).text();
					$(".s_table td a:contains('"+text.split('[')[0]+"')").parent().addClass('selected');
				});
				$(".s_table td").mouseleave(function(){
					var text = $(this).text();
					$(".s_table td a:contains('"+text.split('[')[0]+"')").parent().removeClass('selected');
				});
			}
		  }
		});
}

function setIndustry(name){
	$('#industry').combobox('setText',name);
	var e = jQuery.Event("keydown");
	$('#industry .combo-arrow').click();
	/* var e = jQuery.Event("keydown");
	e.keyCode=32;
	setTimeout(function(){$(".combo-text").trigger(e);}, 200); */
	
	/* var evtType = 'keyup';
	var evtObj = document.createEvent('UIEvents');
    evtObj.initUIEvent(evtType, true, true, window, 1);

    delete evtObj.keyCode;
    if (typeof evtObj.keyCode === "undefined") {//为了模拟keycode
        Object.defineProperty(evtObj, "keyCode", { value: 32 });                       
    } else {
        evtObj.key = String.fromCharCode(keyCode);
    }

    if (typeof evtObj.ctrlKey === 'undefined') {//为了模拟ctrl键
        Object.defineProperty(evtObj, "ctrlKey", { value: true });
    } else {
        evtObj.ctrlKey = true;
    }
    $('.combo-arrow')[0].dispatchEvent(evtObj); */
    
	simulateKeyPress(' ');
}

function simulateKeyPress(character) {
	jQuery.event.trigger({ type : 'keypress', which : character.charCodeAt(0) });
}

function list(id){
	$.ajax({
	  type: 'get',
	  url: '/screener?method=list',
	  dataType: 'json',
	  data: "type=cn",
      success: function (data) {
		if(data){
			var result = eval(data);
			$("#conditionSelect").empty();
			$(result).each(function(i) {
			    var cond = result[i];
               	$("#conditionSelect").append("<option value=" + cond.id + ">" + cond.name + "</option>");
			});
			if(id != null){
				$("#conditionSelect").find("option[value='"+id+"']").attr("selected",true);
			}
			
		}
	  }
	});
}

function save(){
	$.ajax({
	  type: 'post',
	  url: '/screener?method=save',
	  dataType: 'json',
	  data: "type=cn&n="+$('#condition').val()+"&id="+$('#id').val()+'&'+$('#screenerForm').serialize(),
      success: function (data) {
		if(data){
			$("#cancel-btn").click();
			$("#id").val(data.id);
			messageAndClose(20,150,1000,"保存成功！");
			list(data.id);			
		}		
	  }
	});
}

function clearCondition(){
	$("#condition").val('');
	$("#id").val('');
	$("#screenerForm")[0].reset();	
	$('#industry').combobox('setValues','');
	$('#strategy').combobox('setValues','');
}

function select(){	
	$.ajax({
	  type: 'get',
	  url: '/screener?method=select',
	  dataType: 'json',
	  data: "id="+$("#conditionSelect").val(),
      success: function (data) {
		if(data){
			clearCondition();
			$("#id").val(data.id);
			$("#condition").val(data.name);				
			loadJsonToForm($('#screenerForm'), data.text);
		}
	  }
	});
}

function extract(){
	$.ajax({
		  type: 'post',
		  url: '/screener?method=extract',
		  dataType: 'json',
		  data: "codes="+$("textarea[name='codes']").val().replace(/\%/g,'%25').replace(/\&/g,''),
	      success: function (data) {
			if(data){
				clearCondition();
				var result = eval(data);
				$(result).each(function(i) {
				    var code = result[i];
				    if(i == 0){
				    	$("textarea[name='codes']").val(code);
				    }else{
				    	$("textarea[name='codes']").val($("textarea[name='codes']").val()+','+code);
				    }
				});
			}
		  }
		});
}

function getMVRefreshTime(){
	$.ajax({
		  type: 'get',
		  url: '/screener?method=getMVRefreshTime',
	      success: function (data) {
			if(data){
				$('#mv_refresh_time').html('物化视图最后刷新时间：'+data)
			}
		  }
		});
}
function mvRefresh(){
	$('#mvRefreshBtn').prop("disabled", true);
	$("#mvRefreshBtn").prepend("<span class=\"loading-btn\"></span>");
	$.ajax({
		  type: 'get',
		  url: '/screener?method=refreshMV',
	      success: function (data) {
	    	$('#mvRefreshBtn').prop("disabled", false);
	    	$("#mvRefreshBtn .loading-btn").remove();
			if(data){
				getMVRefreshTime();
			}
		  }
		});
}
function deleteCondition(){
	if($("#id").val() == ''){
		alertAndClose(20,200,1000,'请选择查询条件！');
		return;
	}
	confirmDialog(50,200,"确定要删除查询条件 '"+$('#condition').val()+"' 吗？",deleteSearchCondition,$("#id").val());
}
function deleteSearchCondition(id){
	$.ajax({
		  type: 'get',
		  url: '/screener?method=deleteCondition&id='+id,
	      success: function (data) {
			if(data > 0){
				list(null);
				messageAndClose(20,150,1000,"删除成功");
				clearCondition();
			}
		  }
		});
	return true;
}
function doStrategy(){
	search('doStrategy');
}
</script>