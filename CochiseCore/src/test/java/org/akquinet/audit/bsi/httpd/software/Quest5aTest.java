package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest5aTest
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final File _negativeTrivial = new File(_userDir + "/testFiles/Quest5a/neg_triv.conf");
	private static final File _negativeConditional = new File(_userDir + "/testFiles/Quest5a/neg_cond.conf");
	private static final File _positiveTrivial = new File(_userDir + "/testFiles/Quest5a/pos_triv.conf");
	private static final File _someConfig = _negativeTrivial;

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
	public final void testNegativeTrivial() throws Exception
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_negativeTrivial));
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeConditional() throws Exception
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_negativeConditional));
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws Exception
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_positiveTrivial));
		SUT.initialize();
		assertTrue(SUT.answer());
	}

	@Test
	public final void testGetID() throws ParserException, IOException
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest5a"));
	}

	@Test
	public final void testIsCritical() throws ParserException, IOException 
	{
		Quest5a SUT = new Quest5a(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
