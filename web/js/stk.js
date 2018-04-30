function prop(obj) {
  var props = "";
  for(var p in obj){
    if(typeof(obj[p])=="function"){
      props+="function "+p+"\r";
    }else{
      props+=p+"="+obj[p]+"\r";
    }
  }
  alert(props);
}
function getRequest() { 
	var url = location.search; // 获取url中"?"符后的字串
	var theRequest = new Object(); 
	if (url.indexOf("?") != -1) { 
		var str = url.substr(1); 
		strs = str.split("&"); 
		for(var i = 0; i < strs.length; i ++) { 
			theRequest[strs[i].split("=")[0]] = decodeURI(strs[i].split("=")[1]); 
		} 
	} 
	return theRequest; 
} 
//---- care ----//
function care(){  
    //$("#care-btn").attr('disabled',"true");
	$("#care-btn").prepend("<span class=\"loading-btn\"></span>");
	$.ajax({
	  type: 'post',
	  url: '/stk?method=care',
	  dataType: 'json',
	  data: {'code':$('.care #code').val(),'info':$('.care #info').val(),'url':$('.care #url').val(),'type':$('.care #type').val(),'createtime':$('.care #createtime').val(),'memo':$('.care #memo').val(),'param1':$('.care #param1').val(),'param2':$('.care #param2').val()},
      success: function (result) {
    	$("#care-btn .loading-btn").remove();
		if(result == 1){
			$("#care-cancel-btn").click();
			messageAndClose(20,150,1000,"保存成功");
		}else{
			alertAndClose(20,150,1000,"未知错误");
		}
		//$("#care-btn").attr("disabled","false");
      }
	});
	
}

//---- keyword ----//
function addStopKeyword(){
	var kw = $("#keywordonly").val();
	$.ajax({
		url:'/keyword.do?method=stopKeyword&k='+kw,
		success: function (data) { 
			if(data){
				$('#keywordonly').val('');
			}else{
				alert('加关键字出错！');
			}
		}
	});
}

function addKeyword(){
	var aurl = '/keyword.do?kwcode='+$("#kwcode").val()+'&kwtype='+$("#kwtype").val()+'&k='+$("#keyword").val();
	listKeyword(aurl);
}
function listKeyword(uri){
	$.ajax({
		url:uri,
		dataType:'json',
		success: function (data) { 
			if(data){
				$('#keyword').val('');
				$('#showkeyword').empty();
				var result = eval(data);
				var htm = '';
				$(result).each(function(i) {
				    var json = result[i];
					var ul = '<a target="_blank" href="/search?q='+json.NAME+'" class="keyword" linkid="'+json.LINKID+'" keywordid="'+json.KEYWORDID+'">'+json.NAME+'</a>';
					htm += ul;
				});
				if(htm == '')htm='N/A';
				$('#showkeyword').html(htm);
			}
		}
	});
}
function listMainBusiness(uri){
	$.ajax({
		url:uri,
		dataType:'json',
		success: function (data) { 
			if(data){
				  var result = eval(data);
				  var htm = '';
				  $(result).each(function(i) {
				    var json = result[i];
					var ul = '<a href="#" style="margin-right:8px" class="keyword" linkid="'+json.LINKID+'" keywordid="'+json.KEYWORDID+'">'+json.NAME+'</a>';
					htm += ul;
				  });
				  $('#mainbusiness').html(htm);
			}
			$('a.keyword').tooltip({
					position: 'top',
					content: function(){
						var t = $(this);
						var linkid = t.attr('linkid');
						var keywordid = t.attr('keywordid');
						return "<a href='#' onclick='deleteLink("+linkid+")'>删除链接<a/>&nbsp;<a href='#' onclick='deleteKeyword("+keywordid+")'>删除关键字<a/>";
					},
	                showEvent: 'mouseenter',
	                onShow: function(){
	                    var t = $(this);
	                    t.tooltip('tip').unbind().bind('mouseenter', function(){
	                        t.tooltip('show');
	                    }).bind('mouseleave', function(){
	                        t.tooltip('hide');
	                    }).css({
	                        backgroundColor: 'yellow',
	                        borderColor: '#666'
	                    });
	                }
					
			});
		}
	});
}
function deleteLink(id){
	alert(id);
}
function deleteKeyword(id){
	$.ajax({
		url:'/keyword.do?method=deleteKeyword&id='+id,
		dataType:'json',
		success: function (data) { 
			if(data){
				  if(data == '1'){
					  listMainBusiness('/keyword.do?method=listMainBusiness&code='+$('#scode').val());
					  alert('delete successful!');
				  }
			}
		}
	});
}

function listRelatedStk(uri){
	$.ajax({
		url:uri,
		dataType:'json',
		success: function (data) { 
			if(data){
				//var result = eval(data);
				var stk1 = data.stk1; 
				var htm = '';
				$(stk1).each(function(i) {
				    var json = stk1[i];
					var ul = '<a target="_blank" href="/stk?s='+json.code+'" style="margin-right:8px">'+json.name+'</a>';
					htm += ul;
				});
				$('#relatedstk').html(htm);
				
				var stk2 = data.stk2; 
				htm = '';
				$(stk2).each(function(i) {
				    var json = stk2[i];
					var ul = '<a target="_blank" href="/stk?s='+json.code+'" style="margin-right:8px">'+json.name+'</a>';
					htm += ul;
				});
				$('#relatedstk2').html(htm);
			}
		}
	});
}

//---- text ----//

