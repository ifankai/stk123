//amchart.js
AmCharts.AmChart.prototype.brr = function(){
/*	var k = document.createElement("span");
	var h = document.createTextNode("stk123.com");
	k.appendChild(h);
	this.chartDiv.appendChild(k);
*/};

function drawLineChart(strId,chartData,template) {
	var dataSet = new AmCharts.DataSet();
	dataSet.fieldMappings = template.dataSet.fieldMappings;
    dataSet.dataProvider = chartData;
	dataSet.categoryField = template.dataSet.categoryField;
	
	var chart = new AmCharts.AmStockChart();
	chart.language = "cn";
	chart.categoryAxesSettings.equalSpacing = true;
	chart.categoryAxesSettings.maxSeries=1500;
	chart.pathToImages = "./images/amcharts/";
	chart.dataSets = [dataSet];
	var totalPanelHeight = 0;
	for(var a in template.panel){
		var panel = template.panel[a];
		var stockPanel = new AmCharts.StockPanel();
		stockPanel.showCategoryAxis = true;
		//stockPanel.title = panel["title"];
		for(var c in panel){
			eval("stockPanel."+c+"=panel[c]");
		}
		if(panel["height"] != undefined){
			totalPanelHeight += panel["height"];
		}else{
			totalPanelHeight = totalPanelHeight + template.height;
		}
		
		var axises = [];
		for(var b in panel["axis"]){
			var valueAxis = new AmCharts.ValueAxis();
			var axis = panel["axis"][b];
			for(var c in axis){
				eval("valueAxis."+c+"=axis[c]");
			}
			stockPanel.addValueAxis(valueAxis);
			axises.push(valueAxis);
		}
		for(var b in panel["graph"]){
			var graph = panel["graph"][b];
			var stockGraph = new AmCharts.StockGraph();
			stockGraph.lineThickness = 1.5;
			stockGraph.useDataSetColors = false;
			stockGraph.balloonText = "[[title]]:<b>[[value]]</b>";
			//stockGraph.compareGraphBalloonText = "[[title]]:<b>[["+eval("graph.valueField")+"]]</b>";
			for(var c in graph){
				if("valueAxis" == c){
					stockGraph.valueAxis=axises[eval("graph[c]")];
				}else{
					eval("stockGraph."+c+"=graph[c]");
				}
			}
			stockPanel.addStockGraph(stockGraph);
		}
		
		chart.panels.push(stockPanel);
		stockPanel.stockLegend = new AmCharts.StockLegend();
	}
	eval("$('#"+strId+"').height('"+totalPanelHeight+"')");

	// OTHER SETTINGS 
	var sbsettings = new AmCharts.ChartScrollbarSettings();
	sbsettings.graph = graph;
	sbsettings.graphType = "line";
	sbsettings.usePeriod = "DD";
	chart.chartScrollbarSettings = sbsettings;

	var cursorSettings = new AmCharts.ChartCursorSettings();
	cursorSettings.valueBalloonsEnabled = true;
	chart.chartCursorSettings = cursorSettings;
	
	var periodSelector = new AmCharts.PeriodSelector();
	periodSelector.position = "bottom";
	periodSelector.periods = [
	 {period : "MM",count : 3,label : "3个月"}, 
	 {period : "MM",count : 6,label : "6个月"}, 
	 {period : "YYYY",count : 1,label : "1年"}, 
	 {period : "YYYY",count : 2,label : "2年",selected : true}, 
	 {period : "YYYY",count : 3,label : "3年"}, 
	 {period : "MAX",label : "最大"} 
	];
	chart.periodSelector = periodSelector;

	chart.panelsSettings = new AmCharts.PanelsSettings();
	chart.panelsSettings.marginRight = 10;
	chart.panelsSettings.marginLeft = 10;
	chart.write(strId);
	return chart;
}


