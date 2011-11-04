package org.akquinet.httpd.syntax;

import java.util.List;

import org.akquinet.httpd.MultipleMarkerInputStream;
import org.akquinet.httpd.ParserException;

public class Head extends SyntaxElement
{
	private StatementList _statements;
	
	public Head(SyntaxElement parent, MultipleMarkerInputStream text, String serverRoot, String containingFile) throws ParserException
	{
		super(parent, text, containingFile);
		setServerRoot(serverRoot);
		parse();
	}

	@Override
	public void parse() throws ParserException
	{
		_statements = new StatementList(this);
	}

	public StatementList getStatements()
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
