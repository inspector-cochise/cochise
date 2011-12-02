package org.akquinet.audit.bsi.httpd.os;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Quest1Test
{
	private static final String _userDir = System.getProperty("user.dir");
	private static final String _commandPath = _userDir + "/testFiles/";
	private static final String _posExecutable = "emptyScript.bat";
	private static final String _negExecutable = "failScript.bat";
	
	@Test
	public final void testNoHighSecurityLevel()
	{
		Quest1 SUT = new Quest1(false, _commandPath, _negExecutable);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testNegative()
	{
		Quest1 SUT = new Quest1(true, _commandPath, _negExecutable);
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositive()
	{
		Quest1 SUT = new Quest1(true, _commandPath, _posExecutable);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID()
	{
		Quest1 SUT = new Quest1(false);
		assertTrue(SUT.getID().equals("Quest1"));
	}
	
	@Test
	public final void testIsCritical()
	{
		Quest1 SUT = new Quest1(false);
		assertFalse(SUT.isCritical());
	}
}
