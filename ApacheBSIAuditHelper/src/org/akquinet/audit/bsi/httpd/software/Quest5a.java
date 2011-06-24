package org.akquinet.audit.bsi.httpd.software;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest5a implements YesNoQuestion
{
	private static final String _id = "Quest5a";
	private ConfigFile _conf;
	private ResourceBundle _labels;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	public Quest5a(ConfigFile conf)
	{
		_conf = conf;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	/**
	 * checks whether there are any Include-directives in the config file
	 */
	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		try
		{
			_conf.reparse();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		List<Directive> incList = _conf.getAllDirectivesIgnoreCase("Include");
		
		if(!incList.isEmpty())
		{
			_uc.printAnswer(false, _labels.getString("S1_bad") );
			for (Directive directive : incList)
			{
				_uc.println( directive.getContainingFile() + ":" + directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
			}
			return false;
		}
		else
		{
			_uc.printAnswer(true, _labels.getString("S1_good") );
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

	@Override
	public int getBlockNumber()
	{
		return 2147483647;
	}

	@Override
	public int getNumber()
	{
		return 2147483647;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}
}
