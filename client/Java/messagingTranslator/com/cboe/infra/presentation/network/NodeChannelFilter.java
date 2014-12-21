package com.cboe.infra.presentation.network;

import com.cboe.infra.presentation.filter.Filter;

public class NodeChannelFilter implements Filter
{
    private String channelName = null;
    
    public NodeChannelFilter( String includeChanneName ) 
    {
        this.channelName = includeChanneName;
    }
    
    public void setChannelName( String includeChannelName )
    {
        this.channelName = includeChannelName;
    }
    
    public String getChannelName()
    {
        return this.channelName;
    }
    
    public boolean accept( Object o )
    {
        if( !( o instanceof SBTNode ) )
        {
            return false;
        }
        if( channelName == null )
        {
            return true;
        }
        return ( (SBTNode)o ).belongsToChannel( channelName );
    }
    
    public NodeChannelFilter() 
    {
        
    }
} // end NodeChannelFilter
