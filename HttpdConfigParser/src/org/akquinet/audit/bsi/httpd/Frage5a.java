package org.akquinet.audit.bsi.httpd;

import java.util.List;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Frage5a extends YesNoQuestion
{
	private ConfigFile _conf;
	
	public Frage5a(ConfigFile conf)
	{
		// TODO Auto-generated method stub
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
			return false;
		}
		else
		{
			return true;
		}
	}
}
