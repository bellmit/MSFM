// -----------------------------------------------------------------------------------
// Source file: QuoteEntry.java
//
// PACKAGE: com.cboe.interfaces.presentation.quote;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.quote;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.product.SessionProduct;

/**
 * Defines the contract a Quote wrapper for a QuoteStruct
 */
public interface QuoteEntry extends BusinessModel
{
    // helper methods to struct attributes
    public int getProductKey();
    public String getSessionName();

    public Price getBidPrice();
    public int getBidQuantity();
    public Price getAskPrice();
    public int getAskQuantity();
    public String getUserAssignedId();

    public SessionProduct getSessionProduct()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    /**
     * @deprecated Use public getters to get struct contents always
     */
    public QuoteEntryStruct getQuoteEntryStruct();
}
