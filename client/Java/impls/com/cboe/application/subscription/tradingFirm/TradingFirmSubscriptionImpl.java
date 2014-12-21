package com.cboe.application.subscription.tradingFirm;

import com.cboe.application.subscription.SubscriptionImpl;
import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.interfaces.domain.TradingFirmGroupWrapper;

/**
 * Author: mahoney
 * Date: Apr 22, 2008
 */
public abstract class TradingFirmSubscriptionImpl extends SubscriptionImpl
{
    TradingFirmGroupWrapper tradingFirm;

    public TradingFirmSubscriptionImpl(TradingFirmGroupWrapper firm, SubscriptionAbstractCollection subscriptionCollection)
    {
        super(subscriptionCollection);
        tradingFirm = firm;
        defaultKey = tradingFirm;
    }
}
