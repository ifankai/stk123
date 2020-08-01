<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/common/error.jsp"%>
<%@include file="/common/import.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
  pageContext.setAttribute(StkConstant.PAGE_TITLE, "小智慧365_我的投资地盘"); 
%>
<%@include file="/common/header.jsp" %>
<%@include file="/common/js_datatables.jsp" %>
<%@include file="/common/js_easyui_jqueryui.jsp" %>
<%@include file="/common/js_amcharts.jsp" %>
<%@include file="/common/js_jstree.jsp" %>
<%@include file="/common/js_contextmenu.jsp" %>

<style type="text/css">
.dataTables_filter input{
width: 100px;
}
.dataTables_wrapper label{
color: #878d96;
}
</style>
<body>
<%@include file="/common/nav.jsp" %>
<div class="container" role="main">
<%
  if(_user == null){
%>
<div class="login-container">
  <span style="font-size: 14px;vertical-align: middle;">小智慧365，我的投资地盘，我做主。</span>
  <button type="button" class="btn-register" data-toggle="modal" data-target="#myModal">马上注册</button>
  <div style="float: right;">
    <form id="form1" action="/login" method="post" style="vertical-align: middle;margin: 0px">
    <input type="text" class="text" id="username" placeholder="登陆邮箱" name="username" tabindex="1" value="ifankaigmail.com">
    <input type="password" class="text" id="password" placeholder="密码" name="password" tabindex="2" value="123456"> 
    <input type="checkbox" id="autologin" name="autologin" value="1" checked tabindex="3"/>
    <span style="font-size: 14px;vertical-align: middle;">&nbsp;自动登录</span>
    <button class="btn-login" tabindex="4">登录</button>
    </form>
  </div>
<%
StkContext sc = StkContext.getContext();
if(sc != null && sc.get(StkConstant.ATTRIBUTE_LOGIN_ERROR) != null){ 
%>  
  <div class="alert alert-danger login-error" >用户名密码错误，请重新输入</div>
<%} %>  
</div>

<div class="modal" id="myModal" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display:none;">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal"><span class="sr-only">x</span></button>
        <h4 class="modal-title" id="myModalLabel" style="color:#d43f3f">注册页面</h4>
      </div>
      <div class="modal-body form-horizontal" id="regForm">
		<fieldset>
			<div class="control-group" id="nickname-group">
				<label class="control-label" for="nickname">昵称：</label>
				<div class="controls">
					<input id="nickname" class="input-large" type="text" tabindex="10" placeholder="昵称">
				</div>
			</div>
			<div class="control-group" id="email-group">
				<label class="control-label" for="email">邮箱：</label>
				<div class="controls">
					<input id="email" class="input-large" type="text" tabindex="11" placeholder="登陆邮箱">
				</div>
			</div>
			<div class="control-group" id="pw1-group">
				<label class="control-label" for="pw1">密码：</label>
				<div class="controls">
					<input id="pw1" class="input-large" type="password" tabindex="12" placeholder="登录密码，不少于6位，不超过20位">
				</div>
			</div>
			<div class="control-group" id="pw2-group">
				<label class="control-label" for="pw2">确认密码：</label>
				<div class="controls">
					<input id="pw2" class="input-large" type="password" tabindex="13" placeholder="重复输入你的登录密码">
				</div>
			</div>
		</fieldset>
      </div>
      <div class="modal-footer">
     	<button type="button" class="btn btn-primary" id="reg-btn" tabindex="14">注册</button>
       	<button type="button" class="btn btn-default" id="cancel-btn" data-dismiss="modal">取消</button>
      </div>
    </div>
  </div>
</div>

<script>
var emailReg = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
function checkEmail(){
	if($("#email").val() != "" && !emailReg.test($("#email").val())){
		$("#email-group .alert").remove();
		$("#email").after("<div class=\"alert alert-danger\" role=\"alert\">你输入的邮箱格式不正确</div>");
		return false;
	}else{
		$("#email-group .alert").remove();
		return true;
	}
}
function checkPwd(id){
	if($("#"+id).val() != "" && ($("#"+id).val().length < 6 || $("#"+id).val().length > 20)){
		$("#"+id+"-group .alert").remove();
		$("#"+id).after("<div class=\"alert alert-danger\" role=\"alert\">你输入的密码不少于6位，不超过20位</div>");
		return false;
	}else{
		$("#"+id+"-group .alert").remove();
		return true;
	}
}
$(document).ready( function() {
	$("#nickname").blur(function(){
		$("#nickname-group .alert").remove();
	});
	$("#email").blur(function(){
		checkEmail();
	});
	$("#pw1").blur(function(){
		checkPwd("pw1");
	});
	$("#pw2").blur(function(){
		checkPwd("pw2");
	});
	$("#reg-btn").bind("click",function(){
		if(reg()){
			$(this).prepend("<span class=\"loading-btn\"></span>");
			$.ajax({
			  type: 'post',
			  async:false,
			  url: '/reg?method=register',
			  dataType: 'json',
			  data: {'nickname':$('#nickname').val(),'email':$('#email').val(),'pw1':$('#pw1').val(),'pw2':$('#pw2').val()},
		      success: function (result) {
		    	$("#reg-btn .loading-btn").remove();
				if(result){
					if(result == 1){
						$("#cancel-btn").click();
						messageAndClose(20,150,2000,"注册成功,请登陆");
						$("#username").focus();
					}else if(result == -1){
						alertAndClose(20,150,2000,"未知错误");
					}else{
						$.each(result, function(i, n){
							if(n == 101){
								$("#nickname").after("<div class=\"alert alert-danger\" role=\"alert\">你输入的昵称已经被使用，请重新输入</div>");
							}
							if(n == 102){
								$("#email").after("<div class=\"alert alert-danger\" role=\"alert\">你输入的邮箱已经被使用，请重新输入</div>");
							}
						});
					}
				}
		      }
			});
		}
	});
});

function reg(){
  var canSubmit = true;
  $("#regForm .alert").remove();
  
  if ($("#nickname").val() == "") {
  	$("#nickname").after("<div class=\"alert alert-danger\" role=\"alert\">请输入昵称</div>");
      canSubmit = false;
  }
  if ($("#email").val() == "") {
  	$("#email").after("<div class=\"alert alert-danger\" role=\"alert\">请输入登陆邮箱</div>");
      canSubmit = false;
  }else{
  	canSubmit = checkEmail();
  }
  if ($("#pw1").val() == "") {
  	$("#pw1").after("<div class=\"alert alert-danger\" role=\"alert\">请输入密码</div>");
      canSubmit = false;
  }else{
  	canSubmit = checkPwd("pw1");
  }
  if ($("#pw2").val() == "") {
  	$("#pw2").after("<div class=\"alert alert-danger\" role=\"alert\">请输入确认密码</div>");
      canSubmit = false;
  }else{
  	canSubmit = checkPwd("pw2");
  }
  if($("#pw1").val() != $("#pw2").val()){
  	$("#pw2").after("<div class=\"alert alert-danger\" role=\"alert\">两次输入的密码不一致</div>");
      canSubmit = false;
  }
  return canSubmit;
}

function rememberUser(){
  if(document.cookie != ""){
	  $('#username').val(getCookie('username').replaceAll('"',''));
	  $('#password').val(getCookie('password'));    
  }
}
rememberUser();
function setLoginCookie(){
  if($('#username').val()!="" && $('#password').val()!=""){
    setCookie("username", $('#username').val());
    setCookie("password", $('#password').val());
  }
}
$(document).ready(function(){
	rememberUser();
});
</script>
<%}%>
<div class="hero-unit blow">  
  <p><span class="logo-stk">小智慧365</span> 给你提供一个全新的投资平台，让你的投资不再凌乱！发挥你的小智慧吧！让财富自由增长！</p>
  <div class="nav-secondary inverse">
    <nav>
      <ul>
        <li><a class="wuxify-me" href="#" desc="财经导航 - 提供各种知名财经网站，行业网站链接。"><span class="icon-globe"></span>财经导航</a><span class="badge badge-inverse">6</span></li>
        <li><a class="wuxify-me" href="#" desc="个股信息 - 提供A股市场个股基本信息，个股财务数据，个股关键字导航，个股历史高低PE/PB/PEG等。"><span class="icon-bar-chart"></span>个股信息</a><span class="badge badge-inverse">2529</span></li>
        <li><a class="wuxify-me" href="#" desc="行业分类 - 提供包括巨潮、万点、和讯在内的行业分类，自定义分类，可以进行多股多指标的横向比较。"><span class="icon-table"></span>行业分类</a></li>
        <li><a class="wuxify-me" href="#" desc="数据制图 - 提供包括从宏观数据到个股数据的图形定制功能，让数据一目了然。"><span class="icon-picture"></span>数据指标</a></li>
        <li><a class="wuxify-me" href="#" desc="文档编辑 - 提供针对个股信息及个人文档的编辑收藏功能，让您不再为整理文档而发愁。"><span class="icon-edit"></span>文档编辑</a></li>
        <li><a class="wuxify-me" href="#" desc="全文搜索 - 提供所有个股信息及您所收藏的文档的全文搜索功能。"><span class="icon-search"></span>全文搜索</a></li>
      </ul>
    </nav>
  </div>
  <div style="position:absolute;"><span id="nav-comments" style="font-size: 14px;"></span></div>
