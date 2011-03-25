package org.akquinet.audit;

import java.io.IOException;
import java.util.List;

import org.akquinet.audit.FormattedConsole.OutputLevel;

public class InteractiveAsker
{
	private List<YesNoQuestion> _questions;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	public static final String _id = "InteractiveAsker";
	
	public InteractiveAsker(List<YesNoQuestion> questions)
	{
		_questions = questions;
	}
	
	public boolean askQuestions()
	{
		boolean overallAnswer = true;
		for (YesNoQuestion question : _questions)
		{
			boolean answer = question.answer();
			overallAnswer &= answer;
			
			if(!answer && question.isCritical())
			{
				_console.println(OutputLevel.HEADING , _id, "You answered a question with 'no', which has effect on evaluation of future questions.");
				_console.println(OutputLevel.HEADING , _id, "Fix that issue and then run me again.");
				return false;
			}
			
			try
			{
				_console.waitForUserToContinue();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return overallAnswer;
	}
}
