package org.akquinet.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.bsi.httpd.PrologueQuestion;
import org.akquinet.audit.bsi.httpd.os.Quest1;

@WebServlet("/main.html")
public class AskerServlet extends HttpServlet
{
	private enum QuestionStatus
	{
		GOOD,
		BAD,
		OPEN
	}
	
	private PrologueQuestion _prologue;
	private List<YesNoQuestion> _questions;
	private HashMap<String, YesNoQuestion> _questionsById;
	private HashMap<YesNoQuestion, QuestionStatus> _statusMap;
	private boolean _prologueOk = false;
	
	public AskerServlet()
	{
		super();

		String apacheExec = "";
		String apacheConf = "";
		
		PrologueData pD = new PrologueData(apacheExec, apacheConf, null, null, null, true, true);
		_prologue = new PrologueQuestion(pD);
		
		_questions = new LinkedList<YesNoQuestion>();
		_questions.add(new Quest1(pD._highSec));	//TODO add more than just a dummy question
		
		_statusMap = new HashMap<YesNoQuestion, AskerServlet.QuestionStatus>();
		_questionsById = new HashMap<String, YesNoQuestion>();
		for (YesNoQuestion q : _questions)
		{
			_statusMap.put(q, QuestionStatus.OPEN);	//TODO maybe initialize with something better
			_questionsById.put(q.getID(), q);
		}
		
		startQuestions();
	}
	
	private void startQuestions()
	{
		for (YesNoQuestion q : _questions)
		{
			Thread th = new AnsweringThread(q);
			th.start();
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		triggerActions(request);
		
		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<head>");
		//writer.println("	<meta charset=\"utf-8\"/>");	//TODO wtf?
		writer.println("	<title>Inspector-Cochise</title>");
		writer.println("	<link rel=\"stylesheet\" href=\"./style.css\" type=\"text/css\" />");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<div id=\"header\">");
		writer.println("	<div id=\"logo\"></div>");
		writer.println("</div>");
		writer.println("<div id=\"middle\">");
		writer.println("	<div id=\"left\">");
		writer.println("		<div id=\"upper-left\"><div id=\"content\">");
		writer.println("			<table>");
		
		writeNavigationContent(writer);
		
		writer.println("			</table>");
		writer.println("		</div></div>");
		writer.println("		<div id=\"middle-left\">");
		writer.println("			<h1>Fortschritt</h1>");
		writer.println("			<ul>");
		
		writeStatistics(writer);
		
		writer.println("			</ul>");
		writer.println("			<form><input type=\"button\" name=\"generate\" value=\"Report generieren\"/></form>");	//TODO make button work
		writer.println("		</div>");
		writer.println("		<div id=\"lower-left\">");
		writer.println("			&copy; Copyright by akquinet AG<br/>");
		writer.println("			blablabla<br/> GPL<br/> blablablabla");	//TODO real content
		writer.println("		</div>");
		writer.println("	</div>");
		writer.println("	<div id=\"right\"><div id=\"content\">");
		
		writeMainContent(request, response);
		
		writer.println("	</div></div>");
		writer.println("</div>");
		writer.println("</body>");
		writer.println("</html>");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}

	private void writeMainContent(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String requestedId = request.getParameter("quest");
		
		if(!_prologueOk || requestedId == null || requestedId.equals(_prologue.getID()))
		{
			viewQuestion(_prologue, response.getWriter());
		}
		else
		{
			viewQuestion(_questionsById.get(requestedId), response.getWriter());
		}
	}

	private void writeNavigationContent(PrintWriter writer) throws IOException
	{
		writer.println("				<tr><td><a href=\"./main.html\">Einstellungen</a></td>	<td/></tr>");	//TODO href to real target
		writer.println("				<tr><td/>						<td/></tr>");
		
		for (YesNoQuestion q : _questions)
		{
			writer.print("				<tr><td><a href=\"./main.html\">");	//TODO href to real target
			writer.print(q.getName());
			switch(_statusMap.get(q))
			{
			case GOOD:
				writer.println("</a></td>	<td><span class=\"good\">positiv</span></td></tr>");
				break;
			case BAD:
				writer.println("</a></td>	<td><span class=\"bad\">negativ</span></td></tr>");
				break;
			case OPEN:
				writer.println("</a></td>	<td><span class=\"open\">offen</span></td></tr>");
				break;
			}
		}
	}

	private void writeStatistics(PrintWriter writer) throws IOException
	{
		int good = 0;
		int bad = 0;
		int open = 0;
		for (YesNoQuestion q : _questions)
		{
			switch(_statusMap.get(q))
			{
			case GOOD:
				++good;
				break;
			case BAD:
				++bad;
				break;
			case OPEN:
				++open;
				break;
			}
		}
		int goodPercentage = (100*good) / _questions.size();
		int badPercentage = (100*bad) / _questions.size();
		int openPercentage = (100*open) / _questions.size();
		
		writer.println("				<li><span class=\"good\">" + goodPercentage + "%</span> positiv beantwortet</li>");
		writer.println("				<li><span class=\"open\">" + openPercentage + "%</span> noch nicht beantwortet</li>");
		writer.println("				<li><span class=\"bad\">" + badPercentage + "%</span> negativ beantwortet</li>");
	}

	private void viewQuestion(YesNoQuestion quest, PrintWriter writer)
	{
		//TODO real content here
		writer.println("		<h1>TODO</h1>");
	}

	private void triggerActions(HttpServletRequest request)
	{
		// TODO Auto-generated method stub
	}
	
	private class AnsweringThread extends Thread
	{
		private YesNoQuestion _quest;
		private Boolean _answer;
		
		AnsweringThread(YesNoQuestion quest)
		{
			quest = _quest;
			_answer = null;
		}
		
		@Override
		public void run()
		{
			_answer = _quest.answer();
		}
		
		public Boolean getAnswer()
		{
			return _answer;
		}
	}
}
