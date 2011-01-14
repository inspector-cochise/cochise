package org.akquinet.audit;


public class FormattedConsole
{
	public enum OutputLevel
	{
		HEADING,
		Q1,			//for main questions
		Q2			//for subquestions
	};
	
	private static FormattedConsole _default = null;

	private FormattedConsole()
	{
	}
	
	public static FormattedConsole getDefault()
	{
		if(_default == null)
		{
			_default = new FormattedConsole();
		}
		return _default;
	}
	
	public void println(OutputLevel level, String str)
	{
		StringBuffer buf = new StringBuffer(str);
		switch(level)
		{
		case HEADING:
			break;
		case Q1:
			buf = (new StringBuffer("  \t")).append(buf);
			break;
		case Q2:
			buf = (new StringBuffer("  \t\t")).append(buf);
			break;
		}
		System.out.println(buf);
	}
	
	public void printAnswer(OutputLevel level, Boolean answer, String comment)
	{
		StringBuffer buf = new StringBuffer();
		String str_answer = "--";
		if(answer == null)
		{
			str_answer = "--";
		}
		else if(answer)
		{
			str_answer = "yy";
		}
		else
		{
			str_answer = "NN";
		}
		
		switch(level)
		{
		case HEADING:
			buf.append(comment);
			break;
		case Q1:
			buf = buf.append(str_answer).append("\t").append(comment);
			break;
		case Q2:
			str_answer = "|" + str_answer.substring(1);
			buf = buf.append(str_answer).append("\t\t").append(comment);
			break;
		}
		System.out.println(buf);
	}
}
