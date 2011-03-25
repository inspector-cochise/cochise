package org.akquinet.audit;

public interface YesNoQuestion
{
	/**
	 * Answers the question.
	 * @return The answer. True means yes and false means no.
	 */
	abstract public boolean answer();
	abstract public boolean isCritical();
	abstract public String getID();
}
