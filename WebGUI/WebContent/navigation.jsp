<%@page import="org.akquinet.audit.YesNoQuestion"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="org.akquinet.web.CommonData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	ResourceBundle labels = ResourceBundle.getBundle("navigation", Locale.getDefault());
%>
						<table>
							<tr>
								<td><a href="<%= response.encodeURL("inspector.jsp?" + CommonData.PARAM_REQUESTED_QUEST + '=' + CommonData.PROLOGUE_ID) %>"> <%= labels.getString("settings") %></a></td>
								<td />
							</tr>
							<tr>
								<td />
								<td />
							</tr>
							<%-- TODO automatically generate this list (don't forget the prologue) --%>
							<%
								for(YesNoQuestion quest : CommonData.getDefault().getQuestions().values())
								{
							%>
									<tr>
										<td><a href="<%= response.encodeURL("inspector.jsp?" + CommonData.PARAM_REQUESTED_QUEST + '=' + quest.getID()) %>">
										<%= quest.getName() %></a></td>
										<%
											String statusKey;
											switch(CommonData.getDefault().getStatus(quest))
											{
											case OPEN:
												statusKey = "ope";
												break;
											case GOOD:
												statusKey = "pos";
												break;
											case BAD:
												statusKey = "neg";
												break;
											}
										%>
										<td><span class="good"><%= labels.getString("") %></span></td>
									</tr>
							<%
								}
							%>
						</table>