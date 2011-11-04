package org.akquinet.httpd;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class MultipleMarkerInputStream extends BufferedInputStream
{
	private Stack<Integer> _markPositions;
	
	public MultipleMarkerInputStream(InputStream in)
	{
		super(in);
		_markPositions = new Stack<Integer>();
		_markPositions.push(-1);
	}
	
	@Override
	public synchronized void mark(int readlimit)
	{
		_markPositions.push(pos);
		marklimit = readlimit < marklimit ? marklimit : readlimit;
		super.mark(readlimit);
	}
	
	@Override
	public synchronized void reset() throws IOException
	{
		if(_markPositions.peek() != -1)
		{
			_markPositions.pop();
		}
		
		super.reset();
		markpos = _markPositions.peek();
	}

}
