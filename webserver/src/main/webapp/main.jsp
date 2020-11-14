<%@ page import="com.stk123.task.schedule.InternetSearch" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	pageContext.setAttribute(CommonConstant.PAGE_TITLE, "每日指标");
	StkContext sc = StkContext.getContext();
    Connection conn = sc.getConnection();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="/common/header.jsp" %>
<%@include file="/common/js_amcharts.jsp" %>
<body>
<%@include file="/common/nav.jsp" %>
<script>$(".icon-globe").parent().parent().addClass("active");</script>
<div style="text-align:center;margin:0 20px;">
<div id="chart-growth-pe-cn" style="width:100%; height:800px;"></div>
<br/>
<div id="chart-daily-report-us" style="width:100%; height:800px;"></div>
<br/>
<%
  out.println(InternetSearch.createStkAccountInfoTable(conn, 10000));
%>
</div>
</body>
</html>
<script type="text/javascript">

$(function() {
	if($("#chart-growth-pe-cn").length > 0){
	    $.ajax({
	      url:'/chartgrowthpe.do',
	      async:false,
	      success: function (data) {
	    	  var chartData = JSON.parse(data, function (key, value) {
	    	        var a;  
	    	        if (typeof value === 'string') { 
	    	        	if(key == 'DATE'){
	    	        		var a = /^(\d{4})(\d{2})(\d{2})$/.exec(value);
	    	        		return new Date(a[1],--a[2],a[3],0,0,0);
	    	        	}
	    	        }  
	    	        return value;  
	    	  });
	    	  createLineChart("chart-growth-pe-cn",chartData,chartTemplate);
	      }
	    });
	}
	
	if($("#chart-daily-report-us").length > 0){
	    $.ajax({
	      url:'/chartdailyreportus.do',
	      async:false,
	      success: function (data) {
	    	  var chartData = JSON.parse(data, function (key, value) {
	    	        var a;  
	    	        if (typeof value === 'string') { 
	    	        	if(key == 'DATE'){
	    	        		var a = /^(\d{4})(\d{2})(\d{2})$/.exec(value);
	    	        		return new Date(a[1],--a[2],a[3],0,0,0);
	    	        	}
	    	        }  
	    	        return value;  
	    	  });
	    	  createLineChart("chart-daily-report-us",chartData,chartTemplateUS);
	      }
	    });
	}
});

var chartTemplate = {dataSet:{
	 fieldMappings:[
	         		{fromField: "VALUE",toField: "VALUE"},
	         		{fromField: "SH180",toField: "SH180"},
	          	    {fromField: "PE",toField: "PE"},
	          		{fromField: "ENEUPPER",toField: "ENEUPPER"},
	          		{fromField: "ENELOWER",toField: "ENELOWER"},
	          		{fromField: "FLOWLARGE",toField: "FLOWLARGE"},
	          		{fromField: "FLOWSMALL",toField: "FLOWSMALL"},
	          		{fromField: "BIAS",toField: "BIAS"},
	          		{fromField: "RESULT_3",toField: "RESULT_3"},
	          		{fromField: "RESULT_4",toField: "RESULT_4"},
	          		{fromField: "RESULT_5",toField: "RESULT_5"},
	          		{fromField: "RESULT_6",toField: "RESULT_6"}
	          		
	          	  ],
		categoryField:"DATE"
	 },
	 panel:[
           {
           	title:"Panel-1",
           	axis:[{},{position:"right",gridAlpha:0}],
           	graph:[
                      {
                   	   title:"中证1000",
                   	   valueField:"VALUE",
                   	   lineColor:"blue",
                          valueAxis:0 //对应axis数组的index
                      },
                      {
                   	   title:"上证180",
                   	   valueField:"SH180",
                   	   lineColor:"green",
                          valueAxis:1
                      }
                     ]
           },
           {
              	title:"时间窗口",
              	graph:[
                         {
                      	   title:"时间窗口",
                      	   valueField:"RESULT_6",
                      	   lineColor:"blue",
                          	   valueAxis:0
                         }
                        ]
           },
           {
           	title:"ENE",
           	axis:[{},{position:"right",gridAlpha:0,offset:30}],
                  graph:[
                          {
                       	   title:"ENE Upper",
                       	   valueField:"ENEUPPER",
                       	   lineColor:"green",
                       	   valueAxis:0
                          },
                          {
                       	   title:"ENE Lower",
                       	   valueField:"ENELOWER",
                       	   lineColor:"red",
                       	   valueAxis:2
                          }
                         ]
           },
           {
           	title:"成长股PE",
           	graph:[
                      {
                   	   title:"成长股PE",
                   	   valueField:"PE",
                   	   lineColor:"red",
                       	   valueAxis:0
                      }
                     ]
           },
           {
           	title:"乖离率",
           	axis:[{},{position:"right",gridAlpha:0}],
           	graph:[
                      {
                   	   title:"乖离率",
                   	   //hidden:true,
                   	   valueField:"BIAS",
                   	   lineColor:"blue",
                       	   valueAxis:0
                      }
                     ]
           },
           {
           	title:"抄底-1",
           	axis:[{},{position:"right",gridAlpha:0}],
                  graph:[
					      {
                       	   title:"一品抄底-底部",
                       	   valueField:"RESULT_3",
                       	   lineColor:"green",
                       	   valueAxis:0
                          },
                          {
                       	   title:"二品抄底-买入时机",
                       	   valueField:"RESULT_5",
                       	   lineColor:"red",
                       	   valueAxis:1
                          }
                         ]
           },
           {
           	title:"抄底-2",
                  graph:[
					   {
						   title:"一品抄底-MACD",
						   valueField:"RESULT_4",
						   lineColor:"blue",
						   valueAxis:0
					   }
                   ]
           },
           {
           	title:"资金流向-1",
                  graph:[
					   {
						   title:"资金流向(大盘股)",
						   valueField:"FLOWLARGE",
						   lineColor:"blue",
						   valueAxis:1
					   }
					 ]
           },
           {
           	title:"资金流向-2",
           	graph:[
                      {
                   	   title:"资金流向(小盘股)",
                   	   valueField:"FLOWSMALL",
                   	   lineColor:"blue",
                       	   valueAxis:0
                      }
                     ]
           }
           
	 ]};