function createLineChart(strId,chartData,template) {
	var dataSet = new AmCharts.DataSet();
	dataSet.fieldMappings = template.dataSet.fieldMappings;
    dataSet.dataProvider = chartData;
	dataSet.categoryField = template.dataSet.categoryField;
	
	var chart = new AmCharts.AmStockChart();
	chart.categoryAxesSettings.equalSpacing = true;
	chart.categoryAxesSettings.maxSeries=1500;
	chart.pathToImages = "./images/amcharts/";
	chart.dataSets = [dataSet];
	var totalPanelHeight = 0;
	for(var a in template.panel){
		var panel = template.panel[a];
		var stockPanel = new AmCharts.StockPanel();
		stockPanel.showCategoryAxis = true;
		//stockPanel.title = panel["title"];
		for(var c in panel){
			eval("stockPanel."+c+"=panel[c]");
		}
		if(panel["height"] != undefined){
			totalPanelHeight += panel["height"];
		}else{
			totalPanelHeight = totalPanelHeight + 360;
		}
		
		var axises = [];
		for(var b in panel["axis"]){
			var valueAxis = new AmCharts.ValueAxis();
			var axis = panel["axis"][b];
			for(var c in axis){
				eval("valueAxis."+c+"=axis[c]");
			}
			stockPanel.addValueAxis(valueAxis);
			axises.push(valueAxis);
		}
		for(var b in panel["graph"]){
			var graph = panel["graph"][b];
			var stockGraph = new AmCharts.StockGraph();
			stockGraph.lineThickness = 1.5;
			stockGraph.useDataSetColors = false;
			stockGraph.balloonText = "[[title]]:<b>[["+eval("graph.valueField")+"]]</b>";
			//stockGraph.compareGraphBalloonText = "[[title]]:<b>[["+eval("graph.valueField")+"]]</b>";
			for(var c in graph){
				if("valueAxis" == c){
					stockGraph.valueAxis=axises[eval("graph[c]")];
				}else{
					eval("stockGraph."+c+"=graph[c]");
				}
			}
			stockPanel.addStockGraph(stockGraph);
		}
		
		chart.panels.push(stockPanel);
		stockPanel.stockLegend = new AmCharts.StockLegend();
	}
	eval("$('#"+strId+"').height('"+totalPanelHeight+"')");

	// OTHER SETTINGS ////////////////////////////////////
	var scrollbarSettings = new AmCharts.ChartScrollbarSettings();
	chart.chartScrollbarSettings = scrollbarSettings;

	var cursorSettings = new AmCharts.ChartCursorSettings();
	cursorSettings.valueBalloonsEnabled = true;
	chart.chartCursorSettings = cursorSettings;

	chart.panelsSettings = new AmCharts.PanelsSettings();
	chart.panelsSettings.marginRight = 30;
	
	chart.write(strId);
}


$(function() { 
  if($("#chartstk").length > 0){
    var code = $("#scode").val();
    $.ajax({
      url:'/chartstk.do?s='+$("#scode").val(),
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
    	  createStockChart("chartstk",chartData);
      }
    });
  }  
});

