package com.cboe.application.cache;

// Java classes
import java.util.*;

import javax.cache.CacheException;
import com.cboe.application.jcache.Cache;

// CAS interfaces
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;



/**
 * Reference implementation of the {@link Cache} interface.
 * <p>
 * This class provides a complete and efficient implementation of the {@link Cache} interface. It provides for
 * caching of generic Objects with multiple keys via the use of {@link CacheKeyGenerator} objects that
 * create keys that are used by the internal {@link HashMap}s.
 * <p>
 * BaseCache is designed to support synchronization to a non-local data source by implementing the
 * {@link Synchable} interface and updating its synchronization ID by querying inserted objects using
 * a {@link CacheElementQuery} object. Inserted objects are queried to provide a synchronization ID and
 * if that ID is larger than any previously seen ID, it replaces the cache's synchronization ID. A synchronization
 * layer can use that ID to limit the amount of data being retrieved to only those objects with
 * synchronization IDs greater than those within the cache. Currently, no synchronization occurs, so a default
 * implementation of the query object returns a zero value.
 * <p>
 * Synchronization between local caches (for instance, product and session product caches) is implemented through
 * the use of {@link CacheSubscriber} objects that reconcile changes in one cache with one or more additional caches.
 * As many synchronizations are bi-directional, care must be used to prevent infinite loops
 * (product-->session product-->product-->etc.). This can be accomplished through the use of the
 * <code>ignoreSubscriber</code> parameter in <code>updateCache</code>.
 * <p>
 * BaseCache should not be instantiated directly. Use of the {@link CacheFactory} object is highly recommended, as
 * it provides a central location for instantiation, initialization and synchronization of caches, as well as forcing
 * a single instance of any supported cache type (product, class, session strategy, etc.)
 * <p>
 * It is important to note the primary key generator for the cache must <b>never</b> return <code>null</code> from
 * a <code>generateKey</code> call, as the primary key must accept any element that could possibly be accepted
 * by secondary keys. The reason for this restriction is that the primary key is used to import and export
 * the entire contents of the cache, and thus must reflect <b>all</b> elements. Secondary keys are not required
 * to accept all elements, and may, in fact, be <b>required</b> to reject some if it cannot generate a unique
 * key for that object.
 * <p>
 * For similar reasons, the primary key generator <b>cannot</b> provide a group-by relationship - it must be a simple
 * unique key lookup. Group-by lookups, being Maps of Maps, are inherently slower to convert than simple Maps, and
 * therefore are prohibitively costly for cache export duties. Additionally, group-by maps can contain multiple copies
 * of the same key (such groups are <i>inclusive</i>: key 1 can exist in group 1 <b>and</b> group 2), which would also
 * cause performance issues during export.
 * @author Brian Erst
 * @see Cache
 * @see CacheFactory
 * @version 2001.1.12
 */




public class BaseCache implements com.cboe.interfaces.application.Cache
{
    Map allElements;                    // Primary map - Key: primaryGenerator.generateKey(obj), obj
    Map allKeyedElements;               // Map of maps - Key: CacheKeyGenerator, Value: Map of CacheKeyGenerator.generateKey(obj), obj
    Set allSubscribers;                 // Set of cache update subscribers - called after each cache update
    CacheKeyGenerator primaryGenerator; // Generates keys for the allElements map
    CacheElementQuery query;            // Allows elements in the cache to be queried by the cache
    long syncID;
    Class elementClass;
    
    String primaryCacheName=null;
    boolean useJCache=false;
    String cachingDirectory=null;
    String cacheType=null;
    
    /**
     * Helper for public constructors.
     */
    private BaseCache(String cacheType, String name,  boolean useJCache,String cachingDirectory, CacheKeyGenerator generator, Object sampleElement)
    {
        this.cacheType=cacheType;
        if(name==null && generator !=null)
        {
            String className = generator.getClass().getName();
            String generatorString = generator.toString();
            StringBuilder cacheName = new StringBuilder(className.length()+generatorString.length()+1);
            cacheName.append(className).append(".").append(generatorString);
            primaryCacheName= cacheName.toString();
        }
        else
        {
            primaryCacheName = name;

        }
        this.useJCache = useJCache;
        this.cachingDirectory = cachingDirectory;
        allElements = CacheFactory.createJCache(primaryCacheName,useJCache,true);
        allKeyedElements = new HashMap();
        allSubscribers = new HashSet();
        primaryGenerator = null;
        query = null;
        syncID = 0;
        elementClass = sampleElement.getClass();
        primaryGenerator = generator;
    }
    