function addText(){
	var text = editor.getData();
	if(text == ''){
		return false;
	}
	var len = strlen(editor.document.getBody().getText());
	if(len >= 20000){
		alertAndClose(20,350,3000,"文档内容不能超过2万字，当前文档长度"+len+"字。");
		return;
	}
	var datas = getTextData();
	$("#btn-save").prop("disabled", true);
	$.ajax({
		  type: 'post',
		  async:false,
		  url: '/article?method=addOrUpdate',
		  dataType: 'json',
		  data: datas,
	      success: function (data) {
			if(data){
				listStkText('text_stk',1);
				$("#btn-save").prop("disabled", false);
				$("#btn-save .loading-btn").remove();
				cancelText();
			}
	      }
	});
}

function cancelText(){
	$("#text-title").val('');
	$('#text_id').val('');
	$("#disp-order").val('0');
	if($("#text_type").val() == 2){//long text
		$(window).scrollTop(window_scroll);
		$("#editor-container").css({display:"block!important",position: "static",height:"184px","background-color": "",top:"auto",left:"auto",filter: "none","z-index":"auto"});
		$('body').css('overflow', 'auto');
		$(".editor-title").hide();
		$("#long-text-btn").show();
	}
	$("#text_type").val(1);
	if(editor)editor.destroy(true);
	editor = editorCreateFull('text',100);
	editor.setData('');
}
var window_scroll = 0;

function longText(){
  $("#text_id").val('');
  $("#text_type").val(2);
  window_scroll = $(window).scrollTop();
  $("#editor-container").css({display: "block",width:"100%", height:"100%", position: "absolute", "background-color": "#FFF", top:"0px",left:"0px",filter: "alpha(opacity = 70)", "z-index": 199});
  $(".editor-title").show();
  $("#text-title").focus();
  $('body').css('overflow', 'hidden');
  $("#long-text-btn").hide();
  editor.resize('100%',680);
}

function getTextData(){
	var id = $("#text_id").val();
	var type = $("#text_type").val();
	var ctype = $("#text_ctype").val();
	var code = $("#text_code").val();
	var order = $("#disp-order").attr("checked")=="checked"?1:0;
	var title = $("#text-title").val()==undefined?'':$("#text-title").val();
	var text = editor.getData();
	var labels = '';
	if($('#text-labels').length > 0){
	  labels = $("#text-labels").values();
	}
	var datas = {'id':id,'type':type,'ctype':ctype,'code':code,'title':title,'order':order,'text':text,'label':JSON.stringify(labels)};
	return datas
}

function strlen(str) {
	var regExp = new RegExp(" ","g");	
	str = str.replace(regExp , ""); 
	str = str.replace(/\r\n/g,""); 
	var realLength = 0, len = str.length, charCode = -1; 
	for (var i = 0; i < len; i++) { 
		charCode = str.charCodeAt(i); 
		if (charCode >= 0 && charCode <= 128) 
			realLength += 1; 
		else 
			realLength += 2; 
	} 
	return realLength; 
};

function listStkText(id,page){
	var callback = listStkText;
	var ctype = $("#text_ctype").val();
	var code = $("#text_code").val();
	var datas = 'ctype='+ctype+'&code='+code;
	$.ajax({
	  type: 'post',
	  url: '/text?method=listStkText&page='+page,
	  dataType: 'json',
	  data: datas,
      success: function (data) {
		if(data){
		  pagination("pager1",page,data.perpage,data.count,callback,id);
		  $('#text').val('');
		  $('#'+id).empty();
		  var htm = '';
		  $(data.data).each(function(i) {
		    var json = data.data[i];
		    var title = json.title;
		    var edit = '<div id="text_ed_'+json.id+'" style="float:left;display:none;margin-left:10px;"><a onclick="textEdit('+json.id+','+json.dispOrder+')" href="javascript:void(0)">编辑</a><span style="margin-left:10px"><a onclick="deleteTextConfirm('+json.id+')" href="javascript:void(0)">删除</a></span></div>';
		    if(title != '')title = '<a onclick="javascript:preview('+json.id+');" href="javascript:void(0)"><strong><div id="title_'+json.id+'">'+title+'</div></strong></a>';
			var ul = '<ul id="text_ul_'+json.id+'" style="margin:5px;" onmouseover="textShow('+json.id+')" onmouseout="textHidden('+json.id+')"><span style="color:#888888;float:left;">'+json.updateTime+
			    '</span>'+edit+'<br>'+title+'<li id="text_'+json.id+'">'+json.text+'</li></ul>';
		    $('#'+id).append(ul);
		  });
		  bindImgExpand();
		}
	  }
	});
}

function listRelatedText(id,page){
	var callback = listRelatedText;
	var code = $("#text_code").val();
	$.ajax({
	  type: 'post',
	  url: '/text?method=listText&page='+page,
	  dataType: 'json',
	  data: 'code='+code,
      success: function (data) {
		if(data){
		  pagination("pager2",page,data.perpage,data.count,callback,id);
		  $('#text').val('');
		  $('#'+id).empty();
		  var htm = '';
		  $(data.data).each(function(i) {
		    var json = data.data[i];
		    var title = json.title;
		    if(title != '')title = '<a onclick="javascript:preview('+json.id+');" href="javascript:void(0)"><strong><div id="title_'+json.id+'">'+title+'</div></strong></a>';
			var ul = '<ul style="margin:5px;" onmouseover="textShow('+json.id+')" onmouseout="textHidden('+json.id+')"><span style="color: #888888;">'+json.updateTime+
			    '</span><br>'+title+'<li id="text_'+json.id+'">'+json.text+'</li></ul>';
		    $('#'+id).append(ul);
		  });
		  bindImgExpand();
		  if(data.data == ''){
			  $('#'+id).append('<p>没有找到相关的文档。</p>');
		  }
		}
	  }
	});
}

