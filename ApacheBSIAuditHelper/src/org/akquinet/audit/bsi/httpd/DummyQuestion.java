package org.akquinet.audit.bsi.httpd;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;

public class DummyQuestion implements YesNoQuestion
{
	private static final String _id = "";	//DymmyQuestion has no ID
	private String _message;
	private FormattedConsole.OutputLevel _level;
	private static final FormattedConsole _console = FormattedConsole.getDefault();

	public DummyQuestion(String message)
	{
		this(message, FormattedConsole.OutputLevel.HEADING);
	}
	
	public DummyQuestion(String message, FormattedConsole.OutputLevel level)
	{
		_message = message;
		_level = level;
	}
	
	@Override
	public boolean answer()
	{
		_console.println(_level, _id, _message);
		return true;
	}

	@Override
	public boolean isCritical()
	{
		return false;
	}

	@Override
	public String getID()
	{
		return _id;
	}

}
