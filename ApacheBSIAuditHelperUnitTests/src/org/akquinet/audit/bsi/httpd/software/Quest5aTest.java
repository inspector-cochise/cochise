package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest5aTest
{
	private static final File _negativeTrivial = new File("./testFiles/Quest5a/neg_triv.conf");
	private static final File _negativeConditional = new File("./testFiles/Quest5a/neg_cond.conf");
	private static final File _positiveTrivial = new File("./testFiles/Quest5a/pos_triv.conf");
	private static final File _someConfig = _negativeTrivial;

	@Test
	public final void testNegativeTrivial() throws IOException
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_negativeTrivial));
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeConditional() throws IOException
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_negativeConditional));
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws IOException
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_positiveTrivial));
		assertTrue(SUT.answer());
	}

	@Test
	public final void testGetID() throws IOException
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest5a"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_someConfig));
		assertTrue(SUT.isCritical());
	}
}
