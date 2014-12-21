package com.cboe.domain.rateMonitor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.cboe.idl.property.PropertyGroupStruct;

import com.cboe.interfaces.domain.userLoadManager.UserLoadManagerHome;
import com.cboe.interfaces.businessServices.PropertyService;
import com.cboe.interfaces.businessServices.PropertyServiceHome;
import com.cboe.interfaces.domain.RateMonitor;
import com.cboe.interfaces.domain.RateMonitorHome;
import com.cboe.interfaces.domain.RateMonitorTypeConstants;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.rateMonitor.RateLimits;

import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.startup.ClientBOHome;
import com.cboe.domain.util.RateMonitorKeyContainer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;

/**
 *  RateMonitorHomeImpl
 *
 *  This home acts as a cache of rate monitors for a given service.
 *  Rate monitors are cached based on RateMonitorContainerKey.
 *  Author: Connie Feng
 */
public class RateMonitorHomeImpl extends ClientBOHome implements RateMonitorHome {

    //  map of generic keys to rate monitors
    private ConcurrentHashMap<RateMonitorKeyContainer,RateMonitor> rateMonitorCollection = null;

    private PropertyService propertyService = null;

    private UserLoadManagerHome userLoadManagerHome = null;
    private final static String USE_MANAGED_THREADPOOLS = "ORB.POA.CASQuote.UseManagedThreadPool"; //using only one Managed POA -D setting because all the Managed POAs are controlled with the same flag. 
    public static boolean useManagedThreadPools; //if true:  managed thread pools are configured.
    
    public RateMonitorHomeImpl() {
        super();
        rateMonitorCollection = new ConcurrentHashMap();
        useManagedThreadPools = Boolean.parseBoolean(System.getProperty(USE_MANAGED_THREADPOOLS,"false"));
        if (Log.isDebugOn())
        {				
			Log.debug(this, "useManagedThreadPools = " + useManagedThreadPools);
		}
    }

    public void clientInitialize()
        throws Exception
    {
        Log.debug(this, "SMA Type = " + this.getSmaType());
        
    }

    /**
     * Create a new RateMonitor
     * @param windowSize              size of window
     * @param windowMilliSecondPeriod time frame
     * @return com.cboe.interfaces.domain.RateMonitor
     */
    private RateMonitor create(RateMonitorKeyContainer key, int windowSize, long windowMilliSecondPeriod)
    {
        int defaultWindowSize = windowSize;
        long defaultWindowMilliSecondPeriod = windowMilliSecondPeriod;

        if(Log.isDebugOn())
        {
            Log.debug("RateMonitorHomeImpl -> create():: "
                      + key
                      + " windowSize:" + windowSize
                      + " windowMillsSecondPeriod:" + windowMilliSecondPeriod);
        }

        try
        {
            PropertyGroupStruct propertyGroupStruct = getPropertyService().getProperties(PropertyCategoryTypes.RATE_LIMITS
                                                                                         , RateLimitsFactory.getRateMonitorKey(key.getUserId()
                                                                                                                               , key.getExchange()
                                                                                                                               , key.getAcronym()));
            PropertyServicePropertyGroup propertyServiceGroup = PropertyFactory.createPropertyGroup(propertyGroupStruct);
            RateLimits rateLimit = RateLimitsFactory.getRateLimitBySessionType(propertyServiceGroup, key.getSession(), key.getType());
            windowSize = rateLimit.getWindowSize();
            windowMilliSecondPeriod = rateLimit.getWindowInterval();
        }
        catch(Exception e)
        {
            // ignore miss as use inbound values as the default
            if(Log.isDebugOn())
            {
                Log.debug("RateMonitorHomeImpl -> create():: default/inbound values being used.");
            }
        }

        RateMonitorImpl rateMonitor = new RateMonitorImpl(key, windowSize, defaultWindowSize, windowMilliSecondPeriod, defaultWindowMilliSecondPeriod);
        rateMonitor.create(String.valueOf(rateMonitor.hashCode()));
        addToContainer(rateMonitor);
        
        if (useManagedThreadPools) {
			if (key.getType() == RateMonitorTypeConstants.ACCEPT_ORDER
					|| key.getType() == RateMonitorTypeConstants.ACCEPT_LIGHT_ORDER
					|| key.getType() == RateMonitorTypeConstants.ACCEPT_QUOTE) {
				getUserLoadManagerHome().find().addUser(key, rateMonitor);

			}
		}
        
        return rateMonitor;
    }

