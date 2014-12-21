// -----------------------------------------------------------------------------------
// Source file: DetailBookDepthImpl.java
//
// PACKAGE: com.cboe.presentation.bookDepth;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.bookDepth;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.api.APIHome;
import com.cboe.interfaces.presentation.bookDepth.DetailBookDepth;
import com.cboe.interfaces.presentation.bookDepth.DetailOrderBookPrice;
import com.cboe.interfaces.presentation.bookDepth.OrderBookPrice;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.idl.cmiMarketData.BookDepthStructV2;
import com.cboe.domain.util.BookDepthStructBuilder;
import org.omg.CORBA.UserException;

public class DetailBookDepthImpl extends AbstractBusinessModel implements DetailBookDepth
{
    private BookDepthStructV2 struct;
    private DetailOrderBookPrice[] detailBuySide;
    private DetailOrderBookPrice[] detailSellSide;
    private OrderBookPrice[] sellSide;
    private OrderBookPrice[] buySide;
    private SessionProduct product;

    DetailBookDepthImpl(BookDepthStructV2 struct)
    {
        this();
        checkParam(struct, "BookDepthStructV2");
        this.struct = struct;
    }

    private DetailBookDepthImpl()
    {
        super();
    }

    /**
     * Gets the buy side of the book for this product
     * @return OrderBookPrice sequence where each element represents a different price for buys
     */
    public DetailOrderBookPrice[] getDetailBuySide()
    {
        if (detailBuySide == null)
        {
            detailBuySide = OrderBookFactory.createDetailOrderBookPrices(this.struct.buySideSequence);
        }
        return detailBuySide;
    }

    /**
     * Gets the sell side of the book for this product
     * @return OrderBookPrice sequence where each element represents a different price for sells
     */
    public DetailOrderBookPrice[] getDetailSellSide()
    {
        if (detailSellSide == null)
        {
            detailSellSide = OrderBookFactory.createDetailOrderBookPrices(this.struct.sellSideSequence);
        }
        return detailSellSide;
    }

    /**
     * Gets the SessionProduct that this book is for
     * @return SessionProduct this book represents
     */
    public SessionProduct getSessionProduct()
    {
        try
        {
            product = APIHome.findProductQueryAPI().getProductByKeyForSession(struct.sessionName, struct.productKeys.productKey);
        }
        catch (UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Product could not be obtained for the DetailBookDepth");
        }

        return product;
    }

    /**
     * Gets the buy side of the book for this product
     * @return OrderBookPrice sequence where each element represents a different price for buys
     */
    public OrderBookPrice[] getBuySide()
    {
        if (buySide == null)
        {
            DetailOrderBookPrice[] detailBuySide = getDetailBuySide();
            buySide = new OrderBookPrice[detailBuySide.length];
            for (int i = 0; i < buySide.length; i++)
            {
                buySide[i] = detailBuySide[i];
            }
        }
        return buySide;
    }

    /**
     * Gets the sell side of the book for this product
     * @return OrderBookPrice sequence where each element represents a different price for sells
     */
    public OrderBookPrice[] getSellSide()
    {
        if (sellSide == null)
        {
            DetailOrderBookPrice [] detailSellSide = getDetailSellSide();
            sellSide = new OrderBookPrice[detailSellSide.length];
            for (int i = 0; i < sellSide.length; i++)
            {
                sellSide[i] = detailSellSide[i];
            }
        }
        return sellSide;
    }

    /**
     * Gets the transaction sequence number
     */
    public int getTransactionSequenceNumber()
    {
        return this.struct.transactionSequenceNumber;
    }

    /**
     * Determines if all prices are included
     * @return True if all prices are included, false if this BookDepth only represents a partial view.
     */
    public boolean isAllPricesIncluded()
    {
        return this.struct.allPricesIncluded;
    }

    public Object clone() throws CloneNotSupportedException
    {
        BookDepthStructV2 clonedStruct = BookDepthStructBuilder.cloneBookDepthStructV2(this.struct);
        return OrderBookFactory.createDetailBookDepth(clonedStruct);
    }
}
