/*
 * CircularQueueTest.java
 * JUnit based test
 *
 * Created on July 12, 2002, 3:18 PM
 */

package com.cboe.lwt.queue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

 
/**
 *
 * @author dotyl
 */
public class CircularQueueTest extends TestCase
{
    public void testIsEmpty()
    {
        System.out.println("CircularQueue: testIsEmpty");
        
        
        int capacity = 32;
        Object[] objs = new Object[ capacity ];
        CircularQueue cq = new CircularQueue( capacity );
        
        for ( int i = 0; i < capacity; i++ )
        {
            objs[ i ] = new String( "" + i + "abcd" );
            cq.enqueue( objs[i] );
        }

        assertEquals( capacity, cq.available() );
        assertTrue( cq.isFull() );

        cq.clear();

        assertEquals( 0, cq.available() );
        assertTrue( cq.isEmpty() );
        assertTrue( ! cq.isFull() );

        cq.enqueue( objs[ 0 ] );
        assertEquals( 1, cq.available() );
        assertTrue( ! cq.isEmpty() );
        assertTrue( ! cq.isFull() );

        for ( int i = 1; i < capacity; ++i )
        {
            cq.enqueue( objs[ i ] );

            assertEquals( i + 1, cq.available() );
            assertTrue( ! cq.isEmpty() );
        }
        assertEquals( capacity, cq.available() );
        assertTrue( ! cq.isEmpty() );
        assertTrue( cq.isFull() );

        while ( cq.available() > 0 )
        {
            cq.dequeue();
        }
        assertEquals( 0, cq.available() );
        assertTrue( cq.isEmpty() );
        assertTrue( ! cq.isFull() );
    }

    
    public void testEnqueueDequeue()
    {
        System.out.println("CircularQueue: testEnqueue");
        
        int capacity = 5;
        Object[] objs = new Object[ capacity ];
        CircularQueue cq = new CircularQueue( capacity );
        
        for ( int i = 0; i < objs.length; i++ )
        {
            objs[ i ] = new String( "" + i + "abcd" );
        }
        
        cq.clear();

        assertEquals( 0, cq.available() );

        cq.enqueue( objs[ 0 ] );
        assertEquals( 1, cq.available() );

        cq.enqueue( objs[ 1 ] );
        assertEquals( 2, cq.available() );
        assedrtStringsEqual( objs[ 0 ], cq.dequeue() );
        assedrtStringsEqual( objs[ 1 ], cq.dequeue() );
        assertEquals( 0, cq.available() );

        cq.enqueue( objs[ 2 ] );
        cq.enqueue( objs[ 3 ] );
        cq.enqueue( objs[ 4 ] );
        assertEquals( 3, cq.available() );
        assedrtStringsEqual( objs[ 2 ], cq.dequeue() );
        assedrtStringsEqual( objs[ 3 ], cq.dequeue() );
        assedrtStringsEqual( objs[ 4 ], cq.dequeue() );

        assertEquals( 0, cq.available() );
    }
    
    
    public void testFlushToArray()
    {
        System.out.println("CircularQueue: testFlushToArray");
        
        int capacity = 5;
        Object[] objs = new Object[ capacity ];
        CircularQueue cq = new CircularQueue( capacity );
        
        for ( int i = 0; i < objs.length; i++ )
        {
            objs[ i ] = new String( "" + i + "abcd" );
        }
        
        cq.clear();

        assertEquals( 0, cq.available() );

        cq.enqueue( objs[ 0 ] );
        assertEquals( 1, cq.available() );

        cq.enqueue( objs[ 1 ] );
        assertEquals( 2, cq.available() );

        Object[] multiDq = cq.flushToArray();

        assertEquals( 2, multiDq.length );
        assedrtStringsEqual( objs[ 0 ], multiDq[ 0 ] );
        assedrtStringsEqual( objs[ 1 ], multiDq[ 1 ] );
        assertEquals( 0, cq.available() );

        cq.enqueue( objs[ 2 ] );
        cq.enqueue( objs[ 3 ] );
        cq.enqueue( objs[ 4 ] );
        assertEquals( 3, cq.available() );

        multiDq = cq.flushToArray();
        assedrtStringsEqual( objs[ 2 ], multiDq[ 0 ] );
        assedrtStringsEqual( objs[ 3 ], multiDq[ 1 ] );
        assedrtStringsEqual( objs[ 4 ], multiDq[ 2 ] );

        assertEquals( 0, cq.available() );
    }
    
    
    public void assedrtStringsEqual( Object p_a, Object p_b )
    {
        if ( p_a != p_b )
        {
            System.out.println("String (" + (String)p_a + ") is not equal to (" + (String)p_b + ")" );
            fail();
        }
    }    
    
    
    public CircularQueueTest(java.lang.String testName)
    {
        super(testName);
    }

        
    public static void main(java.lang.String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }

    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(CircularQueueTest.class);
        
        return suite;
    }

}
