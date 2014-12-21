/*
 * @(#)Hashtable.java	1.70 98/09/30
 *
 * Copyright 1994-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.cboe.util;
import java.util.*;
import java.io.*;

/*
   Modified by CGM on 01/17/2000 to use an integer for the hash key
   instead of an object.
*/

/** 
 * This class implements a hashtable, which maps keys to values. The
 * key is an integer and any object can be used as a value.
 *
 * An instance of <code>Hashtable</code> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of <i>buckets</i> in the hash table, and the
 * <i>initial capacity</i> is simply the capacity at the time the hash table
 * is created.  Note that the hash table is <i>open</i>: in the case a "hash
 * collision", a single bucket stores multiple entries, which must be searched
 * sequentially.  The <i>load factor</i> is a measure of how full the hash
 * table is allowed to get before its capacity is automatically increased.
 * When the number of entries in the hashtable exceeds the product of the load
 * factor and the current capacity, the capacity is increased by calling the
 * <code>rehash</code> method.<p>
 *
 * Generally, the default load factor (.75) offers a good tradeoff between
 * time and space costs.  Higher values decrease the space overhead but
 * increase the time cost to look up an entry (which is reflected in most
 * <tt>Hashtable</tt> operations, including <tt>get</tt> and <tt>put</tt>).<p>
 *
 * The initial capacity controls a tradeoff between wasted space and the
 * need for <code>rehash</code> operations, which are time-consuming.
 * No <code>rehash</code> operations will <i>ever</i> occur if the initial
 * capacity is greater than the maximum number of entries the
 * <tt>Hashtable</tt> will contain divided by its load factor.  However,
 * setting the initial capacity too high can waste space.<p>
 *
 * If many entries are to be made into a <code>Hashtable</code>, 
 * creating it with a sufficiently large capacity may allow the 
 * entries to be inserted more efficiently than letting it perform 
 * automatic rehashing as needed to grow the table. <p>
 *
 * This example creates a hashtable of numbers. It uses the names of 
 * the numbers as keys:
 * <p><blockquote><pre>
 *     IntHashtable numbers = new IntHashtable();
 *     numbers.put(1, "one");
 *     numbers.put(2, "two");
 *     numbers.put(3, "three");
 * </pre></blockquote>
 * <p>
 * To retrieve a number, use the following code: 
 * <p><blockquote><pre>
 *     String n = (String)numbers.get(2);
 *     if (n != null) {
 *         System.out.println( "2 = " + n );
 *     }
 * </pre></blockquote>
 * <p>
*
 * The Enumerations returned by IntHashtable's keys and values methods are
 * <em>not</em> fail-fast.
 *
 * @author  Arthur van Hoff
 * @author  Josh Bloch
 * @version 1.70, 09/30/98
 * @see     Object#equals(java.lang.Object)
 * @see     IntHashtable#rehash()
 * @see     Collection
 * @see	    Map
 * @see	    HashMap
 * @see	    TreeMap
 * @since JDK1.0
 */
public class IntHashtable implements Cloneable, Serializable {
    /**
     * The hash table data.
     */
    private transient Entry table[];

    /**
     * The total number of entries in the hash table.
     */
    private transient int count;

    /**
     * The table is rehashed when its size exceeds this threshold.  (The
     * value of this field is (int)(capacity * loadFactor).)
     *
     * @serial
     */
    private int threshold;

    /**
     * The load factor for the hashtable.
     *
     * @serial
     */
    private float loadFactor;

    /**
     * The number of times this IntHashtable has been structurally modified
     * Structural modifications are those that change the number of entries in
     * the IntHashtable or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the IntHashtable fail-fast.  (See ConcurrentModificationException).
     */
    private transient int modCount = 0;

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    // private static final long serialVersionUID = 1421746759512286392L;

