package com.cboe.cfix.util;

/**
 * debugMarketDataMapper.java
 *
 * @author Dmitry Volpyansky
 *
 */

import java.util.*;

import com.cboe.client.util.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiUtil.*;

public class debugMarketDataMapper
{
    public Random random = new Random();

    protected void debugBuildCurrentTimeStruct(TimeStruct timeStruct)
    {
        long now = System.currentTimeMillis();

        timeStruct.hour   = (byte) DateHelper.getHourOfDay(now, DateHelper.TIMEZONE_OFFSET_CST);
        timeStruct.minute = (byte) DateHelper.getMinuteOfDay(now);
        timeStruct.second = (byte) DateHelper.getSecondOfDay(now);
    }

/*
    protected ProductStruct debugGetProductStruct(int key)
    {
        if (debugCmiProductStruct == null)
        {
            debugCmiProductStruct = new ProductStruct();

            debugCmiProductStruct.productName                               = new ProductNameStruct();
            debugCmiProductStruct.productName.exercisePrice                 = new PriceStruct((short) 0, 50, 0);
            debugCmiProductStruct.productName.optionType                    = OptionTypes.PUT;

            debugCmiProductStruct.productKeys                               = new ProductKeysStruct();
            debugCmiProductStruct.productKeys.productKey                    = key;
            debugCmiProductStruct.productKeys.productType                   = ProductTypes.FUTURE;
        }

        switch (key)
        {
            case 86376706:
                debugCmiProductStruct.productName.reportingClass            = "BAC1C";
                debugCmiProductStruct.productName.expirationDate            = new DateStruct((byte) 4, (byte) 18, (short) 2003);
                break;

            case 83455748:
                debugCmiProductStruct.productName.reportingClass            = "SLB1C";
                debugCmiProductStruct.productName.expirationDate            = new DateStruct((byte) 6, (byte) 20, (short) 2003);
                break;

            default:
                debugCmiProductStruct.productName.reportingClass            = "IBM";
                debugCmiProductStruct.productName.expirationDate            = new DateStruct((byte) 1, (byte) 2, (short) 2003);
                break;
        }

        return debugCmiProductStruct;
    }
*/

