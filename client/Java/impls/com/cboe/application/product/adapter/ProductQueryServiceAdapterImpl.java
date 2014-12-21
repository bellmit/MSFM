package com.cboe.application.product.adapter;

import java.util.*;

import javax.cache.CacheException;

import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.CacheKeyGenerator;
import com.cboe.interfaces.businessServices.ProductQueryService;
import com.cboe.interfaces.businessServices.TradingSessionService;
import com.cboe.interfaces.events.IECProductStatusConsumerHome;
import com.cboe.idl.product.ProductClassStruct;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyRequestStruct;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.exceptions.*;
import com.cboe.application.cache.BaseCache;
import com.cboe.application.cache.CacheFactory;
import com.cboe.application.product.cache.ProductCacheKeyFactory;
import com.cboe.application.product.ProductEventListener;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.userServices.TestClassCacheFactory;
import com.cboe.application.util.CacheDownloadThreadPoolExecutor;
import com.cboe.application.tradingSession.cache.TradingSessionCacheKeyFactory;
import com.cboe.application.productDefinition.ProductDefinitionImpl;
import com.cboe.client.util.PriceHelper;
import com.cboe.client.util.StrategyLegsWrapper;
import com.cboe.client.util.Product;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.CacheClassTrackImpl;
import com.cboe.domain.util.CacheClassTrackFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.domain.util.ClientQuickstart;
import com.cboe.domain.util.SpreadNormalizationStrategyTypes;

public final class ProductQueryServiceAdapterImpl extends BObject implements ProductQueryServiceAdapter
{
    private TradingSessionService   tradingSessionService;
    private ProductQueryService productQueryService;
    private IECProductStatusConsumerHome productStatusConsumerHome;
    private ProductEventListener eventListener;
    private int maxClassesToPreload;

    // We save local references to the cache key generators to minimize the number
    // of synchronized calls (factory calls are all synchronized)
    private CacheKeyGenerator classKey;
    private CacheKeyGenerator classGroupKey;
    private CacheKeyGenerator productKey;
    private CacheKeyGenerator productNameKey;
    private CacheKeyGenerator typeKey;
    private CacheKeyGenerator productGroupKey;
    private CacheKeyGenerator strategyGroupClassKey;
    private CacheKeyGenerator strategyKey;
    private CacheDownloadThreadPoolExecutor cacheDownloadExecutor = CacheDownloadThreadPoolExecutor.getInstance();
    /**
     * This code must be removed after rollout of No full lot normalization is complete.
     */
    private volatile static int spreadNormalizationStrategyType = SpreadNormalizationStrategyTypes.STOCK_LEG_FULL_LOT_NORMALIZATION;

    private static final PendingNameStruct[] EMPTY_PendingNameStruct_ARRAY = new PendingNameStruct[0];

    public ProductQueryServiceAdapterImpl(String cacheType, String cacheDirectory, String productCacheName, String productClassCacheName, int maxClassesToPreload)
    {
        StringBuilder inconst = new StringBuilder(cacheType.length()+cacheDirectory.length()+productCacheName.length()+productClassCacheName.length()+150);
        inconst.append("In ProductQueryServiceAdapterImpl constructor, cacheType=").append(cacheType)
               .append(" cacheDirectory=").append(cacheDirectory)
               .append(" productCacheName=").append(productCacheName)
               .append(" productClassCacheName=").append(productClassCacheName)
               .append(" maxClassesToPreload=").append(maxClassesToPreload);
        Log.information(this, inconst.toString());

        CacheFactory.setCacheType(cacheType);
        CacheFactory.setCachingDirectory(cacheDirectory);
        CacheFactory.setProductCacheName(productCacheName);
        CacheFactory.setProductClassCacheName(productClassCacheName);
        this.maxClassesToPreload = maxClassesToPreload;
     //   initStrategyLegNormalizationType();
    }

    public void create(String name)
    {
        super.create(name);
       // initStrategyLegNormalizationType();
    }

    void foundationFrameworkInitialize()
    {
        getProductQueryService();
        initStrategyLegNormalizationType();
        getProductStatusConsumerHome();

        boolean fatal = false;

        if (productQueryService == null)
        {
            Log.alarm(this, "Unable to access product query service delegate.");
            fatal = true;
        }

        if (productStatusConsumerHome == null)
        {
            Log.alarm(this, "Unable to access product status consumer home.");
            fatal = true;
        }

        if (fatal)
        {
            throw new RuntimeException("Fatal exception during initialization of PQS Adater.");
        }

        try
        {
            getBOHome().registerCommand(this, "productCache", "productCacheArCallback",
                    "Get information from the productCache",
                    new String[] { String.class.getName(), String.class.getName() },
                    new String[] { "KEYS | COUNT | DETAIL | productKey", "[DETAIL] productKey" }
            );
            getBOHome().registerCommand(this, "strategyCache", "strategyCacheArCallback",
                    "Get information from the strategyCache",
                    new String[] { String.class.getName(), String.class.getName() },
                    new String[] { "KEYS | COUNT | DETAIL | productKey", "[DETAIL] productKey" }
            );
            getBOHome().registerCommand(this, "productClassCache", "productClassCacheArCallback",
                    "Get information from the productClassCache",
                    new String[] { String.class.getName() },
                    new String[] { "KEYS | COUNT | classKey" }
            );
            getBOHome().registerCommand(this, "productTypeCache", "productTypeCacheArCallback",
                    "Get information from the productTypeCache",
                    new String[] { String.class.getName() },
                    new String[] { "ALL | productTypeKey" }
            );
            getBOHome().registerCommand(this, "sessionStrategyCache", "sessionStrategyCacheArCallback",
                    "Get information from the sessionStrategyCache",
                    new String[] { String.class.getName(),
                                   String.class.getName(),
                                   String.class.getName(),
                                   String.class.getName(),
                                   String.class.getName(),
                                   String.class.getName()},
                    new String[] { " count | key | reload | stats | dump",
                                   "session Name",
                                   "Leg1(exmaple: 611463666,B,1)",
                                   "Leg2(exmaple: 611463667,B,1)",
                                   "Leg3(exmaple: 611463668,B,1)",
                                   "Leg4(exmaple: 611463669,B,1)"}
            );
        }
        catch (Exception e)
        {
            Log.exception(this, "Cannot register ar command. Ignoring exception", e);
        }
    }

    /**
     * Initializes the caches. Invoked by the AppServerStatusManagerImpl, this method creates and
     * populates the caches. It makes calls to the {@link ProductQueryService} to retrieve product
     * type, class, product and strategy information and add those to the caches. testClass cache ,
     * which will be used for user enablement, is also loaded here.
     * 
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */

