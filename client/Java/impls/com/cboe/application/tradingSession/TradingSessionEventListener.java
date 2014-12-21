package com.cboe.application.tradingSession;

import com.cboe.application.cache.CacheFactory;
import com.cboe.application.tradingSession.cache.TradingSessionCacheKeyFactory;

import com.cboe.domain.util.ClientProductStructBuilder;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;


import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.ArrayList;

public class TradingSessionEventListener extends TradingSessionEventBaseListener
{
    public TradingSessionEventListener(String __sessionName)
    {
        super(__sessionName);
    }

    private void updateClass(SessionClassStruct classStruct)
    {
        CacheFactory.updateSessionClassCache(sessionName, classStruct);
    }

    private void updateProduct(SessionProductStruct productStruct)
    {
        CacheFactory.updateSessionProductCache(sessionName, productStruct);
    }

    private void updateStrategy(SessionStrategyStruct strategyStruct)
    {
        // Update the cache
        StrategyStruct strategy = new StrategyStruct();
        strategy.product = strategyStruct.sessionProductStruct.productStruct;
        int legLength = strategyStruct.sessionStrategyLegs.length;
        StrategyLegStruct[] strategyLegs = new StrategyLegStruct[legLength];
        for (int i =0 ; i<legLength; i++) {
            StrategyLegStruct strategyLeg = new StrategyLegStruct();
            strategyLeg.product = strategyStruct.sessionStrategyLegs[i].product;
            strategyLeg.ratioQuantity = strategyStruct.sessionStrategyLegs[i].ratioQuantity;
            strategyLeg.side = strategyStruct.sessionStrategyLegs[i].side;
            strategyLegs[i] = strategyLeg;
        }
        strategy.strategyLegs = strategyLegs;
        strategy.strategyType = strategyStruct.strategyType;

        CacheFactory.updateStrategyCache(strategy);
        CacheFactory.updateSessionStrategyCache(sessionName,strategyStruct);
    }



    protected void dispatchClass(SessionClassStruct classStruct)
    {
        SessionClassStruct oldClass = (SessionClassStruct) CacheFactory.getSessionClassCache(sessionName).find(TradingSessionCacheKeyFactory.getPrimaryClassKey(), Integer.valueOf(classStruct.classStruct.classKey));
        // Update the cache
        updateClass(classStruct);
        // Inform the clients of the change
        super.dispatchClass(classStruct);
    }

    protected void dispatchProduct(SessionProductStruct productStruct)
    {
        // Update the cache
        updateProduct(productStruct);
        // Inform the clients of the change.
        super.dispatchProduct(productStruct);
    }

    protected void dispatchStrategy(SessionStrategyStruct strategyStruct)
    {
        // Update the cache
        updateStrategy(strategyStruct);
        // Inform the clients of the change
        super.dispatchStrategy(strategyStruct);
    }

    protected void dispatchClassState(ClassStateStruct classState)
    {
        SessionClassStruct oldClass = (SessionClassStruct) CacheFactory.getSessionClassCache(sessionName).find(TradingSessionCacheKeyFactory.getPrimaryClassKey(), Integer.valueOf(classState.classKey));

        // Has this modification already been seen/superceded?
        if ((oldClass == null) || (classState.classStateTransactionSequenceNumber > oldClass.classStateTransactionSequenceNumber))
        {
            // If the class is in the cache, update it
            if (oldClass != null)
            {
                // It's important to clone a new cache member so that in-flight cache elements are not overwritten
                SessionClassStruct newClass = ClientProductStructBuilder.cloneSessionClassStruct(oldClass);
                newClass.classState = classState.classState;
                newClass.classStateTransactionSequenceNumber = classState.classStateTransactionSequenceNumber;

                // Update the cache
                updateClass(newClass);
            }

            // Inform the clients of the state change
            super.dispatchClassState(classState);
        }
    }

    protected void dispatchProductStates(ProductStateStruct[] productStates)
    {
        ArrayList<ProductStateStruct> productStateEvents = new ArrayList<ProductStateStruct>();
        SessionProductStruct oldProduct = null;
        for (int i=0; i < productStates.length; i++)
        {
            oldProduct = (SessionProductStruct) CacheFactory.getSessionProductCache(sessionName).find(TradingSessionCacheKeyFactory.getPrimaryProductKey(), Integer.valueOf(productStates[i].productKeys.productKey));
            if ((oldProduct == null) || (productStates[i].productStateTransactionSequenceNumber > oldProduct.productStateTransactionSequenceNumber))
            {
                // If the product is in the cache, update it
                if (oldProduct != null)
                {
                    // It's important to clone a new cache member so that in-flight cache elements are not overwritten
                    SessionProductStruct newProduct = ClientProductStructBuilder.cloneSessionProduct(oldProduct);
                    newProduct.productState = productStates[i].productState;
                    newProduct.productStateTransactionSequenceNumber = productStates[i].productStateTransactionSequenceNumber;

                    // Update the cache
                    updateProduct(newProduct);
                }
                productStateEvents.add(productStates[i]);
            }
            if ((oldProduct != null) && (productStates[i].productStateTransactionSequenceNumber <= oldProduct.productStateTransactionSequenceNumber))
            {
                StringBuilder dropped = new StringBuilder(110);
                dropped.append("Product state is dropped, oldProductStateSeq=").append(oldProduct.productStateTransactionSequenceNumber)
                       .append(" newProductStateSeq=").append(productStates[i].productStateTransactionSequenceNumber)
                       .append(" productKey=").append(productStates[i].productKeys.productKey);
                Log.information(dropped.toString());
            }
        }
        ProductStateStruct[] array = new ProductStateStruct[productStateEvents.size()];
        super.dispatchProductStates(productStateEvents.toArray(array));
    }
}
