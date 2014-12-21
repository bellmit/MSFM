package com.cboe.application.test;

import com.cboe.ORBInfra.Cache.CacheSingleton;
import com.cboe.util.channel.*;

/**
 * This class is to help debug ChannelEvent Object Pooling.
 * To use this class, specify -DChannelEventCacheClass=com.cboe.application.test.DebugChannelEventCache
 * To enable statistic logging and debuging use -DCacheStats=true -DCacheDebug=true respectively
 *
 * @see com.cboe.util.channel.ChannelEventCache
 * @author Craig Murphy
 * @author Connie Feng
 */
public class DebugChannelEventCache extends ChannelEventCache
{

    CacheSingleton cache;
    int cacheHandle;

    public DebugChannelEventCache()
    {
        cache = CacheSingleton.getSingleton();
        try
        {
            cacheHandle = cache.register( Class.forName( "com.cboe.util.channel.ChannelEvent" ) );
        }
        catch ( ClassNotFoundException cnfe )
        {
            System.err.println( "Error: unable to find class for com.cboe.util.channel.ChannelEvent" );
            throw new ExceptionInInitializerError( "Error: unable to find class for com.cboe.util.channel.ChannelEvent" );
        }
        catch ( com.cboe.ORBInfra.Cache.CacheRegisterException cre )
        {
            System.err.println( cre );
            throw new ExceptionInInitializerError( cre );
        }
    }
    public ChannelEvent getChannelEvent()
    {
        ChannelEvent retVal = null;
        try
        {
            retVal = (ChannelEvent) cache.get( cacheHandle );
        }
        catch ( com.cboe.ORBInfra.Cache.CacheObjectNotExist cone )
        {
            System.err.println( cone );
        }
        return retVal;
    }
    public void returnChannelEvent( ChannelEvent value )
    {
        cache.release( cacheHandle, value );
    }
    public void clear(){};
}
