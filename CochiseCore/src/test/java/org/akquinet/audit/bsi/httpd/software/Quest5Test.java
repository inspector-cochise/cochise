package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest5Test
{
	private static final String _userDir = System.getProperty("user.dir");
	
	//a0 b1 means Quest5a = false and Quest5b = true...
	private static final File _a0_b0 = new File(_userDir + "/testFiles/Quest5/a0_b0.conf");
	private static final File _a0_b1 = new File(_userDir + "/testFiles/Quest5/a0_b1.conf");
	private static final File _a1_b0 = new File(_userDir + "/testFiles/Quest5/a1_b0.conf");
	private static final File _a1_b1 = new File(_userDir + "/testFiles/Quest5/a1_b1.conf");
	
	private static final File _someConfig = _a0_b0;

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
	public final void test_a0_b0() throws Exception
	{
		Quest5 SUT = new Quest5(new ConfigFile(_a0_b0));
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void test_a0_b1() throws Exception
	{
		Quest5 SUT = new Quest5(new ConfigFile(_a0_b1));
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void test_a1_b0() throws Exception
	{
		Quest5 SUT = new Quest5(new ConfigFile(_a1_b0));
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void test_a1_b1() throws Exception
	{
		Quest5 SUT = new Quest5(new ConfigFile(_a1_b1));
		SUT.initialize();
		assertTrue(SUT.answer());
	}

	@Test
	public final void testGetID() throws Exception
	{
		Quest5 SUT = new Quest5(new ConfigFile(_someConfig));
		SUT.initialize();
		assertTrue(SUT.getID().equals("Quest5"));
	}

	@Test
	public final void testIsCritical() throws Exception
	{
		Quest5 SUT = new Quest5(new ConfigFile(_someConfig));
		SUT.initialize();
		assertFalse(SUT.isCritical());
	}
}
