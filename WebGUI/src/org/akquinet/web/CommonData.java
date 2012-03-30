package org.akquinet.web;

import java.io.File;
import java.rmi.UnexpectedException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

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

public class CommonData
{
	public static final String ACTION_GENERATE_REPORT = "genReport";
	public static final String ACTION_SETTINGS = "settings";
	public static final String ACTION_ANSWER = "answer";

	public static final String MAIN_SERVLET = "inspector.jsp";
	public static final String LOGIN_SERVLET = "login.jsp";
	
	public static final String PARAM_REQUESTED_QUEST = "quest";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_APACHE_EXECUTABLE = "apacheExec";
	public static final String PARAM_APACHE_CONFIG = "apacheConfig";
	public static final String PARAM_HIGH_SECURITY = "highSec";
	public static final String PARAM_HIGH_PRIVACY = "highPriv";
	public static final String PARAM_ANSWER = "answer";
	
	public static final String PROLOGUE_ID = "settings";
	
	public static final long RUN_ID = (new Random(System.nanoTime())).nextInt();
	
	public enum QuestionStatus
	{
		GOOD,
		BAD,
		OPEN
	}
	
	private static CommonData _default;
	
	private String _mainContentId;
	private PrologueData _prologueData;
	private HashMap<String, YesNoQuestion> _questionsById;
	private HashMap<YesNoQuestion, YesNoQuestionProperties> _questionProperties;
	private boolean _configured = false;
	
	private CommonData()
	{
		_questionProperties = new LinkedHashMap<YesNoQuestion, YesNoQuestionProperties>();
		_questionsById = new LinkedHashMap<String, YesNoQuestion>();
		_mainContentId = PROLOGUE_ID;
	}
	
	public static CommonData getDefault()
	{
		if(_default == null)
		{
			_default = new CommonData();
		}
		return _default;
	}
	
	public static void clearDefault()
	{
		_default = null;
	}
	
	public PrologueData getPrologueData()
	{
		return _prologueData;
	}
	
	public String getMainContentId()
	{
		return _mainContentId;
	}
	
	public boolean isConfigured()
	{
		return _configured;
	}
	
	public HashMap<String, YesNoQuestion> getQuestions()
	{
		return _questionsById;
	}

