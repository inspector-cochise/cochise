package org.akquinet.httpd.syntax;

import org.akquinet.httpd.BadSyntaxException;
import org.akquinet.httpd.ParserException;

public class Parameter extends SyntaxElement
{
	private String _content = "";

	public Parameter(SyntaxElement parent) throws ParserException
	{
		super(parent, parent.getContainingFile());
		parse();
	}

	@Override
	protected void parse() throws ParserException
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
				throw new BadSyntaxException("Syntax Error. Unexpected end of file.");
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
			throw new RuntimeException(e);
		}

		_content = buf.toString();
	}

	public String getStringContent()
	{
		return _content;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		
		if(!(o instanceof Parameter))
		{
			return false;
		}
		else
		{
			Parameter rhs = (Parameter) o;
			return (_content == null ? rhs._content == null : _content.equals(rhs._content))
					&& super.equals(rhs);
		}
	}
	
	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + (_content == null ? 0 : _content.hashCode());
	}
}
