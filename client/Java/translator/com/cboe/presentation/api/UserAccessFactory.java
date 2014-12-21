//
// -----------------------------------------------------------------------------------
// Source file: UserAccessFactory.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import org.omg.CORBA.SetOverrideType;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.Policy;
import org.omg.CORBA.UserException;
import com.cboe.ORBInfra.ORB.OrbAux;

import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.consumers.callback.UserSessionAdminConsumerFactory;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmi.UserSessionManager;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiV9.UserAccessV9;
import com.cboe.idl.cmiV9.UserAccessV9Helper;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.idl.floorApplication.FloorSessionManager;
import com.cboe.idl.floorApplication.ManualReportingService;
import com.cboe.idl.floorApplication.ManualReportingServiceHelper;
import com.cboe.idl.omt.OMTSessionManager;
import com.cboe.idl.omt.OrderManagementService;
import com.cboe.idl.pcqs.PCQSSessionManager;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketAPI;
import com.cboe.interfaces.presentation.api.ManualReportingAPI;
import com.cboe.interfaces.presentation.api.MarketMakerAPI;
import com.cboe.interfaces.presentation.api.OrderManagementTerminalAPI;
import com.cboe.interfaces.presentation.api.TraderAPI;
import com.cboe.interfaces.presentation.productConfiguration.ProductConfigurationQueryAPI;
import com.cboe.interfaces.presentation.user.Role;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.omt.OMTHelper;
import com.cboe.presentation.user.RoleFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
public class UserAccessFactory
{
    public static final transient String CAS_LOCATION_GUI_SYSTEM_PROP = "CAS_LOCATION_GUI_SYSTEM_PROP";
    public static final transient String ALT_CAS_LOCATION_GUI_SYSTEM_PROP = "ALT_CAS_LOCATION_GUI_SYSTEM_PROP";

    private static final String PROPERTIES_SECTION_NAME = "Timers";
    private static final int DEFAULT_LOGIN_CORBA_TIMEOUT_MILLIS = 20 * 1000;       // in milliseconds
    private static final String LOGIN_CORBA_TIMEOUT_MILLIS_KEY = "LoginCorbaTimeoutMillis";

    static private UserAccessV9 userAccessV9 = null;


    /**
    * SBTAccessFactory constructor comment.
    */
    public UserAccessFactory()
    {
        super();
    }

    /**
    * This method was created in VisualAge.
    */
    public static UserAccessV9 create()
    {
        // first null the reference to force creation. needed for login to alternate cas to work after primary times out
        reset();
        return find();
    }

    public static void reset()
    {
        userAccessV9 = null;
    }

    public static UserAccessV9 find()
    {
        if (userAccessV9 == null)
        {
            try
            {
                org.omg.CORBA.ORB orb = com.cboe.ORBInfra.ORB.Orb.init();   // a CBOE ORB Reference
                org.omg.CORBA.Any any = orb.create_any();                   // create a CORBA Any
                int timeoutValue = getLoginPrimaryCasWaitTime();            // Get a timeout value
                GUILoggerHome.find().information("UserAccessFactory.find: timeoutValue=" + timeoutValue,
                                                 GUILoggerBusinessProperty.USER_SESSION);
                Policy myPolicies[] = new Policy[1];                        // create a CORBA Policy array
                any.insert_long( timeoutValue );                            // stuff the timeout value into the Any
                myPolicies[0] = OrbAux.create_policy( org.omg.Messaging.RELATIVE_RT_TIMEOUT_POLICY_TYPE.value, any ); // set the Policy

            	Object obj = RemoteConnectionFactory.find().find_initial_V9_object();

                obj = OrbAux.set_policy_overides( myPolicies, SetOverrideType.SET_OVERRIDE, (org.omg.CORBA.Object) obj ); // apply the policy to the CORBA Object

                userAccessV9 = UserAccessV9Helper.narrow((org.omg.CORBA.Object) obj);
            }
            catch (OBJECT_NOT_EXIST e)
            {
                GUILoggerHome.find().information("UserAccessFactory.find: re-throwing OBJECT_NOT_EXIST e==" + e.getMessage(),
                                                 GUILoggerBusinessProperty.USER_SESSION);
                // this indicates that the CAS is not available... propagate it
                // so that the GUI can display an appropriate message
                throw e;
            }
            catch (Throwable e)
            {
                GUILoggerHome.find().exception("com.cboe.presentation.api.UserAccessFactory.find()",
                                               "UserAccessV9 remote object connection exception", e);
            }
        }
        return userAccessV9;
    }

    
    /**
     * Hold the reference of the current user session manager.
     */
    private static volatile UserSessionManagerV9 userSession;

