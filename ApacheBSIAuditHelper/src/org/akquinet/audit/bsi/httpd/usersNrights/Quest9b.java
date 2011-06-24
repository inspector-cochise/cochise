package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.util.ResourceBundle;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest9b implements YesNoQuestion
{
	private static final String _id = "Quest9b";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _serverRoot;
	private ResourceBundle _labels;

	public Quest9b(String serverRoot)
	{
		_serverRoot = serverRoot;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		File srvRt = new File(_serverRoot + File.separator + "htdocs");
		
		if(!srvRt.exists())
		{
			_uc.printAnswer(true, _labels.getString("S1") );
			return true;
		}
		else
		{
			if(!srvRt.isDirectory())
			{
				_uc.printAnswer(false,  _labels.getString("S2") );
				return false;
			}
			_uc.println( _labels.getString("L1") );
			boolean isDoc = _uc.askYesNoQuestion( _labels.getString("Q1") );
			boolean access = _uc.askYesNoQuestion( _labels.getString("Q2") );
			_uc.printAnswer(isDoc && access, isDoc && access ?
									_labels.getString("S3") : _labels.getString("S4") );
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
