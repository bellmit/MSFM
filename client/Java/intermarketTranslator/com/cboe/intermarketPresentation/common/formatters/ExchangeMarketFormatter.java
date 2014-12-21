//
// ------------------------------------------------------------------------
// FILE: ExchangeMarketFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ExchangeMarketFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ExchangeVolumeFormatStrategy;
import com.cboe.interfaces.presentation.marketData.ExchangeVolume;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.ExchangeMarket;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.intermarketPresentation.intermarketMessages.ExchangeMarketFactory;
import com.cboe.presentation.common.formatters.Formatter;
import com.cboe.presentation.common.formatters.ExchangeMarketInfoType;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;

/**
 * @author torresl@cboe.com
 */
class ExchangeMarketFormatter extends Formatter implements ExchangeMarketFormatStrategy
{
    public static final String BRIEF_STYLE_DELIMITER = "; ";
    public static final String FULL_STYLE_DELIMITER = "\n";
    public static final String MARKET_INFO_TYPE_LABEL = "Market Info Type";
    public static final String FULL_BID_LABEL = "Best Bid";
    public static final String FULL_ASK_LABEL = "Best Ask";
    public static final String BRIEF_BID_LABEL = "Bid";
    public static final String BRIEF_ASK_LABEL = "Ask";
    public ExchangeMarketFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        addStyle(BRIEF_STYLE, BRIEF_DESCRIPTION);
        addStyle(FULL_STYLE, FULL_DESCRIPTION);
        addStyle(FULL_STYLE_TWO_COLUMN_NAME, FULL_STYLE_TWO_COLUMN_DESCRIPTION);
        setDefaultStyle(FULL_STYLE);
    }

    public String format(ExchangeMarket exchangeMarket)
    {
        return format(exchangeMarket, getDefaultStyle());
    }

    public String format(ExchangeMarket exchangeMarket, String style)
    {
        validateStyle(style);
        // default is FULL
        StringBuffer buffer = new StringBuffer();
        String exchangeVolumeStyle = ExchangeVolumeFormatStrategy.FULL_STYLE;
        String delimiter = FULL_STYLE_DELIMITER;
        String marketInfoTypeLabel = MARKET_INFO_TYPE_LABEL;
        String bidLabel = FULL_BID_LABEL;
        String askLabel = FULL_ASK_LABEL;
        if (style.equals(BRIEF_STYLE))
        {
            exchangeVolumeStyle = ExchangeVolumeFormatStrategy.BRIEF_STYLE;
            delimiter = BRIEF_STYLE_DELIMITER;
            marketInfoTypeLabel = "";
            bidLabel = BRIEF_BID_LABEL;
            askLabel = BRIEF_ASK_LABEL;
        }
        if(marketInfoTypeLabel.length()>0)
        {
            buffer.append(marketInfoTypeLabel).append(": ");
        }
        if(! style.equals(FULL_STYLE_TWO_COLUMN_NAME))
        {
            buffer.append(ExchangeMarketInfoType.toString(exchangeMarket.getMarketInfoType())).append(delimiter);
            buffer.append(bidLabel).append(": ").append(exchangeMarket.getBestBidPrice().toString()).append(delimiter);
            buffer.append(FormatFactory.getExchangeVolumeFormatStrategy().format(exchangeMarket.getBidExchangeVolumes(), exchangeVolumeStyle));
            buffer.append(delimiter);
            buffer.append(askLabel).append(": ").append(exchangeMarket.getBestAskPrice().toString()).append(delimiter);
            buffer.append(FormatFactory.getExchangeVolumeFormatStrategy().format(exchangeMarket.getAskExchangeVolumes(), exchangeVolumeStyle));
        }
        else  // setup in two columns
        {
            int column2Margin = 37;
            int padding = 1;
            buffer.append(ExchangeMarketInfoType.toString(exchangeMarket.getMarketInfoType())).append(delimiter);
            StringBuffer tempBuffer1 = new StringBuffer(50);
            StringBuffer tempBuffer2 = new StringBuffer(50);
            tempBuffer1.append(bidLabel).append(": ").append(exchangeMarket.getBestBidPrice().toString());
            tempBuffer2.append(askLabel).append(": ").append(exchangeMarket.getBestAskPrice().toString());
            addField(buffer, tempBuffer1.toString(), column2Margin, padding, false, true);
            addField(buffer, tempBuffer2.toString(), column2Margin, padding, false, true);
            buffer.append(delimiter);

            int askExchangeVolumesLength = exchangeMarket.getAskExchangeVolumes().length;
            int bidExchangeVolumesLength = exchangeMarket.getBidExchangeVolumes().length;
            int maxLength = Math.max(askExchangeVolumesLength, bidExchangeVolumesLength);
            int margin = 8;
            int pad = 1;
            for(int i=0; i<maxLength; i++)
            {
                StringBuffer bidExchangeBuffer = new StringBuffer();
                if(i<bidExchangeVolumesLength)
                {
                    ExchangeVolume bidExchangeVolume = exchangeMarket.getBidExchangeVolumes()[i];
                    addField(bidExchangeBuffer, bidExchangeVolume.getExchange(), margin, pad, false, true);
                    addField(bidExchangeBuffer, Integer.toString(bidExchangeVolume.getVolume()), margin, pad, false, true);
                }
                addField(buffer, bidExchangeBuffer.toString(), column2Margin, padding, false, true);
                StringBuffer askExchangeBuffer = new StringBuffer();
                if(i<askExchangeVolumesLength)
                {
                    ExchangeVolume askExchangeVolume = exchangeMarket.getAskExchangeVolumes()[i];
                    addField(askExchangeBuffer, askExchangeVolume.getExchange(), margin, pad, false, true);
                    addField(askExchangeBuffer, Integer.toString(askExchangeVolume.getVolume()), margin, pad, false, true);
                }
                addField(buffer, askExchangeBuffer.toString(), column2Margin, padding, false, true);
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

    public String format(ExchangeMarketStruct exchangeMarketStruct)
    {
        return format(exchangeMarketStruct, getDefaultStyle());
    }

    public String format(ExchangeMarketStruct exchangeMarketStruct, String style)
    {
        return format(  ExchangeMarketFactory.createExchangeMarket(exchangeMarketStruct),
                        style);
    }
    public static void main(String[] args)
    {
        ExchangeMarketFormatter impl = new ExchangeMarketFormatter();
        ExchangeMarketStruct exchangeMarketStruct = new ExchangeMarketStruct();

        ExchangeVolumeStruct exchangeVolumeStruct1 = new ExchangeVolumeStruct();
        exchangeVolumeStruct1.exchange = "CBOE";
        exchangeVolumeStruct1.volume = 100;
        ExchangeVolumeStruct exchangeVolumeStruct2 = new ExchangeVolumeStruct();
        exchangeVolumeStruct2.exchange = "AMEX";
        exchangeVolumeStruct2.volume = 150;
        ExchangeVolumeStruct exchangeVolumeStruct3 = new ExchangeVolumeStruct();
        exchangeVolumeStruct3.exchange = "ISE";
        exchangeVolumeStruct3.volume = 300;
        ExchangeVolumeStruct[] bidExchangeVolumeStructs = new ExchangeVolumeStruct[]
        {
            exchangeVolumeStruct1, exchangeVolumeStruct2
        };
        ExchangeVolumeStruct[] askExchangeVolumeStructs = new ExchangeVolumeStruct[]
        {
            exchangeVolumeStruct2, exchangeVolumeStruct1, exchangeVolumeStruct3
        };
        exchangeMarketStruct.bidExchangeVolumes = bidExchangeVolumeStructs;
        exchangeMarketStruct.askExchangeVolumes = askExchangeVolumeStructs;
        exchangeMarketStruct.marketInfoType = ExchangeMarketInfoType.NBBO_ORDER_RECEIVED;
        exchangeMarketStruct.bestBidPrice = DisplayPriceFactory.create("1.25").toStruct();
        exchangeMarketStruct.bestAskPrice = DisplayPriceFactory.create("2.50").toStruct();
        String style = BRIEF_STYLE;
        if (args.length >= 1)
        {
            style = args[0];
        }
        System.out.println(impl.format(exchangeMarketStruct, style));

    }
}