function textShow(id){
	$('#text_ed_'+id).show();
}
function textHidden(id){
	$('#text_ed_'+id).hide();
}
function deleteTextConfirm(id){
	confirmDialog(50,200,'确定要删除该文档吗？',deleteText,id);
}
function deleteText(id){
	var datas = 'id='+id+'&ctype='+$("#text_ctype").val()+'&code='+$("#text_code").val();
	$.ajax({
	  type: 'post',
	  url: '/article?method=delete',
	  dataType: 'json',
	  data: datas,
      success:function (data) {
		if(data > 0){
			messageAndClose(20,150,1000,"删除成功");
			$("#text_ul_"+id).slideUp(300, function() {
				$("#text_ul_"+id).remove();
            });
		}
	  }
	});
	return true;
}
function textEdit(id,dispOrder){
	var title = $('#title_'+id).html();
	if(title != undefined){
		longText();
	}
	$("#text-title").val(title);
	var content = $('#text_'+id+' .content-detail-inner').html();
	editor.setData(content);
	$('#text_id').val(id);
	$("#disp-order").val(dispOrder);
}

function preview(id){
	var content = $('#text_'+id+' .content-detail-inner').html();
	editor.setData(content);
	CKEDITOR.tools.callFunction(6);//6表示 function editorCreate里preview是第6个function
	cancelText();
}

function editorCreate(id, height){
	var editor1 = CKEDITOR.replace( id, {
		enterMode: CKEDITOR.ENTER_BR,
		height:height,
		resize_enabled : false,
		baseFloatZIndex : 1000,
		toolbar: [
		    {name:'source',items:['Preview']},
		    {name:'basicstyles',groups:['basicstyles','cleanup'],items:['Bold','Italic','Underline']},
		    {name:'colors',items:['TextColor']},
		    {name:'links',items:['Link','Unlink']},
			{name:'insert',items:['Image','Table']},
			{name:'document',groups:['mode','document','doctools']},
			{name:'paragraph',groups:['list','indent','blocks','align','bidi'],items:['NumberedList','BulletedList']},
			{name:'tools',items:['Maximize']}
		],
		removePlugins:'elementspath' //disable status bar
	});
	return editor1;
}

function editorCreateFull(id, height){
	var editor1 = CKEDITOR.replace( id, {
		enterMode: CKEDITOR.ENTER_BR,
		height:height,
		resize_enabled : false,
		baseFloatZIndex : 1000,
		removeButtons: '',
	    removeDialogTabs : '',
		removePlugins:'elementspath',
		toolbar: [
            { name:'document',groups:['mode','document','doctools'],items:['Preview','-','Source']},
            { name: 'styles', items: ['Format', 'Font', 'FontSize' ] },
            { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
            { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ], items: [ 'Bold', 'Italic','Underline','-','RemoveFormat'] },
            { name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ], items:['NumberedList','BulletedList','-','Outdent','Indent','-','JustifyLeft', 'JustifyCenter', 'JustifyRight'] },
            { name: 'links', items: [ 'Link', 'Unlink'] },
            { name: 'insert', items: [ 'Image','Table', 'HorizontalRule'] },
			{ name: 'tools', items: [ 'Maximize'] }
          ]
	});
	return editor1;
}


//---- article ----//
function cancelArticle(){
	$("#text-title").val('');
	$('#text_id').val('');
	$("#disp-order").val('0');
	$("#disp-order").removeAttr("checked");
	$('#text-labels').clear();
	$('#article-insert').text('');
	$('#article-update').text('');
	editor.setData('');
	$("#article-list li").removeClass("row-selected");
}
function addArticle(){
	var title = $("#text-title").val();
	if(title == ''){
		$("#text-title").focus();
		return false;
	}
	var len = strlen(editor.document.getBody().getText());
	if(len >= 50000){
		alertAndClose(20,350,3000,"文档内容不能超过5万字，当前文档长度"+len+"字。");
		return;
	}
	var text = editor.getData();
	if(text == ''){
		return false;
	}
	$("#btn-save").prepend("<span class=\"loading-btn\"></span>");
	var datas = getTextData();
	$("#btn-save").prop("disabled", true);
	$.ajax({
	  type: 'post',
	  url: '/article?method=addOrUpdate',
	  dataType: 'json',
	  data: datas,
      success: function (data) {
		if(data){
			window.location = '/article?id='+data;
		}
		$("#btn-save").prop("disabled", false);
	  }
	});
}

function selectArticle(id){
	if(id != ''){
		$.ajax({
		  type: 'get',
		  url: '/selectarticle.do?id='+id,
		  dataType: 'json',
	      success: function (data) {
			if(data){
				var text = data.text;
				$("#disp-order").removeAttr("checked");
				if(text.dispOrder > 0){
					$("#disp-order").attr("checked","checked")
				}
				$('#text_id').val(text.id);
				$("#text-title").val(text.title);
				$('#text-labels').addTags(data.label);
				$('#article-insert').text(text.insertTime);
				$('#article-update').text(text.updateTime);
				editor.setData(text.text);
				$("#article-list li").removeClass("row-selected");
				$("#article_"+id).addClass("row-selected");
			}
		  }
		});
		window.history.pushState(null, null, '/article?id='+id);
	}
}
var labelId;
function selectLabel(id,page){
	$(".label").removeClass("label-select");
	$("#"+id+" span").addClass("label-select");
	var callback = selectLabel;
	labelId = id;
	$.ajax({
		url:'/article?method=selectLabel&labelId='+id+'&page='+page,
		dataType:'json',
		success: function (result) { 
		  if(result){
			  pagination("pager",page,result.perpage,result.count,callback,id);
			  var htm = '';
			  $(result.data).each(function(i) {
			    var json = result.data[i];
			    var zhiding = '';
			    if(json.DISP_ORDER > 0){
			    	zhiding = "<b>[置顶]</b> ";
			    }
				var li = '<li id="article_'+json.ID+'" style="margin-bottom: 5px">'+zhiding+'<a href="javascript:void(0)" title="'+json.TITLE+'" onclick="selectArticle('+json.ID+')">'+json.TITLE+'</a></li>';
				htm += li;
			  });
			  $("#article-list").html(htm);
		  }
		scrollback("article-div","article-scroll");
		}
	});
}
function deleteLabel(id){
	$.ajax({
	  type: 'get',
	  async:false,
	  url: '/article?method=deleteLabel&labelId='+id,
	  dataType: 'json',
      success: function (data) {
		if(data > 0){
			window.location = '/article';
		}else{
			return false;
		}
	  }
	});
	
}
function deleteArticle(id){
	$.ajax({
	  type: 'get',
	  async:false,
	  url: '/article?method=delete&id='+id,
	  dataType: 'json',
      success: function (data) {
		if(data > 0){
			window.location = '/article';
		}
	  }
	});
}

