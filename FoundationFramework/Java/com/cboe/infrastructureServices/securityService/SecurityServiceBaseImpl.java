package com.cboe.infrastructureServices.securityService;

import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

/**
 * The SecurityService base implementation. SecurityService facade implementations could subclass
 * this base implementation.
 * 
 * @author Chuck Maslowski
 * @version 3.3
 */
public abstract class SecurityServiceBaseImpl extends FrameworkComponentImpl
implements SecurityService
{
	protected static String serviceImplClassName = "com.cboe.infrastructureServices.securityService.SecurityServiceImpl";
	private static SecurityService instance = null;

	public SecurityServiceBaseImpl()
	{ 
	}

	public static SecurityService getInstance()
	{
		if ( instance == null )
		{
			try
			{
				Class c = Class.forName(serviceImplClassName);
				instance = (SecurityService)c.newInstance();
			}
			catch ( ClassNotFoundException cnfe )
			{
				new CBOELoggableException( cnfe, MsgPriority.high ).printStackTrace();
				return null;
			}
			catch ( InstantiationException ie )
			{
				new CBOELoggableException( ie, MsgPriority.high ).printStackTrace();
				return null;
			}
			catch ( IllegalAccessException iae )
			{
				new CBOELoggableException( iae, MsgPriority.high ).printStackTrace();
				return null;
			}	    
		}
		return instance;
	}
	
	public boolean initialize(ConfigurationService configService)
	{
		return true;
	}
	
	public static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}

	public static void setServiceImplClassName(String str)
	{
		serviceImplClassName =  str;
	}
}
