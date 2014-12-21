/*
 * TcpIpcTest.java
 * JUnit based test
 *
 * Created on May 17, 2002, 10:03 AM
 */

package com.cboe.lwt.interProcess;

import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.lwt.interProcess.ipc.MeteredReaderThread;
import com.cboe.lwt.interProcess.ipc.MeteredWriterThread;
import com.cboe.lwt.interProcess.ipc.ReaderThread;
import com.cboe.lwt.interProcess.ipc.WriterThread;
import com.cboe.lwt.eventLog.ConsoleLogger;


/**
 *
 * @author dotyl
 */
public class TcpIpcTest extends TestCase
{
    public TcpIpcTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static void main(java.lang.String[] args)
    {
        new ConsoleLogger().setGlobal(); 

        System.out.println( "---- Unit Tests ----" );
        junit.textui.TestRunner.run(suite());
        
        System.out.println( "---- Performance Tests ----" );
        junit.textui.TestRunner.run(perfSuite());
    }
    
    public void testGetInputStream()
    {
        // trivial
    }
    
    public void testGetOutputStream()
    {
        // tested in testConnect
    }
    
    public void testConnect()
    {
        System.out.println("TCP IPC: testConnect");
        try
        {
            final URL[] urls = new URL[1];
            urls[0] = new URL( "http://localhost:47970" );

            ReaderThread reader = new ReaderThread( urls[0].getPort(), 4096, 100000, 2 );
   
            InterProcessConnection ipc = TcpIpc.getInstance( urls, 4096, 4096 );
            
            WriterThread writer = new WriterThread( ipc, 100000 );

            writer.start();
            reader.start();

            writer.join();

            // tests reconnection
            writer.runInThisThread();

            reader.join();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            fail();
        }
    }
    
    public void perfWrite4k()
    {
        System.out.println("perf: TCP IPC: write");
        try
        {
            final URL[] urls = new URL[1];
            urls[0] = new URL( "http://localhost:47960" );

            MeteredReaderThread reader = new MeteredReaderThread( urls[0].getPort(), 4096, 200000, 1000, 1 );
            
            InterProcessConnection writeCon = TcpIpc.getInstance( urls, 4096, 4096 );

            MeteredWriterThread writer = new MeteredWriterThread( writeCon, 200000, 1000 );

            reader.start();
            writer.start();

            writer.join();
            reader.join();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            fail();
        }
    }
    
    
    public void perfWrite8k()
    {
        System.out.println("perf: TCP IPC: write");
        try
        {
            final URL[] urls = new URL[1];
            urls[0] = new URL( "http://localhost:47960" );

            MeteredReaderThread reader = new MeteredReaderThread( urls[0].getPort(), 8192, 200000, 1000, 1 );
            
            InterProcessConnection writeCon = TcpIpc.getInstance( urls, 8192, 8192 );

            MeteredWriterThread writer = new MeteredWriterThread( writeCon, 200000, 1000 );

            reader.start();
            writer.start();

            writer.join();
            reader.join();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
            fail();
        }
    }
    
    public void testDisconnect()
    {
        // tested in testConnect
    }
    
    public static Test perfSuite()
    {
        TestSuite suite = new TestSuite();
        
        suite.addTest( new TcpIpcTest( "perfWrite4k" ) );
        suite.addTest( new TcpIpcTest( "perfWrite8k" ) );
         
        return suite;
    }

    public void testConnectPrimary()
    {
        // untested for now
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(TcpIpcTest.class);
        
        return suite;
    }
    
    public void testWrite()
    {
        // trivial
    }
    
    public void testFlush()
    {
        // trivial
    }
    
    public void testRead()
    {
        // trivial
    }
    
}
