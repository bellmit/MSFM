package com.cboe.application.status;

import com.cboe.application.jcache.Cache;
import com.cboe.application.jcache.JCacheManager;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.startup.ClientRoutingBOHome;
import com.cboe.exceptions.*;
import com.cboe.idl.constants.BusinessDayStates;
import com.cboe.idl.session.BusinessDayStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import java.util.HashMap;

public class AppServerStatusManagerImpl extends BObject implements AppServerStatusManager,
        LocalComponentStatusCollector, RemoteComponentStatusCollector
{
    private boolean reInitCalled = false;  // has reInitialize() been called before
    protected static boolean isCASup = false;
    protected static boolean isFEup = false;
    protected static boolean isCASInitialized = false;
    protected static boolean isBusinessDayStarted = false;
    protected ProductQueryServiceAdapter productQueryServiceAdapter;
    protected TradingSessionServiceAdapter tradingSessionServiceAdapter;
    protected LocalComponentStatusListener localComponentStatusListener;
    protected RemoteComponentStatusListener remoteComponentStatusListener;
    protected BusinessDayStatusListener businessDayStatusListener;
    protected String processName;
    protected String cacheDirectory;
    protected String cacheBasename;
    protected Cache bdCache;
    protected final static String BUSINESS_DAY_KEY = "businessDay";

    public AppServerStatusManagerImpl(String processName, String cacheDirectory, String cacheBasename)
    {
        this.processName = processName;
        this.cacheDirectory = cacheDirectory;
        this.cacheBasename = cacheBasename;
        initialize();
    }

    protected void initialize()
    {
        localComponentStatusListener = new LocalComponentStatusListener(this);
        remoteComponentStatusListener = new RemoteComponentStatusListener(this);
        businessDayStatusListener = new BusinessDayStatusListener(this);

        HashMap cacheParms = new HashMap();
        cacheParms.put("name", cacheBasename);
        cacheParms.put("cacheFolder", cacheDirectory);
        cacheParms.put("persistent", "true");
        try
        {
            bdCache = (Cache) JCacheManager.instance().getCacheFactory().createCache(cacheParms);
        }
        catch (Exception e)
        {
            Log.exception(this, "Cannot create businessDay cache, continuing without cache.", e);
        }
    }

    public String getProcessName()
    {
        return processName;
    }

    public boolean isSystemReady()
    {
        return isCASInitialized;
    }

    public boolean isBusinessDayStarted()
    {
        return isBusinessDayStarted;
    }

    public void acceptLocalComponentStatusUp()
    {
        isCASup = true;
        try
        {
            reInitialize();
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }

    public void acceptLocalComponentStatusDown()
    {
        isCASup = false;        
        cleanup(); 
    }

    public void acceptRemoteComponentStatusUp()
    {
        isFEup = true;
        try
        {
            reInitialize();
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }

    public void acceptRemoteComponentStatusDown()
    {
        isFEup = false;
        
        if ( ClientRoutingBOHome.clientIsRemote() )
        {
            //Without an FE, a Remote Client Cannot Work
            cleanup();
        }
    }

    protected synchronized void reInitialize()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean isCasRemote = ClientRoutingBOHome.clientIsRemote();
        // We need CAS Up message; and a remote CAS needs FE Up message (because it will use the FE)
        if(isCASup && (isFEup || !isCasRemote) && !isCASInitialized)
        {
            // if reInitialize() has been called before, do not initProductQueryManager()
            if(!reInitCalled)
            {
                initProductQueryManager();
            }
            initTradingSession();
            ServicesHelper.getProductConfigurationService(); // on first call, will load PCS cache
            isCASInitialized = true;
            Log.notification(this, "CAS Re-Initialization is Complete.");
            reInitCalled = true;
        }
        else
        {
            StringBuilder nothing = new StringBuilder(110);
            nothing.append("Call to reInitialize will do nothing. isCASup:").append(isCASup)
                   .append(" isFEup:").append(isFEup)
                   .append(" isCasRemote:").append(isCasRemote)
                   .append(" isCASInitialized:").append(isCASInitialized);
            Log.notification(this, nothing.toString());
        }
    }

    protected synchronized void cleanup()
    {
        if(isCASInitialized)
        {
            isCASInitialized = false;
            ServicesHelper.getSessionManagerCleanupHome().logoffAllUsers(true);
            //getProductQueryServiceAdapter().purgeAllProductCaches();
            //Log.debug("Product Cache Purge complete");
            getTradingSessionServiceAdapter().endAllSessions();
            if (Log.isDebugOn())
            {
                Log.debug(this, "Session Product Cache Purge complete");
            }
        }
    }

    protected void initProductQueryManager()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getProductQueryServiceAdapter().initializeProductCaches();
    }

    protected void initTradingSession()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // Allow current business day start messages to flow to us
        getTradingSessionServiceAdapter().startDefaultTradingSessionEventFilters();

        // Get cached value of business day (if any)
        BusinessDayStruct currentBusinessDay = null;
        if (null != bdCache)
        {
            currentBusinessDay = (BusinessDayStruct) bdCache.get(BUSINESS_DAY_KEY);
        }
        if (null == currentBusinessDay || null == currentBusinessDay.currentDay
        ||  !ServicesHelper.getTradingSessionService().isBusinessDayCurrent(currentBusinessDay.currentDay))
        {
            currentBusinessDay = ServicesHelper.getTradingSessionService().getCurrentBusinessDay();
            Log.information(this, "finished calling getCurrentBusinessDay");
            if (null != bdCache)
            {
                bdCache.put(BUSINESS_DAY_KEY, currentBusinessDay);
                try
                {
                    bdCache.flush();
                }
                catch (Exception e)
                {
                    Log.exception(this, "Could not persist businessDay cache, continuing.", e);
                }
            }
        }
        // Find out if the business day has started, and handle either case
        acceptBusinessDayEvent(currentBusinessDay);
    }


    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(productQueryServiceAdapter == null)
        {
            productQueryServiceAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return productQueryServiceAdapter;
    }

    protected TradingSessionServiceAdapter getTradingSessionServiceAdapter()
    {
        if(tradingSessionServiceAdapter == null)
        {
            tradingSessionServiceAdapter = ServicesHelper.getTradingSessionServiceAdapter();
        }
        return tradingSessionServiceAdapter;
    }

    /////////////// Event Collector impl //////////////
    public void acceptBusinessDayEvent(BusinessDayStruct currentDay)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptBusinessDayEvent");
        }

        if (currentDay.dayState == BusinessDayStates.STARTED)
        {
            // Disallow any logins
            isBusinessDayStarted = false;

            // Purge existing session caches and listeners
            getTradingSessionServiceAdapter().endAllSessions();

            // Rebuild session caches and listeners
            getTradingSessionServiceAdapter().startAllSessions(currentDay);

            // Allow logins
            isBusinessDayStarted = true;
        }
        else
        {
            isBusinessDayStarted = false;

            // Logout all the Users and purge the Caches
            ServicesHelper.getSessionManagerCleanupHome().logoffAllUsers(false);

            //PQRefactor
            //ProductQueryManagerImpl.purgeAllProductCaches();
            getProductQueryServiceAdapter().purgeAllProductCaches();

            // Purge existing session caches and listeners (this step is in case we move to a
            // logout all user/no CAS shutdown design)
            getTradingSessionServiceAdapter().endAllSessions();
            if (Log.isDebugOn())
            {
                Log.debug(this, "Product Caches Purge complete. Reason : Business Day ended.");
            }
        }
    }
}
