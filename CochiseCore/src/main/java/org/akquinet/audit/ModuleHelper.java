package org.akquinet.audit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.Serializable;
import java.util.List;

import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.akquinet.httpd.syntax.Directive;

public class ModuleHelper implements Serializable
{
	private static final long serialVersionUID = -6541226395865237818L;
	private ConfigFile _conf;
	private ProcessBuilder _httpd;
	
	public ModuleHelper(ConfigFile conf)
	{
		_conf = conf;
		_httpd = null;
	}
	
	public ModuleHelper(ConfigFile conf, File apacheExecutable)
	{
		_conf = conf;
		
		try
		{
			_httpd = new ProcessBuilder(apacheExecutable.getCanonicalPath(), "-l");
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected List<Directive> getLoadModuleList()
	{
		return _conf.getDirectiveIgnoreCase("LoadModule");
	}
	
	protected List<Directive> getLoadFileList()
	{
		return _conf.getDirectiveIgnoreCase("LoadFile");
	}

	protected String[] getCompiledIntoModulesList()
	{
		try
		{
			Process p = _httpd.start();
			InputStream stdOut = p.getInputStream();
			
			StringBuffer buf = new StringBuffer();
			int b;
			Thread.sleep(100);
			while(stdOut.available() >= 1)
			{
				b = stdOut.read();
				buf.append((char)b);
			}
			p.waitFor();
			
			String output = buf.toString();
			String[] modList = output.split("(\r\n|\n)");
			return modList;
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
	
	protected void reparse() throws ParserException, IOException
	{
		_conf.reparse();
	}
	
	private synchronized void writeObject( java.io.ObjectOutputStream s ) throws IOException
	{
		s.writeLong(serialVersionUID);
		s.writeObject(_conf);
		s.writeBoolean(_httpd != null);
		if(_httpd != null)
		{
			s.writeObject(_httpd.command());
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		if(s.readLong() != serialVersionUID)
		{
			throw new InvalidClassException("Trying to deserialize an object but it's serialVersionUID doesn't match the implementation.");
		}
		
		_conf = (ConfigFile) s.readObject();
		if(s.readBoolean())
		{
			_httpd = new ProcessBuilder((List<String>) s.readObject());
		}
		
	}
}
