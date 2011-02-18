package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;

public class Quest9a implements YesNoQuestion
{
	private static final String _id = "Quest9a";
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q2;
	private static final String _command = "./quest9.sh";
	private String _serverRoot;
	private String _user;

	public Quest9a(String serverRoot, String user)
	{
		_serverRoot = serverRoot;
		_user = user;
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, "----" + _id + "----");
		ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_command, _serverRoot, _user);
		boolean ret = quest.answer();
		StringBuffer probBuf = new StringBuffer();
		InputStream stdOut = quest.getStdOut();
		try
		{
			int b = stdOut.read();
			while(b != -1)
			{
				probBuf.append((char)b);
				b = stdOut.read();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		String problems = probBuf.toString();
		
		_console.printAnswer(_level, ret, ret ? "Your ServerRoot (not speaking about a possible htdocs file/directory) seems to be ok."
							: "Other users than " + _user + " have access to the following files/directories. Please change permissions." + problems);
		
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
