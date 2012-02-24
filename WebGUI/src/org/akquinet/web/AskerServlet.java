package org.akquinet.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.bsi.httpd.os.Quest1;
import org.akquinet.audit.ui.DelayedHtmlUserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;

@WebServlet("/main.html")
public class AskerServlet extends HttpServlet
{
	private static final String ACTION = "action";
	private static final String ACTION_GENERATE_REPORT = "genReport";
	private static final String ACTION_SETTINGS = "settings";

	private static final String PARAM_APACHE_EXECUTABLE = "apacheExec";
	private static final String PARAM_APACHE_CONFIG = "apacheConfig";
	private static final String PARAM_HIGH_SECURITY = "highSec";
	private static final String PARAM_HIGH_PRIVACY = "highPriv";
	
	private static final String GET_DEFAULT_SRV_ROOT_COMMAND = "./srvRoot.sh";
	
	private static final Object PROLOGUE_ID = "settings";

	private enum QuestionStatus
	{
		GOOD,
		BAD,
		OPEN
	}
	
	private HashMap<String, YesNoQuestion> _questionsById;
	private LinkedHashMap<YesNoQuestion, YesNoQuestionProperties> _questionProperties;
	private boolean _prologueOk = false;
	private String _mainContentId;
	private PrologueData _prologueData;
	private ResourceBundle _labels;
	
