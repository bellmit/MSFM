/*
 * Created on Mar 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.sql;

import oracle.jdbc.driver.OracleConnection;

import com.cboe.lwt.eventLog.ConsoleLogger;
import com.cboe.lwt.eventLog.Logger;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestEventDeposit
{
    public static void main( String args[] ) 
    {
        new ConsoleLogger( Logger.SEV_TRACE ).setGlobal();
        
        int  blockingFactor   = Integer.getInteger( "blockingFactor" , 4096 ).intValue();
        int  flushInterval_MS = Integer.getInteger( "flushInterval_MS", 0 ).intValue();
        int  totalMsg         = Integer.getInteger( "rowsToInsert" , 1000000 ).intValue();
        int  remainingRows    = totalMsg;
        long start_MS         = 0;
    
        Logger.info( "Starting with :"
                     + "\n  - blockingFactor = " + blockingFactor
                     + "\n  - flushInterval  = " + flushInterval_MS + " MS"
                     + "\n  - totalMsg       = " + totalMsg
                     + "\n\nBeginning Inserts...\n\n" );
        try
        {
            OracleConnection con = EventConnection.getConnection();
             
            InsertEventStmt stmt = new InsertEventStmt( con, blockingFactor, flushInterval_MS );
            
            start_MS = System.currentTimeMillis();
            
            while( remainingRows-- > 0 )
            {
                stmt.execute( remainingRows,
                              remainingRows,
                              remainingRows,
                              remainingRows,
                              666,
                              remainingRows,
                              remainingRows,
                              remainingRows,
                              remainingRows );
            }
            stmt.flush();
        }
        catch ( java.lang.Exception ex )
        {
            System.out.println("exception: " + ex.getMessage());    
            ex.printStackTrace();
        }
        finally
        {
            long total_MS  = System.currentTimeMillis() - start_MS;
            int rowsProcessed = totalMsg - remainingRows;

            try
            {
                System.out.println( " ---------------------------" );
                
                Logger.info( " Avg Throughput : Rows<" 
                           + String.valueOf( rowsProcessed ) 
                           + "> / MS<"
                           + String.valueOf( total_MS ) 
                           + "> x 1000 = <"
                           + String.valueOf( ((double)rowsProcessed / (double)total_MS) * 1000 ) 
                           + "> Messages/Second" );
            }
            catch ( java.lang.Exception ex )
            {
                System.out.println("exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
