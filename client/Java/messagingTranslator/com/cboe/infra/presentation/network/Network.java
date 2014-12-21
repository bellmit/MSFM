package com.cboe.infra.presentation.network;
import java.util.*;

import java.util.Set;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;

public abstract class Network
{
    protected static Network liveNetwork = null;
    protected static Network staticNetwork = null;
    protected Set listeners = Collections.synchronizedSet( new HashSet() );

    protected ArrayList networkNodes = new ArrayList();

    /**
     * Network uses a Factory pattern
     */
    public static Network getNetwork( Class type )
                           throws ClassCastException
    {
        Network rv = null;
        if( type == SBTLiveNode.class )
        {
            if( liveNetwork == null )
            {
                liveNetwork = new RealTimeNetwork();
            }
            rv = liveNetwork;
        }
        else
        {
            if( type == SBTExtentNode.class )
            {
                if( staticNetwork == null )
                {
                    staticNetwork = new ExtentMapNetwork();
                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug("Completed construction of ExtentMapNetwork",GUILoggerMMBusinessProperty.EXTENT_MAP);
                    }
                }
                rv = staticNetwork;
            }
            else
            {
                throw new ClassCastException( "Networks may only contain homogeneous collections of SBTLiveNode or SBTExtentNode objects." );
            }
        }
        return rv;
    }

    public int size()
    {
        return networkNodes.size();
    }

    public void addNetworkListener( NetworkListener l )
    {
        listeners.add( l );
    }

    public void removeNetworkListener( NetworkListener l )
    {
        listeners.remove( l );
    }

    abstract public SBTNode[] sendsTo( String subjectName );

    public Collection getDNs()
    {
        HashSet dns = new HashSet();
	    synchronized ( networkNodes )
	    {
			Iterator dnIter = networkNodes.iterator();
			while( dnIter.hasNext() )
			{
				SBTNode node = (SBTNode)dnIter.next();
				if( node.getType() == SBTNodeType.DN_NODE )
				{
					dns.add( node );
				}
			}
	    }
        return dns;
    }

    /**
     * Retrieve a *safe* copy of the collection of network elements.
     * Note that this copy is a *snapshot* of the network at the time
     * of invocation, and will reflect subsequent node additions, removals
     * @return Collection The set of network nodes.
     */
    public Collection getNetworkElements()
    {
//        ArrayList networkCopy = null;
//        synchronized ( networkNodes )
//        {
//            networkCopy = new ArrayList();
//            Collections.copy( networkNodes, networkCopy  );
//        }
//        return networkCopy;
        return new ArrayList(networkNodes);
    }

    public void removeNode( SBTNode node )
    {
	    synchronized ( networkNodes )
	    {
            networkNodes.remove( node );
	    }
        fireNodeRemovedEvent( Arrays.asList(new SBTNode[] {node}) );
    }

    protected void fireNodeAddedEvent( Collection affectedNodes )
    {
        NetworkUpdateEvent nue = new NetworkUpdateEvent( this, affectedNodes );
        Iterator iter = listeners.iterator();
        while( iter.hasNext() )
        {
            NetworkListener listener = null;
            try
            {
                listener = (NetworkListener)iter.next();
                listener.nodeAdded( nue );
            } catch (Throwable t)
            {
                System.err.println("Error in nodeAdded event for listener " + listener);
            }
        }
    }

    protected void fireNodeRemovedEvent( Collection affectedNodes )
    {
        NetworkUpdateEvent nue = new NetworkUpdateEvent( this, affectedNodes );
        Iterator iter = listeners.iterator();
        while( iter.hasNext() )
        {
            NetworkListener listener = null;
            try
            {
                listener = (NetworkListener)iter.next();
                listener.nodeRemoved( nue );
            } catch (Throwable t)
            {
                System.err.println("Error in nodeRemoved event for listener " + listener);
            }
        }
    }

    protected void fireNodeUpdatedEvent( Collection affectedNodes )
    {
        NetworkUpdateEvent nue = new NetworkUpdateEvent( this, affectedNodes );
        Iterator iter = listeners.iterator();
        while( iter.hasNext() )
        {
            NetworkListener listener = null;
            try
            {
                listener = (NetworkListener)iter.next();
                listener.nodeUpdated( nue );
            } catch (Throwable t)
            {
                System.err.println("Error in nodeUpdated event for listener " + listener);
                t.printStackTrace(System.err);
            }
        }
    }

    protected void setData( Collection sbtNodes )
    {
	    synchronized ( networkNodes )
	    {
            networkNodes.clear();
            networkNodes.addAll( sbtNodes );
	    }
        fireNodeAddedEvent( sbtNodes );
    }

    protected void addNode( SBTNode node )
    {
	    synchronized ( networkNodes )
	    {
            if ( networkNodes.contains(node) )
            {
                // do not add node, do not notify
                return;
            }
	        networkNodes.add( node );
	    }
        fireNodeAddedEvent( Arrays.asList( new SBTNode[] {node}) );
    }
    
    public void initNetworkElements()
    {}
}
