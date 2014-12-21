package com.cboe.interfaces.application;

import java.util.Iterator;

/**
 * Generates a key value for use by a {@link Cache} based on data contained within a cache element.
 * <p>
 * The CacheKeyGenerator interface is designed to abstract data stored within a cache, so the cache does
 * not need to know anything about the data in order to store it properly. Various CacheKeyGenerators
 * are registered with a cache, and whenever a cache is updated or added to, it will use those key generators
 * to create keys for storage and retrieval.
 * <p>
 * The CacheKeyGenerator(s) provided to a cache must be able to generate keys using only the data contained
 * within a single element. The reference implementation of the Cache interface ({@link BaseCache}) requires
 * keys to be unique, hashable, replicatable, and operationally immutable (the data that is used to generate
 * the key must not be subject to change).
 * <p>
 * If a unique key cannot be generated for an element, returning <code>null</code> will cause the cache to leave
 * that element out of that "key set" of the cache. In other words, that element will not be able to be found using
 * that key, but may be found using other keys. This functionality can be used as an inexpensive "filtered" view
 * of a particular cache (for instance, a product key set that only accepts options). It is important to note
 * that the primary key generator for a cache <b>must</b> be a superset of all its secondary key sets - it is
 * used to export "the entire cache" and, as such, should probably not perform filtering and should not fail to
 * create a key where other keys might succeed. The {@link BaseCache} reference implementation will not even
 * attempt to insert elements into secondary key sets if the primary key generator fails to generate a key.
 * <p>
 * CacheKeyGenerator also can cause the cache to group elements together into sub-caches that are either
 * <i>inclusive</i> (the same element can be in multiple sub-caches) or <i>exclusive</i> (an element can only
 * exist in one of the sub-caches). This allows for "group-by" logic, such as "products grouped by class key",
 * and find methods based on such groups ("find all products in a particular class"). To do this in a thread-safe
 * manner, group keys are generated via an iterator that is created by invoking the <code>groupIterator</code>
 * method. For simple exclusive groups, subclass from the {@link AbstractGroupIterator} found in the
 * {@link com.cboe.application.cache} package. Key generators that implement grouping must return <code>true</code>
 * from the <code>doesGroup</code> method.
 * <p>
 * NOTE: It is important that the <code>doesGroup</code> method only return <code>true</code> if groups are actually
 * created by the <code>groupIterator</code>. If not, the key not be stored properly in the cache.
 * <p>
 * @author Brian Erst
 * @see Cache
 * @see Synchable
 * @version 2001.1.12
 */
public interface CacheKeyGenerator
{
    /**
     * Creates a unique key based on data found within the passed-in object. For non-primary keys, an element
     * can be "skipped" by returning <code>null</code>.
     * @param fromObject element to be inserted into the cache
     * @return a unique, hashable key object or <code>null</code> if no key could be generated
     */
    public Object generateKey(Object fromObject);

    /**
     * Creates a thread-safe iterator for generating group-by keys.
     * @param fromObject element to be inserted into the cache
     * @return an Iterator that, when iterated, returns one or more unique, hashable group keys
     */
    public Iterator groupIterator(Object fromObject);

    /**
     * Indicates whether this key generator creates groups.
     * @return <code>true</code> if grouping is desired, <false> if not.
     */
    public boolean doesGroup();
}
