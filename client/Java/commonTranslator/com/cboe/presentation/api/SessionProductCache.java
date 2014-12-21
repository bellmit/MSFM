//
// -----------------------------------------------------------------------------------
// Source file: SessionProductCache.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.*;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;

import com.cboe.domain.util.ProductStructBuilder;

import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.product.Product;

import com.cboe.presentation.product.SessionProductClassFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * This class caches all products, classes,  and strategies in Translator
 * @author ???
 * @author Jing Chen
 */
public class SessionProductCache
{
    private Map productTypes;
    private Map classesByType;
    private Map allClasses;
    private Map productsByClass;
    private Map allProducts;

    /**
     * Used for quick access to reporting class by hash since functionality is not in API.
     */
    protected Map reportingClassesCache = null;

    /**
     * Used for quick access to products by reporting class hash since functionality is not in API.
     */
    protected Map reportingClassToProduct = null;

    /**
     * Map for caching session classes along with a productKey relationship.  
     */
    // Map was called productToSessionClassesMap
    private Map<Integer,Set<SessionProductClass>> underlyingProductToSessionClassesMap = null;
    private static final int SET_INITIAL_CAPACITY = 20;
    private boolean cacheLoaded;
    private static final int MAP_INITIAL_CAPACITY = 1000;

    public SessionProductCache()
    {
        super();
        productTypes = new HashMap();
        // get these from the config service once we infrastructure a way to do
        classesByType = new HashMap();
        productsByClass = new HashMap();
        allProducts = new HashMap();
        allClasses = new HashMap();
        reportingClassesCache = new HashMap(101);
        reportingClassToProduct = new HashMap(101);
        underlyingProductToSessionClassesMap = new HashMap<Integer,Set<SessionProductClass>>(MAP_INITIAL_CAPACITY);
        cacheLoaded = false;
    }

    private Map getMap(Object key, Map baseMap)
    {
        Map map = (Map)baseMap.get(key);

        if (map == null)
        {
            map = new HashMap();
            baseMap.put(key, map);
        }

        return map;
    }

    private Map getClassesByType(Object key)
    {
        return getMap(key, classesByType);
    }

    private Map getAllClasses()
    {
        return allClasses;
    }

    private Map getProductsByClass(Object key)
    {
        return getMap(key, productsByClass);
    }

    private Map getAllProducts()
    {
        return allProducts;
    }

    private Map<Integer,Set<SessionProductClass>> getUnderlyingProductToSessionClassesMap()
    {
        return underlyingProductToSessionClassesMap;
    }

    public synchronized boolean getCacheLoadedStatus()
    {
        return cacheLoaded;
    }

    public synchronized void setCacheLoadedStatus(boolean cacheStatus)
    {
        cacheLoaded = cacheStatus;
    }

    public synchronized ProductTypeStruct[] getProductTypesForSession()
    {
        ProductTypeStruct[] types = new ProductTypeStruct[productTypes.size()];
        Collection values = productTypes.values();
        values.toArray(types);

        return types;
    }

    public synchronized SessionProductClass[] getClassesForSession(int productType)
    {
        Integer key = new Integer(productType);
        Map classMap = getClassesByType(key);

        SessionProductClass[] classes = new SessionProductClass[classMap.size()];
        Collection values = classMap.values();
        values.toArray(classes);
        return classes;
    }

    public synchronized SessionProductClass getClassByKey(int classKey)
    {
        Integer key = new Integer(classKey);
        SessionProductClass sessionClass = (SessionProductClass)getAllClasses().get(key);

        return sessionClass;
    }

    public synchronized SessionProduct getProductByKey(int productKey)
    {
        Integer key = new Integer(productKey);
        SessionProduct sessionProduct = (SessionProduct)getAllProducts().get(key);

        return sessionProduct;
    }

    public synchronized SessionStrategy getStrategyByKey(int productKey)
    {
        Integer key = new Integer(productKey);
        SessionStrategy sessionStrategy = (SessionStrategy)getAllProducts().get(key);

        return sessionStrategy;
    }

    public synchronized Set<SessionProductClass> getSessionClassesForUnderlyingProduct(int productKey)
    {
        return getUnderlyingProductToSessionClassesMap().get(productKey);
    }

