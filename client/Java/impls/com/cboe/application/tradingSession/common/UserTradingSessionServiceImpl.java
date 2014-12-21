package com.cboe.application.tradingSession.common;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.constants.OperationTypes;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.TradingSessionServiceAdapter;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserTradingSessionService;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import org.omg.CORBA.UserException;

/**
 * This class implements UserTradingSessionService which contains the common user tradingSession service that is shared
 * by cmi and non cmi users. It contains the userEnablement checking of each call and the calls to the server services.
 * @author Jing Chen
 */

public class UserTradingSessionServiceImpl extends BObject implements UserTradingSessionService
{
    protected BaseSessionManager sessionManager;
    protected String userId;
    protected String exchange;
    protected String acronym;
    protected TradingSessionServiceAdapter serviceAdapter;
    protected ProductQueryServiceAdapter productQueryServiceAdapter;
    protected UserEnablement userEnablement;

    public UserTradingSessionServiceImpl(BaseSessionManager sessionManager)
    {
        super();
        try
        {
            userId = sessionManager.getUserId();
            exchange = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.exchange;
            acronym = sessionManager.getValidSessionProfileUserV2().userInfo.userAcronym.acronym;
        }
        catch(UserException e)
        {
            Log.exception(e);
        }
        this.sessionManager = sessionManager;
        serviceAdapter = ServicesHelper.getTradingSessionServiceAdapter();
        productQueryServiceAdapter = ServicesHelper.getProductQueryServiceAdapter();
        userEnablement = ServicesHelper.getUserEnablementService(userId, exchange, acronym);
    }

    public void create(String name)
    {
        super.create(name);
    }

    /////////////// IDL exported methods ////////////////////////////////////

    public ProductTypeStruct[] getProductTypesForSession(String sessionName)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        return getServiceAdapter().getProductTypesForSession(sessionName);
    }

    public TradingSessionStruct[] getCurrentTradingSessions()
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
            Log.debug(this, "calling getCurrentTradingSessions for " + sessionManager);

        return getServiceAdapter().getCurrentTradingSessions();
    }

    public SessionClassStruct[] getClassesForSession(String sessionName, short productType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
            Log.debug(this, "calling getClassesForSession for " + sessionManager);

        return getServiceAdapter().getClassesForSession(sessionName, productType);
    }

    public SessionProductStruct[] getProductsForSession(String sessionName, int classKey)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
            Log.debug(this, "calling getProductsForSession for " + sessionManager + " where classKey="+classKey);

        verifyUserEnablement(sessionName, classKey);
        return getServiceAdapter().getProductsForSession(sessionName, classKey);
    }

    public SessionStrategyStruct[] getStrategiesByClassForSession(String sessionName, int classKey)
         throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
            Log.debug(this, "calling getStrategiesByClassForSession for " + sessionManager + " where classKey = " + classKey);

        verifyUserEnablement(sessionName, classKey);
        return getServiceAdapter().getStrategiesByClassForSession(sessionName, classKey);
    }

    public SessionStrategyStruct[] getStrategiesByComponent(int componentKey, String sessionName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(Log.isDebugOn())
            Log.debug(this, "calling getStrategiesByComponent for " + sessionManager + " sessionName=" + sessionName);

        verifyUserEnablementForProduct(sessionName, componentKey);
        return getServiceAdapter().getStrategiesByComponent(componentKey, sessionName);
    }

    public SessionClassStruct getClassBySessionForKey(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        verifyUserEnablement(sessionName, classKey);
        return getServiceAdapter().getClassBySessionForKey(sessionName, classKey);
    }

    public SessionProductStruct getProductBySessionForKey(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        verifyUserEnablementForProduct(sessionName, productKey);
        SessionProductStruct sessionProduct = getServiceAdapter().getProductBySessionForKey(sessionName, productKey);
        return sessionProduct;
    }

    public SessionStrategyStruct getStrategyBySessionForKey(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        verifyUserEnablementForProduct(sessionName, productKey);
        SessionStrategyStruct sessionStrategy = getServiceAdapter().getStrategyBySessionForKey(sessionName, productKey);
        return sessionStrategy;
    }

    public SessionClassStruct getClassBySessionForSymbol(String sessionName, short productType, String className)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionClassStruct sessionClass = getServiceAdapter().getClassBySessionForSymbol(sessionName, productType, className);
        verifyUserEnablement(sessionName, sessionClass.classStruct.classKey);
        return sessionClass;
    }

    public SessionProductStruct getProductBySessionForName(String sessionName, ProductNameStruct productName)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        SessionProductStruct sessionProduct = getServiceAdapter().getProductBySessionForName(sessionName, productName);
        verifyUserEnablement(sessionName, sessionProduct.productStruct.productKeys.classKey);
        return sessionProduct;
    }

    public void publishMessagesForProductType(short productType)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getServiceAdapter().publishMessagesForProductType(productType,userId);
    }

    public void publishMessagesForProductClass(int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        getServiceAdapter().publishMessagesForProductClass(classKey,userId);
    }

    protected TradingSessionServiceAdapter getServiceAdapter()
    {
        if(serviceAdapter == null)
        {
            serviceAdapter = ServicesHelper.getTradingSessionServiceAdapter();
        }
        return serviceAdapter;
    }

    public void verifyUserEnablement(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablement(sessionName, classKey, OperationTypes.TRADINGSESSION);
    }

    public void verifyUserEnablementForProduct(String sessionName, int productKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        userEnablement.verifyUserEnablementForProduct(sessionName, productKey, OperationTypes.TRADINGSESSION);
    }
}
