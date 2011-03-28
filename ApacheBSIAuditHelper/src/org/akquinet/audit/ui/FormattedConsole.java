package org.akquinet.audit.ui;

import java.io.IOException;

public class FormattedConsole
{
	public enum OutputLevel
	{
		HEADING, Q1, // for main questions
		Q2, // for subquestions
		RAW
		// this is for the main function an similar functions
	};

	private static FormattedConsole _default = null;
	private boolean _ignore_waitForUserToContinue;

	private FormattedConsole()
	{
		_ignore_waitForUserToContinue = false;
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
		print(level, str + "\n");
	}

	public void printAnswer(OutputLevel level, Boolean answer, String comment)
	{
		String str_answer;
		if (answer == null)
		{
			str_answer = "--";
		} else if (answer)
		{
			str_answer = "yy";
		} else
		{
			str_answer = "NN";
		}

		if (level == OutputLevel.Q2)
		{
			str_answer.replaceFirst(".", "|");
		}

		print(level, comment + "\n", str_answer);
	}

	public boolean askYesNoQuestion(OutputLevel level, String question, Boolean defaultAnswer)
	{
		if(defaultAnswer == null)
		{
			print(level, question +  " Yes/No ");
		}
		else
		{
			if(defaultAnswer)
			{
				print(level, question + (level == OutputLevel.RAW ? "" : " Yes/No [Yes] "));
			}
			else
			{
				print(level, question + (level == OutputLevel.RAW ? "" : " Yes/No [No] "));
			}
		}

		Boolean ret = null;

		while (ret == null)
		{
			String answer = "";
			try
			{
				answer = readStdInLine();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}

			if (isYes(answer))
			{
				ret = true;
			}
			else if (isNo(answer))
			{
				ret = false;
			}
			else if(answer.equalsIgnoreCase("") && defaultAnswer != null)
			{
				ret = defaultAnswer;
			}
			else
			{
				print(level, "Unrecognized answer. Please answer with \"no\" or \"yes\": ");
			}
		}

		return ret;
	}

	public String askStringQuestion(OutputLevel level, String question)
	{
		return askStringQuestion(level, question, null);
	}

	public String askStringQuestion(OutputLevel level, String question, String defaultAnswer)
	{
		if (defaultAnswer != null)
		{
			print(level, question + " [Hit enter for default value "
					+ defaultAnswer + "] ");
		} else
		{
			print(level, question + " ");
		}

		String answer = "";
		try
		{
			answer = readStdInLine();

			if (isYes(answer) || "".equals(answer))
			{
				return defaultAnswer == null ? answer : defaultAnswer;
			}

			if (isNo(answer))
			{
				if (defaultAnswer != null)
				{
					print(level, "Please enter your custom value. ");
					answer = readStdInLine();
					return answer;
				} else
				{
					return answer;
				}
			}

			return answer;
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void print(OutputLevel level, String str)
	{
		print(level, str, "  ");
	}

	private void print(OutputLevel level, String str, String answer)
	{
		StringBuffer buf = new StringBuffer(str);
		switch (level)
		{
		case HEADING:
			buf = (new StringBuffer(answer)).append(buf);
			break;
		case Q1:
			buf = (new StringBuffer(answer)).append("\t").append(buf);
			break;
		case Q2:
			buf = (new StringBuffer(answer)).append("\t\t").append(buf);
			break;
		case RAW:
			break;
		}
		System.out.print(buf);
	}

	private String readStdInLine() throws IOException
	{
		StringBuffer buf = new StringBuffer();
		int b = System.in.read();
		while (b != -1 && b != '\n')
		{
			buf.append((char) b);
			b = System.in.read();
		}
		return buf.toString();
	}

	public void waitForUserToContinue() throws IOException
	{
		if (!_ignore_waitForUserToContinue)
		{
			String anyKeyMessage = "\n  Hit enter to continue...";
			System.out.println(anyKeyMessage);

			System.in.read();
		}
	}

	public void printSeperatorLine()
	{
		for (int i = 0; i < getConsoleWidth(); ++i)
		{
			System.out.print('_');
		}
		System.out.print('\n');
	}

	public int getConsoleWidth()
	{
		return System.getProperty("COLUMNS") == null ? 80 : Integer.parseInt(System.getProperty("COLUMNS"));
	}

	public void setIgnore_WaitForUserToContinue(boolean b)
	{
		_ignore_waitForUserToContinue = b;
	}

	private static boolean isYes(String str)
	{
		return str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("y")
				|| str.equalsIgnoreCase("yy");
	}

	private static boolean isNo(String str)
	{
		return str.equalsIgnoreCase("no") || str.equalsIgnoreCase("n")
				|| str.equalsIgnoreCase("nn");
	}
}
