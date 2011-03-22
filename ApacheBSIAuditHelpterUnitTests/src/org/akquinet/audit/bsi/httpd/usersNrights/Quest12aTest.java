package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Quest12aTest
{
	private static final String _scriptPath = "./testFiles/Quest12a/";
	private static final String _emptyScript = "emptyScript.bat";
	private static final String _failScript = "failScript.bat";
	
	private static final String _getUserNGroup = "getUserNGroup.bat";
	
	private static final String _apacheExecutable = "apache2";
	
	@Test
	public final void testNegativeTrivial() throws Throwable
	{
		Quest12a SUT = new Quest12a(_scriptPath, _failScript, _getUserNGroup, _apacheExecutable);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws Throwable
	{
		Quest12a SUT = new Quest12a(_scriptPath, _emptyScript, _getUserNGroup, _apacheExecutable);
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID()
	{
		Quest12a SUT = new Quest12a(_apacheExecutable);
		assertTrue(SUT.getID().equals("Quest12a"));
	}

	@Test
	public final void testIsCritical()
	{
		Quest12a SUT = new Quest12a(_apacheExecutable);
		assertFalse(SUT.isCritical());
	}
}
