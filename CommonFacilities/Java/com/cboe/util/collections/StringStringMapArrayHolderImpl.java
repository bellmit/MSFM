package com.cboe.util.collections;

/**
 * StringStringMapArrayHolderImpl.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED FROM KeyValueArrayHolderImpl.template (String/String, String/String)
 *
 */

public class StringStringMapArrayHolderImpl implements StringStringMapArrayHolder
{
    public String[] keys;
    public String[] values;
    public int       size;

    public static final String VALUE_INVALID_VALUE = null;
    public static final String KEY_INVALID_VALUE   = null;

    public static final StringStringMapArrayHolder EmptyArrayHolder = new StringStringMapArrayHolderImpl()
    {
        public StringStringMapArrayHolder add(String key, String value) {return this;}
    };

    public StringStringMapArrayHolderImpl()
    {
        this(0);
    }

    public StringStringMapArrayHolderImpl(int capacity)
    {
        keys   = new String[capacity];
        values = new String[capacity];
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

    public String[] keys()
    {
        return keys;
    }

    public String[] toKeyArray()
    {
        return arrayclone(keys, size);
    }

    public String[] values()
    {
        return values;
    }

    public String[] toValueArray()
    {
        return arrayclone(values, size);
    }

    public StringStringMapArrayHolder add(String key, String value)
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

    public StringStringMapArrayHolder add(String[] keys, String[] values, int count)
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

    public String getKey(int index)
    {
        if (index < size)
        {
            return keys[index];
        }

        return KEY_INVALID_VALUE;
    }

    public String getValue(int index)
    {
        if (index < size)
        {
            return values[index];
        }

        return VALUE_INVALID_VALUE;
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

    public boolean containsValue(String value)
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

    public StringStringMapVisitor acceptVisitor(StringStringMapVisitor visitor)
    {
        for (int i = 0; i < size; i++)
        {
            visitor.visit(keys[i], values[i]);
        }

        return visitor;
    }

    public StringStringMapArrayHolder clear()
    {
        for (int i = 0; i < size; i++)
        {
            keys[i]   = null;
            values[i] = null;
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
}
