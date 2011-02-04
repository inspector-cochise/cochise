package org.akquinet.audit.bsi.httpd;

import java.text.Normalizer.Form;

import org.akquinet.audit.FormattedConsole;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;

public class Quest1 implements YesNoQuestion
{
	private static final String _id = "Quest1";
	private boolean _highSecReq;
	private static final FormattedConsole _console = FormattedConsole.getDefault();
	private static final FormattedConsole.OutputLevel _level = FormattedConsole.OutputLevel.Q1;
	private static final String _command = "./quest1.sh";

	public Quest1(boolean highSecurityRequired)
	{
		_highSecReq = highSecurityRequired;
	}

	@Override
	public boolean answer()
	{
		_console.println(FormattedConsole.OutputLevel.HEADING, "----" + _id + "----");
		if(_highSecReq)
		{
			ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_command);
			boolean ret = quest.answer();
			
			_console.printAnswer(_level, ret, ret ? "User root is correctly locked."
								: "Please lock user root (\"passwd -l\"). After that you can't log yourself in directly as root.");
			
			return ret;
		}
		else
		{
			_console.printAnswer(_level, null, "skipping question... (no high level of security requested))");
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
