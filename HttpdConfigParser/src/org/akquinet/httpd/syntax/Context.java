package org.akquinet.httpd.syntax;

import java.io.IOException;
import java.util.List;

public class Context extends Statement
{
	private StatmentList _containedStatementList;
	private String _name;
	private String _value;
	private int _beginLineNumber;
	private int _endLineNumber;

	public Context(SyntaxElement parent) throws IOException, FileEndException
	{
		super(parent);
		parse();
	}

	@Override
	protected void parse() throws IOException, FileEndException
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

		// STAGE TWO END
		// //////////////////////////////////////////

		_containedStatementList = new StatmentList(this);

		// //////////////////////////////////////////
		// STAGE TWO _ </ _ Name _ > _ newline

		eatBlanks();
		eatEndBlockLeftDelimiter();
		eatBlanks();
		String tmp = (new Name(this)).getStringContent();
		if (!_name.equals(tmp))
		{
			throw new RuntimeException("Syntax Error. Expecting closing block for " + _name + " in line " + getActualLine() + ".");
		}
		eatBlanks();
		eatRightDelimiter();
		eatBlanks();
		
		_endLineNumber = getActualLine();
		try
		{
			eatNewline();
		}
		catch(FileEndException e)
		{
			//actually this can happen and is ok
		}

		// STAGE TWO END
		// //////////////////////////////////////////
	}

	private void eatEndBlockLeftDelimiter() throws IOException, FileEndException
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

	private void eatRightDelimiter() throws IOException, FileEndException
	{
		if (getActualChar() != '>')
		{
			throw new RuntimeException("Syntax Error. Missing '>' in line " + getActualLine() + ".");
		}
		readNextChar();
	}

	private void eatNewline() throws IOException, FileEndException
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
	
	public StatmentList getContainedStatementList()
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