function setLayoutHeight(id){
    var c = $('#'+id);
    var p = c.layout('panel','center');
    var oldHeight = p.panel('panel').outerHeight();
    p.panel('resize', {height:'auto'});
    var newHeight = p.panel('panel').outerHeight();
    c.layout('resize',{
        height: (c.height() + newHeight - oldHeight)
    });
}

//---- industry tree
var industry_table;
$(function(){
  if($("#treeViewDiv").length > 0){
    $("#treeViewDiv").jstree({
    	"plugins" : [ "themes", "json_data", "ui", "search" ],
    	"json_data" : {"ajax" : {"url" : "/industrylist.do", "data" : "" }},
        "themes":{"theme" : "classic", "dots" : true, "icons" : false },
        "search":{show_only_matches: true}
    }).bind("select_node.jstree", function(e, data){
        if($.attr(data.rslt.obj[0], "id")){
        	if(industry_table)industry_table.destroy();
        	$("title").text($.text(data.rslt.obj[0]) + " - 行业")
        	industrySelect($.attr(data.rslt.obj[0], "id"));
        }
    });
    var curInd = $('#industry_select_id').val();
    if(curInd != ''){
    	setTimeout(function () { $.jstree._focused().select_node("#"+curInd); }, 1500);
    }
  }
});
function industrySearch() {
	$("#treeViewDiv").jstree("search", $("#industry_search").val());
}
$(function() {  
    $("#industry_search").keyup(function(event){  
      if(event.keyCode == 13){  
    	  industrySearch();
    	  $("#industry_search").select();
      }  
    });
});

function industrySelect(id){
	industry_table = $('#industry_dt').DataTable( {
		"paging":false,
        "info":false,
        "filter":false,
        "scrollY":"810px",
        "scrollCollapse": true,
        "language":datatable_lang,
        "columnDefs":[{"className":"ind-center","targets": [0,2]}],
        "ajax": "/industryselect.do?id="+id
    });
	
	$('#industry_gridbox tbody').on( 'click', 'tr', function () {
        if ( $(this).hasClass('selected') ) {
            $(this).removeClass('selected');
        }else{
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
        }
    });
}

function stkValueHistory(code){
	$('#stkValueHistory').DataTable( {
		"processing": true,
		"paging":false,
        "ordering": false,
        "filter":false,
        "info":false,
        "ajax": '/stk?method=stkValueHistory&s='+code
    } );
}

//---- stk monitor ----//
function monitor(uri){
	$.ajax({
		url:uri,
		dataType:'json',
		success: function (data) { 
			if(data){
				  $('#kparam3').val('');
				  $('#kmonitorshow').empty();
				  var result = eval(data);
				  var htm = '';
				  $(result).each(function(i) {
				    var json = result[i];
					var ul = json.text+'<input type="button" value="删除" onclick="kmonitordelete('+json.id+')">&nbsp;';
					htm += ul;
				  });
				  $('#kmonitorshow').html(htm);
			}
		}
	});
}

function kmonitorcreate(){
	if($('#kparam3').val() == '')return;
	var code = $('#scode').val()
	var json = "{param1:"+$('#kparam1').val()+",param2:"+$('#kparam2').val()+",param3:"+$('#kparam3').val()+"}";
	var uri = '/monitorcreate.do?type=1&code='+code+'&json='+json;
	monitor(uri);
}

function kmonitorlist(){
	var code = $('#scode').val();
	monitor('/monitorlist.do?code='+code);
}

function kmonitordelete(id){
	var code = $('#scode').val();
	monitor('/monitordelete.do?id='+id+'&code='+code);
}

$(function() { 
  if($("#kmonitorshow").length > 0){ 
	  kmonitorlist();
  }
});

function copyToClipboard(txt){
	window.clipboardData.clearData();
	window.clipboardData.setData("Text", txt)
}


//-----
function baiduSearch(name){
	var keywords = document.getElementsByName(name);
	var kws = new Array();
	for(var i=0;i<keywords.length;i++){
		var kw = new Object();
		kw["id"]=keywords[i].getAttribute("id");
		kw["keyword"]=keywords[i].getAttribute("keyword");
		kw["intitle"]=keywords[i].getAttribute("intitle");
		kws.push(kw);
	}
	$.ajax({
	  type: 'post',
	  url: '/stk?method=baiduSearch',
	  dataType: 'json',
	  data: 'data='+JSON.stringify(kws),
      success: function (data) {
		if(data){
		  var result = eval(data);
		  $(result).each(function(i) {
			  var json = result[i];
			  if(json.count == 0){
				$("#"+json.id).css({"color":"gray","font-size":"10px"});
			  }else if(json.count == 2){
				$("#"+json.id).wrapInner("<b></b>");
			  }
		  });
		}
	  }
	});
}

