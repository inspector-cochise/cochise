package org.akquinet.audit.bsi.httpd.os;

import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.ShellAnsweredQuestion;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

@Automated
public class Quest1 implements YesNoQuestion
{
	private static final long serialVersionUID = -1156858003091501310L;
	
	private static final String _id = "Quest1";
	private boolean _highSecReq;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private String _commandPath;
	private String _command;
	private transient ResourceBundle _labels;

	public Quest1(boolean highSecurityRequired)
	{
		this(highSecurityRequired, "./", "quest1.sh");
	}
	
	public Quest1(boolean highSecurityRequired, String commandPath, String command)
	{
		_highSecReq = highSecurityRequired;
		_commandPath = commandPath;
		_command = command;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		if(_highSecReq)
		{
			ShellAnsweredQuestion quest = new ShellAnsweredQuestion(_commandPath + _command);
			boolean ret = quest.answer();
			
			_uc.printAnswer(ret, ret ? _labels.getString("S1_good")
								: _labels.getString("S1_bad"));
			
			_uc.beginHidingParagraph( _labels.getString("S2") );
			_uc.printParagraph( _labels.getString("P1") );
			_uc.printParagraph( _labels.getString("P2") );
			_uc.printExample( _labels.getString("S3") );
			_uc.printParagraph( _labels.getString("P3"));
			_uc.printExample( _labels.getString("S4") );
			_uc.endHidingParagraph();
			
			return ret;
		}
		else
		{
			_uc.println( _labels.getString("S5") );
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
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public String getName()
	{
		return _labels.getString("name");
	}
}
