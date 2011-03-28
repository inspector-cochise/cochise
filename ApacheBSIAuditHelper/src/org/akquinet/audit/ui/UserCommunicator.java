package org.akquinet.audit.ui;

import java.io.IOException;

import org.akquinet.audit.ui.FormattedConsole.OutputLevel;

public class UserCommunicator
{
	private static final UserCommunicator _default = new UserCommunicator();
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	
	private int _indentLevel;
	
	public UserCommunicator()
	{
		_indentLevel = 0;
	}

	public void printHeading1(String heading)
	{
		_indentLevel = 0;
		_console.printSeperatorLine();
		_console.println(getLevel(), heading + "\n\n");
		
		
		_indentLevel = 1;
	}
	
	public void printHeading2(String heading)
	{
		_indentLevel = 0;
		_console.printSeperatorLine();
		_console.println(getLevel(), heading + "\n");
		
		
		_indentLevel = 1;
	}
	
	public void printHeading3(String heading)
	{
		_console.println(getLevel(), "---===" + heading + "===--- -- -  -");
	}
	
	public void printParagraph(String text)
	{
		text = _console.wrapString(text, getLevel());
		_console.println(getLevel(), text + "\n");
	}

	public void println(String text)
	{
		_console.println(getLevel(), text);
	}
	
	public void printExample(String example)
	{
		_console.println(getLevel(), "\t" + example.replaceAll("\n", "\n\t"));
	}
	
	private OutputLevel getLevel()
	{
		OutputLevel level;
		switch(_indentLevel)
		{
		case 0:
			level = OutputLevel.HEADING;
			break;
		case 1:
			level = OutputLevel.Q1;
			break;
		case 2:
			level = OutputLevel.Q2;
			break;
		default:
			level = OutputLevel.RAW;
			break;
		}
		return level;
	}

	public void printAnswer(boolean answer, String cause)
	{
		_console.printAnswer(getLevel(), answer, cause);
	}
	
	public void printHidingParagraph(String hiddenText, String expandedText)
	{
	}
	
	public void beginHidingParagraph(String hiddenText)
	{
	}
	
	public void endHidingParagraph()
	{
	}
	
	public void beginIndent()
	{
		_indentLevel++;
	}
	
	public void endIndent()
	{
		_indentLevel--;
	}
	
	public void finishCommunication()
	{
	}

	public boolean askYesNoQuestion(String question)
	{
		return askYesNoQuestion(question, false);
	}
	
	public boolean askYesNoQuestion(String question, Boolean defaultAnswer)
	{
		boolean answer = _console.askYesNoQuestion(getLevel(), question, defaultAnswer);
		return answer;
	}
	
	public String askStringQuestion(String question)
	{
		return askStringQuestion(question, null);
	}

	public String askStringQuestion(String question, String defaultAnswer)
	{
		String answer = _console.askStringQuestion(getLevel(), question, defaultAnswer);
		return answer;
	}
	
	public void waitForUserToContinue() throws IOException
	{
		_console.waitForUserToContinue();
	}
	
	public static UserCommunicator getDefault()
	{
		return _default;
	}
}
