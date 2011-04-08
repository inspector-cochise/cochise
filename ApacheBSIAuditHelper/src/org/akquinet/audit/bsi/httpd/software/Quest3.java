package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.util.List;

import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest3 extends ModuleHelper implements YesNoQuestion
{
	private static final String _id = "Quest3";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	public Quest3(ConfigFile conf, File apacheExecutable)
	{
		super(conf, apacheExecutable);
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);

		List<Directive> loadList = getLoadModuleList();
		for (Directive directive : loadList)
		{
			String[] arguments = directive.getValue().trim().split("[ \t]+");
			if(arguments == null || arguments.length < 2)
			{
				continue;
			}
			
			if(arguments[0].equals("security2_module"))
			{
				Directive modSec = directive;
				_uc.printAnswer(true, "ModSecurity is being loaded:");
				_uc.printExample(modSec.getLinenumber() + ": " + modSec.getName() + " " + modSec.getValue());
				return true;
			}
			
		}
		
		//maybe ModSecurity is compiled into the apache binary, check for that:
		String[] modList = getCompiledIntoModulesList();
		for (String str : modList)
		{
			if(str.matches("( |\t)*mod_security.c( |\t)*"))
			{
				_uc.printAnswer(true, "ModSecurity is compiled into the httpd binary.");
				return true;
			}
		}
		
		_uc.printAnswer(false, "ModSecurity seems not to be loaded.");
		return false;
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
