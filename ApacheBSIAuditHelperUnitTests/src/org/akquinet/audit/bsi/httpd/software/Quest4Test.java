package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.junit.Test;

public class Quest4Test
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final File _emptyExec = new File(_userDir + "/testFiles/emptyScript.bat");
	private static final File _someExec = new File(_userDir + "/testFiles/Quest4/someModules.bat");

	private static final File _trivial = new File(_userDir + "/testFiles/Quest4/triv.conf");
	private static final File _someConfig = _trivial;

	@Test
	public final void testCorrectAnswer() throws IOException, ParserException
	{
		InputStream stdin = System.in;
		Quest4 SUT = new Quest4(new ConfigFile(_trivial), _emptyExec);

		System.setIn(new ByteArrayInputStream("No\n".getBytes()));
		assertFalse("No should imply false", SUT.answer());
		
		System.setIn(new ByteArrayInputStream("\n".getBytes()));
		assertFalse("Standard option should be No and imply false", SUT.answer());

		System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
		assertTrue("Yes should imply true", SUT.answer());
		
		System.setIn(stdin);
	}
	

	@Test
	public final void testGetID() throws IOException, ParserException
	{
		Quest4 SUT = new Quest4(new ConfigFile(_someConfig), _emptyExec);
		assertTrue(SUT.getID().equals("Quest4"));
	}

	@Test
	public final void testIsCritical() throws IOException, ParserException
	{
		Quest4 SUT = new Quest4(new ConfigFile(_someConfig), _emptyExec);
		assertFalse(SUT.isCritical());
	}
}