    public BaseCache(String cacheType,String name,String cachingDirectory, CacheKeyGenerator generator, Object sampleElement)
    {
        this(cacheType,name,true,cachingDirectory,generator,sampleElement);
    }
    public BaseCache(String name,String cachingDirectory, CacheKeyGenerator generator, Object sampleElement)
    {
        this(null,name,true,cachingDirectory,generator,sampleElement);
    }
    /**
     * Constructs a cache with a default primary cache key generator.
     * 
     * @param generator the cache will use this as the primary cache key generator
     */
    public BaseCache(CacheKeyGenerator generator, Object sampleElement)
    {
       this((String)null,(String)null,false,null,generator,sampleElement);
    }

    
    /**
     * Sets the primary cache key generator for the cache. Any existing primary generator will be
     * discarded and the primary map will be rebuilt using the new key generator.
     * @param generator the cache will use this as the primary cache key generator
     */
    public synchronized void setPrimaryGenerator(CacheKeyGenerator generator)
    {
        if ((primaryGenerator == null) || (!primaryGenerator.equals(generator)))
        {
            if ((generator != null) && (allElements.size() != 0))
            {
                // Create a map for the new generator
                Map newMap=null;
                newMap = CacheFactory.createJCache(primaryCacheName,useJCache,true);
                // Populate the new map with all the existing elements
                Iterator it = allElements.values().iterator();
                while (it.hasNext())
                {
                    putInMap(newMap, generator, it.next());
                }

                // Replace the primary map
                allElements = newMap;
            }
            primaryGenerator = generator;
        }
    }

    
    
   

    /**
     * Returns the highest element synchronization ID inserted into the cache.
     * @return the highest sync ID retrieved from elements inserted into the cache
     */
    public long getSyncID() {return syncID;}

    /**
     * Sets the element query object. Element query objects are currently used to retrieve the
     * element's synchronization ID, but the {@CacheElementQuery} interface could be extended in the
     * future to provide additional information to the cache about its elements.
     * @param __query the new element query object
     */
    public void setElementQuery(CacheElementQuery __query)
    {
        query = __query;
    }

    // Gets the query object, creating a default one if none has been provided
    private CacheElementQuery getQuery()
    {
        if (query == null)
        {
            query = new CacheElementQuery()
                    {
                        // Simple implementation - no sync IDs supported!
                        public long getSyncIDFromObject(Object obj) {return 0;}
                    };
        }

        return query;
    }

    // Retrieves a Map from within another Map
    private Map findMap(Object key, Map baseMap)
    {
        return (Map)baseMap.get(key);
    }

    // Checks to see if a given generator is the primary generator for the cache
    private boolean isPrimaryKey(CacheKeyGenerator generator)
    {
        return generator.equals(primaryGenerator);
    }

//  Creates a new key map (or retrieves the existing one). A "factory" method for key maps.
    private Map makeMap(Object key, Map baseMap)
    {
        // Let's see if the map already exists - if it does, use it
        Map map = findMap(key, baseMap);

        // Nope? Then make it
        if (map == null)
        {
            map = new HashMap();
            baseMap.put(key, map);
        }

        return map;
    }


    // Places an element in a key map. The key is generated by the generator. Group-by functionality is handled.
    private void putInMap(Map map, CacheKeyGenerator generator, Object element)
    {
        Object mapKey = generator.generateKey(element);

        // Did the key generator indicate object should NOT be cached via this particular key?
        if (mapKey == null)
        {
            return;
        }

        // We'll check if this a "collator" key ("group by" something)
        Iterator groupIter = generator.groupIterator(element);
        if (groupIter.hasNext())
        {
            // We handle both "inclusive" (element can be inside multiple groups) and "exclusive"
            // (element can only reside within a single group) grouping. Loop until no more groups
            // can be found
            while (groupIter.hasNext())
            {
                // Find or create the submap (the map of all elements belonging to the group),
                // and add the element to it using the key
                makeMap(groupIter.next(), map).put(mapKey, element);
            }
        }
        else
            map.put(mapKey, element);
    }

    private void removeFromMap(Map map, CacheKeyGenerator generator, Object element)
    {
        Object mapKey = generator.generateKey(element);

        // Did the key generator indicate object would NOT be cached via this particular key?
        if (mapKey == null)
        {
            return;
        }

        // We'll check if this a "collator" key ("group by" something)
        Iterator groupIter = generator.groupIterator(element);
        if (groupIter.hasNext())
        {
            // We handle both "inclusive" (element can be inside multiple groups) and "exclusive"
            // (element can only reside within a single group) grouping. Loop until no more groups
            // can be found
            while (groupIter.hasNext())
            {
                // Find or create the submap (the map of all elements belonging to the group),
                // and remove the element from it using the key
                makeMap(groupIter.next(), map).remove(mapKey);
            }
        }
        else
            map.remove(mapKey);
    }

