package org.akquinet.test.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akquinet.audit.ui.HtmlReportLogger;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.test.util.FormattedConsole.OutputLevel;

public class ConsoleUserCommunicator extends UserCommunicator
{
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	
	private int _indentLevel;
	private File _htmlReport;
	private boolean _hide;
	private ResourceBundle _labels;
	
	private HtmlReportLogger _htmlLogger;
	
	public ConsoleUserCommunicator()
	{
		this(null);
	}
	
	public ConsoleUserCommunicator(File htmlReport)
	{
		_htmlReport = htmlReport;
		_indentLevel = 0;
		_htmlLogger = new HtmlReportLogger(Locale.getDefault());
		_hide = false;
		setLocale(Locale.getDefault());
	}
	
	@Override
	public void reportError(String error)
	{
		_console.println(getLevel(), error);
	}
	
	@Override
	public void reportError(Exception error)
	{
		_console.reportException(getLevel(), error);
	}

	@Override
	public void printHeading1(String heading)
	{
		_hide = false;
		_indentLevel = 0;
		_console.printSeperatorLine();
		_console.printSeperatorLine();
		_console.println(getLevel(), heading + "\n\n");
		
		if(_htmlReport != null)
		{
			_htmlLogger.printHeading1(heading);
		}
		
		_indentLevel = 1;
	}
	
	@Override
	public void printHeading2(String heading)
	{
		_hide = false;
		_indentLevel = 0;
		_console.printSeperatorLine();
		_console.println(getLevel(), heading + "\n");

		if(_htmlReport != null)
		{
			_htmlLogger.printHeading2(heading);
		}
		
		_indentLevel = 1;
	}
	
	@Override
	public void printHeading3(String heading)
	{
		_hide = false;
		_console.println(getLevel(), "---===" + heading + "===--- -- -  -");

		if(_htmlReport != null)
		{
			_htmlLogger.printHeading3(heading);
		}
	}

	@Override
	public void printParagraph(String text)
	{
		if(!_hide)
		{
			text = _console.wrapString(text, getLevel());
			_console.println(getLevel(), text + "\n");
		}

		if(_htmlReport != null)
		{
			_htmlLogger.printParagraph(text);
		}
	}

	@Override
	public void println(String text)
	{
		if(!_hide)
		{
			_console.println(getLevel(), text);
		}

		if(_htmlReport != null)
		{
			_htmlLogger.println(text);
		}
	}

	@Override
	public void printExample(String example)
	{
		if(!_hide)
		{
			if(getLevel() == FormattedConsole.OutputLevel.Q2)
			{
				_console.println(getLevel(), "\t" + example.replaceAll("\n", "\n\t\t"));
			}
			else
			{
				_console.println(getLevel(), "\t" + example.replaceAll("\n", "\n\t"));
			}
		}

		if(_htmlReport != null)
		{
			_htmlLogger.printExample(example);
		}
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

	@Override
	public void printAnswer(boolean answer, String cause)
	{
		if(!_hide)
		{
			_console.printAnswer(getLevel(), answer, cause);
		}

		if(_htmlReport != null)
		{
			_htmlLogger.printAnswer(answer, cause);
		}
	}

	@Override
	public void printHidingParagraph(String shortDescription, String expandedText)
	{
		if(_htmlReport != null)
		{
			_htmlLogger.beginHidingParagraph(shortDescription);
			_htmlLogger.printParagraph(expandedText);
			_htmlLogger.endHidingParagraph();
		}
	}

	@Override
	public void beginHidingParagraph(String hiddenText)
	{
		_hide = true;
		if(_htmlReport != null)
		{
			_htmlLogger.beginHidingParagraph(hiddenText);
		}
	}

	@Override
	public void endHidingParagraph()
	{
		_hide = false;
		if(_htmlReport != null)
		{
			_htmlLogger.endHidingParagraph();
		}
	}

	@Override
	public void beginIndent()
	{
		_indentLevel++;
		
		if(_htmlReport != null)
		{
			_htmlLogger.beginIndent();
		}
	}

	@Override
	public void endIndent()
	{
		_indentLevel--;
		
		if(_htmlReport != null)
		{
			_htmlLogger.endIndent();
		}
	}

	@Override
	public void finishCommunication()
	{
		try
		{
			_htmlLogger.writeToFile(_htmlReport);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean askYesNoQuestion(String question)
	{
		boolean answer = askYesNoQuestion(question, false);
		
		return answer;
	}

	@Override
	public boolean askYesNoQuestion(String question, Boolean defaultAnswer)
	{
		boolean answer = _console.askYesNoQuestion(getLevel(), question, defaultAnswer);
		
		_htmlLogger.printParagraph(question + " <i>" + (answer ? _labels.getString("S8_yes") : _labels.getString("S8_no")) + "</i>");
		
		return answer;
	}

	@Override
	public String askStringQuestion(String question)
	{
		String answer = askStringQuestion(question, null);
		
		_htmlLogger.printParagraph(question + " <i>" + answer + "</i>");
		
		return answer;
	}

	@Override
	public String askStringQuestion(String question, String defaultAnswer)
	{
		String answer = _console.askStringQuestion(getLevel(), question, defaultAnswer);
		
		_htmlLogger.printParagraph(question + " <i>" + answer + "</i>");
		
		return answer;
	}

	@Override
	public void waitForUserToContinue()
	{
		_console.waitForUserToContinue();
	}

	@Override
	public void setIgnore_WaitForUserToContinue(boolean b)
	{
		_console.setIgnore_WaitForUserToContinue(b);
	}

	@Override
	public void markReport()
	{
		_htmlLogger.mark();
	}

	@Override
	public void resetReport()
	{
		_htmlLogger.reset();
	}

	@Override
	public Locale getLocale()
	{
		return _labels.getLocale();
	}

	@Override
	public void setLocale(Locale locale)
	{
		_labels = ResourceBundle.getBundle("global", locale);
		_console.setLocale(locale);
		_htmlLogger.setLocale(locale);
	}

	@Override
	public String askTextQuestion(String question)
	{
		return askStringQuestion(question);
	}
}