	public void triggerActions(HttpServletRequest request)
	{
		_mainContentId = request.getParameter(PARAM_REQUESTED_QUEST) != null ? request.getParameter(PARAM_REQUESTED_QUEST) : _mainContentId;
		
		String action = request.getParameter(PARAM_ACTION);
		if(_prologueData == null)
		{
			_prologueData = (new SettingsHelper())._prologueData;
			_configured = false;
		}
		
		if(action != null)
		{
			if(action.equals(ACTION_SETTINGS) || _mainContentId.equals(PROLOGUE_ID))
			{
				initPrologueData(request);
				
				SettingsHelper helper = new SettingsHelper(_prologueData);
				
				String execErrorMsg = helper.getExecErrorMsg();
				String configErrorMsg = helper.getConfigErrorMsg();
				
				_prologueData = helper._prologueData;
				
				if (execErrorMsg.equals("") && configErrorMsg.equals("") && "root".equals(System.getenv("USER")))
				{
					addQuestions();
					_configured = true;
				}
			}
			else if(action.equals(ACTION_GENERATE_REPORT))
			{
				//TODO
			}
			else if(action.equals(ACTION_ANSWER))
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

	private void initPrologueData(HttpServletRequest request)
	{
		String apacheExec = request.getParameter(PARAM_APACHE_EXECUTABLE);
		if(apacheExec == null)
		{
			apacheExec = _prologueData._apacheExec;
		}
		File apacheExecutable = new File(apacheExec);
		
		String apacheConf = request.getParameter(PARAM_APACHE_CONFIG);
		if(apacheConf == null)
		{
			apacheConf = _prologueData._apacheConf;
		}
		File configFile = new File(apacheConf);
		
		boolean highSec = request.getParameter(PARAM_HIGH_SECURITY) != null;
		boolean highPriv = request.getParameter(PARAM_HIGH_PRIVACY) != null;
		
		try
		{
			ConfigFile conf = new ConfigFile(configFile);
			_prologueData = new PrologueData(apacheExec, apacheConf, apacheExecutable, conf, configFile, highSec, highPriv);
		}
		catch (Exception e)
		{
			//we will do the checks again later in settings.jsp
			_prologueData = new PrologueData(apacheExec, apacheConf, apacheExecutable, null, configFile, highSec, highPriv);
		}
	}
	
	public void addQuestions()
	{
		if(!_questionProperties.isEmpty())
		{
			return;
		}
		
		DelayedHtmlUserCommunicator c1	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q1				= new Quest1(_prologueData._highSec, c1);
		c1.setQuestId(q1.getID());
		_questionProperties.put(q1, new YesNoQuestionProperties(q1, c1, QuestionStatus.OPEN, new AnsweringThread(q1, this)));
		
		DelayedHtmlUserCommunicator c2	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q2				= new Quest2(_prologueData._apacheExecutable, c2);
		c2.setQuestId(q2.getID());
		_questionProperties.put(q2, new YesNoQuestionProperties(q2, c2, QuestionStatus.OPEN, new AnsweringThread(q2, this)));
		
		DelayedHtmlUserCommunicator c3	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q3				= new Quest3(_prologueData._conf, _prologueData._apacheExecutable, c3);
		c3.setQuestId(q3.getID());
		_questionProperties.put(q3, new YesNoQuestionProperties(q3, c3, QuestionStatus.OPEN, new AnsweringThread(q3, this)));
		
		DelayedHtmlUserCommunicator c4	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q4				= new Quest4(_prologueData._conf, _prologueData._apacheExecutable, c4);
		c4.setQuestId(q4.getID());
		_questionProperties.put(q4, new YesNoQuestionProperties(q4, c4, QuestionStatus.OPEN, new AnsweringThread(q4, this)));
		
		DelayedHtmlUserCommunicator c5	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q5				= new Quest5(_prologueData._conf, c5);
		c5.setQuestId(q5.getID());
		_questionProperties.put(q5, new YesNoQuestionProperties(q5, c5, QuestionStatus.OPEN, new AnsweringThread(q5, this)));
		
		DelayedHtmlUserCommunicator c6	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q6				= new Quest6(_prologueData._apacheExecutable, c6);
		c6.setQuestId(q6.getID());
		_questionProperties.put(q6, new YesNoQuestionProperties(q6, c6, QuestionStatus.OPEN, new AnsweringThread(q6, this)));

		DelayedHtmlUserCommunicator c7	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q7				= new Quest7(_prologueData._conf, c7);
		c7.setQuestId(q7.getID());
		_questionProperties.put(q7, new YesNoQuestionProperties(q7, c7, QuestionStatus.OPEN, new AnsweringThread(q7, this)));
		
		DelayedHtmlUserCommunicator c8	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q8				= new Quest8(_prologueData._configFile, _prologueData._conf, _prologueData._highSec, c8);
		c8.setQuestId(q8.getID());
		_questionProperties.put(q8, new YesNoQuestionProperties(q8, c8, QuestionStatus.OPEN, new AnsweringThread(q8, this)));
		
		DelayedHtmlUserCommunicator c9	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q9				= new Quest9(_prologueData._conf, _prologueData._apacheExec, _prologueData._highSec, c9);
		c9.setQuestId(q9.getID());
		_questionProperties.put(q9, new YesNoQuestionProperties(q9, c9, QuestionStatus.OPEN, new AnsweringThread(q9, this)));
		
		DelayedHtmlUserCommunicator c10	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q10				= new Quest10(_prologueData._conf, c10);
		c10.setQuestId(q10.getID());
		_questionProperties.put(q10, new YesNoQuestionProperties(q10, c10, QuestionStatus.OPEN, new AnsweringThread(q10, this)));
		
		DelayedHtmlUserCommunicator c11	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q11				= new Quest11(_prologueData._conf, c11);
		c11.setQuestId(q11.getID());
		_questionProperties.put(q11, new YesNoQuestionProperties(q11, c11, QuestionStatus.OPEN, new AnsweringThread(q11, this)));
		
		DelayedHtmlUserCommunicator c12	= new DelayedHtmlUserCommunicator(MAIN_SERVLET);
		YesNoQuestion q12				= new Quest12(_prologueData._conf, _prologueData._apacheExec, c12);
		c12.setQuestId(q12.getID());
		_questionProperties.put(q12, new YesNoQuestionProperties(q12, c12, QuestionStatus.OPEN, new AnsweringThread(q12, this)));
		
		for (YesNoQuestion q : _questionProperties.keySet())
		{
			_questionsById.put(q.getID(), q);
			_questionProperties.get(q).askingThread.start();
		}
	}
	
	/**
	 * 
	 * @param questId The id of the requested question. Can be null.
	 * @return The (HTML-)stringified dialogue-status of the requested question. If questId is null the result will be the dialogue-status of the last requested question.
	 */
	public String getQuestionsOutput(String questId)
	{
		if(questId == null)
		{
			questId = _mainContentId;
		}
		return _questionProperties.get( _questionsById.get(questId) ).communicator.stringifyCurrentState();
	}
	
	private void updateStatus(YesNoQuestion quest, QuestionStatus status)
	{
		synchronized (_questionProperties)
		{
			_questionProperties.get(quest).status = status;
		}
	}
	
	public QuestionStatus getStatus(String questId)
	{
		YesNoQuestion quest = _questionsById.get(questId);
		synchronized (_questionProperties)
		{
			return _questionProperties.get(quest).status;
		}
	}
	
	public QuestionStatus getStatus(YesNoQuestion quest)
	{
		synchronized (_questionProperties)
		{
			return _questionProperties.get(quest).status;
		}
	}
	
	private class AnsweringThread extends Thread
	{
		private YesNoQuestion _quest;
		private Boolean _answer;
		private CommonData _callback;
		
		AnsweringThread(YesNoQuestion quest, CommonData callback)
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
}
