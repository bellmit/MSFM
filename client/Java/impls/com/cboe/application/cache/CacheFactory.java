package com.cboe.application.cache;

import com.cboe.application.jcache.JCacheManager;
import com.cboe.application.product.adapter.TimedClassKey;
import com.cboe.application.product.cache.ProductCacheKeyFactory;
import com.cboe.application.tradingSession.cache.TradingSessionCacheKeyFactory;
import com.cboe.domain.util.ClientProductStructBuilder;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.Cache;
import com.cboe.interfaces.application.CacheKeyGenerator;
import com.cboe.interfaces.application.CacheSubscriber;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.cache.CacheEntry;
import javax.cache.CacheException;
import com.cboe.exceptions.*;
import com.cboe.client.util.Product;

import static java.lang.System.getProperty;



/**
 * A factory class for creating {@link Cache}s for the CAS.
 * <p>
 * This class provides a central factory for creating various types of caches used in the CAS.
 * Currently, product-based (type, class, product and strategy) and session-based (session-oriented
 * versions of the product objects) caches are created here.
 * <p>
 * The caches created here are singletons (one copy per application). If a specific cache has already
 * been created by a different object/thread, the factory will return a reference to that previously
 * created cache - it will not instantiate a new one. In order to ensure a single copy of any cache,
 * all calls to the factory are synchronized for thread-safety. NOTE: Some types of caches (namely session-based
 * ones) do create multiple copies of the same <i>type</i> of cache, but they are singletons in the sense
 * that only one copy of that "sub-typed" cache will be created. For instance, although many session product
 * caches may be created, only one such cache will be created <i>per session</i>.
 * <p>
 * As part of cache instantiation, all the keys used to access data within the cache will also be
 * instantiated and registered with the cache. This currently requires access to <@link ProductCacheKeyFactory>
 * and <@link TradingSessionCacheKeyFactory>. If any new keys are added in the future, instantiating and
 * registering those keys in the appropriate CacheFactory get method is recommended.
 * <p>
 * This class also instantiates and registers <@link CacheSubscriber> objects to perform synchronization
 * duties between two related type of cache. These "bridge objects" subscribe to changes in one cache
 * and update a second cache based on the data received. For instance, a change to the product cache means
 * that the session product cache, which may contain a session product which in turn contains a reference to
 * that product, is updated so that only one copy of the object exists within the JVM. This synchronization
 * is typically bi-directional (updates to a session product means a likely change to the product cache), so
 * two different bridge objects are created, one registered with each cache. Care must be taken to prevent
 * an infinite update loop (product-->session product-->product-->etc.). This can be done through the use
 * of the <code>ignoreSubscriber</code> parameter in the <code>updateCache</code> call. Since the
 * product-related caches are less dependent on session-related caches, the creation and registering of
 * synchronization objects is only done when session-related caches are instantiated.
 * <p>
 * @author Brian Erst
 * @author Gijo Joseph
 * @see Cache
 * @see ProductCacheKeyFactory
 * @see TradingSessionCacheKeyFactory
 * @see FirmCacheKeyFactory
 * @version 2009.03.31
 */

public abstract class CacheFactory
{
    private   static String productCacheName = "ProductCache";
    private   static String productClassCacheName = "ProductClassCache";
    private static Cache typeCache = null;
    private static volatile Cache classCache = null;
    private static volatile BaseCache productCache = null;
    private static volatile Cache strategyCache = null;
    private static volatile Map productClassCache=null;

    private static volatile Cache activeFirmCache = null;
    private static volatile Cache inactiveFirmCache = null;

    private static final String CACHE_TYPE     = "TYPE";
    private static final String CACHE_CLASS    = "CLASS";
    private static final String CACHE_PRODUCT  = "PRODUCT";
    private static final String CACHE_STRATEGY = "STRATEGY";

    private static Map<String, Map> sessionsMap = new HashMap<String, Map>();
    private static String cachingDirectory=null;
    private static String cacheType=null;
    private static int maxElementsInMemory=2000000;
    private static ConcurrentHashMap<Integer,ProductStruct> productKeyMap;
    private static HashMap<Product, ProductStruct> productNameMap;

    static
    {
        typeCache = new BaseCache(ProductCacheKeyFactory.getPrimaryTypeKey(), new ProductTypeStruct());
    }

    // Accessor for the sessionMap map - creates it if needed
    private static Map<String, Map> getSessionsMap()
    {
        return sessionsMap;
    }
// poor way to search .. exposing cache access directly to caller for better performance
    public static ProductStruct getProduct(Product searchItem)
    {
        return productNameMap.get(searchItem);

    }
// poor way to search .. exposing cache access directly to caller for better performance
    public static ProductStruct getProduct(int productKey)
    {
        return productKeyMap.get(productKey);

    }

    // Retrieves a map from a map of maps. If the sub-map does not exist, it is instantiated
    // and added to the parent map.
    private static synchronized Map getMap(String key, Map<String, Map> parentMap)
    {
        Map map = parentMap.get(key);

        // Did we find that sub-map?
        if (map == null)
        {
            // Nope. Create a new one and add it to the map of maps.
            map = new HashMap<String, Map>();
            parentMap.put(key, map);
        }
        return map;
    }

