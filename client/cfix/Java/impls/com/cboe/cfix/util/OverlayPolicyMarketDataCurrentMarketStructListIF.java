package com.cboe.cfix.util;

/**
 * OverlayPolicyMarketDataCurrentMarketStructListIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public interface OverlayPolicyMarketDataCurrentMarketStructListIF extends OverlayPolicyMarketDataListIF
{
    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStruct   struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, CurrentMarketStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
}
