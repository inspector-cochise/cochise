package org.akquinet.httpd.syntax;

import java.util.List;

import org.akquinet.httpd.BadSyntaxException;
import org.akquinet.httpd.ParserException;

public class Context extends Statement
{
	private StatementList _containedStatementList;
	private String _name;
	private String _value;
	private int _beginLineNumber;
	private int _endLineNumber;

	public Context(SyntaxElement parent) throws ParserException
	{
		super(parent);
		parse();
	}

	@Override
	protected void parse() throws ParserException
	{
		try
		{
			// //////////////////////////////////////////
			// STAGE ONE _ < _ Name _ Value _ > _ newline
			// blanks at the beginning of the line have already been eaten
			_beginLineNumber = getActualLine();
			
			readNextChar(); // eat '<'
			eatBlanks();
			_name = (new Name(this)).getStringContent();
			eatBlanks();
			_value = (new Parameter(this)).getStringContent();
			eatBlanks();
			eatRightDelimiter();
			eatBlanks();
			eatNewline();
			
			// STAGE ONE END
			// //////////////////////////////////////////
			
			_containedStatementList = new StatementList(this);
			
			// //////////////////////////////////////////
			// STAGE TWO _ </ _ Name _ > _ newline
			
			eatBlanks();
			eatEndBlockLeftDelimiter();
		}
		catch (FileEndException e)
		{
			throw new BadSyntaxException("Syntax Error. Unexpected end of file. Seems like you started some context but did not finish it.");
		}
		
		eatBlanks();
		String tmp = (new Name(this)).getStringContent();
		if (!_name.equals(tmp))
		{
			throw new BadSyntaxException("Syntax Error. Expecting closing block for " + _name + " in line " + getActualLine() + ".");
		}
		
		eatBlanks();
		
		try
		{
			eatRightDelimiter();
			eatBlanks();
		}
		catch(FileEndException e)
		{
			return;
		}
		finally
		{
			_endLineNumber = getActualLine();
		}
		
		
		try
		{
			eatNewline();
		}
		catch(FileEndException e)
		{
		}

		// STAGE TWO END
		// //////////////////////////////////////////
	}

	private void eatEndBlockLeftDelimiter() throws ParserException, FileEndException
	{
		if (getActualChar() != '<')
		{
			throw new RuntimeException("Syntax Error. Expecting closing block for " + _name + " in line " + getActualLine() + ".");
		}
		readNextChar();
		if (getActualChar() != '/')
		{
			throw new RuntimeException("Syntax Error. Expecting closing block for " + _name + " in line " + getActualLine() + ".");
		}
		readNextChar();
	}

	private void eatRightDelimiter() throws ParserException, FileEndException
	{
		if (getActualChar() != '>')
		{
			throw new RuntimeException("Syntax Error. Missing '>' in line " + getActualLine() + ".");
		}
		readNextChar();
	}

	private void eatNewline() throws ParserException, FileEndException
	{
		if (getActualChar() == '\n')
		{
			readNextChar();
		}
		else if (getActualChar() == '\r' && lookForward() == '\n')
		{
			readNextChar();
			try
			{
				readNextChar();
			}
			catch (FileEndException e)
			{
			} // file ends directly with '\n'
		}
		else
		{
			throw new RuntimeException("Syntax Error. Newline Expected after '>' .");
		}
	}
	
	public StatementList getContainedStatementList()
	{
		return _containedStatementList;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getNameLowerCase()
	{
		return _name.toLowerCase();
	}
	
	public String getValue()
	{
		return _value;
	}

	public int getBeginLineNumber()
	{
		return _beginLineNumber;
	}

	public int getEndLineNumber()
	{
		return _endLineNumber;
	}

	@Override
	public List<Directive> getDirective(String name)
	{
		return _containedStatementList.getDirective(name);
	}
	
	@Override
	public List<Directive> getDirectiveIgnoreCase(String name)
	{
		return _containedStatementList.getDirectiveIgnoreCase(name);
	}

	@Override
	public List<Directive> getAllDirectives(String name)
	{
		return _containedStatementList.getAllDirectives(name);
	}

	@Override
	public List<Directive> getAllDirectivesIgnoreCase(String name)
	{
		return _containedStatementList.getAllDirectivesIgnoreCase(name);
	}
}
