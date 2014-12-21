package com.cboe.infrastructureServices.persistenceService;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * Provide the abstract implementation of the persistence service.
 * This class really serves little other purpose than to contain the Singleton instance that
 * is the peristence service.
 * @version 2.1
 */
public abstract class PersistenceServiceBaseImpl extends FrameworkComponentImpl implements PersistenceService
{
	static String persistenceServiceImplClass = "com.cboe.infrastructureServices.persistenceService.PersistenceServiceImpl";
	private static PersistenceService instance;
	/**
	 * The implementation class name is the name of the class that is to be the persistence service.
	 * This class must be a fully qualified class name of a class with a Public default constructor
	 * and implements the PersistenceService interface.
	 */
	public static void setServiceImplClassName(String name)
	{
		persistenceServiceImplClass = name;
	}
	/**
	 * Use getInstance to create instances of this class.
	 */
	PersistenceServiceBaseImpl() 
	{
	}
	/**
	 * @roseuid 365B706D0128
	 */
	public static PersistenceService getInstance()
	{
		 if (instance == null) {
		    try 
		    {
			    Class c = Class.forName(persistenceServiceImplClass);
				    instance = (PersistenceService)c.newInstance();
			}
			catch (Exception e)
			{
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "PeristenceServiceBaseImpl.getInstance","Failed to create the persistence service.", e);
			}
		 }
		 return instance;
	}
	/**
	 * There is no configuration as defined by this abstract implementation.
	 */
	public boolean initialize(ConfigurationService configService) { return true; }

	/**
	 * Default service has nothing to do.
	 */
	public void goMaster()
	{
	}
	/**
	 * Default service has nothing to do.
	 */
	public void goSlave()
	{
	}
}
