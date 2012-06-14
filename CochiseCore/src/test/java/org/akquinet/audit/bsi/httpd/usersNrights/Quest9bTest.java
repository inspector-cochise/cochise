package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.akquinet.test.util.RethrowingThread;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest9bTest
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final String _srvRootNoHtdocs = _userDir + "/testFiles/Quest9b/srvRootNoHtdocs";
	private static final String _srvRootHtdocs = _userDir + "/testFiles/Quest9b/srvRootHtdocs";
	private static final String _commandPath = _userDir + "/testFiles/";
	private static final String _command = "emptyScript.bat";
	
	private static final String _someDir = _srvRootNoHtdocs;
	
	
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
	
	public final void htdocs(final String answer1, final String answer2, final boolean assertVal) throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				InputStream stdin = System.in;
				Quest9b SUT = new Quest9b(_srvRootHtdocs, _commandPath, _command);
				SUT.initialize();
				System.setIn(new ByteArrayInputStream((answer1 + "\n" + answer2 + "\n").getBytes()));

				assertEquals(SUT.answer(), assertVal);
				System.setIn(stdin);
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest9b unexpectedly asks for user input check that!");
		}
		
		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testHtdocsNoNo() throws Throwable
	{
		htdocs("No", "No", false);
	}
	
	@Test
	public final void testHtdocsYesNo() throws Throwable
	{
		htdocs("Yes", "No", false);
	}
	
	@Test
	public final void testHtdocsNoYes() throws Throwable
	{
		htdocs("No", "Yes", false);
	}
	
	@Test
	public final void testHtdocsYesYes() throws Throwable
	{
		htdocs("Yes", "Yes", true);
	}
	
	@Test
	public final void testPositiveNoHtdocs() throws Throwable
	{
		RethrowingThread th = new RethrowingThread()
		{
			@Override
			public void run()
			{
				Quest9b SUT = new Quest9b(_srvRootNoHtdocs, _commandPath, _command);
				SUT.initialize();
				assertTrue(SUT.answer());
			}
		};
		
		th.start();
		th.join(2000);
		
		if(th.isAlive())
		{
			th.interrupt();
			fail("Seems like this test takes too long, maybe Quest9b unexpectedly asks for user input check that!");
		}
		
		th.throwCaughtThrowable();
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest9b SUT = new Quest9b(_someDir, _commandPath, _command);
		assertTrue(SUT.getID().equals("Quest9b"));
	}
	
	@Test
	public final void testIsCritical() throws IOException
	{
		Quest9b SUT = new Quest9b(_someDir, _commandPath, _command);
		assertFalse(SUT.isCritical());
	}
}