    // Creates a session-related cache, and places it into the appropriate map of caches for that session
    private static synchronized Cache makeSessionCache(String sessionName, String cacheType, CacheKeyGenerator generator, Object sampleElement)
    {
        // Find or create the hash of caches for this session
        Map caches = getMap(sessionName, getSessionsMap());

        Cache cache = new BaseCache(generator, sampleElement);
        caches.put(cacheType, cache);
        return cache;
    }

    // Attempts to retrieve the cache for that session and type
    private static synchronized Cache getSessionCache(String sessionName, String cacheType)
    {
        // Find or create the hash of caches for this session
        Map caches = getMap(sessionName, getSessionsMap());

        // Find the cache for this cache type
        return (Cache) caches.get(cacheType);
    }

  // Public methods

    /**
     * Retrieves the list of sessions that we currently have caches for.
     * @return List of session names (String)
     */
    public static synchronized List getSessionNames()
    {
        return new ArrayList(getSessionsMap().keySet());
    }

    /**
     * Find or create a cache for product types.
     * @return cache of {@link ProductTypeStruct} objects
     */
    public static Cache getProductTypeCache()
    {
        return typeCache;
    }

    public static Cache getClassCache()
    {
    	Cache cc = classCache;
    	if (cc == null)
    	{
    		cc = createClassCache();
    	}
    	return cc;
    }    
    /**
     * Find or create a cache for classes.
     * @return cache of {@link ClassStruct} objects
     */
    private static synchronized Cache createClassCache()
    {
        if (classCache == null)
        {
            classCache = new BaseCache(ProductCacheKeyFactory.getPrimaryClassKey(), new ClassStruct());
            classCache.addGenerator(ProductCacheKeyFactory.getClassGroupByTypeKey());
        }
        return classCache;
    }
    
    public static Cache getProductCache()
    {
        return getProductCache(false);
    }
    
    public static Cache getProductCache(boolean purge)
    {
    	Cache pc = productCache;
    	if (pc == null)
    	{
    		pc = createProductCache(purge);
    	}
    	return pc;
    }
    /**
     * Find or create a cache for products.
     * @return cache of {@link ProductStruct} objects
     */
    private static synchronized Cache createProductCache(boolean purge)
    {
        if (productCache == null)
        {
             productCache = new BaseCache(ProductCacheKeyFactory.getPrimaryProductKey(), new ProductStruct());

            if (purge) 
            {
                productCache.purgeCache();
            }
            else 
            {
                try
                {
                    ((BaseCache)productCache).refresh();
                }
                catch (Exception e)
                {
                    Log.exception("Error in opening product cache. Ignoring exception", e);
                    productCache.purgeCache();
                }
            }
            StringBuilder refreshed = new StringBuilder(45);
            refreshed.append("Product cache refreshed, size=")
                     .append(((BaseCache)CacheFactory.getProductCache()).allElements.size());
            Log.information(refreshed.toString());
            productCache.addGenerator(ProductCacheKeyFactory.getProductNameKey());
            productCache.addGenerator(ProductCacheKeyFactory.getProductGroupByClassKey());
          //  productCache.addSubscriber(SubscriberFactory.productCacheToProductMap);

           productNameMap = (HashMap<Product, ProductStruct>)productCache.allKeyedElements.get(ProductCacheKeyFactory.getProductNameKey());
           productKeyMap =  (ConcurrentHashMap<Integer, ProductStruct>)productCache.allElements; 
        }
        return productCache;
    }

	public static Map getProductClassCache() throws SystemException
	{
		Map pcc = productClassCache;
		if (pcc == null)
		{
			pcc = createProductClassCache();			
		}
		return pcc;
	}
    
    /**
     * Find or create a cache for loaded classes.
     * @return cache of {@link ProductStruct} objects
     * @throws CacheException 
     */
    private static synchronized Map createProductClassCache() throws SystemException
    {
        if (productClassCache == null )
        {
            try
            {
                productClassCache = createJCache(productClassCacheName, true,true);
                if (productClassCache instanceof com.cboe.application.jcache.Cache)
                {
                    ((com.cboe.application.jcache.Cache)productClassCache).refresh();
                }
            }
            catch (CacheException e)
            {
                Log.exception("Error in creating productClassCache. Ignoring exception", e);
                productClassCache.clear();  // contents could be inconsistent, start empty
            }
        }
        return productClassCache;
    }

