package com.cboe.application.systemHealth;

import com.cboe.interfaces.application.SystemHealthQueryProcessor;
import com.cboe.interfaces.application.ProductQueryServiceAdapter;
import com.cboe.interfaces.application.TradingSessionServiceAdapter;
import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.bind.*;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.exceptions.*;

class ProductQueryProcessorImpl implements SystemHealthQueryProcessor
{
    public static final String GET_CLASS_BY_SESSION_FOR_KEY = GIPQSMethodName.GET_CLASS_BY_SESSION_FOR_KEY.toString();
    public static final String GET_CLASSES_FOR_SESSION = GIPQSMethodName.GET_CLASSES_FOR_SESSION.toString();
    public static final String GET_CURRENT_TRADING_SESSIONS = GIPQSMethodName.GET_CURRENT_TRADING_SESSIONS.toString();
    public static final String GET_PRODUCT_BY_SESSION_FOR_KEY = GIPQSMethodName.GET_PRODUCT_BY_SESSION_FOR_KEY.toString();
    public static final String GET_PRODUCT_TYPES_FOR_SESSION = GIPQSMethodName.GET_PRODUCT_TYPES_FOR_SESSION.toString();
    public static final String GET_CLASS_BY_KEY = GIPQSMethodName.GET_CLASS_BY_KEY.toString();
    public static final String GET_PRODUCT_BY_KEY = GIPQSMethodName.GET_PRODUCT_BY_KEY.toString();
    public static final String GET_PRODUCT_CLASSES = GIPQSMethodName.GET_PRODUCT_CLASSES.toString();
    public static final String GET_PRODUCT_TYPES = GIPQSMethodName.GET_PRODUCT_TYPES.toString();
    public static final String GET_PRODUCTS_BY_CLASS = GIPQSMethodName.GET_PRODUCTS_BY_CLASS.toString();
    public static final String GET_STRATEGIES_FOR_SESSION_BY_CLASS = GIPQSMethodName.GET_STRATEGIES_FOR_SESSION_BY_CLASS.toString();
    public static final String GET_STRATEGIES_BY_CLASS = GIPQSMethodName.GET_STRATEGIES_BY_CLASS.toString();
    public static final String GET_STRATEGY_BY_KEY = GIPQSMethodName.GET_STRATEGY_BY_KEY.toString();

    private static ProductQueryServiceAdapter pqsAdapter;
    private static TradingSessionServiceAdapter tssAdapter;

    private String xmlRequest;

    public static void initialize()
    {
        getProductQueryServiceAdapter();
        getTradingSessionServiceAdapter();

        boolean failed = false;
        StringBuilder errorMessage = new StringBuilder("The following dependencies could not be instantiated: ");

        if(pqsAdapter == null)
        {
            failed = true;
            errorMessage.append("ProductQueryServiceAdapter ");
        }

        if(tssAdapter == null)
        {
            failed = true;
            errorMessage.append("TradingSessionServiceAdapter ");
        }

        if(failed)
        {
            Log.alarm("ProductQueryProcessorImpl -> " + errorMessage);
        }
    }

    ProductQueryProcessorImpl(String xmlRequest)
    {
        this.xmlRequest = xmlRequest;
    }

