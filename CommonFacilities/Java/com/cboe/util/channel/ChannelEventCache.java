package com.cboe.util.channel;

/**
 * This class is a singleton cache for ChannelEvent Object Pooling.  The default cache class is <code>ExpandingChannelEventCache<code>
 * To use other class, specify -DChannelEventCacheClass property such as com.cboe.application.test.DebugChannelEventCache
 * @author Craig Murphy
 * @author Connie Feng
 * @author Gijo Joseph
 */
public abstract class ChannelEventCache
{
    static volatile ChannelEventCache cache = null;
    
    static public ChannelEventCache getChannelEventCache()
    {
    	ChannelEventCache cec = cache;
        if ( cec == null )
        {
        	cec = tryCreateChannelEventCache();
        }
        return cec;
    }


    static protected synchronized ChannelEventCache tryCreateChannelEventCache()
    {
        if ( null == cache )
        {
            String cacheClass = System.getProperty( "ChannelEventCacheClass" );
            if ( cacheClass == null )
            {

                cache = new ExpandingChannelEventCache();
            }
            else
            {
                try
                {
                    cache =(ChannelEventCache)((Class.forName( cacheClass )).newInstance());
                }
                catch(ClassNotFoundException e)
                {
                    System.err.println( "Error: unable to find ChannelEventCache class: " + cacheClass );
                }
                catch(java.lang.IllegalAccessException iae)
                {
                    System.err.println( "Error: unable to Access ChannelEventCache class: " + cacheClass );
                }
                catch(java.lang.InstantiationException ine)
                {
                    System.err.println( "Error: unable to Instantiate ChannelEventCache class: " + cacheClass );
                }
            }
        }
        return cache;
    }

    /**
     * gets a ChannelEvent from the cache.
     */
    abstract public ChannelEvent getChannelEvent();

    /**
     * returns a ChannelEvent to the cache.
     */
    abstract public void returnChannelEvent( ChannelEvent value );

    abstract public void clear();
}
