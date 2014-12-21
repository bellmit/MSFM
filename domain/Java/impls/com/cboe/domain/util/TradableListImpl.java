package com.cboe.domain.util;

import com.cboe.interfaces.domain.Tradable;
import com.cboe.interfaces.domain.TradableList;

import java.util.*;

/**
 * This class is designed to provide a type specifc array list implementation for tradable.
 * This implementation is directly borrowed from java.util.ArrayList.
 */
public class TradableListImpl implements TradableList
{

    private transient Tradable elementData[];
    private int size;

    /**
     * Constructs an empty list with the specified initial capacity.
     */
    public TradableListImpl(int initialCapacity) {
        super();
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        }
        this.elementData = new Tradable[initialCapacity];
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public TradableListImpl() {
        this(10);
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.  The TradableList instance has an initial capacity of
     * 110% the size of the specified collection.
     */
    public TradableListImpl(Collection c) {
        size = c.size();
        // Allow 10% room for growth
        elementData = new Tradable[(int)Math.min((size*110L)/100,Integer.MAX_VALUE)];
        c.toArray(elementData);
    }

    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the
     * list's current size.  An application can use this operation to minimize
     * the storage of an TradableList instance.
     */
    public void trimToSize() {
        int oldCapacity = elementData.length;
        if (size < oldCapacity) {
            Tradable oldData[] = elementData;
            elementData = new Tradable[size];
            System.arraycopy(oldData, 0, elementData, 0, size);
        }
    }

    /**
     * Increases the capacity of this TradableList instance, if
     * necessary, to ensure  that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     */
    public void ensureCapacity(int minCapacity) {
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            Tradable oldData[] = elementData;
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity < minCapacity) { newCapacity = minCapacity; }
            elementData = new Tradable[newCapacity];
            System.arraycopy(oldData, 0, elementData, 0, size);
        }
    }

    /**
     * Returns the number of elements in this list.
     */
    public int size() {
        return size;
    }

    /**
     * Tests if this list has no elements.
     *
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     */
    public boolean contains(Tradable elem) {
        return indexOf(elem) >= 0;
    }

    /**
     * Searches for the first occurence of the given argument, testing
     * for equality using the <tt>equals</tt> method.
     */
    public int indexOf(Tradable elem) {
        if (elem == null) {
            for (int i = 0; i < size; i++)
            if (elementData[i]==null)
                return i;
        } else {
            for (int i = 0; i < size; i++)
            if (elem.equals(elementData[i]))
                return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified tradable in
     * this list.
     *
     */
    public int lastIndexOf(Tradable elem) {
        if (elem == null) {
            for (int i = size-1; i >= 0; i--)
            if (elementData[i]==null)
                return i;
        } else {
            for (int i = size-1; i >= 0; i--)
            if (elem.equals(elementData[i]))
                return i;
        }
        return -1;
    }

    /**
     * Returns an array containing all of the elements in this list
     * in the correct order.
     */
    public Tradable[] toArray() {
        Tradable[] result = new Tradable[size];
        System.arraycopy(elementData, 0, result, 0, size);
        return result;
    }

    public Tradable get(int index) {
        RangeCheck(index);
        return elementData[index];
    }

    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     */
    public Tradable set(int index, Tradable element) {
        RangeCheck(index);
        Tradable oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    /**
     * Appends the specified element to the end of this list.
     */
    public boolean add(Tradable o) {
        ensureCapacity(size + 1);  // Increments modCount!!
        elementData[size++] = o;
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     */
    public void add(int index, Tradable element) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException( "Index: "+index+", Size: "+size);
        }
        ensureCapacity(size+1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size++;
    }

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     */
    public Tradable remove(int index) {
        RangeCheck(index);
        Tradable oldValue = elementData[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index+1, elementData, index, numMoved);
        }
        elementData[--size] = null; // Let gc do its work
        return oldValue;
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
    public void clear() {
        // Let gc do its work
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }

    /**
     * Appends all of the elements in the specified Collection to the end of
     * this list, in the order that they are returned by the
     * specified Collection's Iterator.  The behavior of this operation is
     * undefined if the specified Collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified Collection is this list, and this
     * list is nonempty.)
     */
    public boolean addAll(TradableList c) {
        Tradable[] a = c.toArray();
        int numNew = a.length;
        ensureCapacity(size + numNew);  // Increments modCount
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Inserts all of the elements in the specified Collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified Collection's iterator.
     */
    public boolean addAll(int index, TradableList c) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Tradable[] a = c.toArray();
        int numNew = a.length;
        ensureCapacity(size + numNew);  // Increments modCount
        int numMoved = size - index;
        if (numMoved > 0) {
            System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
        }
        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Removes from this List all of the elements whose index is between
     * fromIndex, inclusive and toIndex, exclusive.  Shifts any succeeding
     * elements to the left (reduces their index).
     * This call shortens the list by <tt>(toIndex - fromIndex)</tt> elements.
     * (If <tt>toIndex==fromIndex</tt>, this operation has no effect.)
     *
     * @param fromIndex index of first element to be removed.
     * @param toIndex index after last element to be removed.
     */
    public void removeRange(int fromIndex, int toIndex) {
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);
        // Let gc do its work
        int newSize = size - (toIndex-fromIndex);
        while (size != newSize)
            elementData[--size] = null;
    }

    /**
     * Check if the given index is in range.  If not, throw an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    private void RangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException( "Index: "+index+", Size: "+size);
    }
}
