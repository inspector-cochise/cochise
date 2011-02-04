package org.akquinet.audit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FormattedConsole
{
	public enum OutputLevel
	{
		HEADING, Q1, // for main questions
		Q2
		// for subquestions
	};

	private static FormattedConsole _default = null;

	private FormattedConsole()
	{
	}

	public static FormattedConsole getDefault()
	{
		if (_default == null)
		{
			_default = new FormattedConsole();
		}
		return _default;
	}

	public void println(OutputLevel level, String str)
	{
		StringBuffer buf = new StringBuffer(str);
		switch (level)
		{
		case HEADING:
			buf = (new StringBuffer("  ")).append(buf);
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
		if (answer == null)
		{
			str_answer = "--";
		}
		else if (answer)
		{
			str_answer = "yy";
		}
		else
		{
			str_answer = "NN";
		}

		switch (level)
		{
		case HEADING:
			buf.append("  ").append(comment);
			break;
		case Q1:
			buf = buf.append(str_answer).append("  \t").append(comment);
			break;
		case Q2:
			str_answer = "|" + str_answer.substring(1);
			buf = buf.append(str_answer).append("  \t\t").append(comment);
			break;
		}
		System.out.println(buf);
	}

	public boolean askYesNoQuestion(OutputLevel level, String question)
	{
		StringBuffer buf = new StringBuffer();
		switch (level)
		{
		case HEADING:
			buf.append(question);
			break;
		case Q1:
			buf = buf.append("  \t").append(question).append(" (Yes/No) [No]: ");
			break;
		case Q2:
			buf = buf.append("  \t\t").append(question).append(" (Yes/No) [No]: ");
			break;
		}

		Boolean ret = null;

		while (ret == null)
		{
			String answer = "";
			try
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				System.out.print(buf.toString());
				answer = in.readLine();
			}
			catch (IOException e)
			{
			}

//			String answer = System.console().readLine(buf.toString());
			if (answer.equalsIgnoreCase("yes"))
			{
				ret = true;
			}
			else if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase(""))
			{
				ret = false;
			}
			else
			{
				println(level, "Unrecognized answer. Please answer with \"no\" or \"yes\".");
			}
		}

		return ret;
	}
}
