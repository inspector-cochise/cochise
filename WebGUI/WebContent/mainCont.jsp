<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	if (session.getAttribute("loggedIn") == null
			|| session.getAttribute("runId") == null 
			|| !session.getAttribute("runId").equals(CommonData.RUN_ID)
		)
	{
		response.sendRedirect( response.encodeRedirectURL(CommonData.LOGIN_SERVLET) );
		return;
	}
%>
<%
	boolean prologueOk = false;
	if (session.getAttribute("prologueOk") != null)
	{
		prologueOk = session.getAttribute("prologueOk").equals("true");
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
		<%=CommonData.getDefault().getQuestionsOutput(quest)%>
		<%
	}
%>