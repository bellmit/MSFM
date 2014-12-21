package com.cboe.infrastructureServices.lifeLineService;

import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 */
public abstract class LifeLineServiceBaseImpl extends FrameworkComponentImpl implements LifeLineService 
{
	static String serviceImplClassName = "com.cboe.infrastructureServices.lifeLineService.LifeLineServiceNullImpl";
	static private LifeLineService instance = null;

    /**
     * Nobody should instantiate this directly, use the getInstance method.
     */
	protected LifeLineServiceBaseImpl()
	{
	}
	/**
	 */
	public static LifeLineService getInstance()
	{
	    if ( instance == null )
	    {
			try
			{
				Class c = Class.forName(serviceImplClassName);
		        instance = (LifeLineService)c.newInstance();
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

    /**
     *
     */
	public boolean initialize( ConfigurationService configService )
	{
		return true;
	}
    /**
     *
     */
	public static void setServiceImplClassName(String str)
	{
		serviceImplClassName =  str;
	}
}
