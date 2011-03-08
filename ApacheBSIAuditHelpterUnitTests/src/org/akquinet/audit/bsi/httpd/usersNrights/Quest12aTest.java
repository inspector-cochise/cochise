package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.akquinet.test.util.RethrowingThread;
import org.junit.Test;

public class Quest12aTest
{
	@Test
	public final void testNegativeTrivial() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				InputStream stdin = System.in;
				Quest12a SUT = new Quest12a("./testFiles/", "emptyScript.bat");
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
			fail("Seems like this test takes too long, maybe Quest12a unexpectedly asks for user input check that!");
		}

		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testNegativeRoot() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				InputStream stdin = System.in;
				Quest12a SUT = new Quest12a("./testFiles/", "emptyScript.bat");
				System.setIn(new ByteArrayInputStream("Yes\nroot\n".getBytes()));
				
				assertFalse(SUT.answer());
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest12a unexpectedly asks for user input check that!");
		}

		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testNegativeUserNotOk() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				InputStream stdin = System.in;
				Quest12a SUT = new Quest12a("./testFiles/", "failScript.bat");
				System.setIn(new ByteArrayInputStream("Yes\nexampleUser\n".getBytes()));
				
				assertFalse(SUT.answer());
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest12a unexpectedly asks for user input check that!");
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
				Quest12a SUT = new Quest12a("./testFiles/", "emptyScript.bat");
				System.setIn(new ByteArrayInputStream("Yes\nexampleUser\n".getBytes()));
				
				assertTrue(SUT.answer());
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest12a unexpectedly asks for user input check that!");
		}

		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testGetID()
	{
		Quest12a SUT = new Quest12a();
		assertTrue(SUT.getID().equals("Quest12a"));
	}

	@Test
	public final void testIsCritical()
	{
		Quest12a SUT = new Quest12a();
		assertFalse(SUT.isCritical());
	}
}
