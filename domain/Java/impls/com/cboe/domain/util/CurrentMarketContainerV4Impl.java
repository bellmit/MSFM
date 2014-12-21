package com.cboe.domain.util;

import com.cboe.interfaces.domain.CurrentMarketContainerV4;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;

/**
 * User: Beniwalv
 * Date: Jul 30, 2010
 * Time: 2:25:06 PM
 * This is used by MDX enabled CFIX to remain consistent with IEC enabled CFIX.
 */
public class CurrentMarketContainerV4Impl implements CurrentMarketContainerV4
{

    private  CurrentMarketStructV4[] bestMarkets;
    private  CurrentMarketStructV4[] bestPublicMarkets;
    private int messageSequence;
    private int queueDepth;
    private short queueAction;

    public CurrentMarketContainerV4Impl(CurrentMarketStructV4[] bestMarkets, CurrentMarketStructV4[] bestPublicMarkets, int messageSequence, int queueDepth, short queueAction)
    {
        this.bestMarkets = bestMarkets;
        this.bestPublicMarkets = bestPublicMarkets;
        this.messageSequence = messageSequence;
        this.queueDepth = queueDepth;
        this.queueAction = queueAction;
    }
    
    public CurrentMarketContainerV4Impl(CurrentMarketStructV4[] bestMarkets, CurrentMarketStructV4[] bestPublicMarkets)
    {
        this.bestMarkets = bestMarkets;
        this.bestPublicMarkets = bestPublicMarkets;
        this.messageSequence = 0;
        this.queueDepth = 0;
        this.queueAction = 0;
    }

    /**
     * @return bestMarkets
     */
    public CurrentMarketStructV4[] getBestMarkets() {
        return bestMarkets;
    }

    /**
     * @return bestPublicMarkets
     */
    public CurrentMarketStructV4[] getBestPublicMarkets() {
        return bestPublicMarkets;
    }

    public int getMessageSequence()
    {
        return this.messageSequence;
    }

    public int getQueueDepth()
    {
        return this.queueDepth;
    }

    public short getQueueAction()
    {
        return this.queueAction;
    }

    public void setBestMarkets(CurrentMarketStructV4[] bestMarkets) {
        this.bestMarkets = bestMarkets;
    }

    public void setBestPublicMarkets(CurrentMarketStructV4[] bestPublicMarkets) {
        this.bestPublicMarkets = bestPublicMarkets;
    }

    public void setMessageSequence(int messageSequence)
    {
        this.messageSequence = messageSequence;
    }

    public void setQueueDepth(int queueDepth)
    {
        this.queueDepth = queueDepth;
    }

    public void setQueueAction(short queueAction)
    {
        this.queueAction = queueAction;
    }
}
