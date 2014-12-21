package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataDispatcherInstrumentation.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface CfixMarketDataDispatcherInstrumentation extends Cloneable
{
    public int  getConsumedMessageCount();
    public int  getProcessedMessageCount();
    public int  getDispatchedMessageCount();

    public long getConsumedMessageTime();
    public long getProcessedMessageTime();
    public long getDispatchedMessageTime();

    public Object clone() throws CloneNotSupportedException;
}