    /**
     * gets session product by product name
     * @param productName   ProductNameStruct
     * @return SessionProduct
     * @author Jing Chen
     * @version 11/22/00
     */
    public synchronized SessionProduct getProductByName(ProductNameStruct productName)
    {
        SessionProduct sessionProduct = null;
        Object[] sessionProducts = allProducts.values().toArray();
        String productNameTemp = ProductStructBuilder.toString(productName);
        // attempt to find the product in all products
        for (int i = 0; i < sessionProducts.length; i++) {
            ProductNameStruct tempProductName = ((SessionProduct)sessionProducts[i]).getProductNameStruct();
            if (ProductStructBuilder.toString(tempProductName).equals(productNameTemp)) {
                sessionProduct = (SessionProduct)sessionProducts[i];
                break;
            }
        }
        return sessionProduct;
    }

    public synchronized SessionStrategy getStrategyByName(ProductNameStruct productName)
    {
        SessionStrategy sessionStrategy = null;
        Object[] sessionStrategies = allProducts.values().toArray();
        String productNameTemp = ProductStructBuilder.toString(productName);
        // attempt to find the product in all products
        for (int i = 0; i < sessionStrategies.length; i++) {
            ProductNameStruct tempProductName = ((SessionStrategy)sessionStrategies[i]).getProductNameStruct();
            if (ProductStructBuilder.toString(tempProductName).equals(productNameTemp)) {
                sessionStrategy = (SessionStrategy)sessionStrategies[i];
                break;
            }
        }
        return sessionStrategy;
    }

    /**
     * gets the session class by product type and class symbol
     * @param productType   short
     * @param classSymbol   String
     * @return SessionProductClass
     * @author Jing Chen
     */
    public synchronized SessionProductClass getClassBySymbol(short productType, String classSymbol)
    {
        SessionProductClass sessionProductClass = null;
        SessionProductClass[] sessionClasses = getClassesForSession(productType);
        for (int i=0; i<sessionClasses.length; i++) {
            String tempClassSymbol = sessionClasses[i].getClassSymbol();
            if (tempClassSymbol.equals(classSymbol)) {
                sessionProductClass = sessionClasses[i];
                break;
            }
        }
        return sessionProductClass;
    }

    public synchronized SessionProduct[] getProductsForSession(int classKey)
    {
        Integer key = new Integer(classKey);
        Map productMap = getProductsByClass(key);
        SessionProduct[] products = null;
   //     if (Utility.isStrategy(classKey)){
   //         products = new SessionStrategy[productMap.size()];
   //     } else {
            products = new SessionProduct[productMap.size()];
   //     }
        Collection values = productMap.values();
        values.toArray(products);
        return products;
    }

    /**
     * Gets all the SessionProducts for the passed SessionReportingClass
     * @param reportingClass to get products for
     * @return only SessionProducts that have the passed SessionReportingClass
     */
    public synchronized SessionProduct[] getProductsForReportingClass(SessionReportingClass reportingClass)
    {
        SessionProduct[] products = new SessionProduct[0];

        if(reportingClassToProduct.containsKey(reportingClass))
        {
            ArrayList list = (ArrayList)reportingClassToProduct.get(reportingClass);
            products = (SessionProduct[])list.toArray(products);
        }
        return products;
    }

    public synchronized SessionStrategy[] getStrategiesForSession(int classKey)
    {
        Integer key = new Integer(classKey);
        Map productMap = getProductsByClass(key);
        SessionStrategy[] strategies = null;
  //      if (Utility.isStrategy(classKey)){
        strategies = new SessionStrategy[productMap.size()];
        Collection values = productMap.values();
        values.toArray(strategies);
        return strategies;
    }

    public synchronized SessionProductClass[] getAllClassesForSession()
    {
        Map classMap = getAllClasses();

        SessionProductClass[] classes = new SessionProductClass[classMap.size()];
        Collection values = classMap.values();
        values.toArray(classes);

        return classes;
    }

    /**
     * Gets the session reporting class with the passed key
     * @param reportingClassKey to get reporting class for
     * @return reporting class with matching key
     */
    public synchronized SessionReportingClass getReportingClassByKey(int reportingClassKey)
    {
        return (SessionReportingClass)reportingClassesCache.get(new Integer(reportingClassKey));
    }

    public synchronized void updateProductType(ProductTypeStruct productType)
    {
        productTypes.put(new Integer(productType.type), productType);
    }