    /**
     * Get the UserSessionManager for the current logon.
     * This method will return null if the session wasn't initialized.
     * This call merely returns a reference to the object.
     * 
     * @return the userSessionManager for the current login.
     */
    public final static UserSessionManager getUserSessionManager(){
    	return userSession;
    }
    
    public static TraderAPI traderLogon(UserLogonStruct logonStruct, short sessionType, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, AuthenticationException, NotFoundException
    {
        if ( clientListener == null )
        {
            return null;
        }
        else
        {
            EventChannelAdapterFactory.find().setDynamicChannels(true);
            registerClientListener(clientListener);

            CMIUserSessionAdmin userSessionAdminConsumer = UserSessionAdminConsumerFactory.create(EventChannelAdapterFactory.find());

            userAccessV9 = create();
            if(userAccessV9 !=null)
            {
                boolean gmdTextMessaging = true;
                if (sessionType == LoginSessionTypes.SECONDARY) {
                    gmdTextMessaging = false;
                }
                userSession = userAccessV9.logon(logonStruct, sessionType, userSessionAdminConsumer, gmdTextMessaging);
                try
                {
                    TraderAPI theTrader = TraderAPIFactory.create(userSession, userSessionAdminConsumer, clientListener, gmdTextMessaging);
                    boolean allowIntermarketApiAccess = new Boolean(System.getProperty(IntermarketAPI.ALLOW_INTERMARKET_ACCESS_PROPERTY_NAME)).booleanValue();
                    if (allowIntermarketApiAccess)
                    {
                        IntermarketAPIHome.setUserSessionManager(userSession);
                    }
                    boolean allowPCQSAPIAccess = new Boolean(System.getProperty(ProductConfigurationQueryAPI.ALLOW_PCQS_ACCESS_PROPERTY_NAME)).booleanValue();
                    if(allowPCQSAPIAccess)
                    {
                        initializePCQS(userSession);
                    }

                    initializeOMT(userSession);     // ignore returned value

                    return theTrader;
                }
                catch(OBJECT_NOT_EXIST e)
                {
                    GUILoggerHome.find().information("UserAccessFactory.traderLogon: re-throwing OBJECT_NOT_EXIST e==" + e.getMessage(),
                                                     GUILoggerBusinessProperty.USER_SESSION);
                    throw e;
                }
                catch (Exception e)
                {
                    GUILoggerHome.find().information("UserAccessFactory.traderLogon: caught Exception e==" + e.getMessage(),
                                                     GUILoggerBusinessProperty.USER_SESSION);
                    if(userSession != null)
                    {
                        userSession.logout();
                    }
                    // GUILoggerHome.find().exception("Unexpected exception during translator initialization.", e);
                    throw ExceptionBuilder.systemException(e.toString(), 0);
                }
            }
            else
            {
                return null;
            }
        }
    }

    // this is now called only by unit test. UserSessionImpl now calls logon()
    public static MarketMakerAPI marketMakerLogon(UserLogonStruct logonStruct, short sessionType, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, AuthenticationException, NotFoundException
    {
        if ( clientListener == null )
        {
            return null;
        }
        else
        {
            EventChannelAdapterFactory.find().setDynamicChannels(true);
            registerClientListener(clientListener);

            CMIUserSessionAdmin userSessionAdminConsumer = UserSessionAdminConsumerFactory.create(EventChannelAdapterFactory.find());

            MarketMakerAPI marketMakerAPI = null;
            marketMakerAPI = marketMakerLogon(logonStruct, sessionType, userSessionAdminConsumer, clientListener);
            return marketMakerAPI;
        }
    }

    public static MarketMakerAPI marketMakerLogon(UserLogonStruct logonStruct, short sessionType,
                                                  CMIUserSessionAdmin userSessionAdminConsumer,
                                                  EventChannelListener clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, AuthenticationException, NotFoundException
    {
        boolean gmdTextMessaging = true;
        if (sessionType == LoginSessionTypes.SECONDARY) {
            gmdTextMessaging = false;
        }

        userAccessV9 = create();
        if(userAccessV9 != null)
        {
                userSession = userAccessV9.logon(logonStruct, sessionType, userSessionAdminConsumer, gmdTextMessaging);
                try
                {
                    MarketMakerAPI theMarketMaker = MarketMakerAPIFactory.create(userSession, userSessionAdminConsumer, clientListener, gmdTextMessaging);
                    boolean allowIntermarketApiAccess = new Boolean(System.getProperty(IntermarketAPI.ALLOW_INTERMARKET_ACCESS_PROPERTY_NAME)).booleanValue();
                    if(allowIntermarketApiAccess)
                    {
                        IntermarketAPIHome.setUserSessionManager(userSession);
                    }

                    boolean allowPCQSAPIAccess = new Boolean(System.getProperty(ProductConfigurationQueryAPI.ALLOW_PCQS_ACCESS_PROPERTY_NAME)).booleanValue();
                    if(allowPCQSAPIAccess)
                    {
                        initializePCQS(userSession);
                    }

                    initializeManualReportingService(userSession);

                boolean allowOMTAPIAccess = true;   // TODO
                if (allowOMTAPIAccess)
                {
                    initializeOMT(userSession);     // ignore returned value
                }

                    return theMarketMaker;
                }
                catch(OBJECT_NOT_EXIST e)
                {
                GUILoggerHome.find().information("UserAccessFactory.marketMakerLogon: re-throwing OBJECT_NOT_EXIST e=" + e.getMessage(),
                                                 GUILoggerBusinessProperty.USER_SESSION);
                    throw e;
                }
                catch (AuthorizationException e)
                {
                GUILoggerHome.find().information("UserAccessFactory.marketMakerLogon: is userSession null ? " +
                                                 (userSession == null) + ", re-throwing AuthorizationException e=" + e.getMessage(),
                                                 GUILoggerBusinessProperty.USER_SESSION);
                    if (userSession != null)
                    {
                        userSession.logout();
                    }
                    throw e;
                }
                catch (AuthenticationException e)
                {
                GUILoggerHome.find().information("UserAccessFactory.marketMakerLogon: is userSession null ? " +
                                                 (userSession == null) + ", re-throwing AuthenticationException e=" + e.getMessage(),
                                                 GUILoggerBusinessProperty.USER_SESSION);
                    if (userSession != null)
                    {
                        userSession.logout();
                    }
                    throw e;
                }
                catch (DataValidationException e)
                {
                GUILoggerHome.find().information("UserAccessFactory.marketMakerLogon: is userSession null ? " +
                                                 (userSession == null) + ", re-throwing DataValidationException e=" + e.getMessage(),
                                                 GUILoggerBusinessProperty.USER_SESSION);
                    if (userSession != null)
                    {
                        userSession.logout();
                    }
                    throw e;
                }
                catch (Exception e)
                {
                GUILoggerHome.find().information("UserAccessFactory.marketMakerLogon: is userSession null ? " +
                                                 (userSession == null) + ", re-throwing Exception e=" + e.getMessage(),
                                                  GUILoggerBusinessProperty.USER_SESSION);
                    if(userSession != null)
                    {
                        userSession.logout();
                    }
                    GUILoggerHome.find().exception("Unexpected exception during translator initialization.", e);
                    throw ExceptionBuilder.systemException(e.toString(), 0);
                }
            }
        return null;
    }

    /**
     * Gets the PCQSSessionManager from the CAS.  If that's successful, the ProductConfigurationQueryAPI is
     * initialized.  If any exception is thrown by the CAS, it is caught and the System Property
     * ProductConfigurationQueryAPI.ALLOW_PCQS_ACCESS_PROPERTY_NAME is set to false.
     */
    public static ProductConfigurationQueryAPI initializePCQS(UserSessionManager userSession)
    {
        ProductConfigurationQueryAPI pcqsAPI = null;
        try
        {
            PCQSSessionManager pcqsSessionManager = UserAccessPCQSFactory.getPCQSUserSessionManager(userSession);
            pcqsAPI = ProductConfigurationQueryAPIFactory.create(pcqsSessionManager.getProductConfigurationQueryService());
        }
        catch(OBJECT_NOT_EXIST e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Product Configuration Query Service (PCQS) session is not available from the CAS.");
            System.setProperty(ProductConfigurationQueryAPI.ALLOW_PCQS_ACCESS_PROPERTY_NAME, Boolean.toString(false));
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Exception trying to establish Product Configuration Query Service (PCQS) session");
            System.setProperty(ProductConfigurationQueryAPI.ALLOW_PCQS_ACCESS_PROPERTY_NAME, Boolean.toString(false));
        }
        return pcqsAPI;
    }

    private static ManualReportingAPI initializeManualReportingService(UserSessionManager userSession)
    {
        ManualReportingAPI mrAPI = null;
        try
        {
            FloorSessionManager sessionManager =
                    UserAccessFloorFactory.getUserSessionManager(userSession);

            if(sessionManager!= null)
            {
                ManualReportingService mrService =
                        ManualReportingServiceHelper.narrow(sessionManager.getService(ManualReportingServiceHelper.id()));

                mrAPI = ManualReportingAPIFactory.find();
                mrAPI.initializeService(mrService);
            }
        }
        catch(OBJECT_NOT_EXIST e)
        {
            DefaultExceptionHandlerHome.find()
                    .process(e, "ManualReportingService session is not available from the CAS.");
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find()
                    .process(e, "Exception trying to establish ManualReportingService session.");
        }
        return mrAPI;
    }

     private static OrderManagementTerminalAPI initializeOMT(UserSessionManager userSession)
    {
        OrderManagementTerminalAPI omtAPI = null;
        try
        {
            SessionProfileUserStruct userStruct = userSession.getValidSessionProfileUser();
            char structRole = userStruct.role;
            Role role = RoleFactory.getByChar(structRole);

            if(OMTHelper.isOMTRole(role))
            {
                OMTSessionManager omtSessionManager =
                    UserAccessOMTFactory.getOMTSUserSessionManager(userSession);
                OrderManagementService omtService = omtSessionManager.getOrderManagementService();
                omtAPI = OrderManagementTerminalAPIFactory.find();
                omtAPI.initializeService(omtService);
            }
        }
        catch(OBJECT_NOT_EXIST e)
        {
            DefaultExceptionHandlerHome.find()
                    .process(e, "Order Management Terminal Service session is not available from " +
                                "the CAS.");
            System.setProperty(OrderManagementTerminalAPI.ALLOW_OMT_ACCESS_PROPERTY_NAME,
                               Boolean.toString(false));
        }
        catch(UserException e)
        {
            DefaultExceptionHandlerHome.find()
                    .process(e, "Exception trying to establish Order Management Terminal Service " +
                                "session.");
            System.setProperty(OrderManagementTerminalAPI.ALLOW_OMT_ACCESS_PROPERTY_NAME,
                               Boolean.toString(false));
        }
        return omtAPI;
    }

    /**
     * Register the event channel listener for specific events
     * @usage can be used to subscribe for events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    protected static void registerClientListener(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            registerForLogoff(clientListener);
            registerForTextMessage(clientListener);
            registerForAuthenticate(clientListener);
            registerForHeartbeat(clientListener);
            registerForCallbackRemoval(clientListener);
        }
    }

    /**
     * Unregister the event channel listener for specific events
     * @usage can be used to unsubscribe for events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    protected static void unregisterClientListener(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            // remove the clientListener for ALL channels
            EventChannelAdapterFactory.find().removeChannelListener(clientListener);
        }
    }

    /**
     * Register the event channel listener for the logoff events
     * @usage can be used to subscribe for log off events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForLogoff(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        }
    }

    /**
     * Register the event channel listener for the text message events
     * @usage can be used to subscribe for log off events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForTextMessage(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_TEXT_MESSAGE, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        }
    }

    /**
     * Register the event channel listener for authenticate events
     * @usage can be used to subscribe for log off events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForAuthenticate(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_AUTHENTICATION_NOTICE, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        }
    }

    /**
     * Register the event channel listener for hearbeat events
     * @usage can be used to subscribe for hearbeat events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForHeartbeat(EventChannelListener clientListener)
    {
        if ( clientListener != null )
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_HEARTBEAT, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener,key);
        }
    }

    /**
     * Register the event channel listener for callback removal events
     * @usage can be used to subscribe for callback removal events
     * @param clientListener the listener to subscribe
     * @returns none
     */
    private static void registerForCallbackRemoval(EventChannelListener clientListener)
    {
        if(clientListener!= null)
        {
            ChannelKey key = new ChannelKey(ChannelType.CB_UNREGISTER_LISTENER, new Integer(0));
            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
        }
    }

    private static int getLoginPrimaryCasWaitTime()
    {
        int retValue = DEFAULT_LOGIN_CORBA_TIMEOUT_MILLIS;

        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String value = AppPropertiesFileFactory.find().getValue(PROPERTIES_SECTION_NAME, LOGIN_CORBA_TIMEOUT_MILLIS_KEY);

            try
            {
                retValue = Integer.parseInt(value.trim());
            }
            catch(Exception e)
            {
                GUILoggerHome.find().exception("UserAccessFactory.getLoginPrimaryCasWaitTime()",
                                               "Error parsing section=" + PROPERTIES_SECTION_NAME +
                                               " property=" + LOGIN_CORBA_TIMEOUT_MILLIS_KEY + " value=" + value, e);
            }
        }

        return retValue;
    }
}
