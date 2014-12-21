package com.cboe.infrastructureServices.foundationFramework;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

import com.cboe.common.log.Logger;
import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.cacheService.JCacheFactory;
import com.cboe.infrastructureServices.cacheService.JCacheFactoryBaseImpl;
import com.cboe.infrastructureServices.calendarService.CalendarAdminService;
import com.cboe.infrastructureServices.calendarService.CalendarAdminServiceBaseImpl;
import com.cboe.infrastructureServices.eventService.EventService;
import com.cboe.infrastructureServices.eventService.EventServiceBaseImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.instrumentationService.InstrumentationService;
import com.cboe.infrastructureServices.instrumentationService.InstrumentationServiceBaseImpl;
import com.cboe.infrastructureServices.lifeLineService.LifeLineService;
import com.cboe.infrastructureServices.lifeLineService.LifeLineServiceBaseImpl;
import com.cboe.infrastructureServices.loggingService.LogService;
import com.cboe.infrastructureServices.loggingService.LogServiceBaseImpl;
import com.cboe.infrastructureServices.loggingService.LogServiceConsoleImpl;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.orbService.NoSuchPOAException;
import com.cboe.infrastructureServices.orbService.OrbService;
import com.cboe.infrastructureServices.orbService.OrbServiceBaseImpl;
import com.cboe.infrastructureServices.persistenceService.PersistenceService;
import com.cboe.infrastructureServices.persistenceService.PersistenceServiceBaseImpl;
import com.cboe.infrastructureServices.processWatcherService.ProcessDescription;
import com.cboe.infrastructureServices.processWatcherService.ProcessWatcherService;
import com.cboe.infrastructureServices.processWatcherService.ProcessWatcherServiceBaseImpl;
import com.cboe.infrastructureServices.securityService.SecurityService;
import com.cboe.infrastructureServices.securityService.SecurityServiceBaseImpl;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementService;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceBaseImpl;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementServiceV2;
import com.cboe.infrastructureServices.systemsManagementService.AdminService;
import com.cboe.infrastructureServices.systemsManagementService.AdminServiceBaseImpl;
import com.cboe.infrastructureServices.systemsManagementService.CommandCallbackService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl;
import com.cboe.infrastructureServices.timeService.TimeService;
import com.cboe.infrastructureServices.timeService.TimeServiceBaseImpl;
import com.cboe.infrastructureServices.traderService.TraderService;
import com.cboe.infrastructureServices.traderService.TraderServiceBaseImpl;
import com.cboe.infrastructureServices.uuidService.IdService;
import com.cboe.infrastructureServices.uuidService.IdServiceBaseImpl;
import com.cboe.infrastructureUtility.configuration.ConfigurationFacade;
import com.cboe.infrastructureUtility.configuration.ConfigurationFacadeImpl;
import com.cboe.systemsManagementService.managedObjectFramework.managedObjectBaseClasses.MBean;
import com.cboe.systemsManagementService.managedObjectFramework.systemManagementAdapter.SystemManagementAdapter;

/**
 * The Foundation Framework provides capabilities for initializing new business services, providing access to external services like logging, and orb access.  Each service can have its own set of configuration defaults that can be defined in an Application Configuration Service.  The first step to use the
 * FoundationFramework is to invoke the getInstance operation within an application services process.  The getInstance method returns the configured singleton instance of this class.  To subsequently obtain this instance use getInstance().
 * The foundation framework manages one set of
 * properties. This is merely the Properties class instance that can be updated from an external system through its Admin Interface.
 * @version 5.4
 * @author Dave Hoag
 */
public class FoundationFramework extends FrameworkComponentImpl implements ConfigurationConstants
{
	// Enumeration of Framework states
    protected enum Status { 
        STATUS_NOT_INITIALIZED(0, "Not Initialized "){}, 
        STATUS_INITIALIZING(1, "Initializing  "){}, 
        STATUS_GOING_SLAVE(2,  "Going-Slave   "){}, 
        STATUS_SLAVE(3,        "Slave         "){}, 
        STATUS_GOING_MASTER(4, "Going-Master  "){}, 
        STATUS_MASTER(5,       "Master        "){}, 
        STATUS_SHUTDOWN(6,     "Shutdown      "){};
 
        private int state;
        private final String logStatus;

        Status(int state, String logStatus) { this.state = state; this.logStatus = logStatus; }

        public int getState() { return state; }

        @Override public String toString() { return logStatus; }   

        public static Status fromState(final int state) {
            for (Status s : Status.values()) {
                if (s.state == state) {
                    return s;
                }
            }
            return STATUS_NOT_INITIALIZED;
        }
    }

	
	private static final boolean abortGoSlaveOnRuntimeOrError = Boolean.getBoolean( "FoundationFramework.AbortGoSlaveOnRuntimeOrError" );

	public String name;
	protected static FoundationFramework instance;
	protected ConfigurationService configService;
	protected ProcessDescriptor processDescriptor;
	protected FrameworkSetup frameworkSetup;
	protected String defaultLogComponent;
	boolean started = false;
	private int frameworkState = Status.STATUS_NOT_INITIALIZED.getState();   // value set to one of the above Framework states
	protected HomeFactory coreServiceFactory;
	Boolean isMasterValue;
	Boolean isDiscardingMasterValue=new Boolean(false);
	boolean instStartup;
	static final boolean tellTheHomes = true;
	static final boolean skipTheHomes = false;
	final String FatalFFException="Fatal FF Exception :";
	protected String statusFileName = System.getProperty("FF.statusFile", null);
	protected Writer statusWriter = null;
	protected boolean autoMaster;
	
	/*
	 * For now just define the logging configuration, eventually this will need to be bound
	 * via a -D
	 */
	private final ConfigurationFacade customConfigurationFacade;

	/** doRegisterWithPW is set to true by default
	 *  it's only set to false if it's explicitly set that way as a system property
	 *  need to do the not (bang) oper because it's testing to "false"
	 */
	protected static boolean doRegisterWithPW = !((System.getProperty("FF.registerWithPW", "true")).equalsIgnoreCase("false"));
	

