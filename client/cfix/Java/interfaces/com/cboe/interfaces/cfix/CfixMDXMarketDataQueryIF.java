package com.cboe.interfaces.cfix;

/**
 * CfixMDXMarketDataQueryIF.java
 *
 * @author Dmitry Volpyansky / Vivek Beniwal
 *
 */

import com.cboe.exceptions.*;

public interface CfixMDXMarketDataQueryIF
{
    public void setCfixSessionManager(CfixSessionManager cfixSessionManager)                                                    throws SystemException, CommunicationException, AuthorizationException;
    public CfixSessionManager getCfixSessionManager()                                                                           throws SystemException, CommunicationException, AuthorizationException;

    public void setCfixMarketDataConsumer(CfixMarketDataConsumer cfixFixMarketDataConsumer)                                     throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public CfixMarketDataConsumer getCfixMarketDataConsumer()                                                                   throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeCurrentMarketByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeCurrentMarketByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)             throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeCurrentMarket(String mdReqID)                                                                        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeBookDepthByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)               throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeBookDepthByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)                 throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeBookDepth(String mdReqID)                                                                            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void snapshotBookDepthByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeBookDepthUpdateByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)         throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeBookDepthUpdateByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeBookDepthUpdate(String mdReqID)                                                                      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeExpectedOpeningPriceByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeExpectedOpeningPriceByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeExpectedOpeningPrice(String mdReqID)                                                                 throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeNbboByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)                    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeNbboByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)                      throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeNbbo(String mdReqID)                                                                                 throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeRecapByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)                   throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeRecapByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)                     throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeRecap(String mdReqID)                                                                                throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void   subscribeTickerByProduct(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void   subscribeTickerByClass(CfixMDXMarketDataConsumerHolder cfixFixMDXMarketDataConsumerHolder)                    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void unsubscribeTicker(String mdReqID)                                                                               throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void unsubscribeListener()                                                                                           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
}
