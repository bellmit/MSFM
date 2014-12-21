package com.cboe.infrastructureServices.foundationFramework;

import java.util.Hashtable;

import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
/**
 * This class will manage the 'status' of each BOHome. Adding the 'status' management to the
 * BOHome object itself could be prone to error. This prevents that problem.
 * @author Dave Hoag
 */
public class BOHomeController
{
	static final Status UNINITIALIZED = new Status("Uninitialized");
	static final Status INITIALIZED = new Status("Initialized");
	static final Status STARTED = new Status("Started");
	static final Status STOPPED = new Status("Stopped");
	
	static Hashtable knownHomes = new Hashtable();
	
	BOHome home;
	Status status;
	String boHomeName;
	/**
	 */
	public BOHomeController(String aHomeName)
	{
		boHomeName = aHomeName;
		status = UNINITIALIZED;
		knownHomes.put(aHomeName, this);
	}
	/**
	 */
	public static BOHomeController getController(BOHome home)
	{
		return (BOHomeController )knownHomes.get(home.getName());
	}
	/**
	 * Use the HomeFactory findHome method to support the home lazy initialization.
	 */
	public BOHome getHome() throws CBOELoggableException
	{
		if(home == null)
		{
			home = HomeFactory.getInstance().findHome(boHomeName);
		}
		return home;
	}
	/**
	 */
	public String getName() throws CBOELoggableException
	{
		return getHome().getName();
	}
	/**
	 * Configuration information is stored in the ConfigurationService. A path is necessary to 
	 * get the properties related to this particular home. 
	 * @return String The path to use to determine property values in the configuration service.
	 */
	public String getFullName()  throws CBOELoggableException
	{
		return getHome().getFullName();
	}
	/**
	 * Determine the status of the home.
	 */
	public Status getStatus()
	{
		return status;
	}
	/**
	 * Usefull for command callback service.
	 */
	public String getStatusAsString()
	{
		return getStatus().toString();
	}
	/**
	 * Delegate shutdown request to the BOHome.
	 */
	public void shutdown() throws CBOELoggableException
	{
		if(getStatus() == STARTED)
		{
			getHome().shutdown();
			status = STOPPED;
		}
	}
	/**
	 * Delegate start request to the BOHome.
	 */
	public void start() throws CBOELoggableException
	{
		if(getStatus() == INITIALIZED || getStatus() == STOPPED)
		{
			getHome().start();
			status = STARTED;
		}
		else if(getStatus() == UNINITIALIZED)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "", "Attempted to start BOHome " + getName() + " without ever being initialized.");
		}
	}
	/**
	 * Delegate initialize request to the BOHome.
	 */
	public void initialize() throws CBOELoggableException
	{
		getHome().initialize();
		status = INITIALIZED;
	}
}
class Status
{
	String text;
	Status(String niceText)
	{
		text = niceText;
	}
	public String toString()
	{
		return text;
	}
}
