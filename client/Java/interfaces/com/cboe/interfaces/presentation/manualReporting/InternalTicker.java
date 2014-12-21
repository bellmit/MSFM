//
// -----------------------------------------------------------------------------------
// Source file: InternalTicker.java
//
// PACKAGE: com.cboe.interfaces.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.manualReporting;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.idl.marketData.InternalTickerStruct;

public interface InternalTicker extends MutableBusinessModel
{
    public static final String PROPERTY_TRADE_TIME = "PROPERTY_TRADE_TIME";
    public static final String PROPERTY_PRODUCT_KEYS = "PROPERTY_PRODUCT_KEYS";
    public static final String PROPERTY_SESSION = "PROPERTY_SESSION";
    public static final String PROPERTY_EXCHANGE = "PROPERTY_EXCHANGE";
    public static final String PROPERTY_SALE_PREFIX = "PROPERTY_SALE_PREFIX";
    public static final String PROPERTY_SALE_POSTFIX = "PROPERTY_SALE_POSTFIX";
    public static final String PROPERTY_SALE_PRICE = "PROPERTY_SALE_PRICE";
    public static final String PROPERTY_SALE_VOLUME = "PROPERTY_SALE_VOLUME";
    
    public Time getTradeTime();
    public ProductKeys getProductKeys();
    public String getSessionName();
    public String getExchange();
    public String getSalePrefix();
    public Price getLastSalePrice();
    public int getLastSaleVolume();

    public void setTradeTime(Time time);
    public void setProductKeys(ProductKeys productKeys);
    public void setSessionName(String sessionName);
    public void setExchange(String exchange);
    public void setSalePrefix(String prefix);
    public void setLastSalePrice(Price lastSalePrice);
    public void setLastSaleVolume(int lastSaleSize);

    public InternalTickerStruct getStruct();
}

