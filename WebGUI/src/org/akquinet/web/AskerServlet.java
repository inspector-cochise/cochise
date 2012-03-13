package org.akquinet.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.UnexpectedException;
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
import org.akquinet.audit.bsi.httpd.software.Quest2;
import org.akquinet.audit.bsi.httpd.software.Quest3;
import org.akquinet.audit.bsi.httpd.software.Quest4;
import org.akquinet.audit.bsi.httpd.software.Quest5;
import org.akquinet.audit.bsi.httpd.software.Quest6;
import org.akquinet.audit.bsi.httpd.software.Quest7;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest10;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest11;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest12;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest8;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest9;
import org.akquinet.audit.ui.DelayedHtmlUserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;

@WebServlet("/main.html")
public class AskerServlet extends HttpServlet
{
	private static final String PARAM_REQUESTED_QUEST = "quest";
	private static final String ACTION_GENERATE_REPORT = "genReport";
	private static final String ACTION_SETTINGS = "settings";
	private static final Object ACTION_ANSWER = "answer";

	private static final String PARAM_ACTION = "action";
	private static final String PARAM_APACHE_EXECUTABLE = "apacheExec";
	private static final String PARAM_APACHE_CONFIG = "apacheConfig";
	private static final String PARAM_HIGH_SECURITY = "highSec";
	private static final String PARAM_HIGH_PRIVACY = "highPriv";
	private static final String PARAM_ANSWER = "answer";
	
	private static final String GET_DEFAULT_SRV_ROOT_COMMAND = "./srvRoot.sh";
	
	private static final String PROLOGUE_ID = "settings";

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
		writer.println("	<script language=\"javascript\" src=\"jquery-1.7.1.js\" ></script>");
		writer.println("	<script language=\"javascript\" src=\"hubu.js\" ></script>");
		writer.println("	<script language=\"javascript\" src=\"disclosure.js\" ></script>");
		