    public CurrentMarketStruct[] makeCurrentMarketStruct(int occurance, String sessionName, short productType, int classKey, int[] productKeys, boolean randomPrices)
    {
        CurrentMarketStruct[] structs = new CurrentMarketStruct[productKeys.length];
        CurrentMarketStruct currentMarketStruct;
        int productKey;
        int pennies = 0;

        long now = System.currentTimeMillis();

        TimeStruct timeStruct = new TimeStruct();

        timeStruct.hour   = (byte) DateHelper.getHourOfDay(now, DateHelper.TIMEZONE_OFFSET_CST);
        timeStruct.minute = (byte) DateHelper.getMinuteOfDay(now);
        timeStruct.second = (byte) DateHelper.getSecondOfDay(now);

        for (int i = 0; i < productKeys.length; i++)
        {
            structs[i] = new CurrentMarketStruct();

            currentMarketStruct = structs[i];

            productKey = productKeys[i];

            currentMarketStruct.productKeys                         = new ProductKeysStruct();
            currentMarketStruct.productKeys.classKey                = classKey;
            currentMarketStruct.productKeys.productKey              = productKey;
            currentMarketStruct.productKeys.productType             = productType;
            currentMarketStruct.productKeys.reportingClass          = 12345678;
            currentMarketStruct.sessionName                         = sessionName;
            currentMarketStruct.exchange                            = "";
            if (randomPrices)
            {
                currentMarketStruct.bidPrice                        = new PriceStruct(PriceTypes.VALUED, 1 + random.nextInt(10), (int) (random.nextInt(10) * Math.pow(random.nextInt(7), 10)));
                currentMarketStruct.askPrice                        = new PriceStruct(PriceTypes.VALUED, currentMarketStruct.bidPrice.whole + 1 + random.nextInt(10), (int) (random.nextInt(10) * Math.pow(random.nextInt(7), 10)));
            }
            else
            {
                currentMarketStruct.bidPrice                        = new PriceStruct(PriceTypes.VALUED, pennies / 100, pennies % 100);
                pennies++;
                currentMarketStruct.askPrice                        = new PriceStruct(PriceTypes.VALUED, pennies / 100, pennies % 100);
                pennies++;
            }
            currentMarketStruct.bidSizeSequence                     = new MarketVolumeStruct[1];
            currentMarketStruct.bidSizeSequence[0]                  = new MarketVolumeStruct((short) 1, occurance, false);
            currentMarketStruct.bidIsMarketBest                     = random.nextBoolean();
            currentMarketStruct.askSizeSequence                     = new MarketVolumeStruct[1];
            currentMarketStruct.askSizeSequence[0]                  = new MarketVolumeStruct((short) 1, occurance, false);
            currentMarketStruct.askIsMarketBest                     = random.nextBoolean();

            currentMarketStruct.sentTime                            = timeStruct;

            currentMarketStruct.legalMarket                         = random.nextBoolean();

            currentMarketStruct.bidIsMarketBest                     = random.nextBoolean();
            currentMarketStruct.askIsMarketBest                     = random.nextBoolean();
        }

        return structs;
    }

/*
    public CurrentMarketStruct[] debugMakeCmiCurrentMarketStruct()
    {
        if (debugCmiCurrentMarketStructs == null)
        {
            debugCmiCurrentMarketStruct                                     = new CurrentMarketStruct();
            debugCmiCurrentMarketStructs                                    = new CurrentMarketStruct[1];
            debugCmiCurrentMarketStructs[0]                                 = debugCmiCurrentMarketStruct;

            debugCmiCurrentMarketStruct.productKeys                         = new ProductKeysStruct();
            debugCmiCurrentMarketStruct.productKeys.productKey              = 86376706;
            debugCmiCurrentMarketStruct.productKeys.productType             = ProductTypes.FUTURE;

            debugCmiCurrentMarketStruct.sentTime                            = new TimeStruct();
            debugCmiCurrentMarketStruct.sessionName                         = "ONE_MAIN";

            debugCmiCurrentMarketStruct.bidSizeSequence                     = new MarketVolumeStruct[1];
            debugCmiCurrentMarketStruct.bidSizeSequence[0]                  = new MarketVolumeStruct((short) 0, random.nextInt(10), true);
            debugCmiCurrentMarketStruct.bidPrice                            = new PriceStruct((short) 10, 10, 0);

            debugCmiCurrentMarketStruct.askSizeSequence                     = new MarketVolumeStruct[1];
            debugCmiCurrentMarketStruct.askSizeSequence[0]                  = new MarketVolumeStruct((short) 0, random.nextInt(10), false);
            debugCmiCurrentMarketStruct.askPrice                            = new PriceStruct((short) 10, 10, 0);
        }

        debugBuildCurrentTimeStruct(debugCmiCurrentMarketStruct.sentTime);

        debugCmiCurrentMarketStruct.bidIsMarketBest                         = random.nextBoolean();
        debugCmiCurrentMarketStruct.bidSizeSequence[0].quantity             = 1 + random.nextInt(1000);
        debugCmiCurrentMarketStruct.bidPrice.whole                          = 1 + random.nextInt(10);
        debugCmiCurrentMarketStruct.askSizeSequence[0].quantity             = 1 + random.nextInt(1000);
        debugCmiCurrentMarketStruct.askPrice.whole                          = debugCmiCurrentMarketStruct.bidPrice.whole + 1;

        return debugCmiCurrentMarketStructs;
    }

    public BookDepthStruct[] debugMakeCmiBookDepthStruct()
    {
        if (debugCmiBookDepthStructs == null)
        {
            debugCmiBookDepthStruct                                         = new BookDepthStruct();
            debugCmiBookDepthStructs                                        = new BookDepthStruct[1];
            debugCmiBookDepthStructs[0]                                     = debugCmiBookDepthStruct;

            debugCmiBookDepthStruct.productKeys                             = new ProductKeysStruct();
            debugCmiBookDepthStruct.productKeys.productKey                  = 83455748;
            debugCmiBookDepthStruct.productKeys.productType                 = ProductTypes.FUTURE;

            debugCmiBookDepthStruct.sessionName                             = "ONE_MAIN";
        }

//        debugCmiBookDepthStruct.buySideSequence                             = new OrderBookPriceStruct[1 + random.nextInt(10)];
        debugCmiBookDepthStruct.buySideSequence                             = new OrderBookPriceStruct[1];

        for (int i = 0; i < debugCmiBookDepthStruct.buySideSequence.length; i++)
        {
            debugCmiBookDepthStruct.buySideSequence[i]                      = new OrderBookPriceStruct(new PriceStruct((short) 10, 10, 0), 0, 0);
            debugCmiBookDepthStruct.buySideSequence[i].totalVolume          = 1 + random.nextInt(1000);
            debugCmiBookDepthStruct.buySideSequence[i].contingencyVolume    = 1 + random.nextInt(1000);
            debugCmiBookDepthStruct.buySideSequence[i].price.whole          = 1 + random.nextInt(10);
        }

//        debugCmiBookDepthStruct.sellSideSequence                            = new OrderBookPriceStruct[1 + random.nextInt(10)];
        debugCmiBookDepthStruct.sellSideSequence                            = new OrderBookPriceStruct[1];

        for (int i = 0; i < debugCmiBookDepthStruct.sellSideSequence.length; i++)
        {
            debugCmiBookDepthStruct.sellSideSequence[i]                     = new OrderBookPriceStruct(new PriceStruct((short) 10, 10, 0), 0, 0);
            debugCmiBookDepthStruct.sellSideSequence[i].totalVolume         = 1 + random.nextInt(1000);
            debugCmiBookDepthStruct.sellSideSequence[i].contingencyVolume   = 1 + random.nextInt(1000);
            debugCmiBookDepthStruct.sellSideSequence[i].price.whole         = 1 + random.nextInt(10);
        }

        return debugCmiBookDepthStructs;
    }

    public NBBOStruct[] debugMakeCmiNBBOStruct()
    {
        if (debugCmiNBBOStructs == null)
        {
            debugCmiNBBOStruct                                              = new NBBOStruct();
            debugCmiNBBOStructs                                             = new NBBOStruct[1];
            debugCmiNBBOStructs[0]                                          = debugCmiNBBOStruct;

            debugCmiNBBOStruct.productKeys                                  = new ProductKeysStruct();
            debugCmiNBBOStruct.productKeys.productKey                       = 1;
            debugCmiNBBOStruct.productKeys.productType                      = ProductTypes.FUTURE;

            debugCmiNBBOStruct.sentTime                                     = new TimeStruct();
            debugCmiNBBOStruct.sessionName                                  = "ONE_MAIN";

            debugCmiNBBOStruct.bidPrice                                     = new PriceStruct((short) 10, 10, 0);
            debugCmiNBBOStruct.askPrice                                     = new PriceStruct((short) 10, 10, 0);

            debugCmiNBBOStruct.bidExchangeVolume                            = new ExchangeVolumeStruct[]{new ExchangeVolumeStruct(FixMDMktField.string_Cboe, 100)};
            debugCmiNBBOStruct.askExchangeVolume                            = new ExchangeVolumeStruct[]{new ExchangeVolumeStruct(FixMDMktField.string_Cboe, 100)};
        }

        debugBuildCurrentTimeStruct(debugCmiNBBOStruct.sentTime);

        debugCmiNBBOStruct.bidPrice.whole                                   = 1 + random.nextInt(10);
        debugCmiNBBOStruct.askPrice.whole                                   = debugCmiNBBOStruct.bidPrice.whole + 1;

        debugCmiNBBOStruct.bidExchangeVolume[0].volume                      = 1 + random.nextInt(1000);
        debugCmiNBBOStruct.askExchangeVolume[0].volume                      = 1 + random.nextInt(1000);

        return debugCmiNBBOStructs;
    }

    public TickerStruct[] debugMakeCmiTickerStruct()
    {
        return null; //TODO build
    }

    public RecapStruct[] debugMakeCmiRecapStruct()
    {
        if (debugCmiRecapStructs == null)
        {
            debugCmiRecapStruct                                             = new RecapStruct();
            debugCmiRecapStructs                                            = new RecapStruct[1];
            debugCmiRecapStructs[0]                                         = debugCmiRecapStruct;

            debugCmiRecapStruct.productKeys                                 = new ProductKeysStruct();
            debugCmiRecapStruct.productKeys.productKey                      = 86376706;
            debugCmiRecapStruct.productKeys.productType                     = ProductTypes.FUTURE;

            debugCmiRecapStruct.tradeTime                                   = new TimeStruct();
            debugCmiRecapStruct.bidTime                                     = new TimeStruct();
            debugCmiRecapStruct.askTime                                     = new TimeStruct();

            debugCmiRecapStruct.sessionName                                 = "ONE_MAIN";

            debugCmiRecapStruct.recapPrefix                                 = "recapPrefix:";

            debugCmiRecapStruct.lastSalePrice                               = new PriceStruct((short) 10, 10, 0);
            debugCmiRecapStruct.bidPrice                                    = new PriceStruct((short) 10, 10, 0);
            debugCmiRecapStruct.askPrice                                    = new PriceStruct((short) 10, 10, 0);
            debugCmiRecapStruct.tick                                        = new PriceStruct((short) 10, 10, 0);
            debugCmiRecapStruct.lowPrice                                    = new PriceStruct((short) 10, 10, 0);
            debugCmiRecapStruct.highPrice                                   = new PriceStruct((short) 10, 10, 0);
            debugCmiRecapStruct.openPrice                                   = new PriceStruct((short) 10, 10, 0);
            debugCmiRecapStruct.closePrice                                  = new PriceStruct((short) 10, 10, 0);
            debugCmiRecapStruct.previousClosePrice                          = new PriceStruct((short) 10, 10, 0);
        }

        debugBuildCurrentTimeStruct(debugCmiRecapStruct.tradeTime);
        debugBuildCurrentTimeStruct(debugCmiRecapStruct.bidTime);
        debugBuildCurrentTimeStruct(debugCmiRecapStruct.askTime);

        debugCmiRecapStruct.openInterest                                    = 1234;
        debugCmiRecapStruct.tickDirection                                   = random.nextBoolean() ? '+' : '-';
        debugCmiRecapStruct.lastSaleVolume                                  = 100 + random.nextInt(1000);
        debugCmiRecapStruct.totalVolume                                     += debugCmiRecapStruct.lastSaleVolume;
        debugCmiRecapStruct.bidDirection                                    = random.nextBoolean() ? '+' : '-';

        debugCmiRecapStruct.lastSalePrice.whole                             = 1 + random.nextInt(10);
        debugCmiRecapStruct.bidPrice.whole                                  = 1 + random.nextInt(10);
        debugCmiRecapStruct.askPrice.whole                                  = 1 + random.nextInt(10);
        debugCmiRecapStruct.tick.whole                                      = 1 + random.nextInt(10);
        debugCmiRecapStruct.lowPrice.whole                                  = 1 + random.nextInt(10);
        debugCmiRecapStruct.highPrice.whole                                 = 1 + random.nextInt(10);
        debugCmiRecapStruct.openPrice.whole                                 = 1 + random.nextInt(10);
        debugCmiRecapStruct.closePrice.whole                                = 1 + random.nextInt(10);
        debugCmiRecapStruct.previousClosePrice.whole                        = 1 + random.nextInt(10);

        return debugCmiRecapStructs;
    }
*/
}