function createStockChart(strId,chartData) {
	var dl = 5;
	var chart = new AmCharts.AmStockChart();
	chart.language = "cn";
	chart.categoryAxesSettings.maxSeries=1500;
	//chart.dataSetSelector.listHeight=150;
	chart.categoryAxesSettings.equalSpacing = true;
	chart.pathToImages = "./images/amcharts/";

	// DATASET //////////////////////////////////////////
	var dataSet = new AmCharts.DataSet();
	dataSet.fieldMappings = [ {
		fromField : "open",
		toField : "open"
	}, {
		fromField : "close",
		toField : "close"
	}, {
		fromField : "high",
		toField : "high"
	}, {
		fromField : "low",
		toField : "low"
	}, {
		fromField : "volumn",
		toField : "volumn"
	}, {
		fromField : "amount",
		toField : "amount"
	} ];
	//dataSet.color = "red";
	dataSet.dataProvider = chartData;
	dataSet.title = $("#sname").val();
	dataSet.categoryField = "date";

	chart.dataSets = [dataSet];

	// PANELS ///////////////////////////////////////////                                                  
	var stockPanel = new AmCharts.StockPanel();
	//stockPanel.title = "Value";
	stockPanel.showCategoryAxis = false;
	stockPanel.percentHeight = 80;

	var valueAxis = new AmCharts.ValueAxis();
	valueAxis.dashLength = dl;
	stockPanel.addValueAxis(valueAxis);

	stockPanel.categoryAxis.dashLength = dl;

	// graph of first stock panel
	var graph = new AmCharts.StockGraph();
	graph.type = "candlestick";
	graph.openField = "open";
	graph.closeField = "close";
	graph.highField = "high";
	graph.lowField = "low";
	graph.valueField = "close";
	graph.lineColor = "#7f8da9";
	graph.fillColors = "#7f8da9";
	graph.negativeLineColor = "#33AA11";
	graph.negativeFillColors = "#33AA11";
	graph.fillAlphas = 0.6;
	graph.useDataSetColors = true;
	graph.comparable = true;
	graph.compareField = "amount";
	graph.showBalloon = true;
	graph.balloonText = "开 盘 [[open]]<br>最 高 [[high]]<br>最 低 [[low]]<br>收 盘 [[close]]<br>成交量[[volumn]]";
	stockPanel.addStockGraph(graph);

	var stockLegend = new AmCharts.StockLegend();
	stockLegend.valueTextRegular = undefined;
	stockLegend.periodValueTextComparing = "[[percents.value.close]]%";
	stockPanel.stockLegend = stockLegend;

	stockPanel2 = new AmCharts.StockPanel();
	//stockPanel2.title = "Volumn";
	stockPanel2.percentHeight = 20;
	stockPanel2.marginTop = 1;
	stockPanel2.showCategoryAxis = true;

	var valueAxis2 = new AmCharts.ValueAxis();
	valueAxis2.dashLength = dl;
	stockPanel2.addValueAxis(valueAxis2);

	stockPanel2.categoryAxis.dashLength = dl;

	var graph2 = new AmCharts.StockGraph();
	graph2.valueField = "volumn";
	graph2.type = "column";
	graph2.showBalloon = false;
	
	//graph2.valueField = "close";
	graph2.colorField = "color";
	
	graph2.fillAlphas = 0.8;
	graph2.lineAlpha = 0;
	stockPanel2.addStockGraph(graph2);

	var legend2 = new AmCharts.StockLegend();
	legend2.markerType = "none";
	legend2.markerSize = 0;
	legend2.labelText = "";
	legend2.periodValueTextRegular = "[[value.close]]";
	stockPanel2.stockLegend = legend2;

	chart.panels = [ stockPanel, stockPanel2 ];

	// OTHER SETTINGS ////////////////////////////////////
	var sbsettings = new AmCharts.ChartScrollbarSettings();
	sbsettings.graph = graph;
	sbsettings.graphType = "line";
	sbsettings.usePeriod = "DD";
	chart.chartScrollbarSettings = sbsettings;

	// PERIOD SELECTOR ///////////////////////////////////
	/*var periodSelector = new AmCharts.PeriodSelector();
	periodSelector.position = "bottom";
	periodSelector.periods = [
	 {period : "MM",count : 3,label : "3 months"}, 
	 {period : "MM",count : 6,label : "6 months"}, 
	 {period : "YYYY",count : 1,label : "1 year",selected : true}, 
	 {period : "YYYY",count : 2,label : "2 years"}, 
	 {period : "YYYY",count : 3,label : "3 years"}, 
	 {period : "MAX",label : "MAX"} 
	];
	chart.periodSelector = periodSelector;*/

	chart.write(strId);
}