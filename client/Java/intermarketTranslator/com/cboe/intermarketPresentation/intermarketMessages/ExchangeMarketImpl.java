//
// -----------------------------------------------------------------------------------
// Source file: ExchangeMarketImpl.java
//
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;
import com.cboe.interfaces.presentation.marketData.ExchangeVolume;

import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.marketData.ExchangeVolumeFactory;

class ExchangeMarketImpl implements ExchangeMarket
{
    private ExchangeMarketStruct    exchangeMarketStruct;
    private short                   marketInfoType;
    private Price                   bestBidPrice;
    private ExchangeVolume[]        bidExchangeVolumes;
    private Price                   bestAskPrice;
    private ExchangeVolume[]        askExchangeVolumes;

    public ExchangeMarketImpl(ExchangeMarketStruct exchangeMarketStruct)
    {
        this.exchangeMarketStruct = exchangeMarketStruct;
        initialize();
    }

    private void initialize()
    {
        marketInfoType      = exchangeMarketStruct.marketInfoType;
        bestBidPrice        = DisplayPriceFactory.create(exchangeMarketStruct.bestBidPrice);
        bestAskPrice        = DisplayPriceFactory.create(exchangeMarketStruct.bestAskPrice);
        bidExchangeVolumes  = new ExchangeVolume[exchangeMarketStruct.bidExchangeVolumes.length];
        askExchangeVolumes  = new ExchangeVolume[exchangeMarketStruct.askExchangeVolumes.length];
        for(int i=0; i<exchangeMarketStruct.bidExchangeVolumes.length; i++)
        {
            bidExchangeVolumes[i] = ExchangeVolumeFactory.createExchangeVolume(exchangeMarketStruct.bidExchangeVolumes[i]);
        }
        for(int i=0; i<exchangeMarketStruct.askExchangeVolumes.length; i++)
        {
            askExchangeVolumes[i] = ExchangeVolumeFactory.createExchangeVolume(exchangeMarketStruct.askExchangeVolumes[i]);
        }

    }

    public short getMarketInfoType()
    {
        return marketInfoType;
    }

    public Price getBestBidPrice()
    {
        return bestBidPrice;
    }

    public ExchangeVolume[] getBidExchangeVolumes()
    {
        return bidExchangeVolumes;
    }

    public Price getBestAskPrice()
    {
        return bestAskPrice;
    }

    public ExchangeVolume[] getAskExchangeVolumes()
    {
        return askExchangeVolumes;
    }

    /**
     * Gets the underlying struct
     * @return ExchangeMarketStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public ExchangeMarketStruct getStruct()
    {
        return exchangeMarketStruct;
    }
}