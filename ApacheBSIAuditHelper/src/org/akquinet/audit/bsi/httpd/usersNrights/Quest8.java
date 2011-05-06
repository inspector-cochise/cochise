package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.httpd.syntax.StatementList;

public class Quest8 implements YesNoQuestion
{
	private static final String PERMISSION_PATTERN = "-......---";
	private static final String _id = "Quest8";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _command;
	private String _commandPath;
	private File _configFile;
	private ResourceBundle _labels;
	private ConfigFile _conf;

	public Quest8(File configFile, ConfigFile conf)
	{
		this(configFile, conf, "./", "QfileSafe.sh");
	}

	public Quest8(File configFile, ConfigFile conf, String commandPath, String command)
	{
		_configFile = configFile;
		_commandPath = commandPath;
		_command = command;
		_conf = conf;
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		Set<File> problems = new HashSet<File>();
		
		try
		{
			for(Directive incDir : _conf.getAllDirectivesIgnoreCase("Include"))
			{
				List<File> files = StatementList.filesToInclude(_conf.getHeadExpression().getServerRoot(), incDir.getValue().trim());
				for(File file : files)
				{
					ShellAnsweredQuestion quest = checkFile(file);
					if(!quest.answer())
					{
						problems.add(file);
					}
				}
			}
			
			ShellAnsweredQuestion quest = checkFile(_configFile);
			if(!quest.answer())
			{
				problems.add(_configFile);
			}
			
			boolean ret = problems.isEmpty();
			
			
			_uc.printAnswer(ret, ret ? _labels.getString("S1") : _labels.getString("S2") );
			if(!ret)
			{
				StringBuffer buf = new StringBuffer();
				for (File file : problems)
				{
					buf = buf.append(file.getCanonicalPath()).append('\n');
				}
				
				_uc.printExample(buf.toString());
			}
			
			_uc.beginHidingParagraph( _labels.getString("S3") );
				_uc.printParagraph( _labels.getString("P1") );
				_uc.printExample("-rw-rw---- 1 root root 23650 2011-04-29 15:38 httpd.conf");
			_uc.endHidingParagraph();
			
			return ret;
		}
		catch (StatementList.ServerRootNotSetException e)
		{
			UserCommunicator.getDefault().reportError(e.getMessage());
			return false;
		}
		catch (IOException e)
		{
			UserCommunicator.getDefault().reportError(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private ShellAnsweredQuestion checkFile(File file) throws IOException
	{
		return new ShellAnsweredQuestion(_commandPath + _command, file.getCanonicalPath(), PERMISSION_PATTERN);
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
