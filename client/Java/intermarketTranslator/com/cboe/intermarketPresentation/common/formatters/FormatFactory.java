//
// ------------------------------------------------------------------------
// FILE: FormatFactory.java
//
// PACKAGE: com.cboe.intermarketPresentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.*;

/**
 * @author torresl@cboe.com
 */
public class FormatFactory
{
    static ExchangeVolumeFormatter exchangeVolumeFormatter = null;
    static ExchangeMarketFormatter exchangeMarketFormatter = null;
    static HeldOrderFormatter heldOrderFormatter = null;
    static OrderFillRejectFormatter orderFillRejectFormatter = null;
    static AlertHeaderFormatter alertHeaderFormatter = null;
    static AlertFormatter alertFormatter;
    static SatisfactionAlertFormatter satisfactionAlertFormatter;

    private FormatFactory(){}

    public static ExchangeVolumeFormatStrategy getExchangeVolumeFormatStrategy()
    {
        if(exchangeVolumeFormatter == null)
        {
            exchangeVolumeFormatter = new ExchangeVolumeFormatter();
        }
        return exchangeVolumeFormatter;
    }

    public static ExchangeMarketFormatStrategy getExchangeMarketFormatStrategy()
    {
        if(exchangeMarketFormatter == null)
        {
            exchangeMarketFormatter = new ExchangeMarketFormatter();
        }
        return exchangeMarketFormatter;
    }

    public static HeldOrderFormatStrategy getHeldOrderFormatStrategy()
    {
        if(heldOrderFormatter == null)
        {
            heldOrderFormatter = new HeldOrderFormatter();
        }
        return heldOrderFormatter;
    }

    public static OrderFillRejectFormatStrategy getOrderFillRejectFormatStrategy()
    {
        if (orderFillRejectFormatter == null)
        {
            orderFillRejectFormatter = new OrderFillRejectFormatter();
        }
        return orderFillRejectFormatter;
    }

    public static AlertHeaderFormatStrategy getAlertHeaderFormatStrategy()
    {
        if(alertHeaderFormatter == null)
        {
            alertHeaderFormatter = new AlertHeaderFormatter();
        }
        return alertHeaderFormatter;
    }

    public static AlertFormatStrategy getAlertFormatStrategy()
    {
        if (alertFormatter == null)
        {
            alertFormatter = new AlertFormatter();
        }
        return alertFormatter;
    }

    public static SatisfactionAlertFormatStrategy getSatisfactionAlertFormatStrategy()
    {
        if (satisfactionAlertFormatter == null)
        {
            satisfactionAlertFormatter = new SatisfactionAlertFormatter();
        }
        return satisfactionAlertFormatter;
    }

}