    public String processRequest()
    {
        String result = null;
        String invokedMethod = null;
        try
        {
            if(xmlRequest == null || xmlRequest.equals(EMPTY_STRING))
            {
                throw new IllegalArgumentException("Request string cannot be empty; must be XML.");
            }

            GIProductQueryOperationsRequestType pqor = XmlBindingFacade.getInstance().getGIProductQueryOperationsRequestType(xmlRequest);
            GIProductQueryRequestType pqr = pqor.getProductQueryRequest();
            invokedMethod = pqr.getMethodName().toString();
            if(invokedMethod.equals(GET_CLASS_BY_SESSION_FOR_KEY))
            {
                GISessionClassKeyRequestType request = (GISessionClassKeyRequestType) pqr;
                result = getClassBySessionForKey(request);
            }
            else if(invokedMethod.equals(GET_CLASSES_FOR_SESSION))
            {
                GISessionProductTypeRequestType request = (GISessionProductTypeRequestType) pqr;
                result = getClassesForSession(request);
            }
            else if(invokedMethod.equals(GET_CURRENT_TRADING_SESSIONS))
            {
                result = getCurrentTradingSessions();
            }
            else if(invokedMethod.equals(GET_PRODUCT_BY_SESSION_FOR_KEY))
            {
                GISessionProductKeyRequestType request = (GISessionProductKeyRequestType) pqr;
                result = getProductBySessionForKey(request);
            }
            else if(invokedMethod.equals(GET_PRODUCT_TYPES_FOR_SESSION))
            {
                GISessionRequestType request = (GISessionRequestType) pqr;
                result = getProductTypesForSession(request);
            }
            else if(invokedMethod.equals(GET_CLASS_BY_KEY))
            {
                GIClassKeyRequestType request = (GIClassKeyRequestType) pqr;
                result = getClassByKey(request);
            }
            else if(invokedMethod.equals(GET_PRODUCT_BY_KEY))
            {
                GIProductKeyRequestType request = (GIProductKeyRequestType) pqr;
                result = getProductByKey(request);
            }
            else if(invokedMethod.equals(GET_PRODUCT_CLASSES))
            {
                GIProductTypeRequestType request = (GIProductTypeRequestType) pqr;
                result = getProductClasses(request);
            }
            else if(invokedMethod.equals(GET_PRODUCT_TYPES))
            {
                result = getProductTypes();
            }
            else if(invokedMethod.equals(GET_PRODUCTS_BY_CLASS))
            {
                GIClassKeyRequestType request = (GIClassKeyRequestType) pqr;
                result = getProductsByClass(request);
            }
            else if(invokedMethod.equals(GET_STRATEGIES_FOR_SESSION_BY_CLASS))
            {
                GISessionClassKeyRequestType request = (GISessionClassKeyRequestType) pqr;
                result = getStrategiesForSessionByClass(request);
            }
            else if(invokedMethod.equals(GET_STRATEGIES_BY_CLASS))
            {
                GIClassKeyRequestType request = (GIClassKeyRequestType) pqr;
                result = getStrategiesByClass(request);
            }
            else if(invokedMethod.equals(GET_STRATEGY_BY_KEY))
            {
                GIProductKeyRequestType request = (GIProductKeyRequestType) pqr;
                result = getStrategyByKey(request);
            }
            else // invalid request
            {
                throw new IllegalArgumentException("Invalid request: " + invokedMethod);
            }
        }
        catch(Exception e)
        {
            result = SystemHealthXMLHelper.logAndConvertException(xmlRequest, invokedMethod, e);
        }

        return result;
    }

