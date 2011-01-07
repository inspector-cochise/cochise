package org.akquinet.httpd.syntax;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Directive extends Statement
{
	private String _name;
	private String _value;
	private int _linenumber;

	public Directive(SyntaxElement parent) throws IOException
	{
		super(parent);
		parse();
	}

	@Override
	protected void parse() throws IOException
	{
		try
		{
			_linenumber = getActualLine();
			eatBlanks();
			_name = (new Name(this)).getStringContent();
			eatBlanks();
			_value = (new Value(this)).getStringContent();
		}
		catch (FileEndException e)
		{
			throw new RuntimeException(UNEXPECTED_EOF_ERROR_STRING);
		}

		try
		{
			eatRestOfLine();
		}
		catch (FileEndException e)
		{
		} // no newline at end of file shouldn't be punished too hard
	}

	/**
	 * This method will whack _value into Pieces at the blanks (they are not contained in the return value). It's like interpreting
	 * _value as list of parameters.
	 * It will convert parameters {"true", "on", "yes"} (not case sensitive) to "on" (lower case)
	 * and similar {"false", "off", "no"} gets "off".
	 * It can handle quotation marks. That means if a Parameter stars with '"' all following parameters until a parameter ending with a
	 * '"' will be merged into one parameter (with the quotation marks) with the original blank pattern in between. If no closing
	 * quotation mark exists, no parameters will be folded into one single parameter.
	 * @return array filled with parameters
	 */
	public String[] getParsedValue()
	{
		List<String> retList = new LinkedList<String>();
		String value = _value;
		int i = 0;
		
		// blanks are delimiters between parameters
		while(i < value.length())
		{
			// first eat blanks at the beginning
			while(i < value.length() && isBlank(value.charAt(i)))
			{
				i++;
			}
			value = value.substring(i);
			i = 0;
			// quotation marks get strings containing blanks into one parameter
			if(value.indexOf('"') == i)
			{
				// for our first occurrence we have  value.indexOf('"')==0 because i is 0
				int l = 0+1 + value.substring(0+1).indexOf('"'); // second occurrence
				try
				{
					if(!( l != 0+1 -1 || !isBlank(value.charAt(l+1)) ))
					{
						// l has to be greater than 0 so we don't need to check that to prevent adding an empty string
						retList.add(value.substring(0, l+1));
						value = value.substring(l+1);
						i = 0;
					}
				}
				catch(IndexOutOfBoundsException e) //that means l is the last index
				{
					// l has to be greater than 0 so we don't need to check that to prevent adding an empty string
					retList.add(value.substring(0, l+1));
					value = value.substring(l+1);
					i = 0;
				}
			}

			while(i < value.length() && !isBlank(value.charAt(i)))
			{
				i++;
			}
			if(i > 0)
			{
				retList.add(value.substring(0, i));
				value = value.substring(i);
				i = 0;
			}
		}
		String[] ret = new String[0];
		ret = retList.toArray(ret);
		for(int j = 0; j < ret.length; j++)
		{
			// {true, on, yes} -> "on"
			if(ret[j].equalsIgnoreCase("true") ||
				ret[j].equalsIgnoreCase("on") ||
				ret[j].equalsIgnoreCase("yes"))
			{
				ret[j] = "on";
			}
			else
			// {false, off, no} -> "off"
			if(ret[j].equalsIgnoreCase("false") ||
					ret[j].equalsIgnoreCase("off") ||
					ret[j].equalsIgnoreCase("no"))
			{
				ret[j] = "off";
			}
		}
		
		return ret;
	}

	public String getName()
	{
		return _name;
	}
	
	public String getNameLowerCase()
	{
		return _name.toLowerCase();
	}

	public String getValue()
	{
		return _value;
	}

	public int getLinenumber()
	{
		return _linenumber;
	}

	@Override
	public List<Directive> getDirective(String name)
	{
		List<Directive> ret = new LinkedList<Directive>();

		if( name.equals(_name) )
		{
			ret.add(this);
		}
		
		return ret;
	}

	@Override
	public List<Directive> getDirectiveIgnoreCase(String name)
	{
		List<Directive> ret = new LinkedList<Directive>();

		if( name.equalsIgnoreCase(_name) )
		{
			ret.add(this);
		}
		
		return ret;
	}
	
	
}
