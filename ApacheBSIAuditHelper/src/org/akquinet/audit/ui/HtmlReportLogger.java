package org.akquinet.audit.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

public class HtmlReportLogger
{
	private Map<Integer, HtmlTagPair> _importantTags;
	private int _serialCounter;
	private Stack<Integer> _indentSerialStack;
	private Stack<Integer> _hidingSerialStack;
	private ResourceBundle _labels;
	
	private final int HTML_ID = 0;
	private final int BODY_ID = 1;
	
	private Stack<HtmlTagPair> _openTags;
	
	public HtmlReportLogger()
	{
		_labels = ResourceBundle.getBundle("global", Locale.getDefault());
		_importantTags = new HashMap<Integer, HtmlTagPair>();
		_indentSerialStack = new Stack<Integer>();
		_hidingSerialStack = new Stack<Integer>();
		_openTags = new Stack<HtmlTagPair>();

		HtmlTagPair html = new HtmlTagPair("html");
		HtmlTagPair head = createHeadTag();
		HtmlTagPair body = new HtmlTagPair("body");
		
		html.addContent(head);
		html.addContent(body);
		
		_importantTags.put(HTML_ID, html);
		_importantTags.put(BODY_ID, body);
		_serialCounter = BODY_ID + 1;
		
		_openTags.push(body);
	}

	private HtmlTagPair createHeadTag()
	{
		HtmlTagPair head = new HtmlTagPair("head");
		
		HtmlTagPair title = new HtmlTagPair("title");
		title.addContent(new HtmlText("Cochise audit report"));
		
		HtmlTagPair css = new HtmlTagPair("link");
		css.addAttribute("rel", "stylesheet");
		css.addAttribute("type", "text/css");
		css.addAttribute("href", "./html/style.css");

		HtmlTagPair javascript = new HtmlTagPair("script");
		javascript.addAttribute("language", "javascript");
		javascript.addAttribute("src", "./html/script.js");
		
		head.addContent(title);
		head.addContent(css);
		head.addContent(javascript);
		
		return head;
	}
	
	private void closeAllOpenTags()
	{
		while(_openTags.size() > 1)
		{
			_openTags.pop();
		}
		
		while(! _indentSerialStack.empty())
		{
			_indentSerialStack.pop();
		}
		
		while(! _hidingSerialStack.empty())
		{
			_hidingSerialStack.pop();
		}
	}
	
	public void printHeading1(String heading)
	{
		closeAllOpenTags();
		_openTags.peek().addContent(new HtmlText("<hr/>\n"));
		_openTags.peek().addContent(new HtmlText("<hr/>\n"));
		HtmlTagPair h1 = new HtmlTagPair("h1");
		h1.addContent(new HtmlText(heading));
		
		_openTags.peek().addContent(h1);
	}


	public void printHeading2(String heading)
	{
		closeAllOpenTags();
		_openTags.peek().addContent(new HtmlText("<hr/>\n"));
		HtmlTagPair h2 = new HtmlTagPair("h2");
		h2.addContent(new HtmlText(heading));
		
		_openTags.peek().addContent(h2);
	}

	public void printHeading3(String heading)
	{
		HtmlTagPair h3 = new HtmlTagPair("h3");
		_openTags.peek().addContent(h3);
		
		h3.addContent(new HtmlText(heading));
	}
	
	public void printParagraph(String text)
	{
		HtmlTagPair p = new HtmlTagPair("p");
		p.addContent(new HtmlText(text));
		_openTags.peek().addContent(p);
	}
	
	public void println(String text)
	{
		_openTags.peek().addContent(new HtmlText(text + "<br/>\n"));
	}
	
	public void printExample(String example)
	{
		HtmlTagPair div = new HtmlTagPair("pre");
		div.addAttribute("class", "example");
		div.addContent(new HtmlText(example));
		
		_openTags.peek().addContent(div);
	}
	
	public void beginHidingParagraph(String descriptionOfParagraph)
	{
		String divID = "div" + Integer.toString(_serialCounter);
		String aID = "a" + Integer.toString(_serialCounter);
		
		HtmlTagPair a = new HtmlTagPair("a", _serialCounter);
		a.addAttribute("href",  "javascript:toggle('" + divID + "', '" + aID + "');"  );
		a.addAttribute("id", aID);
		a.addContent(new HtmlText(_labels.getString("S9") + " " + descriptionOfParagraph));
		
		HtmlTagPair div = new HtmlTagPair("div", _serialCounter);
		div.addAttribute("class", "hidden");
		div.addAttribute("id", divID);
		
		_hidingSerialStack.push(_serialCounter++);
		_openTags.peek().addContent(a);
		_openTags.peek().addContent(div);
		_openTags.push(div);
	}
	
	public void endHidingParagraph()
	{
		if(_hidingSerialStack.empty())
		{
			return;	//quietly ignore this call
		}
		
		while(! _openTags.empty())
		{
			if(_openTags.peek().getSerial() == null)
			{
				_openTags.pop();
			}
			else if(_hidingSerialStack.peek().equals(_openTags.peek().getSerial()))
			{
				_openTags.pop();
				break;
			}
		}
		_hidingSerialStack.pop();
	}
	
	public void beginIndent()
	{
		HtmlTagPair div = new HtmlTagPair("div", _serialCounter);
		_indentSerialStack.push(_serialCounter);
		_serialCounter++;
		div.addAttribute("class", "indented");
		
		_openTags.peek().addContent(div);
		_openTags.push(div);
	}
	
	public void endIndent()
	{
		if(_indentSerialStack.empty())
		{
			return;	//quietly ignore this call
		}
		
		while(! _openTags.empty())
		{
			if(_openTags.peek().getSerial() == null)
			{
				_openTags.pop();
			}
			else if(_indentSerialStack.peek().equals(_openTags.peek().getSerial()))
			{
				_openTags.pop();
				break;
			}
		}
		_indentSerialStack.pop();
	}
	
	public void printAnswer(boolean answer, String cause)
	{
		HtmlTagPair div = new HtmlTagPair("div");
		div.addAttribute("class", answer ? "positive" : "negative");
		div.addContent(new HtmlText("<p><b>" +  (answer ? _labels.getString("S10_yes") : _labels.getString("S10_no"))  + "</b></p>"));
		div.addContent(new HtmlText("<p>" + cause + "</p>"));
		
		_openTags.peek().addContent(div);
	}
	
	public void writeToFile(File htmlFile) throws IOException
	{
		FileOutputStream os = new FileOutputStream(htmlFile);
		os.write(_importantTags.get(HTML_ID).stringify().toString().getBytes());
		os.close();
	}
}