</div>
<script>
$(document).ready(function(){
  $('.wuxify-me').hover(
      function () {
          $("#nav-comments").text($(this).attr("desc"));
      },
      function () {
        $("#nav-comments").text('');
      }
  );
  //$('.dropdown-toggle').dropdown();
}); 
</script>


<div class="content">
  <!-- Page header -->
  <div class="page-header" style="position: relative;">
    <div style="float: left;">
    <h3><span class="icon-tasks"></span> 自选股</h3>
    </div>
  </div>
  <!-- Page container -->
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" id="rank-up">
        <table class="careTable table table-striped table-bordered" id="stk-care">
          <thead><tr>
            <% 
              List<StkDictionary> columns = StkService.getColumnNames(1001);
              for(StkDictionary column : columns){
            %>
              	<th><%=column.getText() %></th>
            <%
              } 
            %>
            </tr></thead>
          
        </table>
      </div>
    </div>
    
  </div>
  <!-- /Page container -->
</div>

<script>
/* $(document).ready(function() {
  $('.careTable').dataTable( {
    "language": datatable_lang,
    "order": [[ 7, "desc" ]],
    "ajax":"/stk?method=getCare"
  });
}); */
</script>



<div class="content">
  <div class="page-header" style="position: relative;">
    <h3><span class="icon-tasks"></span> 基金加仓</h3>
    <div style="position: absolute;left:140px;top:9px;" >
	    <ul class="nav nav-tabs" role="tablist">
	<%
		int j=0;
		List<StkDictionary> funds = StkDict.getDictionaryOrderByParam(StkDict.FAMOUS_FUNDS);
		for(StkDictionary subType : funds){
			j++;
	%>      
	      <li <%=j==1?"class='active'":"" %>><a href="#org-<%=j %>" id="org-tab-<%=j %>" data="<%=subType.getKey()%>" data-index="<%=j %>" role="tab" data-toggle="tab"><%=subType.getKey() %></a></li>
	<%
		}
	%>      
		  <li><a href="#org-0" id="org-tab-0" data="" role="tab" data-toggle="tab">查询结果</a></li>
		  <li><a href="#org-a" id="org-tab-a" data="" role="tab" data-toggle="tab"><b>十大股东都是新进的</b></a></li>
	    </ul>
    </div>
    <div class="controls" style="float:right;padding-top:40px">
		<div class="input-append">
		    包括减仓 <input type="checkbox" id="org-decrease-included" />
			从 <input class="datepicker" id="org-from" value="<%=StkUtils.getDate(-400, StkUtils.sf_ymd14) %>" type="text" style="width:70px"><span class="add-on"><i class="icon-calendar"></i></span>
			到 <input class="datepicker" id="org-to" value="<%=StkUtils.getToday(StkUtils.sf_ymd14) %>" type="text" style="width:70px"><span class="add-on"><i class="icon-calendar"></i></span>
			<input type="text" id="org-search" style="width: 80px" placeholder="基金关键字"/>
			<button class="btn" onclick="searchFundByKeyword();">查询</button>
		</div>
	</div>
  </div>
  <div class="page-container">
    <div class="tab-content">
    
      <div class="tab-pane" id="org-0" >
      	<table class="careTable table table-striped table-bordered" id="org-table-0">
          <thead><tr>
            <th>股票</th><th>日期</th><th>机构或基金名称</th><th>持有数量(股)</th><th>占流通股比例</th><th>持股变化(股)</th><th>变动比例</th>
          </tr></thead>
        </table>
	  </div>
