package org.akquinet.audit.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HtmlTagPair implements HtmlElement
{
	private List<HtmlElement> _content;
	
	private String _name;
	private Map<String,String> _attributes;
	private Integer _serial;
	
	public HtmlTagPair(String name)
	{
		this(name, null);
	}
	
	public HtmlTagPair(String name, Integer serial)
	{
		_content = new LinkedList<HtmlElement>();
		_name = name;
		_serial = serial;
		_attributes = new HashMap<String, String>();
	}
	
	/**
	 * 
	 * @return The serial number. Null if not set!
	 */
	public Integer getSerial()
	{
		return _serial;
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
