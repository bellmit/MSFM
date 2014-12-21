//
// -----------------------------------------------------------------------------------
// Source file: NBBOV2GeneratorTask.java
//
// PACKAGE: com.cboe.presentation.api.marketDataCache.test
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api.marketDataCache.test;

import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.idl.cmiConstants.ExchangeStrings;

import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.util.ChannelType;

import com.cboe.domain.util.DateWrapper;

public class NBBOV2GeneratorTask extends TestSessionMarketDataGeneratorTask<NBBOStruct>
{
    private static final String[] EXCHANGES = {
            ExchangeStrings.AMEX, ExchangeStrings.BATS, ExchangeStrings.BOX,
            ExchangeStrings.CBOE, ExchangeStrings.CBOE2, ExchangeStrings.CBOT,
            ExchangeStrings.CFE, ExchangeStrings.ISE, ExchangeStrings.NASD,
            ExchangeStrings.NYSE};
    private static final int SUBSCRIPTION_KEY = -1 * ChannelType.CB_NBBO_BY_CLASS;

    public NBBOV2GeneratorTask(SessionProduct[] products)
    {
        this(products, SUBSCRIPTION_KEY);
    }

    public NBBOV2GeneratorTask(SessionProduct[] products, int subscriptionChannelType)
    {
        super(products, subscriptionChannelType);
    }

    protected ExchangeVolumeStruct[] buildExchangeVolumeStructArray()
    {
        int i = (int) (Math.random() * EXCHANGES.length);
        ExchangeVolumeStruct[] retVal = new ExchangeVolumeStruct[i];
        for(int j=0; j<i; j++)
        {
            retVal[j] = new ExchangeVolumeStruct(EXCHANGES[j], getQty());
        }
        return retVal;
    }

    protected NBBOStruct buildTestMarketData(SessionProduct product)
    {
        NBBOStruct retVal = new NBBOStruct();
        retVal.productKeys = product.getProductKeysStruct();
        retVal.sessionName = product.getTradingSessionName();
        retVal.bidPrice = buildPriceStruct();
        retVal.bidExchangeVolume = buildExchangeVolumeStructArray();
        retVal.askPrice = buildPriceStruct();
        retVal.askExchangeVolume = buildExchangeVolumeStructArray();
        retVal.sentTime = new DateWrapper().toTimeStruct();
        return retVal;
    }
}
