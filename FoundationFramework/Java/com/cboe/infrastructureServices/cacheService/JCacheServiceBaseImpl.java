    /**
     * 
     */
    package com.cboe.infrastructureServices.cacheService;

    import java.util.ArrayList;

    /**
     * @author craig bomba
     * 
     */
    public abstract class JCacheServiceBaseImpl<K, V> implements JCache<K, V>
    {
        protected ArrayList<JCacheVendorListener> listenerList = null;

        public JCacheServiceBaseImpl()
        {
            listenerList = new ArrayList<JCacheVendorListener>();
        }

        /*
         * Placeholder. This method may decorate the cache or do other housekeeping. For example, it
         * may load from a backing store (database) or decorate with some kind of specialized
         * listener.
         * 
         * @see com.cboe.infinispan.FFCache#goMaster()
         */
        public void goMaster()
        {
        }

        /*
         * Placeholder. This method may decorate the cache or do other housekeeping. For example, it
         * may load from a backing store (database) or decorate with some kind of specialized
         * listener.
         * 
         * @see com.cboe.infinispan.FFCache#goSlave()
         */
        public void goSlave()
        {
        }

    }
