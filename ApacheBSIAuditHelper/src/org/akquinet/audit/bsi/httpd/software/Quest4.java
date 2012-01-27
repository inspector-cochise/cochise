package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.ParserException;
import org.akquinet.httpd.syntax.Directive;

@Interactive
public class Quest4 extends ModuleHelper implements YesNoQuestion
{
	private static final long serialVersionUID = 8605771425006250958L;
	
	private static final String _id = "Quest4";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private transient ResourceBundle _labels;
	
	private String[] _compiledIntoModules;
	private String[] _loadModules;
	
	private boolean _lastAnswer;
	
	public Quest4(ConfigFile conf, File apacheExecutable)
	{
		super(conf, apacheExecutable);
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_lastAnswer = false;
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		if(!reevaluationRequired())
		{
			_uc.printAnswer(true, _labels.getString("S1_good"));
			return true;
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
		_uc.printAnswer(ret, ret ?  _labels.getString("S1_good") 
				: _labels.getString("S1_bad") );
		
		_lastAnswer = ret;
		return ret;
	}
	
	private String[] getLoadModules()
	{
		Directive[] loadModules = (Directive[]) getLoadModuleList().toArray();
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
		String[] compModulesOld = _compiledIntoModules;
		String[] loadModulesOld = _loadModules;
		_compiledIntoModules = getCompiledIntoModulesList();
		_loadModules = getLoadModules();
		
		if(!_lastAnswer)
		{
			return true;
		}
		
		if(compModulesOld.length != _compiledIntoModules.length)
		{
			return true;
		}
		for(int i = 0; i < compModulesOld.length; ++i)
		{
			if(!compModulesOld[i].equals( _compiledIntoModules[i]))
			{
				return true;
			}
		}
		
		if(loadModulesOld.length != _loadModules.length)
		{
			return true;
		}
		for(int i = 0; i < loadModulesOld.length; ++i)
		{
			if(!loadModulesOld[i].equals( _loadModules[i]))
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
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}
}
