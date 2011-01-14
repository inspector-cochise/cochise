package org.akquinet.audit.bsi.httpd;

import java.util.List;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest3 implements YesNoQuestion
{
	private ConfigFile _conf;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	
	public Quest3(ConfigFile conf)
	{
		_conf = conf;
	}

	/**
	 * checks whether there are any Include-directives in the config file
	 */
	@Override
	public boolean answer()
	{
		List<Directive> loadList = _conf.getDirectiveIgnoreCase("LoadModule");
		for (Directive directive : loadList)
		{
			String[] arguments = directive.getValue().trim().split("( |\t)*");
			if(arguments == null || arguments.length < 2)
			{
				continue;
			}
			
			if(arguments[0].equals("security2_module"))
			{
				Directive modSec = directive;
				_console.printAnswer(_level, true, "ModSecurity is being loaded:");
				_console.println(_level, modSec.getLinenumber() + ": " + modSec.getName() + " " + modSec.getValue());
				return true;
			}
			
		}
		
		_console.printAnswer(_level, false, "ModSecurity seems not to be loaded.");
		return false;
	}

	@Override
	public boolean isCritical()
	{
		return false;
	}
}
