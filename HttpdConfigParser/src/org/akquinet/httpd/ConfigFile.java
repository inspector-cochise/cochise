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
	
	public ConfigFile(File file) throws IOException
	{
		FileInputStream input = new FileInputStream(file);
		_head = new Head(null, new MultipleMarkerInputStream(input));
	}
	
	public List<Directive> getDirective(String name)
	{
		return _head.getDirective(name);
	}
	
	public List<Directive> getDirectiveIgnoreCase(String name)
	{
		return _head.getDirectiveIgnoreCase(name);
	}
	
	public Head getHeadExpression()
	{
		return _head;
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
