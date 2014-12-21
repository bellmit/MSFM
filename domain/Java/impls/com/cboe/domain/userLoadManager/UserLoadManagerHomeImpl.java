package com.cboe.domain.userLoadManager;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.domain.userLoadManager.UserLoadManager;
import com.cboe.interfaces.domain.userLoadManager.UserLoadManagerHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class UserLoadManagerHomeImpl extends ClientBOHome implements UserLoadManagerHome
{	
	private UserLoadManagerImpl  userLoadManagerImpl 	= null;	
	
	/*
	 * Dynamic ThreadPool feature members
	 */

	private final static String CASQUOTE_MAXIMUM_POOLSIZE = "casQuoteMaximumPoolSize";
	private final static String CASORDERENTRY_MAXIMUM_POOLSIZE = "casOrderEntryMaximumPoolSize";
	private final static String CASLIGHTORDERENTRY_MAXIMUM_POOLSIZE = "casLightOrderEntryMaximumPoolSize";
	private final static String POA_POOL_SIZE_GROWTH_FACTOR = "poaPoolSizeGrowthFactor";
	private final static String BASE_TOTAL_QUOTE_LOAD = "baseTotalQuoteLoad";
	private final static String BASE_TOTAL_ORDER_LOAD = "baseTotalOrderLoad";
	private final static String BASE_TOTAL_LIGHTORDER_LOAD = "baseTotalLightOrderLoad";		
	
	private int poaPoolSizeGrowthFactor = 10;
	protected static int casQuoteMaximumPoolSize;
	protected static int casOrderEntryMaximumPoolSize;
	protected static int casLightOrderEntryMaximumPoolSize;		
	protected static int baseTotalQuoteLoad;
	protected static int baseTotalOrderLoad;
	protected static int baseTotalLightOrderLoad;
	
	/*
	 * Dynamic ConnectionPool feature members
	 */
	private final static String MAXIMUM_CONNECTIONPOOLSIZE = "maximumConnectionPoolSize";
	private final static String MINIMUM_CONNECTIONPOOLSIZE = "minimumConnectionPoolSize";
	private final static String CONNECTIONPOOL_GROWTHFACTOR = "connectionPoolGowthFactor";
	private final static String EVALUATOR_COREPOOLSIZE = "evaluatorCorePoolSize";
	private final static String EVALUATOR_STARTTIME = "evaluatorStartTime";
	private final static String EVALUATOR_DELAY = "evaluatorDelay";
	private final static String FILEWRITER_COREPOOLSIZE = "fileWriterCorePoolSize";
	private final static String FILEWRITER_STARTTIME = "fileWriterStartTime";
	private final static String FILEWRITER_DELAY = "fileWriterDelay";	
	private final static String HISTORY_FILE_NAME = "historyFileName";
	private final static String HISTORY_FILE_LOCATION = "historyFileLocation";
	
	private int maximumConnectionPoolSize = 40;
	private int minimumConnectionPoolSize = 20;
	private int connectionPoolGowthFactor = 10;	
	protected static int evaluatorCorePoolSize;	
	protected static int evaluatorDelay;
	protected static int fileWriterCorePoolSize;	
	protected static int fileWriterDelay;
	private String historyFile = "@";
	protected static String evaluatorStartTime;
	protected static String fileWriterStartTime;
	
	private boolean successfullyDefinedThreadPoolProperties;
	private boolean successfullyDefinedConnectionPoolProperties;
		
	public UserLoadManagerHomeImpl()
	{
		super();
	}
	
	/**
    * Creates an instance of the UserLoadManager
    */
	public UserLoadManager create()
	{
		if ( userLoadManagerImpl == null )
		{
			try
			{			
				casQuoteMaximumPoolSize = Integer.parseInt(getProperty(CASQUOTE_MAXIMUM_POOLSIZE));
				casOrderEntryMaximumPoolSize = Integer.parseInt(getProperty(CASORDERENTRY_MAXIMUM_POOLSIZE));
				casLightOrderEntryMaximumPoolSize = Integer.parseInt(getProperty(CASLIGHTORDERENTRY_MAXIMUM_POOLSIZE));	
				poaPoolSizeGrowthFactor = Integer.parseInt(getProperty(POA_POOL_SIZE_GROWTH_FACTOR));
				baseTotalQuoteLoad = Integer.parseInt(getProperty(BASE_TOTAL_QUOTE_LOAD));
				baseTotalOrderLoad = Integer.parseInt(getProperty(BASE_TOTAL_ORDER_LOAD));
				baseTotalLightOrderLoad = Integer.parseInt(getProperty(BASE_TOTAL_LIGHTORDER_LOAD));
				
				successfullyDefinedThreadPoolProperties = true;
				
				if (Log.isDebugOn())
		        {
					Log.debug(this, "successfullyDefinedThreadPoolProperties = " + successfullyDefinedThreadPoolProperties);
					Log.debug(this, "casQuoteMaximumPoolSize = " + casQuoteMaximumPoolSize);
					Log.debug(this, "casOrderEntryMaximumPoolSize = " + casOrderEntryMaximumPoolSize);
					Log.debug(this, "casLightOrderEntryMaximumPoolSize = " + casLightOrderEntryMaximumPoolSize);
					Log.debug(this, "poaPoolSizeGrowthFactor = " + poaPoolSizeGrowthFactor);
					Log.debug(this, "baseTotalQuoteLoad = " + baseTotalQuoteLoad);
					Log.debug(this, "baseTotalOrderLoad = " + baseTotalOrderLoad);
					Log.debug(this, "baseTotalLightOrderLoad = " + baseTotalLightOrderLoad);		
		        }
			}		
			catch(Exception e)
			{
				successfullyDefinedThreadPoolProperties = false;							
				Log.exception(this, "Failed to get POAThreadPool properties, setting POAThreadPool ULMflags to false", e);
			}
			
			try
			{
				maximumConnectionPoolSize = Integer.parseInt(getProperty(MAXIMUM_CONNECTIONPOOLSIZE));
				minimumConnectionPoolSize = Integer.parseInt(getProperty(MINIMUM_CONNECTIONPOOLSIZE));
				connectionPoolGowthFactor = Integer.parseInt(getProperty(CONNECTIONPOOL_GROWTHFACTOR));
				evaluatorCorePoolSize = Integer.parseInt(getProperty(EVALUATOR_COREPOOLSIZE));
				evaluatorStartTime = getProperty(EVALUATOR_STARTTIME);
				evaluatorDelay = Integer.parseInt(getProperty(EVALUATOR_DELAY));
				fileWriterCorePoolSize = Integer.parseInt(getProperty(FILEWRITER_COREPOOLSIZE));
				fileWriterStartTime = getProperty(FILEWRITER_STARTTIME);
				fileWriterDelay = Integer.parseInt(getProperty(FILEWRITER_DELAY));			
				historyFile = getProperty(HISTORY_FILE_LOCATION) + getProperty(HISTORY_FILE_NAME);
				
				successfullyDefinedConnectionPoolProperties = true;
				
				if (Log.isDebugOn())
		        {	
					Log.debug(this, "successfullyDefinedConnectionPoolProperties = " + successfullyDefinedConnectionPoolProperties);
					Log.debug(this, "maximumConnectionPoolSize = " + maximumConnectionPoolSize);
					Log.debug(this, "minimumConnectionPoolSize = " + minimumConnectionPoolSize);
					Log.debug(this, "connectionPoolGowthFactor = " + connectionPoolGowthFactor);
					Log.debug(this, "evaluatorCorePoolSize = " + evaluatorCorePoolSize);
					Log.debug(this, "evaluatorStartTime = " + evaluatorStartTime);
					Log.debug(this, "evaluatorDelay = " + evaluatorDelay);
					Log.debug(this, "fileWriterCorePoolSize = " + fileWriterCorePoolSize);
					Log.debug(this, "fileWriterStartTime = " + fileWriterStartTime);
					Log.debug(this, "fileWriterDelay = " + fileWriterDelay);
					Log.debug(this, "historyFile = " + historyFile);						
		        }
			}
			catch(Exception e)
			{	
				successfullyDefinedConnectionPoolProperties = false;				
				Log.exception(this, "Fail to get ConnectionPool properties, setting ConnectionPool ULMflags to false", e);
			}			
			
			Log.information(this, "Creating UserLoadManagerImpl");		
			
			userLoadManagerImpl = new UserLoadManagerImpl(poaPoolSizeGrowthFactor, minimumConnectionPoolSize, 
														maximumConnectionPoolSize, connectionPoolGowthFactor, historyFile);			
			
			if(!successfullyDefinedThreadPoolProperties)
			{
				userLoadManagerImpl.setUseManagedThreadPools(false);	
				userLoadManagerImpl.setAllowDynamicThreadPoolGrowth(false);	
			}
			
			if(!successfullyDefinedConnectionPoolProperties)
			{
				userLoadManagerImpl.setUseManagedConnectionPools(false);				
				userLoadManagerImpl.setAllowDynamicConnectionPoolGrowth(false);	
			}
			
			// Every BObject must be added to the container.
			addToContainer(userLoadManagerImpl);	
			
			//Every BObject create MUST have a name...if the object is to be a managed object.
			userLoadManagerImpl.create("UserLoadManagerImpl");			
		}
		
		return userLoadManagerImpl;		
	}
		
	public UserLoadManager find()
	{		
		return create();
	}
	
	public void clientStart()
	{
		create();
		if (userLoadManagerImpl != null) 
		{
			if (userLoadManagerImpl.getUseManagedThreadPools()) 
			{
				userLoadManagerImpl.configureManagedThreadPools();
			}

			if (userLoadManagerImpl.getUseManagedConnectionPools()) 
			{
				userLoadManagerImpl.startScheduledThreadPoolExecutors();
			}			
			
			Log.information(this, "UserLoadManagerImpl setup was Successful");
		}
	}	
	
	public void clientInitialize()
        throws Exception
    {	
		if (Log.isDebugOn())
        {			
            Log.debug(this, "SMA Type = " + this.getSmaType());
        }		
		// Add AR command
		try{
	    	   registerCommand(this, "refreshUserLoad", "refreshUserLoadArCallback","Recalculate CAS's users load",
	               new String[] { String.class.getName()},
	               new String[] { "ignored"});
	    	   registerCommand(this, "userLoadStatus", "userLoadStatusArCallback","Show CAS's users load",
		               new String[] { String.class.getName()},
		               new String[] { "ignored"});
	    	   // user load manager flag
	    	   registerCommand(this, "ulmFlag", "ulmFlagArCallback",
	    			   "Turn user laod manager flags to true/false.",
		               new String[] { String.class.getName(),String.class.getName()},
		               new String[] { "Flag type:" 
	    			   				 +"\n"
	    			   				 +"           1=useManagedThreadPools" 
	    			   				 +"\n"
	    			   				 +"           2=allowDynamicThreadPoolGrowth" 
	    			   				 +"\n"
	    			   				 +"           3=useManagedConnectionPools"
	    			   				 +"\n"
	    			   				 +"           4=allowDynamicConnectionPoolGrowth ",
		               		"Flag Value: true/false"});
	       }catch (Exception e)
	       {
	           Log.exception(this, "Cannot register ar command. Ignoring exception", e);
	       }
    }

	public void clientShutdown()
	{
		
	}  
	
	// AR command
	public String refreshUserLoadArCallback(String input)
	{
		if (Log.isDebugOn()) {
			Log.debug(this,"UserLoadManagerHomeImpl: call refreshUserLoadArCallback()");
		}
		
		if (userLoadManagerImpl != null) {
			return userLoadManagerImpl.refreshLoad();
		}
		
		return "Command failed, userLoadManagerImpl is not ready";
	}

	// AR command
	public String userLoadStatusArCallback(String input)
	{
		if(userLoadManagerImpl != null)
			return userLoadManagerImpl.userLoadStatusArCallback();
		
		return "Command failed, userLoadManagerImpl is not ready";
	}
	
	// AR command
	public String ulmFlagArCallback(String flagType, String flagValue)
	{
		if (Log.isDebugOn()) {
			Log.debug(this,"UserLoadManagerHomeImpl: call ulmFlag()");
		}
		
		if(userLoadManagerImpl != null)
			return userLoadManagerImpl.ulmFlagArCallbackArCallback(flagType,flagValue);
		
		return "Command failed, userLoadManagerImpl is not ready";
	}	
}