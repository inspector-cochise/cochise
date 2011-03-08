package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.akquinet.httpd.ConfigFile;
import org.akquinet.test.util.RethrowingThread;
import org.junit.Test;

public class Quest9Test
{
	//a0 b1 means Quest9a = false and Quest9b = true...
	private static final File _noHtdocsConf = new File("./testFiles/Quest9/noHtdocs.conf");
	private static final File _htdocsConf = new File("./testFiles/Quest9/htdocs.conf");
	private static final File _malformedConf1 = new File("./testFiles/Quest9/malf1.conf");
	private static final File _malformedConf2 = new File("./testFiles/Quest9/malf1.conf");
	
	private static final File _someConfig = _noHtdocsConf;
	
	private static final File _emptyExec = new File("./testFiles/emptyScript.bat");
	private static final File _failExec = new File("./testFiles/failScript.bat");
	

	public final void testDriver(final String stdIn, final File malformedFile, final File executable, final boolean assertVal) throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				try
				{
					InputStream stdInStream = System.in;
					Quest9 SUT = new Quest9(new ConfigFile(malformedFile), executable.getAbsoluteFile().getParent() + File.separator, executable.getName());
					System.setIn(new ByteArrayInputStream(stdIn.getBytes()));
					
					assertEquals(SUT.answer(), assertVal);
					System.setIn(stdInStream);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		};
		
		th.start();
		th.join(); // TODO delete debug line
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest9 unexpectedly asks for user input check that!");
		}

		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testNegativeMalformed1() throws Throwable
	{
		testDriver("", _malformedConf1, _emptyExec, false);
	}
	
	@Test
	public final void testNegativeMalformed2() throws Throwable
	{
		testDriver("", _malformedConf2, _emptyExec, false);
	}
	
	@Test
	public final void test_a0b0() throws Throwable
	{
		testDriver("exampleuser\nNo\nNo\n", _htdocsConf, _failExec, false);
	}
	
	@Test
	public final void test_a0b1() throws Throwable
	{
		testDriver("exampleuser\n", _noHtdocsConf, _failExec, false);
	}
	
	@Test
	public final void test_a1b0() throws Throwable
	{
		testDriver("exampleuser\nNo\nNo\n", _htdocsConf, _emptyExec, false);
	}
	
	@Test
	public final void test_a1b1() throws Throwable
	{
		testDriver("exampleuser\n", _noHtdocsConf, _emptyExec, true);
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest9 SUT = new Quest9(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest9"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest9 SUT = new Quest9(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
