package com.cboe.interfaces.application;
import com.cboe.idl.cmiTradeMaintenanceService.TradeMaintenanceServiceOperations;

/**
 * Extends from cmi interface
 * Since ACL doesn't allow duplicate interface names and there is already 
 * TradeMaintenanceService defined, "External" is added to the interface name. 
 * @author zhuw
 *
 */
public interface ExternalTradeMaintenanceService extends TradeMaintenanceServiceOperations {

}
