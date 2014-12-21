// -----------------------------------------------------------------------------------
// Source file: QuoteImpl.java
//
// PACKAGE: com.cboe.presentation.quote;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.quote;

import com.cboe.domain.util.QuoteStructBuilder;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.formatters.QuoteFormatStrategy;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.quote.Quote;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.formatters.FormatFactory;

/**
 * Quote implementation for a QuoteStruct from the API.
 */
class QuoteImpl extends AbstractBusinessModel implements Quote
{
    private String quoteDisplayName = null;
    private Price bidPrice = null;
    private Price askPrice = null;
    protected QuoteStruct quoteStruct = null;
    static private QuoteFormatStrategy formatter = null;

    /**
     * Constructor
     * @param quoteStruct to represent
     */
    protected QuoteImpl(QuoteStruct quoteStruct)
    {
        this();
        this.quoteStruct = quoteStruct;
    }

    /**
     *  Default constructor.
     */
    protected QuoteImpl()
    {
        super();
        if(QuoteImpl.formatter == null)
        {
            QuoteImpl.formatter = FormatFactory.getQuoteFormatStrategy();
        }
    }

    // helper methods to struct attributes

    /**
     * Get the quote key for this Quote.
     * @return quote key from represented struct
     */
    public int getQuoteKey()
    {
        return getQuoteStruct().quoteKey;
    }

    /**
     * Get the product key for this Quote.
     * @return product key from represented struct
     */
    public int getProductKey()
    {
        return getQuoteStruct().productKey;
    }

    /**
     * Get the session name for this Quote.
     * @return session name String represented struct
     */
    public String getSessionName()
    {
        return getQuoteStruct().sessionName;
    }

    /**
     * Get the user Id for this Quote.
     * @return user Id from represented struct
     */
    public String getUserId()
    {
        return getQuoteStruct().userId;
    }

    /**
     * Returns a hash code value for the object.
     * Overrides hashCode() implemented by the Object
     * @return int hash code
     */
    public int hashCode()
    {
        return getQuoteKey();
    }

    /**
     * Get the bid price for this Quote.
     * @return bid Price
     */
    public Price getBidPrice()
    {
        if(this.bidPrice == null)
        {
            this.bidPrice = DisplayPriceFactory.create(getQuoteStruct().bidPrice);
        }
        return this.bidPrice;
    }

    /**
     * Get the bid quantity for this Quote.
     * @return bid quantity
     */
    public int getBidQuantity()
    {
        return getQuoteStruct().bidQuantity;
    }

    /**
     * Get the ask price for this Quote.
     * @return ask Price
     */
    public Price getAskPrice()
    {
        if(this.askPrice == null)
        {
            this.askPrice = DisplayPriceFactory.create(getQuoteStruct().askPrice);
        }
        return this.askPrice;
    }

    /**
     * Get the ask quantity for this Quote.
     * @return ask quantity
     */
    public int getAskQuantity()
    {
        return getQuoteStruct().askQuantity;
    }

    /**
     * Get the transaction sequence number for this Quote.
     * @return int transaction sequence number
     */
    public int getTransactionSequenceNumber()
    {
        return getQuoteStruct().transactionSequenceNumber;
    }

    /**
     * Get the user assigned Id for this Quote.
     * @return String user assigned Id
     */
    public String getUserAssignedId()
    {
        return getQuoteStruct().userAssignedId;
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
    public QuoteStruct getQuoteStruct()
    {
        return quoteStruct;
    }

    /**
     * Clones this quote by returning another instance that represents a
     * QuoteStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        QuoteImpl dest;
        dest = (QuoteImpl) super.clone();
        if (getQuoteStruct() != null)
        {
            dest.quoteStruct = QuoteStructBuilder.cloneQuoteStruct(getQuoteStruct());
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
     * If <code>obj</code> is an instance of Quote and has the same
     * quote key true is returned, false otherwise.
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
        else if(obj instanceof Quote)
        {
            isEqual = getQuoteKey() == ((Quote)obj).getQuoteKey();
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    /**
     * Returns a String representation of this Quote.
     */
    public String toString()
    {
        if(this.quoteDisplayName == null)
        {
            this.quoteDisplayName = QuoteImpl.formatter.format(getQuoteStruct(), formatter.BRIEF);
        }
        return this.quoteDisplayName;
    }

}