    // note: used to be static and take a PQS argument when it lived in PQManagerImpl
    public synchronized void initializeProductCaches() throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (eventListener == null)
        {
            eventListener = new ProductEventListener();

            // This connects the IEC to the CBOE event channels that we're not filtering per-class
            Integer getAllEvents = 0;

            modifyFilter(new ChannelKey(ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE, getAllEvents), true);
            modifyFilter(new ChannelKey(ChannelType.PQS_PRICE_ADJUSTMENT_APPLIED_NOTICE, getAllEvents), true);
            modifyFilter(new ChannelKey(ChannelType.PQS_PRICE_ADJUSTMENT_UPDATED_NOTICE, getAllEvents), true);
            modifyFilter(new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, getAllEvents), true);
            modifyFilter(new ChannelKey(ChannelType.PQS_UPDATE_REPORTING_CLASS, getAllEvents), true);
            modifyFilter(new ChannelKey(ChannelType.PS_UPDATE_LINKAGE_INDICATOR, getAllEvents), true);

            ProductTypeStruct[] productTypes = getProductQueryService().getProductTypes();

            CacheFactory.loadProductTypeCache(productTypes);

            for (int i = 0; i < productTypes.length; i++)
            {
            	if (Log.isDebugOn())
            	{
            		Log.debug(this, "Loading Classes for ProductType : " + productTypes[i].type);
            	}

                ClassStruct[] classes = getProductQueryService().getClassesByType(productTypes[i].type);
                CacheFactory.loadClassCache(classes);

                // load TestClass Cache
                TestClassCacheFactory.load(classes);
                
                // load reverse lookup Cache for report class
                // strategy will be looked up on individual legs
                if(productTypes[i].type != ProductTypes.STRATEGY)
                	ReportClassCacheFactory.load(classes);
            }

            Loader quickLoader=new Loader()
            {

                public void load(Integer key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
                {
                    checkProductCacheLoaded(key.intValue(), false, true);
                }
            };
            Loader coldLoader=new Loader()
            {

                public void load(Integer key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
                {
                    checkProductCacheLoaded(key.intValue(), true, true);
                    // If it is a strategy, load the strategies as well. 
                    try
                    {
	                	if (getClassByKey(key).productType == ProductTypes.STRATEGY)
	                	{
	                        checkStrategyCacheLoaded(key.intValue(), true);                		
	                	}
                    }
                    catch (NotFoundException nfe)
                    {                    	
                    }
                }
            };
            loadProductCache(ClientQuickstart.isQuickstart(),coldLoader,quickLoader);

        }
    }

    public static int getStrategyLegNormalizationType()
    {
           return spreadNormalizationStrategyType;
    }

    private void initStrategyLegNormalizationType()
    {
        try
        {
            spreadNormalizationStrategyType = getProductQueryService().getSpreadNormalizationStrategyType();
            StringBuilder msg = new StringBuilder(50);
            Log.information(msg.append("Strategy Leg Normalization Type:").append(spreadNormalizationStrategyType).toString());

        }
        catch (SystemException e)
        {
           Log.exception(e);
        }
        catch (CommunicationException e)
        {
            Log.exception(e);
        }
        catch (AuthorizationException e)
        {
            Log.exception(e);
        }

    }



