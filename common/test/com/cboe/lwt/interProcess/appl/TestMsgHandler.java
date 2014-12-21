/*
 * Created on Mar 3, 2005
 */
package com.cboe.lwt.interProcess.appl;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteVector;
import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.interProcess.ConnectionHandlerFactory;
import com.cboe.lwt.interProcess.TcpIpc;
import com.cboe.lwt.queue.InterThreadQueue;
import com.cboe.lwt.queue.QueueException;
import com.cboe.lwt.queue.QueueInterruptedException;
import com.cboe.lwt.thread.ThreadTask;

public class TestMsgHandler 
    extends ApplServerConnectionHandler
{
    InterThreadQueue cmds;
    
    
    public boolean cmdsDone()
    {
        cmds.attemptFlush();
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
    
    
    TestMsgHandler( String p_name,
                    int    p_connectionNumber,
                    TcpIpc p_ipc )
    {
        super( p_name, p_connectionNumber, p_ipc, 128 );
        cmds = InterThreadQueue.getInstance( "cmds", 1, 128 );
    }
    
    
    static final class Factory
        implements ConnectionHandlerFactory
    {
        private TestMsgHandler curHandl = null;
        
        public synchronized TestMsgHandler getCurMsgHandler()
        {
            return curHandl;
        }
        

        public ThreadTask getHandler( String p_name,
                                      int    p_connectionNumber,
                                      TcpIpc p_ipc )
        {
            curHandl = new TestMsgHandler( p_name,
                                           p_connectionNumber,
                                           p_ipc );
            return curHandl;
        }
        
    }
    
    
    private static Factory factory = new Factory();
    
    static Factory getFactory() 
    {
        return factory;
    }
    

    protected void handleConfirmResponse( ApplMessage p_recvMsg )
        throws IOException
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

    protected void handleConnectPrimary( ApplMessage p_recvMsg )
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleConnectPrimary" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleConnectPrimary( p_recvMsg );
    }

    protected void handleConnectSecondary( ApplMessage p_recvMsg )
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleConnectSecondary" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleConnectSecondary( p_recvMsg );
    }

    protected void handleData( ApplMessage p_recvMsg )
        throws IOException
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
        super.handleData( p_recvMsg );
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

    protected void handleDisconnectPrimary( ApplMessage p_recvMsg )
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleDisconnectPrimary" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleDisconnectPrimary( p_recvMsg );
    }

    protected void handleDisconnectSecondary( ApplMessage p_recvMsg )
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleDisconnectSecondary" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleDisconnectSecondary( p_recvMsg );
    }

    protected void handleHeartbeatRequest( ApplMessage p_recvMsg )
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleHeartbeatRequest" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleHeartbeatRequest( p_recvMsg );
    }

    protected void handleDataReject( ApplMessage p_recvMsg )
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleReject" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleDataReject( p_recvMsg );
    }

    
    protected void handleUnknown( ApplMessage p_recvMsg )
        throws IOException
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
        super.handleUnknown( p_recvMsg );
    }

    
    protected void handleSocketDisconnected()
        throws IOException
    {
        try
        {
            cmds.enqueue( "handleSocketDisconnected" );
        }
        catch( QueueException ex )
        {
            Logger.critical( "shutting down", ex );
            System.exit( -1 );
        }       
        super.handleSocketDisconnected();
    }

    protected void sendConnectAccept( ApplMessage p_recvMsg )
        throws IOException
    {
        // TODO Auto-generated method stub
        super.sendConnectAccept( p_recvMsg );
    }

    protected void sendDisconnectAccept( ApplMessage p_recvMsg )
        throws IOException
    {
        // TODO Auto-generated method stub
        super.sendDisconnectAccept( p_recvMsg );
    }

    protected void sendReply( ApplMessage p_recvMsg,
                             byte p_command,
                             byte[] p_msg,
                             int p_msgOffset,
                             int p_msgLength )
        throws IOException
    {
        // TODO Auto-generated method stub
        super.sendReply( p_recvMsg,
                         p_command,
                         p_msg,
                         p_msgOffset,
                         p_msgLength );
    }

    protected void sendReply( ApplMessage p_recvMsg,
                             byte p_command,
                             ByteVector p_msg )
        throws IOException
    {
        // TODO Auto-generated method stub
        super.sendReply( p_recvMsg,
                         p_command,
                         p_msg );
    }

}
