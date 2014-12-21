//
// ------------------------------------------------------------------------
// FILE: OrderQueryV2API.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.util.event.EventChannelListener;

public interface OrderQueryV2API extends OrderQueryAPI
{
    void subscribeOrderStatusForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void subscribeOrderStatusForFirmForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void subscribeOrderStatusV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void unsubscribeOrderStatusForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void unsubscribeOrderStatusForFirmForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    void unsubscribeOrderStatusV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
