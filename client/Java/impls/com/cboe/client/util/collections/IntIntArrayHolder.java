package com.cboe.client.util.collections;

/**
 * IntIntArrayHolder.java
 *
 * @author Dmitry Volpyansky
 *
 * FILE GENERATED BY VELOCITY TEMPLATE ENGINE FROM /vobs/dte/client/generator/DV_XYArrayHolder.java (KEY_TYPE=int, VALUE_TYPE=int)
 *
 */

import com.cboe.client.util.*;

public class IntIntArrayHolder implements IntIntArrayHolderIF
{
    public int[]   keys;
    public int[] values;
    public int      size;

    public static final IntIntArrayHolderIF EmptyArrayHolder = new IntIntArrayHolder()
    {
        public void add(int key, int value) {}
    };

    public IntIntArrayHolder()
    {
        this(0);
    }

    public IntIntArrayHolder(int capacity)
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
            keys   = CollectionHelper.arrayclone(keys,   0, keys.length,   capacity);
            values = CollectionHelper.arrayclone(values, 0, values.length, capacity);
        }

        return keys.length;
    }

    public int[] keys()
    {
        return keys;
    }

    public int[] getKeys()
    {
        return CollectionHelper.arrayclone(keys, size);
    }

    public int[] values()
    {
        return values;
    }

    public int[] getValues()
    {
        return CollectionHelper.arrayclone(values, size);
    }

    public void add(int key, int value)
    {
        int newSize = size + 1;

        if (newSize >= keys.length)
        {
            keys   = CollectionHelper.arrayclone(keys,   0, keys.length,   newSize);
            values = CollectionHelper.arrayclone(values, 0, values.length, newSize);
        }

        keys[size]   = key;
        values[size] = value;

        size = newSize;
    }

    public int getKey(int index)
    {
        if (index < size)
        {
            return keys[index];
        }

        return IntegerHelper.INVALID_VALUE;
    }

    public int getValue(int index)
    {
        if (index < size)
        {
            return values[index];
        }

        return IntegerHelper.INVALID_VALUE;
    }

    public boolean containsKey(int key)
    {
        for (int i = 0; i < size; i++)
        {
            if (keys[i] == key)
            {
                return true;
            }
        }

        return false;
    }

    public boolean containsValue(int value)
    {
        for (int i = 0; i < size; i++)
        {
            if (values[i] == value)
            {
                return true;
            }
        }

        return false;
    }

    public IntIntVisitorIF acceptVisitor(IntIntVisitorIF visitor)
    {
        for (int i = 0; i < size; i++)
        {
            visitor.visit(keys[i], values[i]);
        }

        return visitor;
    }

    public IntIntArrayHolderIF clear()
    {
        for (int i = 0; i < size; i++)
        {
            keys[i]   = 0;
            values[i] = 0;
        }

        size = 0;

        return this;
    }
}
