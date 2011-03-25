package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest9 implements YesNoQuestion
{
	private static final String _id = "Quest9";
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	private ConfigFile _conf;
	private String _serverRoot;
	private String _commandPath;
	private String _command9a;
	private String _getUserNGroupCommand;
	private String _apacheExecutable;
	private Quest9a _q9a;
	private Quest9b _q9b;

	public Quest9(ConfigFile conf, String apacheExecutable)
	{
		this(conf, "./", "quest9.sh", "getApacheStartingUser.sh", apacheExecutable);
	}
	
	public Quest9(ConfigFile conf, String command9aPath, String command9a, String getUserNGroupCommand, String apacheExecutable)
	{
		_conf = conf;
		_serverRoot = getServerRoot();
		_commandPath = command9aPath;
		_command9a = command9a;
		_getUserNGroupCommand = getUserNGroupCommand;
		_apacheExecutable = apacheExecutable;
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, _id, "vvvv" + _id + "vvvv");

		_console.printAnswer(_level, _id, null, "We'll now start to examine the permissions in your ServerRoot.");
		if(_serverRoot != null)
		{
			String user = getApacheStartingUser();
			_console.println(_level, _id, "Seems like your apache is started by " + user + "...");
			
			if(_command9a != null && _commandPath  != null)
			{
				_q9a = new Quest9a(_serverRoot, user, _commandPath, _command9a);
			}
			else
			{
				_q9a = new Quest9a(_serverRoot, user);
			}
			_q9b = new Quest9b(_serverRoot);
			
			boolean ret = _q9a.answer();
			ret &= _q9b.answer();
			_console.println(FormattedConsole.OutputLevel.HEADING, _id, "^^^^" + _id + "^^^^");
			_console.printAnswer(_level, _id, ret, ret ? "Your ServerRoot seems ok." : "Seems like your ServerRoot is unsafe in the way mentioned above.");
			return ret;
		}
		else
		{
			_console.println(FormattedConsole.OutputLevel.HEADING, _id, "^^^^" + _id + "^^^^");
			_console.printAnswer(_level, _id, false, "Either none or multiple ServerRoot directives found. There has to be exactly one.");
			return false;
		}
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
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

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

}
