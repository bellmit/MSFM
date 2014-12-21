package com.cboe.interfaces.cfix;

/**
 * @author beniwalv
 */

public interface CfixMDXMarketDataDispatcherVisitor
{
    public boolean visit(CfixMDXMarketDataDispatcherIF cfixMDXMarketDataDispatcher) throws Exception;
    public boolean exceptionHappened(Exception exception) throws Exception;
    public Exception getException() throws Exception;

}