    public synchronized boolean updateClass(SessionClassStruct classStruct)
    {
        boolean updated = false;
        Integer queryKey = new Integer(classStruct.classStruct.productType);
        Integer key = new Integer(classStruct.classStruct.classKey);

        SessionProductClass oldClass = (SessionProductClass)getAllClasses().get(key);
        if ((oldClass == null) ||
            (classStruct.classStateTransactionSequenceNumber > oldClass.getClassStateTransactionSequenceNumber()))
        {
            SessionProductClass sessionClass;
            sessionClass = SessionProductClassFactory.create(classStruct);

            getClassesByType(queryKey).put(key, sessionClass);
            getAllClasses().put(key, sessionClass);

            SessionReportingClass[] reportingClasses = sessionClass.getSessionReportingClasses();
            addReportingClasses(reportingClasses);

            updated = true;
        }
        return updated;
    }

    /**
     * Adds reporting classes to the cache
     * @param an array of reporting classes to cache
     */
    private void addReportingClasses(SessionReportingClass[] reportingClasses)
    {
        for(int i = 0; i < reportingClasses.length; i++)
        {
            SessionReportingClass reportingClass = reportingClasses[i];
            reportingClassesCache.put(reportingClass.getClassKey(), reportingClass);
        }
    }

    /**
     * Adds the passed SessionProduct to the mapping of SessionReportingClass to SessionProductProduct
     * @param product to map to
     */
    private void addReportingClassToProductMapping(SessionProduct product)
    {
        if(product != null)
        {
            SessionReportingClass reportingClass = getReportingClassByKey(product.getProductKeysStruct().reportingClass);

            if(reportingClass != null)
            {
                ArrayList productsList = (ArrayList)reportingClassToProduct.get(reportingClass);
                if(productsList == null)
                {
                    productsList = new ArrayList(20);
                }

                if(!productsList.contains(product))
                {
                    productsList.add(product);
                    reportingClassToProduct.put(reportingClass, productsList);
                }
            }
        }
    }


    public synchronized boolean updateProduct(SessionProduct product)
    {
        boolean updated = false;
        Integer queryKey = new Integer(product.getProductKeysStruct().classKey);
        Integer key = new Integer(product.getProductKey());
        getProductsByClass(queryKey).put(key, product);
        getAllProducts().put(key, product);
        addReportingClassToProductMapping(product);
        updated = true;
        return updated;
    }

    public synchronized boolean updateStrategy(SessionStrategy strategy)
    {
        boolean updated = false;
        Integer queryKey = new Integer(strategy.getProductKeysStruct().classKey);
        Integer key = new Integer(strategy.getProductKey());

        SessionStrategy oldStrategy = (SessionStrategy)getAllProducts().get(key);
        if ((oldStrategy == null) ||
            (strategy.getProductStateTransactionSequenceNumber() > oldStrategy.getProductStateTransactionSequenceNumber()))
        {
            getProductsByClass(queryKey).put(key, strategy);
            getAllProducts().put(key, strategy);
            updated = true;
        }
        return updated;
    }

    public synchronized boolean updateClassState(ClassStateStruct classState)
    {
        boolean updated = false;
        Integer key = new Integer(classState.classKey);

        SessionProductClass sessionClass = (SessionProductClass)getAllClasses().get(key);
        if ((sessionClass != null) ||
            (classState.classStateTransactionSequenceNumber > sessionClass.getClassStateTransactionSequenceNumber()))
        {
            sessionClass.setClassStateTransactionSequenceNumber(classState.classStateTransactionSequenceNumber);
            sessionClass.setState(classState.classState);;
            updated = true;
        }

        return updated;
    }

    public synchronized boolean updateProductState(ProductStateStruct productState)
    {
        boolean updated = false;

        Integer key = new Integer(productState.productKeys.productKey);

        SessionProduct sessionProduct = (SessionProduct)getAllProducts().get(key);
        if (sessionProduct != null) {
            if (productState.productStateTransactionSequenceNumber > sessionProduct.getProductStateTransactionSequenceNumber())
            {
                sessionProduct.setProductStateTransactionSequenceNumber(productState.productStateTransactionSequenceNumber);
                sessionProduct.setState(productState.productState);
                updated = true;
            }
        }

        return updated;
    }

    public synchronized void updateProductStates(ProductStateStruct[] productStates)
    {
        for (int i=0; i < productStates.length; i++) {
            updateProductState(productStates[i]);
        }
    }

