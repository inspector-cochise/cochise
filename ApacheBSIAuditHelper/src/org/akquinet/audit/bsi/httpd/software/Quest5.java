package org.akquinet.audit.bsi.httpd.software;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

public class Quest5 implements YesNoQuestion
{
	private static final String _id = "Quest5";
	private ConfigFile _conf;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	private Quest5a _q5a;
	private Quest5b _q5b;
	
	public Quest5(ConfigFile conf)
	{
		_conf = conf;
		_q5a = new Quest5a(_conf);
		_q5b = new Quest5b(_conf);
	}

	/**
	 * Looking for AllowOverride-directives. Checking whether there is no with parameters other than None and at least one
	 * in global context with parameter None.
	 */
	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.println("Evaluating whether your main configuration file is your only possible configuration file...");
		_uc.beginIndent();
		boolean ret = _q5a.answer() && _q5b.answer();
		
		_uc.endIndent();
		_uc.println("Back to " + _id);
		_uc.printAnswer(ret, ret ?
					"Your main configuration file is your only possible configuration file."
					: "Your main configuration file is not your only possible configuration file.");
		return ret;
	}

	@Override
	public boolean isCritical()
	{
		//TODO this may gets false if the parsers evaluates Include-directives
		return true;
	}

	@Override
	public String getID()
	{
		return _id;
	}
}
