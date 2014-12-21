package com.cboe.infra.presentation.network;
import java.util.Collection;

/**
 * This class is used to carry information about all of the network events:
 * add, remove, update.   Its two principal methods of interest are #getAffectedNodeCount()
 * and #getAffectedNodes()
 */
public class NetworkUpdateEvent
{
    private Collection nodes = null;
    private Network source = null;

	/**
	 * Determine the com.cboe.infra.presentation.network.Network that generated the event
	 * @return com.cboe.infra.presentation.network.Network The network that generated the event
	 */
    public Network getSource()
    {
        return source;
    }

	/**
	 * Determine the number of nodes affected by the given operation (add,remove,update)
	 * @return
	 */
    public int getAffectedNodeCount()
    {
        return nodes.size();
    }

	/**
	 * Get the nodes involved in the operation (add,remove,update)
	 * @return
	 */
    public Collection getAffectedNodes()
    {
        return nodes;
    }

	/**
	 * Create a NetworkUpdateEvent
	 * @param source The com.cboe.infra.presentation.network.Network that will be returned by getSource()
	 * @param affectedNodes  The nodes involved in the operation (add,remove,update)
	 */
    public NetworkUpdateEvent( Network source, Collection affectedNodes )
    {
        this.source = source;
        this.nodes = affectedNodes;
    }
}