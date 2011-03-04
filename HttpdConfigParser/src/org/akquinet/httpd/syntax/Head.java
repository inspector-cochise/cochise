package org.akquinet.httpd.syntax;

import java.io.IOException;
import java.util.List;

import org.akquinet.httpd.MultipleMarkerInputStream;

public class Head extends SyntaxElement
{
	private StatmentList _statements;
	
	public Head(SyntaxElement parent, MultipleMarkerInputStream text) throws IOException
	{
		super(parent, text);
		parse();
	}

	@Override
	protected void parse() throws IOException
	{
		_statements = new StatmentList(this);
	}

	public StatmentList getStatements()
	{
		return _statements;
	}

	public List<Directive> getDirective(String name)
	{
		return _statements.getDirective(name);
	}
	
	public List<Directive> getDirectiveIgnoreCase(String name)
	{
		return _statements.getDirectiveIgnoreCase(name);
	}

	public List<Directive> getAllDirectives(String name)
	{
		return _statements.getAllDirectives(name);
	}
	
	public List<Directive> getAllDirectivesIgnoreCase(String name)
	{
		return _statements.getAllDirectivesIgnoreCase(name);
	}
}
