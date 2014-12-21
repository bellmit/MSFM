// -----------------------------------------------------------------------------------
// Source file: BookDepthStructBuilder.java
//
// PACKAGE: com.cboe.domain.util
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.util;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiMarketData.OrderBookPriceViewStruct;
import com.cboe.idl.cmiConstants.OrderBookPriceViewTypes;
import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderBookDetailPriceStruct;
import com.cboe.idl.cmiIntermarketMessages.OrderBookStruct;
import com.cboe.idl.cmiConstants.OrderStates;
import com.cboe.idl.cmiConstants.OrderNBBOProtectionTypes;

/**
 * A helper that makes it easy to create valid BookDepth CORBA structs.  The structs created
 * by the methods of this class have default values for all attributes.
 */
public class BookDepthStructBuilder
{
    public BookDepthStructBuilder()
    {
        super();
    }

    /**
     * Creates a default instance of a BookDepthStruct.
     *
     * @return default instance of struct
     */
    public static BookDepthStruct buildBookDepthStruct()
    {
        BookDepthStruct struct = new BookDepthStruct();
        struct.allPricesIncluded = false;
        struct.buySideSequence = new OrderBookPriceStruct[0];
        struct.sellSideSequence = new OrderBookPriceStruct[0];
        struct.productKeys = ClientProductStructBuilder.buildProductKeysStruct();
        struct.sessionName = "";
        struct.transactionSequenceNumber = 0;
        return struct;
    }

    /**
     * Creates a default instance of a OrderBookPriceStruct.
     *
     * @return default instance of struct
     */
    public static OrderBookPriceStruct buildOrderBookPriceStruct()
    {
        OrderBookPriceStruct struct = new OrderBookPriceStruct();
        struct.contingencyVolume = 0;
        struct.price = StructBuilder.buildPriceStruct();
        struct.totalVolume = 0;
        return struct;
    }

    /**
     * Creates a default instance of a BookDepthStructV2.
     *
     * @return default instance of struct
     */
    public static BookDepthStructV2 buildBookDepthStructV2()
    {
        BookDepthStructV2 struct = new BookDepthStructV2();
        struct.allPricesIncluded = false;
        struct.buySideSequence = new OrderBookPriceStructV2[0];
        struct.sellSideSequence = new OrderBookPriceStructV2[0];
        struct.productKeys = ClientProductStructBuilder.buildProductKeysStruct();
        struct.sessionName = "";
        struct.transactionSequenceNumber = 0;
        return struct;
    }

    /**
     * Creates a default instance of a OrderBookPriceStructV2.
     *
     * @return default instance of struct
     */
    public static OrderBookPriceStructV2 buildOrderBookPriceStructV2()
    {
        OrderBookPriceStructV2 struct = new OrderBookPriceStructV2();
        struct.views = new OrderBookPriceViewStruct[0];
        struct.price = StructBuilder.buildPriceStruct();
        return struct;
    }

    /**
     * Creates a default instance of a OrderBookPriceViewStruct.
     *
     * @return default instance of struct
     */
    public static OrderBookPriceViewStruct buildOrderBookPriceViewStruct()
    {
        OrderBookPriceViewStruct struct = new OrderBookPriceViewStruct();
        struct.orderBookPriceViewType = OrderBookPriceViewTypes.BY_ORIGIN_TYPE;
        struct.viewSequence = new MarketVolumeStruct[0];
        return struct;
    }

    /**
     * Clones a BookDepthStruct.
     *
     * @return cloned struct
     */
    public static BookDepthStruct cloneBookDepthStruct(BookDepthStruct bookDepthStruct)
    {
        BookDepthStruct result = null;
        if (bookDepthStruct != null)
        {
            result = new BookDepthStruct();
            result.allPricesIncluded = bookDepthStruct.allPricesIncluded;
            result.buySideSequence = new OrderBookPriceStruct[bookDepthStruct.buySideSequence.length];
            result.sellSideSequence = new OrderBookPriceStruct[bookDepthStruct.sellSideSequence.length];
            result.productKeys = bookDepthStruct.productKeys;
            result.sessionName = bookDepthStruct.sessionName;
            result.transactionSequenceNumber = bookDepthStruct.transactionSequenceNumber;

            for (int i = 0; i < bookDepthStruct.buySideSequence.length; i++)
            {
                  result.buySideSequence[i] = cloneOrderBookPriceStruct(bookDepthStruct.buySideSequence[i]);
            }

            for (int i = 0; i < bookDepthStruct.sellSideSequence.length; i++)
            {
                result.sellSideSequence[i] = cloneOrderBookPriceStruct(bookDepthStruct.sellSideSequence[i]);
            }
        }
        return result;
    }