    /** Create a JCache or a Map. This depends on useJCache, XML parameters and
     * possibly other factors. Because the return might not be a JCache, it's
     * incorrect for a caller to cast the returned object; instead call service
     * methods in this class to perform Cache-specific functions.
     * @param name Name of cache (useful for persistence).
     * @param useJCache If false, create an in-memory Map.
     * @param persistent Allow cache to be saved to disk.
     * @return object which might be a jcache.Cache, or might not.
     * @see #getRecentKeys, #flushCache
     */
    public synchronized static Map createJCache(String name, boolean useJCache, boolean persistent)
    {
        Map cache = null; 
    	if (useJCache && !com.cboe.application.jcache.Cache.HASHMAP_CACHE.equalsIgnoreCase(cacheType))
    	{
            Map map = new HashMap();
            if (cacheType != null)
            {
                map.put(com.cboe.application.jcache.Cache.CACHE_TYPE, cacheType);
            }
            map.put(com.cboe.application.jcache.Cache.CACHE_NAME, name);
            map.put(com.cboe.application.jcache.Cache.CACHE_FOLDER, cachingDirectory);
            map.put(com.cboe.application.jcache.Cache.CACHE_MAX_ELEMENTS_IN_MEMORY, Integer.toString(maxElementsInMemory));
            if (persistent)
            {
                map.put(com.cboe.application.jcache.Cache.CACHE_PERSISTENT, "true");
            }
            else 
            {
                map.put(com.cboe.application.jcache.Cache.CACHE_PERSISTENT, "false");
            }
        	StringBuilder cacheDetails = new StringBuilder();
        	cacheDetails.append("Cache Details: CacheName=");
        	cacheDetails.append(name);
        	cacheDetails.append(" : CacheDirectory=");
        	cacheDetails.append(cachingDirectory);
        	cacheDetails.append(" : cacheType=");
        	cacheDetails.append(cacheType);
        	cacheDetails.append(" : cachePersistent=");
        	cacheDetails.append(persistent);
        	cacheDetails.append(" : maxElementsInMemory=");
        	cacheDetails.append(maxElementsInMemory);            
            try
            {
                cache = JCacheManager.instance().getCacheFactory().createCache(map);
                String details = cacheDetails.toString();
                StringBuilder created = new StringBuilder(details.length()+30);
                created.append("Cache created successfully! ").append(details);
                Log.information(created.toString());
            }
            catch (CacheException e)
            {
                Log.alarm("Error creating cache! " + cacheDetails.toString());
                Log.exception("Exception creating cache " + name, e);
            }
    	}
        if (cache == null)
        {
            cache = new ConcurrentHashMap(100, (float)0.75, 1);
            StringBuilder created = new StringBuilder(name.length()+35);
            created.append("Created an in memory cache for ").append(name);
            Log.information(created.toString());
        }
        return cache;
    }

    /** Get keys from a Map.
     * For a javax.cache.Cache, get the most recently created keys.
     * @param map Map whose keys we want to get. This object should have been
     *    created initially by {@link #createJCache}.
     * @param maxKeys Maximum number of keys we want.
     * @return keys from map (0 <= keys.size() <= maxKeys).
     */
    public static Set getRecentKeys(Map map, int maxKeys)
    {
        Set keys;
        Iterator it;
        int over = map.size() - maxKeys;
        if (! (map instanceof javax.cache.Cache))
        {
            // We have no special knowledge of this map. Sort its keys and
            // remove the first few, if necessary, to make a list no longer
            // than maxKeys.
            keys = new TreeSet(map.keySet());
            it = keys.iterator();
            while (over-- > 0)
            {
                it.next();
                it.remove();
            }
            return keys;
        }

        // This is a JCache so we have a creation time for each key. Sort keys
        // by creation time, and remove the earliest if necessary.
        javax.cache.Cache cache = (javax.cache.Cache) map;
        Set<TimedClassKey> timedKeys = new TreeSet<TimedClassKey>();
        for (Object key : cache.keySet())
        {
            CacheEntry entry = cache.getCacheEntry(key);
            timedKeys.add(new TimedClassKey(entry.getCreationTime(), (Integer) key));
        }

        it = timedKeys.iterator();
        while (over-- > 0)
        {
            it.next();
            it.remove();
        }

        // Now make a set of keys without timestamp, to return to caller
        keys = new TreeSet();
        for (TimedClassKey key : timedKeys)
        {
            keys.add(key.getKey());
        }
        return keys;
    }

    /** Write objects to persistent storage. For an in-memory Map, does nothing.
     * @param map Map whose contents we want to persist. This object should have
     *    been created initially by {@link #createJCache}.
     * @throws CacheException
     */
    public static void flushCache(Map map) throws CacheException
    {
        if (map instanceof com.cboe.application.jcache.Cache)
        {
            ((com.cboe.application.jcache.Cache)map).flush();
        }
    }
    
    public static Cache getStrategyCache()
    {
    	Cache sc = strategyCache;
    	if (sc == null)
    	{
    		sc = createStrategyCache();
    	}
    	return sc;
    }    

    /**
     * Find or create a cache for strategies (spreads).
     * @return cache of {@link StrategyStruct} objects
     */
    private static synchronized Cache createStrategyCache()
    {
        if (strategyCache == null)
        {
            strategyCache = new BaseCache(ProductCacheKeyFactory.getPrimaryStrategyKey(), new StrategyStruct());
            strategyCache.addGenerator(ProductCacheKeyFactory.getStrategyGroupByClassKey());
            strategyCache.addGenerator(ProductCacheKeyFactory.getStrategyGroupByLegProductKey());

            // We have a two-way update link between products and strategies (which contain products)
            strategyCache.addSubscriber(SubscriberFactory.getStrategyToProduct());
            getProductCache().addSubscriber(SubscriberFactory.getProductToStrategy());
        }
        return strategyCache;
    }

    public static Cache getFirmCache(boolean active)
    {
    	Cache fc = active?activeFirmCache:inactiveFirmCache;
    	if (fc == null)
    	{
    		fc = createFirmCache(active);
    	}
    	return fc;
    }    