    private PropertyService getPropertyService()
    {
        if ( propertyService == null )
        {
            try
            {
                PropertyServiceHome home = (PropertyServiceHome) HomeFactory.getInstance().findHome(PropertyServiceHome.HOME_NAME);
                propertyService = home.find();
            }
            catch(CBOELoggableException e)
            {
                Log.exception("Could not find PropertyServiceHome", e);
                // a really ugly way to get around the missing exception in the interface...
//                throw new NullPointerException("Could not find PropertyServiceHome");
            }
        }
        return propertyService;
    }

    /**
     * Return RateMonitor given a key
     * @param Object key
     * @return com.cboe.interfaces.domain.RateMonitor
     */
    public RateMonitor find(Object key, int windowSize, long windowMilliSecondPeriod)
    {
        if(Log.isDebugOn())
        {
            Log.debug("RateMonitorHomeImpl -> find():: "
                      + (RateMonitorKeyContainer)key
                      + " windowSize:" + windowSize
                      + " windowMillsSecondPeriod:" + windowMilliSecondPeriod);
        }

        RateMonitor rateMonitor = (RateMonitor) rateMonitorCollection.get(key);
        if (rateMonitor == null)
        {
            // create a new rate monitor
            rateMonitor = create((RateMonitorKeyContainer)key, windowSize, windowMilliSecondPeriod);
            // add new rate monitor
            updateCache(key, rateMonitor);
        }

        return rateMonitor;
    }

    /**
     *  Synchronized update of a cache with a given key and monitor
     *
     *  @param hashMap cache to be updated
     *  @param key hash key
     *  @param com.cboe.interfaces.domain.RateMonitor rate monitor
     */
    private void updateCache(Object key, RateMonitor rateMonitor)
    {
        if(Log.isDebugOn())
        {
            Log.debug("RateMonitorHomeImpl -> updateCache():: " + (RateMonitorKeyContainer) key);
        }
      synchronized(rateMonitorCollection)
      {
        rateMonitorCollection.put((RateMonitorKeyContainer)key, rateMonitor);
      }
    }

    public synchronized void cleanupRateMonitors(String userId)
    {
        int initiaSize = rateMonitorCollection.size();
        long startTime = System.nanoTime();
        for (Iterator<RateMonitorKeyContainer> it = rateMonitorCollection.keySet().iterator(); it.hasNext();)
        {
            RateMonitorKeyContainer key = it.next();
            if (key.getUserId().equals(userId))
            {
                it.remove();
            }
        }
        int finalSize = rateMonitorCollection.size();
         StringBuilder msg = new StringBuilder(100);
        msg.append("Cleaned up rateMonitorCache for user:");
        msg.append(userId);
        msg.append(" count:");
        msg.append(initiaSize-finalSize);
        msg.append(" in ");
        msg.append(System.nanoTime()-startTime);
        msg.append("nanos.");
        Log.information(msg.toString());
    }
    
    private UserLoadManagerHome getUserLoadManagerHome()
    {
        if (userLoadManagerHome == null)
        {
            try
            {
            	userLoadManagerHome = (UserLoadManagerHome) HomeFactory.getInstance().findHome(UserLoadManagerHome.HOME_NAME);
            }
            catch (CBOELoggableException e)
            {
                Log.exception("Could not find UserLoadManagerHome", e);
                // a really ugly way to get around the missing exception in the interface...
                throw new NullPointerException("Could not find UserLoadManagerHome");
            }
        }

        return userLoadManagerHome;
    }

}
