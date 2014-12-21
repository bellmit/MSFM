/*
 * Created on Mar 3, 2005
 */
package com.cboe.lwt.interProcess.appl;

import java.io.IOException;

import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.interProcess.InterProcessConnection;
import com.cboe.lwt.interProcess.appl.ApplClient;
import com.cboe.lwt.interProcess.appl.ApplMessage;
import com.cboe.lwt.queue.InterThreadQueue;
import com.cboe.lwt.queue.QueueException;
import com.cboe.lwt.queue.QueueInterruptedException;

public class TestClient extends ApplClient
{
    InterThreadQueue cmds;
    
    public boolean cmdsDone()
    {
        return cmds.isEmpty();
    }
    
    
    public String popCmd()
    {
        try
        {
            cmds.flush();
            String result = (String)cmds.dequeue( 500 );
            
            if ( result == null )
            {
                cmds.flush();
                result = (String)cmds.dequeue( 1000 );
            }
            return result;
        }
        catch( QueueInterruptedException ex )
        {
            // TODO Auto-generated catch block
            Logger.critical( "QueueInterrupted", ex );
            System.exit( -1 );
            return null;
        }
    }
    
    
    TestClient( InterProcessConnection p_ipc )
    {
        super( "Client",
               p_ipc, 
               1024,
               "        ",
               "        ", 
               2000 );
        cmds = InterThreadQueue.getInstance( "cmds", 1, 128 );
    }
     

    protected void handleConfirmResponse( ApplMessage p_recvMsg )
    {
        try
        {
            cmds.enqueue( "handleConfirmResponse" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleConfirmResponse( p_recvMsg );
    }

    protected void handleConnectAccept( ApplMessage p_recvMsg )
    {
        try
        {
            cmds.enqueue( "handleConnectAccept" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleConnectAccept( p_recvMsg );
    }

    protected void handleConnectReject( ApplMessage p_recvMsg )
    {
        try
        {
            cmds.enqueue( "handleConnectReject" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleConnectReject( p_recvMsg );
    }

    protected void handleData( ApplMessage p_message )
    {
        try
        {
            cmds.enqueue( "handleData" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleData( p_message );
    }

    protected void handleDataReject( ApplMessage p_recvMsg )
    {
        try
        {
            cmds.enqueue( "handleDataReject" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleDataReject( p_recvMsg );
    }

    protected void handleDataWithConfirm( ApplMessage p_recvMsg )
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleDataWithConfirm" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleDataWithConfirm( p_recvMsg );
    }

    protected void handleDisconnectAccept( ApplMessage p_recvMsg )
    {
        try
        {
            cmds.enqueue( "handleDisconnectAccept" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleDisconnectAccept( p_recvMsg );
    }

    protected void handleDisconnectReject( ApplMessage p_recvMsg )
    {
        try
        {
            cmds.enqueue( "handleDisconnectReject" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleDisconnectReject( p_recvMsg );
    }

    protected void handleUnknown( ApplMessage p_recvMsg )
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleUnknown" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleUnknown( p_recvMsg );
    }

}
