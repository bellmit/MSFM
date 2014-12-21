package com.cboe.util.collections;

/**
 * LongArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedArrayHolderImpl.template (Long/long)
 *
 */

public class LongArrayHolderImpl implements LongArrayHolder
{
    public long[] keys;
    public int size;

    public static final long[] emptyLongArray = new long[0];

    public static final LongArrayHolder EmptyArrayHolder = new LongArrayHolderImpl()
    {
        public LongArrayHolder add(long key)                            {return this;}
        public LongArrayHolder add(long[] keys)                         {return this;}
        public LongArrayHolder add(long[] keys, int offset, int length) {return this;}
    };

    public LongArrayHolderImpl()
    {
        keys = emptyLongArray;
    }

    public LongArrayHolderImpl(int capacity)
    {
        keys = new long[capacity];
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
            keys = arrayclone(keys, 0, keys.length, capacity);
        }

        return keys.length;
    }

    public long[] keys()
    {
        return keys;
    }

    public long[] toArray()
    {
        return arrayclone(keys, size);
    }

    public LongArrayHolder add(long key)
    {
        int newSize = size + 1;

        if (newSize >= keys.length)
        {
            keys = arrayclone(keys, 0, keys.length, newSize);
        }

        keys[size] = key;

        size = newSize;
        
        return this;
    }

    public LongArrayHolder add(long[] keys)
    {
        int newSize = size + keys.length;

        if (newSize >= this.keys.length)
        {
            this.keys = arrayclone(this.keys, 0, this.keys.length, newSize);
        }

        System.arraycopy(keys, 0, this.keys, size, keys.length);

        size = newSize;
        
        return this;
    }

    public LongArrayHolder add(long[] keys, int offset, int length)
    {
        int newSize = size + length;

        if (newSize >= this.keys.length)
        {
            this.keys = arrayclone(this.keys, 0, this.keys.length, newSize);
        }

        System.arraycopy(keys, offset, this.keys, size, length);

        size = newSize;
        
        return this;
    }

    public long getKey(int keyIndex)
    {
        if (keyIndex < size)
        {
            return keys[keyIndex];
        }

        return 0;
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

    public LongVisitor acceptVisitor(LongVisitor visitor)
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            visitor.visit(keys[keyIndex]);
        }

        return visitor;
    }

    public LongArrayHolder clear()
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            keys[keyIndex] = 0;
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
