/*
 * Created on May 5, 2005
 */
package com.cboe.lwt.byteUtil;

import java.io.IOException;
import java.io.OutputStream;


public class WriterByteStream
    extends OutputStream
{
    ByteWriter out;
    
    
    public WriterByteStream( ByteWriter p_out )
    {
        out = p_out;
    }


    public void close()
        throws IOException
    {
        out.flush();
    }


    public void flush()
        throws IOException
    {
        out.flush();
    }


    public void write( byte[] p_toWrite,
                       int    p_off,
                       int    p_len )
        throws IOException
    {
        out.write( p_toWrite,
                   p_off,
                   p_len );
    }


    public void write( byte[] p_toWrite )
        throws IOException
    {
        out.write( p_toWrite,
                   0,
                   p_toWrite.length );
    }


    public void write( int p_toWrite )
        throws IOException
    {
        out.write( (byte)p_toWrite );        
    }

}
