var maxResults = 20; // max # of results to display  
var ignoreKeys = ""; //设置忽略的关键字  
var sMultiply = false; //是否可以连续输入

function suggest(keywords, key, inputID, multiply,callback) {
	var results = document.getElementById("results");
	sMultiply = multiply;
	var theinput = null;
	if (sMultiply) {
		//将input的内容用split截取成数组,最多返回1000个  
		theinput = keywords.split(",", 1000);
		//当输入为多个时只需要匹配最后一  
		if (theinput.length >= 1) {
			keywords = theinput[theinput.length - 1];
		}
	} else {
		keywords = keywords.toString();
	}
	var len = keywords.length;
	if (keywords != "") {
		var terms = null;
		setTimeout(function(){
			
			if($('#'+inputID).val().length != len)return;
			terms = callback();
			
			var ul = document.createElement("ul");
			var li;
			var a;
			if ((key.keyCode == '40' || key.keyCode == '38' || key.keyCode == '13')) {
				navigate(key.keyCode, theinput, inputID);
			} else {
				var kIndex = -1;

				for ( var i = 0; i < terms.length; i++) {
					kIndex = terms[i].n.toLowerCase().indexOf(
							keywords.toLowerCase());

					if (kIndex >= 0) {
						li = document.createElement("li");
						a = document.createElement("a");
						a.href = "javascript://";

						a.setAttribute("rel", terms[i].v);
						a.setAttribute("rev", getRank(terms[i].n.toLowerCase(), keywords.toLowerCase()));

						a.onclick = function() {
							populate(this, theinput, inputID);
						}

						a.appendChild(document.createTextNode(""));

						if (keywords.length == 1) {
							var kws = terms[i].n.toLowerCase().split(" ");
							var firstWord = 0;
							for ( var j = 0; j < kws.length; j++) {
								ul.appendChild(li);
								if (j != 0) {
									kIndex = terms[i].n.toLowerCase()
											.indexOf(" " + keywords.toLowerCase());
									kIndex++;
								}
								break;
							}
						} else if (keywords.length > 1) {
							ul.appendChild(li);
						} else
							continue;

						var before = terms[i].n.substring(0, kIndex);
						var after = terms[i].n.substring(keywords.length
								+ kIndex, terms[i].n.length);

						var middle = terms[i].n.substring(kIndex,
								keywords.length + kIndex);
						a.innerHTML = before + "<strong>" + middle + "</strong>"
								+ after;
						li.appendChild(a);

					}
				}
				
				//增加 搜索 功能
				var liSearch = document.createElement("li");
				liSearch.innerHTML = "<font style='font-weight:bold;margin-left:2px;font-size:14px;color:#000'>搜索</font><a onclick='searchWords()' href='javascript://' rev='9999' style='font-size:13px;margin:2px;'><font color='gray'>关键字</font> <font style='font-size:14px;'><strong>"+keywords+"</strong></span></a>";
				ul.appendChild(liSearch);
				

				if (results.hasChildNodes())
					results.removeChild(results.firstChild);

				var s = document.getElementById(inputID);
				var xy = findPos(s);

				results.style.left = xy[0] + "px";
				results.style.top = xy[1] + s.offsetHeight + "px";
				results.style.width = s.offsetWidth + "px";

				// if there are some results, show them  
				if (ul.hasChildNodes()) {
					results.appendChild(filterResults(ul,liSearch));

					if (results.firstChild.childNodes.length == 1)
						results.firstChild.firstChild.getElementsByTagName("a")[0].className = "hover";
				}

			}
		},500);
		
	} else {
		if (results.hasChildNodes())
			results.removeChild(results.firstChild);
	}
}

function getRank(n, keywords) {
	var ret = -1;
	var kIndex = n.indexOf(keywords);
	ret = (n.charAt(kIndex - 1) == " ") ? kIndex : (200 * kIndex);
	return ret;
}

function filterResults(s, other) {
	var sorted = new Array();
	for ( var i = 0; i < s.childNodes.length; i++) {
		sorted.push(s.childNodes[i]);
	}
	var ul = document.createElement("ul");
	var lis = sorted.sort(sortIndex);
	for ( var j = 0; j < lis.length; j++) {
		if (j < maxResults)
			ul.appendChild(lis[j]);
		else if(j == maxResults){
			ul.appendChild(other);
		}else{
			break;
		}
	}
	return ul;
}

