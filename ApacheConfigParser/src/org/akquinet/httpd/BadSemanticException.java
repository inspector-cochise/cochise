package org.akquinet.httpd;

public class BadSemanticException extends ParserException
{
	public BadSemanticException()
	{
		super();
	}
	
	public BadSemanticException(Exception e)
	{
		super(e);
	}
	
	public BadSemanticException(String msg)
	{
		super(msg);
	}
	
	public BadSemanticException(String msg, Exception e)
	{
		super(msg, e);
	}

	private static final long serialVersionUID = 1972911384620551222L;
}