    /**
     * Find or create a cache for firms.
     * @return cache of {@link FirmStruct} objects
     */
    public static synchronized Cache createFirmCache(boolean active)
    {
        Cache firmCache = active?activeFirmCache:inactiveFirmCache;

        if (firmCache == null)
        {
            firmCache = new BaseCache(FirmCacheKeyFactory.getPrimaryKey(), new FirmStruct());
            firmCache.addGenerator(FirmCacheKeyFactory.getAcronymKey());
            firmCache.addGenerator(FirmCacheKeyFactory.getNumberKey());

            if (active)
                activeFirmCache = firmCache;
            else
                inactiveFirmCache = firmCache;
        }
        return firmCache;
    }

    /**
     * Find or create a cache for product types for the session.
     * @param sessionName find the cache for this session
     * @return cache of {@link ProductTypeStruct} objects
     */
    public static synchronized Cache getSessionProductTypeCache(String sessionName)
    {
        Cache cache = getSessionCache(sessionName, CACHE_TYPE);
        if (cache == null)
        {
            cache = makeSessionCache(sessionName, CACHE_TYPE, TradingSessionCacheKeyFactory.getPrimaryTypeKey(), new ProductTypeStruct());

            // We have a two-way update link between product type and session product types (which contain product types)
            cache.addSubscriber(SubscriberFactory.getSessionTypeToType(sessionName));
            getProductTypeCache().addSubscriber(SubscriberFactory.getTypeToSessionType(sessionName));
        }
        return cache;
    }

    /**
     * Find or create a cache for session classes for the session.
     * @param sessionName find the cache for this session
     * @return Cache of {@link SessionClassStruct} objects
     */
    public static synchronized Cache getSessionClassCache(String sessionName)
    {
        Cache cache = getSessionCache(sessionName, CACHE_CLASS);
        if (cache == null)
        {
            cache = makeSessionCache(sessionName, CACHE_CLASS, TradingSessionCacheKeyFactory.getPrimaryClassKey(), new SessionClassStruct());
            cache.addGenerator(TradingSessionCacheKeyFactory.getClassGroupByTypeKey());

            // We have a two-way update link between class struct and session class types (which contain class structs)
            cache.addSubscriber(SubscriberFactory.getSessionClassToClass(sessionName));
            getClassCache().addSubscriber(SubscriberFactory.getClassToSessionClass(sessionName));
        }
        return cache;
    }

    /**
     * Find or create a cache for session products for the session.
     * @param sessionName find the cache for this session
     * @return cache of {@link SessionProductStruct} objects
     */
    public static synchronized Cache getSessionProductCache(String sessionName)
    {
        Cache cache = getSessionCache(sessionName, CACHE_PRODUCT);
        if (cache == null)
        {
            cache = makeSessionCache(sessionName, CACHE_PRODUCT, TradingSessionCacheKeyFactory.getPrimaryProductKey(), new SessionProductStruct());
            cache.addGenerator(TradingSessionCacheKeyFactory.getProductNameKey());
            cache.addGenerator(TradingSessionCacheKeyFactory.getProductGroupByClassKey());

            // We have a two-way update link between product and session product types (which contain products)
            cache.addSubscriber(SubscriberFactory.getSessionProductToProduct(sessionName));
            getProductCache().addSubscriber(SubscriberFactory.getProductToSessionProduct(sessionName));
        }
        return cache;
    }

    /**
     * Find or create a cache for session strategies for the session.
     * @param sessionName find the cache for this session
     * @return cache of {@link SessionStrategyStruct} objects
     */
    public static synchronized Cache getSessionStrategyCache(String sessionName)
    {
        Cache cache = getSessionCache(sessionName, CACHE_STRATEGY);
        if (cache == null)
        {
            cache = makeSessionCache(sessionName, CACHE_STRATEGY, TradingSessionCacheKeyFactory.getPrimaryStrategyKey(), new SessionStrategyStruct());
            cache.addGenerator(TradingSessionCacheKeyFactory.getStrategyGroupByClassKey());
            cache.addGenerator(TradingSessionCacheKeyFactory.getStrategyKeyByLegs());

            // We have a two-way update link between session product and session strategy types (which contain session products)
            cache.addSubscriber(SubscriberFactory.getSessionStrategyToSessionProduct(sessionName));
            getSessionProductCache(sessionName).addSubscriber(SubscriberFactory.getSessionProductToSessionStrategy(sessionName));
        }
        return cache;
    }

    public static synchronized void updateProductTypeCache(Object productType)
    {
        getProductTypeCache().updateCache(productType);
    }

    public static synchronized void loadProductTypeCache(Object[] productTypes)
    {
        getProductTypeCache().loadCache(productTypes);
    }

    public static synchronized void updateClassCache(Object classStruct)
    {
        getClassCache().updateCache(classStruct);
    }

    public static synchronized void loadClassCache(Object[] classStructs)
    {
        getClassCache().loadCache(classStructs);
    }

    public static synchronized void updateProductCache(Object product)
    {
        getProductCache().updateCache(product);
    }

    public static synchronized void loadProductCache(Object[] products)
    {
        getProductCache().loadCache(products);
    }

    public static synchronized void updateStrategyCache(Object strategy)
    {
        getStrategyCache().updateCache(strategy);
    }

