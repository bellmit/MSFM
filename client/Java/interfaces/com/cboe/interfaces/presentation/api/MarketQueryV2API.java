//
// ------------------------------------------------------------------------
// FILE: MarketQueryV2API.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.api;

import com.cboe.exceptions.*;
import com.cboe.util.event.EventChannelListener;
import com.cboe.interfaces.presentation.bookDepth.DetailBookDepth;

public interface MarketQueryV2API extends MarketQueryAPI
{
    DetailBookDepth getBookDepthDetails(String session, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException;

    void subscribeBookDepthForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeBookDepthForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeBookDepthUpdateForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeBookDepthUpdateForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeCurrentMarketForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeCurrentMarketForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeExpectedOpeningPriceForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeExpectedOpeningPriceForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeNBBOForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeNBBOForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeRecapForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeRecapForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeTickerForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeTickerForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeBookDepthForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeBookDepthForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeBookDepthUpdateForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeBookDepthUpdateForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeCurrentMarketForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeCurrentMarketForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeExpectedOpeningPriceForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeExpectedOpeningPriceForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeNBBOForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeNBBOForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeRecapForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeRecapForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeTickerForClassV2(String session, int classKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeTickerForProductV2(String session, int productKey,  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
