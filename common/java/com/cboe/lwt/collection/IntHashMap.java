package com.cboe.lwt.collection;


import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;


/**
 * Hash table based implementation of the <tt>Map</tt> interface. This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key. (The <tt>IntHashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.) This class makes no guarantees as to the
 * order of the map; in particular, it does not guarantee that the order will
 * remain constant over time.
 * 
 * <p>
 * This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets. Iteration over collection
 * views requires time proportional to the "capacity" of the <tt>IntHashMap</tt>
 * instance (the number of buckets) plus its size (the number of key-value
 * mappings). Thus, it's very important not to set the initial capacity too high
 * (or the load factor too low) if iteration performance is important.
 * 
 * <p>
 * An instance of <tt>IntHashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity </i> and <i>load factor </i>. The
 * <i>capacity </i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created. The
 * <i>load factor </i> is a measure of how full the hash table is allowed to get
 * before its capacity is automatically increased. When the number of entries in
 * the hash table exceeds the product of the load factor and the current
 * capacity, the capacity is roughly doubled by calling the <tt>rehash</tt>
 * method.
 * 
 * <p>
 * As a general rule, the default load factor (.75) offers a good tradeoff
 * between time and space costs. Higher values decrease the space overhead but
 * increase the lookup cost (reflected in most of the operations of the
 * <tt>IntHashMap</tt> class, including <tt>get</tt> and <tt>put</tt>).
 * The expected number of entries in the map and its load factor should be taken
 * into account when setting its initial capacity, so as to minimize the number
 * of <tt>rehash</tt> operations. If the initial capacity is greater than the
 * maximum number of entries divided by the load factor, no <tt>rehash</tt>
 * operations will ever occur.
 * 
 * <p>
 * If many mappings are to be stored in a <tt>IntHashMap</tt> instance,
 * creating it with a sufficiently large capacity will allow the mappings to be
 * stored more efficiently than letting it perform automatic rehashing as needed
 * to grow the table.
 * 
 * <p>
 * <b>Note that this implementation is not synchronized. </b> If multiple
 * threads access this map concurrently, and at least one of the threads
 * modifies the map structurally, it <i>must </i> be synchronized externally. (A
 * structural modification is any operation that adds or deletes one or more
 * mappings; merely changing the value associated with a key that an instance
 * already contains is not a structural modification.) This is typically
 * accomplished by synchronizing on some object that naturally encapsulates the
 * map. If no such object exists, the map should be "wrapped" using the
 * <tt>Collections.synchronizedMap</tt> method. This is best done at creation
 * time, to prevent accidental unsynchronized access to the map:
 * 
 * <pre>
 * 
 *   Map m = Collections.synchronizedMap(new IntHashMap(...));
 *   
 *  
 * </pre>
 * 
 * <p>
 * The iterators returned by all of this class's "collection view methods" are
 * <i>fail-fast </i>: if the map is structurally modified at any time after the
 * iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> or <tt>add</tt> methods, the iterator will throw a
 * <tt>ConcurrentModificationException</tt>. Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 * 
 * <p>
 * Note that the fail-fast behavior of an iterator cannot be guaranteed as it
 * is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification. Fail-fast iterators throw
 * <tt>ConcurrentModificationException</tt> on a best-effort basis. Therefore,
 * it would be wrong to write a program that depended on this exception for its
 * correctness: <i>the fail-fast behavior of iterators should be used only to
 * detect bugs. </i>
 * 
 * @author Doug Lea
 * @author Josh Bloch
 * @author Arthur van Hoff
 * @version 1.52, 04/20/02
 * @see Object#hashCode()
 * @see Collection
 * @see Map
 * @see TreeMap
 * @see Hashtable
 * @since 1.2
 * 
 * @author Lee Doty - intification
 */
public final class IntHashMap
{


    ///////////////////////////////////////////////////////////////////////////
    // inner class
    
    private final static class Entry
    {
        final int hash;
        final int key;
        Object value;
        Entry next;


        /**
         * Create new entry.
         */
        Entry( int    p_hash,
               int    p_key,
               Object p_value,
               Entry  p_prev, 
               Entry  p_next )
        {
            hash  = p_hash;
            key   = p_key;
            value = p_value;
            next  = p_next;
        }
        
        
        public int key()
        {
            return key;
        }
        
        
        public Object value()
        {
            return value;
        }


