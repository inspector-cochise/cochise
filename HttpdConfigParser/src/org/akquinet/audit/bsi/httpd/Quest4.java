package org.akquinet.audit.bsi.httpd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest4 extends ModuleHelper implements YesNoQuestion
{
	private static final String _id = "Quest4";
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	private static final String _listFileName = "/tmp/apacheAudit/moduleList.txt";
	
	public Quest4(ConfigFile conf, File apacheExecutable)
	{
		super(conf, apacheExecutable);
	}

	@Override
	public boolean answer()
	{
		_console.println(_level, "It is necessary, that only modules you really need are being loaded.");
		_console.println(_level, "Cause I don't know what modules you need and this is usally a longer list, I will give you a list in " + _listFileName + " .");
		
		try
		{
			File listFile = new File(_listFileName);
			listFile.getParentFile().mkdirs();
			listFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(listFile));
			
			writer.write("First a list of the modules which have been compiled into your apache executable:\n");
			String[] compModules = getCompiledIntoModulesList();
			for (String mod : compModules)
			{
				writer.write(mod + "\n");
			}
			writer.write("If there is any module you don't need please recompile your apache. " +
					"(It't not too hard to select modules which get compiled into.)\n");

			writer.write("\n");
			
			writer.write("Now let's get to the dynamically loaded modules. The following LoadModule directives in you apache" +
					"-configuration-file may get invoked:\n");
			List<Directive> loadModules = getLoadModuleList();
			for (Directive dir : loadModules)
			{
				writer.write(dir.getLinenumber() + ": " + dir.getName() + " " + dir.getValue() + "\n");
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		_console.println(_level, "Please check whether you need all of these modules.");
		boolean ret = _console.askYesNoQuestion(_level, "Do you need all of these modules?");
		_console.printAnswer(_level, ret, ret ? "No redundant modules loaded."
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
