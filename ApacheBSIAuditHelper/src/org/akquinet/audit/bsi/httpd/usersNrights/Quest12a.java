package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;

import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest12a implements YesNoQuestion
{
	private static final String _id = "Quest12a";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _commandPath;
	private String _command;
	private String _getUserNGroupCommand;
	private String _apacheExecutable;

	public Quest12a(String apacheExecutable)
	{
		this("./", "quest12.sh", "getApacheStartingUser.sh", apacheExecutable);
	}

	public Quest12a(String commandPath, String command, String getUserNGroupCommand, String apacheExecutable)
	{
		_commandPath = commandPath;
		_command = command;
		_getUserNGroupCommand = getUserNGroupCommand;
		_apacheExecutable = apacheExecutable;
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.println("One can customize the startup process of the apache deamon to start it as a special user with low rights.");
		
		String user = null;
		String group = null;
		try
		{
			Process p = (new ProcessBuilder(_commandPath + _getUserNGroupCommand, _apacheExecutable)).start();
			InputStream is = p.getInputStream();
			StringBuffer buf = new StringBuffer();
			int b = is.read();
			while(b != -1)
			{
				buf.append((char)b);
				b = is.read();
			}
			String[] tmp = buf.toString().split("[ \t]+");
			user = tmp[0];
			group = tmp[1];
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}

		ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command, user, group);
		boolean ret = quest.answer();
		
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
		
		_uc.printAnswer(ret, buf.toString());
		
		
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
