<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Inspector-Cochise Login</title>
<link rel="stylesheet" href="style/style.css" type="text/css" />
<link rel="stylesheet" href="style/layout.css" type="text/css" />
<link rel="stylesheet" href="style/overview.css" type="text/css" />
<script language="javascript" src="script/jquery-1.7.1.js"></script>
<!--<script language="javascript" src="script/hubu.js"></script>
<script language="javascript" src="script/disclosure.js"></script>
<script language="javascript" src="script/script.js"></script>
<script language="javascript">
	$(document).ready(function() {

		var title = $.trim($("#container").find('title').remove().text());
		if (title)
			document.title = title;
		var discl = disclosure();
		hub.registerComponent(discl, {
			disclosureId : '.disclosures .feature-title',
			component_name : 'disclosure'
		}).start();

		hub.publish(true, "/container/load", {
			containerId : 'body'
		});
	});
</script>-->
</head>
<body>
	<%
		ResourceBundle labels = ResourceBundle.getBundle("login", Locale.getDefault());
	%>
	<div id="header">
		<div id="logo"></div>
	</div>
	<div id="middle">
		<div id="wrapper">
			<div id="container">

				<div id="content-top">
					<div id="content-top-first-column">
						<img src="img/tree.png" />
					</div>
					<div id="content-top-second-column">
						<form action="checkLogin">
							<p class="slogan"><%=labels.getString("welcome")%></p>
							<table>
								<%--<tr>
										<td><%= labels.getString("user") %>:</td>
										<td><input type="text" size="15" name="user" /></td>
									</tr>--%>
								<tr>
									<td><%=labels.getString("password")%>:</td>
									<td><input type="password" size="15" name="password" /></td>
								</tr>
								<tr>
									<td></td>
									<td align="right"><input type="submit"
										value="<%=labels.getString("submit")%>" /></td>
								</tr>
							</table>
						</form>
						<%
							if (!request.isSecure())
							{
								%>
								<p class="bad"><%=labels.getString("noSSL")%></p>
								<%
							}
						%>
					</div>
				</div>

			</div>
		</div>
	</div>
</body>
</html>