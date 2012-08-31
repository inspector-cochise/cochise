package org.akquinet.audit.bsi.httpd.trustNsec;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.akquinet.audit.Interactive;
import org.akquinet.audit.YesNoQuestion;
import org.akquinet.audit.bsi.httpd.PrologueData;
import org.akquinet.audit.ui.UserCommunicator;
import org.akquinet.httpd.ConfigFile;
import org.akquinet.httpd.syntax.Context;
import org.akquinet.httpd.syntax.Directive;
import org.akquinet.util.ResourceWatcher;

@Interactive
public class Quest14 extends ResourceWatcher implements YesNoQuestion
{
	private static final long serialVersionUID = 8554177576618852641L;
	
	private static final String VIRTUALHOST_CONTEXT_NAME = "VirtualHost";
	private static final String SSLCERTIFICATEFILE_DIRECTIVE_NAME = "SSLCertificateFile";
	private static final String SSLENGINE_DIRECTIVE_NAME = "SSLEngine";

	public static final String _id = "Quest14";
	
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private transient ResourceBundle _labels;
	
	private List<Directive> _sslEngineList = null;
	private List<Directive> _sslCertificateFileList = null;
	private List<Context> _vhostList = null;
	
	private ConfigFile _config;
	
	public Quest14(PrologueData pd)
	{
		this(pd, UserCommunicator.getDefault());
	}
	
	public Quest14(PrologueData pd, UserCommunicator uc)
	{
		this(pd._conf, pd._apacheExecutable, uc);
	}

	public Quest14(ConfigFile conf, File apacheExecutable)
	{
		this(conf, apacheExecutable, UserCommunicator.getDefault());
	}
	
	public Quest14(ConfigFile conf, File apacheExecutable, UserCommunicator uc)
	{
		_uc = uc;
		_config = conf;
		_labels = ResourceBundle.getBundle(_id, _uc.getLocale());
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
		
		_sslEngineList = _config.getAllDirectivesIgnoreCase(SSLENGINE_DIRECTIVE_NAME);
		_sslCertificateFileList = _config.getAllDirectivesIgnoreCase(SSLCERTIFICATEFILE_DIRECTIVE_NAME);
		_vhostList = _config.getAllContextsIgnoreCase(VIRTUALHOST_CONTEXT_NAME);
		List<Context> sslVhosts = new LinkedList<Context>();
		
		for(Context vhost : _vhostList)
		{
			if(vhost.getDirectiveIgnoreCase(SSLENGINE_DIRECTIVE_NAME).size() < 1)
			{
				continue;
			}
			else if(vhost.getDirectiveIgnoreCase(SSLENGINE_DIRECTIVE_NAME).size() > 1)
			{
				uc.printParagraph( MessageFormat.format(_labels.getString("E0"), vhost.getBeginLineNumber(), vhost.getContainingFile()) );
				continue;
			}
			Directive sslengineOn = vhost.getDirectiveIgnoreCase(SSLENGINE_DIRECTIVE_NAME).get(0);
			if(!sslengineOn.getValue().equalsIgnoreCase("on"))
			{
				continue;
			}
			
			
			if(vhost.getDirectiveIgnoreCase(SSLCERTIFICATEFILE_DIRECTIVE_NAME).size() < 1)
			{
				continue;
			}
			else if(vhost.getDirectiveIgnoreCase(SSLCERTIFICATEFILE_DIRECTIVE_NAME).size() > 1)
			{
				uc.printParagraph( MessageFormat.format(_labels.getString("E1"), vhost.getBeginLineNumber(), vhost.getContainingFile()) );
				continue;
			}
			Directive sslCertificateFile = vhost.getDirectiveIgnoreCase(SSLCERTIFICATEFILE_DIRECTIVE_NAME).get(0);
			if(!sslCertificateFile_isOk(sslCertificateFile))
			{
				uc.printParagraph( MessageFormat.format(_labels.getString("E2"), vhost.getBeginLineNumber(), vhost.getContainingFile(), sslCertificateFile.getValue()) );
				continue;
			}
			
			sslVhosts.add(vhost);
		}
		
		if(sslVhosts.isEmpty())
		{
			uc.printAnswer(false, _labels.getString("A0"));
			uc.printExample(SSLENGINE_DIRECTIVE_NAME + " On\\" +
							SSLCERTIFICATEFILE_DIRECTIVE_NAME + " path/to/your/certificate/file");
			return false;
		}
		else
		{
			uc.printParagraph(_labels.getString("P1"));
			StringBuilder vhostListing = new StringBuilder();
			for(Context vhost : sslVhosts)
			{
				vhostListing = vhostListing.append(vhost.getContainingFile());
				vhostListing = vhostListing.append(':');
				vhostListing = vhostListing.append(vhost.getBeginLineNumber());
				vhostListing = vhostListing.append(":    ");
				vhostListing = vhostListing.append('<').append(vhost.getName()).append(' ').append(vhost.getValue()).append('>');
				vhostListing = vhostListing.append('\n');
			}
			uc.printExample(vhostListing.toString());
			
			boolean existsInsecure = uc.askYesNoQuestion( _labels.getString("Q1") );
			
			uc.printAnswer(!existsInsecure, existsInsecure ? _labels.getString("A1") : _labels.getString("A2"));
						
			uc.beginHidingParagraph( _labels.getString("L0") );
				uc.printParagraph( _labels.getString("HP0") );
				uc.printExample("<Directory /some/sensible/directory/ >" +
								"    SSLRequireSSL" +
								"</Directory>");
			uc.endHidingParagraph();
			
			return !existsInsecure;
		}
		
	}

	private boolean sslCertificateFile_isOk(Directive sslCertificateFile)
	{
		File certFile = new File(sslCertificateFile.getValue());
		if(certFile.isAbsolute())
		{
			return certFile.exists();
		}
		else if(_config.getHeadExpression().getServerRoot() != null)
		{
			File absCertFile = new File( _config.getHeadExpression().getServerRoot() + File.separator + certFile.getPath() );
			return absCertFile.exists();
		}
		else
		{
			return false;
		}
	}

	@Override
	public void initialize() throws Exception
	{
		synchronized(this)
		{
			_config.reparse();
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
		return 14;
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
		
		if(_sslEngineList == null ||
		   _sslCertificateFileList == null ||
		   _vhostList == null)
		{
			return true;
		}
		
		if((!_sslEngineList.			equals(  _config.getAllDirectivesIgnoreCase(SSLENGINE_DIRECTIVE_NAME)  )) ||
		   (!_sslCertificateFileList.	equals(  _config.getAllDirectivesIgnoreCase(SSLCERTIFICATEFILE_DIRECTIVE_NAME)  )) ||
		   (!_vhostList.				equals(  _config.getAllContextsIgnoreCase(VIRTUALHOST_CONTEXT_NAME)  )))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
