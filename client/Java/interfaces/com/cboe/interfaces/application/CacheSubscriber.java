package com.cboe.interfaces.application;

/**
 * A stripped-down implementation of the Observer pattern for {@link Cache} objects.
 * <p>
 * The CacheSubscriber interface is designed to provide a way to notify other objects whenever the data
 * in a cache is modified. Objects implementing the CacheSubscriber interface can be registered with a
 * cache via the <code>addSubscriber</code> method. Upon the addition or modification of a data element
 * in the cache, the cache will go through its list of subscribers and call the <code>update</code> method
 * for each subscriber, passing a reference to the added/modified object. The object will most likely need
 * to be cast to the appropriate type within the <code>update</code> method.
 * <p>
 * The primary purpose of the CacheSubscriber interface is to provide bridge objects between caches that
 * store similar data. Many session-related caches contain objects that themselves contain references to
 * objects that reside in the product/class/strategy caches, and these bridge subscribers make sure that
 * only a single copy of these lower-level objects exist inside the JVM by replacing any references within
 * the higher-level objects to these lower-level objects. This does not preclude using the CacheSubscriber
 * interface for other purposes.
 * <p>
 * The addition of a <code>remove</code> method in the future may be useful. At the present time, caches
 * do not allow the removal of cached elements, so such a method is unneeded.
 * <p>
 * @author Brian Erst
 * @see Cache
 * @version 2001.1.12
 */
public interface CacheSubscriber
{
    /**
     * Called by a {@link Cache} whenever an object is added to or modified within it.
     * @param element the added or modified cache element
     */
    public void update(Object element);
}