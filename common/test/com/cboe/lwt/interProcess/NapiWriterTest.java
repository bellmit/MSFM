/*
 * NapiWriterTest.java
 * JUnit based test
 *
 * Created on September 27, 2002, 3:06 PM
 */

package com.cboe.lwt.interProcess;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.lwt.byteUtil.ByteArrayUtils;
import com.cboe.lwt.byteUtil.ByteIterator;
import com.cboe.lwt.testUtils.DummyByteWriter;


/**
 *
 * @author dotyl
 */
public class NapiWriterTest extends TestCase
{
    
    public NapiWriterTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static void main(java.lang.String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
    
    public void testWrite()
    {
        System.out.println("NapiWriter.write");
        byte[] dummyOutBuff = "12345678901234567801234567890".getBytes();

        try
        {
            DummyByteWriter out = new DummyByteWriter();

            NapiWriter outStream = new NapiWriter( out );

            ByteIterator iter = ByteIterator.getInstance( dummyOutBuff );

            outStream.write( iter, iter.remaining() );           
            byte[] result = out.getWrites();
            
            assertEquals( 31, result.length );
            
            ByteArrayUtils.assertEqual( dummyOutBuff, 0, result, NapiUtils.NUM_LENGTH_BYTES, 29 );
        } 
        catch( Exception ex )
        {
            ex.printStackTrace();
            fail();
        }
        
        try
        {
            DummyByteWriter out = new DummyByteWriter();

            NapiWriter outStream = new NapiWriter( out );

            ByteIterator iter = ByteIterator.getInstance( dummyOutBuff );

            outStream.write( iter, iter.remaining() );           
            byte[] result = out.getWrites();
            
            assertEquals( 31, result.length );
            
            ByteArrayUtils.assertEqual( dummyOutBuff, 0, result, NapiUtils.NUM_LENGTH_BYTES, 29 );
        } 
        catch( Exception ex )
        {
            ex.printStackTrace();
            fail();
        }
        
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(NapiWriterTest.class);
        
        return suite;
    }
    
}
