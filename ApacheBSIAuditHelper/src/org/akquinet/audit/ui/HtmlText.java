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

	@Override
	public void mark()
	{
		//_text can't be changed, so there's nothing to do
	}

	@Override
	public void reset()
	{
		//_text can't be changed, so there's nothing to do
	}

}
