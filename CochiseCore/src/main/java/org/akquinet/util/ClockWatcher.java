package org.akquinet.util;

import java.io.Serializable;

/**
 * Watches the clock and marks itself changed after xxx milliseconds. After xxx milliseconds resourceChanged was called it will again mark itself changed.
 * @author immanuel
 *
 */
public class ClockWatcher extends ResourceWatcher implements Serializable
{
	private static final long serialVersionUID = 4483635222757532087L;
	
	private String _resourceId = "clock";
	private long _mark;
	private int _interval;

	public ClockWatcher()
	{
		this(500);
	}
	
	public ClockWatcher(int millis)
	{
		_resourceId = "clock." + millis + "ms";
		_interval = millis;
		_mark = System.currentTimeMillis();
	}
	
	@Override
	public String getResourceId()
	{
		return _resourceId;
	}

	@Override
	public boolean resourceChanged()
	{
		if(_mark + _interval <= System.currentTimeMillis())
		{
			_mark = System.currentTimeMillis();
			return true;
		}
		else
		{
			return false;
		}
	}

}
