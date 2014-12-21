package com.cboe.interfaces.application;


public interface FloorTradeMaintenanceServiceHome {
	public final static String HOME_NAME = "FloorTradeMaintenanceServiceHome";
    public FloorTradeMaintenanceService create(SessionManagerV6 sessionManager);
}