package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.util.IResourceWatcher;

@Interactive
public class Quest4 extends ModuleHelper implements YesNoQuestion, IResourceWatcher
{
	private static final long serialVersionUID = 8721136798597684044L;
	
	public static final String _id = "Quest4";
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private transient ResourceBundle _labels;
	
	private String[] _compiledIntoModules;
	private String[] _loadModules;
	@SuppressWarnings("unused")		//maybe will be used later, having it here now so that it get's serialized
	private String _explanation = "";
	private boolean _firstRun = true;
	
	private transient Object _monitor = new Object();
	
	private boolean _lastAnswer;
	
	public Quest4(PrologueData pd)
	{
		this(pd._conf, pd._apacheExecutable);
	}
	
	public Quest4(PrologueData pd, UserCommunicator uc)
	{
		this(pd._conf, pd._apacheExecutable, uc);
	}
	
	public Quest4(ConfigFile conf, File apacheExecutable)
	{
		this(conf, apacheExecutable, UserCommunicator.getDefault());
	}
	
	public Quest4(ConfigFile conf, File apacheExecutable, UserCommunicator uc)
	{
		super(conf, apacheExecutable);
		_uc = uc;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_lastAnswer = false;
		
		_compiledIntoModules = getCompiledIntoModulesList();
		_loadModules = getLoadModules();
	}

	@Override
	public synchronized boolean answer()
	{
		_firstRun = false;
		_uc.printHeading3( _labels.getString("name") );
		_uc.printParagraph( _labels.getString("Q0") );
		
		if(!reevaluationRequired() && _lastAnswer)
		{
			_uc.printAnswer(true, _labels.getString("S1_good"));
			return true;
		}
		
		synchronized (_monitor)
		{
			_compiledIntoModules = getCompiledIntoModulesList();
			_loadModules = getLoadModules();
		}
		
		_uc.println( _labels.getString("L1") );
		_uc.println( _labels.getString("L2") );
		
		{
			StringBuffer buf = new StringBuffer();
			
			_uc.println( _labels.getString("L3") );
			_uc.println("");
			for (String mod : _compiledIntoModules)
			{
				buf = buf.append(mod + "\n");
			}
			
			_uc.printExample(buf.toString());
			buf = new StringBuffer();
			
			_uc.println( _labels.getString("L4") );

			_uc.println("");
			
			_uc.println( _labels.getString("L5") );
			_uc.println("");
			for (String mod : _loadModules)
			{
				buf = buf.append(mod + "\n");
			}
			_uc.printExample(buf.toString());
		}
		
		_uc.println( _labels.getString("L6") );
		boolean ret = _uc.askYesNoQuestion( _labels.getString("Q1") );
		
		if(ret)
		{
			_explanation = _uc.askTextQuestion( _labels.getString("REASON") );
		}
		
		_uc.printAnswer(ret, ret ?  _labels.getString("S1_good") 
				: _labels.getString("S1_bad") );
		
		_lastAnswer = ret;
		return ret;
	}
	
	private String[] getLoadModules()
	{
		Directive[] loadModules = (Directive[]) getLoadModuleList().toArray(new Directive[0]);
		String[] ret = new String[loadModules.length];
		for (int i = 0; i < loadModules.length; ++i)
		{
			Directive dir = loadModules[i];
			ret[i] = dir.getContainingFile() + ":" + dir.getLinenumber() + ": " + dir.getName() + " " + dir.getValue();
		}
		return ret;
	}
	
	private boolean reevaluationRequired()
	{
		try
		{
			initialize();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		
		String[] compModulesOld;
		String[] loadModulesOld;
		
		synchronized (_monitor)
		{
			compModulesOld = _compiledIntoModules.clone();
			loadModulesOld = _loadModules.clone();
		}
		String[] compiledIntoModules = getCompiledIntoModulesList();
		String[] loadModules = getLoadModules();
		
		if(compModulesOld.length !=	compiledIntoModules.length)
		{
			return true;
		}
		for(int i = 0; i < compModulesOld.length; ++i)
		{
			if(!compModulesOld[i].equals( compiledIntoModules[i]))
			{
				return true;
			}
		}
		
		if(loadModulesOld.length != loadModules.length)
		{
			return true;
		}
		for(int i = 0; i < loadModulesOld.length; ++i)
		{
			if(!loadModulesOld[i].equals( loadModules[i]))
			{
				return true;
			}
		}
		
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
	public void initialize() throws ParserException, IOException
	{
		reparse();
	}
	
	private synchronized void readObject( java.io.ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		_monitor = new Object();
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
			return reevaluationRequired();
		}
	}
}
