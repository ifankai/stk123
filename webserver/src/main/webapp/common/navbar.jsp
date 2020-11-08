<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	StkContext _sc = StkContext.getContext();
	WebUtils.setCareStk(_sc);
	List<Index> careStks = (List<Index>)_sc.get("care_indexs");
%>
<div class="navbar" align="center">
<table width="98%" border="0" cellpadding="3px" cellspacing="0">
  <tr>
    <td width="85px" align="center"><span style="color: #fff"><b>百度大数据：</b></span></td>
    <td width="140px">
    <input type="text" size="10px" id="baiduIndexWord" value=""/>
    <input type="submit" onclick="baiduIndex()" value="GO" />
    </td>
    <td>
      <input size="10px" type="text" id="stkcode" onclick="suggest(this.value,event,this.id,'fromAndToRetailGroupList',false);" onkeyup="suggest(this.value,event,this.id,'fromAndToRetailGroupList',false);" onkeydown="return tabfix(this.value,event);" onblur="clearSuggest();"  style="width:180px"/>
      <p class="nodisplay">  
      	<label>kIndex</label>  
        <input class="nodisplayd" type="text" id="keyIndex" />  
      </p>  
      <p class="nodisplay">  
      	<label>rev</label>  
        <input class="nodisplayd" type="text" id="sortIndex" />  
      </p>  
      <div id="results"></div>
    </td>
    	
    <td width="56px" align="center" onmouseover="shows()">自选股</td>
    <td width="180px"><input id="keywordonly" type="text" size="12" /><input type="button" onclick="addStopKeyword()" value="加Stop字"/></td>
    <td width="200px" align="center">
    	<div style="border:0;background-color: #006f66">
        	<a href="#" onclick="javascirpt:window.location='/main'" class="easyui-linkbutton navbara" data-options="plain:true"><b>首页</b></a>
        	<a href="#" onclick="javascirpt:window.location='article'" class="easyui-linkbutton navbara" data-options="plain:true"><b>好文收藏</b></a>
        	<a href="#" onclick="javascript:window.open('http://vip.stock.finance.sina.com.cn/q/go.php/vFinanceAnalyze/kind/performance/index.phtml?order=xiaxian%7C2')" class="easyui-linkbutton navbara" data-options="plain:true"><b>业绩预告</b></a>
    	</div>
    </td>
    <td width="120px" align="center">
	    <div class="easyui-panel" style="border:0;background-color: #006f66">
	    	<a href="#" class="easyui-linkbutton navbara" data-options="plain:true">
	    		<b><%= _sc.getUser().getStkUser().getNickname() %></b>
	    	</a>
	    	<a href="#" onclick="logout()" class="easyui-linkbutton navbara" data-options="plain:true">
	    		<b>退出</b>
	    	</a>
	    </div>
    </td>
  </tr>
</table>
<div id="stkcare">
<table style="height: 100%; " cellpadding="3px">
<%  
	String newHigh600 = (String)_sc.get("no_of_newhigh600");
	String newHigh250 = (String)_sc.get("no_of_newhigh250");
%>
</table></div>
</div>
<script>
<%-- 
var menu = new dhtmlXMenuObject("menuObj");
menu.setIconsPath("/images/menu/imgs/");
// initing;
menu.addNewSibling(null, "file", "每日报告", false);
menu.addNewChild("file", 0, "industry_rank", "行业排名", false);
menu.addNewSeparator("industry_rank");
menu.addNewChild("file", 2, "new_high_600", "创600日新高<%if(newHigh600 != null)out.print("("+newHigh600+")");%>", false);
menu.addNewChild("file", 3, "new_high_250", "突破250日振幅60%以内箱体的股票<%if(newHigh250 != null)out.print("("+newHigh250+")");%>", false);
menu.addNewChild("file", 4, "250_rs", "250日RS强度业绩排行", false);
menu.addNewChild("file", 6, "print", "Print", false, "print.gif");
menu.addNewChild("file", 7, "pageSetup", "Page Setup", true, null, "page_setup_dis.gif");
menu.addNewSeparator("pageSetup");
menu.addNewChild("file", 12, "close", "Close", false, "close.gif");
menu.addNewSibling("file", "article", "好文收藏", false);
menu.addNewSibling("article", "home", "首页", false);
menu.addNewSibling("home","performance","业绩预告",false)

menu.attachEvent("onClick", menuClick);
function menuClick(id) {
	if('industry_rank' == id){
		window.location = "/dailyindustryrank.do";
	}else if('home' == id){
		window.location = "/main";
	}else if('article' == id){
		window.location = "/article";
	}else if('new_high_600' == id){
		window.location = "/dailyreportnewhigh.do?type=1";
	}else if('new_high_250' == id){
		window.location = "/dailyreportnewhigh.do?type=4";
	}else if('250_rs' == id){
		window.location = "/dailyreportnewhigh.do?type=2&k=1";
	}else if('performance' == id){
		window.open("http://vip.stock.finance.sina.com.cn/q/go.php/vFinanceAnalyze/kind/performance/index.phtml?order=xiaxian%7C2");
	}
    return true;
} 
--%>

function baiduIndex(){
	var word = $("#baiduIndexWord").val();
	var ret = '';
	$.ajax({
		url:'/ajax.do?method=urlEncode2Gb2312&word='+word,
		async:false,
		success: function (data) {ret = data;}
	});
	window.open("http://index.baidu.com/?tpl=trend&word="+ret);
}

function get_data(dataID)  
{  
    var terms = new Array();    
    var result = '';
    $.ajax({
		url:'/stk.do?method=search&k='+$('#stkcode').val(),
		async:false,
		dataType:'json',
		success: function (data) { 
			if(data){
				result = eval(data);
				$(result).each(function(i) {
				    var json = result[i];
				    terms.push(json);
				});
				
			}
		}
	});
    return terms;
}

function logout(){
	window.location = "/login?method=logout";
}
</script>
