package com.cboe.client.util;

/**
 * BitHelper.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Helper file for bit manipulation
 *
 */

public final class BitHelper
{
    public static final int  BITS_PER_INT      = 32; // sizeof(int)
    public static final int  BITS_PER_LONG     = 64; // sizeof(long)
    public static final int  INT_ALL_BITS_SET  = ~0;
    public static final long LONG_ALL_BITS_SET = ~0L;

    public static final int INT_SET_BIT_0  = 1;
    public static final int INT_SET_BIT_1  = INT_SET_BIT_0  << 1;   public static final int INT_SET_BIT_2  = INT_SET_BIT_1  << 1;   public static final int INT_SET_BIT_3  = INT_SET_BIT_2  << 1;   public static final int INT_SET_BIT_4  = INT_SET_BIT_3  << 1;
    public static final int INT_SET_BIT_5  = INT_SET_BIT_4  << 1;   public static final int INT_SET_BIT_6  = INT_SET_BIT_5  << 1;   public static final int INT_SET_BIT_7  = INT_SET_BIT_6  << 1;   public static final int INT_SET_BIT_8  = INT_SET_BIT_7  << 1;
    public static final int INT_SET_BIT_9  = INT_SET_BIT_8  << 1;   public static final int INT_SET_BIT_10 = INT_SET_BIT_9  << 1;   public static final int INT_SET_BIT_11 = INT_SET_BIT_10 << 1;   public static final int INT_SET_BIT_12 = INT_SET_BIT_11 << 1;
    public static final int INT_SET_BIT_13 = INT_SET_BIT_12 << 1;   public static final int INT_SET_BIT_14 = INT_SET_BIT_13 << 1;   public static final int INT_SET_BIT_15 = INT_SET_BIT_14 << 1;   public static final int INT_SET_BIT_16 = INT_SET_BIT_15 << 1;
    public static final int INT_SET_BIT_17 = INT_SET_BIT_16 << 1;   public static final int INT_SET_BIT_18 = INT_SET_BIT_17 << 1;   public static final int INT_SET_BIT_19 = INT_SET_BIT_18 << 1;   public static final int INT_SET_BIT_20 = INT_SET_BIT_19 << 1;
    public static final int INT_SET_BIT_21 = INT_SET_BIT_20 << 1;   public static final int INT_SET_BIT_22 = INT_SET_BIT_21 << 1;   public static final int INT_SET_BIT_23 = INT_SET_BIT_22 << 1;   public static final int INT_SET_BIT_24 = INT_SET_BIT_23 << 1;
    public static final int INT_SET_BIT_25 = INT_SET_BIT_24 << 1;   public static final int INT_SET_BIT_26 = INT_SET_BIT_25 << 1;   public static final int INT_SET_BIT_27 = INT_SET_BIT_26 << 1;   public static final int INT_SET_BIT_28 = INT_SET_BIT_27 << 1;
    public static final int INT_SET_BIT_29 = INT_SET_BIT_28 << 1;   public static final int INT_SET_BIT_30 = INT_SET_BIT_29 << 1;   public static final int INT_SET_BIT_31 = INT_SET_BIT_30 << 1;

