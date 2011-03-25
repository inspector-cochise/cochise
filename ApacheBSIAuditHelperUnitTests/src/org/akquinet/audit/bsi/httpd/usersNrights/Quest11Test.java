package org.akquinet.audit.bsi.httpd.usersNrights;

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

public class Quest11Test
{
	//a0 b1 means Quest12a = false and Quest12b = true...
	private static final File _a0 = new File("./testFiles/Quest11/a0.conf");
	private static final File _a1 = new File("./testFiles/Quest11/a1.conf");
	
	private static final File _someConfig = _a0;

	@Test
	public final void test_a0_b0() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				try
				{
					InputStream stdin = System.in;
					Quest11 SUT = new Quest11(new ConfigFile(_a0));
					System.setIn(new ByteArrayInputStream("No\n".getBytes()));
					
					assertFalse(SUT.answer());
					System.setIn(stdin);
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
			fail("Seems like this test takes too long, maybe Quest12 unexpectedly asks for user input check that!");
		}

		th.throwCaughtThrowable();
	}
	
	@Test
	public final void test_a0_b1() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				try
				{
					InputStream stdin = System.in;
					Quest11 SUT = new Quest11(new ConfigFile(_a0));
					System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
					
					assertTrue(SUT.answer());
					System.setIn(stdin);
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
			fail("Seems like this test takes too long, maybe Quest12 unexpectedly asks for user input. Check that!");
		}

		th.throwCaughtThrowable();
	}
	
	@Test
	public final void test_a1() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				try
				{
					//Quest11a should not be consulted, so we don't simulate user input
					Quest11 SUT = new Quest11(new ConfigFile(_a1));
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
			fail("Seems like this test takes too long, maybe Quest11 unexpectedly asks for user input. Check that!");
		}

		th.throwCaughtThrowable();
	}

	@Test
	public final void testGetID() throws IOException
	{
		Quest11 SUT = new Quest11(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest11"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest11 SUT = new Quest11(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
