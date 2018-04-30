<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	StkContext sc = StkContext.getContext();
	Industry industrySelect = (Industry)sc.get("index_select");
	if(industrySelect!=null){
		pageContext.setAttribute(StkConstant.PAGE_TITLE, industrySelect.getType().getName() + " - 数据指标");
	}else{
		pageContext.setAttribute(StkConstant.PAGE_TITLE, "数据指标");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="/common/header.jsp" %>
<link rel="stylesheet" type="text/css" href="/css/larger.css" />
<style type="text/css">
.over{
background-color: #eeeeee;
}
.loading-hide{
display:none; 
height:100%;
width: 100%;
position: absolute;
text-align:center;
vertical-align:middle;
filter:alpha(opacity=50);
-moz-opacity:0.5;
opacity: 0.5;
}
.icon-max{
float: right;
margin-top:8px;
margin-right:-6px;
cursor: pointer;
}
.chart-container-full{
display:block;
width:100%;
height:100%; 
position:absolute; 
"background-color":#eeeeee; 
top:0px;
left:0px;
/* filter: "alpha(opacity = 50)", 
"-moz-opacity":"0.5",
"opacity":"0.5", */
z-index: 1200;
text-align: center;
}
.content-full{
margin-left:20px;
margin-right:20px;
margin-top:20px;
-webkit-box-shadow: 0 5px 15px rgba(0,0,0,.5);
box-shadow: 0 5px 15px rgba(0,0,0,.5);
overflow:auto;
}
.chart-tags{
float:left; 
width: 80%;
margin-left:10px;
border: 2px solid #a5d24a;
border-radius: 5px;
-moz-border-radius: 5px;
-webkit-border-radius: 5px;
padding: 8px 35px 8px 14px;
}
</style>
<body>
<%@include file="/common/nav.jsp" %>
<script>$(".icon-picture").parent().parent().addClass("active");</script>
<%@include file="/common/js_jstree.jsp" %>
<div class="container" id="chart-container">
<div class="content">
  <div class="page-header" style="position: relative;">
    <div style="float: left;">
    <h3><span class="icon-picture"></span> 数据指标</h3>
    </div>
    <div style="position: absolute;left:165px;top:9px;" >
    <ul class="nav nav-tabs" role="tablist">
      <li class="active"><a href="#chart-box" role="tab" data-toggle="tab">图表区</a></li>
      <li><a href="#q" role="tab" data-toggle="tab">制图区</a></li>
    </ul>
    </div>
  </div>
  <div id="p" class="easyui-layout" style="height:800px;">
     <div data-options="region:'west'" id="chart-left" style="width:160px; padding:5px;overflow: auto;">
       <input type="text" id="index_search" style="width: 120px" placeholder="查询指标"/>
       <div id="index-tree"></div>
     </div>
        
     <div data-options="region:'center'" id="chart-center" style="border: 0px">
        <div id="loading-model" class="loading-hide over" style="z-index: 999">
          <div id="dd"></div>
      	  <span class="loading" style="margin:320px auto;" data-original-title="正在努力加载…">加载…</span>
        </div>
        <div class="tab-content" >
          <div id="chart-box" class="tab-pane active" style="height: 99%"></div>
            <div id="q" class="tab-pane">
              <div class="well" style="margin:20px 30px;">
                sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss
              </div>
              <div class="well" style="margin:20px 30px;background-color: #fff;overflow: auto;">
                <div style="margin: 10px;overflow: auto;">
                  <h1 style="float:left;margin-top:5px">左轴：</h1>
                <div id="tags-left" class="chart-tags"></div>
              </div>
              <div style="padding:10px;overflow:auto;">
                <h1 style="float:left;margin-top:5px">右轴：</h1>
                <div id="tags-right" class="chart-tags"></div>
              </div>
              </div>
              
      	  </div>
        </div>
    </div>
    
  </div>
</div>

</div>
</body>
</html>
<%@include file="/common/js_easyui_jqueryui.jsp" %>
<%@include file="/common/js_amcharts.jsp" %>
<script>
var chart;
var chartHeight=window.screen.height-230;
$(function(){
  if($("#index-tree").length > 0){
    $("#index-tree").jstree({
      "plugins" : [ "themes", "json_data", "ui", "search" ],
      "json_data" : {"ajax" : {"url" : "/index?method=getIndexTree", "data" : "" }},
        "themes":{"theme" : "classic", "dots" : true, "icons" : false },
        "search":{show_only_matches: true}
    }).bind("select_node.jstree", function(e, data){
      if(data.rslt.obj.attr("isleaf") == 'Y'){
        reloadData(data.rslt.obj.attr("id"),data.rslt.obj.attr("title"));
      }
    });
    /* var curInd = $('#industry_select_id').val();
    if(curInd != ''){
      setTimeout(function () {$.jstree._focused().select_node("#"+curInd); }, 500);
    } */
  }
  if(getRequest()['s'] != 'undefined'){
	  $("#index_search").val(getRequest()['s']);
	  setTimeout(indexSearch,300);
	  setTimeout(function(){
		  $('.jstree-search').click();
	  },500);
  }
});
function indexSearch() {
  $("#index-tree").jstree("search", $("#index_search").val());
}
$(function() {  
    $("#index_search").keyup(function(event){  
      if(event.keyCode == 13){  
        indexSearch();
        $("#index_search").select();
      }  
    });
});

$(function() {
  if($("#chart-box").length > 0){
    $(".layout-button-left").bind("click",function(){
      chartResize();
    });
    $(".layout-button-right").bind("click",function(){chartResize();});
    $("#chart-container").bind("click",function(){chartResize();});
  }
});

function chartResize(){
  if(chart != null)chart.invalidateSize();
}

function reloadData(id,t) {  
  var defaultTemplate = {
     height:chartHeight,
     dataSet:{fieldMappings:[{fromField:id,toField:id}],categoryField:"d"},
     panel:[{graph:[{title:t,valueField:id}]}]
  };
  
  var chartTemplate;
  var chartData;
  
  var dynamicData = $('body').data(id);
  if(dynamicData == undefined){
    $.ajax({
          url:'/index?method=index&id='+id,
          async:false,
          dataType:'json',
          success: function (data) {
            var result = eval(data);
            chartTemplate = result.template;
            $.each(result.datas, function (i, value) {
                  var a = /^(\d{4})(\d{2})(\d{2})$/.exec(value.d);
                  result.datas[i].d = new Date(a[1],--a[2],a[3],0,0,0);
            });
            chartData = result.datas;
            $('body').data(id,{"template":chartTemplate,"data":chartData});
          }
    });
  }else{
    chartTemplate = dynamicData.template;
    chartData = dynamicData.data;
  }
  if(chartTemplate == "undefined"){
      chartTemplate = defaultTemplate;
    }else{
      chartTemplate = eval("("+chartTemplate+")");
    }
  //chartTemplate = {height:(chartHeight+150),dataSet:{fieldMappings:[{fromField:"a",toField:"a"},{fromField:"b",toField:"b"},{fromField:"c",toField:"c"},{fromField:"v",toField:"v"}],categoryField:"d"},panel:[{graph:[{title:"中小板",valueField:"a"},{title:"创业板",valueField:"b"},{title:"沪深",valueField:"c"},{title:"平均PE",valueField:"v",bullet:"round"}]}]};
    chart = drawLineChart("chart-box",chartData,chartTemplate);;  
    //chart.validateNow();  
    //chart.validateData();  
} 
</script>