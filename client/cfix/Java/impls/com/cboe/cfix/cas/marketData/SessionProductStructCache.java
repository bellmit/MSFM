package com.cboe.cfix.cas.marketData;

/**
 * SessionProductStructCache.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.application.shared.ServicesHelper;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.interfaces.application.TradingSessionServiceAdapter;

public class SessionProductStructCache
{
    protected static TradingSessionServiceAdapter tradingSessionServiceAdapter;
    protected static TradingSessionServiceAdapter getTradingSessionService()
    {
        if(tradingSessionServiceAdapter == null)
        {
            tradingSessionServiceAdapter = ServicesHelper.getTradingSessionServiceAdapter();
        }
        return tradingSessionServiceAdapter;
    }

    public static SessionProductStruct[] getSessionProductStructFromClassKey(String sessionName, ClassStruct classStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getTradingSessionService().getProductsForSession(sessionName, classStruct.classKey);
    }

    public static SessionProductStruct[] getSessionProductStructFromClassKey(String sessionName, ProductStruct productStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getTradingSessionService().getProductsForSession(sessionName, productStruct.productKeys.classKey);
    }

    public static SessionProductStruct[] getSessionProductStructs(SessionClassStruct sessionClassStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getTradingSessionService().getProductsForSession(sessionClassStruct.sessionName, sessionClassStruct.classStruct.classKey);
    }

    public static SessionProductStruct[] getSessionProductStructs(SessionProductStruct sessionProductStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getTradingSessionService().getProductsForSession(sessionProductStruct.sessionName, sessionProductStruct.productStruct.productKeys.classKey);
    }

    public static SessionProductStruct[] getSessionProductStructsFromClassKey(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getTradingSessionService().getProductsForSession(sessionName, classKey);
    }

    public static SessionProductStruct getSessionProductStruct(String sessionName, ProductKeysStruct productKeyStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getTradingSessionService().getProductBySessionForKey(sessionName, productKeyStruct.productKey);
    }

    public static SessionProductStruct getSessionProductStruct(String sessionName, int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getTradingSessionService().getProductBySessionForKey(sessionName, productKey);
    }

    public static SessionClassStruct getClassBySymbol(String sessionName, int marketDataType, String symbol) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getTradingSessionService().getClassBySessionForSymbol(sessionName, (short) marketDataType, symbol);
    }

    public static SessionProductStruct getProductByName(String session, ProductNameStruct productNameStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        return getTradingSessionService().getProductBySessionForName(session, productNameStruct);
    }
}