	/**
	 * The FrameworkSetup is the object that contains the details about initializing the framework.
	 * This can be very different depending upon the selected configuration service.
	 */
	public void setFrameworkSetup(FrameworkSetup setup)
	{
		frameworkSetup = setup;
	}
	/**
	 * The Public Constructor simply intializes a container.
	 * All of the real intialization occurs in initialize(String processName, ConfigurationService configService)
	 * @see #initialize(java.lang.String , ConfigurationService )
	 * @author Dave Hoag
	 */
	public FoundationFramework()
	{
		setSmaType("GlobalFoundationFramework.FoundationFramework");
		setFrameworkSetup(new FrameworkSetup());
		
		customConfigurationFacade = ConfigurationFacadeImpl.buildFacade();
	}
	/**
	 * Determine if autoInitialization is enabled.
	 * AutoInit is defaulted to true. Only a value of 'false' will stop the auto init.
	 * @return boolean indicating if the BOHomes should be initialized immediately after declaration.
	 */
	public boolean getAutoStart()
	{
		boolean autoStart = false;
        String value = getProperty( AUTO_START, "false");
        autoStart = value.equals("true");
        if(!autoStart)
        {
            this.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "getAutoStart","Auto start disabled.");
        }
		return autoStart;
	}
	/**
	 * Determine if autoInitialization is enabled.
	 * AutoInit is defaulted to true. Only a value of 'false' will stop the auto init.
	 * @return boolean indicating if the BOHomes should be initialized immediately after declaration.
	 */
	public boolean getAutoInit()
	{
		// AutoInit is defaulted to true. Only a value of 'false' will stop the auto init.
		boolean autoInit = true;
        String value = getProperty( AUTO_INIT, "true");
        autoInit = ! value.equals("false");
        if(!autoInit)
        {
            this.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "getAutoInit","Auto initialization disabled.");
        }
		return autoInit;
	}
	
	/**
	 * Fetch the facade used to apply custom and dynamic infrastructure changes
	 * 
	 * @return a non-null facade object
	 */
	public ConfigurationFacade getCustomConfigurationFacade()
	{
		return customConfigurationFacade;
	}
	
	/**
	 * Get the singleton instance of the service.
	 *
	 * @return The singelton instance managed by the base implementation.
	 */
	public CommandCallbackService getCommandCallbackService()
	{
		return CommandCallbackService.getInstance();
	}
	/**
	 * The configuration service used to intialize this FoundationFramework instance.
	 *
	 * @return ConfigurationService used to initialize the framework.
	 */
	public ConfigurationService getConfigService()
	{
		return configService;
	}
	/**
	 * Get the singleton instance of the service.
	 *
	 * @return The singelton instance managed by the base implementation.
	 */
  	public EventService getEventService()
  	{
  		return EventServiceBaseImpl.getInstance();
	}
	/**
	 * Get the singleton instance of the FoundationFramework.
	 * Collaborates with the createInstance method to achieve thread
	 * safe instantiation.
	 *
	 * @return The singelton instance.
	 */
	public static FoundationFramework getInstance()
	{
		if (instance == null)
		{
			createInstance();
		}
		return instance;
	}
	/**
	 *  This method will take a different instance of the FF to be used for testing
	 */
	public static void setInstanceForTesting(FoundationFramework testinstance)
	{
	 instance=testinstance;
	}

	/**
	 * Make it thread safe while not synchronizing everytime someone calls getInstance()
	 * @see #getInstance()
	 */
	protected static synchronized void createInstance()
	{
		if (instance == null)
		{
			instance =  new FoundationFramework();
		}
	}
	/**
	 * Get the singleton instance of the service.
	 *
	 * @return The singelton instance managed by the base implementation.
	 */
	public InstrumentationService getInstrumentationService()
	{
		return InstrumentationServiceBaseImpl.getInstance();
	}
	/**
	 * Get the singleton instance of the service.
	 *
	 * @return The singelton instance managed by the base implementation.
	 */
	public SessionManagementService getSessionManagementService()
	{
		return SessionManagementServiceBaseImpl.getInstance();
	}
	/**
	 * Get the singleton instance of the service.
	 *
	 * @return The singelton instance managed by the base implementation.
	 */
	public SessionManagementServiceV2 getSessionManagementServiceV2()
	{
		return SessionManagementServiceBaseImpl.getV2Instance();
	}	
	
	/**
	 * The logService that will log your message. The result of this method is dependeny upon
	 * the specified parameter value.
	 * @param componentName A known logging service component.
	 * @return LogService facade that can log the message.
	 */
	public LogService getLogService(String componentName)
	{
		if(componentName == null)
		{
			return getDefaultLogService(); //The defaultLogService call will either call this class with a compName or return a ConsoleImpl
		}
		try
		{
			return LogServiceBaseImpl.getInstance(componentName);
		}
		catch (RuntimeException ex)
		{
			System.out.println("########### The configured logging service threw exception: " + ex +  " . Returning a console log service.");
			return new LogServiceConsoleImpl();
		}
		catch (Exception ex)
		{
			System.out.println("########### The configured logging service threw exception: " + ex +  " . Returning a console log service.");
			return new LogServiceConsoleImpl();
		}
	}
	/**
	 * Get the singleton instance of the service.
	 * @return The singelton instance managed by the base implementation.
	 */
	public AdminService getAdminService()
	{
		return AdminServiceBaseImpl.getInstance();
	}
	/**
	 * The process name for the foundation framework.
	 *
	 * @return String representing the process name.
	 */
	public String getName()
	{
		return name;
	}
	/**
	 *  A facility to allow the growing of the core services without requiring FF code changes.
	 * @param serviceName String This the name of the service. This should probably be a constant value defined in the service itself.
	 * @return BOHome the home of the service. Use the home to get the actual service implementation.
	 * @exception com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException A problem was encountered getting the core serivce.
	 */
	public BOHome getCoreService(String serviceName) throws CBOELoggableException
	{
		return coreServiceFactory.findHome(serviceName);
	}
	/**
	 * Just hide the way to get a reference to the IdService.
	 * @see com.cboe.infrastructureServices.uuidService.IdService
	 * @see com.cboe.infrastructureServices.uuidService.IdServiceBaseImpl
	 */
	public IdService getIdService()
	{
		return IdServiceBaseImpl.getInstance();
	}
	
	/**
	 * Just hide the way to get a reference to the LifeLineService.
	 * @see com.cboe.infrastructureServices.lifeLineService.LifeLineService
	 * @see com.cboe.infrastructureServices.lifeLineService.LifeLineServiceBaseImpl
	 */
	public LifeLineService getLifeLineService()
	{
		return LifeLineServiceBaseImpl.getInstance();
	}	
	
	/**
	 * Just hide the way to get a reference to the ProcessWatcherService.
	 * @see com.cboe.infrastructureServices.processWatcherService.ProcessWatcherService
	 * @see com.cboe.infrastructureServices.processWatcherService.ProcessWatcherServiceBaseImpl
	 */
	public ProcessWatcherService getProcessWatcherService()
	{
		return ProcessWatcherServiceBaseImpl.getInstance();
	}		
	/**
	 * The process decriptor is used to create the descriptors from the configuration
	 * service.
	 *
	 * @return The ProcessDescriptor that was used to configure this foundation framework instance.
	 */
	public ProcessDescriptor getProcessDescriptor()
	{
		return processDescriptor;
	}
	/**
	 * An unused method???
	 * @roseuid 3656361200D0
	 *
	 * @deprecated Old method from initial designs
	 */
	public java.util.Properties getProperties()
	{
		return System.getProperties();
	}
	/**
	 * Get the singleton instance of the service.
	 * @return The singelton instance managed by the base implementation.
	 */
	public SecurityService getSecurityService()
	{
		return SecurityServiceBaseImpl.getInstance();
	}
	/**
	 * Get the singleton instance of the service.
	 * @return The singelton instance managed by the base implementation.
	 */
	public TimeService getTimeService()
	{
		return TimeServiceBaseImpl.getInstance();
	}
	/**
	 * Get the singleton instance of the service.
	 * @return The singelton instance managed by the base implementation.
	 */
	public TraderService getTraderService()
	{
		return TraderServiceBaseImpl.getInstance();
	}
	/**
	 * Get the singleton instance of the service.
	 * @return The singelton instance managed by the base implementation.
	 */
	public CalendarAdminService getCalendarService()
	{
		return CalendarAdminServiceBaseImpl.getInstance();
	}
    /**
     * Get the singleton instance of the service.
     * @return The singelton instance managed by the base implementation.
     */
	public static JCacheFactory getCacheService()
    {
        return JCacheFactoryBaseImpl.getInstance();
    }    
	/**
	 * Get the singleton instance of the service.
	 * @return The singelton instance managed by the base implementation.
	 */
	public PersistenceService getPersistenceService()
	{
		return PersistenceServiceBaseImpl.getInstance();
	}
	/**
	 * Get the singleton instance of the service.
	 * @return The singelton instance managed by the base implementation.
	 */
	public OrbService getOrbService()
	{
		return OrbServiceBaseImpl.getInstance();
	}

    /*
     * @author Uma Diddi
     * 12/15/03...
     * Create a thread of execution to abort framework after sleeping 5 mins, if orb.shutdown() hung on us
     * Currently the sleep interval is not configurable...
    */
    protected void abortFramework()
    {
        System.out.println("CAME TO abortFramework -----------");
        Runnable timer = new Runnable()
        {
            public void run()
            {
                // Sleep 5 minutes before a system exit...
                try{
                    System.out.println("Sleeping 5 minutes before shutdown of FoundationFramework -----------");
                    Thread.sleep(60000 * 5);
                    System.out.println("Slept 5 minutes, abort FoundationFramework -----------");
                    System.exit(1);
                }
                catch(java.lang.InterruptedException ex){
                    ex.printStackTrace();
                }
            }
        };

        Thread t = new Thread(timer);
        t.start();
        System.out.println("Started the timer thread to abort FF -----------");

        try {
            System.out.println("Shutting down the orbservice -----------");
            getOrbService().shutdown();
            System.out.println("OrbService shutdown complete, abort FoundationFramework -----------");
            System.exit(1);
        }
        catch (Exception ex){
            ex.printStackTrace();
            System.exit(1);
        }
    }

	/**
	 * This method will initialize the foundationFramework. If the framework is set to autoStart, it will
	 * also 'start' the homes.
	 *
	 * @param processName The root name by which all properties will be looked up.
	 * @param configService An implementation of the ConfigurationService to use for initialization.
	 * @return boolean indicating the success of the initialization.
	 * @exception java.lang.ClassNotFoundException A class specified in the configuration could not be found.
	 * @exception java.lang.IllegalAccessException
	 * @exception java.lang.InstantiationException
	 * @exception Exception Catch all- Just about anything could go wrong during initialization
	 */
	public synchronized boolean initialize(String processName, ConfigurationService configService)  throws ClassNotFoundException, IllegalAccessException, InstantiationException , Exception
	{
        try{
            return init(processName,configService);
        }
        catch (FatalFoundationFrameworkException ffe)
        {
            Log.exception("Caught FatalFoundationFrameworkException in FF initialize", ffe);
			if (ffe.getException() != null)
			{
					Log.exception("Original exception:",ffe.getException());
					ffe.getException().printStackTrace();
			}
			Log.exception(FatalFFException,ffe);
            ffe.printStackTrace();
            abortFramework();
        }
        return false;
    }

    protected boolean init(String processName, ConfigurationService configService) throws ClassNotFoundException, IllegalAccessException, InstantiationException , Exception
    {
		if(name != null)
		{
			getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification,"intialize", "It appears as if the FoundationFramework is being initialized twice.");
		}

	    instStartup  = System.getProperty("InstrumentStartup")  != null;
	    frameworkSetup.setInstStartup( instStartup);
	    
		//FF Initialization
		setName(processName);
		initStatusLog();
		logStatus(Status.STATUS_INITIALIZING.toString());
		setState(Status.STATUS_INITIALIZING.getState());
		setConfigService(configService);
  		configService.setName(processName);
		coreServiceFactory = new HomeFactory();
		frameworkSetup.initializeCoreServices(this, configService, coreServiceFactory);

        this.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "intialize","Status of FF is: " +getStateAsString() );

		goSlave(skipTheHomes);//Every thing should first goSlave

		frameworkSetup.buildHomesAndContainers(this);
        this.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "intialize","All the homes have been defined and initialized.");
		frameworkSetup.registerCommandCallbacks(this);
		frameworkSetup.registerProperties(this);

		//What do we tell the others
		String prop = getProperty( IS_MASTER, "").toUpperCase();
		autoMaster = prop.equals("TRUE");
		if(autoMaster)
		{
			goMaster(skipTheHomes, false);
		}

		boolean autoStart = getAutoStart();
		if(autoStart)
		{
			this.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "intialize","About to auto start the foundation framework.");
			startup();
		}
		else
  		{
			this.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "intialize","FoundationFramework waiting for startup.");
  		}
		if(autoMaster && autoStart) //otherwise we have already notified the homes
		{
			notifyHomesOfMasterStatus(true, false);
            getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initialize", "Notification of all of the homes to goMaster is complete.");
            afterHomesToMaster();
            getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "intialize","Status of FF in init is > " +getStateAsString() );
		}


		//All done, finish up
		isMasterValue = new Boolean(autoMaster);
		getEventService().startupEventService();
        getInstrumentationService().initRegistrar();

        if ( doRegisterWithPW ) {
            registerWithProcessWatcher(); 
        }

        getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"initialize", "Event service is receiving events. All initialization is complete.");
		return true;
	}
	/**
	 * The default log service can be used when logging messages not related to a specific componet.
	 * @return The log service with the component name of the defaultLogComponent
	 */
	public LogService getDefaultLogService()
	{
		if(defaultLogComponent ==  null)
		{
			System.out.println("###########The framework appears to not be initialized: no default logServiceComponent. Returning a console log service.");
			return new LogServiceConsoleImpl();
		}
		return this.getLogService(defaultLogComponent);
	}
	/**
	 * The component name is default logging service component to use for the foundation
	 * framework.
	 *
	 * @return Default component name.
	 */
	public String getComponentName()
	{
		return defaultLogComponent;
	}
	public void setDefaultLogComponent(String str)
	{
		defaultLogComponent = str;
	}
	/**
	 * Used for initialization of the Service in the Foundation Framework
	 * arg[0] is the Application name.
	 * arg[1] is the Configuration service implementation
	 * Additional args may be supplied for the configuration services
	 * @param args String [] of the command line arguments.
	 */
	
	private void registerWithProcessWatcher()
	{
	  ProcessDescription pd=new ProcessDescription();
	  String orbName=System.getProperty("ORB.OrbName");
	  pd.setOrbName( orbName );
	  String hostname=getHostName();
	  pd.setHostName( hostname );
	  String port=System.getProperty("ORB.IIOPTransport.PortNum");
	  if ( port == null || ( port.length() == 0 ) )
	  {
		  port=System.getProperty("ORB.PortNum");
	  }
	  pd.setPortNumber( Integer.parseInt(port) );
	  pd.setProcessName( getProcessName(orbName) );
	  pd.setType( getType(orbName) );
  	  pd.setReferencedProcesses(getReferencedProcesses(orbName));
      pd.setPOAName( getPOAName(orbName) );
	  StringBuffer buffer = new StringBuffer();
	  buffer.append("Process Info");
	  buffer.append("\npd.getOrbName()="+pd.getOrbName());
	  buffer.append("\npd.getHostName()="+pd.getHostName());
	  buffer.append("\npd.getPortNumber()="+pd.getPortNumber());		
	  buffer.append("\npd.getProcessName()="+pd.getProcessName());
	  buffer.append("\npd.getPOAName()="+pd.getPOAName());
	  buffer.append("\npd.getType()="+pd.getType());
	  buffer.append("\npd.getReferencedProcesses=");
      for (String ref_proc : pd.getReferencedProcesses() )
      {
    	buffer.append("\n "+ref_proc);
      }
      getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "PWregistration", buffer.toString() );
      try
      {
          ProcessWatcherService pwService = getProcessWatcherService();
          pwService.register(pd);
          getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "PWregistration", "Process successfully registered" );
      }
      catch (DataValidationException e)
      {
          getDefaultLogService().log(MsgPriority.high, MsgCategory.systemNotification, "PWregistration","Error registering process (orbName=" + orbName +")\n Exception + "+e);
      }
	  return;
	}

	private String getHostName()
	{
	   String hostname;
	   hostname=System.getenv("host");
	   if (hostname == null )
	       hostname=System.getenv("HOST");
 	   if (hostname == null )
	   {
	     try
	     {
	       hostname = java.net.InetAddress.getLocalHost().getHostName();
 	     }
	     catch ( java.net.UnknownHostException e)
	     { hostname = null; }

	   }
	   return hostname;
	}
	
	private String getProcessName(String OrbName)
	{
		String processName=System.getProperty("SMA.Process");
		if ( (processName == null) || (processName.length() == 0) )
		{
		 String prefix=System.getenv("SBT_PREFIX");
		 int strtindex=prefix.length();
		 processName=OrbName.substring(strtindex);
		}
		return processName;
	}
	
	private short getType(String OrbName)
	{
		short type;
		Pattern  source_pattern=Pattern.compile(System.getProperty("PW.Source", "fixcas|cas01v2|cfix") );
		Pattern connection_pattern=Pattern.compile(System.getProperty("PW.Source", "v20Frontend") );
		Matcher source_match=source_pattern.matcher(OrbName);
		Matcher connection_match=connection_pattern.matcher(OrbName);
		if ( source_match.find() )
		{
	        type=ProcessDescription.PROCESS_TYPE_SOURCE;
		}
		else
		{
			if ( connection_match.find() )
			{
		        type=ProcessDescription.PROCESS_TYPE_CONNECTION;
			}
			else
			{
			type=ProcessDescription.PROCESS_TYPE_CRITICAL;
			}
		}
		return type;
	}

	private String getPOAName(String OrbName)
	{
		String poaName=new String("/UserPOA");
		Pattern  nopoa_pattern=Pattern.compile(System.getProperty("PW.NoPOA", "fixcas|cas01v2|cfix|SMSProxy") );
		Matcher nopoa_match=nopoa_pattern.matcher(OrbName);
		if ( nopoa_match.find() )
		{
			poaName=new String("NO_POA");
		}
		return poaName;
	}
	
	private String[] getReferencedProcesses(String OrbName)
	{
		String [] referencedProcesses=new String[0];
		Pattern  source_pattern=Pattern.compile(System.getProperty("PW.Source", "fixcas|cas01v2|cfix") );
		Matcher source_match=source_pattern.matcher(OrbName);
		try
		{
		 if ( source_match.find() )
		 {
		  SystemManagementAdapter sma= SystemManagementAdapter.getInstance();
		  MBean rootBean=sma.getMBean("Processes");
		  MBean[] processProperties=rootBean.getComponents("Process(*)");
		  for ( MBean bean: processProperties )
		  {
	             Object sms_property=bean.getPropertyValue("SMSRelationName");
	             Object[] sms_property_object=(Object[])sms_property;
	             String[] sms_property_string=new String[sms_property_object.length];
	             System.arraycopy(sms_property_object, 0, sms_property_string, 0, sms_property_string.length);
	             String SMS_Relation=sms_property_string[0];
	             Object remote_host_property=bean.getPropertyValue("remoteHost");
	             Object[] remote_host_object=(Object[])remote_host_property;
	             String[] remote_host_string=new String[remote_host_object.length];
	             System.arraycopy(remote_host_object, 0, remote_host_string, 0, remote_host_string.length);
	             String[] relations_string=new String[remote_host_string.length];
	             int idx=0;
	             for (String rhost : remote_host_string)
	             {
	              String sub_string=new String(SMS_Relation);
	              int index=sub_string.indexOf("{");
	              sub_string=sub_string.substring(0,index);
	              sub_string=sub_string.concat(rhost);
	              System.out.println("rhost="+sub_string);
	              relations_string[idx]= new String(sub_string);
	              idx++;
	             }
	             referencedProcesses=relations_string;
		 	  } 
		   }
		  }
		  catch (Exception e)
		  {
			  System.out.println("Exception caught processing MBean for "+OrbName+" : caught="+e);
		  }
		return referencedProcesses;
	}
	
	public static void main(String[] args)
	{
		if (args.length < 2)
		{
			System.out.println("Usage: java com.cboe.infrastructureServices.foundationFramework.FoundationFramework <ApplicationServerName> com.cboe.infrastructureServices.systemsManagementService.ConfigurationServiceFileImpl [ ApplicationServer.properties ] ");
			System.exit(1);
		}

		String processName = args [0];
		String adminConfigServiceImplClassName = args [1];

		try
		{
			Class c = Class.forName(adminConfigServiceImplClassName);
			FoundationFramework ff = FoundationFramework.getInstance();
			ConfigurationService configService = (ConfigurationService)c.newInstance();
			configService.initialize(args, 2);

			ff.initialize(processName, configService);
//  			ff.processEvents();
		}
		catch (CBOELoggableException ex)
		{
			ex.printStackTrace();
        }
        catch (FatalFoundationFrameworkException ex)
		{
            System.err.println("FoundationFramework :- Caught a FatalFoundationFrameworkException in main, Abnormal exit " + ex);
			if (ex.getException() != null)
			{
				System.err.println("Original exception:");
				ex.getException().printStackTrace();
			}
			System.err.println("Fatal FF exception:");
			ex.printStackTrace();
            FoundationFramework.getInstance().abortFramework();
		}
  		catch (Throwable e)
  		{	//First try to log the error via the LogService.
  			boolean logged = false;
  			if(FoundationFramework.getInstance() != null)
  			{
  				if(FoundationFramework.getInstance().getDefaultLogService() != null)
  				{
  					FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "main","Failed to initialize the Foundation Framework", e);
  					logged = true;
  					e.printStackTrace();
  				}
  			}
  			//We could not get a reference to the log service, so use the console for this error message.
  			if(!logged)
  			{
  				System.out.println("Failed to initialize foundation framework. " + e);
  				e.printStackTrace();
  			}
  			System.out.println("Abnormal exit " + e);
  			System.exit(1);
		}

	}
	/**
	 * A method to initialize the FoundationFramework with default null impls of many services.
	 * The persistence service will be live.
	 * The admin serivce is null.
	 * Messages logged to console.
	 *
	 * @param processName Required for successful execution of this method. Can be any name.
	 * @param propertiesFile A properties file. Contents of this file will override any other values. Can be null.
	 * @param defaultProps Default properties. These will override properties defines in this method (default service configurations), but can be overridden by the properties file. Useful for defining homes and such. Can be null.
	 */
	public static void initializeForTest(String processName, String propertiesFile, java.util.Properties defaultProps)
	{
		String properties = "";
		properties += "\n" + processName + '.' + EVENT_SERVICE_IMPL + "=com.cboe.infrastructureServices.eventService.EventServiceInProcessImpl";
		properties += "\n" + processName + '.' + LOG_SERVICE_IMPL + "=com.cboe.infrastructureServices.loggingService.LogServiceConsoleImpl";
		properties += "\n" + processName + '.' + LOG_COMPONENT + "=TEST";
		properties += "\n" + processName + '.' + ORB_SERVICE_IMPL + "=com.cboe.infrastructureServices.orbService.OrbServiceInProcessImpl";
		properties += "\n" + processName + '.' + TRADER_SERVICE_IMPL + "=com.cboe.infrastructureServices.traderService.TraderServiceInProcessImpl";
		properties += "\n" + processName + '.' + ADMIN_SERVICE_IMPL + "=com.cboe.infrastructureServices.systemsManagementService.AdminServiceNullImpl";
		properties += "\n" + processName + '.' + PERSISTENCE_SERVICE_IMPL + "=com.cboe.infrastructureServices.persistenceService.PersistenceServiceImpl";
		properties += "\n" + processName + '.' + ID_SERVICE_IMPL + "=com.cboe.infrastructureServices.uuidService.IdServiceSimpleImpl";
		ConfigurationService cf = null;
		try
		{
			java.io.StringReader in = new java.io.StringReader(properties);
			java.util.Properties po = new java.util.Properties();
			po.load(in);
			if (defaultProps != null)
			{
				po.putAll(defaultProps); // load all default properties
			}
			if(propertiesFile != null && new java.io.File(propertiesFile).exists())
			{
				java.io.FileInputStream fin = new java.io.FileInputStream(propertiesFile);
				po.load(fin);
				fin.close();
			}
			cf = getConfigurationService(po);
			cf.initialize(null, 0);
			FoundationFramework.getInstance().setFrameworkSetup(new FrameworkSetup());
			FoundationFramework.getInstance().initialize(processName, cf);
		}
		catch (Exception e)
		{
			e.printStackTrace();
            throw new RuntimeException("Failed to initialize foundation framework: " + e);
		}
	}
	/**
	 * Used by initializeForTest to install test configuration settings.
	 *
	 * @see #initializeForTest
	 * @param props Properties to use for configuration.
	 */
	protected static ConfigurationService getConfigurationService(final java.util.Properties props)
	{
		return new ConfigurationServiceFileImpl()
		{
			java.util.Properties prop = props;
			/**
			 * Override initialize.
			 */
			public boolean initialize(String[] parameters, int firstConfigurationParameter )
			{
				properties = prop;
				return true;
			}
		};
	}
	/**
	 * This need to be outside of the startup() because the FF need to accept request for admin
	 * one of the requests will be start(), which will call startup()
	 * Since we are using the trader service, it is questionable if this method is even needed.
	 */
	public void processEvents()
	{
		OrbServiceBaseImpl.getInstance().processRequest();
	}
	/**
	 * The configuration service used to initialize this foundation framework.
	 *
	 * @param aVal An implementation of the ConfigurationService
	 */
	public void setConfigService(ConfigurationService aVal)
	{
		configService = aVal;
	}
 	/**
 	 * The process name for the foundation framework. This process name is used when retrieving
 	 * properties from the configuration service.
 	 *
 	 * @param aName String of any type.
 	 */
	public void setName(String aName)
	{
		setSmaName(aName);
		name = aName;
	}
	
	private void setState(int newState)
	{
	    frameworkState = newState;
	}

	private String getStateAsString()
    {
	    return Status.fromState(frameworkState).toString();
    }
	
    public boolean isGoingSlave()
    {
        return (frameworkState == Status.STATUS_GOING_SLAVE.getState() ? true : false);
    }
 
    public boolean isGoingMaster()
    {
        return (frameworkState == Status.STATUS_GOING_MASTER.getState() ? true : false);
    }

    /**
	 * The process decriptor is used to create the descriptors from the configuration
	 * service.
	 *
	 * @param pd ProcessDescriptor
	 */
	public void setProcessDescriptor(ProcessDescriptor pd)
	{
		processDescriptor = pd;
	}
	/**
	 * An unused method???
	 * @roseuid 36563612012A
	 *
	 * @param propertyName
	 * @param value
	 * @deprecated Old method from initial designs
	 */
	public void setProperty(String propertyName, String value)
	{
		System.getProperties().put(propertyName, value);
	}
	/**
	 * Called once all initialization is complete.
	 * This is the point at which the home objects will either bind to other service objects, the event service, CORBA, etc...
	 *
	 * @author Dave Hoag
	 */
	public void startup()
	{
		if(! started)
		{
			Enumeration enumerator = HomeFactory.getInstance().getHomes();
			while(enumerator.hasMoreElements())
			{
				BOHome home = (BOHome)enumerator.nextElement();
				try
				{
					if (frameworkSetup.instrumentStartup())
					{
						frameworkSetup.createInstrumentor("StartupHomes", home.getName());
						frameworkSetup.preProcess("StartupHomes", home.getName());
					}
					getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "startUp","Starting BOHome " + home.getName());
					BOHomeController controller = BOHomeController.getController(home);
					controller.start();
					if (frameworkSetup.instrumentStartup())
					{
						frameworkSetup.postProcess("StartupHomes", home.getName(),false);
					}
				}
				catch (FatalFoundationFrameworkException ffe)
				{
					Log.exception("Caught FatalFoundationFrameworkException in FF startup", ffe);
					if (ffe.getException() != null)
					{
							Log.exception("Original exception:",ffe.getException());
							ffe.getException().printStackTrace();
					}
                    Log.exception(FatalFFException, ffe);
					ffe.printStackTrace();
					abortFramework();
				}
				catch (Exception ex)
				{
					getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "startUp","Failed to start BOHome " + home.getName(), ex);
				}
			}
			started = true;

			//Always go to slave after startup
			notifyHomesOfMasterStatus(false, false); //Tell homes they are slave
			getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "startUp","Completed startup of FoundationFramework.");
		}
		else
		{
			getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "startUp","Subsequent startup call to FoundationFramework ignored. Framework already started.");
		}
	}
	/**
	 * Called when shutting down the FoundationFramework.
	 */
	public void shutdown()
	{
		if(started)
		{
			Enumeration enumerator = HomeFactory.getInstance().getHomes();
			while(enumerator.hasMoreElements())
			{
				BOHome home = (BOHome)enumerator.nextElement();
				try
				{
					getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"shutdown", "Shutdown BOHome " + home.getName());
					BOHomeController controller = BOHomeController.getController(home);
					controller.shutdown();
				}
				catch (FatalFoundationFrameworkException ffe)
				{
					Log.exception("Caught FatalFoundationFrameworkException in FF shutdown", ffe);
					if (ffe.getException() != null)
					{
							Log.exception("Original exception:",ffe.getException());
							ffe.getException().printStackTrace();
					}
                    Log.exception(FatalFFException, ffe);
					ffe.printStackTrace();
					abortFramework();
				}
				catch (Exception ex)
				{
					getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm,"shutdown", "Failed to shutdown BOHome " + home.getName(), ex);
				}
			}
			started = false;

			setState(Status.STATUS_SHUTDOWN.getState());
            this.getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "shutdown","Status of FF is: " +getStateAsString() );
		}
		else
		{
			getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"shutdown", "Call to FoundationFramework shutdown when Framework not yet started.");
		}
	}
	/**
	 * Stop the foundation framework server.
	 * @roseuid 365B748202CF
	 */
	public void shutdownProcess()
	{
		getDefaultLogService().log(MsgPriority.medium, MsgCategory.systemNotification, "shutdownProcess","FoundationFramework shutdown requested.");
		shutdown();
		new Thread(getShutdown(Thread.currentThread())).start();
	}
	/**
	 * Shut down the framework on its own thread. This is to allow some normal processing to
	 * occur before shutting every thing down.
	 *
	 * @param currentThread The current thread. Likely the CORBA allocated thread.
	 * @return A runnable that can shutdown the foundation framework.
	 */
	Runnable getShutdown(final Thread currentThread)
	{
		return new Runnable()
		{
			public void run()
			{
				try
				{
					currentThread.join(1000); //Let the CORBA request return.
				}
				catch (Throwable e)
				{
					getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm,"getShutdown", "Exeception during shutdown.", e);
				}
				getOrbService().shutdown(); // Last thing to do.
				System.exit(0);
			}
		};
	}
	/**
	 * Exposed the the command callback service to startup this foundation framework.
	 */
	public void startupProcess()
	{
		startup();
		if(isMaster())
		{
			notifyHomesOfMasterStatus(true,false); //Tell homes they are master
            afterHomesToMaster();
            getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "startupProcess","Status of FF in startup is : " +getStateAsString() );
		}
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"startupProcess", "FoundationFramework startup completed.");
	}
	/**
	 * Used internally
	 */
	public void goMaster()
	{
		goMaster("false");
	}
	/**
	 *  Called via the CommandCallbackService as a result of a callback or SMA invocation.
	 * Since this is called via the CommandCallbackService, the parameter must be a string.
	 * @param failOver String "true" or "false"
	 */
	public void goMaster(String failOver)
	{

		boolean fail = false;
		if(failOver != null && failOver.length() > 0)
		{
			fail = failOver.toUpperCase().charAt(0) == 'T';
		}
		if(! started)
		{
			if(isSlave())
				goMaster(skipTheHomes, fail);

			startupProcess(); //Startup process will tell the homes
		}
		else
		if(isSlave()) //Otherwise nothing to do
		{
			goMaster(tellTheHomes, fail);
		}
		else
		{
			getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"goMaster", "Call to goMaster ignored. Already a master.");
		}
	}
	/**
	 * Called when the server is ready to go 'live'.
	 * This assumes there are no other servers (like the secondary server) already in the Master state.
	 * @param notifyHomes true if the collection of homes are to be notified of the goMaster call
	 */
	public synchronized void goMaster(boolean notifyHomes, boolean failOver)
	{
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"goMaster", "FoundationFramework starting transition to master.");
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"goMaster", "PersistenceService to master.");
		getPersistenceService().goMaster();
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"goMaster", "IdService to master.");
        getIdService().goMaster();
		if(notifyHomes)
		{
			notifyHomesOfMasterStatus(true, failOver);
		}
		if(isMasterValue != null) //A null values indicates a start sequence goSlave call. Value will be set later
		{
			isMasterValue = new Boolean(true);
		}

        if( notifyHomes)
        {
            afterHomesToMaster();
            getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "goMaster","Status of FF in goMaster is : " +getStateAsString() );
        }

		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"goMaster", "FoundationFramework goMaster completed.");

	}

    /**
     *  Operations to be performed after homes have "gone master".
     *  (have orb service 'go master' (activate POAa))
     */
    protected synchronized void afterHomesToMaster()
    {
		getOrbService().goMaster();
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"goMaster", "OrbService to master.");
		logStatus(Status.STATUS_MASTER.toString());
        setState(Status.STATUS_MASTER.getState());

    }

	/**
	 * Send each home a goMaster or goSlave request.
	 * @param toMaster true if we are notifying homes to goMaster, false if they are to goSlave
	 * @param failOver A goMaster call may not be related to a failOver attempt. true if this call is related to a fail over
	 */
	protected void notifyHomesOfMasterStatus(final boolean toMaster, final boolean failOver)
	{
	    logStatus(toMaster ? Status.STATUS_GOING_MASTER.toString() : Status.STATUS_GOING_SLAVE.toString());

        if (toMaster)
            setState(Status.STATUS_GOING_MASTER.getState());
        else 
            setState(Status.STATUS_GOING_SLAVE.getState());
        
		Enumeration enumerator = HomeFactory.getInstance().getHomes();
		ArrayList homes = new ArrayList();
		while(enumerator.hasMoreElements())
		{
			BOHome home = (BOHome)enumerator.nextElement();
			if(toMaster)
			{
				try
				{
					if (frameworkSetup.instrumentStartup())
					{
						frameworkSetup.createInstrumentor("GoMaster", home.getName());
						frameworkSetup.preProcess("GoMaster", home.getName());
					}
					getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "goMaster","BOHome " + home.getName() + " goMaster(" + failOver + ')');
					home.goMaster(failOver);
                    getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "goMaster","BOHome " + home.getName() + " goMaster(" + failOver + ") completed");
					if (frameworkSetup.instrumentStartup())
					{
						frameworkSetup.postProcess("GoMaster", home.getName(), false);
					}
				}
				catch(ThreadDeath td) { throw td; } //Pass this error on
                catch (Exception e)
                {
					Log.exception("Caught Exception in FF goMaster for home(" + home.getName() + ").", e);
					if ( e instanceof FatalFoundationFrameworkException ) {
						FatalFoundationFrameworkException ffe = (FatalFoundationFrameworkException)e;
						if (ffe.getException() != null)
						{
							Log.exception("Original exception:",ffe.getException());
							ffe.getException().printStackTrace();
						}
						ffe.printStackTrace();
	                    abortFramework();
					}
					// If its not a FatalFFException, move on.
                }
                catch( Error e ) {
                	// Log doesn't handle anything but Exception.  Use Logger.
                	Logger.sysAlarm("Caught Error in FF goMaster for home(" + home.getName() + ").", e );
                	abortFramework();
                }
			}
			else
			{
				homes.add(home);
			}
		}
		if(! toMaster)
		{
			//Go slave in the reverse order - List should be empty
			for(int i = homes.size() - 1; i >= 0 ; i--) {
				BOHome home = (BOHome)homes.get(i); // So it can be used in catch block below.
				try
				{
					if (frameworkSetup.instrumentStartup())
					{
						frameworkSetup.createInstrumentor("GoSlave", home.getName());
						frameworkSetup.preProcess("GoSlave", home.getName());
					}

					getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "goSlave","BOHome "+ home.getName()+ ".goSlave()");
					home.goSlave();

					if (frameworkSetup.instrumentStartup())
					{
						frameworkSetup.postProcess("GoSlave", home.getName(), false);
					}
				}
				catch(ThreadDeath td) { throw td; } //Pass this error on
				catch (RuntimeException e)
				{
					Log.exception("Caught Exception in FF goSlave for home(" + home.getName() + ").", e);
					if ( e instanceof FatalFoundationFrameworkException ) {
						FatalFoundationFrameworkException ffe = (FatalFoundationFrameworkException)e;
						if (ffe.getException() != null)
						{
							Log.exception("Original exception:",ffe.getException());
							ffe.getException().printStackTrace();
						}
					}
					e.printStackTrace();
					if ( e instanceof FatalFoundationFrameworkException || abortGoSlaveOnRuntimeOrError ) {
						abortFramework();
					}
				}
				catch( Error e ) {
					// Log doesn't handle throwable.  Use Logger, at least.
					Logger.sysAlarm( "Caught Error in FF goSlave for home(" + home.getName() + ").", e );
					if ( abortGoSlaveOnRuntimeOrError ) {
						abortFramework();
					}
				}
			}
		}
		if (!toMaster) 
		{
			// ('to master' status logging is done in afterHomesToMaster(), after the orb is activated.)
			logStatus(Status.STATUS_SLAVE.toString());
            setState(Status.STATUS_SLAVE.getState());
		}
	}
	/**
	 *  Called via the CommandCallbackService as a result of a callback or SMA invocation
	 */
	public void goSlave()
	{
		if(! started)
		{
			goSlave(skipTheHomes); //homes by default go to slave, no need to notify them
			startupProcess();
		}
		else
		if( isMaster()) //No need to do anything if we aren't a master
		{
			goSlave(tellTheHomes);
		}
	}
	/**
	 * Gracefully shutdown and become a slave server.
	 * @param notifyHomes A true value will result in Home.goSlave calls.
	 */
	public synchronized void goSlave(boolean notifyHomes)
	{
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"goSlave", "FoundationFramework starting transition to slave.");


		if(notifyHomes)
		{
			notifyHomesOfMasterStatus(false, false);
		}
		getOrbService().goSlave();

		getPersistenceService().goSlave();
		if(isMasterValue != null) //A null values indicates a start sequence goSlave call. Value will be set later
		{
			isMasterValue = new Boolean(false);
		}

        setState(Status.STATUS_SLAVE.getState());
        getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification, "goSlave","Status of FF is: " +getStateAsString() );

		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"goSlave", "FoundationFramework goSlave completed.");

	}
	/**
     * Is this Framework instance a master?
     * Until a startup sequence is complete and the goMaster call is made, this will return false.
     * This implies that a Framework instance is neither in a Master or Slave state until startup
     * sequence completes
     */
    public boolean isMaster()
    {
    	return isMasterValue != null && isMasterValue.booleanValue();
    }
	/**
	 * Is this Framework instance a slave?
	 * Until a startup sequence is complete or if it is in a Master state, this will return false.
	 * This implies that a Framework instance is neither in a Master or Slave state until startup
	 * sequence completes
	 */
	public boolean isSlave()
	{
		return isMasterValue != null && (! isMasterValue.booleanValue());
	}
	
	/**
	 * This method will check to see if the framework is currently in slave mode AND was
	 * intended to be a slave in the first place.  This indicates that initialization is 
	 * complete and it has completed going slave.
	 * 
	 * @return boolean - true indicates the app is slave
	 */
	public boolean isValidSlave()
    {
        return isMasterValue != null && (! isMasterValue.booleanValue()) && (! autoMaster );
    }
	
	protected synchronized void initStatusLog()
	{
		if (statusFileName != null)
		{
			try
			{
				statusWriter = new FileWriter(statusFileName, true/*append*/);
			}
			catch (IOException ex)
			{
				System.out.println("Failed to initialize FF status writer for file '" + statusFileName + "': " + ex);
				System.err.println("Failed to initialize FF status writer for file '" + statusFileName + "'");
				ex.printStackTrace();
			}
		}
	}

	protected synchronized void closeStatusLog()
	{
		if (statusWriter != null)
		{
			try
			{
				statusWriter.close();
			}
			catch (IOException ex)
			{
				System.out.println("Failed to close FF status writer for file '" + statusFileName + "': " + ex);
				System.err.println("Failed to close FF status writer for file '" + statusFileName + "'");
				ex.printStackTrace();
			}
			statusWriter = null;
		}
	}

	protected synchronized void logStatus(String status)
	{
		if (statusWriter != null)
		{
			try
			{
				statusWriter.write(status);
				SimpleDateFormat dateFmt = new SimpleDateFormat("EEE yyyy-MM-dd HH:mm:ss");
				statusWriter.write(dateFmt.format(new java.util.Date()));
				statusWriter.write("\n");
				statusWriter.flush(); // (we want this in the file ASAP)
			}
			catch (IOException ex)
			{
				System.out.println("Failed to write FF status '" + status + "' to file '" + statusFileName + "': " + ex);
				System.err.println("Failed to write FF status '" + status + "' to file '" + statusFileName + "'");
				ex.printStackTrace();
			}
		}
	}	
	
	public synchronized void disableSpecificPOAOnMasterOnFailOver(String commandLine){
		String parentPOA="RootPOA";		
		ArrayList<String> commandLineTokens=parseCommandLine(commandLine,",");	
		HashMap<String,POA> childPOAMap =buildPOACollection(parentPOA);
		HashMap<String,POA> specificChildPOAMap=new HashMap();
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"disableSpecificPOAOnMasterOnFailOver", "FoundationFramework disabling Specific POA on Master up on FailOver .");
	
		childPOAMap.remove("CommandConsole");
	    childPOAMap.remove("AdminServicePOA");
		Set<String> poaNamesSet=(Set)childPOAMap.keySet();
	
		if(null!=commandLineTokens && commandLineTokens.size()>0){
		   	  Iterator<String > inclusionsIterator=commandLineTokens.iterator();
		   	  while(inclusionsIterator.hasNext()){
		   		  Pattern pattern=Pattern.compile(inclusionsIterator.next(),Pattern.CASE_INSENSITIVE);
		   		  Iterator poaNamesIterator=poaNamesSet.iterator();
		   		  while(poaNamesIterator.hasNext()){
		   			  String childPOAName=(String)poaNamesIterator.next();
		   			  Matcher matcher = pattern.matcher(childPOAName);
		   			  if(matcher.find() && (matcher.start()!=matcher.end())){
		   				specificChildPOAMap.put(childPOAName,childPOAMap.get(childPOAName));
		   			  }   				  		   			  		   				  
		   	  	 }
		   	 }
		}
		changePOAState(specificChildPOAMap,State._ACTIVE,State._DISCARDING);
		isDiscardingMasterValue=new Boolean(true);		
	}
	
	public synchronized void disableMasterOnFailOver(String commandLine){
		String parentPOA="RootPOA";
		commandLine=commandLine+","+"CommandConsole"+","+"AdminServicePOA";
		ArrayList<String> commandLineTokens=parseCommandLine(commandLine,",");	
		HashMap<String,POA> childPOAMap =buildPOACollection(parentPOA);
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"disableMasterOnFailOver", "FoundationFramework disabling Master up on FailOver .");
	
		Set<String> poaNamesSet=(Set)childPOAMap.keySet(); 
		if(null!=commandLineTokens && commandLineTokens.size()>0){
		   	  Iterator<String > exclusionsIterator=commandLineTokens.iterator();
		   	  while(exclusionsIterator.hasNext()){
		   		  Pattern pattern=Pattern.compile(exclusionsIterator.next(),Pattern.CASE_INSENSITIVE);
		   		  Iterator poaNamesIterator=poaNamesSet.iterator();
		   		  while(poaNamesIterator.hasNext()){
		   			  String childPOAName=(String)poaNamesIterator.next();
		   			  Matcher matcher = pattern.matcher(childPOAName);
		   			  if(matcher.find() && (matcher.start()!=matcher.end())){
		   				  poaNamesIterator.remove();
		   			  }   				  		   			  		   				  
		   	  	 }
		   	 }
		}
		changePOAState(childPOAMap,State._ACTIVE,State._DISCARDING);
		isDiscardingMasterValue=new Boolean(true);
	}
	
	public synchronized void prepareSlaveOnFailOver(String commandLine){
		String parentPOA="RootPOA";
		commandLine=commandLine+","+"UserPOA";
		ArrayList<String> commandLineTokens=parseCommandLine(commandLine,",");	
		HashMap<String,POA> childPOAMap =buildPOACollection(parentPOA);
		HashMap<String,POA> specificChildPOAMap=new HashMap();
		getDefaultLogService().log(MsgPriority.low, MsgCategory.systemNotification,"prepareSlaveOnFailOver", "FoundationFramework preparing  Slave up on FailOver .");
		Set<String> poaNamesSet=(Set)childPOAMap.keySet(); 
		if(null!=commandLineTokens && commandLineTokens.size()>0){
		   	  Iterator<String > exclusionsIterator=commandLineTokens.iterator();
		   	  while(exclusionsIterator.hasNext()){
		   		  Pattern pattern=Pattern.compile(exclusionsIterator.next(),Pattern.CASE_INSENSITIVE);
		   		  Iterator poaNamesIterator=poaNamesSet.iterator();
		   		  while(poaNamesIterator.hasNext()){
		   			  String childPOAName=(String)poaNamesIterator.next();
		   			  Matcher matcher = pattern.matcher(childPOAName);
		   			  if(matcher.find() && (matcher.start()!=matcher.end())){
		   				specificChildPOAMap.put(childPOAName,childPOAMap.get(childPOAName));
		   			  }   				  		   			  		   				  
		   	  	 }
		   	 }
		}
		changePOAState(specificChildPOAMap,State._DISCARDING,State._HOLDING);	
	}
	
	private ArrayList parseCommandLine(String commandLineString,String delimiter) {
		//commandLineString can be  comma separated list of POA names or wild cards
		ArrayList<String> commandLineArguments=new ArrayList<String>();
		if (null != commandLineString && commandLineString.length()>0){		
			StringTokenizer stringTokenizer=new StringTokenizer(commandLineString, delimiter, false);
			while (stringTokenizer.hasMoreTokens()){				
				commandLineArguments.add(stringTokenizer.nextToken().trim());
			}
		}
		return commandLineArguments;
	}
	
	private HashMap buildPOACollection(String parentPOA) {
		HashMap<String,POA> childPOAMap =new HashMap<String,POA>();
		try{
			POA poa=(POA)getOrbService().getPOA(parentPOA);
		    if(poa!=null){
			   POA [] children =(POA []) poa.the_children();
			   for(int i=0;i<children.length;i++){
			   	  POA child=(POA)children[i];			    	  
				  childPOAMap.put(child.the_name(),child);
				  if((child.the_children()).length>0){
			  		  POA [] secondDescendents =(POA []) child.the_children();
			   		  for(int j=0;j<secondDescendents.length;j++){			    			  
				    	  POA innerchild=(POA)secondDescendents[j];
				    	  childPOAMap.put(child.the_name()+""+innerchild.the_name(),innerchild);						    	 
					  }		     			    	 
			      }
			   }
			}
		}catch(NoSuchPOAException e){
			Log.exception("Caught Exception while resolving the  the POA .", e);		
			e.printStackTrace();
		}
	    return childPOAMap;
	}
	
	private void changePOAState(HashMap childPOAMap, int currentState, int newState) {
		try{
			Collection childPOASet=(Collection)childPOAMap.values();
			Iterator childPOAIterator=childPOASet.iterator();
			while (childPOAIterator.hasNext()){
			  POA childPOA =(POA)childPOAIterator.next();
		   	  POAManager pm = (POAManager)childPOA.the_POAManager();		
			  if((currentState == pm.get_state().value()) && (State._DISCARDING==newState )){  
				  //set to  State._DISCARDING     :
				  pm.discard_requests( false );
			  } else if ((currentState == pm.get_state().value()) && (State._HOLDING==newState )){
				  //set to  State._HOLDING     :
				  pm.hold_requests( false );
			  }
		   } 
		}catch(AdapterInactive e){
			Log.exception("Caught Exception while changing the POA state .", e);
			e.printStackTrace();
		}
	}
	
	public boolean isDiscardingMaster()
    {
    	return isDiscardingMasterValue != null && isDiscardingMasterValue.booleanValue();
    }
}
