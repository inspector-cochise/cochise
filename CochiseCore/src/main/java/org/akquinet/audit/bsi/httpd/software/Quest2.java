package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.UserCommunicator;

@Interactive
public class Quest2 implements YesNoQuestion
{
	private static final long serialVersionUID = -7930218858624626816L;
	
	public static final String _id = "Quest2";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private transient ProcessBuilder _httpd;
	private transient ProcessBuilder _getCurrent;
	private transient ProcessBuilder _getRunning;
	private transient ResourceBundle _labels;
	
	
	public Quest2(PrologueData pd)
	{
		this(pd._apacheExecutable);
	}
	
	public Quest2(PrologueData pd, UserCommunicator uc)
	{
		this(pd._apacheExecutable, uc);
	}
	
	public Quest2(File apacheExecutable)
	{
		this(apacheExecutable, UserCommunicator.getDefault());
	}
	
	public Quest2(File apacheExecutable, UserCommunicator uc)
	{
		this(apacheExecutable, "./", "newestVersion.sh", "runningVersion.sh", uc);
	}
	
	public Quest2(File apacheExecutable, String commandPath, String getCurrentVersionCommand, String getRunningVersionCommand)
	{
		this(apacheExecutable, commandPath, getCurrentVersionCommand, getRunningVersionCommand, UserCommunicator.getDefault());
	}
	
	public Quest2(File apacheExecutable, String commandPath, String getCurrentVersionCommand, String getRunningVersionCommand, UserCommunicator uc)
	{
		_uc = uc;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		try
		{
			_httpd = new ProcessBuilder(apacheExecutable.getCanonicalPath(), "-v");
			_getCurrent = new ProcessBuilder(commandPath + getCurrentVersionCommand);
			_getRunning = new ProcessBuilder(commandPath + getRunningVersionCommand, apacheExecutable.getAbsolutePath());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( _labels.getString("Q0") );
		
		String version = getRunningVersionInformation();
		
		_uc.println(_labels.getString("S2"));
		_uc.printExample(version);
		
		String running = getRunningVersion();
		String newest = getNewestStableVersion();
		int versionCompare = versionCompare(running, newest);
		
		if(versionCompare == 0)
		{
			_uc.printAnswer(true, _labels.getString("S4"));
			return true;
		}
		else if(versionCompare == -1)
		{
			_uc.printParagraph( MessageFormat.format(_labels.getString("S5"), newest) );
			
			boolean ret = _uc.askYesNoQuestion(_labels.getString("Q1"));
			_uc.printAnswer(ret, ret ? _labels.getString("S3_good")
					: _labels.getString("S3_bad"));
			return ret;
		}
		else //if(versionCompare == +1)
		{
			_uc.printAnswer(false, MessageFormat.format(_labels.getString("S6"), running, newest) );
			return false;
		}
	}

	/**
	 * compares the running version number with the newest stable version number
	 * @param running the version number of the running apache
	 * @param newest the version number of the newest stable apache
	 * @return 0 if both are equal, +1 if the running version is newer than the newest stable or -1 else (i.e. the running version is older than the newest stable)
	 */
	private int versionCompare(String running, String newest)
	{
		String[] tmp;
		tmp = running.split("\\.");
		int running_Major = Integer.parseInt(tmp[0]);
		int running_Minor = Integer.parseInt(tmp[1]);
		int running_release = Integer.parseInt(tmp[2]);
		
		tmp = newest.split("\\.");
		int newest_Major = Integer.parseInt(tmp[0]);
		int newest_Minor = Integer.parseInt(tmp[1]);
		int newest_release = Integer.parseInt(tmp[2]);
		
		if(running_Major == newest_Major && running_Minor == newest_Minor && running_release == newest_release)
		{
			return 0;
		}
		else if(running_Major > newest_Major ||
				running_Major == newest_Major && running_Minor > newest_Minor ||
				running_Major == newest_Major && running_Minor == newest_Minor && running_release > newest_release)
		{
			return +1;
		}
		else
		{
			return -1;
		}
	}

	private String getRunningVersion()
	{
		String version = getProcessOutput(_getRunning);
		if(version.equals(""))
		{
			throw new RuntimeException( _labels.getString("S1") );
		}
		return version.substring(0, version.length()-1);
	}

	private String getNewestStableVersion()
	{
		String version = getProcessOutput(_getCurrent);
		if(version.equals(""))
		{
			throw new RuntimeException( _labels.getString("S1") );
		}
		return version.substring(0, version.length()-1);
	}

	private String getRunningVersionInformation()
	{
		String version = getProcessOutput(_httpd);
		if(version.equals(""))
		{
			throw new RuntimeException( _labels.getString("S1") );
		}
		return version.substring(0, version.length()-1);
	}

	private String getProcessOutput(ProcessBuilder pb)
	{
		String output = "";
		try
		{
			Process p = pb.start();
			InputStream stdOut = p.getInputStream();
			boolean wait = true;

			while(wait)
			{
				try
				{
					p.waitFor();
					wait = false;
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
			
			StringBuffer buf = new StringBuffer();
			int b = stdOut.read();
			while(b != -1)
			{
				buf.append((char)b);
				b = stdOut.read();
			}
			output = buf.toString();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return output;
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
		return 2;
	}

	@Override
	public int getNumber()
	{
		return 2;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}
	
	@Override
	public void initialize()
	{
		//nothing to do here
	}
	
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException
	{
		s.defaultWriteObject();
		s.writeObject(_httpd.command());
		s.writeObject(_getCurrent.command());
		s.writeObject(_getRunning.command());
	}
	
	@SuppressWarnings("unchecked")
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		_httpd = new ProcessBuilder((List<String>) s.readObject());
		_getCurrent = new ProcessBuilder((List<String>) s.readObject());
		_getRunning = new ProcessBuilder((List<String>) s.readObject());
		
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
}
