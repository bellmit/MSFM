package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertTrue;
import org.junit.Test;      // annotation

public class ExceptionHelperTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(ExceptionHelperTest.class);
    }

    @Test public void testNoArgs()
    {
        String st[] = ExceptionHelper.getStackTrace().split("\n");
        assertTrue(st.length > 2);
        assertTrue(st[1].contains(
                "com.cboe.client.util.ExceptionHelper.getStackTrace("));
        assertTrue(st[2].contains(
                "com.cboe.client.util.ExceptionHelperTest.testNoArgs("));
    }

    @Test public void testDelimiter()
    {
        String trace = ExceptionHelper.getStackTrace("~");
        assertTrue(trace.contains(")~"));
        
        String st[] = trace.split("~");
        assertTrue(st.length > 2);
        assertTrue(st[1].contains(
                "com.cboe.client.util.ExceptionHelper.getStackTrace("));
        assertTrue(st[2].contains(
                "com.cboe.client.util.ExceptionHelperTest.testDelimiter("));
    }

    @Test public void testThrowable()
    {
        String message = "created for testing";
        Exception e = new Exception(message);
        String st[] = ExceptionHelper.getStackTrace(e).split("\n");
        assertTrue(st.length > 1);
        assertTrue(st[0].contains(message));
        assertTrue(st[1].contains(
                "com.cboe.client.util.ExceptionHelperTest.testThrowable("));
    }

    @Test public void testThrowableCharDelimiter()
    {
        String message = "What I tell you three times is True.";
        Exception e = new NoSuchMethodException(message);
        String trace = ExceptionHelper.getStackTrace(e, '#');
        assertTrue(trace.contains(")#"));

        String st[] = trace.split("#");
        assertTrue(st.length > 1);
        assertTrue(st[0].contains(message));
        assertTrue(st[1].contains(
          "com.cboe.client.util.ExceptionHelperTest.testThrowableCharDelimiter("
        ));
    }

    @Test public void testThrowableStrDelimiter()
    {
        String message = "Never try to guess";
        Exception e = new IllegalThreadStateException(message);
        String trace = ExceptionHelper.getStackTrace(e, "%");
        assertTrue(trace.contains(")%"));

        String st[] = trace.split("%");
        assertTrue(st.length > 1);
        assertTrue(st[0].contains(message));
        assertTrue(st[1].contains(
          "com.cboe.client.util.ExceptionHelperTest.testThrowableStrDelimiter("
        ));
    }
}
