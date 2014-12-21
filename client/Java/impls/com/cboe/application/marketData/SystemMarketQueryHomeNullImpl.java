package com.cboe.application.marketData;

/**
 * This type was created in VisualAge.
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.floorApplication.LastSaleService;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;


public class SystemMarketQueryHomeNullImpl extends BOHome implements MarketQueryHome {

	private  Map <SessionManager, SystemMarketQuery> sessionMap = 
		Collections.synchronizedMap(new HashMap<SessionManager, SystemMarketQuery>());
	/**
      * MarketDataFactory constructor comment.
      */
    public MarketQueryV3 createMarketQuery(SessionManager sessionManager)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating MarketQuery for " + sessionManager);
        }
        return (MarketQueryV3) findNullImpl(sessionManager);

    }

	public LastSaleService createLargeTradeLastSale(SessionManager sessionManager) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "finding LastSaleService for " + sessionManager);
        }
        return (LastSaleService) findNullImpl(sessionManager);
	}
	
	private SystemMarketQuery findNullImpl(SessionManager sessionManager) {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating SystemMarketQueryNullImpl for " + sessionManager);
        }
        SystemMarketQuery boiFor2 = null;
        synchronized(sessionMap) {
        	boiFor2 = sessionMap.get(sessionManager);
        	// create if not exists
        	if(boiFor2 == null) {
        		SystemMarketQueryNullImpl bo = new SystemMarketQueryNullImpl();        		
        		bo.setSessionManager(sessionManager);
        		bo.create(String.valueOf(bo.hashCode()));
        		addToContainer(bo);  
        		// The addToContainer call MUST occur prior to creation of the interceptor.
                SystemMarketQueryInterceptor boi = null;
                try
                {
                    boi = (SystemMarketQueryInterceptor) this.createInterceptor(bo);
                    boiFor2 =  (SystemMarketQuery) boi;
                    sessionMap.put(sessionManager, boiFor2);
                } catch(Exception ex)
                {
                    Log.exception(this, ex);
                }
    	    }
        }	
        return boiFor2;
    }

	public void removeSession(SessionManager sessionManager) {
		synchronized(sessionMap) {
        	sessionMap.remove(sessionManager);
		}
	}

}
