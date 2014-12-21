package com.cboe.application.inprocess.marketData;

import com.cboe.exceptions.SystemException;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelListener;

import java.util.HashSet;

class RemoteSubscriptionTracker
{
    static final short BOOK_DEPTH = 1;
    static final short CURRENT_MARKET = 2;
    static final short EXPECTED_OPENING_PRICE = 3;
    static final short NBBO = 4;
    static final short RECAP = 5;
    static final short TICKER = 6;
    
    private final long interval;
    private final int maxResubscribeAttempts;
    
    private ChannelListener proxyListener;
    private short marketDataSubscriptionType;
    
    private long timestamp;
    private int resubscribeAttemptCount;
    
    private HashSet subscriptions;
    
    RemoteSubscriptionTracker(long interval, int maxResubscribeAttempts)
    {
        this.interval = interval;
        this.maxResubscribeAttempts = maxResubscribeAttempts;
        this.resubscribeAttemptCount = 0;

        this.subscriptions = new HashSet(11);
    }
    
    synchronized void incrementResubscribeCount() throws Exception
    {
        long currentTime = TimeServiceWrapper.getCurrentDateTimeInMillis();
        
        if(currentTime - timestamp > interval)
        {
            timestamp = currentTime;
            resubscribeAttemptCount = 0;
        }
        
        resubscribeAttemptCount++;
        
        if(resubscribeAttemptCount > maxResubscribeAttempts)
        {
            throw new Exception("Maximum resubscribe attempts exceeded for resubscribe interval.");
        }
    }
    
    void setProxyListener(ChannelListener proxyListener)
    {
        this.proxyListener = proxyListener;
    }

    void setMarketDataSubscriptionType(short marketDataSubscriptionType)
    {
        this.marketDataSubscriptionType = marketDataSubscriptionType;
    }

    ChannelListener getProxyListener()
    {
        return proxyListener;
    }
    
    short getMarketDataSubscriptionType()
    {
        return marketDataSubscriptionType;
    }
    
    synchronized void addSubscription(SessionKeyContainer sessionKey)
    {
        subscriptions.add(sessionKey);
    }
    
    synchronized void deleteSubscription(SessionKeyContainer sessionKey)
    {
        subscriptions.remove(sessionKey);
    }

    int getSubscriptionCount()
    {
        return subscriptions.size();
    }
}
