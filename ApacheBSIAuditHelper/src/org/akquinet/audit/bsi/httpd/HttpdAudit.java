package org.akquinet.audit.bsi.httpd;

import java.io.File;
import java.io.IOException;

import org.akquinet.audit.bsi.httpd.os.Quest1;
import org.akquinet.audit.bsi.httpd.software.Quest2;
import org.akquinet.audit.bsi.httpd.software.Quest3;
import org.akquinet.audit.bsi.httpd.software.Quest4;
import org.akquinet.audit.bsi.httpd.software.Quest5;
import org.akquinet.audit.bsi.httpd.software.Quest6;
import org.akquinet.audit.bsi.httpd.software.Quest7;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest10;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest11;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest12;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest8;
import org.akquinet.audit.bsi.httpd.usersNrights.Quest9;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

public class HttpdAudit
{
	enum OperatingSystem
	{
		Ubuntu,
		SUSE,
		RedHat,
		UNKNOWN
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			if(!"root".equals(System.getenv("USER")))
			{
				System.out.println("This application has to be run as root!");
				System.exit(1);
			}
			
			String apacheExec;
			String apacheConf;
			String tmp = "";
			
			OperatingSystem os = getOperatingSystem();
			switch(os)
			{
			case Ubuntu:
				apacheExec = "/usr/sbin/apache2";
				apacheConf = "/etc/apache2/apache2.conf";
				break;
			case SUSE:
				apacheExec = "/usr/sbin/httpd2";
				apacheConf = "/etc/apache2/httpd.conf";
				break;
			case RedHat:
				apacheExec = "/usr/sbin/httpd";
				apacheConf = "/etc/httpd/httpd.conf";
				break;
			case UNKNOWN:
			default:
				apacheExec = "/usr/sbin/httpd";
				apacheConf = "/etc/httpd/httpd.conf";
				break;
			}
			
			try
			{
				File htmlReport = new File("./htmlReport.htm");
				System.out.println("I will save a detailed and more readable report of this audit in " + htmlReport.getCanonicalPath() + " .");
				UserCommunicator.setDefault(new UserCommunicator(htmlReport));
			}
			catch (Exception e) //shouldn't happen
			{
				e.printStackTrace();
			}
			
			UserCommunicator uc = UserCommunicator.getDefault();
			
			uc.printHeading1("Introduction");
			
			uc.printParagraph("Hello, during this audit I will ask you a bunch of questions. " +
					"Please rethink your answers twice before you give them to me.");

			uc.printParagraph("This audit will in most cases require you to make significant changes to your apache configuration. " +
					"This audit will in most cases require you to make significant changes to your apache configuration. " +
					"Another point is that the requirements of this audit conflict with most of the administration tools " +
					"(like a2enmod, a2dismod, yast2, ...).");
			
			uc.println("First of all, let's start with some basic information about your system and your security requirements.");
			uc.println("Normally the apache the apache httpd executable is something like:");
			uc.printExample("/usr/sbin/apache2    (Debian/Ubuntu)\n" +
					"/usr/sbin/httpd      (RedHat)\n" +
					"/usr/sbin/httpd2     (SUSE)");
			
			tmp = uc.askStringQuestion("What is your apache executable?", apacheExec);
			apacheExec = "".equals(tmp.trim()) ? apacheExec : tmp;
			File apacheExecutable = new File(apacheExec);

			while(! apacheExecutable.exists() )
			{
				tmp = uc.askStringQuestion(tmp + " doesn't exist. So what is your apache executable? ");
				apacheExecutable = new File(tmp.trim());
			}
			
			uc.println("");
			
			uc.println("The main configuration file for the apache web server normally is something like:");
			uc.printExample("/etc/apache2/apache2.conf    (Debian/Ubuntu)\n" +
					"/etc/httpd/httpd.conf        (RedHat)\n" +
					"/etc/apache2/httpd.conf      (SUSE)");
			tmp = uc.askStringQuestion("What is you apache main configuration file?", apacheConf);
			apacheConf = "".equals(tmp.trim()) ? apacheConf : tmp;
			File configFile = new File(apacheConf);
			
			while(! configFile.exists() )
			{
				tmp = uc.askStringQuestion(tmp + " doesn't exist. So what is you apache main configuration file? ");
				configFile = new File(tmp.trim());
			}
			
			ConfigFile conf = new ConfigFile(configFile);
			
			uc.println("");
			
			boolean highSec = uc.askYesNoQuestion("Does your application require a high level of security?", true);
			
			boolean highPriv = uc.askYesNoQuestion("Does your application require a high level of privacy?", true);
			
			uc.println("");
			uc.printParagraph("Ok, then let's start.");
			uc.printHeading1("The actual audit");
			
			boolean overallAnswer = true;
			

			uc.printHeading2("Section Operating System:");
			overallAnswer &= (new Quest1(highSec)).answer();
			
			uc.printHeading2("Section Software:");
			overallAnswer &= (new Quest2(apacheExecutable)).answer();
			overallAnswer &= (new Quest3(conf, apacheExecutable)).answer();
			overallAnswer &= (new Quest4(conf, apacheExecutable)).answer();
			overallAnswer &= (new Quest5(conf)).answer();
			overallAnswer &= (new Quest6(apacheExecutable)).answer();
			overallAnswer &= (new Quest7(conf)).answer();
			
			uc.printHeading2("Section Management of Users and Permissions:");
			overallAnswer &= (new Quest8(configFile)).answer();
			overallAnswer &= (new Quest9(conf, apacheExecutable.getName())).answer();
			overallAnswer &= (new Quest10(conf)).answer();
			overallAnswer &= (new Quest11(conf)).answer();
			overallAnswer &= (new Quest12(conf, apacheExecutable.getName())).answer();
			
			if(overallAnswer)
			{
				uc.printHeading1("Conclusion");
				uc.printParagraph("Your apache seems to be safe.");
			}
			else
			{
				uc.printHeading1("Conclusion");
				uc.printParagraph("Seems like there are some questions answered with no. Your apache seems to be unsafe.");
			}
			
			uc.finishCommunication();
		}
		catch (IOException e) { e.printStackTrace(); }
	}

	private static OperatingSystem getOperatingSystem()
	{
		try
		{
			Process p = (new ProcessBuilder("./distro.sh")).start();
			int exitVal = p.waitFor();
			
			switch(exitVal)
			{
			case 10:
				return OperatingSystem.Ubuntu;
			case 20:
				return OperatingSystem.SUSE;
			case 30:
				return OperatingSystem.RedHat;
			case 0:
			default:
				return OperatingSystem.UNKNOWN;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return OperatingSystem.UNKNOWN;
		}
	}

}
