package org.akquinet.web;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import org.akquinet.audit.ui.DelayedHtmlUserCommunicator.InputState;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

public class CommonData
{
	private static final String LOADER_ANIMATION = "<div class=\"loader\"></div>";
	
	public static final String ACTION_RESTART_QUESTION = "restartQuestion";
	public static final String ACTION_RESTART_ALL_QUESTIONS = "restartAllQuestions";
	public static final String ACTION_SETTINGS = "settings";
	public static final String ACTION_ANSWER = "answer";

	public static final String MAIN_SERVLET_URL = "inspector.jsp";
	public static final String LOGIN_SERVLET_URL = "login.jsp";
	public static final String REPORT_SERVLET_URL = "report.jsp";

	public static final String PARAM_REQUESTED_QUEST = "quest";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_APACHE_EXECUTABLE = "apacheExec";
	public static final String PARAM_APACHE_CONFIG = "apacheConfig";
	public static final String PARAM_HIGH_SECURITY = "highSec";
	public static final String PARAM_HIGH_PRIVACY = "highPriv";
	public static final String PARAM_ANSWER = "answer";

	public static final String PROLOGUE_ID = "settings";
	public static final String PROLOGUE_OK = "prologueOk";

	public static final long RUN_ID = (new Random(System.nanoTime())).nextInt();

	public enum QuestionStatus
	{
		GOOD, BAD, OPEN
	}

	private static CommonData _default;

	private String _mainContentId;
	private PrologueData _prologueData;
	private HashMap<String, YesNoQuestion> _questionsById;
	private HashMap<YesNoQuestion, YesNoQuestionProperties> _questionProperties;
	private boolean _configured = false;

	private CommonData()
	{
		_questionProperties = new HashMap<YesNoQuestion, YesNoQuestionProperties>();
		_questionsById = new HashMap<String, YesNoQuestion>();
		_mainContentId = PROLOGUE_ID;
	}

	public static CommonData getDefault()
	{
		if (_default == null)
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

	public List<String> getQuestionIds()
	{
		ArrayList<String> ret = new ArrayList<String>(_questionsById.keySet());
		Collections.sort(ret, new Comparator<String>()
		{
			@Override
			public int compare(String s1, String s2)
			{
				String n1 = s1.replaceAll("\\P{Digit}", "");
				String n2 = s2.replaceAll("\\P{Digit}", "");
				try
				{
					return (new Integer(n1)).compareTo(Integer.parseInt(n2));
				}
				catch (Exception e)
				{
					return s1.compareTo(s2);
				}
			}
		});
		return ret;
	}

	public YesNoQuestion getQuestion(String questId)
	{
		return _questionsById.get(questId);
	}

	public void updateMainContentId(HttpServletRequest request)
	{
		_mainContentId = request.getParameter(PARAM_REQUESTED_QUEST) != null ? request.getParameter(PARAM_REQUESTED_QUEST) : _mainContentId;
	}

	public void triggerActions(HttpServletRequest request)
	{
		updateMainContentId(request);

		String action = request.getParameter(PARAM_ACTION);
		if (_prologueData == null)
		{
			_prologueData = (new SettingsHelper())._prologueData;
			_configured = false;
		}

		if (action != null)
		{
			if (action.equals(ACTION_SETTINGS) || _mainContentId.equals(PROLOGUE_ID))
			{
				initPrologueData(request);

				SettingsHelper helper = new SettingsHelper(_prologueData);

				String execErrorMsg = helper.getExecErrorMsg();
				String configErrorMsg = helper.getConfigErrorMsg();

				_prologueData = helper._prologueData;

				if (execErrorMsg.equals("") && configErrorMsg.equals("") && "root".equals(System.getenv("USER")))
				{
					removeQuestions();
					addQuestions();
					_configured = true;
				}
			}
			else if (action.equals(ACTION_RESTART_QUESTION))
			{
				Class<? extends YesNoQuestion> questClass = _questionsById.get(_mainContentId).getClass();
				removeQuestion(_mainContentId);
				try
				{
					createQuestion(questClass);
				}
				catch (InvalidQuestionClassException e)
				{
					throw new RuntimeException(e);
				}
			}
			else if (action.equals(ACTION_RESTART_ALL_QUESTIONS))
			{
				_mainContentId = PROLOGUE_ID;
				removeQuestions();
				addQuestions();
			}
			else if (action.equals(ACTION_ANSWER))
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
						// we just waited to give the AskingThread some time to
						// call updateStatus, it's not so important...
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
				// unknown action, just ignore this
			}
		}
	}

