package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;      // annotation

public class PairTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(PairTest.class);
    }

    @Test public void testConstructor()
    {
        Pair empty = new Pair();
        assertNull(empty.first);
        assertNull(empty.second);

        String s = "hi";
        Integer i = 42;

        Pair p = new Pair(s, i);
        assertSame(s, p.first);
        assertSame(i, p.second);
    }

    @Test public void testSetGet()
    {
        Character c = 'x';
        Byte b = 17;

        Pair p = new Pair();
        assertNull(p.getFirst());
        assertNull(p.getSecond());
        p.setFirst(c);
        p.setSecond(b);
        assertSame(c, p.getFirst());
        assertNotSame(c, p.getSecond());
        assertSame(b, p.getSecond());

        Long l = 12345678910L;
        String s = "[%%]";
        p.reset(s, l);
        assertSame(s, p.getFirst());
        assertSame(l, p.getSecond());
    }

    @Test public void testToString()
    {
        Short sh = 4321;
        String st = "tea";
        Pair p = new Pair(sh, st);
        assertEquals("Pair([4321],[tea])", p.toString());
    }
}
