<%@page import="org.akquinet.web.AuthenticatorServlet"%>
<%@page import="org.akquinet.audit.QuestionManager"%>
<%@page import="org.akquinet.audit.YesNoQuestion"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="org.akquinet.web.CommonData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	if (!AuthenticatorServlet.authenticate(request.getSession()))
	{
		response.sendRedirect( response.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL) );
		return;
	}
%>

<%
	ResourceBundle labels = ResourceBundle.getBundle("navigation", Locale.getDefault());
	String mainContId = QuestionManager.getDefault().getMainContentId();
%>
						<table>
							<tr>
								<td><span id="navlink_<%= CommonData.PROLOGUE_ID %>" onclick="updateMainContent('<%= CommonData.PROLOGUE_ID %>')" class="navigationLink"
									<%
										if(QuestionManager.getDefault().getMainContentId().equals(CommonData.PROLOGUE_ID))
										{
											out.println("style=\"font-weight: bold;\"");
										}
									%>
								>
								<%= labels.getString("settings") %>
								</span></td>
								<td />
							</tr>
							<tr>
								<td />
								<td />
							</tr>
							<%
								for(String questId : QuestionManager.getDefault().getQuestionIds())
								{
									YesNoQuestion quest = QuestionManager.getDefault().getQuestion(questId);
							%>
									<tr>
										<td><span id="navlink_<%= questId %>" onclick="updateMainContent('<%= questId %>')" class="navigationLink"
											<%
												if(QuestionManager.getDefault().getMainContentId().equals(questId))
												{
													out.println("style=\"font-weight: bold;\"");
												}
											%>
										>
										<%= quest.getName() %>
										</span></td>
										<td id="<%= questId %>">
										<%
											String statusKey;
											switch(QuestionManager.getDefault().getStatus(quest))
											{
											case OPEN:
												%>
													<a onmouseover="showWMTT('openTip')" onmouseout="hideWMTT()" href="<%= CommonData.MAIN_SERVLET_URL %>"><img src="img/open.png" /></a>
													<%--<span class="open"><%= labels.getString("ope") %></span>--%>
												<%
												break;
											case GOOD:
												%>
													<a onmouseover="showWMTT('goodTip')" onmouseout="hideWMTT()" href="<%= CommonData.MAIN_SERVLET_URL %>"><img src="img/good.png" /></a>
													<%--<span class="good"><%= labels.getString("pos") %></span>--%>
												<%
												break;
											case BAD:
												%>
													<a onmouseover="showWMTT('badTip')" onmouseout="hideWMTT()" href="<%= CommonData.MAIN_SERVLET_URL %>"><img src="img/bad.png" /></a>
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