    /**
     * Updates the productKey:SessionClass cache
     * @param productKey
     * @param sessionClassSet
     */
    public synchronized void updateUnderlyingProductToSessionClassCache(Integer productKey, Set<SessionProductClass> sessionClassSet)
    {
        if(!underlyingProductToSessionClassesMap.containsKey(productKey))
        {
            underlyingProductToSessionClassesMap.put(productKey,sessionClassSet);
        }
        underlyingProductToSessionClassesMap.get(productKey).addAll(sessionClassSet);
    }

    /**
     * updates the productKey:SessionClasses cache
     * @param sessionClass
     * @param isUpdate flags whether we need to remove first and then add to cache. This is when there
     * is an update in the session class attribute other than class key which do not alters the hashcode 
     */
    public synchronized void updateProductKeySessionClassCacheViaChannel(SessionProductClass sessionClass, boolean isUpdate)
    {
        if(sessionClass != null)
        {
            int productKey = sessionClass.getUnderlyingProduct().getProductKey();
            if(productKey > 0)
            {
                updateUnderlyingProductToSessionClassRelationship(productKey, sessionClass, isUpdate);
            }
            else
            {
                try
                {
                    Product[] products = APIHome.findProductQueryAPI().getAllProductsForClass(sessionClass.getClassKey(),true);
                    for(Product product : products)
                    {
                        productKey = product.getProductKey();
                        updateUnderlyingProductToSessionClassRelationship(productKey, sessionClass, isUpdate);
                    }
                }
                catch(UserException e)
                {
                    GUILoggerHome.find().exception("Exception in updateProductKeySessionClassCacheViaChannel ",e);
                }
            }
        }
    }

    /**
     * Updates the productKey - SessionClassSet map
     * @param product product whose relation to be updated
     */
    public synchronized void updateProductKeySessionClassCacheViaChannel(SessionProduct product,boolean isUpdate)
    {
        int classKey = product.getProductKeysStruct().classKey;
        int productKey = product.getProductKey();
        if(!getUnderlyingProductToSessionClassesMap().containsKey(productKey))
        {
            getUnderlyingProductToSessionClassesMap().put(productKey, new HashSet<SessionProductClass>(SET_INITIAL_CAPACITY));
        }
        if(isUpdate)
        {
            getUnderlyingProductToSessionClassesMap().get(productKey).remove(getClassByKey(classKey));
        }
        getUnderlyingProductToSessionClassesMap().get(productKey).add(getClassByKey(classKey));

    }

    private void updateUnderlyingProductToSessionClassRelationship(int productKey, SessionProductClass sessionClass, boolean isUpdate)
    {
        if(!underlyingProductToSessionClassesMap.containsKey(productKey))
        {
            underlyingProductToSessionClassesMap.put(productKey, new HashSet<SessionProductClass>(SET_INITIAL_CAPACITY));
        }
        if(isUpdate)
        {
            underlyingProductToSessionClassesMap.get(productKey).remove(sessionClass);
        }
        underlyingProductToSessionClassesMap.get(productKey).add(sessionClass);
    }


    public synchronized void loadProductTypeCache(ProductTypeStruct[] productTypes)
    {
        for (int i = 0; i < productTypes.length; i++)
        {
            updateProductType(productTypes[i]);
        }
    }

    public synchronized void loadClassesCache(SessionClassStruct[] classStructs)
    {
        for (int i = 0; i < classStructs.length; i++)
        {
            updateClass(classStructs[i]);
        }
    }

    public synchronized void loadProductsCache(SessionProduct[] products)
    {
        for (int i = 0; i < products.length; i++)
        {
            updateProduct(products[i]);
        }
    }

    public synchronized void loadStrategiesCache(SessionStrategy[] products)
    {
        for (int i = 0; i < products.length; i++)
        {
            updateStrategy(products[i]);
        }
    }

    /**
     * loads the productKey:sessionClassSet cache
     * @param relationMap sessionClass:productStruct[] map
     */
    public synchronized void loadUnderlyingProductToSessionClassCache(Map<Integer, Set<SessionProductClass>> relationMap)
    {
        Set<Integer> productKeys =  relationMap.keySet();
        for (Integer productKey : productKeys)
        {
            updateUnderlyingProductToSessionClassCache(productKey, relationMap.get(productKey));
        }
    }
}
