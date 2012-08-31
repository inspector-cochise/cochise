/**
 * 
 */
package org.akquinet.audit.bsi.httpd.trustNsec;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.akquinet.audit.ui.DevNullUserCommunicator;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.test.util.ConsoleUserCommunicator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author immanuel
 * 
 */
@RunWith(value = Parameterized.class)
public class Quest13Test
{
	private static final String testDirectory = System.getProperty("user.dir") + "/testFiles/Quest13/";
	private static final UserCommunicator devNullUC = new DevNullUserCommunicator(); 
	
	private String configFilePath;
	private String apacheExecutablePath;
	private boolean expectedResult;

	public Quest13Test(String configFilePath, String apacheExecutablePath, boolean expectedResult)
	{
		this.configFilePath = configFilePath;
		this.apacheExecutablePath = apacheExecutablePath;
		this.expectedResult = expectedResult;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		if (UserCommunicator.getDefault() == null)
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

	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] {
				{ testDirectory + "pos_triv.conf",					testDirectory + "pos_triv.bat",		true },
				{ testDirectory + "pos_triv.conf",					testDirectory + "neg_triv.bat",		true },
				{ testDirectory + "neg_notloaded.conf",				testDirectory + "pos_triv.bat",		true },
				{ testDirectory + "neg_notloaded.conf",				testDirectory + "neg_triv.bat",		false},
				{ testDirectory + "neg0.conf",						testDirectory + "pos_triv.bat",		false },
				{ testDirectory + "neg0.conf",						testDirectory + "neg_triv.bat",		false },
				{ testDirectory + "neg1.conf",						testDirectory + "pos_triv.bat",		false },
				{ testDirectory + "neg2.conf",						testDirectory + "pos_triv.bat",		false },
				{ testDirectory + "neg3.conf",						testDirectory + "pos_triv.bat",		false },
				{ testDirectory + "neg4.conf",						testDirectory + "pos_triv.bat",		false }
				});
	}

	@Test
	public void test()
	{
		try
		{
			Quest13 SUT = new Quest13(new ConfigFile(configFilePath), new File(apacheExecutablePath), devNullUC);
			
			assertEquals(expectedResult, SUT.answer());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
