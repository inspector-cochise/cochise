package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	private String _commandPath;
	private String _command;

	public Quest10(ConfigFile conf)
	{
		this(conf, "./", "QfileSafe.sh");
	}
	
	public Quest10(ConfigFile conf, String commandPath, String command)
	{
		super(conf);
		_commandPath = commandPath;
		_command = command;
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, _id, "----" + _id + "----");
		_console.println(_level, _id, "I'll now check whether any user not in root has access to one of the modules you load...");
		
		boolean ret = true;
		List<File> moduleFiles = getModuleFiles();
		Set<String> probs = new HashSet<String>();
		for (File file : moduleFiles)
		{
			ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command, file.getAbsolutePath(), "........-.");
			boolean ans = quest.answer();
			ret &= ans;
			if(!ans)
			{
				probs.add(file.getAbsolutePath());
			}
			//also the enclosing directory has to be safe (for the case that file is a symbolic link, which we assume)
			quest = new ShellAnsweredQuestion(_commandPath + _command, file.getParent(), "........-.");
			ans = quest.answer();
			ret &= ans;
			if(!ans)
			{
				probs.add(file.getParent());
			}
		}

		if(!probs.isEmpty())
		{
			_console.println(_level, _id, "I found some files causing problems (in most cases this means some user not in root has write access)");
			for (String str : probs)
			{
				_console.println(_level, _id, str);
			}
		}
		
		_console.printAnswer(_level, _id, ret, ret ? "Seems like only users in root have access to the dynamically loadable modules."
							: "Other users than users in root may have access to the above listed modules or their containing directories.\n");
		
		return ret;
	}

	private List<File> getModuleFiles()
	{
		List<File> ret = new LinkedList<File>();
		List<Directive> dirList = getLoadModuleList();
		for (Directive dir : dirList)
		{
			ret.add(new File(dir.getValue().trim().split("[ \t]+")[1]));
		}
		
		dirList = getLoadFileList();
		for (Directive dir : dirList)
		{
			ret.add(new File(dir.getValue().trim()));
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
