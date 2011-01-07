package org.akquinet.httpd.syntax;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Comment extends Statement
{
	private int _linenumber;
	
	public Comment(SyntaxElement parent) throws IOException
	{
		super(parent);
		_linenumber = getActualLine();
		parse();
	}
	
	protected void parse() throws IOException
	{
		if(getActualChar() != '#')
		{
			throw new RuntimeException("Internal error while Parsing apache config file.");
		}
		try
		{
			eatRestOfLine();
		}
		catch (FileEndException e)
		{
			//that's ok an expected to happen
			return;
		}
	}
	
	public int getLinenumber()
	{
		return _linenumber;
	}

	@Override
	public List<Directive> getDirective(String name)
	{
		return new LinkedList<Directive>();
	}

	@Override
	public List<Directive> getDirectiveIgnoreCase(String name)
	{
		return new LinkedList<Directive>();
	}
}
