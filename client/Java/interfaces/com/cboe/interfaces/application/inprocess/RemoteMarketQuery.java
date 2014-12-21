package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiCallbackV2.*;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.CommunicationException;

public interface RemoteMarketQuery
{
    void subscribeCurrentMarketForClassV2(String sessionName, int classKey, CMICurrentMarketConsumer cmiCurrentMarketConsumer, short actionOnQueue)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void unsubscribeCurrentMarketForClassV2(String sessionName, int classKey, CMICurrentMarketConsumer cmiCurrentMarketConsumer)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void subscribeRecapForClassV2(String sessionName, int classKey, CMIRecapConsumer cmiRecapConsumer, short actionOnQueue)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void unsubscribeRecapForClassV2(String sessionName, int classKey, CMIRecapConsumer cmiRecapConsumer)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void subscribeNBBOForClassV2(String sessionName, int classKey, CMINBBOConsumer cminbboConsumer, short actionOnQueue)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void unsubscribeNBBOForClassV2(String sessionName, int classKey, CMINBBOConsumer cminbboConsumer)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void subscribeTickerForClassV2(String sessionName, int classKey, CMITickerConsumer cmiTickerConsumer, short actionOnQueue)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void unsubscribeTickerForClassV2(String sessionName, int classKey, CMITickerConsumer cmiTickerConsumer)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void subscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer, short actionOnQueue)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void unsubscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void subscribeBookDepthForClassV2(String sessionName, int classKey, CMIOrderBookConsumer cmiOrderBookConsumer, short actionOnQueue)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
    void unsubscribeBookDepthForClassV2(String sessionName, int classKey, CMIOrderBookConsumer cmiOrderBookConsumer)
            throws SystemException, AuthorizationException, DataValidationException, CommunicationException;
}
