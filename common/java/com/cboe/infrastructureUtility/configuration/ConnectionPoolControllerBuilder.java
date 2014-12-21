package com.cboe.infrastructureUtility.configuration;

/**
 * Builds ConnectionControllers via the Builder pattern.
 */
public interface ConnectionPoolControllerBuilder
{
	/**
	 * Define the number of connections that should be applied to the controller. If not provided a
	 * reasonable default will be used.
	 */
	public ConnectionPoolControllerBuilder connections(int count);

	/**
	 * Build a new controller for the provided identity
	 */
	public ConnectionPoolController build();
}
