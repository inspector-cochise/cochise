package org.akquinet.audit.bsi.httpd;

import java.util.List;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.FormattedConsole.OutputLevel;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Frage5a extends YesNoQuestion
{
	private ConfigFile _conf;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q2;
	
	public Frage5a(ConfigFile conf)
	{
		_conf = conf;
	}

	/**
	 * checks whether there are any Include-directives in the config file
	 */
	@Override
	public boolean answer()
	{
		List<Directive> incList = _conf.getDirectiveIgnoreCase("Include");
		
		if(!incList.isEmpty())
		{
			_console.printAnswer(_level, false, "There are Include-directives in your apache configuration:");
			for (Directive directive : incList)
			{
				_console.println(_level, directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
			}
			return false;
		}
		else
		{
			_console.printAnswer(OutputLevel.Q2, true, "No Include-directives found.");
			return true;
		}
	}

	@Override
	public boolean isCritical()
	{
		//TODO this may gets false if the parsers evaluates Include-directives
		return true;
	}
}
