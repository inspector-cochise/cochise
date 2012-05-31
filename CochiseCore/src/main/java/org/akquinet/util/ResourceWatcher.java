package org.akquinet.util;

public abstract class ResourceWatcher
{
	abstract public String getResourceId();

	/**
	 * Determines whether the watched resource has changed since the last call of this method or the creation of the watcher if this method has never been called before on this watcher.
	 * @return true if the watched resource has changed and false otherwise. 
	 */
	abstract public boolean resourceChanged();
	
	/**
	 * Two ResourceWatchers are equal if they have the same resourceId.
	 */
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		
		if(!(o instanceof ResourceWatcher))
		{
			return false;
		}
		else
		{
			ResourceWatcher rhs = (ResourceWatcher) o;
			return getResourceId().equals(rhs.getResourceId());
		}
	}
	
	@Override
	public int hashCode()
	{
		return getResourceId().hashCode();
	}
}
