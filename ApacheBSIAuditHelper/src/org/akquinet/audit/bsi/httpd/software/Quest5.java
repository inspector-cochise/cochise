package org.akquinet.audit.bsi.httpd.software;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;

@Automated
public class Quest5 implements YesNoQuestion
{
	private static final long serialVersionUID = -4279173783561872555L;
	
	private static final String _id = "Quest5";
	private ConfigFile _conf;
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	
	private Quest5a _q5a;
	private Quest5b _q5b;
	private transient ResourceBundle _labels;
	
	public Quest5(ConfigFile conf)
	{
		this(conf, UserCommunicator.getDefault());
	}
	
	public Quest5(ConfigFile conf, UserCommunicator uc)
	{
		_uc = uc;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_conf = conf;
		_q5a = new Quest5a(_conf, uc);
		_q5b = new Quest5b(_conf, uc);
	}

	/**
	 * Looking for AllowOverride-directives. Checking whether there is no with parameters other than None and at least one
	 * in global context with parameter None.
	 */
	@Override
	public boolean answer()
	{
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( MessageFormat.format( _labels.getString("Q0") , _conf.getFileName()) );
		_uc.printHidingParagraph(_labels.getString("S0"), _labels.getString("P0"));
		
		
		_uc.println( _labels.getString("L1") );
		_uc.beginIndent();
		boolean answer5a = _q5a.answer();
		boolean answer5b = _q5b.answer();
		boolean ret = answer5a && answer5b;
		
		_uc.endIndent();
		_uc.println(MessageFormat.format( _labels.getString("L2") , _labels.getString("name")));
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
		return 2;
	}

	@Override
	public int getNumber()
	{
		return 5;
	}

	@Override
	public String[] getRequirements()
	{
		String[] ret = new String[1];
		ret[0] = "Quest6";
		return ret;
	}
	
	@Override
	public void initialize() throws Exception
	{
		_q5a.initialize();
		_q5b.initialize();
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
