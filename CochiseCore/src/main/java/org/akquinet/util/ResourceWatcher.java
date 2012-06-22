package org.akquinet.util;

public abstract class ResourceWatcher implements IResourceWatcher
{
	/**
	 * Two IResourceWatchers are equal if they have the same resourceId.
	 */
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		
		if(o instanceof IResourceWatcher)
		{
			IResourceWatcher rhs = (IResourceWatcher) o;
			return getResourceId().equals(rhs.getResourceId());
		}
		else
		{
			return super.equals(o);
		}
	}
	
	@Override
	public int hashCode()
	{
		return getResourceId().hashCode();
	}
}
