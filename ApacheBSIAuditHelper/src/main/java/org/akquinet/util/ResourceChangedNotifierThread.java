package org.akquinet.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResourceChangedNotifierThread extends Thread
{
	private static ResourceChangedNotifierThread _default = null;
	private Map<ResourceWatcher, Set<IResourceChangedListener>> _watchersToListeners;
	
	private ResourceChangedNotifierThread()
	{
		_watchersToListeners = new HashMap<ResourceWatcher, Set<IResourceChangedListener>>();
		start();
	}
	
	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				synchronized (_watchersToListeners)
				{
					for(ResourceWatcher watcher : _watchersToListeners.keySet())
					{
						if(watcher.resourceChanged())
						{
							for(IResourceChangedListener listener : _watchersToListeners.get(watcher))
							{
								listener.resourceChanged(watcher.getResourceId());
							}
						}
					}
				}
				Thread.sleep(500);
			}
		}
		catch (InterruptedException e)
		{
			//this is the proper way to stop this thread
		}
	}

	/**
	 * Graceful shutdown for this Thread. Will always stop this Thread (no infinite waiting or something like that).
	 */
	public void shutdown()
	{
		interrupt();
	}
	
	/**
	 * Adds the listener to the set of change listeners for the mentioned ResourceWatcher. If the ResourceWatcher is
	 * not known by this thread it will be after the call. If it is already known (in terms of ResourceWatcher.equals)
	 * the existing watcher will be used.
	 * @param listener
	 * @param watcher
	 */
	public void addResourceChangedListener(IResourceChangedListener listener, ResourceWatcher watcher)
	{
		synchronized (_watchersToListeners)
		{
			Set<IResourceChangedListener> listeners = _watchersToListeners.get(watcher);
			if(listeners == null)
			{
				listeners = new HashSet<IResourceChangedListener>();
				_watchersToListeners.put(watcher, listeners);
			}
			
			listeners.add(listener);
		}
	}
	
	/**
	 * Remove listener from being invoked when watcher detects a change on it's resource.
	 * @param listener
	 * @param watcher
	 */
	public void removeResourceChangedListener(IResourceChangedListener listener, ResourceWatcher watcher)
	{
		synchronized (_watchersToListeners)
		{
			Set<IResourceChangedListener> listeners = _watchersToListeners.get(watcher);
			if(listeners != null)
			{
				listeners.remove(listener);
			}
		}
	}
	
	/**
	 * Remove listener from all watchers.
	 * @param listener
	 */
	public void removeResourceChangedListener(IResourceChangedListener listener)
	{
		synchronized (_watchersToListeners)
		{
			Set<ResourceWatcher> toRemove = new HashSet<ResourceWatcher>();
			
			for(ResourceWatcher watcher : _watchersToListeners.keySet())
			{
				Set<IResourceChangedListener> listeners = _watchersToListeners.get(watcher);
				if(listeners != null)
				{
					listeners.remove(listener);
				}
				if(listeners.isEmpty())
				{
					toRemove.add(watcher);
				}
			}
			
			for(ResourceWatcher watcher : toRemove)
			{
				_watchersToListeners.remove(watcher);
			}
		}
	}
	
	public Set<ResourceWatcher> getResourceWatchers()
	{
		synchronized (_watchersToListeners)
		{
			return _watchersToListeners.keySet();
		}
	}
	
	public static ResourceChangedNotifierThread getDefault()
	{
		_default = _default == null ? new ResourceChangedNotifierThread() : _default;
		return _default;
	}
}
