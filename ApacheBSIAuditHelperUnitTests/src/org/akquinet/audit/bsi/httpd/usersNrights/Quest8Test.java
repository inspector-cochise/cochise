package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest8Test
{
	private static final String _emptyExec = "emptyScript.bat";
	private static final String _failExec = "failScript.bat";
	
	private static final File _someFile = new File("./testFiles/emptyScript.bat");
	private static ConfigFile _conf = null;
	
	@Before
	public final void setUp() throws IOException
	{
		_conf = new ConfigFile("./testFiles/Quest8/emptyConf.conf");
	}
	
	@Test
	public final void testNegative() throws IOException
	{
		Quest8 SUT = new Quest8(_someFile, _conf, "./testFiles/", _failExec, true);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositive() throws IOException
	{
		Quest8 SUT = new Quest8(_someFile, _conf, "./testFiles/", _emptyExec, true);
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
