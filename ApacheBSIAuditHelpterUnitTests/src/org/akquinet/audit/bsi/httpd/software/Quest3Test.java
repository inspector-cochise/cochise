package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.io.IOException;
import static org.junit.Assert.*;
import org.akquinet.httpd.ConfigFile;
import org.junit.Test;

public class Quest3Test
{
	static final File _senselessExecutable = new File("C:\\autoexec.bat");
	
	static final File _negativeTrivial = new File("./testFiles/Quest3/neg_triv.conf"); 
	static final File _negativeSimilar = new File("./testFiles/Quest3/neg_sim.conf"); 
	static final File _negativeMalformed = new File("./testFiles/Quest3/neg_malf.conf"); 
	static final File _negativeConditionally = new File("./testFiles/Quest3/neg_cond.conf"); 
	
	static final File _positiveTrivial = new File("./testFiles/Quest3/pos_triv.conf"); 
	static final File _positiveMalformed = new File("./testFiles/Quest3/pos_malf.conf"); 
	
	static final File _someConfig = new File("./testFiles/Quest3/pos_triv.conf"); 

	@Test
	public final void testNegativeTrivial() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_negativeTrivial), _senselessExecutable);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeSimilar() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_negativeSimilar), _senselessExecutable);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeMalformed() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_negativeMalformed), _senselessExecutable);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testNegativeConditionally() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_negativeConditionally), _senselessExecutable);
		assertFalse(SUT.answer());
	}
	
	@Test
	public final void testPositiveTrivial() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_positiveTrivial), _senselessExecutable);
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testPositiveMalformed() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_positiveMalformed), _senselessExecutable);
		assertTrue(SUT.answer());
	}
	
	@Test
	public final void testGetID() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_someConfig), _senselessExecutable);
		assertTrue(SUT.getID().equals("Quest3"));
	}
	
	@Test
	public final void testIsCritical() throws IOException
	{
		Quest3 SUT = new Quest3(new ConfigFile(_someConfig), _senselessExecutable);
		assertFalse(SUT.isCritical());
	}
}
