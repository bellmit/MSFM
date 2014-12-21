//
// -----------------------------------------------------------------------------------
// Source file: ExchangeProductMappingCache.java
//
// PACKAGE: com.cboe.internalPresentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.api;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiConstants.ProductTypes;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.common.exchange.ExchangeFactory;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.ProductType;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.user.ExchangeFactoryImpl;

/**
 * This singleton class provides the caching of exchange:underlyingProducts mapping.
 */
public class ExchangeProductMappingCache implements EventChannelListener
{

    private static final ExchangeProductMappingCache SELF_INSTANCE = new ExchangeProductMappingCache();
    private static final int MAP_INITIAL_CAPACITY = 50;
    private static final int SET_INITIAL_CAPACITY = 100;

    private boolean isInitialized = false;

    private final Map<String, Set<Product>> exchangeToProductMap = new HashMap<String, Set<Product>>(MAP_INITIAL_CAPACITY);

    private ExchangeProductMappingCache()
    {
    }

    /**
     * returns the instance of exchangecache
     * @return ExchangeProductMappingCache
     */
    public static ExchangeProductMappingCache getInstance()
    {
        return SELF_INSTANCE;
    }

    /**
     * Retrieves the products corresponding to the exchangeName
     * @param exchangeName
     * @return products underlying product corresponding to the exchange.
     */
    public synchronized Product[] getProductsForExchange(String exchangeName)
    {
        if(exchangeName == null)
        {
            throw new IllegalArgumentException("Exchange name parameter cannot be null");
        }
        
        if(!isInitialized)
        {
            throw new IllegalStateException("ExchangeProductMappingCache Not initialized");
        }
        
        Product[] products;
        Set<Product> productSet = exchangeToProductMap.get(exchangeName);
        if(productSet != null)
        {
            products = productSet.toArray(new Product[productSet.size()]);
        }
        else
        {
            products = new Product[0];
        }
        return products;
    }

    /**
     * Initializes and Populates the exchange:product mapping cache.
     * @throws DataValidationException
     * @throws AuthorizationException
     * @throws CommunicationException
     * @throws SystemException
     * @throws NotFoundException
     */
    public void initializeCache() throws
            DataValidationException, AuthorizationException, CommunicationException,
            SystemException, NotFoundException
    {
        isInitialized = false;

        initialize();

        initializeCacheAllUnderlyings();

        isInitialized = true;
    }

    /**
     * Initializes populates the exchange:product mapping cache for all isUnderlying() based products.
     * @throws DataValidationException
     * @throws AuthorizationException
     * @throws CommunicationException
     * @throws SystemException
     * @throws NotFoundException
     */
    public void initializeCacheAllUnderlyings() throws DataValidationException,
            AuthorizationException, CommunicationException, SystemException, NotFoundException
    {
        ProductType[] productTypes = SystemAdminAPIFactory.find().getAllProductTypes();
        ProductClass[] productClasses;
        for(ProductType productType : productTypes)
        {
            productClasses = SystemAdminAPIFactory.find().getAllClassesForType(productType.getType());
            if(productClasses != null && productClasses.length > 0)
            {
                if(isUnderlying(productType.getType()))
                {
                    populateCache(productClasses);
                }
            }
        }
    }

