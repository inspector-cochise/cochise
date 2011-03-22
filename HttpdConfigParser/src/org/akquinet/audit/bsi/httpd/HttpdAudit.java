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
			
			FormattedConsole console = FormattedConsole.getDefault();
			FormattedConsole.OutputLevel Q1 = FormattedConsole.OutputLevel.Q1;
			FormattedConsole.OutputLevel Q2 = FormattedConsole.OutputLevel.Q2;
			
			
			console.printSeperatorLine();
			console.println(Q1, "Hello, during this audit I will ask you a bunch of questions.");
			console.println(Q1, "Please rethink your answers twice before you give them to me.");
			console.println(Q1, "");

			console.println(Q1, "This audit will in most cases require you to make significant changes to your apache configuration.");
			console.println(Q1, "Another point is that the requirements of this audit conflict with most of the administration tools");
			console.println(Q1, "(like a2enmod, a2dismod, yast2, ...).");
			console.println(Q1, "");
			
			console.println(Q1, "First of all, let's start with some basic information about your system and your security requirements.");
			console.println(Q1, "Normally the apache the apache httpd executable is something like:");
			console.println(Q2, "/usr/sbin/apache2    (Debian/Ubuntu)");
			console.println(Q2, "/usr/sbin/httpd      (RedHat)");
			console.println(Q2, "/usr/sbin/httpd2     (SUSE)");
			File apacheExecutable = new File(console.askStringQuestion(Q1, "What is your apache executable? "));
			
			console.println(Q1, "");
			
			console.println(Q1, "The main configuration file for the apache web server normally is something like:");
			console.println(Q2, "/etc/apache2/apache2.conf    (Debian/Ubuntu)");
			console.println(Q2, "/etc/httpd/httpd.conf        (RedHat)");
			console.println(Q2, "/etc/apache2/httpd.conf      (SUSE)");
			File configFile = new File(console.askStringQuestion(Q1, "What is you apache main configuration file? "));
			ConfigFile conf = new ConfigFile(configFile);
			
			console.println(Q1, "");
			
			String answer = console.askStringQuestion(Q1, "Does your application require a high level of security? (Yes/No) ");
			boolean highSec = answer.equalsIgnoreCase("Yes");
			
			answer = console.askStringQuestion(Q1, "Does your application require a high level of privacy? (Yes/No) ");
			boolean highPriv = answer.equalsIgnoreCase("Yes");
			
			console.println(Q1, "");
			console.println(Q1, "Ok, then let's start.");
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
			tmpList.add(new Quest9(conf));
			tmpList.add(new Quest10(conf));
			tmpList.add(new Quest11(conf));
			tmpList.add(new Quest12(conf, apacheExecutable.getName()));
			
			InteractiveAsker asker = new InteractiveAsker(tmpList);
			
			boolean overallAnswer = asker.askQuestions();

			if(overallAnswer)
			{
				System.out.println("\n\nYour apache seems to be safe.");
			}
			else
			{
				System.out.println("\n\nSeems like there are some questions. Your apache seems to be unsafe.");
			}
		}
		catch (IOException e) { e.printStackTrace(); }
	}

}
