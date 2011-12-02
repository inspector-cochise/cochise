package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class Quest6Test
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final File _emptyExec = new File(_userDir + "/testFiles/emptyScript.bat");
	private static final File _failExec = new File(_userDir + "/testFiles/failScript.bat");
	
	@Test
	public final void testNegative()
	{
		Quest6 SUT = new Quest6(_failExec);
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositive()
	{
		Quest6 SUT = new Quest6(_emptyExec);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest6 SUT = new Quest6(_emptyExec);
		assertTrue(SUT.getID().equals("Quest6"));
	}
	
	@Test
	public final void testIsCritical() throws IOException
	{
		Quest6 SUT = new Quest6(_emptyExec);
		assertTrue(SUT.isCritical());
	}
}
