package org.akquinet.audit.bsi.httpd.trustNsec;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.akquinet.audit.Automated;
import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.DevNullUserCommunicator;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.util.ResourceWatcher;

@Automated
public class Quest13 extends ResourceWatcher implements YesNoQuestion
{
	private static final long serialVersionUID = 1903677931753535191L;
	private static final String SSLCipherSuite_Minimum = "SSLCipherSuite !NULL:!eNULL:!aNULL:!ADH:!MD5:!RC2";

	private static final String _id = "Quest13";
	
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private transient ResourceBundle _labels;
	
	private ConfigFile _config;
	private ModuleHelper _moduleHelper;
	private Boolean _lastAnswer = null;
	
	public Quest13(PrologueData pd)
	{
		this(pd, UserCommunicator.getDefault());
	}
	
	public Quest13(PrologueData pd, UserCommunicator uc)
	{
		this(pd._conf, pd._apacheExecutable, uc);
	}

	public Quest13(ConfigFile conf, File apacheExecutable)
	{
		this(conf, apacheExecutable, UserCommunicator.getDefault());
	}
	
	public Quest13(ConfigFile conf, File apacheExecutable, UserCommunicator uc)
	{
		_uc = uc;
		_config = conf;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_moduleHelper = new ModuleHelper(_config, apacheExecutable);
	}
	
	@Override
	public boolean answer()
	{
		return answer(_uc);
	}
	
	public boolean answer(UserCommunicator uc)
	{
		uc.printHeading3( _labels.getString("name") );
		uc.printParagraph( _labels.getString("Q0") );
		
		if(!mod_sslIsPresent(uc) ||
		   !SSLRandomSeedIsWellAndPresent(uc) ||
		   !SSLMutexIsWellAndPresent(uc) ||
		   !SSLCipherSuiteIsWellAndPresent(uc))
		{
			uc.printAnswer(false, _labels.getString("A0"));
			return false;
		}
		else
		{
			uc.printAnswer(true, _labels.getString("A1"));
			return true;
		}
	}

	private boolean mod_sslIsPresent(UserCommunicator uc)
	{
		if(_moduleHelper.isModuleLoaded("ssl_module") || _moduleHelper.isModuleCompiledInto("mod_ssl.c"))
		{
			uc.println( _labels.getString("L1") );
			return true;
		}
		else
		{
			uc.println( _labels.getString("L2") );
			return false;
		}
	}

	private boolean SSLCipherSuiteIsWellAndPresent(UserCommunicator uc)
	{
		List<Directive> dirs = _config.getAllDirectivesIgnoreCase("SSLCipherSuite");
		
		if(dirs.size() != 1)
		{
			uc.printParagraph( _labels.getString("L3") );
			uc.printExample(SSLCipherSuite_Minimum);
			return false;
		}
		
		Set<String> spec = new HashSet<String>( Arrays.asList( dirs.get(0).getValue().split(":") ));
		
		//TODO isn't that for high security only?
		boolean ok = spec.contains("!NULL") &&
						spec.contains("!eNULL") &&
						spec.contains("!aNULL") &&
						spec.contains("!ADH") &&
						spec.contains("!MD5") &&
						spec.contains("!RC2");
		
		if(!ok)
		{
			uc.printParagraph( _labels.getString("L4") );
			uc.printExample(SSLCipherSuite_Minimum);
			return false;
		}
		else
		{
			uc.println( _labels.getString("L5") );
			return true;
		}
	}

	private boolean SSLMutexIsWellAndPresent(UserCommunicator uc)
	{
		List<Directive> dirs = _config.getAllDirectivesIgnoreCase("SSLMutex");
		
		if(dirs.size() != 1)
		{
			uc.println( _labels.getString("L6") );
			return false;
		}
		else if(dirs.get(0).getValue().equalsIgnoreCase("none") || dirs.get(0).getValue().equalsIgnoreCase("no"))
		{
			uc.println( _labels.getString("L7") );
			return false;
		}
		else
		{
			uc.println( _labels.getString("L8") );
			return true;
		}
	}

	private boolean SSLRandomSeedIsWellAndPresent(UserCommunicator uc)
	{
		List<Directive> sslRandomSeeds = _config.getAllDirectivesIgnoreCase("SSLRandomSeed");
		int connectCount = 0;
		int startupCount = 0;
		
		for(Directive seed : sslRandomSeeds)
		{
			if(seed.getValue().trim().substring(0, 7).equalsIgnoreCase("startup"))
			{
				++startupCount;
			}
			else if(seed.getValue().trim().substring(0, 7).equalsIgnoreCase("connect"))
			{
				++connectCount;
			}
		}
		
		boolean ret = true;
		
		if(startupCount <= 0)
		{
			uc.println( _labels.getString("L9") );
			ret = false;
		}
		else
		{
			uc.println( _labels.getString("L10") );
			ret &= true;
		}
		
		if(connectCount <= 0)
		{
			uc.println( _labels.getString("L11") );
			ret = false;
		}
		else
		{
			uc.println( _labels.getString("L12") );
			ret &= true;
		}
		
		return ret;
	}

	@Override
	public void initialize() throws Exception
	{
		synchronized(this)
		{
			_config.reparse();
			_moduleHelper.reparse();
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
	public String getName()
	{
		return _labels.getString("name");
	}

	@Override
	public int getNumber()
	{
		return 13;
	}

	@Override
	public int getBlockNumber()
	{
		return 4;
	}

	@Override
	public String[] getRequirements()
	{
		String[] ret = new String[1];
		ret[0] = "Quest6";
		return ret;
	}

	@Override
	public void setUserCommunicator(UserCommunicator uc)
	{
		_uc = uc;
	}
	
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		_uc = UserCommunicator.getDefault();
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	@Override
	public String getResourceId()
	{
		return "quest." + _id;
	}

	@Override
	public boolean resourceChanged()
	{
		try
		{
			initialize();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		//this is just a not so intelligent dummy-solution
		boolean answer = answer(new DevNullUserCommunicator());
		
		if(_lastAnswer != null && answer != _lastAnswer)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
