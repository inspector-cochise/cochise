package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest10 extends ModuleHelper implements YesNoQuestion
{
	private static final String _id = "Quest10";
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	private static final String _command = "./QfileSafe.sh";

	public Quest10(ConfigFile conf)
	{
		super(conf);
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, "----" + _id + "----");
		_console.println(_level, "I'll now check whether any user not in root has access to one of the modules you load...");
		
		boolean ret = true;
		List<File> moduleFiles = getModuleFiles();
		for (File file : moduleFiles)
		{
			ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_command, file.getAbsolutePath());
			boolean ans = quest.answer();
			ret &= ans;
			if(!ans)
			{
				_console.println(_level, file.getAbsolutePath());
			}
		}
		
		_console.printAnswer(_level, ret, ret ? "Seems like only users in root have access to the dynamically loadable modules."
							: "Other users than users in root have access to the above listed modules.\n");
		
		return ret;
	}

	private List<File> getModuleFiles()
	{
		List<File> ret = new LinkedList<File>();
		List<Directive> dirList = getLoadModuleList();
		for (Directive dir : dirList)
		{
			ret.add(new File(dir.getName().trim()));
		}
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
