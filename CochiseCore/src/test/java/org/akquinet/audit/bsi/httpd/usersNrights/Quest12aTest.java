package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest12aTest
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final String _scriptPath = _userDir + "/testFiles/Quest12a/";
	private static final String _emptyScript = "emptyScript.bat";
	private static final String _failScript = "failScript.bat";
	
	private static final String _getUserNGroup = "getUserNGroup.bat";
	
	private static final String _apacheExecutable = "apache2";
	
	@BeforeClass
	public static final void setUp()
	{
		if(UserCommunicator.getDefault() == null)
		{
			try
			{
				UserCommunicator.setDefault(new ConsoleUserCommunicator());
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	@Test
	public final void testNegativeTrivial() throws Throwable
	{
		Quest12a SUT = new Quest12a(_scriptPath, _failScript, _getUserNGroup, _apacheExecutable);
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws Throwable
	{
		Quest12a SUT = new Quest12a(_scriptPath, _emptyScript, _getUserNGroup, _apacheExecutable);
		SUT.initialize();
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
