package org.akquinet.audit.bsi.httpd.usersNrights;

import java.util.Locale;
import java.util.ResourceBundle;

import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

public class Quest11 implements YesNoQuestion
{
	private static final String _id = "Quest11";
	private ConfigFile _conf;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	private Quest11a _q11a;
	private Quest11b _q11b;
	private ResourceBundle _labels;
	
	public Quest11(ConfigFile conf)
	{
		_conf = conf;
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
		_q11a = new Quest11a();
		_q11b = new Quest11b(_conf);
	}

	/**
	 * Looking for AllowOverride-directives. Checking whether there is no with parameters other than None and at least one
	 * in global context with parameter None.
	 */
	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		_uc.println( _labels.getString("L1") );
		_uc.beginIndent();
		
		boolean ret = _q11b.answer();
		_uc.endIndent();
		if(!ret)
		{
			_uc.println( _labels.getString("L2") );
			_uc.beginIndent();
			 	ret = _q11a.answer();
			_uc.endIndent();
		}
		
		_uc.println("Back to " + _id);
		_uc.printAnswer(ret, ret ?
				 	  _labels.getString("S1_good") 
					:  _labels.getString("S1_bad") );
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
