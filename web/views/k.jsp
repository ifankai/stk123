<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="../common/import.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@include file="../common/header.jsp" %>
<style type="text/css">
a {
  text-decoration: none;
  color: #0055a2;
}
.well {
    padding: 5px;
    margin-bottom: 8px;
}
.container{
    width: 98%;
}
.content{
    width: 100%;
}
#k-table td{
    padding: 20px 0;
}
</style>
<body>
<%@include file="../common/nav.jsp" %>
<script type="text/javascript">
$(function(){
    show('600600,000001');
    $("#xq-my").click(function () {
      xueqiu('我的');
    })
});
function xueqiu(name) {
  $.ajax({
    url:'/k/xueqiu/'+name,
    success: function (data) {
      parse(data);
    }
  });
}

function show(codes){
    $.ajax({
        url:'/k/show/'+codes,
        success: function (data) {
            parse(data);
        }
    });
}

function parse(data){
  if(data){
    $('#k-table').empty();
    $.each(data, function(){
      $('#k-table').append("<tr>" +
        "<td>" + this.nameAndCodeLink + "</td>" +
        "<td><img class='lazy' data-original='"+ this.dailyUrl +"'></td>" +
        "<td><img class='lazy' data-original='"+ this.weekUrl +"'></td>" +
        "<td><img class='lazy' data-original='"+ this.monthUrl +"'></td>" +
        "</tr>");
    });
    /*var tpl =
        "{{#.}}<tr>" +
        "<td>{{nameAndCodeLink}}</td>" +
        "<td><img class='lazy' src='' data-original='{{dailyUrl}}'></td>" +
        "<td><img class='lazy' src='' data-original='{{weekUrl}}'></td>" +
        "<td><img class='lazy' src='' data-original='{{monthUrl}}'></td>" +
        "</tr>{{/.}}";
    var html = Mustache.render(tpl, data);
    $('#k-table').html(html);*/
    $('#k-table img.lazy').lazyload();
    setTimeout(function () {
      $(window).trigger("resize");
    }, 50);
  }else{
    alert('出错！');
  }
}
</script>
<div class="container" role="main">
<div class="content">
	<div class="page-container">
        <div class="row">
            <div class="well"><a id="xq-my" class="btn">雪球自选股(我的)</a></div>
        </div>
		<div class="row">
            <table class="table table-bordered table-striped">
                <thead>
                    <tr>
                        <th>名字</th>
                        <th>日线</th>
                        <th>周线</th>
                        <th>月线</th>
                    </tr>
                </thead>
                <tbody id="k-table"></tbody>
            </table>
		</div>
    </div>
</div>
</div>
<%@include file="../common/footer.jsp" %>
</body>
</html>
