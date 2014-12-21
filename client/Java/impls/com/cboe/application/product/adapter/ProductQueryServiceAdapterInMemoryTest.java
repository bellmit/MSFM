package com.cboe.application.product.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.cache.CacheEntry;

import com.cboe.application.cache.CacheFactory;
import com.cboe.application.jcache.JCacheManager;
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
public class ProductQueryServiceAdapterInMemoryTest extends TestCase
{
    List keys = new ArrayList();
    ProductQueryServiceAdapterImpl adapter=null;
    
    public void setUp() throws Exception
    {
        adapter= new ProductQueryServiceAdapterImpl(com.cboe.application.jcache.Cache.HASHMAP_CACHE,"c:\\temp\\cache","ProductCache","ProductClassCache",2);;
        CacheFactory.getProductCache().purgeCache();
        assertNotNull(CacheFactory.getProductClassCache());
        CacheFactory.getProductCache().updateCache(getProductStruct((short) 1, 1, 100, "A"));
        CacheFactory.getProductCache().updateCache(getProductStruct((short) 2, 2, 200, "B"));
        CacheFactory.getProductCache().updateCache(getProductStruct((short) 3, 3, 300, "C"));
        assertEquals(true,CacheFactory.getProductCache().hasData());
        CacheFactory.getProductClassCache().put(2,2);
        CacheFactory.getProductClassCache().put(1,1);
        CacheFactory.getProductClassCache().put(3,3);
        CacheFactory.shutdownProductCache();
        CacheFactory.shutdownProductClassCache();
    }
    
    public void testColdStart() throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {    
        adapter.performColdStart(new Loader() {
            public void load(Integer key) throws  SystemException, CommunicationException, AuthorizationException, DataValidationException
            {
                keys.add(key);
            }
        });
        assertEquals(0,keys.size());
        assertEquals(false,CacheFactory.getProductCache().hasData());
        assertEquals(0,CacheFactory.getProductClassCache().size());
        CacheFactory.shutdownProductCache();
        CacheFactory.shutdownProductClassCache();
    }

    
    
    public void testQuickStart() throws Exception
    {    
        setUp();
        adapter.performQuickStart(new Loader() {
            public void load(Integer key) throws  SystemException, CommunicationException, AuthorizationException, DataValidationException
            {
                keys.add(key);
            }
        });
        assertEquals(0,keys.size());
        assertEquals(false,CacheFactory.getProductCache().hasData());
        assertEquals(0,CacheFactory.getProductClassCache().size());
        CacheFactory.shutdownProductCache();
        CacheFactory.shutdownProductClassCache();
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
