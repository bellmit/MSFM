package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiQuote.ClassQuoteResultStructV2;
import com.cboe.exceptions.*;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.interfaces.application.inprocess.LockedQuoteStatusConsumer;


/**
 * @author Jing Chen
 */
public interface QuoteQuery
{
    public void subscribeQuoteStatus(QuoteStatusConsumer quoteStatusConsumer, boolean includeUserInitiatedStatus)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    public void subscribeQuoteStatusForFirm(QuoteStatusConsumer quoteStatusConsumer)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    public void subscribeQuoteLockedNotification(LockedQuoteStatusConsumer lockedQuoteStatusConsumer)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    public void subscribeRFQ(String session, int classKey, RFQConsumer rfqConsumer)
            throws SystemException, CommunicationException, DataValidationException, AuthorizationException;
    public void unsubscribeRFQ(String session, int classKey, RFQConsumer rfqConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public short getQuoteStatus(String session, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException;

}
