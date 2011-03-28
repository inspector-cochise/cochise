package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;

import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest9a implements YesNoQuestion
{
	private static final String _id = "Quest9a";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _commandPath;
	private String _command;
	private String _serverRoot;
	private String _user;

	public Quest9a(String serverRoot, String user)
	{
		this(serverRoot, user, "./", "quest9.sh");
	}
	
	public Quest9a(String serverRoot, String user, String commandPath, String command)
	{
		_serverRoot = serverRoot;
		_user = user;
		_commandPath = commandPath;
		_command = command;
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command, _serverRoot, _user);
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
		
		_uc.printAnswer(ret, ret ? "Your ServerRoot (not speaking about a possible htdocs file/directory) seems to be ok."
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
