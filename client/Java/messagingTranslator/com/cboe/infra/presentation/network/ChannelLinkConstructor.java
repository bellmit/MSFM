package com.cboe.infra.presentation.network;
import java.util.*;

import com.cboe.infra.presentation.util.RegExStringUtil;
import com.cboe.presentation.environment.EnvironmentProperties;
import com.cboe.presentation.environment.EnvironmentManagerFactory;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

public class ChannelLinkConstructor
{
	/** This is a collection of ChannelLink objects that represent the legs of the graph */
    protected List linkModel = new ArrayList();
	/** This is a map of a all the nodes that publish a local message (on this channel) and a node receives */
    protected Map localPublish = new HashMap();
	/** This is a map of a sender node to all nodes who receive local messages (on this channel) */
    protected Map localSubscribe = new HashMap();
	/** This is a map of a all the nodes that publish a global message (on this channel) and a node receives */
    protected Map globalPublish = new HashMap();
	/** This is a map of a sender node to all nodes who receive global messages (on this channel) */
    protected Map globalSubscribe = new HashMap();
	/** The channel filter used to narrow down the network to nodes on a particular channel */
    protected NodeChannelFilter channelFilter;
	/** The subset of network nodes that participate in the current channel */
    protected Set networkNodes;
	/** The monitoring service prefix prepended to channel/extent names in the environment(s) - this can be a regex*/
    protected String prefix;
	/**
	 * Create a ChannelLinkConstuctor
	 * @param data The network elements to construct the graph for
	 * @param channelName The name of the channel to analyze the network on
	 */
    public ChannelLinkConstructor( Collection data, String channelName )
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ChannelLinkConstructor.<INIT>: node set size = " + data.size() ,GUILoggerMMBusinessProperty.CHANNELS);
        }
        EnvironmentProperties currentEnvironment = EnvironmentManagerFactory.find().getCurrentEnvironment();
        prefix = (String) currentEnvironment.getMonitorPrefix();
        if ( prefix == null )
        {
            prefix = "Prod"; // default
        }
        // filter out the nodes in the overall network that do
        // not participant in this channel
        networkNodes = new HashSet();
        channelFilter = new NodeChannelFilter( channelName );
	    // data may be from Network.getNetworkElements()
	    // which is a synchronized Set
	    synchronized ( data ) {
			Iterator dataIter = data.iterator();
			while( dataIter.hasNext() )
			{
				SBTNode node = (SBTNode)dataIter.next();
				if( channelFilter.accept( node ) )
				{
					networkNodes.add( node );
				}
				else
				{
                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug( "-->Node " + node.getName() + " failed channel filter test (" + channelName + ")" ,GUILoggerMMBusinessProperty.CHANNELS);
                    }
				}
			}
	    }

        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("===> ChannelLinkConstructor:  there are " + networkNodes.size() + " nodes to analyze" ,GUILoggerMMBusinessProperty.CHANNELS);
        }
    }

	/**
	 * Retrieve the set of ChannelLink objects that present the legs of the graph of
	 * communication within the network on our channel
	 * @return java.util.Collection A collection of ChannelLink objects (can be directioned or bidirectional links)
	 */
    public Collection getLinkModel()
    {
        return linkModel;
    }

	/**
	 * Retrieve the set of nodes the either publish or subscribe locally to/from the messages
	 * of a DN
	 * @param dn The DN to find partner nodes for
	 * @return java.util.Collection Collection of SBTNodes (either SBTLiveNode or SBTExtentNode)
	 * that the DN supplied chats with (on our channel)
	 */
    public Collection getAffiliatedNodes( SBTNode dn )
    {
        Collection rv = new HashSet();
        Iterator iter = networkNodes.iterator();
        Collection dns = Network.getNetwork( SBTLiveNode.class ).getDNs();
        while( iter.hasNext() )
        {
            SBTNode dnOwner = getDNAffinity( (SBTNode)iter.next(), dns );
            if( dnOwner.equals( dn ) )
            {
                rv.add( dnOwner );
            }
        }
        return rv;
    }

    /**
     * Returns a collection of ChannelLinks
     */
    public void getOutgoingPath(SBTNode traceNode, Collection linkSet) {
            Iterator globals =  getGlobalSubscribers(traceNode).iterator();

            while ( globals.hasNext() ) {
	            Object l = globals.next();
	            if ( l instanceof DirectedChannelLink )
	            {
					DirectedChannelLink dcl = (DirectedChannelLink) l;
					if ( dcl.getChannelName().equals( channelFilter.getChannelName() ) ) {
						if (! linkSet.contains( dcl ) ) {
							linkSet.add( dcl );
							getOutgoingPath(dcl.getSubscriber(), linkSet ) ;
						}
					}
	            }
	        }

            Iterator locals =  getLocalSubscribers(traceNode).iterator();

            while ( locals.hasNext() ) {
				DirectedChannelLink dcl = (DirectedChannelLink) locals.next();
				if ( dcl.getChannelName().equals( channelFilter.getChannelName() ) ) {
					if (! linkSet.contains( dcl ) ) {
						linkSet.add( dcl );
						getOutgoingPath(dcl.getSubscriber(), linkSet ) ;
					}
				}
            }
    }


    /**
     * Returns a collection of ChannelLinks
     */
    public void getIncomingPath(SBTNode traceNode, Collection linkSet ) {
        Iterator globals = getGlobalPublishers(traceNode).iterator();
        while ( globals.hasNext() ) {
	        Object l = globals.next();
	        if ( l instanceof DirectedChannelLink )
	        {
				DirectedChannelLink dcl = (DirectedChannelLink) l;
				if ( dcl.getChannelName().equals( channelFilter.getChannelName() ) ) {
					if (! linkSet.contains( dcl ) ) {
						linkSet.add( dcl );
						getIncomingPath(dcl.getPublisher(), linkSet ) ;
					}
				}
	        }
        }


        Iterator locals =  getLocalPublishers(traceNode).iterator();

        while ( locals.hasNext() ) {
	        Object l = locals.next();
	        if ( l instanceof DirectedChannelLink )
	        {
				DirectedChannelLink dcl = (DirectedChannelLink) l;
				if ( dcl.getChannelName().equals( channelFilter.getChannelName() ) ) {
					if (! linkSet.contains( dcl ) ) {
						linkSet.add( dcl );
						getIncomingPath(dcl.getPublisher(), linkSet ) ;
					}
				}
	        }
        }

    }

    /**
     * Remove all of the network nodes, and all of the links from our internal model
     */
    public void removeAll()
    {
        networkNodes.clear();
        linkModel.clear();
    }

    /**
     * Returns a collection of ChannelLink objects where the subscriber
     * is the supplied node.
     */
    public Collection getLocalPublishers( SBTNode node )
    {
        Collection subscribers = new HashSet();
        Iterator modelIter = linkModel.iterator();
        while( modelIter.hasNext() )
        {
            ChannelLink link = (ChannelLink)modelIter.next();
            if( link instanceof DirectedChannelLink )
            {
                DirectedChannelLink dlink = (DirectedChannelLink)link;
                if( dlink.getSubscriber().equals( node ) && dlink.isGlobal == false )
                {
                    subscribers.add( dlink );
                }
            }
        }
        return subscribers;
    }

    /**
     * Returns a collection of ChannelLink objects where the subscriber
     * is the supplied node.
     */

    public Collection getGlobalPublishers( SBTNode node )
    {
        Collection subscribers = new HashSet();
        Iterator modelIter = linkModel.iterator();
        while( modelIter.hasNext() )
        {
            ChannelLink link = (ChannelLink)modelIter.next();
            if( link instanceof DirectedChannelLink )
            {
                DirectedChannelLink dlink = (DirectedChannelLink)link;
                if( dlink.getSubscriber().equals( node ) && dlink.isGlobal == true )
                {
                    subscribers.add( dlink );
                }
            }
        }
        return subscribers;
    }

	/**
	 * Manually add a node to the model.  This is used if a node comes online/active
	 * after the ChannelLinkConstructor has been created.  Note that the node will be
	 * tested to see if it participates in our channel before being added
	 * @param node  The node to add ( if it qualifies )
	 */
    public void addNode( SBTNode node )
    {
        if( channelFilter.accept( node ) )
        {
            networkNodes.add( node );
        }
    }

	/**
	 * Add a group of nodes at once.  This method calls addNode(SBTNode) on each node in the Collection
	 * And the same channel-participation constraint applies here.
	 * @param nodes The nodes to add. Some, none, or all may be added depending on their channel participation
	 */
    public void addNodes( Collection nodes )
    {
	    // nodes may be from Network.getNetworkElements()
	    // which is a synchronized Set
	    synchronized ( nodes )
	    {
			Iterator i = nodes.iterator();
			while( i.hasNext() )
			{
				addNode( (SBTNode)i.next() );
			}
	    }
    }

	/**
	 * Remove the collection of nodes from our internal model, and the links they are associated with
	 * This method calls removeNode( SBTNode ) for each node in the collection.
	 * @param nodes  The nodes to remove
	 */
    public void removeAll( Collection nodes )
    {
        Iterator i = nodes.iterator();
        while( i.hasNext() )
        {
            removeNode( (SBTNode)i.next() );
            // also remove links this node participates in
        }
    }

    public void removeNode( SBTNode node )
    {
        networkNodes.remove( node );
	    Iterator linkIter = linkModel.iterator();
	    while ( linkIter.hasNext() )
	    {
		    ChannelLink cl = (ChannelLink) linkIter.next();
		    if ( cl.contains(node) )
		    {
			    linkIter.remove();
		    }
	    }
    }


	/**
	 * Get all of the nodes that comprise the nodes of this graph
 	 * @return The set of nodes participating (actively) in this channel
	 */
    public Set getChannelNodes()
    {
        return networkNodes;
    }

    /*
    * May have to modify this method so that it pulls out EVENT_CHANNEL_PREFIX
    * before doing the match.   SBTLiveNode already does this, and SBTExtentNode
    * which matches naturally, would then have to do it as well.
    * This would lead us to looking for matches on the "core" channel name,
    * i.e. 'Ticker' instead of 'ProdTicker'.
    */
    public void analyze()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ChannelLinkConstructor.analyze()",GUILoggerMMBusinessProperty.CHANNELS);
        }
        // for each node
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug( "There are " + networkNodes.size() + " nodes to analyze for links." ,GUILoggerMMBusinessProperty.CHANNELS);
        }
        String channelName = channelFilter.getChannelName();
        channelName = RegExStringUtil.strip( prefix, channelName );
        Iterator iter = networkNodes.iterator();
        while( iter.hasNext() )
        {
            SBTNode node = (SBTNode)iter.next();
            Topic[] localPubs = node.getLocalPublishList();

            // tally the local pulishers for extents on this our channel
            for( int k = 0;k < localPubs.length;++k )
            {
                if( RegExStringUtil.strip( prefix, localPubs[k].getChannelName() ).equals( channelName ) )
                {
                    addParticipant( localPublish, localPubs[k], node );
                }
            }
            // tally the local subscribers for extents on this our channel
            Topic[] localSubs = node.getLocalSubscribeList();
            for( int n = 0;n < localSubs.length;++n )
            {
                if( RegExStringUtil.strip( prefix, localSubs[n].getChannelName() ).equals( channelName ) )
                {
                    addParticipant( localSubscribe, localSubs[n], node );
                }
            }
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ChannelLinkConstructor.analyze() - completed creating local links",GUILoggerMMBusinessProperty.CHANNELS);
            }

            Topic[] globalPubs = node.getGlobalPublishList();
            for( int r = 0;r < globalPubs.length;++r )
            {
                if( RegExStringUtil.strip( prefix, globalPubs[r].getChannelName() ).equals( channelName ) )
                {
                    addParticipant( globalPublish, globalPubs[r], node );
                }
            }

            Topic[] globalSubs = node.getGlobalSubscribeList();
            for( int p = 0;p < globalSubs.length;++p )
            {
                if( RegExStringUtil.strip( prefix, globalSubs[p].getChannelName() ).equals( channelName ) )
                {
                    addParticipant( globalSubscribe, globalSubs[p], node );
                }
            }
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ChannelLinkConstructor.analyze() - completed creating global links",GUILoggerMMBusinessProperty.CHANNELS);
        }
        // pair localPub <-> localSub
        Iterator localIter = localPublish.keySet().iterator();
        while( localIter.hasNext() )
        {
            // extent name turns out to roughly be the channel name + local/global
            Topic topic = (Topic)localIter.next();
            Collection p = (Collection)localPublish.get( topic );
            SBTNode[] publishers = (SBTNode[])p.toArray( new SBTNode[0] );
            Collection s = (Collection)localSubscribe.get( topic );
            if( s != null )
            {
                SBTNode[] subscribers = (SBTNode[])s.toArray( new SBTNode[0] );

                // for each publisher, map all possible subscribers
                for( int z = 0;z < publishers.length;++z )
                {
                    for( int r = 0;r < subscribers.length;++r )
                    {
                        if( subscribers[r].getName().equals( publishers[z].getName() ) )
                        {
                            continue; // do not worry about self-link
                        }
                        DirectedChannelLink link = new DirectedChannelLink( publishers[z], subscribers[r], topic, false );
                        linkModel.add( link );
                    }
                }
            }
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ChannelLinkConstructor.analyze() - completed analyzing local links",GUILoggerMMBusinessProperty.CHANNELS);
        }
        // now, pair globalPub <-> globalSub
        Iterator globalIter = globalPublish.keySet().iterator();
        while( globalIter.hasNext() )
        {
            // extent name turns out to roughly be the channel name + global/global
            Topic topic = (Topic)globalIter.next();
            Collection p = (Collection)globalPublish.get( topic );
            SBTNode[] publishers = (SBTNode[])p.toArray( new SBTNode[0] );
            Collection s = (Collection)globalSubscribe.get( topic );
            if( s != null )
            {
                SBTNode[] subscribers = (SBTNode[])s.toArray( new SBTNode[0] );

                // for each publisher, map all possible subscribers
                for( int z = 0;z < publishers.length;++z )
                {
                    for( int r = 0;r < subscribers.length;++r )
                    {
                        if( subscribers[r].getName().equals( publishers[z].getName() ) )
                        {
                            continue; // do not worry about self-link
                        }
                        DirectedChannelLink link = new DirectedChannelLink( publishers[z], subscribers[r], topic, true );
                        linkModel.add( link );
                    }
                }
            }
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ChannelLinkConstructor.analyze() - completed analyzing global links",GUILoggerMMBusinessProperty.CHANNELS);
        }

        // before completing, do some re-processing.   Look through entire set of
        // links where link == link.reverse().  In this case, remove both, add back
        // in a Bidirectional Link (???? think about this some more ... )
        // can't use iterator here because of double remove
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ChannelLinkConstructor.analyze() - searching for bidirectional links",GUILoggerMMBusinessProperty.CHANNELS);
        }
        Set doubleups = new HashSet();
        for( int m = 0;m < linkModel.size();++m )
        {
            DirectedChannelLink l = (DirectedChannelLink)linkModel.get( m );
            for( int j = 0;j < linkModel.size();++j )
            {
                DirectedChannelLink other = (DirectedChannelLink)linkModel.get( j );
                if( l.equals( other ) )
                {
                    continue;
                }
                else
                {
                    if( l.equals( other.reverse() ) )
                    {
                        // Create birectional link?
                        // remove linkModel(m) and linkModel(j);
                        linkModel.remove( l );
                        linkModel.remove( other );
                        ChannelLink bidirectional = new ChannelLink( l.getPublisher(), l.getSubscriber(), new Topic( l.getChannelName(), l.getExtent(), false ), l.isGlobal() );
                        doubleups.add( bidirectional );
                    }
                }
            }
        }
        linkModel.addAll( doubleups );
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug( "Done analyzing channel links! - model contains " + linkModel.size() + " links." ,GUILoggerMMBusinessProperty.CHANNELS);
        }
    }

    /**
     * Returns a collection of ChannelLink objecs where the publisher
     * is the supplied node.
     */
    public Collection getLocalSubscribers( SBTNode node )
    {
        Collection subscribers = new HashSet();
        Iterator modelIter = linkModel.iterator();
        while( modelIter.hasNext() )
        {
            ChannelLink link = (ChannelLink)modelIter.next();
            if( link instanceof DirectedChannelLink )
            {
                DirectedChannelLink dlink = (DirectedChannelLink)link;
                if( dlink.getPublisher().equals( node ) && dlink.isGlobal == false )
                {
                    subscribers.add( dlink );
                }
            }
        }
        return subscribers;
    }

    /**
     * Returns a collection of ChannelLink objecs where the publisher
     * is the supplied node.
     */
    public Collection getGlobalSubscribers( SBTNode node )
    {
        Collection subscribers = new HashSet();
        Iterator modelIter = linkModel.iterator();
        while( modelIter.hasNext() )
        {
            ChannelLink link = (ChannelLink)modelIter.next();
            if( link instanceof DirectedChannelLink )
            {
                DirectedChannelLink dlink = (DirectedChannelLink)link;
                if( dlink.getPublisher().equals( node ) && dlink.isGlobal == true )
                {
                    subscribers.add( dlink );
                }
            }
        }
        return subscribers;
    }


    protected SBTNode getDNAffinity( SBTNode free, Collection owners )
    {
        Set receivers = new HashSet();
        Iterator fis = getLocalSubscribers( free ).iterator();
        while( fis.hasNext() )
        {
            ChannelLink l = (ChannelLink)fis.next();
            receivers.add( l.otherNode( free ) );
        }
        Set talkers = new HashSet();
        Iterator fip = getLocalPublishers( free ).iterator();
        while( fip.hasNext() )
        {
            ChannelLink l = (ChannelLink)fip.next();
            receivers.add( l.otherNode( free ) );
        }
        SBTNode winner = null;
        int winningCount = 0;
        Iterator i = owners.iterator();
        while( i.hasNext() )
        {
            int references = 0;
            SBTNode n = (SBTNode)i.next();
            Collection s = getLocalSubscribers( n );
            Iterator si = s.iterator();
            while( si.hasNext() )
            {
                ChannelLink l = (ChannelLink)si.next();
                if( receivers.contains( l.otherNode( n ) ) )
                {
                    references++;
                }
                else
                {
                    if( talkers.contains( l.otherNode( n ) ) )
                    {
                        references++;
                    }
                }
            }
            Collection p = getLocalPublishers( n );
            Iterator pi = p.iterator();
            while( pi.hasNext() )
            {
                ChannelLink l = (ChannelLink)pi.next();
                if( receivers.contains( l.otherNode( n ) ) )
                {
                    references++;
                }
                else
                {
                    if( talkers.contains( l.otherNode( n ) ) )
                    {
                        references++;
                    }
                }
            }
            if( references > winningCount )
            {
                winner = n;
                if (GUILoggerHome.find().isDebugOn())
                { 
                    GUILoggerHome.find().debug("Found Node affinity for " + free + ": " + winner ,GUILoggerMMBusinessProperty.CHANNELS);
                }
 
            }
        }
        return winner;
    }

    private void addParticipant( Map role, Topic link, SBTNode node )
    {
        Set s = (Set)role.get( link );
        if( s == null )
        {
            s = new HashSet();
            role.put( link, s );
        }
        s.add( node );
    }
}
