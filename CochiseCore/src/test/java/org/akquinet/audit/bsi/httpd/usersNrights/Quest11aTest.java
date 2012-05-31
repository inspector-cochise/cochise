package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.akquinet.test.util.RethrowingThread;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest11aTest
{
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
	public final void testNegativeTrivial() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				InputStream stdin = System.in;
				Quest11a SUT = new Quest11a();
				try
				{
					SUT.initialize();
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
				System.setIn(new ByteArrayInputStream("No\n".getBytes()));
				
				assertFalse(SUT.answer());
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest11a unexpectedly asks for user input check that!");
		}

		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testPositiveTrivial() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				InputStream stdin = System.in;
				Quest11a SUT = new Quest11a();
				try
				{
					SUT.initialize();
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
				System.setIn(new ByteArrayInputStream("Yes\n".getBytes()));
				
				assertTrue(SUT.answer());
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest11a unexpectedly asks for user input check that!");
		}

		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testGetID()
	{
		Quest11a SUT = new Quest11a();
		assertTrue(SUT.getID().equals("Quest11a"));
	}

	@Test
	public final void testIsCritical()
	{
		Quest11a SUT = new Quest11a();
		assertFalse(SUT.isCritical());
	}
}
