//
// -----------------------------------------------------------------------------------
// Source file: InternalTickerDetail.java
//
// PACKAGE: com.cboe.interfaces.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.manualReporting;

import com.cboe.interfaces.presentation.marketData.NBBO;
import com.cboe.interfaces.presentation.marketData.MarketDataDetail;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;


public interface InternalTickerDetail extends MutableBusinessModel
{
    public static final String PROPERTY_LAST_SALE_TICKER = "PROPERTY_LAST_SALE_TICKER";
    public static final String PROPERTY_MARKET_DETAIL = "PROPERTY_MARKET_DETAIL";

    public InternalTicker getLastSale();
    public MarketDataDetail getMarketDataDetail();
    public char getSource();
    public CboeIdStruct getTradeId();
    public String getGeneratingId();
    public boolean getIsDisseminated();
    public NBBO getBotr();
    public SessionProduct getSessionProduct();
    public SessionProductClass getSessionProductClass();

    public void setLastSale(InternalTicker lastSale);
    public void setMarketDataDetail(MarketDataDetail marketDataDetail);

    public InternalTickerDetailStruct getStruct();
  }

