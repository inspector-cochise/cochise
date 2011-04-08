package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.util.List;

import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest4 extends ModuleHelper implements YesNoQuestion
{
	private static final String _id = "Quest4";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	public Quest4(ConfigFile conf, File apacheExecutable)
	{
		super(conf, apacheExecutable);
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.println("It is necessary, that only modules you really need are being loaded.");
		_uc.println("Cause I don't know what modules you need I will give you a list of all modules that are loaded or have been compiled into your httpd binary.");
		
		{
			StringBuffer buf = new StringBuffer();
			
			_uc.println("First a list of the modules which have been compiled into your apache executable:\n");
			String[] compModules = getCompiledIntoModulesList();
			for (String mod : compModules)
			{
				buf = buf.append(mod + "\n");
			}
			
			_uc.printExample(buf.toString());
			buf = new StringBuffer();
			
			_uc.println("If there is any module you don't need please recompile your apache. " +
					"(It's not too hard to select modules which get compiled into.)");

			_uc.println("");
			
			_uc.println("Now let's get to the dynamically loaded modules. The following LoadModule directives in you apache" +
					"-configuration-file may get invoked:\n");
			List<Directive> loadModules = getLoadModuleList();
			for (Directive dir : loadModules)
			{
				buf = buf.append(dir.getLinenumber() + ": " + dir.getName() + " " + dir.getValue() + "\n");
			}
			_uc.printExample(buf.toString());
		}
		
		_uc.println("Please check whether you need all of these modules.");
		boolean ret = _uc.askYesNoQuestion("Do you need all of these modules?");
		_uc.printAnswer(ret, ret ? "No redundant modules loaded."
				: "Please remove these modules, if necessary recompile your apache.");
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
