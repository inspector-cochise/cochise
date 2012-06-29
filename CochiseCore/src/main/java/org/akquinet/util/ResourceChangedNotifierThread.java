package org.akquinet.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResourceChangedNotifierThread extends Thread
{
	private static ResourceChangedNotifierThread _default = null;
	private Map<IResourceWatcher, Set<IResourceChangedListener>> _watchersToListeners;
	
	private ResourceChangedNotifierThread()
	{
		_watchersToListeners = Collections.synchronizedMap(new HashMap<IResourceWatcher, Set<IResourceChangedListener>>());
		start();
	}
	
	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				// clone _watchersToListeners to avoid blocking threads
				Map<IResourceWatcher, Set<IResourceChangedListener>> watchersToListeners;
				synchronized (_watchersToListeners)
				{
					watchersToListeners = new HashMap<IResourceWatcher, Set<IResourceChangedListener>>();
					for(IResourceWatcher watcher : _watchersToListeners.keySet())
					{
						watchersToListeners.put(watcher, new HashSet<IResourceChangedListener>(_watchersToListeners.get(watcher)));
					}
				}
				
				for(IResourceWatcher watcher : watchersToListeners.keySet())
				{
					if(watcher.resourceChanged())
					{
						for(IResourceChangedListener listener : watchersToListeners.get(watcher))
						{
							listener.resourceChanged(watcher.getResourceId());
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
	public void addResourceChangedListener(IResourceChangedListener listener, IResourceWatcher watcher)
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
	 * Don't evaluate that watcher anymore. All it's listeners will be removed too.
	 * @param watcher
	 */
	public void removeResourceWatcher(IResourceWatcher watcher)
	{
		synchronized (_watchersToListeners)
		{
			_watchersToListeners.remove(watcher);
		}
	}
	
	/**
	 * Remove listener from being invoked when watcher detects a change on it's resource.
	 * @param listener
	 * @param watcher
	 */
	public void removeResourceChangedListener(IResourceChangedListener listener, IResourceWatcher watcher)
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
			Set<IResourceWatcher> toRemove = new HashSet<IResourceWatcher>();
			
			for(IResourceWatcher watcher : _watchersToListeners.keySet())
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
			
			for(IResourceWatcher watcher : toRemove)
			{
				_watchersToListeners.remove(watcher);
			}
		}
	}
	
	
	public Set<IResourceWatcher> getResourceWatchers()
	{
		synchronized (_watchersToListeners)
		{
			return new HashSet<IResourceWatcher>(_watchersToListeners.keySet());
		}
	}
	
	public static ResourceChangedNotifierThread getDefault()
	{
		_default = _default == null ? new ResourceChangedNotifierThread() : _default;
		return _default;
	}
}
