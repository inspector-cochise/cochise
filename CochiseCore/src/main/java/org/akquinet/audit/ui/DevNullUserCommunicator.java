package org.akquinet.audit.ui;

import java.util.Locale;

public class DevNullUserCommunicator extends UserCommunicator
{
	private Locale _locale = Locale.getDefault();
	private boolean _defaultBooleanAnswer;
	private String _defaultStringAnswer;
	
	public DevNullUserCommunicator()
	{
		this(false, "");
	}

	public DevNullUserCommunicator(boolean defaultBooleanAnswer, String defaultStringAnswer)
	{
		_defaultBooleanAnswer = defaultBooleanAnswer;
		_defaultStringAnswer = defaultStringAnswer;
	}

	@Override
	public void reportError(String error)
	{
	}

	@Override
	public void reportError(Exception error)
	{
	}

	@Override
	public void printHeading1(String heading)
	{
	}

	@Override
	public void printHeading2(String heading)
	{
	}

	@Override
	public void printHeading3(String heading)
	{
	}

	@Override
	public void printParagraph(String text)
	{
	}

	@Override
	public void println(String text)
	{
	}

	@Override
	public void printExample(String example)
	{
	}

	@Override
	public void printAnswer(boolean answer, String cause)
	{
	}

	@Override
	public void printHidingParagraph(String shortDescription, String expandedText)
	{
	}

	@Override
	public void beginHidingParagraph(String hiddenText)
	{
	}

	@Override
	public void endHidingParagraph()
	{
	}

	@Override
	public void beginIndent()
	{
	}

	@Override
	public void endIndent()
	{
	}

	@Override
	public boolean askYesNoQuestion(String question)
	{
		return _defaultBooleanAnswer;
	}

	@Override
	public boolean askYesNoQuestion(String question, Boolean defaultAnswer)
	{
		return defaultAnswer;
	}

	@Override
	public String askStringQuestion(String question)
	{
		return _defaultStringAnswer;
	}

	@Override
	public String askStringQuestion(String question, String defaultAnswer)
	{
		return defaultAnswer;
	}

	@Override
	public String askTextQuestion(String question)
	{
		return _defaultStringAnswer;
	}

	@Override
	public Locale getLocale()
	{
		return _locale;
	}

	@Override
	public void setLocale(Locale locale)
	{
		_locale = locale;
	}

}
