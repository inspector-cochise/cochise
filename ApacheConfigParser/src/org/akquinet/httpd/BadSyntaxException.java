package org.akquinet.httpd;

public class BadSyntaxException extends ParserException
{
	public BadSyntaxException()
	{
		super();
	}
	
	public BadSyntaxException(Exception e)
	{
		super(e);
	}
	
	public BadSyntaxException(String msg)
	{
		super(msg);
	}
	
	public BadSyntaxException(String msg, Exception e)
	{
		super(msg, e);
	}
	
	private static final long serialVersionUID = 4002850761011362285L;
}
