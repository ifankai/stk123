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
</style>
<body>
<%@include file="../common/nav.jsp" %>
<script type="text/javascript">
$(function(){

});

function show(codes){
    $.ajax({
        url:'/k/show/'+codes,
        success: function (data) {
            if(data){
                /*$.each(data, function(){
                    $('#k-table').append("<tr>" +
                        "<td>" + this.nameAndCodeLink + "</td>" +
                        "<td><img class='lazy' src='' data-original='"+ this.kDUrl +"'></td>" +
                        "<td><img class='lazy' src='' data-original='"+ this.kWUrl +"'></td>" +
                        "<td><img class='lazy' src='' data-original='"+ this.kMUrl +"'></td>" +
                        "</tr>");
                });*/
                var tpl =
                    "{{#.}}<tr>" +
                    "<td>{{nameAndCodeLink}}</td>" +
                    "<td><img class='lazy' src='' data-original='{{kDUrl}}'></td>" +
                    "<td><img class='lazy' src='' data-original='{{kWUrl}}'></td>" +
                    "<td><img class='lazy' src='' data-original='{{kMUrl}}'></td>" +
                    "</tr>{{/.}}";
                var html = Mustache.to_html(tpl, data);
                $('#k-table').html(html);
                $('#k-table img.lazy').lazyload();
            }else{
                alert('出错！');
            }
        }
    });
}
</script>
<div class="container" role="main">
<div class="content">
	<div class="page-container">
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
