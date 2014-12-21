package com.cboe.util.channel;

public class BoundedChannelEventCache extends ChannelEventCache
{
    private ChannelEvent cache[];
    private int cacheStackPointer;
    private int maxCacheSize;

    BoundedChannelEventCache()
    {
        cache = new ChannelEvent[ 32 ];
        cacheStackPointer = -1;
    }
    public synchronized ChannelEvent getChannelEvent()
    {
        ChannelEvent retVal = null;
        if ( 0 <= cacheStackPointer )
        {
            retVal = cache[ cacheStackPointer ];
            cache[ cacheStackPointer-- ] = null; // Give gc a chance to work
        }
        else
        {
            retVal = new ChannelEvent();
        }
        return retVal;
    }
    public synchronized void returnChannelEvent( ChannelEvent value )
    {
        if ( ++cacheStackPointer < 32 )
        {
            cache[ cacheStackPointer ] = value;
        }
        else
        {
            --cacheStackPointer;
        }
    }

    public synchronized void clear()
    {
        cache = new ChannelEvent[0];
        cacheStackPointer = -1;
        maxCacheSize = 0;
    }
}
