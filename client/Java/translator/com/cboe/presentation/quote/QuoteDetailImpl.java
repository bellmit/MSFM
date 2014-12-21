// -----------------------------------------------------------------------------------
// Source file: QuoteDetailImpl.java
//
// PACKAGE: com.cboe.presentation.quote;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.quote;

import com.cboe.domain.util.QuoteStructBuilder;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.interfaces.presentation.quote.Quote;
import com.cboe.interfaces.presentation.quote.QuoteDetail;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

/**
 * QuoteDetail implementation for a QuoteDetailStruct from the API.
 */
class QuoteDetailImpl extends AbstractBusinessModel implements QuoteDetail
{
    private QuoteDetailStruct quoteDetailStruct;
    private Quote quote;

    /**
     * Constructor
     * @param quoteDetailStruct to represent
     */
    protected QuoteDetailImpl(QuoteDetailStruct quoteDetailStruct)
    {
        this();
        this.quoteDetailStruct = quoteDetailStruct;
    }

    /**
     *  Default constructor.
     */
    protected QuoteDetailImpl()
    {
        super();
    }

    // helper methods to struct attributes
    /**
     * Get the ProductKeysStruct for this QuoteDetail.
     * @return ProductKeysStruct from represented struct
     */
    public ProductKeysStruct getProductKeysStruct()
    {
        return getQuoteDetailStruct().productKeys;
    }

    /**
     * Get the ProductNameStruct for this QuoteDetail.
     * @return ProductNameStruct from represented struct
     */
    public ProductNameStruct getProductNameStruct()
    {
        return getQuoteDetailStruct().productName;
    }

    /**
     * Get the statusChange for this QuoteDetail.
     * @return statusChange from represented struct
     */
    public short getStatusChange()
    {
        return getQuoteDetailStruct().statusChange;
    }

    /**
     * Get the Quote this QuoteDetail represents.
     * @return Quote
     */
    public Quote getQuote()
    {
        if ( quote == null )
        {
            quote = QuoteFactory.create(getQuoteDetailStruct().quote);
        }
        return quote;
    }

    /**
     * Get the QuoteDetailStruct that this QuoteDetail represents.
     * @return QuoteDetailStruct
     * @deprecated
     */
    public QuoteDetailStruct getQuoteDetailStruct()
    {
        return this.quoteDetailStruct;
    }

    /**
     * Returns a hash code value for the object.
     * Overrides hashCode() implemented by the Object
     * @return int hash code
     */
    public int hashCode()
    {
        return getQuote().getQuoteKey();
    }

    /**
     * Clones this quote detail by returning another instance that represents a
     * QuoteDetailStruct that was also cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        QuoteDetailImpl dest;
        dest = (QuoteDetailImpl) super.clone();
        if (getQuoteDetailStruct() != null)
        {
            dest.quoteDetailStruct = QuoteStructBuilder.cloneQuoteDetailStruct(getQuoteDetailStruct());
        }
        if (this.quote != null && this.quote instanceof QuoteImpl)
        {
            dest.quote = (Quote) ((QuoteImpl) (this.quote)).clone();
        }
        return dest;
    }

    /**
     * If <code>obj</code> is an instance of QuoteDetail and has the same
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
        else if(obj instanceof QuoteDetail)
        {
            isEqual = getQuote().getQuoteKey() == ((QuoteDetail)obj).getQuote().getQuoteKey();
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    /**
     * Returns a String representation of this QuoteDetail.
     */
    public String toString()
    {
        return getQuote().toString();
    }
}