    // Converts a Map into an array of objects, handling null Maps gracefully.
    private Object[] mapToDataArray(Map map)
    {
        if ((map == null) || (map.values().size() == 0))
        {
            return (Object[]) java.lang.reflect.Array.newInstance(elementClass, 0);
        }
        else
        {
            // NOTE: OK, this one is a little tricky. We want to return an array of elements, not generic Objects.
            // If we just call toArray(), it will create an array of java.lang.Object's, as opposed to an array
            // of the data type that was inserted into the cache. In order to create an array of the real data type,
            // we have to use the Reflection library to create an array based on the class of one of the cache's data
            // elements. Hence, grab an element's class use it to create an array, which we hand off to
            // toArray(Object[]), which will copy it properly.
            return map.values().toArray((Object[])java.lang.reflect.Array.newInstance(elementClass, map.values().size()));
        }
    }

    /**
     * Adds a secondary key map to the cache associated with this generator. Secondary keys can exclude
     * elements if needed (due, perhaps, to non-unique keys) and provide inclusive or exclusive grouping.
     * @param generator tells the cache to create a map to be associated with this generator
     */
    public synchronized void addGenerator( CacheKeyGenerator generator)
    {
        // Do the get to prevent expensive reentry into Map populator
        Map keyMap = findMap(generator, allKeyedElements);

        // If the map for this type of generator does not exist, create a new one
        if (keyMap == null)
        {
            // Create a map for this type of generator
            keyMap = makeMap(generator, allKeyedElements);

            // Populate the new map with all the existing elements
            Iterator it = allElements.values().iterator();
            while (it.hasNext())
            {
                Object o = it.next();
                putInMap(keyMap, generator, o);
            }
        }
    }

    /**
     * Removes a secondary key map from the cache. The map is garbage collected.
     * @param generator tells the cache to remove the map associated with this generator
     */
    public synchronized void removeGenerator(CacheKeyGenerator generator)
    {
        allKeyedElements.remove(generator);
    }

    /**
     * Adds a subscriber/observer to the cache. The subscriber will be notified whenever an
     * element is added to or updated in the cache.
     * @param sub called whenever update/insertion occurs
     */
    public synchronized void addSubscriber(CacheSubscriber sub)
    {
        allSubscribers.add(sub);
    }

    /**
     * Removes a subscriber/observer from the cache.
     * @param sub previously added subscriber to be removed
     */
    public synchronized void removeSubscriber(CacheSubscriber sub)
    {
        allSubscribers.remove(sub);
    }

    /**
     * Loads multiple elements into the cache. Equivalent of multiple <code>updateCache</code> calls.
     * Can be used to import a previously exported cache or populate a cache after a synchronization call.
     * @param cacheElements array of objects to added to the cache
     */
    public synchronized void loadCache(Object[] cacheElements)
    {
        if (cacheElements == null)
        {
            return;
        }

        for (int i = 0; i < cacheElements.length; i++)
        {
            updateCache(cacheElements[i]);
        }
    }

    /**
     * Retrieves the contents of the primary cache map. This can be used to export the contents of the cache
     * to a persistence layer, or to simply retrieve all cache members.
     * @return array of objects consisting of all objects within the cache
     */
    public synchronized Object[] retrieveCache()
    {
        return mapToDataArray(allElements);
    }

    /**
     * Removes all elements from the cache. Handles removal of the secondary maps without wiping out
     * the generators that are used as the keys into the secondary maps.
     */
    public synchronized void purgeCache()
    {
        StringBuilder purging = new StringBuilder(primaryCacheName.length()+25);
        purging.append(" Purge invoked, cache=").append(primaryCacheName);
        Log.information(purging.toString());
        allElements.clear();
        Iterator it = allKeyedElements.values().iterator();
        while (it.hasNext())
        {
            Map keyMap = (Map) it.next();
            keyMap.clear();
        }
    }
    
   

