//
// -----------------------------------------------------------------------------------
// Source file: StrategyOrderImpl.java
//
// PACKAGE: com.cboe.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import java.util.HashMap;
import java.util.Map;

import com.cboe.domain.util.NoPrice;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.order.LegOrderDetail;
import com.cboe.interfaces.presentation.order.MutableLegOrderDetail;
import com.cboe.interfaces.presentation.order.MutableOrder;
import com.cboe.interfaces.presentation.order.MutableStrategyOrder;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionStrategyLeg;
import com.cboe.interfaces.presentation.validation.ValidationResult;

public class StrategyOrderImpl extends OrderImpl implements MutableStrategyOrder
{
    private Map legDetailsMap;

    public StrategyOrderImpl()
    {
        this(OrderFactory.createDefaultStrategyOrderStruct());
        _setSide(new Character(Sides.AS_DEFINED));
        newOrder = true;
    }

    public StrategyOrderImpl(OrderStruct orderStruct)
    {
        super(orderStruct);
    }

    /**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        StrategyOrderImpl newImpl;
        if(isCreatedFromStruct())
        {
            newImpl = new StrategyOrderImpl(getStruct());
        }
        else
        {
            newImpl = new StrategyOrderImpl();
            // JMK - 8/26/04 - Set session strategy here only when creating an entirely new order
            // Otherwise it'll zero out leg details that were correctly propagated above from the struct
            newImpl.setSessionStrategy(getSessionStrategy());
        }

        return newImpl;
    }

    public LegOrderDetail[] getLegOrderDetails()
    {
        LegOrderDetail[] legs = new LegOrderDetail[0];
        legs = (LegOrderDetail[])getLegDetailsMap().values().toArray(legs);
        return legs;
    }

    public MutableLegOrderDetail getMutableLegOrder(SessionStrategyLeg leg)
    {
        return (MutableLegOrderDetail)getLegOrder(leg);
    }

    public MutableLegOrderDetail getMutableLegOrder(int productKey)
    {
        return (MutableLegOrderDetail)getLegOrder(productKey);
    }

    /**
     * Since this class hides(overrides) the getLegOrderDetails() method in AbstractOrder class, this method
     * is needed to return the pristine array of leg order details in the strategy order without the side effects
     * caused by this class' implementation of getLegOrderDetails()
     * @return LegOrderDetail[]
     */
    public LegOrderDetail[] getLegOrderDetailsArray()
    {
        return legOrderDetails;
    }

    /**
     * Get the order for the given strategy leg
     * @param leg to get order for
     * @return LegOrderDetail
     * @throws IllegalArgumentException if the given leg does not exist within this
     * order's strategy.
     */
    public LegOrderDetail getLegOrder(SessionStrategyLeg leg)
    {
        return getLegOrder(leg.getProductKey());
    }

    /**
     * Get the order for the given strategy leg product key
     * @param productKey to get order for
     * @return LegOrderDetail
     * @throws IllegalArgumentException if the given leg does not exist within this
     * order's strategy.
     */
    public LegOrderDetail getLegOrder(int productKey)
    {
        Integer legKey = new Integer(productKey);
        LegOrderDetail detail = (LegOrderDetail)getLegDetailsMap().get(legKey);
        if(detail == null)
        {
            throw new IllegalArgumentException("The product key = " +
                                               productKey +
                                               " does not match any legs of the strategy.");
        }
        return detail;
    }

    /**
     * Convenience method for getting the strategy that this order contains
     * @return SessionStrategy
     */
    public SessionStrategy getSessionStrategy()
    {
        return (SessionStrategy)getSessionProduct();
    }

    public void setSessionProduct(SessionProduct sessionProduct)
    {
        //Since this is a StrategyOrder, we are looking for a SessionStrategy instance
//        if(sessionProduct != null  &&  sessionProduct.getProductKeysStruct() != null && sessionProduct instanceof SessionStrategy)
        if(sessionProduct instanceof SessionStrategy)
        {
            setSessionStrategy((SessionStrategy)sessionProduct);
        }
        else
        {
            throw new IllegalArgumentException("sessionProduct must be an instance of SessionStrategy.");

        }
    }