function sortIndex(a, b) {
	return (a.getElementsByTagName("a")[0].rev - b.getElementsByTagName("a")[0].rev);
}

function navigate(key, theinput, inputID) {
	var results = document.getElementById("results");
	var keyIndex = document.getElementById("keyIndex");

	var i = keyIndex.value;

	if (i == "" || !i)
		i = -1;
	else
		i = parseFloat(i);

	var ul = results.childNodes[0];

	if (ul) {
		if (key == '40') //DOWN  
		{
			i++;
			if (i > ul.childNodes.length - 1)
				i = ul.childNodes.length - 1;

			keyIndex.value = i;

			try {
				ul.childNodes[i].getElementsByTagName("a")[0].className = "hover";
				ul.childNodes[i - 1].getElementsByTagName("a")[0].className = "";
			} catch (e) {
			}
		} else if (key == '38') // UP  
		{
			i--;
			if (i <= 0)
				i = 0;

			keyIndex.value = i;

			try {
				ul.childNodes[i].getElementsByTagName("a")[0].className = "hover";
				ul.childNodes[i + 1].getElementsByTagName("a")[0].className = "";
			} catch (e) {
			}
		} else if (key == '13' || key == '9') // ENTER/TAB -- POPULATE  
		{
			if (i == -1)
				i = 0;
			populate(ul.childNodes[i].getElementsByTagName("a")[0],theinput,inputID);
		} else
			return;
	}else{
		if(key == '13'){
			searchWords();
		}
	}
}

function tabfix(keywords, key) {
	if (key.keyCode == '9') {//onkeydown
		navigate(key.keyCode);
		return false;
	} else
		return true;
}

function populate(a, theinput, inputID) {
	var ul = document.getElementById("results").childNodes[0];
	var inputold = "";
	var inputObj = document.getElementById(inputID);
	var pick;
	if (theinput != null) {
		pick = a.innerHTML.replace("<strong>", "").replace("</strong>", "") + ",";

		// IE6 converts HTML elements to uppercase -- could be done with RegExp  
		if (document.all)
			pick = a.innerHTML.replace("<STRONG>", "").replace("</STRONG>", "") + ",";

		if (theinput.length >= 1) {
			//多项输入时要用到  
            //重新将当前输入值前的值添加到input标签里
			for ( var i = 0; i < theinput.length - 1; i++) {
				inputold = inputold + theinput[i] + ",";
			}

		}
		//添加新配置的值  
		inputObj.value = inputold + pick;
	} else {
		if(a.getAttribute("rev") == 9999){
			searchWords();
		}else{
			inputObj.value = a.getAttribute("rel");
			query();
		}
	}
	clearSuggest();
}

function findPos(obj) {
	var curleft = curtop = 0;
	if (obj.offsetParent) {
		curleft = obj.offsetLeft
		curtop = obj.offsetTop
		while (obj = obj.offsetParent) {
			curleft += obj.offsetLeft
			curtop += obj.offsetTop
		}
	}
	return [ curleft, curtop ];
}

function clearSuggest() {
	setTimeout("hideSuggest()", 200);
}

function hideSuggest() {
	var results = document.getElementById("results");
	if (results.hasChildNodes())
		results.removeChild(results.firstChild);
	document.getElementById("keyIndex").value = "-1"; // reset the suggestions index  
}

// get list values;  
function getListvalues(listID) {
	var options = this.getOptions(listID);
	var values = new Array();
	for ( var i = 0; i < options.length; i++) {
		var flag = true;
		for ( var j = 0; j < values.length; j++) {
			if (values[j] == options[i].innerText) {
				flag = false;
				break;
			}
		}
		if (flag) {
			values.push(options[i].innerText);
		}
	}
	return values;
}

