package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

@Automated
public class Quest12b implements YesNoQuestion
{
	private static final long serialVersionUID = 3583119563799878098L;
	
	private static final String _id = "Quest12b";
	private ConfigFile _conf;
	private transient ResourceBundle _labels;
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private static String _command;
	private static String _commandPath;

	public Quest12b(ConfigFile conf)
	{
		this(conf, "./", "quest12.sh");
	}
	
	public Quest12b(ConfigFile conf, UserCommunicator uc)
	{
		this(conf, "./", "quest12.sh", UserCommunicator.getDefault());
	}
	
	public Quest12b(ConfigFile conf, String commandPath, String command)
	{
		this(conf, commandPath, command, UserCommunicator.getDefault());
	}
	
	public Quest12b(ConfigFile conf, String commandPath, String command, UserCommunicator uc)
	{
		_uc = uc;
		_conf = conf;
		_commandPath = commandPath;
		_command = command;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		_uc.printExample( _labels.getString("S0") );

		
		_uc.println( _labels.getString("L1") );
		
		String user = null;
		String group = null;
		if(_conf.getDirective("User").size() == 0 || _conf.getDirective("Group").size() == 0)
		{
			_uc.printAnswer(false,  _labels.getString("S2") );
			return false;
		}
		
		user = _conf.getDirective("User").get(0).getValue().trim();
		group = _conf.getDirective("Group").get(0).getValue().trim();
		
		_uc.println( MessageFormat.format( _labels.getString("L2") , user, group) );
		
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
		
		String[] shellOut = buf.toString().split("[ |\t]");
		if(shellOut.length == 1 && ! buf.toString().equals(""))
		{
			_uc.printAnswer(ret, _labels.getString(shellOut[0]) );
		}
		else if(shellOut.length > 1)
		{
			String[] argArr = new String[shellOut.length - 1];
			for(int i = 1; i < shellOut.length; ++i)
			{
				argArr[i-1] = shellOut[i].replaceAll("\n", "");
			}
			String tmp = MessageFormat.format(_labels.getString(shellOut[0]), (Object[])argArr);
			_uc.printAnswer(ret, tmp );
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
	public void initialize() throws Exception
	{
		_conf.reparse();
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
