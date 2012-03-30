<%@page import="java.util.Iterator"%>
<%@page import="org.akquinet.web.CommonData"%>
<%@page import="java.util.ResourceBundle"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	if (session.getAttribute("loggedIn") == null
			|| session.getAttribute("runId") == null
			|| !session.getAttribute("runId").equals(CommonData.RUN_ID)
		)
	{
		response.sendRedirect(response.encodeRedirectURL(CommonData.LOGIN_SERVLET));
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Inspector-Cochise</title>
<link rel="stylesheet" href="./style.css" type="text/css" />
<script language="javascript" src="jquery-1.7.1.js"></script>
<script language="javascript" src="hubu.js"></script>
<script language="javascript" src="disclosure.js"></script>
<script language="javascript" src="md5.js"></script>
<script language="javascript" src="script.js"></script>
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
	
	<%
	{
		ResourceBundle navLabels = ResourceBundle.getBundle("navigation", Locale.getDefault());
		%>
		var pos = '<%= navLabels.getString("pos") %>';
		var neg = '<%= navLabels.getString("neg") %>';
		var ope = '<%= navLabels.getString("ope") %>';
		var questIds =
			[
			<%
			Iterator<YesNoQuestion> it = CommonData.getDefault().getQuestions().values().iterator();
			while(it.hasNext())
			{
				YesNoQuestion current = it.next();
				out.print("'" + current.getID() + "'");
				if(it.hasNext())
				{
					out.print(", ");
				}
			}
			%>
			 ];
		<%
	}
	%>
</script>
</head>
<body>
	<%CommonData.getDefault().triggerActions(request);%>
	<div id="header">
		<div id="logo"></div>
		<div id="header-right">
			<input type="button" value="Logout" onclick="location='checkLogin?logout=true'" />
		</div>
	</div>
	<div id="middle">
		<div id="left">
			<div id="upper-left">
				<div id="content">
					<%{%><%-- preventing "duplicate local variable"-error with multiple @includes in one scope --%>
					<%@ include file="navigation.jsp"%>
					<%}%>
				</div>
			</div>
			<div id="middle-left">
				<h1>Fortschritt</h1>
				<%-- TODO calculate these percantages --%>
				<ul>
					<li><span class="good">50%</span> positiv beantwortet</li>
					<li><span class="open">25%</span> noch nicht beantwortet</li>
					<li><span class="bad">25%</span> negativ beantwortet</li>
				</ul>
				<form>
					<%-- TODO make button work --%>
					<input type="button" value="Report generieren"
						onclick="location='inspector.jsp?action=genReport'" />
				</form>
			</div>
			<div id="lower-left">
				&copy; <a href="http://www.akquinet.de" target="_blank">akquinet
					AG</a><br /> Diese Software wird veröffentlicht unter den Bedingungen
				der GPLv3.<br /> <a href="http://www.inspector-cochise.de"
					target="_blank">www.inspector-cochise.de</a>
			</div>
		</div>
		<div id="right">
			<div id="content">
				<%@include file="mainCont.jsp" %>
			</div>
		</div>
	</div>
</body>
</html>