    /**
     * Subscribes for events when new ProductClass/Product is introduced into the system
     */
    public void subscribeForEvents()
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, 0);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, 0);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

    }

    /**
     * Unsubscribes for events when new ProductClass/Product is introduced into the system
     */
    public void unsubscribeForEvents()
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, 0);
        EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, 0);
        EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

    }

    /**
     * clears the underlying map of exchange cache 
     */
    public synchronized void clear()
    {
        isInitialized = false;
        exchangeToProductMap.clear();
    }


    /**
     * Event channel listener implementation
     */
    public void channelUpdate(ChannelEvent channelEvent)
    {
        int channelType = ((ChannelKey) channelEvent.getChannel()).channelType;
        Object eventData = channelEvent.getEventData();
        ProductClass productClass = null;
        switch(channelType)
        {
            case ChannelType.PQS_UPDATE_PRODUCT_CLASS:

                productClass = (ProductClass) eventData;
                updateViaChannel(productClass);
                break;

            case ChannelType.PQS_UPDATE_PRODUCT:
                Product product = (Product) eventData;
                try
                {
                    productClass = SystemAdminAPIFactory.find()
                            .getProductClassByKey(product.getProductKeysStruct().classKey);
                }
                catch(UserException e)
                {
                    DefaultExceptionHandlerHome.find().process(e);
                }
                if(productClass != null)
                {
                    updateViaChannel(productClass);
                }
                
                break;

            default :
                break;
        }
    }

    /**
     * verifies the input and calls appropriate upadate method.
     * @param productClass productClass to be updated in the cache.
     */
    private void updateViaChannel(ProductClass productClass)
    {
        if(isUnderlying(productClass.getProductType()))
        {
            String primaryExchange = productClass.getPrimaryExchange();
            update(primaryExchange,
                   productClass.getUnderlyingProduct(), true);
        }
        else
        {
            try
            {
                update(productClass, true);
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
    }

    /**
     * Initializes the exchange:product_set cache with available exchange names including undefined
     * exchange
     */
    private synchronized void initialize()
    {
        exchangeToProductMap.clear();
        ExchangeFactory exchangeFactory = new ExchangeFactoryImpl();
        Exchange[] exchanges = exchangeFactory.getExchangeList();

        for(Exchange exchange : exchanges)
        {
            exchangeToProductMap.put(exchange.getExchange(), null);
        }

        exchangeToProductMap.put(exchangeFactory.getUnspecifiedExchange().getExchange(),
                                 new HashSet<Product>(SET_INITIAL_CAPACITY));
    }

    /**
     * Populates the exchange-product relation cache.
     * @param productClasses product classes for populating the cache.
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     */
    private void populateCache(ProductClass[] productClasses) throws
            SystemException, CommunicationException, AuthorizationException,
            DataValidationException
    {
        String primaryExchange;
        Product[] products;
        for(ProductClass productClass : productClasses)
        {
            primaryExchange = productClass.getPrimaryExchange();
            products = SystemAdminAPIFactory.find().getAllProductsForClass(productClass.getClassKey(), true);
            if(products != null)
            {
                for(Product product : products)
                {
                    update(primaryExchange, product, false);
                }
            }
        }
    }

    /**
     * updates the exchange-product relation map.
     * @param primaryExchange
     * @param underlyingProduct
     * @param isUpdate : if an update, then we need to remove the product from the set, since the 
     * hashcode remains same, though other attributes may have a change.
     */
    private synchronized void update(String primaryExchange,
                                     Product underlyingProduct,
                                     boolean isUpdate)
    {
        Set<Product> productSet;
        if(primaryExchange != null && primaryExchange.trim().length() > 0)
        {
            productSet = exchangeToProductMap.get(primaryExchange);
            if(productSet == null)
            {
                productSet = new HashSet<Product>(SET_INITIAL_CAPACITY);
                exchangeToProductMap.put(primaryExchange, productSet); 
            }
        }
        else
        {
            productSet = exchangeToProductMap.get(ExchangeFactoryImpl.UNSPECIFIED_EXCHANGE.getExchange());
        }
        if(isUpdate)
        {
            productSet.remove(underlyingProduct);
        }
        productSet.add(underlyingProduct);
    }

    /**
     * Recursively updates the cache with products of type equity/index/commodity/debt
     * @param productClass product class whose product to be updated in the cache
     * @param isUpdate
     */
    private void update(ProductClass productClass,
                        boolean isUpdate)
            throws DataValidationException, AuthorizationException, CommunicationException,
            SystemException, NotFoundException
    {
        if(productClass != null)
        {

            Product underlyingProduct = productClass.getUnderlyingProduct();
            ProductClass productClassToUpdate = SystemAdminAPIFactory.find()
                    .getProductClassByKey(underlyingProduct.getProductKeysStruct().classKey);

            if(isUnderlying(productClassToUpdate.getProductType()))
            {
                update(productClassToUpdate.getPrimaryExchange(),
                       underlyingProduct, isUpdate);
            }
            else
            {
                update(productClassToUpdate, isUpdate);
            }
        }
    }

    /**
     * returns true if product type is considered an underlying asset (not derived)
     * @param productType
     * @return boolean
     */
    private boolean isUnderlying(short productType)
    {
        return (productType == ProductTypes.EQUITY
                || productType == ProductTypes.INDEX
                || productType == ProductTypes.COMMODITY
                || productType == ProductTypes.DEBT);
    }

    /**
     * returns the initialized status of ExchangeProductMappingCache. 
     * @return boolean status
     */
    public boolean isInitialized()
    {
        return isInitialized;
    }
}
