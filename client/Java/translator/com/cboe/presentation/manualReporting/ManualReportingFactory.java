//
// -----------------------------------------------------------------------------------
// Source file: ManualReportingFactory.java
//
// PACKAGE: com.cboe.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.manualReporting;

import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.marketData.ManualPriceReportEntryStruct;
import com.cboe.idl.quote.ManualQuoteDetailStruct;
import com.cboe.idl.quote.ManualQuoteStruct;
import com.cboe.idl.cmiMarketData.ProductClassVolumeStruct;

import com.cboe.interfaces.presentation.manualReporting.*;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;

/**
 *  Factory for creating instances of Manual Reporting objects
 */
public class ManualReportingFactory {

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ManualReportingFactory()
    {}

    /**
     * Creates an instance of a ManualQuote from a ManualQuoteStruct.
     * @param manualQuoteStruct to wrap in instance of Quote
     * @return ManualQuote to represent the ManualQuoteStruct
     */
    public static ManualQuote create(ManualQuoteStruct manualQuoteStruct)
    {
        if (manualQuoteStruct == null)
        {
            throw new IllegalArgumentException("ManualQuoteStruct can not be NULL");
        }

        return new ManualQuoteImpl(manualQuoteStruct);
    }

    /**
     * Creates an instance of a ManualQuote from a SessionProduct.
     * @param sessionProduct
     * @return ManualQuote to represent the ManualQuoteStruct
     */
    public static ManualQuote createManualQuote(SessionProductClass sessionProductClass,
                                                SessionReportingClass sessionReportingClass,
                                                SessionProduct sessionProduct)
    {
        ManualQuote manualQuote = null;
        if (sessionProduct == null && sessionReportingClass == null && sessionProductClass == null)
        {
            manualQuote = new ManualQuoteImpl();
        }
        else if(sessionProduct != null)
        {
            manualQuote = new ManualQuoteImpl(sessionProduct);
        }
        else if(sessionReportingClass != null)
        {
            manualQuote = new ManualQuoteImpl(sessionReportingClass);
        }
        else if(sessionProductClass != null)
        {
            manualQuote = new ManualQuoteImpl(sessionProductClass);
        }
        return manualQuote;
    }

    /**
     * Creates an instance of a ManualQuote from w/out product info.
     * @return empty ManualQuoteStruct
     */
    public static ManualQuote createManualQuote()
    {
        return new ManualQuoteImpl();
    }


    /**
     * Creates an instance of a ManualQuoteDetail from a ManualQuoteDetailStruct.
     * @param manualQuoteDetailStruct to wrap in instance of QuoteDetail
     * @return QuoteDetail to represent the QuoteDetailStruct
     */
    public static ManualQuoteDetail create(ManualQuoteDetailStruct manualQuoteDetailStruct)
    {
        if (manualQuoteDetailStruct == null)
        {
            throw new IllegalArgumentException("ManualQuoteDetailStruct can not be NULL");
        }

        return new ManualQuoteDetailImpl(manualQuoteDetailStruct);
    }

    /**
     * Creates an instance of a InternalTickerDetail from a InternalTickerDetailStruct.
     * @param internalTickerDetailStruct to wrap in instance of InternalTickerDetail
     * @return InternalTickerDetail to represent the InternalTickerDetailStruct
     */
    public static InternalTickerDetail create(InternalTickerDetailStruct internalTickerDetailStruct)
    {
        if (internalTickerDetailStruct == null)
        {
            throw new IllegalArgumentException("InternalTickerDetailStruct can not be NULL");
        }

        InternalTickerDetail ticker = new InternalTickerDetailImpl(internalTickerDetailStruct);

        return ticker;
    }

    /**
     * Creates an instance of a InternalTickerDetail from a SessionProduct.
     * @param sessionProduct
     * @return InternalTickerDetail to represent the InternalTickerDetailStruct
     */
    public static InternalTickerDetail createInternalTickerDetail(SessionProductClass sessionProductClass, SessionProduct sessionProduct)
    {
        if (sessionProductClass == null || sessionProduct == null)
        {
            throw new IllegalArgumentException("SessionProductClass and SessionProduct can not be NULL");
        }
        InternalTickerDetail ticker = new InternalTickerDetailImpl(sessionProductClass,sessionProduct);

        return ticker;
    }

    /**
     * Creates an instance of a InternalTicker from a InternalTickerStruct.
     * @param internalTickerStruct to wrap in instance of InternalTicker
     * @return InternalTicker to represent the InternalTickerStruct
     */
    public static InternalTicker create(InternalTickerStruct internalTickerStruct)
    {
        if (internalTickerStruct == null)
        {
            throw new IllegalArgumentException("InternalTickerStruct can not be NULL");
        }

        InternalTicker ticker = new InternalTickerImpl(internalTickerStruct);

        return ticker;
    }

    /**
     * Creates an instance of a ManualPrice from a ManualPriceEntryStruct.
     * @param manualPriceReportEntryStruct to wrap in instance of ManualPrice
     * @return ManualPrice to represent the ManualPriceEntryStruct
     */
    public static ManualPrice create(ManualPriceReportEntryStruct manualPriceReportEntryStruct)
    {
        if (manualPriceReportEntryStruct == null)
        {
            throw new IllegalArgumentException("ManualPriceReportEntryStruct can not be NULL");
        }

        return new ManualPriceImpl(manualPriceReportEntryStruct);
    }


    /**
     * Creates an instance of a ManualPrice from a SessionProduct/SessionProductClass.
     * @param sessionProduct
     * @return ManualQuote to represent the ManualPriceStruct
     */
    public static ManualPrice createManualPrice(SessionProductClass sessionProductClass,
                                                SessionReportingClass sessionReportingClass,
                                                SessionProduct sessionProduct)
    {
        ManualPrice manualPrice = null;
        if (sessionProduct == null && sessionProductClass == null && sessionReportingClass == null)
        {
            manualPrice = new ManualPriceImpl();
        }
        else if(sessionProduct != null)
        {
            manualPrice = new ManualPriceImpl(sessionProduct);
        }
        else if(sessionReportingClass != null)
        {
            manualPrice = new ManualPriceImpl(sessionReportingClass);
        }
        else if(sessionProductClass != null)
        {
            manualPrice = new ManualPriceImpl(sessionProductClass);
        }
        return manualPrice;
    }

    /**
     * Creates an instance of a ManualPrice from w/out product info.
     * @return empty ManualPriceStruct
     */
    public static ManualPrice createManualPrice()
    {
        return new ManualPriceImpl();
    }

    
    /**
     * Creates an instance of a ProductClassVolume from a ProductClassVolumeStruct.     
     * @return ProductClassVolume to represent the ProductClassVolumeStruct
     */
    public static ProductClassVolume create()
    {
        return new ProductClassVolumeImpl();
    }
}
