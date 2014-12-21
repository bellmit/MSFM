package com.cboe.util.collections;

/**
 * IntIntMapArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolderImpl.template (Int/int, Int/int)
 *
 */

public class IntIntMapArrayHolderImpl implements IntIntMapArrayHolder
{
    public int[] keys;
    public int[] values;
    public int       size;

    public static final int VALUE_INVALID_VALUE = Integer.MIN_VALUE;
    public static final int KEY_INVALID_VALUE   = Integer.MIN_VALUE;

    public static final IntIntMapArrayHolder EmptyArrayHolder = new IntIntMapArrayHolderImpl()
    {
        public IntIntMapArrayHolder add(int key, int value) {return this;}
    };

    public IntIntMapArrayHolderImpl()
    {
        this(0);
    }

    public IntIntMapArrayHolderImpl(int capacity)
    {
        keys   = new int[capacity];
        values = new int[capacity];
    }

    public int size()
    {
        return size;
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public int capacity()
    {
        return keys.length;
    }

    public int ensureCapacity(int capacity)
    {
        if (keys.length < capacity)
        {
            keys   = arrayclone(keys,   0, keys.length,   capacity);
            values = arrayclone(values, 0, values.length, capacity);
        }

        return keys.length;
    }

    public int[] keys()
    {
        return keys;
    }

    public int[] toKeyArray()
    {
        return arrayclone(keys, size);
    }

    public int[] values()
    {
        return values;
    }

    public int[] toValueArray()
    {
        return arrayclone(values, size);
    }

    public IntIntMapArrayHolder add(int key, int value)
    {
        int newSize = size + 1;

        if (newSize >= keys.length)
        {
            keys   = arrayclone(keys,   0, keys.length,   newSize);
            values = arrayclone(values, 0, values.length, newSize);
        }

        keys[size]   = key;
        values[size] = value;

        size = newSize;
        
        return this;
    }

    public IntIntMapArrayHolder add(int[] keys, int[] values, int count)
    {
        int newSize = size + count;

        if (newSize >= this.keys.length)
        {
            this.keys   = arraycloneExpandGap(this.keys,   0, size, newSize, size, 0);
            this.values = arraycloneExpandGap(this.values, 0, size, newSize, size, 0);
        }

        System.arraycopy(keys,   size, this.keys,   size, count);
        System.arraycopy(values, size, this.values, size, count);

        size = newSize;
        
        return this;
    }

    public int getKey(int index)
    {
        if (index < size)
        {
            return keys[index];
        }

        return KEY_INVALID_VALUE;
    }

    public int getValue(int index)
    {
        if (index < size)
        {
            return values[index];
        }

        return VALUE_INVALID_VALUE;
    }

    public boolean containsKey(int key)
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            if (key == keys[keyIndex])
            {
                return true;
            }
        }

        return false;
    }

    public boolean containsValue(int value)
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            if (value == values[keyIndex])
            {
                return true;
            }
        }

        return false;
    }

    public IntIntMapVisitor acceptVisitor(IntIntMapVisitor visitor)
    {
        for (int i = 0; i < size; i++)
        {
            visitor.visit(keys[i], values[i]);
        }

        return visitor;
    }

    public IntIntMapArrayHolder clear()
    {
        for (int i = 0; i < size; i++)
        {
            keys[i]   = 0;
            values[i] = 0;
        }

        size = 0;

        return this;
    }

        private int[] arrayclone(int from)
        {
            int[] to = new int[1];

            to[0] = from;

            return to;
        }

        private int[] arrayclone(int[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (int[]) from.clone();
        }

        private int[] arrayclone(int[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            int[] to = new int[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private int[] arrayclone(int[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            int[] to = new int[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private int[] arraycloneCombine(int[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            int[] to = new int[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private int[] arraycloneExpandGap(int[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            int[] to = new int[toSize];
            int gap = gapOffset + gapLength;
            System.arraycopy(from, fromOffset, to, 0,   gapOffset);
            System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
            return to;
        }
}
