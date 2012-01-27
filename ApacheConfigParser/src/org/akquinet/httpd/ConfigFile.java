package org.akquinet.httpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.akquinet.httpd.syntax.Directive;
import org.akquinet.httpd.syntax.Head;

public class ConfigFile implements Serializable
{
	private static final long serialVersionUID = -487133373233062784L;
	
	private transient Head _head;
	private File _file;
	private String _serverRoot;
	
	public ConfigFile(File file) throws ParserException, IOException
	{
		this(file, null);
	}
	
	public ConfigFile(String file) throws ParserException, IOException
	{
		this(new File(file), null);
	}
	
	public ConfigFile(File file, String serverRoot) throws ParserException, IOException
	{
		FileInputStream input = null;
		try
		{
			_file = file;
			_serverRoot = serverRoot;
			input = new FileInputStream(_file);
			_head = new Head(null, new MultipleMarkerInputStream(input), _serverRoot, _file.getCanonicalPath());
		}
		finally
		{
			if(input != null)
				input.close();
		}
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
	public void reparse() throws ParserException, IOException
	{
		FileInputStream input = null;
		try
		{
			input = new FileInputStream(_file);
			_head = new Head(null, new MultipleMarkerInputStream(input), _serverRoot, _file.getCanonicalPath());
		}
		finally
		{
			if(input != null)
				input.close();
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
	
	public String getFileName()
	{
		return _file.getAbsolutePath();
	}
	
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException, ParserException
	{
		s.defaultReadObject();
		FileInputStream input = new FileInputStream(_file);
		_head = new Head(null, new MultipleMarkerInputStream(input), _serverRoot, _file.getCanonicalPath());
	}
}
