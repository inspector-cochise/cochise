package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest4Test
{
	private static final File _emptyExec = new File("./testFiles/emptyScript.bat");
	private static final File _someExec = new File("./testFiles/Quest4/someModules.bat");

	private static final File _trivial = new File("./testFiles/Quest4/triv.conf");
	private static final File _someConfig = _trivial;

	@Test
	public final void testCorrectAnswer() throws IOException
	{
		InputStream stdin = System.in;
		Quest4 SUT = new Quest4(new ConfigFile(_trivial), _emptyExec, "./tmp");

		System.setIn(new ByteArrayInputStream("No\n".getBytes()));
		assertFalse("No should imply false", SUT.answer());
		
		System.setIn(new ByteArrayInputStream("\n".getBytes()));
		assertFalse("Standard option should be No and imply false", SUT.answer());

		System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
		assertTrue("Yes should imply true", SUT.answer());
		
		System.setIn(stdin);
	}
	
	@Test
	public final void testFile() throws IOException
	{
		InputStream stdin = System.in;
		Quest4 SUT = new Quest4(new ConfigFile(_trivial), _emptyExec, "./tmp");

		System.setIn(new ByteArrayInputStream("No\n".getBytes()));
		SUT.answer();

		System.setIn(stdin);
		
		String[] loadedModules = getLoadedModules().split("\n");
		assertEquals("all loaded modules should be displayed correctly", 4, loadedModules.length);
		
		assertTrue("module authz_host should be displayed correctly", loadedModules[0].matches(".*LoadModule[ \t]*authz_host_module[ \t]*/usr/lib/apache2/modules/mod_authz_host\\.so.*"));
		assertTrue("module cgi should be displayed correctly", loadedModules[1].matches(".*LoadModule[ \t]*cgi_module[ \t]*/usr/lib/apache2/modules/mod_cgi\\.so.*"));
		assertTrue("module dbd should be displayed correctly", loadedModules[2].matches(".*LoadModule[ \t]*dbd_module[ \t]*/usr/lib/apache2/modules/mod_dbd\\.so.*"));
		assertTrue("module dir should be displayed correctly", loadedModules[3].matches(".*LoadModule[ \t]*dir_module[ \t]*/usr/lib/apache2/modules/mod_dir\\.so.*"));
	}
	
	@Test
	public final void testCompiledInto() throws IOException
	{
		InputStream stdin = System.in;
		Quest4 SUT = new Quest4(new ConfigFile(_trivial), _someExec, "./tmp");
		System.setIn(new ByteArrayInputStream("No\n".getBytes()));
		SUT.answer();
		System.setIn(stdin);
		
		String[] compiledIntoModules = getCompiledIntoModules().split("(\r\n|\n)");
		assertEquals("all compiled into modules should be displayed correctly", 6, compiledIntoModules.length);
		
		assertTrue("module core should be displayed correctly", compiledIntoModules[1].contains("core.c"));
		assertTrue("module log_config should be displayed correctly", compiledIntoModules[2].contains("mod_log_config.c"));
		assertTrue("module logio should be displayed correctly", compiledIntoModules[3].contains("mod_logio.c"));
		assertTrue("module prefork should be displayed correctly", compiledIntoModules[4].contains("prefork.c"));
		assertTrue("module so should be displayed correctly", compiledIntoModules[5].contains("mod_so.c"));
	}

	@Test
	public final void testGetID() throws IOException
	{
		Quest4 SUT = new Quest4(new ConfigFile(_someConfig), _emptyExec);
		assertTrue(SUT.getID().equals("Quest4"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest4 SUT = new Quest4(new ConfigFile(_someConfig), _emptyExec);
		assertFalse(SUT.isCritical());
	}
	
	private static String getLoadedModules() throws IOException
	{
		return getOutputFileContent().split("----\n")[3];
	}
	
	private static String getCompiledIntoModules() throws IOException
	{
		return getOutputFileContent().toString().split("----\n")[1];
	}
	
	private static String getOutputFileContent() throws IOException
	{
		FileInputStream is = new FileInputStream("./tmp/moduleList.txt");
		StringBuffer buf = new StringBuffer();
		
		int b = is.read();
		while(b != -1)
		{
			buf.append((char)b);
			b = is.read();
		}
		
		is.close();
		
		return buf.toString();
	}
}

