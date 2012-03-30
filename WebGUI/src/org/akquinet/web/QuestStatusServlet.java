package org.akquinet.web;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.akquinet.audit.YesNoQuestion;

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
		HttpSession session = request.getSession();
		if (session.getAttribute("loggedIn") == null
				|| session.getAttribute("runId") == null 
				|| !session.getAttribute("runId").equals(CommonData.RUN_ID)
			)
		{
			response.sendRedirect( response.encodeRedirectURL(CommonData.LOGIN_SERVLET) );
			return;
		}
		
		String requestedId = request.getParameter(CommonData.PARAM_REQUESTED_QUEST);
		if(requestedId == null)
		{
			//in this case we will return a list of all available questions
			StringBuilder answer = new StringBuilder();
			
			Iterator<YesNoQuestion> it = CommonData.getDefault().getQuestions().values().iterator();
			while(it.hasNext())
			{
				YesNoQuestion current = it.next();
				answer.append(current.getID());
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
				switch(CommonData.getDefault().getStatus(requestedId))
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

}
