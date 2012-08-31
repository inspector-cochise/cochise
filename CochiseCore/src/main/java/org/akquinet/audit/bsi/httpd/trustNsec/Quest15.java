package org.akquinet.audit.bsi.httpd.trustNsec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.akquinet.audit.Automated;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.DevNullUserCommunicator;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.util.ResourceWatcher;

@Automated
public class Quest15 extends ResourceWatcher implements YesNoQuestion
{
	private static final long serialVersionUID = 9005102833398189797L;
	
	private static final String SSLCERTIFICATEFILE_DIRECTIVE_NAME = "SSLCertificateFile";
	private static final String SSLCERTIFICATEKEYFILE_DIRECTIVE_NAME = "SSLCertificateKeyFile";
	
	public static final String _id = "Quest15";
	
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private ConfigFile _conf;
	private String _commandPath;
	private String _command;
	private String _getUserNGroupCommand;
	private String _apacheExecutable;
	private transient ResourceBundle _labels;
	private String _user;
	private Collection<File> _privateKeyFiles = null;
	private Boolean _lastAnswer = null;


	public Quest15(PrologueData pd)
	{
		this(pd._conf, pd._apacheExec);
	}
	
	public Quest15(PrologueData pd, UserCommunicator uc)
	{
		this(pd._conf, pd._apacheExec, uc);
	}
	
	public Quest15(ConfigFile conf, String apacheExecutable)
	{
		this(conf, "./", "listUsersWhoHaveAccess.sh", "getApacheStartingUser.sh", apacheExecutable);
	}
	
	public Quest15(ConfigFile conf, String apacheExecutable, UserCommunicator uc)
	{
		this(conf, "./", "listUsersWhoHaveAccess.sh", "getApacheStartingUser.sh", apacheExecutable, uc);
	}

	public Quest15(ConfigFile conf, String commandPath, String command, String getUserNGroupCommand, String apacheExecutable)
	{
		this(conf, commandPath, command, getUserNGroupCommand, apacheExecutable, UserCommunicator.getDefault());
	}
	
	public Quest15(ConfigFile conf, String commandPath, String command, String getUserNGroupCommand, String apacheExecutable, UserCommunicator uc)
	{
		_uc = uc;
		_conf = conf;
		_commandPath = commandPath;
		_getUserNGroupCommand = getUserNGroupCommand;
		_apacheExecutable = apacheExecutable;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_lastAnswer = answer(_uc);
		return _lastAnswer;
	}

	private boolean answer(UserCommunicator uc)
	{
		uc.printHeading3( _labels.getString("name") );
		uc.printParagraph( _labels.getString("Q0") );
		
		uc.printParagraph( _labels.getString("P0") );
		StringBuilder b = new StringBuilder();
		for(File f : _privateKeyFiles)
		{
			b = b.append(f.getPath()).append('\n');
		}
		uc.printExample(b.toString());
		
		uc.printHidingParagraph( _labels.getString("SD0"), _labels.getString("HP0"));
		
		uc.beginIndent();
			boolean ret = true;
			ret &= answer15a(uc);
			ret &= answer15b(uc);
		uc.endIndent();
		
		uc.printAnswer(ret, ret ? _labels.getString("A0") : _labels.getString("A1") );
		return ret;
	}
	
	private boolean answer15a(UserCommunicator uc)
	{
		uc.printHeading3( _labels.getString("name_15a") );
		uc.printParagraph( _labels.getString("Q0a") );
		
		boolean ret = true;
		
		for(File dir : new HashSet<File>(_privateKeyFiles))
		{
			List<String> accessUsers = listAccessUsers(dir);
			accessUsers.remove("root");
			accessUsers.remove(_user);
			
			if(!accessUsers.isEmpty())
			{
				uc.println( MessageFormat.format(_labels.getString("P1"), _user, dir.getPath()) );
			}
			ret &= accessUsers.isEmpty();
		}
		
		uc.printAnswer(ret, ret ? _labels.getString("A0a") : _labels.getString("A1a"));
		return ret;
	}
	
