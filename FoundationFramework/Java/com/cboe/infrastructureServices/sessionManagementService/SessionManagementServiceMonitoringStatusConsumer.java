package com.cboe.infrastructureServices.sessionManagementService;


/**
 *  The callback interface with which to register with the
 *  SessionManagementService fascade for component monitoring status events.
 *  Register implementations of this interface with any of the registerConsumerXxx() methods in SMS facade.
 */
public interface SessionManagementServiceMonitoringStatusConsumer extends SessionManagementServiceComponentConsumer
{
	void acceptComponentMonitoringStatus(String componentName, boolean monitoringEnabled);

	/**
	 * Called if the consumer is registered with the SMS facade for a specific set of
	 * components.  When all of these components have their monitoring status disabled
     * (due to process monitoring failure, possibly indicating a network failure, excessive
     * network latency, all 'watchdog' processes failed, etc), this method will be called.
	 */
	void allRegisteredComponentsMonitoringDisabled();
}