    public static final long LONG_SET_BIT_0  = 1L;
    public static final long LONG_SET_BIT_1  = LONG_SET_BIT_0  << 1; public static final long LONG_SET_BIT_2  = LONG_SET_BIT_1  << 1; public static final long LONG_SET_BIT_3  = LONG_SET_BIT_2  << 1; public static final long LONG_SET_BIT_4  = LONG_SET_BIT_3  << 1;
    public static final long LONG_SET_BIT_5  = LONG_SET_BIT_4  << 1; public static final long LONG_SET_BIT_6  = LONG_SET_BIT_5  << 1; public static final long LONG_SET_BIT_7  = LONG_SET_BIT_6  << 1; public static final long LONG_SET_BIT_8  = LONG_SET_BIT_7  << 1;
    public static final long LONG_SET_BIT_9  = LONG_SET_BIT_8  << 1; public static final long LONG_SET_BIT_10 = LONG_SET_BIT_9  << 1; public static final long LONG_SET_BIT_11 = LONG_SET_BIT_10 << 1; public static final long LONG_SET_BIT_12 = LONG_SET_BIT_11 << 1;
    public static final long LONG_SET_BIT_13 = LONG_SET_BIT_12 << 1; public static final long LONG_SET_BIT_14 = LONG_SET_BIT_13 << 1; public static final long LONG_SET_BIT_15 = LONG_SET_BIT_14 << 1; public static final long LONG_SET_BIT_16 = LONG_SET_BIT_15 << 1;
    public static final long LONG_SET_BIT_17 = LONG_SET_BIT_16 << 1; public static final long LONG_SET_BIT_18 = LONG_SET_BIT_17 << 1; public static final long LONG_SET_BIT_19 = LONG_SET_BIT_18 << 1; public static final long LONG_SET_BIT_20 = LONG_SET_BIT_19 << 1;
    public static final long LONG_SET_BIT_21 = LONG_SET_BIT_20 << 1; public static final long LONG_SET_BIT_22 = LONG_SET_BIT_21 << 1; public static final long LONG_SET_BIT_23 = LONG_SET_BIT_22 << 1; public static final long LONG_SET_BIT_24 = LONG_SET_BIT_23 << 1;
    public static final long LONG_SET_BIT_25 = LONG_SET_BIT_24 << 1; public static final long LONG_SET_BIT_26 = LONG_SET_BIT_25 << 1; public static final long LONG_SET_BIT_27 = LONG_SET_BIT_26 << 1; public static final long LONG_SET_BIT_28 = LONG_SET_BIT_27 << 1;
    public static final long LONG_SET_BIT_29 = LONG_SET_BIT_28 << 1; public static final long LONG_SET_BIT_30 = LONG_SET_BIT_29 << 1; public static final long LONG_SET_BIT_31 = LONG_SET_BIT_30 << 1; public static final long LONG_SET_BIT_32 = LONG_SET_BIT_31 << 1;
    public static final long LONG_SET_BIT_33 = LONG_SET_BIT_32 << 1; public static final long LONG_SET_BIT_34 = LONG_SET_BIT_33 << 1; public static final long LONG_SET_BIT_35 = LONG_SET_BIT_34 << 1; public static final long LONG_SET_BIT_36 = LONG_SET_BIT_35 << 1;
    public static final long LONG_SET_BIT_37 = LONG_SET_BIT_36 << 1; public static final long LONG_SET_BIT_38 = LONG_SET_BIT_37 << 1; public static final long LONG_SET_BIT_39 = LONG_SET_BIT_38 << 1; public static final long LONG_SET_BIT_40 = LONG_SET_BIT_39 << 1;
    public static final long LONG_SET_BIT_41 = LONG_SET_BIT_40 << 1; public static final long LONG_SET_BIT_42 = LONG_SET_BIT_41 << 1; public static final long LONG_SET_BIT_43 = LONG_SET_BIT_42 << 1; public static final long LONG_SET_BIT_44 = LONG_SET_BIT_43 << 1;
    public static final long LONG_SET_BIT_45 = LONG_SET_BIT_44 << 1; public static final long LONG_SET_BIT_46 = LONG_SET_BIT_45 << 1; public static final long LONG_SET_BIT_47 = LONG_SET_BIT_46 << 1; public static final long LONG_SET_BIT_48 = LONG_SET_BIT_47 << 1;
    public static final long LONG_SET_BIT_49 = LONG_SET_BIT_48 << 1; public static final long LONG_SET_BIT_50 = LONG_SET_BIT_49 << 1; public static final long LONG_SET_BIT_51 = LONG_SET_BIT_50 << 1; public static final long LONG_SET_BIT_52 = LONG_SET_BIT_51 << 1;
    public static final long LONG_SET_BIT_53 = LONG_SET_BIT_52 << 1; public static final long LONG_SET_BIT_54 = LONG_SET_BIT_53 << 1; public static final long LONG_SET_BIT_55 = LONG_SET_BIT_54 << 1; public static final long LONG_SET_BIT_56 = LONG_SET_BIT_55 << 1;
    public static final long LONG_SET_BIT_57 = LONG_SET_BIT_56 << 1; public static final long LONG_SET_BIT_58 = LONG_SET_BIT_57 << 1; public static final long LONG_SET_BIT_59 = LONG_SET_BIT_58 << 1; public static final long LONG_SET_BIT_60 = LONG_SET_BIT_59 << 1;
    public static final long LONG_SET_BIT_61 = LONG_SET_BIT_60 << 1; public static final long LONG_SET_BIT_62 = LONG_SET_BIT_61 << 1; public static final long LONG_SET_BIT_63 = LONG_SET_BIT_62 << 1;

//-------------------------------------------------------------------------------------------------------------------------------------
    public static int makeBitMask(int index)
    {
        return 1 << index;
    }
    public static int makeBitMask(int index1, int index2)
    {
        return (1 << index1) | (1 << index2);
    }
    public static int makeBitMask(int index1, int index2, int index3)
    {
        return (1 << index1) | (1 << index2) | (1 << index3);
    }
    public static int makeBitMask(int index1, int index2, int index3, int index4)
    {
        return (1 << index1) | (1 << index2) | (1 << index3) | (1 << index4);
    }
    public static int makeBitMask(int index1, int index2, int index3, int index4, int index5)
    {
        return (1 << index1) | (1 << index2) | (1 << index3) | (1 << index4) | (1 << index5);
    }
    public static int makeBitMask(int index1, int index2, int index3, int index4, int index5, int index6)
    {
        return (1 << index1) | (1 << index2) | (1 << index3) | (1 << index4) | (1 << index5) | (1 << index6);
    }
    public static int makeBitMask(int index1, int index2, int index3, int index4, int index5, int index6, int index7)
    {
        return (1 << index1) | (1 << index2) | (1 << index3) | (1 << index4) | (1 << index5) | (1 << index6) | (1 << index7);
    }
    public static int makeBitMask(int index1, int index2, int index3, int index4, int index5, int index6, int index7, int index8)
    {
        return (1 << index1) | (1 << index2) | (1 << index3) | (1 << index4) | (1 << index5) | (1 << index6) | (1 << index7) | (1 << index8);
    }
    public static int makeBitMask(int index1, int index2, int index3, int index4, int index5, int index6, int index7, int index8, int index9)
    {
        return (1 << index1) | (1 << index2) | (1 << index3) | (1 << index4) | (1 << index5) | (1 << index6) | (1 << index7) | (1 << index8) | (1 << index9);
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int orBits(int index)
    {
        return index;
    }
    public static int orBits(int index1, int index2)
    {
        return (index1) | (index2);
    }
    public static int orBits(int index1, int index2, int index3)
    {
        return (index1) | (index2) | (index3);
    }
    public static int orBits(int index1, int index2, int index3, int index4)
    {
        return (index1) | (index2) | (index3) | (index4);
    }
    public static int orBits(int index1, int index2, int index3, int index4, int index5)
    {
        return (index1) | (index2) | (index3) | (index4) | (index5);
    }
    public static int orBits(int index1, int index2, int index3, int index4, int index5, int index6)
    {
        return (index1) | (index2) | (index3) | (index4) | (index5) | (index6);
    }
    public static int orBits(int index1, int index2, int index3, int index4, int index5, int index6, int index7)
    {
        return (index1) | (index2) | (index3) | (index4) | (index5) | (index6) | (index7);
    }
    public static int orBits(int index1, int index2, int index3, int index4, int index5, int index6, int index7, int index8)
    {
        return (index1) | (index2) | (index3) | (index4) | (index5) | (index6) | (index7) | (index8);
    }
    public static int orBits(int index1, int index2, int index3, int index4, int index5, int index6, int index7, int index8, int index9)
    {
        return (index1) | (index2) | (index3) | (index4) | (index5) | (index6) | (index7) | (index8) | (index9);
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean areAllBitsClear(int variable)
    {
        return variable == 0;
    }
    public static boolean areAllBitsClear(long variable)
    {
        return variable == 0L;
    }
    public static boolean areAllBitsFromBitMaskClear(int variable, int bitMask)
    {
        return (variable & bitMask) == 0;
    }
    public static boolean areAllBitsFromBitMaskClear(long variable, long bitMask)
    {
        return (variable & bitMask) == 0L;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean isBitMaskSet(int variable, int bitMask)
    {
        return (variable & bitMask) == bitMask;
    }
    public static boolean isBitMaskSet(long variable, long bitMask)
    {
        return (variable & bitMask) == bitMask;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean areAllBitsSet(int variable)
    {
        return variable == INT_ALL_BITS_SET;
    }
    public static boolean areAllBitsSet(long variable)
    {
        return variable == LONG_ALL_BITS_SET;
    }
    public static boolean areAnyBitsClear(int variable)
    {
        return variable != INT_ALL_BITS_SET;
    }
    public static boolean areAnyBitsClear(long variable)
    {
        return variable != LONG_ALL_BITS_SET;
    }
    public static boolean areAnyBitsFromBitMaskClear(int variable, int bitMask)
    {
        return (variable & bitMask) != bitMask;
    }
    public static boolean areAnyBitsFromBitMaskClear(long variable, long bitMask)
    {
        return (variable & bitMask) != bitMask;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean areAnyBitsFromBitMaskSet(int variable, int bitMask)
    {
        return (variable & bitMask) != 0;
    }
    public static boolean areAnyBitsFromBitMaskSet(long variable, long bitMask)
    {
        return (variable & bitMask) != 0L;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int clearBitAt(int variable, int bitIndex)
    {
        if (bitIndex < BITS_PER_INT)
            return variable & ~(1 << bitIndex);
        return variable;
    }
    public static long clearBitAt(long variable, int bitIndex)
    {
        if (bitIndex < BITS_PER_LONG)
            return variable & ~(1L << bitIndex);
        return variable;
    }
    public static int clearBits(int variable, int bits)
    {
        return variable & ~bits;
    }
    public static long clearBits(long variable, long bits)
    {
        return variable & ~bits;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int clearLeastSignificantBits(int variable, int offset)
    {
       if (variable == 0 || offset >= BITS_PER_INT)
            return variable;

        return variable & (INT_ALL_BITS_SET << offset);
    }
    public static long clearLeastSignificantBits(long variable, int offset)
    {
       if (variable == 0L || offset >= BITS_PER_LONG)
            return variable;

        return variable & (LONG_ALL_BITS_SET << offset);
    }
    public static int clearMostSignificantBits(int variable, int offset)
    {
       if (variable == 0 || offset >= BITS_PER_INT)
            return variable;

        return variable & (INT_ALL_BITS_SET >>> offset);
    }
    public static long clearMostSignificantBits(long variable, int offset)
    {
       if (variable == 0L || offset >= BITS_PER_LONG)
            return variable;

        return variable & (LONG_ALL_BITS_SET >>> offset);
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int setLeastSignificantBits(int variable, int offset)
    {
       if (variable == INT_ALL_BITS_SET || offset > BITS_PER_INT)
            return variable;

        return variable | (INT_ALL_BITS_SET >>> (BITS_PER_INT - offset));
    }
    public static long setLeastSignificantBits(long variable, int offset)
    {
       if (variable == LONG_ALL_BITS_SET || offset > BITS_PER_LONG)
            return variable;

        return variable | (LONG_ALL_BITS_SET >>> (BITS_PER_LONG - offset));
    }
    public static int setMostSignificantBits(int variable, int offset)
    {
       if (variable == INT_ALL_BITS_SET || offset > BITS_PER_INT)
            return variable;

        return variable | (INT_ALL_BITS_SET << (BITS_PER_INT - offset));
    }
    public static long setMostSignificantBits(long variable, int offset)
    {
       if (variable == LONG_ALL_BITS_SET || offset > BITS_PER_LONG)
            return variable;

        return variable | (LONG_ALL_BITS_SET << (BITS_PER_LONG - offset));
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean doAllBitsMatch(int variable, int bitMask)
    {
        return variable == bitMask;
    }
    public static boolean doAllBitsMatch(long variable, long bitMask)
    {
        return variable == bitMask;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int findClearBit(int variable)
    {
        return findClearBit(variable, 0);
    }
    public static int findClearBit(int variable, int offset)
    {
        for (int i = offset; i < BITS_PER_INT; i++)
        {
            if (isBitClearAt(variable, i))
                return i;
        }

        return -1;
    }
    public static int findClearBit(long variable)
    {
        return findClearBit(variable, 0);
    }
    public static int findClearBit(long variable, int offset)
    {
        for (int i = offset; i < BITS_PER_LONG; i++)
        {
            if (isBitClearAt(variable, i))
                return i;
        }

        return -1;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int findSetBit(int variable)
    {
        return findSetBit(variable, 0);
    }
    public static int findSetBit(int variable, int offset)
    {
        for (int i = offset; i < BITS_PER_INT; i++)
        {
            if (isBitSetAt(variable, i))
                return i;
        }

        return -1;
    }
    public static int findSetBit(long variable)
    {
        return findSetBit(variable, 0);
    }
    public static int findSetBit(long variable, int offset)
    {
        for (int i = offset; i < BITS_PER_LONG; i++)
        {
            if (isBitSetAt(variable, i))
                return i;
        }

        return -1;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean isBitClearAt(int variable, int bitIndex)
    {
        return (variable & (1 << bitIndex)) == 0;
    }
    public static boolean isBitClearAt(long variable, int bitIndex)
    {
        return (variable & (1L << bitIndex)) == 0L;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static boolean isBitSetAt(int variable, int bitIndex)
    {
        return !isBitClearAt(variable, bitIndex);
    }
    public static boolean isBitSetAt(long variable, int bitIndex)
    {
        return !isBitClearAt(variable, bitIndex);
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int numberClearBits(int variable)
    {
        int total = 0;

        for (int i = 0; i < BITS_PER_INT; i++)
        {
            if (isBitClearAt(variable, i))
                total++;
        }

        return total;
    }
    public static int numberClearBits(long variable)
    {
        int total = 0;

        for (int i = 0; i < BITS_PER_LONG; i++)
        {
            if (isBitClearAt(variable, i))
                total++;
        }

        return total;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int numberSetBits(int variable)
    {
        int total = 0;

        for (int i = 0; i < BITS_PER_INT; i++)
        {
            if (isBitSetAt(variable, i))
                total++;
        }

        return total;
    }
    public static int numberSetBits(long variable)
    {
        int total = 0;

        for (int i = 0; i < BITS_PER_LONG; i++)
        {
            if (isBitSetAt(variable, i))
                total++;
        }

        return total;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int setBitAt(int bitIndex)
    {
        if (bitIndex < BITS_PER_INT)
            return (1 << bitIndex);
        return 0;
    }
    public static int setBitAt(int variable, int bitIndex)
    {
        if (bitIndex < BITS_PER_INT)
            return variable | (1 << bitIndex);
        return variable;
    }
    public static long setBitAt(long variable, int bitIndex)
    {
        if (bitIndex < BITS_PER_LONG)
            return variable | (1L << bitIndex);
        return variable;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int setBits(int variable, int bits)
    {
        return variable | bits;
    }
    public static long setBits(long variable, long bits)
    {
        return variable | bits;
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    /** Insert least significant part of a long into another long. Example:<BR>
     * target: 0x1234567891234567L, targetDest: 32<BR>
     * source: 0xABCDEFABCDEFABCDL, sourceCount: 8<BR>
     * result: 0x123456CD91234567L
     * @param target value to insert into.
     * @param targetDest rightmost bit of destination bit field
     *     (least significant bit is #0).
     * @param source value to extract bits for inserting into target.
     * @param sourceCount number of bits to extract from source.
     * @return Result of bit transfer.
     */
    public static long insertLSNPortionOfLongIntoAnotherLong(long target, int targetDest, long source, int sourceCount)
    {
        return (target & ~((LONG_ALL_BITS_SET >>> (BITS_PER_LONG-sourceCount)) << targetDest)) | ((source & (LONG_ALL_BITS_SET >>> (BITS_PER_LONG-sourceCount))) << targetDest);
    }
    public static int reverseBits(int x)
    {
	    x = ((x & 0xaaaaaaaa) >>> 1) | ((x & 0x55555555) << 1);
	    x = ((x & 0xcccccccc) >>> 2) | ((x & 0x33333333) << 2);
	    x = ((x & 0xf0f0f0f0) >>> 4) | ((x & 0x0f0f0f0f) << 4);
	    x = ((x & 0xff00ff00) >>> 8) | ((x & 0x00ff00ff) << 8);

	    return (x >>> 16) | (x << 16);
    }
    public static int indexLsb(int x)
    {
        switch (x & -x)
        {
            case INT_SET_BIT_0 : return 0;
            case INT_SET_BIT_1 : return 1;
            case INT_SET_BIT_2 : return 2;
            case INT_SET_BIT_3 : return 3;

            case INT_SET_BIT_4 : return 4;
            case INT_SET_BIT_5 : return 5;
            case INT_SET_BIT_6 : return 6;
            case INT_SET_BIT_7 : return 7;

            case INT_SET_BIT_8 : return 8;
            case INT_SET_BIT_9: return 9;
            case INT_SET_BIT_10: return 10;
            case INT_SET_BIT_11: return 11;

            case INT_SET_BIT_12: return 12;
            case INT_SET_BIT_13: return 13;
            case INT_SET_BIT_14: return 14;
            case INT_SET_BIT_15: return 15;

            case INT_SET_BIT_16: return 16;
            case INT_SET_BIT_17: return 17;
            case INT_SET_BIT_18: return 18;
            case INT_SET_BIT_19: return 19;

            case INT_SET_BIT_20: return 20;
            case INT_SET_BIT_21: return 21;
            case INT_SET_BIT_22: return 22;
            case INT_SET_BIT_23: return 23;

            case INT_SET_BIT_24: return 24;
            case INT_SET_BIT_25: return 25;
            case INT_SET_BIT_26: return 26;
            case INT_SET_BIT_27: return 27;

            case INT_SET_BIT_28: return 28;
            case INT_SET_BIT_29: return 29;
            case INT_SET_BIT_30: return 30;
            case INT_SET_BIT_31: return 31;

            default:             return -1;
        }
    }
    public static int lsb(int x)
    {
        return x & -x;
    }
    public static int msb(int x)
    {
        x |= (x >> 1);
        x |= (x >> 2);
        x |= (x >> 4);
        x |= (x >> 8);
        x |= (x >> 16);

        return x & ~(x >>> 1);
    }
    public static int countOnes(int x)
    {
        x -= ((x >> 1) & 0x55555555);
        x  = ((x >> 2) & 0x33333333) + (x & 0x33333333);
        x  = ((x >> 4) + x) & 0x0f0f0f0f;
        x +=  (x >> 8);
        x +=  (x >> 16);

        return x & 0x0000003f;
    }
    public static int countZeroes(int x)
    {
        return BITS_PER_INT - countOnes(x);
    }
    public static int countTrailingZeroes(int x)
    {
        return countOnes((x & -x) - 1);
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static String toBinaryString(int var)
    {
        return StringHelper.breakString(StringHelper.leftPad(Integer.toBinaryString(var), BITS_PER_INT, '0'), 8, ' ');
    }
    public static String toBinaryString(long var)
    {
        return StringHelper.breakString(StringHelper.leftPad(Long.toBinaryString(var), BITS_PER_LONG, '0'), 8, ' ');
    }
//-------------------------------------------------------------------------------------------------------------------------------------
    public static int    packInt(int high, int low)       {return ((short) (high)) << 16 | ((short) low);}
    public static int    packInt(byte high, int low)      {return ((int) high << 24) | ((short) low);}
    public static short  unpackHighShort(long num)        {return ((short) (num >> 48));}
    public static short  unpackMiddleHighShort(long num)  {return ((short) (num >> 32));}
    public static short  unpackMiddleLowShort(long num)   {return ((short) (num >> 24));}
    public static short  unpackLowShort(long num)         {return ((short) (num >> 16));}
    public static int    unpackHighInt(long num)          {return ((int) (num >> 48));}
    public static int    unpackMiddleHighInt(long num)    {return ((int) (num >> 32));}
    public static int    unpackMiddleLowInt(long num)     {return ((int) (num >> 24));}
    public static int    unpackLowInt(long num)           {return ((int) (num >> 16));}
    public static byte   unpackHighByte(int num)          {return ((byte) (num >> 24));}
    public static byte   unpackMiddleHighByte(int num)    {return ((byte) (num >> 16));}
    public static byte   unpackMiddleLowByte(int num)     {return ((byte) (num >> 8));}
    public static byte   unpackLowByte(int num)           {return (byte) num;}
    public static short  unpackHighShort(int num)         {return ((short) (num >> 16));}
    public static int    unpackHighShortAsInt(int num)    {return (int) ((short) (num >> 16));}
    public static short  unpackMiddleShort(int num)       {return ((short) (num >> 8));}
    public static int    unpackMiddleShortAsInt(int num)  {return (int) ((short) (num >> 8));}
    public static short  unpackLowShort(int num)          {return ((short) num);}
    public static int    unpackLowShortAsInt(int num)     {return (int) ((short) num);}
//-------------------------------------------------------------------------------------------------------------------------------------
/*
    public static void main(String[] args)
    {
        int[] bit = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        for (int i = 0; i < bit.length; i++)
        {
            System.out.println("LSB[" + i + "] '" + bit[i] + "' is " + lsb(bit[i]));
            System.out.println("MSB[" + i + "] '" + bit[i] + "' is " + msb(bit[i]));
            System.out.println("CN1[" + i + "] '" + bit[i] + "' is " + countOnes(bit[i]));
            System.out.println("CN0[" + i + "] '" + bit[i] + "' is " + countZeroes(bit[i]));
            System.out.println("TR0[" + i + "] '" + bit[i] + "' is " + countTrailingZeroes(bit[i]));
        }

        int a = 10;
        int b = 2000;
        int c = 30000;
        int d = 400000;
        int p;
        byte bb = 1;

        p = BitHelper.packInt(b, c);

        System.out.println("packed=" + p + " b=" + BitHelper.unpackHighShort(p) + " c=" + BitHelper.unpackLowShort(p));

        p = BitHelper.packInt(bb, c);

        System.out.println("packed=" + p + " bb=" + BitHelper.unpackHighByte(p) + " c=" + BitHelper.unpackLowShort(p));
    }
*/
}
