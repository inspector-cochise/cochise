package org.akquinet.audit.ui;

public class HtmlText implements HtmlElement
{

	private String _text;
	
	public HtmlText(String text)
	{
		_text = text;
	}

	@Override
	public StringBuffer stringify()
	{
		return new StringBuffer(_text);
	}

}
