package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest5bTest
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final File _negativeTrivial = new File(_userDir + "/testFiles/Quest5b/neg_triv.conf");
	private static final File _negativeConditional = new File(_userDir + "/testFiles/Quest5b/neg_cond.conf");
	private static final File _negativeMalformed = new File(_userDir + "/testFiles/Quest5b/neg_malf.conf");
	
	private static final File _positiveTrivial1 = new File(_userDir + "/testFiles/Quest5b/pos_triv1.conf");
	private static final File _positiveTrivial2 = new File(_userDir + "/testFiles/Quest5b/pos_triv2.conf");
	
	private static final File _someConfig = _negativeTrivial;

	@Test
	public final void testNegativeTrivial() throws Exception
	{
		Quest5b SUT = new Quest5b(new ConfigFile(_negativeTrivial));
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeConditional() throws Exception
	{
		Quest5b SUT = new Quest5b(new ConfigFile(_negativeConditional));
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeMalformed() throws Exception
	{
		Quest5b SUT = new Quest5b(new ConfigFile(_negativeMalformed));
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial1() throws Exception
	{
		Quest5b SUT = new Quest5b(new ConfigFile(_positiveTrivial1));
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial2() throws Exception
	{
		Quest5b SUT = new Quest5b(new ConfigFile(_positiveTrivial2));
		SUT.initialize();
		assertTrue(SUT.answer());
	}

	@Test
	public final void testGetID() throws Exception
	{
		Quest5b SUT = new Quest5b(new ConfigFile(_someConfig));
		SUT.initialize();
		assertTrue(SUT.getID().equals("Quest5b"));
	}

	@Test
	public final void testIsCritical() throws Exception
	{
		Quest5b SUT = new Quest5b(new ConfigFile(_someConfig));
		SUT.initialize();
		assertFalse(SUT.isCritical());
	}
}
