    /**
     * 
     */
    package com.cboe.infrastructureServices.cacheService;

    import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
    import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
    import java.util.Map;
    import java.util.concurrent.ConcurrentHashMap;

    import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
    import com.cboe.infrastructureServices.loggingService.MsgPriority;

    /**
     * @author craig bomba
     * 
     */
    public abstract class JCacheFactoryBaseImpl extends FrameworkComponentImpl
    {
        static String serviceImplClassName = "com.cboe.infrastructureServices.cacheService.JCacheFactorySimpleImpl";
        static private JCacheFactory instance = null;

        protected static ConcurrentHashMap<String, JCache> cacheInstances;

        static
        {
            cacheInstances = new ConcurrentHashMap<String, JCache>();
        }

        public static JCacheFactory getInstance()
        {
            if (instance == null)
            {
                try
                {
                    Class c = Class.forName(serviceImplClassName);
                    instance = (JCacheFactory) c.newInstance();
                }
                catch (ClassNotFoundException ex)
                {
                    new CBOELoggableException( ex, MsgPriority.high);
                }
                catch (InstantiationException ex)
                {
                    new CBOELoggableException( ex, MsgPriority.high );
                }
                catch (IllegalAccessException ex)
                {
                    new CBOELoggableException( ex, MsgPriority.high );
                }
            }
            return instance;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.cboe.infinispan.FFCacheFactory#getCache(java.lang.String)
         */

        public static String getServiceImplClassName()
        {
            return serviceImplClassName;
        }

        public static void setServiceImplClassName(String str)
        {
            serviceImplClassName = str;
        }

        /**
         * Nobody should instantiate this directly, use the getInstance method.
         */
        protected JCacheFactoryBaseImpl()
        {
        }

        /*
         * FoundationFramework Framework methods. CacheService is one of the FF core services which
         * will have to provide these two methods. FoundationFramework will call this method to
         * initialize the "base" CacheService. From this base cacheservice, application can then use
         * getCacheService(cacheName) to get the cache.
         */
        
        public void goMaster()
        {
            // doing nothing.
        }

        public boolean initialize(ConfigurationService config)
        {
            return true;
        }

        /*
         * Might want to use this method to stop cache services when the FoundationFramework
         * is shutdown.  Classes that extend this BaseImpl should override and implement
         * this method if they need to do "cleanup" activities on a shutdown.
         */
        public void shutdown() 
        {
        }
    }