var chartTemplateUS = {dataSet:{
	 fieldMappings:[
	         		{fromField: "DJI",toField: "DJI"},
	          		{fromField: "RESULT_1",toField: "RESULT_1"},
	          		{fromField: "RESULT_2",toField: "RESULT_2"}
	          	  ],
		categoryField:"DATE"
	 },
	 panel:[
           {
           	title:"道琼斯",
           	axis:[{},{position:"right",gridAlpha:0}],
           	graph:[
                      {
                   	   title:"道琼斯",
                   	   valueField:"DJI",
                   	   lineColor:"blue",
                          valueAxis:0 //对应axis数组的index
                      }
                     ]
           },
           {
              	title:"中概平均PE (中概avg pe在20~30之间波动)",
              	graph:[
                         {
                      	   title:"PE",
                      	   valueField:"RESULT_1",
                      	   lineColor:"blue",
                          	   valueAxis:0
                         }
                        ]
           },
           {
             	title:"美股平均PE",
             	graph:[
                        {
                     	   title:"PE",
                     	   valueField:"RESULT_2",
                     	   lineColor:"blue",
                         	   valueAxis:0
                        }
                       ]
          }
	 ]};



function createChartOfMain(chartData){
	
 		var dataSet = new AmCharts.DataSet();
 		dataSet.fieldMappings = [
 		{
 	        fromField: "value",
 	        toField: "value"},
 	    {
 			fromField: "PE",
 			toField: "PE"
 		},
 		{
 			fromField: "ENEUPPER",
 			toField: "ENEUPPER"
 		},
 		{
 			fromField: "ENELOWER",
 			toField: "ENELOWER"
 		}
 	    ];
 		dataSet.dataProvider = chartData;
 		dataSet.categoryField = "date";

    //createStockChart3("chart-growth-pe",[dataSet /* , dataSet2  */],chartTemplate);
    createStockChart2("chart-growth-pe",[dataSet /* , dataSet2  */]);
}

