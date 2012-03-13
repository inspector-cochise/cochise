package org.akquinet.audit.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

public class HtmlReportLogger
{
	private int _serialCounter;
	private Stack<Integer> _indentSerialStack;
	private Stack<Integer> _hidingSerialStack;
	private ResourceBundle _labels;

	private HtmlTagPair _root;
	
	private Stack<HtmlTagPair> _openTags;
	
	private Backup _markBu;
	
	public HtmlReportLogger(Locale locale)
	{
		this(locale, true);
	}
	
	public HtmlReportLogger(Locale locale, boolean createOuterTags)
	{
		_labels = ResourceBundle.getBundle("global", locale);
		_indentSerialStack = new Stack<Integer>();
		_hidingSerialStack = new Stack<Integer>();
		_openTags = new Stack<HtmlTagPair>();
		_serialCounter = 1;

		if(createOuterTags)
		{
			HtmlTagPair html = new HtmlTagPair("html");
			HtmlTagPair head = createHeadTag();
			HtmlTagPair body = new HtmlTagPair("body");
			
			html.addContent(head);
			html.addContent(body);
			
			_openTags.push(body);
			_root = html;
		}
		else
		{
			HtmlTagPair div = new HtmlTagPair("div");
			
			_openTags.push(div);
			_root = div;
		}
		
		_markBu = new Backup(_serialCounter, _indentSerialStack, _hidingSerialStack, _openTags);
	}

	private HtmlTagPair createHeadTag()
	{
		HtmlTagPair head = new HtmlTagPair("head");

		HtmlTagPair meta = new HtmlTagPair("meta");
		meta.addAttribute("charset", System.getProperty("file.encoding"));
		
		HtmlTagPair title = new HtmlTagPair("title");
		title.addContent(new HtmlText("Cochise audit report"));
		
		HtmlTagPair css = new HtmlTagPair("link");
		css.addAttribute("rel", "stylesheet");
		css.addAttribute("type", "text/css");
		css.addAttribute("href", "./html/style.css");

		HtmlTagPair javascript = new HtmlTagPair("script");
		javascript.addAttribute("language", "javascript");
		javascript.addAttribute("src", "./html/script.js");
		
		head.addContent(meta);
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
		String cleansedExample = example.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		div.addContent(new HtmlText(cleansedExample));
		
		_openTags.peek().addContent(div);
	}
	
	public void beginHidingParagraph(String descriptionOfParagraph)
	{
		HtmlTagPair div = new HtmlTagPair("div");
		
		HtmlTagPair description = new HtmlTagPair("div");
		description.addAttribute("class", "feature-title");
		description.addContent(new HtmlText(descriptionOfParagraph));
		
		HtmlTagPair disclosures = new HtmlTagPair("div");
		disclosures.addAttribute("class", "disclosures");
		disclosures.addContent(new HtmlText("<a name=\""+ descriptionOfParagraph +"\"></a>"));
		disclosures.addContent(description);
		disclosures.addContent(div);
		
		_hidingSerialStack.push(_serialCounter++);
		_openTags.peek().addContent(disclosures);
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
		os.write(_root.stringify().toString().getBytes());
		os.close();
	}
	
	public String stringify()
	{
		return _root.stringify().toString();
	}

	public void mark()
	{
		_markBu = new Backup(_serialCounter, _indentSerialStack, _hidingSerialStack, _openTags);
		_root.mark();
	}

	public void reset()
	{
		_markBu.restore(this);
		_root.reset();
	}
	
	private class Backup
	{
		private int __serialCounter;
		private Stack<Integer> __indentSerialStack;
		private Stack<Integer> __hidingSerialStack;
		private Stack<HtmlTagPair> __openTags;
		
		@SuppressWarnings("unchecked")
		public Backup(int serialCounter,
					  Stack<Integer> indentSerialStack,
					  Stack<Integer> hidingSerialStack,
					  Stack<HtmlTagPair> openTags)
		{
			__serialCounter = serialCounter;
			__indentSerialStack = (Stack<Integer>) indentSerialStack.clone();
			__hidingSerialStack = (Stack<Integer>) hidingSerialStack.clone();
			__openTags = (Stack<HtmlTagPair>) openTags.clone();
		}
		
		public void restore(HtmlReportLogger logger)
		{
			logger._serialCounter = __serialCounter;
			logger._indentSerialStack = __indentSerialStack;
			logger._hidingSerialStack = __hidingSerialStack;
			logger._openTags = __openTags;
		}
	}

	public void setLocale(Locale locale)
	{
		_labels = ResourceBundle.getBundle("global", locale);
	}
}
