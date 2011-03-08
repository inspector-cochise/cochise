package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;

public class Quest12a implements YesNoQuestion
{
	private static final String _id = "Quest12a";
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q2;
	private String _commandPath;
	private String _command;

	public Quest12a()
	{
		this("./", "quest12.sh");
	}

	public Quest12a(String commandPath, String command)
	{
		_commandPath = commandPath;
		_command = command;
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, "----" + _id + "----");
		_console.println(_level, "One can customize the startup process of the apache deamon to start it as a special user with low rights.");
		boolean ret = _console.askYesNoQuestion(_level, "Have you done so?");
		if(!ret)
		{
			_console.printAnswer(_level, false, "So this way can't work.");
		}
		
		String user = _console.askStringQuestion(_level, "What is the name of that special user? ");
		if(user.equals("root"))
		{
			_console.printAnswer(_level, false, "root has way too much permissions to restrict apache's actions.");
			return false;
		}
		
		ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command, user);
		ret = quest.answer();
		
		StringBuffer buf = new StringBuffer();
		InputStream stdOut = quest.getStdOut();
		try
		{
			int b = stdOut.read();
			while(b != -1)
			{
				buf.append((char) b);
				b = stdOut.read();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		_console.printAnswer(_level, ret, buf.toString());
		
		
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
