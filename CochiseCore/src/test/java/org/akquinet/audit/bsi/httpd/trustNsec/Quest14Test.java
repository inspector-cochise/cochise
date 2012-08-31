/**
 * 
 */
package org.akquinet.audit.bsi.httpd.trustNsec;

import static org.junit.Assert.assertEquals;

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
public class Quest14Test
{
	private static final String testDirectory = System.getProperty("user.dir") + "/testFiles/Quest14/";
	
	private String configFilePath;
	private boolean expectedResult;
	private UserCommunicator devNullUC; 

	public Quest14Test(String configFilePath, UserCommunicator devNullUC, boolean expectedResult)
	{
		this.configFilePath = configFilePath;
		this.devNullUC = devNullUC;
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
				{ testDirectory + "pos_triv.conf",	new DevNullUserCommunicator(false, ""),		true },
				{ testDirectory + "pos_triv.conf",	new DevNullUserCommunicator(true, ""),		false },
				{ testDirectory + "neg_triv.conf",	new DevNullUserCommunicator(false, ""),		false },
				{ testDirectory + "neg0.conf",		new DevNullUserCommunicator(false, ""),		false },
				{ testDirectory + "neg0.conf",		new DevNullUserCommunicator(false, ""),		false },
				{ testDirectory + "neg1.conf",		new DevNullUserCommunicator(false, ""),		false },
				{ testDirectory + "neg2.conf",		new DevNullUserCommunicator(false, ""),		false },
				{ testDirectory + "neg3.conf",		new DevNullUserCommunicator(false, ""),		false },
				{ testDirectory + "neg4.conf",		new DevNullUserCommunicator(false, ""),		false }
				});
	}

	@Test
	public void test()
	{
		try
		{
			Quest14 SUT = new Quest14(new ConfigFile(configFilePath), devNullUC);
			
			assertEquals(expectedResult, SUT.answer());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
