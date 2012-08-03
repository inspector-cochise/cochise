<%@page import="org.akquinet.audit.QuestionManager"%>
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
		response.sendRedirect(response.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL));
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Inspector-Cochise</title>
<link rel="stylesheet" href="style/style.css" type="text/css" />
<script language="javascript" src="script/i18n_de.js"></script>
<script language="javascript" src="script/jquery-1.7.1.js"></script>
<script language="javascript" src="script/hubu.js"></script>
<script language="javascript" src="script/disclosure.js"></script>
<script language="javascript" src="script/md5.js"></script>
<script language="javascript" src="script/script.js"></script>
<script language="javascript">
	<%
	{
		ResourceBundle navLabels = ResourceBundle.getBundle("navigation", Locale.getDefault());
		%>
		var noQuestions = '<%= navLabels.getString("noQuestions") %>';
		var stillOpenQuestions = '<%= navLabels.getString("stillOpenQuestions") %>';
		var pos = '<%= navLabels.getString("pos") %>';
		var neg = '<%= navLabels.getString("neg") %>';
		var ope = '<%= navLabels.getString("ope") %>';
		var questIds =
			[
			<%
			Iterator<String> it = QuestionManager.getDefault().getQuestionIds().iterator();
			while(it.hasNext())
			{
				String currentId = it.next();
				out.print("'" + currentId + "'");
				if(it.hasNext())
				{
					out.print(", ");
				}
			}
			%>
			 ];
		var mainCont = '';
		<%
	}
	%>

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
		
		mainCont = getMainContent();
		$('#right #content').html(mainCont);
		
		setInterval(updateMainContent, 3000);
		setInterval(updateQuestions, 1000);
		
		updateQuestions();
	});
</script>
</head>
<body>
	<%QuestionManager.getDefault().triggerActions(request.getParameterMap());%>
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
				<ul>
					<li><span class="good">--%</span> positiv beantwortet</li>
					<li><span class="open">--%</span> noch nicht beantwortet</li>
					<li><span class="bad">--%</span> negativ beantwortet</li>
				</ul>
				<form>
					<input type="button" value="<%= ResourceBundle.getBundle("site", Locale.getDefault()).getString("reportButton") %>"
						onclick="showReport();" />
					<input type="button" value="<%= ResourceBundle.getBundle("site", Locale.getDefault()).getString("saveStateButton") %>" onclick="location = '<%= CommonData.ZIPSTATE_URL %>';" />
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
			<div id="content"></div>
		</div>
	</div>
<%
	ResourceBundle navLabels = ResourceBundle.getBundle("navigation", Locale.getDefault());
%>
<div id="openTip" class="tooltip"><%= navLabels.getString("openTip") %></div>
<div id="goodTip" class="tooltip"><%= navLabels.getString("goodTip") %></div>
<div id="badTip" class="tooltip"><%= navLabels.getString("badTip") %></div>
</body>
</html>