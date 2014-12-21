//
// -----------------------------------------------------------------------------------
// Source file: ProductSessionProcessor.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;

import org.omg.CORBA.UserException;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;

import com.cboe.domain.util.ObjectKeyContainer;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.Strategy;

import com.cboe.presentation.product.SessionProductFactory;
import com.cboe.presentation.product.SessionProductClassFactory;
import com.cboe.presentation.product.ProductFactoryHome;
import com.cboe.presentation.product.ProductClassFactoryHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.formatters.ProductStates;
import com.cboe.idl.cmiConstants.ProductTypes;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

public class ProductSessionProcessor implements EventChannelListener {

    private static final int SET_INITIAL_CAPACITY = 50;
    private static final int MAP_INITIAL_CAPACITY = 500;

    private ProductQueryOrderedCacheProxy productCache;
    private final String Category = this.getClass().getName();

    public ProductSessionProcessor () {
        productCache = ProductQueryCacheFactory.find();
    }

    public void subscribeForSessionProductEvents(String sessionName) {
    ChannelKey channelKey;

    channelKey = new ChannelKey(ChannelType.CB_CLASS_UPDATE_BY_TYPE, sessionName);
    EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

    channelKey = new ChannelKey(ChannelType.CB_CLASS_STATE_BY_TYPE, sessionName);
    EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

    channelKey = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS, sessionName);
    EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

    channelKey = new ChannelKey(ChannelType.CB_PRODUCT_STATE_BY_CLASS, sessionName);
    EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

