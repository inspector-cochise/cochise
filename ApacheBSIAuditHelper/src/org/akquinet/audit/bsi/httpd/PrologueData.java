package org.akquinet.audit.bsi.httpd;

import java.io.File;
import java.io.Serializable;

import org.akquinet.httpd.ConfigFile;

public class PrologueData implements Serializable
{
	private static final long serialVersionUID = -5150925213077439859L;
	
	public String _apacheExec = null;
	public String _apacheConf = null;
	public File _apacheExecutable = null;
	public ConfigFile _conf = null;
	public File _configFile = null;
	public boolean _highSec = true;
	public boolean _highPriv = true;
	
	
	public PrologueData(String apacheExec,String apacheConf, File apacheExecutable,
						ConfigFile conf, File configFile,
						boolean highSec, boolean highPriv)
	{
		_apacheExec = apacheExec;
		_apacheConf = apacheConf;
		_apacheExecutable = apacheExecutable;
		_conf = conf;
		_configFile = configFile;
		_highSec = highSec;
		_highPriv = highPriv;
	}
}
