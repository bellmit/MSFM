/*
 * NapiByteReaderTest.java
 * JUnit based test
 *
 * Created on September 26, 2002, 4:36 PM
 */

package com.cboe.lwt.interProcess;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.lwt.byteUtil.ByteArrayUtils;
import com.cboe.lwt.testUtils.DummyByteReader;
import com.cboe.lwt.eventLog.ConsoleLogger;

/**
 *
 * @author dotyl
 */
public class NapiReaderTest extends TestCase
{
    
    public NapiReaderTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static void main(java.lang.String[] args)
    {
        new ConsoleLogger().setGlobal();
        
        junit.textui.TestRunner.run(suite());
    }
    
    public void testGetBlock()
    {
        System.out.println("NapiByteReader.getBlock");
        byte[] buff = new byte[100];
        byte[] dummyInBuff = "--12345678901234567801234567890".getBytes();
                
        NapiUtils.setBlockSize( dummyInBuff, 0, dummyInBuff.length - NapiUtils.NUM_LENGTH_BYTES );

        try
        {
            DummyByteReader in = new DummyByteReader();

            in.addSimulatedRead( dummyInBuff );

            NapiReader inStream = new NapiReader( in );

            int bytesRead = inStream.getBlock( buff );
            
            ByteArrayUtils.assertEqual( buff, 0, dummyInBuff, NapiUtils.NUM_LENGTH_BYTES, bytesRead );
        } 
        catch( Exception ex )
        {
            ex.printStackTrace();
            fail();
        }
        
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(NapiReaderTest.class);
        
        return suite;
    }
    
}