    /**
     * Updates/inserts an object into the cache. Updates the cache's synchronization ID and notifies
     * cache subscribers of the change. Will skip <code>ignoreSubscriber</code> from the notification
     * chain if not <code>null</code>. This can be used to prevent subscription infinite loops.
     * @param cacheElement new/updated object to insert into the cache
     * @param ignoreSubscriber if not <code>null</code>, will <b>not</b> be notified of the update
     * @see #updateCache(Object)
     */
    public synchronized void updateCache(Object cacheElement, CacheSubscriber ignoreSubscriber)
    {
        Object primaryKey = primaryGenerator.generateKey(cacheElement);
        if (primaryKey == null)
            return;
        else {
            allElements.put(primaryKey, cacheElement);

        }
        Iterator it = allKeyedElements.keySet().iterator();
        while (it.hasNext())
        {
            CacheKeyGenerator generator = (CacheKeyGenerator) it.next();

            // Find the appropriate map and put the element into it
            putInMap(findMap(generator, allKeyedElements), generator, cacheElement);
        }

        // The cache syncID should reflect the maximum syncID added to the cache
        syncID = Math.max(getSyncID(), getQuery().getSyncIDFromObject(cacheElement));

        // Cache value was modified - notify anyone who cares
        it = allSubscribers.iterator();
        while (it.hasNext())
        {
            CacheSubscriber sub = (CacheSubscriber) it.next();

            // Note: We are doing reference equivalence (point to the SAME object), so use a
            // factory object to create the subscribers!
            if (sub != ignoreSubscriber)
            {
                sub.update(cacheElement);
            }
        }
    }

    /**
     * Adds/updates the cache with the new object, updating <b>all</b> subscribers.
     * @param cacheElement new/updated object to insert into the cache
     * @see #updateCache(Object, CacheSubscriber)
     */
    public synchronized void updateCache(Object cacheElement)
    {
        updateCache(cacheElement, null);
    }

    /**
     * Removes the object from the cache.
     * @param cacheElement object to remove from the cache
     */
    public synchronized void remove(Object cacheElement)
    {
        Object primaryKey = primaryGenerator.generateKey(cacheElement);
        if (primaryKey == null)
        {
            return;
        }
        else
        {
            allElements.remove(primaryKey);
        }
        Iterator it = allKeyedElements.keySet().iterator();
        while (it.hasNext())
        {
            CacheKeyGenerator generator = (CacheKeyGenerator) it.next();

            // Find the appropriate map and put the element into it
            removeFromMap(findMap(generator, allKeyedElements), generator, cacheElement);
        }
    }

    /**
     * Remove all objects in a sub-group within a specific key map. Remember, not all key maps
     * contain all cache elements!
     * @param generator the generator used to create the original key
     * @param groupValue used to determine which sub-group within the map to return
     * @return an array of matching objects or <code>null</code>.
     */
    public synchronized Object[] removeAllInGroup(CacheKeyGenerator generator, Object groupValue)
    {
        Object[] group = findAllInGroup(generator, groupValue);

        for (int i = 0; i < group.length; i++)
        {
            remove(group[i]);
        }
        
        return group;
    }


    /**
     * Finds an object in the cache using a specific key/key value.
     * @param generator the generator used to create the original key
     * @param keyValue used to match against a key in the map
     * @return the found object or <code>null</code>
     * @see #find(CacheKeyGenerator, Object, Object)
     */
    public synchronized Object find(CacheKeyGenerator generator, Object keyValue)
    {
        return find(generator, null, keyValue);
    }

