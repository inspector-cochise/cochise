package org.akquinet.audit.ui;

import java.util.Locale;
import java.util.ResourceBundle;

public class DelayedHtmlUserCommunicator extends UserCommunicator
{
	private HtmlReportLogger _htmlBuilder;
	private Locale _locale;
	private ResourceBundle _labels;
	
	private Object _inputBlocker;
	private Boolean _yesNoAnswer = null;
	private String _stringAnswer = null;

	public DelayedHtmlUserCommunicator()
	{
		_inputBlocker = new Object();
		_locale = Locale.getDefault();
		_labels = ResourceBundle.getBundle("global", _locale);
		_htmlBuilder = new HtmlReportLogger(_locale, false);
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
		synchronized (_inputBlocker)
		{
			_htmlBuilder.mark();
			
			HtmlTagPair form = new HtmlTagPair("form");
			form.addContent(new HtmlText(question + " "));
			form.addContent(new HtmlText("<input type=\"button\" value=\"" + _labels.getString("S8_yes") +
					"\" onclick=\"location='main.html'\"/>"));	//TODO set correct location
			form.addContent(new HtmlText("<input type=\"button\" value=\"" + _labels.getString("S8_no") +
					"\" onclick=\"location='main.html'\"/>"));	//TODO set correct location
			_htmlBuilder.printParagraph(form.stringify().toString());
			
			try
			{
				_inputBlocker.wait();
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);	//this should not happen
			}
			
			boolean ret = _yesNoAnswer;
			_yesNoAnswer = null;
			
			_htmlBuilder.reset();
			_htmlBuilder.printParagraph(question + " <i>" +
								(ret ? _labels.getString("S8_yes") : _labels.getString("S8_no"))
								+ "<i/>");
			
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
		synchronized (_inputBlocker)
		{
			_htmlBuilder.mark();
			
			HtmlTagPair form = new HtmlTagPair("form");	//TODO set action
			form.addContent(new HtmlText(question + " "));
			form.addContent(new HtmlText("<input type=\"text\" size=\"150\" value=\"" + defaultAnswer + "\"/>"));
			form.addContent(new HtmlText("<input type=\"submit\" value=\"Absenden\">"));
			_htmlBuilder.printParagraph(form.stringify().toString());
			
			try
			{
				_inputBlocker.wait();
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);	//this should not happen
			}
			
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
}
