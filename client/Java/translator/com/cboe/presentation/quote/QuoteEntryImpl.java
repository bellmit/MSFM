// -----------------------------------------------------------------------------------
// Source file: QuoteEntryImpl.java
//
// PACKAGE: com.cboe.presentation.quote;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.quote;

import com.cboe.domain.util.QuoteStructBuilder;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.formatters.QuoteFormatStrategy;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.quote.Quote;
import com.cboe.interfaces.presentation.quote.QuoteEntry;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.FormatFactory;

/**
 * Quote implementation for a QuoteEntryStruct from the API.
 */
class QuoteEntryImpl extends AbstractBusinessModel implements QuoteEntry
{
    private String quoteDisplayName = null;
    private Price bidPrice = null;
    private Price askPrice = null;
    protected QuoteEntryStruct quoteEntryStruct = null;
    static private QuoteFormatStrategy formatter = null;

    /**
     * Constructor
     * @param quoteEntryStruct to represent
     */
    protected QuoteEntryImpl(QuoteEntryStruct quoteEntryStruct)
    {
        this();
        this.quoteEntryStruct = quoteEntryStruct;
    }

    /**
     *  Default constructor.
     */
    protected QuoteEntryImpl()
    {
        super();
        if(QuoteEntryImpl.formatter == null)
        {
            QuoteEntryImpl.formatter = FormatFactory.getQuoteFormatStrategy();
        }
    }

    // helper methods to struct attributes

    /**
     * Get the product key for this QuoteEntry.
     * @return product key from represented struct
     */
    public int getProductKey()
    {
        return getQuoteEntryStruct().productKey;
    }

    /**
     * Get the session name for this QuoteEntry.
     * @return session name String represented struct
     */
    public String getSessionName()
    {
        return getQuoteEntryStruct().sessionName;
    }

    /**
     * Get the bid price for this Quote.
     * @return bid Price
     */
    public Price getBidPrice()
    {
        if(this.bidPrice == null)
        {
            this.bidPrice = DisplayPriceFactory.create(getQuoteEntryStruct().bidPrice);
        }
        return this.bidPrice;
    }

    /**
     * Get the bid quantity for this Quote.
     * @return bid quantity
     */
    public int getBidQuantity()
    {
        return getQuoteEntryStruct().bidQuantity;
    }

    /**
     * Get the ask price for this Quote.
     * @return ask Price
     */
    public Price getAskPrice()
    {
        if(this.askPrice == null)
        {
            this.askPrice = DisplayPriceFactory.create(getQuoteEntryStruct().askPrice);
        }
        return this.askPrice;
    }

    /**
     * Get the ask quantity for this Quote.
     * @return ask quantity
     */
    public int getAskQuantity()
    {
        return getQuoteEntryStruct().askQuantity;
    }

    /**
     * Get the user assigned Id for this Quote.
     * @return String user assigned Id
     */
    public String getUserAssignedId()
    {
        return getQuoteEntryStruct().userAssignedId;
    }

    /**
     * Get the SessionProduct for this Quote.
     * @return SessionProduct
     */
    public SessionProduct getSessionProduct()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return APIHome.findProductQueryAPI().getProductByKeyForSession(getSessionName(), getProductKey());
    }

    /**
     * Get the QuoteStruct that this Quote represents.
     * @return QuoteStruct
     * @deprecated
     */
    public QuoteEntryStruct getQuoteEntryStruct()
    {
        return quoteEntryStruct;
    }

    /**
     * Clones this quote by returning another instance that represents a
     * QuoteStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        QuoteEntryImpl dest;
        dest = (QuoteEntryImpl) super.clone();
        if (getQuoteEntryStruct() != null)
        {
            dest.quoteEntryStruct = QuoteStructBuilder.cloneQuoteEntryStruct(getQuoteEntryStruct());
        }
        if (this.quoteDisplayName != null)
        {
            dest.quoteDisplayName = new String(this.quoteDisplayName);
        }
        if (this.bidPrice != null)
        {
            dest.bidPrice = DisplayPriceFactory.create(this.bidPrice.toStruct());
        }
        if (this.askPrice != null)
        {
            dest.askPrice = DisplayPriceFactory.create(this.askPrice.toStruct());
        }

        return dest;
    }

    /**
     * If <code>obj</code> is the same instance of QuoteEntry true is returned, false otherwise.
     * This may have to change in the future.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if(this == obj)
        {
            isEqual = true;
        }

        return isEqual;
    }

    /**
     * Returns a String representation of this QuoteEntry.
     */
    public String toString()
    {
        if(this.quoteDisplayName == null)
        {
            this.quoteDisplayName = QuoteEntryImpl.formatter.format(this, formatter.BRIEF);
        }
        return this.quoteDisplayName;
    }

}
