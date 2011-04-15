package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

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
	private ResourceBundle _labels;

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
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
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
			throw new RuntimeException(e);
		}
		
		String problems = probBuf.toString();
		
		_uc.printAnswer(ret, ret ? _labels.getString("S1")
							: MessageFormat.format( _labels.getString("S2"), _user));
		if(!ret)
		{
			_uc.printExample(problems);
		}
		
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
