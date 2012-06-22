package org.akquinet.util;

/**
 * Do not forget to implement hashCode and equals so that two IResourceWatchers are equal if they return the same value on getResourceId().
 * @author immanuel
 *
 */
public interface IResourceWatcher
{
	public String getResourceId();

	/**
	 * Determines whether the watched resource has changed since the last call of this method or the creation of the watcher if this method has never been called before on this watcher.
	 * @return true if the watched resource has changed and false otherwise. 
	 */
	public boolean resourceChanged();
}