    /**
     * Finds an object in the cache using a specific key/key value within a specific sub-group.
     * @param generator the generator used to create the original key
     * @param groupValue used to determine which group within the map to search
     * @param keyValue used to match against a key in the map
     * @return the found object or <code>null</code>
     * @see #find(CacheKeyGenerator, Object)
     */
    public synchronized Object find(CacheKeyGenerator generator, Object groupValue, Object keyValue)
    {
        // See if this is the primary generator, if it is, use the primary map
        if (isPrimaryKey(generator))
        {
            return allElements.get(keyValue);
        }
        else
        {
            // Find the proper secondary map
            Map map = findMap(generator, allKeyedElements);

            if (map != null)
            {
                // Is this a map of maps?
                if (generator.doesGroup())
                {
                    Map subMap = findMap(groupValue, map);
                    if (subMap != null)
                    {
                        return subMap.get(keyValue);
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    return map.get(keyValue);
                }
            }
            else
            {
                // Do a brute force search
                Iterator it = allElements.keySet().iterator();
                while (it.hasNext())
                {
                    Object element = it.next();

                    // If the key value generated using the element is the same as the key value
                    // passed in, we've found the correct element
                    if (generator.generateKey(element).equals(keyValue))
                    {
                        return element;
                    }
                }

                // Not found!
                return null;
            }
        }
    }

    /**
     * Retrieves all objects in a sub-group within a specific key map. Remember, not all key maps
     * contain all cache elements!
     * @param generator the generator used to create the original key
     * @param groupValue used to determine which sub-group within the map to return
     * @return an array of matching objects or <code>null</code>.
     */
    public synchronized Object[] findAllInGroup(CacheKeyGenerator generator, Object groupValue)
    {
        // Find the proper secondary map
        Map map = findMap(generator, allKeyedElements);

        if (map != null)
        {
            // Is this a map of maps?
            if (generator.doesGroup())
            {
                // Grab the map that corresponds to the group and make it a data array
                return mapToDataArray(findMap(groupValue, map));
            }
        }

        return com.cboe.client.util.CollectionHelper.EMPTY_Object_ARRAY;
    }

    /**
     * Retrieves all objects within a specific key map. Remember, not all key maps
     * contain all cache elements!
     * @param generator the generator used to create the original key
     * @return an array of objects or <code>null</code>.
     * @see #findAllInGroup
     */
    public synchronized Object[] findAll(CacheKeyGenerator generator)
    {
        Map map;

        // Get the appropriate map
        if (isPrimaryKey(generator))
        {
            map = allElements;
        }
        else
        {
            map = findMap(generator, allKeyedElements);
        }

        // Assuming we found it, is this a map of maps?
        if ((map != null) && (generator.doesGroup()))
        {
            // This map will contain all the elements of the submaps
            Map groupMap = new HashMap();

            // This will accumulate all the submaps into a master map
            Iterator it = map.values().iterator();
            while (it.hasNext())
            {
                Map subMap = (Map) it.next();
                groupMap.putAll(subMap);
            }

            map = groupMap;
        }

        return mapToDataArray(map);
    }

    /**
     * Determines if any data resides in the cache.
     * @return <code>true</code> if there is data in the cache, <code>false</code> if not
     * @see #hasData(CacheKeyGenerator)
     * @see #groupHasData(CacheKeyGenerator, Object)
     */
    public boolean hasData()
    {
        return (allElements==null) ? false : (allElements.size() != 0);
    }

    /**
     * Determines if any data resides in the cache generated by a specific key generator.
     * Note: Secondary keys do not necessarily contain all the data contained by the primary key.
     * @return <code>true</code> if data was found, <code>false</code> if not
     * @see #hasData(CacheKeyGenerator)
     * @see #groupHasData(CacheKeyGenerator, Object)
     */
    public boolean hasData(CacheKeyGenerator generator)
    {
        Map map;

        // Get the appropriate map
        if (isPrimaryKey(generator))
        {
            map = allElements;
        }
        else
        {
            map = findMap(generator, allKeyedElements);
        }

        if (map == null)
        {
            return false;
        }

        if (generator.doesGroup())
        {
            Iterator it = map.values().iterator();
            while (it.hasNext())
            {
                map = (Map) it.next();
                if (map.size() != 0)
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            return map.size() != 0;
        }
    }

    /**
     * Determines if any data exists in the cache for the specified group.
     * Note: Secondary keys do not necessarily contain all the data contained by the primary key.
     * @return <code>true</code> if data was found, <code>false</code> if not
     * @see #hasData(CacheKeyGenerator)
     * @see #groupHasData(CacheKeyGenerator, Object)
     */
    public boolean groupHasData(CacheKeyGenerator generator, Object groupValue)
    {
        // Find the proper secondary map
        Map map = findMap(generator, allKeyedElements);

        if (map != null)
        {
            // Is this a map of maps?
            if (generator.doesGroup())
            {
                map = findMap(groupValue, map);
                return ((map != null) && (map.size() != 0));
            }
        }

        return false;
    }

    // New methods...
    public void flushAll()
    {
        if(allElements != null && allElements instanceof Cache) {
            try
            {
                ((Cache)allElements).flush();
            }
            catch (CacheException e)
            {
                Log.exception("Error in flushing name="+primaryCacheName, e);
            }
        }
        
    }
    
    public int size() {
        if (allElements == null) {
            return 0;
        }
        else {
            return allElements.size();
        }
    }
   
    public synchronized void shutdown()
    {
        if(allElements != null && allElements instanceof Cache) {           
          ((Cache)allElements).shutdown();        
        }
    }
    
    public synchronized void refresh() throws Exception
    {
        if(allElements != null && allElements instanceof Cache) {           
        
            ((Cache)allElements).refresh();
           
        }
    }

}
