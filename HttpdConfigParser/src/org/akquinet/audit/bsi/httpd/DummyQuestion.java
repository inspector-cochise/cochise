package org.akquinet.audit.bsi.httpd;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;

public class DummyQuestion implements YesNoQuestion
{
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
		_console.println(_level, _message);
		return true;
	}

	@Override
	public boolean isCritical()
	{
		return false;
	}

}
