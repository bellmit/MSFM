// -----------------------------------------------------------------------------------
// Source file: AdminServiceTradingSessionImpl.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.idl.cmi.TradingSessionOperations;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiCallback.CMIClassStatusConsumer;
import com.cboe.idl.cmiCallback.CMITradingSessionStatusConsumer;
import com.cboe.idl.cmiCallback.CMIProductStatusConsumer;
import com.cboe.idl.cmiCallback.CMIStrategyStatusConsumer;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.exceptions.*;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.systemsManagementService.asynchronousClient.AdminServiceClientAsync;
import com.cboe.client.xml.XmlBindingFacade;
import com.cboe.client.xml.XmlProductConversionFacade;
import com.cboe.client.xml.bind.*;
import com.cboe.util.ExceptionBuilder;
import com.cboe.interfaces.presentation.api.TimedOutException;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;

public class AdminServiceTradingSessionImpl implements TradingSessionOperations
{
    private AdminServiceClientAsync adminService;

    public AdminServiceTradingSessionImpl(AdminServiceClientAsync adminService)
    {
        super();
        this.adminService = adminService;
        if ( adminService == null )
        {
            throw new IllegalArgumentException("AdminService can not be null");
        }
    }

    // TradingSessionOperations
    public SessionClassStruct getClassBySessionForKey(String sessionName, int classKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createClassBySessionForKeyRequest(sessionName, classKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                SessionClassStruct[] sessionClassStructs = XmlProductConversionFacade.getInstance().getSessionClassStructs((GIProductQueryOperationsType)unmarshalledObject);
                if(sessionClassStructs.length > 0) // return the first one found (should have been only one anyway).
                {
                    return sessionClassStructs[0];
                }
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        return null;
    }

    public SessionClassStruct getClassBySessionForSymbol(String s, short i, String s1) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        throw getUnsupportedException("getClassBySessionForSymbol(String, short, String):SessionClassStruct");
    }

    public SessionClassStruct[] getClassesForSession(String sessionName, short productType, CMIClassStatusConsumer cmiClassStatusConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createClassesForSessionRequest(sessionName, productType);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if(unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getSessionClassStructs((GIProductQueryOperationsType)unmarshalledObject);
            }
        }

        catch(SystemException e)
        {
            throw e;
        }
        catch(CommunicationException e)
        {
            throw e;
        }
        catch(AuthorizationException e)
        {
            throw e;
        }
        catch(DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }


