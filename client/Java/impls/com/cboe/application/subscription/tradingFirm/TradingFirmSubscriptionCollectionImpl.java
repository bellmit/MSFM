package com.cboe.application.subscription.tradingFirm;

import com.cboe.interfaces.application.subscription.FirmSubscriptionCollection;
import com.cboe.interfaces.application.subscription.Subscription;
import com.cboe.interfaces.domain.TradingFirmGroupWrapper;
import com.cboe.application.subscription.SubscriptionAbstractCollection;

/**
 * Author: mahoney
 * Date: Apr 22, 2008
 */
public class TradingFirmSubscriptionCollectionImpl extends SubscriptionAbstractCollection implements FirmSubscriptionCollection
{
    TradingFirmGroupWrapper tradingFirm;
    Subscription orderSubscription;
    Subscription quoteSubscription;

    public TradingFirmSubscriptionCollectionImpl(TradingFirmGroupWrapper firm, boolean defaultSubscriptionOn)
    {
        super(defaultSubscriptionOn);
        this.tradingFirm = firm;
        orderSubscription = new OrderSubscriptionImpl(tradingFirm, this);
        quoteSubscription = new QuoteSubscriptionImpl(tradingFirm, this);
        if(defaultSubscriptionOn)
        {
            orderSubscription.setDefaultSubscriptionFlag(true);
            quoteSubscription.setDefaultSubscriptionFlag(true);
        }
    }

    public Subscription getOrderSubscription()
    {
        return orderSubscription;
    }

    public Subscription getQuoteSubscription()
    {
        return quoteSubscription;
    }
}
