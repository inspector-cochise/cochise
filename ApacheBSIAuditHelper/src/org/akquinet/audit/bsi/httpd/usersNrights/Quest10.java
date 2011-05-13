package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest10 extends ModuleHelper implements YesNoQuestion
{
	private static final String _id = "Quest10";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private static final String PERMISSION_PATTERN = ".....-..-.";
	private String _commandPath;
	private String _command;
	private ResourceBundle _labels;

	public Quest10(ConfigFile conf)
	{
		this(conf, "./", "QfileSafe.sh");
	}
	
	public Quest10(ConfigFile conf, String commandPath, String command)
	{
		super(conf);
		_commandPath = commandPath;
		_command = command;
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		_uc.println( _labels.getString("L1") );
		
		Set<String> probs = lookForProblems(PERMISSION_PATTERN);
		boolean ret = probs.isEmpty();;

		if(!ret)
		{
			_uc.println( _labels.getString("L2") );
			for (String str : probs)
			{
				_uc.println(str);
			}
		}
		
		_uc.printAnswer(ret, ret ?  _labels.getString("S1_good") 
							:  _labels.getString("S1_bad") );
		
		return ret;
	}

	private List<File> getModuleFiles()
	{
		List<File> ret = new LinkedList<File>();
		List<Directive> dirList = getLoadModuleList();
		for (Directive dir : dirList)
		{
			try
			{
				ret.add(new File(dir.getValue().trim().split("[ \t]+")[1]));
			}
			catch(IndexOutOfBoundsException e)
			{
				//This is a malformed LoadModule directive. Actually "apache2 -t" recognises this as error.
				//So it's ok not to do anything here 'cause it will be handled in Quest6.
			}
		}
		
		dirList = getLoadFileList();
		for (Directive dir : dirList)
		{
			ret.add(new File(dir.getValue().trim()));
		}
		return ret;
	}
	
	private Set<String> lookForProblems(String permissionPattern)
	{
		List<File> moduleFiles = getModuleFiles();
		Set<String> probs = new HashSet<String>();
		for (File file : moduleFiles)
		{
			ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command, file.getAbsolutePath(), permissionPattern);
			if(!quest.answer())
			{
				probs.add(file.getAbsolutePath());
			}
			//also the enclosing directory has to be safe (for the case that file is a symbolic link, which we assume)
			quest = new ShellAnsweredQuestion(_commandPath + _command, file.getParent(), permissionPattern);
			if(!quest.answer())
			{
				probs.add(file.getParent());
			}
		}
		
		return probs;
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