    public static synchronized void loadStrategyCache(Object[] strategies)
    {
        getStrategyCache().loadCache(strategies);
    }

    public static synchronized void updateSessionClassCache(String sessionName, Object classStruct)
    {
        getSessionClassCache(sessionName).updateCache(classStruct);
    }

    public static synchronized void loadSessionClassCache(String sessionName, Object[] classStructs)
    {
        getSessionClassCache(sessionName).loadCache(classStructs);
    }

    public static synchronized void updateSessionProductCache(String sessionName, Object product)
    {
        getSessionProductCache(sessionName).updateCache(product);
    }

    public static synchronized void loadSessionProductCache(String sessionName, Object[] products)
    {
        getSessionProductCache(sessionName).loadCache(products);
    }

    public static synchronized void updateSessionStrategyCache(String sessionName, Object strategy)
    {
        getSessionStrategyCache(sessionName).updateCache(strategy);
    }

    public static synchronized void loadSessionStrategyCache(String sessionName, Object[] strategies)
    {
        getSessionStrategyCache(sessionName).loadCache(strategies);
    }

    public static void setCachingDirectory(String cachingDirectory)
    {
        CacheFactory.cachingDirectory = cachingDirectory;
  
    }

    public static void shutdownProductCache()
    {
        if (productCache != null)
        {
            ((BaseCache) productCache).shutdown();
            productCache = null;
        }
        
    }
    public static void shutdownProductClassCache()
    {
        if (productClassCache != null)
        {
            if(productClassCache instanceof com.cboe.application.jcache.Cache) {           
                ((com.cboe.application.jcache.Cache)productClassCache).shutdown();        
              }
            productClassCache = null;
        }
        
    }

    public static void setProductCacheName(String value)
    {
        productCacheName=value;
        
    }

    public static void setProductClassCacheName(String value)
    {
       productClassCacheName=value;
        
    }

    public static void setCacheType(String value)
    {
        cacheType=value;
        
    }

   

   

}

// We don't synchronize the methods in this factory because they are only called by synchronized methods in
// the CacheFactory.
class SubscriberFactory
{
    private static CacheSubscriber productToStrategy = new CacheSubscriber()
                    {
                        // Optimization: prevents a HashMap lookup
                        CacheKeyGenerator   strategyKey = ProductCacheKeyFactory.getPrimaryStrategyKey();

                        public void update(Object element)
                        {
                            ProductStruct  product  = (ProductStruct) element;
                            StrategyStruct strategy = (StrategyStruct) CacheFactory.getStrategyCache().find(strategyKey, Integer.valueOf(product.productKeys.productKey));

                            // We only update - we don't add if it doesn't exist
                            if (strategy != null)
                            {
                                //Log.debug("CacheFactory--->Product updating strategy");
                                strategy = ClientProductStructBuilder.cloneStrategy(strategy, product);
                                CacheFactory.getStrategyCache().updateCache(strategy, SubscriberFactory.getStrategyToProduct());
                            }
                        }
                    };
    private static CacheSubscriber strategyToProduct = new CacheSubscriber()
                    {
                        public void update(Object element)
                        {
                            //Log.debug("CacheFactory--->Strategy updating product");

                            // Update the product cache, prevent strategy updater from being called (infinite loop time)
                            CacheFactory.getProductCache().updateCache(((StrategyStruct)element).product, SubscriberFactory.getProductToStrategy());
                        }
                    };

    public static CacheSubscriber getProductToStrategy()
    {
        return productToStrategy;
    }

    public static CacheSubscriber getStrategyToProduct()
    {
        return strategyToProduct;
    }

    // since all these methods are called only few times, the synchronization should be ok. - GJ
    private static HashMap<String, CacheSubscriber> typeToSessionType = new HashMap<String, CacheSubscriber>();
    public static CacheSubscriber getTypeToSessionType(String sessionName)
    {
        synchronized(typeToSessionType)
        {
            CacheSubscriber sub = typeToSessionType.get(sessionName);
            if (sub == null)
            {
                sub = new TypeToSessionTypeCacheSubscriber(sessionName);
                typeToSessionType.put(sessionName, sub);
            }
            return sub;
        }
    }

    private static HashMap<String, CacheSubscriber> sessionTypeToType = new HashMap<String, CacheSubscriber>();
    public static CacheSubscriber getSessionTypeToType(String sessionName)
    {
        synchronized (sessionTypeToType)
        {
            CacheSubscriber sub = sessionTypeToType.get(sessionName);
            if (sub == null)
            {
                sub = new SessionTypeToTypeCacheSubscriber(sessionName);
                sessionTypeToType.put(sessionName, sub);
            }
            return sub;
        }
    }

    private static HashMap<String, CacheSubscriber> classToSessionClass = new HashMap<String, CacheSubscriber>();
    public static  CacheSubscriber getClassToSessionClass(String sessionName)
    {
        synchronized (classToSessionClass)
        {
            CacheSubscriber sub = classToSessionClass.get(sessionName);
            if (sub == null)
            {
                sub = new ClassToSessionClassCacheSubscriber(sessionName);
                classToSessionClass.put(sessionName, sub);
            }
            return sub;
        }
    }

