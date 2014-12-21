package com.cboe.interfaces.application.inprocess;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.interfaces.callback.*;

/**
 * @author Jing Chen
 */
public interface MarketQuery
{
    public BookDepthStruct getBookDepth(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException, NotAcceptedException;
    public CurrentMarketStruct getCurrentMarketForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException, NotAcceptedException;
    public RecapStruct getRecapForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException, NotAcceptedException;

    public void subscribeBookDepthForClass(SessionClassStruct sessionClass, OrderBookV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeBookDepthForClass(SessionClassStruct sessionClass, OrderBookV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeCurrentMarketForClass(SessionClassStruct sessionClass, CurrentMarketV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeCurrentMarketForClass(SessionClassStruct sessionClass, CurrentMarketV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeExpectedOpeningPriceForClass(SessionClassStruct sessionClass,
                                                      ExpectedOpeningPriceV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeExpectedOpeningPriceForClass(SessionClassStruct sessionClass,
                                                        ExpectedOpeningPriceV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeNBBOForClass(SessionClassStruct sessionClass, NBBOV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeNBBOForClass(SessionClassStruct sessionClass, NBBOV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeRecapForClass(SessionClassStruct sessionClass, RecapV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeRecapForClass(SessionClassStruct sessionClass, RecapV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

    public void subscribeTickerForClass(SessionClassStruct sessionClass, TickerV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeTickerForClass(SessionClassStruct sessionClass, TickerV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