		writer.println("	<script language=\"javascript\">");
		writer.println("		$(document).ready(function(){");
		writer.println("		");
		writer.println("		  var title = $.trim( $(\"#container\").find('title').remove().text() );");
		writer.println("		  if ( title ) document.title = title;");
		writer.println("          var discl = disclosure();");
		writer.println("		  hub");
		writer.println("		      .registerComponent(discl, {");
		writer.println("		          disclosureId: '.disclosures .feature-title',");
		writer.println("		          component_name: 'disclosure'");
		writer.println("		      })");
		writer.println("		      .start();");
		writer.println("		  ");
		writer.println("		  hub.publish(true, \"/container/load\", { containerId: 'body' });");
		writer.println("		});");
		writer.println("	</script>");
		
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<div id=\"container\">");
		writer.println("<div id=\"header\">");
		writer.println("	<div id=\"logo\"></div>");
		writer.println("</div>");
		writer.println("<div id=\"middle\">");
		writer.println("	<div id=\"left\">");
		writer.println("		<div id=\"upper-left\"><div id=\"content\">");
		writer.println("			<table>");
		
		writeNavigationContent(response);
		
		writer.println("			</table>");
		writer.println("		</div></div>");
		writer.println("		<div id=\"middle-left\">");
		writer.println("			<h1>Fortschritt</h1>");
		writer.println("			<ul>");
		
		writeStatistics(writer);
		
		writer.println("			</ul>");
		writer.println("			<form><input type=\"button\" value=\"" + _labels.getString("genReportButton") + "\" onclick=\"location='" + response.encodeURL("main.html?action=" + ACTION_GENERATE_REPORT) +"'\"/></form>");	//TODO make button work
		writer.println("		</div>");
		writer.println("		<div id=\"lower-left\">");
		writer.println("			" + _labels.getString("copyright") + "<br/>");
		writer.println("		<a href=\"http://www.inspector-cochise.de\" target=\"_blank\">www.inspector-cochise.de</a>");
		writer.println("		</div>");
		writer.println("	</div>");
		writer.println("	<div id=\"right\"><div id=\"content\">");
		
		writeMainContent(request, response);
		
		writer.println("	</div></div>");
		writer.println("</div>");
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
		if(shouldShowPrologue())
		{
			viewPrologue(response.getWriter());
		}
		else
		{
			viewQuestion(_questionsById.get(_mainContentId), response.getWriter());
		}
	}

	private boolean shouldShowPrologue()
	{
		return _mainContentId == null || _questionsById.get(_mainContentId) == null || !_prologueOk || _mainContentId.equals(PROLOGUE_ID);
	}

	private String getExecErrorMsg()
	{
		File apacheExec = new File(_prologueData._apacheExec);
		
		if(!apacheExec.exists())
		{
			return _labels.getString("E1");
		}
		else
		{
			_prologueData._apacheExecutable = apacheExec;
			return "";
		}
	}

	private String getConfigErrorMsg()
	{
		File apacheConf = new File(_prologueData._apacheConf);
		
		if(!apacheConf.exists())
		{
			return _labels.getString("E1");
		}
		_prologueData._configFile = apacheConf;

		try
		{
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
				//return _labels.getString("E3");	//TODO hint to envvars problem
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
				return _labels.getString("E2");
			}
		}
	}

	private void writeNavigationContent(HttpServletResponse response) throws IOException
	{
		PrintWriter writer = response.getWriter();
		writer.println("				<tr><td><a href=\"" + response.encodeURL("main.html?quest=" + PROLOGUE_ID) + "\">");
		if(shouldShowPrologue())
		{
			writer.print("<b>");
		}
		writer.print(_labels.getString("prologue2"));
		if(shouldShowPrologue())
		{
			writer.print("</b>");
		}
		writer.print("</a></td>	<td/></tr>");
		writer.println("				<tr><td/>						<td/></tr>");
		
		for (YesNoQuestion q : _questionProperties.keySet())
		{
			writer.print("				<tr><td><a href=\"" + response.encodeURL("main.html?quest=" + q.getID()) + "\">");
			if(q.getID().equals(_mainContentId))
			{
				writer.print("<b>");
			}
			writer.print(q.getName());
			if(q.getID().equals(_mainContentId))
			{
				writer.print("</b>");
			}
			
			switch(_questionProperties.get(q).status)
			{
			case GOOD:
				writer.println("</a></td>	<td><span class=\"good\">"+ _labels.getString("pos") +"</span></td></tr>");
				break;
			case BAD:
				writer.println("</a></td>	<td><span class=\"bad\">"+ _labels.getString("neg") +"</span></td></tr>");
				break;
			case OPEN:
				writer.println("</a></td>	<td><span class=\"open\">"+ _labels.getString("ope") +"</span></td></tr>");
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
		
		if(_questionProperties.keySet().size() == 0)
		{
			writer.println("				<li>"+ _labels.getString("stat1") +"</li>");
		}
		else
		{
			int goodPercentage = Math.round((100.0f*good) / _questionProperties.keySet().size());
			int badPercentage = Math.round((100.0f*bad) / _questionProperties.keySet().size());
			int openPercentage = Math.round((100.0f*open) / _questionProperties.keySet().size());

			//correct the 2 possible round errors
			//if 2 percantages are xx.5 then the sum grows over 100
			if(goodPercentage + badPercentage + openPercentage > 100)
			{
				if(goodPercentage > 0)
				{
					--goodPercentage;
				}
				else if(openPercentage > 0)
				{
					--openPercentage;
				}
				else if(badPercentage > 0)
				{
					--badPercentage;
				}
			}
			//if all percantages are xx.y with y < .5 then the sum is 99
			else if(goodPercentage + badPercentage + openPercentage < 100)
			{
				if(badPercentage < 100)
				{
					++badPercentage;
				}
				else if(openPercentage < 100)
				{
					++openPercentage;
				}
				else if(goodPercentage < 100)
				{
					++goodPercentage;
				}
			}
			
			writer.println("				<li><span class=\"good\">" + goodPercentage + "%</span> "+ _labels.getString("stat2") +"</li>");
			writer.println("				<li><span class=\"open\">" + openPercentage + "%</span> "+ _labels.getString("stat3") +"</li>");
			writer.println("				<li><span class=\"bad\">" + badPercentage + "%</span> "+ _labels.getString("stat4") +"</li>");
		}
	}

	private void viewPrologue(PrintWriter writer)
	{
		String execErrorMsg = getExecErrorMsg();
		String configErrorMsg = getConfigErrorMsg();
		
		if(execErrorMsg.equals("") && configErrorMsg.equals("") && "root".equals(System.getenv("USER")))
		{
			_prologueOk = true;
			if(_questionProperties.isEmpty())
			{
				addQuestions();
			}
		}
		else
		{
			_prologueOk = false;
		}
		
		if(!"root".equals(System.getenv("USER")))
		{
			writer.println("		<span class=\"bad\">"+ _labels.getString("prologue1") +"</span>");
		}
		
		writer.println("		<h1>"+ _labels.getString("prologue2") +"</h1>");
		writer.println("		"+ _labels.getString("prologue3") +"");
		writer.println("		<form action=\"main.html\"><table>");
		writer.println("		<input type=\"hidden\"" +
										"name=\"action\"" +
										"value=\"" + ACTION_SETTINGS + "\" />");
		
		writer.println("			<tr><td>"+ _labels.getString("prologue4") +"</td>" +
				"<td><input type=\"text\"" +
				"name=\""+ PARAM_APACHE_EXECUTABLE + "\"" +
				"size=\"40\"" +
				"value=\"" + _prologueData._apacheExec + "\" /><span class=\"bad\">" +
				execErrorMsg +
				"</span></td></tr>");
		
		writer.println("			<tr><td>"+ _labels.getString("prologue5") +"</td>" +
				"<td><input type=\"text\"" +
							"name=\""+ PARAM_APACHE_CONFIG + "\"" +
							"size=\"40\"" +
							"value=\"" + _prologueData._apacheConf + "\" /><span class=\"bad\">" +
							configErrorMsg +
							"</span></td></tr>");
		
		
		
		writer.println("			<tr><td>"+ _labels.getString("prologue6") +"</td>" +
				"<td><input type=\"checkbox\"" +
				"name=\""+ PARAM_HIGH_SECURITY + "\"" +
				"value=\"val\"" +
				(_prologueData._highSec ? "checked=\"checked\"" : "") +
				"/></td></tr>");
		
		writer.println("			<tr><td>"+ _labels.getString("prologue7") +"</td>" +
				"<td><input type=\"checkbox\"" +
							"name=\""+ PARAM_HIGH_PRIVACY + "\"" +
							"value=\"val\"" +
							(_prologueData._highPriv ? "checked=\"checked\"" : "") +
							"/></td></tr>");
		
		writer.println("			<tr><td/><td><input type=\"submit\"" +
														"value=\""+ _labels.getString("prologue8") +"\" /></td></tr>");
		writer.println("		</table></form>");
	}

	private void addQuestions()
	{
		DelayedHtmlUserCommunicator c1	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q1				= new Quest1(_prologueData._highSec, c1);
		c1.setQuestId(q1.getID());
		_questionProperties.put(q1, new YesNoQuestionProperties(q1, c1, QuestionStatus.OPEN, new AnsweringThread(q1, this)));
		
		DelayedHtmlUserCommunicator c2	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q2				= new Quest2(_prologueData._apacheExecutable, c2);
		c2.setQuestId(q2.getID());
		_questionProperties.put(q2, new YesNoQuestionProperties(q2, c2, QuestionStatus.OPEN, new AnsweringThread(q2, this)));
		
		DelayedHtmlUserCommunicator c3	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q3				= new Quest3(_prologueData._conf, _prologueData._apacheExecutable, c3);
		c3.setQuestId(q3.getID());
		_questionProperties.put(q3, new YesNoQuestionProperties(q3, c3, QuestionStatus.OPEN, new AnsweringThread(q3, this)));
		
		DelayedHtmlUserCommunicator c4	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q4				= new Quest4(_prologueData._conf, _prologueData._apacheExecutable, c4);
		c4.setQuestId(q4.getID());
		_questionProperties.put(q4, new YesNoQuestionProperties(q4, c4, QuestionStatus.OPEN, new AnsweringThread(q4, this)));
		
		DelayedHtmlUserCommunicator c5	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q5				= new Quest5(_prologueData._conf, c5);
		c5.setQuestId(q5.getID());
		_questionProperties.put(q5, new YesNoQuestionProperties(q5, c5, QuestionStatus.OPEN, new AnsweringThread(q5, this)));
		
		DelayedHtmlUserCommunicator c6	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q6				= new Quest6(_prologueData._apacheExecutable, c6);
		c6.setQuestId(q6.getID());
		_questionProperties.put(q6, new YesNoQuestionProperties(q6, c6, QuestionStatus.OPEN, new AnsweringThread(q6, this)));

		DelayedHtmlUserCommunicator c7	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q7				= new Quest7(_prologueData._conf, c7);
		c7.setQuestId(q7.getID());
		_questionProperties.put(q7, new YesNoQuestionProperties(q7, c7, QuestionStatus.OPEN, new AnsweringThread(q7, this)));
		
		DelayedHtmlUserCommunicator c8	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q8				= new Quest8(_prologueData._configFile, _prologueData._conf, _prologueData._highSec, c8);
		c8.setQuestId(q8.getID());
		_questionProperties.put(q8, new YesNoQuestionProperties(q8, c8, QuestionStatus.OPEN, new AnsweringThread(q8, this)));
		
		DelayedHtmlUserCommunicator c9	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q9				= new Quest9(_prologueData._conf, _prologueData._apacheExec, _prologueData._highSec, c9);
		c9.setQuestId(q9.getID());
		_questionProperties.put(q9, new YesNoQuestionProperties(q9, c9, QuestionStatus.OPEN, new AnsweringThread(q9, this)));
		
		DelayedHtmlUserCommunicator c10	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q10				= new Quest10(_prologueData._conf, c10);
		c10.setQuestId(q10.getID());
		_questionProperties.put(q10, new YesNoQuestionProperties(q10, c10, QuestionStatus.OPEN, new AnsweringThread(q10, this)));
		
		DelayedHtmlUserCommunicator c11	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q11				= new Quest11(_prologueData._conf, c11);
		c11.setQuestId(q11.getID());
		_questionProperties.put(q11, new YesNoQuestionProperties(q11, c11, QuestionStatus.OPEN, new AnsweringThread(q11, this)));
		
		DelayedHtmlUserCommunicator c12	= new DelayedHtmlUserCommunicator();
		YesNoQuestion q12				= new Quest12(_prologueData._conf, _prologueData._apacheExec, c12);
		c12.setQuestId(q12.getID());
		_questionProperties.put(q12, new YesNoQuestionProperties(q12, c12, QuestionStatus.OPEN, new AnsweringThread(q12, this)));
		
		for (YesNoQuestion q : _questionProperties.keySet())
		{
			_questionsById.put(q.getID(), q);
			_questionProperties.get(q).askingThread.start();
		}
	}

	private void viewQuestion(YesNoQuestion quest, PrintWriter writer)
	{
		writer.println(_questionProperties.get(quest).communicator.stringifyCurrentState());
	}

	private void triggerActions(HttpServletRequest request)
	{
		_mainContentId = request.getParameter(PARAM_REQUESTED_QUEST) != null ? request.getParameter(PARAM_REQUESTED_QUEST) : _mainContentId;
		
		String action = request.getParameter(PARAM_ACTION);
		if(action != null && _prologueOk)
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
			else if(action.equals(ACTION_ANSWER) && _mainContentId != null)
			{
				String answer = request.getParameter(PARAM_ANSWER);
				try
				{
					YesNoQuestion q = _questionsById.get(_mainContentId);
					DelayedHtmlUserCommunicator uc = _questionProperties.get(q).communicator;
					uc.setAnswer(answer);
					try
					{
						Thread.sleep(100);
					}
					catch (InterruptedException e)
					{
						//we just waited to give the AskingThread some time to call updateStatus, it's not so important...
					}
				}
				catch (NullPointerException e)
				{
					throw new RuntimeException("You didn't specify all parameters needed for an answer.", e);
				}
				catch (UnexpectedException e)
				{
					throw new RuntimeException("I didn't expect to receive any answer.", e);
				}
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
			try
			{
				_quest.initialize();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			
			_answer = _quest.answer();
			_callback.updateStatus(_quest, _answer ? QuestionStatus.GOOD : QuestionStatus.BAD);
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
