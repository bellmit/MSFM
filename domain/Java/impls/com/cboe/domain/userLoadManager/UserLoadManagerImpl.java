package com.cboe.domain.userLoadManager;

import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.idl.infrastructureServices.sessionManagementService.UserLoginStruct;
import com.cboe.interfaces.domain.RateMonitor;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureUtility.configuration.ThreadPoolController;
import com.cboe.infrastructureUtility.configuration.ThreadPoolIdentity;
import com.cboe.infrastructureUtility.configuration.ConnectionIdentity;
import com.cboe.infrastructureUtility.configuration.ConnectionPoolController;
import com.cboe.infrastructureUtility.configuration.ConnectionMetric;
import com.cboe.infrastructureUtility.configuration.Rating;
import com.cboe.infrastructureUtility.configuration.ConfigurationException;
import com.cboe.infrastructureUtility.configuration.ThreadPoolMetric;
import com.cboe.interfaces.domain.userLoadManager.UserLoadManager;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Calendar;

/**
 *  3/11/2011
 * This class is responsible for 2 features:
 * 1. To keep separate aggregates of the Rate limits for certain types and for all Users logged into the CAS. The rate limit types that are aggregated are 
 *    ACCEPT_QUOTE, ACCEPT_ORDER, and ACCEPT_LIGHT_ORDER. Using these aggregates as a gauge, CAS will dynamically increase the ThreadPool Size for 
 *    CASQuote POA, CASOrderEntry POA, and CASLightOrderEntry POA.
 * 
 * 2. Periodically evaluate ConnectionPool metrics for all serviceReferences and dynamically increase the ConnectionPoolSizes accordingly.
 * 	  Periodically write to a ConnectionPoolHistory file that saves these metrics. 
 * 	  The ConnectionPoolHistory file will be read upon startup to determine the starting ConnectionPool Size for each serviceReference.
 */
public class UserLoadManagerImpl extends BObject implements UserLoadManager
{	
	/**
	 * Dynamic POA ThreadPool feature class members
	 */
	//private static HashMap<String, RateManager> usersOrderRateManager;
	private static HashMap<String, HashMap<RateMonitorKeyContainer,RateMonitor>> userToRateMonitorKeyMap;	
	private static HashMap<String, ThreadPoolFactorsStruct> poaNameToThreadPoolFactorsMap;	
	private static int totalOrderLoad = 0;
	private static int totalQuoteLoad = 0;
	private static int totalLightOrderLoad = 0;
	private static int INITIAL_USER_RATE_MONITORS_SIZE = 6;	
	
	private long casQuoteTotalCalls = 0;
	private long casOrderEntryTotalCalls = 0;
	private long casLightOrderTotalCalls = 0;
		
	/**
	 * Dynamic ConnectionPool feature class members
	 */
	private ScheduledThreadPoolExecutor evaluatorExecutor;	
	private ScheduledThreadPoolExecutor historyFileWriterExecutor;	
	private static HashMap<String, ConnectionPoolStruct> svcRouteKeyToConnectionPoolStructMap;
	private static HashMap<String, ConnectionHistoryStruct> svcRouteKeyToConnectionHistoryStructMap; 
		
	private static boolean useConnectionPoolHistory;	
	private static boolean initialConnectionMetric = true;
	
	private final static String CAS_QUOTE = "CASQuote";
	private final static String CAS_ORDER_ENTRY = "CASOrderEntry";
	private final static String CAS_LIGHT_ORDER_ENTRY = "CASLightOrderEntry";	
	private final static String HISTORY_FILE_HEADER = "svcRouteKey,availableConnections,activeConnectionHighWaterMark,totalCalls,starvedCount,busyCount";
	
	private final static int DEFAULT_INITIAL_EVALUATOR_DELAY = 300; //seconds
	private final static int DEFAULT_INITIAL_FILEWRITER_DELAY = 600; //seconds
	
	private final static String FLAG_DEFAULT = "false";
	private final static String USE_MANAGED_THREADPOOLS = "ORB.POA.CASQuote.UseManagedThreadPool"; //using only one Managed POA -D setting because all the Managed POAs are controlled with the same flag. 
	private final static String ALLOW_DYNAMIC_THREADPOOL_GROWTH = "AllowDynamicThreadPoolGrowth";	
	private boolean useManagedThreadPools; //if true:  managed thread pools are configured.
	private boolean allowDynamicThreadPoolGrowth;	//if true: thread pools grow dynamically, if false: all functionality occurs except the thread pools do not grow.
	
	private final static String USE_MANAGED_CONN_POOLS = "ORB.IIOPTransport.UseManagedConnPools";
	private final static String ALLOW_DYNAMIC_CONNECTIONPOOL_GROWTH = "AllowDynamicConnectionPoolGrowth";	
	private final static String USE_USERLOADMANAGER_STARTTIMES = "UseUserLoadManagerStartTimes";
	private boolean useManagedConnectionPools;//if true:  startScheduledThreadPoolExecutors. if false: do not startScheduledThreadPoolExecutors
	private boolean allowDynamicConnectionPoolGrowth;//if true: connection pools grow dynamically. if false: all functionality occurs except the connection pools do not grow.
	private boolean useUserLoadManagerStartTimes;//if false: connection pools evaluator will start using default initial delays, ignoring UserLoadManagerHomeImpl.evaluatorStartTime & UserLoadManagerHomeImpl.fileWriterStartTime 
	
	private int poaPoolSizeGrowthFactor;	
	private int minimumConnectionPoolSize;
	private int maximumConnectionPoolSize;
	private int connectionPoolGowthFactor;
	private String historyFile;
	
	/**
	 * UserLoadManagerImpl constructor
	 */
	public UserLoadManagerImpl(int poaPoolSizeGrowthFactor, int minimumConnectionPoolSize, int maximumConnectionPoolSize, int connectionPoolGowthFactor, String historyFile)
	{
		super();
		
		this.poaPoolSizeGrowthFactor = poaPoolSizeGrowthFactor;
		this.minimumConnectionPoolSize = minimumConnectionPoolSize;
		this.maximumConnectionPoolSize = maximumConnectionPoolSize;
		this.connectionPoolGowthFactor = connectionPoolGowthFactor;
		this.historyFile = historyFile;
		
		userToRateMonitorKeyMap = new HashMap<String, HashMap<RateMonitorKeyContainer,RateMonitor>> (INITIAL_USER_RATE_MONITORS_SIZE);
		poaNameToThreadPoolFactorsMap = new HashMap<String, ThreadPoolFactorsStruct> ();
		svcRouteKeyToConnectionPoolStructMap = new HashMap<String, ConnectionPoolStruct> ();
		svcRouteKeyToConnectionHistoryStructMap = new HashMap<String, ConnectionHistoryStruct> ();			
		
		//set up flags
		useManagedThreadPools = Boolean.parseBoolean(System.getProperty(USE_MANAGED_THREADPOOLS,FLAG_DEFAULT));
		useManagedConnectionPools = Boolean.parseBoolean(System.getProperty(USE_MANAGED_CONN_POOLS,FLAG_DEFAULT));
		allowDynamicThreadPoolGrowth = Boolean.parseBoolean(System.getProperty(ALLOW_DYNAMIC_THREADPOOL_GROWTH,FLAG_DEFAULT));			
		allowDynamicConnectionPoolGrowth = Boolean.parseBoolean(System.getProperty(ALLOW_DYNAMIC_CONNECTIONPOOL_GROWTH,FLAG_DEFAULT));
		useUserLoadManagerStartTimes = Boolean.parseBoolean(System.getProperty(USE_USERLOADMANAGER_STARTTIMES,"true"));		
		
		if (Log.isDebugOn())
        {				
			Log.debug("UserLoadManagerImpl useManagedThreadPools = " + useManagedThreadPools);
			Log.debug("UserLoadManagerImpl useManagedConnectionPools = " + useManagedConnectionPools);
			Log.debug("UserLoadManagerImpl allowDynamicThreadPoolGrowth = " + allowDynamicThreadPoolGrowth);
			Log.debug("UserLoadManagerImpl allowDynamicConnectionPoolGrowth = " + allowDynamicConnectionPoolGrowth);
			Log.debug("UserLoadManagerImpl useUserLoadManagerStartTimes = " + useUserLoadManagerStartTimes);
        }		
	}	
	
