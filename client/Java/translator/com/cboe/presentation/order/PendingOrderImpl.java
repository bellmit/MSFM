//
// -----------------------------------------------------------------------------------
// Source file: PendingOrderImpl.java
//
// PACKAGE: com.cboe.presentation.wrappers;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.PendingOrderStruct;

import com.cboe.interfaces.presentation.product.PendingNameContainer;
import com.cboe.interfaces.presentation.order.PendingOrder;
import com.cboe.interfaces.presentation.order.Order;

import com.cboe.presentation.product.PendingNameFactory;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

/**
 * This class wraps <code>PendingOrderStruct</code> and provides various helper methods.
 */
class PendingOrderImpl extends AbstractBusinessModel implements PendingOrder
{
    private PendingOrderStruct struct;
    private Order currentOrder;
    private Order pendingOrder;
    private PendingNameContainer nameContainer;

    /**
     * Constructor to initialize with struct
     * @param struct com.cboe.idl.cmiOrder.PendingOrderStruct
     */
    protected PendingOrderImpl(PendingOrderStruct struct)
    {
        super();

        checkParam(struct, "PendingOrderStruct");
        this.struct = struct;

        nameContainer = PendingNameFactory.create(struct.pendingProductName);

        currentOrder = OrderFactory.createOrder(struct.currentOrder);
        pendingOrder = OrderFactory.createOrder(struct.pendingOrder);
    }

    public Object clone() throws CloneNotSupportedException
    {
        PendingOrderImpl newImpl = new PendingOrderImpl(getStruct());
        return newImpl;
    }

    /**
     * Returns the product key, current order key and pending order key as a hashCode.
     * @return product key, current order key and pending order key  as hash code
     */
    public int hashCode()
    {
        int value = getPendingName().getProduct().getProductKey() +
                getCurrentOrder().getOrderId().getCboeId().hashCode() +
                getPendingOrder().getOrderId().getCboeId().hashCode();

        return value;
    }

    /**
     * Returns the product name as the String
     * @return account name. If inactive text will be appended also.
     */
    public String toString()
    {
        return getPendingName().getProduct().toString();
    }

    /**
     * Determines if this object is equal to passed object. Comparison will be done
     * on instance, type, product, current order and pending order.
     * @param obj to compare this with
     * @return True if all equal, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            if(obj instanceof PendingOrder)
            {
                PendingOrder castedObj = (PendingOrder)obj;

                if(getPendingName().getProduct().equals(castedObj.getPendingName().getProduct()) &&
                    getCurrentOrder().equals(castedObj.getCurrentOrder()) &&
                    getPendingOrder().equals(castedObj.getPendingOrder()))
                {
                    isEqual = true;
                }
            }
        }
        return isEqual;
    }

    /**
     * Gets the PendingName
     * @return PendingNameContainer
     */
    public PendingNameContainer getPendingName()
    {
        return nameContainer;
    }

    /**
     * Gets the current order
     * @return current order
     */
    public Order getCurrentOrder()
    {
        return currentOrder;
    }

    /**
     * Gets the pending order
     * @return pending order
     */
    public Order getPendingOrder()
    {
        return pendingOrder;
    }

    /**
     * Get the underlying struct
     * @deprecated here for backwards compatibility only
     */
    public PendingOrderStruct getStruct()
    {
        return struct;
    }
}