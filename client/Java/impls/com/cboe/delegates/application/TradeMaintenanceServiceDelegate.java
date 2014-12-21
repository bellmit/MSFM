package com.cboe.delegates.application;

import com.cboe.idl.cmiTradeMaintenanceService.POA_TradeMaintenanceService_tie;
import com.cboe.interfaces.application.ExternalTradeMaintenanceService;

public class TradeMaintenanceServiceDelegate 
		extends POA_TradeMaintenanceService_tie{

	public TradeMaintenanceServiceDelegate(ExternalTradeMaintenanceService delegate) {
		super(delegate);
	}

}
