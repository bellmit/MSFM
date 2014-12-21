/**
 * 
 */
package com.cboe.interfaces.application;

/**
 * @author Arun Ramachandran Nov 12, 2009
 *
 */
public interface TradingClassStatusQueryService extends com.cboe.idl.cmiV8.TradingClassStatusQueryOperations {
	//Method used by fix users to subscribe for TCS. FIX now supports only class based TCS subscription.
	public void subscribeTradingClassStatusForClasses(java.lang.String sessionName, int[] classKeys, com.cboe.interfaces.application.inprocess.TradingClassStatusQueryConsumer clientListener)
    throws com.cboe.exceptions.SystemException, com.cboe.exceptions.CommunicationException, com.cboe.exceptions.AuthorizationException, com.cboe.exceptions.DataValidationException;
}
