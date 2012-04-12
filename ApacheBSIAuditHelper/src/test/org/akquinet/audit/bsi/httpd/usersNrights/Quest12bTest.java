package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.junit.Test;

public class Quest12bTest
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final File _negativeTrivial = new File(_userDir + "/testFiles/Quest12b/neg_triv.conf");
	private static final File _negativePartial1 = new File(_userDir + "/testFiles/Quest12b/neg_part1.conf");
	private static final File _negativePartial2 = new File(_userDir + "/testFiles/Quest12b/neg_part2.conf");
	
	private static final File _positiveTrivial = new File(_userDir + "/testFiles/Quest12b/pos_triv.conf");
	
	private static final File _someConfig = _negativeTrivial;

	@Test
	public final void testNegativeTrivial() throws Exception
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_negativeTrivial), _userDir + "/testFiles/", "emptyScript.bat");
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativePartial1() throws Exception
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_negativePartial1), _userDir + "/testFiles/", "emptyScript.bat");
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativePartial2() throws Exception
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_negativePartial2), _userDir + "/testFiles/", "emptyScript.bat");
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeExec() throws Exception
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_positiveTrivial), _userDir + "/testFiles/", "failScript.bat");
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws Exception
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_positiveTrivial), _userDir + "/testFiles/", "emptyScript.bat");
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID() throws IOException, ParserException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest12b"));
	}

	@Test
	public final void testIsCritical() throws IOException, ParserException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
