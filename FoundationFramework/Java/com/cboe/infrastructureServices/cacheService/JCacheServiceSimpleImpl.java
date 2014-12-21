package com.cboe.infrastructureServices.cacheService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

    /**
     * @author craig bomba
     * @param <K, V> generics for Key and Value
     *
     */
    public class JCacheServiceSimpleImpl<K, V> extends JCacheServiceBaseImpl<K, V> implements JCache<K, V>
    {
        private Map<K, V> cacheAdaptee = null;
        // TODO - make the listenerList collection below thread safe!  Collections.synchronizedCollection for exmaple!
        //        Or, may need to synchronize on some object
        
        public JCacheServiceSimpleImpl(Map<K, V> cache)
        {
            super();
            this.cacheAdaptee = cache;
        }
        
        public void addListener(JCacheListener<K, V> listener)
        {

        }
        
        public void clear()
        {
            // TODO Auto-generated method stub
            
        }

        public boolean containsKey(Object key)
        {
            // TODO Auto-generated method stub
            return cacheAdaptee.containsKey(key);
        }

        public boolean containsValue(Object value)
        {
            // TODO Auto-generated method stub
            return false;
        }

        public Set<java.util.Map.Entry<K, V>> entrySet()
        {
            return cacheAdaptee.entrySet();
        }

        public V get(Object key)
        {
            // TODO Auto-generated method stub
            return cacheAdaptee.get(key);
        }

        public boolean isEmpty()
        {
            // TODO Auto-generated method stub
            return false;
        }

        public Set<K> keySet()
        {
            // TODO Auto-generated method stub
            return null;
        }

        public V put(K key, V value)
        {
            // TODO Auto-generated method stub
            return cacheAdaptee.put(key, value);
        }

        public void putAll(Map<? extends K, ? extends V> m)
        {
            // TODO Auto-generated method stub
            
        }

        public V remove(Object key)
        {
            // TODO Auto-generated method stub
            return cacheAdaptee.remove(key);
        }

        public void removeListener(JCacheListener<K, V> listener)
        {
        }
        public int size()
        {
            return cacheAdaptee.size();
        }

        public Collection<V> values()
        {
            // TODO Auto-generated method stub
            return null;
        }

    }
