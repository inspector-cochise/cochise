package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest3 extends ModuleHelper implements YesNoQuestion
{
	private static final String _id = "Quest3";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private ResourceBundle _labels;
	
	public Quest3(ConfigFile conf, File apacheExecutable)
	{
		super(conf, apacheExecutable);
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );

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
				_uc.printAnswer(true, _labels.getString("S1"));
				_uc.printExample(modSec.getContainingFile() + ":" + modSec.getLinenumber() + ": " + modSec.getName() + " " + modSec.getValue());
				_uc.printHidingParagraph(_labels.getString("S4"), _labels.getString("S5"));
				return true;
			}
			
		}
		
		//maybe ModSecurity is compiled into the apache binary, check for that:
		String[] modList = getCompiledIntoModulesList();
		for (String str : modList)
		{
			if(str.matches("( |\t)*mod_security.c( |\t)*"))
			{
				_uc.printAnswer(true, _labels.getString("S2"));
				_uc.printHidingParagraph(_labels.getString("S4"), _labels.getString("S5"));
				return true;
			}
		}
		
		_uc.printAnswer(false, _labels.getString("S3"));
		
		_uc.printHidingParagraph(_labels.getString("S4"), _labels.getString("S5"));
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

	@Override
	public int getBlockNumber()
	{
		return 2;
	}

	@Override
	public int getNumber()
	{
		return 3;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}
}
