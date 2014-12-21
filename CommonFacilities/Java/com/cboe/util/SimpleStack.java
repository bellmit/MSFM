/**
 * 
 */
package com.cboe.util;

public final class SimpleStack<T>
{
    final Object[] array;
    int size;
    
    public SimpleStack(final int p_len)
    {
        array = new Object[p_len];
    }
    
    @SuppressWarnings("unchecked")
    public T pop()
    {
        if (size == 0)
            return null;
        final Object item = array[--size];
        array[size] = null; // clean up the reference
        return (T)item;
    }

    public boolean push(final T p_item)
    {
        if (size != array.length)
        {
            array[size++] = p_item;
            return true;
        }
        return false;
    }
    
    public int size()
    {
        return size;
    }

}