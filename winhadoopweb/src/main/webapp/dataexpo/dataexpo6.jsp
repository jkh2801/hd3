<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>항공사별 도착/출발 지연 분석 (정시/지연/조기) : 막대 그래프</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script type="text/javascript"
	src="https://chartjs.org/dist/2.9.3/Chart.min.js"></script>
</head>
<body>
	<h3>항공사별 출발/도착 지연 분석 (정시/지연/조기)</h3>
	<form action="${pageContext.request.contextPath}/CarrMultiServlet2" method="post" name="f">
		<select name="year">
			<c:forEach var="y" begin="1987" end="1988">
				<option <c:if test="${param.year == y}">selected</c:if>>${y}</option>
			</c:forEach>
		</select> 
		<input type="radio" name="kbn" value="d" checked>출발 &nbsp;&nbsp;
		<input type="radio" name="kbn" value="a" <c:if test='${param.kbn == d}'>checked</c:if>>도착 &nbsp;&nbsp;
		<input type="submit" value="데이터 분석">
	</form>
	<c:if test="${!empty file }">
		<div id="canvas-holder" style="width: 70%; height: 300px;">
			<canvas id="chart" width="100%" height="100%"></canvas>
		</div>
		<script type="text/javascript">
			var randomColorFactor = function () {
				return Math.round(Math.random() * 255);
			}
			var randomColor = function (opacity) {
				return "rgba("+randomColorFactor() + "," +  randomColorFactor() + "," + randomColorFactor() + "," + (opacity || "0.3") + ")";
			}
			
			arrcolor = new Array("rgb(255,0,0)","rgb(0,255,0)","rgb(0,0,255)", "rgb(129,120,211)");
			var kbn = "${param.kbn == 'a'? '출발' : '도착'}";
			var label = new Array("조기" + kbn, "정시"+kbn, "지연"+kbn ,"운항거리(1000마일)");
			var config = {
					type : "bar",
					data : {
						datasets : [
							<c:forEach items="${list}" var="map" varStatus="stat">
							{
							label : "${file}년" + label[
								${stat.index}
							],
							data : [
								<c:forEach items="${map}" var="m">"${m.value}",</c:forEach>
							],
							backgroundColor : [
								<c:forEach items="${map}" var="m">arrcolor[${stat.index}],</c:forEach>
							]
						},</c:forEach>],
						labels : [<c:forEach items="${list[0]}" var="m">"${m.key}",</c:forEach>]
					},
					options : {responsive : true}
			};
			window.onload = function () {
				var ctx = document.getElementById("chart").getContext("2d");
				new Chart(ctx, config);
				
			}
		</script>
	</c:if>
</body>
</html>