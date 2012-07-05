package org.akquinet.audit.bsi.httpd.os;

import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.DevNullUserCommunicator;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.util.ResourceWatcher;

@Automated
public class Quest1 extends ResourceWatcher implements YesNoQuestion
{
	private static final long serialVersionUID = -1576174569449307817L;
	
	private static final String _id = "Quest1";
	private boolean _highSecReq;
	private transient UserCommunicator _uc;
	private String _commandPath;
	private String _command;
	private transient ResourceBundle _labels;
	private boolean _lastAnswer = false;

	private boolean _firstRun = true;

	public Quest1(PrologueData pd)
	{
		this(pd._highSec);
	}
	
	public Quest1(PrologueData pd, UserCommunicator uc)
	{
		this(pd._highSec, uc);
	}
	
	public Quest1(boolean highSecurityRequired)
	{
		this(highSecurityRequired, UserCommunicator.getDefault());
	}
	
	public Quest1(boolean highSecurityRequired, UserCommunicator uc)
	{
		this(highSecurityRequired, "./", "quest1.sh", uc);
	}
	
	public Quest1(boolean highSecurityRequired, String commandPath, String command)
	{
		this(highSecurityRequired, commandPath, command, UserCommunicator.getDefault());
	}
	
	public Quest1(boolean highSecurityRequired, String commandPath, String command, UserCommunicator uc)
	{
		_uc = uc;
		_highSecReq = highSecurityRequired;
		_commandPath = commandPath;
		_command = command;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_lastAnswer = answer(_uc);
		_firstRun = false;
		return _lastAnswer;
	}

	private boolean answer(UserCommunicator uc)
	{
		uc.printHeading3( _labels.getString("name") );
		uc.printParagraph( _labels.getString("Q0") );
		
		if(_highSecReq)
		{
			ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command);
			boolean ret = quest.answer();
			
			uc.printAnswer(ret, ret ? _labels.getString("S1_good")
								: _labels.getString("S1_bad"));
			
			uc.beginHidingParagraph( _labels.getString("S2") );
			uc.printParagraph( _labels.getString("P1") );
			uc.printParagraph( _labels.getString("P2") );
			uc.printExample( _labels.getString("S3") );
			uc.printParagraph( _labels.getString("P3"));
			uc.printExample( _labels.getString("S4") );
			uc.endHidingParagraph();
			
			return ret;
		}
		else
		{
			uc.println( _labels.getString("S5") );
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
		return 1;
	}

	@Override
	public int getNumber()
	{
		return 1;
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
		_uc = UserCommunicator.getDefault();
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public String getName()
	{
		return _labels.getString("name");
	}

	@Override
	public void setUserCommunicator(UserCommunicator uc)
	{
		_uc = uc;
	}

	@Override
	public String getResourceId()
	{
		return "quest." + _id;
	}

	@Override
	public boolean resourceChanged()
	{
		boolean answer = answer(new DevNullUserCommunicator());
		
		if(!_firstRun && answer != _lastAnswer)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
