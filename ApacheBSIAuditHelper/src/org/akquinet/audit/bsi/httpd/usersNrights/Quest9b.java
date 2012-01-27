package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

@Interactive
public class Quest9b implements YesNoQuestion
{
	private static final long serialVersionUID = -1197600716075991886L;
	
	private static final String _id = "Quest9b";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _serverRoot;
	private transient ResourceBundle _labels;
	private long _htdocsLastModified;
	private boolean _lastUserAnswer;

	public Quest9b(String serverRoot)
	{
		_serverRoot = serverRoot;
		_htdocsLastModified = -1;
		_lastUserAnswer = false;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		File htdocs = new File(_serverRoot + File.separator + "htdocs");
		
		if(!htdocs.exists())
		{
			_uc.printAnswer(true, _labels.getString("S1") );
			return true;
		}
		else
		{
			if(!htdocs.isDirectory())
			{
				_uc.printAnswer(false,  _labels.getString("S2") );
				return false;
			}
			_uc.println( _labels.getString("L1") );
			
			boolean isDoc = true;
			boolean access = true;
			if(htdocs.lastModified() > _htdocsLastModified || !_lastUserAnswer)
			{
				_htdocsLastModified = htdocs.lastModified();
				isDoc = _uc.askYesNoQuestion( _labels.getString("Q1") );
				access = _uc.askYesNoQuestion( _labels.getString("Q2") );
				_lastUserAnswer = isDoc && access;
			}
			_uc.printAnswer(_lastUserAnswer, _lastUserAnswer ?
									_labels.getString("S3") : _labels.getString("S4") );
			return _lastUserAnswer;
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

	@Override
	public void initialize()
	{
		//nothing to do here
	}
	
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}
}
