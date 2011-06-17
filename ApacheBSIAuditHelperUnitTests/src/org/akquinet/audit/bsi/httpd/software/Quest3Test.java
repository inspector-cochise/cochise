package org.akquinet.audit.bsi.httpd.software;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest3Test
{
	private static final String _userDir = System.getProperty("user.dir");
	
	private static final File _emptyExec = new File(_userDir + "/testFiles/emptyScript.bat");
	
	private static final File _negativeTrivialExec = new File(_userDir + "/testFiles/Quest3/neg_triv.bat"); 
	private static final File _negativeSimilarExec = new File(_userDir + "/testFiles/Quest3/neg_sim.bat"); 
	private static final File _negativeMalformedExec = new File(_userDir + "/testFiles/Quest3/neg_malf.bat"); 
	
	private static final File _positiveTrivialExec = new File(_userDir + "/testFiles/Quest3/pos_triv.bat"); 
	private static final File _positiveMalformedExec = new File(_userDir + "/testFiles/Quest3/pos_malf.bat");
	
	
	private static final File _negativeTrivial = new File(_userDir + "/testFiles/Quest3/neg_triv.conf"); 
	private static final File _negativeSimilar = new File(_userDir + "/testFiles/Quest3/neg_sim.conf"); 
	private static final File _negativeMalformed = new File(_userDir + "/testFiles/Quest3/neg_malf.conf"); 
	private static final File _negativeConditionally = new File(_userDir + "/testFiles/Quest3/neg_cond.conf"); 
	
	private static final File _positiveTrivial = new File(_userDir + "/testFiles/Quest3/pos_triv.conf"); 
	private static final File _positiveMalformed = new File(_userDir + "/testFiles/Quest3/pos_malf.conf"); 
	
	private static final File _emptyConfig = new File(_userDir + "/testFiles/Quest3/empty.conf"); 

	@Test
	public final void testNegativeTrivial_File() throws IOException
	{
		System.out.println((new File(_userDir)).getCanonicalPath());
		Quest3 SUT = new Quest3(new ConfigFile(_negativeTrivial), _emptyExec);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeSimilar_File() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_negativeSimilar), _emptyExec);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeMalformed_File() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_negativeMalformed), _emptyExec);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeConditionally_File() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_negativeConditionally), _emptyExec);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial_File() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_positiveTrivial), _emptyExec);
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testPositiveMalformed_File() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_positiveMalformed), _emptyExec);
		assertTrue(SUT.answer());
	}
	
	//-------------------------------------
	//
	//-------------------------------------
	
	
	@Test
	public final void testNegativeTrivial_Exec() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_emptyConfig), _negativeTrivialExec);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeSimilar_Exec() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_emptyConfig), _negativeSimilarExec);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeMalformed_Exec() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_emptyConfig), _negativeMalformedExec);
		assertFalse(SUT.answer());
	}

	@Test
	public final void testPositiveTrivial_Exec() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_emptyConfig), _positiveTrivialExec);
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testPositiveMalformed_Exec() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_emptyConfig), _positiveMalformedExec);
		assertTrue(SUT.answer());
	}
	
	
	
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_emptyConfig), _emptyExec);
		assertTrue(SUT.getID().equals("Quest3"));
	}
	
	@Test
	public final void testIsCritical() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_emptyConfig), _emptyExec);
		assertFalse(SUT.isCritical());
	}
}
