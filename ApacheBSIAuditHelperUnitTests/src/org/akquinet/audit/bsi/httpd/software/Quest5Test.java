package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest5Test
{
	//a0 b1 means Quest5a = false and Quest5b = true...
	private static final File _a0_b0 = new File("./testFiles/Quest5/a0_b0.conf");
	private static final File _a0_b1 = new File("./testFiles/Quest5/a0_b1.conf");
	private static final File _a1_b0 = new File("./testFiles/Quest5/a1_b0.conf");
	private static final File _a1_b1 = new File("./testFiles/Quest5/a1_b1.conf");
	
	private static final File _someConfig = _a0_b0;

	@Test
	public final void test_a0_b0() throws IOException
	{
		Quest5 SUT = new Quest5(new ConfigFile(_a0_b0));
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void test_a0_b1() throws IOException
	{
		Quest5 SUT = new Quest5(new ConfigFile(_a0_b1));
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void test_a1_b0() throws IOException
	{
		Quest5 SUT = new Quest5(new ConfigFile(_a1_b0));
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void test_a1_b1() throws IOException
	{
		Quest5 SUT = new Quest5(new ConfigFile(_a1_b1));
		assertTrue(SUT.answer());
	}

	@Test
	public final void testGetID() throws IOException
	{
		Quest5 SUT = new Quest5(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest5"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest5 SUT = new Quest5(new ConfigFile(_someConfig));
		assertTrue(SUT.isCritical());
	}
}