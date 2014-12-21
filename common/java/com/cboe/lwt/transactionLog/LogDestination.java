/*
 * Created on Apr 11, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cboe.lwt.transactionLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.cboe.lwt.pool.ObjectPool;
import com.cboe.lwt.queue.InterThreadQueue;
import com.cboe.lwt.queue.QueueException;
import com.cboe.lwt.thread.ThreadTask;
import com.cboe.lwt.eventLog.Logger;


/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LogDestination extends ThreadTask
{
    private static final String TIME_FORMAT_STRING    = "\nHH:mm:ss| ";
    private static final int    WRITE_OVERHEAD_LENGTH = TIME_FORMAT_STRING.length();

    private int              writeTimeout;    
    private int              bufferSize;
    private InterThreadQueue writeQueue;
    private FileChannel      out;
    private String           fileBaseName;
    

    // pool of ByteBuffers to reuse
    private ObjectPool pool = null;
       
       
    public LogDestination( String p_fileBaseName, 
                           int    p_bufferSize,
                           int    p_queueBlockSize,
                           int    p_queueBlocks, 
                           int    p_writeTimeout )
        throws IOException                           
    {
        super( "Transaction log : " + p_fileBaseName );
        
        bufferSize = p_bufferSize + WRITE_OVERHEAD_LENGTH;
        
        fileBaseName = p_fileBaseName;
        writeTimeout = p_writeTimeout;
                
        writeQueue = InterThreadQueue.getInstance( "Logging : " + fileBaseName,
                                                   p_queueBlockSize,
                                                   p_queueBlocks );
                                                                                                             
        File outFile = openLogFile( fileBaseName );
        FileOutputStream outStream = new FileOutputStream( outFile );
        if ( outStream == null )
        {
            Logger.error( "Log destinatin for [" + fileBaseName + "] : Error creating File [" + outFile.getAbsolutePath() + "]" );
            throw new IOException( "Couldn't create channel" );
        }

        out = outStream.getChannel();  
        
        go();
    }


    public void establishPool( int p_maxEntries,
                               int p_initialEntries )
    {
        assert ( pool == null ) : "LogDestination's ByteBuffer pool already established";
        assert ( p_initialEntries <= p_maxEntries ) : "p_maxEntries of " + p_maxEntries + ", is less than p_initialEntries of " + p_initialEntries;
                
        if ( p_maxEntries > writeQueue.available() )
        {
            Logger.error( "---- CONFIGURATION ERROR ---- : LogDestination " + name + " has an oversized pool.  Only " + writeQueue.available() + " entries can be used." );
        }
        
        pool = ObjectPool.getInstance( "LogDest" + fileBaseName + "Pool", p_maxEntries );
        
        for ( int i = 0; i < p_initialEntries; ++i )
        {
            checkIn( ByteBuffer.allocateDirect( bufferSize ) );
        }
    }
    
    
    public void write( ByteBuffer p_buff )
    {
        try
        {
            writeQueue.enqueue( p_buff, writeTimeout );
        }
        catch ( QueueException ex )
        {
            Logger.error( "Log destinatin for [" + fileBaseName + "] : Transaction Log write failed due to full queue... discarding log" );
            checkIn( p_buff );
        }
    }
    
    
    public void flush()
    {
        writeQueue.flush();
    }
    
    
    public ByteBuffer getFreeBuffer()
    {
        ByteBuffer freeBuffer = null;
        
        if ( pool != null )
        {
            freeBuffer = (ByteBuffer)pool.checkOut();
        }
        
        if ( freeBuffer == null )
        {
            freeBuffer = ByteBuffer.allocateDirect( bufferSize );
        }
        
        freeBuffer.clear();
        
        if ( freeBuffer.remaining() < bufferSize ) 
        {
             throw new RuntimeException( "buffer allocated with insufficient space. requested " 
                                        + bufferSize + ", received " + freeBuffer.remaining() ); 
        }
        
        return freeBuffer;
    }
 
    
    public String getFileBaseName()
    {
        return fileBaseName;
    }
    
    
    public static File openLogFile( String p_fileBaseName )
        throws IOException
    {
        int curFileSuffix = 0;
        File logFile;

        String newFileName;

        while ( true )
        {
            newFileName = ( p_fileBaseName + curFileSuffix ) + ".log";
            logFile = new File( newFileName );
            
            if ( logFile.createNewFile() ) // returns false if file already exists
            {
                break;  // if here, then file was successfully created
            }
            ++curFileSuffix;
        }
         
        Logger.trace( "Created Log file : " + logFile.getAbsolutePath() );

        return logFile;
    }
    
    
    /* (non-Javadoc)
     * @see com.cboe.lwt.eventLog.WorkerThread#doTask()
     */
    protected void doTask()
    {
        try
        {
            ByteBuffer buff = (ByteBuffer)writeQueue.dequeue( 200 ); // only wait 200 MS
            
            while ( buff == null )
            {  // dequeue didn't get a buffer
                writeQueue.flush();
                buff = (ByteBuffer)writeQueue.dequeue( 10000 );  // wait 10 seconds before polling again
            }
            
            buff.flip();
            while ( buff.remaining() > 0 )
            {
                out.write( buff );
            }

            checkIn( buff );
        }
        catch ( QueueException ex )    
        {
            Logger.error( "Error dequeuing a full transaction log file buffer", ex );  // return to check status before continuing
        }
        catch ( IOException ex )
        {
            if ( Thread.interrupted() )
            {
                Logger.info( getName() + " Interrupted", ex );
                return;  // return to check status before continuing
            }
            
            Logger.critical( "IOException is shutting down transaction logging for " + getName(), ex );

            while ( true )
            {
                try
                {
                    writeQueue.dequeueMultiple();  
                }
                catch ( QueueException ex2 )
                {
                    Logger.warning( "DequeueMultiple while in no-write mode caused exception", ex2 );
                    if ( Thread.interrupted() )
                    {
                        return;// return to check status before continuing
                    }
                } 
            }
        }
    }


    private void checkIn( ByteBuffer buff )
    {
        if ( pool != null )
        {
            pool.checkIn( buff );
        }
    }

}
