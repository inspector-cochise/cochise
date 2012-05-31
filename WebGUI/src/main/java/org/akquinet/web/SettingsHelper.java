package org.akquinet.web;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;

public class SettingsHelper
{
	private static final String GET_DEFAULT_SRV_ROOT_COMMAND = "./srvRoot.sh";
	
	private ResourceBundle _labels = ResourceBundle.getBundle("settings", Locale.getDefault());
	public PrologueData _prologueData;
	
	public enum OperatingSystem
	{
		Ubuntu,
		Debian,
		SUSE,
		RedHat,
		UNKNOWN
	}
	
	public SettingsHelper()
	{
		String apacheExec;
		String apacheConf;
		
		switch(getOperatingSystem())
		{
		case Debian:
		case Ubuntu:
			apacheExec = "/usr/sbin/apache2";
			apacheConf = "/etc/apache2/apache2.conf";
			break;
		case SUSE:
			apacheExec = "/usr/sbin/httpd2";
			apacheConf = "/etc/apache2/httpd.conf";
			break;
		case RedHat:
			apacheExec = "/usr/sbin/httpd";
			apacheConf = "/etc/httpd/httpd.conf";
			break;
		case UNKNOWN:
		default:
			apacheExec = "/usr/sbin/httpd";
			apacheConf = "/etc/httpd/httpd.conf";
			break;
		}
		
		File execFile = new File(apacheExec);
		File confFile = new File(apacheConf);
		
		_prologueData = new PrologueData(apacheExec, apacheConf, execFile, null, confFile, true, true);
	}
	
	public SettingsHelper(PrologueData pd)
	{
		_prologueData = pd;
	}
	
	public String getExecErrorMsg()
	{
		File apacheExec = new File(_prologueData._apacheExec);

		if (!apacheExec.exists())
		{
			return _labels.getString("E1");
		}
		else
		{
			_prologueData._apacheExecutable = apacheExec;
			return "";
		}
	}

	public String getConfigErrorMsg()
	{
		File apacheConf = new File(_prologueData._apacheConf);

		if (!apacheConf.exists())
		{
			return _labels.getString("E1");
		}
		_prologueData._configFile = apacheConf;

		try
		{
			ConfigFile conf = new ConfigFile(apacheConf, getDefaultServerRoot());
			_prologueData._conf = conf;
			return "";
		}
		catch (ParserException e)
		{
			if (e.getMessage() != null)
			{
				return e.getMessage();
			}
			else
			{
				return e.getClass().getName();
				//return _labels.getString("E3");	//TODO hint to envvars problem
			}
		}
		catch (IOException e)
		{
			if (e.getMessage() != null)
			{
				return e.getMessage();
			}
			else
			{
				return _labels.getString("E2");
			}
		}
	}
	
	private String getDefaultServerRoot()
	{
		ShellAnsweredQuestion q = new ShellAnsweredQuestion(GET_DEFAULT_SRV_ROOT_COMMAND, _prologueData._apacheExec);
		q.answer();
		try
		{
			int c = q.getStdOut().read();
			StringBuffer buf = new StringBuffer();
			while(c != -1)
			{
				buf.append((char)c);
				c = q.getStdOut().read();
			}
			if(buf.charAt(buf.length()-1) == '\n')
			{
				return buf.substring(0, buf.length()-1);
			}
			else
			{
				return buf.toString();
			}
		}
		catch (IOException e)
		{
			//getting the default server's root may be vital for the application
			throw new RuntimeException(e);
		}
	}
	
	public static OperatingSystem getOperatingSystem()
	{
		try
		{
			Process p = (new ProcessBuilder("./distro.sh")).start();
			int exitVal = 0;
			boolean wait = true;

			while(wait)
			{
				try
				{
					exitVal = p.waitFor();
					wait = false;
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
			
			switch(exitVal)
			{
			case 10:
				return OperatingSystem.Ubuntu;
			case 20:
				return OperatingSystem.SUSE;
			case 30:
				return OperatingSystem.RedHat;
			case 40:
				return OperatingSystem.Debian;
			case 0:
			default:
				return OperatingSystem.UNKNOWN;
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e); //this means something is not right calling the scripts. Will also cause problems later.
		}
	}
}
