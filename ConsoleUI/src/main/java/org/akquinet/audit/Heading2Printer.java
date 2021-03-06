package org.akquinet.audit;

import org.akquinet.audit.ui.UserCommunicator;


public class Heading2Printer implements YesNoQuestion
{
	private static final long serialVersionUID = -8777472635403592769L;
	
	private transient UserCommunicator _uc = UserCommunicator.getDefault();
	private String _heading;
	private int _blockNumber;

	public Heading2Printer(String heading, int blockNumber)
	{
		_heading = heading;
		_blockNumber = blockNumber;
	}
	
	@Override
	public boolean answer()
	{
		_uc.printHeading2( _heading );
		return true;
	}

	@Override
	public int getBlockNumber()
	{
		return _blockNumber;
	}

	@Override
	public String getID()
	{
		return _heading;
	}

	@Override
	public int getNumber()
	{
		return -1;
	}

	@Override
	public String[] getRequirements()
	{
		return new String[0];
	}

	@Override
	public boolean isCritical()
	{
		return false;
	}

	@Override
	public void initialize() throws Exception
	{
		//nothing to do here
	}

	@Override
	public String getName()
	{
		return "HeadingPrinter";
	}

	@Override
	public void setUserCommunicator(UserCommunicator uc)
	{
		_uc = uc;
	}

}
