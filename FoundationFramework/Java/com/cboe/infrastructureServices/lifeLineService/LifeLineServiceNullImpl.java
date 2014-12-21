package com.cboe.infrastructureServices.lifeLineService;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

public class LifeLineServiceNullImpl implements LifeLineService {
	
	private static String MSG_HEAD = "LifeLineServiceNullImpl >> ";
	
	public boolean initialize(ConfigurationService configService)
	{
		return true;
	}
	/**
	 * Start a LifeLine service which will accept lifeline call from other processes.
	 */
	public void startLifeLineServer()
	{
		String msg = MSG_HEAD + "startLifeLineServer is called. NullImpl will do nothing";
		System.out.println(msg);
	}
	/**
	 * Application will use this method to register a callback.
	 */
	public void registerListener(LifeLineNotificationListener aListener)
	{
		String msg = MSG_HEAD + "registerListener is called. NullImpl will do nothing";
		System.out.println(msg);		
	}
	/**
	 * Application uses this method to start LifeLineClients by providing a collection of targets. 
	 * The lifeline client will start to "ping" the targets, and inform the listener if any target 
	 * becomes unavailable.
	 */
	public void startLifeLineClient(String[] targets)
	{
		String msg = MSG_HEAD + "startLifeLineClient is called. NullImpl will do nothing";
		System.out.println(msg);	
	}

	/**
	 * If application wants to have a finer control on the decision making process 
	 * of LifeLineService regarding the target's Unavailablility, it can use this
	 * method to specify a timeout value, and a retry count. 
	 * 
	 * @param target: life line target.
	 * @param timeoutInMillis: the timeout value for LifeLine calls to the target 
	 * @param retryCount: when target is unavailable, retry this many times before
	 * 					  LifeLineNotificationListener is notified. 
	 */
	public void startLifeLineClient(String target, int timeoutInMillis, int retryCount)
	{
		String msg = MSG_HEAD + "startLifeLineClient is called with";
		msg = msg + "target/timeout/retryCount=" + target + "/" + timeoutInMillis + "/" + retryCount;
		msg = msg + ". NullImpl will do nothing";
		System.out.println(msg);		
	}
	
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
	public int getLifeLineTimeout(String target) throws NotFoundException 
	{
		String msg = MSG_HEAD + "getLifeLineTimeout is called with";
		msg = msg + " target = " + target;
		System.out.println(msg);
		return 1000;
	}
	
	public double getLifeLineRTTMultiplier(String target) throws NotFoundException
	{
		String msg = MSG_HEAD + "getLifeLineRTTMultiplier is called with";
		msg = msg + " target = " + target;
		System.out.println(msg);
		return 1.5;		
	}
	
	public int getLifeLineRetryCount(String target) throws NotFoundException
	{
		String msg = MSG_HEAD + "getLifeLineRetryCount is called with";
		msg = msg + " target = " + target;
		System.out.println(msg);
		return 3;		
	}
	
	public int getLifeLineInterval(String target) throws NotFoundException
	{
		String msg = MSG_HEAD + "getLifeLineInterval is called with";
		msg = msg + " target = " + target;
		System.out.println(msg);
		return 1000;		
	}
	
	/**
	 * The following four methods are designed for applications to dynamically reset 
	 * the LifeLine configuration parameters. If LifeLineClient for the target is not
	 * started yet, a NotFoundException will be thrown. And a DataValidationException
	 * will be thrown if the new value is bad.
	 */
	public void resetLifeLineTimeout(String target, int newValue) throws NotFoundException,DataValidationException
	{
		String msg = MSG_HEAD + "resetLifeLineTimeout is called with ";
		msg = msg + "target/newValue=" + target + "/" + newValue;
		System.out.println(msg);		
	}
	
	public void resetLifeLineRTTMultiplier(String target, double newValue) throws NotFoundException,DataValidationException
	{
		String msg = MSG_HEAD + "resetLifeLineRTTMultiplier is called with ";
		msg = msg + "target/newValue=" + target + "/" + newValue;
		System.out.println(msg);	
	}
	
	public void resetLifeLineRetryCount(String target, int newValue) throws NotFoundException,DataValidationException
	{
		String msg = MSG_HEAD + "resetLifeLineRetryCount is called with ";
		msg = msg + "target/newValue=" + target + "/" + newValue;
		System.out.println(msg);	
	}
	
	public void resetLifeLineInterval(String target, int newValue) throws NotFoundException,DataValidationException
	{
		String msg = MSG_HEAD + "resetLifeLineInterval is called with ";
		msg = msg + "target/newValue=" + target + "/" + newValue;
		System.out.println(msg);	
	}
		
}
