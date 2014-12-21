/*
 * Created on Mar 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.string;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.cboe.lwt.eventLog.Logger;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BufferFile
{
    public static StringBuffer loadFromFile( File p_file ) 
        throws IOException
    {
        if ( ! p_file.exists() )
        {
            throw new IOException( "Specified Configuration file (" 
                                   + p_file.getAbsolutePath()
                                   + "), does not exist" );
        }

        Logger.info( "Using Configuration file (" + p_file.getAbsolutePath() + ")" );
        
        FileInputStream fis = new FileInputStream(p_file);
        FileChannel fc = fis.getChannel();

        // Create a read-only CharBuffer on the file
        ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int)fc.size());
        return new StringBuffer( Charset.forName("8859_1").newDecoder().decode(bbuf).toString() );
    }


    public static void writeToFile( File p_file, StringBuffer p_contents ) 
        throws IOException
    {
        if ( p_file.exists() )
        {
            Logger.info( "Overwriting Configuration file (" + p_file
                       + ")" );
        }
        else
        {
            Logger.info( "Creating Configuration file (" + p_file.getAbsolutePath()
                         + ")" );
        }
        
        PrintWriter out = new PrintWriter( new BufferedOutputStream( new FileOutputStream( p_file ) ) );
        
        out.write( p_contents.toString() );
        out.flush();
        out.close();
    }



}
