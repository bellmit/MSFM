//
// -----------------------------------------------------------------------------------
// Source file: ManualPrice.java
//
// PACKAGE: com.cboe.interfaces.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.manualReporting;

import com.cboe.idl.marketData.ManualPriceReportEntryStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;

/**
 * Defines the contract a ManualPrice wrapper for a ManualPriceReportEntryStruct
 */
public interface ManualPrice extends MutableBusinessModel {
    public static final String PROPERTY_PRODUCT_KEYS = "PROPERTY_PRODUCT_KEYS";
    public static final String PROPERTY_TRADE_TIME = "PROPERTY_TRADE_TIME";
    public static final String PROPERTY_SALE_PREFIX = "PROPERTY_SALE_PREFIX";
    public static final String PROPERTY_SALE_PRICE = "PROPERTY_SALE_PRICE";
    public static final String PROPERTY_SALE_VOLUME = "PROPERTY_SALE_VOLUME";
    public static final String PROPERTY_BUYER_BROKER = "PROPERTY_BUYER_BROKER";
    public static final String PROPERTY_SELLER_BROKER = "PROPERTY_SELLER_BROKER";

    public Time getTradeTime();
    public ProductKeys getProductKeys();
    public String getSessionName();
    public String getSalePrefix();
    public Price getLastSalePrice();
    public int getLastSaleVolume();
    public ExchangeAcronym getBuyerBroker();
    public ExchangeAcronym getSellerBroker();
    public SessionProduct getSessionProduct();
    public SessionReportingClass getSessionReportingClass();
    public SessionProductClass getSessionProductClass();

    public void setSessionProduct(SessionProduct sessionProduct);
    public void setSessionReportingClass(SessionReportingClass sessionReportingClass);
    public void setSessionProductClass(SessionProductClass sessionProductClass);
    public void setTradeTime(Time time);
    public void setSalePrefix(String prefix);
    public void setLastSalePrice(Price lastSalePrice);
    public void setLastSaleVolume(int lastSaleSize);
    public void setBuyerBroker(ExchangeAcronym broker);
    public void setSellerBroker(ExchangeAcronym broker);

    public ManualPriceReportEntryStruct getStruct();
}