        public Object setValue( Object p_newValue )
        {
            Object oldValue = value;
            value = p_newValue;
            return oldValue;
        }


        public int hashCode()
        {
            return key;
        }


        public String toString()
        {
            return key + "=" + value;
        }
    }

    // inner class
    ///////////////////////////////////////////////////////////////////////////
    // inner class

    public final class Iter
    {
        Entry next; // next entry to return
        int index; // current slot
        Entry current; // current entry


        Iter()
        {
            int i = table.length;
            Entry n = null;
            if( size != 0 )
            { // advance to first entry
                while( i > 0 && ( n = table[ --i ] ) == null )
                {
                }
            }
            next = n;
            index = i;
        }


        public final boolean hasNext()
        {
            return next != null;
        }


        public final void next()
        {
            if( next == null )
            {
                throw new NoSuchElementException();
            }
            
            current = next;
           
            next = current.next;
            
            while ( next == null && index > 0 )
            {
                next = table[ --index ];
            }
        }
        
        
        public final int getKey()
        {
            return current.key;
        }
        
        
        public final Object getValue()
        {
            return current.value;
        }


        public final void remove()
        {
            if( current == null )
                throw new IllegalStateException();

            IntHashMap.this.remove( current.key );
            current = null;
        }

    }

    // inner class
    ///////////////////////////////////////////////////////////////////////////

    
    /**
     * The maximum capacity, used if a higher value is implicitly specified by
     * either of the constructors with arguments. MUST be a power of two <= 1 <
     * <30.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load fast used when none specified in constructor.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    Entry[] table;

    /**
     * The number of key-value mappings contained in this identity hash map.
     */
    int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     * 
     * @serial
     */
    private int threshold;

    /**
     * The load factor for the hash table.
     * 
     * @serial
     */
    private float loadFactor;


    /**
     * Constructs an empty <tt>IntHashMap</tt> with the specified initial
     * capacity and load factor.
     * 
     * @param p_initialCapacity
     *            The initial capacity.
     * @param p_loadFactor
     *            The load factor.
     * @throws IllegalArgumentException
     *             if the initial capacity is negative or the load factor is
     *             nonpositive.
     */
    public IntHashMap( int p_initialCapacity,
                       float p_loadFactor )
    {
        if( p_initialCapacity < 0 )
            throw new IllegalArgumentException( "Illegal initial capacity: " + p_initialCapacity );
        if( p_initialCapacity > MAXIMUM_CAPACITY )
            p_initialCapacity = MAXIMUM_CAPACITY;
        if( p_loadFactor <= 0 || Float.isNaN( p_loadFactor ) )
            throw new IllegalArgumentException( "Illegal load factor: " + p_loadFactor );

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while( capacity < p_initialCapacity )
            capacity <<= 1;

        loadFactor = p_loadFactor;
        threshold = (int)( capacity * p_loadFactor );
        table = new Entry[ capacity ];
    }


    /**
     * Constructs an empty <tt>IntHashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     * 
     * @param initialCapacity
     *            the initial capacity.
     * @throws IllegalArgumentException
     *             if the initial capacity is negative.
     */
    public IntHashMap( int initialCapacity )
    {
        this( initialCapacity,
              DEFAULT_LOAD_FACTOR );
    }


    /**
     * Constructs an empty <tt>IntHashMap</tt> for use only with internal factory methods
     * like shallowCopy()
     */
    private IntHashMap()
    {
    }


    public final IntHashMap shallowCopy()
    {
        IntHashMap result = new IntHashMap();
         
        result.size       = size;
        result.loadFactor = loadFactor;
        result.threshold  = threshold;
        result.table      = new Entry[ table.length ];
        
        result.putAll( this );
        
        return result;
    }


    // internal utilities

    /**
     * Returns a hash value for the specified object. In addition to the
     * object's own hashCode, this method applies a "supplemental hash
     * function," which defends against poor quality hash functions. This is
     * critical because IntHashMap uses power-of two length hash tables.
     * <p>
     * 
     * The shift distances in this function were chosen as the result of an
     * automated search over the entire four-dimensional search space.
     */
    private final int hashFromKey( int p_key )
    {
        p_key += ~( p_key << 9 );
        p_key ^=  ( p_key >>> 14 );
        p_key +=  ( p_key << 4 );
        p_key ^=  ( p_key >>> 10 );
        
        return p_key;
    }
    
    
    private final int hashIndexFromKey( int p_key )
    {
        return indexFromHash( hashFromKey( p_key ) );
    }
    
    
    private final int indexFromHash( int p_hash )
    {
        return p_hash & ( table.length - 1 );
    }


