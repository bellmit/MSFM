package com.cboe.lwt.testUtils;

/**
 *
 * @author  dotyl
 */
public class SystemUtils
{
    
    /** non instantiatable */
    private SystemUtils()
    {
    }
    
    
    public static long getThroughput( long p_numberProcessed, long p_startTime_MS, long p_endTime_MS )
    {
        long elapsedTime = p_endTime_MS - p_startTime_MS;
        if ( elapsedTime > 0 )
        {
            assert ( ( p_endTime_MS - p_startTime_MS ) > 0 ) : "Start Time : " + p_startTime_MS + " End Time : " + p_endTime_MS;
            return ( ( p_numberProcessed * 1000 ) / ( elapsedTime ) );
        }

        System.out.println( "(" + p_numberProcessed + ") processed in 0 time... possible test error or compiler optimization" );
        return 0;
    }
    
    
    public static void pauseForOtherThreads()
    {
        pauseForOtherThreads( 200 );
    }
    
    public static void pauseForOtherThreads( int p_timeToPause )
    {
        try
        {
            Thread.sleep( p_timeToPause );
        }
        catch( InterruptedException ex )
        {
            ex.printStackTrace();
            throw new RuntimeException( "Interrupted" );
        }
    }
}
