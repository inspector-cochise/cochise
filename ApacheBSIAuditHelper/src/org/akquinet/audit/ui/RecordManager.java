package org.akquinet.audit.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class RecordManager
{
	private Properties _props;
	private File _recordsFile;

	public RecordManager(File records)
	{
		_recordsFile = records;
		_props = new Properties();

		try
		{
			_props.load(new FileInputStream(_recordsFile));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String getRecord(String name)
	{
		if (_props.containsKey(name))
		{
			return _props.get(name).toString();
		}

		return null;
	}

	public void saveRecord(String name, String content)
	{
		_props.put(name, content);
		try
		{
			_props.store(new FileOutputStream(_recordsFile), "");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