    boolean loadProductCache(boolean quickStart, Loader coldLoader, Loader quickLoader) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            if ( quickStart && CacheFactory.getProductCache().hasData() )
            {
                performQuickStart(quickLoader);
                ////////////// re-visit ////////////////
                if (CacheFactory.getProductCache().hasData())
                {
                	 return true;
                }
            }
        }
        catch (Exception ex)
        {
            Log.exception(this, "Ignoring exception, attempting a full cold start", ex);
        }

        CacheFactory.shutdownProductCache();

        performColdStart(coldLoader);
        return false;
    }

    /*
     * Perform cold start, The steps are 1. Clear the product cache 2. Get a list of all the class
     * keys to be loaded from the productClassCache 3. Clear the productClassCache 4. load all the
     * product classes.
     */
    public void performColdStart(Loader loader) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Log.information(this, "Cold start of product cache started");
        StringBuilder sb = new StringBuilder(60);
        if (CacheFactory.getProductClassCache() != null)
        {
            Log.information(this, "Creating the product cache");
            CacheFactory.getProductCache(true);
            Log.information(this, "The product cache created, reading product classes to be fetched from the server");
            Map productClassCache = CacheFactory.getProductClassCache();
            Set<Integer> classKeys = CacheFactory.getRecentKeys(productClassCache, maxClassesToPreload);
            // Now that we have the keys to load, clear the cache and start fresh
            CacheFactory.getProductClassCache().clear();
            sb.append("Number of product classes to be fetched is ").append(maxClassesToPreload);
            Log.information(this, sb.toString());
            sb.setLength(0);
            for (Integer key : classKeys)
            {
            	if (Log.isDebugOn())
            	{
            		Log.debug(this, "Loading Products for Class : " + key);
            	}
                try {
                	loader.load(key);
                }
                catch (DataValidationException dve)
                {
                	Log.information(this, "DataValidationException while trying to load products for class:" + key +
                            ". " + dve.details.message + " (" + dve.details.error + ")");
                }
            }
        }
        flushProductClassCache();
        sb.append("Cold start of product cache is complete, size=").append(((BaseCache) CacheFactory.getProductCache()).size());
        Log.information(this, sb.toString());
    }

    private void flushProductClassCache() throws SystemException
    {
        try
        {
            CacheFactory.flushCache(CacheFactory.getProductClassCache());
        }
        catch (CacheException e)
        {
            Log.exception(this, "Ignoring exception", e);
        }
    }

    /*
     * Perform quick start, the steps are 1. Refresh method of the product cache (BaseCache) will be
     * invoked which will load all the information from the local file and update all the secondary
     * caches with in the BaseCache instance. 2. All the class keys will be read from the new
     * ProductCalssCache and these keys will be registered to receive the notifications from the
     * server.
     */
    public void performQuickStart(Loader loader) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        Log.information(this, "Quick start of product cache started, no notification");
        StringBuilder sb = new StringBuilder(90);
        if (CacheFactory.getProductClassCache() != null)
        {
            sb.append("Loading the product cache from the local file, initial product class size=")
              .append(CacheFactory.getProductClassCache().size());
            Log.information(this, sb.toString());
            CacheFactory.getProductCache(false);
            sb.setLength(0);
            sb.append("Registering for the product class events, product cache size=")
              .append(((BaseCache) CacheFactory.getProductCache()).size());
            Log.information(this, sb.toString());
            sb.setLength(0);
            Map productClassCache = CacheFactory.getProductClassCache();
            for (Object key : productClassCache.keySet())
            {
                loader.load((Integer) productClassCache.get(key));
            }
        }
        flushProductClassCache();
        sb.append("Quick start of product cache is complete, final size=")
          .append(((BaseCache) CacheFactory.getProductCache()).size());
        Log.information(this, sb.toString());
        sb.setLength(0);
        sb.append("final product class size=")
          .append(CacheFactory.getProductClassCache().size());
        Log.information(this, sb.toString());
    }

    // note: used to be static when it lived in PQManagerImpl
    public synchronized void purgeAllProductCaches()
    {
        ProductTypeStruct[] productTypeStructs = (ProductTypeStruct[]) CacheFactory.getProductTypeCache().findAll(ProductCacheKeyFactory.getPrimaryTypeKey());

        if (productTypeStructs != null && productTypeStructs.length != 0)
        {
            for (int i = 0; i < productTypeStructs.length; i++)
            {
                ClassStruct[] classes  = (ClassStruct[]) CacheFactory.getClassCache().findAllInGroup(ProductCacheKeyFactory.getClassGroupByTypeKey(), Integer.valueOf(productTypeStructs[i].type));

                if (classes != null && classes.length != 0)
                {
                    try
                    {
                        for (int j = 0; j < classes.length; j++)
                        {
                            SessionKeyContainer sessionKey = new SessionKeyContainer("", classes[j].classKey);
                            CacheClassTrackFactory.remove(sessionKey);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.exception(this, "ProductQueryServiceAdapterImpl.purgeAllProductCaches", e);
                    }
                }
            }
        }

        CacheFactory.getProductCache().purgeCache();
        CacheFactory.getStrategyCache().purgeCache();
        CacheFactory.getClassCache().purgeCache();
        CacheFactory.getProductTypeCache().purgeCache();

        if (eventListener != null)
        {
            eventListener.shutdown();
            eventListener = null;
        }
    }

    public PendingAdjustmentStruct[] getAllPendingAdjustments() throws SystemException, CommunicationException, AuthorizationException
    {
        return getProductQueryService().getPendingAdjustments();
    }

    private ProductClassStruct[] getProductClassesByKey(int[] classKeys, boolean includeReportingClasses, boolean includeProducts, boolean includeActiveOnly) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ProductClassStruct[] productClasses = getProductQueryService().getProductClassesByKey(classKeys, includeReportingClasses, includeProducts, includeActiveOnly);
        return productClasses;
    }

    public ClassStruct getClassByKey(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        ClassStruct classStruct = (ClassStruct) CacheFactory.getClassCache().find(getClassKey(), Integer.valueOf(classKey));

        if (classStruct == null)
        {
            StringBuilder called = new StringBuilder(70);
            called.append("ProductQueryService:getProductClassByKey called, classKey=").append(classKey);
            Log.information(this, called.toString());
            ProductClassStruct productClass = getProductQueryService().getProductClassByKey(classKey, true, false, false);
            classStruct = productClass.info;
            if (classStruct != null)
            {
                CacheFactory.updateClassCache(classStruct);
            }
        }

        return classStruct;
    }

    public ClassStruct getClassBySymbol(short productType, String classSymbol) throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        ClassStruct classStruct = (ClassStruct) CacheFactory.getClassCache().find(getClassGroupByTypeKey(), Integer.valueOf(productType), classSymbol);

        if (classStruct == null)
        {
            StringBuilder called = new StringBuilder(classSymbol.length()+65);
            called.append("ProductQueryService:getProductClassesBySymbol called, Symbol=").append(classSymbol);
            Log.information(this, called.toString());
            
            String[] classes = { classSymbol };
            ProductClassStruct[] productClassStructs = getProductQueryService().getProductClassesBySymbol(classes, productType, true, false, false);

            if (productClassStructs.length == 0)
            {
                throw ExceptionBuilder.dataValidationException("Does not contain product class with symbol = " + classSymbol,  DataValidationCodes.INVALID_PRODUCT);
            }

            classStruct = productClassStructs[0].info;
            if (classStruct != null)
            {
                CacheFactory.updateClassCache(classStruct);
            }
        }

        return classStruct;
    }

    public ClassStruct getClassBySymbolFromCache(short productType, String classSymbol) 
        throws SystemException, CommunicationException, DataValidationException, AuthorizationException, NotFoundException
    {
        return (ClassStruct) CacheFactory.getClassCache().find(getClassGroupByTypeKey(), Integer.valueOf(productType), classSymbol);
    }

    public PendingNameStruct[] getPendingAdjustmentProducts(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getProductQueryService().getPendingAdjustmentNameByClass(classKey);
    }

    public PendingAdjustmentStruct[] getPendingAdjustments(int classKey, boolean includeProducts) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        int[] classKeys = { classKey };

        PendingAdjustmentStruct[] pendingInfo = getProductQueryService().getPendingAdjustmentInfo(classKeys);

        if (!includeProducts)
        {
            for (int i = 0; i < pendingInfo.length; i++)
            {
                pendingInfo[i].productsPending = EMPTY_PendingNameStruct_ARRAY;
            }
        }

        return pendingInfo;
    }

    public ProductStruct getProductByKey(int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        long startTime = System.nanoTime();
        ProductStruct productStruct = CacheFactory.getProduct(productKey);
        boolean cacheMiss = false;
        if(productStruct == null)
        {
            cacheMiss=true;
            int[] productKeys = { productKey };
            ProductStruct[] products = getProductQueryService().getProductsByKey(productKeys);
            if (products.length == 0)
            {
                throw ExceptionBuilder.dataValidationException("Does not contain product with key = " + productKey, DataValidationCodes.INVALID_PRODUCT);
            }
            
            productFilterForClass(products[0].productKeys.classKey, true);
            productStruct = products[0];
            if (productStruct != null)
            {
                checkProductCacheLoaded(productStruct.productKeys.classKey, true, false);
            }
        }
        long delta = System.nanoTime()-startTime;
        if(delta>100000)
        {
            StringBuilder msg = new StringBuilder(200);
            msg.append("getProductsByKey called PK:");
            msg.append(productKey);
            msg.append(" cacheMiss:");
            msg.append(cacheMiss);
            msg.append(" Time:");
            msg.append(System.nanoTime()-startTime);
            Log.information(this,msg.toString());
        }
        return productStruct;
    }


    public ProductStruct getProductByName(ProductNameStruct productName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        long startTime = System.nanoTime();
        boolean cacheMiss = false;
        ProductStruct productStruct = CacheFactory.getProduct(Product.createProduct(productName));
        StringBuilder msg = new StringBuilder(200);
        if(productStruct == null)
        {
            cacheMiss=true;
            ProductNameStruct[] theNames = { productName };
            msg.append("getProductsByName called, productName=[\"");
            msg.append(productName.reportingClass);
            msg.append(" ");
            msg.append(com.cboe.domain.util.fixUtil.FixUtilPriceHelper.priceStructToString(productName.exercisePrice));
            msg.append(" ");
            msg.append(com.cboe.domain.util.fixUtil.FixUtilDateHelper.dateStructToString(productName.expirationDate));
            msg.append(" ");
            msg.append(productName.productSymbol);
            msg.append("]");
            ProductStruct[] productStructs = getProductQueryService().getProductsByName(theNames);
            if (productStructs.length == 0)
            {
                throw ExceptionBuilder.notFoundException("Does not contain product with name = " + productName.productSymbol, DataValidationCodes.INVALID_PRODUCT);
            }
            productFilterForClass(productStructs[0].productKeys.classKey, true);
            productStruct = productStructs[0];

            if (productStruct != null)
            {
                checkProductCacheLoaded(productStruct.productKeys.classKey, true, false);
            }
        }
        long delta = System.nanoTime() - startTime ;
        msg.append(" Time:");
        msg.append(delta);
        msg.append(" cacheMiss:");
        msg.append(cacheMiss);
        Log.information(this,msg.toString());
        return productStruct;
    }

    public ClassStruct[] getProductClasses(short productType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ClassStruct[] classStructs = (ClassStruct[]) CacheFactory.getClassCache().findAllInGroup(getClassGroupByTypeKey(), Integer.valueOf(productType));

        if (classStructs == null || classStructs.length == 0)
        {
            StringBuilder called = new StringBuilder(65);
            called.append("ProductQueryService:getClassesByType called, productType=").append(productType);
            Log.information(this, called.toString());
            
            classStructs = getProductQueryService().getClassesByType(productType);
            CacheFactory.getClassCache().loadCache(classStructs);
        }

        return classStructs;
    }

    public ProductNameStruct getProductNameStruct(int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        ProductStruct productStruct = getProductByKey(productKey);

        if (productStruct == null)
        {
            throw ExceptionBuilder.notFoundException("Does not contain product with key = " + productKey, DataValidationCodes.INVALID_PRODUCT);
        }

        return productStruct.productName;
    }

    public ProductTypeStruct[] getProductTypes() throws SystemException, CommunicationException, AuthorizationException
    {
        ProductTypeStruct[] productTypeStructs = (ProductTypeStruct[]) CacheFactory.getProductTypeCache().findAll(getTypeKey());

        // Cache miss? Hit the ancestor and store the results.
        if (productTypeStructs == null || productTypeStructs.length == 0)
        {
            productTypeStructs = getProductQueryService().getProductTypes();
            CacheFactory.getProductTypeCache().loadCache(productTypeStructs);
        }

        return productTypeStructs;
    }

    public ProductStruct[] getProductsByClass(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getProductsByClass for classkey = " + classKey);
        }
        checkProductCacheLoaded(classKey);
        ProductStruct[] productStructs = (ProductStruct[]) CacheFactory.getProductCache().findAllInGroup(getProductGroupByClassKey(), Integer.valueOf(classKey));
        return productStructs;
    }

    public StrategyStruct[] getStrategiesByClass(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling getStrategiesByClass for " + classKey);
        }

        checkStrategyCacheLoaded(classKey);

        StrategyStruct[] strategyStructs = (StrategyStruct[]) CacheFactory.getStrategyCache().findAllInGroup(getStrategyGroupByClassKey(), Integer.valueOf(classKey));

        return strategyStructs;
    }

    public StrategyStruct[] getStrategiesByComponent(int componentProductKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getProductQueryService().getStrategiesByComponent(componentProductKey);
    }

    public StrategyStruct getStrategyByKey(int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        long startTime = System.nanoTime();
        boolean cacheMiss = false;
        StrategyStruct strategyStruct = (StrategyStruct) CacheFactory.getStrategyCache().find(getStrategyKey(), Integer.valueOf(productKey));

        if (strategyStruct == null)
        {
        	cacheMiss = true;
            strategyStruct = getProductQueryService().getStrategyByKey(productKey);
            checkStrategyCacheLoaded(strategyStruct.product.productKeys.classKey,false);
        }
        long timeTook = System.nanoTime() - startTime ;
        // log it if it took over 100microsecond
        if (timeTook > 100000)
        {
            StringBuilder slow = new StringBuilder(75);
            slow.append("getStrategyByKey lookup took ").append(timeTook).append(" ns. cacheMiss=").append(cacheMiss);
            Log.information(this, slow.toString());
        }

        return strategyStruct;
    }

    public boolean isValidProductName(ProductNameStruct productName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            return getProductByName(productName) != null;
        }
        catch (NotFoundException e)
        {
            return false;
        }
    }

    // note: this used to be synchronized (on PQManagerImpl) when PQ stuff was encapsulated in there
    // so only would execute once per session at a time
    public void checkProductCacheLoaded(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        checkProductCacheLoaded(classKey, true, true);
    }

    public void checkProductCacheLoaded(int classKey, boolean load, boolean synchronous) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	// if the product cache need not be loaded synchronously,
    	// the task is executed in a separate thread using a ThreadPoolExecutor
    	if (!synchronous)
		{
    		cacheDownloadExecutor.execute(new ProductCacheDownload(classKey));
		}
    	else
    	{
    		downloadProductCache(classKey, load, "");
    	}
    }

    public void downloadProductCache(int classKey, boolean load, String msg) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer("", classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);
        synchronized (classTrack)
        {
            if (!classTrack.wasProductsLoaded())
            {
                if (load)
                {
                    long t0= System.nanoTime();
                	loadProductsForClass(classKey);
                    long t1= System.nanoTime();
                    if(msg==null)
                    {
                        msg="";
                    }
                    StringBuilder delta = new StringBuilder(msg.length()+70);
                    delta.append(msg).append(" Product load for classkey ").append(classKey)
                         .append(" took ").append(t1-t0).append(" nanos");
                    Log.information(this, delta.toString());
                }
                else
                {
                    productFilterForClass(classKey, true);
                }
                classTrack.setSessionLessProductsLoaded(true);
            }
            else
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "*** product ALREADY loaded for classKey=" + classKey);
                }
            }
        }
    }

    
    private void checkStrategyCacheLoaded(final int classKey,boolean synchronous) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    	// if the strategy cache need not be loaded synchronously,
    	// the task is executed in a separate thread using a ThreadPoolExecutor
    	if (!synchronous)
		{
    		cacheDownloadExecutor.execute(new StrategyCacheDownload(classKey));
		}
    	else
    	{
    		checkStrategyCacheLoaded(classKey);
    	}
    }
    
    private void checkStrategyCacheLoaded(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        checkStrategyCacheLoaded(classKey,"");

    }
    // note: this used to be sychronized (on PQManagerImpl) when PQ stuff was encapsulated in there
    // so only would execute once per session at a time
    private void checkStrategyCacheLoaded(int classKey,String msg) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer("", classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        synchronized (classTrack)
        {
            if (!classTrack.wasStrategiesLoaded())
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "*** strategy NOT loaded for classKey=" + classKey);
                }
                long t0= System.nanoTime();
                loadStrategiesForClass(classKey);
                classTrack.setSessionLessStrategiesLoaded(true);
                long t1= System.nanoTime();
                if(msg==null)
                {
                    msg="";
                }
                StringBuilder delta = new StringBuilder(msg.length()+70);
                delta.append(msg).append(" Strategy load for classkey ").append(classKey).append(" took ").append((t1-t0)).append(" nanos");
                Log.information(this, delta.toString());
            }
            else
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "*** strategy ALREADY loaded for classKey=" + classKey);
                }
            }
        }
    }

    // note: this used to be sychronized (on PQManagerImpl) when PQ stuff was encapsulated in there
    // so only would execute once per session at a time
    private ProductStruct[] loadProductsForClass(int classKey) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        long startTime = System.nanoTime();
        if (Log.isDebugOn())
        {
            Log.debug(this, "Loading product cache where classKey=" + classKey);
        }

        ProductStruct[] products = getProductQueryService().getProductsByClass(classKey);
        productFilterForClass(classKey, true);
        
        int size1 = ((BaseCache) CacheFactory.getProductCache()).size(); // REMOVE
        CacheFactory.loadProductCache(products);
        int size2 = ((BaseCache) CacheFactory.getProductCache()).size();
        Integer key = classKey;
        CacheFactory.getProductClassCache().put(key, key);
        long timeTook = System.nanoTime() - startTime ;
        StringBuilder delta = new StringBuilder(166);
        delta.append("ProductQueryService:getProductsByClass called, clasKey=").append(classKey)
             .append(" : size=").append(products.length)
             .append(" : previous cache size=").append(size1)
             .append(" : new cache size=").append(size2)
             .append(" : timeTook in ns=").append(timeTook);
        Log.information(this, delta.toString()); 
        return products;
    }

    // note: this used to be sychronized (on PQManagerImpl) when PQ stuff was encapsulated in there
    // so only would execute once per session at a time
    protected StrategyStruct[] loadStrategiesForClass(int classKey) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Loading strategy cache where classKey=" + classKey);
        }

        StrategyStruct[] strategies = getProductQueryService().getStrategiesByClass(classKey, false);
        strategyFilterForClass(classKey, true);

        CacheFactory.loadStrategyCache(strategies);
        Integer key = classKey;
        CacheFactory.getProductClassCache().put(key, key);
        return strategies;
    }

    private void productFilterForClass(int classKey, boolean addFilter) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS, Integer.valueOf(classKey));
        modifyFilter(channelKey, addFilter);
    }

    private void strategyFilterForClass(int classKey, boolean addFilter) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, Integer.valueOf(classKey));
        modifyFilter(channelKey, addFilter);
    }

    private void modifyFilter(ChannelKey channelKey, boolean addFilter) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        if (addFilter)
        {
            getProductStatusConsumerHome().addFilter(channelKey);
        }
        else
        {
            getProductStatusConsumerHome().removeFilter(channelKey);
        }
    }

    private ProductQueryService getProductQueryService()
    {
        if (productQueryService == null)
        {
            productQueryService = ServicesHelper.getProductQueryService();
        }
        return productQueryService;
    }

    private IECProductStatusConsumerHome getProductStatusConsumerHome()
    {
        if (productStatusConsumerHome == null)
        {
            productStatusConsumerHome = ServicesHelper.getProductStatusConsumerHome();
        }
        return productStatusConsumerHome;
    }

    // We just do this to minimize the number of synchronized calls (factory calls are all
    // synchronized)
    private CacheKeyGenerator getClassKey()
    {
        if (classKey == null)
        {
            classKey = ProductCacheKeyFactory.getPrimaryClassKey();
        }
        return classKey;
    }

    // We just do this to minimize the number of synchronized calls (factory calls are all
    // synchronized)
    private CacheKeyGenerator getClassGroupByTypeKey()
    {
        if (classGroupKey == null)
        {
            classGroupKey = ProductCacheKeyFactory.getClassGroupByTypeKey();
        }
        return classGroupKey;
    }

    // We just do this to minimize the number of synchronized calls (factory calls are all
    // synchronized)
    private CacheKeyGenerator getProductKey()
    {
        if (productKey == null)
        {
            productKey = ProductCacheKeyFactory.getPrimaryProductKey();
        }
        return productKey;
    }

    // We just do this to minimize the number of synchronized calls (factory calls are all
    // synchronized)
    private CacheKeyGenerator getProductNameKey()
    {
        if (productNameKey == null)
        {
            productNameKey = ProductCacheKeyFactory.getProductNameKey();
        }
        return productNameKey;
    }

    // We just do this to minimize the number of synchronized calls (factory calls are all
    // synchronized)
    private CacheKeyGenerator getTypeKey()
    {
        if (typeKey == null)
        {
            typeKey = ProductCacheKeyFactory.getPrimaryTypeKey();
        }
        return typeKey;
    }

    // We just do this to minimize the number of synchronized calls (factory calls are all
    // synchronized)
    private CacheKeyGenerator getProductGroupByClassKey()
    {
        if (productGroupKey == null)
        {
            productGroupKey = ProductCacheKeyFactory.getProductGroupByClassKey();
        }
        return productGroupKey;
    }

    // We just do this to minimize the number of synchronized calls (factory calls are all
    // synchronized)
    private CacheKeyGenerator getStrategyGroupByClassKey()
    {
        if (strategyGroupClassKey == null)
        {
            strategyGroupClassKey = ProductCacheKeyFactory.getStrategyGroupByClassKey();
        }
        return strategyGroupClassKey;
    }

    // We just do this to minimize the number of synchronized calls (factory calls are all
    // synchronized)
    private CacheKeyGenerator getStrategyKey()
    {
        if (strategyKey == null)
        {
            strategyKey = ProductCacheKeyFactory.getPrimaryStrategyKey();
        }
        return strategyKey;
    }

    public void refreshProductCachesForClassKey(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer("", classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        if (classTrack != null)
        {
            StringBuilder refresh = new StringBuilder(60);
            synchronized (classTrack)
            {
                if (classTrack.wasProductsLoaded())
                {
                    // this is a product class key
                    refresh.append("Products in product class ").append(classKey).append(" are being refreshed.");
                    Log.information(this, refresh.toString());
                    refresh.setLength(0);
                    refreshProductCacheForClassKey(classKey);
                }
                else
                {
                	if (Log.isDebugOn())
                	{
                		Log.debug(this, "No products refreshed for class key " + classKey);
                	}
                }

                if (classTrack.wasStrategiesLoaded())
                {
                    // this is a strategy class key
                    refresh.append("Strategies in strategy class ").append(classKey).append(" are being refreshed.");
                    Log.information(this, refresh.toString());
                    refreshStrategyCacheForClassKey(classKey);
                }
                else
                {
                	if (Log.isDebugOn())
                	{
                		Log.debug(this, "No strategies refreshed for class key " + classKey);
                	}
                }
            }
        }
        else
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "No products or strategies found for refresh for class key " + classKey);
            }
        }
    }

    private void refreshProductCacheForClassKey(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean reload = unloadProductCache(classKey);
        if (reload)
        {
            checkProductCacheLoaded(classKey);
        }
    }

    private void refreshStrategyCacheForClassKey(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        boolean reload = unloadStrategyCache(classKey);
        if (reload)
        {
            checkStrategyCacheLoaded(classKey);
        }
    }

    // PQRefactor note: used to be declared synchronized when PQ stuff was encapsulated in
    // PQManagerImpl;
