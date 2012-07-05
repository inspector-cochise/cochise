package org.akquinet.audit.bsi.httpd.software;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

@Automated
public class Quest5a implements YesNoQuestion
{
	private static final long serialVersionUID = 2140086557092986457L;
	
	private static final String _id = "Quest5a";
	private ConfigFile _conf;
	private transient ResourceBundle _labels;
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	
	public Quest5a(ConfigFile conf)
	{
		this(conf, UserCommunicator.getDefault());
	}
	
	public Quest5a(ConfigFile conf, UserCommunicator uc)
	{
		_uc = uc;
		_conf = conf;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	/**
	 * checks whether there are any Include-directives in the config file
	 */
	@Override
	public boolean answer()
	{
		return answer(_uc);
	}
	

	public boolean answer(UserCommunicator uc)
	{
		uc.printHeading3( _labels.getString("name") );
		uc.printParagraph( _labels.getString("Q0") );
		
		
		List<Directive> incList = _conf.getAllDirectivesIgnoreCase("Include");
		
		if(!incList.isEmpty())
		{
			uc.printAnswer(false, _labels.getString("S1_bad") );
			StringBuffer buf = new StringBuffer();
			for (Directive directive : incList)
			{
				buf.append( directive.getContainingFile() + ":" + directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue() + "\n");
			}
			buf.deleteCharAt(buf.length()-1);
			uc.printExample(buf.toString());
			return false;
		}
		else
		{
			uc.printAnswer(true, _labels.getString("S1_good") );
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
		_conf.reparse();
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
