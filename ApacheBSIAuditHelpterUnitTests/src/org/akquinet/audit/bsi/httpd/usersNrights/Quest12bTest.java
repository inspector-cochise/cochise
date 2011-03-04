package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest12bTest
{
	private static final File _negativeTrivial = new File("./testFiles/Quest12b/neg_triv.conf");
	
	private static final File _positiveTrivial = new File("./testFiles/Quest12b/pos_triv.conf");
	
	private static final File _someConfig = _negativeTrivial;

	@Test
	public final void testNegativeTrivial() throws IOException
	{
		Quest12b SUT = new Quest12b(new ConfigFile(_negativeTrivial), "./testFiles/", "emptyScript.bat");
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
