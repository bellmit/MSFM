package com.cboe.infrastructureServices.sessionManagementService;


/**
 *  The callback interface with which to register with the
 *  SessionManagementService fascade for component status events.
 */
public interface SessionManagementServiceComponentConsumer
{
	void acceptComponentEstablished(String componentName);
	void acceptComponentFailed(String componentName);
	void acceptComponentIsMaster(String componentName, boolean isMaster);

	/**
	 * Called if the consumer is registered with the SMS facade for a specific set of
	 * components.  When all of these components have failed, this method will be called.
	 * If the consumer is registered for all components, then this method will not be called.
	 */
	void allRegisteredComponentsFailed();

	/**
	 * Called if the consumer is registered with the SMS facade for a specific set of
	 * components.  When all of these components have gone 'not master', this method will be called.
	 * If the consumer is registered for all components, then this method will not be called.
	 */
	void allRegisteredComponentsNotMaster();
	
	/**
	 * ==================================================================================
	 * The following methods are added to provide functionality to dynamically add/remove
	 * a process component from the process graph
	 * ==================================================================================
	 */
	void acceptComponentAdded(String componentName,int componentType,String parentComponentName,int currentState);
	void acceptComponentRemoved(String componentName, String[] parentComponents);
}
