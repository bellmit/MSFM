package com.cboe.client.util;

/**
 * CountingBitArray.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 * Used to pack boolean values together. Keeps count of how many times each item was changed
 *
 */

public class CountingBitArray implements BitArrayIF
{
    public int[] bits;
    public int   bitCount;

    public CountingBitArray()
    {
        this(0);
    }

    public CountingBitArray(int bitCount)
    {
        this.bitCount = bitCount;

        if (bitCount == 0)
        {
            bits = CollectionHelper.EMPTY_int_ARRAY;
        }
        else
        {
            bits = new int[bitCount];
        }
    }

    public CountingBitArray(CountingBitArray changeDetectingBitArray)
    {
        bitCount = changeDetectingBitArray.bitCount;
        bits     = CollectionHelper.arrayclone(changeDetectingBitArray.bits);
    }

    public void copyFrom(BitArrayIF bitArray)
    {
        int neededArrayLength = bitArray.size();

        if (neededArrayLength == 0)
        {
            clear();
            return;
        }

        bitCount = neededArrayLength;

        if (neededArrayLength > bits.length)
        {
            bits = new int[neededArrayLength];
        }

        if (bitArray instanceof CountingBitArray)
        {
            System.arraycopy(((CountingBitArray) bitArray).bits, 0, bits, 0, neededArrayLength);
        }
        else
        {
            for (int i = neededArrayLength; --i >= 0; )
            {
                clear(i);
                if (bitArray.isSet(i))
                {
                    set(i);
                }
            }
        }
    }

    public void clear(int bitIndex)
    {
        if (bitIndex >= bits.length)
        {
            resize(bitIndex);
        }

        bits[bitIndex] = 0;

        if (bitCount <= bitIndex)
        {
            bitCount = bitIndex + 1;
        }
    }

    public void set(int bitIndex)
    {
        if (bitIndex >= bits.length)
        {
            resize(bitIndex);
        }

        bits[bitIndex]++;

        if (bitCount <= bitIndex)
        {
            bitCount = bitIndex + 1;
        }
    }

    public boolean isSet(int bitIndex)
    {
        if (bitIndex >= bitCount)
        {
            return false;
        }

        return bits[bitIndex] > 0;
    }

    public boolean isClear(int bitIndex)
    {
        if (bitIndex >= bitCount)
        {
            return false;
        }

        return bits[bitIndex] == 0;
    }

    public int timesChanged(int bitIndex)
    {
        if (bitIndex >= bitCount)
        {
            return 0;
        }

        return bits[bitIndex];
    }

    public void clear()
    {
        for (int i = bits.length; --i >= 0; )
        {
            bits[i] = 0;
        }

        bitCount = 0;
    }

    public int capacity()
    {
        return bits.length;
    }

    public int size()
    {
        return bitCount;
    }

    protected void resize(int bitIndex)
    {
        if (bitIndex >= bits.length)
        {
            bits = CollectionHelper.arrayclone(bits, bitIndex + 1);
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
        return new CountingBitArray(this);
    }
}