function getStks()  
{  
    var terms = new Array();    
    var result = '';
    $.ajax({
		url:'/stk?method=search&k='+$('#stkcode').val(),
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
$(function() {
  if($('#stkcode').length > 0){
    $('#stkcode').focus();
  }
});

function query() {
  window.location = "/stk?s="+$("#stkcode").val();
}

function searchWords(){
  window.location = "/search?q="+$("#stkcode").val();
}

function ajaxSearch(obj,page){
  var callback = ajaxSearch;
  $.ajax({
   url:'/search?method=search',
   contentType:"application/x-www-form-urlencoded; charset=utf-8", 
   dataType:'json',
   type:"post",
   data:'q='+$("#stkcode").val()+"&page="+page,
   success: function (data) {
    pagination("search-pager",page,data.perpage,data.count,callback,obj);
    $('#search-list').empty();
    var result = data.data;
    var left = result.left;
    $(left).each(function(i) {
      var json = left[i];
      var title = json.title;
      var time = json.time;
      if(title != '')title = '<strong><div id="title_'+json.id+'" style="margin-top:4px;">'+title+'</div></strong>';
      if(time != undefined){
        time = '<span style="color:#888888;margin-left:5px;">'+time+'</span>';
      }else{
        time = '';
      }
      var content = '';
      if(json.content != undefined){
    	  content = '<li id="text_'+json.id+'">'+json.content+'</li>';
      }
      var ul = '<ul>'+title+time+content+'</ul>';
      $('#search-list').append(ul);
    });
    $('#search-right').empty();
    var right = result.right.list;
    $(right).each(function(i) {
      var json = right[i];
      var ul = '<ul>';
      if(json.name == '1'){
        $(json.value).each(function(j) {
          if(j % 2 == 0){
            var li = json.value[j];
            var li2 = json.value[++j];
            if(li2 == undefined)li2='';
            ul += '<li style="padding:2px"><table><tr><td width="140px">'+li+"</td><td>"+li2+'</td></tr></table></li>';
          }
        });
      }else{
        $(json.value).each(function(j) {
          var li = json.value[j];
          ul += '<li style="padding:2px">'+li+'</li>';
        });
      }
      ul += '</ul>';
      $('#search-right').append(ul);
        });
      $("#loading").remove();
      
      bindImgExpand();
      $("body").scrollTop(0);
    }
  });
}

function bindImgExpand(){
	$(".content-detail img").bind("click", function(){
		expand(this);
	});
}

var scroll_expand;
function expand(obj) {
    if ($(obj).closest(".content-summary").css("display") == "block") {
    	$(obj).closest(".content-summary").next(".content-detail").css("display", "block");
    	$(obj).closest(".content-summary").css("display", "none");
    	scroll_expand = $("body").scrollTop();
    } 
    if ($(obj).closest(".content-detail").css("display") == "block"){
    	$(obj).closest(".content-detail").prev(".content-summary").css("display", "block");
    	$(obj).closest(".content-detail").css("display", "none");
        //$("body").scrollTop($(obj).closest("ul").offset().top - 50);
    	$("body").scrollTop(scroll_expand);
    }
}

function logout(){
	window.location = "/login?method=logout";
}

function trim(strValue) {     
  return strValue.replace(/(^\s*)|(\s*$)/g, '');
}
String.prototype.trim = function(){       
	return this.replace(/(^s*)|(s*$)/g, "");   
}   
String.prototype.replaceAll = function(reallyDo, replaceWith, ignoreCase) { 
	if (!RegExp.prototype.isPrototypeOf(reallyDo)) { 
		return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi": "g")), replaceWith); 
	} else { 
		return this.replace(reallyDo, replaceWith); 
	} 
}
function setCookie(sName, sValue) {     
  document.cookie = sName + "=" + escape(sValue); 
}   

function getCookie(sName) {     
  var aCookie = document.cookie.split(";");     
  for(var　i=0;　i　< aCookie.length;　i++) {     
    var aCrumb = aCookie[i].split("=");     
    if(sName == trim(aCrumb[0])) {     
      return unescape(aCrumb[1]);     
    }     
  }     
  return null;     
}
function setCookieScroll(id, scrollCookieName){
  var sValue = $(id).scrollTop();
  setCookie(scrollCookieName, sValue);
}
function scrollback(id, scrollCookieName){     
  if(getCookie(scrollCookieName) != null){
    $(id).scrollTop(getCookie(scrollCookieName));
  }     
}
