package com.cboe.infrastructureServices.processWatcherService;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

public interface ProcessWatcherService {

	/**
	 *  Initialize the fascade.
	 */
	public boolean initialize(ConfigurationService configService);	
	
	/**
	 * register a process with ProcessWatcher
	 */
	public void register(ProcessDescription process) throws DataValidationException;
	
	/**
	 * query a process by name
	 */
	public ProcessDescription getProcessByName(String processName) throws NotFoundException;
	
}
