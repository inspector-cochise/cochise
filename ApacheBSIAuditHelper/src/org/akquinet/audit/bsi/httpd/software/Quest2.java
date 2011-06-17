package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest2 implements YesNoQuestion
{
	private static final String _id = "Quest2";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private ProcessBuilder _httpd;
	private ResourceBundle _labels;
	
	public Quest2(File apacheExecutable)
	{
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
		try
		{
			_httpd = new ProcessBuilder(apacheExecutable.getCanonicalPath(), "-v");
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		Process p;
		String version = _labels.getString("S1");
		try
		{
			p = _httpd.start();
			InputStream stdOut = p.getInputStream();
			p.waitFor();
			
			StringBuffer buf = new StringBuffer();
			int b = stdOut.read();
			while(b != -1)
			{
				buf.append((char)b);
				b = stdOut.read();
			}
			version = buf.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		_uc.println(_labels.getString("S2"));
		_uc.printExample(version);
		boolean ret = _uc.askYesNoQuestion(_labels.getString("Q1"));
		_uc.printAnswer(ret, ret ? _labels.getString("S3_good")
				: _labels.getString("S3_bad"));
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
}
