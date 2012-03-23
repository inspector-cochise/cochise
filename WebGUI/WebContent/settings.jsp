
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
		response.sendRedirect( response.encodeRedirectURL("login.jsp") );
		return;
	}
%>
<%
	ResourceBundle labels = ResourceBundle.getBundle("settings", Locale.getDefault());
%>
<h1><%=labels.getString("prologue2")%></h1>
<%
	SettingsHelper helper = null;
	if(session.getAttribute("prologueData") != null)
	{
		helper = new SettingsHelper((PrologueData) session.getAttribute("prologueData"));
	}
	else
	{
		helper = new SettingsHelper();
	}
	
	String execErrorMsg = helper.getExecErrorMsg();
	String configErrorMsg = helper.getConfigErrorMsg();

	if (execErrorMsg.equals("") && configErrorMsg.equals("") && "root".equals(System.getenv("USER")))
	{
		session.setAttribute("prologueOk", "true");
		CommonData.getDefault().addQuestions();
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
		</tr>

		<tr>
			<td><%= labels.getString("prologue5") %></td>
			<td><input type="text" name="<%=CommonData.PARAM_APACHE_CONFIG%>" size="40" value="<%= helper._prologueData._apacheConf %>" />
				<span class="bad"><%= configErrorMsg %></span></td>
		</tr>
		<tr>
			<td><%= labels.getString("prologue6") %></td>
			<td><input type="checkbox" name="<%=CommonData.PARAM_HIGH_SECURITY%>" value="val" <%= helper._prologueData._highSec ? "checked=\"checked\"" : "" %> />
				</td>
		</tr>
		<tr>
			<td><%= labels.getString("prologue7") %></td>
			<td><input type="checkbox" name="<%=CommonData.PARAM_HIGH_PRIVACY%>" value="val" <%= helper._prologueData._highPriv ? "checked=\"checked\"" : "" %> />
				</td>
		</tr>
		<tr>
			<td></td><td><input type="submit" value="<%= labels.getString("prologue8") %>"></td>
		</tr>
	</table>
</form>