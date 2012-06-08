package org.akquinet.audit.ui;

import java.util.Locale;

public abstract class UserCommunicator
{
	private static UserCommunicator _default = null;
	
	abstract public void reportError(String error);
	abstract public void reportError(Exception error);

	abstract public void printHeading1(String heading);
	abstract public void printHeading2(String heading);
	abstract public void printHeading3(String heading);
	abstract public void printParagraph(String text);
	abstract public void println(String text);
	abstract public void printExample(String example);
	abstract public void printAnswer(boolean answer, String cause);
	abstract public void printHidingParagraph(String shortDescription, String expandedText);

	abstract public void beginHidingParagraph(String hiddenText);
	abstract public void endHidingParagraph();
	abstract public void beginIndent();
	abstract public void endIndent();
	
	
	abstract public boolean askYesNoQuestion(String question);
	abstract public boolean askYesNoQuestion(String question, Boolean defaultAnswer);
	abstract public String askStringQuestion(String question);
	abstract public String askStringQuestion(String question, String defaultAnswer);
	
	/**
	 * for bigger answers
	 * @param question
	 * @return
	 */
	abstract public String askTextQuestion(String question);
	
	public void finishCommunication() {}

	public static UserCommunicator getDefault()
	{
		return _default;
	}
	
	public static void setDefault(UserCommunicator def) throws Exception
	{
		if(_default == null)
		{
			_default = def;
		}
		else
		{
			throw new Exception("Default value already set.");
		}
	}
	
	public void waitForUserToContinue() {}
	public void setIgnore_WaitForUserToContinue(boolean b) {}

	public void markReport() {}
	public void resetReport() {}
	
	abstract public Locale getLocale();
	abstract public void setLocale(Locale locale);
}
