package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

@Interactive
public class Quest9b implements YesNoQuestion
{
	private static final long serialVersionUID = -2139911139722226035L;
	
	public static final String _id = "Quest9b";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private String _serverRoot;
	private transient ResourceBundle _labels;
	private long _htdocsLastModified;
	private boolean _lastUserAnswer;
	@SuppressWarnings("unused")		//maybe will be used later, having it here now so that it get's serialized
	private String _explanation = "";

	private String _commandPath;
	private String _command;

	public Quest9b(String serverRoot)
	{
		this(serverRoot, UserCommunicator.getDefault());
	}
	
	public Quest9b(String serverRoot, UserCommunicator uc)
	{
		this(serverRoot, "./", "listUsersWhoHaveAccess.sh", uc);
	}

	public Quest9b(String serverRoot, String commandPath, String command)
	{
		this(serverRoot, commandPath, command, UserCommunicator.getDefault());
	}
	
	public Quest9b(String serverRoot, String commandPath, String command, UserCommunicator uc)
	{
		_uc = uc;
		_serverRoot = serverRoot;
		_commandPath = commandPath;
		_command = command;
		_htdocsLastModified = -1;
		_lastUserAnswer = false;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( _labels.getString("Q0") );
		
		File htdocs = new File(_serverRoot + File.separator + "htdocs");
		
		if(!htdocs.exists())
		{
			_uc.printAnswer(true, _labels.getString("S1") );
			return true;
		}
		else
		{
			if(!htdocs.isDirectory())
			{
				_uc.printAnswer(false,  _labels.getString("S2") );
				return false;
			}
			_uc.println( _labels.getString("L1") );
			
			boolean isDoc = true;
			boolean access = true;
			if(htdocs.lastModified() > _htdocsLastModified || !_lastUserAnswer)
			{
				_htdocsLastModified = htdocs.lastModified();
				isDoc = _uc.askYesNoQuestion( _labels.getString("Q1") );
				listAccessUsers(htdocs);
				access = _uc.askYesNoQuestion( _labels.getString("Q2") );
				if(access)
				{
					_explanation = _uc.askTextQuestion( _labels.getString("REASON") );
				}
				
				_lastUserAnswer = isDoc && access;
			}
			_uc.printAnswer(_lastUserAnswer, _lastUserAnswer ?
									_labels.getString("S3") : _labels.getString("S4") );
			return _lastUserAnswer;
		}
	}

	private void listAccessUsers(File htdocs)
	{
		_uc.printParagraph( _labels.getString("S2_1") );
		ShellAnsweredQuestion script = new ShellAnsweredQuestion(_commandPath + _command, htdocs.getAbsolutePath());
		if(script.answer())
		{
			InputStream stdOut = script.getStdOut();
			StringBuilder sb = new StringBuilder();
			
			try
			{
				int c = stdOut.read();
				while(c >= 0)
				{
					sb = sb.append((char)c);
					c = stdOut.read();
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			
			_uc.printExample(sb.toString());
		}
		else
		{
			_uc.printExample( _labels.getString("EVERYBODY") );
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
