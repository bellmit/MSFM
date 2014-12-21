package com.cboe.interfaces.domain.session;

import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.idl.order.OrderAcknowledgeStructV3;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStructV3;

import com.cboe.exceptions.*;

/**
 * This is the acknowledge session manager interface
 * @author Keith A. Korecky
 */
public interface AcknowledgeSessionManager
{

    /**
    * Post a message delivery verification with the Text Messaging Service
    * @return void
    */
    public void acceptMessageDelivery(String userId, int messageId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException;

    /**
    * Post a acknowledge/verification with the OSSS service
    * @return void
    */
    public void ackOrderStatusV3(OrderAcknowledgeStructV3 orderAcknowledge)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    /**
    * Post a acknowledge/verification with the QSSS service
    * @return void
    */
    public void ackQuoteStatusV3(QuoteAcknowledgeStructV3 quoteAcknowledge)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;


}
