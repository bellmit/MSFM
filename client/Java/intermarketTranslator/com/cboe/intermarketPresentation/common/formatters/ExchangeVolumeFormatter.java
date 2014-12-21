//
// ------------------------------------------------------------------------
// FILE: ExchangeVolumeFormatter.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ExchangeVolumeFormatStrategy;
import com.cboe.interfaces.presentation.marketData.ExchangeVolume;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.cboe.presentation.marketData.ExchangeVolumeFactory;
import com.cboe.presentation.common.formatters.Formatter;

/**
 * @author torresl@cboe.com
 */
class ExchangeVolumeFormatter extends Formatter implements ExchangeVolumeFormatStrategy
{
    public static final String  BRIEF_STYLE_DELIMITER = "; ";
    public static final String  FULL_STYLE_DELIMITER = "\n";
    public static final String  BRIEF_EXCHANGE_VOLUME_SEPARATOR = " ";
    public static final String  FULL_EXCHANGE_VOLUME_SEPARATOR = "\n";
    public static final String  EXCHANGE_TEXT = "Exchange";
    public static final String  VOLUME_TEXT = "Volume";
    public ExchangeVolumeFormatter()
    {
        addStyle(BRIEF_STYLE, BRIEF_DESCRIPTION);
        addStyle(FULL_STYLE, FULL_DESCRIPTION);
        addStyle(BID_STYLE, BID_DESCRIPTION);
        addStyle(ASK_STYLE, ASK_DESCRIPTION);
        setDefaultStyle(FULL_STYLE);
    }

    public String format(ExchangeVolume exchangeVolume)
    {
        return format(exchangeVolume, getDefaultStyle());
    }

    public String format(ExchangeVolume exchangeVolume, String style)
    {
        return format(exchangeVolume.getStruct(), style);
    }

    public String format(ExchangeVolumeStruct exchangeVolumeStruct)
    {
        return format(exchangeVolumeStruct, getDefaultStyle());
    }

    public String format(ExchangeVolumeStruct exchangeVolumeStruct, String style)
    {
        ExchangeVolumeStruct[] structs = new ExchangeVolumeStruct[] {
            exchangeVolumeStruct
        };
        return format(structs, style);
    }

    public String format(ExchangeVolume[] exchangeVolumes)
    {
        return format(exchangeVolumes, getDefaultStyle());
    }

    public String format(ExchangeVolume[] exchangeVolumes, String style)
    {
        ExchangeVolumeStruct[] exchangeVolumeStructs = new ExchangeVolumeStruct[exchangeVolumes.length];
        for (int i = 0; i < exchangeVolumeStructs.length; i++)
        {
            exchangeVolumeStructs[i] = exchangeVolumes[i].getStruct();
        }
        return format(exchangeVolumeStructs, style);
    }

    public String format(ExchangeVolumeStruct[] exchangeVolumeStructs)
    {
        return format(exchangeVolumeStructs, getDefaultStyle());
    }

    public String format(ExchangeVolumeStruct[] exchangeVolumeStructs, String style)
    {
        validateStyle(style);
        // assume up to 6 chars for exchange and 6 chars for each qty and one char for delimiter
        StringBuffer buffer = new StringBuffer(exchangeVolumeStructs.length * 13);
        boolean useDelimiter = exchangeVolumeStructs.length > 1;

        // defaults are for FULL_STYLE

        boolean includeDelimiterForLastElement = true;
        String delimiter = FULL_STYLE_DELIMITER;
        String exchangeVolumeSeparator = FULL_EXCHANGE_VOLUME_SEPARATOR;
        String exchangeText = EXCHANGE_TEXT;
        String volumeText = VOLUME_TEXT;

        if(!style.equals(FULL_STYLE))
        {
            delimiter = BRIEF_STYLE_DELIMITER;
            includeDelimiterForLastElement = true;
            exchangeVolumeSeparator = BRIEF_EXCHANGE_VOLUME_SEPARATOR;
            exchangeText = "";
            volumeText = "";
        }

        for (int i = 0; i < exchangeVolumeStructs.length; i++)
        {
            ExchangeVolumeStruct exchangeVolumeStruct = exchangeVolumeStructs[i];
            if (style.equals(BID_STYLE))
            {
                // for bid followed by ask, bid exchange is after bid volume
                if(volumeText.length() > 0)
                {
                    buffer.append(volumeText).append(": ");
                }
                buffer.append(exchangeVolumeStruct.volume);
                buffer.append(exchangeVolumeSeparator);
                if(exchangeText.length()>0)
                {
                    buffer.append(exchangeText).append(": ");
                }
                buffer.append(exchangeVolumeStruct.exchange);
            }
            else
            {
                if(exchangeText.length() > 0)
                {
                    buffer.append(exchangeText).append(": ");
                }
                buffer.append(exchangeVolumeStruct.exchange);
                buffer.append(exchangeVolumeSeparator);
                if(volumeText.length() > 0)
                {
                    buffer.append(volumeText).append(": ");
                }
                buffer.append(exchangeVolumeStruct.volume);
            }
            if (useDelimiter && includeDelimiterForLastElement && (i != exchangeVolumeStructs.length - 1))
            {
                buffer.append(delimiter);
            }
        }

        return buffer.toString();
    }
    public static void main(String[] args)
    {
        ExchangeVolumeFormatter impl = new ExchangeVolumeFormatter();
        ExchangeVolumeStruct exchangeVolumeStruct1 = new ExchangeVolumeStruct();
        exchangeVolumeStruct1.exchange = "CBOE";
        exchangeVolumeStruct1.volume = 100;
        ExchangeVolumeStruct exchangeVolumeStruct2 = new ExchangeVolumeStruct();
        exchangeVolumeStruct2.exchange = "AMEX";
        exchangeVolumeStruct2.volume = 150;
        ExchangeVolume ev1 = ExchangeVolumeFactory.createExchangeVolume(exchangeVolumeStruct1);
        ExchangeVolume ev2 = ExchangeVolumeFactory.createExchangeVolume(exchangeVolumeStruct2);
        ExchangeVolume[] exchangeVolumes = new ExchangeVolume[]
        {
            ev1, ev2
        };
        String style = BRIEF_STYLE;
        if(args.length>=1)
        {
            style = args[0];
        }
        System.out.println(impl.format(exchangeVolumes, style));
    }
}
