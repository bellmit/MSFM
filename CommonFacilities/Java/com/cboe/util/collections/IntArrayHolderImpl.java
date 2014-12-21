package com.cboe.util.collections;

/**
 * IntArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedArrayHolderImpl.template (Int/int)
 *
 */

public class IntArrayHolderImpl implements IntArrayHolder
{
    public int[] keys;
    public int size;

    public static final int[] emptyIntArray = new int[0];

    public static final IntArrayHolder EmptyArrayHolder = new IntArrayHolderImpl()
    {
        public IntArrayHolder add(int key)                            {return this;}
        public IntArrayHolder add(int[] keys)                         {return this;}
        public IntArrayHolder add(int[] keys, int offset, int length) {return this;}
    };

    public IntArrayHolderImpl()
    {
        keys = emptyIntArray;
    }

    public IntArrayHolderImpl(int capacity)
    {
        keys = new int[capacity];
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

    public int[] keys()
    {
        return keys;
    }

    public int[] toArray()
    {
        return arrayclone(keys, size);
    }

    public IntArrayHolder add(int key)
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

    public IntArrayHolder add(int[] keys)
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

    public IntArrayHolder add(int[] keys, int offset, int length)
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

    public int getKey(int keyIndex)
    {
        if (keyIndex < size)
        {
            return keys[keyIndex];
        }

        return 0;
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

    public IntVisitor acceptVisitor(IntVisitor visitor)
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            visitor.visit(keys[keyIndex]);
        }

        return visitor;
    }

    public IntArrayHolder clear()
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            keys[keyIndex] = 0;
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
