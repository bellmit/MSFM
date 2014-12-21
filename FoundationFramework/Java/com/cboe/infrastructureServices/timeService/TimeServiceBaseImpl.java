package com.cboe.infrastructureServices.timeService;

import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * @version 1.2
 */
public abstract class TimeServiceBaseImpl implements TimeService
{

	static String serviceImplClassName = "com.cboe.infrastructureServices.timeService.TimeServiceImpl";
	static private TimeService instance = null;

	protected TimeServiceBaseImpl() 
	{
	}

	/**
	 */
	public long getCurrentDateTime()
	{
		return System.currentTimeMillis();
	}
	/**
	   @roseuid 365B5E66032A
	 */
	public static TimeService getInstance()
	{
	    if ( instance == null )
	    {
			try
			{
				Class c = Class.forName(serviceImplClassName);
		        instance = (TimeService)c.newInstance();
		    }
		    catch ( ClassNotFoundException ex )
		    {
		        new CBOELoggableException( ex, MsgPriority.high);
		    }
		    catch ( InstantiationException ex ) 
		    {
		        new CBOELoggableException( ex, MsgPriority.high );
		    }
		    catch ( IllegalAccessException ex )
		    {
		        new CBOELoggableException( ex, MsgPriority.high );
		    }	    
		}
		return instance;
	}
	
	public static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}
	
	public boolean initialize( ConfigurationService configService )
	{
		return true;
	}

	public static void setServiceImplClassName(String str)
	{
		serviceImplClassName =  str;
	}
}
