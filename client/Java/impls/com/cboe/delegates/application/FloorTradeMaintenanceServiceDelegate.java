package com.cboe.delegates.application;

import com.cboe.interfaces.application.FloorTradeMaintenanceService;

/**
 * Created by IntelliJ IDEA.
 * User: josephg
 * Date: Feb 18, 2002
 */
public class FloorTradeMaintenanceServiceDelegate extends com.cboe.idl.cmiV6.POA_FloorTradeMaintenanceService_tie {
    public FloorTradeMaintenanceServiceDelegate(FloorTradeMaintenanceService delegate) {
        super(delegate);
    }
}