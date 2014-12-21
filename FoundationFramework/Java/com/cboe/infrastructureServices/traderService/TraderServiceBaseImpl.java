package com.cboe.infrastructureServices.traderService;

// Source file: G:/FoundationFramework/java/com/cboe/infrastructureServices/foundationFramework/TraderService.java

import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

/**
 * @version 1.2
 */
public abstract class TraderServiceBaseImpl extends FrameworkComponentImpl implements TraderService {

	static String serviceImplClassName = "com.cboe.infrastructureServices.traderService.TraderServiceImpl";
	static private TraderService instance = null;

	public TraderServiceBaseImpl() {
	}

	/**
	   @roseuid 365B5E66032A
	 */
	public static TraderService getInstance()  {
	    if ( instance == null ) {
			try {
				Class c = Class.forName(serviceImplClassName);
		        instance = (TraderService)c.newInstance();
		    }
		    catch ( ClassNotFoundException ex ) {
		        new CBOELoggableException( ex, MsgPriority.high).printStackTrace();
		        System.exit(0);
		    }
		    catch ( InstantiationException ex ) {
		        new CBOELoggableException( ex, MsgPriority.high ).printStackTrace();
		        System.exit(0);    	    }
		    catch ( IllegalAccessException ex ) {
		        new CBOELoggableException( ex, MsgPriority.high ).printStackTrace();
		        System.exit(0);
		    }	    
		}
		return instance;
	}
	
	public static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}
	
	public boolean initialize( ConfigurationService configService ) {
		return true;
	}

	public static void setServiceImplClassName(String str)
	{
		serviceImplClassName =  str;
	}
}
