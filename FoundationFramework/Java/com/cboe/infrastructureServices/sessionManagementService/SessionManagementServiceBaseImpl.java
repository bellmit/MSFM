package com.cboe.infrastructureServices.sessionManagementService;

import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

/**
 *  Base implementation for the SessionManagementService fascade.
 * @version 1.2
 */
public abstract class SessionManagementServiceBaseImpl extends FrameworkComponentImpl
	implements SessionManagementService
{
	static String serviceImplClassName = "com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceImpl";
	static private SessionManagementService instance = null;

	public SessionManagementServiceBaseImpl() 
	{
		setSmaName("SessionManagementService");
	}

	/**
	 * Return the instance
	 */
	public static SessionManagementService getInstance()  
	{
	    if ( instance == null ){ 
	    	createServiceInstance();
		}
		return instance;
	}
	
	public static SessionManagementServiceV2 getV2Instance() {
		if (getInstance()instanceof SessionManagementServiceV2){
			return (SessionManagementServiceV2) getInstance();
		}
		return null;
	}
	
	private static void createServiceInstance()
	{
		try 
		{
			Class c = Class.forName(serviceImplClassName);
	        instance = (SessionManagementService)c.newInstance();
	    }
	    catch ( ClassNotFoundException ex ) 
		{
	        ex.printStackTrace();
	        System.exit(0);
	    }
	    catch ( InstantiationException ex ) 
		{
	        ex.printStackTrace();
	        System.exit(0);    	    }
	    catch ( IllegalAccessException ex ) 
		{
	        ex.printStackTrace();
	        System.exit(0);
	    }	 		
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
