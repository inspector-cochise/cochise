package org.akquinet.audit.bsi.httpd.usersNrights;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;

public class Quest11a implements YesNoQuestion
{
	private static final String _id = "Quest11a";
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q2;

	public Quest11a()
	{
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, _id, "----" + _id + "----");
		boolean ret = _console.askYesNoQuestion(_level, _id, "Have you properly set up a chroot environment for the apache httpd server which will block access outside of the servers root directory?");
		_console.printAnswer(_level, _id, ret, ret ? 
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
