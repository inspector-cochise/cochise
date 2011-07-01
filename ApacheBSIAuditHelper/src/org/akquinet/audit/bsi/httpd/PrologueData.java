package org.akquinet.audit.bsi.httpd;

import java.io.File;

import org.akquinet.httpd.ConfigFile;

class PrologueData
{
	String _apacheExec = null;
	String _apacheConf = null;
	File _apacheExecutable = null;
	ConfigFile _conf = null;
	File _configFile = null;
	boolean _highSec = true;
	boolean _highPriv = true;
	
	
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
