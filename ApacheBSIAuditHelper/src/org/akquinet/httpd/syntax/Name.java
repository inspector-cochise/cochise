package org.akquinet.httpd.syntax;

import java.io.IOException;

public class Name extends SyntaxElement
{
	private String _content = "";
	
	public Name(SyntaxElement parent) throws IOException, FileEndException
	{
		super(parent, parent.getContainingFile());
		parse();
	}

	@Override
	protected void parse() throws IOException, FileEndException
	{
		StringBuffer buf = new StringBuffer();
		while(allowedCharInName(getActualChar()))
		{
			buf.append(getActualChar());
			readNextChar();
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
