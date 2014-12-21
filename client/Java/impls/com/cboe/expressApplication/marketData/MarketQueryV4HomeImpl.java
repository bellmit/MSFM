//
// -----------------------------------------------------------------------------------
// Source file: MarketQueryV4HomeImpl.java
//
// PACKAGE: com.cboe.expressApplication.marketData
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.expressApplication.marketData;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.expressApplication.ExpressMarketQuery;
import com.cboe.interfaces.expressApplication.MarketQueryV4;
import com.cboe.interfaces.expressApplication.MarketQueryV4Home;
import com.cboe.interfaces.floorApplication.MarketQueryV5;
import com.cboe.interfaces.floorApplication.NBBOService;

public class MarketQueryV4HomeImpl extends ClientBOHome implements MarketQueryV4Home
{	
    public MarketQueryV4HomeImpl()
    {
        super();
    }

    public MarketQueryV4 createMarketQueryV4(SessionManager sessionManager)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Creating MarketQueryV4Impl for " + sessionManager);
        }
        return (MarketQueryV4) findImpl(sessionManager);
    }
    
    public NBBOService createNBBOService(SessionManager sessionManager)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Creating NBBOService for " + sessionManager);
        }
        return (NBBOService) findImpl(sessionManager);
    }
    
    public MarketQueryV5 createMarketQueryV5(SessionManager sessionManager)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Creating MarketQueryV4Imp(floorApplication/V5) for " + sessionManager);
        }
        return (MarketQueryV5) findImpl(sessionManager);
    }
    
    private ExpressMarketQuery findImpl(SessionManager sessionManager)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Creating MarketQueryV4Impl for " + sessionManager);
        }
        ExpressMarketQuery boiFor2 = null;
		MarketQueryV4Impl bo = new MarketQueryV4Impl();		       		
		bo.setSessionManager(sessionManager);
		
		//Every BObject must be added to the container.
		addToContainer(bo);
		
		bo.create(String.valueOf(bo.hashCode()));
		
		//The addToContainer call MUST occur prior to creation of the interceptor.
		MarketQueryV4Interceptor boi = null;
		try {
			bo.initialize();
			boi = (MarketQueryV4Interceptor) this.createInterceptor(bo);
	        boi.setSessionManager(sessionManager);
	        if(getInstrumentationEnablementProperty())
	        {
	               boi.startInstrumentation(getInstrumentationProperty());
	        }
	        boiFor2 = (ExpressMarketQuery) boi;
		} catch (Exception e) {
			Log.exception(this, e);
		}		
        return boiFor2;
    }

	
}
