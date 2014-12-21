package com.cboe.infrastructureServices.lifeLineService;

public interface LifeLineNotificationListener {
	
	/**
	 * When a lifeline target changes from being available to
	 * being unavailable, the listener will be infromed. Please
	 * note that it is the state change triggers the callback, 
	 * not the state itself.
	 */
	public void targetUnavailable(String target);
	
	/**
	 * When a lifeline target changes from being unavailable to
	 * being available, the listener will be infromed. Please
	 * note that it is the state change triggers the callback, 
	 * not the state itself.
	 */	
	public void targetAvailable(String target);
}
