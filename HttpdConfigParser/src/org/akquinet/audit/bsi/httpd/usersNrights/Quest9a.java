package org.akquinet.audit.bsi.httpd.usersNrights;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.YesNoQuestion;

public class Quest9a implements YesNoQuestion
{
	private static final String _id = "Quest9a";
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q2;

	public Quest9a(String _serverRoot)
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean answer()
	{
		// TODO Auto-generated method stub
		return false;
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
