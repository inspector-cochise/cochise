package org.akquinet.audit.bsi.httpd.usersNrights;

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
	private Quest9a _q9a;
	private Quest9b _q9b;

	public Quest9(ConfigFile conf)
	{
		_conf = conf;
		_serverRoot = getServerRoot();
	}

	@Override
	public boolean answer()
	{
		//TODO is this everything?
		_console.println(FormattedConsole.OutputLevel.HEADING, "vvvv" + _id + "vvvv");

		_console.printAnswer(_level, null, "We'll now start to examine the permissions in your ServerRoot.");
		String user = _console.askStringQuestion(_level, "Which user starts the apache web server? [root]");
		if(user.equals(""))
		{
			user = "root";
		}
		
		_q9a = new Quest9a(_serverRoot, user);
		_q9b = new Quest9b(_serverRoot);
		
		boolean ret = _q9a.answer();
		ret &= _q9b.answer();
		_console.println(FormattedConsole.OutputLevel.HEADING, "^^^^" + _id + "^^^^");
		_console.printAnswer(_level, ret, ret ? "Your ServerRoot seems ok." : "Seems like your ServerRoot is unsafe in the way mentioned above.");
		
		return ret;
	}

	private String getServerRoot()
	{
		//TODO handle problems like multiple ServerRoot directives or ServerRoot not in global context
		List<Directive> srvRootList = _conf.getDirective("ServerRoot");
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
