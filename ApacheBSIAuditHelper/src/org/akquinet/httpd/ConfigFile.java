package org.akquinet.httpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.akquinet.httpd.syntax.Directive;
import org.akquinet.httpd.syntax.Head;

public class ConfigFile
{
	private Head _head;
	private File _file;
	private long _fileLastModified;
	private String _serverRoot;
	
	public ConfigFile(File file) throws IOException
	{
		this(file, null);
	}
	
	public ConfigFile(String file) throws IOException
	{
		this(new File(file), null);
	}
	
	public ConfigFile(File file, String serverRoot) throws IOException
	{
		_file = file;
		_fileLastModified = _file.lastModified();
		_serverRoot = serverRoot;
		FileInputStream input = new FileInputStream(_file);
		_head = new Head(null, new MultipleMarkerInputStream(input), _serverRoot, _file.getCanonicalPath());
	}

	/**
	 * 
	 * @param name name of the directive
	 * @return list of all directives with this name directly contained in root context
	 */
	public List<Directive> getDirective(String name)
	{
		return _head.getDirective(name);
	}
	
	/**
	 * 
	 * @param name name of the directive
	 * @return list of all directives with this name directly contained in root context
	 */
	public List<Directive> getDirectiveIgnoreCase(String name)
	{
		return _head.getDirectiveIgnoreCase(name);
	}
	
	/**
	 * 
	 * @param name name of the directive
	 * @return list of all directives with this name (indepent of context)
	 */
	public List<Directive> getAllDirectives(String name)
	{
		return _head.getAllDirectives(name);
	}
	
	/**
	 * 
	 * @param name name of the directive
	 * @return list of all directives with this name (indepent of context)
	 */
	public List<Directive> getAllDirectivesIgnoreCase(String name)
	{
		return _head.getAllDirectivesIgnoreCase(name);
	}
	
	public Head getHeadExpression()
	{
		return _head;
	}
	
	/**
	 * If the file has been modified since the creation of this object (or the last call to this method) it will
	 * again read the config-file and parse is (i. e. this object will be aware of any changes made to the file).
	 * @throws IOException
	 */
	public void reparse() throws IOException
	{
		long tmp = _file.lastModified();
		if(tmp > _fileLastModified)
		{
			_fileLastModified = tmp;
			FileInputStream input = new FileInputStream(_file);
			_head = new Head(null, new MultipleMarkerInputStream(input), _serverRoot, _file.getCanonicalPath());
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			File file = new File(args[0]);
			new ConfigFile(file);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
