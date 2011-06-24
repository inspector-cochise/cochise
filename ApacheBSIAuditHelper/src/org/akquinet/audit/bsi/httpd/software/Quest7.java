package org.akquinet.audit.bsi.httpd.software;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest7 implements YesNoQuestion
{
	private static final String _id = "Quest7";
	private ConfigFile _conf;
	private ResourceBundle _labels;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	public Quest7(ConfigFile conf)
	{
		_conf = conf;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		try
		{
			_conf.reparse();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		_uc.println( _labels.getString("L1") );
		_uc.println( _labels.getString("L2") );
		
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
				   _labels.getString("L3")
				 : _labels.getString("L4") );
		boolean allOk = problems.isEmpty();
		if(!allOk)
		{
			_uc.println( _labels.getString("L5") );
			_uc.println( _labels.getString("L6") );
			for (Directive directive : problems)
			{
				_uc.printExample(directive.getContainingFile() + ":" + directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
			}
			allOk = _uc.askYesNoQuestion( _labels.getString("Q1") );
		}
		
		boolean ret = isSetGlobal && allOk;
		_uc.printAnswer(ret, ret ?  _labels.getString("S2_good") 
								:   _labels.getString("S2_bad") );
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
		return 2;
	}

	@Override
	public int getNumber()
	{
		return 7;
	}

	@Override
	public String[] getRequirements()
	{
		String[] ret = new String[1];
		ret[0] = "Quest6";
		return ret;
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
		warnDB.put("[Ii]ndexes",  _labels.getString("S3") );
		warnDB.put("[Ii]ncludes",  _labels.getString("S4") );
		
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
