package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import org.junit.Test;      // annotation

public class MutableIntegerTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(MutableIntegerTest.class);
    }

    @Test public void testConstructor()
    {
        MutableInteger mi = new MutableInteger();
        assertEquals(0, mi.integer);

        mi = new MutableInteger(-17);
        assertEquals(-17, mi.integer);

        mi = new MutableInteger(Integer.valueOf(76));
        assertEquals(76, mi.integer);
    }

    @Test public void testSetValue()
    {
        MutableInteger mi = new MutableInteger();
        assertEquals(0, mi.intValue());

        mi.set(600);
        assertEquals(600, mi.intValue());

        mi.reset();
        assertEquals(0, mi.intValue());
    }

    @Test public void testIncDec()
    {
        MutableInteger mi = new MutableInteger(45);
        assertEquals(46, mi.inc());
        assertEquals(46, mi.integer);
        
        assertEquals(50, mi.inc(4));
        assertEquals(50, mi.intValue());

        assertEquals(49, mi.dec());
        assertEquals(49, mi.intValue());

        assertEquals(42, mi.dec(7));
        assertEquals(42, mi.intValue());

        assertEquals(41, mi.decZero());
        assertEquals(37, mi.decZero(4));
        mi.reset();
        assertEquals(-1, mi.dec());
        assertEquals(0, mi.decZero());
        mi.set(2);
        assertEquals(1, mi.decZero(1));
        assertEquals(0, mi.decZero(3));
    }

    @Test public void testHashString()
    {
        MutableInteger mi = new MutableInteger(54321);
        assertEquals(54321, mi.hashCode());
        assertEquals("54321", mi.toString());
    }

    class MThread extends Thread
    {
        public void run()
        {
            MutableInteger mi = MutableInteger.threadLocalMutableInteger
                    .getMutableInteger();
            assertEquals(0, mi.intValue());
        }
    }

    @Test public void testThreadLocal()
    {
        MutableInteger mi = MutableInteger.threadLocalMutableInteger
                .getMutableInteger();
        mi.set(17);
        assertEquals(17, mi.intValue());

        // Get thread-local variable on another thread, demonstrate that
        // it's a different variable than the one just above.
        new MThread().start();
    }
}
