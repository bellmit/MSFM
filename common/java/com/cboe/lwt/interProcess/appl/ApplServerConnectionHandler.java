/*
 * Created on Jun 3, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cboe.lwt.interProcess.appl;

import java.io.IOException;

import com.cboe.lwt.byteUtil.ByteVector;
import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.interProcess.ConnectionHandlerFactory;
import com.cboe.lwt.interProcess.IpcException;
import com.cboe.lwt.interProcess.NapiReader;
import com.cboe.lwt.interProcess.NapiWriter;
import com.cboe.lwt.interProcess.TcpIpc;
import com.cboe.lwt.thread.ThreadTask;

/**
 * @author dotyl
 */
public class ApplServerConnectionHandler 
    extends ThreadTask
{
    protected final static byte       APPL_SERVICE_ACCEPT_CODE            = 0;
    protected static final ByteVector APPL_SERVICE_CONNECT_ACCEPT_DATA    = ByteVector.getInstance( " HOST GATEWAY - CONNECTION ACCEPTED" );
    protected static final ByteVector APPL_SERVICE_DISCONNECT_ACCEPT_DATA = ByteVector.getInstance( " HOST GATEWAY - DISCONNECT ACCEPTED" );

    static 
    {
        APPL_SERVICE_CONNECT_ACCEPT_DATA.set( APPL_SERVICE_ACCEPT_CODE, 0 );
        APPL_SERVICE_DISCONNECT_ACCEPT_DATA.set( APPL_SERVICE_ACCEPT_CODE, 0 );
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    // inner class

    public class Factory
        implements ConnectionHandlerFactory
    {
        int     __readSize; 
        String  __origin; 
        String  __dest;     
        
        
        public Factory( int p_readSize )
        {
            this( p_readSize,
                  ApplMessage.NO_ADDR_STRING,
                  ApplMessage.NO_ADDR_STRING );
        }
        

        public Factory( int    p_readSize,
                        String p_origin,
                        String p_dest )
        {
            __readSize = p_readSize;
            __origin   = p_origin;  
            __dest     = p_dest;    
        }
            
        
        
        public ThreadTask getHandler( String p_name,
                                      int    p_connectionNumber,
                                      TcpIpc p_ipc )
        {
            return new ApplServerConnectionHandler(  p_name,             
                                                     p_connectionNumber, 
                                                     p_ipc,
                                                     __readSize,
                                                     __origin,  
                                                     __dest );  
        }
    
    }
    
    // inner class
    ///////////////////////////////////////////////////////////////////////////
    
    
    private int        id;
    private TcpIpc     ipc;
    
    private NapiReader in;
    private NapiWriter out;
    
    private byte[]     readBuf;

    private ByteVector origin;
    private ByteVector dest;
    
                    
    protected ApplServerConnectionHandler( String p_name,
                                           int    p_id,
                                           TcpIpc p_ipc,
                                           int    p_readSize  )
    { 
        this( p_name,
              p_id,
              p_ipc,
              p_readSize,
              ApplMessage.NO_ADDR_STRING,
              ApplMessage.NO_ADDR_STRING );
    }
    
    
    protected ApplServerConnectionHandler( String p_name,
                                           int    p_id,
                                           TcpIpc p_ipc,
                                           int    p_readSize,
                                           String p_origin,
                                           String p_dest )
    {
        super( p_name + p_id );

        id  = p_id;
        ipc = p_ipc;
        out = new NapiWriter( ipc );
        in  = new NapiReader( ipc );

        readBuf = new byte[ p_readSize ];

        origin = ByteVector.getInstance( p_origin );
        dest = ByteVector.getInstance( p_dest );
    }
    
    
    protected int getId()
    {
        return id;
    }
    
        
    public void doTask()
        throws IOException
    {
        try
        {
            int blockLength;
            ByteVector block = ByteVector.getInstance();

            while ( ( blockLength = in.getBlock( readBuf ) ) > 0 )
            {
                block.rebase( readBuf, 0, blockLength );
                ApplMessage recvMsg = ApplMessage.createFromBytes( block );
                    
                switch ( recvMsg.getCommand() )
                {
                    case ApplMessage.CONNECT_PRIMARY:
                    handleConnectPrimary( recvMsg );
                    break;
                
                    case ApplMessage.CONNECT_SECONDARY:
                    handleConnectSecondary( recvMsg );
                    break;
                    
                    case ApplMessage.DISCONNECT_PRIMARY:
                    handleDisconnectPrimary( recvMsg );
                    return;
                    
                    case ApplMessage.DISCONNECT_SECONDARY:
                    handleDisconnectSecondary( recvMsg );
                    return;
                    
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
            
                    // For Server
                    case ApplMessage.HEARTBEAT_REQUEST:
                    handleHeartbeatRequest( recvMsg );
                    break;
                    
                    default:
                    handleUnknown( recvMsg );
                    break;
                }  
            }
        }
        catch( IOException ex )
        {
            Logger.error( "APPL SERVER : Exception in Connection Worker Thread", ex );
        }
        finally
        {
            try
            {
                ipc.disconnect();
                handleSocketDisconnected();
                signalKill();
            }
            catch( IpcException ex )
            {
                Logger.info( "APPL SERVER : socket disconnect error", ex );
            }
        }
        
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    // msg receive methods
    
    
    protected void handleConnectPrimary( ApplMessage p_recvMsg )
        throws IOException
    {
        Logger.trace( "APPL SERVER : Received Primary Connect Request" );
     
        sendConnectAccept( p_recvMsg );
    }
        
    
    protected void handleConnectSecondary( ApplMessage p_recvMsg )
        throws IOException
    {
        Logger.trace( "APPL SERVER : Received Secondary Connect Request" );
          
        sendConnectAccept( p_recvMsg );
    }
        
        
    protected void handleDisconnectPrimary( ApplMessage p_recvMsg )
        throws IOException
    {
        Logger.trace( "APPL SERVER : Received Disconnect Primary Request" );
     
        sendDisconnectAccept(p_recvMsg );
    }
    
    
    protected void handleDisconnectSecondary( ApplMessage p_recvMsg )
        throws IOException
    {
        Logger.trace( "APPL SERVER : Received Disconnect Primary Request" );
     
        sendDisconnectAccept( p_recvMsg );
    }
    
    
    protected void handleData( ApplMessage p_message )
        throws IOException  // though these methods don't throw this exception, overrides might
    {
        Logger.trace( "APPL SERVER: Received Data" );
    }
    
    
    protected void handleDataWithConfirm( ApplMessage p_recvMsg )
        throws IOException
    {
        Logger.trace( "APPL SERVER: Received Data With Confirm" );

        sendReply( p_recvMsg, 
                   ApplMessage.CONFIRM_RESPONSE, 
                   null );
    }    


    protected void handleDataReject( ApplMessage p_recvMsg )
        throws IOException  // though these methods don't throw this exception, overrides might
    {
        Logger.trace( "APPL SERVER: Received Data Reject Notification" );
    }


    protected void handleConfirmResponse( ApplMessage p_recvMsg )
        throws IOException  // though these methods don't throw this exception, overrides might
    {
        Logger.trace( "APPL SERVER: Received Data Confirm Response" );
    }
            
            
    protected void handleHeartbeatRequest( ApplMessage p_recvMsg )
        throws IOException
    {
        sendReply( p_recvMsg, 
                   ApplMessage.HEARTBEAT_RESPONSE, 
                   null,
                   0,
                   0 );
    }


    protected void handleUnknown( ApplMessage p_recvMsg )
        throws IOException
    {
        Logger.trace( "APPL SERVER : Received Unknown Message" );
        
        sendReply( p_recvMsg, 
                   ApplMessage.DATA_REJECT, 
                   p_recvMsg.getData() );
    }


    protected void handleSocketDisconnected()
        throws IOException  // though these methods don't throw this exception, overrides might
    {
        Logger.trace( "APPL SERVER : Socket Disconnected" );
    }


    // msg receive methods
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    // msg send methods
    
    
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
                                                                                         
            synchronized( out )
            {
                out.write( msg.iterator(), msg.length() );
                out.flush();
            }
        }
        else
        {
            Logger.error( "APPL SERVER : attempting to reply to appl message while disconnected" );
        }
    }

    
    protected void sendReply( ApplMessage p_recvMsg,
                              byte        p_command, 
                              byte[]      p_msg,
                              int         p_msgOffset,
                              int         p_msgLength ) 
        throws IOException
    {
        sendReply( p_recvMsg,
                   p_command,
                   ByteVector.getInstance( p_msg, 
                                           p_msgOffset, 
                                           p_msgLength ) );
    }
    
    
    protected void sendReply( ApplMessage p_recvMsg,
                              byte        p_command, 
                              ByteVector  p_msg ) 
        throws IOException
    {
        if ( out != null )
        {
            ByteVector reply = p_recvMsg.constructReply( p_command, 
                                                         p_msg );
            
            synchronized( out )
            {
                out.write( reply.iterator(), reply.length() );
                out.flush();
            }
        }
        else
        {
            Logger.error( "APPL SERVER : attempting to reply to appl message while disconnected" );
        }
    }


    protected void sendConnectAccept( ApplMessage p_recvMsg ) throws IOException
    {
        sendReply( p_recvMsg,
                   ApplMessage.CONNECT_ACCEPT,
                   APPL_SERVICE_CONNECT_ACCEPT_DATA );
    }


    protected void sendDisconnectAccept( ApplMessage p_recvMsg ) throws IOException
    {
        sendReply( p_recvMsg,
                   ApplMessage.DISCONNECT_ACCEPT,
                   APPL_SERVICE_DISCONNECT_ACCEPT_DATA );
    }
    
    
    // msg send methods
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////


}
