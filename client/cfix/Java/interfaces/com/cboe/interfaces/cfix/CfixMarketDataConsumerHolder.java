package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataConsumerHolder.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.exceptions.*;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;

public interface CfixMarketDataConsumerHolder
{
    public CfixMarketDataConsumer         getCfixMarketDataConsumer();
    public OverlayPolicyMarketDataListIF  getOverlayPolicyMarketDataList();
    public boolean                        containsSessionProductStruct();
    public SessionProductStruct           getSessionProductStruct();
    public SessionClassStruct             getSessionClassStruct();
    public MethodInstrumentor             getMethodInstrumentor();

    public void acceptMarketDataCurrentMarket(CurrentMarketStruct[] currentMarketStructs)                                               throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataCurrentMarket(CurrentMarketStruct[] currentMarketStructs, int offset, int length)                       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataCurrentMarket(CurrentMarketStruct currentMarketStruct)                                                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataBookDepth(BookDepthStruct[] bookDepthStructs)                                                           throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataBookDepth(BookDepthStruct[] bookDepthStructs, int offset, int length)                                   throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataBookDepth(BookDepthStruct bookDepthStruct)                                                              throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataBookDepthUpdate(BookDepthStruct[] bookDepthStructs)                                                     throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataBookDepthUpdate(BookDepthStruct[] bookDepthStructs, int offset, int length)                             throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataBookDepthUpdate(BookDepthStruct bookDepthStruct)                                                        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataRecap(RecapStruct[] recapStructs)                                                                       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataRecap(RecapStruct[] recapStructs, int offset, int length)                                               throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataRecap(RecapStruct recapStruct)                                                                          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataTicker(TickerStruct[] tickerStructs)                                                                    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataTicker(TickerStruct[] tickerStructs, int offset, int length)                                            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataTicker(TickerStruct tickerStruct)                                                                       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataNbbo(NBBOStruct[] nbboStructs)                                                                          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataNbbo(NBBOStruct[] nbboStructs, int offset, int length)                                                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataNbbo(NBBOStruct nbboStruct)                                                                             throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs)                          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct[] expectedOpeningPriceStructs, int offset, int length)  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataExpectedOpeningPrice(ExpectedOpeningPriceStruct expectedOpeningPriceStruct)                             throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
}
