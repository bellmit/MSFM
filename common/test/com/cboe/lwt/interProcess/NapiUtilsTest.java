/*
 * NapiUtilsTest.java
 * JUnit based test
 *
 * Created on August 20, 2002, 11:10 AM
 */

package com.cboe.lwt.interProcess;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 *
 * @author dotyl
 */
public class NapiUtilsTest extends TestCase
{
    ////////////////////////////////////////////////////////////////////////////

    class ToCoppFromOpraInputStream extends InputStream
    {
        private byte[] buffer;
        private int    readPos;
        private int    end;

        public synchronized void setInputBuffer( byte[] p_buffer, int p_start, int p_end )
        {
            buffer  = p_buffer;
            readPos = p_start;
            end     = p_end;
            
            notify();
        }

        public synchronized int read()
            throws IOException
        {
            try
            {
                while( end == readPos )
                {
                    wait();
                }
            }
            catch( InterruptedException ex )
            {
                throw new InterruptedIOException( ex.getMessage() );
            }
            
            return buffer[readPos++];
        }

        public synchronized int read( byte[] p_dest )
            throws IOException
        {
            try
            {
                while( end == readPos )
                {
                    wait();
                }
            }
            catch( InterruptedException ex )
            {
                throw new InterruptedIOException( ex.getMessage() );
            }

            int copyLength = end - readPos;

            junit.framework.Assert.assertTrue( "unset read buffer for virtual InputStream", copyLength > 0 );

            System.arraycopy( buffer, readPos, p_dest, 0, copyLength );
            readPos = end;

            return copyLength;
        }

        public synchronized int read( byte[] p_dest, int p_start, int p_length )
            throws IOException
        {
            try
            {
                while( end == readPos )
                {
                    wait();
                }
            }
            catch( InterruptedException ex )
            {
                throw new InterruptedIOException( ex.getMessage() );
            }

            int copyLength = ( p_length < ( end - readPos ) )
                           ? p_length
                           : end - readPos;

            System.arraycopy( buffer, readPos, p_dest, 0, copyLength );
            readPos = end;

            return copyLength;
        }
    };    
   
    
    ////////////////////////////////////////////////////////////////////////////
    
    
    public NapiUtilsTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static void main(java.lang.String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
    
    public void testParseBlockSize()
    {
        // tested in testSetBlockSize()
    }
    
    
    public void testSetBlockSize()
    {
        System.out.println("testSetBlockSize");
        
        byte [] buff = new byte[20];
        NapiUtils.setBlockSize( buff, 0, 1234 );
        assertEquals( 1234, NapiUtils.parseBlockSize( buff[0], buff[1] ) );
        
        NapiUtils.setBlockSize( buff, 0, 0 );
        assertEquals( 0, NapiUtils.parseBlockSize( buff[0], buff[1] ) );
        
        NapiUtils.setBlockSize( buff, 0, 128 );
        assertEquals( 128, NapiUtils.parseBlockSize( buff[0], buff[1] ) );
        
        NapiUtils.setBlockSize( buff, 0, 65000 );
        assertEquals( 65000, NapiUtils.parseBlockSize( buff[0], buff[1] ) );
        
        NapiUtils.setBlockSize( buff, 0, 12 );
        assertEquals( 12, NapiUtils.parseBlockSize( buff[0], buff[1] ) );
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(NapiUtilsTest.class);
        
        return suite;
    }
    
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
    
}
