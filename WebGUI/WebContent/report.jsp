<%@page import="org.akquinet.web.AuthenticatorServlet"%>
<%@page import="org.akquinet.audit.QuestionManager"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.akquinet.audit.YesNoQuestion"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="org.akquinet.web.CommonData"%>
<%@page import="java.util.List"%>
<%@page import="java.io.PrintWriter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	if (!AuthenticatorServlet.authenticate(request.getSession()))
	{
		response.sendRedirect(response.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL));
		return;
	}

	ResourceBundle labels = ResourceBundle.getBundle("report", Locale.getDefault());
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="style/report.css" type="text/css" />
<title>Inspector-Cochise Audit-Report</title>
</head>
<h1>Inspector-Cochise Audit-Report</h1>
<h2><%= labels.getString("overview") %></h2>
<p><%= labels.getString("p1") %></p>
<p><b><%= labels.getString("alert1") %></b></p>
<p><%= labels.getString("p2") %></p>
<ol>
	<li><%= labels.getString("overview") %></li>
	<li><%= labels.getString("evaluation") %></li>
	<li><%= labels.getString("recording") %></li>
	<li><%= labels.getString("warning") %></li>
</ol>
<%
	Calendar cal = Calendar.getInstance( Locale.getDefault() );
	String day		= (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH);
	String month	= (cal.get(Calendar.MONTH)+1 < 10 ? "0" : "") + (cal.get(Calendar.MONTH)+1);
	String year		= "" + cal.get(Calendar.YEAR);
	String hour		= "" + cal.get(Calendar.HOUR_OF_DAY);
	String minute	= (cal.get(Calendar.MINUTE) < 10 ? "0" : "") + cal.get(Calendar.MINUTE);
	String timezone	= cal.getTimeZone().getDisplayName(Locale.getDefault());
%>
<p><%= labels.getString("time") %>: <%= day %>.<%= month %>.<%= year %> <%= hour %>:<%= minute %> <%= timezone %></p>
<h2><%= labels.getString("evaluation") %></h2>

<%
	if(QuestionManager.getDefault().allGood())
	{%>
		<ul style="list-style-image: url('img/good.png');">
			<li><%= labels.getString("good") %></li>
		</ul>
	<%}
	else
	{%>
		<ul style="list-style-image: url('img/bad.png');">
			<li><%= labels.getString("bad") %> (<%= labels.getString("see") %>
			<%
				Iterator<YesNoQuestion> it = QuestionManager.getDefault().getProblems().iterator();
				while(it.hasNext())
				{
					YesNoQuestion currentQuest = it.next();
					out.print(currentQuest.getName());
					if(it.hasNext())
					{
						out.print(", ");
					}
				}
			%>).</li>
		</ul>
	<%}
%>

<h2><%= labels.getString("recording") %></h2>
<body>
<%
	List<String> questIds = QuestionManager.getDefault().getQuestionIds();
	for(String questId : questIds)
	{
		out.println( QuestionManager.getDefault().getQuestionsOutput(questId) );
		out.println("<hr /><hr />");
	}
%>
<h2><%= labels.getString("warning") %></h2>
<p><%= labels.getString("p3") %></p>
</body>
</html>