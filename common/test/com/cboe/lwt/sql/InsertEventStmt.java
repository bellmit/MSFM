package com.cboe.lwt.sql;

import java.sql.SQLException;

import oracle.jdbc.driver.OracleCallableStatement;
import oracle.jdbc.driver.OracleConnection;

import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.thread.ThreadTask;


public class InsertEventStmt
{
    ////////////////////////////////////////////////////////////////////////////
    // Inner class
    
    private class FlushThread extends ThreadTask
    {
        private int autoFlush_MS;

        FlushThread( int p_autoFlush_MS )
        {
            super( baseName + " - Flush Thread" );
            autoFlush_MS = p_autoFlush_MS;
        }

        public synchronized void doTask()
            throws InterruptedException
        {
            while ( true )
            {
                wait( autoFlush_MS );
                try
                {
                    flush();
                }
                catch ( SQLException ex )
                {
                    Logger.critical( "Flush thread shut down by exception", ex );
                }
            }
        }
    };
    
    // Inner class
    ////////////////////////////////////////////////////////////////////////////

    
    OracleConnection        dbConnection;
    OracleCallableStatement dbStatement;
    int                     blockingFactor;
    int                     currentBlockCount;
    String                  baseName = "Insert Event Statement";

    ThreadTask              flushThread = null;

    
    public InsertEventStmt( OracleConnection p_connection,
                            int              p_blockingFactor,
                            int              p_flushInterval_MS )
            throws SQLException 
    {
        dbConnection = p_connection;
        String sqlString = 
              "INSERT INTO dotyl.event                                                                                       "
            + "    ( unitId, entityId, extraInfo, timeStamp, flags, machine, processID, timeStampMillis, databaseIdentifier )"
            + "VALUES                                                                                                        "
            + "    ( ?,      ?,        ?,         ?,         ?,     ?,       ?,         ?,               ?                  )";
            
        dbStatement = (OracleCallableStatement)p_connection.prepareCall( sqlString );
        
        blockingFactor = p_blockingFactor;
        currentBlockCount = 0;
        
        if ( p_flushInterval_MS > 0 )
        {
            flushThread = new FlushThread( p_flushInterval_MS );
            flushThread.go();
        }
    }


    public void close()
        throws SQLException
    {
        dbStatement.close();
    }


    public void execute( int p_unitId,
                         int p_entityId,
                         int p_extraInfo,
                         int p_timeStamp,
                         int p_flags,
                         int p_machine,
                         int p_processID,
                         int p_timeStampMillis,
                         int p_databaseIdentifier )
        throws SQLException
    {
        dbStatement.setInt( 1, p_unitId             );
        dbStatement.setInt( 2, p_entityId           );
        dbStatement.setInt( 3, p_extraInfo          );
        dbStatement.setInt( 4, p_timeStamp          );
        dbStatement.setInt( 5, p_flags              );
        dbStatement.setInt( 6, p_machine            );
        dbStatement.setInt( 7, p_processID          );
        dbStatement.setInt( 8, p_timeStampMillis    );
        dbStatement.setInt( 9, p_databaseIdentifier );
 
        dbStatement.addBatch();
        ++currentBlockCount;
        
        if ( currentBlockCount >= blockingFactor )
        {
            flush();
        }
    }


    public void flush()
        throws SQLException
    {
        if ( currentBlockCount > 0 )
        {
            dbStatement.executeBatch();
            currentBlockCount = 0;
        }
    }
} 