    /**
     * Returns the number of key-value mappings in this map.
     * 
     * @return the number of key-value mappings in this map.
     */
    public int size()
    {
        return size;
    }


    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     * 
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty()
    {
        return size == 0;
    }


    /**
     * Returns the value to which the specified key is mapped in this identity
     * hash map, or <tt>null</tt> if the map contains no mapping for this key.
     * A return value of <tt>null</tt> does not <i>necessarily </i> indicate
     * that the map contains no mapping for the key; it is also possible that
     * the map explicitly maps the key to <tt>null</tt>. The
     * <tt>containsKey</tt> method may be used to distinguish these two cases.
     * 
     * @param key
     *            the key whose associated value is to be returned.
     * @return the value to which this map maps the specified key, or
     *         <tt>null</tt> if the map contains no mapping for this key.
     */
    public Object get( int key )
    {
        int hashIndex = hashIndexFromKey( key );
        Entry e = table[ hashIndex ];
        while( e != null )
        {
            if( e.key == key )
            {
                return e.value;
            }
            e = e.next;
        }

        return e;
    }


    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     * 
     * @param key
     *            The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key.
     */
    public boolean containsKey( int key )
    {
        int hashIndex = hashIndexFromKey( key );
        Entry e = table[ hashIndex ];
        while( e != null )
        {
            if( e.key == key )
            {
                return true;
            }
            e = e.next;
        }
        return false;
    }


    /**
     * Returns the entry associated with the specified key in the IntHashMap.
     * Returns null if the IntHashMap contains no mapping for this key.
     */
    Entry getEntry( int key )
    {
        int hashIndex = hashIndexFromKey( key );
        
        for( Entry cur = table[ hashIndex ];
             cur != null;
             cur = cur.next )
        {
            if ( cur.key == key )
            {
                return cur;
            }
        }
        return null;
    }


    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for this key, the old value is
     * replaced.
     * 
     * @param p_key
     *            key with which the specified value is to be associated.
     * @param p_value
     *            value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key. A <tt>null</tt> return can
     *         also indicate that the IntHashMap previously associated
     *         <tt>null</tt> with the specified key.
     */
    public Object put( int    p_key,
                       Object p_value )
    {
        int hash      = hashFromKey( p_key );
        int hashIndex = indexFromHash( hash );

        for( Entry e = table[ hashIndex ]; e != null; e = e.next )
        {
            if( e.key == p_key )
            {
                return e.setValue( p_value );
            }
        }

        addEntry( p_key,
                  p_value,
                  hash,
                  hashIndex );
        return null;
    }


    /**
     * Rehashes the contents of this map into a new <tt>IntHashMap</tt>
     * instance with a larger capacity. This method is called automatically when
     * the number of keys in this map exceeds its capacity and load factor.
     * 
     * @param newCapacity
     *            the new capacity, MUST be a power of two.
     */
    private void resize( int newCapacity )
    {
        // assert (newCapacity & -newCapacity) == newCapacity; // power of 2
        int oldCapacity = table.length;

        // check if needed
        if( size < threshold || oldCapacity > newCapacity )
        {
            return;
        }

        Entry[] newTable = new Entry[ newCapacity ];

        transfer( newTable );
        table = newTable;
        
        threshold = ( newCapacity >= MAXIMUM_CAPACITY ) 
                    ? Integer.MAX_VALUE
                    : (int)( newCapacity * loadFactor );
    }


    /**
     * Transfer all entries from current table to newTable.
     */
    private void transfer( Entry[] p_dest )
    {
        Entry[] src = table;
        int newCapacity = p_dest.length;
        for( int i = 0; i < src.length; i++ )
        {
            Entry e = src[ i ];
            if( e != null )
            {
                src[ i ] = null;
                do
                {
                    Entry next = e.next;
                    int index = e.hash & ( newCapacity - 1 );
                    e.next = p_dest[ index ];
                    p_dest[ index ] = e;
                    e = next;
                }
                while( e != null );
            }
        }
    }
    
    
    
