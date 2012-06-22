package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.util.FileWatcher;
import org.akquinet.util.ResourceWatcher;

@Interactive
public class Quest9 extends ResourceWatcher implements YesNoQuestion
{
	private static final long serialVersionUID = -4428911855077358238L;
	
	private static final String _id = "Quest9";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private ConfigFile _conf;
	private String _serverRoot = null;
	private String _commandPath;
	private String _command9a;
	private String _command9b;
	private String _getUserNGroupCommand;
	private String _apacheExecutable;
	private Quest9a _q9a;
	private Quest9b _q9b;
	private transient ResourceBundle _labels;
	private boolean _highSec;
	private String _user;
	private FileWatcher _srvRootWatcher = null;
	private boolean _resourceChanged = false;
	private boolean _firstRun = true;


	public Quest9(PrologueData pd)
	{
		this(pd._conf, pd._apacheExec, pd._highSec);
	}
	
	public Quest9(PrologueData pd, UserCommunicator uc)
	{
		this(pd._conf, pd._apacheExec, pd._highSec, uc);
	}
	
	public Quest9(ConfigFile conf, String apacheExecutable, boolean highSec)
	{
		this(conf, "./", "quest9.sh", "listUsersWhoHaveAccess.sh", "getApacheStartingUser.sh", apacheExecutable, highSec);
	}
	
	public Quest9(ConfigFile conf, String apacheExecutable, boolean highSec, UserCommunicator uc)
	{
		this(conf, "./", "quest9.sh", "listUsersWhoHaveAccess.sh", "getApacheStartingUser.sh", apacheExecutable, highSec, uc);
	}

	public Quest9(ConfigFile conf, String command9aPath, String command9a, String command9b, String getUserNGroupCommand, String apacheExecutable, boolean highSec)
	{
		this(conf, command9aPath, command9a, command9b, getUserNGroupCommand, apacheExecutable, highSec, UserCommunicator.getDefault());
	}
	
	public Quest9(ConfigFile conf, String command9aPath, String command9a, String command9b, String getUserNGroupCommand, String apacheExecutable, boolean highSec, UserCommunicator uc)
	{
		_uc = uc;
		_conf = conf;
		_commandPath = command9aPath;
		_command9a = command9a;
		_command9b = command9b;
		_getUserNGroupCommand = getUserNGroupCommand;
		_apacheExecutable = apacheExecutable;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_highSec = highSec;
	}

	@Override
	public boolean answer()
	{
		_firstRun = false;
		_resourceChanged = false;
		if(_srvRootWatcher != null)
		{
			_srvRootWatcher.resourceChanged();
		}
		
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( _labels.getString("Q0") );
		
		_uc.beginHidingParagraph( _labels.getString("S5") );
			_uc.println( _labels.getString("S6") );
			_uc.printExample("ServerRoot /path/to/some/directory/");
			_uc.printParagraph( _labels.getString("S7") );
		_uc.endHidingParagraph();

		_uc.println( _labels.getString("L1") );
		
		if(_serverRoot == null || _serverRoot == "")
		{
			_uc.printAnswer(false, _labels.getString("E1") );
			return false;
		}

		_uc.printExample(_serverRoot);
		
		boolean ret = false;
		if(_serverRoot != null)
		{
			_uc.println( MessageFormat.format( _labels.getString("L2") , _user));
			
			_uc.beginIndent();
				ret = _q9a.answer();
				ret &= _q9b.answer();
			_uc.endIndent();
			_uc.println( MessageFormat.format(_labels.getString("S1"), _labels.getString("name")));
			_uc.printAnswer(ret, ret ?  _labels.getString("S2")  :  _labels.getString("S3") );
			
		}
		else
		{
			_uc.println( MessageFormat.format(_labels.getString("S1"), _labels.getString("name")));
			_uc.printAnswer(false,  _labels.getString("S4") );
			ret = false;
		}
		
		return ret;
	}

	private String getApacheStartingUser()
	{
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
			return buf.toString().split("[ \t]+")[0];
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieves the ServerRoot. It doesn't use the parser's methods to directly get it because we explicitly want this to
	 * be mentioned by a valid directive. The parser's direct method would also support implicit ServerRoot (i.e. the
	 * value compiled into the apache binary).
	 * @return
	 */
	private String getServerRoot()
	{
		List<Directive> srvRootList = _conf.getDirective("ServerRoot");
		if(srvRootList.size() != 1)
		{
			return null;
		}
		
		String ret = srvRootList.get(0).getValue().trim();
		if(ret.startsWith("\"") && ret.endsWith("\"") && ret.length() > 1)
		{
			ret = ret.substring(1);
			ret = ret.substring(0, ret.length()-1);
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
		return 3;
	}

	@Override
	public int getNumber()
	{
		return 9;
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
		
		String serverRoot = getServerRoot();
		if(serverRoot != null && !serverRoot.equals(_serverRoot))
		{
			_serverRoot = serverRoot;
			_srvRootWatcher = new FileWatcher(_serverRoot);
			_resourceChanged = true;
		}
		
		_user = getApacheStartingUser();
		if(_command9a != null && _commandPath  != null)
		{
			_q9a = new Quest9a(_serverRoot, _user, _commandPath, _command9a, _highSec, _uc);
		}
		else
		{
			_q9a = new Quest9a(_serverRoot, _user, _highSec, _uc);
		}
		
		if(_command9b != null && _commandPath != null)
		{
			_q9b = new Quest9b(_serverRoot, _commandPath, _command9b, _uc);
		}
		else
		{
			_q9b = new Quest9b(_serverRoot, _uc);
		}
		
		_q9a.initialize();
		_q9b.initialize();
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

	@Override
	public String getResourceId()
	{
		return "quest." + _id;
	}

	@Override
	public boolean resourceChanged()
	{
		if(_firstRun)
		{
			return false;
		}
		else
		{
			boolean ret = _srvRootWatcher.resourceChanged() || _resourceChanged;
			_resourceChanged = false;
			return ret;
		}
	}
}
