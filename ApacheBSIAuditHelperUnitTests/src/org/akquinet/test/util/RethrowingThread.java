package org.akquinet.test.util;

import java.lang.Thread.UncaughtExceptionHandler;

public abstract class RethrowingThread extends Thread implements UncaughtExceptionHandler
{
	Throwable _exc;
	
	public RethrowingThread()
	{
		super();
		_exc = null;
		setUncaughtExceptionHandler(this);
	}
	
	public RethrowingThread(Runnable r)
	{
		super(r);
		_exc = null;
		setUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
		_exc = e;
	}
	
	public boolean throwableCaught()
	{
		return _exc != null;
	}
	
	public Throwable getCaughtThrowable()
	{
		return _exc;
	}
	
	/**
	 * Checks whether any Throwable has been caught and if so rethrows it.
	 * @throws Throwable
	 */
	public void throwCaughtThrowable() throws Throwable
	{
		if(!throwableCaught())
		{
			return;
		}
		
		Throwable t = getCaughtThrowable();
		if(t instanceof Exception)
		{
			throw (Exception)t;
		}
		else if(t instanceof Error)
		{
			throw (Error)t;
		}
		else
		{
			throw t;
		}
	}
}
