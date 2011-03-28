package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest2 implements YesNoQuestion
{
	private static final String _id = "Quest2";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private ProcessBuilder _httpd;
	
	public Quest2(File apacheExecutable)
	{
		try
		{
			_httpd = new ProcessBuilder(apacheExecutable.getCanonicalPath(), "-v");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		Process p;
		String version = "(couldn't retrieve version information)";
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
		_uc.println("Version information of your apache httpd:\n" + version);
		boolean ret = _uc.askYesNoQuestion("Is your apache httpd well patched and have you checked that recently?");
		_uc.printAnswer(ret, ret ? "Well done."
				: "Please check for new patches and install them.");
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
