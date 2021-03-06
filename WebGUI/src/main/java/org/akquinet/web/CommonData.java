package org.akquinet.web;

import java.util.Random;
import java.util.logging.Logger;

public class CommonData
{
	public static final String ACTION_RESTART_QUESTION = "restartQuestion";
	public static final String ACTION_RESTART_ALL_QUESTIONS = "restartAllQuestions";
	public static final String ACTION_SETTINGS = "settings";
	public static final String ACTION_ANSWER = "answer";
	public static final String ACTION_IS_STALE = "isStale";
	public static final String ACTION_IS_AVAILABLE = "isAvailable";
	public static final String ACTION_QUEST_STATUS = "questStatus";

	public static final String MAIN_SERVLET_URL = "inspector.jsp";
	public static final String LOGIN_SERVLET_URL = "login.jsp";
	public static final String REPORT_SERVLET_URL = "report.jsp";
	public static final String ZIPSTATE_URL = "QuestionData.zip";
	public static final String UPLOADSTATE_URL = "UploadQuestions";

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

	public static String CochiseDataPath = "../cochise-data/";
	
	public enum QuestionStatus
	{
		GOOD, BAD, OPEN
	}

	private static CommonData _default;
	
	private static Logger _logger = Logger.getAnonymousLogger();

	private CommonData()
	{
	}
	
	public static Logger getLogger()
	{
		return _logger;
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
}
