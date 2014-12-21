package com.cboe.cfix.util;

/**
 * OverlayPolicyMarketDataRecapStructListIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.cfix.*;

public interface OverlayPolicyMarketDataRecapStructListIF extends OverlayPolicyMarketDataListIF
{
    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapStruct   struct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapStruct[] structs, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
}
