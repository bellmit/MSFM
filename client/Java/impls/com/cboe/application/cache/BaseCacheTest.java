package com.cboe.application.cache;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.cache.CacheException;

import junit.framework.TestCase;

import com.cboe.application.product.cache.ProductCacheKeyFactory;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.interfaces.application.*;

public class BaseCacheTest extends TestCase
{
    CacheKeyGenerator pkGenerator = null;
    CacheKeyGenerator skGenerator = null;

    public void testProductStruct() throws IOException
    {
       
        
        BaseCache cache = new BaseCache("bar",  "c:\\caches", pkGenerator, new Integer(1));
        
        cache.addGenerator(skGenerator);
        cache.purgeCache();
        assertEquals(0, cache.allElements.size());
        cache.updateCache(getProductStruct((short) 9, 9, 1, "IBM1"));
        cache.updateCache(getProductStruct((short) 9, 9, 2, "IBM2"));
        cache.updateCache(getProductStruct((short) 9, 9, 3, "IBM3"));
        cache.updateCache(getProductStruct((short) 9, 9, 3, "IBM4"));
        cache.updateCache(getProductStruct((short) 9, 9, 3, "IBM5"));
        cache.remove(getProductStruct((short) 9, 9, 2, "IBM2"));
        assertEquals(2, cache.allElements.size());
        assertEquals(1, ((Map) cache.allKeyedElements.get(skGenerator)).size());
        cache.shutdown();
        
        cache = new BaseCache("bar",  "c:\\caches", pkGenerator, new Integer(1));
        cache.addGenerator(skGenerator);
        assertEquals(2, cache.allElements.size());
        assertEquals(1, ((Map) cache.allKeyedElements.get(skGenerator)).size());
        cache.shutdown(); 
        
        cache = new BaseCache("bar",  "c:\\caches", pkGenerator, new Integer(1));
        cache.purgeCache();
        cache.addGenerator(skGenerator);
        assertEquals(0, cache.allElements.size());
        assertEquals(0, ((Map) cache.allKeyedElements.get(skGenerator)).size());
        cache.updateCache(getProductStruct((short) 9, 9, 1, "IBM1"));
        cache.updateCache(getProductStruct((short) 9, 8, 2, "IBM2"));
        assertEquals(2, ((Map) cache.allKeyedElements.get(skGenerator)).size());
        cache.shutdown();
    }

    public void testProductTypeCache() throws IOException, CacheException {
        BaseCache cache=new BaseCache(ProductCacheKeyFactory.getPrimaryTypeKey(), new ProductTypeStruct());  
        assertFalse(cache.allElements instanceof com.cboe.application.jcache.Cache);
    }
    
    public void testPersistent() throws IOException, CacheException {
        BaseCache cache = new BaseCache("foobar",  "c:\\caches", pkGenerator, new Integer(1));
        
        cache.addGenerator(skGenerator);
        cache.purgeCache();
        cache.updateCache(getProductStruct((short) 9, 9, 1, "IBM"));
        cache.updateCache(getProductStruct((short) 9, 8, 2, "IBM"));
        cache.updateCache(getProductStruct((short) 9, 7, 3, "IBM"));
        cache.updateCache(getProductStruct((short) 9, 7, 4, "IBM"));
        //cache.flush();
        assertEquals(4, cache.allElements.size());
        assertEquals(3, ((Map) cache.allKeyedElements.get(skGenerator)).size());
        cache.shutdown();
        BaseCache cache2 = new BaseCache("foobar", "c:\\caches", pkGenerator, new Integer(1));
        
        cache2.addGenerator(skGenerator);
        assertEquals(4, cache2.allElements.size());
        assertEquals(3, ((Map) cache2.allKeyedElements.get(skGenerator)).size());    
        cache.shutdown();
    }
    
    
    
    
    public void testPopulate() throws IOException, CacheException
    {
      
        long started = System.currentTimeMillis();
        
        BaseCache cache = new BaseCache("foo",  "c:\\caches", pkGenerator, new Integer(1));
        
        for(int i=0;i<1000;i++) {
            cache.updateCache(getProductStruct((short) 9, i/2, i, "IBM"));
        }
        //cache.flush();
        cache.shutdown();
        System.out.println("populate Time taken="+(System.currentTimeMillis()-started));
    }
   
    
    public void testRefresh() throws IOException, CacheException, SystemException
    {
        long started = System.currentTimeMillis();
        
        BaseCache cache = new BaseCache("foo",  "c:\\caches", pkGenerator, new Integer(1));
        
        System.out.println("Refresh cache creation Time taken="+(System.currentTimeMillis()-started));
        
        //cache.refresh();      
       
        assertEquals(1000, cache.allElements.size());
        cache.shutdown();
     }
    
    public void testPurge() throws IOException, CacheException, SystemException
    { long started = System.currentTimeMillis();
        
        BaseCache cache = new BaseCache("foo",  "c:\\caches", pkGenerator, new Integer(1));
        cache.purgeCache();
        //cache.refresh();
        System.out.println("purge Time taken="+(System.currentTimeMillis()-started));
        assertEquals(0, cache.allElements.size());
        cache.shutdown();
       
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

    public void setUp()
    {
        pkGenerator = new CacheKeyGenerator()
        {
            public boolean doesGroup()
            {
                return false;
            }

            public Object generateKey(Object fromObject)
            {
                ProductStruct ps = (ProductStruct) fromObject;
                return new Integer(ps.productKeys.productKey);
            }

            public Iterator groupIterator(Object fromObject)
            {
                return null;
            }
        };
        // classGroupByTypeKey
        skGenerator = new AbstractCacheKeyGenerator()
        {
            public Object generateKey(Object fromObject)
            {
                ProductStruct ps = (ProductStruct) fromObject;
                return new Integer(ps.productKeys.classKey);
            }

            class ClassGroupByTypeIterator extends AbstractGroupIterator
            {
                ClassGroupByTypeIterator(Object o)
                {
                    super(o);
                }

                public Object next()
                {
                    hasNext = false;
                    return new Integer(((ProductStruct) fromObject).productKeys.classKey);
                    // return fromObject.toString();
                }
            }

            public Iterator groupIterator(Object fromObject)
            {
                return new ClassGroupByTypeIterator(fromObject);
            }

            public boolean doesGroup()
            {
                return true;
            }
        };

    }

}