<%
	j = 0;
	for(StkDictionary subType : funds){
		j++;
%>
      <div class="tab-pane <%=j==1?"active":"" %>" id="org-<%=j%>" >
      	<table class="careTable table table-striped table-bordered" id="org-table-<%=j %>">
          <thead><tr>
            <th>股票</th><th>日期</th><th>机构或基金名称</th><th>持有数量(股)</th><th>占流通股比例</th><th>持股变化(股)</th><th>变动比例</th>
          </tr></thead>
        </table>
	  </div>
<%
	}
%>
	  <div class="tab-pane" id="org-a" >
      	<table class="careTable table table-striped table-bordered" id="org-table-a">
          <thead><tr>
            <th>股票</th><th>日期</th><th>新进个数</th><th>新进机构或基金</th>
          </tr></thead>
        </table>
	  </div>
	  
    </div>
  </div>
</div>
<pre>
   其他牛人牛基：进化论,宁波宁聚,林园,明汯,景林
</pre>
<script type="text/javascript">
$(function() {
  listFund(1, '<%=funds.get(0).getKey()%>');
  $("a[id^='org-tab-']").on('click', function(){listFund($(this).attr('data-index'),$(this).attr('data'));});
  $("#org-tab-a").on('click', function(){listNewFund(); });
});

