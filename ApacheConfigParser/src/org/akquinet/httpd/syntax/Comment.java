package org.akquinet.httpd.syntax;

import java.util.LinkedList;
import java.util.List;

import org.akquinet.httpd.ParserException;

public class Comment extends Statement
{
	private int _linenumber;
	
	public Comment(SyntaxElement parent) throws ParserException
	{
		super(parent);
		_linenumber = getActualLine();
		parse();
	}
	
	protected void parse() throws ParserException
	{
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

	@Override
	public List<Directive> getAllDirectives(String name)
	{
		return new LinkedList<Directive>();
	}

	@Override
	public List<Directive> getAllDirectivesIgnoreCase(String name)
	{
		return new LinkedList<Directive>();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		
		if(!(o instanceof Comment))
		{
			return false;
		}
		else
		{
			return true;	//the content or position of comments doesn't matter
		}
	}
	
	@Override
	public int hashCode()
	{
		return 1;	//the content or position of comments doesn't matter
	}
}
