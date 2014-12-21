package com.cboe.infra.presentation.network;


import com.cboe.infra.presentation.filter.Filter;
public class ChannelLink
{

    SBTNode firstNode = null;
    SBTNode secondNode = null;
    String channelName = null;
    String extentName = null;
    boolean isGlobal = false;

    public boolean contains( SBTNode node )
    {
        return ( firstNode.equals( node ) || secondNode.equals( node ) );
    }

    public boolean equals( Object o )
    {
        boolean rv = false;
        ChannelLink other = (ChannelLink)o;
	    rv = ( this.firstNode.equals( other.firstNode ) || this.firstNode.equals( other.secondNode ) ) && ( this.secondNode.equals( other.secondNode ) || this.secondNode.equals( other.firstNode ) ) && this.channelName.equals( other.channelName ) && this.extentName.equals( other.extentName ) && this.isGlobal == other.isGlobal;
//        rv = ( this.firstNode.equals( other.firstNode ) && this.secondNode.equals( other.secondNode ) && this.channelName.equals( other.channelName ) && this.extentName.equals( other.extentName ) && this.isGlobal == other.isGlobal );
        return rv;
    }

    public String toString()
    {
        StringBuffer asString = new StringBuffer( channelName );
        asString.append( "(" );
        asString.append( extentName );
        asString.append( ") : " );
        asString.append( firstNode );
        asString.append( " <--> " );
        asString.append( secondNode );
        return asString.toString();
    }

    public String getChannelName()
    {
        return channelName;
    }

    public boolean isDNToDNACS()
    {
        boolean rv = true;
        if( firstNode.getType() == SBTNodeType.DN_NODE || secondNode.getType() == SBTNodeType.DN_NODE )
        {
            rv = false;
        }
        return rv;
    }

    public SBTNode[] getLinkNodes()
    {
        return new SBTNode[]
        {
            firstNode, secondNode
        };
    }

    public SBTNode otherNode( SBTNode node )
    {
        SBTNode rv = null;
        if( firstNode.equals( node ) )
        {
            rv = secondNode;
        }
        else
        {
            if( secondNode.equals( node ) )
            {
                rv = firstNode;
            }
            else
            {
                throw new IllegalArgumentException( "ChannelLink::otherNode - node " + node.getName() + " not present in link!" );
            }
        }
        return rv;
    }

    public ChannelLink( SBTNode first, SBTNode second, Topic link, boolean global )
    {
        firstNode = first;
        secondNode = second;
        channelName = link.getChannelName();
        extentName = link.getExtentName();
        isGlobal = global;
    }

    public boolean isGlobal()
    {
        return isGlobal;
    }

    public String getExtent()
    {
        return extentName;
    }
    public static class ChannelLinkChannelFilter implements Filter
    {
        String targetName = null;

        public boolean accept( Object o )
        {
            if( !( o instanceof ChannelLink ) )
            {
                return false;
            }
            return ( (ChannelLink)o ).channelName.indexOf( targetName ) != -1;
        }

        public ChannelLinkChannelFilter( String channelName )
        {
            targetName = channelName;
        }
    }
}
