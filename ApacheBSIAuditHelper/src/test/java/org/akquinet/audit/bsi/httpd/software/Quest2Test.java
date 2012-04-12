package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.akquinet.test.util.RethrowingThread;
import org.junit.Test;

public class Quest2Test
{
	private static final String _userDir = System.getProperty("user.dir");
	private static final File _correctListing = new File("./testFiles/Quest2/version.bat");
	private static final String SCRIPT_PATH = _userDir +  "/testFiles/Quest2/";
	private static final String _1_1_1 = "1.1.1.bat";
	private static final String _1_1_2 = "1.1.2.bat";
	private static final String _1_2_1 = "1.2.1.bat";
	private static final String _2_1_1 = "2.1.1.bat";
	
	@Test
	public final void testNegative() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				final InputStream stdin = System.in;
				
				Quest2 SUT = new Quest2(_correctListing, SCRIPT_PATH, _1_1_2, _1_1_1);
				SUT.initialize();
				System.setIn(new ByteArrayInputStream("No\n".getBytes()));
				assertFalse(SUT.answer());
				
				SUT = new Quest2(_correctListing, SCRIPT_PATH, _1_2_1, _1_1_1);
				SUT.initialize();
				System.setIn(new ByteArrayInputStream("No\n".getBytes()));
				assertFalse(SUT.answer());
				
				SUT = new Quest2(_correctListing, SCRIPT_PATH, _2_1_1, _1_1_1);
				SUT.initialize();
				System.setIn(new ByteArrayInputStream("No\n".getBytes()));
				assertFalse(SUT.answer());
				
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(4000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest2 asks for more user input than expected. Check that!");
		}
		
		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testPositive() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				final InputStream stdin = System.in;
				
				Quest2 SUT = new Quest2(_correctListing, SCRIPT_PATH, _1_1_1, _1_1_1);
				SUT.initialize();
				assertTrue(SUT.answer());
				
				SUT = new Quest2(_correctListing, SCRIPT_PATH, _2_1_1, _1_1_1);
				SUT.initialize();
				System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
				assertTrue(SUT.answer());
				
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(4000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest2 asks for more user input than expected. Check that!");
		}
		
		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testCorrectListing() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				final PrintStream stdout = System.out;
				
				Quest2 SUT = new Quest2(_correctListing, SCRIPT_PATH, _1_1_1, _1_1_1);
				SUT.initialize();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				System.setOut(new PrintStream(out));
				
				SUT.answer();
				
				String printed = out.toString();
				printed = printed.replaceAll("\r", "");	// there are problems with
				printed = printed.replaceAll("\n", ""); // String.matches() and newlines
				System.setOut(stdout);
				System.out.print(printed);
				assertTrue(printed.matches(".*12345VERSION54321.*"));
			}
		};
		
		th.start();
		th.join(4000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest2 asks for more user input than expected. Check that!");
		}
		
		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testRunningIsNewerThanLatestStable() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				Quest2 SUT = new Quest2(_correctListing, SCRIPT_PATH, _1_1_1, _2_1_1);
				SUT.initialize();
				assertFalse(SUT.answer());
			}
		};
		
		th.start();
		th.join(4000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest2 asks for more user input than expected. Check that!");
		}
		
		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest2 SUT = new Quest2(_correctListing);
		assertTrue(SUT.getID().equals("Quest2"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest2 SUT = new Quest2(_correctListing);
		assertFalse(SUT.isCritical());
	}
}
