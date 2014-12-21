//
// -----------------------------------------------------------------------------------
// Source file: AbstractCacheElement.java
//
// PACKAGE: com.cboe.presentation.api.productcache
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.productcache;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.*;

import com.cboe.interfaces.presentation.marketData.express.V4MarketData;

/**
 * Abstract class that implements some methods of the ProductCacheElement interface.
 * 
 * 
 * @author Eric Maheo
 *
 * @param <T> represents a struct passed by the consumer.
 */
abstract class AbstractCacheElement<T extends V4MarketData> implements ProductCacheElement<T>
{

    /** Holds the classKey value for this product. */
    private final int classKey;
    /** Holds the productKey value for this product. */
    private final int productKey;
    /** 
     * Holds the product for different market exchange.
     * It respects the sequence order of the product updated for 
     * each market exchange (ie: it won't update a product in cache by an obsolete one).
     */
    private final Map<String, T> marketExchanges;
    
    /** Lock that guards the Map marketExchanges in order to guarentee thread safety.*/
    private final Lock lockMarketExchanges = new ReentrantLock();
    
    /**
     * Template Array is being used in the toArray(T[0]) and require to have its 
     * type set by the concreate implementation of this class.
     * So the method initTemplateArray provide the type by its subclass implementation.
     */
    private final T[] templateArray;
    
    /**
     * Constructor that must be called by extended classes.
     * 
     * @param cKey classKey for a product.
     * @param pKey productKey for a product.
     * 
     */
    public AbstractCacheElement(int cKey, int pKey){
       super();
       classKey = cKey;
       productKey = pKey;
       marketExchanges = new HashMap<String, T>();
       templateArray = initTemplateArray();
    }

    /**
     * {@inheritDoc}.
     */   
    @Override
    public T addProductForExchangeMarket(String exchange, T struct)
    {
        T oldstruct = null;
        lockMarketExchanges.lock();
        try{
            oldstruct = marketExchanges.put(exchange, struct);
        }
        finally{
            lockMarketExchanges.unlock();
        }
        return oldstruct;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int getClassKey()
    {
        return classKey;
    }

    /**
     * {@inheritDoc}.
     */
     @Override
    public T getProductForExchangeMarket(String exchange)
    {
        T struct = null;
        lockMarketExchanges.lock();
        try{
            struct = marketExchanges.get(exchange);
        }
        finally{
            lockMarketExchanges.unlock();
        }
        return struct;
    }
     
     /**
      * {@inheritDoc}.
      */
     @Override
     public T[] getProductForAllExchangeMarket()
     {
         T[] struct=null;
         lockMarketExchanges.lock();
         try{
             struct = marketExchanges.values().toArray(templateArray);
         }
         finally{
             lockMarketExchanges.unlock();
         }
         return struct;
     }

     /**
      * {@inheritDoc}.
      */
    @Override
    public int getProductKey()
    {
        return productKey;
    }
    
    /**
     * {@inheritDoc}.
     * 
     */
    @Override
    public int hashCode(){
        return productKey;
    }
    
    /**
     * The purpose of this method is to provide a type that will be 
     * used in the toArray(T[]) call.
     * The concreate implementation should return an array of type with 0 element.
     * 
     * @return an array of 0 element like new String[0].
     * 
     */
    protected abstract T[] initTemplateArray();
}
