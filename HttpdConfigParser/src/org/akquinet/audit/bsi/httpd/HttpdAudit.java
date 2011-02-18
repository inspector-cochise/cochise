package org.akquinet.audit.bsi.httpd;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
		//TODO all
		//for now this is just for debugging
		try
		{
			File configFile = null;
			ConfigFile conf = null;
			File apacheExecutable = null;
			boolean highSec = true;	//TODO initialize me in some way
			switch(args.length)
			{
			case 2:
				configFile = new File(args[0]);
				conf = new ConfigFile(configFile);
				apacheExecutable = new File(args[1]);
				break;
			default:
				System.err.println("parameters: apacheConfigFile apacheExecutable");
				return;
			}
			
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
			
			InteractiveAsker asker = new InteractiveAsker(tmpList);
			
			asker.askQuestions();
		}
		catch (IOException e) { e.printStackTrace(); }
	}

}