	private boolean answer15b(UserCommunicator uc)
	{
		uc.printHeading3( _labels.getString("name_15b") );
		uc.printParagraph( _labels.getString("Q0b") );
		
		Set<File> directoriesToInspect = new HashSet<File>();
		
		for(File f : _privateKeyFiles)
		{
			directoriesToInspect.add(f.getParentFile());
		}
		
		boolean ret = true;
		
		for(File dir : directoriesToInspect)
		{
			List<String> accessUsers = listAccessUsers(dir);
			accessUsers.remove("root");
			accessUsers.remove(_user);
			
			if(!accessUsers.isEmpty())
			{
				uc.println( MessageFormat.format(_labels.getString("P1"), _user, dir.getPath()) );
			}
			ret &= accessUsers.isEmpty();
		}
		
		uc.printAnswer(ret, ret ? _labels.getString("A0b") : _labels.getString("A1b"));
		return ret;
	}

	private List<String> listAccessUsers(File file)
	{
		ShellAnsweredQuestion script = new ShellAnsweredQuestion(_commandPath + _command, file.getAbsolutePath(), "list_for_file");
		if(script.answer())
		{
			InputStream stdOut = script.getStdOut();
			StringBuilder sb = new StringBuilder();
			
			try
			{
				int c = stdOut.read();
				while(c >= 0)
				{
					sb = sb.append((char)c);
					c = stdOut.read();
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			
			return Arrays.asList( sb.toString().split("\n") );
		}
		else
		{
			List<String> ret = new ArrayList<String>(1);
			ret.add("EVERYBODY");
			return ret;
		}
	}
	
	private Collection<File> getPrivateKeyFiles()
	{
		final Set<File> ret = new LinkedHashSet<File>();
		
		for(Directive dir : _conf.getAllDirectivesIgnoreCase(SSLCERTIFICATEKEYFILE_DIRECTIVE_NAME))
		{
			ret.add( createFileFromApacheFileString(dir.getValue()) );
		}
		
		for(Directive dir :_conf.getAllDirectivesIgnoreCase(SSLCERTIFICATEFILE_DIRECTIVE_NAME))
		{
			File f = createFileFromApacheFileString(dir.getValue());
			if(fileContainsPrivateKey(f))
			{
				ret.add( f );
			}
		}
		
		return ret;
	}

	private boolean fileContainsPrivateKey(File f)
	{
		BufferedReader r = null;
		try
		{
			r = new BufferedReader( new FileReader(f) );
			String line = r.readLine();
			
			while(line != null)
			{
				if(line.matches("-----BEGIN .* PRIVATE KEY-----"))
				{
					return true;
				}
				line = r.readLine();
			}
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			try
			{
				if(r != null)
				{
					r.close();
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		return false;
	}

	private File createFileFromApacheFileString(String filedescriptor)
	{
		if(filedescriptor.startsWith("/"))
		{
			return new File(filedescriptor);
		}
		else
		{
			return new File(_conf.getHeadExpression().getServerRoot() + File.separator + filedescriptor);
		}
	}

	private String getApacheStartingUser()
	{
		try
		{
			Process p = (new ProcessBuilder(_commandPath + _getUserNGroupCommand, _apacheExecutable)).start();
			InputStream is = p.getInputStream();
			StringBuffer buf = new StringBuffer();
			int b = is.read();
			while(b != -1)
			{
				buf.append((char)b);
				b = is.read();
			}
			return buf.toString().split("[ \t]+")[0];
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
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
		return 4;
	}

	@Override
	public int getNumber()
	{
		return 15;
	}

	@Override
	public String[] getRequirements()
	{
		String[] ret = new String[1];
		ret[0] = "Quest6";
		return ret;
	}

	@Override
	public void initialize() throws Exception
	{
		_conf.reparse();
		_user = getApacheStartingUser();
		_privateKeyFiles = getPrivateKeyFiles();
	}
	
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		_uc = UserCommunicator.getDefault();
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}
	
	@Override
	public String getName()
	{
		return _labels.getString("name");
	}

	@Override
	public void setUserCommunicator(UserCommunicator uc)
	{
		_uc = uc;
	}

	@Override
	public String getResourceId()
	{
		return "quest." + _id;
	}

	@Override
	public boolean resourceChanged()
	{
		try
		{
			initialize();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		//this is just a not so intelligent dummy-solution
		boolean answer = answer(new DevNullUserCommunicator());
		
		if(_lastAnswer != null && answer != _lastAnswer)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
