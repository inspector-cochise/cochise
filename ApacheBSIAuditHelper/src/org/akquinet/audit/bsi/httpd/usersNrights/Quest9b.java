package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest9b implements YesNoQuestion
{
	private static final String _id = "Quest9b";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _serverRoot;

	public Quest9b(String serverRoot)
	{
		_serverRoot = serverRoot;
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		File srvRt = new File(_serverRoot + File.separator + "htdocs");
		
		if(!srvRt.exists())
		{
			_uc.printAnswer(true, "There is no htdocs directory in your ServerRoot. (This is Ok.)");
			return true;
		}
		else
		{
			if(!srvRt.isDirectory())
			{
				_uc.printAnswer(false, "In your ServerRoot there is a file htdocs this should be a directory or nonexistent. Please move that file and run me again");
				return false;
			}
			_uc.println("I found a directory named htdocs in your ServerRoot. (I suggest to move this directory out of the ServerRoot.)");
			boolean isDoc = _uc.askYesNoQuestion("Does this directory only contain the apache httpd documentation.");
			boolean access = _uc.askYesNoQuestion("Do only users who explicitly are supposed to access this documentation have (unix-file-)permissions to access that directory?");
			_uc.printAnswer(isDoc && access, isDoc && access ? "Your htdocs directory seems to be ok." : "There are permission or content problems whith your htdocs directory. I suggest to move this directory out of ServerRoot to resolve these problems easier.");
			return isDoc && access;
		}
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