    private String getClassBySessionForKey(GISessionClassKeyRequestType request)
        throws CommunicationException, DataValidationException, NotFoundException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_CLASS_BY_SESSION_FOR_KEY);
        String sessionName = request.getSessionName();
        int classKey = request.getClassKey();
        SessionClassStruct[] queryResult = new SessionClassStruct[1];
        queryResult[0] = getTradingSessionServiceAdapter().getClassBySessionForKey(sessionName, classKey);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getClassesForSession(GISessionProductTypeRequestType request)
        throws CommunicationException, DataValidationException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_CLASSES_FOR_SESSION);
        String sessionName = request.getSessionName();
        short productType = request.getProductType();
        SessionClassStruct[] queryResult = getTradingSessionServiceAdapter().getClassesForSession(sessionName, productType);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getCurrentTradingSessions()
        throws CommunicationException, DataValidationException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_CURRENT_TRADING_SESSIONS);
        TradingSessionStruct[] tradingSessions = getTradingSessionServiceAdapter().getCurrentTradingSessions();
        return SystemHealthProductQueryXMLHelper.convertToXml(tradingSessions);
    }

    private String getProductBySessionForKey(GISessionProductKeyRequestType request)
        throws CommunicationException, DataValidationException, NotFoundException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_PRODUCT_BY_SESSION_FOR_KEY);
        String sessionName = request.getSessionName();
        int productKey = request.getProductKey();
        SessionProductStruct[] queryResult = new SessionProductStruct[1];
        queryResult[0] = getTradingSessionServiceAdapter().getProductBySessionForKey(sessionName, productKey);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getProductTypesForSession(GISessionRequestType request)
        throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
        Log.information("Processing request: " + GET_PRODUCT_BY_SESSION_FOR_KEY);
        String sessionName = request.getSessionName();
        ProductTypeStruct[] queryResult = getTradingSessionServiceAdapter().getProductTypesForSession(sessionName);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getClassByKey(GIClassKeyRequestType request)
        throws CommunicationException, DataValidationException, NotFoundException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_CLASS_BY_KEY);
        int classKey = request.getClassKey();
        ClassStruct[] queryResult = new ClassStruct[1];
        queryResult[0] = getProductQueryServiceAdapter().getClassByKey(classKey);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);

    }

    private String getProductByKey(GIProductKeyRequestType request)
        throws CommunicationException, DataValidationException, NotFoundException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_PRODUCT_BY_KEY);
        int productKey = request.getProductKey();
        ProductStruct[] queryResult = new ProductStruct[1];
        queryResult[0] = getProductQueryServiceAdapter().getProductByKey(productKey);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getProductClasses(GIProductTypeRequestType request)
        throws CommunicationException, DataValidationException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_PRODUCT_CLASSES);
        short productType = request.getProductType();
        ClassStruct[] queryResult = getProductQueryServiceAdapter().getProductClasses(productType);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getProductTypes()
        throws SystemException, CommunicationException, AuthorizationException
    {
        Log.information("Processing request: " + GET_PRODUCT_TYPES);
        ProductTypeStruct[] queryResult = getProductQueryServiceAdapter().getProductTypes();
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getProductsByClass(GIClassKeyRequestType request)
            throws CommunicationException, DataValidationException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_PRODUCTS_BY_CLASS);
        int classKey = request.getClassKey();
        ProductStruct[] queryResult = getProductQueryServiceAdapter().getProductsByClass(classKey);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getStrategiesForSessionByClass(GISessionClassKeyRequestType request)
            throws CommunicationException, DataValidationException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_STRATEGIES_FOR_SESSION_BY_CLASS);
        String sessionName = request.getSessionName();
        int classKey = request.getClassKey();
        SessionStrategyStruct[] queryResult = getTradingSessionServiceAdapter().getStrategiesByClassForSession(sessionName, classKey);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    private String getStrategiesByClass(GIClassKeyRequestType request)
            throws CommunicationException, DataValidationException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_STRATEGIES_BY_CLASS);
        int classKey = request.getClassKey();
        StrategyStruct[] queryResult = getProductQueryServiceAdapter().getStrategiesByClass(classKey);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    static String getStrategyByKey(GIProductKeyRequestType request)
        throws CommunicationException, DataValidationException, NotFoundException, AuthorizationException, SystemException
    {
        Log.information("Processing request: " + GET_STRATEGY_BY_KEY);
        int strategyKey = request.getProductKey();
        StrategyStruct[] queryResult = new StrategyStruct[1];
        queryResult[0] = getProductQueryServiceAdapter().getStrategyByKey(strategyKey);
        return SystemHealthProductQueryXMLHelper.convertToXml(queryResult);
    }

    static ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqsAdapter == null)
        {
            pqsAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqsAdapter;
    }

    static TradingSessionServiceAdapter getTradingSessionServiceAdapter()
    {
        if(tssAdapter == null)
        {
            tssAdapter = ServicesHelper.getTradingSessionServiceAdapter();
        }
        return tssAdapter;
    }
}