    channelKey = new ChannelKey(ChannelType.CB_STRATEGY_UPDATE, sessionName);
    EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }

    public void addProductTypes(String sessionName, ProductTypeStruct[] types) {
        SessionProductCacheFactory.find(sessionName).loadProductTypeCache(types);
    }

    public void addClasses(String sessionName, SessionClassStruct[] classStructs)
    {
        SessionProductCacheFactory.find(sessionName).loadClassesCache(classStructs);
        for (int i =0 ;i<classStructs.length; i++) {
            TradingSessionFinderFactory.find().addClassToSession(classStructs[i].classStruct.classKey, sessionName);
        }
    }

    public void addClass(SessionClassStruct classStruct)
    {
        SessionProductCacheFactory.find(classStruct.sessionName).updateClass(classStruct);
        TradingSessionFinderFactory.find().addClassToSession(classStruct.classStruct.classKey, classStruct.sessionName);
    }

    public void addProducts(String sessionName, SessionProductStruct[] sessionProductStructs)
    {
        int productLength = sessionProductStructs.length;
        SessionProduct[] products = new SessionProduct[productLength];
        for (int i = 0; i < productLength; i++) {
            products[i] = SessionProductFactory.create(sessionProductStructs[i]);
        }
        SessionProductCacheFactory.find(sessionName).loadProductsCache(products);
    }

    public SessionProduct addInactiveSessionProduct(String originalSessionName, String inactiveSessionName, ProductStruct productStruct)
    {
        SessionProduct[] products = new SessionProduct[1];
        products[0] = SessionProductFactory.createInactiveSessionProduct(originalSessionName, inactiveSessionName, productStruct);
        SessionProductCacheFactory.find(inactiveSessionName).loadProductsCache(products);
        return SessionProductCacheFactory.find(inactiveSessionName).getProductByKey(productStruct.productKeys.productKey);
    }

    public SessionProduct addInactiveSessionStrategy(String originalSessionName, String inactiveSessionName, StrategyStruct strategyStruct)
    {
        SessionStrategy[] products = new SessionStrategy[1];
        products[0] = SessionProductFactory.createInactiveSessionStrategy(originalSessionName, inactiveSessionName, strategyStruct);
        SessionProductCacheFactory.find(inactiveSessionName).loadStrategiesCache(products);
        return SessionProductCacheFactory.find(inactiveSessionName).getStrategyByKey(strategyStruct.product.productKeys.productKey);
    }

    public void updateProduct(SessionProductStruct sessionProductStruct)
    {
        if (sessionProductStruct.productStruct.productKeys.productType == ProductTypes.STRATEGY) {
            SessionStrategy strategy = SessionProductCacheFactory.find(sessionProductStruct.sessionName).getStrategyByKey(sessionProductStruct.productStruct.productKeys.productKey);
            if ( strategy != null ) {
                SessionStrategyStruct strategyStruct = new SessionStrategyStruct();
                strategyStruct.sessionProductStruct = sessionProductStruct;
                strategyStruct.sessionStrategyLegs = strategy.getSessionStrategyLegStructs();
                strategyStruct.strategyType = strategy.getStrategyType();
                updateStrategy(strategyStruct);
            } else {
                try {
                    APIHome.findProductQueryAPI().getStrategyByKeyForSession(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.productKey);
                } catch (Exception e) {
                    GUILoggerHome.find().exception(Category+".updateProduct()","exception in getting strategy by sessionName and productKey",e);
                }
            }
            return;

        }
        SessionProduct sessionProduct = SessionProductFactory.create(sessionProductStruct);
        SessionProductCacheFactory.find(sessionProductStruct.sessionName).updateProduct(sessionProduct);


        
    }
    public void addStrategies(String sessionName,SessionStrategyStruct[] strategies)
    {
        int strategyLength = strategies.length;
        SessionStrategy[] sessionStrategies = new SessionStrategy[strategyLength];
        for (int i = 0; i < strategies.length; i++) {
             sessionStrategies[i] = SessionProductFactory.create(strategies[i]);
        }
        SessionProductCacheFactory.find(sessionName).loadStrategiesCache(sessionStrategies);
    }

    public void updateStrategy(SessionStrategyStruct sessionStrategyStruct)
    {
        SessionStrategy sessionStrategy = SessionProductFactory.create(sessionStrategyStruct);
        SessionProductCacheFactory.find(sessionStrategyStruct.sessionProductStruct.sessionName).updateStrategy(sessionStrategy);
    }

    public void channelUpdate(ChannelEvent event) {
        int channelType = ((ChannelKey)event.getChannel()).channelType;
        Object eventData = event.getEventData();
        switch(channelType)
        {
            case ChannelType.CB_PRODUCT_STATE_BY_CLASS:
                 ProductStateStruct productState = (ProductStateStruct)eventData;
                 if ( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY) ) {
                     GUILoggerHome.find().debug(Category+".channelUpdate()", GUILoggerBusinessProperty.PRODUCT_QUERY,
                                "product state for productKey = " + productState.productKeys.productKey + " state = "+
                                ProductStates.toString(productState.productState));
                 }
                 SessionProductCacheFactory.find(productState.sessionName).updateProductState(productState);
                 break;
            case ChannelType.CB_PRODUCT_UPDATE_BY_CLASS:
                 if ( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY)) {
                     StringBuffer stringBuffer = new StringBuffer();
                     stringBuffer.append("Adding product for session ");
                     stringBuffer.append(((SessionProduct)eventData).getSessionProductStruct().sessionName);
                     stringBuffer.append(" of type = " + ((SessionProduct)eventData).getSessionProductStruct().productStruct.productKeys.productType);
                     stringBuffer.append(" with class = " + ((SessionProduct)eventData).getSessionProductStruct().productStruct.productKeys.classKey);
                     stringBuffer.append(" and product = " + ((SessionProduct)eventData).getSessionProductStruct().productStruct.productKeys.productKey);
                     GUILoggerHome.find().debug(Category+".channelUpdate()",GUILoggerBusinessProperty.PRODUCT_QUERY,stringBuffer.toString());
                            
                }
                SessionProduct sessionProduct = (SessionProduct)eventData;
                updateProduct(sessionProduct.getSessionProductStruct());
                Product product = null;
                publishProductUpdateByClassForType(sessionProduct);
                if ( sessionProduct.getProductKeysStruct().productType == ProductTypes.STRATEGY ){
                    Strategy strategy = productCache.getStrategyByKey(sessionProduct.getProductKey());
                    if (strategy != null) {
                        StrategyStruct strategyStruct = new StrategyStruct();
                        strategyStruct.product = sessionProduct.getProductStruct();
                        strategyStruct.strategyLegs = strategy.getStrategyLegStructs();
                        Strategy newStrategy = ProductFactoryHome.find().create(strategyStruct);
                        productCache.updateStrategy(newStrategy);
                    } else {
                        try {
                            APIHome.findProductQueryAPI().getStrategyByKey(sessionProduct.getProductKey());
                        } catch (Exception e) {
                            GUILoggerHome.find().exception(Category+".channelUpdate()","exception in getting strategy by productKey",e);
                        }
                    }
                    return;
                }
                product = ProductFactoryHome.find().create(sessionProduct.getProductStruct());
                productCache.updateProduct(product);
                break;
            case ChannelType.CB_CLASS_STATE_BY_TYPE:
                ClassStateStruct classState = (ClassStateStruct)eventData;
                SessionProductCacheFactory.find(classState.sessionName).updateClassState(classState);
                break;
            case ChannelType.CB_CLASS_UPDATE_BY_TYPE:
                SessionClassStruct classStruct = ((SessionProductClass)eventData).getSessionClassStruct();
                ProductClass productClass = ProductClassFactoryHome.find().create(classStruct.classStruct);
                productCache.updateClass(productClass);
                addClass(classStruct);
                updateUnderlyingProductToSessionClassCache(classStruct);
                break;
            case ChannelType.CB_STRATEGY_UPDATE:
                if ( GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.PRODUCT_QUERY) ) 
                {
                    GUILoggerHome.find().debug(Category+".channelUpdate()",GUILoggerBusinessProperty.PRODUCT_QUERY,
                            "Strategy update for productKey = " + ((SessionStrategy)eventData).getTradingSessionName() + " : "
                            + ((SessionStrategy)eventData).getProductKey());
                }
                SessionStrategy sessionStrategy = (SessionStrategy)eventData;
                StrategyStruct strategyStruct = new StrategyStruct();
                strategyStruct.product = sessionStrategy.getProductStruct();
                strategyStruct.strategyType = sessionStrategy.getStrategyType();
                strategyStruct.strategyLegs = sessionStrategy.getStrategyLegStructs();
                Strategy strategy = ProductFactoryHome.find().create(strategyStruct);
                productCache.updateStrategy(strategy);
                SessionStrategyStruct sessionStrategyStruct = new SessionStrategyStruct();
                sessionStrategyStruct.sessionProductStruct = sessionStrategy.getSessionProductStruct();
                sessionStrategyStruct.sessionStrategyLegs = sessionStrategy.getSessionStrategyLegStructs();
                sessionStrategyStruct.strategyType = sessionStrategy.getStrategyType();
                updateStrategy(sessionStrategyStruct);
                break;
            default:
        }
    }
    
    private void publishProductUpdateByClassForType(SessionProduct sessionProduct)
    {
        int classKey = sessionProduct.getProductKeysStruct().classKey; 
        ChannelKey key = new ChannelKey(ChannelType.CB_PRODUCT_UPDATE_BY_CLASS,new ObjectKeyContainer(new Integer(classKey),classKey));
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this,key,sessionProduct);
        EventChannelAdapterFactory.find().dispatch(event);
    }
    
    /**
     * Populates productKey:sessionClass_set cache
     * @param sessionName
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    public synchronized void populateProductKeyToSessionClassesCache(String sessionName, SessionProductClass[] productClasses)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        Map<Integer, Set<SessionProductClass>> productKeySessionClassRelationMap =
                new HashMap<Integer, Set<SessionProductClass>>(MAP_INITIAL_CAPACITY);
        for(SessionProductClass productClass : productClasses)
        {
            addUnderlyingToProductClassRelationshipToMap(productClass, productKeySessionClassRelationMap);
        }

        updateUnderlyingProductToSessionClassCache(sessionName, productKeySessionClassRelationMap);
    }

    /**
     * processes the session class struct and updates the relation between productKey and 
     * SessionClasses
     * @param productKeyToSessionClassesMap
     * @throws com.cboe.exceptions.SystemException
     * @throws com.cboe.exceptions.CommunicationException
     * @throws com.cboe.exceptions.AuthorizationException
     * @throws com.cboe.exceptions.DataValidationException
     */
    // method was called processSessionClassStruct()
    private void addUnderlyingToProductClassRelationshipToMap(SessionProductClass productClass,
                                                              Map<Integer, Set<SessionProductClass>> productKeyToSessionClassesMap)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException
    {
        int productKey = productClass.getUnderlyingProduct().getProductKey();

        if(productKey > 0)
        {
            updateProductKeyToSessionClassesMap(productKeyToSessionClassesMap, productKey,
                                                productClass);
        }
        else
        {
            // will have to leave this code here, even though it makes a call to the CAS to getProductsForClass() for every Equity and Index class,
            // because in the case of equities and indices that are traded in our sessions, we need to treat the equity Product as the underlying for
            // the SessionProductClass (e.g., for W_STOCK NSB Equity SessionProductClass we should treat the NSB Equity Product as its underlying, so when
            // Product States are changed for all SessionProductClasses that rely on an underlying, the W_STOCK NSB SPC's state will be changed)
            Product[] products = APIHome.findProductQueryAPI().getAllProductsForClass(productClass.getClassKey(), false);
            if(products != null)
            {
                for(Product product : products)
                {
                    updateProductKeyToSessionClassesMap(productKeyToSessionClassesMap, product.getProductKey(),
                                                        productClass);
                }
            }
        }
    }
    
    /**
     * updating the productkey:sessionClass_set cache
     * @param sessionName
     * @param relationMap map of SessionClassStruct and corresponding product structs
     */
    // method was called updateProductKeySessionClassCache()
    private void updateUnderlyingProductToSessionClassCache(String sessionName, Map<Integer,Set<SessionProductClass>> relationMap)
    {
        SessionProductCacheFactory.find(sessionName).loadUnderlyingProductToSessionClassCache(relationMap);
    }

    /**
     * updating the productkey:sessionClass_set cache when sessionclass stuct is available
     * @param sessionStruct
     */
    // method was called updateProductKeySessionClassCache()
    private void updateUnderlyingProductToSessionClassCache(SessionClassStruct sessionStruct)
    {
        SessionProductClass productClass;
        try
        {
            productClass = APIHome.findProductQueryAPI().getClassByKeyForSession(sessionStruct.sessionName, sessionStruct.classStruct.classKey);
        }
        catch(UserException e)
        {
            productClass = SessionProductClassFactory.create(sessionStruct);
            GUILoggerHome.find().exception("Error trying to get SessionProductClass for "+sessionStruct.sessionName+':'+sessionStruct.classStruct.classKey, e);
        }
        SessionProductCacheFactory.find(sessionStruct.sessionName).updateProductKeySessionClassCacheViaChannel(productClass, true);
    }

    /**
     * updates the productKey:SessionClassSet map
     * @param productKeyToSessionClassesMap
     * @param productKey
     * @param productClass
     */
    private void updateProductKeyToSessionClassesMap(
            Map<Integer, Set<SessionProductClass>> productKeyToSessionClassesMap,
            int productKey, SessionProductClass productClass)
    {
        if(!productKeyToSessionClassesMap.containsKey(productKey))
        {
            productKeyToSessionClassesMap
                    .put(productKey, new HashSet<SessionProductClass>(SET_INITIAL_CAPACITY));
        }
        productKeyToSessionClassesMap.get(productKey).add(productClass);
    }
}
