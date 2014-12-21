package com.cboe.application.product.adapter;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.interfaces.application.ProductQueryServiceAdapterHome;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;

public final class ProductQueryServiceAdapterHomeImpl extends ClientBOHome implements ProductQueryServiceAdapterHome
{
    private static final String LOGGING_DIR = "LoggingDir";
    private static final String CACHE_DIRECTORY = "cacheDirectory";
    private static final String PRODUCT_CACHE_NAME = "productCacheName";
    private static final String PRODUCT_CLASS_CACHE_NAME = "productClassCacheName";
    private static final String CACHE_TYPE = "cacheType";
    private static final String NUMBER_OF_CLASSES = "maxClassesToPreload";

    // Value to use if System.getProperty(LOGGING_DIR) fails (such as when running Simulator)
    private static final String NO_LOGGING_DIR = ".";

    ProductQueryServiceAdapterImpl instance;
    private String cacheType;
    private String cacheDirectory;
    private String productCacheName;
    private String productClassCacheName;
    private int maxClassesToPreload=0;

    public ProductQueryServiceAdapterHomeImpl()
    {
        setSmaType("GlobalProductQueryServiceAdapterHome.ProductQueryServiceAdapterHome");
    }

    public void clientInitialize() throws Exception
    {
        super.clientInitialize();
        cacheType = getProperty(CACHE_TYPE);
        cacheDirectory = getProperty(CACHE_DIRECTORY).replace("{}", System.getProperty(LOGGING_DIR, NO_LOGGING_DIR));
        productCacheName = getProperty(PRODUCT_CACHE_NAME);
        productClassCacheName = getProperty(PRODUCT_CLASS_CACHE_NAME);
        String count=getProperty(NUMBER_OF_CLASSES);
        maxClassesToPreload = Integer.parseInt(count);
        
        create(); 
        instance.foundationFrameworkInitialize(); 
    }
    
    public ProductQueryServiceAdapter find()
    {
        if(instance == null)
        {
            instance = new ProductQueryServiceAdapterImpl(cacheType, cacheDirectory,productCacheName,productClassCacheName,maxClassesToPreload);
            instance.create("ProductQueryServiceAdapter");
            addToContainer(instance);
        }
        return instance;
    }

    public ProductQueryServiceAdapter create()
    {
        return find();
    }

}