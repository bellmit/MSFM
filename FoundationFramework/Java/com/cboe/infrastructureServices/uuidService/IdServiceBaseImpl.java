package com.cboe.infrastructureServices.uuidService;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * Provide the facade to the core IdServce implementation.
 * @version 1.2
 * @author Dave Hoag
 */
public abstract class IdServiceBaseImpl extends FrameworkComponentImpl implements IdService
{
	static String serviceImplClassName = "com.cboe.infrastructureServices.uuidService.IdServiceNullImpl";
	static private IdService instance = null;
    /**
     */
    public void goMaster()
    {
    }
    /**
     * Nobody should instantiate this directly, use the getInstance method.
     */
	protected IdServiceBaseImpl()
	{
	}
	/**
	 */
	public static IdService getInstance()
	{
	    if ( instance == null )
	    {
			try
			{
				Class c = Class.forName(serviceImplClassName);
		        instance = (IdService)c.newInstance();
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
	 *
	 */
	public long getNextUUIDFromBlock() throws SystemException, NotFoundException
	{
		return 1;
	}
	/**
     *
     */
	public long getPerThreadBlockSize()
	{
		return 10;
	}

	public static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}	
	/**
	 * 
	 * @param blockSize
	 */
	public void setPerThreadBlockSize(long blockSize)
	{ }
	
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
