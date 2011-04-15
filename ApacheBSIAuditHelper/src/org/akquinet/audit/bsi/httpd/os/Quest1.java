package org.akquinet.audit.bsi.httpd.os;

import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest1 implements YesNoQuestion
{
	private static final String _id = "Quest1";
	private boolean _highSecReq;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _commandPath;
	private String _command;

	public Quest1(boolean highSecurityRequired)
	{
		this(highSecurityRequired, "./", "quest1.sh");
	}
	
	public Quest1(boolean highSecurityRequired, String commandPath, String command)
	{
		_highSecReq = highSecurityRequired;
		_commandPath = commandPath;
		_command = command;
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		if(_highSecReq)
		{
			ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command);
			boolean ret = quest.answer();
			
			_uc.printAnswer(ret, ret ? "User root is correctly locked."
								: "Please lock user root (\"passwd -l\"). After that you can't directly log yourself in as root.");
			
			_uc.beginHidingParagraph("(more Information)");
			_uc.printParagraph("For a high level of security you should block access to the standard administrative accounts. " +
					"This adds security to your server because an attacker in many cases also needs to know the name of an administrative account.");
			_uc.printParagraph("On most Linux systems you can lock the root account by executing the following command (USE WITH CARE!)");
			_uc.printExample("passwd -l root");
			_uc.printParagraph("Please ensure that you first created another account with administrative rights. " +
					"Because you won't be able to login as root anymore after that. " +
					"(But you can still use sudo.) " +
					"If you ever need to unlock your root account you can do so by executing the following command.");
			_uc.printExample("passwd -u root");
			_uc.endHidingParagraph();
			
			return ret;
		}
		else
		{
			_uc.println("skipping question... (no high level of security requested))");
			return true;
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
