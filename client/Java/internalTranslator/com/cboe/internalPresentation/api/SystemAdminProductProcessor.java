//
// -----------------------------------------------------------------------------------
// Source file: ProductSessionProcessor.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.api;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;

import com.cboe.idl.cmiStrategy.StrategyStruct;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.Strategy;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.presentation.api.ProductQueryOrderedCacheProxy;
import com.cboe.presentation.api.ProductQueryCacheFactory;
import com.cboe.presentation.api.SessionProductCacheFactory;
import com.cboe.presentation.api.TradingSessionFinderFactory;
import com.cboe.presentation.api.SessionProductCache;
import com.cboe.presentation.product.ProductFactoryHome;

import java.util.LinkedList;
import java.util.Iterator;

public class SystemAdminProductProcessor implements EventChannelListener {

    private ProductQueryOrderedCacheProxy productCache;
    private final String Category = this.getClass().getName();

    public SystemAdminProductProcessor () {
        productCache = ProductQueryCacheFactory.find();
        subscribeForProductEvents();
    }

    public void subscribeForProductEvents()
    {
        ChannelKey channelKey;

    channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, new Integer(0));
    EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

    channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, new Integer(0));
    EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

    channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, new Integer(0));
    EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
   }

    public void unsubscribeForProductEvents()
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, new Integer(0));
        EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, new Integer(0));
        EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, new Integer(0));
        EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
    }

   private void updateStrategySessionCache(Strategy newStrategy) {
        LinkedList tradingSessions = TradingSessionFinderFactory.find().getTradingSessions(newStrategy.getProductKeysStruct().classKey);
        if (tradingSessions != null) {
            Iterator iter = tradingSessions.iterator();
            String sessionName = null;
            SessionProductCache sessionProductCache;
            while (iter.hasNext()) {
                sessionName = (String) iter.next();
                sessionProductCache = SessionProductCacheFactory.find(sessionName);
                SessionStrategy sessionStrategyTemp = sessionProductCache.getStrategyByKey(newStrategy.getProductKey());
                if (sessionStrategyTemp != null) {
                    sessionStrategyTemp.updateStrategy(newStrategy);
                    sessionProductCache.
                            updateProductKeySessionClassCacheViaChannel(sessionStrategyTemp,true);
                }
            }
        }
    }

    private void updateProductSessionCache(Product newProduct) {
        LinkedList tradingSessions = TradingSessionFinderFactory.find().getTradingSessions(newProduct.getProductKeysStruct().classKey);
        if (tradingSessions != null) {
            Iterator iter = tradingSessions.iterator();
            String sessionName = null;
            SessionProductCache sessionProductCache;
            while (iter.hasNext()) {
                sessionName = (String) iter.next();
                sessionProductCache = SessionProductCacheFactory.find(sessionName);
                SessionProduct sessionProductTemp = sessionProductCache.getProductByKey(newProduct.getProductKey());
                if (sessionProductTemp != null) {
                    sessionProductTemp.updateProduct(newProduct);
                    sessionProductCache.
                            updateProductKeySessionClassCacheViaChannel(sessionProductTemp,true);
                }
            }
        }
    }

    private void updateClassSessionCache(ProductClass newClass) {
        LinkedList tradingSessions = TradingSessionFinderFactory.find().getTradingSessions(newClass.getClassKey());
        if (tradingSessions != null) {
            Iterator iter = tradingSessions.iterator();
            String sessionName = null;
            SessionProductCache sessionProductCache;
            while (iter.hasNext()) {
                sessionName = (String) iter.next();
                sessionProductCache = SessionProductCacheFactory.find(sessionName);
                SessionProductClass sessionProductClassTemp = sessionProductCache.getClassByKey(newClass.getClassKey());
                if (sessionProductClassTemp != null) {
                    sessionProductClassTemp.updateProductClass(newClass);
                    sessionProductCache.
                            updateProductKeySessionClassCacheViaChannel(sessionProductClassTemp,true);
                }
            }
        }
    }

    public void channelUpdate(ChannelEvent event) {
        int channelType = ((ChannelKey)event.getChannel()).channelType;
        Object eventData = event.getEventData();
        switch(channelType)
        {
            case ChannelType.PQS_UPDATE_PRODUCT:
                if ( GUILoggerHome.find().isDebugOn() ) {
                    GUILoggerHome.find().debug(Category+".channelUpdate()",GUILoggerBusinessProperty.PRODUCT_QUERY,
                            "Product Update: class = " + ((Product)eventData).getProductKeysStruct().classKey
                            + " and product = " + ((Product)eventData).getProductKey());
                }
                Product product = ((Product)eventData);
                if (product.getProductType() == ProductTypes.STRATEGY ){
                    synchronized (productCache) {
                        Strategy strategy = productCache.getStrategyByKey(product.getProductKey());
                        StrategyStruct strategyStruct = new StrategyStruct();
                        strategyStruct.product = product.getProductStruct();
                        strategyStruct.strategyLegs = strategy.getStrategyLegStructs();
                        Strategy newStrategy = ProductFactoryHome.find().create(strategyStruct);
                        productCache.updateStrategy(newStrategy);
                        updateStrategySessionCache(newStrategy);
                        return;
                    }
                }
                productCache.updateProduct(product);
                updateProductSessionCache(product);
                break;
            case ChannelType.PQS_UPDATE_PRODUCT_CLASS:
                if ( GUILoggerHome.find().isDebugOn() ) {
                     GUILoggerHome.find().debug(Category+".channelUpdate()",GUILoggerBusinessProperty.PRODUCT_QUERY,
                    "Product Class Update: class = " + ((ProductClass)eventData).getClassKey());
                }
                ProductClass productClass = ((ProductClass)eventData);
                productCache.updateClass(productClass);
                updateClassSessionCache(productClass);
                break;
            case ChannelType.PQS_STRATEGY_UPDATE:
                if ( GUILoggerHome.find().isDebugOn() ) {
                    GUILoggerHome.find().debug(Category+".channelUpdate()",GUILoggerBusinessProperty.PRODUCT_QUERY,
                            "Strategy update for productKey = " + ((Strategy)eventData).getProductKey()
                            + " and classKey = " + ((Strategy)eventData).getProductKeysStruct().classKey);
                }
                Strategy strategy = ((Strategy)eventData);
                synchronized (productCache) {
                    productCache.updateStrategy(strategy);
                }
                break;
            default:
        }
    }
}