//------pagination-----//
function pagination(id, page, maxPage, count,callback,arg){
	var pageCount = Math.ceil(count/maxPage);
	var ul = $("<ul>");
	var a = $("<a>",{href:"javascript:void(0)"});
	var prev = $("<li title='上一页'>").append(a.html($("<span>")).text("<"));
	if(page == 1){
		prev.addClass("disabled");
	}else{
		a.click(function(){callback(arg, page-1);});
	}
	ul.append(prev);
	
	a = $("<a>",{href:"javascript:void(0)"});
	if(page == 1){
		var first = $("<li>").addClass("active").html(a.text(1));
		ul.append(first);
	}else{
		var first = $("<li>").html(a.html(1).click(function(){callback(arg,1);}));
		ul.append(first);
	}
	
	if(pageCount <= 7){
		for(var i=2;i<pageCount;i++){
			a = $("<a>",{href:"javascript:void(0)"});
			if(page == i){
				tmp = $("<li>").addClass("active").html(a.text(i));
			}else{
				tmp = $("<li>").html(a.html(i).click(function(){callback(arg,parseInt($(this).text()));}));
			}
			ul.append(tmp);
		}
	}else{
		if(page >= 5){
			a = $("<a>",{href:"javascript:void(0)"});
			var tmp = $("<li>").addClass("disabled").html(a.html("..."));
			ul.append(tmp);
			for(var i=page-2;i<=page+2 && i<pageCount;i++){
				a = $("<a>",{href:"javascript:void(0)"});
				if(page == i){
					tmp = $("<li>").addClass("active").html(a.text(i));
				}else{
					tmp = $("<li>").html(a.html(i).click(function(){callback(arg,parseInt($(this).html()));}));
				}
				ul.append(tmp);
			}
		}else{
			for(var i=2;i<=page+2 && i<pageCount;i++){
				a = $("<a>",{href:"javascript:void(0)"});
				if(page == i){
					tmp = $("<li>").addClass("active").html(a.text(i));
				}else{
					tmp = $("<li>").html(a.html(i).click(function(){callback(arg,parseInt($(this).html()));}));
				}
				ul.append(tmp);
			}
		}
		if(pageCount > page + 3){
			a = $("<a>",{href:"javascript:void(0)"});
			var tmp = $("<li>").addClass("disabled").html(a.text("..."));
			ul.append(tmp);
		}
	}
	
	if(pageCount > 1){
		a = $("<a>",{href:"javascript:void(0)"});
		if(page == pageCount){
			var last = $("<li>").addClass("active").html(a.text(pageCount));
			ul.append(last);
		}else{
			var last = $("<li>").html(a.html(pageCount).click(function(){callback(arg,pageCount);}));
			ul.append(last);
		}
	}
	
	a = $("<a>",{href:"javascript:void(0)"});
	var next = $("<li title='下一页'>").append(a.html($("<span>").text(">")));
	if(page == pageCount){
		next.addClass("disabled");
	}else{
		a.click(function(){callback(arg,page+1);});
	}
	ul.append(next);
	$("#"+id).html(ul);
}
function alertAndClose(height,width,timeout,msg){
	var content = $("<div>");
	content.addClass("stk-alert-content").html("<span class='icon-exclamation-sign'></span>&nbsp;"+msg);
	content.css({"height":height,"min-width":width});
	var div = $("<div>");
	div.addClass("stk-alert").html(content);
	div.css("top",($(document).scrollTop() + ((document.documentElement || document.body).clientHeight - height) / 2)); 
	div.css("left",($(document).scrollLeft() + ((document.documentElement || document.body).clientWidth - width) / 2)); 
	$("body").append(div);
	setTimeout(function(){div.fadeOut("slow");},timeout);
}
function messageAndClose(height,width,timeout,msg){
	var content = $("<div>");
	content.addClass("stk-alert-content").html("<span class='icon-ok'></span>&nbsp;"+msg);
	content.css({"height":height,"min-width":width});
	var div = $("<div>");
	div.addClass("stk-alert").html(content);
	div.css("top",($(document).scrollTop() + ((document.documentElement || document.body).clientHeight - height) / 2)); 
	div.css("left",($(document).scrollLeft() + ((document.documentElement || document.body).clientWidth - width) / 2)); 
	$("body").append(div);
	setTimeout(function(){div.fadeOut("slow");},timeout);
}
function confirmDialog(height,width,msg,callback,arg){
	var content = $("<div>");
	content.addClass("stk-alert-content").html("<span class='icon-question-sign'></span>&nbsp;"+msg);
	content.css({"height":height,"min-width":width});
	
	var p = $("<p style=\"margin:5px\">");

	var btn = $("<button>");
	btn.addClass("btn");
	btn.text("确 定");
	btn.bind("click", function(){
		btn.prepend("<span class=\"loading-btn\"></span>");
		btn.prop("disabled", true);
		if(callback(arg)){
			confirmClose();
		}
	});
	
	var btn2 = $("<button>");
	btn2.addClass("btn");
	btn2.text("取 消");
	btn2.bind("click", function(){confirmClose();});
	p.append(btn).append(" ").append(btn2);
	content.append(p);
	
	var div = $("<div>");
	div.addClass("stk-alert").html(content);
	div.css("top",($(document).scrollTop() + ((document.documentElement || document.body).clientHeight - height) / 2)); 
	div.css("left",($(document).scrollLeft() + ((document.documentElement || document.body).clientWidth - width) / 2)); 
	$("body").append(div);
	btn.focus();
	//setTimeout(function(){div.fadeOut("slow");},timeout);
}
function confirmClose(){
	$(".stk-alert").hide();
}
//阻止子元素继承父元素事件
function stopBubble(e){ 
	if (e && e.stopPropagation) {
	　　e.stopPropagation(); 
	}else {
		window.event.cancelBubble = true; 
	} 
}

