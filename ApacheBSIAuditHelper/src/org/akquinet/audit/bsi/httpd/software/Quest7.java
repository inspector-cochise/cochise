package org.akquinet.audit.bsi.httpd.software;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

@Interactive
public class Quest7 implements YesNoQuestion
{
	private static final long serialVersionUID = 373411500569846577L;
	
	private static final String _id = "Quest7";
	private ConfigFile _conf;
	private transient ResourceBundle _labels;
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	
	private boolean _lastAnswer;
	private List<String> _problems;

	private boolean _isSetGlobal;
	
	public Quest7(ConfigFile conf)
	{
		_conf = conf;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
		_lastAnswer = false;
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		
		_uc.println( _labels.getString("L1") );
		_uc.println( _labels.getString("L2") );
		
		if(!reevaluationRequired())
		{
			_uc.printAnswer(true, _labels.getString("S2_good"));
			return true;
		}
		
		_uc.println(_isSetGlobal ?
				   _labels.getString("L3")
				 : _labels.getString("L4") );
		boolean allOk = _problems.isEmpty();
		if(!allOk)
		{
			_uc.println( _labels.getString("L5") );
			_uc.println( _labels.getString("L6") );
			for (String problem : _problems)
			{
				_uc.printExample(problem);
			}
			allOk = _uc.askYesNoQuestion( _labels.getString("Q1") );
		}
		
		boolean ret = _isSetGlobal && allOk;
		_uc.printAnswer(ret, ret ?  _labels.getString("S2_good") 
								:   _labels.getString("S2_bad") );
		
		_lastAnswer = ret;
		return ret;
	}
	
	private boolean reevaluationRequired()
	{
		
		boolean isSetGlobalOld = _isSetGlobal;
		_isSetGlobal = false;
		List<String> problemsOld = _problems;
		List<Directive> dirList = _conf.getAllDirectives("Options");
		_problems = new LinkedList<String>();
		
		for (Directive directive : dirList)
		{
			if(!directive.getValue().matches("[ \t]*[Nn]one[ \t]*"))
			{
				if(!directive.getValue().matches("[ \t]*-(\\S)*[ \t]*"))	//maybe an option is deactivated
				{
					_problems.add(directive.getContainingFile() + ":" + directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
				}
			}
			else if(directive.getSurroundingContexts().get(0) == null)
			{
				_isSetGlobal = true;
			}
		}
		
		if(!_lastAnswer)
		{
			return true;
		}
		
		if(isSetGlobalOld != _isSetGlobal || problemsOld.size() != _problems.size())
		{
			return true;
		}
		
		for(int i = 0; i < _problems.size(); ++i)
		{
			if(!_problems.get(i).equals( problemsOld.get(i) ))
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
		return 7;
	}

	@Override
	public String[] getRequirements()
	{
		String[] ret = new String[1];
		ret[0] = "Quest6";
		return ret;
	}
	
	@SuppressWarnings("unused")
	private void warnForPlugin(String optionsValue)
	{
		String[] pluginArr = optionsValue.split("[ \t]");
		for(int i = 0; i < pluginArr.length; ++i)
		{
			if(pluginArr[i].startsWith("+"))
			{
				pluginArr[i].replaceFirst("+", "");
			}
			
			if(pluginArr[i].startsWith("-"))
			{
				pluginArr[i] = "";
			}
		}
		
		Map<String,String> warnDB = new HashMap<String, String>(); //format regex -> warn-message
		//TODO maybe do a lookup in some database or file for this -> in future it may grows!
		warnDB.put("[Ii]ndexes",  _labels.getString("S3") );
		warnDB.put("[Ii]ncludes",  _labels.getString("S4") );
		
		for (String plugin : pluginArr)
		{
			for (String regex : warnDB.keySet())
			{
				if(plugin.matches(regex))
				{
					_uc.printExample(optionsValue + "\t " + warnDB.get(regex));
				}
			}
		}
	}
	
	@Override
	public void initialize() throws Exception
	{
		_conf.reparse();
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
