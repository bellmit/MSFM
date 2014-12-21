//
// -----------------------------------------------------------------------------------
// Source file: TradableImpl.java
//
// PACKAGE: com.cboe.internalPresentation.bookDepth
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.bookDepth;

import org.omg.CORBA.UserException;

import com.cboe.idl.orderBook.TradableStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.internalPresentation.bookDepth.Tradable;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.user.ExchangeFirmFactory;
import com.cboe.presentation.util.CBOEIdImpl;

/*
 * Implements the contract for a Tradable from the OrderBook.
 */
public class TradableImpl implements Tradable
{
    protected TradableStruct struct;
    protected Price price;
    protected Product product = null;
    protected ExchangeFirm firm = null;

    /**
     * Default constructor
     * @param struct to represent
     */
    public TradableImpl(TradableStruct struct)
    {
        this.struct = struct;

        if(struct.price != null)
        {
            this.price = DisplayPriceFactory.create(struct.price);
        }
        else
        {
            this.price = DisplayPriceFactory.create(0.00);
        }
    }

    /**
     * Gets the firm of the tradable
     */
    public ExchangeFirm getFirm()
    {
        if(firm == null)
        {
            firm = ExchangeFirmFactory.createExchangeFirm(struct.firm);
        }
        return firm;
    }

    /**
     * Gets the cboe id for this tradable
     */
    public CBOEId getId()
    {
        return new CBOEIdImpl(struct.tradableId);
    }

    /**
     * Gets the price for this price
     * @return price this price represents
     */
    public Price getPrice()
    {
        return price;
    }

    /**
     * Gets the Product that this tradable is for
     * @return Product this book represents
     */
    public Product getProduct()
    {
        if(product == null)
        {
            try
            {
                product = APIHome.findProductQueryAPI().getProductByKey(struct.productKey);
            }
            catch(UserException e)
            {
                DefaultExceptionHandlerHome.find().process(e, "Product could not be obtained for the Tradable");
            }
        }
        return product;
    }

    /**
     * Gets the remaining qty for this tradable
     * @return Integer with remaining qty
     */
    public Integer getRemainingQuantity()
    {
        return new Integer(struct.remainingQuantity);
    }

    /**
     * Gets the side of this tradable
     * @return char of this tradables side
     */
    public char getSide()
    {
        return struct.side;
    }

    /**
     * Gets the type of this tradable
     * @return char of this tradable type
     */
    public char getTradableType()
    {
        return struct.tradableType;
    }

    /**
     * Gets the user assigned id of the tradable
     */
    public String getUserAssignedId()
    {
        return struct.userAssignedId;
    }

    /**
     * Gets the user id of the tradable
     */
    public String getUserId()
    {
        return struct.userId;
    }
}
