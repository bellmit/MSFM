package com.cboe.infrastructureServices.foundationFramework;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import com.cboe.infrastructureServices.calendarService.CalendarAdminServiceBaseImpl;
import com.cboe.infrastructureServices.eventService.EventServiceBaseImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.instrumentationService.InstrumentationService;
import com.cboe.infrastructureServices.instrumentationService.InstrumentationServiceBaseImpl;
import com.cboe.infrastructureServices.loggingService.LogService;
import com.cboe.infrastructureServices.loggingService.LogServiceBaseImpl;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.orbService.OrbService;
import com.cboe.infrastructureServices.orbService.OrbServiceBaseImpl;
import com.cboe.infrastructureServices.persistenceService.PersistenceServiceBaseImpl;
import com.cboe.infrastructureServices.securityService.SecurityServiceBaseImpl;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceBaseImpl;
import com.cboe.infrastructureServices.systemsManagementService.AdminServiceBaseImpl;
import com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.infrastructureServices.timeService.TimeServiceBaseImpl;
import com.cboe.infrastructureServices.traderService.TraderServiceBaseImpl;
import com.cboe.infrastructureServices.uuidService.IdServiceBaseImpl;
import com.cboe.infrastructureServices.lifeLineService.LifeLineServiceBaseImpl;
import com.cboe.infrastructureServices.processWatcherService.ProcessWatcherServiceBaseImpl;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.infrastructureServices.cacheService.JCacheFactoryBaseImpl;
/**
 * Abstract the way we register for command callbacks into it's own class.
 *
 * @author Dave Hoag
 * @version 2.1
 */
public class FrameworkSetup implements ConfigurationConstants
{
	protected boolean instStartup;
	
