package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataDispatcherVisitor.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface CfixMarketDataDispatcherVisitor
{
    public boolean visit(CfixMarketDataDispatcherIF cfixMarketDataDispatcher) throws Exception;
    public boolean exceptionHappened(Exception exception) throws Exception;
    public Exception getException() throws Exception;
}