    /**
     * Clones a BookDepthStructV2.
     *
     * @return cloned struct
     */
    public static BookDepthStructV2 cloneBookDepthStructV2(BookDepthStructV2 bookDepthStruct)
    {
        BookDepthStructV2 result = null;
        if (bookDepthStruct != null)
        {
            result = new BookDepthStructV2();
            result.allPricesIncluded = bookDepthStruct.allPricesIncluded;
            result.buySideSequence = new OrderBookPriceStructV2[bookDepthStruct.buySideSequence.length];
            result.sellSideSequence = new OrderBookPriceStructV2[bookDepthStruct.sellSideSequence.length];
            result.productKeys = bookDepthStruct.productKeys;
            result.sessionName = bookDepthStruct.sessionName;
            result.transactionSequenceNumber = bookDepthStruct.transactionSequenceNumber;

            for (int i = 0; i < bookDepthStruct.buySideSequence.length; i++)
            {
                result.buySideSequence[i] = cloneOrderBookPriceStructV2(bookDepthStruct.buySideSequence[i]);
            }

            for (int i = 0; i < bookDepthStruct.sellSideSequence.length; i++)
            {
                result.sellSideSequence[i] = cloneOrderBookPriceStructV2(bookDepthStruct.sellSideSequence[i]);
            }
        }
        return result;
    }

    /**
     * Clones a OrderBookPriceStruct.
     *
     * @return cloned struct
     */
    public static OrderBookPriceStruct cloneOrderBookPriceStruct(OrderBookPriceStruct orderBookPriceStruct)
    {
        OrderBookPriceStruct result = null;
        if (orderBookPriceStruct != null)
        {
            result = new OrderBookPriceStruct();
            result.contingencyVolume = orderBookPriceStruct.contingencyVolume;
            result.price = StructBuilder.clonePrice(orderBookPriceStruct.price);
            result.totalVolume = orderBookPriceStruct.totalVolume;
        }
        return result;
    }

    /**
     * Clones a OrderBookPriceStructV2.
     *
     * @return cloned struct
     */
    public static OrderBookPriceStructV2 cloneOrderBookPriceStructV2(OrderBookPriceStructV2 orderBookPriceStruct)
    {
        OrderBookPriceStructV2 result = null;
        if (orderBookPriceStruct != null)
        {
            result = new OrderBookPriceStructV2();
            result.views = new OrderBookPriceViewStruct[orderBookPriceStruct.views.length];
            result.price = StructBuilder.clonePrice(orderBookPriceStruct.price);
            for (int i = 0; i < result.views.length; i++)
            {
                result.views[i] = cloneOrderBookPriceViewStruct(orderBookPriceStruct.views[i]);
            }
        }
        return result;
    }
    /**
     * Clones a OrderBookPriceStruct.
     *
     * @return cloned struct
     */
    public static OrderBookPriceViewStruct cloneOrderBookPriceViewStruct(OrderBookPriceViewStruct orderBookPriceViewStruct)
    {
        OrderBookPriceViewStruct result = null;
        if (orderBookPriceViewStruct != null)
        {
            result = new OrderBookPriceViewStruct();
            result.orderBookPriceViewType = orderBookPriceViewStruct.orderBookPriceViewType;
            result.viewSequence = new MarketVolumeStruct[orderBookPriceViewStruct.viewSequence.length];
            for (int i = 0; i < result.viewSequence.length; i++)
            {
                result.viewSequence[i] = MarketDataStructBuilder.cloneMarketVolumeStruct(orderBookPriceViewStruct.viewSequence[i]);
            }
        }
        return result;
    }
    
    /**
     * Creates a default instance of a BookDepthDetailedStruct.
     *
     * @return default instance of struct
     */
    public static BookDepthDetailedStruct buildBookDepthDetailedStruct()
    {
        BookDepthDetailedStruct struct = new BookDepthDetailedStruct();
        struct.buyOrdersAtDifferentPrice = new OrderBookDetailPriceStruct[0];
        struct.sellOrdersAtDifferentPrice = new OrderBookDetailPriceStruct[0];
        struct.productKeys = ClientProductStructBuilder.buildProductKeysStruct();
        struct.sessionName = "";
        struct.transactionSequenceNumber = 0;
        return struct;
    }
    
    /**
     * Creates a default instance of a BookDepthDetailedStruct.
     *
     * @return default instance of struct
     */
    public static OrderBookDetailPriceStruct buildOrderBookDetailPriceStruct()
    {
        OrderBookDetailPriceStruct struct = new OrderBookDetailPriceStruct();
        struct.price = StructBuilder.buildPriceStruct();
        struct.orderInfo = new OrderBookStruct[0];
        return struct;
    }
    
    /**
     * Creates a default instance of a BookDepthDetailedStruct.
     *
     * @return default instance of struct
     */
    public static OrderBookStruct buildOrderBookStruct()
    {
        OrderBookStruct struct = new OrderBookStruct();
        struct.price = StructBuilder.buildPriceStruct();
        struct.orderId = OrderStructBuilder.buildOrderIdStruct(); 
        struct.originalQuantity = 0;
        struct.remainingQuantity = 0;
        struct.classKey = 0;
        struct.productKey = 0;
        struct.productType = 0;
        struct.side = 0;
        struct.price = StructBuilder.buildPriceStruct();
        struct.timeInForce = ' ';
        struct.receivedTime = StructBuilder.buildDateTimeStruct();
        struct.contingency = OrderStructBuilder.buildOrderContingencyStruct();
        struct.orderOriginType = ' ';
        struct.state = OrderStates.INACTIVE;
        struct.orderNBBOProtectionType = OrderNBBOProtectionTypes.NONE;
        struct.optionalData = "";
        struct.tradableType = ' ';
        return struct;
    }
}
