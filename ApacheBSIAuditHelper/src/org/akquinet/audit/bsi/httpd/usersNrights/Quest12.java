package org.akquinet.audit.bsi.httpd.usersNrights;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

public class Quest12 implements YesNoQuestion
{
	private static final String _id = "Quest12";
	private ConfigFile _conf;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	private Quest12a _q12a;
	private Quest12b _q12b;
	
	public Quest12(ConfigFile conf, String apacheExecutable)
	{
		this(conf, "./", "quest12.sh", "getApacheStartingUser.sh", apacheExecutable);
	}
	
	public Quest12(ConfigFile conf, String commandPath, String command, String getUserNGroupCommand, String apacheExecutable)
	{
		_conf = conf;
		_q12a = new Quest12a(commandPath, command, getUserNGroupCommand, apacheExecutable);
		_q12b = new Quest12b(_conf, commandPath, command);
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.println("Ensuring that the apache web server is running with few permissions.");
		_uc.beginIndent();
			boolean ret = _q12b.answer();
		_uc.endIndent();
		if(!ret)
		{
			_uc.println("There is also another way to ensure few permissions:");
			_uc.beginIndent();
				ret = _q12a.answer();
			_uc.endIndent();
		}
		
		_uc.println("Back to " + _id);
		_uc.printAnswer(ret, ret ?
					"The apache web server is correctly running with few permissions."
					: "The apache web server is maybe running with too many permissions.");
		return ret;
	}

	@Override
	public boolean isCritical()
	{
		return false;
	}

	@Override
	public String getID()
	{
		return _id;
	}
}
