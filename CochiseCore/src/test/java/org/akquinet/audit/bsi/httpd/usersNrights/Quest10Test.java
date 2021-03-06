package org.akquinet.audit.bsi.httpd.usersNrights;

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

public class Quest10Test
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final File _someConfig = new File(_userDir + "/testFiles/Quest10/someConfig.conf");
	private static final File _emptyConfig = new File(_userDir + "/testFiles/Quest10/emptyConfig.conf");
	
	private static final String _commandPath = _userDir + "/testFiles/";
	
	private static final String _emptyScript =  "emptyScript.bat";
	private static final String _failScript =  "failScript.bat";
	
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
		Quest10 SUT = new Quest10(new ConfigFile(_someConfig), _commandPath, _failScript);
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws Exception
	{
		Quest10 SUT = new Quest10(new ConfigFile(_someConfig), _commandPath, _emptyScript);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testPositiveEmptyConf() throws Exception
	{
		Quest10 SUT = new Quest10(new ConfigFile(_emptyConfig), _commandPath, _failScript);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID() throws IOException, ParserException
	{
		Quest10 SUT = new Quest10(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest10"));
	}

	@Test
	public final void testIsCritical() throws IOException, ParserException
	{
		Quest10 SUT = new Quest10(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
