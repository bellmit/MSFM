//
// -----------------------------------------------------------------------------------
// Source file: InternalTickerImpl.java
//
// PACKAGE: com.cboe.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.manualReporting;

import com.cboe.interfaces.presentation.manualReporting.InternalTicker;
import com.cboe.interfaces.presentation.common.formatters.ManualReportingFormatStrategy;
import com.cboe.interfaces.presentation.product.ProductKeys;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.TimeImpl;
import com.cboe.presentation.product.ProductKeysImpl;
import com.cboe.domain.util.MarketDataStructBuilder;

public class InternalTickerImpl extends AbstractMutableBusinessModel implements InternalTicker
{
    protected InternalTickerStruct internalTickerStruct;
    protected Price lastSalePrice;
    protected Time tradeTime;
    protected ProductKeys productKeys;
    static private ManualReportingFormatStrategy formatter = null;

    protected InternalTickerImpl()
    {
        super();
        if(formatter == null)
        {
            formatter = FormatFactory.getManualReportingFormatStrategy();
        }
    }

    protected InternalTickerImpl(InternalTickerStruct struct)
    {
        this();
        this.internalTickerStruct = struct;
    }

    public InternalTickerImpl(ProductKeys productKeys, Price salePrice, int saleQuantity, String salePrefix)
    {
        this();
        internalTickerStruct = new InternalTickerStruct();
        internalTickerStruct.ticker = MarketDataStructBuilder.buildTickerStruct(productKeys.getStruct());
        internalTickerStruct.ticker.salePrefix = salePrefix;
        internalTickerStruct.ticker.lastSalePrice = salePrice.toStruct();
        internalTickerStruct.ticker.lastSaleVolume = saleQuantity;
        internalTickerStruct.ticker.salePrefix = salePrefix;
    }

    public InternalTickerStruct getStruct()
    {
        return internalTickerStruct;
    }

    public Time getTradeTime()
    {
        if(tradeTime == null )
        {
            tradeTime = new TimeImpl(getStruct().tradeTime);
        }
        return tradeTime;
    }

    public ProductKeys getProductKeys()
    {
        if(productKeys == null)
        {
            productKeys = new ProductKeysImpl(getStruct().ticker.productKeys);
        }
        return productKeys;
    }

    public String getSessionName() {
        return getStruct().ticker.sessionName;
    }

    public String getExchange()
    {
        return getStruct().ticker.exchangeSymbol;
    }

    public String getSalePrefix() {
        return getStruct().ticker.salePrefix;
    }

    public Price getLastSalePrice()
    {
        if(lastSalePrice == null)
        {
            lastSalePrice = DisplayPriceFactory.create(getStruct().ticker.lastSalePrice);
        }
        return lastSalePrice;
    }

    public int getLastSaleVolume()
    {
        return getStruct().ticker.lastSaleVolume;
    }


    public void setTradeTime(Time time)
    {
        Object oldValue = getTradeTime();
        getStruct().tradeTime = time.getTimeStruct();
        tradeTime = time;
        setModified(true);
        firePropertyChange(PROPERTY_TRADE_TIME, oldValue, time);
    }

    public void setProductKeys(ProductKeys productKeys)
    {
        Object oldValue = getProductKeys();
        getStruct().ticker.productKeys = productKeys.getStruct();
        this.productKeys = productKeys;
        setModified(true);
        firePropertyChange(PROPERTY_PRODUCT_KEYS, oldValue, productKeys);
    }

    public void setSessionName(String sessionName)
    {
        Object oldValue = getSessionName();
        getStruct().ticker.sessionName = sessionName;
        setModified(true);
        firePropertyChange(PROPERTY_SESSION, oldValue, sessionName);
    }

    public void setExchange(String exchange)
    {
        Object oldValue = getExchange();
        getStruct().ticker.exchangeSymbol = exchange;
        setModified(true);
        firePropertyChange(PROPERTY_EXCHANGE, oldValue, exchange);
    }

    public void setSalePrefix(String prefix)
    {
        Object oldValue = getSalePrefix();
        getStruct().ticker.salePrefix = prefix;
        setModified(true);
        firePropertyChange(PROPERTY_SALE_PREFIX, oldValue, prefix);
    }

    public void setLastSalePrice(Price lastSalePrice)
    {
        Object oldValue = getLastSalePrice();
        getStruct().ticker.lastSalePrice = lastSalePrice.toStruct();
        this.lastSalePrice = lastSalePrice;
        setModified(true);
        firePropertyChange(PROPERTY_SALE_PRICE, oldValue, lastSalePrice);
    }

    public void setLastSaleVolume(int lastSaleSize)
    {
        Object oldValue = getLastSaleVolume();
        getStruct().ticker.lastSaleVolume = lastSaleSize;
        setModified(true);
        firePropertyChange(PROPERTY_SALE_VOLUME, oldValue, lastSaleSize);
    }
}
