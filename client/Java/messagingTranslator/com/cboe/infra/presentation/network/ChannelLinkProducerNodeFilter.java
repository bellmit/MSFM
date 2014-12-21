package com.cboe.infra.presentation.network;


import com.cboe.infra.presentation.filter.Filter;

public class ChannelLinkProducerNodeFilter implements Filter
{
    private SBTNode producer; // i.e. publisher

    public boolean accept( Object o )
    {
        boolean rv = false;
	    try {
			DirectedChannelLink link = (DirectedChannelLink)o;
			rv = link.getPublisher().equals( producer );
	    } catch (ClassCastException cce) {}
        return rv;
    }

    public ChannelLinkProducerNodeFilter( SBTNode toLookFor )
    {
        producer = toLookFor;
    }
}
