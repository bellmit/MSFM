package com.cboe.infrastructureUtility.configuration;

/**
 * The <code>ConnectionController</code> provides a hook into the underlying infra connection
 * system. Through this API you can observe and control the number of connections to an end-point.
 * This API is applied to corba objects via the bind method. The {@link ConnectionIdentity} of the
 * corba object must match the {@link ConnectionIdentity} of this controller.
 */
public interface ConnectionPoolController
{
	/**
	 * Request <code>count</code> additional connections. This may not be honored, the request will
	 * be logged however
	 */
	public void requestAdditionalConnections(int count);

	/**
	 * Returns a connection identity for which this controller is valid.
	 */
	public ConnectionIdentity getConnectionIdentity();

	/**
	 * Takes a memento snapshot of the current state of the controller, A new object is returned
	 * each time.
	 */
	public ConnectionMetric getPerformanceMemento();

	/**
	 * Attempt to bind the corba object to this connection controller. If the object does not match
	 * this objects ConnectionIdentity an exception will be thrown.
	 */
	public void bind(org.omg.CORBA.Object object) throws ConfigurationException;

}
