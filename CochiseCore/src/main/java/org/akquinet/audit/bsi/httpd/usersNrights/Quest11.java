package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.util.ResourceWatcher;

@Interactive
public class Quest11 extends ResourceWatcher implements YesNoQuestion
{
	private static final long serialVersionUID = 2595066356127306236L;
	
	public static final String _id = "Quest11";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	
	private Quest11a _q11a;
	private Quest11b _q11b;
	private transient ResourceBundle _labels;
	private boolean _last11aAnswer = false;
	private boolean _firstRun = true;
	
	public Quest11(PrologueData pd)
	{
		this(pd._conf);
	}
	
	public Quest11(PrologueData pd, UserCommunicator uc)
	{
		this(pd._conf, uc);
	}
	
	public Quest11(ConfigFile conf)
	{
		this(conf, UserCommunicator.getDefault());
	}
	
	public Quest11(ConfigFile conf, UserCommunicator uc)
	{
		_uc = uc;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_q11a = new Quest11a(_uc);
		_q11b = new Quest11b(conf, _uc);
	}

	/**
	 * Looking for AllowOverride-directives. Checking whether there is no with parameters other than None and at least one
	 * in global context with parameter None.
	 */
	@Override
	public boolean answer()
	{
		_firstRun = false;
		
		_uc.printHeading3( _labels.getString("name") );
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
				_last11aAnswer = _q11a.answer();
			 	ret = _last11aAnswer;
			_uc.endIndent();
		}
		
		_uc.println(_labels.getString("S2") + " " + _labels.getString("name"));
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
		if(_firstRun)
		{
			return false;
		}
		else
		{
			return _q11b.resourceChanged() && !_last11aAnswer;
		}
	}
}
