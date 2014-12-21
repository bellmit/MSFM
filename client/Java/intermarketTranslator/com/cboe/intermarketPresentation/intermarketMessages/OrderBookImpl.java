//
// ------------------------------------------------------------------------
// FILE: OrderBookImpl.java
// 
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.OrderBookStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderBook;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.interfaces.presentation.order.OrderContingency;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.domain.Price;
import com.cboe.presentation.order.OrderIdFactory;
import com.cboe.presentation.order.OrderContingencyFactory;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.APIHome;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.domain.util.StructBuilder;

/**
 * @author torresl@cboe.com
 */
class OrderBookImpl implements OrderBook
{
    protected OrderId orderId;
    protected int originalQuantity;
    protected int remainingQuantity;
    protected int classKey;
    protected int productKey;
    protected short productType;
    protected char side;
    protected Price price;
    protected char timeInForce;
    protected DateTime receivedTime;
    protected OrderContingency contingency;
    protected char orderOriginType;
    protected short state;
    protected short orderNBBOProtectionType;
    protected String optionalData;
    protected char tradableType;

    protected Product product;
    protected ProductClass productClass;
    protected OrderBookStruct orderBookStruct;

    public OrderBookImpl(OrderBookStruct orderBookStruct)
    {
        super();
        this.orderBookStruct = orderBookStruct;
        initialize();
    }

    // This constructor is only provided to construct table rows as nothing gets initialized and NullPointerExceptions
    // will be thrown by operating on a object instantiated with this constructor.
    public OrderBookImpl()
    {
        super();
    }
    private void initialize()
    {
        orderId = OrderIdFactory.createOrderId(orderBookStruct.orderId);
        originalQuantity = orderBookStruct.originalQuantity;
        remainingQuantity = orderBookStruct.remainingQuantity;
        classKey = orderBookStruct.classKey;
        productKey= orderBookStruct.productKey;
        productType = orderBookStruct.productType;
        side = orderBookStruct.side;
        price = DisplayPriceFactory.create(orderBookStruct.price);
        timeInForce = orderBookStruct.timeInForce;
        receivedTime = new DateTimeImpl(orderBookStruct.receivedTime);
        contingency = OrderContingencyFactory.createOrderContingency(orderBookStruct.contingency);
        orderOriginType = orderBookStruct.orderOriginType;
        state = orderBookStruct.state;
        orderNBBOProtectionType = orderBookStruct.orderNBBOProtectionType;
        optionalData = orderBookStruct.optionalData;
        tradableType = orderBookStruct.tradableType;
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKey(productKey);
            productClass = APIHome.findProductQueryAPI().getProductClassByKey(classKey);
        }
        catch (SystemException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        catch (CommunicationException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        catch (AuthorizationException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        catch (DataValidationException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
        }
    }

    public OrderId getOrderId()
    {
        return orderId;
    }

    public int getOriginalQuantity()
    {
        return originalQuantity;
    }

    public int getRemainingQuantity()
    {
        return remainingQuantity;
    }

    public int getClassKey()
    {
        return classKey;
    }

    public int getProductKey()
    {
        return productKey;
    }

    public short getProductType()
    {
        return productType;
    }

    public char getSide()
    {
        return side;
    }

    public Price getPrice()
    {
        return price;
    }

    public char getTimeInForce()
    {
        return timeInForce;
    }

    public DateTime getReceivedTime()
    {
        return receivedTime;
    }

    public OrderContingency getContingency()
    {
        return contingency;
    }

    public char getOrderOriginType()
    {
        return orderOriginType;
    }

    public short getState()
    {
        return state;
    }

    public short getOrderNBBOProtectionType()
    {
        return orderNBBOProtectionType;
    }

    public String getOptionalData()
    {
        return optionalData;
    }

    public char getTradableType()
    {
        return tradableType;
    }

    public OrderBookStruct toStruct()
    {
        return orderBookStruct;
    }

    public Product getProduct()
    {
        return product;
    }

    public ProductClass getProductClass()
    {
        return productClass;
    }
}
