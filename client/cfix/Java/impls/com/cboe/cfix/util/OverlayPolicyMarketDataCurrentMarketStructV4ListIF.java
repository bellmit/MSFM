package com.cboe.cfix.util;

import com.cboe.interfaces.cfix.OverlayPolicyMarketDataListIF;
import com.cboe.interfaces.cfix.CfixMarketDataConsumer;
import com.cboe.idl.cmiMarketData.CurrentMarketStructV4;
import com.cboe.exceptions.*;

/**
 * OverlayPolicyMarketDataCurrentMarketStructListIF.java
 *
 * @author Dmitry Volpyansky / Vivek Beniwal
 *
 */
public interface OverlayPolicyMarketDataCurrentMarketStructV4ListIF extends OverlayPolicyMarketDataListIF
{
    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStructV4 struct)                                throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStructV4[] structs, int offset, int length)     throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
}
