package org.akquinet.audit.bsi.httpd.usersNrights;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest11a implements YesNoQuestion
{
	private static final String _id = "Quest11a";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();

	public Quest11a()
	{
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		boolean ret = _uc.askYesNoQuestion("Have you properly set up a chroot environment for the apache httpd server which will block access outside of the servers root directory?");
		_uc.printAnswer(ret, ret ? 
						"Ok this should block access to files outside of the servers root directory."
						: "No chroot - it may be possible to access files outside of the servers root directory if not sealed otherwise.");
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
