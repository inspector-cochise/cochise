package org.akquinet.audit.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class HtmlReportLogger
{
	private Map<Integer, HtmlTagPair> _importantTags;
	private int _serialCounter;
	
	private final int HTML_ID = 0;
	private final int BODY_ID = 1;
	
	private Stack<HtmlTagPair> _openTags;
	
	public HtmlReportLogger()
	{
		_importantTags = new HashMap<Integer, HtmlTagPair>();
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
		
		HtmlTagPair css = new HtmlTagPair("style");
		css.addAttribute("type", "text/css");
		css.addContent(new HtmlText(".indented {text-indent: 3em;}\n"));
		
		head.addContent(title);
		
		return head;
	}
	
	private void closeAllOpenTags()
	{
		while(_openTags.size() > 1)
		{
			_openTags.pop();
		}
	}
	
	public void printHeading1(String heading)
	{
		closeAllOpenTags();
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
		HtmlTagPair div = new HtmlTagPair("div");
		HtmlTagPair h3 = new HtmlTagPair("h3");
		h3.addContent(new HtmlText(heading));
		div.addContent(h3);
		div.addAttribute("class", "indented");
		
		_openTags.push(div);
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
	}
	
	public void beginHidingParagraph(String hiddenText)
	{
	}
	
	public void endHidingParagraph()
	{
	}
	
	public void beginIndent()
	{
	}
	
	public void endIndent()
	{
	}
	
	public void printAnswer(boolean answer, String cause)
	{
	}
	
	public void writeToFile(File htmlFile)
	{
	}
}
