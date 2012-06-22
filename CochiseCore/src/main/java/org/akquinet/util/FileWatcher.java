package org.akquinet.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Watches a file (or directory) for changes. Any change of content or permissions are treated as change.
 * For a directory adding data into it will be treated as change. 
 * @author immanuel
 */
public class FileWatcher extends ResourceWatcher implements Serializable
{
	private static final long serialVersionUID = -8293181465534551233L;
	
	private static final String GETCTIME_SCRIPT = "getCTime.sh";
	private static String SCRIPT_PATH = "./";
	private File _watchedFile;
	private long _ctime;
	
	public FileWatcher(String fileToWatch) throws FileNotFoundException
	{
		this(new File(fileToWatch));
	}
	
	public FileWatcher(File fileToWatch) throws FileNotFoundException
	{
		if(!fileToWatch.exists())
		{
			throw new FileNotFoundException(fileToWatch.getPath());
		}
		
		_watchedFile = fileToWatch;
		_ctime = getCTime(_watchedFile);
	}

	@Override
	public String getResourceId()
	{
		try
		{
			return _watchedFile.getCanonicalPath();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean resourceChanged()
	{
		boolean ret;
		if(_ctime != getCTime(_watchedFile))
		{
			ret = true;
		}
		else
		{
			ret = false;
		}

		_ctime = getCTime(_watchedFile);
		return ret;
	}
	
	private static long getCTime(File file)
	{
		try
		{
			Process p = (new ProcessBuilder(SCRIPT_PATH + GETCTIME_SCRIPT, file.getAbsolutePath())).start();
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
			
			StringBuilder builder = new StringBuilder();
			int c = stdOut.read();
			while(c >= 0)
			{
				builder.append((char)c);
				c = stdOut.read();
			}
			
			return Long.parseLong(builder.toString().replaceAll("\n", "").replaceAll("\r", ""));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static void setScriptPath(String scriptPath)
	{
		SCRIPT_PATH = scriptPath;
	}
}
