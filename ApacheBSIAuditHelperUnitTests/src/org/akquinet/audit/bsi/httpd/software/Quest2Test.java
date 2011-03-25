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
	private static final File _emptyScript = new File("./testFiles/emptyScript.bat");
	private static final File _correctListing = new File("./testFiles/Quest2/version.bat");
	
	@Test
	public final void testNegative() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				InputStream stdin = System.in;
				Quest2 SUT = new Quest2(_emptyScript);
				
				System.setIn(new ByteArrayInputStream("No".getBytes()));
				assertFalse(SUT.answer());
				
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(2000);
		
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
				InputStream stdin = System.in;
				Quest2 SUT = new Quest2(_emptyScript);
				
				System.setIn(new ByteArrayInputStream("Yes".getBytes()));
				assertTrue(SUT.answer());
				
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest2 asks for more user input than expected. Check that!");
		}
		
		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testCorrectListing() throws IOException
	{
		InputStream stdin = System.in;
		PrintStream stdout = System.out;
		
		Quest2 SUT = new Quest2(_correctListing);
		System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		
		SUT.answer();
		
		String printed = out.toString();
		printed = printed.replaceAll("\r", "");	// there are problems with
		printed = printed.replaceAll("\n", ""); // String.matches() and newlines
		System.setOut(stdout);
		System.out.print(printed);
		assertTrue(printed.matches(".*12345VERSION54321.*"));
		
		System.setIn(stdin);
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest2 SUT = new Quest2(_emptyScript);
		assertTrue(SUT.getID().equals("Quest2"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest2 SUT = new Quest2(_emptyScript);
		assertFalse(SUT.isCritical());
	}
}