function takeCare(){
  $('.r-menu-care').contextMenu('right-menu-care', {
      bindings: {
        'care': function(t) {
        	$('#stk-care-modal').on('show.bs.modal', function (event) {
        		  var code = $(t).attr("code");
        		  var name = $(t).attr("name");
        		  var info = $(t).text();
        		  var url = $(t).attr("href");
        		  var type = $(t).attr("type");
        		  var createtime = $(t).attr("createtime");
        		  var modal = $(this);
        		  modal.find('.care #name').val(name);
        		  modal.find('.care #code').val(code);
        		  modal.find('.care #info').val(info);
        		  modal.find('.care #url').val(url);
        		  modal.find('.care #type').val(type);
        		  modal.find('.care #createtime').val(createtime);
        	});
        	$('#stk-care-modal').modal();
        }
      },      
      shadow: false
  });
}

function listNews(type, page){
	var callback = listNews;
	var code = $("#scode").val();
	var datas = 'type='+type+'&code='+code;
	$.ajax({
	  type: 'post',
	  url: '/news.do?method=listNews&page='+page,
	  dataType: 'json',
	  data: datas,
      success: function (data) {
		if(data){
		  pagination("pager-news-"+type,page,data.perpage,data.count,callback,type);
		  $('#news-list-'+type).empty();
		  var htm = '';
		  $(data.data).each(function(i) {
		    var json = data.data[i];
			var ul = '<ul id="text_ul_'+json.id+'" style="margin:5px;"><li id="news_'+json.id+'"><div style="width:140px;float:left;">['+json.info+']</div> '+json.title+'<span style="color:#888888;float:right;">'+json.infoCreateTime+'</span></li></ul>';
		    $('#news-list-'+type).append(ul);
		  });
		  if(data.data == ''){
			  $('#news-list-'+type).append('<p>没有找到相关的新闻。</p>');
		  }
		  takeCare();
		}
	  }
	});
}

function listMsg(type, page){
	var callback = listMsg;
	var code = $("#scode").val();
	var datas = 'type='+type+'&code='+code;
	$.ajax({
	  type: 'post',
	  url: '/news.do?method=listMsg&page='+page,
	  dataType: 'json',
	  data: datas,
      success: function (data) {
		if(data){
		  pagination("pager-msg-"+type,page,data.perpage,data.count,callback,type);
		  $('#msg-list-'+type).empty();
		  var htm = '';
		  $(data.data).each(function(i) {
		    var json = data.data[i];
			var ul = '<ul id="text_ul_'+json.id+'" style="margin:5px;"><li id="msg_'+json.id+'"><div style="width:190px;float:left;">'+json.title+'</div> '+json.info+'<span style="color:#888888;float:right;">'+json.insertTime+'</span></li></ul>';
		    $('#msg-list-'+type).append(ul);
		  });
		  if(data.data == ''){
			  $('#msg-list-'+type).append('<p>没有找到相关的信息。</p>');
		  }
		}
	  }
	});
}

function listDocument(type, page){
	var callback = listDocument;
	var datas = 'subtype='+type;
	$.ajax({
	  type: 'post',
	  url: '/text.do?method=listDocument&page='+page,
	  dataType: 'json',
	  data: datas,
      success: function (data) {
		if(data){
		  pagination("pager-text-"+type,page,data.perpage,data.count,callback,type);
		  $('#text-list-'+type).empty();
		  var htm = '';
		  $(data.data).each(function(i) {
		    var json = data.data[i];
		    var title = '';
		    if(json.title != ''){
		    	title = '<strong>'+json.title+' </strong>';
		    }
			var ul = '<ul id="text_ul_'+json.id+'" style="margin:5px;"><li id="text_'+json.id+'"><div style="width:180px;float:left;">'+json.code+'</div><div style="float:left">'+title+json.text+'</div><div style="color:#888888;float:right;">'+json.insertTime+'</div><div style="font-size:0px; line-height:0px; clear:both;"></li></ul>';
		    $('#text-list-'+type).append(ul);
		  });
		  if(data.data == ''){
			  $('#text-list-'+type).append('<p>没有找到相关的文档。</p>');
		  }
		}
	  }
	});
}

function listDocumentByKeyword(type, page){
	var callback = listDocumentByKeyword;
	var datas = 'subtype='+type+'&keyword='+$("#text_search").val()+'&from='+$("#text_from").val()+'&to='+$("#text_to").val();
	$.ajax({
	  type: 'post',
	  url: '/text.do?method=listDocumentByKeyword&page='+page,
	  dataType: 'json',
	  data: datas,
      success: function (data) {
		if(data){
		  pagination("pager-text-"+type,page,data.perpage,data.count,callback,type);
		  $('#text-list-'+type).empty();
		  var htm = '';
		  $(data.data).each(function(i) {
		    var json = data.data[i];
		    var title = '';
		    if(json.title != ''){
		    	title = '<strong>'+json.title+' </strong>';
		    }
			var ul = '<ul id="text_ul_'+json.id+'" style="margin:5px;"><li id="text_'+json.id+'"><div style="width:180px;float:left;">'+json.code+'</div><div style="float:left">'+title+json.text+'</div><div style="color:#888888;float:right;">'+json.insertTime+'</div><div style="font-size:0px; line-height:0px; clear:both;"></li></ul>';
		    $('#text-list-'+type).append(ul);
		  });
		  if(data.data == ''){
			  $('#text-list-'+type).append('<p>没有找到相关的文档。</p>');
		  }
		}
	  }
	});
}

