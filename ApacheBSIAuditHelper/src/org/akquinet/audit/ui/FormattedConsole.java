package org.akquinet.audit.ui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
			String tmp = wrapString(question +  " Yes/No ", level);
			print(level, tmp);
		}
		else
		{
			if(defaultAnswer)
			{
				String tmp = wrapString(question + (level == OutputLevel.RAW ? "" : " Yes/No [Yes] "), level);
				print(level, tmp);
			}
			else
			{
				String tmp = wrapString(question + (level == OutputLevel.RAW ? "" : " Yes/No [No] "), level);
				print(level, tmp);
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
			String tmp = wrapString(question +
					" [Hit enter for default value "	+ defaultAnswer + "] ", level);
			print(level, tmp);
		}
		else
		{
			String tmp = wrapString(question + " ", level);
			print(level, tmp);
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
			buf = (new StringBuffer(answer)).append("    ").append(buf);
			break;
		case Q2:
			buf = (new StringBuffer(answer)).append("        ").append(buf);
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
	
	public static int getIndentation(OutputLevel level)
	{
		switch (level)
		{
		case HEADING:
			return 2;
		case Q1:
			return 6;
		case Q2:
			return 10;
		case RAW:
		default:
			return 0;
		}
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
	
	public String wrapString(String text, OutputLevel level)
	{
		int maxLength = getConsoleWidth();
		String prepend = "";
		
		{
			StringBuffer buf = new StringBuffer();
			for(int i = 0; i < getIndentation(level); ++i)
			{
				buf = buf.append(" ");
			}
			prepend = buf.toString();
		}
		
		List<String> wrappedText = new LinkedList<String>();
		int i = 0;
		int split = -1;

		while(! text.isEmpty() )
		{
			if(text.length() + getIndentation(level) <= maxLength)
			{
				wrappedText.add(text);
				text = "";
				i = -1;
				continue;
			}
			
			char c = text.charAt(i);
			if(c == '\n')
			{
				wrappedText.add(text.substring(0, i));
				text = prepend + ( i+1 < text.length() ? text.substring(i+1) : "" );
			}
			else if(c == ' ' || c == '-' || c == '\t' || c == '/' || c == ',' || c == '.' || c == '!' || c == '?')
			{
				if(maxLength - i - prepend.length() <= 0)
				{
					if(split == -1)
					{
						wrappedText.add(text.substring(0, maxLength + 1));
						text = prepend + ( maxLength+1 < text.length() ? text.substring(maxLength+1) : "" );
					}
					else
					{
						wrappedText.add(text.substring(0, split+1));
						text = prepend + ( split+1 < text.length() ? text.substring(split+1) : "" );
					}
					i = 0;
					split = -1;
					continue;
				}
				else
				{
					split = i;
				}
			}
			
			++i;
		}
		
		StringBuffer buf = new StringBuffer();
		for (String str : wrappedText)
		{
			buf = buf.append(str).append('\n');
		}
		
		return buf.toString().substring(0, buf.length()-1);	//without last '\n'
	}
}
