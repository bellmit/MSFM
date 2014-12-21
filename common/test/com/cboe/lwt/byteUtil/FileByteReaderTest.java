/*
 * Created on Jan 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.byteUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FileByteReaderTest extends TestCase
{    
    public FileByteReaderTest( java.lang.String testName )
    {
        super( testName );
    }
    
    public static void main( String[] args )
    {
        junit.textui.TestRunner.run( FileByteReaderTest.class );
    }
    
    
    public static Test suite()
    {
        return new TestSuite( FileByteReaderTest.class );
    }
    
    
    public void testRead()
    {
        System.out.println( "FileByteReader : test read/write" );
        
        File file = null;
        
        try
        {
            file = File.createTempFile( "test", ".cfg" );
            StringBuffer chk = new StringBuffer();
            
            PrintStream printstream = new PrintStream( new FileOutputStream( file ) );
            for ( int i = 0; i < 10; ++i )
            {
                printstream.print( "Property" + i + "=value" + i + "\n" );
                chk.append( "Property" )
                   .append( i )
                   .append( "=value" )
                   .append( i )
                   .append( "\n" );
            }
            printstream.flush();
             
            File readFile = new File( file.getAbsolutePath() );
            FileByteReader fbr = new FileByteReader( readFile, 1000 );
            
            int length = (int)fbr.fileSize();
            byte[] buff = new byte[ length ];
            int readLength = fbr.read( buff, 0, length, length );
            
            assertEquals( length, readLength );
            
            String readStr = new String( buff );
            String chkString = chk.toString();
            
            int compareResult = readStr.compareTo( chkString );
            assertTrue( "\nread :\n[" + readStr + "]\nchk :\n[" + chkString + "]\n Diff value = " + compareResult, 
                        compareResult == 0 );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            fail();
        }
        finally
        {
            if ( file != null )
            {
                if ( file.exists() )
                {
                    file.delete();
                }
            }
        }
    }
    
}
