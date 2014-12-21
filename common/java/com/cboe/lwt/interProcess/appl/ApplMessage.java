package com.cboe.lwt.interProcess.appl;

import com.cboe.lwt.byteUtil.ByteVector;
import com.cboe.lwt.eventLog.Logger;


/**
 * This class defines an APPL-APPL message for communicating with legacy
 * applications.
 * <p>
 * Manages creating and retreiving of individual fields in the Appl-Appl
 * protocol message.
 * <p>
 * 
 * Appl-Appl protocol
 * <p>
 * 
 * VERSION1 contains the following fields
 * <p>
 *          Version(0,1) 
 *          MsgType(1,1) 
 *          Origin(2,8) 
 *          Destination(10,8) 
 *          Key(18,4) 
 *          Data(22,n)
 * 
 * VERSION2 and VERSION3 contains the following fields
 * <p>
 *          Version(0,1) 
 *          MsgType(1,1) 
 *          Origin(2,8) 
 *          Destination(10,8) 
 *          Key(18,4)
 *          HdrLen(22,1)  = X
 *          ApplHdr(23,n) 
 *          Data(X,m)

 */
public class ApplMessage
{
    public static final byte HEADER_VERSION1 = 1;
    public static final byte HEADER_VERSION2 = 2;
    public static final byte HEADER_VERSION3 = 3;
    public static final byte HEADER_VERSION4 = 4;
    public static final byte HEADER_VERSION5 = 5;
    public static final byte HEADER_VERSION6 = 6;

    public static final int MAX_APPLICATION_HEADER_LENGTH = 256;

    public static final ByteVector NO_KEY         = ByteVector.getInstance( "    " );
    public static final String     NO_ADDR_STRING = "        ";
    public static final ByteVector NO_ADDR        = ByteVector.getInstance( NO_ADDR_STRING );
    
    public static final int KEY_SIZE = 4;

    // Message types - Sent by CLIENT
    public static final byte CONNECT_PRIMARY      = 1;
    public static final byte CONNECT_SECONDARY    = 2;
    public static final byte DISCONNECT_PRIMARY   = 5;
    public static final byte DISCONNECT_SECONDARY = 6;
    public static final byte HEARTBEAT_REQUEST    = 12;
    
    // Message types - Sent by CLIENT AND SERVER
    public static final byte DATA                 = 8;
    public static final byte DATA_REJECT          = 9;
    public static final byte DATA_WITH_CONFIRM    = 10;
    public static final byte CONFIRM_RESPONSE     = 11;
 
    // Message types - Sent by SERVER
    public static final byte CONNECT_ACCEPT       = 3;
    public static final byte CONNECT_REJECT       = 4;
    public static final byte DISCONNECT_ACCEPT    = 7;
    public static final byte HEARTBEAT_RESPONSE   = 13;
    public static final byte DISCONNECT_REJECT    = 14;
    
    // Message type names
    private static final String[] commandStrings 
            =
            { 
                "", 
                "CONNECT_PRIMARY", 
                "CONNECT_SECONDARY", 
                "CONNECT_ACCEPT", 
                "CONNECT_REJECT", 
                "DISCONNECT_PRIMARY", 
                "DISCONNECT_SECONDARY", 
                "DISCONNECT_ACCEPT", 
                "DATA",
                "DATA_REJECT",
                "DATA_WITH_CONFIRM", 
                "CONFIRM_RESPONSE",
                "HEARTBEAT_REQUEST",
                "HEARTBEAT_RESPONSE", 
                "DISCONNECT_REJECT" 
            };

    private static final int V1_HEADER_LENGTH            = 22;
    private static final int V2_HEADER_LENGTH            = 23;
    private static final int V2_APP_HEADER_LENGTH_OFFSET = 22;
    private static final int V2_APP_HEADER_OFFSET        = 23;

    private static final int DEST_OFFSET   = 10;
    private static final int ORIGIN_OFFSET = 2;
    public static final int DEST_SIZE     = 8;
    public static final int ORIGIN_SIZE   = 8;
    private static final int KEY_OFFSET    = 18;

    // instance data (instances only represent deserialized messages)
    private byte       version;
    private byte       command;
    private ByteVector key;
    private ByteVector origin;
    private ByteVector dest;
    private ByteVector header;
    private ByteVector data;


