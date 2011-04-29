package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.akquinet.httpd.ConfigFile;
import org.akquinet.test.util.RethrowingThread;
import org.junit.Test;

public class Quest7Test
{
	private static final File _negativeTrivial = new File("./testFiles/Quest7/neg_triv.conf");
	private static final File _negativeConditional = new File("./testFiles/Quest7/neg_cond.conf");
	
	private static final File _correctListing = new File("./testFiles/Quest7/correct_listing.conf");
	
	private static final File _positiveTrivial = new File("./testFiles/Quest7/pos_triv.conf");
	private static final File _positiveOthers = new File("./testFiles/Quest7/pos_others.conf");
	
	private static final File _someConfig = _negativeTrivial;

	@Test
	public final void testNegativeTrivial() throws IOException
	{
		InputStream stdin = System.in;
		Quest7 SUT = new Quest7(new ConfigFile(_negativeTrivial));
		System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));

		assertFalse(SUT.answer());
		
		System.setIn(stdin);
	}
	
	@Test
	public final void testNegativeConditional() throws IOException
	{
		InputStream stdin = System.in;
		Quest7 SUT = new Quest7(new ConfigFile(_negativeConditional));
		System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
		
		assertFalse(SUT.answer());
		
		System.setIn(stdin);
	}

	
	@Test
	public final void testPositiveConditional() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				Quest7 SUT;
				try
				{
					SUT = new Quest7(new ConfigFile(_positiveTrivial));
					//we don't send any input cause we shouldn't be asked for anything
					assertTrue(SUT.answer());
				}
				catch (IOException e)
				{
					e.printStackTrace();
					fail("Caught IOException...");
				}
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest7 asks for user input check that!");
		}
		
		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testPositiveOthers() throws IOException
	{
		InputStream stdin = System.in;
		Quest7 SUT = new Quest7(new ConfigFile(_positiveOthers));
		System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
		
		assertTrue(SUT.answer());
		
		System.setIn(stdin);
	}
	
	@Test
	public final void testCorrectListing() throws IOException
	{
		InputStream stdin = System.in;
		PrintStream stdout = System.out;
		
		Quest7 SUT = new Quest7(new ConfigFile(_correctListing));
		System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		
		SUT.answer();
		
		String printed = out.toString();
		System.setOut(stdout);
		System.out.print(printed);
		
		String[] feedback = new String[4];
		int i = 0;
		for(String line : printed.split("(\r\n)|\n"))
		{
			if(line.matches("[ \t]*.*:.*:.*"))
			{
				feedback[i++] = line;
			}
		}
		
		assertEquals("all explicitly enabled options should be displayed", 4, feedback.length);
		
		assertTrue("option +bar should be displayed correctly", feedback[0].matches("[ \t]*.*:.*:[ \t]*Options[ \t]*\\+bar.*"));
		assertTrue("option +Indexes +Include should be displayed correctly", feedback[1].matches("[ \t]*.*:.*:[ \t]*Options[ \t]*\\+Indexes \\+Include.*"));
		assertTrue("option +iwas should be displayed correctly", feedback[2].matches("[ \t]*.*:.*:[ \t]*Options[ \t]*\\+iwas.*"));
		assertTrue("option +foo should be displayed correctly", feedback[3].matches("[ \t]*.*:.*:[ \t]*Options[ \t]*\\+foo.*"));
		
		System.setIn(stdin);
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest7 SUT = new Quest7(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest7"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest7 SUT = new Quest7(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
