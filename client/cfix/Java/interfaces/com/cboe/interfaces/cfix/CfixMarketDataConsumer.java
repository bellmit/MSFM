package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataConsumer.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.exceptions.*;

public interface CfixMarketDataConsumer
{
    public void acceptMarketDataCurrentMarket(OverlayPolicyMarketDataListIF        cfixOverlayPolicyMarketDataList) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void acceptMarketDataBookDepth(OverlayPolicyMarketDataListIF            cfixOverlayPolicyMarketDataList) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void acceptMarketDataBookDepthUpdate(OverlayPolicyMarketDataListIF      cfixOverlayPolicyMarketDataList) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void acceptMarketDataExpectedOpeningPrice(OverlayPolicyMarketDataListIF cfixOverlayPolicyMarketDataList) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void acceptMarketDataNbbo(OverlayPolicyMarketDataListIF                 cfixOverlayPolicyMarketDataList) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void acceptMarketDataRecap(OverlayPolicyMarketDataListIF                cfixOverlayPolicyMarketDataList) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void acceptMarketDataTicker(OverlayPolicyMarketDataListIF               cfixOverlayPolicyMarketDataList) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public void acceptMarketDataReject(CfixMarketDataRejectStruct                  cfixFixMarketDataRejectStruct)   throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public boolean isAcceptingMarketData()                                                                          throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
}