        return new SessionClassStruct[0];
    }


    public TradingSessionStruct[] getCurrentTradingSessions(CMITradingSessionStatusConsumer cmiTradingSessionStatusConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createCurrentTradingSessionsRequest();
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if(unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getTradingSessionStructs((GIProductQueryOperationsType)unmarshalledObject);
            }
        }
        catch(SystemException e)
        {
            throw e;
        }
        catch(CommunicationException e)
        {
            throw e;
        }
        catch(AuthorizationException e)
        {
            throw e;
        }
        catch(DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }


        return new TradingSessionStruct[0];
    }

    public SessionProductStruct getProductBySessionForKey(String sessionName, int productKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createProductBySessionForKeyRequest(sessionName, productKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                SessionProductStruct[] sessionProductStructs = XmlProductConversionFacade.getInstance().getSessionProductStructs((GIProductQueryOperationsType)unmarshalledObject);
                if(sessionProductStructs.length > 0) // return the first one found (should have been only one anyway).
                {
                    return sessionProductStructs[0];
                }
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (NotFoundException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }

        return null;
    }

    public SessionProductStruct getProductBySessionForName(String s, ProductNameStruct productNameStruct) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        throw getUnsupportedException("getProductBySessionForName(String, ProductNameStruct):SessionProductStruct");
    }

    public ProductTypeStruct[] getProductTypesForSession(String sessionName) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createProductTypesForSessionRequest(sessionName);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if(unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getProductTypeStructs((GIProductQueryOperationsType)unmarshalledObject);
            }
        }
        catch(SystemException e)
        {
            throw e;
        }
        catch(CommunicationException e)
        {
            throw e;
        }
        catch(AuthorizationException e)
        {
            throw e;
        }
        catch(DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }


        return new ProductTypeStruct[0];
    }

    public SessionProductStruct[] getProductsForSession(String session, int classKey, CMIProductStatusConsumer cmiProductStatusConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createProductsForSessionRequest(session, classKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getSessionProductStructs((GIProductQueryOperationsType) unmarshalledObject);
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }

        return new SessionProductStruct[0];
    }

    public SessionStrategyStruct[] getStrategiesByClassForSession(String sessionName, int classKey, CMIStrategyStatusConsumer cmiStrategyStatusConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createStrategiesForSessionByClassRequest(sessionName, classKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                return XmlProductConversionFacade.getInstance().getSessionStrategyStructs((GIProductQueryOperationsType)unmarshalledObject);
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        return new SessionStrategyStruct[0];
    }

    public SessionStrategyStruct[] getStrategiesByComponent(int i, String s) throws SystemException, DataValidationException, AuthorizationException, CommunicationException
    {
        throw getUnsupportedException("getStrategiesByComponent(int, String):SessionStrategyStruct[]");
    }

    public SessionStrategyStruct getStrategyBySessionForKey(String sessionName, int strategyKey) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        try
        {
            String requestXml = XmlBindingFacade.getInstance().createStrategyBySessionForKeyRequest(sessionName, strategyKey);
            java.lang.Object unmarshalledObject = XmlBindingHelper.sendRequest(requestXml);
            if (unmarshalledObject instanceof GIProductQueryOperationsType)
            {
                SessionStrategyStruct[] sessionStrategyStructs = XmlProductConversionFacade.getInstance().getSessionStrategyStructs((GIProductQueryOperationsType) unmarshalledObject);
                if(sessionStrategyStructs.length > 0) // return the first one found (should have been only one anyway)
                {
                    return sessionStrategyStructs[0];
                }
            }
        }
        catch (SystemException e)
        {
            throw e;
        }
        catch (CommunicationException e)
        {
            throw e;
        }
        catch (AuthorizationException e)
        {
            throw e;
        }
        catch (DataValidationException e)
        {
            throw e;
        }
        catch (UserException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        catch (TimedOutException e)
        {
            SystemException se = ExceptionBuilder.systemException(e.getMessage(), 0);
            se.initCause(e);
            throw se;
        }
        return null;
    }

    public void unsubscribeClassesByTypeForSession(String s, short i, CMIClassStatusConsumer cmiClassStatusConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void unsubscribeProductsByClassForSession(String s, int i, CMIProductStatusConsumer cmiProductStatusConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void unsubscribeStrategiesByClassForSession(String s, int i, CMIStrategyStatusConsumer cmiStrategyStatusConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    public void unsubscribeTradingSessionStatus(CMITradingSessionStatusConsumer cmiTradingSessionStatusConsumer) throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
    }

    // CORBA.Object
    public boolean _is_a(String repositoryIdentifier)
    {
        return false;
    }

    public boolean _is_equivalent(Object other)
    {
        return false;
    }

    public boolean _non_existent()
    {
        return false;
    }

    public int _hash(int maximum)
    {
        return 0;
    }

    public Object _duplicate()
    {
        return null;
    }

    public void _release()
    {
    }

    public Object _get_interface_def()
    {
        return null;
    }

    public Request _request(String operation)
    {
        return null;
    }

    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result)
    {
        return null;
    }

    public Request _create_request(Context ctx,
                                   String operation,
                                   NVList arg_list,
                                   NamedValue result,
                                   ExceptionList exclist,
                                   ContextList ctxlist)
    {
        return null;
    }

    public Policy _get_policy(int policy_type)
    {
        return null;
    }

    public DomainManager[] _get_domain_managers()
    {
        return new DomainManager[0];
    }

    public Object _set_policy_override(Policy[] policies,
                                       SetOverrideType set_add)
    {
        return null;
    }

    private UnsupportedOperationException getUnsupportedException(String methodNotSupported)
    {
        return new UnsupportedOperationException("The method " + methodNotSupported + " is not supported by this API.");
    }

}
