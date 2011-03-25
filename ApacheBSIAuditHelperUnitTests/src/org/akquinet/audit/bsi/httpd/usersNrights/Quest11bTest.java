package org.akquinet.audit.bsi.httpd.usersNrights;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest11bTest
{
	private static final String _negContained1 = "./testFiles/Quest11b/neg_contained1.conf";
	private static final String _negContained2 = "./testFiles/Quest11b/neg_contained2.conf";
	private static final String _negContained3 = "./testFiles/Quest11b/neg_contained3.conf";
	private static final String _negFalseOrder = "./testFiles/Quest11b/neg_falseOrder.conf";
	private static final String _negMalformed1 = "./testFiles/Quest11b/neg_malf1.conf";
	private static final String _negMalformed2 = "./testFiles/Quest11b/neg_malf2.conf";
	private static final String _negMultipleOrder = "./testFiles/Quest11b/neg_multipleOrder.conf";
	private static final String _negSecureNet = "./testFiles/Quest11b/neg_secureNet.conf";
	private static final String _negTooSpecial = "./testFiles/Quest11b/neg_tooSpecial.conf";
	private static final String _negTrivial = "./testFiles/Quest11b/neg_triv.conf";
	
	private static final String _posTrivial = "./testFiles/Quest11b/pos_triv.conf";
	private static final String _posWithExceptions = "./testFiles/Quest11b/pos_withExceptions.conf";
	
	private static final String _someConfig = _negTrivial;
	

	@Test
	public final void testNegativeContained1() throws IOException
	{
		assertFalse(getSUT(_negContained1).answer());
	}

	@Test
	public final void testNegativeContained2() throws IOException
	{
		assertFalse(getSUT(_negContained2).answer());
	}

	@Test
	public final void testNegativeContained3() throws IOException
	{
		assertFalse(getSUT(_negContained3).answer());
	}
	
	@Test
	public final void testNegativeFalseOrder() throws IOException
	{
		assertFalse(getSUT(_negFalseOrder).answer());
	}
	
	@Test
	public final void testNegativeMalformed1() throws IOException
	{
		assertFalse(getSUT(_negMalformed1).answer());
	}

	@Test
	public final void testNegativeMalformed2() throws IOException
	{
		assertFalse(getSUT(_negMalformed2).answer());
	}

	@Test
	public final void testNegativeMultipleOrder() throws IOException
	{
		assertFalse(getSUT(_negMultipleOrder).answer());
	}

	@Test
	public final void testNegativeSecureNet() throws IOException
	{
		assertFalse(getSUT(_negSecureNet).answer());
	}

	@Test
	public final void testNegativeTooSpecial() throws IOException
	{
		assertFalse(getSUT(_negTooSpecial).answer());
	}
	
	@Test
	public final void testNegativeTrivial() throws IOException
	{
		assertFalse(getSUT(_negTrivial).answer());
	}

	@Test
	public final void testPositiveTrivial() throws IOException
	{
		assertTrue(getSUT(_posTrivial).answer());
	}

	@Test
	public final void testPositiveWithExceptions() throws IOException
	{
		assertTrue(getSUT(_posWithExceptions).answer());
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest11b SUT = getSUT(_someConfig);
		assertTrue(SUT.getID().equals("Quest11b"));
	}

	@Test
	public final void testIsCritical() throws IOException
	{
		Quest11b SUT = getSUT(_someConfig);
		assertFalse(SUT.isCritical());
	}
	
	private final Quest11b getSUT(String confFile) throws IOException
	{
		return new Quest11b(new ConfigFile(confFile));
	}
}
