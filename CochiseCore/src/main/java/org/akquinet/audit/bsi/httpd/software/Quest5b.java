package org.akquinet.audit.bsi.httpd.software;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.Automated;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

@Automated
public class Quest5b implements YesNoQuestion
{
	private static final long serialVersionUID = -4340749679512931763L;
	
	private static final String _id = "Quest5b";
	private ConfigFile _conf;
	private transient ResourceBundle _labels;
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	
	public Quest5b(ConfigFile conf)
	{
		this(conf, UserCommunicator.getDefault());
	}
	
	public Quest5b(ConfigFile conf, UserCommunicator uc)
	{
		_uc = uc;
		_conf = conf;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
	}

	/**
	 * Looking for AllowOverride-directives. Checking whether there is no with parameters other than None and at least one
	 * in global context with parameter None.
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
		
		
		List<Directive> dirList = _conf.getAllDirectives("AllowOverride");
		List<Directive> problems = new LinkedList<Directive>();
		boolean isSetGlobalRoot = false;	//will be changed if at least one directive in global context is found
		
		for (Directive directive : dirList)
		{
			if(!directive.getValue().matches("[ \t]*[Nn]one[ \t]*"))
			{
				problems.add(directive);
			}
			else if(directive.getSurroundingContexts().size() == 2) //that means one real context and one context being null (i.e. global context)
			{
				if(directive.getSurroundingContexts().get(0).getName().equalsIgnoreCase("directory") && 
						directive.getSurroundingContexts().get(0).getValue().matches("[ \t]*/[ \t]*") &&
						directive.getSurroundingContexts().get(1) == null)
				{
					isSetGlobalRoot = true;
				}
			}
		}
		String global = isSetGlobalRoot ?
						  _labels.getString("S1")
						: _labels.getString("S2");
		String overrides = problems.isEmpty() ?
				  _labels.getString("S3")
				: _labels.getString("S4");
		uc.printAnswer(isSetGlobalRoot & problems.isEmpty(), global + " " + overrides);
		for (Directive directive : problems)
		{
			uc.println(directive.getContainingFile() + ":" + directive.getLinenumber() + ": " + directive.getName() + " " + directive.getValue());
		}

		
		return isSetGlobalRoot && problems.isEmpty();
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
