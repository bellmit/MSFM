//
// -----------------------------------------------------------------------------------
// Source file: ManualQuote.java
//
// PACKAGE: com.cboe.interfaces.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.manualReporting;

import com.cboe.idl.cmiUtil.KeyValueStruct;
import com.cboe.idl.quote.ManualQuoteStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;

/**
 * Defines the contract a ManualQuote wrapper for a ManualQuoteStruct
 */
public interface ManualQuote extends MutableBusinessModel
{
    public static final String PROPERTY_PRODUCT_KEYS = "PROPERTY_PRODUCT_KEYS";
    public static final String PROPERTY_SIDE = "PROPERTY_SIDE";
    public static final String PROPERTY_PRICE = "PROPERTY_PRICE";
    public static final String PROPERTY_SIZE = "PROPERTY_SIZE";
    public static final String PROPERTY_OVERRIDE = "PROPERTY_OVERRIDE";

    // helper methods to struct attributes
    public String getSessionName();
    public ProductKeys getProductKeys();
    public char getSide();
    public Price getPrice();
    public int getSize();
    public boolean isOverrideIndicator();
    public ManualQuoteDetail getManualQuoteDetail();
    public KeyValueStruct[] getExtensions();
    public SessionProduct getSessionProduct();
    public SessionReportingClass getSessionReportingClass();
    public SessionProductClass getSessionProductClass();

    public void setSessionProduct(SessionProduct sessionProduct);
    public void setSessionReportingClass(SessionReportingClass sessionReportingClass);
    public void setSessionProductClass(SessionProductClass sessionProductClass);
    public void setSide(char side);
    public void setPrice(Price price);
    public void setSize(int size);
    public void setOverrideIndicator(boolean override);
    public void setManualQuoteDetail(ManualQuoteDetail manualQuoteDetail);
    public void setExtensions(KeyValueStruct[] keyValues);

    public ManualQuoteStruct getStruct();
}
