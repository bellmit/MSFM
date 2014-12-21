/*
 * TpfReceiver.java
 *
 * Created on August 16, 2002, 2:39 PM
 */

package com.cboe.lwt.interProcess.appl;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteVector;
import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.interProcess.InterProcessConnection;
import com.cboe.lwt.interProcess.NapiReader;
import com.cboe.lwt.interProcess.NapiWriter;
import com.cboe.lwt.thread.ThreadTask;


/**
 * Active object that connects to TPF through a port and receives blocks for processing
 *
 * NOTE: this class will be replaced by the ApplClient-esque class when we integrate with 
 * the infrastructure
 *
 * @author  dotyl
 */
public abstract class ApplClient extends ThreadTask
{
    private   byte[]                 readBuf;

    protected InterProcessConnection ipc;
    protected NapiWriter             out;
    protected NapiReader             in;
    
    protected ByteVector             origin;
    protected ByteVector             dest;
    protected int                    connectRetry_MS;
    
    private   Object                 connectMonitor;
    private   boolean                isConnected;
         
    /**
     * @param p_tpfAddresses Ordered list of URLs to use to connect to the TPF server
     * @param p_readBuffSize size of the buffer which the socket will read bytes into
     * @param p_blockProcessor the processor that accepts raw TPF blocks and breaks them up for further processing
     */    
    public ApplClient( String                 p_name,
                       InterProcessConnection p_ipc,
                       int                    p_readBuffSize,
                       String                 p_origin,
                       String                 p_dest,
                       int                    p_connectRetry_MS )
    {
        super( p_name + ".reader" );

        ipc             = p_ipc;
        
        readBuf         = new byte[ p_readBuffSize ];
        out             = new NapiWriter( ipc );  
        in              = new NapiReader( ipc );
        
        dest            = ByteVector.getInstance( p_origin );
        origin          = ByteVector.getInstance( p_dest );
        connectRetry_MS = p_connectRetry_MS;
        connectMonitor  = new Object();
        isConnected     = false;
    }
    
    
    /**
     * task thread method:  receives blocks, pprocesses them
     */
    protected final void doTask() 
        throws IOException
    {
        ipc.connect( connectRetry_MS ); 
        
        sendApplLogin();
        ByteVector block = ByteVector.getInstance();
        
        try
        {
            while ( true )
            {
                int blockLength = in.getBlock( readBuf );
                
                block.rebase( readBuf, 0, blockLength );
                ApplMessage recvMsg = ApplMessage.createFromBytes( block );
                
                switch ( recvMsg.getCommand() )
                {
                    case ApplMessage.CONNECT_ACCEPT:
                    handleConnectAccept( recvMsg );
                    break;
            
                    case ApplMessage.CONNECT_REJECT:
                    handleConnectReject( recvMsg );
                    break;
                
                    // For Server
                    case ApplMessage.DISCONNECT_ACCEPT:
                    handleDisconnectAccept( recvMsg );
                    signalKill();
                    return;
                
                    // For Server
                    case ApplMessage.DISCONNECT_REJECT:
                    handleDisconnectReject( recvMsg );
                    break;
                
                    case ApplMessage.DATA:
                    handleData( recvMsg );
                    break;
                
                    case ApplMessage.DATA_WITH_CONFIRM:
                    handleDataWithConfirm( recvMsg );
                    break;
               
                    case ApplMessage.DATA_REJECT:
                    handleDataReject( recvMsg );
                    break;
                    
                    case ApplMessage.CONFIRM_RESPONSE:
                    handleConfirmResponse( recvMsg );
                    break;
                    
                    case ApplMessage.HEARTBEAT_RESPONSE:
                    handleHeartbeatResponse( recvMsg );
                    break;
    
                    default:
                    Logger.error( "APPL CLIENT : Unknown data ignored : " 
                                  + recvMsg.getData().toString() );
                    handleUnknown( recvMsg );
                    break;
                }     
            }
        }
        catch ( IOException ex )
        {
            Logger.critical( "APPL CLIENT : Connection terminated by IOException", ex );
        }
        finally
        {
            ipc.disconnect();
            handleSocketDisconnected();
        }
    }
    
    
    protected void handleConnectAccept( ApplMessage p_recvMsg )
    {
        Logger.trace( "APPL CLIENT : Received Connect Accept" );
        synchronized ( connectMonitor )
        {
            isConnected = true;
            connectMonitor.notifyAll();
        }
    }
    
    
    protected void handleConnectReject( ApplMessage p_recvMsg )
    {
        Logger.error( "APPL CLIENT : Received Connect Reject" );
    }
    
    
    protected void handleDisconnectAccept( ApplMessage p_recvMsg )
    {
        Logger.trace( "APPL CLIENT : Received Disconnect Accept" );
    }
    
    
    protected void handleDisconnectReject( ApplMessage p_recvMsg )
    {
        Logger.error( "APPL CLIENT : Received Disconnect Reject" );
    }