    /**
     * Constructs a new, empty hashtable with the specified initial 
     * capacity, the specified load factor, and the given hash func .
     *
     * @param      initialCapacity   the initial capacity of the hashtable.
     * @param      loadFactor        the load factor of the hashtable.
     * @exception  IllegalArgumentException  if the initial capacity is less
     *             than zero, or if the load factor is nonpositive.
     */
    public IntHashtable(int initialCapacity, float loadFactor) {
	if (initialCapacity < 0)
	    throw new IllegalArgumentException("Illegal Capacity: "+
                                               initialCapacity);
        if (loadFactor <= 0)
            throw new IllegalArgumentException("Illegal Load: "+loadFactor);

        if (initialCapacity==0)
            initialCapacity = 1;
	this.loadFactor = loadFactor;
	table = new Entry[initialCapacity];
	threshold = (int)(initialCapacity * loadFactor);
    }

    /**
     * Constructs a new, empty hashtable with the specified initial capacity
     * and default load factor, which is <tt>0.75</tt>.
     *
     * @param     initialCapacity   the initial capacity of the hashtable.
     * @exception IllegalArgumentException if the initial capacity is less
     *              than zero.
     */
    public IntHashtable(int initialCapacity) {
	this(initialCapacity, 0.75f );
    }

    /**
     * Constructs a new, empty hashtable with a default capacity and load
     * factor, which is <tt>0.75</tt>. 
     */
    public IntHashtable() {
	this(101, 0.75f );
    }


    /**
     * Returns the number of keys in this hashtable.
     *
     * @return  the number of keys in this hashtable.
     */
    public int size() {
	return count;
    }

    /**
     * Tests if this hashtable maps no keys to values.
     *
     * @return  <code>true</code> if this hashtable maps no keys to values;
     *          <code>false</code> otherwise.
     */
    public boolean isEmpty() {
	return count == 0;
    }

    /**
     * Returns an enumeration of the keys in this hashtable.
     *
     * @return  an enumeration of the keys in this hashtable.
     * @see     Enumeration
     * @see     #elements()
     * @see	#keySet()
     * @see	Map
     */
    public synchronized Enumeration keys() {
	return new Enumerator(KEYS, false);
    }

    /**
     * Returns an enumeration of the values in this hashtable.
     * Use the Enumeration methods on the returned object to fetch the elements
     * sequentially.
     *
     * @return  an enumeration of the values in this hashtable.
     * @see     java.util.Enumeration
     * @see     #keys()
     * @see	#values()
     * @see	Map
     */
    public synchronized Enumeration elements() {
	return new Enumerator(VALUES, false);
    }

