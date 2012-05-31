package org.akquinet.httpd;


public class ParserException extends Exception
{
	public ParserException()
	{
		super();
	}
	
	public ParserException(Exception e)
	{
		super(e);
	}
	
	public ParserException(String msg)
	{
		super(msg);
	}
	
	public ParserException(String msg, Exception e)
	{
		super(msg, e);
	}

	private static final long serialVersionUID = -2830752690161216799L;
}
