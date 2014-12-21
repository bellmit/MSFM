package com.cboe.application.product.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.cache.CacheEntry;
import javax.cache.CacheException;

import com.cboe.application.cache.CacheFactory;
import com.cboe.application.cache.BaseCache;
import com.cboe.application.jcache.JCacheManager;
import com.cboe.application.jcache.Cache;
import com.cboe.application.product.cache.ProductCacheKeyFactory;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import junit.framework.TestCase;
public class ProductQueryServiceAdapterPersistentCacheTest extends TestCase
{
    private int seconds=0;
    List keys = new ArrayList();
    ProductQueryServiceAdapterImpl adapter=null;
    Loader coldLoader;
    Loader quickLoader;
    boolean coldLoaderCalled;
    boolean quickLoaderCalled;
    
    
    
    public void setUp() throws Exception
    {
        createCache();
        coldLoaderCalled=false;
        quickLoaderCalled=false;
        coldLoader = new Loader() {
            public void load(Integer key) throws  SystemException, CommunicationException, AuthorizationException, DataValidationException
            {
                CacheFactory.getProductClassCache().put(key,key);
                CacheFactory.getProductCache().updateCache(getProductStruct((short) key.intValue(), key.intValue(), key.intValue(), "A"));
                keys.add(key);
                coldLoaderCalled=true;
            }
        };
        quickLoader = new Loader() {
            public void load(Integer key) throws  SystemException, CommunicationException, AuthorizationException, DataValidationException
            {
                keys.add(key);
                quickLoaderCalled=true;
            }
        };
    }
    
    public void tearDown() throws Exception
    {
        CacheFactory.shutdownProductCache();
        CacheFactory.shutdownProductClassCache();
    }
   
    public void createCache() throws Exception
    {    
        adapter= new ProductQueryServiceAdapterImpl(com.cboe.application.jcache.Cache.EHCACHE_CACHE,"c:\\temp\\product\\valid","ProductCache","ProductClassCache",2);;
        CacheFactory.getProductCache(true);
        assertEquals(false,CacheFactory.getProductCache().hasData());
        assertNotNull(CacheFactory.getProductClassCache());
        CacheFactory.getProductCache().updateCache(getProductStruct((short) 1, 1, 100, "A"));
        CacheFactory.getProductCache().updateCache(getProductStruct((short) 2, 2, 200, "B"));
        CacheFactory.getProductCache().updateCache(getProductStruct((short) 3, 3, 300, "C"));
        assertEquals(true,CacheFactory.getProductCache().hasData());
        CacheFactory.getProductClassCache().clear();
        CacheFactory.getProductClassCache().put(2,2);
        CacheFactory.getProductClassCache().put(4,4);
        Thread.currentThread().sleep(2000);
        CacheFactory.getProductClassCache().put(1,1);
        CacheFactory.getProductClassCache().put(3,3);
        assertEquals(true,CacheFactory.getProductCache().hasData());
        CacheFactory.shutdownProductCache();
        CacheFactory.shutdownProductClassCache();
        CacheFactory.getProductCache(false);
        assertEquals(true,CacheFactory.getProductCache().hasData());
        CacheFactory.shutdownProductCache();
    }
    public void testColdStartSuccess() throws Exception
    {    
         boolean mode=adapter.loadProductCache(false,coldLoader,quickLoader);
        assertTrue(coldLoaderCalled);
        assertFalse(quickLoaderCalled);
        assertFalse(mode);
        assertColdStart();
    }
    public void testQuickStartSuccess() throws Exception
    {    
        boolean mode=adapter.loadProductCache(true,coldLoader,quickLoader);
        assertFalse(coldLoaderCalled);
        assertTrue(quickLoaderCalled);
        assertTrue(mode);
        assertQuickStart();
    }
    public void testQuickStartDataCorrupted() throws Exception
    {    
        adapter= new ProductQueryServiceAdapterImpl(com.cboe.application.jcache.Cache.EHCACHE_CACHE,"c:\\temp\\product\\datacorrupted","ProductCache","ProductClassCache",2);;       
        boolean mode=adapter.loadProductCache(true,coldLoader,quickLoader);
        assertTrue(coldLoaderCalled);
        assertFalse(quickLoaderCalled);
        assertFalse(mode);
        assertEquals(2,keys.size());
    }
    
   
   
    private void assertQuickStart() throws SystemException, InterruptedException
    {
        assertEquals(4,keys.size());
        assertTrue(keys.contains(2));
        assertTrue(keys.contains(4));
        
        assertTrue(keys.contains(1));
        assertTrue(keys.contains(3));
        assertEquals(4,CacheFactory.getProductClassCache().size());
        assertEquals(3,((BaseCache)CacheFactory.getProductCache()).size());
        assertEquals(true,CacheFactory.getProductCache().hasData());
        
        System.out.println("Waiting for secons="+seconds);
        Thread.currentThread().sleep(seconds*000);
    }
    private void assertColdStart() throws SystemException, InterruptedException
    {
        assertEquals(2,keys.size());
        assertFalse(keys.contains(2));
        assertFalse(keys.contains(4));
        
        assertTrue(keys.contains(1));
        assertTrue(keys.contains(3));
        assertEquals(2,CacheFactory.getProductClassCache().size());
        assertEquals(2,((BaseCache)CacheFactory.getProductCache()).size());
        assertEquals(true,CacheFactory.getProductCache().hasData());
        
        System.out.println("Waiting for secons="+seconds);
        Thread.currentThread().sleep(seconds*000);
    }

    
    
    
    private ProductStruct getProductStruct(short typeKey, int classKey, int productKey, String symbol)
    {
        ProductStruct ps = new ProductStruct();
        ps.productKeys = new ProductKeysStruct();
        ps.productKeys.classKey = classKey;
        ps.productKeys.productKey = productKey;
        ps.productKeys.productType = typeKey;
        ps.productKeys.reportingClass = 1;
        ps.productName = new ProductNameStruct();
        ps.productName.productSymbol = symbol;
        return ps;
    }
}
