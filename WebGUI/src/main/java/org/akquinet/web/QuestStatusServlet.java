package org.akquinet.web;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akquinet.audit.QuestionManager;

/**
 * Servlet implementation class QuestStatusServlet
 */
@WebServlet("/qStat.jsp")
public class QuestStatusServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public QuestStatusServlet()
	{
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String requestedId = request.getParameter(CommonData.PARAM_REQUESTED_QUEST);
		String action = request.getParameter(CommonData.PARAM_ACTION);
		
		HttpSession session = request.getSession();
		if (session.getAttribute("loggedIn") == null
				|| session.getAttribute("runId") == null 
				|| !session.getAttribute("runId").equals(CommonData.RUN_ID)
			)
		{
			if(action != null && action.equals(CommonData.ACTION_IS_AVAILABLE))
			{
				response.getWriter().print("false");
			}
			else
			{
				response.sendRedirect( response.encodeRedirectURL(CommonData.LOGIN_SERVLET_URL) );
			}
			return;
		}
		
		if(action == null || action.equals(CommonData.ACTION_QUEST_STATUS))
		{
			if(requestedId == null)
			{
				//in this case we will return a list of all available questions
				StringBuilder answer = new StringBuilder();
				
				Iterator<String> it = QuestionManager.getDefault().getQuestionIds().iterator();
				while(it.hasNext())
				{
					String current = it.next();
					answer.append(current);
					if(it.hasNext())
					{
						answer.append(',');
					}
				}
				
				response.getWriter().print(answer.toString());
				return;
			}
			else
			{
				try
				{
					switch(QuestionManager.getDefault().getStatus(requestedId))
					{
					case BAD:
						response.getWriter().print("neg");
						return;
					case GOOD:
						response.getWriter().print("pos");
						return;
					case OPEN:
						response.getWriter().print("ope");
						return;
					}
				}
				catch(NullPointerException e)
				{
					response.getWriter().print("requested question unknown");
					return;
				}
			}
		}
		if(action.equals(CommonData.ACTION_IS_AVAILABLE))
		{
			response.getWriter().print("true");
		}
		else if (action.equals(CommonData.ACTION_IS_STALE))
		{
			if(QuestionManager.getDefault().isStale(request.getParameter("quest")))
			{
				response.getWriter().print("true");
			}
			else
			{
				response.getWriter().print("false");
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

}
