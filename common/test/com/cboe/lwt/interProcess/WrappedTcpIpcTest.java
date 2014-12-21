/*
 * WrappedTcpIpcTest.java
 * JUnit based test
 *
 * Created on October 11, 2002, 1:12 PM
 */

package com.cboe.lwt.interProcess;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author dotyl
 */
public class WrappedTcpIpcTest extends TestCase
{
    
    public WrappedTcpIpcTest(java.lang.String testName)
    {
        super(testName);
    }
    
    public static void main(java.lang.String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(WrappedTcpIpcTest.class);
        
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
    
    public void testConnectPrimary()
    {
        // trivial
    }
    
    public void testConnect()
    {
        // trivial
    }
    
    public void testDisconnect()
    {
        // trivial
    }
    
    // Add test methods here, they have to start with 'test' name.
    // for example:
    // public void testHello() {}
    
    
}
