package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

@Automated
public class Quest12a implements YesNoQuestion
{
	private static final long serialVersionUID = -1132313583118521051L;
	
	private static final String _id = "Quest12a";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private String _commandPath;
	private String _command;
	private String _getUserNGroupCommand;
	private String _apacheExecutable;
	private transient ResourceBundle _labels;

	public Quest12a(String apacheExecutable)
	{
		this("./", "quest12.sh", "getApacheStartingUser.sh", apacheExecutable);
	}
	
	public Quest12a(String apacheExecutable, UserCommunicator uc)
	{
		this("./", "quest12.sh", "getApacheStartingUser.sh", apacheExecutable, uc);
	}
	
	public Quest12a(String commandPath, String command, String getUserNGroupCommand, String apacheExecutable)
	{
		this(commandPath, command, getUserNGroupCommand, apacheExecutable, UserCommunicator.getDefault());
	}

	public Quest12a(String commandPath, String command, String getUserNGroupCommand, String apacheExecutable, UserCommunicator uc)
	{
		_uc = uc;
		_commandPath = commandPath;
		_command = command;
		_getUserNGroupCommand = getUserNGroupCommand;
		_apacheExecutable = apacheExecutable;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		return answer(_uc);
	}
	
	public boolean answer(UserCommunicator uc)
	{
		uc.printHeading3( _labels.getString("name") );
		uc.printParagraph( _labels.getString("Q0") );
		
		uc.printParagraph( _labels.getString("P1") );
		
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
			throw new RuntimeException(e);
		}

		uc.println( MessageFormat.format(_labels.getString("L1"), user, group) );
		uc.println( _labels.getString("L2") );
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
			throw new RuntimeException(e);
		}
		
		String[] shellOut = buf.toString().split("[ |\t|\n|\r\n]");
		if(shellOut.length == 1 && ! buf.toString().equals(""))
		{
			uc.printAnswer(ret, _labels.getString(shellOut[0]) );
		}
		else if(shellOut.length > 1)
		{
			String[] argArr = new String[shellOut.length - 1];
			for(int i = 1; i < shellOut.length; ++i)
			{
				argArr[i-1] = shellOut[i];
			}
			String tmp = MessageFormat.format(_labels.getString(shellOut[0]), (Object[])argArr);
			uc.printAnswer(ret, tmp );
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
	
	@Override
	public void initialize()
	{
		//nothing to do here
	}
	
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		_uc = UserCommunicator.getDefault();
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}
	
	@Override
	public String getName()
	{
		return _labels.getString("name");
	}

	@Override
	public void setUserCommunicator(UserCommunicator uc)
	{
		_uc = uc;
	}
}
