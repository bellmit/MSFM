package com.cboe.infrastructureServices.processWatcherService;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

public class ProcessWatcherServiceNullImpl implements ProcessWatcherService {

	private static String MSG_HEAD = "ProcessWatcherServiceNullImpl >> ";
	
	public boolean initialize(ConfigurationService configService) 
	{
		String msg = MSG_HEAD + "initialze.";
		System.out.println(msg);
		
		return true;
	}

	public void register(ProcessDescription process)
			throws DataValidationException 
	{
		String msg = MSG_HEAD + "register. For process: " + process;
		System.out.println(msg);
	}

	public ProcessDescription getProcessByName(String processName)
			throws NotFoundException 
	{
		String msg = MSG_HEAD + "getProcessByName. For process name: " + processName;
		System.out.println(msg);
		
		ProcessDescription pd = new ProcessDescription();
		pd.setProcessName(processName);
		return pd;
	}

}
