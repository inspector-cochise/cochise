package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest10Test
{
	private static final File _someConfig = new File("./testFiles/Quest10/someConfig.conf");
	private static final File _emptyConfig = new File("./testFiles/Quest10/emptyConfig.conf");
	
	private static final String _commandPath = "./testFiles/";
	
	private static final String _emptyScript =  "emptyScript.bat";
	private static final String _failScript =  "failScript.bat";
	
	@Test
	public final void testNegativeTrivial() throws IOException
	{
		Quest10 SUT = new Quest10(new ConfigFile(_someConfig), _commandPath, _failScript);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws IOException
	{
		Quest10 SUT = new Quest10(new ConfigFile(_someConfig), _commandPath, _emptyScript);
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testPositiveEmptyConf() throws IOException
	{
		Quest10 SUT = new Quest10(new ConfigFile(_emptyConfig), _commandPath, _failScript);
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest10 SUT = new Quest10(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest10"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest10 SUT = new Quest10(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
