package com.cboe.interfaces.cfix;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.exceptions.*;
import com.cboe.interfaces.domain.RecapContainerV4IF;

/**
 * CfixMDXMarketDataConsumerHolder.java
 *
 * @author Dmitry Volpyansky / Vivek Beniwal
 *
 */
public interface CfixMDXMarketDataConsumerHolder extends CfixMarketDataConsumerHolder
{
    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4[] currentMarketStructsV4)                                               throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4[] currentMarketStructsV4, int offset, int length)                       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataCurrentMarket(CurrentMarketStructV4 currentMarketStructV4)                                                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void acceptMarketDataTicker(TickerStructV4[] tickerStructsV4)                                                                    throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataTicker(TickerStructV4[] tickerStructsV4, int offset, int length)                                            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataTicker(TickerStructV4 tickerStructV4)                                                                       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

    public void acceptMarketDataRecap(RecapContainerV4IF[] recapContainersV4)                                                               throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataRecap(RecapContainerV4IF[] recapContainersV4, int offset, int length)                                       throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;
    public void acceptMarketDataRecap(RecapContainerV4IF recapContainerV4)                                                                  throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException, AlreadyExistsException;

}

