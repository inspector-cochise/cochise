package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest12bTest
{
	private static final File _negativeTrivial = new File("./testFiles/Quest12b/neg_triv.conf");
	private static final File _negativePartial1 = new File("./testFiles/Quest12b/neg_part1.conf");
	private static final File _negativePartial2 = new File("./testFiles/Quest12b/neg_part2.conf");
	
	private static final File _positiveTrivial = new File("./testFiles/Quest12b/pos_triv.conf");
	
	private static final File _someConfig = _negativeTrivial;

	@Test
	public final void testNegativeTrivial() throws IOException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_negativeTrivial), "./testFiles/", "emptyScript.bat");
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativePartial1() throws IOException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_negativePartial1), "./testFiles/", "emptyScript.bat");
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativePartial2() throws IOException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_negativePartial2), "./testFiles/", "emptyScript.bat");
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeExec() throws IOException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_positiveTrivial), "./testFiles/", "failScript.bat");
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws IOException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_positiveTrivial), "./testFiles/", "emptyScript.bat");
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest12b"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
