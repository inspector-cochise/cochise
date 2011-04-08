package org.akquinet.audit.bsi.httpd.software;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest7 implements YesNoQuestion
{
	private static final String _id = "Quest7";
	private ConfigFile _conf;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	public Quest7(ConfigFile conf)
	{
		_conf = conf;
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.println("All options should be explicitly deactivated and only necessary options should be activated.");
		_uc.println("Scanning for \"Options None\" in global context...");
		
		List<Directive> dirList = _conf.getAllDirectives("Options");
		List<Directive> problems = new LinkedList<Directive>();
		boolean isSetGlobal = false;	//will be changed if at least one directive in global context is found
		
		for (Directive directive : dirList)
		{
			if(!directive.getValue().matches("[ \t]*[Nn]one[ \t]*"))
			{
				if(!directive.getValue().matches("[ \t]*-(\\S)*[ \t]*"))	//maybe an option is deactivated
				{
					problems.add(directive);
				}
			}
			else if(directive.getSurroundingContexts().get(0) == null)
			{
				isSetGlobal = true;
			}
		}
		_uc.println(isSetGlobal ?
						"Directive \"Options None\" is correctly stated in global context." :
						"You haven't stated the directive \"Options None\" in global context.");
		boolean allOk = problems.isEmpty();
		if(!allOk)
		{
			_uc.println("As expected you activated some options. I will give you the line of each in you configuration file." );
			_uc.println("Please check whether you really need all these options." );
			for (Directive directive : problems)
			{
				_uc.printExample("line " + directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
			}
			allOk = _uc.askYesNoQuestion("Do you really need all these options?");
		}
		
		boolean ret = isSetGlobal && allOk;
		_uc.printAnswer(ret, ret ? "(De-)Activation of options is well done." : "Please state \"Options None\" in the global context and/or do not activate unneeded options.");
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
	
	private void warnForPlugin(String optionsValue)
	{
		String[] pluginArr = optionsValue.split("[ \t]");
		for(int i = 0; i < pluginArr.length; ++i)
		{
			if(pluginArr[i].startsWith("+"))
			{
				pluginArr[i].replaceFirst("+", "");
			}
			
			if(pluginArr[i].startsWith("-"))
			{
				pluginArr[i] = "";
			}
		}
		
		Map<String,String> warnDB = new HashMap<String, String>(); //format regex -> warn-message
		//TODO maybe do a lookup in some database or file for this -> in future it may grows!
		warnDB.put("[Ii]ndexes", "Use this one with care! You probably show information by accident that should not be shown.");
		warnDB.put("[Ii]ncludes", "This one is a security risk. Please disable. (Will be topic of a later question)");
		
		for (String plugin : pluginArr)
		{
			for (String regex : warnDB.keySet())
			{
				if(plugin.matches(regex))
				{
					_uc.printExample(optionsValue + "\t " + warnDB.get(regex));
				}
			}
		}
	}
}