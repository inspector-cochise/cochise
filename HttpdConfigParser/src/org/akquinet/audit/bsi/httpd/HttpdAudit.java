package org.akquinet.audit.bsi.httpd;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.akquinet.audit.InteractiveAsker;
import org.akquinet.audit.YesNoQuestion;
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
			ConfigFile conf = new ConfigFile(new File("D:/Immanuel/workspace/ApacheBSIAuditHelper/testFiles/example.conf"));
			List<YesNoQuestion> tmpList = new LinkedList<YesNoQuestion>();

			tmpList.add(new Quest11b(conf));
			
			InteractiveAsker asker = new InteractiveAsker(tmpList);
			
			asker.askQuestions();
		}
		catch (IOException e) { e.printStackTrace(); }
	}

}
