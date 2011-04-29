package org.akquinet.httpd.syntax;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

abstract public class Statement extends SyntaxElement
{
	public Statement(SyntaxElement parent) throws IOException
	{
		super(parent, parent.getContainingFile());
		//nothing to do
	}

	abstract public List<Directive> getDirective(String name);
	abstract public List<Directive> getDirectiveIgnoreCase(String name);
	abstract public List<Directive> getAllDirectives(String name);
	abstract public List<Directive> getAllDirectivesIgnoreCase(String name);
	
	/**
	 * Every Statement has Contexts in it's parental hierarchy or is global. All these surrounding Contexts shall be found
	 * by this function.
	 * @return All surrounding Contexts including the global Context (which is represented by null) sorted inner to outer ones. So the last Element will always be null.
	 */
	public List<Context> getSurroundingContexts()
	{
		LinkedList<Context> ret = new LinkedList<Context>();
		
		SyntaxElement ele = getParent();
		while(ele != null)
		{
			if(ele instanceof Context)
			{
				ret.add((Context)ele);
			}
			ele = ele.getParent();
		}
		
		ret.add(null);
		return ret;
	}
}
