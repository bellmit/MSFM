package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;  // annotation

public class CountingBitArrayTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(CountingBitArrayTest.class);
    }

    @Test public void testConstructorInt()
    {
        // CountingBitArray holds bits in an int[] array, one bit per int.
        CountingBitArray zero = new CountingBitArray(0);
        assertEquals(zero.bits.length, 0);
        assertEquals(zero.bitCount, 0);
        zero = new CountingBitArray();
        assertEquals(zero.bits.length, 0);
        assertEquals(zero.bitCount, 0);
        
        CountingBitArray one = new CountingBitArray(1);
        assertEquals(one.bits.length, 1);
        assertEquals(one.bitCount, 1);
        one = new CountingBitArray(Integer.SIZE-1);
        assertEquals(one.bits.length, Integer.SIZE-1);
        assertEquals(one.bitCount, Integer.SIZE-1);
        one = new CountingBitArray(Integer.SIZE);
        assertEquals(one.bits.length, Integer.SIZE);
        assertEquals(one.bitCount, Integer.SIZE);

        CountingBitArray two = new CountingBitArray(Integer.SIZE+1);
        assertEquals(two.bits.length, Integer.SIZE+1);
        two = new CountingBitArray(Integer.SIZE + Integer.SIZE/2);
        assertEquals(two.bits.length, Integer.SIZE + Integer.SIZE/2);
        two = new CountingBitArray(Integer.SIZE*2 - 1);
        assertEquals(two.bits.length, Integer.SIZE*2 - 1);
        two = new CountingBitArray(Integer.SIZE*2);
        assertEquals(two.bits.length, Integer.SIZE*2);

        CountingBitArray three = new CountingBitArray(Integer.SIZE*2+1);
        assertEquals(three.bits.length, Integer.SIZE*2+1);
        three = new CountingBitArray(Integer.SIZE*3);
        assertEquals(three.bits.length, Integer.SIZE*3);
    }

    @Test public void testConstructorCopy()
    {
        CountingBitArray ba = new CountingBitArray();
        for (int i = 10; i <= 150; i += 10)
        {
            ba.set(i);
        }

        CountingBitArray c = new CountingBitArray(ba);
        assertEquals(ba.bitCount, c.bitCount);
        assertArrayEquals(ba.bits, c.bits);

        for (int j = 10; j <= 150; j += 10)
        {
            assertTrue(c.isSet(j));
            assertFalse(c.isSet(j-1));
        }
    }

    @Test public void testCopyFrom()
    {
        CountingBitArray fifty = new CountingBitArray(50);
        CountingBitArray zero = new CountingBitArray();
        CountingBitArray k = new CountingBitArray(1024);
        CountingBitArray ba = new CountingBitArray(Integer.SIZE);

        fifty.set(14);
        fifty.set(37);
        fifty.set(49);

        k.set(42);
        k.set(312);
        k.set(666);
        k.set(1020);

        ba.copyFrom(fifty);
        assertEquals(ba.bitCount, 50);
        assertTrue(ba.isSet(14));
        assertTrue(ba.isSet(37));
        assertTrue(ba.isSet(49));
        assertFalse(ba.isSet(42));
        assertFalse(ba.isSet(0));

        ba.copyFrom(zero);
        assertEquals(ba.bitCount, 0);

        ba.copyFrom(k);
        assertEquals(ba.bitCount, 1024);
        assertTrue(ba.isSet(42));
        assertTrue(ba.isSet(312));
        assertTrue(ba.isSet(666));
        assertTrue(ba.isSet(1020));
        assertFalse(ba.isSet(14));

        BitArray fifty_b = new BitArray(50);
        BitArray zero_b = new BitArray();
        BitArray k_b = new BitArray(1024);

        fifty_b.set(14);
        fifty_b.set(37);
        fifty_b.set(49);

        k_b.set(42);
        k_b.set(312);
        k_b.set(666);
        k_b.set(1020);

        ba.copyFrom(fifty_b);
        assertEquals(ba.bitCount, 50);
        assertTrue(ba.isSet(14));
        assertTrue(ba.isSet(37));
        assertTrue(ba.isSet(49));
        assertFalse(ba.isSet(42));
        assertFalse(ba.isSet(0));

        ba.copyFrom(zero_b);
        assertEquals(ba.bitCount, 0);

        ba.copyFrom(k_b);
        assertEquals(ba.bitCount, 1024);
        assertTrue(ba.isSet(42));
        assertTrue(ba.isSet(312));
        assertTrue(ba.isSet(666));
        assertTrue(ba.isSet(1020));
        assertFalse(ba.isSet(14));

        BitArray ba_b = new BitArray(Integer.SIZE);

        ba_b.copyFrom(fifty);
        assertEquals(ba_b.bitCount, 50);
        assertTrue(ba_b.isSet(14));
        assertTrue(ba_b.isSet(37));
        assertTrue(ba_b.isSet(49));
        assertFalse(ba_b.isSet(42));
        assertFalse(ba_b.isSet(0));

        ba_b.copyFrom(zero);
        assertEquals(ba_b.bitCount, 0);

        ba_b.copyFrom(k);
        assertEquals(ba_b.bitCount, 1024);
        assertTrue(ba_b.isSet(42));
        assertTrue(ba_b.isSet(312));
        assertTrue(ba_b.isSet(666));
        assertTrue(ba_b.isSet(1020));
        assertFalse(ba_b.isSet(14));
    }


    @Test public void testSetAndClear()
    {
        // Set, clear and test bits within the limit we specified at creation.
        CountingBitArray two = new CountingBitArray(Integer.SIZE*2-10);
        two.set(0);
        assertEquals(0x1, two.bits[0]);
        for (int i = 1; i < Integer.SIZE*2-10; ++i)
        {
            assertEquals("i="+i, 0, two.bits[i]);
        }
        assertTrue(two.isSet(0));
        assertFalse(two.isSet(1));
        two.set(Integer.SIZE-1);
        assertEquals(1, two.bits[0]);
        assertEquals(1, two.bits[Integer.SIZE-1]);
        assertEquals(two.bits[1], 0);
        assertTrue(two.isSet(0));
        for (int i = 1; i < Integer.SIZE-2; ++i)
        {
            assertEquals("i="+i, 0, two.bits[i]);
        }
        assertTrue(two.isSet(Integer.SIZE-1));

        two.clear(Integer.SIZE-1);
        assertFalse(two.isSet(Integer.SIZE-1));
        assertFalse(two.isSet(1));
        assertTrue(two.isSet(0));

        // Test bits beyond the limit we specified at creation.
        two.set(Integer.SIZE*2);
        two.set(Integer.SIZE*2+5);
        two.set(Integer.SIZE*3-1);
        assertEquals(Integer.SIZE*3, two.bits.length);
        assertEquals(1, two.bits[Integer.SIZE*2]);
        assertEquals(1, two.bits[Integer.SIZE*2+5]);
        assertEquals(1, two.bits[Integer.SIZE*3-1]);
    }

    @Test public void testClear()
    {
        CountingBitArray five = new CountingBitArray(5);
        CountingBitArray word = new CountingBitArray(Integer.SIZE);
        CountingBitArray word10 = new CountingBitArray(Integer.SIZE*10);

        five.set(2);
        assertTrue(five.isSet(2));
        assertFalse(five.isSet(3));
        assertEquals(0, five.bits[0]);
        assertEquals(5, five.bitCount);

        word.set(0);
        word.set(10);
        word.set(20);
        word.set(26);
        word.set(27);
        word.set(Integer.SIZE-1);
        assertTrue(word.isSet(0));
        assertTrue(word.isSet(10));
        assertTrue(word.isSet(20));
        assertTrue(word.isSet(26));
        assertTrue(word.isSet(27));
        assertTrue(word.isSet(Integer.SIZE-1));
        assertFalse(word.isSet(1));
        assertFalse(word.isSet(Integer.SIZE-2));
        assertEquals(1, word.bits[0]);
        assertEquals(Integer.SIZE, word.bitCount);

        word10.set(Integer.SIZE-1);
        word10.set(5*Integer.SIZE-1);
        word10.set(10*Integer.SIZE-1);
        assertTrue(word10.isSet(Integer.SIZE-1));
        assertTrue(word10.isSet(5*Integer.SIZE-1));
        assertTrue(word10.isSet(10*Integer.SIZE-1));
        assertFalse(word10.isSet(8*Integer.SIZE-1));
        assertEquals(10*Integer.SIZE, word10.bits.length);
        assertEquals(10*Integer.SIZE, word10.bitCount);

        five.clear();
        assertEquals(0, five.bitCount);
        assertEquals(0, five.bits[0]);

        word.clear();
        assertEquals(word.bitCount, 0);
        assertEquals(word.bits[0], 0L);

        word10.clear();
        assertEquals(0, word10.bitCount);
        assertEquals(0, word10.bits[Integer.SIZE-1]);
        assertEquals(0, word10.bits[5*Integer.SIZE-1]);
        assertEquals(0, word10.bits[10*Integer.SIZE-1]);
    }

    @Test public void testTimesChanged()
    {
        CountingBitArray ba = new CountingBitArray();

        ba.set(3);
        ba.set(6);
        assertEquals(0, ba.timesChanged(0));
        assertEquals(1, ba.timesChanged(3));
        assertEquals(1, ba.timesChanged(6));
        assertEquals(0, ba.timesChanged(7));
        ba.set(3);
        assertEquals(2, ba.timesChanged(3));
        ba.set(3);
        assertEquals(3, ba.timesChanged(3));
        assertTrue(ba.isSet(3));
    }

    @Test public void testSizeAndCapacity()
    {
        CountingBitArray three = new CountingBitArray(3);
        assertEquals(3, three.size());
        assertEquals(3, three.capacity());

        CountingBitArray hundred = new CountingBitArray(100);
        assertEquals(100, hundred.size());
        assertEquals(100, hundred.capacity());

        three.set(100);
        assertEquals(101, three.size());
        assertEquals(101, three.capacity());
    }

    @Test public void testResize()
    {
        CountingBitArray ba = new CountingBitArray();
        ba.set(200);
        assertTrue(ba.isSet(200));

        ba.resize(300);
        assertTrue(ba.isSet(200));
        assertFalse(ba.isSet(201));
        assertEquals(301, ba.size());

        ba.resize(100);
        assertEquals(101, ba.size());
        assertFalse(ba.isSet(200));
    }

    @Test public void testToString()
    {
        String zero = "[00000000 00000000 00000000 00000000]";
        String one  = "[00000000 00000000 00000000 00000001]";
        CountingBitArray ba = new CountingBitArray();
        assertEquals(ba.toString(), "");

        ba.set(3);
        assertEquals(zero + " " + zero + " " + zero + " " + one,
                ba.toString());
    }

    @Test public void testClone()
    {
        CountingBitArray ba = new CountingBitArray();
        ba.set(12);
        ba.set(40);
        ba.set(99);

        CountingBitArray c = (CountingBitArray) ba.clone();
        assertEquals(c.size(), ba.size());
        assertEquals(ba.bits.length, c.bits.length);
        for (int i = 0; i < ba.bits.length; i++)
        {
            assertEquals(ba.bits[i], c.bits[i]);
        }
        assertNotSame(ba, c);
    }
}
