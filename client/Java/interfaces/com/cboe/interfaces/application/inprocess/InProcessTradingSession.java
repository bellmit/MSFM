package com.cboe.interfaces.application.inprocess;

import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.exceptions.*;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.interfaces.callback.ClassStatusConsumer;
import com.cboe.interfaces.callback.TradingSessionStatusConsumer;
import com.cboe.interfaces.callback.StrategyStatusConsumer;
import com.cboe.interfaces.callback.ProductStatusConsumer;

/**
 * @author Jing Chen
 */

public interface InProcessTradingSession
{
    public SessionClassStruct getClassBySessionForKey(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionClassStruct getClassBySessionForSymbol(String sessionName, short productType, String symbol)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionClassStruct[] getClassesForSession(String sessionName, short productType,ClassStatusConsumer ClassStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public TradingSessionStruct[] getCurrentTradingSessions(TradingSessionStatusConsumer tradingSessionStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionProductStruct getProductBySessionForKey(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionProductStruct getProductBySessionForName(String sessionName, ProductNameStruct productNameStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public ProductTypeStruct[] getProductTypesForSession(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionProductStruct[] getProductsForSession(String sessionName, int classKey, ProductStatusConsumer productStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionStrategyStruct[] getStrategiesByClassForSession(String sessionName, int classKey, StrategyStatusConsumer strategyStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionStrategyStruct getStrategyBySessionForKey(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionProductStruct[] subscribeProductForSession(String sessionName, int classKey, int productKey, ProductStatusConsumer productStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeProductForSession(String sessionName, int classKey, int productKey, ProductStatusConsumer productStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeClassesByTypeForSession(String sessionName, short productType, ClassStatusConsumer classStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeProductsByClassForSession(String sessionName, int classKey, ProductStatusConsumer productStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeStrategiesByClassForSession(String sessionName, int classKey, StrategyStatusConsumer strategyStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void unsubscribeTradingSessionStatus(TradingSessionStatusConsumer tradingSessionStatusConsumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
