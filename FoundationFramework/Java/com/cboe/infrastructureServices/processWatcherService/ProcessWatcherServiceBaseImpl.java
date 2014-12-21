package com.cboe.infrastructureServices.processWatcherService;

import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

public abstract class ProcessWatcherServiceBaseImpl extends FrameworkComponentImpl
		implements ProcessWatcherService {

	private static String serviceImplClassName = "com.cboe.infrastructureServices.processWatcherService.ProcessWatcherServiceNullImpl";
	private static ProcessWatcherService instance = null;
	
	/**
	 */
	protected ProcessWatcherServiceBaseImpl(){	
	}
	
	/**
	 */
	public static ProcessWatcherService getInstance()
	{
	    if ( instance == null )
	    {
			try
			{
				Class c = Class.forName(serviceImplClassName);
		        instance = (ProcessWatcherService)c.newInstance();
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

	/**
	 */
	public static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}		
	
	/**
	 */
	public static void setServiceImplClassName(String str)
	{
		serviceImplClassName =  str;
	}
	
	/**
	 */
	public boolean initialize(ConfigurationService configService) {
		return true;
	}
}
