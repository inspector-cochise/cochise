package org.akquinet.httpd.syntax;

import java.io.IOException;

import org.akquinet.httpd.MultipleMarkerInputStream;
import org.akquinet.httpd.ParserException;

abstract public class SyntaxElement
{
	private SyntaxElement _parent;
	private MultipleMarkerInputStream _text;
	private char _actualChar;
	private char _markedChar;
	private int _actualLine;
	private String _containingFile;
	private int _markedLine;
	private String _serverRoot;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            set this to null if it is the root Element
	 * @throws ParserException 
	 */
	public SyntaxElement(SyntaxElement parent) throws ParserException
	{
		this(parent, null, null);
	}

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            set this to null if it is the root Element
	 * @throws ParserException 
	 */
	public SyntaxElement(SyntaxElement parent, String containingFile) throws ParserException
	{
		this(parent, null, containingFile);
	}
	
	/**
	 * Constructor for root Element
	 * 
	 * @param parent
	 *            set this to null if it is the root Element
	 * @param text
	 *            the text which should be parsed, only necessary for the root
	 *            element
	 * @throws ParserException 
	 */
	public SyntaxElement(SyntaxElement parent, MultipleMarkerInputStream text, String containingFile) throws ParserException
	{
		_serverRoot = null;
		_parent = parent;
		_text = text;
		if (parent == null)
		{
			int tmp;
			try
			{
				tmp = _text.read();
			}
			catch (IOException e)
			{
				throw new ParserException(e);
			}
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
		
		_containingFile = containingFile;
	}

	protected char getActualChar()
	{
		return _parent == null ? _actualChar : _parent.getActualChar();
	}
	
	protected int getActualLine()
	{
		return _parent == null ? _actualLine : _parent.getActualLine();
	}
	
	public String getContainingFile()
	{
		return _containingFile;
	}
	
	protected boolean available() throws ParserException
	{
		boolean ret;
		if (_parent != null)
		{
			ret = _parent.available();
		}
		else
		{
			try
			{
				_text.mark(1);
				int tmp = _text.read();
				_text.reset();
				if (tmp == -1)
				{
					ret = false;
				}
				else
				{
					ret = true;
				}
			}
			catch (IOException e)
			{
				throw new ParserException(e);
			}
		}
		return ret;
	}

	protected void readNextChar() throws FileEndException, ParserException
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
			try
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
			catch (IOException e)
			{
				throw new ParserException(e);
			}
		}
	}

	protected char lookForward() throws ParserException, FileEndException
	{
		if (_parent != null)
		{
			return _parent.lookForward();
		}
		else
		{
			try
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
			catch (IOException e)
			{
				throw new ParserException(e);
			}
		}
	}

	protected char look2Forward() throws ParserException, FileEndException
	{
		if (_parent != null)
		{
			return _parent.look2Forward();
		}
		else
		{
			try
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
			catch(IOException e)
			{
				throw new ParserException(e);
			}
		}
	}

	protected void eatBlanks() throws ParserException
	{
		try
		{
			while (isBlank(getActualChar()))
			{
				readNextChar();
			}
		}
		catch(FileEndException e)
		{
			return;
		}

	}

	protected boolean isBlank(char c)
	{
		return  c == ' ' || c == '\t';
	}

	protected void eatRestOfLine() throws ParserException, FileEndException
	{
		while (getActualChar() != '\n')
		{
			readNextChar();
		}
		readNextChar(); // eat '\n'
	}

	protected void eatLineAppender() throws ParserException, FileEndException
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

	protected void reset() throws ParserException
	{
		if (_parent != null)
		{
			_parent.reset();
		}
		else
		{
			try
			{
				_text.reset();
			}
			catch (IOException e)
			{
				throw new ParserException(e);
			}
			finally
			{
				_actualChar = _markedChar;
				_actualLine = _markedLine;
			}
		}
	}
	
	protected void setServerRoot(String serverRoot)
	{
		if(serverRoot != null && serverRoot.startsWith("\"") && serverRoot.endsWith("\""))
		{
			serverRoot = serverRoot.substring(1, serverRoot.length()-1);
		}
		
		_serverRoot = serverRoot;
		if(_parent != null)
		{
			_parent.setServerRoot(serverRoot);
		}
	}
	
	public String getServerRoot()
	{
		if(_parent != null)
		{
			return _parent.getServerRoot();
		}
		else
		{
			return _serverRoot;
		}
	}
	
	protected class FileEndException extends Exception
	{
		private static final long serialVersionUID = 7146223868534182633L;
	}
	
	abstract protected void parse() throws ParserException;
}
