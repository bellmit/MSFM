package com.cboe.delegates.application;

import com.cboe.idl.cmiTradeMaintenanceService.POA_TMSUserSessionManager_tie;
import com.cboe.interfaces.application.TMSUserSessionManager;

public class TMSUserSessionManagerDelegate 
			 extends POA_TMSUserSessionManager_tie  {

	public TMSUserSessionManagerDelegate(TMSUserSessionManager delegate) {
		super(delegate);
	}

}
