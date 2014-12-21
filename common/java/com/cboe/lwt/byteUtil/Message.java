/*
 * Created on Mar 15, 2005
 */
package com.cboe.lwt.byteUtil;

import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.pool.LocalCacheObjectPool;

public final class Message
{
    ////////////////////////////////////////////////////////////////////////////
    //  static
    
    private static LocalCacheObjectPool msgPool          = null;
    private static int                  poolIteratorSize = 0;

    
    public static LocalCacheObjectPool establishPool( int p_poolIteratorSize,
                                                      int p_maxPoolSize, 
                                                      int p_localCacheSize,
                                                      int p_initialPoolSize )
    {
        assert ( msgPool == null ) : "Programming Error: Pool already established";
        assert ( p_poolIteratorSize > 0 ) : "Programming Error: PoolIterator Size must be > 0";
        assert ( p_maxPoolSize > 0 ) : "Programming Error : max pool size must be > 0";
        assert ( p_localCacheSize > 0 ) : "Programming Error : local pool cache size must be > 0";
        assert ( p_initialPoolSize >= 0 ) : "Programming Error : negative value for initial pool size";

        poolIteratorSize = p_poolIteratorSize;
        
        msgPool = LocalCacheObjectPool.getInstance( "Message", p_maxPoolSize, p_localCacheSize );

        for ( int i = 0; i < p_initialPoolSize; ++i )
        {
            msgPool.checkIn( new Message( ByteIterator.getInstance( poolIteratorSize ) ) );
        }
        
        Logger.trace( "ByteIterator pool initialized with "
                      + msgPool.available() 
                      + " iterators of size "
                      + poolIteratorSize
                      + ", and a total capacity of "
                      + msgPool.capacity() );
        
        return msgPool;
    }
    
   
    public static Message getInstance( int p_minSize )
    {
        Message result = null;
        
        if ( p_minSize <= poolIteratorSize )
        {
            result = (Message)msgPool.checkOut();
        }
        
        if ( result == null )
        {
            result = new Message( ByteIterator.getInstance( poolIteratorSize ) );
        }
        
        result.msg.resetToLength( p_minSize );
        
        return result;
    }
    

    //  static
    ////////////////////////////////////////////////////////////////////////////
    // instance data
    
    private ByteIterator msg;
    
    // instance data
    ////////////////////////////////////////////////////////////////////////////
    // private constructors

    
    private Message( ByteIterator p_iter ) 
    {
        msg = p_iter;
    }
    
    
    // private constructors
    ////////////////////////////////////////////////////////////////////////////
    
    
    public final ByteIterator getIter()
    {
        return msg;
    }

    
    public final void release()
    {
        if ( msgPool != null )
        {
            if ( msg.reset() >= poolIteratorSize )
            {
                msgPool.checkIn( this );
            }
        }
    }

    

}
