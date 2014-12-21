//
// -----------------------------------------------------------------------------------
// Source file: OrderDetailImpl.java
//
// PACKAGE: com.cboe.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.idl.cmiOrder.OrderDetailStruct;

import com.cboe.interfaces.presentation.order.OrderDetail;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.product.ProductName;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.product.ProductNameFactory;

class OrderDetailImpl extends AbstractBusinessModel implements OrderDetail
{
    private ProductName productName;
    private Short statusChangeReason;
    private Order order;

    private String productRenderString;

    public OrderDetailImpl()
    {
        super();
        productRenderString = "";
    }

    public OrderDetailImpl(OrderDetailStruct struct)
    {
        this();
        setStruct(struct);
        productRenderString = null;
    }

    public Order getOrder()
    {
        return order;
    }

    public ProductName getProductName()
    {
        return productName;
    }

    public Short getStatusChange()
    {
        return statusChangeReason;
    }

    /**
     * Gets the underlying struct
     * @return OrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public OrderDetailStruct getStruct()
    {
        OrderDetailStruct newStruct = new OrderDetailStruct();
        newStruct.statusChange = statusChangeReason.shortValue();
        newStruct.productInformation = productName.getStruct();
        newStruct.orderStruct = order.getStruct();
        return newStruct;
    }

    public Product getContainedProduct()
    {
        return order.getSessionProduct();
    }

    public ProductClass getContainedProductClass()
    {
        return order.getSessionProductClass();
    }

    public String getProductRenderString()
    {
        if(productRenderString == null)
        {
            productRenderString = getContainedProduct().toString();
        }
        return productRenderString;
    }

    private void setStruct(OrderDetailStruct struct)
    {
        productName = ProductNameFactory.createProductName(struct.productInformation);
        statusChangeReason = new Short(struct.statusChange);
        order = OrderFactory.createOrder(struct.orderStruct);
    }
}