package org.akquinet.audit.bsi.httpd.software;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.akquinet.audit.ModuleHelper;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Directive;

public class Quest4 extends ModuleHelper implements YesNoQuestion
{
	private static final String _id = "Quest4";
	private static final UserCommunicator _uc = UserCommunicator.getDefault();
	private ResourceBundle _labels;
	
	public Quest4(ConfigFile conf, File apacheExecutable)
	{
		super(conf, apacheExecutable);
		_labels = ResourceBundle.getBundle(_id, Locale.getDefault());
	}

	@Override
	public boolean answer()
	{
		_uc.printHeading3(_id);
		_uc.printParagraph( _labels.getString("Q0") );
		
		_uc.println( _labels.getString("L1") );
		_uc.println( _labels.getString("L2") );
		
		{
			StringBuffer buf = new StringBuffer();
			
			_uc.println( _labels.getString("L3") );
			_uc.println("");
			String[] compModules = getCompiledIntoModulesList();
			for (String mod : compModules)
			{
				buf = buf.append(mod + "\n");
			}
			
			_uc.printExample(buf.toString());
			buf = new StringBuffer();
			
			_uc.println( _labels.getString("L4") );

			_uc.println("");
			
			_uc.println( _labels.getString("L5") );
			_uc.println("");
			List<Directive> loadModules = getLoadModuleList();
			for (Directive dir : loadModules)
			{
				buf = buf.append(dir.getContainingFile() + ":" + dir.getLinenumber() + ": " + dir.getName() + " " + dir.getValue() + "\n");
			}
			_uc.printExample(buf.toString());
		}
		
		_uc.println( _labels.getString("L6") );
		boolean ret = _uc.askYesNoQuestion( _labels.getString("Q1") );
		_uc.printAnswer(ret, ret ?  _labels.getString("S1_good") 
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
}
