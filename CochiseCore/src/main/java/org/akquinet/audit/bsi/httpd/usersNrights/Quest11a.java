package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

@Interactive
public class Quest11a implements YesNoQuestion
{
	private static final long serialVersionUID = 1332971417282126972L;
	
	public static final String _id = "Quest11a";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private transient ResourceBundle _labels;

	public Quest11a()
	{
		this(UserCommunicator.getDefault());
	}
	
	public Quest11a(UserCommunicator uc)
	{
		_uc = uc;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( _labels.getString("Q0") );
		
		boolean ret = _uc.askYesNoQuestion( _labels.getString("Q1") );
		_uc.printAnswer(ret, ret ? 
				 		  _labels.getString("S1_good") 
						: _labels.getString("S1_bad") );
		
		_uc.printHidingParagraph(_labels.getString("S2"), _labels.getString("P1"));
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
	public void initialize() throws Exception
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
}
