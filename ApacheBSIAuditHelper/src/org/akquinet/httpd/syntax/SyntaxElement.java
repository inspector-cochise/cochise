package org.akquinet.httpd.syntax;

import java.io.IOException;

import org.akquinet.httpd.MultipleMarkerInputStream;

abstract public class SyntaxElement
{
	protected static final String UNEXPECTED_EOF_ERROR_STRING = "Unexpected end of file. Please check Syntax.";
	private SyntaxElement _parent;
	private MultipleMarkerInputStream _text;
	private char _actualChar;
	private char _markedChar;
	private int _actualLine;
	private int _markedLine;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            set this to null if it is the root Element
	 */
	public SyntaxElement(SyntaxElement parent) throws IOException
	{
		this(parent, null);
	}

	/**
	 * Constructor for root Element
	 * 
	 * @param parent
	 *            set this to null if it is the root Element
	 * @param text
	 *            the text which should be parsed, only necessary for the root
	 *            element
	 */
	public SyntaxElement(SyntaxElement parent, MultipleMarkerInputStream text) throws IOException
	{
		_parent = parent;
		_text = text;
		if (parent == null)
		{
			int tmp = _text.read();
			if (tmp == -1)
			{
				return;
			}
			else
			{
				_actualChar = (char) tmp;
				_actualLine = 1;
			}
		}
		else
		{
			_actualChar = parent._actualChar;
			_actualLine = parent._actualLine;
		}
	}

	protected char getActualChar()
	{
		return _parent == null ? _actualChar : _parent.getActualChar();
	}
	
	protected int getActualLine()
	{
		return _parent == null ? _actualLine : _parent.getActualLine();
	}

	protected void readNextChar() throws IOException, FileEndException
	{
		if (_parent != null)
		{
			try
			{
				_parent.readNextChar();
			}
			finally
			{
				_actualChar = _parent._actualChar;
				_actualLine = _parent._actualLine;
			}
		}
		else
		{
			int tmp = _text.read();
			if (tmp == -1)
			{
				_actualChar = '\n';	//this is the most harmless character
				throw new FileEndException();
			}
			_actualChar = (char) tmp;
			if(_actualChar == '\n')
			{
				_actualLine++;
			}
			
			eatLineAppender();
		}
	}

	protected char lookForward() throws IOException, FileEndException
	{
		if (_parent != null)
		{
			return _parent.lookForward();
		}
		else
		{
			_text.mark(1);
			int tmp = _text.read();
			_text.reset();
			if (tmp == -1)
			{
				throw new FileEndException();
			}
			return (char) tmp;
		}
	}

	protected char look2Forward() throws IOException, FileEndException
	{
		if (_parent != null)
		{
			return _parent.look2Forward();
		}
		else
		{
			_text.mark(2);
			_text.read();
			int tmp = _text.read();
			_text.reset();
			if (tmp == -1)
			{
				throw new FileEndException();
			}
			return (char) tmp;
		}
	}

	protected void eatBlanks() throws IOException, FileEndException
	{
		while (isBlank(getActualChar()))
		{
			readNextChar();
		}

	}

	protected boolean isBlank(char c)
	{
		return  c == ' ' || c == '\t';
	}

	protected void eatRestOfLine() throws IOException, FileEndException
	{
		while (getActualChar() != '\n')
		{
			readNextChar();
		}
		readNextChar(); // eat '\n'
	}

	protected void eatLineAppender() throws IOException, FileEndException
	{
		if (getActualChar() == '\\')
		{
			try
			{
				if (lookForward() == '\n')
				{
					readNextChar();
					readNextChar(); // eat both '\\' and '\n'
				}
				else if (lookForward() == '\r' && look2Forward() == '\n')
				{
					readNextChar();
					readNextChar();
					readNextChar(); // eat em all '\\' and '\r' and '\n'
				}
			}
			catch (FileEndException e)
			{
			} // this backslash wasn't supposed to append the next line to this
				// one
		}
	}

	protected SyntaxElement getParent()
	{
		return _parent;
	}

	protected void mark(int arg0)
	{
		if (_parent != null)
		{
			_parent.mark(arg0);
		}
		else
		{
			_text.mark(arg0);
			_markedChar = getActualChar();
			_markedLine = getActualLine();
		}
	}

	protected void reset() throws IOException
	{
		if (_parent != null)
		{
			_parent.reset();
		}
		else
		{
			_text.reset();
			_actualChar = _markedChar;
			_actualLine = _markedLine;
		}
	}
	
	abstract protected void parse() throws IOException, FileEndException;
}