function listInvest(type,page){
	var callback = listInvest;
	var code = $("#scode").val();
	var datas = 'code='+code;
	if('search'==type){
		datas = datas+'&from='+$('#invest_from').val()+'&to='+$('#invest_to').val();
	}else if('keyword'==type){
		datas = datas+'&keyword='+$('#invest_search').val();
	}
	$.ajax({
	  type: 'post',
	  url: '/invest.do?method=listInvest&page='+page,
	  dataType: 'json',
	  data: datas,
      success: function (data) {
		if(data){
		  pagination("pager-invest",page,data.perpage,data.count,callback,type);
		  $('#invest-list').empty();
		  var htm = '';
		  $(data.data).each(function(i) {
			  var json = data.data[i];
			  var ul;
			  var title = '<a class="invest-list" target="_blank" href="'+json.sourceUrl+'">'+json.title+'</a>';
			  if('stk'==type){
				  ul = '<ul id="text_ul_'+json.id+'" style="margin:5px;"><strong>'+title+'</strong><div style="float:right;">[<strong>'+json.investigatorCount+'</strong>] <font style="color:#888888">'+json.investDate+'</font></div><li id="text_'+json.id+'">'+json.text+'</li></ul>';
			  }else{
				  ul = '<ul id="text_ul_'+json.id+'" style="margin:5px;"><li id="invest_'+json.id+'"><div style="width:180px;float:left;">'+json.code+'</div> '+title+'<div style="float:right;">[<strong>'+json.investigatorCount+'</strong>] <font style="color:#888888">'+json.investDate+'</font></div></li></ul>';
			  }
		      $('#invest-list').append(ul);
		  });
		  if(data.data == ''){
			  $('#invest-list').append('<p>没有找到相关的信息。</p>');
		  }
		}
	  }
	});
}

function listNotice(type,page){
	var callback = listNotice;
	var code = $("#scode").val();
	var datas = 'code='+code+'&type='+type;
	$.ajax({
	  type: 'post',
	  url: '/stk.do?method=listNotice&page='+page,
	  dataType: 'json',
	  data: datas,
      success: function (data) {
		if(data){
		  pagination("pager-notice",page,data.perpage,data.count,callback,type);
		  $('#notice-list').empty();
		  var htm = '';
		  $(data.data).each(function(i) {
			  var json = data.data[i];
			  var ul;
			  ul = '<ul style="margin:5px;">'+json.description+'<div style="float:right;">[<strong><a class="invest-list" target="_blank" href="'+json.url+'">'+json.count+'</a></strong>] <font style="color:#888888">'+json.createtime+'</font></div></ul>';
		      $('#notice-list').append(ul);
		  });
		  if(data.data == ''){
			  $('#notice-list').append('<p>没有找到相关的信息。</p>');
		  }
		}
	  }
	});
}

function loadJsonToForm(form, jsonValue) {
    var obj = form;
    if (typeof jsonValue === 'string') {
    	jsonValue = $.parseJSON(jsonValue);
    } 

    $.each(jsonValue, function(name, ival) {
        var oinput = $(":input[name='" + name + "']");
        //console.log(name);
        if(($('#'+name).prop('class')+"").indexOf("easyui-combobox") >= 0 ){
        	$('#'+name).combobox('setValues',ival);      		
    	}else if (oinput.prop("type") == "radio"
                || oinput.prop("type") == "checkbox") {
            oinput.each(function() {
                if (Object.prototype.toString.apply(ival) == '[object Array]') {//是复选框，并且是数组         
                    for (var i = 0; i < ival.length; i++) {
                        if ($(this).val() == ival[i])
                            $(this).attr("checked", "checked");
                    }
                } else {
                    if ($(this).val() == ival)
                        $(this).attr("checked", "checked");
                }
            });
        } else if (oinput.prop("tagName") == "textarea") {//多行文本框            
            obj.find("[name=" + name + "]").html(ival);
        } else {
            obj.find("[name=" + name + "]").val(ival);
        }
    });
};

String.prototype.startWith = function(str){  
  if(str == null || str== "" || this.length== 0 || str.length > this.length){  
     return false;  
  }   
  if(this.substr(0,str.length) == str){  
     return true;  
  }else{  
     return false;  
   }         
  return true;   
 };