function searchFundByKeyword(){
	$('#org-tab-0').click();
	$('#org-table-0').DataTable().destroy();
	listFund(0, $('#org-search').val());
}
function listNewFund(){
	$('#org-a').parent().addClass('active');
	$('#org-table-a').DataTable( {
	    "language": datatable_lang,
	    "processing": true,
	    "ordering": false,
	    "retrieve": true,
	    "ajax":"/stk?method=listNewFund"
	});
}
</script>



<div class="content">
  <div class="page-header" style="position: relative;">
    <div style="float: left;">
      <h3><span class="icon-tasks"></span> 系统消息</h3>
      <div style="position: absolute;left:140px;top:9px;" >
        <ul class="nav nav-tabs" role="tablist">
         <li class="active"><a href="#msg-all" role="tab" data-toggle="tab">全部</a></li>
<%
	List<StkImportInfoType> types = News.getSystemTypes();
	for(StkImportInfoType newType : types){
%>      
         <li><a href="#msg-<%=newType.getType() %>" id="msg-tab-<%=newType.getType() %>" data="<%=newType.getType()%>" role="tab" data-toggle="tab"><%=newType.getName() %></a></li>
<%
	}
%>      
        </ul>
      </div>
    </div>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" id="msg-all">
      	<div id="msg-list-all">
			<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
		</div>
		<div class="pagination pagination-right" id="pager-msg-all"></div>
      </div>
      <%
	for(StkImportInfoType newType : types){
%>
      <div class="tab-pane" id="msg-<%=newType.getType()%>" >
      	<div id="msg-list-<%=newType.getType() %>" style="height:100%;overflow:auto;text-align:left;">
      		<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
      	</div>
		<div class="pagination pagination-right" id="pager-msg-<%=newType.getType() %>"></div>
	  </div>
<%
	}
%>
    </div>
  </div>
</div>
<script type="text/javascript">
$(function() {
  listMsg('all',1);
  $("a[id^='msg-tab-']").one('click', function(){listMsg($(this).attr('data'),1);});
});
</script>


<div class="content">
  <div class="page-header" style="position: relative;">
    <h3 style="line-height: 59px;" class="r-menu-care"><span class="icon-tasks"></span> 新闻</h3>
    <div style="position: absolute;left:100px;top:9px;" >
    <ul class="nav nav-tabs" role="tablist">
      <li class="active"><a href="#news-all" role="tab" data-toggle="tab">全部</a></li>
<%
	types = News.getTypes();
	for(StkImportInfoType newType : types){
%>      
      <li><a href="#news-<%=newType.getType() %>" id="news-tab-<%=newType.getType() %>" data="<%=newType.getType()%>" role="tab" data-toggle="tab"><%=newType.getName() %></a></li>
<%
	}
%>      
    </ul>
  </div>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" id="news-all">
      	<div id="news-list-all">
			<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
		</div>
		<div class="pagination pagination-right" id="pager-news-all"></div>
      </div>
<%
	for(StkImportInfoType newType : types){
%>
      <div class="tab-pane" id="news-<%=newType.getType()%>" >
      	<div id="news-list-<%=newType.getType() %>" style="height:100%;overflow:auto;text-align:left;">
      		<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
      	</div>
		<div class="pagination pagination-right" id="pager-news-<%=newType.getType() %>"></div>
	  </div>
<%
	}
%>
    </div>
  </div>
</div>

<%@include file="/common/stk_care_modal.jsp" %>

<script type="text/javascript">
$(function() {
  listNews('all',1);
  $("a[id^='news-tab-']").one('click', function(){listNews($(this).attr('data'),1);});
  
  rightMenu('关注','right-menu-care','care');
  $("#care-btn").bind("click",care);
});
</script>


<div class="content">
  <!-- Page header -->
  <div class="page-header" style="position: relative;">
    <div style="float: left;">
    <h3><span class="icon-tasks"></span> 非公开发行、员工持股监控</h3>
    </div>
  </div>
  <!-- Page container -->
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" >
        <table class="monitorTable table table-striped table-bordered" id="stk-care">
          <thead><tr>
            <% 
              columns = StkService.getColumnNames(1002);
              for(StkDictionary column : columns){
            %>
              	<th><%=column.getText() %></th>
            <%
              } 
            %>
            </tr></thead>
          
        </table>
      </div>
    </div>
    
  </div>
  <!-- /Page container -->
