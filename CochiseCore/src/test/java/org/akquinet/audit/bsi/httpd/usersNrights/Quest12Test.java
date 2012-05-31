package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest12Test
{
	private static final String _userDir = System.getProperty("user.dir");
	
	//a0 b1 means Quest12a = false and Quest12b = true...
	private static final File _a0 = new File(_userDir + "/testFiles/Quest12/a0.conf");
	private static final File _a1 = new File(_userDir + "/testFiles/Quest12/a1.conf");
	
	private static final File _someConfig = _a0;
	
	private static final String _scriptPath = _userDir + "/testFiles/Quest12/";
	private static final String _emptyScript = "emptyScript.bat";
	private static final String _failScript = "failScript.bat";
	
	private static final String _getUserNGroup = "getUserNGroup.bat";
	
	private static final String _apacheExecutable = "apache2";

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
		InputStream stdin = System.in;
		Quest12 SUT = new Quest12(new ConfigFile(_a0), _scriptPath, _failScript, _getUserNGroup, _apacheExecutable);
		SUT.initialize();
		System.setIn(new ByteArrayInputStream("No\n".getBytes()));
		
		assertFalse(SUT.answer());
		System.setIn(stdin);
	}
	
	@Test
	public final void test_a0_b1() throws Exception
	{
		Quest12 SUT = new Quest12(new ConfigFile(_a0), _scriptPath, _emptyScript, _getUserNGroup, _apacheExecutable);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void test_a1() throws Exception
	{
		Quest12 SUT = new Quest12(new ConfigFile(_a1), _scriptPath, _emptyScript, _getUserNGroup, _apacheExecutable);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID() throws IOException, ParserException
	{
		Quest12 SUT = new Quest12(new ConfigFile(_someConfig), _apacheExecutable);
		assertTrue(SUT.getID().equals("Quest12"));
	}

	@Test
	public final void testIsCritical() throws IOException, ParserException
	{
		Quest12 SUT = new Quest12(new ConfigFile(_someConfig), _apacheExecutable);
		assertFalse(SUT.isCritical());
	}
}