var codeKline = '';
var isMouseOver = false;
var y_scroll = '';
function hide_div(){var adv=document.getElementById('adv_1');adv.style.display='none';}
function show_div(obj, _windCode){
  if(document.getElementById('adv_1') == null)creatediv();
  var advs= document.getElementById('adv_1');
  //var HQUrl='http://image.sinajs.cn/newchart/daily/n/'+_windCode+'.gif';
  //var HQImageHtml = '<img src=\"'+HQUrl+'\" onmouseout="mounseout()" onmouseout="mover()";/>';
  var scode = _windCode;
  if(_windCode.length==5 && !isNaN(_windCode)){
	  scode = 'hk_HK'+(scode.substring(1))
  }else{
	  scode = 'hs_'+_windCode
	  //scode = (_windCode.startWith('6')?'sh':'sz')+_windCode
  }
  var HQImageHtml = '<iframe src="/common/hq.html#'+scode+'" width="100%" height="506" marginheight="0" marginwidth="0" frameborder="0" scrolling="no" onmouseout="mounseout()" onmouseout="mover()"></iframe>';
  var fontHeight = 15;var lineHeight = 15;
  var contentDiv = obj;
  var xPos = obj.offsetLeft+obj.offsetWidth-5;
  var yPos = obj.offsetTop + obj.offsetHeight - 150;
  var offsetWidth = obj.offsetWidth;
  var offsetHeight = obj.offsetHeight;
  while(obj = obj.offsetParent){
    xPos += obj.offsetLeft;
    yPos += obj.offsetTop;
  }
  var contentDivWidth = 700/2; 
  if(contentDiv.offsetWidth > contentDivWidth){
    while(contentDiv.tagName!='DIV'){
      contentDiv = contentDiv.parentNode;
    }
    if(yPos - mouseEventY <= lineHeight){
      xPos = contentDiv.offsetLeft;
      while(contentDiv = contentDiv.offsetParent){
        xPos += contentDiv.offsetLeft;
      }
      xPos += 10; 
    }
    if(yPos >= mouseEventY + lineHeight){
      yPos -= lineHeight;
    }
  }
  var browserVersion = getBrowserVersion().split(' ');
  var clientBounds = getClientBounds(browserVersion[0], browserVersion[1]);
  var clientHeight = clientBounds.height;
  var clientWidth = clientBounds.width;
  if(yPos + 350 > clientHeight && yPos - 330 - offsetHeight > 0)
    yPos -= 330 + offsetHeight;
  if(xPos + 380 > clientWidth && xPos -580 >0){
    if(xPos + offsetWidth > clientWidth){
      xPos -= 580;
    }else if(580 - offsetWidth>0){
      xPos -= 580 - offsetWidth;
    }else{
      xPos -= 580;
    }
  }
  if(browserVersion[0] == 'firefox' || (browserVersion[0] == 'msie' && parseInt(browserVersion[1]) >= 8)){
  if(document.documentElement.clientWidth < document.documentElement.offsetWidth-4)
    xPos -= 10; 
  }
  advs.style.left = xPos + 'px';
  advs.style.top = yPos + 'px';
  advs.style.display='block';
  if(advs.style.display == 'none'){
    document.getElementById('showinfo').innerHTML = '<div>' + HQImageHtml + '</div>';
  }else{
    if(codeKline!=_windCode){
      document.getElementById('showinfo').innerHTML = '<div>' + HQImageHtml + '</div>';
    }
  }
  codeKline = _windCode;
  setTimeout(function(){window.scroll(0,y_scroll);}, 0);
}
function show_kline(obj, _windCode){
  y_scroll = document.body.scrollTop;
  isMouseOver = true;
  show_div(obj, _windCode);
}
function creatediv(){
  if(document.getElementById('adv_1') != null) return;
  var adv= document.createElement('DIV');
  adv.onmouseover=function(){isMouseOver = true;};
  //adv.onmouseout=function(){isMouseOver = false;hide_div();};
  //adv.onclick =function(){hide_div();};
  adv.id = 'adv_1';
  adv.className='div_kline';
  adv.style.display='none';
  var html = '';
  //html += '<div class=\"div_header\">&nbsp;</div>';
  html += '<div id=\"showinfo\" style=\"font-size:12px;text-align:center;\"/>';
  adv.innerHTML = html;
  document.body.appendChild(adv);
}
function mover(){
	isMouseOver = true;
}
function mouseover(obj, _windCode){
  y_scroll = document.body.scrollTop;
  isMouseOver = true;
  setTimeout(function(){
	  show_div(obj, _windCode);
  }, 300);
}
function mounseout(){
  setTimeout(function () {hide_div();},200);
}
function getBrowserVersion() {
  var  browser = {};
  var  userAgent = navigator.userAgent.toLowerCase();
  var  s;
  (s = userAgent.match(/msie ([\\d.]+)/)) ? browser.ie = s[1] : (s = userAgent.match(/firefox\/([\\d.]+)/)) ? browser.firefox = s[1] : (s = userAgent.match(/chrome\/([\\d.]+)/)) ? browser.chrome = s[1] : (s = userAgent.match(/opera.([\\d.]+)/)) ? browser.opera = s[1] : (s = userAgent.match(/version\/([\\d.]+).*safari/))? browser.safari = s[1] : 0;
  var version = '';
  if(browser.ie){
    version = 'msie ' + browser.ie;
  }else if(browser.firefox){
    version = 'firefox ' + browser.firefox;
  }else if  (browser.chrome){
    version = 'chrome ' + browser.chrome;
  }else if  (browser.opera){
    version = 'opera ' + browser.opera;
  }else if  (browser.safari){
    version = 'safari ' + browser.safari;
  }else{
    version = '未知浏览器';
  }
  return  version;
}
function getClientBounds(browser){
  var clientWidth;
  var clientHeight;
  if (browser=='msie'){
    clientWidth = Math.min(document.body.clientWidth, document.documentElement.clientWidth);
    clientHeight = Math.min(document.body.clientHeight, document.documentElement.clientHeight);
  }else if (browser=='safari'){
    clientWidth = window.innerWidth;
    clientHeight = window.innerHeight;
  }else if (browser=='opera'){
    clientWidth = Math.min(window.innerWidth, document.body.clientWidth);
    clientHeight = Math.min(window.innerHeight, document.body.clientHeight);
  }else{
    clientWidth = Math.min(window.innerWidth, document.documentElement.clientWidth);
    clientHeight = Math.min(window.innerHeight, document.documentElement.clientHeight);
  }
  return { width : clientWidth, height : clientHeight };
}
function show(tab){var e=tab.nextSibling;while(e!=null){if(e.style.display==''){e.style.display='none';}else{e.style.display='';}e = e.nextSibling;}}

function showKline(tab, col){
	$(tab).DataTable().column(col).nodes().to$().hover(
		function () {
			mouseover(this,$(this).children('a[data-code]').attr('data-code'));
		},
		function () {
			setTimeout(function () {if(!isMouseOver){hide_div();}},300);
			isMouseOver=false;
		}
	);
}