    public void setSessionStrategy(SessionStrategy strategy)
    {
        SessionStrategy oldValue = getSessionStrategy();

        super.setSessionProduct(strategy);

        //Get the legs of the strategy and set up the default coverages and
        //position effects.
        clearLegDetails();
        SessionStrategyLeg[] legs = strategy.getSessionStrategyLegs();

        for(int i = 0; i < legs.length; i++)
        {
            int productKey = legs[i].getProductKey();
            MutableLegOrderDetail detail = LegOrderDetailFactory.createMutableLegOrderDetail(
                    productKey);
            addLegOrderDetail(productKey, detail);
        }

        setModified();
        firePropertyChange(PROPERTY_STRATEGY_PRODUCT, oldValue, strategy);
    }

    void setOrderFields(OrderStruct orderStruct)
    {
        super.setOrderFields(orderStruct);

        clearLegDetails();
        LegOrderDetailStruct[] legs = orderStruct.legOrderDetails;
        for(int i = 0; i < legs.length; i++)
        {
            addLegOrderDetail(legs[i].productKey, LegOrderDetailFactory.createMutableLegOrderDetail(legs[i]));
        }
    }

    public void setLegs(StrategyLegStruct[] legs)
    {
        clearLegDetails();

        if (legs != null)
        {
            for (StrategyLegStruct leg : legs)
            {
                MutableLegOrderDetail legOrderDetail = (MutableLegOrderDetail) LegOrderDetailFactory.createLegOrderDetail(leg.product);
                addLegOrderDetail(leg.product, legOrderDetail);
            }
        }
    }

    protected void clearLegDetails()
    {
        getLegDetailsMap().clear();
    }

    protected void addLegOrderDetail(SessionStrategyLeg leg, MutableLegOrderDetail detail)
    {
        int key = leg.getProductKey();
        addLegOrderDetail(key, detail);
    }

    protected void addLegOrderDetail(int productKey, MutableLegOrderDetail detail)
    {
        Integer legKey = new Integer(productKey);
        getLegDetailsMap().put(legKey, detail);
    }

    protected Map getLegDetailsMap()
    {
        if(legDetailsMap == null)
        {
            legDetailsMap = new HashMap();
        }
        return legDetailsMap;
    }

    protected ValidationResult validatePriceQuantity()
    {
        ValidationResult result = super.validatePriceQuantity();
        if (result.isValid())
        {
            // 8/16/04 jk -- Strategies can now have prices on each leg, but if they do,
            //               the overall price must be NOPRICE and all leg prices must be entered
            //               So - check all leg prices are entered before changing order price to NOPRICE
            if (getPrice().isValuedPrice() && getPrice().toDouble() == 0.0)
            {
                 if (areAllLegPricesSet())
                 {
                     if (this instanceof MutableOrder)
                         ((MutableOrder)this).setPrice(new NoPrice());
                 }
            }
        }
        return result;
    }

    // "Set" means a non-zero value price
    public boolean areAllLegPricesSet()
    {
        LegOrderDetail[] legOrderDetails = getLegOrderDetails();
        for (int i = 0; i < legOrderDetails.length; ++i)
        {
            Price price = legOrderDetails[i].getMustUsePrice();
            if ( !price.isValuedPrice() || price.toDouble() == 0.00)
             {
                 return false;
             }
        }
        return true;
    }

    // "Set" means a non-zero value price
    public boolean areAnyLegPricesSet()
    {
        LegOrderDetail[] legOrderDetails = getLegOrderDetails();
        for (int i = 0; i < legOrderDetails.length; ++i)
        {
            Price price = legOrderDetails[i].getMustUsePrice();
            if (price.isValuedPrice() && price.toDouble() > 0.00)
            {
                return true;
            }
        }
        return false;
    }
}