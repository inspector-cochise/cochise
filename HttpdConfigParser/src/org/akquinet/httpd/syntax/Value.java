package org.akquinet.httpd.syntax;

import java.io.IOException;

public class Value extends SyntaxElement
{
	private String _content = "";
	
	public Value(SyntaxElement parent) throws IOException, FileEndException
	{
		super(parent);
		parse();
	}

	@Override
	protected void parse() throws IOException, FileEndException
	{
		StringBuffer buf = new StringBuffer();
		while(allowedCharInValue(getActualChar()))
		{
			buf.append(getActualChar());
			readNextChar();
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
