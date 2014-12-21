package com.cboe.client.util;

import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;  // annotation

public class BitHelperTest
{
    private final static int INT_HIGH = Integer.MIN_VALUE;
    private final static long LONG_HIGH = Long.MIN_VALUE;

    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(BitHelperTest.class);
    }

    @Test public void testMakeBitMask1()
    {
        assertEquals(1, BitHelper.makeBitMask(0));
        assertEquals(16777216, BitHelper.makeBitMask(24));
        assertEquals(-2147483648, BitHelper.makeBitMask(31));
    }

    @Test public void testMakeBitMask2()
    {
        assertEquals(3, BitHelper.makeBitMask(0,1));
        assertEquals(3, BitHelper.makeBitMask(1,0));
        assertEquals(0x80008000, BitHelper.makeBitMask(31, 15));
    }

    @Test public void testMakeBitMask3()
    {
        assertEquals(0x80008001, BitHelper.makeBitMask(15, 0, 31));
        assertEquals(0x7, BitHelper.makeBitMask(0,1,2));
        assertEquals(0x1, BitHelper.makeBitMask(0,0,0));
    }

    @Test public void testMakeBitMask4()
    {
        assertEquals(0xF, BitHelper.makeBitMask(0, 1, 2, 3));
        assertEquals(0xF0, BitHelper.makeBitMask(7, 4, 6, 5));
        assertEquals(0x10, BitHelper.makeBitMask(4, 4, 4, 4));
    }

    @Test public void testMakeBitMask5()
    {
        assertEquals(0x11111, BitHelper.makeBitMask(0, 4, 8, 12, 16));
        assertEquals(0x11111, BitHelper.makeBitMask(12, 0, 16, 8, 4));
        assertEquals(0x2, BitHelper.makeBitMask(1, 1, 1, 1, 1));
    }

    @Test public void testMakeBitMask6()
    {
        assertEquals(0x88888800, BitHelper.makeBitMask(31, 27, 23, 19, 15, 11));
        assertEquals(0x5, BitHelper.makeBitMask(0, 2, 0, 0, 2, 2));
    }

    @Test public void testMakeBitMask7()
    {
        assertEquals(0x1248124, BitHelper.makeBitMask(2, 5, 8, 15, 18, 21, 24));
        assertEquals(0xFC000001, BitHelper.makeBitMask(0,26,27,28,29,30,31));
    }

    @Test public void testMakeBitMask8()
    {
        assertEquals(0xF000000F, BitHelper.makeBitMask(31,30,29,28,3,2,1,0));
        assertEquals(0xFF000, BitHelper.makeBitMask(12,13,14,15,16,17,18,19));
    }

    @Test public void testMakeBitMask9()
    {
        assertEquals(0xA2222222, BitHelper.makeBitMask(1, 5, 9, 13,
                17, 21, 25, 29, 31));
    }

    @Test public void testOrBits1()
    {
        for (int i = 4; i < 10000; i += 403)
        {
            assertEquals(i, BitHelper.orBits(i));
        }
    }

    @Test public void testOrBits2()
    {
        for (int i = 50; i < 500; i += 42)
        {
            assertEquals(i+1, BitHelper.orBits(i, 1));
        }
        assertEquals(0x80000001, BitHelper.orBits(INT_HIGH, 1));
        assertEquals(1, BitHelper.orBits(1,1));
    }

    @Test public void testOrBits3()
    {
        assertEquals(7, BitHelper.orBits(1, 2, 4));
        assertEquals(0x80000003, BitHelper.orBits(INT_HIGH, 1, 2));
        assertEquals(15, BitHelper.orBits(8, 5, 3));
    }

    @Test public void testOrBits4()
    {
        assertEquals(0, BitHelper.orBits(0, 0, 0, 0));
        assertEquals(17, BitHelper.orBits(16, 1, 17, 0));
        assertEquals(31, BitHelper.orBits(15, 1, 17, 0));
    }

    @Test public void testOrBits5()
    {
        assertEquals(7, BitHelper.orBits(5, 2, 5, 2, 5));
        assertEquals(0xC000000C, BitHelper.orBits(INT_HIGH, 8 , 4,
                0x40000000, 8));
    }

    @Test public void testOrBits6()
    {
        assertEquals(0x1234, BitHelper.orBits(0x1200, 0x230, 0x1004,
                0x10, 0x1030, 0x24));
        assertEquals(0x8000000F, BitHelper.orBits(INT_HIGH,INT_HIGH,1,9,5,6));
    }

    @Test public void testOrBits7()
    {
        assertEquals(0x5010B, BitHelper.orBits(0x40000, 0x10100, 8, 3, 0x40001,
                10, 256));
        assertEquals(127, BitHelper.orBits(5, 1, 100, 12, 64, 8, 50));
    }

    @Test public void testOrBits8()
    {
        assertEquals(105, BitHelper.orBits(65, 32, 96, 8, 72, 33, 64, 41));
        assertEquals(1, BitHelper.orBits(1, 1, 1, 1, 1, 1, 1, 1));
    }

    @Test public void testOrBits9()
    {
        assertEquals(15, BitHelper.orBits(1, 2, 3, 4, 5, 6, 7, 8, 9));
        assertEquals(-1, BitHelper.orBits(-10, -9, -8, -7, -6, -5, -4, -3, -2));
    }

    @Test public void testAreAllBitsClear()
    {
        assertTrue(BitHelper.areAllBitsClear(0));
        assertFalse(BitHelper.areAllBitsClear(INT_HIGH));
        assertFalse(BitHelper.areAllBitsClear(0x80000001));
        assertTrue(BitHelper.areAllBitsClear(0L));
        assertFalse(BitHelper.areAllBitsClear(LONG_HIGH));
        assertFalse(BitHelper.areAllBitsClear(0x8000000080000000L));
        assertFalse(BitHelper.areAllBitsClear(1L));
    }

    @Test public void testAreAllBitsFromBitMaskClear()
    {
        int zeroMask = 0;
        int oddBitsMask = 0xAAAAAAAA;   // bits 1(=2), 3(=8), 5(=32) ...
        int evenBitsMask = 0x55555555;  // bits 0 (=1), 2(=4), 4 (=16) ...
        int allMask = -1;
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(42, zeroMask));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(0x55555555,
                oddBitsMask));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(0xAAAAAAAA,
                evenBitsMask));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(1, oddBitsMask));
        assertFalse(BitHelper.areAllBitsFromBitMaskClear(INT_HIGH,
                oddBitsMask));
        assertFalse(BitHelper.areAllBitsFromBitMaskClear(1, evenBitsMask));
        assertFalse(BitHelper.areAllBitsFromBitMaskClear(75, allMask));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(0, allMask));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(0, zeroMask));
        assertFalse(BitHelper.areAllBitsFromBitMaskClear(-1, allMask));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(-1, zeroMask));

        long maskZero = 0L;
        long maskAll = -1L;
        long maskOddBits = 0xAAAAAAAAAAAAAAAAL;
        long maskEvenBits = 0x5555555555555555L;
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(42L, maskZero));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(0x5555555555555555L,
                maskOddBits));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(0xAAAAAAAAAAAAAAAAL,
                maskEvenBits));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(1L, maskOddBits));
        assertFalse(BitHelper.areAllBitsFromBitMaskClear(LONG_HIGH,
                maskOddBits));
        assertFalse(BitHelper.areAllBitsFromBitMaskClear(1L, maskEvenBits));
        assertFalse(BitHelper.areAllBitsFromBitMaskClear(75L, maskAll));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(0L, maskAll));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(0L, maskZero));
        assertFalse(BitHelper.areAllBitsFromBitMaskClear(-1L, maskAll));
        assertTrue(BitHelper.areAllBitsFromBitMaskClear(-1L, maskZero));
    }

    @Test public void testIsBitMaskSet()
    {
        int zeroMask = 0;
        int allMask = -1;
        int oddBitsMask = 0xAAAAAAAA;   // bits 1(=2), 3(=8), 5(=32) ...
        int evenBitsMask = 0x55555555;  // bits 0 (=1), 2(=4), 4 (=16) ...
        assertTrue(BitHelper.isBitMaskSet(1, zeroMask));
        assertTrue(BitHelper.isBitMaskSet(0, zeroMask));
        assertTrue(BitHelper.isBitMaskSet(-1, allMask));
        assertFalse(BitHelper.isBitMaskSet(1, oddBitsMask));
        assertFalse(BitHelper.isBitMaskSet(1, evenBitsMask));
        assertFalse(BitHelper.isBitMaskSet(INT_HIGH, oddBitsMask));

        long maskZero = 0L;
        long maskAll = -1L;
        long maskOddBits = 0xAAAAAAAAAAAAAAAAL;
        long maskEvenBits = 0x5555555555555555L;
        assertTrue(BitHelper.isBitMaskSet(1L, maskZero));
        assertTrue(BitHelper.isBitMaskSet(0L, maskZero));
        assertTrue(BitHelper.isBitMaskSet(-1L, maskAll));
        assertFalse(BitHelper.isBitMaskSet(1L, maskOddBits));
        assertFalse(BitHelper.isBitMaskSet(1L, maskEvenBits));
        assertFalse(BitHelper.isBitMaskSet(INT_HIGH, maskOddBits));
    }

    @Test public void testAreAllBitsSet()
    {
        assertFalse(BitHelper.areAllBitsSet(0));
        assertFalse(BitHelper.areAllBitsSet(1));
        assertFalse(BitHelper.areAllBitsSet(INT_HIGH));
        assertFalse(BitHelper.areAllBitsSet(~INT_HIGH));
        assertFalse(BitHelper.areAllBitsSet(~1));
        assertTrue(BitHelper.areAllBitsSet(~0));

        assertFalse(BitHelper.areAllBitsSet(0L));
        assertFalse(BitHelper.areAllBitsSet(1L));
        assertFalse(BitHelper.areAllBitsSet(LONG_HIGH));
        assertFalse(BitHelper.areAllBitsSet(~LONG_HIGH));
        assertFalse(BitHelper.areAllBitsSet(~1L));
        assertTrue(BitHelper.areAllBitsSet(~0L));
    }

    @Test public void testAreAnyBitsClear()
    {
        assertTrue(BitHelper.areAnyBitsClear(0));
        assertTrue(BitHelper.areAnyBitsClear(1));
        assertTrue(BitHelper.areAnyBitsClear(INT_HIGH));
        assertTrue(BitHelper.areAnyBitsClear(~INT_HIGH));
        assertTrue(BitHelper.areAnyBitsClear(~1));
        assertFalse(BitHelper.areAnyBitsClear(~0));

        assertTrue(BitHelper.areAnyBitsClear(0L));
        assertTrue(BitHelper.areAnyBitsClear(1L));
        assertTrue(BitHelper.areAnyBitsClear(LONG_HIGH));
        assertTrue(BitHelper.areAnyBitsClear(~LONG_HIGH));
        assertTrue(BitHelper.areAnyBitsClear(~1L));
        assertFalse(BitHelper.areAnyBitsClear(~0L));
    }

    @Test public void testAreAnyBitsFromMaskClear()
    {
        int zeroMask = 0;
        int allMask = -1;
        int oddBitsMask = 0xAAAAAAAA;   // bits 1(=2), 3(=8), 5(=32) ...
        int evenBitsMask = 0x55555555;  // bits 0 (=1), 2(=4), 4 (=16) ...
        int highBitMask = INT_HIGH;

        assertFalse(BitHelper.areAnyBitsFromBitMaskClear(0, zeroMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskClear(0, allMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskClear(1, oddBitsMask));
        assertFalse(BitHelper.areAnyBitsFromBitMaskClear(~0, oddBitsMask));
        assertFalse(BitHelper.areAnyBitsFromBitMaskClear(-10, highBitMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskClear(-2, evenBitsMask));

        long maskZero = 0L;
        long maskAll = -1L;
        long maskOddBits = 0xAAAAAAAAAAAAAAAAL;
        long maskEvenBits = 0x5555555555555555L;
        long maskHighBit = LONG_HIGH;

        assertFalse(BitHelper.areAnyBitsFromBitMaskClear(0L, maskZero));
        assertTrue(BitHelper.areAnyBitsFromBitMaskClear(0L, maskAll));
        assertTrue(BitHelper.areAnyBitsFromBitMaskClear(1L, maskOddBits));
        assertFalse(BitHelper.areAnyBitsFromBitMaskClear(~0L, maskOddBits));
        assertFalse(BitHelper.areAnyBitsFromBitMaskClear(-10L, maskHighBit));
        assertTrue(BitHelper.areAnyBitsFromBitMaskClear(-2L, maskEvenBits));
    }

    @Test public void testAreAnyBitsFromBitMaskSet()
    {
        int zeroMask = 0;
        int allMask = -1;
        int highBitMask = INT_HIGH;
        int lowBitMask = 1;

        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(1, lowBitMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(~0, lowBitMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(100000001, lowBitMask));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(42, lowBitMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(1024, allMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(-1, allMask));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(0, allMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(INT_HIGH, highBitMask));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(-1984, highBitMask));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(2525, highBitMask));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(0, zeroMask));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(~0, zeroMask));

        long maskZero = 0L;
        long maskAll = -1L;
        long maskHighBit = LONG_HIGH;
        long maskLowBit = 1L;

        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(1L, maskLowBit));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(~0L, maskLowBit));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(100000001L, maskLowBit));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(42L, maskLowBit));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(1024L, maskAll));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(-1L, maskAll));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(0L, maskAll));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(Long.MIN_VALUE,
                maskHighBit));
        assertTrue(BitHelper.areAnyBitsFromBitMaskSet(-1984L, maskHighBit));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(2525L, maskHighBit));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(0L, maskZero));
        assertFalse(BitHelper.areAnyBitsFromBitMaskSet(~0L, maskZero));
    }

    @Test public void testClearBitAt()
    {
        int zero = 0;
        int low = 1;
        int twelve = 12;
        int high = INT_HIGH;

        assertEquals(zero, BitHelper.clearBitAt(zero, 0));
        assertEquals(zero, BitHelper.clearBitAt(zero, 1));
        assertEquals(zero, BitHelper.clearBitAt(zero, 31));
        assertEquals(0, BitHelper.clearBitAt(low, 0));
        assertEquals(low, BitHelper.clearBitAt(low, 1));
        assertEquals(low, BitHelper.clearBitAt(low, 20));
        assertEquals(high, BitHelper.clearBitAt(high, 0));
        assertEquals(0, BitHelper.clearBitAt(high, 31));
        assertEquals(8, BitHelper.clearBitAt(twelve, 2));

        long efes = 0L;
        long echad = 1L;
        long shteymEsreh = 12L;
        long gavoha = LONG_HIGH;

        assertEquals(efes, BitHelper.clearBitAt(efes, 0));
        assertEquals(efes, BitHelper.clearBitAt(efes, 1));
        assertEquals(efes, BitHelper.clearBitAt(efes, 31));
        assertEquals(0L, BitHelper.clearBitAt(echad, 0));
        assertEquals(echad, BitHelper.clearBitAt(echad, 1));
        assertEquals(echad, BitHelper.clearBitAt(echad, 20));
        assertEquals(gavoha, BitHelper.clearBitAt(gavoha, 0));
        assertEquals(0L, BitHelper.clearBitAt(gavoha, 63));
        assertEquals(8L, BitHelper.clearBitAt(shteymEsreh, 2));
    }

    @Test public void testClearBits()
    {
        int zero = 0;
        int all = ~0;
        
        assertEquals(zero, BitHelper.clearBits(zero, 0x111));
        assertEquals(Integer.MAX_VALUE, BitHelper.clearBits(all, INT_HIGH));
        assertEquals(-2, BitHelper.clearBits(all, 1));
        assertEquals(0xFFFEEEFD, BitHelper.clearBits(all, 0x11102));

        long nitz = 0L;
        long altz = ~0L;

        assertEquals(nitz, BitHelper.clearBits(zero, 0x111L));
        assertEquals(Long.MAX_VALUE, BitHelper.clearBits(altz, LONG_HIGH));
        assertEquals(-2L, BitHelper.clearBits(altz, 1L));
        assertEquals(0xFFFFFFFFFEEEFDFFL, BitHelper.clearBits(altz,0x1110200L));
    }

    @Test public void testClearLeastSignificantBits()
    {
        int zero = 0;
        int all = ~0;
        int cccc = 400;

        assertEquals(cccc, BitHelper.clearLeastSignificantBits(cccc, 0));
        assertEquals(zero, BitHelper.clearLeastSignificantBits(zero, 5));
        assertEquals(INT_HIGH, BitHelper.clearLeastSignificantBits(all,
                Integer.SIZE-1));
        assertEquals(2, BitHelper.clearLeastSignificantBits(3, 1));
        assertEquals(0xFFFF0000, BitHelper.clearLeastSignificantBits(all,
                Integer.SIZE/2));

        long lzero = 0L;
        long lall = ~0L;
        long lcccc = 400L;

        assertEquals(lcccc, BitHelper.clearLeastSignificantBits(lcccc, 0));
        assertEquals(lzero, BitHelper.clearLeastSignificantBits(lzero, 5));
        assertEquals(LONG_HIGH, BitHelper.clearLeastSignificantBits(lall,
                Long.SIZE-1));
        assertEquals(2L, BitHelper.clearLeastSignificantBits(3L, 1));
        assertEquals(0xFFFFFFFF00000000L, BitHelper.clearLeastSignificantBits(
                lall, Long.SIZE/2));
    }

    @Test public void testClearMostSignificantBits()
    {
        int all = ~0;
        assertEquals(1, BitHelper.clearMostSignificantBits(all,Integer.SIZE-1));
        assertEquals(3, BitHelper.clearMostSignificantBits(all,Integer.SIZE-2));
        assertEquals(Integer.MAX_VALUE,
                BitHelper.clearMostSignificantBits(all, 1));
        assertEquals(0, BitHelper.clearMostSignificantBits(0, 12));

        long lall = ~0L;
        assertEquals(1L, BitHelper.clearMostSignificantBits(lall, Long.SIZE-1));
        assertEquals(3L, BitHelper.clearMostSignificantBits(lall, Long.SIZE-2));
        assertEquals(Long.MAX_VALUE,
                BitHelper.clearMostSignificantBits(lall, 1));
        assertEquals(0L, BitHelper.clearMostSignificantBits(0L, 12));
    }

    @Test public void testSetLeastSignificantBits()
    {
        assertEquals(1, BitHelper.setLeastSignificantBits(0, 1));
        assertEquals(7, BitHelper.setLeastSignificantBits(0, 3));
        assertEquals(Integer.MAX_VALUE,
                BitHelper.setLeastSignificantBits(0, Integer.SIZE-1));
        assertEquals(-1, BitHelper.setLeastSignificantBits(0, Integer.SIZE));
        assertEquals(0xF000000F,
                BitHelper.setLeastSignificantBits(0xF0000000, 4));

        assertEquals(1L, BitHelper.setLeastSignificantBits(0L, 1));
        assertEquals(7L, BitHelper.setLeastSignificantBits(0L, 3));
        assertEquals(Long.MAX_VALUE,
                BitHelper.setLeastSignificantBits(0L, Long.SIZE-1));
        assertEquals(-1, BitHelper.setLeastSignificantBits(0L, Long.SIZE));
        assertEquals(0xF00000000000000FL,
                BitHelper.setLeastSignificantBits(0xF000000000000000L, 4));
    }

    @Test public void testSetMostSignificantBits()
    {
        assertEquals(Integer.MIN_VALUE, BitHelper.setMostSignificantBits(0, 1));
        assertEquals(-1, BitHelper.setMostSignificantBits(0, Integer.SIZE));
        assertEquals(0xFF000001, BitHelper.setMostSignificantBits(1, 8));

        assertEquals(Long.MIN_VALUE, BitHelper.setMostSignificantBits(0L, 1));
        assertEquals(-1L, BitHelper.setMostSignificantBits(0L, Long.SIZE));
        assertEquals(0xFF00000000000001L,
                BitHelper.setMostSignificantBits(1L, 8));
    }

    @Test public void testDoAllBitsMatch()
    {
        assertTrue(BitHelper.doAllBitsMatch(0, 0));
        assertTrue(BitHelper.doAllBitsMatch(-1, -1));
        assertTrue(BitHelper.doAllBitsMatch(Integer.MIN_VALUE,
                Integer.MIN_VALUE));
        assertTrue(BitHelper.doAllBitsMatch(Integer.MAX_VALUE,
                Integer.MAX_VALUE));
        assertFalse(BitHelper.doAllBitsMatch(0, 1));
        assertFalse(BitHelper.doAllBitsMatch(1, 0));
        assertFalse(BitHelper.doAllBitsMatch(Integer.MIN_VALUE,
                Integer.MIN_VALUE+1));
        assertFalse(BitHelper.doAllBitsMatch(1, -1));

        assertTrue(BitHelper.doAllBitsMatch(0L, 0L));
        assertTrue(BitHelper.doAllBitsMatch(-1L, -1L));
        assertTrue(BitHelper.doAllBitsMatch(Long.MIN_VALUE, Long.MIN_VALUE));
        assertTrue(BitHelper.doAllBitsMatch(Long.MAX_VALUE, Long.MAX_VALUE));
        assertFalse(BitHelper.doAllBitsMatch(0L, 1L));
        assertFalse(BitHelper.doAllBitsMatch(1L, 0L));
        assertFalse(BitHelper.doAllBitsMatch(Long.MIN_VALUE, Long.MIN_VALUE+1));
        assertFalse(BitHelper.doAllBitsMatch(1L, -1L));
    }

    @Test public void testFindClearBit()
    {
        assertEquals(0, BitHelper.findClearBit(0));
        assertEquals(2, BitHelper.findClearBit(0, 2));
        assertEquals(-1, BitHelper.findClearBit(-1));
        assertEquals(3, BitHelper.findClearBit(0x70, 3));
        assertEquals(7, BitHelper.findClearBit(0x70, 4));
        assertEquals(Integer.SIZE-1, BitHelper.findClearBit(Integer.MAX_VALUE));

        assertEquals(0, BitHelper.findClearBit(0L));
        assertEquals(2, BitHelper.findClearBit(0L, 2));
        assertEquals(-1, BitHelper.findClearBit(-1L));
        assertEquals(3, BitHelper.findClearBit(0x70L, 3));
        assertEquals(7, BitHelper.findClearBit(0x70L, 4));
        assertEquals(Long.SIZE-1, BitHelper.findClearBit(Long.MAX_VALUE));
    }

    @Test public void testFindSetBit()
    {
        assertEquals(-1, BitHelper.findSetBit(0));
        assertEquals(3, BitHelper.findSetBit(8));
        assertEquals(-1, BitHelper.findSetBit(8, 4));
        assertEquals(Integer.SIZE-1, BitHelper.findSetBit(Integer.MIN_VALUE));

        assertEquals(-1, BitHelper.findSetBit(0L));
        assertEquals(3, BitHelper.findSetBit(8L));
        assertEquals(-1, BitHelper.findSetBit(8L, 4));
        assertEquals(Long.SIZE-1, BitHelper.findSetBit(Long.MIN_VALUE));
    }

    @Test public void testIsBitClearAt()
    {
        assertTrue(BitHelper.isBitClearAt(0, 0));
        assertFalse(BitHelper.isBitClearAt(1, 0));
        assertTrue(BitHelper.isBitClearAt(Integer.MIN_VALUE, Integer.SIZE-2));
        assertFalse(BitHelper.isBitClearAt(-1, Integer.SIZE-1));

        assertTrue(BitHelper.isBitClearAt(0L, 0));
        assertFalse(BitHelper.isBitClearAt(1L, 0));
        assertTrue(BitHelper.isBitClearAt(Long.MIN_VALUE, Long.SIZE-2));
        assertFalse(BitHelper.isBitClearAt(-1L, Long.SIZE-1));
    }

    @Test public void testIsBitSetAt()
    {
        assertFalse(BitHelper.isBitSetAt(0, 0));
        assertTrue(BitHelper.isBitSetAt(1, 0));
        assertFalse(BitHelper.isBitSetAt(Integer.MIN_VALUE, Integer.SIZE-2));
        assertTrue(BitHelper.isBitSetAt(-1, Integer.SIZE-1));

        assertFalse(BitHelper.isBitSetAt(0L, 0));
        assertTrue(BitHelper.isBitSetAt(1L, 0));
        assertFalse(BitHelper.isBitSetAt(Long.MIN_VALUE, Long.SIZE-2));
        assertTrue(BitHelper.isBitSetAt(-1L, Long.SIZE-1));
    }

    @Test public void testNumberClearBits()
    {
        assertEquals(0, BitHelper.numberClearBits(-1));
        assertEquals(1, BitHelper.numberClearBits(Integer.MAX_VALUE));
        assertEquals(Integer.SIZE-1,
                BitHelper.numberClearBits(Integer.MIN_VALUE));
        assertEquals(Integer.SIZE-1, BitHelper.numberClearBits(1));
        assertEquals(Integer.SIZE-1, BitHelper.numberClearBits(16777216));
        assertEquals(Integer.SIZE/2, BitHelper.numberClearBits(0xAAAAAAAA));
        assertEquals(Integer.SIZE/2, BitHelper.numberClearBits(0x55555555));

        assertEquals(0, BitHelper.numberClearBits(-1L));
        assertEquals(1, BitHelper.numberClearBits(Long.MAX_VALUE));
        assertEquals(Long.SIZE-1, BitHelper.numberClearBits(Long.MIN_VALUE));
        assertEquals(Long.SIZE-1, BitHelper.numberClearBits(1L));
        assertEquals(Long.SIZE-1, BitHelper.numberClearBits(16777216L));
        assertEquals(Long.SIZE/2,
                BitHelper.numberClearBits(0xAAAAAAAAAAAAAAAAL));
        assertEquals(Long.SIZE/2,
                BitHelper.numberClearBits(0x5555555555555555L));
    }

    @Test public void testNumberSetBits()
    {
        assertEquals(Integer.SIZE, BitHelper.numberSetBits(-1));
        assertEquals(Integer.SIZE-1,BitHelper.numberSetBits(Integer.MAX_VALUE));
        assertEquals(1, BitHelper.numberSetBits(Integer.MIN_VALUE));
        assertEquals(1, BitHelper.numberSetBits(1));
        assertEquals(1, BitHelper.numberSetBits(16777216));
        assertEquals(Integer.SIZE/2, BitHelper.numberSetBits(0xAAAAAAAA));
        assertEquals(Integer.SIZE/2, BitHelper.numberSetBits(0x55555555));

        assertEquals(Long.SIZE, BitHelper.numberSetBits(-1L));
        assertEquals(Long.SIZE-1, BitHelper.numberSetBits(Long.MAX_VALUE));
        assertEquals(1, BitHelper.numberSetBits(Long.MIN_VALUE));
        assertEquals(1, BitHelper.numberSetBits(1L));
        assertEquals(1, BitHelper.numberSetBits(16777216L));
        assertEquals(Long.SIZE/2, BitHelper.numberSetBits(0xAAAAAAAAAAAAAAAAL));
        assertEquals(Long.SIZE/2, BitHelper.numberSetBits(0x5555555555555555L));
    }

    @Test public void testSetBitAt()
    {
        assertEquals(1, BitHelper.setBitAt(0));
        assertEquals(16, BitHelper.setBitAt(4));
        assertEquals(Integer.MIN_VALUE, BitHelper.setBitAt(Integer.SIZE-1));

        assertEquals(0x100F, BitHelper.setBitAt(0xF, 12));
        assertEquals(-1, BitHelper.setBitAt(-2, 0));
        assertEquals(-1, BitHelper.setBitAt(Integer.MAX_VALUE, Integer.SIZE-1));

        assertEquals(0x100FL, BitHelper.setBitAt(0xFL, 12));
        assertEquals(-1L, BitHelper.setBitAt(-2L, 0));
        assertEquals(-1L, BitHelper.setBitAt(Long.MAX_VALUE, Long.SIZE-1));
    }

    @Test public void testSetBits()
    {
        int zero = 0;
        int all = ~0;
        assertEquals(0x12345, BitHelper.setBits(zero, 0x12345));
        assertEquals(all, BitHelper.setBits(all, 0x12345));
        assertEquals(zero, BitHelper.setBits(zero, 0));
        assertEquals(all, BitHelper.setBits(all, all));
        assertEquals(INT_HIGH, BitHelper.setBits(zero, INT_HIGH));
        assertEquals(INT_HIGH, BitHelper.setBits(Integer.MIN_VALUE, 0));
        assertEquals(Integer.MIN_VALUE+1,
                BitHelper.setBits(Integer.MIN_VALUE,1));

        long lzero = 0L;
        long lall = ~0L;
        assertEquals(0x12345L, BitHelper.setBits(lzero, 0x12345L));
        assertEquals(lall, BitHelper.setBits(lall, 0x12345L));
        assertEquals(lzero, BitHelper.setBits(lzero, 0L));
        assertEquals(lall, BitHelper.setBits(lall, lall));
        assertEquals(LONG_HIGH, BitHelper.setBits(lzero, LONG_HIGH));
        assertEquals(LONG_HIGH, BitHelper.setBits(Long.MIN_VALUE, 0L));
        assertEquals(Long.MIN_VALUE+1, BitHelper.setBits(Long.MIN_VALUE,1L));
    }

    @Test public void testInsertLSNPortionOfLongIntoAnotherLong()
    {
        long zero = 0L;
        long all = ~0L;
        assertEquals(zero, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                zero, 20, zero, 5));
        assertEquals(all, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                all, 42, all, 10));
        assertEquals(0x700000, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                zero, 20, 0xFFL, 3));
        assertEquals(LONG_HIGH, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                zero, Long.SIZE-1, all, 1));
        assertEquals(1L, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                zero, 0, all, 1));
        assertEquals(0L, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                1L, 0, zero, 1));
        assertEquals(1L, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                1L, 10, zero, 5));
        assertEquals(11L, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                10L, 0, 1L, 1));
        assertEquals(0x40AL, BitHelper.insertLSNPortionOfLongIntoAnotherLong(
                10L, 10, 1L, 1));
    }

    @Test public void testReverseBits()
    {
        int zero = 0;
        int all = ~0;
        assertEquals(zero, BitHelper.reverseBits(zero));
        assertEquals(all, BitHelper.reverseBits(all));
        assertEquals(1, BitHelper.reverseBits(INT_HIGH));
        assertEquals(0x55555555, BitHelper.reverseBits(0xAAAAAAAA));
        assertEquals(0xCCCC0000, BitHelper.reverseBits(0x3333));
        assertEquals(0x993300, BitHelper.reverseBits(0xCC9900));
        assertEquals(0x03030303, BitHelper.reverseBits(0xC0C0C0C0));
        assertEquals(0xC0C0C0C0, BitHelper.reverseBits(0x03030303));
    }

    @Test public void testIndexLsb()
    {
        for (int i = 0; i < Integer.SIZE; ++i)
        {
            assertEquals("indexLsb(" + i + ")", i, BitHelper.indexLsb(1 << i));
            assertEquals("indexLsb(HIGH|" + i + ")", i,
                    BitHelper.indexLsb(INT_HIGH | (1 << i)));
        }
    }

    @Test public void testLsb()
    {
        assertEquals(0, BitHelper.lsb(0));
        assertEquals(0x20, BitHelper.lsb(0xF00F00E0));
        assertEquals(INT_HIGH, BitHelper.lsb(INT_HIGH));
        assertEquals(1, BitHelper.lsb(1));
        assertEquals(0x40000000, BitHelper.lsb(0xC0000000));
        assertEquals(0x10, BitHelper.lsb(0x12345670));
    }

    @Test public void testMsb()
    {
        assertEquals(INT_HIGH, BitHelper.msb(~0));
        assertEquals(1, BitHelper.msb(1));
        assertEquals(0x8000, BitHelper.msb(0xF037));
        assertEquals(0, BitHelper.msb(0));
        assertEquals(INT_HIGH, BitHelper.msb(0x80000001));
        assertEquals(INT_HIGH, BitHelper.msb(0xF0000000));
    }

    @Test public void testCountOnes()
    {
        for (int i = INT_HIGH; i != 0; i >>>= 1)
        {
            assertEquals(Integer.toString(i), 1, BitHelper.countOnes(i));
        }
        assertEquals(0, BitHelper.countOnes(0));
        assertEquals(Integer.SIZE, BitHelper.countOnes(~0));
        assertEquals(3, BitHelper.countOnes(0xC0000001));
        assertEquals(3, BitHelper.countOnes(0x4000C000));
        assertEquals(4, BitHelper.countOnes(0xF0000000));
        assertEquals(Integer.SIZE/2, BitHelper.countOnes(0xAAAAAAAA));
        assertEquals(Integer.SIZE/2, BitHelper.countOnes(0x55555555));
        assertEquals(Integer.SIZE/2, BitHelper.countOnes(0xFFFF0000));
        assertEquals(Integer.SIZE/2, BitHelper.countOnes(0xFFFF));
    }

    @Test public void testCountZeroes()
    {
        for (int i = INT_HIGH; i != 0; i >>>= 1)
        {
            assertEquals(Integer.toString(i), Integer.SIZE-1,
                    BitHelper.countZeroes(i));
        }
        assertEquals(Integer.SIZE, BitHelper.countZeroes(0));
        assertEquals(0, BitHelper.countZeroes(~0));
        assertEquals(Integer.SIZE-3, BitHelper.countZeroes(0xC0000001));
        assertEquals(Integer.SIZE-3, BitHelper.countZeroes(0x4000C000));
        assertEquals(Integer.SIZE-4, BitHelper.countZeroes(0xF0000000));
        assertEquals(Integer.SIZE/2, BitHelper.countZeroes(0xAAAAAAAA));
        assertEquals(Integer.SIZE/2, BitHelper.countZeroes(0x55555555));
        assertEquals(Integer.SIZE/2, BitHelper.countZeroes(0xFFFF0000));
        assertEquals(Integer.SIZE/2, BitHelper.countZeroes(0xFFFF));
    }

    @Test public void testCountTrailingZeroes()
    {
        for (int i = 0; i < Integer.SIZE; ++i)
        {
            String is = Integer.toString(i);
            assertEquals(is, i, BitHelper.countTrailingZeroes(1 << i));
            assertEquals(is, i, BitHelper.countTrailingZeroes((~0) << i));
        }
        assertEquals(1, BitHelper.countTrailingZeroes(-2));
        assertEquals(4, BitHelper.countTrailingZeroes(-16));
        assertEquals(0, BitHelper.countTrailingZeroes(0x80000001));
    }

    @Test public void testToBinaryString()
    {
        assertEquals("00000000 00000000 00000000 00000000",
                BitHelper.toBinaryString(0));
        assertEquals("10000001 00000000 00000000 00000011",
                BitHelper.toBinaryString(0x81000003));
        assertEquals("11111111 11111111 11111111 11111110",
                BitHelper.toBinaryString(-2));

        assertEquals("00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000",
                BitHelper.toBinaryString(0L));
        assertEquals("10000001 00000000 00000000 00000011 00000000 01000101 00000000 00000111",
                BitHelper.toBinaryString(0x8100000300450007L));
        assertEquals("11111111 11111111 11111111 11111111 11111111 11111111 11111111 11111110",
                BitHelper.toBinaryString(-2L));
    }

    @Test public void testPackInt()
    {
        assertEquals(0, BitHelper.packInt(0, 0));
        assertEquals(0x10000, BitHelper.packInt(1, 0));
        assertEquals(INT_HIGH, BitHelper.packInt(0x8000, 0));
        assertEquals(~0, BitHelper.packInt(0xFFFF, 0xFFFF));
        assertEquals(0, BitHelper.packInt(0x10000, 0));

        byte b0 = 0;
        byte bhigh = (byte) 0x80;
        byte ball = (byte) 0xFF;
        byte bmid = (byte) 0x3C;

        assertEquals(0xFF000000, BitHelper.packInt(ball, 0));
        assertEquals(0, BitHelper.packInt(b0, 0));
        assertEquals(0x3C000001, BitHelper.packInt(bmid, 1));
        assertEquals(INT_HIGH, BitHelper.packInt(bhigh, 0));
        assertEquals(~0, BitHelper.packInt(b0, ~0)); // I expected 0xFFFFFF
        assertEquals(0, BitHelper.packInt(b0, 0x800000)); // I expected 0x800000
    }

    @Test public void testUnpackShortFromLong()
    {
        long high = 0x89ABCDEF01234567L;
        long low  = 0x123456789ABCDEF0L;
        short result;

        result = BitHelper.unpackHighShort(high);
        assertEquals((short)0x89AB, result);
        result = BitHelper.unpackHighShort(low);
        assertEquals((short)0x1234, result);

        result = BitHelper.unpackMiddleHighShort(high);
        assertEquals((short)0xCDEF, result);
        result = BitHelper.unpackMiddleHighShort(low);
        assertEquals((short)0x5678, result);

        result = BitHelper.unpackMiddleLowShort(high);
        assertEquals((short)0xEF01, result);
        result = BitHelper.unpackMiddleLowShort(low);
        assertEquals((short)0x789A, result);

        result = BitHelper.unpackLowShort(high);
        assertEquals((short)0x0123, result);
        result = BitHelper.unpackLowShort(low);
        assertEquals((short)0x9ABC, result);
    }

    @Test public void testUnpackIntFromLong()
    {
        long high = 0x89ABCDEF01234567L;
        long low  = 0x123456789ABCDEF0L;
        int result;

        result = BitHelper.unpackHighInt(high);
        assertEquals(0xFFFF89AB, result);
        result = BitHelper.unpackHighInt(low);
        assertEquals(0x1234, result);

        result = BitHelper.unpackMiddleHighInt(high);
        assertEquals(0x89ABCDEF, result);
        result = BitHelper.unpackMiddleHighInt(low);
        assertEquals(0x12345678, result);

        result = BitHelper.unpackMiddleLowInt(high);
        assertEquals(0xABCDEF01, result);
        result = BitHelper.unpackMiddleLowInt(low);
        assertEquals(0x3456789A, result);

        result = BitHelper.unpackLowInt(high);
        assertEquals(0xCDEF0123, result);
        result = BitHelper.unpackLowInt(low);
        assertEquals(0x56789ABC, result);
    }

    @Test public void testUnpackByte()
    {
        int high = 0x89ABCDEF;
        int low = 0x12345678;
        byte result;

        result = BitHelper.unpackHighByte(high);
        assertEquals(0xFFFFFF89, result);
        result = BitHelper.unpackHighByte(low);
        assertEquals(0x12, result);

        result = BitHelper.unpackMiddleHighByte(high);
        assertEquals(0xFFFFFFAB, result);
        result = BitHelper.unpackMiddleHighByte(low);
        assertEquals(0x34, result);

        result = BitHelper.unpackMiddleLowByte(high);
        assertEquals(0xFFFFFFCD, result);
        result = BitHelper.unpackMiddleLowByte(low);
        assertEquals(0x56, result);

        result = BitHelper.unpackLowByte(high);
        assertEquals(0xFFFFFFEF, result);
        result = BitHelper.unpackLowByte(low);
        assertEquals(0x78, result);
    }

    @Test public void testUnpackShortFromInt()
    {
        int high = 0x89ABCDEF;
        int low  = 0x12345678;
        short sresult;
        int iresult;

        sresult = BitHelper.unpackHighShort(high);
        assertEquals((short)0x89AB, sresult);
        sresult = BitHelper.unpackHighShort(low);
        assertEquals((short)0x1234, sresult);

        iresult = BitHelper.unpackHighShortAsInt(high);
        assertEquals(0xFFFF89AB, iresult);
        iresult = BitHelper.unpackHighShortAsInt(low);
        assertEquals(0x1234, iresult);

        sresult = BitHelper.unpackMiddleShort(high);
        assertEquals((short)0xABCD, sresult);
        sresult = BitHelper.unpackMiddleShort(low);
        assertEquals((short)0x3456, sresult);

        iresult = BitHelper.unpackMiddleShortAsInt(high);
        assertEquals(0xFFFFABCD, iresult);
        iresult = BitHelper.unpackMiddleShortAsInt(low);
        assertEquals(0x3456, iresult);

        sresult = BitHelper.unpackLowShort(high);
        assertEquals((short)0xCDEF, sresult);
        sresult = BitHelper.unpackLowShort(low);
        assertEquals((short)0x5678, sresult);

        iresult = BitHelper.unpackLowShortAsInt(high);
        assertEquals(0xFFFFCDEF, iresult);
        iresult = BitHelper.unpackLowShortAsInt(low);
        assertEquals(0x5678, iresult);
    }
}
