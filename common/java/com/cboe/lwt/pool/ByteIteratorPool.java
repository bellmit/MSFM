/*
 * Created on Feb 18, 2005
 */
package com.cboe.lwt.pool;

import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.eventLog.Logger;

/**
 * @author dotyl
 */
public class ByteIteratorPool
{
////////////////////////////////////////////////////////////////////////////////
//  static

    private static ObjectPool pool         = null;
    private static int        pooledIterSize;  // must be bigger than largest Iter (prior day trade correction = 142)

    
    public static ObjectPool establishPool( int p_pooledIterSize,
                                            int p_maxPoolSize,
                                            int p_initialPoolSize )
    {
        assert ( pool == null )  : "ByteIterator pool already established";
        assert ( p_pooledIterSize > 0 ) : "Illegal pool allocation size for ByteIterator pool";
        
        pooledIterSize = p_pooledIterSize;
        
        try
        {
            pool = ObjectPool.getInstance( "ByteIterator Pool", p_maxPoolSize );

            for ( int i = 0; i < p_initialPoolSize; ++i )
            {
                pool.checkIn( ByteIterator.getInstance( pooledIterSize ) );
            }
            
            Logger.trace( "ByteIterator pool initialized with " + p_initialPoolSize + " ready ByteIterators" );
        }
        catch( Exception ex )
        {
            Logger.error( "Error establishing SiacBlock pool", ex );
            pool = null;
        }
        
        return pool;
    }
    
    
    public static ByteIterator checkout( int p_minSize )
    {
        if ( pool != null )
        {
            if ( pooledIterSize >= p_minSize )
            {
                ByteIterator result = (ByteIterator)pool.checkOut();

                if ( result != null )
                {
                    // set up for use
                    result.rebaseToArrayBounds();
                    result.first();
                    return result;
                }

                // if here, then pool checkout failed, but pooling is on
                result = ByteIterator.getInstance( pooledIterSize );

                return result;
            }
        }

        // create with explicit size, not pooled size
        ByteIterator result = ByteIterator.getInstance( p_minSize );

        return result;
    }
    
    
    public static void checkin( ByteIterator p_iter )
    {
        if ( pool != null )
        {
            if ( pooledIterSize <= p_iter.length() )
            {
                p_iter.rebaseToArrayBounds();
                p_iter.first();
                pool.checkIn( p_iter );
            }
        }
    }
    

}
