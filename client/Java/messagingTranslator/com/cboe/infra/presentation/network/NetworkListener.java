package com.cboe.infra.presentation.network;

/**
 * This interface is used to register as a party interested in network membership events
 */
public interface NetworkListener
{
    /**
     * This callback method is invoked when a node or nodes has been removed from the network
     * @param evt  The event contains information as to which node(s) were removed
     */
    public void nodeRemoved( NetworkUpdateEvent evt );

	/**
	 * This callback method is invoked when a node or nodes has been updated from under us.
	 * This technique is used mainly for node properties that are not <i>bound</i>.  For changes
	 * in bound properties, use the com.cboe.infra.presentation.models.BackDoorEditor.
	 * @param evt The event object contains information as to which node(s) were updated
	 */
    public void nodeUpdated( NetworkUpdateEvent evt );

	/**
	 * This callback method is invoked when a node or nodes has been added to the network
	 * Note the different subclasses of com.cboe.infra.network.Network are free to use this method
	 * in their own choosing.  RealTimeNetwork, for instance, invokes this method each time a node
	 * is added, because that information is dynamic.  ExtentMapNetwork, however, invokes this method
	 * when <b>all</b< nodes have been added, because its membership list is static.
	 * @param evt  The event contains information as to which node(s) were added
	 */
    public void nodeAdded( NetworkUpdateEvent evt );
}
