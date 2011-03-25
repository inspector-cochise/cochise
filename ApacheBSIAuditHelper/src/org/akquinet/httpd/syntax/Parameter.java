package org.akquinet.httpd.syntax;

import java.io.IOException;

public class Parameter extends SyntaxElement
{
	private String _content = "";

	public Parameter(SyntaxElement parent) throws IOException
	{
		super(parent);
		parse();
	}

	@Override
	protected void parse() throws IOException
	{
		mark(1024);
		int i = 0;
		int j = 0;
		while (getActualChar() != '\n')
		{
			if (getActualChar() == '>')
			{
				j = i;
			}

			try
			{
				i++;
				readNextChar();
			}
			catch (FileEndException e)
			{
				throw new RuntimeException("Syntax Error. Unexpected end of file.");
			}
		}
		reset();

		StringBuffer buf = new StringBuffer();
		try
		{
			for (int k = 0; k < j; k++)
			{
				buf.append(getActualChar());
				readNextChar();
			}
			//readNextChar(); // so that getActualChar() == '>'
		}
		catch (FileEndException e)
		{
		} // this can not happen

		_content = buf.toString();
	}

	public String getStringContent()
	{
		return _content;
	}
}
