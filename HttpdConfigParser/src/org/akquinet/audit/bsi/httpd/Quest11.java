package org.akquinet.audit.bsi.httpd;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;

public class Quest11 implements YesNoQuestion
{
	private static final String _id = "Quest11";
	private ConfigFile _conf;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	
	private Quest11a _q11a;
	private Quest11b _q11b;
	
	public Quest11(ConfigFile conf)
	{
		_conf = conf;
		_q11a = new Quest11a();
		_q11b = new Quest11b(_conf);
	}

	/**
	 * Looking for AllowOverride-directives. Checking whether there is no with parameters other than None and at least one
	 * in global context with parameter None.
	 */
	@Override
	public boolean answer()
	{
		_console.printAnswer(_level, null, "Evaluating whether it may be possible to access files outside of the servers root directory.");
		boolean ret = _q11b.answer();
		if(!ret)
		{
			_console.println(_level, "There is also another way to block access to files outside of the servers root directory:");
			 ret = _q11a.answer();
		}
		
		_console.printAnswer(_level, ret, ret ?
					"Access to files outside of the servers root directory is correctly blocked."
					: "In some way it may be possible to access files outside of the servers root directory.");
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
