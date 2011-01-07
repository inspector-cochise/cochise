package org.akquinet.httpd.syntax;

import java.io.IOException;
import java.util.List;

abstract public class Statement extends SyntaxElement
{
	public Statement(SyntaxElement parent) throws IOException
	{
		super(parent);
		//nothing to do
	}

	abstract public List<Directive> getDirective(String name);
	abstract public List<Directive> getDirectiveIgnoreCase(String name);
}
