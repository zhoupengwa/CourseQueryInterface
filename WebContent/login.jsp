<%@ page language="java" contentType="text/html; charset=gb2312"%>
<!DOCTYPE html>
<html>
<head>
<title>Simple Xhu-Edu System</title>
</head>
<body>
	<div align="center">

		<form action="Login.CourseQuery" method="post">
			<table>
				<tr>
					<th>sno</th>
					<td><input type="text" name="sno" /></td>
				</tr>
				<tr>
					<th>password</th>
					<td><input type="password" name="password" /></td>
				</tr>
				<tr>
					<th>checkcode</th>

					<td><img
						src="data:image/gif;base64,<%=request.getAttribute("checkcode")%>" /><input
						type="text" name="checkcode" /></td>
				</tr>
				<tr>
					<td><input type="hidden" name="loginCookie"
						value="<%=request.getAttribute("loginCookie")%>" /> <input
						type="hidden" name="viewState"
						value="<%=request.getAttribute("viewState")%>" /></td>
				</tr>
				<tr colspan="2">
					<td><input type="submit" value="login" /></td>
				</tr>
			</table>
		</form>
		<hr />
	</div>
</body>
</html>