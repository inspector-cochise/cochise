package org.akquinet.audit.bsi.httpd.software;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
	private String _listFileName;
	
	public Quest4(ConfigFile conf, File apacheExecutable, String tempDirectory)
	{
		super(conf, apacheExecutable);
		_listFileName = tempDirectory + File.separator + "moduleList.txt";
	}
	
	public Quest4(ConfigFile conf, File apacheExecutable)
	{
		this(conf, apacheExecutable, "/tmp/apacheAudit");
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.println("It is necessary, that only modules you really need are being loaded.");
		_uc.println("Cause I don't know what modules you need and this is usally a longer list, I will give you a list in " + _listFileName + " .");
		
		try
		{
			File listFile = new File(_listFileName);
			listFile.getParentFile().mkdirs();
			listFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(listFile));
			
			writer.write("First a list of the modules which have been compiled into your apache executable:\n");
			writer.write("----\n");
			String[] compModules = getCompiledIntoModulesList();
			for (String mod : compModules)
			{
				writer.write(mod + "\n");
			}
			writer.write("----\n");
			writer.write("If there is any module you don't need please recompile your apache. " +
					"(It't not too hard to select modules which get compiled into.)\n");

			writer.write("\n");
			
			writer.write("Now let's get to the dynamically loaded modules. The following LoadModule directives in you apache" +
					"-configuration-file may get invoked:\n");
			writer.write("----\n");
			List<Directive> loadModules = getLoadModuleList();
			for (Directive dir : loadModules)
			{
				writer.write(dir.getLinenumber() + ": " + dir.getName() + " " + dir.getValue() + "\n");
			}
			writer.write("----\n");
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
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
