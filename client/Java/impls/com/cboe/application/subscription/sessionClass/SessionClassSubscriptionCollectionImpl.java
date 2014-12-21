package com.cboe.application.subscription.sessionClass;

import com.cboe.application.subscription.SubscriptionAbstractCollection;
import com.cboe.interfaces.application.subscription.SessionClassSubscriptionCollection;
import com.cboe.interfaces.application.subscription.Subscription;
import com.cboe.interfaces.domain.SessionKeyWrapper;

public class SessionClassSubscriptionCollectionImpl extends SubscriptionAbstractCollection implements SessionClassSubscriptionCollection
{
    protected Subscription textMessagingSubscription;
    protected Subscription currentMarketSubscription;
    protected Subscription recapSubscription;
    protected Subscription tickerSubscription;
    protected Subscription bookDepthSubscription;
    protected Subscription expectedOpeningPriceSubscription;
    protected Subscription auctionSubscription;
    protected Subscription rfqSubscription;
    protected Subscription ltlsSubscription;
    protected SessionKeyWrapper sessionClass;

    public SessionClassSubscriptionCollectionImpl(SessionKeyWrapper sessionClass, boolean defaultSubscriptionOn)
    {
        super(defaultSubscriptionOn);
        this.sessionClass = sessionClass;
        textMessagingSubscription = new TextMessagingSubscriptionImpl(sessionClass, this);
        currentMarketSubscription = new CurrentMarketSubscriptionImpl(sessionClass, this);
        recapSubscription = new RecapSubscriptionImpl(sessionClass, this);
        tickerSubscription = new TickerSubscriptionImpl(sessionClass, this);
        bookDepthSubscription = new BookDepthSubscriptionImpl(sessionClass, this);
        expectedOpeningPriceSubscription = new ExpectedOpeningPriceSubscriptionImpl(sessionClass, this);
        auctionSubscription = new AuctionSubscriptionImpl(sessionClass, this);
        rfqSubscription = new RFQSubscriptionImpl(sessionClass, this);
        ltlsSubscription = new LargeTradeLastSaleSubscriptionImpl(sessionClass, this);
        
        if(defaultSubscriptionOn)
        {
            textMessagingSubscription.setDefaultSubscriptionFlag(true);
        }
    }
    
    public SessionClassSubscriptionCollectionImpl(boolean defaultSubscriptionOn)
    {
        super(defaultSubscriptionOn);
    }

    public Subscription getCurrentMarketSubscription()
    {
        return currentMarketSubscription;
    }
    public Subscription getTickerSubscription()
    {
        return tickerSubscription;
    }
    public Subscription getRecapSubscription()
    {
        return recapSubscription;
    }
    public Subscription getBookDepthSubscription()
    {
        return bookDepthSubscription;
    }
    public Subscription getExpectedOpeningPriceSubscription()
    {
        return expectedOpeningPriceSubscription;
    }
    public Subscription getRFQSubscription()
    {
        return rfqSubscription;
    }
    public Subscription getAuctionSubscription()
    {
        return auctionSubscription;
    }
    public Subscription getTextMessagingSubscription()
    {
        return textMessagingSubscription;
    }

	public Subscription getLargeTradeLastSaleSubscription() {
		return ltlsSubscription;
	}
}