</div>

<script>
$(document).ready(function() {
  $('.monitorTable').dataTable( {
    //"sPaginationType": "bootstrap",
    "language": datatable_lang,
    "order": [[ 5, "asc" ]],
    "ajax":"/stk?method=getMonitor"
  });
});
</script>

<div class="content">
  <!-- Page header -->
  <div class="page-header" style="position: relative;">
    <div style="float: left;">
    <h3><span class="icon-tasks"></span> 盈利预测</h3>
    </div>
  </div>
  <!-- Page container -->
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" >
        <table class="efTable table table-striped table-bordered" >
          <thead><tr>
            <% 
              columns = StkService.getColumnNames(1003);
              for(StkDictionary column : columns){
            %>
              	<th><%=column.getText() %></th>
            <%
              } 
            %>
            </tr></thead>
          
        </table>
      </div>
    </div>
    
  </div>
  <!-- /Page container -->
</div>

<script>
$(document).ready(function() {
  $('.efTable').dataTable( {
    //"sPaginationType": "bootstrap",
    "language": datatable_lang,
    "order": [[ 4, "desc" ]],
    "ajax":"/stk?method=getEarningsForecast"
  });
});
</script>


<script src="/js/bootstrap-datepicker.js"></script>
<div class="content">
  <div class="page-header" >
    <div style="float: left;">
      <h3><span class="icon-tasks"></span> 投资者关系</h3> [<a target="_blank" href="http://irm.cninfo.com.cn/ircs/index">互动易</a>, <a target="_blank" href="http://irm.cninfo.com.cn/ircs/search?keyword=5G">5G</a>]
    </div>
    <div class="controls" style="float:right;">
		<div class="input-append">
		    汇总 <input type="checkbox" value=""/>
			从 <input class="datepicker" id="invest_from" value="<%=StkUtils.getDate(-30, StkUtils.sf_ymd14) %>" type="text" style="width:70px"><span class="add-on"><i class="icon-calendar"></i></span>
			到 <input class="datepicker" id="invest_to" value="<%=StkUtils.getToday(StkUtils.sf_ymd14) %>" type="text" style="width:70px"><span class="add-on"><i class="icon-calendar"></i></span>
			<button class="btn" onclick="listInvest('search',1);">搜索</button>
			<button class="btn" onclick="listInvest('index',1);">刷新</button>
			<input type="text" id="invest_search" style="width: 80px" placeholder="查询关键字"/>
			<button class="btn" onclick="listInvest('keyword',1);">查询关键字</button>
		</div>
	</div>
  </div>
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active">
      	<div id="invest-list">
			<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
		</div>
		<div class="pagination pagination-right" id="pager-invest"></div>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
$(function() {
  listInvest('index',1);
  $('.datepicker').datepicker();
});
</script>

<div class="content">
  <div class="page-header" style="position: relative;">
    <h3><span class="icon-tasks"></span> 文档</h3>
    <div style="position: absolute;left:100px;top:9px;" >
	    <ul class="nav nav-tabs" role="tablist">
	      <li class="active"><a href="#text-all" role="tab" data-toggle="tab">全部</a></li>
	<%
		for(StkDictionary subType : StkDict.getDictionaryOrderByParam(StkDict.TEXT_SUB_TYPE)){
	%>      
	      <li><a href="#text-<%=subType.getKey() %>" id="text-tab-<%=subType.getKey() %>" data="<%=subType.getKey()%>" role="tab" data-toggle="tab"><%=subType.getText() %></a></li>
	<%
		}
	%>      
	    </ul>
    </div>
    <div class="controls" style="float:right;padding-top:10px">
		<div class="input-append">
		    仅查询标题 <input type="checkbox" id="text_content_included" checked/>
			从 <input class="datepicker" id="text_from" value="<%=StkUtils.getDate(-365, StkUtils.sf_ymd14) %>" type="text" style="width:70px"><span class="add-on"><i class="icon-calendar"></i></span>
			到 <input class="datepicker" id="text_to" value="<%=StkUtils.getToday(StkUtils.sf_ymd14) %>" type="text" style="width:70px"><span class="add-on"><i class="icon-calendar"></i></span>
			<input type="text" id="text_search" style="width: 80px" value="困境反转"/>
			<button class="btn" onclick="searchTextByKeyword();">查询关键字</button>
		</div>
	</div>
  </div>
  <div class="page-container">
    <div class="tab-content">
      <div class="tab-pane active" id="text-all">
      	<div id="text-list-all">
			<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
		</div>
		<div class="pagination pagination-right" id="pager-text-all"></div>
      </div>
