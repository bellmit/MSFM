// -----------------------------------------------------------------------------------
// Source file: Quote.java
//
// PACKAGE: com.cboe.interfaces.presentation.quote;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.quote;

import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.exceptions.*;

/**
 * Defines the contract a Quote wrapper for a QuoteStruct
 */
public interface Quote extends BusinessModel
{
    // helper methods to struct attributes
    public int getQuoteKey();
    public int getProductKey();
    public String getSessionName();
    public String getUserId();
    public Price getBidPrice();
    public int getBidQuantity();
    public Price getAskPrice();
    public int getAskQuantity();
    public int getTransactionSequenceNumber();
    public String getUserAssignedId();

    public SessionProduct getSessionProduct()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    /**
     * @deprecated Use public getters to get struct contents always
     */
    public QuoteStruct getQuoteStruct();
}
