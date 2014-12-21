package com.cboe.client.util;

import java.io.StringWriter;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertTrue;
import org.junit.Test;      // annotation

public class ThreadHelperTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(ThreadHelperTest.class);
    }

    @Test public void testSleep()
    {
        long start = System.currentTimeMillis();
        ThreadHelper.sleep(5);
        long end = System.currentTimeMillis();
        assertTrue(end-start >= 5);
    }

    @Test public void testSleepSeconds()
    {
        long start = System.currentTimeMillis();
        ThreadHelper.sleepSeconds(2);
        long end = System.currentTimeMillis();
        assertTrue(end-start >= 2000);
    }

    @Test public void testDumpThreadGroup() throws Exception
    {
        StringWriter sw1 = new StringWriter();
        ThreadHelper.dumpThreadGroup(sw1,
                Thread.currentThread().getThreadGroup(), 42, true, false);
        String noxml = sw1.toString();
        assertTrue(noxml.startsWith("ThreadGroup(42)"));
        assertTrue(noxml.contains("Thread("));

        StringWriter sw2 = new StringWriter();
        ThreadHelper.dumpThreadGroup(sw2,
                Thread.currentThread().getThreadGroup(), 42, true, true);
        String xml = sw2.toString();
        assertTrue(xml.startsWith("<ThreadGroup name="));
        assertTrue(xml.contains("<Thread name="));
    }

    @Test public void testDumpThreadGruops() throws Exception
    {
        StringWriter sw = new StringWriter();
        ThreadHelper.dumpThreadGroups(sw, false);
        String threads = sw.toString();
        assertTrue(threads.startsWith("ThreadGroup(0)[system]"));
        assertTrue(threads.contains("Thread("));
    }
}
