package com.cboe.infra.presentation.network;


import com.cboe.infra.presentation.filter.Filter;

public class ChannelLinkNodeFilter implements Filter
{
    private SBTNode participant;
    
    public boolean accept( Object o )
    {
        boolean rv = false;
        ChannelLink link = (ChannelLink)o;
        rv = ( link.firstNode.equals( participant ) || link.secondNode.equals( participant ) );
        return rv;
    }
    
    public ChannelLinkNodeFilter( SBTNode toLookFor ) 
    {
        participant = toLookFor;
    }
}
