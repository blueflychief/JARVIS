<%@page contentType="text/html; charset=UTF-8"%>

<html>
	<body align="center">
		<form action="<%=request.getContextPath()%>/JARVIS" method="post">
			<input type="radio" name="order" value="shutdown" style="height:200px;width:200px;"><font size="70px">关机</font>
			<input type="radio" name="order" value="screenshot" style="height:200px;width:200px;"><font size="70px">截图</font>
			<input type="radio" name="order" value="takepicture" style="height:200px;width:200px;"><font size="70px">拍照</font><br>
			<input type="radio" name="order" value="wakeOnLAN" style="height:200px;width:200px;"><font size="70px">开机</font>
			<input type="radio" name="order" value="pcStateCheck" style="height:200px;width:200px;"><font size="70px">检测PC状态</font>
			<input type="radio" name="order" value="rPiStateCheck" style="height:200px;width:200px;"><font size="70px">检测RPi状态</font>
			<br><button id="submit"  style="height:300px;width:500px;"><font size="70px">执行</font>
		</form>
	</body>
</html>