package org.akquinet.audit.ui;

import java.rmi.UnexpectedException;
import java.util.Locale;
import java.util.ResourceBundle;

public class DelayedHtmlUserCommunicator extends UserCommunicator
{
	private HtmlReportLogger _htmlBuilder;
	private Locale _locale;
	private ResourceBundle _labels;
	
	public enum InputState
	{
		NO_INPUT_EXPECTED,
		STRING_EXPECTED,
		BOOLEAN_EXPECTED
	}
	
	private InputState _inputState;
	private Object _inputWaiter = new Object();
	private Boolean _yesNoAnswer = null;
	private String _stringAnswer = null;
	private String _questId;
	private String _target;

	/**
	 * Don't forget to call setQuestId before using any of the ask* methods.
	 */
	public DelayedHtmlUserCommunicator(String webTarget)
	{
		this(webTarget, null);
	}
	
	public DelayedHtmlUserCommunicator(String webTarget, String questId)
	{
		_questId = questId;
		_inputState = InputState.NO_INPUT_EXPECTED;
		_locale = Locale.getDefault();
		_labels = ResourceBundle.getBundle("global", _locale);
		_htmlBuilder = new HtmlReportLogger(_locale, false);
		_target = webTarget;
	}
	
	public void setQuestId(String questId)
	{
		_questId = questId;
	}
	
	@Override
	public void reportError(String error)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void reportError(Exception error)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void printHeading1(String heading)
	{
		_htmlBuilder.printHeading1(heading);
	}

	@Override
	public void printHeading2(String heading)
	{
		_htmlBuilder.printHeading2(heading);
	}

	@Override
	public void printHeading3(String heading)
	{
		_htmlBuilder.printHeading3(heading);
	}

	@Override
	public void printParagraph(String text)
	{
		_htmlBuilder.printParagraph(text);
	}

	@Override
	public void println(String text)
	{
		_htmlBuilder.println(text);
	}

	@Override
	public void printExample(String example)
	{
		_htmlBuilder.printExample(example);
	}

	@Override
	public void printAnswer(boolean answer, String cause)
	{
		_htmlBuilder.printAnswer(answer, cause);
	}

	@Override
	public void printHidingParagraph(String shortDescription, String expandedText)
	{
		_htmlBuilder.beginHidingParagraph(shortDescription);
			_htmlBuilder.printParagraph(expandedText);
		_htmlBuilder.endHidingParagraph();
	}

	@Override
	public void beginHidingParagraph(String hiddenText)
	{
		_htmlBuilder.beginHidingParagraph(hiddenText);
	}

	@Override
	public void endHidingParagraph()
	{
		_htmlBuilder.endHidingParagraph();
	}

	@Override
	public void beginIndent()
	{
		_htmlBuilder.beginIndent();
	}

	@Override
	public void endIndent()
	{
		_htmlBuilder.endIndent();
	}

	@Override
	public boolean askYesNoQuestion(String question)
	{
		return askYesNoQuestion(question, false);
	}

	@Override
	public boolean askYesNoQuestion(String question, Boolean defaultAnswer)
	{
		synchronized (_inputWaiter)
		{
			_inputState = InputState.BOOLEAN_EXPECTED;
			_htmlBuilder.mark();
			
			HtmlTagPair form = new HtmlTagPair("form");
			form.addContent(new HtmlText(question + " "));
			form.addContent(new HtmlText("<input type=\"button\" value=\"" + _labels.getString("S8_yes") +
					"\" onclick=\"location='" + _target + "?quest=" + _questId + "&action=answer&answer=yes'\"/>"));
			form.addContent(new HtmlText("<input type=\"button\" value=\"" + _labels.getString("S8_no") +
					"\" onclick=\"location='" + _target + "?quest=" + _questId + "&action=answer&answer=no'\"/>"));
			_htmlBuilder.printParagraph(form.stringify().toString());
			
			try
			{
				_inputWaiter.wait();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				return false;
			}
			
			_inputState = InputState.NO_INPUT_EXPECTED;
			
			boolean ret = _yesNoAnswer;
			_yesNoAnswer = null;
			
			_htmlBuilder.reset();
			_htmlBuilder.printParagraph(question + " <i>" +
								(ret ? _labels.getString("S8_yes") : _labels.getString("S8_no"))
								+ "</i>");
			
			return ret;
		}
	}

