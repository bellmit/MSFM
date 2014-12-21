package com.cboe.interfaces.application;

public interface ExternalTradeMaintenanceServiceHome {
	public final static String HOME_NAME = "ExternalTradeMaintenanceServiceHome";
    public ExternalTradeMaintenanceService create(SessionManagerTMS sessionManager);

}
