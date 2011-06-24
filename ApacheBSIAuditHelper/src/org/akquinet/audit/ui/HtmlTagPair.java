package org.akquinet.audit.ui;

import java.util.HashMap;
import java.util.LinkedList;

public class HtmlTagPair implements HtmlElement
{
	private LinkedList<HtmlElement> _content;
	
	private String _name;
	private HashMap<String,String> _attributes;
	private Integer _serial;
	
	private Backup _markBu;
	
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
		_markBu = new Backup(_content, _attributes);
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

	public void mark()
	{
		_markBu = new Backup(_content, _attributes);
		for (HtmlElement ele : _content)
		{
			ele.mark();
		}
	}

	public void reset()
	{
		_markBu.restore(this);
		for (HtmlElement ele : _content)
		{
			ele.reset();
		}
	}
	
	private class Backup
	{
		private LinkedList<HtmlElement> __content;
		private HashMap<String,String> __attributes;
		
		@SuppressWarnings("unchecked")
		public Backup(LinkedList<HtmlElement> content, HashMap<String,String> attributes)
		{
			__content = (LinkedList<HtmlElement>) content.clone();
			__attributes = (HashMap<String, String>) attributes.clone();
		}
		
		public void restore(HtmlTagPair tp)
		{
			tp._content = __content;
			tp._attributes = __attributes;
		}
	}
}
