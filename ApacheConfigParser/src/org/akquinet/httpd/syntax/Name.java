package org.akquinet.httpd.syntax;

import org.akquinet.httpd.ParserException;

/**
 * A directive is a pair of name and value like
 * Options None
 * And any context has a name and a parameter
 * This class represents the name. Which is "Options" in the above example.
 * 
 * @author immanuel
 *
 */
public class Name extends SyntaxElement
{
	private String _content = "";
	
	public Name(SyntaxElement parent) throws ParserException
	{
		super(parent, parent.getContainingFile());
		parse();
	}

	@Override
	protected void parse() throws ParserException
	{
		StringBuffer buf = new StringBuffer();
		while(allowedCharInName(getActualChar()))
		{
			buf.append(getActualChar());
			try
			{
				readNextChar();
			}
			catch (FileEndException e)
			{
				//If this is the name of a directive, this is not ok but will be noticed by Context itself.
				//Looks like a parameterless directive at eof with no newline before eof. This is ok.
			}
		}
		_content = buf.toString();
	}

	private boolean allowedCharInName(char c)
	{
		boolean ret = true;
		
		switch(c)
		{
		case ' ':
		case '\t':
		case '\r':
		case '\n':
		case '/':
		case '<':
		case '>':
		case '\\':
			ret = false;
			break;
		default:
			break;
		}
		
		return ret;
	}

	public String getStringContent()
	{
		return _content;
	}
}
