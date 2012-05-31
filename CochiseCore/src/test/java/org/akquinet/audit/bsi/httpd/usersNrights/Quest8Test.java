package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest8Test
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final String _emptyExec = "emptyScript.bat";
	private static final String _failExec = "failScript.bat";
	
	private static final File _someFile = new File(_userDir + "/testFiles/emptyScript.bat");
	private static ConfigFile _conf = null;
	
	@BeforeClass
	public static final void setUpClass()
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
	
	@Before
	public final void setUp() throws IOException, ParserException
	{
		_conf = new ConfigFile(_userDir + "/testFiles/Quest8/emptyConf.conf");
	}
	
	@Test
	public final void testNegative() throws Exception
	{
		Quest8 SUT = new Quest8(_someFile, _conf, _userDir + "/testFiles/", _failExec, true);
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositive() throws Exception
	{
		Quest8 SUT = new Quest8(_someFile, _conf, _userDir + "/testFiles/", _emptyExec, true);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest8 SUT = new Quest8(_someFile, _conf, true);
		assertTrue(SUT.getID().equals("Quest8"));
	}
	
	@Test
	public final void testIsCritical() throws IOException
	{
		Quest8 SUT = new Quest8(_someFile, _conf, true);
		assertFalse(SUT.isCritical());
	}
}