	@Override
	public String askStringQuestion(String question)
	{
		return askStringQuestion(question, "");
	}

	@Override
	public String askStringQuestion(String question, String defaultAnswer)
	{
		synchronized (_inputWaiter)
		{
			_inputState = InputState.STRING_EXPECTED;
			_htmlBuilder.mark();
			
			HtmlTagPair form = new HtmlTagPair("form");
			form.addAttribute("action", "" + _target + "?quest=" + _questId + "&action=answer");
			form.addContent(new HtmlText(question + " "));
			form.addContent(new HtmlText("<input type=\"text\" name=\"answer\" size=\"40\" value=\"" + defaultAnswer + "\"/>"));
			form.addContent(new HtmlText("<input type=\"submit\" value=\"Absenden\">"));
			_htmlBuilder.printParagraph(form.stringify().toString());
			
			try
			{
				_inputWaiter.wait();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				return "";
			}
			
			_inputState = InputState.NO_INPUT_EXPECTED;
			
			String ret = _stringAnswer;
			_stringAnswer = null;
			
			_htmlBuilder.reset();
			_htmlBuilder.printParagraph(question + " <i>" + ret + "<i/>");
			
			return ret;
		}		
	}

	@Override
	public String askTextQuestion(String question)
	{
		synchronized (_inputWaiter)
		{
			_inputState = InputState.STRING_EXPECTED;
			_htmlBuilder.mark();
			
			HtmlTagPair form = new HtmlTagPair("form");
			form.addAttribute("action", "" + _target + "?quest=" + _questId + "&action=answer");
			form.addContent(new HtmlText(question + " "));
			form.addContent(new HtmlText("<textarea name=\"answer\" cols=\"80\" rows=\"10\" value=\"\"></textarea>"));
			form.addContent(new HtmlText("<input type=\"submit\" value=\"Absenden\">"));
			_htmlBuilder.printParagraph(form.stringify().toString());
			
			try
			{
				_inputWaiter.wait();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				return "";
			}
			
			_inputState = InputState.NO_INPUT_EXPECTED;
			
			String ret = _stringAnswer;
			_stringAnswer = null;
			
			_htmlBuilder.reset();
			_htmlBuilder.printParagraph(question + " <i>" + ret + "<i/>");
			
			return ret;
		}
	}

	@Override
	public Locale getLocale()
	{
		return _locale;
	}

	@Override
	public void setLocale(Locale locale)
	{
		_locale = locale;
		_htmlBuilder.setLocale(_locale);
		_labels = ResourceBundle.getBundle("global", _locale);
	}

	public String stringifyCurrentState()
	{
		return _htmlBuilder.stringify();
	}
	
	public InputState getInputState()
	{
		return _inputState;
	}
	
	public void setStringAnswer(String answer) throws UnexpectedException
	{
		if(_inputState == InputState.STRING_EXPECTED)
		{
			_stringAnswer = answer;
			synchronized (_inputWaiter)
			{
				_inputWaiter.notifyAll();
			}
		}
		else
		{
			throw new UnexpectedException("Internal Error: String expected.");
		}
	}
	
	public void setBooleanAnswer(boolean answer) throws UnexpectedException
	{
		if(_inputState == InputState.BOOLEAN_EXPECTED)
		{
			_yesNoAnswer = answer;
			synchronized (_inputWaiter)
			{
				_inputWaiter.notifyAll();
			}
		}
		else
		{
			throw new UnexpectedException("Internal Error: Boolean expected.");
		}
	}
	
	public void setBooleanAnswer(String answer) throws UnexpectedException
	{
		if(answer.equalsIgnoreCase("yes"))
		{
			setBooleanAnswer(true);
		}
		else
		{
			setBooleanAnswer(false);
		}
	}
	
	public void setAnswer(String answer) throws UnexpectedException
	{
		switch (_inputState)
		{
		case BOOLEAN_EXPECTED:
			setBooleanAnswer(answer);
			break;
		case STRING_EXPECTED:
			setStringAnswer(answer);
		case NO_INPUT_EXPECTED:
			throw new UnexpectedException("Internal Error: No Input expected.");
		}
	}
}
