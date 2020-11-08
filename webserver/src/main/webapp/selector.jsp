<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/common/import.jsp" %>
<%
	StkContext sc = StkContext.getContext();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<%@include file="/common/top.jsp" %>
<style type="text/css">
li { 
float:left; /* 往左浮动 */
}

h1 { padding: .2em; margin: 0; }
  #products { float:left; width: 500px; margin-right: 2em; }
  #cart { width: 200px; float: left; margin-top: 1em; }
  /* style the list to maximize the droppable hitarea */
  #cart ol { margin: 0; padding: 1em 0 1em 3em; }
</style>
<!-- jquery-ui js and css -->
<script type="text/javascript" src="/js/jquery-ui/jquery-ui.js"></script>
<link rel="stylesheet" type="text/css" href="/js/jquery-ui/jquery-ui.css" />
</head>
<body>
<div align="center" >
<div id="center2" style="width:98%;padding-top:33px">

<ul>
  <li id="draggable" class="ui-state-highlight">Drag me down</li>
</ul>
 
<ul id="sortable">
  <li class="ui-state-default">Item 1</li>
  <li class="ui-state-default">Item 2</li>
  <li class="ui-state-default">Item 3</li>
  <li class="ui-state-default">Item 4</li>
  <li class="ui-state-default">Item 5</li>
</ul>

<table>
  <tr>
    <td id="draggable2" class="ui-state-highlight">test</td>
  </tr>
</table>

<table>
  <tr id="sortable2">
    <td style="padding: 10px;margin: 10px" class="ui-state-default">11111111</td>
    <td style="padding: 10px;margin: 10px" class="ui-state-default">222222</td>
  </tr>
</table>


<div id="products">
  <h1 class="ui-widget-header">Products</h1>
  <div id="catalog">
    <h2><a href="#">T-Shirts</a></h2>
    <div>
      <ul>
        <li>Lolcat Shirt</li>
        <li>Cheezeburger Shirt</li>
        <li>Buckit Shirt</li>
      </ul>
    </div>
    <h2><a href="#">Bags</a></h2>
    <div>
      <ul>
        <li>Zebra Striped</li>
        <li>Black Leather</li>
        <li>Alligator Leather</li>
      </ul>
    </div>
    <h2><a href="#">Gadgets</a></h2>
    <div>
      <ul>
        <li>iPhone</li>
        <li>iPod</li>
        <li>iPad</li>
      </ul>
    </div>
  </div>
</div>
 
<div id="cart">
  <h1 class="ui-widget-header">Shopping Cart</h1>
  <table class="ui-widget-content">
    <tr>
      <td class="placeholder">Add your items here</td>
    </tr>
  </table>
</div>

</div>
</div>
</body>
</html>
   
<script>
  $(function() {
    $( "#sortable" ).sortable({
      revert: true
    });
    $( "#draggable" ).draggable({
      connectToSortable: "#sortable",
      helper: "clone",
      revert: "invalid"
    });
    $( "ul, li" ).disableSelection();
    
    
    
    $( "#sortable2" ).sortable({
        revert: true
      });
    $( "#draggable2" ).draggable({
        connectToSortable: "#sortable2",
        helper: "clone",
        revert: "invalid"
      });
    $( "table, tr, td" ).disableSelection();
  });
  
  $(function() {
	    $( "#catalog" ).accordion();
	    $( "#catalog li" ).draggable({
	      appendTo: "body",
	      helper: "clone"
	    });
	    $( "#cart tr" ).droppable({
	      activeClass: "ui-state-default",
	      hoverClass: "ui-state-hover",
	      accept: ":not(.ui-sortable-helper)",
	      drop: function( event, ui ) {
	        $( this ).find( ".placeholder" ).remove();
	        $( "<td></td>" ).text( ui.draggable.text() ).appendTo( this );
	      }
	    }).sortable({
	      items: "td:not(.placeholder)",
	      sort: function() {
	        // gets added unintentionally by droppable interacting with sortable
	        // using connectWithSortable fixes this, but doesn't allow you to customize active/hoverClass options
	        $( this ).removeClass( "ui-state-default" );
	      }
	    });
	  });
  </script>