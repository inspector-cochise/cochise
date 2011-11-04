package org.akquinet.httpd.syntax;

import org.akquinet.httpd.ParserException;

/**
 * A directive is a pair of name and value like
 * Options +Indexes -FollowSymLinks
 * This class represents the value. Which is "+Indexes -FollowSymLinks" in the above example.
 * 
 * @author immanuel
 *
 */
public class Value extends SyntaxElement
{
	private String _content = "";
	
	public Value(SyntaxElement parent) throws ParserException
	{
		super(parent, parent.getContainingFile());
		parse();
	}

	@Override
	protected void parse() throws ParserException
	{
		StringBuffer buf = new StringBuffer();
		while(allowedCharInValue(getActualChar()))
		{
			buf.append(getActualChar());
			try
			{
				readNextChar();
			}
			catch (FileEndException e)
			{
				//Looks like a directive at eof with no newline before eof. This is ok.
			}
		}
		_content = buf.toString();
	}

	private boolean allowedCharInValue(char c)
	{
		boolean ret = true;
		
		switch(c)
		{
		case '\r':
		case '\n':
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
