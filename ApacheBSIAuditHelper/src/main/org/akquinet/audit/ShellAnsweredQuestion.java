package org.akquinet.audit;

import java.io.IOException;
import java.io.InputStream;


public class ShellAnsweredQuestion
{
	private ProcessBuilder _command;
	private InputStream _stdOut;
	private InputStream _stdErr;

	public ShellAnsweredQuestion(String... commands)
	{
		_command = new ProcessBuilder(commands);
	}

	public boolean answer()
	{
		int retVal = 1;
		try
		{
			Process p = _command.start();
			_stdOut = p.getInputStream();
			_stdErr = p.getErrorStream();

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
			
			retVal = p.exitValue();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return retVal == 0;
	}
	
	public InputStream getStdOut()
	{
		return _stdOut;
	}
	
	public InputStream getStdErr()
	{
		return _stdErr;
	}
}
