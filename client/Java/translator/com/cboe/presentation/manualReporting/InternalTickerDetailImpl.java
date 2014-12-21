//
// -----------------------------------------------------------------------------------
// Source file: InternalTickerDetailImpl.java
//
// PACKAGE: com.cboe.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.manualReporting;

import com.cboe.presentation.marketData.MarketDataHistoryFactory;
import com.cboe.presentation.marketData.NBBOFactory;
import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.interfaces.presentation.marketData.MarketDataDetail;
import com.cboe.interfaces.presentation.marketData.NBBO;
import com.cboe.interfaces.presentation.manualReporting.InternalTickerDetail;
import com.cboe.interfaces.presentation.manualReporting.InternalTicker;
import com.cboe.interfaces.presentation.common.formatters.ManualReportingFormatStrategy;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiMarketData.MarketDataDetailStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.exceptions.*;

import java.util.Date;

import org.omg.CORBA.UserException;

public class InternalTickerDetailImpl extends AbstractMutableBusinessModel implements InternalTickerDetail
{
    protected InternalTickerDetailStruct internalTickerDetailStruct;

    protected InternalTicker lastSale;
    protected MarketDataDetail marketDataDetail;
    protected Price tradePrice;
    protected Time tradeTime;
    protected NBBO nbbo;
    protected SessionProductClass sessionProductClass;
    protected SessionProduct sessionProduct;

    static private ManualReportingFormatStrategy formatter = null;

    private InternalTickerDetailImpl()
    {
        super();
        if(formatter == null)
        {
            formatter = FormatFactory.getManualReportingFormatStrategy();
        }
    }

    protected InternalTickerDetailImpl(InternalTickerDetailStruct internalTickerDetailStruct)
    {
        this();
        this.internalTickerDetailStruct = internalTickerDetailStruct;
        sessionProductClass = ProductHelper.getSessionProductClass(internalTickerDetailStruct.lastSaleTicker.ticker.sessionName,
                                                         internalTickerDetailStruct.lastSaleTicker.ticker.productKeys.classKey);
        sessionProduct = ProductHelper.getSessionProduct(internalTickerDetailStruct.lastSaleTicker.ticker.sessionName,
                                                         internalTickerDetailStruct.lastSaleTicker.ticker.productKeys.productKey);
    }

    protected InternalTickerDetailImpl(SessionProductClass sessionProductClass, SessionProduct sessionProduct)
    {
        this();
        internalTickerDetailStruct = MarketDataStructBuilder.buildInternalTickerDetailStruct(sessionProduct.getProductKeysStruct());
        internalTickerDetailStruct.detailData.brokers = new ExchangeAcronymStruct[] { new ExchangeAcronymStruct(sessionProductClass.getPrimaryExchange(),"")};
        internalTickerDetailStruct.detailData.contras = new ExchangeAcronymStruct[] { new ExchangeAcronymStruct(sessionProductClass.getPrimaryExchange(),"")};
        internalTickerDetailStruct.lastSaleTicker.ticker.exchangeSymbol = sessionProductClass.getPrimaryExchange();
        this.sessionProductClass = sessionProductClass;
        this.sessionProduct = sessionProduct;
    }


    public InternalTickerDetailStruct getStruct()
    {
        return internalTickerDetailStruct;
    }

    public InternalTicker getLastSale()
    {
        if(lastSale == null)
        {
            lastSale = ManualReportingFactory.create(getStruct().lastSaleTicker);
        }
        return lastSale;
    }

    public MarketDataDetail getMarketDataDetail() {
        if(marketDataDetail == null)
        {
            marketDataDetail = MarketDataHistoryFactory.createMarketDataDetail(getStruct().detailData);
        }
        return marketDataDetail;
    }

    public char getSource()
    {
        return getStruct().source;
    }

    public CboeIdStruct getTradeId()
    {
        return getStruct().tradeId;
    }

    public String getGeneratingId()
    {
        return getStruct().generatingId;
    }

    public boolean getIsDisseminated()
    {
        return getStruct().isDisseminated;
    }

    public NBBO getBotr()
    {
        if(nbbo == null)
        {
            nbbo = NBBOFactory.createNBBO(getStruct().botrStruct);
        }
        return nbbo;
    }

    public SessionProduct getSessionProduct()
    {
        return sessionProduct;
    }

    public SessionProductClass getSessionProductClass()
    {
        return sessionProductClass;
    }

    public void setLastSale(InternalTicker lastSale)
    {
        Object oldValue = getLastSale();
        getStruct().lastSaleTicker = lastSale.getStruct();
        this.lastSale = lastSale;
        setModified(true);
        firePropertyChange(PROPERTY_LAST_SALE_TICKER, oldValue, lastSale);
    }

    public void setMarketDataDetail(MarketDataDetail marketDataDetail)
    {
        Object oldValue = getMarketDataDetail();
        getStruct().detailData = marketDataDetail.getStruct();
        this.marketDataDetail = marketDataDetail;
        setModified(true);
        firePropertyChange(PROPERTY_MARKET_DETAIL, oldValue, marketDataDetail);
    }
}
