package org.akquinet.audit.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HtmlTagPair implements HtmlElement
{
	private List<HtmlElement> _content;
	
	private String _name;
	private Map<String,String> _attributes;
	
	public HtmlTagPair(String name)
	{
		_content = new LinkedList<HtmlElement>();
		_name = name;
	}
	
	public void addAttribute(String name, String value)
	{
		_attributes.put(name, value);
	}
	
	public void addContent(HtmlElement content)
	{
		_content.add(content);
	}
	
	@Override
	public StringBuffer stringify()
	{
		StringBuffer ret = new StringBuffer();
		
		ret = ret.append("<" + _name + " ");
		for(String attributeName : _attributes.keySet())
		{
			ret = ret.append(attributeName).append("=\"").append(_attributes.get(attributeName)).append("\" ");
		}
		ret = ret.append(">\n");
		
		for(HtmlElement tp : _content)
		{
			ret.append(tp.stringify());
		}
		
		ret = ret.append("</" + _name + ">\n");
		return ret;
	}
}