	private void initPrologueData(HttpServletRequest request)
	{
		String apacheExec = request.getParameter(PARAM_APACHE_EXECUTABLE);
		if (apacheExec == null)
		{
			apacheExec = _prologueData._apacheExec;
		}
		File apacheExecutable = new File(apacheExec);

		String apacheConf = request.getParameter(PARAM_APACHE_CONFIG);
		if (apacheConf == null)
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
			// we will do the checks again later in settings.jsp
			_prologueData = new PrologueData(apacheExec, apacheConf, apacheExecutable, null, configFile, highSec, highPriv);
		}
	}

	public void addQuestions()
	{
		if (!_questionProperties.isEmpty())
		{
			return;
		}

		try
		{
			createQuestion(Quest1.class);
			createQuestion(Quest2.class);
			createQuestion(Quest3.class);
			createQuestion(Quest4.class);
			createQuestion(Quest5.class);
			createQuestion(Quest6.class);
			createQuestion(Quest7.class);
			createQuestion(Quest8.class);
			createQuestion(Quest9.class);
			createQuestion(Quest10.class);
			createQuestion(Quest11.class);
			createQuestion(Quest12.class);
		}
		catch (InvalidQuestionClassException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void removeQuestions()
	{
		HashSet<String> questIds = new HashSet<String>(_questionsById.keySet());
		for(String questId : questIds)
		{
			removeQuestion(questId);
		}
	}

	/**
	 * 
	 * @param questId
	 *            The id of the requested question. Can be null.
	 * @return The (HTML-)stringified dialogue-status of the requested question.
	 *         If questId is null the result will be the dialogue-status of the
	 *         last requested question.
	 */
	public String getQuestionsOutput(String questId)
	{
		if (questId == null)
		{
			questId = _mainContentId;
		}
		YesNoQuestionProperties questProps = _questionProperties.get(_questionsById.get(questId));
		StringBuilder questOutput = new StringBuilder(questProps.communicator.stringifyCurrentState());

		if (questProps.askingThread.isAlive() && questProps.communicator.getInputState() == InputState.NO_INPUT_EXPECTED)
		{
			questOutput.append(LOADER_ANIMATION);
		}
		return questOutput.toString();
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

	/**
	 * Creates an instance of the parameters question type, adds it to _questionProperties, _questionsById, suites it with a DelayedUserCommunicator and starts
	 * to ask it in a new thread.
	 * @param questClass A question type. It mustn't be abstract (obviously) and must have a public constructor with 2 parameters, the first of type PrologueData and the second of type UserCommunicator.
	 * @throws InvalidQuestionClassException If the parameter doesn't match one of the restrictions explained above.
	 */
	private void createQuestion(Class<? extends YesNoQuestion> questClass) throws InvalidQuestionClassException
	{
		try
		{
			Constructor<? extends YesNoQuestion> ctr = questClass.getConstructor(PrologueData.class, UserCommunicator.class);
			
			DelayedHtmlUserCommunicator comm = new DelayedHtmlUserCommunicator(MAIN_SERVLET_URL);
			YesNoQuestion quest = ctr.newInstance(_prologueData, comm);
			comm.setQuestId(quest.getID());
			
			_questionProperties.put(quest, new YesNoQuestionProperties(quest, comm, QuestionStatus.OPEN, new AnsweringThread(quest, this)));
			_questionsById.put(quest.getID(), quest);
			_questionProperties.get(quest).askingThread.start();
		}
		catch (NoSuchMethodException e)		//if no constructor with Parameters PrologueData, UserCommunicator exists
		{
			throw new InvalidQuestionClassException(e);
		}
		catch (InstantiationException e)	//if the class is abstract
		{
			throw new InvalidQuestionClassException(e);
		}
		catch (IllegalAccessException e)	//if the constructor is inaccessible
		{
			throw new InvalidQuestionClassException(e);
		}
		catch (InvocationTargetException e)	//if the constructor throws an exception
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Removes the question from _questionsById and _questionProperties. Also (gracefully) stops the AskingThread. This may takes a while.
	 * @param questId
	 */
	private void removeQuestion(String questId)
	{
		YesNoQuestion quest = _questionsById.get(questId);
		if(quest == null)
		{
			return;
		}
		
		_questionsById.remove(questId);
		YesNoQuestionProperties props = _questionProperties.get(quest);
		while(props.askingThread.isAlive())
		{
			props.askingThread.interrupt();
		}
		_questionProperties.remove(quest);
	}
	
	private class InvalidQuestionClassException extends Exception
	{
		private static final long serialVersionUID = 8834208485623397706L;

		public InvalidQuestionClassException(Throwable cause)
		{
			super(cause);
		}
	}

	public boolean allGood()
	{
		for(YesNoQuestionProperties props : _questionProperties.values())
		{
			if(props.status != QuestionStatus.GOOD)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public List<YesNoQuestion> getProblems()
	{
		List<YesNoQuestion> ret = new LinkedList<YesNoQuestion>();
		
		for(String questId : getQuestionIds())
		{
			YesNoQuestionProperties prop = _questionProperties.get(_questionsById.get(questId));
			if( prop.status != QuestionStatus.GOOD)
			{
				ret.add(prop.quest);
			}
		}
		
		return ret;
	}
}
