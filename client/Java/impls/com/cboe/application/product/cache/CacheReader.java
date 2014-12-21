package com.cboe.application.product.cache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.cache.CacheException;

import com.cboe.application.jcache.Cache;
import com.cboe.application.jcache.JCacheManager;
import com.cboe.idl.cmiProduct.ProductStruct;

public class CacheReader
{
    private static String targetCacheFolder=".";
    private static String targetCacheName="ProductClassCache";
    private static String classKeyFile=null;
    
    public static void main(String[] args) throws CacheException, IOException
    {
        if (args.length < 2) {
            System.out.println("Usage: CacheReader cacheFolder cacheName {classKeysFilename}|{clear}");
            System.exit(1);
        }
        targetCacheFolder=args[0];
        targetCacheName=args[1];
       
        Cache cache=createCache("ehcache",targetCacheFolder,targetCacheName);
        cache.refresh();
        if (args.length >2) {
           
            if ("clear".equalsIgnoreCase(args[2])) {
                
                cache.clear();
                cache.flush();
                System.out.println("Cache "+targetCacheName+" cleared");
            }
            else {
                classKeyFile=args[2];
                updateCache(cache);
                System.out.println("Cache "+targetCacheName+" updated");
            }
            
        }
        if ("ProductCache".equalsIgnoreCase(targetCacheName)) {
            readProductCache(cache);
        }
        else if ("ProductClassCache".equalsIgnoreCase(targetCacheName)) {
            readProductClassCache(cache);
        }
    }
    
    private static void updateCache(Cache cache) throws IOException, CacheException
    {
        Reader fileReader = new FileReader(classKeyFile);
        BufferedReader reader= new BufferedReader(fileReader);
        String line=null;
        while((line=reader.readLine()) != null) {
            line = line.trim();
            int key = Integer.parseInt(line);
            cache.put(key, key);
        }
        cache.flush();
    }
    
    private static void readProductClassCache(Cache cache) throws CacheException
    {
       
        System.out.println("Product class cache contains following "+cache.size()+" class keys:");
        for(Object value:cache.values()) {
            System.out.println(value);
        }
    }
    private static void readProductCache(Cache cache) throws CacheException
    {
        
        System.out.println("Product cache contains following "+cache.size()+" products:");
        for(Object value: cache.values()) {
            ProductStruct ps = (ProductStruct)value;
            StringBuilder buffer = new StringBuilder(160);
            buffer.append("productKey="+ps.productKeys.productKey);
            buffer.append(",classKey="+ps.productKeys.classKey);
            buffer.append(",Company="+ps.companyName);
            buffer.append(",symbol="+ps.productName.productSymbol);
            buffer.append(",optionType="+ps.productName.optionType);
            buffer.append(",exercisePrice="+ps.productName.exercisePrice);
            buffer.append(",expirationDate="+ps.productName.expirationDate);
            System.out.println(buffer.toString());
        }
    }

    private static Cache createCache(String cacheType, String cacheFolder, String cacheName) throws CacheException
    {
        JCacheManager manager =JCacheManager.instance();
        Map map = new HashMap();
        map.put(Cache.CACHE_TYPE, cacheType);
        map.put("name", cacheName);
        map.put("cacheFolder", cacheFolder);
        map.put("persistent", "true");
        Cache cache = (Cache) manager.getCacheFactory().createCache(map);
        return cache;
    }

}
