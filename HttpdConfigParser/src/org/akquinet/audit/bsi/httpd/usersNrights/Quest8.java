package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;

public class Quest8 implements YesNoQuestion
{
	private static final String _id = "Quest8";
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	private String _command;
	private String _commandPath;
	private File _configFile;

	public Quest8(File configFile)
	{
		this(configFile, "./", "QfileSafe.sh");
	}

	public Quest8(File configFile, String commandPath, String command)
	{
		_configFile = configFile;
		_commandPath = commandPath;
		_command = command;
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, "----" + _id + "----");
		ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command, _configFile.getAbsolutePath());
		boolean ret = quest.answer();
		
		_console.printAnswer(_level, ret, ret ? "Your main configuration file seems to be safe."
							: "Users not in the group \"root\" may have some access to your configuration file. They should get no rights (no read, no write, no execute)");
		
		return ret;
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
