package com.cboe.infrastructureUtility.configuration;

import java.util.Set;

/**
 * This just provides a single place to chain configuration tools.
 */
public interface ConfigurationFacade
{
	/**
	 * look up the connection identity for the org.omg.CORBA.Object form. If non can be found then
	 * null will be returned
	 */
	public ConnectionIdentity findConnectionIdentity(org.omg.CORBA.Object obj);

	/**
	 * Look up the connection identity for the serialized {@link String} form. If non can be found
	 * then null will be returned
	 */
	public ConnectionIdentity findConnectionIdentity(String serializedForm);

	/**
	 * Creates a builder for the connection configuration
	 */
	public ConnectionPoolControllerBuilder createConnectionControllerBuilder(ConnectionIdentity identity);

	/**
	 * Adds a new thread pool capable of increasing threads dynamically. 
	 */
	public ThreadPoolIdentity register(String uniqueName, ManagedThreadPool managedThreadPool);
	

	/**
	 * Get a new builder for making Thread Pool Controllers
	 */
	public ThreadPoolControllerBuilder createThreadPoolControllerBuilder(ThreadPoolIdentity identity);
	
	/**
	 * Return a list of identifiers for each thread pool attached to the dynamic system.
	 */
	public Set<ThreadPoolIdentity> getManagedThreadPoolIdentifiers();
}
