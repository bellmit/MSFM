package com.cboe.cfix.cas.marketData;

/**
 * CfixMarketDataDispatcherInstrumentationImpl.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.interfaces.cfix.*;

public final class CfixMarketDataDispatcherInstrumentationImpl implements CfixMarketDataDispatcherInstrumentation
{
    public int consumedMessages;
    public int processedMessages;
    public int dispatchedMessages;

    public long consumedTime;
    public long processedTime;
    public long dispatchedTime;

    public int getConsumedMessageCount()
    {
        return consumedMessages;
    }

    public int getProcessedMessageCount()
    {
        return processedMessages;
    }

    public int getDispatchedMessageCount()
    {
        return dispatchedMessages;
    }

    public long getConsumedMessageTime()
    {
        return consumedTime;
    }

    public long getProcessedMessageTime()
    {
        return processedTime;
    }

    public long getDispatchedMessageTime()
    {
        return dispatchedTime;
    }

    public void incMessagesConsumed()
    {
        consumedMessages++;
        consumedTime = System.currentTimeMillis();
    }

    public void incMessagesProcessed()
    {
        processedMessages++;
        processedTime = System.currentTimeMillis();
    }

    public void incMessagesDispatched()
    {
        dispatchedMessages++;
        dispatchedTime = System.currentTimeMillis();
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