<%
	for(StkDictionary subType : StkDict.getDictionaryOrderByParam(StkDict.TEXT_SUB_TYPE)){
%>
      <div class="tab-pane" id="text-<%=subType.getKey()%>" >
      	<div id="text-list-<%=subType.getKey() %>" style="height:100%;overflow:auto;text-align:left;">
      		<p style="text-align:center;"><span class="loading red" data-original-title="加载中，请等待…">加载…</span></p>
      	</div>
		<div class="pagination pagination-right" id="pager-text-<%=subType.getKey() %>"></div>
	  </div>
<%
	}
%>
    </div>
  </div>
</div>
<pre>
   查询关键字：高成长,行业龙头，反转
</pre>
<script type="text/javascript">
$(function() {
  listDocument('all',1);
  $("a[id^='text-tab-']").one('click', function(){listDocument($(this).attr('data'),1);});
});

function searchTextByKeyword(){
	var subType = $(".active > a[id^='text-tab-']").attr('data');
	if(subType===undefined)subType='all';
	listDocumentByKeyword(subType, 1);
}
</script>


<div class="content">
  <!-- Page header -->
  <div class="page-header" style="position: relative;">
    <h3><span class="icon-tasks"></span> 涨跌幅榜</h3>
    <div style="position: absolute;left:150px;top:9px;" >
    <!-- Nav tabs -->
    <ul class="nav nav-tabs" role="tablist">
      <li class="active"><a href="#rank-up" role="tab" data-toggle="tab" class="r-menu-close">涨幅榜</a></li>
      <li><a href="#rank-down" role="tab" data-toggle="tab">跌幅榜</a></li>
      <%-- <div class="btn-group btn-mini pull-right" style="position: absolute;right:0px;top: 0px">
        <a href="#" data-toggle="dropdown" class="btn dropdown-toggle">选择列<span class="caret"></span></a>
        <ul class="dropdown-menu datatable-controls">
        <%
          List<StkDictionary> columns = StkService.getColumnNames();
          for(int i=0; i<columns.size(); i++){
            StkDictionary column = (StkDictionary)columns.get(i);
        %>
          <li><label class="checkbox" for="dt_col_<%=(i+1)%>"><input type="checkbox" value="<%=i%>" id="dt_col_<%=(i+1)%>" name="toggle-cols" <%=StkConstant.NUMBER_ONE.equals(column.getRemark())?"checked=\"checked\"":""%> /><%=column.getText() %></label></li>
        <%
          }
        %>
        </ul>
      </div> --%>
    </ul>
  </div>
<script type="text/javascript">
$(function() {
  rightMenu('关闭','right-menu-close','close');
  $('.r-menu-close').contextMenu('right-menu-close', {
      bindings: {
        'close': function(t) {
          alert('Trigger was '+t.id+' Action was Delete');
        }
      },      
      shadow: false
  });
  
});  
</script>
  </div>
  <!-- Page container -->
  <div class="page-container">
    <div class="tab-content" >
      <div class="tab-pane active" id="rank-up">
        <table class="datatable table table-striped table-bordered" id="stk-updown">
          <thead><tr>
            <% 
              columns = StkService.getColumnNames(StkDict.STKS_COLUMN_NAMES);
              for(StkDictionary column : columns){
            	  if(column.getRemark().equals(StkConstant.NUMBER_ONE)){
            %>
              <th><%=column.getText() %></th>
            <%
            	  }
              } 
            %>
            </tr></thead>
          <tbody>
          <%
          StkService ss = new StkService();
          List<Map> list = ss.getStksAsList(ss.getUpdownCodes("desc"));
          for(int i=0; i<list.size(); i++){
            Map map = (Map)list.get(i);
            if(i % 2 == 0){
          %>
            <tr class="odd">
              <%
              for(StkDictionary column : columns){
                out.print("<td style='text-align:"+column.getParam2()+"'>"+map.get(column.getKey())+"</td>");
              }
              %>
            </tr>
          <%
            }else{
          %>
            <tr class="even">
              <%
              for(StkDictionary column : columns){
                out.print("<td style='text-align:"+column.getParam2()+"'>"+map.get(column.getKey())+"</td>");
              }
              %>
            </tr>
          <%
            }
          }
          %>
          </tbody>
        </table>
      </div>
        <div class="tab-pane" id="rank-down">22222222222222</div>
    </div>
    
  </div>
  <!-- /Page container -->
</div>

