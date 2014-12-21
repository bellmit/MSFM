package com.cboe.util.collections;

/**
 * StringArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM TypedArrayHolderImpl.template (String/String)
 *
 */

public class StringArrayHolderImpl implements StringArrayHolder
{
    public String[] keys;
    public int size;

    public static final String[] emptyStringArray = new String[0];

    public static final StringArrayHolder EmptyArrayHolder = new StringArrayHolderImpl()
    {
        public StringArrayHolder add(String key)                            {return this;}
        public StringArrayHolder add(String[] keys)                         {return this;}
        public StringArrayHolder add(String[] keys, int offset, int length) {return this;}
    };

    public StringArrayHolderImpl()
    {
        keys = emptyStringArray;
    }

    public StringArrayHolderImpl(int capacity)
    {
        keys = new String[capacity];
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

    public String[] keys()
    {
        return keys;
    }

    public String[] toArray()
    {
        return arrayclone(keys, size);
    }

    public StringArrayHolder add(String key)
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

    public StringArrayHolder add(String[] keys)
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

    public StringArrayHolder add(String[] keys, int offset, int length)
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

    public String getKey(int keyIndex)
    {
        if (keyIndex < size)
        {
            return keys[keyIndex];
        }

        return null;
    }

    public boolean containsKey(String key)
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            if (key != null && key.equals(keys[keyIndex]))
            {
                return true;
            }
        }

        return false;
    }

    public StringVisitor acceptVisitor(StringVisitor visitor)
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            visitor.visit(keys[keyIndex]);
        }

        return visitor;
    }

    public StringArrayHolder clear()
    {
        for (int keyIndex = 0; keyIndex < size; keyIndex++)
        {
            keys[keyIndex] = null;
        }

        size = 0;

        return this;
    }

        private String[] arrayclone(String from)
        {
            String[] to = new String[1];

            to[0] = from;

            return to;
        }

        private String[] arrayclone(String[] from)
        {
            if (from == null)
            {
                return null;
            }

            return (String[]) from.clone();
        }

        private String[] arrayclone(String[] from, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            System.arraycopy(from, 0, to, 0, from.length);
            return to;
        }

        private String[] arrayclone(String[] from, int fromOffset, int fromSize, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            System.arraycopy(from, fromOffset, to, 0, fromSize);
            return to;
        }

        private String[] arraycloneCombine(String[] from, int startOffset, int endOffset, int toSize)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
            int firstPortion = from.length - startOffset;
            System.arraycopy(from, startOffset, to, 0,            firstPortion);
            System.arraycopy(from, 0,           to, firstPortion, endOffset);
            return to;
        }

        private String[] arraycloneExpandGap(String[] from, int fromOffset, int fromSize, int toSize, int gapOffset, int gapLength)
        {
            if (from == null)
            {
                return null;
            }

            String[] to = new String[toSize];
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
