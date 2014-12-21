package com.cboe.util.collections;

/**
 * LongObjectMapArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolderImpl.template (Long/long, Object/Object)
 *
 */

public class LongObjectMapArrayHolderImpl implements LongObjectMapArrayHolder
{
    public long[] keys;
    public Object[] values;
    public int       size;

    public static final Object VALUE_INVALID_VALUE = null;
    public static final long KEY_INVALID_VALUE   = Long.MIN_VALUE;

    public static final LongObjectMapArrayHolder EmptyArrayHolder = new LongObjectMapArrayHolderImpl()
    {
        public LongObjectMapArrayHolder add(long key, Object value) {return this;}
    };

    public LongObjectMapArrayHolderImpl()
    {
        this(0);
    }

    public LongObjectMapArrayHolderImpl(int capacity)
    {
        keys   = new long[capacity];
        values = new Object[capacity];
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

    public long[] keys()
    {
        return keys;
    }

    public long[] toKeyArray()
    {
        return arrayclone(keys, size);
    }

    public Object[] values()
    {
        return values;
    }

    public Object[] toValueArray()
    {
        return arrayclone(values, size);
    }

    public LongObjectMapArrayHolder add(long key, Object value)
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

    public LongObjectMapArrayHolder add(long[] keys, Object[] values, int count)
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

    public long getKey(int index)
    {
        if (index < size)
        {
            return keys[index];
        }

        return KEY_INVALID_VALUE;
    }

    public Object getValue(int index)
    {
        if (index < size)
        {
            return values[index];
        }

        return VALUE_INVALID_VALUE;
    }

    public boolean containsKey(long key)
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

    public boolean containsValue(Object value)
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            if (value != null && value.equals(values[keyIndex]))
            {
                return true;
            }
        }

        return false;
    }

    public LongObjectMapVisitor acceptVisitor(LongObjectMapVisitor visitor)
    {
        for (int i = 0; i < size; i++)
        {
            visitor.visit(keys[i], values[i]);
        }

        return visitor;
    }

    public LongObjectMapArrayHolder clear()
    {
        for (int i = 0; i < size; i++)
        {
            keys[i]   = 0;
            values[i] = null;
        }

        size = 0;

        return this;
    }

        private long[] arrayclone(long from)
        {
            long[] to = new long[1];

            to[0] = from;

            return to;
        }

        private long[] arrayclone(long[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (long[]) from.clone();
        }

        private long[] arrayclone(long[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            long[] to = new long[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private long[] arrayclone(long[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            long[] to = new long[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private long[] arraycloneCombine(long[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            long[] to = new long[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private long[] arraycloneExpandGap(long[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            long[] to = new long[toSize];
            int gap = gapOffset + gapLength;
            System.arraycopy(from, fromOffset, to, 0,   gapOffset);
            System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
            return to;
        }

        private Object[] arrayclone(Object from)
        {
            Object[] to = new Object[1];

            to[0] = from;

            return to;
        }

        private Object[] arrayclone(Object[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (Object[]) from.clone();
        }

        private Object[] arrayclone(Object[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private Object[] arrayclone(Object[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private Object[] arraycloneCombine(Object[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private Object[] arraycloneExpandGap(Object[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            Object[] to = new Object[toSize];
            int gap = gapOffset + gapLength;
            System.arraycopy(from, fromOffset, to, 0,   gapOffset);
            System.arraycopy(from, gapOffset,  to, gap, fromSize - gapOffset);
            return to;
        }
}