<script>
$(document).ready(function() {
  $('.datatable').dataTable( {
    //"sDom": "<'row'<'span6'l><'span6'f>r>t<'row'<'span6'i><'span6'p>>",
    "sPaginationType": "bootstrap",
    "language": datatable_lang,
    "order": [[ 1, "desc" ]],
    //stateSave: true,
    //"scrollX": true,
    "columnDefs":[
                  <%
                  for(int i=0; i<columns.size(); i++){
            StkDictionary column = (StkDictionary)columns.get(i);
            if(StkConstant.NUMBER_ZERO.equals(column.getRemark())){
              if(i==columns.size()-1){
                out.println("{\"targets\":["+i+"], \"visible\":false}");
              }else{
                out.println("{\"targets\":["+i+"], \"visible\":false},");
              }
            }
                  }
                  %>
                 ]
    
  });
  /* $('.datatable-controls').on('click','li input',function(){
    dtShowHideCol( $(this).val() );
  }); */
  
});
</script>

<div class="content">
  <div class="page-header">
    <h3><span class="icon-globe"></span> 财经导航</h3>
  </div>
  <div class="page-container">
    <div class="row daohang">
      <article class="span5">
        <h3>股票业绩预告</h3>
        <blockquote>
          <p>
          <span><a target="_blank" href="http://vip.stock.finance.sina.com.cn/q/go.php/vFinanceAnalyze/kind/performance/index.phtml?order=xiaxian%7C2">新浪业绩预告</a></span>
          <span><a target="_blank" href="http://data.cfi.cn/cfidata.aspx">中财网数据</a></span>
          <span><a target="_blank" href="http://datainfo.hexun.com/wholemarket/html/yjyg.aspx">和讯网数据</a></span>
          <span><a target="_blank" href="http://stock.jrj.com.cn/report/yjyg.shtml">金融界数据</a></span>
          </p>
        </blockquote>
      </article>
      <article class="span5">
        <h3>报纸媒体</h3>
        <blockquote>
          <p><span><a target="_blank" href="http://www.cs.com.cn">中国证券报</a></span>
          <span>上海证券报</span>
          <span>证券时报</span>
          <span>证券日报</span></p>
        </blockquote>
      </article>
    </div>
    <div class="row daohang">
      <article class="span5">
        <h3>宏观经济数据</h3>
        <blockquote>
          <p><span class="half">M1</span><span class="half">M2</span><span>汇丰PMI数据</span>
          </p>
          <p><span><a target="_blank" href="http://app.finance.ifeng.com/data/indu/jgzs.php?symbol=58">BDI</a></span>
          </p>
        </blockquote>
      </article>
      <article class="span5">
        <h3>研报</h3>
        <blockquote>
          <span><a target="_blank" href="http://www.767stock.com/">乐睛智库</a></span>
          <span>上海证券报</span>
          <span>证券时报</span>
          <span>证券日报</span>
        </blockquote>
      </article>
    </div>
  </div>
</div>

</div>  


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
<div class="modal" role="dialog" aria-labelledby="" aria-hidden="true" style="display: none;"></div>
<div class="container" id="chart-container">
<div class="content">
  <div class="page-header" style="position: relative;">
    <div style="float: left;">
    <h3><span class="icon-picture"></span> 数据指标</h3>
    </div>
    <div style="position: absolute;left:150px;top:9px;" >
    <ul class="nav nav-tabs" role="tablist">
      <li class="active"><a href="#chart-box" role="tab" data-toggle="tab">图表区</a></li>
      <li><a href="#q" role="tab" data-toggle="tab">制图区</a></li>
    </ul>
    </div>
    <div class="icon-max"><h3><span class="icon-resize-full" title="放大"></span></h3></div>
  </div>
  
  <div id="p" class="easyui-layout" style="height:660px;">
     <div data-options="region:'west'" title="数据指标" id="chart-left" style="width:160px; padding:5px;overflow: auto;">
       <input type="text" id="index_search" style="width: 120px" placeholder="查询指标"/>
       <div id="index-tree"></div>
     </div>
        
     <div data-options="region:'center'" id="chart-center" style="border: 0px">
          <div id="loading-model" class="loading-hide over" style="z-index: 999">
            <div id="dd">Dialog Content.</div>
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
<script type="text/javascript">
//$("#tags").tagsInput();
$("#tags-left").tagsInput({
	unique: false
});
$("#tags-right").tagsInput({
	unique: false
});

