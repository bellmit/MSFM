/*
 * Created on Sep 30, 2004
 *
 */
package com.cboe.lwt.collection;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author dotyl
 *
 */
public class IntHashMapTest
{
 

    
    @Test
    public final void testGetPut()
    {
        IntHashMap map = new IntHashMap( 1000 );
        
        // initial correctness
        for( int i = 0; i < 100000; ++i )
        {
            map.put( i, new Integer( i ) );
            Assert.assertEquals( i, ( (Integer)map.get( i ) ).intValue() );
        }
        
        // long-term correctness
        for( int i = 0; i < 100000; ++i )
        {
        	Assert.assertEquals( i, ( (Integer)map.get( i ) ).intValue() );
        }
        
        // replacing correctness
        for( int i = 0; i < 100000; ++i )
        {
            map.put( i, new Integer( i * 2 ) );
            Assert.assertEquals( i * 2, ( (Integer)map.get( i ) ).intValue() );
        }
        
        // long-term replacing correctness
        for( int i = 0; i < 100000; ++i )
        {
        	Assert.assertEquals( i * 2, ( (Integer)map.get( i ) ).intValue() );
        }
        
    }


}
