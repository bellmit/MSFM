package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataDispatcherHome.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.idl.cmiSession.*;

public interface CfixMarketDataDispatcherHome
{
    public CfixMarketDataDispatcherIF create(SessionClassStruct sessionClassStruct, int dispatchType);
    public CfixMarketDataDispatcherIF create(SessionProductStruct sessionProductStruct, int dispatchType);
    public CfixMarketDataDispatcherIF find(SessionClassStruct sessionClassStruct, int dispatchType);
    public CfixMarketDataDispatcherIF find(SessionProductStruct sessionProductStruct, int dispatchType);
    public int size();
    public void accept(CfixMarketDataDispatcherVisitor cfixMarketDataDispatcherVisitor) throws Exception;
}
