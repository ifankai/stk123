<%@ page import="com.stk123.StkConstant" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	StkContext sc = StkContext.getContext();
	List<StkLabel> labels = (List<StkLabel>)sc.get("labels");
	String id = RequestUtils.getString(request,"id");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
  pageContext.setAttribute(StkConstant.PAGE_TITLE, "文档编辑");
%>
<%@include file="/common/header.jsp" %>
<link rel="stylesheet" type="text/css" href="/css/larger.css" />
<body>
<%@include file="/common/nav.jsp" %>
<script>$(".icon-edit").parent().parent().addClass("active");</script>
<div class="container" role="main">
  <div class="content">
	<div id="cc" class="easyui-layout" style="height:840px;">
	  <div data-options="region:'west'" style="width:260px;">
	    <div class="easyui-layout article-fav" data-options="fit:true">
	      <div data-options="region:'north'" class="label-box" style="height:120px;padding:4px 2px;border:0px;border-bottom:1px solid #D4D4D4">
	          <a href="javascript:void(0)" onclick="selectLabel(0,1)"><strong>所有文档</strong></a>
	<%
	    for(StkLabel label : labels){
	%>
		<a href="javascript:void(0)" onclick="selectLabel(<%=label.getId()%>,1)" id="<%=label.getId()%>" name="<%=label.getName() %>" class="r-menu-close">
			<span class="label label-info"><%=label.getName() %></span>
		</a>
	<%
	    }
	%>
		  </div>
	      <div id="article-div" onscroll="setCookieScroll('#article-div', 'article-scroll')" data-options="region:'center'" style="height:100%;padding:5px;border:0px">
	          <ul id="article-list" ></ul>
	      </div>
	      <div data-options="region:'south'" style="overflow:visible;border:0px">
	        <div class="pagination pagination-right" id="pager" style="height:30px;padding:2px"></div>
	      </div>
	    </div>
	  </div>
	  <div data-options="region:'center'" class="article-box">
	    <div>
		    <input id="text-title" tabindex="1" type="text" placeholder="点击这里输入标题">
		    置顶：<input type="checkbox" value="0" id="disp-order"/>
		    <div style="margin-left:20px"><table><tr>
		    <td width="40px">标签：</td><td class="label-td"><div id="text-labels"></div></td>
		    <td width="60px">文档创建：</td><td width="120px" id="article-insert"></td><td width="10px"></td>
		    <td width="60px">最后更新：</td><td width="120px" id="article-update"></td>
		    </tr></table></div>
		    <div id="text"></div>
		    <button id="readOnlyOn" onclick="toggleReadOnly();" class="btn" style="display:none">只 读</button>
			<button id="readOnlyOff" onclick="toggleReadOnly( false );" class="btn" style="display:none">编 辑</button>
		    <button id="btn-save" class="btn">保 存</button>
		    <button class="btn" onclick="cancelArticle()">取 消</button>
		    <button id="btn-delete" class="btn">删 除</button>
		    <button class="btn" onclick="cancelArticle()">新建文档</button>
		    <input type="hidden" value="" id="text_id"/>
		    <input type="hidden" value="0" id="text_type"/>
			<input type="hidden" value="" id="text_ctype"/>
			<input type="hidden" value="" id="text_code" />
		</div>
	  </div>
	 </div>
  </div>
</div>
</body>
</html>
<%@include file="/common/js_easyui_jqueryui.jsp" %>
<%@include file="/common/js_ckeditor.jsp" %>
<%@include file="/common/js_contextmenu.jsp" %>
<script type="text/javascript">
var availableTags = [
<%
for(int i=0;i<labels.size();i++){
	StkLabel label = (StkLabel)labels.get(i);
	if(i==labels.size()-1){
		out.print("'"+label.getName()+"'");
	}else{
		out.print("'"+label.getName()+"',");
	}
}
%>                     
];
$("#text-labels").tagsInput({
	'autocomplete_url':availableTags,
	defaultText:"添加标签",
	sortable:false
});


var editor;

$( document ).ready( function() {
	editor = editorCreateFull('text',708);
	setTimeout(function(){editor.setReadOnly(true)},500);
	selectLabel(0,1);
	
	editor.on( 'readOnly', function() {
		document.getElementById( 'readOnlyOn' ).style.display = this.readOnly ? 'none' : '';
		document.getElementById( 'readOnlyOff' ).style.display = this.readOnly ? '' : 'none';
	});
} );

function toggleReadOnly( isReadOnly ) {
	//CKEDITOR.tools.callFunction(3);
	editor.setReadOnly( isReadOnly );
}

$(function() {
	$("#btn-delete").bind("click",function(){
		var id = $("#text_id").val();
		if(id == '')return;
		confirmDialog(50,200,'确定要删除该文档吗？',deleteArticle,id);
	});
	$("#btn-save").bind("click",function(){
		addArticle();
	});
	
	rightMenu('删除','right-menu-delete','delete');
	$('.r-menu-close').contextMenu('right-menu-delete', {
      bindings: {
        'delete': function(t) {
        	confirmDialog(50,260,'确定要删除标签 <b>'+t.name+'</b> 吗？',deleteLabel,t.id);
        }
      },      
      shadow: false
    });
	
	if("<%=id%>".length > 0){
		selectArticle(<%=id%>);
	}
});
</script>