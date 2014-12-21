package com.cboe.interfaces.application;

import com.cboe.exceptions.*;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.DateTimeStruct;

/**
 * @author Jing Chen
 */

public interface UserMarketDataService
{
    public BookDepthStruct getBookDepth(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException;
    public BookDepthStructV2 getBookDepthDetails(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException;
    public MarketDataHistoryDetailStruct getDetailMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public MarketDataHistoryDetailStruct getPriorityMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public MarketDataHistoryStruct getMarketDataHistoryByTime(String sessionName, int productKey, DateTimeStruct startTime, short direction)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public CurrentMarketStruct[] getCurrentMarketsForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public CurrentMarketStruct getCurrentMarketForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public NBBOStruct[] getNBBOsForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public NBBOStruct getNBBOForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public RecapStruct[] getRecapsForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public RecapStruct getRecapForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public BookDepthStruct[] getBookDepthsForClass(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public BookDepthStruct getBookDepthForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserCurrentMarketEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserNBBOEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserExpectdOpeningPriceEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserRecapEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserTickerEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserBookDepthEnablement(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
