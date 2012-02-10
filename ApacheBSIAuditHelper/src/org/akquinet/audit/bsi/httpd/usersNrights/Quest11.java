package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

@Interactive
public class Quest11 implements YesNoQuestion
{
	private static final long serialVersionUID = -936378799140023359L;
	
	private static final String _id = "Quest11";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	private Quest11a _q11a;
	private Quest11b _q11b;
	private transient ResourceBundle _labels;
	
	public Quest11(ConfigFile conf)
	{
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_q11a = new Quest11a();
		_q11b = new Quest11b(conf);
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

		
		_uc.printHidingParagraph(_labels.getString("S0"), _labels.getString("P0"));
		
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
		
		_uc.println(_labels.getString("S2") + " " + _id);
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

	@Override
	public int getBlockNumber()
	{
		return 3;
	}

	@Override
	public int getNumber()
	{
		return 11;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}
	
	@Override
	public void initialize() throws Exception
	{
		_q11a.initialize();
		_q11b.initialize();
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
