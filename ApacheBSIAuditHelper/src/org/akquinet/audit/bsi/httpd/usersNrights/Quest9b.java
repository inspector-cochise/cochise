package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.util.Locale;
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
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
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

}
