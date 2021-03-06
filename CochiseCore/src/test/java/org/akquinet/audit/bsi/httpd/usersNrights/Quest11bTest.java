package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.junit.BeforeClass;
import org.junit.Test;

public class Quest11bTest
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final String _negContained1 = _userDir + "/testFiles/Quest11b/neg_contained1.conf";
	private static final String _negContained2 = _userDir + "/testFiles/Quest11b/neg_contained2.conf";
	private static final String _negContained3 = _userDir + "/testFiles/Quest11b/neg_contained3.conf";
	private static final String _negFalseOrder = _userDir + "/testFiles/Quest11b/neg_falseOrder.conf";
	private static final String _negMalformed1 = _userDir + "/testFiles/Quest11b/neg_malf1.conf";
	private static final String _negMalformed2 = _userDir + "/testFiles/Quest11b/neg_malf2.conf";
	private static final String _negMultipleOrder = _userDir + "/testFiles/Quest11b/neg_multipleOrder.conf";
	private static final String _negSecureNet = _userDir + "/testFiles/Quest11b/neg_secureNet.conf";
	private static final String _negTooSpecial = _userDir + "/testFiles/Quest11b/neg_tooSpecial.conf";
	private static final String _negTrivial = _userDir + "/testFiles/Quest11b/neg_triv.conf";
	
	private static final String _posTrivial = _userDir + "/testFiles/Quest11b/pos_triv.conf";
	private static final String _posWithExceptions = _userDir + "/testFiles/Quest11b/pos_withExceptions.conf";
	
	private static final String _someConfig = _negTrivial;
	

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
	public final void testNegativeContained1() throws Exception
	{
		assertFalse(getSUT(_negContained1).answer());
	}

	@Test
	public final void testNegativeContained2() throws Exception
	{
		assertFalse(getSUT(_negContained2).answer());
	}

	@Test
	public final void testNegativeContained3() throws Exception
	{
		assertFalse(getSUT(_negContained3).answer());
	}
	
	@Test
	public final void testNegativeFalseOrder() throws Exception
	{
		assertFalse(getSUT(_negFalseOrder).answer());
	}
	
	@Test
	public final void testNegativeMalformed1() throws Exception
	{
		assertFalse(getSUT(_negMalformed1).answer());
	}

	@Test
	public final void testNegativeMalformed2() throws Exception
	{
		assertFalse(getSUT(_negMalformed2).answer());
	}

	@Test
	public final void testNegativeMultipleOrder() throws Exception
	{
		assertFalse(getSUT(_negMultipleOrder).answer());
	}

	@Test
	public final void testNegativeSecureNet() throws Exception
	{
		assertFalse(getSUT(_negSecureNet).answer());
	}

	@Test
	public final void testNegativeTooSpecial() throws Exception
	{
		assertFalse(getSUT(_negTooSpecial).answer());
	}
	
	@Test
	public final void testNegativeTrivial() throws Exception
	{
		assertFalse(getSUT(_negTrivial).answer());
	}

	@Test
	public final void testPositiveTrivial() throws Exception
	{
		assertTrue(getSUT(_posTrivial).answer());
	}

	@Test
	public final void testPositiveWithExceptions() throws Exception
	{
		assertTrue(getSUT(_posWithExceptions).answer());
	}
	
	@Test
	public final void testGetID() throws Exception
	{
		Quest11b SUT = getSUT(_someConfig);
		assertTrue(SUT.getID().equals("Quest11b"));
	}

	@Test
	public final void testIsCritical() throws Exception
	{
		Quest11b SUT = getSUT(_someConfig);
		assertFalse(SUT.isCritical());
	}
	
	private final Quest11b getSUT(String confFile) throws Exception
	{
		Quest11b SUT = new Quest11b(new ConfigFile(confFile));
		SUT.initialize();
		return SUT;
	}
}
