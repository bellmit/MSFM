package com.cboe.domain.util.intMaps;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Natively int-keyed HashMap implementation.  Source is ripped from JDK 1.6_07 AbstractHashMap and
 * HashMap, merged to a single class, any minimally mutated to replace all K references with int.
 * 
 * @author Steve
 * 
 * @see com.cboe.domain.util.intMaps.ConcurrentIntHashMap
 * @see java.util.AbstractHashMap
 * @see java.util.HashMap
 *
 * @param <V> Type of value to store in map
 */
public final class IntHashMap<V> implements IntMap<V>, Cloneable
{

    ////////////////////////////////////////////
    //
    // Code "borrowed" from AbstractHashMap:
    //
    //
    // Query Operations

    public boolean containsKey(int key)
    {
        return getEntry(key) != null;
    }

    // Views

    /**
     * Each of these fields are initialized to contain an instance of the
     * appropriate view the first time this view is requested. The views are
     * stateless, so there's no reason to create more than one of each.
     */
    transient volatile Set<Integer>  keySet = null;
    transient volatile Collection<V> values = null;

    // Comparison and hashing

    /**
     * Compares the specified object with this map for equality. Returns
     * <tt>true</tt> if the given object is also a map and the two maps
     * represent the same mappings. More formally, two maps <tt>m1</tt> and
     * <tt>m2</tt> represent the same mappings if
     * <tt>m1.entrySet().equals(m2.entrySet())</tt>. This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     * 
     * <p>
     * This implementation first checks if the specified object is this map; if
     * so it returns <tt>true</tt>. Then, it checks if the specified object
     * is a map whose size is identical to the size of this map; if not, it
     * returns <tt>false</tt>. If so, it iterates over this map's
     * <tt>entrySet</tt> collection, and checks that the specified map
     * contains each mapping that this map contains. If the specified map fails
     * to contain such a mapping, <tt>false</tt> is returned. If the iteration
     * completes, <tt>true</tt> is returned.
     * 
     * @param o
     *            object to be compared for equality with this map
     * @return <tt>true</tt> if the specified object is equal to this map
     */
    public boolean equals(Object o)
    {
        if (o == this)
            return true;

        if (!(o instanceof IntHashMap))
            return false;
        IntHashMap<V> m = (IntHashMap<V>) o;
        if (m.size() != size())
            return false;

        try
        {
            Iterator<IntMapEntry<V>> i = entrySet().iterator();
            while (i.hasNext())
            {
                IntMapEntry<V> e = i.next();
                int key = e.getKey();
                V value = e.getValue();
                if (value == null)
                {
                    if (!(m.get(key) == null && m.containsKey(key)))
                        return false;
                } else
                {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused)
        {
            return false;
        } catch (NullPointerException unused)
        {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code value for this map. The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * <tt>entrySet()</tt> view. This ensures that <tt>m1.equals(m2)</tt>
     * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
     * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     * 
     * <p>
     * This implementation iterates over <tt>entrySet()</tt>, calling
     * {@link IntMapEntry#hashCode hashCode()} on each element (entry) in the set, and
     * adding up the results.
     * 
     * @return the hash code value for this map
     * @see IntMapEntry#hashCode()
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    public int hashCode()
    {
        int h = 0;
        Iterator<IntMapEntry<V>> i = entrySet().iterator();
        while (i.hasNext())
            h += i.next().hashCode();
        return h;
    }

    /**
     * Returns a string representation of this map. The string representation
     * consists of a list of key-value mappings in the order returned by the
     * map's <tt>entrySet</tt> view's iterator, enclosed in braces (<tt>"{}"</tt>).
     * Adjacent mappings are separated by the characters <tt>", "</tt> (comma
     * and space). Each key-value mapping is rendered as the key followed by an
     * equals sign (<tt>"="</tt>) followed by the associated value. Keys and
     * values are converted to strings as by {@link String#valueOf(Object)}.
     * 
     * @return a string representation of this map
     */
    public String toString()
    {
        Iterator<IntMapEntry<V>> i = entrySet().iterator();
        if (!i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;)
        {
            IntMapEntry<V> e = i.next();
            int key = e.getKey();
            V value = e.getValue();
            sb.append(key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (!i.hasNext())
                return sb.append('}').toString();
            sb.append(", ");
        }
    }


    ////////////////////////////////////////////
    //
    // Code "borrowed" from HashMap:
    //
    //

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int       DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified by
     * either of the constructors with arguments. MUST be a power of two <= 1<<30.
     */
    static final int       MAXIMUM_CAPACITY         = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float     DEFAULT_LOAD_FACTOR      = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    transient EntryImpl<V>[]      table;

    /**
     * The number of key-value mappings contained in this map.
     */
    transient int          size;

    /**
     * The next size value at which to resize (capacity * load factor).
     * 
     * @serial
     */
    int                    threshold;

    /**
     * The load factor for the hash table.
     * 
     * @serial
     */
    final float            loadFactor;

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g., rehash).
     * This field is used to make iterators on Collection-views of the HashMap
     * fail-fast. (See ConcurrentModificationException).
     */
    transient volatile int modCount;

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     * 
     * @param initialCapacity
     *            the initial capacity
     * @param loadFactor
     *            the load factor
     * @throws IllegalArgumentException
     *             if the initial capacity is negative or the load factor is
     *             nonpositive
     */
    public IntHashMap(int initialCapacity, float loadFactor)
    {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        this.loadFactor = loadFactor;
        threshold = (int) (capacity * loadFactor);
        table = new EntryImpl[capacity];
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     * 
     * @param initialCapacity
     *            the initial capacity.
     * @throws IllegalArgumentException
     *             if the initial capacity is negative.
     */
    public IntHashMap(int initialCapacity)
    {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public IntHashMap()
    {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new EntryImpl[DEFAULT_INITIAL_CAPACITY];
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>. The <tt>HashMap</tt> is created with default
     * load factor (0.75) and an initial capacity sufficient to hold the
     * mappings in the specified <tt>Map</tt>.
     * 
     * @param m
     *            the map whose mappings are to be placed in this map
     * @throws NullPointerException
     *             if the specified map is null
     */
    public IntHashMap(IntHashMap<? extends V> m)
    {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAllForCreate(m);
    }

    // internal utilities

    /**
     * Applies a supplemental hash function to a given hashCode, which defends
     * against poor quality hash functions. This is critical because HashMap
     * uses power-of-two length hash tables, that otherwise encounter collisions
     * for hashCodes that do not differ in lower bits. Note: Null keys always
     * map to hash 0, thus index 0.
     */
    static int hash(int h)
    {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length)
    {
        return h & (length - 1);
    }

    /**
     * Returns the number of key-value mappings in this map.
     * 
     * @return the number of key-value mappings in this map
     */
    public int size()
    {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     * 
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     * 
     * <p>
     * More formally, if this map contains a mapping from a key {@code k} to a
     * value {@code v} such that {@code (key==null ? k==null : key.equals(k))},
     * then this method returns {@code v}; otherwise it returns {@code null}.
     * (There can be at most one such mapping.)
     * 
     * <p>
     * A return value of {@code null} does not <i>necessarily</i> indicate that
     * the map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to {@code null}. The
     * {@link #containsKey containsKey} operation may be used to distinguish
     * these two cases.
     * 
     * @see #put(Object, Object)
     */
    public V get(int key)
    {
        int hash = hash(key);
        for (EntryImpl<V> e = table[indexFor(hash, table.length)]; e != null; e = e.next)
        {
            if (e.hash == hash && (e.key == key))
                return e.value;
        }
        return null;
    }

    /**
     * Returns the entry associated with the specified key in the HashMap.
     * Returns null if the HashMap contains no mapping for the key.
     */
    final EntryImpl<V> getEntry(int key)
    {
        int hash = hash(key);
        for (EntryImpl<V> e = table[indexFor(hash, table.length)]; e != null; e = e.next)
        {
            int k;
            if (e.hash == hash && ((k = e.key) == key || (key==k)))
                return e;
        }
        return null;
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is
     * replaced.
     * 
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>. (A
     *         <tt>null</tt> return can also indicate that the map previously
     *         associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(int key, V value)
    {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (EntryImpl<V> e = table[i]; e != null; e = e.next)
        {
            if (e.hash == hash && e.key == key)
            {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }

    /**
     * This method is used instead of put by constructors and pseudoconstructors
     * (clone, readObject). It does not resize the table, check for
     * comodification, etc. It calls createEntry rather than addEntry.
     */
    private void putForCreate(int key, V value)
    {
        int hash = hash(key);
        int i = indexFor(hash, table.length);

        /**
         * Look for preexisting entry for key. This will never happen for clone
         * or deserialize. It will only happen for construction if the input Map
         * is a sorted map whose ordering is inconsistent w/ equals.
         */
        for (EntryImpl<V> e = table[i]; e != null; e = e.next)
        {
            if (e.hash == hash && e.key == key)
            {
                e.value = value;
                return;
            }
        }

        createEntry(hash, key, value, i);
    }

    private void putAllForCreate(IntHashMap<? extends V> m)
    {
        for (Iterator<? extends IntMapEntry<? extends V>> i = m.entrySet().iterator(); i.hasNext();)
        {
            IntMapEntry<? extends V> e = i.next();
            putForCreate(e.getKey(), e.getValue());
        }
    }

    /**
     * Rehashes the contents of this map into a new array with a larger
     * capacity. This method is called automatically when the number of keys in
     * this map reaches its threshold.
     * 
     * If current capacity is MAXIMUM_CAPACITY, this method does not resize the
     * map, but sets threshold to Integer.MAX_VALUE. This has the effect of
     * preventing future calls.
     * 
     * @param newCapacity
     *            the new capacity, MUST be a power of two; must be greater than
     *            current capacity unless current capacity is MAXIMUM_CAPACITY
     *            (in which case value is irrelevant).
     */
    void resize(int newCapacity)
    {
        EntryImpl[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY)
        {
            threshold = Integer.MAX_VALUE;
            return;
        }

        EntryImpl[] newTable = new EntryImpl[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * Transfers all entries from current table to newTable.
     */
    void transfer(EntryImpl[] newTable)
    {
        EntryImpl[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++)
        {
            EntryImpl<V> e = src[j];
            if (e != null)
            {
                src[j] = null;
                do
                {
                    EntryImpl<V> next = e.next;
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }
    
    public void putAll(Map<Integer, ? extends V> m)
    {
        for (Integer key : m.keySet())
        {
            if (key != null)
                put(key.intValue(), m.get(key));
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map. These
     * mappings will replace any mappings that this map had for any of the keys
     * currently in the specified map.
     * 
     * @param m
     *            mappings to be stored in this map
     * @throws NullPointerException
     *             if the specified map is null
     */
    public void putAll(IntMap<? extends V> m)
    {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;

        /*
         * Expand the map if the map if the number of mappings to be added is
         * greater than or equal to threshold. This is conservative; the obvious
         * condition is (m.size() + size) >= threshold, but this condition could
         * result in a map with twice the appropriate capacity, if the keys to
         * be added overlap with the keys already in this map. By using the
         * conservative calculation, we subject ourself to at most one extra
         * resize.
         */
        if (numKeysToBeAdded > threshold)
        {
            int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length)
                resize(newCapacity);
        }

        for (Iterator<? extends IntMapEntry<? extends V>> i = m.entrySet().iterator(); i.hasNext();)
        {
            IntMapEntry<? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * 
     * @param key
     *            key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>. (A
     *         <tt>null</tt> return can also indicate that the map previously
     *         associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V remove(int key)
    {
        EntryImpl<V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }

    /**
     * Removes and returns the entry associated with the specified key in the
     * HashMap. Returns null if the HashMap contains no mapping for this key.
     */
    final EntryImpl<V> removeEntryForKey(int key)
    {
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        EntryImpl<V> prev = table[i];
        EntryImpl<V> e = prev;

        while (e != null)
        {
            EntryImpl<V> next = e.next;
            int k;
            if (e.hash == hash && ((k = e.key) == key || (key == k)))
            {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Special version of remove for EntrySet.
     */
    final EntryImpl<V> removeMapping(Object o)
    {
        if (!(o instanceof IntMapEntry))
            return null;

        IntMapEntry<V> entry = (IntMapEntry<V>) o;
        int key = entry.getKey();
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        EntryImpl<V> prev = table[i];
        EntryImpl<V> e = prev;

        while (e != null)
        {
            EntryImpl<V> next = e.next;
            if (e.hash == hash && e.equals(entry))
            {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }

    /**
     * Removes all of the mappings from this map. The map will be empty after
     * this call returns.
     */
    public void clear()
    {
        modCount++;
        EntryImpl[] tab = table;
        for (int i = 0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     * 
     * @param value
     *            value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    public boolean containsValue(Object value)
    {
        if (value == null)
            return containsNullValue();

        EntryImpl[] tab = table;
        for (int i = 0; i < tab.length; i++)
            for (EntryImpl e = tab[i]; e != null; e = e.next)
                if (value.equals(e.value))
                    return true;
        return false;
    }

    /**
     * Special-case code for containsValue with null argument
     */
    private boolean containsNullValue()
    {
        EntryImpl[] tab = table;
        for (int i = 0; i < tab.length; i++)
            for (EntryImpl e = tab[i]; e != null; e = e.next)
                if (e.value == null)
                    return true;
        return false;
    }

    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and
     * values themselves are not cloned.
     * 
     * @return a shallow copy of this map
     */
    public Object clone()
    {
        IntHashMap<V> result = null;
        try
        {
            result = (IntHashMap<V>)super.clone();
            result.keySet = null;
            result.values = null;
        } catch (CloneNotSupportedException e)
        {
            // assert false;
        }
        result.table = new EntryImpl[table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.putAllForCreate(this);

        return result;
    }

    static class EntryImpl<V> implements IntMapEntry<V>
    {
        final int     key;
        V           value;
        EntryImpl<V> next;
        final int   hash;

        /**
         * Creates new entry.
         */
        EntryImpl(int h, int k, V v, EntryImpl<V> n)
        {
            value = v;
            next = n;
            key = k;
            hash = h;
        }

        public final int getKey()
        {
            return key;
        }

        public final V getValue()
        {
            return value;
        }

        public final V setValue(V newValue)
        {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o)
        {
            if (!(o instanceof IntMapEntry))
                return false;
            IntMapEntry e = (IntMapEntry) o;
            int k1 = getKey();
            int k2 = e.getKey();
            if (k1 == k2 || (k1==k2))
            {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        public final int hashCode()
        {
            return key ^ (value == null ? 0 : value.hashCode());
        }

        public final String toString()
        {
            return getKey() + "=" + getValue();
        }

        /**
         * This method is invoked whenever the value in an entry is overwritten
         * by an invocation of put(k,v) for a key k that's already in the
         * HashMap.
         */
        void recordAccess(IntHashMap<V> m)
        {
        }

        /**
         * This method is invoked whenever the entry is removed from the table.
         */
        void recordRemoval(IntHashMap<V> m)
        {
        }
    }

    /**
     * Adds a new entry with the specified key, value and hash code to the
     * specified bucket. It is the responsibility of this method to resize the
     * table if appropriate.
     * 
     * Subclass overrides this to alter the behavior of put method.
     */
    void addEntry(int hash, int key, V value, int bucketIndex)
    {
        EntryImpl<V> e = table[bucketIndex];
        table[bucketIndex] = new EntryImpl<V>(hash, key, value, e);
        if (size++ >= threshold)
            resize(2 * table.length);
    }

    /**
     * Like addEntry except that this version is used when creating entries as
     * part of Map construction or "pseudo-construction" (cloning,
     * deserialization). This version needn't worry about resizing the table.
     * 
     * Subclass overrides this to alter the behavior of HashMap(Map), clone, and
     * readObject.
     */
    void createEntry(int hash, int key, V value, int bucketIndex)
    {
        EntryImpl<V> e = table[bucketIndex];
        table[bucketIndex] = new EntryImpl<V>(hash, key, value, e);
        size++;
    }

    private abstract class IntHashIterator<E> implements Iterator<E>
    {
        EntryImpl<V> next;            // next entry to return
        int         expectedModCount; // For fast-fail
        int         index;           // current slot
        EntryImpl<V> current;         // current entry

        IntHashIterator()
        {
            expectedModCount = modCount;
            if (size > 0)
            { // advance to first entry
                EntryImpl[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
        }

        public final boolean hasNext()
        {
            return next != null;
        }

        final EntryImpl<V> nextEntry()
        {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            EntryImpl<V> e = next;
            if (e == null)
                throw new NoSuchElementException();

            if ((next = e.next) == null)
            {
                EntryImpl[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
            current = e;
            return e;
        }

        public void remove()
        {
            if (current == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            final int k = current.key;
            current = null;
            IntHashMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }

    private final class ValueIterator extends IntHashIterator<V>
    {
        public V next()
        {
            return nextEntry().value;
        }
    }

    private final class KeyIterator extends IntHashIterator<Integer>
    {
        public Integer next()
        {
            return new Integer(nextEntry().getKey());
        }
    }

    private final class EntryIterator extends IntHashIterator<IntMapEntry<V>>
    {
        public IntMapEntry<V> next()
        {
            return nextEntry();
        }
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    Iterator<Integer> newKeyIterator()
    {
        return new KeyIterator();
    }

    Iterator<V> newValueIterator()
    {
        return new ValueIterator();
    }

    Iterator<IntMapEntry<V>> newEntryIterator()
    {
        return new EntryIterator();
    }

    // Views

    private transient Set<IntMapEntry<V>> entrySet = null;

    /**
     * Returns a {@link Set} view of the keys contained in this map. The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. If the map is modified while an iteration over the set is in
     * progress (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined. The set supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     */
    public Set<Integer> keySet()
    {
        Set<Integer> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    private final class KeySet extends AbstractSet<Integer>
    {
        public Iterator<Integer> iterator()
        {
            return newKeyIterator();
        }

        public int size()
        {
            return size;
        }
        
        public boolean contains(Object o)
        {
            return o instanceof Integer && containsKey(((Integer)o).intValue());
        }
        
        public boolean remove(Object o)
        {
            return IntHashMap.this.removeEntryForKey(((Integer)o).intValue()) != null;
        }

        public void clear()
        {
            IntHashMap.this.clear();
        }
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are reflected
     * in the collection, and vice-versa. If the map is modified while an
     * iteration over the collection is in progress (except through the
     * iterator's own <tt>remove</tt> operation), the results of the iteration
     * are undefined. The collection supports element removal, which removes the
     * corresponding mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt>
     * and <tt>clear</tt> operations. It does not support the <tt>add</tt>
     * or <tt>addAll</tt> operations.
     */
    public Collection<V> values()
    {
        Collection<V> vs = values;
        return (vs != null ? vs : (values = new Values()));
    }

    private final class Values extends AbstractCollection<V>
    {
        public Iterator<V> iterator()
        {
            return newValueIterator();
        }

        public int size()
        {
            return size;
        }

        public boolean contains(Object o)
        {
            return containsValue(o);
        }

        public void clear()
        {
            IntHashMap.this.clear();
        }
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map. The set
     * is backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. If the map is modified while an iteration over the set is in
     * progress (except through the iterator's own <tt>remove</tt> operation,
     * or through the <tt>setValue</tt> operation on a map entry returned by
     * the iterator) the results of the iteration are undefined. The set
     * supports element removal, which removes the corresponding mapping from
     * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt>
     * operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     * 
     * @return a set view of the mappings contained in this map
     */
    public Set<IntMapEntry<V>> entrySet()
    {
        return entrySet0();
    }

    private Set<IntMapEntry<V>> entrySet0()
    {
        Set<IntMapEntry<V>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    private final class EntrySet extends AbstractSet<IntMapEntry<V>>
    {
        public Iterator<IntMapEntry<V>> iterator()
        {
            return newEntryIterator();
        }

        public boolean contains(Object o)
        {
            if (!(o instanceof IntMapEntry))
                return false;
            IntMapEntry<V> e = (IntMapEntry<V>) o;
            EntryImpl<V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }

        public boolean remove(Object o)
        {
            return removeMapping(o) != null;
        }

        public int size()
        {
            return size;
        }

        public void clear()
        {
            IntHashMap.this.clear();
        }
    }
}
