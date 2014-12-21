// -----------------------------------------------------------------------------------
// Source file: QuoteDetail.java
//
// PACKAGE: com.cboe.interfaces.presentation.quote;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.quote;

import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 * Defines the contract a QuoteDetail wrapper for a QuoteDetailStruct
 */
public interface QuoteDetail extends BusinessModel
{
    // helper methods to struct attributes
    public ProductKeysStruct getProductKeysStruct();
    public ProductNameStruct getProductNameStruct();
    public short getStatusChange();
    public Quote getQuote();

    /**
     * @deprecated Use public getters to get struct contents always
     */
    public QuoteDetailStruct getQuoteDetailStruct();
}
