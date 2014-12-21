package com.cboe.lwt.queue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.instrumentationService.monitor.GlobalMetricsMonitor;

 
/**
 *
 * @author dotyl
 */
public class HashRouterTest extends TestCase
{
    private static class Hashed
    {
        private int hash;
        
        Hashed( int p_hash )
        {
            hash = p_hash;
        }
        
        public int hashCode()
        {
            return hash;
        }
    }
    
    
    public void testNegativeHash()
    {
        System.out.println("HashRouter: testNegativeHash");
        
        InterThreadQueue inQ = InterThreadQueue.getInstance( "IN", 2, 32 );
        
        HashRouter router = new HashRouter( inQ, InterThreadQueue.INFINITE_TIMEOUT );
        
        int LINES = 13;
        
        InterThreadQueue[] outQs = new InterThreadQueue[ LINES ];
        for ( int i = 0; i < LINES; i++ )
        {
            InterThreadQueue outQ = InterThreadQueue.getInstance( "OUT" + i, 100, 100 );
            router.enableDest( outQ );
            outQs[i] = outQ;
        }

        int NUM_ENQUEUES = 1000;
        
        router.go();
        
        for ( int i = 0; i < NUM_ENQUEUES; i++ )
        {
            Hashed obj = new Hashed( ( i % 2 == 0 ) 
                                     ? i
                                     : -i );
            
            try
            {
                inQ.enqueue( obj );
            }
            catch( QueueException ex1 )
            {
                ex1.printStackTrace();
                fail();
            }
        }
        
        inQ.flush();
        
        try
        {
            Thread.sleep( 5000 );
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            fail();
        }
        
        int numRouted = 0;
        
        for ( int i = 0; i < LINES; i++ )
        {
            int QSize = outQs[i].available();
            numRouted += QSize;
            System.out.println( "Line " + i + " had " + QSize );
            
            assertTrue( "size for queue " + i + " is too small : " + QSize, 
                        QSize > ( NUM_ENQUEUES / ( LINES * 2 ) ) );  // make sure that no line has less than half the expected number of objects
        }
        
        assertEquals( "wrong number of dequeues", NUM_ENQUEUES, numRouted ); 
    }


    public HashRouterTest(java.lang.String testName)
    {
        super(testName);
    }

        
    public static void main(java.lang.String[] args)
    {
        GlobalMetricsMonitor.initThroughputMonitor( System.out, 500 ).go();
        junit.textui.TestRunner.run(suite());
    }

    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(HashRouterTest.class);
        
        return suite;
    }

}
