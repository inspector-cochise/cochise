package org.akquinet.audit;

import java.util.Properties;

public class QuestionData
{
	public String _questID;
	public String _tape;
	public boolean _answer;
	
	public QuestionData(String questID, Properties props) throws DataNotFoundException
	{
		_questID = questID;
		try
		{
			_tape = props.getProperty(_questID + ".tape");
			_answer = props.getProperty(_questID + ".answer").equals("1");
		}
		catch(NullPointerException e)
		{
			throw new DataNotFoundException();
		}
		
		if(_tape == null)
		{
			throw new DataNotFoundException();
		}
	}
	
	public QuestionData(String questID, String tape, boolean answer)
	{
		_questID = questID;
		_tape = tape;
		_answer = answer;
	}
	
	public void saveToProperties(Properties props)
	{
		props.setProperty(_questID + ".tape", _tape);
		props.setProperty(_questID + ".answer", _answer == true ? "1" : "0");
	}
	
	
	public class DataNotFoundException extends Exception
	{
		private static final long serialVersionUID = -5950536437287932019L;
	}
}
