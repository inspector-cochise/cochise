package org.akquinet.audit;

public interface YesNoQuestion
{
	/**
	 * Asks the question and returns its answer.
	 * @return The answer. True means yes and false means no.
	 */
	abstract public boolean answer();
	
	/**
	 * Will be called before answer(). Maybe it will be called multiple times
	 * (example: initialize->answer->initialize->answer).
	 * @throws Exception
	 */
	abstract public void initialize() throws Exception;
	
	/**
	 * Criticalness here means, if this question fails (i.e. gets answered with no), then prerequisites of other questions
	 * are not fulfilled. An example for a critical question would be a syntax check. If the syntax is not correct you
	 * can't make further investigations.
	 * @return true if this question is critical.
	 */
	abstract public boolean isCritical();
	
	/**
	 * An identifier that should be unique.
	 * @return ID
	 */
	abstract public String getID();
	
	/**
	 * Number of that question in a block. Questions will be asked in order of these numbers if all requirements
	 * are fulfilled.
	 * @return number
	 */
	abstract public int getNumber();
	
	/**
	 * Every question belongs to a special block of questions.
	 * @return Number of that block for this question.
	 */
	abstract public int getBlockNumber();
	
	/**
	 * If some questions are required to be answered with yes for the check of this question. (Like a syntax check)
	 * @return An array of the IDs (-> getID()) of the required questions.
	 */
	abstract public String[] getRequirements();
}
