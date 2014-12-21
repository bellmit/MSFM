/**
 * 
 */
package com.cboe.application.tradingClassStatus;

import java.util.HashMap;
import java.util.Map;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.TradingClassStatusQueryService;
import com.cboe.interfaces.application.TradingClassStatusQueryServiceHome;

/**
 * @author Arun Ramachandran Nov 15, 2009
 * 
 */
public class TradingClassStatusQueryServiceHomeImpl extends ClientBOHome implements TradingClassStatusQueryServiceHome {

	protected Map<SessionManager, TradingClassStatusQueryServiceImpl> userTradingClassStatusServices;

	public TradingClassStatusQueryServiceHomeImpl() {
		super();
		userTradingClassStatusServices = new HashMap<SessionManager, TradingClassStatusQueryServiceImpl>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cboe.interfaces.application.TradingClassStatusQueryServiceHome#create(com.cboe.interfaces.application.SessionManager)
	 */

	public synchronized TradingClassStatusQueryService create(SessionManager sessionManager) {
		TradingClassStatusQueryServiceImpl userTradingClassStatus = userTradingClassStatusServices.get(sessionManager);
		if (userTradingClassStatus == null) {
			userTradingClassStatus = new TradingClassStatusQueryServiceImpl(sessionManager);
			addToContainer(userTradingClassStatus);
			userTradingClassStatusServices.put(sessionManager,
					userTradingClassStatus);
			Log.information(this.getClass().getSimpleName()+" Instance created");
		}
		
		return userTradingClassStatus;
	}

	public TradingClassStatusQueryService find(SessionManager sessionManager) {
		return create(sessionManager);
	}

	public void clientInitialize() throws Exception {
        if (Log.isDebugOn())
        {
            Log.debug(this, "TradingClassStatusQueryServiceHome initialized...");
        }
	}

	public synchronized void remove(SessionManager sessionManager) {
		try{
			userTradingClassStatusServices.remove(sessionManager);
            if (Log.isDebugOn())
            {
                Log.debug(this, "TradingClassStatusQueryService removed for user :<"+sessionManager.getUserId() +">");
            }
		}catch(Exception e){
			Log.exception(this, e);
		}
	}
}
