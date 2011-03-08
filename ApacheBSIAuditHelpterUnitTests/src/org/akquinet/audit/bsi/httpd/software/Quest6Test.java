package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class Quest6Test
{
	private static final File _emptyExec = new File("./testFiles/emptyScript.bat");
	private static final File _failExec = new File("./testFiles/failScript.bat");
	
	@Test
	public final void testNegative() throws IOException
	{
		Quest6 SUT = new Quest6(_failExec);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositive() throws IOException
	{
		Quest6 SUT = new Quest6(_emptyExec);
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