// would have only been called by one thread at a time per session
    private void unloadProductsForClass(int classKey) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Unloading product cache where classKey=" + classKey);
        }

        productFilterForClass(classKey, false);
        CacheFactory.getProductCache().removeAllInGroup(ProductCacheKeyFactory.getProductGroupByClassKey(), Integer.valueOf(classKey));
        CacheFactory.getProductClassCache().remove(Integer.valueOf(classKey));
    }

    // PQRefactor note: used to be declared synchronized when PQ stuff was encapsulated in
    // PQManagerImpl;
// would have only been called by one thread at a time per session
    private void unloadStrategiesForClass(int classKey) throws AuthorizationException, CommunicationException, DataValidationException, SystemException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Unloading strategy cache where classKey=" + classKey);
        }

        strategyFilterForClass(classKey, false);
        CacheFactory.getStrategyCache().removeAllInGroup(ProductCacheKeyFactory.getProductGroupByClassKey(), Integer.valueOf(classKey));
    }

// note: used to be declared synchronized when PQ stuff was encapsulated in PQManagerImpl;
// would have only been called by one thread at a time per session
    private boolean unloadProductCache(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer("", classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        boolean previouslyLoaded = false;
        synchronized (classTrack)
        {
            previouslyLoaded = classTrack.wasProductsLoaded();
            if (previouslyLoaded)
            {
                unloadProductsForClass(classKey);
                classTrack.setSessionLessProductsLoaded(false);
            }
        }

        return previouslyLoaded;
    }

// note: used to be declared synchronized when PQ stuff was encapsulated in PQManagerImpl;
// would have only been called by one thread at a time per session
    private boolean unloadStrategyCache(int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer("", classKey);
        CacheClassTrackImpl classTrack = CacheClassTrackFactory.find(sessionKey);

        boolean previouslyLoaded = false;
        synchronized (classTrack)
        {
            previouslyLoaded = classTrack.wasStrategiesLoaded();
            if (previouslyLoaded)
            {
                unloadStrategiesForClass(classKey);
                classTrack.setSessionLessStrategiesLoaded(false);
            }
        }

        return previouslyLoaded;
    }

    // ---------- ar commands ----------

    private String toString(DateStruct dateStruct)
    {
        StringBuilder s = new StringBuilder();
        s.append(dateStruct.year).append('-').append(dateStruct.month).append('-').append(dateStruct.day);
        return s.toString();
    }

    private String toString(DateTimeStruct dt)
    {
        StringBuilder s = new StringBuilder();
        s.append(dt.date.year).append('-').append(dt.date.month).append('-').append(dt.date.day)
         .append('.')
         .append(dt.time.hour).append(':');
        if (dt.time.minute < 10)
        {
            s.append('0');
        }
        s.append(dt.time.minute).append(':');
        if (dt.time.second < 10)
        {
            s.append('0');
        }
        s.append(dt.time.second);
        if (dt.time.fraction != 0)
        {
            s.append('.');
            // fraction is a byte, so max value is 255, so it can't hold more
            // than 2 decimal digits of fractional second.
            if (dt.time.fraction < 10)
            {
                s.append('0');
            }
            s.append(dt.time.fraction);
        }
        return s.toString();
    }

    private String toString(ProductStruct productStruct)
    {
        if (null == productStruct)
        {
            return "(no product in cache)";
        }

        StringBuilder result = new StringBuilder();
        result.append("ProductKeysStruct{")
              .append(" productKey=").append(productStruct.productKeys.productKey)
              .append(" classKey=").append(productStruct.productKeys.classKey)
              .append(" productType=").append(productStruct.productKeys.productType)
              .append(" reportingClass=").append(productStruct.productKeys.reportingClass)
              .append(" }\nProductNameStruct{")
              .append(" reportingClass=").append(productStruct.productName.reportingClass)
              .append(" exercisePrice=").append(PriceHelper.toString(productStruct.productName.exercisePrice))
              .append(" expirationDate=").append(toString(productStruct.productName.expirationDate))
              .append(" optionType=").append(productStruct.productName.optionType)
              .append(" productSymbol=").append(productStruct.productName.productSymbol)
              .append(" }\nlistingState=").append(productStruct.listingState)
              .append(" description=").append(productStruct.description)
              .append(" companyName=").append(productStruct.companyName)
              .append(" unitMeasure=").append(productStruct.unitMeasure)
              .append(" standardQuantity=").append(productStruct.standardQuantity)
              .append(" maturityDate=").append(toString(productStruct.maturityDate))
              .append(" activationDate=").append(toString(productStruct.activationDate))
              .append(" inactivationDate=").append(toString(productStruct.inactivationDate))
              .append(" createdTime=").append(toString(productStruct.createdTime))
              .append(" lastModifiedTime=").append(toString(productStruct.lastModifiedTime));
        return result.toString();
    }

    private static boolean isIntegerString(String s)
    {
        return s.matches("^[0-9][0-9]*$");
    }

    public String productCacheArCallback(String command, String productKey)
    {
        com.cboe.interfaces.application.Cache cache = CacheFactory.getProductCache();
        if (null == cache)
        {
            return "(no cache)";
        }
        if (command.equalsIgnoreCase("keys"))
        {
            ProductStruct[] prods = (ProductStruct[]) cache.retrieveCache();
            TreeSet<Integer> keys = new TreeSet<Integer>();
            for (ProductStruct productStruct : prods)
            {
                keys.add(productStruct.productKeys.productKey);
            }
            StringBuilder result = new StringBuilder();
            result.append('[').append(keys.size()).append(']');
            for (Integer k : keys)
            {
                result.append(' ').append(k);
            }
            return result.toString();
        }
        else if (command.equalsIgnoreCase("count"))
        {
            ProductStruct[] prods = (ProductStruct[]) cache.retrieveCache();
            return new StringBuilder().append('[').append(prods.length).append(']').toString();
        }
        else if (command.equalsIgnoreCase("detail"))
        {
            if (null == productKey)
            {
                return "(DETAIL needs a productKey)";
            }
            if (!isIntegerString(productKey))
            {
                return "(productKey must be an integer)";
            }
            ProductStruct productStruct = (ProductStruct) cache.find(getProductKey(), Integer.valueOf(productKey));
            return toString(productStruct);
        }
        else if (isIntegerString(command))
        {
            ProductStruct productStruct = (ProductStruct) cache.find(getProductKey(), Integer.valueOf(command));
            if (null == productStruct)
            {
                return "(no product in cache)";
            }
            StringBuilder result = new StringBuilder();
            result.append("classKey=").append(productStruct.productKeys.classKey)
                  .append(" productKey=").append(productStruct.productKeys.productKey)
                  .append(' ').append(productStruct.productName.reportingClass)
                  .append(' ').append(toString(productStruct.productName.expirationDate))
                  .append(' ').append(PriceHelper.toString(productStruct.productName.exercisePrice))
                  .append(' ').append(productStruct.productName.optionType);
            return result.toString();
        }

        return "(unknown request, try 'productCache help')";
    }

    public String strategyCacheArCallback(String command, String productKey)
    {
        com.cboe.interfaces.application.Cache cache = CacheFactory.getStrategyCache();
        if (null == cache)
        {
            return "(no cache)";
        }
        if (command.equalsIgnoreCase("keys"))
        {
            StrategyStruct[] strats = (StrategyStruct[]) cache.retrieveCache();
            TreeSet<Integer> keys = new TreeSet<Integer>();
            for (StrategyStruct strategyStruct : strats)
            {
                keys.add(strategyStruct.product.productKeys.productKey);
            }
            StringBuilder result = new StringBuilder();
            result.append('[').append(keys.size()).append(']');
            for (Integer k : keys)
            {
                result.append(' ').append(k);
            }
            return result.toString();
        }
        else if (command.equalsIgnoreCase("count"))
        {
            StrategyStruct[] strats = (StrategyStruct[]) cache.retrieveCache();
            return new StringBuilder().append('[').append(strats.length).append(']').toString();
        }
        else if (command.equalsIgnoreCase("detail"))
        {
            if (null == productKey)
            {
                return "(DETAIL needs a productKey)";
            }
            if (!isIntegerString(productKey))
            {
                return "(productKey must be an integer)";
            }
            StrategyStruct strategyStruct = (StrategyStruct) cache.find(getStrategyKey(), Integer.valueOf(productKey));
            if (null == strategyStruct)
            {
                return "(no strategy in cache)";
            }
            StringBuilder result = new StringBuilder();
            result.append("product=ProductStruct{ ").append(toString(strategyStruct.product))
                  .append(" }\nstrategyType=").append(strategyStruct.strategyType)
                  .append(" strategyLegs[").append(strategyStruct.strategyLegs.length).append(']');
            int i = 0;
            for (StrategyLegStruct sls : strategyStruct.strategyLegs)
            {
                result.append("\n[").append(++i)
                      .append("] product=").append(sls.product)
                      .append(" ratioQuantity=").append(sls.ratioQuantity)
                      .append(" side=").append(sls.side);
            }
            return result.toString();
        }
        else if (isIntegerString(command))
        {
            StrategyStruct strategyStruct = (StrategyStruct) cache.find(getStrategyKey(), Integer.valueOf(command));
            if (null == strategyStruct)
            {
                return "(no strategy in cache)";
            }
            StringBuilder result = new StringBuilder();
            result.append("classKey=").append(strategyStruct.product.productKeys.classKey)
                  .append(" productKey=").append(strategyStruct.product.productKeys.productKey)
                  .append(' ').append(strategyStruct.product.productName.reportingClass)
                  .append(' ').append(strategyStruct.product.productName.productSymbol);
            return result.toString();
        }

        return "(unknown request, try 'strategyCache help')";
    }

    public String productClassCacheArCallback(String command)
    {
        Map<Integer,?> productClassCache;   // This Map is really used as a Set
        try
        {
            productClassCache = CacheFactory.getProductClassCache();
        }
        catch (SystemException e)
        {
            Log.exception(this, e);
            return "Could not get cache: " + e.getMessage()
                    + " " + e.details.error + "/" + e.details.message;
        }
        if (null == productClassCache)
        {
            return "(no cache)";
        }
        if (command.equalsIgnoreCase("keys"))
        {
            TreeSet<Integer> keys = new TreeSet<Integer>(productClassCache.keySet());
            StringBuilder result = new StringBuilder();
            result.append('[').append(keys.size()).append(']');
            for (Integer k : keys)
            {
                result.append(' ').append(k);
            }
            return result.toString();
        }
        else if (command.equalsIgnoreCase("count"))
        {
            return new StringBuilder().append('[').append(productClassCache.size()).append(']').toString();
        }
        else if (isIntegerString(command))
        {
            int key = Integer.parseInt(command);
            return (productClassCache.containsKey(key) ? "Key is" : "Not" ) +" in productClassCache";
        }

        return "(unknown request, try 'productClassCache help')";
    }

    public String productTypeCacheArCallback(String productTypeKey)
    {
        com.cboe.interfaces.application.Cache cache = CacheFactory.getProductTypeCache();
        if (null == cache)
        {
            return "(no cache)";
        }
        ProductTypeStruct[] ptsa;
        if (productTypeKey.equalsIgnoreCase("all"))
        {
            ptsa = (ProductTypeStruct[]) cache.findAll(ProductCacheKeyFactory.getPrimaryTypeKey());
        }
        else
        {
            ptsa = new ProductTypeStruct[1];
            ptsa[0] = (ProductTypeStruct) cache.find(ProductCacheKeyFactory.getPrimaryTypeKey(), Integer.valueOf(productTypeKey));
            if (null == ptsa[0])
            {
                return "(no productType in cache)";
            }
        }

        StringBuilder result = new StringBuilder();
        int i = 0;
        for (ProductTypeStruct pts : ptsa)
        {
            if (i > 0)
            {
                result.append('\n');
            }
            result.append('[').append(++i)
                  .append("] type=").append(pts.type)
                  .append(" name=").append(pts.name)
                  .append(" description=").append(pts.description)
                  .append(" createdTime=").append(toString(pts.createdTime))
                  .append(" lastModifiedTime=").append(toString(pts.lastModifiedTime));
        }
        return result.toString();
    }

	public ProductStruct[] getProductsForReportingClassSymbol(String reportingClassSymbol, short type) 
		throws  SystemException, 
				CommunicationException, 
				AuthorizationException, 
				DataValidationException {
		ProductStruct[] products = com.cboe.client.util.CollectionHelper.EMPTY_ProductStruct_ARRAY;
		if(type != ProductTypes.STRATEGY) {
			Integer classKey = ReportClassCacheFactory.getClassKeyByReportClassSysmbol(reportingClassSymbol, type);
			ProductStruct[] productsByClass = getProductsByClass(classKey.intValue());
			
			
			ArrayList<ProductStruct> productsByReportClass = new ArrayList<ProductStruct>();
			if(productsByClass.length > 0) {
				for(int i =0; i < productsByClass.length; i++) {
					 if(productsByClass[i].productName.reportingClass.equalsIgnoreCase(reportingClassSymbol))
					 productsByReportClass.add(productsByClass[i]);
				}
				if(productsByReportClass.size() > 0)
					products = productsByReportClass.toArray(products);
			}
			return products;
		}
		else 
			throw new DataValidationException();
	}

//        StrategyStruct[] strats = (StrategyStruct[]) cache.retrieveCache();
    public String sessionStrategyCacheArCallback (String aCommand,
                                                  String aSessionName,
                                                  String aLeg1,
                                                  String aLeg2,
                                                  String aLeg3,
                                                  String aLeg4)
    {
        StringBuilder result = new StringBuilder();
        
        int myLegsCount = 0;
        String[] myLegs = null;
        if(aLeg1!=null&&aLeg1.length()>0){
            myLegsCount++;
            if(aLeg2!=null&&aLeg2.length()>0){
                myLegsCount++;
                if(aLeg3!=null&&aLeg3.length()>0){
                    myLegsCount++;
                    if(aLeg4!=null&&aLeg4.length()>0){
                        myLegsCount++;
                    }
                }
            }

            myLegs = new String[myLegsCount];
            switch(myLegsCount){
                case 1:
                    myLegs[0] = aLeg1;
                    break;
                case 2:
                    myLegs[0] = aLeg1;
                    myLegs[1] = aLeg2;
                    break;
                case 3:
                    myLegs[0] = aLeg1;
                    myLegs[1] = aLeg2;
                    myLegs[2] = aLeg3;
                    break;
                case 4:
                    myLegs[0] = aLeg1;
                    myLegs[1] = aLeg2;
                    myLegs[2] = aLeg3;
                    myLegs[3] = aLeg4;
                    break;
                default:
                    break;
            }
        }

        if (aCommand.equalsIgnoreCase("key"))
        {
            if(aSessionName.equalsIgnoreCase("ALL")){
                List<String> mySessionNames = CacheFactory.getSessionNames();
                for(String mySessionName:mySessionNames)
                {
                    getSessionDataInfo(mySessionName, result);
                }
                return result.toString();
            }
            else if(aLeg1.equalsIgnoreCase("ALL")){
                getSessionDataInfo(aSessionName, result);
                return result.toString();
            }

            if(myLegs.length<2)
            {
                return result.append("Not enough arguments.\n").toString();
            }
            try{
                StrategyLegsWrapper myWrapper = new StrategyLegsWrapper(myLegs);

                SessionStrategyStruct strategyStruct =
                        (SessionStrategyStruct) CacheFactory.getSessionStrategyCache(aSessionName).
                        find(TradingSessionCacheKeyFactory.getStrategyKeyByLegs(), myWrapper);
                if(strategyStruct==null){
                    result.append("SessionStrategyStruct not found for the given legs.\n").toString();
                }else{
                    result.append("productKey:")
                        .append(strategyStruct.sessionProductStruct.productStruct.productKeys.productKey)
                        .append(" session:")
                        .append(strategyStruct.sessionProductStruct.sessionName)
                        .append(" legs:{");

                    for (SessionStrategyLegStruct myLegStruct : strategyStruct.sessionStrategyLegs)
                    {
                        result.append(myLegStruct.product)
                              .append(",")
                              .append(myLegStruct.side)
                              .append(",")
                              .append(myLegStruct.ratioQuantity)
                              .append(" ");
                    }
                    result.append("}\n");
                }
            }catch(NotFoundException nfe){
                return result.append("SessionStrategyStruct not found for the given legs.\n").toString();
            }
            catch(DataValidationException nfe){
                return result.append("SessionStrategyStruct not found for the given legs.\n").toString();
            }
        }
        else if (aCommand.equalsIgnoreCase("count"))
        {
            List<String> mySessionNames = CacheFactory.getSessionNames();
            for(String mySessionName:mySessionNames)
            {
                com.cboe.interfaces.application.Cache cache = 
                        CacheFactory.getSessionStrategyCache(mySessionName);
                Object[]  strats = cache.retrieveCache();
                result.append(mySessionName).append(": ").append('[').append(strats.length).append(']').append('\n');
            }
        }
        else if (aCommand.equalsIgnoreCase("reload"))
        {
            if(myLegs.length<2)
            {
                return result.append("Not enough arguments.\n").toString();
            }
            StrategyLegStruct[] myLegStruct = new StrategyLegStruct[myLegs.length];
            for(int i=0;i<myLegs.length;i++){
                Scanner myScanner = new Scanner(myLegs[i]);
                myScanner.useDelimiter(",");
                int myProduct = myScanner.nextInt();
                char mySide = myScanner.next().trim().toUpperCase().charAt(0);
                int myRatio =  myScanner.nextInt();
                myLegStruct[i] = new StrategyLegStruct(myProduct,
                       myRatio,
                       mySide);
            }
            StrategyRequestStruct myRequest = new StrategyRequestStruct(myLegStruct);
            try{
                SessionStrategyStruct strategyStruct = getTradingSessionService().acceptStrategy(aSessionName, myRequest);
                CacheFactory.updateSessionStrategyCache(aSessionName, strategyStruct);
                result.append("sessionStrategyCache data updated from global service.\n");
                result.append("productKey:")
                    .append(strategyStruct.sessionProductStruct.productStruct.productKeys.productKey)
                    .append(" session:")
                    .append(strategyStruct.sessionProductStruct.sessionName)
                    .append(" legs:{");
                for (SessionStrategyLegStruct legStruct : strategyStruct.sessionStrategyLegs)
                {
                    result.append(legStruct.product)
                          .append(",")
                          .append(legStruct.side)
                          .append(",")
                          .append(legStruct.ratioQuantity)
                          .append(" ");
                }
                result.append("}\n");
            }catch(Exception e){
                return result.append("Can not get data from global service.\n").toString();
            }
        }
        else if (aCommand.equalsIgnoreCase("stats"))
        {
            result.append("Total acceptStrategy() call:").append('[').append(ProductDefinitionImpl.getTotalStrategyCallCount()).append(']').append('\n')
                  .append("Global call:").append('[').append(ProductDefinitionImpl.getGlobalStrategyCallCount()).append(']');
        }
        else if(aCommand.equalsIgnoreCase("dump"))
        {
            if(aSessionName==null||aSessionName.length()<=0)
                return result.append("Please provide sessionName or ALL.").toString();

            if(aSessionName.equalsIgnoreCase("ALL"))
            {
                List<String> mySessionNames = CacheFactory.getSessionNames();
                for(String mySessionName:mySessionNames)
                {
                    CacheFactory.getSessionStrategyCache(mySessionName).purgeCache();
                }
            }
            else{
                CacheFactory.getSessionStrategyCache(aSessionName).purgeCache();
            }
            result.append("Session Strategy Cache of ").append('[').append(aSessionName).append(']').append("purged\n");
        }
        else
            result.append("unknown command.");
        return result.toString();
    }

    private void getSessionDataInfo(String aSessionName, StringBuilder result){
        Object[] myObjects=CacheFactory.getSessionStrategyCache(aSessionName).findAll(TradingSessionCacheKeyFactory.getStrategyKeyByLegs());
        result.append("[").append(aSessionName).append("]:");
        if(myObjects==null||myObjects.length<=0){
            result.append(" No data in session.\n").toString();
        }
        else
        {
            for(Object myObject: myObjects){
                SessionStrategyStruct strategyStruct = (SessionStrategyStruct) myObject;
                result.append("productKey:")
                    .append(strategyStruct.sessionProductStruct.productStruct.productKeys.productKey)
                    .append(" session:")
                    .append(strategyStruct.sessionProductStruct.sessionName)
                    .append(" legs:{");
                for (SessionStrategyLegStruct myLegStruct : strategyStruct.sessionStrategyLegs)
                {
                    result.append(myLegStruct.product)
                          .append(",")
                          .append(myLegStruct.side)
                          .append(",")
                          .append(myLegStruct.ratioQuantity)
                          .append(" ");
                }
                result.append("}\n");
            }
        }
    }
    private TradingSessionService getTradingSessionService()
    {
        if (tradingSessionService == null )
            tradingSessionService = ServicesHelper.getTradingSessionService();
        return tradingSessionService;
    }
    
    class ProductCacheDownload implements Runnable
    {
    	int classKey;
    	ProductCacheDownload(int classKey)
		{
			this.classKey = classKey;
		}

    	public void run()
    	{
    		try
    		{
    			downloadProductCache(classKey, true, "Delayed");
    		}
    		catch (Exception e)
    		{
    			Log.exception("Delayed ProductCacheDownload failed for classKey " + classKey, e);
    		}
    	}
    }
    
    class StrategyCacheDownload implements Runnable
    {
    	int classKey;
    	StrategyCacheDownload(int classKey)
		{
			this.classKey = classKey;
		}

    	public void run()
    	{
    		try
    		{
    			checkStrategyCacheLoaded(classKey,"Delayed");
    		}
    		catch (Exception e)
    		{
    			Log.exception("Delayed StrategyCacheDownload failed for classKey " + classKey, e);
    		}
    	}
    }
    
}
