package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.UserCommunicator;

@Automated
public class Quest6 implements YesNoQuestion
{
	private static final long serialVersionUID = 1923926407490053986L;
	
	private static final String _id = "Quest6";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();

	private transient ProcessBuilder _httpd;
	private transient ResourceBundle _labels;
	
	public Quest6(PrologueData pd)
	{
		this(pd._apacheExecutable);
	}
	
	public Quest6(PrologueData pd, UserCommunicator uc)
	{
		this(pd._apacheExecutable, uc);
	}
	
	public Quest6(File apacheExecutable)
	{
		this(apacheExecutable, UserCommunicator.getDefault());
	}
	
	public Quest6(File apacheExecutable, UserCommunicator uc)
	{
		_uc = uc;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		try
		{
			_httpd = new ProcessBuilder(apacheExecutable.getCanonicalPath(), "-t");
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Looking for AllowOverride-directives. Checking whether there is no with parameters other than None and at least one
	 * in global context with parameter None.
	 */
	@Override
	public boolean answer()
	{
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( _labels.getString("Q0") );
		
		try
		{
			Process p = _httpd.start();
			InputStream stdErr = p.getErrorStream();
			int exit = p.waitFor();
			
			if(exit == 0)
			{
				_uc.printAnswer(true, _labels.getString("S1") );
				printExtraInfo();
				return true;
			}
			else
			{
				_uc.printAnswer(false, _labels.getString("S2") );
				StringBuffer buf = new StringBuffer();
				int b = stdErr.read();
				while(b != -1)
				{
					buf.append((char)b);
					b = stdErr.read();
				}
				_uc.printExample(buf.toString());
				
				_uc.printParagraph( _labels.getString("S5") );
				printExtraInfo();
				return false;
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void printExtraInfo()
	{
		_uc.beginHidingParagraph(_labels.getString("S3"));
			_uc.printParagraph(_labels.getString("S4"));
		_uc.endHidingParagraph();
	}

	@Override
	public boolean isCritical()
	{
		return true;
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
		return 6;
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
	}
	
	@SuppressWarnings("unchecked")
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		_httpd = new ProcessBuilder((List<String>) s.readObject());
		
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