    private static HashMap<String, CacheSubscriber> sessionClassToClass = new HashMap<String, CacheSubscriber>();
    public static CacheSubscriber getSessionClassToClass(String sessionName)
    {
        synchronized (sessionClassToClass)
        {
            CacheSubscriber sub = sessionClassToClass.get(sessionName);
            if (sub == null)
            {
                sub = new SessionClassToClassCacheSubscriber(sessionName);
                sessionClassToClass.put(sessionName, sub);
            }
            return sub;
        }
    }

    private static HashMap<String, CacheSubscriber> productToSessionProduct = new HashMap<String, CacheSubscriber>();
    public static CacheSubscriber getProductToSessionProduct(String sessionName)
    {
        synchronized (productToSessionProduct)
        {
            CacheSubscriber sub = productToSessionProduct.get(sessionName);
            if (sub == null)
            {
                sub = new ProductToSessionProductCacheSubscriber(sessionName);
                productToSessionProduct.put(sessionName, sub);
            }
            return sub;
        }
    }

    private static HashMap<String, CacheSubscriber> sessionProductToProduct = new HashMap<String, CacheSubscriber>();
    public static CacheSubscriber getSessionProductToProduct(String sessionName)
    {
        synchronized (sessionProductToProduct)
        {
            CacheSubscriber sub = sessionProductToProduct.get(sessionName);
            if (sub == null)
            {
                sub = new SessionProductToProductCacheSubscriber(sessionName);
                sessionProductToProduct.put(sessionName, sub);
            }
            return sub;
        }
    }

    private static HashMap<String, CacheSubscriber> sessionStrategyToSessionProduct = new HashMap<String, CacheSubscriber>();
    public static CacheSubscriber getSessionStrategyToSessionProduct(String sessionName)
    {
        synchronized (sessionStrategyToSessionProduct)
        {
            CacheSubscriber sub = sessionStrategyToSessionProduct.get(sessionName);
            if (sub == null)
            {
                sub = new SessionStrategyToSessionProductCacheSubscriber(sessionName);
                sessionStrategyToSessionProduct.put(sessionName, sub);
            }
            return sub;
        }
    }

    private static HashMap<String, CacheSubscriber> sessionProductToSessionStrategy = new HashMap<String, CacheSubscriber>();
    public static CacheSubscriber getSessionProductToSessionStrategy(String sessionName)
    {
        synchronized (sessionProductToSessionStrategy)
        {
            CacheSubscriber sub = sessionProductToSessionStrategy.get(sessionName);
            if (sub == null)
            {
                sub = new SessionProductToSessionStrategyCacheSubscriber(sessionName);
                sessionProductToSessionStrategy.put(sessionName, sub);
            }
            return sub;
        }
    }
}

class TypeToSessionTypeCacheSubscriber implements CacheSubscriber
{
    Cache               sessionTypeCache;
    CacheKeyGenerator   sessionKey;
    String              sessionName = null;
    volatile CacheSubscriber     ignoreSubscriber = null;

    public TypeToSessionTypeCacheSubscriber(String __sessionName)
    {
        sessionName       = __sessionName;
        sessionTypeCache  = CacheFactory.getSessionProductTypeCache(__sessionName);
        sessionKey        = TradingSessionCacheKeyFactory.getPrimaryTypeKey();
    }

    // Creating it post-constructor prevents an infinite loop on construction
    private synchronized CacheSubscriber initIgnoreSubscriber()
    {
        if(ignoreSubscriber == null)
        {
            ignoreSubscriber  = SubscriberFactory.getSessionTypeToType(sessionName);
        }
        return ignoreSubscriber;
    }

    private CacheSubscriber getIgnoreSubscriber()
    {
    	CacheSubscriber cs = ignoreSubscriber;
        if (cs == null)
        {
            cs = initIgnoreSubscriber();
        }
        return cs;
    }

    public void update(Object element)
    {
        ProductTypeStruct productType = (ProductTypeStruct) element;
        ProductTypeStruct sessionType = (ProductTypeStruct) sessionTypeCache.find(sessionKey, Integer.valueOf(productType.type));

        // We only update - we don't add if it doesn't exist
        if (sessionType != null)
        {
            //Log.debug("CacheFactory--->Product type updating session product type");
            sessionTypeCache.updateCache(productType, getIgnoreSubscriber());
        }
    }
}

class SessionTypeToTypeCacheSubscriber implements CacheSubscriber
{
    String          sessionName = null;
    volatile CacheSubscriber ignoreSubscriber = null;

    public SessionTypeToTypeCacheSubscriber(String __sessionName)
    {
        sessionName = __sessionName;
    }

    // Creating it post-constructor prevents an infinite loop on construction
    private synchronized CacheSubscriber initIgnoreSubscriber()
    {
        if (ignoreSubscriber == null)
        {
            ignoreSubscriber = SubscriberFactory.getTypeToSessionType(sessionName);
        }
        return ignoreSubscriber;
    }

    private CacheSubscriber getIgnoreSubscriber()
    {
    	CacheSubscriber cs = ignoreSubscriber;
        if (cs == null)
        {
            cs = initIgnoreSubscriber();
        }
        return cs;
    }

    public void update(Object element)
    {
        //Log.debug("CacheFactory--->Session product type updating product type");

        // Update the type cache, prevent session type updater from being called (infinite loop time)
        CacheFactory.getProductTypeCache().updateCache(element, getIgnoreSubscriber());
    }
}

