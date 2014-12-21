package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;  // annotation

public class BitArrayTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(BitArrayTest.class);
    }

    @Test public void testConstructorInt()
    {
        // BitArray holds bits in a long[] array. Each long has Long.SIZE bits.
        BitArray zero = new BitArray(0);
        assertEquals(zero.bits.length, 0);
        assertEquals(zero.bitCount, 0);
        zero = new BitArray();
        assertEquals(zero.bits.length, 0);
        assertEquals(zero.bitCount, 0);
        
        BitArray one = new BitArray(1);
        assertEquals(one.bits.length, 1);
        assertEquals(one.bitCount, 1);
        one = new BitArray(Long.SIZE-1);
        assertEquals(one.bits.length, 1);
        assertEquals(one.bitCount, Long.SIZE-1);
        one = new BitArray(Long.SIZE);
        assertEquals(one.bits.length, 1);
        assertEquals(one.bitCount, Long.SIZE);

        BitArray two = new BitArray(Long.SIZE+1);
        assertEquals(two.bits.length, 2);
        two = new BitArray(Long.SIZE + Long.SIZE/2);
        assertEquals(two.bits.length, 2);
        two = new BitArray(Long.SIZE*2 - 1);
        assertEquals(two.bits.length, 2);
        two = new BitArray(Long.SIZE*2);
        assertEquals(two.bits.length, 2);

        BitArray three = new BitArray(Long.SIZE*2+1);
        assertEquals(three.bits.length, 3);
        three = new BitArray(Long.SIZE*3);
        assertEquals(three.bits.length, 3);
    }

    @Test public void testConstructorCopy()
    {
        BitArray ba = new BitArray();
        for (int i = 10; i <= 150; i += 10)
        {
            ba.set(i);
        }

        BitArray c = new BitArray(ba);
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
        BitArray fifty = new BitArray(50);
        BitArray zero = new BitArray();
        BitArray k = new BitArray(1024);
        BitArray ba = new BitArray(Long.SIZE);

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
    }

    @Test public void testSetAndClear()
    {
        // Set, clear and test bits within the limit we specified at creation.
        BitArray two = new BitArray(Long.SIZE*2-10);
        two.set(0);
        assertEquals(two.bits[0], 0x1L);
        assertEquals(two.bits[1], 0);
        assertTrue(two.isSet(0));
        assertFalse(two.isSet(1));
        two.set(Long.SIZE-1);
        assertEquals(two.bits[0], 0x8000000000000001L);
        assertEquals(two.bits[1], 0);
        assertTrue(two.isSet(0));
        assertTrue(two.isSet(Long.SIZE-1));
        two.set(Long.SIZE);
        assertEquals(two.bits[0], 0x8000000000000001L);
        assertEquals(two.bits[1], 0x1L);
        assertTrue(two.isSet(0));
        assertTrue(two.isSet(Long.SIZE));
        assertFalse(two.isSet(Long.SIZE+1));
        two.clear(Long.SIZE-1);
        assertEquals(two.bits[0], 0x1L);
        assertEquals(two.bits[1], 0x1L);
        assertTrue(two.isSet(0));
        assertTrue(two.isSet(Long.SIZE));
        assertFalse(two.isSet(Long.SIZE-1));
        assertFalse(two.isSet(10));

        // Test bits beyond the limit we specified at creation.
        two.set(Long.SIZE*2);
        two.set(Long.SIZE*2+5);
        two.set(Long.SIZE*3-1);
        assertEquals(two.bits.length, 3);
        assertEquals(two.bits[2], 0x8000000000000021L);
        assertTrue(two.isSet(Long.SIZE*2));
        assertTrue(two.isSet(Long.SIZE*2+5));
        assertTrue(two.isSet(Long.SIZE*3-1));
        assertFalse(two.isSet(Long.SIZE*2+1));
        assertFalse(two.isSet(Long.SIZE*2+15));
    }

    @Test public void testClear()
    {
        BitArray five = new BitArray(5);
        BitArray word = new BitArray(Long.SIZE);
        BitArray word10 = new BitArray(Long.SIZE*10);

        five.set(2);
        assertTrue(five.isSet(2));
        assertFalse(five.isSet(3));
        assertEquals(five.bits[0], 0x4L);
        assertEquals(five.bitCount, 5);

        word.set(0);
        word.set(20);
        word.set(30);
        word.set(31);
        word.set(32);
        word.set(Long.SIZE-1);
        assertTrue(word.isSet(0));
        assertTrue(word.isSet(20));
        assertTrue(word.isSet(30));
        assertTrue(word.isSet(31));
        assertTrue(word.isSet(32));
        assertTrue(word.isSet(Long.SIZE-1));
        assertFalse(word.isSet(1));
        assertFalse(word.isSet(Long.SIZE-2));
        assertEquals(word.bits[0], 0x80000001C0100001L);
        assertEquals(word.bitCount, Long.SIZE);

        word10.set(Long.SIZE-1);
        word10.set(5*Long.SIZE-1);
        word10.set(10*Long.SIZE-1);
        assertTrue(word10.isSet(Long.SIZE-1));
        assertTrue(word10.isSet(5*Long.SIZE-1));
        assertTrue(word10.isSet(10*Long.SIZE-1));
        assertFalse(word10.isSet(8*Long.SIZE-1));
        assertEquals(word10.bits.length, 10);
        assertEquals(word10.bitCount, Long.SIZE*10);

        five.clear();
        assertEquals(five.bitCount, 0);
        assertEquals(five.bits[0], 0L);

        word.clear();
        assertEquals(word.bitCount, 0);
        assertEquals(word.bits[0], 0L);

        word10.clear();
        assertEquals(word10.bitCount, 0);
        assertEquals(word10.bits[0], 0L);
        assertEquals(word10.bits[6], 0L);
        assertEquals(word10.bits[9], 0L);
    }

    @Test public void testTimesChanged()
    {
        BitArray ba = new BitArray();

        ba.set(3);
        ba.set(6);
        assertEquals(ba.timesChanged(0), 0);
        assertEquals(ba.timesChanged(3), 1);
        assertEquals(ba.timesChanged(6), 1);
        assertEquals(ba.timesChanged(7), 0);
    }

    @Test public void testSizeAndCapacity()
    {
        BitArray three = new BitArray(3);
        assertEquals(three.size(), 3);
        assertEquals(three.capacity(), Long.SIZE);

        BitArray hundred = new BitArray(100);
        assertEquals(hundred.size(), 100);
        assertEquals(hundred.capacity(), 2*Long.SIZE);

        three.set(100);
        assertEquals(three.size(), 101);
        assertEquals(three.capacity(), 2*Long.SIZE);
    }

    @Test public void testToArray()
    {
        BitArray ba = new BitArray();
        for (int i = 1; i < 3000; i++)
        {
            ba.set(i);
        }
        long bits[] = ba.toArray();
        assertArrayEquals(ba.bits, bits);
    }

    @Test public void testResize()
    {
        BitArray ba = new BitArray();
        ba.set(200);
        assertTrue(ba.isSet(200));

        ba.resize(300);
        assertTrue(ba.isSet(200));
        assertFalse(ba.isSet(201));
        assertEquals(ba.size(), 301);

        ba.resize(100);
        assertEquals(ba.size(), 101);
        assertFalse(ba.isSet(200));
    }

    @Test public void testToString()
    {
        String bit0 = "[00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000001]";
        String bit3 = "[00000000 00000000 00000000 00000000 00000000 00000000 00000000 00001000]";
        BitArray ba = new BitArray();
        assertEquals(ba.toString(), "");

        ba.set(3);
        assertEquals(ba.toString(), bit3);
        ba.set(64);
        assertEquals(ba.toString(), bit3 + " " + bit0);
    }

    @Test public void testClone()
    {
        BitArray ba = new BitArray();
        ba.set(12);
        ba.set(40);
        ba.set(99);

        BitArray c = (BitArray) ba.clone();
        assertEquals(c.size(), ba.size());
        assertEquals(ba.bits.length, c.bits.length);
        for (int i = 0; i < ba.bits.length; i++)
        {
            assertEquals(ba.bits[i], c.bits[i]);
        }
        assertFalse(ba == c);
    }
}
