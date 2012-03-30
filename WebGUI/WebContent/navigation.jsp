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
		response.sendRedirect( response.encodeRedirectURL(CommonData.LOGIN_SERVLET) );
		return;
	}
%>

<%
	ResourceBundle labels = ResourceBundle.getBundle("navigation", Locale.getDefault());
	String mainContId = CommonData.getDefault().getMainContentId();
%>
						<table>
							<tr>
								<td><a href="<%= response.encodeURL(CommonData.MAIN_SERVLET + "?" + CommonData.PARAM_REQUESTED_QUEST + '=' + CommonData.PROLOGUE_ID) %>">
								<%
									if(mainContId.equals(CommonData.PROLOGUE_ID))
									{
										%><b><%
									}
								%>
								<%= labels.getString("settings") %>
								<%
									if(mainContId.equals(CommonData.PROLOGUE_ID))
									{
										%></b><%
									}
								%>
								</a></td>
								<td />
							</tr>
							<tr>
								<td />
								<td />
							</tr>
							<%
								for(YesNoQuestion quest : CommonData.getDefault().getQuestions().values())
								{
							%>
									<tr>
										<td><a href="<%= response.encodeURL(CommonData.MAIN_SERVLET + "?" + CommonData.PARAM_REQUESTED_QUEST + '=' + quest.getID()) %>">
										<%
											if(mainContId.equals(quest.getID()))
											{
												%><b><%
											}
										%>
										<%= quest.getName() %>
										<%
											if(mainContId.equals(quest.getID()))
											{
												%></b><%
											}
										%>
										</a></td>
										<td id="<%= quest.getID() %>">
										<%
											String statusKey;
											switch(CommonData.getDefault().getStatus(quest))
											{
											case OPEN:
												%>
													<a onmouseover="showWMTT('openTip')" onmouseout="hideWMTT()" href=""><img src="img/open.png" />
													<%--<span class="open"><%= labels.getString("ope") %></span>--%>
												<%
												break;
											case GOOD:
												%>
													<a onmouseover="showWMTT('goodTip')" onmouseout="hideWMTT()" href=""><img src="img/good.png" />
													<%--<span class="good"><%= labels.getString("pos") %></span>--%>
												<%
												break;
											case BAD:
												%>
													<a onmouseover="showWMTT('badTip')" onmouseout="hideWMTT()" href=""><img src="img/bad.png" /></a>
													<%--<span class="bad"><%= labels.getString("neg") %></span>--%>
												<%
												break;
											}
										%>
										</td>
									</tr>
							<%
								}
							%>
						</table>