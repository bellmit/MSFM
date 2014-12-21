package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataQueryIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.exceptions.*;

public interface CfixMarketDataQueryIF
{
    public void setCfixSessionManager(CfixSessionManager cfixSessionManager)                                            throws SystemException, CommunicationException, AuthorizationException;
    public CfixSessionManager getCfixSessionManager()                                                                   throws SystemException, CommunicationException, AuthorizationException;

    public void setCfixMarketDataConsumer(CfixMarketDataConsumer cfixFixMarketDataConsumer)                             throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public CfixMarketDataConsumer getCfixMarketDataConsumer()                                                           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeCurrentMarketByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeCurrentMarketByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeCurrentMarket(String mdReqID)                                                                throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeBookDepthByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)             throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeBookDepthByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)               throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeBookDepth(String mdReqID)                                                                    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void snapshotBookDepthByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)                throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeBookDepthUpdateByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeBookDepthUpdateByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeBookDepthUpdate(String mdReqID)                                                              throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeExpectedOpeningPriceByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeExpectedOpeningPriceByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeExpectedOpeningPrice(String mdReqID)                                                         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeNbboByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeNbboByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)                    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeNbbo(String mdReqID)                                                                         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeRecapByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)                 throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeRecapByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)                   throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeRecap(String mdReqID)                                                                        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeTickerByProduct(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)                throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeTickerByClass(CfixMarketDataConsumerHolder cfixFixMarketDataConsumerHolder)                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeTicker(String mdReqID)                                                                       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void unsubscribeListener()                                                                                   throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
}
