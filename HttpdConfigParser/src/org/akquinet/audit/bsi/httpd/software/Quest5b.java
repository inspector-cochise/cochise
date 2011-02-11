package org.akquinet.audit.bsi.httpd.software;

import java.util.LinkedList;
import java.util.List;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest5b implements YesNoQuestion
{
	private static final String _id = "Quest5b";
	private ConfigFile _conf;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q2;
	
	public Quest5b(ConfigFile conf)
	{
		_conf = conf;
	}

	/**
	 * Looking for AllowOverride-directives. Checking whether there is no with parameters other than None and at least one
	 * in global context with parameter None.
	 */
	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, "----" + _id + "----");
		List<Directive> dirList = _conf.getDirective("AllowOverride");
		List<Directive> problems = new LinkedList<Directive>();
		boolean isSetGlobalRoot = false;	//will be changed if at least one directive in global context is found
		
		for (Directive directive : dirList)
		{
			if(!directive.getValue().matches("[ \t]*None[ \t]*"))
			{
				problems.add(directive);
			}
			else if(directive.getSurroundingContexts().size() == 2) //that means one real context and one context being null (i.e. global context)
			{
				if(directive.getSurroundingContexts().get(0).getName().equalsIgnoreCase("directory") && 
						directive.getSurroundingContexts().get(0).getValue().matches("[ \t]*/[ \t]*") &&
						directive.getSurroundingContexts().get(1) == null)
				{
					isSetGlobalRoot = true;
				}
			}
		}
		String global = isSetGlobalRoot ?
						"Directive \"AllowOverride None\" is correctly stated in context for root directory being in global context." :
						"You haven't stated the directive \"AllowOverride None\" in a directory context for the root directory which is itself placed in global context.";
		String overrides = problems.isEmpty() ?
						"Directive \"AllowOverride\" correctly doesn't appear with a parameter other than \"None\"" :
						"You have stated the directive \"AllowOverrid\" with parameters other than \"None\". Remove these:";
		_console.printAnswer(_level, isSetGlobalRoot & problems.isEmpty(), global + " " + overrides);
		for (Directive directive : problems)
		{
			_console.println(_level, "line " + directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
		}

		
		return isSetGlobalRoot && problems.isEmpty();
	}
	
	@Override
	public boolean isCritical()
	{
		//Quest5 is critical, this is a subquestion so this is handled by Quest5
		return false;
	}

	@Override
	public String getID()
	{
		return _id;
	}
}
