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
import com.cboe.lwt.interProcess.NapiReader;
import com.cboe.lwt.interProcess.NapiWriter;


public class ApplMsgHandler 
{
    protected final static byte       APPL_SERVICE_ACCEPT_CODE            = 0;
    protected static final ByteVector APPL_SERVICE_CONNECT_ACCEPT_DATA    = ByteVector.getInstance( " HOST GATEWAY - CONNECTION ACCEPTED" );
    protected static final ByteVector APPL_SERVICE_DISCONNECT_ACCEPT_DATA = ByteVector.getInstance( " HOST GATEWAY - DISCONNECT ACCEPTED" );

    static 
    {
        APPL_SERVICE_CONNECT_ACCEPT_DATA.set( APPL_SERVICE_ACCEPT_CODE, 0 );
        APPL_SERVICE_DISCONNECT_ACCEPT_DATA.set( APPL_SERVICE_ACCEPT_CODE, 0 );
    }
    
    protected NapiReader in;
    protected NapiWriter out;
    
    private ByteVector origin;
    private ByteVector dest;
    
    
    protected ApplMsgHandler( NapiReader p_in,
                              NapiWriter p_out )
    {
        this( p_in,
              p_out,
              ApplMessage.NO_ADDR_STRING,
              ApplMessage.NO_ADDR_STRING );
    }
    
    
    protected ApplMsgHandler(  NapiReader p_in,
                               NapiWriter p_out,
                               String     p_origin,
                               String     p_dest )
    {
        assert ( p_in != null )  : "Null in param";
        assert ( p_out != null ) : "Null out param";

        in  = p_in;
        out = p_out;
        origin = ByteVector.getInstance( p_origin );
        dest = ByteVector.getInstance( p_dest );
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
                                                                                         
            out.write( msg.iterator(), msg.length() );
            out.flush();
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
                                                                                         
            out.write( reply.iterator(), reply.length() );
            out.flush();
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
