package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest4Test
{
	private static final File _senselessExecutable = new File("./testFiles/senselessExecutable.bat");
	
	
	private static final File _trivial = new File("./testFiles/Quest4/triv.conf");
	private static final File _someConfig = _trivial;
	
	
	@Test
	public final void testFileTrivial() throws IOException
	{
		Quest4 SUT = new Quest4(new ConfigFile(_trivial), _senselessExecutable);
		//TODO
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest4 SUT = new Quest4(new ConfigFile(_someConfig), _senselessExecutable);
		assertTrue(SUT.getID().equals("Quest4"));
	}
	
	@Test
	public final void testIsCritical() throws IOException
	{
		Quest4 SUT = new Quest4(new ConfigFile(_someConfig), _senselessExecutable);
		assertFalse(SUT.isCritical());
	}
}
