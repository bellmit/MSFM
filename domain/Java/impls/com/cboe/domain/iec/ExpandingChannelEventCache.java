package com.cboe.domain.iec;

import com.cboe.util.channel.ChannelEvent;

public class ExpandingChannelEventCache implements ChannelEventCache
{
    private ChannelEvent cache[];
    private int cacheStackPointer;
    private int currentCacheCapacity;

    ExpandingChannelEventCache()
    {
        cache = new ChannelEvent[ currentCacheCapacity = 128 ];
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
        if ( ++cacheStackPointer == currentCacheCapacity )
        {
           int newCacheCapacity = (currentCacheCapacity+1)*2;
           ChannelEvent newCache[] = new ChannelEvent[ newCacheCapacity ];
           System.arraycopy( cache,               // Source
                             0,                   // Source position
                             newCache,            // Destination
                             0,                   // Destination position
                             currentCacheCapacity // length
                            );
          currentCacheCapacity = newCacheCapacity;
          cache = newCache;
        }
        cache[ cacheStackPointer ] = value;
    }

    public synchronized void clear()
    {
        cache = new ChannelEvent[0];
        cacheStackPointer = -1;
        currentCacheCapacity = 0;
    }
}
