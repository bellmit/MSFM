package com.cboe.infrastructureServices.loggingService;

import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * The abstract base class that supports the logging service.
 * @version 3.6
 * @author Dave Hoag
 */
public abstract class LogServiceBaseImpl extends FrameworkComponentImpl implements LogService
{
	static String serviceImplClassName = "com.cboe.infrastructureServices.loggingService.LogServiceImpl";
	static LogServiceBaseImpl factoryReference;
	private static LogServiceBaseImpl instance;
    /**
     * Default to true.
     */
    public boolean isEnabled(MsgPriority priority, MsgCategory category)
    {
        return true;
    }
	/** */
	public static void setUseSMA(boolean newValue)
	{
		getFactoryReference().initUseOfSMA(newValue);
    }
	/**
	 */
	protected void initUseOfSMA(boolean newValue)
	{
	}
    /**
	 */
	LogServiceBaseImpl()
	{
	}
	/**
	 * Does this class use the actual logging service code?
	 */
	protected boolean fullService() { return true; }
	
	protected LogService createInstance(String str) throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		return this;
	}
	protected void finalInitialization(ConfigurationService service) throws Exception
	{
	}
	/**
	 * Only called once during the startup sequence.
	 * All other calls to 'getInstance' should provide a component name.
	 */
	public static LogService getInstance()
	{
		if (instance == null ) {
			try
			{
				Class c = Class.forName(serviceImplClassName);
				instance = (LogServiceBaseImpl)c.newInstance();
			}
			catch(Throwable t)
			{
				System.out.println("The logging service is not initialized.");
				t.printStackTrace();
			}
		}
		return instance;
	}
	/**
	 */
	public static LogService getInstance(final String componentName)
	{
        try
        {
            return getFactoryReference().createInstance(componentName);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Failed to instantiate the log service : " + ex);
        }
	}
    /**
     * Since our init sequence is difference if we are using property files.
     */
    public static void propertyFileInit(ConfigurationService configService) throws Exception
    {
		getFactoryReference().finalInitialization(configService);
		
    }
	/**
	 * This allows the individual implementations to initialize in their own way.
	 * @return LogServiceBaseImpl A particular implementation
	 */
	protected static LogServiceBaseImpl getFactoryReference()
	{
		return instance;
	}
	/**
	   Return  the name
	 */
	public static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}
	/**
	 * Only called once during the startup sequence.
	 */
	public boolean initialize(ConfigurationService configService)
	{
        return true;
    }
	/**
	 * Set  the name
	 */
	public static void setServiceImplClassName(String aName)
	{
	    serviceImplClassName = aName;
	}
}
