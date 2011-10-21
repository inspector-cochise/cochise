package org.akquinet.audit.bsi.httpd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

public class PrologueQuestion implements YesNoQuestion
{
	private static final String GET_DEFAULT_SRV_ROOT_COMMAND = "./srvRoot.sh";
	private static final String _id = "Prologue";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private PrologueData _pD;
	
	private ResourceBundle _labels;

	public PrologueQuestion(PrologueData pD)
	{
		_pD = pD;
		_labels = ResourceBundle.getBundle("global", _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading1( _labels.getString("H1") );
		_uc.printParagraph( _labels.getString("P1") );
		_uc.printParagraph( _labels.getString("P2") );
		_uc.printParagraph( _labels.getString("P5") );
		_uc.printParagraph( _labels.getString("P3") );
		_uc.printExample("/usr/sbin/apache2    (Debian/Ubuntu)\n" +
				"/usr/sbin/httpd      (RedHat)\n" +
		"/usr/sbin/httpd2     (SUSE)");
		
		String tmp = _uc.askStringQuestion( _labels.getString("Q1") , _pD._apacheExec);
		_pD._apacheExec = "".equals(tmp.trim()) ? _pD._apacheExec : tmp;
		_pD._apacheExecutable = new File(_pD._apacheExec);
		
		while(! _pD._apacheExecutable.exists() )
		{
			tmp = _uc.askStringQuestion( MessageFormat.format(_labels.getString("E2"), tmp) );
			_pD._apacheExecutable = new File(tmp.trim());
		}
		_uc.println("");
		_uc.println( _labels.getString("L2") );
		_uc.printExample("/etc/apache2/apache2.conf    (Debian/Ubuntu)\n" +
				"/etc/httpd/httpd.conf        (RedHat)\n" +
		"/etc/apache2/httpd.conf      (SUSE)");
		
		tmp = _uc.askStringQuestion( _labels.getString("Q2") , _pD._apacheConf);
		_pD._apacheConf = "".equals(tmp.trim()) ? _pD._apacheConf : tmp;
		_pD._configFile = new File(_pD._apacheConf);
		
		while(! _pD._configFile.exists() )
		{
			tmp = _uc.askStringQuestion( MessageFormat.format(_labels.getString("E3"), tmp) );
			_pD._configFile = new File(tmp.trim());
		}
		try
		{
			String defaultServerRoot = getDefaultServerRoot(_pD._apacheExec);
			_pD._conf = new ConfigFile(_pD._configFile, defaultServerRoot);
		}
		catch(RuntimeException e)
		{
			_uc.printParagraph(_labels.getString("E4HttpdAudit"));
			_uc.printExample(e.getMessage());
			_uc.finishCommunication();
			System.exit(1);
		}
		catch (IOException e)
		{
			_uc.reportError(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		
		_uc.println("");
		
		_pD._highSec = _uc.askYesNoQuestion( _labels.getString("Q3") , true);
		_pD._highPriv = _uc.askYesNoQuestion( _labels.getString("Q4") , true);
		
		_uc.println("");
		_uc.printParagraph( _labels.getString("P4") );
		_uc.waitForUserToContinue();
		_uc.printHeading1( _labels.getString("H2") );
		
		
		return true;
	}

	private String getDefaultServerRoot(String apacheExec)
	{
		ShellAnsweredQuestion q = new ShellAnsweredQuestion(GET_DEFAULT_SRV_ROOT_COMMAND, apacheExec);
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

	@Override
	public int getBlockNumber()
	{
		return -1;
	}

	@Override
	public String getID()
	{
		return _id;
	}

	@Override
	public int getNumber()
	{
		return 0;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}

	@Override
	public boolean isCritical()
	{
		return false;
	}

}
