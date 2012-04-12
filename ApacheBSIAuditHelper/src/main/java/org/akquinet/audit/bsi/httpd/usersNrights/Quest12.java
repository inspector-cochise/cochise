package org.akquinet.audit.bsi.httpd.usersNrights;

import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

@Automated
public class Quest12 implements YesNoQuestion
{
	private static final long serialVersionUID = 5225915545021155768L;
	
	private static final String _id = "Quest12";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	
	private Quest12a _q12a;
	private Quest12b _q12b;
	private transient ResourceBundle _labels;
	
	public Quest12(PrologueData pd)
	{
		this(pd._conf, pd._apacheExec);
	}
	
	public Quest12(PrologueData pd, UserCommunicator uc)
	{
		this(pd._conf, pd._apacheExec, uc);
	}
	
	public Quest12(ConfigFile conf, String apacheExecutable)
	{
		this(conf, "./", "quest12.sh", "getApacheStartingUser.sh", apacheExecutable);
	}
	
	public Quest12(ConfigFile conf, String apacheExecutable, UserCommunicator uc)
	{
		this(conf, "./", "quest12.sh", "getApacheStartingUser.sh", apacheExecutable, uc);
	}
	
	public Quest12(ConfigFile conf, String commandPath, String command, String getUserNGroupCommand, String apacheExecutable)
	{
		this(conf, commandPath, command, getUserNGroupCommand, apacheExecutable, UserCommunicator.getDefault());
	}
	
	public Quest12(ConfigFile conf, String commandPath, String command, String getUserNGroupCommand, String apacheExecutable, UserCommunicator uc)
	{
		_uc = uc;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_q12a = new Quest12a(commandPath, command, getUserNGroupCommand, apacheExecutable, _uc);
		_q12b = new Quest12b(conf, commandPath, command, _uc);
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( _labels.getString("Q0") );
		
		
		_uc.println( _labels.getString("L1") );
		_uc.printHidingParagraph( _labels.getString("S0"), _labels.getString("P1") );
		
		_uc.beginIndent();
			boolean ret = _q12b.answer();
		_uc.endIndent();
		if(!ret)
		{
			_uc.println( _labels.getString("L2") );
			_uc.beginIndent();
				ret = _q12a.answer();
			_uc.endIndent();
		}
		
		_uc.println(_labels.getString("S2") + " " + _labels.getString("name"));
		_uc.printAnswer(ret, ret ?
				 	  _labels.getString("S1_good") 
					: _labels.getString("S1_bad") );
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
		return 12;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}
	
	@Override
	public void initialize() throws Exception
	{
		_q12a.initialize();
		_q12b.initialize();
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