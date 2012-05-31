package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.akquinet.test.util.RethrowingThread;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest11Test
{
	private static final String _userDir = System.getProperty("user.dir");
	
	//a0 b1 means Quest12a = false and Quest12b = true...
	private static final File _a0 = new File(_userDir + "/testFiles/Quest11/a0.conf");
	private static final File _a1 = new File(_userDir + "/testFiles/Quest11/a1.conf");
	
	private static final File _someConfig = _a0;

	@BeforeClass
	public static final void setUp()
	{
		if(UserCommunicator.getDefault() == null)
		{
			try
			{
				UserCommunicator.setDefault(new ConsoleUserCommunicator());
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
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
					SUT.initialize();
					System.setIn(new ByteArrayInputStream("No\n".getBytes()));
					
					assertFalse(SUT.answer());
					System.setIn(stdin);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
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
					SUT.initialize();
					System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
					
					assertTrue(SUT.answer());
					System.setIn(stdin);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
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
					SUT.initialize();
					assertTrue(SUT.answer());
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
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
	public final void testGetID() throws IOException, ParserException
	{
		Quest11 SUT = new Quest11(new ConfigFile(_someConfig));
		assertTrue(SUT.getID().equals("Quest11"));
	}

	@Test
	public final void testIsCritical() throws IOException, ParserException
	{
		Quest11 SUT = new Quest11(new ConfigFile(_someConfig));
		assertFalse(SUT.isCritical());
	}
}
