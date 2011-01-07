package org.akquinet.audit;

import java.util.List;
import org.akquinet.audit.FormattedConsole.OutputLevel;

public class InteractiveAsker
{
	private List<YesNoQuestion> _questions;
	private FormattedConsole _console;
	
	public InteractiveAsker(List<YesNoQuestion> questions, FormattedConsole console)
	{
		_questions = questions;
		_console = console;
	}
	
	public boolean askQuestions()
	{
		boolean overallAnswer = true;
		for (YesNoQuestion question : _questions)
		{
			boolean answer = question.answer();
			overallAnswer &= answer;
			
			if(question.isCritical())
			{
				_console.println(OutputLevel.HEADING , "You answered a question with 'no', which has effect on evaluation of future questions.");
				_console.println(OutputLevel.HEADING , "Fix that issue and then run me again.");
				return false;
			}
		}
		return overallAnswer;
	}
}