	public AskerServlet()
	{
		super();

		String apacheExec = "/usr/sbin/apache2";			//TODO OS-dependet initial value
		String apacheConf = "/etc/apache2/apache2.conf";	//TODO OS-dependet initial value
		_questionProperties = new LinkedHashMap<YesNoQuestion, YesNoQuestionProperties>();
		_questionsById = new HashMap<String, YesNoQuestion>();
		
		_labels = ResourceBundle.getBundle("AskerServlet", Locale.getDefault());
		
		_prologueData = new PrologueData(apacheExec, apacheConf, null, null, null, true, true);
		
		DelayedHtmlUserCommunicator c1	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q1				= new Quest1(_prologueData._highSec, c1);
		_questionProperties.put(q1, new YesNoQuestionProperties(q1, c1, QuestionStatus.OPEN, new AnsweringThread(q1, this)));
		
		for (YesNoQuestion q : _questionProperties.keySet())
		{
			_questionsById.put(q.getID(), q);
			_questionProperties.get(q).askingThread.start();
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
		writer.println("	<script language=\"javascript\" src=\"script.js\" ></script>");
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
		writer.println("			<form><input type=\"button\" value=\"" + _labels.getString("genReportButton") + "\" onclick=\"location='main.html?action=" + ACTION_GENERATE_REPORT + "'\"/></form>");	//TODO make button work
		writer.println("		</div>");
		writer.println("		<div id=\"lower-left\">");
		writer.println("			" + _labels.getString("copyright") + "<br/>");
		writer.println("		<a href=\"www.inspector-cochise.de\" target=\"_blank\">www.inspector-cochise.de</a>");
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
		if(requestedId != null)
		{
			_mainContentId = requestedId;
		}
		
		if(_questionsById.get(_mainContentId) == null || !_prologueOk || _mainContentId.equals(PROLOGUE_ID))
		{
			viewPrologue(response.getWriter());
		}
		else
		{
			viewQuestion(_questionsById.get(_mainContentId), response.getWriter());
		}
	}

	private String getExecErrorMsg()
	{
		File apacheExec = new File(_prologueData._apacheExec);
		
		if(!apacheExec.exists())
		{
			return "Diese Datei existiert nicht.";	//TODO language dependency
		}
		else
		{
			return "";
		}
	}

	private String getConfigErrorMsg()
	{
		//TODO language dependency
		File apacheConf = new File(_prologueData._apacheConf);
		
		if(!apacheConf.exists())
		{
			return "Diese Datei existiert nicht.";
		}

		try
		{
			//ConfigFile conf = new ConfigFile(apacheConf);
			ConfigFile conf = new ConfigFile(apacheConf, getDefaultServerRoot());
			_prologueData._conf = conf;
			return "";
		}
		catch (ParserException e)
		{
			if(e.getMessage() != null)
			{
				return e.getMessage();
			}
			else
			{
				return e.getClass().getName();
				//return "Ihre Konfigurationsdatei enthält Fehler. Bitte beheben Sie diese.";	//TODO hint to envvars problem
			}
		}
		catch (IOException e)
		{
			if(e.getMessage() != null)
			{
				return e.getMessage();
			}
			else
			{
				return "Auf diese Konfigurationsdatei konnte nicht zugegriffen werden.";
			}
		}
	}

	private void writeNavigationContent(PrintWriter writer) throws IOException
	{
		writer.println("				<tr><td><a href=\"./main.html?quest=" + PROLOGUE_ID + "\">Einstellungen</a></td>	<td/></tr>");
		writer.println("				<tr><td/>						<td/></tr>");
		
		for (YesNoQuestion q : _questionProperties.keySet())
		{
			writer.print("				<tr><td><a href=\"./main.html?quest=" + q.getID() + "\">");
			writer.print(q.getName());
			switch(_questionProperties.get(q).status)
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
		for (YesNoQuestion q : _questionProperties.keySet())
		{
			switch(_questionProperties.get(q).status)
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
		int goodPercentage = (100*good) / _questionProperties.keySet().size();
		int badPercentage = (100*bad) / _questionProperties.keySet().size();
		int openPercentage = (100*open) / _questionProperties.keySet().size();
		
		writer.println("				<li><span class=\"good\">" + goodPercentage + "%</span> positiv beantwortet</li>");
		writer.println("				<li><span class=\"open\">" + openPercentage + "%</span> noch nicht beantwortet</li>");
		writer.println("				<li><span class=\"bad\">" + badPercentage + "%</span> negativ beantwortet</li>");
	}

	private void viewPrologue(PrintWriter writer)
	{
		String execErrorMsg = getExecErrorMsg();
		String configErrorMsg = getConfigErrorMsg();
		
		if(execErrorMsg.equals("") && configErrorMsg.equals("") && "root".equals(System.getenv("USER")))
		{
			_prologueOk = true;
		}
		else
		{
			_prologueOk = false;
		}
		
		if(!"root".equals(System.getenv("USER")))
		{
			writer.println("		<span class=\"bad\">Diese Anwendung (d. h. der Tomcat-Server) muss mit von root ausgeführt werden.</span>");
		}
		
		// TODO language dependency
		writer.println("		<h1>Einstellungen</h1>");
		writer.println("		Bitte stellen Sie die folgenden Werte passend für Ihr System ein:");
		writer.println("		<form action=\"main.html\"><table>");
		writer.println("		<input type=\"hidden\"" +
										"name=\"action\"" +
										"value=\"" + ACTION_SETTINGS + "\" />");
		
		writer.println("			<tr><td>Ihr Apache Executable</td>" +
				"<td><input type=\"text\"" +
				"name=\""+ PARAM_APACHE_EXECUTABLE + "\"" +
				"size=\"40\"" +
				"value=\"" + _prologueData._apacheExec + "\" /><span class=\"bad\">" +
				execErrorMsg +
				"</span></td></tr>");
		
		writer.println("			<tr><td>Ihre Hauptkonfigurationsdatei</td>" +
				"<td><input type=\"text\"" +
							"name=\""+ PARAM_APACHE_CONFIG + "\"" +
							"size=\"40\"" +
							"value=\"" + _prologueData._apacheConf + "\" /><span class=\"bad\">" +
							configErrorMsg +
							"</span></td></tr>");
		
		
		
		writer.println("			<tr><td>Ist ein hohes Maß an Sicherheit erforderlich?</td>" +
				"<td><input type=\"checkbox\"" +
				"name=\""+ PARAM_HIGH_SECURITY + "\"" +
				"value=\"val\"" +
				(_prologueData._highSec ? "checked=\"checked\"" : "") +
				"/></td></tr>");
		
		writer.println("			<tr><td>Ist ein hohes Maß an Vertraulichkeit erforderlich?</td>" +
				"<td><input type=\"checkbox\"" +
							"name=\""+ PARAM_HIGH_PRIVACY + "\"" +
							"value=\"val\"" +
							(_prologueData._highPriv ? "checked=\"checked\"" : "") +
							"/></td></tr>");
		
		writer.println("			<tr><td/><td><input type=\"submit\"" +
														"value=\"Übernehmen\" /></td></tr>");
		writer.println("		</table></form>");
	}

	private void viewQuestion(YesNoQuestion quest, PrintWriter writer)
	{
		//TODO link/button targets
		writer.println(_questionProperties.get(quest).communicator.stringifyCurrentState());
	}

	private void triggerActions(HttpServletRequest request)
	{
		String action = request.getParameter(ACTION);
		if(action != null)
		{
			if(action.equals(ACTION_SETTINGS))
			{
				String apacheExec = request.getParameter(PARAM_APACHE_EXECUTABLE);
				File apacheExecutable = null;
				if(apacheExec != null)
				{
					apacheExecutable = new File(apacheExec);
				}
				else
				{
					apacheExec = _prologueData._apacheExec;
				}
				
				String apacheConf = request.getParameter(PARAM_APACHE_CONFIG);
				File configFile = null;
				if(apacheConf != null)
				{
					configFile = new File(apacheConf);
				}
				else
				{
					apacheConf = _prologueData._apacheConf;
				}
				
				boolean highSec = request.getParameter(PARAM_HIGH_SECURITY) != null;
				boolean highPriv = request.getParameter(PARAM_HIGH_PRIVACY) != null;
				
				if(configFile != null)
				{
					try
					{
						ConfigFile conf = new ConfigFile(configFile);
						_prologueData = new PrologueData(apacheExec, apacheConf, apacheExecutable, conf, configFile, highSec, highPriv);
					}
					catch (Exception e)
					{
						//we will do the checks again later in viewProloge()
						_prologueData = new PrologueData(apacheExec, apacheConf, apacheExecutable, null, configFile, highSec, highPriv);
					}
				}
				else
				{
					_prologueData = new PrologueData(apacheExec, apacheConf, apacheExecutable, null, configFile, highSec, highPriv);
				}
			}
			else if(action.equals(ACTION_GENERATE_REPORT))
			{
				//TODO
			}
			else if(_questionsById.get(action) != null)
			{
				//TODO
			}
			else
			{
				//unknown action, just ignore this
			}
		}
	}
	
	private void updateStatus(YesNoQuestion quest, QuestionStatus status)
	{
		synchronized (_questionProperties)
		{
			_questionProperties.get(quest).status = status;
		}
	}

	private class AnsweringThread extends Thread
	{
		private YesNoQuestion _quest;
		private Boolean _answer;
		private AskerServlet _callback;
		
		AnsweringThread(YesNoQuestion quest, AskerServlet callback)
		{
			_quest = quest;
			_answer = null;
			_callback = callback;
		}
		
		@Override
		public void run()
		{
			_answer = _quest.answer();
			_callback.updateStatus(_quest, _answer ? QuestionStatus.GOOD : QuestionStatus.BAD);
		}
		
		public Boolean getAnswer()
		{
			return _answer;
		}
	}
	
	private class YesNoQuestionProperties
	{
		YesNoQuestion quest;
		DelayedHtmlUserCommunicator communicator;
		QuestionStatus status;
		AnsweringThread askingThread;
		
		YesNoQuestionProperties(YesNoQuestion _quest, DelayedHtmlUserCommunicator _communicator, QuestionStatus _status, AnsweringThread _askingThread)
		{
			quest = _quest;
			communicator = _communicator;
			status = _status;
			askingThread = _askingThread;
		}
	}
	
	private String getDefaultServerRoot()
	{
		ShellAnsweredQuestion q = new ShellAnsweredQuestion(GET_DEFAULT_SRV_ROOT_COMMAND, _prologueData._apacheExec);
		q.answer();
		try
		{
			int c = q.getStdOut().read();
			StringBuffer buf = new StringBuffer();
			while(c != -1)
			{
				buf.append((char)c);
				c = q.getStdOut().read();
			}
			if(buf.charAt(buf.length()-1) == '\n')
			{
				return buf.substring(0, buf.length()-1);
			}
			else
			{
				return buf.toString();
			}
		}
		catch (IOException e)
		{
			//getting the default server's root may be vital for the application
			throw new RuntimeException(e);
		}
	}
}
