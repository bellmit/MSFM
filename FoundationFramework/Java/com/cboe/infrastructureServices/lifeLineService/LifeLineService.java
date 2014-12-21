package com.cboe.infrastructureServices.lifeLineService;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;

public interface LifeLineService {
	
	/**
	 *  Initialize the fascade.
	 */
	public boolean initialize(ConfigurationService configService);	
	
	/**
	 * Start a LifeLine service which will accept lifeline call from other processes.
	 */
	public void startLifeLineServer();
	
	/**
	 * Application will use this method to register a callback.
	 */
	public void registerListener(LifeLineNotificationListener aListener); 
	
	/**
	 * Application uses this method to start LifeLineClients by providing 
	 * a collection of targets. The lifeline client will start to "ping" 
	 * the targets, and inform the listener if any target's state changes. 
	 */
	public void startLifeLineClient(String[] targets); 
	
	/**
	 * If application wants to have a finer control on the decision making process 
	 * of LifeLineService regarding the target's Unavailablility, it can use this
	 * method to specify a timeout value, and a retry count. The intervalBetweenRetry
	 * will be set to system default.
	 * 
	 * @param target: life line target.
	 * @param timeoutInMillis: the timeout value for LifeLine calls to the target 
	 * @param retryCount: when target is unavailable, retry this many times before
	 * 					  LifeLineNotificationListener is notified. 
	 */
	public void startLifeLineClient(String target, int timeoutInMillis, int retryCount);
	
	/**
	 * The following four methods are designed for applications to dynamically discover
	 * the current LifeLine configuration parameters.
	 * 1. LifeLine Timeout Value (timeout value)
	 * 2. LifeLine Round Trip Timeout Multiplier (used with lifeline timeout to determine RTT)
	 * 3. LifeLine Retry Count ( number of tries before informing target's failure)
	 * 4. LifeLine Interval (interval between each life line call)
	 * 
	 * If the target does not exist, NotFoundException will be thrown.
	 */
	public int getLifeLineTimeout(String target) throws NotFoundException;
	public double getLifeLineRTTMultiplier(String target) throws NotFoundException;
	public int getLifeLineRetryCount(String target) throws NotFoundException;
	public int getLifeLineInterval(String target) throws NotFoundException;
	
	/**
	 * The following four methods are designed for applications to dynamically reset 
	 * the LifeLine configuration parameters. If LifeLineClient for the target is not
	 * started yet, a NotFoundException will be thrown. And a DataValidationException
	 * will be thrown if the new value is bad.
	 */
	public void resetLifeLineTimeout(String target, int newValue) throws NotFoundException,DataValidationException;
	public void resetLifeLineRTTMultiplier(String target, double newValue) throws NotFoundException,DataValidationException;
	public void resetLifeLineRetryCount(String target, int newValue) throws NotFoundException,DataValidationException;
	public void resetLifeLineInterval(String target, int newValue) throws NotFoundException,DataValidationException;
	
}