class ClassToSessionClassCacheSubscriber implements CacheSubscriber
{
    Cache               sessionClassCache;
    CacheKeyGenerator   sessionKey;
    String              sessionName = null;
    volatile CacheSubscriber     ignoreSubscriber = null;

    public ClassToSessionClassCacheSubscriber(String __sessionName)
    {
        sessionName       = __sessionName;
        sessionClassCache = CacheFactory.getSessionClassCache(__sessionName);
        sessionKey        = TradingSessionCacheKeyFactory.getPrimaryClassKey();
    }

    // Creating it post-constructor prevents an infinite loop on construction
    private synchronized CacheSubscriber initIgnoreSubscriber()
    {
        if (ignoreSubscriber == null)
        {
            ignoreSubscriber = SubscriberFactory.getSessionClassToClass(sessionName);
        }
        return ignoreSubscriber;
    }

    private CacheSubscriber getIgnoreSubscriber()
    {
    	CacheSubscriber cs = ignoreSubscriber;
        if (cs == null)
        {
            cs = initIgnoreSubscriber();
        }
        return cs;
    }

    public void update(Object element)
    {
        ClassStruct aClass = (ClassStruct) element;
        SessionClassStruct sessionClass = (SessionClassStruct) sessionClassCache.find(sessionKey, Integer.valueOf(aClass.classKey));

        // We only update - we don't add if it doesn't exist
        if (sessionClass != null)
        {
            //Log.debug("CacheFactory--->Class updating session class");
            sessionClass = ClientProductStructBuilder.cloneSessionClassStruct(sessionClass, aClass);
            sessionClassCache.updateCache(sessionClass, getIgnoreSubscriber());
        }
    }
}

class SessionClassToClassCacheSubscriber implements CacheSubscriber
{
    String          sessionName = null;
    volatile CacheSubscriber  ignoreSubscriber = null;
    Cache           classCache = null;

    public SessionClassToClassCacheSubscriber(String __sessionName)
    {
        sessionName = __sessionName;
        classCache  = CacheFactory.getClassCache();
    }

    private synchronized CacheSubscriber initIgnoreSubscriber()
    {
        if (ignoreSubscriber == null)
        {
            ignoreSubscriber = SubscriberFactory.getClassToSessionClass(sessionName);
        }
        return ignoreSubscriber;
    	
    }
    
    // Creating it post-constructor prevents an infinite loop on construction
    private CacheSubscriber getIgnoreSubscriber()
    {
    	CacheSubscriber cs = ignoreSubscriber;
        if (cs == null)
        {
            cs = initIgnoreSubscriber();
        }
        return cs;
    }

    public void update(Object element)
    {
        SessionClassStruct sessionClass = (SessionClassStruct) element;

        //Log.debug("CacheFactory--->Session class updating class");

        // Update the class cache, prevent session class updater from being called (infinite loop time)
        classCache.updateCache(sessionClass.classStruct, getIgnoreSubscriber());
    }
}

class ProductToSessionProductCacheSubscriber implements CacheSubscriber
{
    Cache               sessionProductCache;
    CacheKeyGenerator   sessionKey;
    String              sessionName = null;
    volatile CacheSubscriber     ignoreSubscriber = null;

    public ProductToSessionProductCacheSubscriber(String __sessionName)
    {
        sessionName         = __sessionName;
        sessionProductCache = CacheFactory.getSessionProductCache(__sessionName);
        sessionKey          = TradingSessionCacheKeyFactory.getPrimaryProductKey();
    }

    // Creating it post-constructor prevents an infinite loop on construction
    private synchronized CacheSubscriber initIgnoreSubscriber()
    {
        if (ignoreSubscriber == null)
        {
            ignoreSubscriber    = SubscriberFactory.getSessionProductToProduct(sessionName);
        }
        return ignoreSubscriber;
    }

    private CacheSubscriber getIgnoreSubscriber()
    {
    	CacheSubscriber cs = ignoreSubscriber;
        if (cs == null)
        {
            cs = initIgnoreSubscriber();
        }
        return cs;
    }

    public void update(Object element)
    {
        ProductStruct aProduct = (ProductStruct) element;
        SessionProductStruct sessionProduct = (SessionProductStruct) sessionProductCache.find(sessionKey, Integer.valueOf(aProduct.productKeys.productKey));

        // We only update - we don't add if it doesn't exist
        if (sessionProduct != null)
        {
            //Log.debug("CacheFactory--->Product updating session product");
            sessionProduct = ClientProductStructBuilder.cloneSessionProduct(sessionProduct, aProduct);
            sessionProductCache.updateCache(sessionProduct, getIgnoreSubscriber());
        }
    }
}

class SessionProductToProductCacheSubscriber implements CacheSubscriber
{
    String          sessionName = null;
    volatile CacheSubscriber ignoreSubscriber = null;
    Cache           productCache = null;

    public SessionProductToProductCacheSubscriber(String __sessionName)
    {
        sessionName  = __sessionName;
        productCache = CacheFactory.getProductCache();
    }

