
<%@page import="org.akquinet.audit.QuestionManager"%>
<%@page import="org.akquinet.audit.bsi.httpd.PrologueData"%>
<%@page import="org.akquinet.web.CommonData"%>
<%@page import="org.akquinet.web.SettingsHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%
	if (session.getAttribute("loggedIn") == null
			|| session.getAttribute("runId") == null
			|| !session.getAttribute("runId").equals(CommonData.RUN_ID)
		)
	{
		response.sendRedirect( response.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL) );
		return;
	}
%>
<%
	ResourceBundle labels = ResourceBundle.getBundle("settings", Locale.getDefault());
%>
<h1><%=labels.getString("prologue2")%></h1>
<%
	SettingsHelper helper = new SettingsHelper(QuestionManager.getDefault().getPrologueData());
	
	String execErrorMsg = helper.getExecErrorMsg();
	String configErrorMsg = helper.getConfigErrorMsg();

	if (execErrorMsg.equals("") && configErrorMsg.equals("") && "root".equals(System.getenv("USER")) && QuestionManager.getDefault().isConfigured())
	{
		session.setAttribute("prologueOk", "true");
	}
	else
	{
		session.setAttribute("prologueOk", "false");
	}

	if (!"root".equals(System.getenv("USER")))
	{
		%>
		<span class="bad"><%=labels.getString("prologue1")%></span>
		<%
	}
%>
<%=labels.getString("prologue3")%>
<form action="<%= response.encodeURL("inspector.jsp") %>">
	<input type="hidden" name="action" value="<%=CommonData.ACTION_SETTINGS%>" />
	<table>
		<tr>
			<td><%=labels.getString("prologue4")%></td>
			<td><input type="text" name="<%=CommonData.PARAM_APACHE_EXECUTABLE%>" size="40" value="<%= helper._prologueData._apacheExec%>" />
				<span class="bad"><%= execErrorMsg %></span></td>
			<td><a onmouseover="showWMTT('apacheExecTip')" onmouseout="hideWMTT()" href=""><img src="img/help.png" /></a></td>
		</tr>

		<tr>
			<td><%= labels.getString("prologue5") %></td>
			<td><input type="text" name="<%=CommonData.PARAM_APACHE_CONFIG%>" size="40" value="<%= helper._prologueData._apacheConf %>" />
				<span class="bad"><%= configErrorMsg %></span></td>
			<td><a onmouseover="showWMTT('apacheConfTip')" onmouseout="hideWMTT()" href=""><img src="img/help.png" /></a></td>
		</tr>
		<tr>
			<td><%= labels.getString("prologue6") %></td>
			<td><input type="checkbox" name="<%=CommonData.PARAM_HIGH_SECURITY%>" value="val" <%= helper._prologueData._highSec ? "checked=\"checked\"" : "" %> />
				</td>
			<td><a onmouseover="showWMTT('highSecTip')" onmouseout="hideWMTT()" href=""><img src="img/help.png" /></a></td>
		</tr>
		<tr>
			<td><%= labels.getString("prologue7") %></td>
			<td><input type="checkbox" name="<%=CommonData.PARAM_HIGH_PRIVACY%>" value="val" <%= helper._prologueData._highPriv ? "checked=\"checked\"" : "" %> />
				</td>
			<td><a onmouseover="showWMTT('highPrivTip')" onmouseout="hideWMTT()" href=""><img src="img/help.png" /></a></td>
		</tr>
		<tr>
			<td></td><td><input type="submit" value="<%= labels.getString("prologue8") %>"></td>
		</tr>
	</table>
</form>

<div id="apacheExecTip" class="tooltip"><%= labels.getString("apacheExecTip") %></div>
<div id="apacheConfTip" class="tooltip"><%= labels.getString("apacheConfTip") %></div>
<div id="highSecTip" class="tooltip"><%= labels.getString("highSecTip") %></div>
<div id="highPrivTip" class="tooltip"><%= labels.getString("highPrivTip") %></div>
