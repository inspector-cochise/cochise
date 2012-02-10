package org.akquinet.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;


public class SessionManager
{
	private static final long FOUR_HOURS = 14400000;

	private static SessionManager _default;
	private static Random _generator = new Random();
	
	private HashMap<Integer, Date> _validSessions;
	
	private SessionManager()
	{
	}
	
	public int openNewSession()
	{
		int id = _generator.nextInt();
		_validSessions.put(id, new Date());
		return id;
	}
	
	public boolean isValidSessionId(int id)
	{
		Date creationDate = _validSessions.get(id);
		if(creationDate == null)
		{
			return false;
		}
		if(creationDate.getTime() - System.currentTimeMillis() >= FOUR_HOURS)
		{
			_validSessions.remove(id);
		}
		return false;
	}
	
	public static SessionManager getDefault()
	{
		return _default;
	}
}
