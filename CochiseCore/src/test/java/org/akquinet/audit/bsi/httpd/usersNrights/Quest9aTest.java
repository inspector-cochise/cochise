package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest9aTest
{
	private static final String _userDir = System.getProperty("user.dir");
	
	public static final String _commandPath = _userDir + "/testFiles/";
	
	public static final String _emptyExecutable = "emptyScript.bat";
	public static final String _failExecutable = "failScript.bat";
	public static final String _listExecutable = "Quest9a/list.bat";
	
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
	public final void testNegative()
	{
		Quest9a SUT = new Quest9a("somePath", "someUser", _commandPath, _failExecutable, true);
		SUT.initialize();
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositive()
	{
		Quest9a SUT = new Quest9a("somePath", "someUser", _commandPath, _emptyExecutable, true);
		SUT.initialize();
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testCorrectListing()
	{
		PrintStream stdout = System.out;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		
		Quest9a SUT = new Quest9a("somePath", "someUser", _commandPath, _listExecutable, true);
		SUT.initialize();
		SUT.answer();
		
		String printed = out.toString();
		printed = printed.replaceAll("\r", "");	// there are problems with
		printed = printed.replaceAll("\n", ""); // String.matches() and newlines
		System.setOut(stdout);
		System.out.print(printed);
		assertTrue(printed.matches(".*123ListTest321.*1234ListTest4321.*"));
	}
	
	@Test
	public final void testGetID()
	{
		Quest9a SUT = new Quest9a("somePath", "someUser", true);
		assertTrue(SUT.getID().equals("Quest9a"));
	}

	@Test
	public final void testIsCritical()
	{
		Quest9a SUT = new Quest9a("somePath", "someUser", true);
		assertFalse(SUT.isCritical());
	}
}
