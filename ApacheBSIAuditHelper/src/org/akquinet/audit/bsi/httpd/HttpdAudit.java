package org.akquinet.audit.bsi.httpd;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.InteractiveAsker;
import org.akquinet.audit.YesNoQuestion;
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
			String tmp;
			
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
			
			FormattedConsole console = FormattedConsole.getDefault();
			FormattedConsole.OutputLevel Q1 = FormattedConsole.OutputLevel.Q1;
			FormattedConsole.OutputLevel Q2 = FormattedConsole.OutputLevel.Q2;
			
			String id = "main";
			
			console.printSeperatorLine();
			console.println(Q1, id, "Hello, during this audit I will ask you a bunch of questions.");
			console.println(Q1, id, "Please rethink your answers twice before you give them to me.");
			console.println(Q1, id, "");

			console.println(Q1, id, "This audit will in most cases require you to make significant changes to your apache configuration.");
			console.println(Q1, id, "Another point is that the requirements of this audit conflict with most of the administration tools");
			console.println(Q1, id, "(like a2enmod, a2dismod, yast2, ...).");
			console.println(Q1, id, "");
			
			console.println(Q1, id, "First of all, let's start with some basic information about your system and your security requirements.");
			console.println(Q1, id, "Normally the apache the apache httpd executable is something like:");
			console.println(Q2, id, "/usr/sbin/apache2    (Debian/Ubuntu)");
			console.println(Q2, id, "/usr/sbin/httpd      (RedHat)");
			console.println(Q2, id, "/usr/sbin/httpd2     (SUSE)");
			tmp = console.askStringQuestion(Q1, id, "What is your apache executable? [" + apacheExec + "]");
			apacheExec = "".equals(tmp.trim()) ? apacheExec : tmp;
			File apacheExecutable = new File(apacheExec);
			
			console.println(Q1, id, "");
			
			console.println(Q1, id, "The main configuration file for the apache web server normally is something like:");
			console.println(Q2, id, "/etc/apache2/apache2.conf    (Debian/Ubuntu)");
			console.println(Q2, id, "/etc/httpd/httpd.conf        (RedHat)");
			console.println(Q2, id, "/etc/apache2/httpd.conf      (SUSE)");
			tmp = console.askStringQuestion(Q1, id, "What is you apache main configuration file? [" + apacheConf + "]");
			apacheConf = "".equals(tmp.trim()) ? apacheConf : tmp;
			File configFile = new File(apacheConf);
			ConfigFile conf = new ConfigFile(configFile);
			
			console.println(Q1, id, "");
			
			String answer = console.askStringQuestion(Q1, id, "Does your application require a high level of security? (Yes/No) ");
			boolean highSec = answer.equalsIgnoreCase("Yes");
			
			console.println(Q1, id, "");
			
			answer = console.askStringQuestion(Q1, id, "Does your application require a high level of privacy? (Yes/No) ");
			boolean highPriv = answer.equalsIgnoreCase("Yes");
			
			console.println(Q1, id, "");
			console.println(Q1, id, "Ok, then let's start.");
			console.printSeperatorLine();
			
			List<YesNoQuestion> tmpList = new LinkedList<YesNoQuestion>();

			tmpList.add(new Quest1(highSec));
			tmpList.add(new Quest2(apacheExecutable));
			tmpList.add(new Quest3(conf, apacheExecutable));
			tmpList.add(new Quest4(conf, apacheExecutable));
			tmpList.add(new Quest5(conf));
			tmpList.add(new Quest6(apacheExecutable));
			tmpList.add(new Quest7(conf));
			tmpList.add(new Quest8(configFile));
			tmpList.add(new Quest9(conf, apacheExecutable.getName()));
			tmpList.add(new Quest10(conf));
			tmpList.add(new Quest11(conf));
			tmpList.add(new Quest12(conf, apacheExecutable.getName()));
			
			InteractiveAsker asker = new InteractiveAsker(tmpList);
			
			boolean overallAnswer = asker.askQuestions();

			if(overallAnswer)
			{
				console.println(Q1, id, "");
				console.println(Q1, id, "Your apache seems to be safe.");
			}
			else
			{
				console.println(Q1, id, "");
				console.println(Q1, id, "Seems like there are some questions. Your apache seems to be unsafe.");
			}
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
