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
	private static final String PERMISSION_PATTERN = "...------";
	private static final String PERMISSION_PATTERN_LOW = "....--.--";
	private String _commandPath;
	private String _command;
	private String _serverRoot;
	private String _user;
	private ResourceBundle _labels;
	private boolean _highSec;

	public Quest9a(String serverRoot, String user, boolean highSec)
	{
		this(serverRoot, user, "./", "quest9.sh", highSec);
	}
	
	public Quest9a(String serverRoot, String user, String commandPath, String command, boolean highSec)
	{
		_serverRoot = serverRoot;
		_user = user;
		_commandPath = commandPath;
		_command = command;
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
		_highSec = highSec;
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		String problems = lookForProblems(PERMISSION_PATTERN);
		boolean ret = problems == null;
		
		if(!_highSec && !ret)
		{
			String problems_low = lookForProblems(PERMISSION_PATTERN_LOW);
			_uc.printAnswer(false, problems_low == null ? _labels.getString("S3")
					: MessageFormat.format( _labels.getString("S2"), _user));
		}
		else
		{
			_uc.printAnswer(ret, ret ? _labels.getString("S1")
					: MessageFormat.format( _labels.getString("S2"), _user));
		}
		
		if(!ret)
		{
			_uc.printExample(problems);
		}
		
		return ret;
	}
	
	private String lookForProblems(String permissionPattern)
	{
		ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command, _serverRoot, _user, "...------");
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
		
		if(ret)
		{
			return null;
		}
		else
		{
			return probBuf.toString();
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

	@Override
	public int getBlockNumber()
	{
		return 2147483647;
	}

	@Override
	public int getNumber()
	{
		return 2147483647;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}

}