	public void create(String name)
	{
		super.create(name);		
		
		if(!useManagedThreadPools)
		{
			allowDynamicThreadPoolGrowth = false;
		}
		
		if(useManagedConnectionPools)
		{
			useConnectionPoolHistory = readConnectionPoolHistory();				
		}
		else
		{
			allowDynamicConnectionPoolGrowth = false;
		}	
	}
	
	/**
	 * configureManagedThreadPools is and should be called only once at startup.
	 * This method gets the set of Managed Thread Pools, builds the FF configuration, 
	 * builds the ThreadPoolControllers and calls buildThreadPoolFactorsMap to store the controllers.
	 */
	protected void configureManagedThreadPools()
	{		 
		 FoundationFramework foundationFramework = FoundationFramework.getInstance();
		 Set<ThreadPoolIdentity> ids = foundationFramework.getCustomConfigurationFacade().getManagedThreadPoolIdentifiers();	
		 if (Log.isDebugOn())
		 {
			 Log.debug(this, "ThreadPoolIdentity Set size=" + ids.size());		
		 }
		 
		 Iterator<ThreadPoolIdentity> threadPoolIdentityIterator = ids.iterator();			
		 while (threadPoolIdentityIterator.hasNext())
		 {
			 ThreadPoolIdentity threadPoolIdentity = threadPoolIdentityIterator.next();
			 String threadPoolIdentityName = threadPoolIdentity.getName();					 
			 if (Log.isDebugOn())
			 {
				 Log.debug(this, "ThreadPoolIdentity id, id.getName() = " +  threadPoolIdentityName);
			 }		 
			 
			 ThreadPoolController controller = foundationFramework.getCustomConfigurationFacade().createThreadPoolControllerBuilder(threadPoolIdentity).build();			 
			 buildThreadPoolFactorsMap(threadPoolIdentityName, controller);					 
		 }	 
	}
	