	/**
	 * The details of initializing the logging service.
	 * If this service can not be started, the application is terminated.
	 */
	protected void initLoggingService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
        String logComponent = "";
        boolean result = false;
		try
		{
			logComponent = configService.getProperty(name + '.' + LOG_COMPONENT);
			foundationFramework.defaultLogComponent = logComponent;
			String className = configService.getProperty(name + '.' + LOG_SERVICE_IMPL);
			System.out.println("Starting log service " + className);
			if(className != null && (!className.equals("")))
			{
  				LogServiceBaseImpl.setServiceImplClassName(className);
  			}
			LogService service = LogServiceBaseImpl.getInstance();
            if(service == null) System.exit(1);
            result = service.initialize(configService);

	    //            ManagedLoggingServiceImpl.initialize(); //Not supported
		}
		catch (NoSuchPropertyException ex)
		{
			System.out.println("Failed to start logging service: " + ex);
			ex.printStackTrace();
		}
        if(result)
        {
            foundationFramework.getLogService(logComponent).log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Log Service initialized.");
        }
        else
        {
            System.err.println( "Log Service indicated a failure to initialize.");
            System.exit(1);
        }
	}
	/**
	 * The details of initializing the command callback service.
	 */
	protected void initCommandService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
		try
		{
			String className = configService.getProperty(name + '.' + COMMAND_CALLBACK_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Starting CommandCallbackService : " + className);
			if(className != null && (!className.equals("")))
			{
				CommandCallbackService.setServiceImplClassName(className);
			}
			boolean result = CommandCallbackService.getInstance().initialize(configService);
			if(result)
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "CommandCallback Service initialized.");

			}
			else
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","CommandCallback Service indicated a failure to initialize.");
			}
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "initializeCoreServices","No commandCallbackServiceImpl property specified.");
		}
	}
	/**
	 * The details of initializing the orb service.
	 * If this service can not be started, the application is terminated.
	 */
	protected void initOrbService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
		try
		{
			String className = configService.getProperty(name + '.' + ORB_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Starting OrbService : " + className);
			if(className != null && (!className.equals("")))
			{
				OrbServiceBaseImpl.setServiceImplClassName(className);
			}
            OrbService orbService = OrbServiceBaseImpl.getInstance();
			boolean result = orbService.initialize(configService);
			if(result)
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Orb Service initialized.");

			}
			else
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Orb Service indicated a failure to initialize.");
                throw new RuntimeException("Failed to start orb service");
			}
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "initializeCoreServices","No orbServiceImpl property specified.");
		}
	}
	/**
	 * The details of initializing the Admin service.
	 */
	protected void initAdminService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();

		try
		{
		  	String className = configService.getProperty(name + '.' + ADMIN_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Starting Admin Service : " + className);
		 	if(className != null && (!className.equals("")))
		 	{
		  		AdminServiceBaseImpl.setServiceImplClassName(className);
		  	}
		  	boolean result = AdminServiceBaseImpl.getInstance().initialize(configService);
			if(result)
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Admin Service initialized.");
			}
			else
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Admin Service indicated a failure to initialize.");
			}
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "initializeCoreServices","No adminServiceImpl property specified.");
		}
	}
	/**
	 * The details of initializing the Event service.
	 */
	protected void initEventService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
		try
		{
			String className = configService.getProperty(name + '.' + EVENT_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Starting Event Service : " + className);
			if(className != null && (!className.equals("")))
			{
				EventServiceBaseImpl.setServiceImplClassName(className);
			}
			boolean result = EventServiceBaseImpl.getInstance().initialize(configService);

			if(result)
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Event Service initialized.");
			}
			else
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Event Service indicated a failure to initialize.");
				return;
			}

		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"initializeCoreServices", "No eventServiceImpl property specified.");
		}
    }
	/**
	 * The details of initializing the Trader service.
	 */
	protected void initTraderService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
        boolean result = false;
		try
		{
		  	String className = configService.getProperty(name + '.' + TRADER_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Starting Trader Service : " + className);
		  	if(className != null && (!className.equals("")))
		  	{
		  		TraderServiceBaseImpl.setServiceImplClassName(className);
		  	}
		  	result = TraderServiceBaseImpl.getInstance().initialize(configService);
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "initializeCoreServices","No traderServiceImpl property specified.");
		}
        if(result)
        {
            foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Trader Service initialized.");
        }
        else
        {
            foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Trader Service indicated a failure to initialize.");
            throw new RuntimeException("Failed to start trader service");
        }
	}
	/**
	 * The details of initializing the Security service.
	 */
	protected void initSecurityService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
		try
		{
		  	String className = configService.getProperty(name + '.' + SECURITY_SERVICE_IMPL);
			foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.debug, "initializeCoreServices","Using Security Service " + className);
		 	if(className != null && (!className.equals("")))
		 	{
		  		SecurityServiceBaseImpl.setServiceImplClassName(className);
		  	}

		  	foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.debug, "initializeCoreServices","\tSecurity Service " + SecurityServiceBaseImpl.getInstance());

		  	boolean result = SecurityServiceBaseImpl.getInstance().initialize(configService);
			if(result)
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Security Service initialized.");
			}
			else
			{
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Security Service indicated a failure to initialize.");
			}
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "initializeCoreServices","No securityServiceImpl property specified.");
		}
	}
	/**
	 * The details of initializing the Persistence service.
	 */
	protected void initPersistenceService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
		try
		{
		  	String className = configService.getProperty(name + '.' + PERSISTENCE_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Starting PersistenceService : " + className);
	  		PersistenceServiceBaseImpl.setServiceImplClassName(className);
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "initializeCoreServices","No persistenceServiceImpl property specified. Using default service.");
		}
		boolean result = PersistenceServiceBaseImpl.getInstance().initialize(configService);
		if(result)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Persistence Service initialized.");
		}
		else
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Persistence Service indicated a failure to initialize.");
		}
	}
	/**
	 * The details of initializing the Instrumentation service.
	 */
	protected void initInstrumentationService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
		try
		{
		  	String className = configService.getProperty(name + '.' + INSTRUMENTATION_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeCoreServices","Starting InstrumentationService : " + className);
	  		InstrumentationServiceBaseImpl.setServiceImplClassName(className);
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "initializeCoreServices","No instrumentationServiceImpl property specified. Using default service.");
		}
		boolean result = InstrumentationServiceBaseImpl.getInstance().initialize(configService);
		if(result)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Instrumentation Service initialized.");
		}
		else
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Instrumentation Service indicated a failure to initialize.");
		}
	}
	/**
	 * The details of initializing the Timer service.
	 */
	protected void initTimerService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();
		try
		{
		  	String className = configService.getProperty(name + '.' + TIMER_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Starting TimeService : " + className);
	  		TimeServiceBaseImpl.setServiceImplClassName(className);
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"initializeCoreServices", "No timerServiceImpl property specified. Using default service.");
		}
		TimeServiceBaseImpl.getInstance().initialize(configService);
		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Timer Service initialized.");
	}
	/**
	 * The details of initializing the Calender service.
	 */
	protected void initCalendarService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		try{
		String name = foundationFramework.getName();
		String className = configService.getProperty(name + '.' + CALENDAR_SERVICE_IMPL);
		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.debug, "initializeCoreServices","Using Calendar Service " + className);
		if(className != null && (!className.equals("")))
		{
			CalendarAdminServiceBaseImpl.setServiceImplClassName(className);
		} 

		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.debug, "initializeCoreServices","\tCalendar Service " + CalendarAdminServiceBaseImpl.getInstance());
		}catch(NoSuchPropertyException nspe){
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"initializeCoreServices", "No calendarServiceImpl property specified. Using default service.");
		}
		if ( CalendarAdminServiceBaseImpl.getInstance() instanceof FrameworkComponent)
	    {
			((FrameworkComponent)CalendarAdminServiceBaseImpl.getInstance()).setParentComponent(foundationFramework);
	    }
		boolean success = CalendarAdminServiceBaseImpl.getInstance().initialize(configService);
		if(success)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Calendar Service initialized.");
		}
		else
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Calendar Service indicated a failure to initialize.");
		}
	}
    /**
     *
     */
    protected BOHomeDescriptor [] getCoreServiceHomes(ConfigurationService svc, String processName) throws NoSuchPropertyException
    {
		String homeList = svc.getProperty(processName + '.' + LIST_OF_SERVICES, null);
        if(homeList == null)
        {
            return new BOHomeDescriptor [0];
        }
        Vector result = new Vector();
		StringTokenizer st = new StringTokenizer(homeList, ",");
        ProcessDescriptor pd = new ProcessDescriptor();
		while(st.hasMoreTokens())
		{
			String homeDescriptorName = st.nextToken().trim();
            Log.notification("Creating CoreService " + homeDescriptorName);
			String propertyKey = processName + '.' +  homeDescriptorName  + '.';
			BOHomeDescriptor bohd = pd.createHomeDescriptor(homeDescriptorName, svc, propertyKey );
			result.addElement(bohd);
		}
        BOHomeDescriptor [] resultArray = new BOHomeDescriptor [ result.size() ];
        result.copyInto(resultArray);
        return resultArray;
    }
	/**
	 * The details of initializing the Id service.
	 */
	protected void initIdService(FoundationFramework foundationFramework,  ConfigurationService configService) 
	throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();

		try
		{
		  	String className = configService.getProperty(name + '.' + ID_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Starting uuidService : " + className);
	  		IdServiceBaseImpl.setServiceImplClassName(className);
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"initializeCoreServices", "No " + ID_SERVICE_IMPL + " property specified. Using default service.");
		}
		boolean init =  IdServiceBaseImpl.getInstance().initialize(configService);
		if(init) 
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "UUID Service initialized.");
		}
		else
		{
			throw new RuntimeException("Failed to start the ID service");
		}
	}
    /**
     * The details of initializing the Cache service.
     */
    protected void initJCacheService(FoundationFramework foundationFramework,  ConfigurationService configService) 
    throws InstantiationException, ClassNotFoundException, IllegalAccessException
    {
        String name = foundationFramework.getName();

        try
        {
            String className = configService.getProperty(name + '.' + JCACHE_SERVICE_IMPL);
            foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Starting jcacheService : " + className);
            JCacheFactoryBaseImpl.setServiceImplClassName(className);
        }
        catch (NoSuchPropertyException ex)
        {
            foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"initializeCoreServices", "No " + JCACHE_SERVICE_IMPL + " property specified. Using default service.");
        }
        boolean init =  IdServiceBaseImpl.getInstance().initialize(configService);
        if(init) 
        {
            foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Cache Service initialized.");
        }
        else
        {
            throw new RuntimeException("Failed to start the JCache service");
        }
    }
	
	protected void initLifeLineService(FoundationFramework foundationFramework, ConfigurationService configService)
	throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		boolean initialized = false;
		String name = foundationFramework.getName();
		try
		{
		  	String className = configService.getProperty(name + '.' + LIFE_LINE_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Initializing lifeLineService : " + className);
	  		LifeLineServiceBaseImpl.setServiceImplClassName(className);
			initialized =  LifeLineServiceBaseImpl.getInstance().initialize(configService); 
			if(initialized) {
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "LifeLine Service initialized.");
			}
			else{
				throw new RuntimeException("Failed to initialize the LifeLine service");
			}				
		}
		catch (NoSuchPropertyException ex)
		{
			String msg = "No " + LIFE_LINE_SERVICE_IMPL + " property specified. Service will not be initialized."; 
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"initializeCoreServices", msg);
		}	 		
	}
	
	
	protected void initProcessWatcherService(FoundationFramework foundationFramework, ConfigurationService configService)
	throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		boolean initialized = false;
		String name = foundationFramework.getName();
		try
		{
		  	String className = configService.getProperty(name + '.' + PROCESS_WATCHER_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Initializing ProcessWatcherService : " + className);
	  		ProcessWatcherServiceBaseImpl.setServiceImplClassName(className);
			initialized =  ProcessWatcherServiceBaseImpl.getInstance().initialize(configService); 
			if(initialized) {
				foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "ProcessWatcher Service initialized.");
			}
			else{
				throw new RuntimeException("Failed to initialize the ProcessWatcher service");
			}				
		}
		catch (NoSuchPropertyException ex)
		{
			String msg = "No " + PROCESS_WATCHER_SERVICE_IMPL + " property specified. Service will not be initialized."; 
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"initializeCoreServices", msg);
		}	 		
	}	
	
	/**
	 * The details of initializing the SessionManagement service.
	 */
	protected void initSessionManagementService(FoundationFramework foundationFramework,  ConfigurationService configService) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String name = foundationFramework.getName();

		try
		{
		  	String className = configService.getProperty(name + '.' + SESSION_MGMT_SERVICE_IMPL);
    		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Starting SessionManagementService : " + className);
	  		SessionManagementServiceBaseImpl.setServiceImplClassName(className);
		}
		catch (NoSuchPropertyException ex)
		{
			foundationFramework.getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"initializeCoreServices", "No sessionManagementServiceImpl property specified. Using default service.");
		}
		if ( SessionManagementServiceBaseImpl.getInstance() instanceof FrameworkComponent)
	    {
			((FrameworkComponent)SessionManagementServiceBaseImpl.getInstance()).setParentComponent(foundationFramework);
	    }
		SessionManagementServiceBaseImpl.getInstance().initialize(configService);
		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "SessionManagement Service initialized.");
	}
	/**
	 * Create all of the core services from the properties specified in the parameter.
	 *
	 * <processName>.logServiceComponent is the default logging service.
	 * <processName>.logServiceImpl is the required logging service impl.
	 *
	 * @param configService ConfigurationService containing the necessary property values.
	 * @exception java.lang.InstantiationException Creating a class specified in the config service caused an exception.
	 * @exception java.lang.ClassNotFoundException A class specified by the config service could not be found.
	 * @exception java.lang.IllegalAccessException
	 */
	protected void initializeCoreServices( FoundationFramework foundationFramework,  ConfigurationService configService, HomeFactory coreServiceFactory) throws InstantiationException, ClassNotFoundException, IllegalAccessException
	{
		String className;
		String name = foundationFramework.getName();

		//No log service exists, print to Stdout.
		System.out.println("Beginning initialization of core services.");

		initLoggingService(foundationFramework, configService);
		initCommandService(foundationFramework, configService);
		initOrbService(foundationFramework, configService);
		initEventService(foundationFramework, configService);
		initTraderService(foundationFramework, configService);
		initSecurityService(foundationFramework, configService);
		initAdminService(foundationFramework, configService);
		initPersistenceService(foundationFramework, configService);
		initInstrumentationService(foundationFramework, configService);
		initTimerService(foundationFramework, configService);
		initSessionManagementService(foundationFramework, configService);
		initCalendarService(foundationFramework, configService);

        setupCoreServiceHomes(name, configService, coreServiceFactory);
        initIdService(foundationFramework, configService);
        initJCacheService(foundationFramework, configService);
        initLifeLineService(foundationFramework, configService);
        initProcessWatcherService(foundationFramework, configService);        
		foundationFramework.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeCoreServices", "Completed the initialization of core services.");
	}
    /**
     * Setup up a special set of BOHomes that are used for CoreServices.
     */
    protected void setupCoreServiceHomes(String name, ConfigurationService configService, HomeFactory coreServiceFactory)
    {
        try
        {
            BOContainerDescriptor containerDesc = new BOContainerDescriptor();
            containerDesc.setName("frameworkContainer");
		    BOContainer boc = ContainerFactory.getInstance().createBOContainer(containerDesc);
		    ContainerFactory.getInstance().addBOContainer(boc);

            BOHomeDescriptor  [] otherServices = getCoreServiceHomes(configService, name);
	        for(int i = 0; i < otherServices.length; ++i)
            try
	        {
	            otherServices[i].setBOContainerDescriptorName("frameworkContainer");
                coreServiceFactory.defineHome(otherServices[i].getBOHomeName(), otherServices[i]);
                BOHome home = coreServiceFactory.findHome(otherServices[i].getBOHomeName());
                home.initialize();
                home.start();
	        }
            catch(Exception ex)
            {
                Log.exception(otherServices[i].getBOHomeName() + " failed to start or initialize." , ex);
            } //Skip this service
        }
        catch (Exception ex) //Don't necessariliy halt the process if one of these services fail
        {
            Log.exception("Error when creating additional services from the service list.", ex);
        }
    }
	/**
     *
     */
	public void registerCommandCallbacks(FoundationFramework ff) throws CBOELoggableException
	{
		CommandCallbackService service = ff.getCommandCallbackService();
		service.registerForCommandCallback(ff, ff.getName() + ".startupCommand", "startupProcess", "startup the process", new String[0], new String[0]);
		service.registerForCommandCallback(ff, ff.getName() + ".shutdownCommand", "shutdownProcess", "shutdown the process", new String[0], new String[0]);
		service.registerForCommandCallback(ff, ff.getName() + ".goMasterCommand", "goMaster", "Move the server to a 'master' state.", new String[] { "java.lang.String" } , new String[] { "isFailover(true|false)" });
		service.registerForCommandCallback(ff, ff.getName() + ".goSlaveCommand", "goSlave", "Move the server to a 'slave' state.", new String[0], new String[0]);
		service.registerForCommandCallback(ff, ff.getName() + ".isMasterCommand", "isMaster", "Query FoundationFramework's current state", new String[0], new String[0]);
		Enumeration anEnum = HomeFactory.getInstance().getHomes();
		Vector registeredNames = new Vector();
		while(anEnum.hasMoreElements())
		{
			BOHome home = (BOHome)anEnum.nextElement();
			String fullName = home.getFullName();
			if(! registeredNames.contains(fullName))
			try
			{
				ff.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"registerCommandCallbacks", "Providing command callback control to BOHome " + fullName);
				BOHomeController controller = BOHomeController.getController(home);
				service.registerForCommandCallback(controller, fullName + ".shutdownCommand", "shutdown", "shutdown the home.", new String[0], new String[0]);
				service.registerForCommandCallback(controller, fullName + ".startupCommand", "start", "start the home.", new String[0], new String[0]);
				service.registerForCommandCallback(controller, fullName + ".statusCommand", "getStatusAsString", "Get the current status of the home.", new String[0], new String[0]);
				service.registerForCommandCallback(controller, fullName + ".initializeCommand", "initialize", "Initialize the home.", new String[0], new String[0]);
				registeredNames.addElement(fullName);
			}
			catch (Exception ex)
			{
				ff.getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm,"registerCommandCallbacks", "Failed to register command callbacks for BOHome " + home.getName(), ex);
			}
		}
	}
	
	public void registerProperties(FoundationFramework ff) throws CBOELoggableException
	{
		CommandCallbackService service = ff.getCommandCallbackService();
		service.registerProperty(ff, "IsMaster", null, "isMaster");
	}
	
	/**
     * 
     * @exception Exception There are a TON of reasons a home or container could fail to build
	 */
	protected void buildHomesAndContainers(FoundationFramework ff) throws Exception
	{
    	ff.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "buildHomesAndContainers", "Using standard FrameworkSetup to build Homes and Containers");
		ProcessDescriptor desc = new ProcessDescriptor();
		desc.initialize(ff.getName());
		ff.setProcessDescriptor(desc);
		buildFromProcessDescriptor(ff, desc);
	}
	/**
	 * Build container and home objects with this process descriptor.
	 * @assumption All of the 'core' services have been created prior to calling this method.
	 *
	 * @param processDescriptor ProcessDescriptor An object that contains all of the Descriptor information.
	 */
	protected void buildFromProcessDescriptor(FoundationFramework ff, final ProcessDescriptor processDescriptor) throws Exception
	{
		//Create containers.
		initializeContainers(ff, processDescriptor);
		//Register homes with HomeFactory
		initializeHomes(ff, processDescriptor);
	}
	/**
	 * Enumerate over all of the BOContainerDescriptors and create new BOContainers.
	 * @param processDescriptor ProcessDescriptor An object that contains all of the Descriptor information.
	 */
	protected void initializeContainers(FoundationFramework ff, final ProcessDescriptor processDescriptor) throws Exception
	{
		ContainerFactory containerFactory = ContainerFactory.getInstance();
		Enumeration anEnum = processDescriptor.getBOContainerDescriptors();
		while (anEnum.hasMoreElements())
		{
			BOContainerDescriptor bocd = (BOContainerDescriptor) anEnum.nextElement();
		    try
		    {
    			ff.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "initializeContainers", "Building container " + bocd.getName());

			    BOContainer boc = containerFactory.createBOContainer(bocd);
			    containerFactory.addBOContainer(boc);
			}
			catch(Exception t)
			{
      			ff.getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "initializeContainers","Failed to create container " + bocd.getName() , t);
                throw t;
			}
		}
	}
	/**
	 * Enumerate over all of the HomeDescriptors and create new BOHomes.
	 * @param processDescriptor ProcessDescriptor An object that contains all of the Descriptor information.
	 */
	protected void initializeHomes(FoundationFramework ff, final ProcessDescriptor processDescriptor) throws Exception
	{
		boolean autoInit = ff.getAutoInit();
		Enumeration anEnum = processDescriptor.getBOHomeDescriptors();
		HomeFactory homeFactory = HomeFactory.getInstance();
		while (anEnum.hasMoreElements())
		{
			//Define the homes
			BOHomeDescriptor bohd = (BOHomeDescriptor) anEnum.nextElement();
			try
			{
    			ff.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initializeHomes", "Defining home " + bohd.getBOHomeName());
			    homeFactory.defineHome(bohd.getBOHomeName(), bohd);
			    //The controller manages status information about the BOHome. Initialize MUST go through
			    //the controller.
			    BOHomeController controller = new BOHomeController(bohd.getBOHomeName());
			    if(autoInit)
			    {
			    	controller.initialize();
			    }
			}
			catch(Exception ex)
			{
      			ff.getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "initializeHomes","Failed to create and initialize home " + bohd.getBOHomeName() , ex);
                throw ex;
			}
		}
	}
	public void setInstStartup (boolean inst)
	{
		instStartup = inst;
	}
	
	public boolean instrumentStartup()
	{
		return instStartup;
	}
	
	/**
	 * creatingInstrumentor
	 * @param methodKey String uniquely identifying the method.
	 * @param homeName String uniquely identifying the homeName
	 */
	public void createInstrumentor(final String methodKey, final String homeName)
	{
		MethodInstrumentor mi = null;
		
		System.out.println ("createInstrumentor methodKey = " + methodKey + "homeName = " + homeName);
    	InstrumentationService srvc = FoundationFramework.getInstance().getInstrumentationService();
		try {
			if ( srvc.getMethodInstrumentorFactory() != null ) {
				mi = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().find( getPath( methodKey,homeName ) );

				if (mi == null) {
					mi = srvc.getMethodInstrumentorFactory().create( getPath(methodKey,homeName), null );
					srvc.getMethodInstrumentorFactory().register( mi );
				}
			}
		} 
		catch( InstrumentorAlreadyCreatedException e ) {
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.low, MsgCategory.information ,"createInstrumentor", "Failed to createInstrumentor " + homeName + ":" + methodKey, e);
		}
	}
	
	/**
	 * The instrumentation path.
	 * @param methodKey String uniquely identifying the method.
	 * @param homeName String uniquely identifying the homeName.
	 */
	public String getPath(final String methodKey, final String homeName)
	{
	    return homeName + '/' + methodKey;
	}
	
	/**
	 * PreProcess merely tracks the start time of a method invocation.
	 * @param methodID String The id representing the method in progress.
	 * @param homeName String uniquely identifying the homeName.
	 */
	public void preProcess(String methodID, String homeName)
	{
		MethodInstrumentor mi = null;
		if ( FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory() != null ) {

			mi = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().find( getPath( methodID,homeName ) );
		}
		if ( mi != null ) {
			mi.beforeMethodCall();
		}
	}	

	/**
	 * PostProcessing increments the number of calls and end time of a method invocation.
	 * @param methodID String The id representing the method in progress.
	 * @param homeName String uniquely identifying the homeName.
	 * @param exception boolean True if an exception occurred while executing the method
	 */
	public void postProcess(String methodID, String homeName, boolean exception)
	{
		MethodInstrumentor mi = null;
		if ( FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory() != null ) {

			mi = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory().find( getPath( methodID,homeName ) );
		}

		if ( mi != null ) {
			mi.incCalls( 1 );
			mi.afterMethodCall();
			if ( exception ) {
				mi.incExceptions( 1 );
			}
		}
	}
	
}
