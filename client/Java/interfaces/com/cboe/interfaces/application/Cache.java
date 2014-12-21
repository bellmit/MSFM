package com.cboe.interfaces.application;

/**
 * Provides keyed access to data elements.
 * <p>
 * The Cache interface provides for caching of generic Objects with multiple keys via the use of
 * {@link CacheKeyGenerator} objects that create keys based on data within the Object. For the
 * reference implementation ({@link BaseCache}), these keys must be unique, hashable, replicatable
 * and operationally immutable (the data used to construct the key should not change).
 * <p>
 * Cache is designed to support synchronization to a non-local data source by implementing the
 * Synchable interface and updating its synchronization ID by querying inserted objects using
 * a {@link CacheElementQuery object}. See {@link BaseCache} for implementation details.
 * <p>
 * Synchronization between local caches (for instance, product and session product caches) is implemented through
 * the use of {@link CacheSubscriber} objects that reconcile changes in one cache with one or more additional caches.
 * As many synchronizations are bi-directional, care must be used to prevent infinite loops
 * (product-->session product-->product-->etc.). This can be accomplished through the use of the
 * <code>ignoreSubscriber</code> parameter in <code>updateCache</code>.
 * @author Brian Erst
 * @see BaseCache
 * @see CacheElementQuery
 * @see CacheKeyGenerator
 * @see CacheSubscriber
 * @version 2001.1.12
 */
public interface Cache extends Synchable
{
    /**
     * Sets the element query object. Element query objects are currently used to retrieve the
     * element's synchronization ID, but the {@CacheElementQuery} interface could be extended in the
     * future to provide additional information to the cache about its elements.
     * @param __query the new element query object
     */
    public void setElementQuery(CacheElementQuery query);

    /**
     * Sets the primary cache key generator for the cache.
     * @param generator the cache will use this as the primary cache key generator
     */
    public void setPrimaryGenerator(CacheKeyGenerator generator);


    /**
     * Adds a secondary key map to the cache associated with this generator. Secondary keys can exclude
     * elements if needed (due, perhaps, to non-unique keys) and provide inclusive or exclusive grouping.
     * @param generator tells the cache to create a map to be associated with this generator
     */
    public void addGenerator(CacheKeyGenerator generator);

    /**
     * Removes a secondary key map from the cache.
     * @param generator tells the cache to remove the map associated with this generator
     */
    public void removeGenerator(CacheKeyGenerator generator);

    /**
     * Adds a subscriber/observer to the cache. The subscriber will be notified whenever an
     * element is added to or updated in the cache.
     * @param sub called whenever update/insertion occurs
     */
    public void addSubscriber(CacheSubscriber sub);

    /**
     * Removes a subscriber/observer from the cache.
     * @param sub previously added subscriber to be removed
     */
    public void removeSubscriber(CacheSubscriber sub);

    /**
     * Loads multiple elements into the cache. Equivalent of multiple <code>updateCache</code> calls.
     * Can be used to import a previously exported cache or populate a cache after a synchronization call.
     * @param cacheElements array of objects to added to the cache
     */
    public void loadCache(Object[] cacheElements);

    /**
     * Retrieves the contents of the cache. This can be used to export the contents of the cache
     * to a persistence layer, or to simply retrieve all cache members.
     * @return array of Objects consisting of all objects within the cache
     */
    public Object[] retrieveCache();

    /**
     * Removes all elements from the cache.
     */
    public void purgeCache();

    /**
     * Remove an object from the cache.
     * @param cacheElement object to remove from the cache
     */
    public void remove(Object cacheElement);

    /**
     * Removes all objects in a sub-group within a specific key set.
     * Note: Non-primary key sets <i>may not</i> contain all cache elements.
     * @param generator the generator used to create the original key
     * @param groupValue used to determine which sub-group within the map to return
     * @return an array of matching objects or <code>null</code>
     */
    public Object[] removeAllInGroup(CacheKeyGenerator generator, Object groupValue);

    /**
     * Adds/updates the cache with the new object, updating <b>all</b> subscribers.
     * @param cacheElement new/updated object to insert into the cache
     * @see #updateCache(Object, CacheSubscriber)
     */
    public void updateCache(Object cacheElement);

    /**
     * Updates/inserts an object into the cache and notifies cache subscribers of the change.
     * Will skip <code>ignoreSubscriber</code> from the notification chain if not <code>null</code>.
     * This can be used to prevent subscription infinite loops.
     * @param cacheElement new/updated object to insert into the cache
     * @param ignoreSubscriber if not <code>null</code>, will <b>not</b> be notified of the update
     * @see #updateCache(Object)
     */
    public void updateCache(Object cacheElement, CacheSubscriber ignoreSubscriber);

    /**
     * Finds an object in the cache using a specific key/key value.
     * @param generator the generator used to create the original key
     * @param keyValue key used to retrieve object from the cache
     * @return the found object or <code>null</code>
     * @see #find(CacheKeyGenerator, Object, Object)
     */
    public Object find(CacheKeyGenerator generator, Object keyValue);

    /**
     * Finds an object in the cache using a specific key/key value within a specific sub-group.
     * @param generator the generator used to create the original key
     * @param groupValue used to determine which group within the map to search
     * @param keyValue key used to retrieve object from the cache
     * @return the found object or <code>null</code>
     * @see #find(CacheKeyGenerator, Object)
     */
    public Object find(CacheKeyGenerator generator, Object groupValue, Object keyValue);

    /**
     * Retrieves all objects in a sub-group within a specific key set.
     * Note: Non-primary key sets <i>may not</i> contain all cache elements.
     * @param generator the generator used to create the original key
     * @param groupValue used to determine which sub-group within the map to return
     * @return an array of matching objects or <code>null</code>
     */
    public Object[] findAllInGroup(CacheKeyGenerator generator, Object groupValue);

    /**
     * Retrieves all objects within a specific key set. Note: Non-primary key sets <i>may not</i> contain all cache elements.
     * @param generator the generator used to create the original key
     * @return an array of objects or <code>null</code>.
     * @see #findAllInGroup
     */
    public Object[] findAll(CacheKeyGenerator generator);


    /**
     * Determines if any data resides in the cache.
     * @return <code>true</code> if there is data in the cache, <code>false</code> if not
     * @see #hasData(CacheKeyGenerator)
     * @see #hasData(CacheKeyGenerator, Object)
     */
    public boolean hasData();

    /**
     * Determines if any data resides in the cache generated by a specific key generator.
     * Note: Secondary keys do not necessarily contain all the data contained by the primary key.
     * @return <code>true</code> if data was found, <code>false</code> if not
     * @see #hasData(CacheKeyGenerator)
     * @see #hasData(CacheKeyGenerator, Object)
     */
    public boolean hasData(CacheKeyGenerator generator);

    /**
     * Determines if any data exists in the cache for the specified group.
     * Note: Secondary keys do not necessarily contain all the data contained by the primary key.
     * @return <code>true</code> if data was found, <code>false</code> if not
     * @see #hasData(CacheKeyGenerator)
     * @see #hasData(CacheKeyGenerator, Object)
     */
    public boolean groupHasData(CacheKeyGenerator generator, Object groupValue);
}