    /**
     * Tests if some key maps into the specified value in this hashtable.
     * This operation is more expensive than the <code>containsKey</code>
     * method.<p>
     *
     * Note that this method is identical in functionality to containsValue,
     * (which is part of the Map interface in the collections framework).
     * 
     * @param      value   a value to search for.
     * @return     <code>true</code> if and only if some key maps to the
     *             <code>value</code> argument in this hashtable as 
     *             determined by the <tt>equals</tt> method;
     *             <code>false</code> otherwise.
     * @exception  NullPointerException  if the value is <code>null</code>.
     * @see        #containsKey(Object)
     * @see        #containsValue(Object)
     * @see	   Map
     */
    public synchronized boolean contains(Object value) {
	if (value == null) {
	    throw new NullPointerException();
	}

	Entry tab[] = table;
	for (int i = tab.length ; i-- > 0 ;) {
	    for (Entry e = tab[i] ; e != null ; e = e.next) {
		if (e.value.equals(value)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * Returns true if this IntHashtable maps one or more keys to this value.<p>
     *
     * Note that this method is identical in functionality to contains
     * (which predates the Map interface).
     *
     * @param value value whose presence in this IntHashtable is to be tested.
     * @see	   Map
     * @since JDK1.2
     */
    public boolean containsValue(Object value) {
	return contains(value);
    }

    /**
     * Tests if the specified object is a key in this hashtable.
     * 
     * @param   key   possible key.
     * @return  <code>true</code> if and only if the specified object 
     *          is a key in this hashtable, as determined by the 
     *          <tt>equals</tt> method; <code>false</code> otherwise.
     * @see     #contains(Object)
     */
    public synchronized boolean containsKey(final int key) {
	Entry tab[] = table;
	int index = (key & 0x7FFFFFFF) % tab.length;
	for (Entry e = tab[index] ; e != null ; e = e.next) {
	    if (key == e.key ) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Returns the value to which the specified key is mapped in this hashtable.
     *
     * @param   key   a key in the hashtable.
     * @return  the value to which the key is mapped in this hashtable;
     *          <code>null</code> if the key is not mapped to any value in
     *          this hashtable.
     * @see     #put(Object, Object)
     */
    public synchronized Object get(final int key) {
	Entry tab[] = table;
	int index = (key & 0x7FFFFFFF) % tab.length;
	for (Entry e = tab[index] ; e != null ; e = e.next) {
	    if ( key == e.key ) {
		return e.value;
	    }
	}
	return null;
    }

    /**
     * Increases the capacity of and internally reorganizes this 
     * hashtable, in order to accommodate and access its entries more 
     * efficiently.  This method is called automatically when the 
     * number of keys in the hashtable exceeds this hashtable's capacity 
     * and load factor. 
     */
    protected void rehash() {
	int oldCapacity = table.length;
	Entry oldMap[] = table;

	int newCapacity = oldCapacity * 2 + 1;
	Entry newMap[] = new Entry[newCapacity];

	modCount++;
	threshold = (int)(newCapacity * loadFactor);
	table = newMap;

	for (int i = oldCapacity ; i-- > 0 ;) {
	    for (Entry old = oldMap[i] ; old != null ; ) {
		Entry e = old;
		old = old.next;

		int index = (e.key & 0x7FFFFFFF) % newCapacity;
		e.next = newMap[index];
		newMap[index] = e;
	    }
	}
    }

    /**
     * Maps the specified <code>key</code> to the specified 
     * <code>value</code> in this hashtable. Neither the key nor the 
     * value can be <code>null</code>. <p>
     *
     * The value can be retrieved by calling the <code>get</code> method 
     * with a key that is equal to the original key. 
     *
     * @param      key     the hashtable key.
     * @param      value   the value.
     * @return     the previous value of the specified key in this hashtable,
     *             or <code>null</code> if it did not have one.
     * @exception  NullPointerException  if the key or value is
     *               <code>null</code>.
     * @see     Object#equals(Object)
     * @see     #get(Object)
     */
    public synchronized Object put(final int key, final Object value) {
	// Make sure the value is not null
	if (value == null) {
	    throw new NullPointerException();
	}

	// Makes sure the key is not already in the hashtable.
	Entry tab[] = table;
	int index = (key & 0x7FFFFFFF) % tab.length;
	for (Entry e = tab[index] ; e != null ; e = e.next) {
	    if ( key == e.key ) {
		Object old = e.value;
		e.value = value;
		return old;
	    }
	}

	modCount++;
	if (count >= threshold) {
	    // Rehash the table if the threshold is exceeded
	    rehash();

            tab = table;
            index = (key & 0x7FFFFFFF) % tab.length;
	} 

	// Creates the new entry.
	Entry e = new Entry(key, value, tab[index]);
	tab[index] = e;
	count++;
	return null;
    }

    /**
     * Removes the key (and its corresponding value) from this 
     * hashtable. This method does nothing if the key is not in the hashtable.
     *
     * @param   key   the key that needs to be removed.
     * @return  the value to which the key had been mapped in this hashtable,
     *          or <code>null</code> if the key did not have a mapping.
     */
    public synchronized Object remove(final int key) {
	Entry tab[] = table;
	int index = (key & 0x7FFFFFFF) % tab.length;
	for (Entry e = tab[index], prev = null ; e != null ; prev = e, e = e.next) {
	    if ( key == e.key ) {
		modCount++;
		if (prev != null) {
		    prev.next = e.next;
		} else {
		    tab[index] = e.next;
		}
		count--;
		Object oldValue = e.value;
		e.value = null;
		return oldValue;
	    }
	}
	return null;
    }


    /**
     * Clears this hashtable so that it contains no keys. 
     */
    public synchronized void clear() {
	Entry tab[] = table;
	modCount++;
	for (int index = tab.length; --index >= 0; )
	    tab[index] = null;
	count = 0;
    }

    /**
     * Creates a shallow copy of this hashtable. All the structure of the 
     * hashtable itself is copied, but the keys and values are not cloned. 
     * This is a relatively expensive operation.
     *
     * @return  a clone of the hashtable.
     */
    public synchronized Object clone() {
	try { 
	    IntHashtable t = (IntHashtable)super.clone();
	    t.table = new Entry[table.length];
	    for (int i = table.length ; i-- > 0 ; ) {
		t.table[i] = (table[i] != null) 
		    ? (Entry)table[i].clone() : null;
	    }
	    t.modCount = 0;
	    return t;
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    /**
     * Returns a string representation of this <tt>Hashtable</tt> object 
     * in the form of a set of entries, enclosed in braces and separated 
     * by the ASCII characters "<tt>,&nbsp;</tt>" (comma and space). Each 
     * entry is rendered as the key, an equals sign <tt>=</tt>, and the 
     * associated element, where the <tt>toString</tt> method is used to 
     * convert the key and element to strings. <p>Overrides to 
     * <tt>toString</tt> method of <tt>Object</tt>.
     *
     * @return  a string representation of this hashtable.
     */
    public synchronized String toString() {
	int max = size();
	StringBuilder buf = new StringBuilder(500);
	Entry tab[] = table;
    int outCount = 0;

	buf.append("{");
	for (int i = 0; i < tab.length; i++) {
	    for (Entry e = tab[i] ; e != null ; e = e.next) {
            buf.append(e.key).append("=").append(e.value);
            if (++outCount < max)
            {
                buf.append(", ");
            }
        }
	}
	buf.append("}");
	return buf.toString();
    }


    /**
     * Save the state of the IntHashtable to a stream (i.e., serialize it).
     *
     * @serialData The <i>capacity</i> of the IntHashtable (the length of the
     *		   bucket array) is emitted (int), followed  by the
     *		   <i>size</i> of the IntHashtable (the number of key-value
     *		   mappings), followed by the key (Object) and value (Object)
     *		   for each key-value mapping represented by the IntHashtable
     *		   The key-value mappings are emitted in no particular order.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
	// Write out the length, threshold, loadfactor
	s.defaultWriteObject();

	// Write out length, count of elements, and then the key/value objects
	s.writeInt(table.length);
	s.writeInt(count);
	for (int index = table.length-1; index >= 0; index--) {
	    Entry entry = table[index];

	    while (entry != null) {
		s.writeInt(entry.key);
		s.writeObject(entry.value);
		entry = entry.next;
	    }
	}
    }

    /**
     * Reconstitute the IntHashtable from a stream (i.e., deserialize it).
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
	// Read in the length, threshold, and loadfactor
	s.defaultReadObject();

	// Read the original length of the array, number of elements, and the elements themselves
	int origlength = s.readInt();
	int elements = s.readInt();

	// Compute new size with a bit of room 5% to grow but
	// No larger than the original size.  Make the length
	// odd if it's large enough, this helps distribute the entries.
	// Guard against the length ending up zero, that's not valid.
	int length = (int)(elements * loadFactor) + (elements / 20) + 3;
	if (length > elements && (length & 1) == 0)
	    length--;
	if (origlength > 0 && length > origlength)
	    length = origlength;

	table = new Entry[length];
	count = 0;

	// Read the number of elements and then all the key/value objects
	for (; elements > 0; elements--) {
	    int key = s.readInt();
	    Object value = s.readObject();
	    put(key, value);
	}
    }


    /**
     * IntHashtable collision list.
     */
    private static class Entry  {
	int key;
	Object value;
	Entry next;

	protected Entry(int key, Object value, Entry next) {
	    this.key = key;
	    this.value = value;
	    this.next = next;
	}

	protected Object clone() {
	    return new Entry(key, value,
			     (next==null ? null : (Entry)next.clone()));
	}

	// Map.Entry Ops 

	public int getKey() {
	    return key;
	}

	public Object getValue() {
	    return value;
	}

	public Object setValue(Object value) {
	    Object oldValue = this.value;
	    this.value = value;
	    return oldValue;
	}

	public boolean equals(Object o) {
	    if (!(o instanceof Entry))
		return false;
	    Entry e = (Entry)o;

	    return ( key == e.getKey() &&
	       (value==null ? e.getValue()==null : value.equals(e.getValue())) );
	}

	public String toString() {
        String valueStr = value.toString();
        StringBuilder sb = new StringBuilder(valueStr.length()+11);
        sb.append(key).append("=").append(valueStr);
        return sb.toString();
	}
    }

    // Types of Enumerations/Iterations
    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;

    /**
     * A hashtable enumerator class.  This class implements both the
     * Enumeration and Iterator interfaces, but individual instances
     * can be created with the Iterator methods disabled.  This is necessary
     * to avoid unintentionally increasing the capabilities granted a user
     * by passing an Enumeration.
     */
    private class Enumerator implements Enumeration, Iterator {
	Entry[] table = IntHashtable.this.table;
	int index = table.length;
	Entry entry = null;
	Entry lastReturned = null;
	int type;

	/**
	 * Indicates whether this Enumerator is serving as an Iterator
	 * or an Enumeration.  (true -> Iterator).
	 */
	boolean iterator;

	/**
	 * The modCount value that the iterator believes that the backing
	 * List should have.  If this expectation is violated, the iterator
	 * has detected concurrent modification.
	 */
	private int expectedModCount = modCount;

	Enumerator(int type, boolean iterator) {
	    this.type = type;
	    this.iterator = iterator;
	}

	public boolean hasMoreElements() {
	    while (entry==null && index>0)
		entry = table[--index];

	    return entry != null;
	}

	public Object nextElement() {
	    while (entry==null && index>0)
		entry = table[--index];

	    if (entry != null) {
		Entry e = lastReturned = entry;
		entry = e.next;
		return type == KEYS ? Integer.valueOf( e.key ) : (type == VALUES ? e.value : e);
	    }
	    throw new NoSuchElementException("Hashtable Enumerator");
	}

	// Iterator methods
	public boolean hasNext() {
	    return hasMoreElements();
	}

	public Object next() {
	    if (modCount != expectedModCount)
		throw new ConcurrentModificationException();
	    return nextElement();
	}

	public void remove() {
	    if (!iterator)
		throw new UnsupportedOperationException();
	    if (lastReturned == null)
		throw new IllegalStateException("Hashtable Enumerator");
	    if (modCount != expectedModCount)
		throw new ConcurrentModificationException();

	    synchronized(IntHashtable.this) {
		Entry[] tab = IntHashtable.this.table;
		int index = (lastReturned.key & 0x7FFFFFFF) % tab.length;

		for (Entry e = tab[index], prev = null; e != null;
		     prev = e, e = e.next) {
		    if (e == lastReturned) {
			modCount++;
			expectedModCount++;
			if (prev == null)
			    tab[index] = e.next;
			else
			    prev.next = e.next;
			count--;
			lastReturned = null;
			return;
		    }
		}
		throw new ConcurrentModificationException();
	    }
	}
    }
}