    // Creating it post-constructor prevents an infinite loop on construction
    private synchronized CacheSubscriber initIgnoreSubscriber()
    {
        if (ignoreSubscriber == null)
        {
            ignoreSubscriber = SubscriberFactory.getProductToSessionProduct(sessionName);
        }
        return ignoreSubscriber;
    }

    private CacheSubscriber getIgnoreSubscriber()
    {
    	CacheSubscriber cs = ignoreSubscriber;
        if (cs == null)
        {
            cs = initIgnoreSubscriber();
        }
        return cs;
    }

    public void update(Object element)
    {
        SessionProductStruct sessionProduct = (SessionProductStruct) element;

        //Log.debug("CacheFactory--->Session product updating product");

        // Update the class cache, prevent session class updater from being called (infinite loop time)
        productCache.updateCache(sessionProduct.productStruct, getIgnoreSubscriber());
    }
}

class SessionStrategyToSessionProductCacheSubscriber implements CacheSubscriber
{
    Cache               sessionProductCache;
    String              sessionName = null;
    volatile CacheSubscriber     ignoreSubscriber = null;

    public SessionStrategyToSessionProductCacheSubscriber(String __sessionName)
    {
        sessionName          = __sessionName;
        sessionProductCache  = CacheFactory.getSessionProductCache(__sessionName);
    }

    // Creating it post-constructor prevents an infinite loop on construction
    private synchronized CacheSubscriber initIgnoreSubscriber()
    {
        if (ignoreSubscriber == null)
        {
            ignoreSubscriber = SubscriberFactory.getSessionProductToSessionStrategy(sessionName);
        }
        return ignoreSubscriber;
    }

    private CacheSubscriber getIgnoreSubscriber()
    {
    	CacheSubscriber cs = ignoreSubscriber;
        if (cs == null)
        {
            cs = initIgnoreSubscriber();
        }
        return cs;
    }

    public void update(Object element)
    {
        SessionStrategyStruct aSessionStrategy = (SessionStrategyStruct) element;

        //Log.debug("CacheFactory--->Session strategy updating session product");
        // Update the session Product cache, prevent session strategy updater from being called (infinite loop time)
        sessionProductCache.updateCache(aSessionStrategy.sessionProductStruct, getIgnoreSubscriber());
    }
}




class SessionProductToSessionStrategyCacheSubscriber implements CacheSubscriber
{
    Cache               sessionStrategyCache;
    CacheKeyGenerator   sessionKey;
    String              sessionName = null;
    volatile CacheSubscriber     ignoreSubscriber = null;

    public SessionProductToSessionStrategyCacheSubscriber(String __sessionName)
    {
        sessionName          = __sessionName;
        sessionStrategyCache = CacheFactory.getSessionStrategyCache(__sessionName);
        sessionKey           = TradingSessionCacheKeyFactory.getPrimaryStrategyKey();
    }

    // Creating it post-constructor prevents an infinite loop on construction
    private synchronized CacheSubscriber initIgnoreSubscriber()
    {
        if (ignoreSubscriber == null)
        {
            ignoreSubscriber = SubscriberFactory.getSessionStrategyToSessionProduct(sessionName);
        }
        return ignoreSubscriber;
    }

    private CacheSubscriber getIgnoreSubscriber()
    {
    	CacheSubscriber cs = ignoreSubscriber;
        if (cs == null)
        {
            cs = initIgnoreSubscriber();
        }
        return cs;
    }

    public void update(Object element)
    {
        SessionProductStruct aSessionProduct = (SessionProductStruct) element;
        SessionStrategyStruct sessionStrategy = (SessionStrategyStruct) sessionStrategyCache.find(sessionKey, Integer.valueOf(aSessionProduct.productStruct.productKeys.productKey));

        // We only update - we don't add if it doesn't exist
        if (sessionStrategy != null)
        {
            //Log.debug("CacheFactory--->session product updating session strategy");
            sessionStrategy = ClientProductStructBuilder.cloneSessionStrategy(sessionStrategy, aSessionProduct);
            sessionStrategyCache.updateCache(sessionStrategy, getIgnoreSubscriber());
        }
    }
}

class CallStack
{
    ArrayList stack;

    public CallStack()
    {
        // Create the call stack string array
        stack = new ArrayList();

        // Grab the call stack
        Throwable t = new Throwable();

        // Print it to a single string
        StringWriter stringWriter = new StringWriter();
        t.printStackTrace(new PrintWriter(stringWriter));
        String trace = stringWriter.toString();

        // Parse the string into separate lines for each call in the stack
        boolean isFirst = true;
        StringTokenizer tok = new StringTokenizer(trace, "\n");

        while (tok.hasMoreTokens())
        {
            String next = tok.nextToken();

            // Skip the first line - it's not part of the stack
            if (!isFirst)
                stack.add(next.substring(8));  // the first 9 characters are junk

            isFirst = false;
        }
    }

    public Iterator iterator() {return stack.iterator();}

    public void print(PrintStream out, int levels)
    {
        Iterator it = iterator();
        while (it.hasNext() && levels != 0)
        {
            out.println(it.next().toString());
            levels--;
        }
    }

    public void print(PrintWriter out, int levels)
    {
        Iterator it = iterator();
        while (it.hasNext() && levels != 0)
        {
            out.println(it.next().toString());
            levels--;
        }
    }
}
