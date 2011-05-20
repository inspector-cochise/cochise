package org.akquinet.audit.bsi.httpd.usersNrights;

import java.util.Locale;
import java.util.ResourceBundle;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;

public class Quest11a implements YesNoQuestion
{
	private static final String _id = "Quest11a";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private ResourceBundle _labels;

	public Quest11a()
	{
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
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
}