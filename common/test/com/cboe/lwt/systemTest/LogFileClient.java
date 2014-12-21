/*
 * MeteringOpraSimulator.java
 * JUnit based test
 *
 * Created on March, 2002, 12:00 PM
 */

package com.cboe.lwt.systemTest;

import com.cboe.lwt.eventLog.ConsoleLogger;
import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.thread.ThreadTask;


public class LogFileClient
{
    public static void main( java.lang.String[] args ) 
    {
        System.out.println( "LogFileClient\n---------------------------" );
        
        try 
        {                
            int    logSeverityFilter = Integer.getInteger( "logFilter",    Logger.SEV_INFO ).intValue();           
            new ConsoleLogger( logSeverityFilter ).setGlobal();

            int    receiveBuffSize   = Integer.getInteger( "recvBuffSize", 1000000 ).intValue();          
            int    sendBuffSize      = Integer.getInteger( "sendBuffSize", 8192 ).intValue();          
            int    blockSize         = Integer.getInteger( "blockSize",    65536 ).intValue();
            String logFileBaseName   = System.getProperty( "logFileBase",  "CoppLine" );
            
            if ( args.length == 0 )
            {
                args = new String[1];
                args[0] = "47980";
            }

            ThreadTask[] listeners = new ThreadTask[ args.length ];
             
            for ( int i = 0; i < args.length; i++ )
            {       
                int port = Integer.parseInt( args[i] );
                
                listeners[i] = new TcpToFileThread( port, 
                                                    receiveBuffSize,  
                                                    sendBuffSize,
                                                    blockSize, 
                                                    logFileBaseName + i + "_",
                                                    512000000 );
                listeners[i].go();           
            }

            for ( int i = 0; i < args.length; i++ )
            {
                listeners[i].waitForTermination(); 
            }
            
            System.out.println( "All threads stopped" );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }
};
    
    
    
    