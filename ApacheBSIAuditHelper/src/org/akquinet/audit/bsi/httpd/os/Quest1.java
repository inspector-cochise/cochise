package org.akquinet.audit.bsi.httpd.os;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;

public class Quest1 implements YesNoQuestion
{
	private static final String _id = "Quest1";
	private boolean _highSecReq;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	private String _commandPath;
	private String _command;

	public Quest1(boolean highSecurityRequired)
	{
		this(highSecurityRequired, "./", "quest1.sh");
	}
	
	public Quest1(boolean highSecurityRequired, String commandPath, String command)
	{
		_highSecReq = highSecurityRequired;
		_commandPath = commandPath;
		_command = command;
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, _id, "----" + _id + "----");
		if(_highSecReq)
		{
			ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command);
			boolean ret = quest.answer();
			
			_console.printAnswer(_level, _id, ret, ret ? "User root is correctly locked."
								: "Please lock user root (\"passwd -l\"). After that you can't directly log yourself in as root.");
			
			return ret;
		}
		else
		{
			_console.printAnswer(_level, _id, null, "skipping question... (no high level of security requested))");
			return true;
		}
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