    void transfer2( Entry[] newTable )
    {
        Entry[] src = table;
        int newCapacity = newTable.length;
        for( int j = 0; j < src.length; j++ )
        {
            Entry e = src[ j ];
            if( e != null )
            {
                src[ j ] = null;
                do
                {
                    Entry next = e.next;
                    int i = e.hash & (newCapacity-1);
                    e.next = newTable[ i ];
                    newTable[ i ] = e;
                    e = next;
                }
                while( e != null );
            }
        }
    }
    


    /**
     * Copies all of the mappings from the specified map to this map These
     * mappings will replace any mappings that this map had for any of the keys
     * currently in the specified map.
     * 
     * @param p_src
     *            mappings to be stored in this map.
     * @throws NullPointerException
     *             if the specified map is null.
     */
    public void putAll( IntHashMap p_src )
    {
        if( p_src.size() == 0 || table.length == MAXIMUM_CAPACITY )
        {
            return;
        }

        int newSize = size + p_src.size();
        
        if( newSize >= threshold )
        {
            int newCapacity = table.length;
            int targetCapacity = (int)( newSize / loadFactor + 1 );
            
            while( newCapacity < targetCapacity )
            {
                newCapacity <<= 1;
            }

            if( newCapacity > MAXIMUM_CAPACITY )
            {
                newCapacity = MAXIMUM_CAPACITY;
            }

            resize( newCapacity );
        }

        for( Iter i = p_src.iterator(); i.hasNext(); )
        {
            i.next();
            put( i.getKey(),
                 i.getValue() );
        }
    }


    /**
     * Copies all of the mappings from the specified map to this map These
     * mappings will replace any mappings that this map had for any of the keys
     * currently in the specified map.
     * 
     * @param t
     *            mappings to be stored in this map.
     * @throws NullPointerException
     *             if the specified map is null.
     */
    public void putAll( Map t )
    {
        // Expand enough to hold t's elements without resizing.
        int n = t.size();
        if( n == 0 )
            return;
        if( n >= threshold )
        {
            n = (int)( n / loadFactor + 1 );
            if( n > MAXIMUM_CAPACITY )
                n = MAXIMUM_CAPACITY;
            int capacity = table.length;
            while( capacity < n )
                capacity <<= 1;
            resize( capacity );
        }

        for( Iterator i = t.entrySet().iterator(); i.hasNext(); )
        {
            Entry e = (Entry)i.next();
            put( e.key,
                 e.value );
        }
    }


    /**
     * Removes the mapping for this key from this map if present.
     * 
     * @param key
     *            key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key. A <tt>null</tt> return can
     *         also indicate that the map previously associated <tt>null</tt>
     *         with the specified key.
     */
    public Object remove( int key )
    {
        int hashIndex = hashIndexFromKey( key );
        
        for( Entry cur = table[ hashIndex ], prev = null;
             cur != null;
             prev = cur, cur = cur.next )
        {
            if ( cur.key == key )
            {
                size--;
                
                if( prev == null )
                {
                    table[ hashIndex ] = cur.next;
                }
                else
                {
                    prev.next = cur.next;
                }
                
                return cur.value;
            }
        }
        
        return null;
    }


    /**
     * Removes all mappings from this map.
     */
    public void clear()
    {
        Entry tab[] = table;
        for( int i = 0; i < tab.length; i++ )
            tab[ i ] = null;
        size = 0;
    }


    /**
     * Add a new entry with the specified key, value and hash code to the
     * specified bucket. It is the responsibility of this method to resize the
     * table if appropriate.
     * 
     * Subclass overrides this to alter the behavior of put method.
     */
    private void addEntry( int    p_key,
                           Object p_value,
                           int    p_hash,
                           int    p_hashIndex )
    {
        table[ p_hashIndex ] = new Entry( p_hash,
                                          p_key,
                                          p_value,
                                          null,
                                          table[ p_hashIndex ] );
                                  
        if( size++ >= threshold )
        {
            resize( table.length << 1 );
        }
    }
    
    public Iter iterator()
    {
        return new Iter();
    }
    
    
    public Object[] copyToArray()
    {
        if ( size == 0 ) 
        {
            return new Object[ 0 ];
        }
        
        Object[] result = new Object[ size ];
        
        Iter i = iterator();
        int  index = 0;
        
        while ( i.hasNext() )
        {
            i.next();
            result[ index ] = i.getValue();
            ++index;
        }
        
        return result;  // TODO
    }


    // These methods are used when serializing HashSets
    int capacity()
    {
        return table.length;
    }


    float loadFactor()
    {
        return loadFactor;
    }
    
}