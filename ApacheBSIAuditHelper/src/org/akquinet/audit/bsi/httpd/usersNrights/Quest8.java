package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest8 implements YesNoQuestion
{
	private static final String _id = "Quest8";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _command;
	private String _commandPath;
	private File _configFile;
	private ResourceBundle _labels;

	public Quest8(File configFile)
	{
		this(configFile, "./", "QfileSafe.sh");
	}

	public Quest8(File configFile, String commandPath, String command)
	{
		_configFile = configFile;
		_commandPath = commandPath;
		_command = command;
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command, _configFile.getAbsolutePath());
		boolean ret = quest.answer();
		
		_uc.printAnswer(ret, ret ? _labels.getString("S1")
							: _labels.getString("S2") );
		
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
