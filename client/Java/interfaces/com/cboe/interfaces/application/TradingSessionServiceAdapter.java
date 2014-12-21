package com.cboe.interfaces.application;

import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.session.BusinessDayStruct;
import com.cboe.exceptions.*;

public interface TradingSessionServiceAdapter
{
    // TradingSession operations
    public SessionClassStruct getClassBySessionForKey(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionClassStruct getClassBySessionForSymbol(String sessionName, short productType, String className) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionClassStruct[] getClassesForSession(String sessionName, short productType) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public TradingSessionStruct[] getCurrentTradingSessions() throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionProductStruct getProductBySessionForKey(String sessionName, int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionProductStruct getProductBySessionForName(String sessionName, ProductNameStruct productName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public ProductTypeStruct[] getProductTypesForSession(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionProductStruct[] getProductsForSession(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionStrategyStruct[] getStrategiesByClassForSession(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionStrategyStruct[] getStrategiesByComponent(int componentKey, String sessionName) throws SystemException, DataValidationException, AuthorizationException, CommunicationException;
    public SessionStrategyStruct getStrategyBySessionForKey(String sessionName, int strategyKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    // Additional operations
    public boolean isSessionInitialized(String session);
    public void startDefaultTradingSessionEventFilters() throws AuthorizationException, CommunicationException, DataValidationException, SystemException;
    public void startAllSessions(BusinessDayStruct day);
    public void endAllSessions();

    /**
     * Returns true if product state change should be republished.
     * 
     * @param sessionName
     * @param classKey
     * @return
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @throws NotFoundException
     */ 
    public boolean refreshCachedSessionProductsForClassKey(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;

    public void publishMessagesForProductClass(int classKey, String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void publishMessagesForProductType(short productType, String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