function createStockChart2(strId,ds) {
	var chart = new AmCharts.AmStockChart();
	chart.categoryAxesSettings.equalSpacing = true;
	chart.pathToImages = "./images/amcharts/";
	chart.dataSets = ds;
	chart.categoryAxesSettings.maxSeries=1500;
	// PANELS ///////////////////////////////////////////                                                  
	var stockPanel = new AmCharts.StockPanel();
	stockPanel.showCategoryAxis = true;
	stockPanel.title = "Panel-1";
	
	var stockPanel2 = new AmCharts.StockPanel();
	stockPanel2.showCategoryAxis = true;
	stockPanel2.title = "Panel-2";
	
	// add value axes
    var valueAxis1 = new AmCharts.ValueAxis();
    stockPanel.addValueAxis(valueAxis1);
    
    var valueAxis2 = new AmCharts.ValueAxis();
    valueAxis2.gridAlpha = 0;
    valueAxis2.position = "right";
    stockPanel.addValueAxis(valueAxis2);
    
    var valueAxis3 = new AmCharts.ValueAxis();
    //valueAxis3.gridAlpha = 0;
    valueAxis3.position = "right";
    //valueAxis3.offset = 30;
    stockPanel2.addValueAxis(valueAxis3);
	
    
	var graph = new AmCharts.StockGraph();
	graph.title = "深证综指";
	graph.valueField = "value";
	graph.balloonText = "[[title]]:<b>[[value]]</b>";
	graph.compareGraphBalloonText = "[[title]]:<b>[[value]]</b>";
	graph.lineThickness = 2;
	graph.lineColor = "gray";
    graph.useDataSetColors = false;
	graph.valueAxis = valueAxis1;
	stockPanel.addStockGraph(graph);
	
	var graph2 = new AmCharts.StockGraph();
	graph2.title = "成长股PE";
	graph2.valueField = "PE";
	graph2.balloonText = "[[title]]:<b>[[PE]]</b>";
	graph2.compareGraphBalloonText = "[[title]]:<b>[[PE]]</b>";
	graph2.lineThickness = 2;
	graph2.useDataSetColors = true;
	graph2.valueAxis = valueAxis2;
	stockPanel.addStockGraph(graph2);
	
	var graph3 = new AmCharts.StockGraph();
	graph3.title = "ENE Upper";
	graph3.valueField = "ENEUPPER";
	graph3.balloonText = "[[title]]:<b>[[ENEUPPER]]</b>";
	graph3.compareGraphBalloonText = "[[title]]:<b>[[ENEUPPER]]</b>";
	graph3.lineThickness = 2;
	graph3.useDataSetColors = false;
	graph3.lineColor = "green";
	graph3.valueAxis = valueAxis3;
	stockPanel2.addStockGraph(graph3);
	
	var graph4 = new AmCharts.StockGraph();
	graph4.title = "ENE Lower";
	graph4.valueField = "ENELOWER";
	graph4.balloonText = "[[title]]:<b>[[ENELOWER]]</b>";
	graph4.compareGraphBalloonText = "[[title]]:<b>[[ENELOWER]]</b>";
	graph4.lineThickness = 2;
	graph4.useDataSetColors = false;
	graph4.lineColor = "red";
	graph4.valueAxis = valueAxis3;
	stockPanel2.addStockGraph(graph4);
	
	chart.panels = [stockPanel,stockPanel2];
	stockPanel.stockLegend = new AmCharts.StockLegend();
	stockPanel2.stockLegend = new AmCharts.StockLegend();

	// OTHER SETTINGS ////////////////////////////////////
	var scrollbarSettings = new AmCharts.ChartScrollbarSettings();
	chart.chartScrollbarSettings = scrollbarSettings;

	var cursorSettings = new AmCharts.ChartCursorSettings();
	cursorSettings.valueBalloonsEnabled = true;
	chart.chartCursorSettings = cursorSettings;

	chart.panelsSettings = new AmCharts.PanelsSettings();
	//chart.panelsSettings.marginRight = 30;
	
	chart.write(strId);
}

function createStockChart(strId,ds) {
	var chart = new AmCharts.AmStockChart();
	chart.categoryAxesSettings.equalSpacing = true;
	chart.pathToImages = "./images/amcharts/";

	chart.dataSets = ds;

	// PANELS ///////////////////////////////////////////                                                  
	var stockPanel = new AmCharts.StockPanel();
	stockPanel.showCategoryAxis = true;
	stockPanel.title = "Value";
	stockPanel.eraseAll = false;
	
	var graph = new AmCharts.StockGraph();
	graph.valueField = "value";
	graph.comparable = true;
	graph.compareField = "value"; 
	graph.balloonText = "[[title]]:<b>[[value]]</b>";
	graph.compareGraphBalloonText = "[[title]]:<b>[[value]]</b>";
	graph.lineThickness = 2;
	graph.useDataSetColors = true;
	stockPanel.addStockGraph(graph);
	
	var stockLegend = new AmCharts.StockLegend();
	stockLegend.periodValueTextComparing = "[[percents.value.close]]%";
	stockLegend.periodValueTextRegular = "[[value.close]]";
	stockPanel.stockLegend = stockLegend;
	stockPanel.drawingIconsEnabled = true;

	chart.panels = [stockPanel];


	// OTHER SETTINGS ////////////////////////////////////
	var scrollbarSettings = new AmCharts.ChartScrollbarSettings();
	scrollbarSettings.graph = graph;
	scrollbarSettings.updateOnReleaseOnly = true;
	chart.chartScrollbarSettings = scrollbarSettings;

	var cursorSettings = new AmCharts.ChartCursorSettings();
	cursorSettings.valueBalloonsEnabled = true;
	chart.chartCursorSettings = cursorSettings;


	// PERIOD SELECTOR ///////////////////////////////////
	var periodSelector = new AmCharts.PeriodSelector();
	periodSelector.position = "left";
	periodSelector.periods = [{
		period: "DD",
		count: 10,
		label: "10 days"
	}, {
		period: "MM",
		count: 1,
		label: "1 month"
	}, {
		period: "YYYY",
		count: 1,
		label: "1 year"
	}, {
		period: "YTD",
		label: "YTD"
	}, {
		period: "MAX",
		label: "MAX"
	}];
	chart.periodSelector = periodSelector;

	var panelsSettings = new AmCharts.PanelsSettings();
	chart.panelsSettings = panelsSettings;
	
	var dataSetSelector = new AmCharts.DataSetSelector();
	dataSetSelector.position = "left";
	//dataSetSelector.width = "150px";
	chart.dataSetSelector = dataSetSelector;

	chart.write(strId);
}
</script>