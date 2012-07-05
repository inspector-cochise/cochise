package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.DevNullUserCommunicator;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.util.IResourceWatcher;

@Automated
public class Quest3 extends ModuleHelper implements YesNoQuestion, IResourceWatcher
{
	private static final long serialVersionUID = -994168432495652020L;
	
	private static final String _id = "Quest3";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private transient ResourceBundle _labels;

	private boolean _lastAnswer = false;

	private boolean _firstRun = true;
	
	public Quest3(PrologueData pd)
	{
		this(pd._conf, pd._apacheExecutable);
	}
	
	public Quest3(PrologueData pd, UserCommunicator uc)
	{
		this(pd._conf, pd._apacheExecutable, uc);
	}
	
	public Quest3(ConfigFile conf, File apacheExecutable)
	{
		this(conf, apacheExecutable, UserCommunicator.getDefault());
	}
	
	public Quest3(ConfigFile conf, File apacheExecutable, UserCommunicator uc)
	{
		super(conf, apacheExecutable);
		_uc = uc;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public boolean answer()
	{
		_lastAnswer = answer(_uc);
		_firstRun = false;
		return _lastAnswer;
	}

	private boolean answer(UserCommunicator _uc)
	{
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( _labels.getString("Q0") );

		
		List<Directive> loadList = getLoadModuleList();
		for (Directive directive : loadList)
		{
			String[] arguments = directive.getValue().trim().split("[ \t]+");
			if(arguments == null || arguments.length < 2)
			{
				continue;
			}
			
			if(arguments[0].equals("security2_module"))
			{
				Directive modSec = directive;
				boolean ret;
				if(!modSec.isGlobal())
				{
					_uc.println(_labels.getString("S6"));
					_uc.printExample(modSec.getContainingFile() + ":" + modSec.getLinenumber() + ": " + modSec.getName() + " " + modSec.getValue());
					_uc.printAnswer(false, _labels.getString("S7"));
					ret = false;
				}
				else
				{
					_uc.printAnswer(true, _labels.getString("S1"));
					_uc.printExample(modSec.getContainingFile() + ":" + modSec.getLinenumber() + ": " + modSec.getName() + " " + modSec.getValue());
					ret = true;
				}
				_uc.printHidingParagraph(_labels.getString("S4"), _labels.getString("S5"));
				return ret;
			}
			
		}
		
		//maybe ModSecurity is compiled into the apache binary, check for that:
		String[] modList = getCompiledIntoModulesList();
		for (String str : modList)
		{
			if(str.matches("( |\t)*mod_security.c( |\t)*"))
			{
				_uc.printAnswer(true, _labels.getString("S2"));
				_uc.printHidingParagraph(_labels.getString("S4"), _labels.getString("S5"));
				return true;
			}
		}
		
		_uc.printAnswer(false, _labels.getString("S3"));
		
		_uc.printHidingParagraph(_labels.getString("S4"), _labels.getString("S5"));
		return false;
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
		return 3;
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
		reparse();
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
	
	/**
	 * Two IResourceWatchers are equal if they have the same resourceId.
	 */
	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		
		if(o instanceof IResourceWatcher)
		{
			IResourceWatcher rhs = (IResourceWatcher) o;
			return getResourceId().equals(rhs.getResourceId());
		}
		else
		{
			return super.equals(o);
		}
	}
	
	@Override
	public int hashCode()
	{
		return getResourceId().hashCode();
	}
}
