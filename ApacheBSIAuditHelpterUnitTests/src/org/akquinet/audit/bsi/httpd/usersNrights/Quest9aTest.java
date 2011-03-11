package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.akquinet.audit.bsi.httpd.software.Quest2;
import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest9aTest
{
	public static final String _commandPath = "./testFiles/";
	
	public static final String _emptyExecutable = "emptyScript.bat";
	public static final String _failExecutable = "failScript.bat";
	public static final String _listExecutable = "Quest9a/list.bat";
	
	@Test
	public final void testNegative()
	{
		Quest9a SUT = new Quest9a("somePath", "someUser", _commandPath, _failExecutable);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositive()
	{
		Quest9a SUT = new Quest9a("somePath", "someUser", _commandPath, _emptyExecutable);
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testCorrectListing()
	{
		PrintStream stdout = System.out;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		
		Quest9a SUT = new Quest9a("somePath", "someUser", _commandPath, _listExecutable);
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
		Quest9a SUT = new Quest9a("somePath", "someUser");
		assertTrue(SUT.getID().equals("Quest9a"));
	}

	@Test
	public final void testIsCritical()
	{
		Quest9a SUT = new Quest9a("somePath", "someUser");
		assertFalse(SUT.isCritical());
	}
}
