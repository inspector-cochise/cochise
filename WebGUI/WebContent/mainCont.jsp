<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="org.akquinet.web.CommonData"%>
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
	CommonData.getDefault().updateMainContentId(request);

	boolean prologueOk = false;
	if (session.getAttribute(CommonData.PROLOGUE_OK) != null)
	{
		prologueOk = session.getAttribute(CommonData.PROLOGUE_OK).equals("true");
	}
	String action = request.getParameter(CommonData.PARAM_ACTION);
	String quest = request.getParameter(CommonData.PARAM_REQUESTED_QUEST);
	
	if(quest == null)
	{
		quest = CommonData.getDefault().getMainContentId();
	}

	if (prologueOk == false
		|| (action != null && action.equals(CommonData.ACTION_SETTINGS))
		|| quest.equals(CommonData.PROLOGUE_ID))
	{
		%>
		<%@include file="settings.jsp"%>
		<%
	}
	else
	{
		%>
		<div class="flush-right">
			<input type="button" value="<%= ResourceBundle.getBundle("site", Locale.getDefault()).getString("restartQuestionButton") %>" onclick="restartQuestion();" />
			<input type="button" value="<%= ResourceBundle.getBundle("site", Locale.getDefault()).getString("restartAllQuestionsButton") %>" onclick="restartAllQuestions();" />
		</div>
		<%=CommonData.getDefault().getQuestionsOutput(quest)%>
		<%
	}
%>