var scroll;
var chart;
var chartHeight=window.screen.height-524;//1024-524=500
$(".icon-resize-full").toggle(
  function () {
    scroll = $(window).scrollTop();  
    $(window).scrollTop(0);
    $('.modal').modal('show');
    $('body').css('overflow', 'hidden');
    $(this).prop({"class":"icon-resize-small","title":"缩小"});
    $("#chart-container").addClass("chart-container-full").css("margin-top",window.document.body.scrollTop);
    $("#chart-container .content").addClass("content-full");
    var c = $('#p');
    c.height(chartHeight+313);
    c.layout('resize',{
            height:$("#chart-container .content-full").height(),
            width:$("#chart-container .content-full").width()
        });
    $("#chart-center").height(chartHeight+311);
    $("#chart-left").height(chartHeight+274);
    $("#chart-box").height(chartHeight+308);
    chartHeight=window.screen.height-524+158;
    chartResize();
  },
  function () {
    chartHeight=window.screen.height-524;
    $('.modal').modal('hide');
    $(this).prop({"class":"icon-resize-full","title":"放大"});
    $("#chart-container").removeClass("chart-container-full");
    $("#chart-container .content").removeClass("content-full");
    $("#chart-container").css("margin-top","0");
    $('body').css('overflow', 'auto');
    var c = $('#p');
    c.layout('resize',{
            height:760,
            width:$("#chart-container .content").width()
        });
    $("#chart-center").height(chartHeight+158);
    $("#chart-left").height(chartHeight+121);
    $("#chart-container").height(chartHeight+207);
    $("#p").height(chartHeight+160);
    $("#chart-box").height(chartHeight+150);
    $(window).scrollTop(scroll);
    chartResize();
  }
);
$(document).keyup(function(event){
  switch(event.keyCode) {
    case 27:
    case 96:
      $(".icon-resize-small").click();
  }
});
/* $('#data-index1').tree({
  dnd: false,
  onClick:function(node){
    //alert($("<p>").append($(this)).html());
  },
  onDragEnter: function(target, source){
    //return false;
  },
  onBeforeDrag: function(node){
    //$(source).prop("src",node.id);
  },
  onLoadSuccess: function(node,data){
    $('.tree-file').next().draggable({ //easyui.draggable
        revert:true,
        proxy: function(source){
        var p = $('<div style="border:1px solid #ccc;z-index:1999" class="tag"></div>');
        p.html($(source).clone()).appendTo('body');
        p.appendTo('body');
        alert(node.id);
        return p; 
      }
    });
  },
  data: [{
    id:1,
    text: 'Item2',
    state: 'closed',
    children: [{id:0, text: 'Item11'},{id:0,text: 'Item12'}]
  },{id:"000997",text: 'Item2'}]
  
}); */

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

/* $('#chart-center').droppable({
    onDragEnter:function(){
        $(this).addClass('over');
    },
    onDragLeave:function(){
        $(this).removeClass('over');
    },
    onDrop:function(e,source){
        $(this).removeClass('over');
        if ($(source).hasClass('assigned')){
            //$(this).append(source);
        } else {
            var c = $(source).clone().addClass('assigned');
            //$(this).append(c);
            c.draggable({
                revert:true
            }); 
        }
        //$('#loading-model').show();
        $('#dd').dialog({
            title: 'My Dialog',
            width: 400,
            height: 200,
            closed: false,
            cache: false,
            href: '',
            modal: true,
            noheader:true
        });
    }
}); */


//this function need remove
/*
function getChartData(id){
  var chartData;
  $.ajax({
      url:'/index?method=index&id='+id,
      async:false,
      success: function (data) {
        chartData = JSON.parse(data.data, function (key, value) {
              var a;  
              if (typeof value === 'string') { 
                if(key == 'd'){
                  var a = /^(\d{4})(\d{2})(\d{2})$/.exec(value);
                  return new Date(a[1],--a[2],a[3],0,0,0);
                }
              }  
              return value;  
        });
      }
  });
  $('body').data(id,chartData);
  return chartData;
}

$(function() {
  if($("#chart-box1").length > 0){
  var chartTemplate = {
    height:(chartHeight+150),
      dataSet:{fieldMappings:[{fromField: "v",toField: "v"}],categoryField:"d"},
     panel:[{graph:[{title:"",valueField:"v"}]}]
  };
    chart = drawLineChart("chart-box",{},chartTemplate);
    $(".layout-button-left").bind("click",function(){chart.invalidateSize();});
    $(".layout-button-right").live("click",function(){chart.invalidateSize();});
    $("#chart-container").bind("click",function(){chart.invalidateSize();});
    $(window).bind("resize",function(){
      var c = $('#p');
    c.layout('resize',{
            height:$("#chart-container .content-full").height(),
            width:$("#chart-container .content-full").width()
        });
      chart.invalidateSize();
    });
  }
});
*/
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
    height:(chartHeight+150),
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


<%@include file="/common/footer.jsp" %>
</body>
</html>

