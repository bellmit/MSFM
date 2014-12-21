package com.cboe.client.util;

/**
 * BitArray.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Used to pack boolean values together. Faster than Java's BitSet. Does NOT keep count of how many times each item was changed
 * Bit numbers start at 0.
 */

public class BitArray implements BitArrayIF
{
    public long[] bits;
    public int    bitCount;

    protected static final int MOD_BPL   = BitHelper.BITS_PER_LONG - 1;
    protected static final int SHIFT_BPL = 6; // 64 = 1^6

    public BitArray()
    {
        this(0);
    }

    public BitArray(int bitCount)
    {
        this.bitCount = bitCount;

        if (bitCount == 0)
        {
            bits = CollectionHelper.EMPTY_long_ARRAY;
        }
        else
        {
            int neededArrayLength = (bitCount >> SHIFT_BPL) + ((bitCount & MOD_BPL) == 0 ? 0 : 1);

            bits = new long[neededArrayLength > 0 ? neededArrayLength : 1];
        }
    }

    public BitArray(BitArray bitArray)
    {
        bitCount = bitArray.bitCount;
        bits     = CollectionHelper.arrayclone(bitArray.bits);
    }

    public void copyFrom(BitArrayIF bitArray)
    {
        int othersize = bitArray.size();

        if (othersize == 0)
        {
            clear();
            return;
        }

        int neededArrayLength = (othersize >> SHIFT_BPL) + ((othersize & MOD_BPL) == 0 ? 0 : 1);

        bitCount = othersize;

        if (neededArrayLength > bits.length)
        {
            bits = new long[neededArrayLength];
        }

        if (bitArray instanceof BitArray)
        {
            System.arraycopy(((BitArray) bitArray).bits, 0, bits, 0, neededArrayLength);
        }
        else
        {
            for (int i = othersize; --i >= 0; )
            {
                if (bitArray.isSet(i))
                {
                    set(i);
                }
            }
        }
    }

    public void clear(int bitIndex)
    {
        // Calculation for 0-based index is different than calculation
        // for count (such as in the constructor method).
        int neededArrayLength = (bitIndex >> SHIFT_BPL) + 1;

        if (neededArrayLength > bits.length)
        {
            resize(bitIndex);

            bits[bitIndex >> SHIFT_BPL] &= ~(1L << (bitIndex & MOD_BPL));
        }
        else
        {
            bits[bitIndex >> SHIFT_BPL] &= ~(1L << (bitIndex & MOD_BPL));

            if (bitCount <= bitIndex)
            {
                bitCount = bitIndex + 1;
            }
        }
    }

    public void set(int bitIndex)
    {
        int neededArrayLength = (bitIndex >> SHIFT_BPL) + 1;

        if (neededArrayLength > bits.length)
        {
            resize(bitIndex);

            bits[bitIndex >> SHIFT_BPL] |= (1L << (bitIndex & MOD_BPL));
        }
        else
        {
            bits[bitIndex >> SHIFT_BPL] |= (1L << (bitIndex & MOD_BPL));

            if (bitCount <= bitIndex)
            {
                bitCount = bitIndex + 1;
            }
        }
    }

    public boolean isSet(int bitIndex)
    {
        if (bitIndex >= bitCount)
        {
            return false;
        }
        return 0 != (bits[bitIndex >> SHIFT_BPL] & (1L << (bitIndex & MOD_BPL)));
    }

    public boolean isClear(int bitIndex)
    {
        if (bitIndex >= bitCount)
        {
            return false;
        }

        return 0 == (bits[bitIndex >> SHIFT_BPL] & (1L << (bitIndex & MOD_BPL)));
    }

    public int timesChanged(int bitIndex)
    {
        if (bitIndex >= bitCount)
        {
            return 0;
        }

        return (0 != (bits[bitIndex >> SHIFT_BPL] & (1L << (bitIndex & MOD_BPL)))) ? 1 : 0;
    }

    public void clear()
    {
        for (int i = bits.length; --i >= 0; )
        {
            bits[i] = 0L;
        }

        bitCount = 0;
    }

    public int capacity()
    {
        return bits.length * BitHelper.BITS_PER_LONG;
    }

    public int size()
    {
        return bitCount;
    }

    public long[] toArray()
    {
        return (long[]) bits.clone();
    }

    protected void resize(int bitIndex)
    {
        int arrayIndex = bitIndex >> SHIFT_BPL;

        if (arrayIndex >= bits.length)
        {
            bits = CollectionHelper.arrayclone(bits, arrayIndex + 1);
        }

        bitCount = bitIndex + 1;
    }

    public String toString()
    {
        if (bitCount == 0)
        {
            return "";
        }

        StringBuilder buffer = new StringBuilder(1024);

        for (int i = 0; i < bits.length; i++)
        {
            buffer.append("[");
            buffer.append(BitHelper.toBinaryString(bits[i]));
            buffer.append("]");

            if (i + 1 < bits.length)
            {
                buffer.append(" ");
            }
        }

        return buffer.toString();
    }

    public Object clone()
    {
        return new BitArray(this);
    }
}
