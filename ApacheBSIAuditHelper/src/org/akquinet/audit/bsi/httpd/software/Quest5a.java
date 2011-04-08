package org.akquinet.audit.bsi.httpd.software;

import java.util.List;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest5a implements YesNoQuestion
{
	private static final String _id = "Quest5a";
	private ConfigFile _conf;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	public Quest5a(ConfigFile conf)
	{
		_conf = conf;
	}

	/**
	 * checks whether there are any Include-directives in the config file
	 */
	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		List<Directive> incList = _conf.getAllDirectivesIgnoreCase("Include");
		
		if(!incList.isEmpty())
		{
			_uc.printAnswer(false, "There are Include-directives in your apache configuration:");
			for (Directive directive : incList)
			{
				_uc.println("line " + directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
			}
			return false;
		}
		else
		{
			_uc.printAnswer(true, "No Include-directives found.");
			return true;
		}
	}

	@Override
	public boolean isCritical()
	{
		return true;
	}

	@Override
	public String getID()
	{
		return _id;
	}
}