    protected void handleHeartbeatResponse( ApplMessage p_recvMsg )
    {
        Logger.trace( "APPL CLIENT : Received Heartbeat Response" );
    }


    protected void handleUnknown( ApplMessage p_recvMsg )
        throws IOException
    {
        Logger.trace( "APPL CLIENT : Received Unknown Message" );
        
        sendReply( p_recvMsg, 
                   ApplMessage.DATA_REJECT,
                   p_recvMsg.getData() );
    }


    protected void handleSocketDisconnected()
    {
        Logger.trace( "APPL CLIENT : Socket Disconnected" );
    }
    
    
    protected void handleData( ApplMessage p_message )
    {
        Logger.trace( "APPL CLIENT : Received Data" );
    }
    
    
    protected void handleDataWithConfirm( ApplMessage p_recvMsg )
        throws IOException
    {
        Logger.trace( "APPL CLIENT : Received Data With Confirm" );

        sendReply( p_recvMsg, 
                   ApplMessage.CONFIRM_RESPONSE, 
                   null );
    }    


    protected void handleDataReject( ApplMessage p_recvMsg )
    {
        Logger.error( "APPL CLIENT : Received Data Reject Notification" );
    }


    protected void handleConfirmResponse( ApplMessage p_recvMsg )
    {
        Logger.trace( "APPL CLIENT : Received Data Confirm Response" );
    }
        

    protected void sendReply( ApplMessage p_recvMsg,
                              byte        p_command, 
                              ByteVector  p_msg ) 
        throws IOException
    {
        ByteVector reply = p_recvMsg.constructReply( p_command, 
                                                     p_msg );
                                                                                     
        out.write( reply.iterator(), reply.length() );
        out.flush();
    }
    
    
    private void sendApplLogin()
        throws IOException
    {
        send( ApplMessage.CONNECT_PRIMARY,
              null );
        
        Logger.trace( "APPL CLIENT : Login Sent" );
    }
    
    
    
    public final void waitForConnect() 
        throws InterruptedException
    {
        synchronized ( connectMonitor )
        {
            while ( ! isConnected )
            {
                connectMonitor.wait();
            }
        }
    }
    
    
    public final void sendData( ByteVector p_msg ) 
        throws IOException
    {
        send( ApplMessage.DATA, p_msg );
    }

    
    public final void send( byte        p_command,
                            ByteVector  p_msg ) 
        throws IOException
    {
        if ( out != null )
        {
            ByteVector msg = ApplMessage.createApplMessageV1( p_command, 
                                                              origin,
                                                              dest,
                                                              ApplMessage.NO_KEY,
                                                              p_msg );
                                                                                         
            out.write( msg.iterator(), msg.length() );
            out.flush();
        }
        else
        {
            Logger.error( "APPL SERVER : attempting to reply to appl message while disconnected" );
        }
    }
    
    
};
