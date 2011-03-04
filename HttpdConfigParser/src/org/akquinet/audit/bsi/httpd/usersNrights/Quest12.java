package org.akquinet.audit.bsi.httpd.usersNrights;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;

public class Quest12 implements YesNoQuestion
{
	private static final String _id = "Quest12";
	private ConfigFile _conf;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	
	private Quest12a _q12a;
	private Quest12b _q12b;
	
	public Quest12(ConfigFile conf)
	{
		_conf = conf;
		_q12a = new Quest12a();
		_q12b = new Quest12b(_conf);
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, "vvvv" + _id + "vvvv");
		_console.printAnswer(_level, null, "Ensuring tha the apache web server is running with few permissions.");
		boolean ret = _q12b.answer();
		if(!ret)
		{
			_console.println(_level, "There is also another way to ensure few permissions:");
			 ret = _q12a.answer();
		}
		
		_console.println(FormattedConsole.OutputLevel.HEADING, "^^^^" + _id + "^^^^");
		_console.printAnswer(_level, ret, ret ?
					"The apache web server is correctly running with few permissions."
					: "The apache web server is maybe running with too many permissions.");
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