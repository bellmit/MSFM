// -----------------------------------------------------------------------------------
// Source file: MarketDataDetailImpl
//
// PACKAGE: com.cboe.presentation.marketData
// 
// Created: Jul 9, 2004 4:05:21 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.marketData.MarketDataDetail;
import com.cboe.interfaces.presentation.marketData.ExchangeVolume;
import com.cboe.interfaces.presentation.marketData.ExchangeIndicator;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.interfaces.presentation.common.properties.KeyValueProperties;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiMarketData.MarketDataDetailStruct;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.properties.KeyValuePropertiesImpl;
import com.cboe.presentation.user.ExchangeAcronymFactory;

public class MarketDataDetailImpl extends AbstractBusinessModel implements MarketDataDetail
{
    private MarketDataDetailStruct struct;

    private Price nbboAskPrice;
    private Price nbboBidPrice;
    private Price bestPublishedBidPrice;
    private Price bestPublishedAskPrice;

    private ExchangeVolume[] nbboAskExchanges;
    private ExchangeVolume[] nbboBidExchanges;

    private ExchangeIndicator[] exchangeIndicators;

    private ExchangeAcronym[] brokers;
    private ExchangeAcronym[] contras;

    private KeyValueProperties extensions;

    public MarketDataDetailImpl(MarketDataDetailStruct struct)
    {
        super();
        setStruct(struct);
    }

    private void setStruct(MarketDataDetailStruct struct)
    {
        this.checkState(struct);

        nbboAskPrice = DisplayPriceFactory.create(struct.nbboAskPrice);
        nbboBidPrice = DisplayPriceFactory.create(struct.nbboBidPrice);

        bestPublishedBidPrice = DisplayPriceFactory.create(struct.bestPublishedBidPrice);
        bestPublishedAskPrice = DisplayPriceFactory.create(struct.bestPublishedAskPrice);

        exchangeIndicators = new ExchangeIndicator[struct.exchangeIndicators.length];
        for (int i=0; i<exchangeIndicators.length; i++)
        {
            exchangeIndicators[i] = ExchangeIndicatorFactory.create(struct.exchangeIndicators[i]);
        }

        nbboAskExchanges = new ExchangeVolume[struct.nbboAskExchanges.length];
        for (int i=0; i<nbboAskExchanges.length; i++)
        {
            nbboAskExchanges[i] = ExchangeVolumeFactory.createExchangeVolume(struct.nbboAskExchanges[i]);
        }

        nbboBidExchanges = new ExchangeVolume[struct.nbboBidExchanges.length];
        for (int i=0; i<nbboBidExchanges.length; i++)
        {
            nbboBidExchanges[i] = ExchangeVolumeFactory.createExchangeVolume(struct.nbboBidExchanges[i]);
        }

        brokers = new ExchangeAcronym[struct.brokers.length];
        for (int i=0; i<brokers.length; i++)
        {
            brokers[i] = ExchangeAcronymFactory.createExchangeAcronym(struct.brokers[i]);
        }

        contras = new ExchangeAcronym[struct.contras.length];
        for (int i=0; i<contras.length; i++)
        {
            contras[i] = ExchangeAcronymFactory.createExchangeAcronym(struct.contras[i]);
        }

        extensions = KeyValuePropertiesImpl.createKeyValueProperties(struct.extensions);
        this.struct = struct;
    }

    public MarketDataDetailStruct getStruct()
    {
        return struct;
    }

    public char getOverrideIndicator()
    {
        return struct.overrideIndicator;
    }

    public Price getNbboAskPrice()
    {
        return nbboAskPrice;
    }

    public ExchangeVolume[] getNbboAskExchanges()
    {
        return nbboAskExchanges;
    }

    public Price getNbboBidPrice()
    {
        return nbboBidPrice;
    }

    public ExchangeVolume[] getNbboBidExchanges()
    {
        return nbboBidExchanges;
    }

    public boolean getTradeThroughIndicator()
    {
        return struct.tradeThroughIndicator;
    }

    public ExchangeIndicator[] getExchangeIndicators()
    {
        return exchangeIndicators;
    }

    public Price getBestPublishedBidPrice()
    {
        return bestPublishedBidPrice;
    }

    public int getBestPublishedBidVolume()
    {
        return struct.bestPublishedBidVolume;
    }

    public Price getBestPublishedAskPrice()
    {
        return bestPublishedAskPrice;
    }

    public int getBestPublishedAskVolume()
    {
        return struct.bestPublishedAskVolume;
    }

    public ExchangeAcronym[] getBrokers()
    {
        return brokers;
    }

    public ExchangeAcronym[] getContras()
    {
        return contras;
    }

    public KeyValueProperties getExtensions()
    {
        return extensions;
    }

} // -- end of class MarketDataDetailImpl
