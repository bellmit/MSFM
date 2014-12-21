package com.cboe.interfaces.application;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;

/**
 * @author Jing Chen
 */
public interface UserTradingSessionService
{
    public SessionClassStruct getClassBySessionForKey(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionClassStruct getClassBySessionForSymbol(String sessionName, short productType, String symbol)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionClassStruct[] getClassesForSession(String sessionName, short productType)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public TradingSessionStruct[] getCurrentTradingSessions()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionProductStruct getProductBySessionForKey(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionProductStruct getProductBySessionForName(String sessionName, ProductNameStruct productNameStruct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public ProductTypeStruct[] getProductTypesForSession(String sessionName)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionProductStruct[] getProductsForSession(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionStrategyStruct[] getStrategiesByClassForSession(String sessionName, int classKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public SessionStrategyStruct getStrategyBySessionForKey(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException;
    public SessionStrategyStruct[] getStrategiesByComponent(int componentKey, String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void publishMessagesForProductType(short productType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void publishMessagesForProductClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserEnablement(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
    public void verifyUserEnablementForProduct(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException;
}
