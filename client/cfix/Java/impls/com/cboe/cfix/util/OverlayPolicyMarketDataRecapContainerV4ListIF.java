package com.cboe.cfix.util;

import com.cboe.interfaces.cfix.OverlayPolicyMarketDataListIF;
import com.cboe.interfaces.cfix.CfixMarketDataConsumer;
import com.cboe.interfaces.domain.RecapContainerV4IF;
import com.cboe.exceptions.*;

/**
 * Created by IntelliJ IDEA.
 * To change this template use File | Settings | File Templates.
 */
public interface OverlayPolicyMarketDataRecapContainerV4ListIF extends OverlayPolicyMarketDataListIF
{
    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapContainerV4IF recapContainerV4IF)                            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void add(CfixMarketDataConsumer cfixMarketDataConsumer, RecapContainerV4IF[] recapContainersV4IF, int offset, int length) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
}