    /**
     * Appl Message Constructor restricts instantiation to only this class.
     */
    private ApplMessage( ByteVector p_rawMsg )
    {
        header  = null;
        data    = null;

        version = p_rawMsg.get( 0 );
        command = p_rawMsg.get( 1 );
        
        origin = p_rawMsg.subVector( 2,  8 );
        dest   = p_rawMsg.subVector( 10, 8 );

        key     = p_rawMsg.subVector( KEY_OFFSET, KEY_SIZE );

        int dataLength = p_rawMsg.length();
        
        switch( version )
        {
        case HEADER_VERSION1 :
            dataLength -= V1_HEADER_LENGTH;

            if( dataLength > 0 )
            {
                data = p_rawMsg.subVector( V1_HEADER_LENGTH,
                                          dataLength );
            }
            break;
            
        case HEADER_VERSION2 :
        case HEADER_VERSION3 :
        case HEADER_VERSION4 :
        case HEADER_VERSION5 :
        case HEADER_VERSION6 :
            int totalHdrLength = 0xff & p_rawMsg.get( V2_APP_HEADER_LENGTH_OFFSET );
            int applHdrLength = totalHdrLength - V2_HEADER_LENGTH;
            
            dataLength -= totalHdrLength;

            if( applHdrLength > 0 )
            {
                header = p_rawMsg.subVector( V2_HEADER_LENGTH,
                                             applHdrLength );
            }

            if( dataLength > 0 )
            {
                data = p_rawMsg.subVector( totalHdrLength,
                                          dataLength );
            }

            break;
            
        default: 
            Logger.error( "Illegal Appl version number : " + version );
        }
    }
    
    
    public static ByteVector createApplMessageV1( byte       p_command,
                                                  ByteVector p_origin,
                                                  ByteVector p_dest,
                                                  ByteVector p_key,
                                                  ByteVector p_data )
    {
        assert( p_origin.length() >= ORIGIN_SIZE ) : "Illegal origin length";
        assert( p_dest.length()   >= DEST_SIZE )   : "Illegal dest length";
        assert( p_key.length()    >= KEY_SIZE )    : "Illegal key length";
        
        int dataLength = ( p_data == null ) ? 0 : p_data.length();
        int totalLength = V1_HEADER_LENGTH + dataLength;
        
        ByteVector result = ByteVector.getInstance( totalLength );
        result.set( HEADER_VERSION1, 0 );
        result.set( p_command, 1 );
        
        result.set( p_origin, 0, ORIGIN_OFFSET, ORIGIN_SIZE );
        result.set( p_dest,   0, DEST_OFFSET,   DEST_SIZE );
        result.set( p_key,    0, KEY_OFFSET,    KEY_SIZE );

        if ( p_data != null )
        {
            result.set( p_data, 0, V1_HEADER_LENGTH, p_data.length() );
        }
        
        return result;
    }
    
    
    public static ByteVector createApplMessage( byte       p_version,
                                                byte       p_command,
                                                ByteVector p_origin,
                                                ByteVector p_dest,
                                                ByteVector p_key,
                                                ByteVector p_appHeader,
                                                ByteVector p_data )
    {
        if ( p_version == HEADER_VERSION1 )
        {
            return createApplMessageV1( p_command,
                                        p_origin,
                                        p_dest,
                                        p_key,
                                        p_data );
        }
        
        assert( p_origin.length() >= ORIGIN_SIZE ) : "Illegal origin length";
        assert( p_dest.length()   >= DEST_SIZE )   : "Illegal dest length";
        assert( p_key.length()    >= KEY_SIZE )    : "Illegal key length";
        assert( p_appHeader.length() >  0 )        : "Illegal header length";
 
        int dataLength = ( p_data == null ) ? 0 : p_data.length();
        int appHeaderLength = ( p_appHeader == null ) ? 0 : p_appHeader.length();
        int totalLength = V2_HEADER_LENGTH + appHeaderLength + dataLength;

        ByteVector result = ByteVector.getInstance( totalLength );
        result.set( p_version, 0 );
        result.set( p_command, 1 );
        
        result.set( p_origin, 0, ORIGIN_OFFSET, ORIGIN_SIZE );
        result.set( p_dest,   0, DEST_OFFSET,   DEST_SIZE );
        result.set( p_key,    0, KEY_OFFSET,    KEY_SIZE );

        int dataStartOffset = V2_HEADER_LENGTH + appHeaderLength;
        result.set( (byte)dataStartOffset, V2_APP_HEADER_LENGTH_OFFSET );
            
        result.set( p_appHeader, 0, V2_APP_HEADER_OFFSET, p_appHeader.length() );
 
        if ( p_data != null )
        {
            result.set( p_data, 0, dataStartOffset, p_data.length() );
        }
        
        return result;
    }

    
    


    /**
     * This method constructs a reply for associated with this message.
     */
    public final ByteVector constructReply( byte       p_command,
                                            ByteVector p_data )
    {
        return createApplMessage( version,
                                  p_command,
                                  dest,  // reverse dest and origin, since it's a reply
                                  origin,
                                  key,
                                  header,
                                  p_data );
    }
    

    public static ApplMessage createFromBytes( ByteVector p_msg )
    {
        ApplMessage result = new ApplMessage( p_msg );
        
        return result;
    }

    
    public int getCommand()
    {
        return command;
    }


    public static String getCommandName( int command )
    {
        if( ( command >= 0 ) && ( command < commandStrings.length ) )
        {
            return commandStrings[ command ];
        }

        return "UNKNOWN_COMMAND";
    }


    public ByteVector getData()
    {
        return data;
    }

}
