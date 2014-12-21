package com.cboe.cfix.util;

/**
 * GrowableIntArray.java
 *
 * @author Dmitry Volpyansky
 *
 */

/**
 *
 * Fast insertion-only growable array of <i>ints</i> that is optimised for insertions only, trading off memory usage for higher insertion speed.
 *
 */

import com.cboe.client.util.*;

public class GrowableIntArray implements PackedIntArrayIF
{
    protected int[] array;
    protected int   length = 0;

    public final static int CONSTANT_RESIZE = 16;

    public GrowableIntArray()
    {
	    this(CONSTANT_RESIZE + CONSTANT_RESIZE);
    }
    public GrowableIntArray(int initialCapacity)
    {
	    array = new int[initialCapacity];
    }
    public void add(int i)
    {
	    if (array.length <= length)
	    {
		    int[] newArray = new int[array.length + CONSTANT_RESIZE];

		    System.arraycopy(array, 0, newArray, 0, array.length);

		    array = newArray;
	    }

	    array[length++] = i;
    }
    public int get(int index)
    {
	    if (length == 0)
	    {
		    throw new IllegalArgumentException(ClassHelper.getClassNameFinalPortion(this) + " GrowableIntArray.get() was called, but it was empty!");
	    }

	    if (index < 0)
	    {
			 return array[index + length];
	    }
	    else
	    {
			 return array[index];
	    }
    }
    public int[] getArrayClone()
    {
	    int[] newArray = new int[length];

	    System.arraycopy(newArray, 0, array, 0, length);

	    return newArray;
    }
    public int[] getArray()
    {
		return array;
    }
    public int length()
    {
	    return length;
    }
    public boolean isEmpty()
    {
        return length == 0;
    }
    public void clear()
    {
        length = 0;
    }
}
