//
// ------------------------------------------------------------------------
// FILE: QuoteV2API.java
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
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV2;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.util.event.EventChannelListener;

public interface QuoteV2API extends QuoteAPI
{
    void initializeQuoteV2CallbackListener()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    ClassQuoteResultStructV2[] acceptQuotesForClassV2(int classKey, QuoteEntryStruct[] quoteEntryStructs)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException, TransactionFailedException;

    void subscribeQuoteLockedNotification(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;

    void unsubscribeQuoteLockedNotification(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeQuoteLockedNotificationForClass(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;

    void unsubscribeQuoteLockedNotificationForClass(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeQuoteStatusV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;

    void unsubscribeQuoteStatusV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeQuoteStatusForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;

    void unsubscribeQuoteStatusForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeQuoteStatusForFirmForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;

    void unsubscribeQuoteStatusForFirmForClassV2(int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeQuoteStatusForFirmV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;

    void unsubscribeQuoteStatusForFirmV2(EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeRFQV2(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void unsubscribeRFQV2(String session, int classKey, EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    void subscribeAllEventsQuoteStatusV2(EventChannelListener clientListener) throws Exception;
    void unsubscribeAllEventsQuoteStatusV2(EventChannelListener clientListener) throws Exception;
    void subscribeAllEventsQuoteStatusForClassV2(int classKey, EventChannelListener clientListener) throws Exception;
    void unsubscribeAllEventsQuoteStatusForClassV2(int classKey, EventChannelListener clientListener) throws Exception;
    void subscribeAllEventsQuoteStatusForProductV2(int productKey, EventChannelListener clientListener) throws Exception;
    void unsubscribeAllEventsQuoteStatusForProductV2(int productKey, EventChannelListener clientListener) throws Exception;

    void subscribeQuoteStatusForProductV2(int productKey, EventChannelListener clientListener) throws Exception;
    void subscribeQuoteDeletedReportV2(EventChannelListener clientListener) throws Exception;
    void subscribeQuoteDeletedReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception;
    void subscribeQuoteDeletedReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception;
    void subscribeQuoteFilledReportV2(EventChannelListener clientListener) throws Exception;
    void subscribeQuoteFilledReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception;
    void subscribeQuoteFilledReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception;
    void subscribeQuoteBustReportV2(EventChannelListener clientListener) throws Exception;
    void subscribeQuoteBustReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception;
    void subscribeQuoteBustReportForProductV2(int productKey, EventChannelListener clientListener) throws Exception;

    void unsubscribeQuoteStatusForProductV2(int productKey, EventChannelListener clientListener) throws Exception;
    void unsubscribeQuoteDeletedReportV2(EventChannelListener clientListener) throws Exception;
    void unsubscribeQuoteDeletedReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception;
    void unsubscribeQuoteFilledReportV2(EventChannelListener clientListener) throws Exception;
    void unsubscribeQuoteFilledReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception;
    void unsubscribeQuoteBustReportV2(EventChannelListener clientListener) throws Exception;
    void unsubscribeQuoteBustReportForClassV2(int classKey, EventChannelListener clientListener) throws Exception;
}
