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
		response.sendRedirect( response.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL) );
		return;
	}
%>

<%
	ResourceBundle labels = ResourceBundle.getBundle("navigation", Locale.getDefault());
	String mainContId = CommonData.getDefault().getMainContentId();
%>
						<table>
							<tr>
								<td><span id="navlink_<%= CommonData.PROLOGUE_ID %>" onclick="updateMainContent('<%= CommonData.PROLOGUE_ID %>')" class="navigationLink"
									<%
										if(CommonData.getDefault().getMainContentId().equals(CommonData.PROLOGUE_ID))
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
								for(String questId : CommonData.getDefault().getQuestionIds())
								{
									YesNoQuestion quest = CommonData.getDefault().getQuestion(questId);
							%>
									<tr>
										<td><span id="navlink_<%= questId %>" onclick="updateMainContent('<%= questId %>')" class="navigationLink"
											<%
												if(CommonData.getDefault().getMainContentId().equals(questId))
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
											switch(CommonData.getDefault().getStatus(quest))
											{
											case OPEN:
												%>
													<a onmouseover="showWMTT('openTip')" onmouseout="hideWMTT()" href=""><img src="img/open.png" /></a>
													<%--<span class="open"><%= labels.getString("ope") %></span>--%>
												<%
												break;
											case GOOD:
												%>
													<a onmouseover="showWMTT('goodTip')" onmouseout="hideWMTT()" href=""><img src="img/good.png" /></a>
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