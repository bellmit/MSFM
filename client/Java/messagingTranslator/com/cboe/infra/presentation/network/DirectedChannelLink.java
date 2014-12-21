package com.cboe.infra.presentation.network;


/**
 * A DirectedChannelLink is a kind of ChannelLink in which the communication
 * goes one-way.  DirectedChannelLink treats the super.firstNode as the publisher
 * and super.secondNode as the subscriber.
 */

public class DirectedChannelLink extends ChannelLink
{

    public SBTNode getPublisher()
    {
        return firstNode;
    }

    public SBTNode getSubscriber()
    {
        return secondNode;
    }

    /**
     * Returns a copy of this link with the firstNode (publisher) and secondNode (subscriber) switched
     */

    public DirectedChannelLink reverse()
    {
        DirectedChannelLink link = new DirectedChannelLink( secondNode, firstNode, new Topic( channelName, extentName, false ), isGlobal );
        return link;
    }

    public boolean equals( Object o )
    {
        boolean rv = false;
        ChannelLink other = (ChannelLink)o;
	    rv = ( this.firstNode.equals( other.firstNode ) && this.secondNode.equals( other.secondNode ) && this.channelName.equals( other.channelName ) && this.extentName.equals( other.extentName ) && this.isGlobal == other.isGlobal );
        return rv;
    }

    public String toString()
    {
        StringBuffer asString = new StringBuffer( channelName );
        asString.append( "(" );
        asString.append( extentName );
        asString.append( ") : " );
        asString.append( firstNode );
        asString.append( " --> " );
        asString.append( secondNode );
        return asString.toString();
    }

    public DirectedChannelLink( SBTNode pub, SBTNode sub, Topic link, boolean global )
    {
        super( pub, sub, link, global );
    }
}