	/**
	 * buildThreadPoolFactorsMap is & should be called only once at startup.
	 * Given the beginningPoolSize and POAName, This method calculates the ratePerThread which is used as a 
	 * part of the calculation to determine if the POA ThreadPool Size should increase
	 * It then stores builds a ThreadPoolFactorsStruct that contains the needed data and stores it into a map
	 */
	private void buildThreadPoolFactorsMap(String POAName, ThreadPoolController controller)
	{		
		int beginningPoolSize = controller.getPerformanceMemento().getTotalThreads();		
		
		if ( beginningPoolSize > 0 )
		{
			if (POAName.equals(CAS_QUOTE))
			 {
				
				 float ratePerThread = (UserLoadManagerHomeImpl.baseTotalQuoteLoad/beginningPoolSize);				 
				 ThreadPoolFactorsStruct threadPoolFactorsStruct = new ThreadPoolFactorsStruct(controller, ratePerThread, UserLoadManagerHomeImpl.baseTotalQuoteLoad, UserLoadManagerHomeImpl.casQuoteMaximumPoolSize);
				 poaNameToThreadPoolFactorsMap.put(POAName, threadPoolFactorsStruct);
				 
				 if (Log.isDebugOn())
				 {
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".beginningPoolSize=" + beginningPoolSize);
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".ratePerThread=" + poaNameToThreadPoolFactorsMap.get(POAName).ratePerThread);
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".baseTotalQuoteLoad=" + poaNameToThreadPoolFactorsMap.get(POAName).baseTotalLoad);				
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".casQuoteMaximumPoolSize=" + poaNameToThreadPoolFactorsMap.get(POAName).maximumPoolSize);
				 }
			 }
			 else if (POAName.equals(CAS_ORDER_ENTRY))
			 {
				 float ratePerThread = (UserLoadManagerHomeImpl.baseTotalOrderLoad/beginningPoolSize);
				 ThreadPoolFactorsStruct threadPoolFactorsStruct = new ThreadPoolFactorsStruct(controller, ratePerThread, UserLoadManagerHomeImpl.baseTotalOrderLoad, UserLoadManagerHomeImpl.casOrderEntryMaximumPoolSize);
				 poaNameToThreadPoolFactorsMap.put(POAName, threadPoolFactorsStruct);
				 
				 if (Log.isDebugOn())
				 {
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".beginningPoolSize=" + beginningPoolSize);
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".ratePerThread=" + poaNameToThreadPoolFactorsMap.get(POAName).ratePerThread);
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".baseTotalOrderLoad=" + poaNameToThreadPoolFactorsMap.get(POAName).baseTotalLoad);				 
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".casOrderEntryMaximumPoolSize=" + poaNameToThreadPoolFactorsMap.get(POAName).maximumPoolSize);
				 }
			 }
			 else if (POAName.equals(CAS_LIGHT_ORDER_ENTRY))
			 {
				 float ratePerThread = (UserLoadManagerHomeImpl.baseTotalLightOrderLoad/beginningPoolSize);
				 ThreadPoolFactorsStruct threadPoolFactorsStruct = new ThreadPoolFactorsStruct(controller, ratePerThread, UserLoadManagerHomeImpl.baseTotalLightOrderLoad, UserLoadManagerHomeImpl.casLightOrderEntryMaximumPoolSize);
				 poaNameToThreadPoolFactorsMap.put(POAName, threadPoolFactorsStruct);
				 
				 if (Log.isDebugOn())
				 {
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".beginningPoolSize=" + beginningPoolSize);
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".ratePerThread=" + poaNameToThreadPoolFactorsMap.get(POAName).ratePerThread);
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".baseTotalLightOrderLoad=" + poaNameToThreadPoolFactorsMap.get(POAName).baseTotalLoad);			 
					 Log.debug(this, "initializeThreadPoolGrowthFactors." + POAName + ".casLightOrderEntryMaximumPoolSize=" + poaNameToThreadPoolFactorsMap.get(POAName).maximumPoolSize);
				 }
			 }
			 else
			 {
				 Log.debug(this, "calculateThreadPoolGrowthFactor: No Matching POAName found for" + POAName);
			 }		
		}
		else //else beginning pool size is zero or less than zero
		{
			Log.alarm(this, "WARNING, " + POAName +  " beginningPoolSize is " + beginningPoolSize);			
		}	
	}	
	
	// key is RateMonitorKeyContainer
	public synchronized void addUser(Object key,RateMonitor rateMonitor){
		RateMonitorKeyContainer rateMonitorKeyContainer = (RateMonitorKeyContainer)key;
		String userId = rateMonitorKeyContainer.getUserId();
		String sessionId = rateMonitorKeyContainer.getSession();
		short typeId = rateMonitorKeyContainer.getType();
		
		if(!useManagedThreadPools)
			return;
		if( Log.isDebugOn())
		{					
			Log.debug( this, "UserLoadManagerImpl.addUser(UserId:" + userId +  ").");
		}
		//get user rate limit
		int rateLimit = 0;
		if( rateMonitorKeyContainer.getType() == RateMonitorTypeConstants.ACCEPT_QUOTE){
			rateLimit = getRateLimit(rateMonitor.getWindowSize(),rateMonitor.getWindowMilliSecondPeriod());
			totalQuoteLoad =totalQuoteLoad + rateLimit;
			HashMap<RateMonitorKeyContainer,RateMonitor> userRateMonitorMap = getUserRateMonitorMap(userId);
			userRateMonitorMap.put(rateMonitorKeyContainer,rateMonitor);
			
			if(allowDynamicThreadPoolGrowth)
			{
				requestAdditionalThreads(CAS_QUOTE, totalQuoteLoad);
			}			
		}
		else if( rateMonitorKeyContainer.getType() == RateMonitorTypeConstants.ACCEPT_ORDER){
			//aggregate
			rateLimit = getRateLimit(rateMonitor.getWindowSize(),rateMonitor.getWindowMilliSecondPeriod());
			totalOrderLoad = totalOrderLoad + rateLimit;
			HashMap<RateMonitorKeyContainer,RateMonitor> userRateMonitorMap = getUserRateMonitorMap(userId);
			userRateMonitorMap.put(rateMonitorKeyContainer,rateMonitor);
			
			if(allowDynamicThreadPoolGrowth)
			{
				requestAdditionalThreads(CAS_ORDER_ENTRY, totalOrderLoad);
			}
		}
		else if( rateMonitorKeyContainer.getType() == RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER){
			//aggregate
			rateLimit = getRateLimit(rateMonitor.getWindowSize(),rateMonitor.getWindowMilliSecondPeriod());
			totalLightOrderLoad = totalLightOrderLoad + rateLimit;
			HashMap<RateMonitorKeyContainer,RateMonitor> userRateMonitorMap = getUserRateMonitorMap(userId);
			userRateMonitorMap.put(rateMonitorKeyContainer,rateMonitor);
			
			if(allowDynamicThreadPoolGrowth)
			{
				requestAdditionalThreads(CAS_LIGHT_ORDER_ENTRY, totalLightOrderLoad);
			}
		}
		
		if( Log.isDebugOn())
		{					
			Log.debug( this, "UserId:" + userId+"/Session:"+sessionId+"/Type:"+typeId+" rateLimit is:"+rateLimit
					+"; Quote TotalLoad: "+totalQuoteLoad
					+"; Order TotalLoad: "+totalOrderLoad 
					+"; LightOrder TotalLoad: "+totalLightOrderLoad);
		}		
	}
	
	// remove user load from total when user logged out.
	public synchronized void removeUser(String userId){
		
		if(!useManagedThreadPools)
			return;
		
		if (userToRateMonitorKeyMap.containsKey(userId)){
			HashMap<RateMonitorKeyContainer,RateMonitor> userRateMonitorsMap = getUserRateMonitorMap(userId);
			totalQuoteLoad =totalQuoteLoad - getUserTotalLimit(RateMonitorTypeConstants.ACCEPT_QUOTE,userRateMonitorsMap);
			if(totalQuoteLoad <0)
				totalQuoteLoad =0;
			
			totalOrderLoad =totalOrderLoad - getUserTotalLimit(RateMonitorTypeConstants.ACCEPT_ORDER,userRateMonitorsMap);
			if(totalOrderLoad<0)
				totalOrderLoad =0;
			
			totalLightOrderLoad =totalLightOrderLoad - getUserTotalLimit(RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER,userRateMonitorsMap);
			if(totalLightOrderLoad<0)
				totalLightOrderLoad = 0;
			
			//remove the user from map or we need mark it as inactive.
			userRateMonitorsMap = null;
			userToRateMonitorKeyMap.remove(userId);
			
			if( Log.isDebugOn())
			{					
				Log.debug( this, "removeUser(UserId:" + userId +  "), user removed"
						+"; total Order limit load: "+totalOrderLoad
						+"; total LightOrder limit load: "+totalLightOrderLoad
						+"; total Quote limit load: "+totalQuoteLoad);
			}
		}
		else{
			if( Log.isDebugOn())
			{					
				Log.debug( this, "removeUser(UserId:" + userId +  "), User not exist!");
			}
		}
	}
	
	/* During test, we found somehow the update event comes more than once sometime. So we have to do refresh instead of . 
	 * 
	 */
	// called from RateMonitorImpl
	public synchronized void updateUserLimit(Object rateMonitorKey, int oldWindowSize, long oldWindowPeriod){
		RateMonitorKeyContainer rateMonitorKeyContainer = (RateMonitorKeyContainer)rateMonitorKey;
		String userId = rateMonitorKeyContainer.getUserId();
		short type = rateMonitorKeyContainer.getType();
		int newLimit = 0;
		int diff = 0;
		if(!useManagedThreadPools)
			return;
		if( Log.isDebugOn())
		{					
			Log.debug( this, "updateUserLimit(UserId:" + userId +  ").");
		}
		if (userToRateMonitorKeyMap.containsKey(userId)){
			HashMap<RateMonitorKeyContainer,RateMonitor> userRateMonitorsMap = getUserRateMonitorMap(userId);
			//
			if(userRateMonitorsMap.containsKey(rateMonitorKeyContainer) ){
				RateMonitor userRateMonitor = userRateMonitorsMap.get(rateMonitorKeyContainer);
				if( type == RateMonitorTypeConstants.ACCEPT_QUOTE){
					newLimit = getRateLimit(userRateMonitor.getWindowSize(),userRateMonitor.getWindowMilliSecondPeriod());
					diff =  newLimit - getRateLimit(oldWindowSize,oldWindowPeriod);
					totalQuoteLoad =totalQuoteLoad + diff;
					
					if(totalQuoteLoad <0)
						totalQuoteLoad =0;
					/*
					 * The original implementation was based on the assumption that event of changing a limit type for a trade session comes only once. 	
					 * During test, we found somehow the update event comes more than once sometime. So we have to do refresh instead. 
					 */
					/*if(diff != 0)
						internalRefresh();*/
					if(allowDynamicThreadPoolGrowth)
						requestAdditionalThreads(CAS_QUOTE, totalQuoteLoad);
				}
				else if(type == RateMonitorTypeConstants.ACCEPT_ORDER){
					newLimit = getRateLimit(userRateMonitor.getWindowSize(),userRateMonitor.getWindowMilliSecondPeriod());
					diff =  newLimit - getRateLimit(oldWindowSize,oldWindowPeriod);
					totalOrderLoad =totalOrderLoad + diff;
					if(totalOrderLoad<0)
						totalOrderLoad =0;
					/*
					 * The original implementation was based on the assumption that event of changing a limit type for a trade session comes only once. 	
					 * During test, we found somehow the update event comes more than once sometime. So we have to do refresh instead. 
					 */
					/*if(diff != 0)
						internalRefresh();*/
					
					if(allowDynamicThreadPoolGrowth)
						requestAdditionalThreads(CAS_ORDER_ENTRY, totalOrderLoad);
				}
				else if(type == RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER){
					newLimit = getRateLimit(userRateMonitor.getWindowSize(),userRateMonitor.getWindowMilliSecondPeriod());
					diff =  newLimit - getRateLimit(oldWindowSize,oldWindowPeriod);
					totalLightOrderLoad =totalLightOrderLoad + diff;
					if(totalLightOrderLoad<0)
						totalLightOrderLoad = 0;
					/*
					 * The original implementation was based on the assumption that event of changing a limit type for a trade session comes only once. 	
					 * During test, we found somehow the update event comes more than once sometime. So we have to do refresh instead. 
					 */
					/*if(diff != 0)
						internalRefresh();*/
					if(allowDynamicThreadPoolGrowth)
						requestAdditionalThreads(CAS_LIGHT_ORDER_ENTRY, totalLightOrderLoad);
				}
				else{
					// not the type we monitor
				}
				StringBuffer msg = new StringBuffer(100);
				msg.append("UserLoadManagerImpl.UpdateUserLimit(UserId:");
				msg.append(userId);
				msg.append( "),User new limit: Type( ");
				msg.append( type);
				msg.append( ")= ");
				msg.append( newLimit);
				msg.append( ", change delta:");
				msg.append( diff);
				msg.append("; new total Quote limit load: ");
				msg.append(totalQuoteLoad);
				msg.append("; Total Order load is:");
				msg.append(totalOrderLoad);
				msg.append("; Total LightOrder load is:");
				msg.append(totalLightOrderLoad);
				//if( Log.isDebugOn())
				//{					
					Log.debug( this, msg.toString());
				//}				
			}			
		}		
	}
	
	/**
	 * The requestAdditionalThreads method has the algorithm that calculates the needed thread pool size 
	 * and uses this value to determine how many additional threads to add to the thread pool. 
	 * If there are any to add, using the controller, this method will directly requestAdditionalThreads
	 */
	private void requestAdditionalThreads(String POAName, int totalLoad)
	{
		ThreadPoolController controller = poaNameToThreadPoolFactorsMap.get(POAName).controller;
		int currentPoolSize = controller.getPerformanceMemento().getTotalThreads();	
		
		float ratePerThread = poaNameToThreadPoolFactorsMap.get(POAName).ratePerThread;				
		int neededPoolSize = Math.round(totalLoad/ratePerThread);		
		
		int maximumPoolSize = poaNameToThreadPoolFactorsMap.get(POAName).maximumPoolSize;
		
		if (Log.isDebugOn())
		 {
			 Log.debug(this, "calculateThreadPoolSize." + POAName + ".growthFactor_RatePerThread=" + ratePerThread);
			 Log.debug(this, "calculateThreadPoolSize." + POAName + ".currentPoolSize=" + currentPoolSize);
			 Log.debug(this, "calculateThreadPoolSize." + POAName + ".neededPoolSize=" + neededPoolSize);
		 }
		
		if ( currentPoolSize < maximumPoolSize && neededPoolSize > currentPoolSize )
		{
			int deltaPoolSize = neededPoolSize - currentPoolSize;			
			
			if ( deltaPoolSize >= poaPoolSizeGrowthFactor )
			{
				int mod = deltaPoolSize % poaPoolSizeGrowthFactor;
				int numberOfThreadsToAdd = (mod > 0 ? ((deltaPoolSize - mod) + poaPoolSizeGrowthFactor ) : deltaPoolSize);	
				int calculatedPoolSize = numberOfThreadsToAdd + currentPoolSize;				
								
				if ( calculatedPoolSize <=  maximumPoolSize )
				{
					controller.requestAdditionalThreads(numberOfThreadsToAdd);	
				}
				else //else max out pool size.
				{
					numberOfThreadsToAdd = maximumPoolSize - currentPoolSize;
					controller.requestAdditionalThreads(numberOfThreadsToAdd);					
				}							
			}
			else //only 1 jump needed
			{				
				int calculatedPoolSize = poaPoolSizeGrowthFactor + currentPoolSize;
				int numberOfThreadsToAdd;
				
				if ( calculatedPoolSize <=  maximumPoolSize )
				{
					numberOfThreadsToAdd = poaPoolSizeGrowthFactor;
					controller.requestAdditionalThreads(numberOfThreadsToAdd);	
				}
				else //else max out pool size.
				{
					numberOfThreadsToAdd = maximumPoolSize - currentPoolSize;
					controller.requestAdditionalThreads(numberOfThreadsToAdd);					
				}				
			}
			
			StringBuilder s = new StringBuilder(100);
			s.append(POAName);
			s.append(" ThreadPoolSize has increased ");
			s.append("Size Before/After is ");
			s.append(currentPoolSize);
			s.append("/");
			s.append(controller.getPerformanceMemento().getTotalThreads());
			Log.information(this, s.toString());
		}
		else if ( currentPoolSize >= maximumPoolSize )
		{
			StringBuilder s = new StringBuilder(100);
			s.append(POAName);
			s.append(" currentThreadPoolSize >= maximumThreadPoolSize: ");
			s.append(currentPoolSize);
			s.append(" >= ");
			s.append(maximumPoolSize);
			
			if (POAName.equals(CAS_QUOTE))
			{
				s.append(", totalQuoteLoad is " );
				s.append(totalQuoteLoad);
				Log.alarm(this, s.toString());
			}
			else if (POAName.equals(CAS_ORDER_ENTRY))
			{
				s.append(", totalOrderLoad is " );
				s.append(totalOrderLoad);
				Log.alarm(this, s.toString());
			}
			else if (POAName.equals(CAS_LIGHT_ORDER_ENTRY))
			{
				s.append(", totalLightOrderLoad is " );
				s.append(totalLightOrderLoad);
				Log.alarm(this, s.toString());
			}				
		}
	}	
	
	// help method: getUserRateMonitorMap by userID, if not exist, new one.
	private HashMap<RateMonitorKeyContainer,RateMonitor> getUserRateMonitorMap(String userId){
		HashMap<RateMonitorKeyContainer,RateMonitor> userRateMonitor;
		if( userToRateMonitorKeyMap.containsKey(userId)){
			userRateMonitor = userToRateMonitorKeyMap.get(userId);
			return userRateMonitor;
		}
		userRateMonitor = new HashMap<RateMonitorKeyContainer,RateMonitor>(INITIAL_USER_RATE_MONITORS_SIZE);
		userToRateMonitorKeyMap.put(userId,userRateMonitor);
		return userRateMonitor;		
	}
	
	// helper method: getUserTotalLimit.
	private int getUserTotalLimit(short type,HashMap<RateMonitorKeyContainer,RateMonitor> userRateMonitorMap){
		int total = 0;
		Set<RateMonitorKeyContainer> userRateMonitors = userRateMonitorMap.keySet();
		Iterator<RateMonitorKeyContainer> i = userRateMonitors.iterator();
		RateMonitor rm;
		while(i.hasNext()){
			RateMonitorKeyContainer key = (RateMonitorKeyContainer)i.next();
			if(key.getType() == type){
				rm = userRateMonitorMap.get(key);
				total = total +getRateLimit(rm.getWindowSize(),rm.getWindowMilliSecondPeriod());
			}
		}
		return total;
	}
	
	// help: return ratio of rate limit
	private int getRateLimit(int windowSize, long windowMilliSecondPeriod){
		if( windowMilliSecondPeriod == 0)
			return 0;
		return Math.round(((float)(windowSize*1000))/((float)windowMilliSecondPeriod));		
	}
	
	// return: total limit by type
	public synchronized int getTotalLimit(short type){
		if( type == RateMonitorTypeConstants.ACCEPT_ORDER){
			return totalOrderLoad;
		}
		else if (type == RateMonitorTypeConstants.ACCEPT_QUOTE){
			return totalQuoteLoad;
		}
		else if (type == RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER){
			return totalLightOrderLoad;
		}
		return 0;
	}	
	
	// refresh total used by AR command
	public synchronized String refreshLoad() {
		
		internalRefresh();
		StringBuffer msg = new StringBuffer(100);
		msg.append("UserLoad refreshed: ");
		msg.append("Total Quote load is:");
		msg.append(totalQuoteLoad);
		msg.append("; Total Order load is:");
		msg.append(totalOrderLoad);
		msg.append("; Total LightOrder load is:");
		msg.append(totalLightOrderLoad);
		
		if( Log.isDebugOn())
		{					
			Log.debug( this, "refreshLoad():"
					+msg.toString());
		}
		
		return msg.toString();
	}

	// helper method used by AR command and update(), need to protested in synchronized block.
	private void internalRefresh(){
		int tmpTotalOrder = 0;
		int tmpTotalQuote = 0;
		int tmpTotalLightOrder = 0;

		String userIdStr = null;
		Set<String> userIds = userToRateMonitorKeyMap.keySet();
		Iterator<String> userIdItr = userIds.iterator();
		Set<RateMonitorKeyContainer> userRateMonitors = null;
		Iterator<RateMonitorKeyContainer> i = null;
		short type;
		RateMonitorKeyContainer key = null;

		while (userIdItr.hasNext()) {
			userIdStr = userIdItr.next();
			HashMap<RateMonitorKeyContainer, RateMonitor> userRateMonitorMap = (HashMap<RateMonitorKeyContainer, RateMonitor>) userToRateMonitorKeyMap
					.get(userIdStr);
			userRateMonitors = userRateMonitorMap.keySet();
			i = userRateMonitors.iterator();
			RateMonitor rm;
			while (i.hasNext()) {
				key = (RateMonitorKeyContainer) i.next();
				type = key.getType();
				rm = userRateMonitorMap.get(key);
				switch (type) {
				case (RateMonitorTypeConstants.ACCEPT_QUOTE):
					tmpTotalQuote = tmpTotalQuote
							+ getRateLimit(rm.getWindowSize(), rm
									.getWindowMilliSecondPeriod());
					break;
				case (RateMonitorTypeConstants.ACCEPT_ORDER):
					tmpTotalOrder = tmpTotalOrder
							+ getRateLimit(rm.getWindowSize(), rm
									.getWindowMilliSecondPeriod());
					break;
				case (RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER):
					tmpTotalLightOrder = tmpTotalLightOrder
							+ getRateLimit(rm.getWindowSize(), rm
									.getWindowMilliSecondPeriod());
					break;
				default:
					Log.alarm("UserLoadManagerImpl: invalid type:" + type
							+ " for user:" + userIdStr);
					break;
				}
			}
		}
		
		// update totals
		totalLightOrderLoad = tmpTotalLightOrder;
		totalOrderLoad = tmpTotalOrder;
		totalQuoteLoad = tmpTotalQuote;

	}

    public void acceptLogout(String userId, String sourceComponent, boolean forced, int sessionKey, String message )
    {}

    public void acceptLogin(String p0, String p1, String p2, int p3)
    {}

    public void acceptSessionClosed(int p0, String userId, boolean p2, String message)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> UserSessionClosed for " + userId + " ::SessionKey:" +p0+",ForcedLogout:"+p2+";"+ message);
        }
        
        removeUser(userId);
    }

    public void acceptSessionOpened(int p0, String userId)
    {}

    public void acceptOpenSessions(UserLoginStruct[] userSession)
    {}
    
    /**
	 * registerServiceReference is and should be called once for each NonGlobalService serviceReference found in 
	 * the NonGlobalServiceClientRoutingProxy's routing table. The call is made from NonGlobalServiceClientRoutingProxy.
	 * The registerServiceReference method is responsible for building the ConnectionPool with the proper start size.
	 * The starting ConnectionPool size is either gathered from the ConnectionPoolHistory file or the minimum starting ConnectionPool Size.
	 * It then binds the Controller to the serviceReference and stores it in a map.
	 */   
    public void registerServiceReference(String svcRouteKey, org.omg.CORBA.Object serviceReference)
    {
    	if (Log.isDebugOn())
	 	{
		 	Log.debug(this, "Entering registerServiceReference");
	 	}
    	
		Log.information(this, "useConnectionPoolHistory is " + useConnectionPoolHistory);
	 	
    	ConnectionPoolController connectionPoolController;
    	FoundationFramework foundationFramework = FoundationFramework.getInstance();
    	ConnectionIdentity connectionIdentity = foundationFramework.getCustomConfigurationFacade().findConnectionIdentity(serviceReference);
    	
    	StringBuilder s = new StringBuilder(100);
    	s.append("Starting ConnectionPool Size for ");
		s.append(svcRouteKey);
		s.append(" will be ");
    	
    	if(useConnectionPoolHistory)
    	{
    		int availableConnections = calculateInitialConnectionPoolSize(svcRouteKey);
    		connectionPoolController = foundationFramework.getCustomConfigurationFacade().createConnectionControllerBuilder(connectionIdentity).connections(availableConnections).build();   
    		try
    		{
    			svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).availableConnections = availableConnections;
    		}
    		catch(Exception e)
    		{
    			svcRouteKeyToConnectionHistoryStructMap.put(svcRouteKey, new ConnectionHistoryStruct(0)); 
        		svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).availableConnections = availableConnections;
    		}   		    		   		
    		
    		s.append(availableConnections);    		
        	Log.information(this, s.toString());
    	}
    	else
    	{    		
    		connectionPoolController = foundationFramework.getCustomConfigurationFacade().createConnectionControllerBuilder(connectionIdentity).connections(minimumConnectionPoolSize).build();
    		svcRouteKeyToConnectionHistoryStructMap.put(svcRouteKey, new ConnectionHistoryStruct(0)); 
    		svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).availableConnections = minimumConnectionPoolSize;
    		
    		s.append(" the minimumConnectionPoolSize " );
    		s.append(minimumConnectionPoolSize);
    		Log.information(this, s.toString());
    	}    	
    	
    	try
    	{
    		connectionPoolController.bind(serviceReference);
    	}
    	catch( ConfigurationException ce )
    	{
    		Log.exception(this, "ConfigurationException occured while trying to bind serviceReference with  svcRouteKey=" + svcRouteKey, ce);
    	}   
    	
    	svcRouteKeyToConnectionPoolStructMap.put(svcRouteKey, new ConnectionPoolStruct(svcRouteKey,serviceReference,connectionPoolController));    	
    	    	
    	if (Log.isDebugOn())
		 {
			 Log.debug(this, "registerServiceReference.svcRouteKey=" + svcRouteKeyToConnectionPoolStructMap.get(svcRouteKey).svcRouteKey);
			 Log.debug(this, "registerServiceReference.serviceReference=" +  svcRouteKeyToConnectionPoolStructMap.get(svcRouteKey).serviceReference.toString());
			 Log.debug(this, "registerServiceReference.connectionPoolController=" + svcRouteKeyToConnectionPoolStructMap.get(svcRouteKey).connectionPoolController.toString()); 
			 
			 int availableConnections = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).availableConnections;
			 int activeConnectionHWM = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).activeConnectionHighWaterMark;				
			 
			 Log.debug(this, "registerServiceReference.availableConnections=" + availableConnections);
			 Log.debug(this, "registerServiceReference.activeConnectionHWM=" + activeConnectionHWM);			
		 }
    }
    
    /**
	 * startScheduledThreadPoolExecutors creates the evaluatorExecutor and historyFileWriterExecutor
	 *  and starts the scheduled executions with initial and fixed delays.
	 */
	protected void startScheduledThreadPoolExecutors()
	{
		if (Log.isDebugOn())
	 	{
		 	Log.debug(this, "Entering startScheduledThreadPoolExecutors");
	 	}		
		
		evaluatorExecutor = new ScheduledThreadPoolExecutor(UserLoadManagerHomeImpl.evaluatorCorePoolSize);
		historyFileWriterExecutor = new ScheduledThreadPoolExecutor(UserLoadManagerHomeImpl.fileWriterCorePoolSize);
		
		if(useUserLoadManagerStartTimes)
		{
			evaluatorExecutor.scheduleWithFixedDelay(new ConnectionMetricEvaluator(), calculateEvaluatorInitialDelayInSeconds(UserLoadManagerHomeImpl.evaluatorStartTime), UserLoadManagerHomeImpl.evaluatorDelay, TimeUnit.SECONDS);
			historyFileWriterExecutor.scheduleWithFixedDelay(new HistoryFileWriter(), calculateHistoryFileWriterInitialDelayInSeconds(UserLoadManagerHomeImpl.fileWriterStartTime), UserLoadManagerHomeImpl.fileWriterDelay, TimeUnit.SECONDS);				
		}
		else
		{
			evaluatorExecutor.scheduleWithFixedDelay(new ConnectionMetricEvaluator(), DEFAULT_INITIAL_EVALUATOR_DELAY, UserLoadManagerHomeImpl.evaluatorDelay, TimeUnit.SECONDS);
			historyFileWriterExecutor.scheduleWithFixedDelay(new HistoryFileWriter(), DEFAULT_INITIAL_FILEWRITER_DELAY, UserLoadManagerHomeImpl.fileWriterDelay, TimeUnit.SECONDS);		
		
			Log.information(this, "useUserLoadManagerStartTimes = " + useUserLoadManagerStartTimes +", Will Use default initial delays: " + DEFAULT_INITIAL_EVALUATOR_DELAY + "seconds / " + DEFAULT_INITIAL_FILEWRITER_DELAY + "seconds" + " for ConnectionPoolEvaluator/HistoryFileWriter");
		}
	}	
    
    private int calculateInitialConnectionPoolSize(String svcRouteKey)
    {
    	int initialConnectionPoolSize;
    	
    	try
    	{    		
        	int activeConnectionHighWaterMark = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).activeConnectionHighWaterMark;
        	
        	if (activeConnectionHighWaterMark > minimumConnectionPoolSize)
        	{
        		int mod = activeConnectionHighWaterMark % connectionPoolGowthFactor;
        		initialConnectionPoolSize = ( mod > 0 ? ((activeConnectionHighWaterMark - mod) + connectionPoolGowthFactor) : activeConnectionHighWaterMark);

        		if(initialConnectionPoolSize > maximumConnectionPoolSize)
        		{
        			initialConnectionPoolSize = maximumConnectionPoolSize; //In this case max out connection pool size.
        		}    		
        	}
        	else
        	{
        		initialConnectionPoolSize = minimumConnectionPoolSize; //In this case size = minimumSize
        	}    	
    	}
    	catch(Exception e)
    	{
    		initialConnectionPoolSize = minimumConnectionPoolSize;
    		Log.exception(this, "Exception Caught while attempting to calculate Initial ConnectionPool Size for svcRoute " + svcRouteKey + " ,Will use minimumConnectionPoolSize=" + minimumConnectionPoolSize,e);
    	}    	
    	
    	return initialConnectionPoolSize;
    }    
	
	private int calculateEvaluatorInitialDelayInSeconds(String startTime)
	{
		int initialDelayInSeconds;
		
		String[] parsedStringArray = startTime.split(":");
		
		GregorianCalendar startDateTime = new GregorianCalendar();		
		startDateTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parsedStringArray[0]));
		startDateTime.set(Calendar.MINUTE, Integer.parseInt(parsedStringArray[1]));
		startDateTime.set(Calendar.SECOND, Integer.parseInt(parsedStringArray[2]));
		
		GregorianCalendar currentDateTime = new GregorianCalendar();
		
		if (startDateTime.after(currentDateTime))
		{
			initialDelayInSeconds = (int)((startDateTime.getTimeInMillis() - currentDateTime.getTimeInMillis())/1000);			
		}
		else
		{			
			initialDelayInSeconds =  DEFAULT_INITIAL_EVALUATOR_DELAY;			
		}
		
		if(Log.isDebugOn())
		{				
			Log.debug(this, "calculateEvaluatorInitialDelayInSeconds.initialDelayInSeconds=" + initialDelayInSeconds);
		}
		
		return initialDelayInSeconds;
	}
	
	private int calculateHistoryFileWriterInitialDelayInSeconds(String startTime)
	{		
		int initialDelayInSeconds;
		
		String[] parsedStringArray = startTime.split(":");
		
		GregorianCalendar startDateTime = new GregorianCalendar();		
		startDateTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parsedStringArray[0]));
		startDateTime.set(Calendar.MINUTE, Integer.parseInt(parsedStringArray[1]));
		startDateTime.set(Calendar.SECOND, Integer.parseInt(parsedStringArray[2]));
		
		GregorianCalendar currentDateTime = new GregorianCalendar();
		
		if (startDateTime.after(currentDateTime))
		{
			initialDelayInSeconds = (int)((startDateTime.getTimeInMillis() - currentDateTime.getTimeInMillis())/1000);			
		}
		else
		{			
			initialDelayInSeconds =  DEFAULT_INITIAL_FILEWRITER_DELAY;			
		}
		
		if(Log.isDebugOn())
		{				
			Log.debug(this, "calculateEvaluatorInitialDelayInSeconds.initialDelayInSeconds=" + initialDelayInSeconds);
		}
		
		return initialDelayInSeconds;
	}
	
	/**
	 *  method reads the connectionPoolHistory file, parses each line and stores the data into the map.
	 * @return true if the file was read successfully, false otherwise
	 */
	private boolean readConnectionPoolHistory()
	{
		if (Log.isDebugOn())
	 	{
		 	Log.debug(this, "Entering readConnectionPoolHistory");
	 	}
		
		boolean historyFileRead = false;		
		
		if(allowDynamicConnectionPoolGrowth)
		{
			try
			{
				BufferedReader bufferedReader = new BufferedReader(new FileReader(historyFile));
				String historyRecord;
				
				Log.information(this, "Reading " + historyFile +  " file");
		     			
				bufferedReader.readLine(); //read the file header to discard it.
				int linesRead = 0;
				while((historyRecord = bufferedReader.readLine()) != null)
				{				
					String[] parsedStringArray = historyRecord.split(",");
					
					String svcRouteKey = parsedStringArray[0];
					int activeConnectionHighWaterMark = Integer.parseInt(parsedStringArray[2]);
					svcRouteKeyToConnectionHistoryStructMap.put(svcRouteKey, new ConnectionHistoryStruct(activeConnectionHighWaterMark));		
					if (Log.isDebugOn())
				 	{
					 	Log.debug(this, "Reader for " + svcRouteKey + " activeConnectionHighWaterMark=" + activeConnectionHighWaterMark);
				 	}
					linesRead++;
				}
				
				bufferedReader.close();			
				historyFileRead = true;				
					
				Log.information(this, "Finished Reading " + historyFile +  " file " + linesRead + " lines of data read");
				
				if( linesRead <= 1 )
				{
					throw new Exception();
				}
			}
			catch(IOException ioe)
			{
				historyFileRead = false;
				Log.information(this, "Unable to read ConnectionPoolHistory file due to IOEException. UserLoadManager will use minimumConnectionPoolSize=" + minimumConnectionPoolSize + " as starting size for all ConnectionPools");
			}
			catch(Exception e)
			{
				historyFileRead = false;
				Log.information(this, "Unable to read ConnectionPoolHistory file due to Exception. UserLoadManager will use minimumConnectionPoolSize=" + minimumConnectionPoolSize + " as starting size for all ConnectionPools");
			}
		}
		
		return historyFileRead;	
	}    
    
	private class ConnectionMetricEvaluator implements Runnable
    {
    	/*ConnectionMetricEvaluator()
    	{
    		super();
    	}*/
    	
		/** 
		 * run() evaluates each serviceReference's connectionMetric and requests additional connections if the Rating is Starved or busy.
		 * run() will ignore the initial ConnectionMetric for each serviceRoute in order to reset the PerformanceMemento at the start time of the scheduler
		 * If the first ConnectionMetric were NOT ignored,  PerformanceMemento is NOT Reset and as a result, 
		 * run() would evaluate ConnectionMetrics between the timespan of the CAS Re-Init and the first execution of run().
		 * Since the first ConnectionMetric IS ignored, the PerformanceMemento IS reset and as a result,
		 * run() starts evaluating upon its first delay and continue evaluating after each delay.
		 */
    	public void run()
    	{    		
    		Set<String> svcRouteKeySet = svcRouteKeyToConnectionPoolStructMap.keySet();
    		
    		if (initialConnectionMetric)
    		{
    			for (String svcRouteKey : svcRouteKeySet)
        		{  			
        			ConnectionHistoryStruct connectionHistoryStruct = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey);
        			ConnectionPoolStruct connectionPoolStruct = svcRouteKeyToConnectionPoolStructMap.get(svcRouteKey);
        			ConnectionMetric connectionMetric = connectionPoolStruct.connectionPoolController.getPerformanceMemento();        			
        			connectionHistoryStruct.totalCalls = connectionMetric.getTotalCalls();        			
        			connectionHistoryStruct.activeConnectionHighWaterMark = 0; //reseting this value to get correct activeConnectionHighWaterMark for the day.
        		}
    			initialConnectionMetric = false;
    			return;
    		}
    		
    		for (String svcRouteKey : svcRouteKeySet)
    		{  			
    			ConnectionHistoryStruct connectionHistoryStruct = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey);
    			ConnectionPoolStruct connectionPoolStruct = svcRouteKeyToConnectionPoolStructMap.get(svcRouteKey);
    			ConnectionMetric connectionMetric = connectionPoolStruct.connectionPoolController.getPerformanceMemento();    			
    			
				Rating rating = connectionMetric.getRating();
    			
    			if (Log.isDebugOn())
       		 	{
       			 	Log.debug("EvaluateConnectionMetrics.run() evaulating serviceReference " + svcRouteKey + " Rating is: "  + rating.name());
       		 	}
    			
    			if(rating == Rating.Starved || rating == Rating.Busy)
    			{ 
    				StringBuilder s = new StringBuilder(100);
    				s.append("UserLoadManager requested ");
					s.append(connectionPoolGowthFactor);
					s.append(" Additional Connections to ");
					s.append(svcRouteKey);
					s.append(" due to Rating ");
					s.append(rating.name());
					
    				int availableConnections = connectionMetric.getAvailableConnections();       				
    				
    				if(availableConnections < maximumConnectionPoolSize)
    				{
    					if(allowDynamicConnectionPoolGrowth)
    					{
    						connectionPoolStruct.connectionPoolController.requestAdditionalConnections(connectionPoolGowthFactor);    	
    						
    						int newConnectionPoolSize = connectionPoolStruct.connectionPoolController.getPerformanceMemento().getAvailableConnections();            				
    						svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).availableConnections = newConnectionPoolSize;       						
    						
    						s.append(" ConnectionPoolSize Before/After request are ");
    						s.append(availableConnections);
    						s.append("/");
    						s.append(newConnectionPoolSize);
	        				Log.information(s.toString());
    					}
    				}
    				else
    				{
    					s.append(" but it is already at its MAXIMUM_SIZE of ");
    					s.append(availableConnections);
    					Log.information(s.toString());
    				} 
    				
    				if(rating == Rating.Starved)
    				{
    					connectionHistoryStruct.starvedCount++; 
    				}
    				else
    				{
    					connectionHistoryStruct.busyCount++;
    				}     				        				
    			}
    			
    			int latestActiveConnectionHWM = connectionMetric.getActiveConnectionHWM();				
				if(latestActiveConnectionHWM > connectionHistoryStruct.activeConnectionHighWaterMark) 
				{
					connectionHistoryStruct.activeConnectionHighWaterMark = latestActiveConnectionHWM;
				}
				
				connectionHistoryStruct.totalCalls = connectionMetric.getTotalCalls();
    		}    		
    	}
    }
	
	private class HistoryFileWriter implements Runnable
    {
    	/*HistoryFileWriter()
    	{
    		super();
    	}*/
    	
		/**
		 * run() creates the connectionPoolHistory file, gathers the data stored in the maps and writes it to the file.
		 */
    	public void run()
    	{
    		if (Log.isDebugOn())
    	 	{
    		 	Log.debug("Entering HistoryFileWriter.run()");
    	 	}
    		
    		try
    		{
    			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(historyFile));
    			bufferedWriter.write(HISTORY_FILE_HEADER);
    			
    			StringBuilder historyRecord = new StringBuilder(100);
    			
    			Set<String> svcRouteKeySet = svcRouteKeyToConnectionPoolStructMap.keySet();
    			for(String svcRouteKey : svcRouteKeySet)
    			{
    				int availableConnections = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).availableConnections; //Taken here, availableConnections is the ConnectionPoolSize at end of day, this will be the next day's historicalConnectionPoolSize
    				int activeConnectionHighWaterMark = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).activeConnectionHighWaterMark; //Taken here, activeConnectionHighWaterMark is Day's HighWaterMark for the number of ConnectionsUsed in the ConnectionPool this value will be used to compute tomorrow's starting connectionPoolSize.
    				long totalCalls = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).totalCalls; //totalCalls is the total number of calls to a ConnectionPool.
    				int starvedCount = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).starvedCount; //Taken here, starvedCount is the total number times a ConnectionPool had a Rating of STARVED for the day.
    				int busyCount = svcRouteKeyToConnectionHistoryStructMap.get(svcRouteKey).busyCount; //Taken here, busyCount is the total number times a ConnectionPool had a Rating of BUSY for the day.
    								
    				historyRecord
    				.append('\n')
    				.append(svcRouteKey).append(",")				
    				.append(availableConnections).append(",")
    				.append(activeConnectionHighWaterMark).append(",")
    				.append(totalCalls).append(",")				
    				.append(starvedCount).append(",")
    				.append(busyCount);
    							
    				bufferedWriter.write(historyRecord.toString());
    				historyRecord.setLength(0);
    			}	
    			
    			bufferedWriter.close();
    		}
    		catch (IOException ioe)
    		{
    			Log.exception(ioe);
    		}		
    	}
    }
	
	private class ThreadPoolFactorsStruct
	{    	
		ThreadPoolController controller;
		float ratePerThread;
		int baseTotalLoad;
		int maximumPoolSize;
		
		ThreadPoolFactorsStruct(ThreadPoolController controller, float ratePerThread, int baseTotalLoad, int maximumPoolSize)
		{
			this.controller = controller;
			this.ratePerThread = ratePerThread;
			this.baseTotalLoad = baseTotalLoad;
			this.maximumPoolSize = maximumPoolSize;
		}
	}
    
    private class ConnectionPoolStruct
    {
    	String svcRouteKey;    	 
    	org.omg.CORBA.Object serviceReference;
    	ConnectionPoolController connectionPoolController;
    	
    	ConnectionPoolStruct(String svcRouteKey, org.omg.CORBA.Object serviceReference, ConnectionPoolController connectionPoolController)
    	{    		
    		this.svcRouteKey 				= 	svcRouteKey;    		
    		this.serviceReference 			= 	serviceReference;
    		this.connectionPoolController 	= 	connectionPoolController;
    	}    	
    }
    
	private class ConnectionHistoryStruct
    {    	
    	int activeConnectionHighWaterMark;
    	int availableConnections = 0;
    	long totalCalls = 0;
		int starvedCount = 0;
		int busyCount = 0;
		
    	ConnectionHistoryStruct(int activeConnectionHighWaterMark)
    	{ 
    		this.activeConnectionHighWaterMark 	= 	activeConnectionHighWaterMark;	   		
    	}
    }
	
	// AR command
	public String userLoadStatusArCallback()
	{
		if (Log.isDebugOn()) {
			Log.debug(this,"call userLoadStatusArCallback()");
		}
		StringBuffer msg = new StringBuffer(800);
		
        msg.append("Flag useManagedThreadPools=");
		msg.append(useManagedThreadPools);
		msg.append("\n");
		msg.append("Flag allowDynamicThreadPoolGrowth=");
		msg.append(allowDynamicThreadPoolGrowth);
		msg.append("\n");
		msg.append("Flag useManagedConnectionPools=");
		msg.append(useManagedConnectionPools);
		msg.append("\n");
		msg.append("Flag allowDynamicConnectionPoolGrowth=");
		msg.append(allowDynamicConnectionPoolGrowth);
		msg.append("\n");

		msg.append("Quote TotalLoad is:");
		msg.append(totalQuoteLoad);
		msg.append("; Order TotalLoad is:");
		msg.append(totalOrderLoad);
		msg.append("; LightOrder TotalLoad is:");
		msg.append(totalLightOrderLoad);
		
		int casQuoteThreadPoolSize = -1;
		int casOrderEntryThreadPoolSize = -1;
		int casLightOrderEntryThreadPoolSize = -1;		
		int casQuoteGlobalHWM = -1;
		int casOrderEntryGlobalHWM = -1;
		int casLightOrderEntryGlobalHWM = -1;
		int casQuoteRecentHWM = -1;
		int casOrderEntryRecentHWM = -1;
		int casLightOrderEntryRecentHWM = -1;			
		
		ThreadPoolFactorsStruct threadPoolFactorsStruct;				
		
		threadPoolFactorsStruct = poaNameToThreadPoolFactorsMap.get(CAS_QUOTE);
		if( threadPoolFactorsStruct != null )
		{
			ThreadPoolMetric threadPoolMetric = threadPoolFactorsStruct.controller.getPerformanceMemento();
			casQuoteThreadPoolSize = threadPoolMetric.getTotalThreads();
			casQuoteGlobalHWM = threadPoolMetric.getGlobalHWM();
			casQuoteRecentHWM = threadPoolMetric.getRecentHWM();			
			casQuoteTotalCalls = threadPoolMetric.getTotalCalls();			
		}
			
		threadPoolFactorsStruct = poaNameToThreadPoolFactorsMap.get(CAS_ORDER_ENTRY);
		if( threadPoolFactorsStruct != null )
		{
			ThreadPoolMetric threadPoolMetric = threadPoolFactorsStruct.controller.getPerformanceMemento();
			casOrderEntryThreadPoolSize = threadPoolMetric.getTotalThreads();
			casOrderEntryGlobalHWM = threadPoolMetric.getGlobalHWM();
			casOrderEntryRecentHWM = threadPoolMetric.getRecentHWM();
			casOrderEntryTotalCalls = threadPoolMetric.getTotalCalls();			
		}			
		
		threadPoolFactorsStruct = poaNameToThreadPoolFactorsMap.get(CAS_LIGHT_ORDER_ENTRY);
		if( threadPoolFactorsStruct != null )
		{
			ThreadPoolMetric threadPoolMetric = threadPoolFactorsStruct.controller.getPerformanceMemento();
			casLightOrderEntryThreadPoolSize = threadPoolMetric.getTotalThreads();
			casLightOrderEntryGlobalHWM = threadPoolMetric.getGlobalHWM();
			casLightOrderEntryRecentHWM = threadPoolMetric.getRecentHWM();
			casLightOrderTotalCalls = threadPoolMetric.getTotalCalls();	
		}
				
		msg.append("\n");
		msg.append("CASQuote TotalThreads:");
		if(casQuoteThreadPoolSize > 0)
			msg.append(casQuoteThreadPoolSize);
		else
			msg.append("UNDEFINED");
		msg.append("; CASOrderEntry TotalThreads:");
		if(casOrderEntryThreadPoolSize > 0)
			msg.append(casOrderEntryThreadPoolSize);
		else
			msg.append("UNDEFINED");	
		msg.append("; CASLightOrderEntry TotalThreads:");
		if(casLightOrderEntryThreadPoolSize > 0)			
			msg.append(casLightOrderEntryThreadPoolSize);
		else
			msg.append("UNDEFINED");
		
		msg.append("\n")
			.append("CASQuote GlobalHWM:")
			.append(casQuoteGlobalHWM)
			.append("; CASOrderEntry GlobalHWM:")
			.append(casOrderEntryGlobalHWM)
			.append("; CASLightOrderEntry GlobalHWM:")
			.append(casLightOrderEntryGlobalHWM)
			.append("\n")
			.append("CASQuote RecentHWM:")
			.append(casQuoteRecentHWM)
			.append("; CASOrderEntry RecentHWM:")
			.append(casOrderEntryRecentHWM)
			.append("; CASLightOrderEntry RecentHWM:")
			.append(casLightOrderEntryRecentHWM)
			.append("\n")
			.append("CASQuote TotalCalls:")
			.append(casQuoteTotalCalls)
			.append("; CASOrderEntry TotalCalls:")
			.append(casOrderEntryTotalCalls)
			.append("; CASLightOrderEntry TotalCalls:")
			.append(casLightOrderTotalCalls);	

		return msg.toString();
	}
	
	// AR command:
	public synchronized String ulmFlagArCallbackArCallback(String flagType, String flagValue)
	{
		//public static boolean useManagedThreadPools; //if true:  managed thread pools are configured.
		//public static boolean allowDynamicThreadPoolGrowth;	//if true: thread pools grow dynamically, if false: all functionality occurs except the thread pools do not grow.
		//public static boolean useManagedConnectionPools;//if true:  startScheduledThreadPoolExecutors. if false: do not startScheduledThreadPoolExecutors
		//public static boolean allowDynamicConnectionPoolGrowth;//if true: connection pools grow dynamically. if false: all functionality occurs except the connection pools do not grow.
		
		StringBuilder s = new StringBuilder(150);
		if(flagType.equalsIgnoreCase("1")){
			if(flagValue.equalsIgnoreCase("false")){
				// if useManagedThreadPools turns to false, allowDynamicThreadPoolGrowth need to be false
				if( useManagedThreadPools){
					useManagedThreadPools = false;
					allowDynamicThreadPoolGrowth = false;
					s.append("Turn useManagedThreadPools = false,allowDynamicThreadPoolGrowth = false");
				}
				else{
					s.append("Flag useManagedThreadPools = false already.");
				}
				
			}
			else if(flagValue.equalsIgnoreCase("true") ){
				//need to check if ULM is inited
				boolean ulmThreadPool =Boolean.parseBoolean(System.getProperty(USE_MANAGED_THREADPOOLS,"false"));
				if(ulmThreadPool){//inited
					if(useManagedThreadPools){
						s.append("useManagedThreadPools = true already");
					}
					else{
						useManagedThreadPools = true;
						s.append("Turn useManagedThreadPools = true");
					}
				}
				else{
					s.append("could not turn useManagedThreadPools = true, user load manager is not initialized");
				}
			}
			else{
				s.append("invalid flag value:'");
				s.append(flagValue);
				s.append("'");
			}
			
		}
		else if(flagType.equalsIgnoreCase("2")){ //allowDynamicThreadPoolGrowth
			if(flagValue.equalsIgnoreCase("false")){
				// if useManagedThreadPools turns to false, allowDynamicThreadPoolGrowth need to be false
				if( allowDynamicThreadPoolGrowth){
					allowDynamicThreadPoolGrowth = false;
					s.append("Turn allowDynamicThreadPoolGrowth = false");
				}
				else{
					s.append("Flag allowDynamicThreadPoolGrowth = false already.");
				}
			}
			else if(flagValue.equalsIgnoreCase("true") ){
				//need to check if useManagedThreadPools is true
				if(useManagedThreadPools){//inited
					if(allowDynamicThreadPoolGrowth){
						s.append("allowDynamicThreadPoolGrowth = true already");
					}
					else{
						allowDynamicThreadPoolGrowth = true;
						s.append("Turn allowDynamicThreadPoolGrowth = true");
					}
				}
				else{
					s.append("could not turn allowDynamicThreadPoolGrowth = true, because useManagedThreadPools = false");
				}
			}
			else{
				s.append("invalid flag value:'");
				s.append(flagValue);
				s.append("'");
			}
			
		}
		else if(flagType.equalsIgnoreCase("3")){ //useManagedConnectionPools
			if(flagValue.equalsIgnoreCase("false")){
				// if useManagedConnectionPools turns to false, allowDynamicThreadPoolGrowth need to be false
				if( useManagedConnectionPools){
					useManagedConnectionPools = false;
					allowDynamicConnectionPoolGrowth = false;
					s.append("Turn useManagedConnectionPools = false,allowDynamicConnectionPoolGrowth = false");
				}
				else{
					s.append("Flag useManagedConnectionPools = false already.");
				}
				
			}
			else if(flagValue.equalsIgnoreCase("true") ){
				//need to check if ULM is inited
				boolean ulmThreadPool =Boolean.parseBoolean(System.getProperty(USE_MANAGED_CONN_POOLS,"false"));
				if(ulmThreadPool){//inited
					if(useManagedConnectionPools){
						s.append("useManagedConnectionPools = true already");
					}
					else{
						useManagedConnectionPools = true;
						s.append("Turn useManagedConnectionPools = true");
					}
				}
				else{
					s.append("could not turn useManagedConnectionPools = true, user load manager is not initialized");
				}
			}
			else{
				s.append("invalid flag value:'");
				s.append(flagValue);
				s.append("'");
			}
			
		}
		else if(flagType.equalsIgnoreCase("4")){ //allowDynamicConnectionPoolGrowth
			if(flagValue.equalsIgnoreCase("false")){
				// if useManagedThreadPools turns to false, allowDynamicThreadPoolGrowth need to be false
				if( allowDynamicConnectionPoolGrowth){
					allowDynamicConnectionPoolGrowth = false;
					s.append("Turn allowDynamicConnectionPoolGrowth= false");
				}
				else{
					s.append("Flag allowDynamicConnectionPoolGrowth = false already.");
				}
			}
			else if(flagValue.equalsIgnoreCase("true") ){
				//need to check if useManagedConnectionPools is true
				if(useManagedConnectionPools){//inited
					if(allowDynamicConnectionPoolGrowth){
						s.append("allowDynamicConnectionPoolGrowth = true already");
					}
					else{
						allowDynamicConnectionPoolGrowth = true;
						s.append("Turn allowDynamicConnectionPoolGrowth = true");
					}
				}
				else{
					s.append("could not turn allowDynamicConnectionPoolGrowth = true, because useManagedConnectionPools = false");
				}
			}
			else{
				s.append("invalid flag value: '");
				s.append(flagValue);
				s.append("'");
			}
			
		}
		else{
			s.append("invalid arguments");
			s.append("\n");
			s.append("Usage: ar CasName ulmFlag FlagType Flagvalue");
			s.append("\n");
			s.append("       Casname: e.g. prod*cas01v2cas*");
			s.append("\n");
			s.append("       FlagType: e.g. 1" );
			s.append("\n");
			s.append("                 1=useManagedThreadPools");
			s.append("\n");
			s.append("                 2=allowDynamicThreadPoolGrowth");
			s.append("\n");
			s.append("                 3=useManagedConnectionPools");
			s.append("\n");
			s.append("                 4=allowDynamicConnectionPoolGrowth");
			s.append("\n");
			s.append("       FlagValue: true/flase ");
			s.append("\n");
			s.append("       Example: ar test3cas01v2dev3cas ulmFlag 1 false");
		}
		
		return s.toString();
	}
	
	// get and set
	public boolean getUseManagedThreadPools(){
		return useManagedThreadPools;
	}
	
	public void setUseManagedThreadPools( boolean p_useManagedThreadPools){
		useManagedThreadPools = p_useManagedThreadPools;
	}

	public boolean getAllowDynamicThreadPoolGrowth(){
		return allowDynamicThreadPoolGrowth;
	}
	
	public void setAllowDynamicThreadPoolGrowth( boolean p_allowDynamicThreadPoolGrowth){
		allowDynamicThreadPoolGrowth = p_allowDynamicThreadPoolGrowth;
	}
	
	public boolean getUseManagedConnectionPools(){
		return useManagedConnectionPools;
	}
	
	public void setUseManagedConnectionPools( boolean p_useManagedConnectionPools){
		useManagedConnectionPools = p_useManagedConnectionPools;
	}
	
	public boolean getAllowDynamicConnectionPoolGrowth(){
		return allowDynamicConnectionPoolGrowth;
	}
	
	public void setAllowDynamicConnectionPoolGrowth( boolean p_allowDynamicConnectionPoolGrowth){
		allowDynamicConnectionPoolGrowth = p_allowDynamicConnectionPoolGrowth;
	}

}
