//
// -----------------------------------------------------------------------------------
// Source file: FIXUserAccessFactory.java
//
// PACKAGE: com.cboe.presentation.fix.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.fix.api;

import org.omg.CORBA.OBJECT_NOT_EXIST;

import com.cboe.consumers.callback.UserSessionAdminConsumerFactory;
import com.cboe.consumers.callback.fix.FIXUserSessionAdminConsumerFactory;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiV3.UserAccessV3;
import com.cboe.idl.cmiV3.UserSessionManagerV3;
import com.cboe.idl.cmiV9.UserAccessV9;
import com.cboe.idl.cmiV9.UserSessionManagerV9;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketAPI;
import com.cboe.interfaces.presentation.api.FIXMarketMakerAPI;
import com.cboe.presentation.api.IntermarketAPIHome;
import com.cboe.presentation.api.UserAccessFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.fix.session.UserAccessFixHome;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

public class FIXUserAccessFactory extends UserAccessFactory
{
    static private UserAccessV3 fixUserAccessV3 = null;

    public FIXUserAccessFactory()
    {
        super();
    }

    public static UserAccessV3 findFIXUserAccess()
    {
        if(fixUserAccessV3 == null)
        {
            try
            {
                // todo: ask for a factory with a static method instead of creating a UserAccessFixHome here
                fixUserAccessV3 = new UserAccessFixHome().find();
            }
            catch (OBJECT_NOT_EXIST e)
            {
                // this indicates that the CAS is not available... propagate it
                // so that the GUI can display an appropriate message
                throw e;
            }
            catch (Throwable e)
            {
                GUILoggerHome.find().exception("com.cboe.presentation.api.UserAccessV3Factory.find()", "UserAccessV3 remote object connection exception", e);
            }
        }
        return fixUserAccessV3;
    }

    /**
     *
     * @param logonStruct
     * @param sessionType
     * @param clientListener
     * @return interface M
     * @throws SystemException
     * @throws CommunicationException
     * @throws AuthorizationException
     * @throws DataValidationException
     * @throws AuthenticationException
     * @throws NotFoundException
     */
    public static FIXMarketMakerAPI marketMakerLogon(UserLogonStruct logonStruct, UserLogonStruct fixLogonStruct, short sessionType, EventChannelListener clientListener)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, AuthenticationException, NotFoundException
    {
        if ( clientListener == null )
        {
            return null;
        }
        else
        {
            UserSessionManagerV9 userSession;
            UserSessionManagerV3 fixUserSession;
            EventChannelAdapterFactory.find().setDynamicChannels(true);
            registerClientListener(clientListener);
            //todo
            registerFIXClientListener(clientListener);

            CMIUserSessionAdmin userSessionAdminConsumer = UserSessionAdminConsumerFactory.create(EventChannelAdapterFactory.find());
            CMIUserSessionAdmin fixUserSessionAdminConsumer = FIXUserSessionAdminConsumerFactory.create(EventChannelAdapterFactory.find());

            UserAccessV9 userAccessV9 = find();
            UserAccessV3 fixUserAccessV3 = findFIXUserAccess();

            if(userAccessV9 != null && fixUserAccessV3 != null)
            {
                boolean gmdTextMessaging = true;
                if (sessionType == LoginSessionTypes.SECONDARY) {
                    gmdTextMessaging = false;
                }
                userSession = userAccessV9.logon(logonStruct, sessionType, userSessionAdminConsumer, gmdTextMessaging);
                try
                {
                    fixUserSession = fixUserAccessV3.logon(fixLogonStruct, sessionType, fixUserSessionAdminConsumer, gmdTextMessaging);
                }
                // if CMi login succeeded, but FIX login failed, logout from CMi
                catch(SystemException e)
                {
                    userSession.logout();
                    throw(e);
                }
                catch(CommunicationException e)
                {
                    userSession.logout();
                    throw(e);
                }
                catch(AuthorizationException e)
                {
                    userSession.logout();
                    throw(e);
                }
                catch(AuthenticationException e)
                {
                    userSession.logout();
                    throw(e);
                }
                catch(NotFoundException e)
                {
                    userSession.logout();
                    throw(e);
                }

                try
                {
                    FIXMarketMakerAPI fixMarketMaker = FIXMarketMakerAPIFactory.create(userSession, fixUserSession, userSessionAdminConsumer, clientListener, gmdTextMessaging);
                    boolean allowIntermarketApiAccess = new Boolean(System.getProperty(IntermarketAPI.ALLOW_INTERMARKET_ACCESS_PROPERTY_NAME)).booleanValue();
                    if(allowIntermarketApiAccess)
                    {
                        IntermarketAPIHome.setUserSessionManager(userSession);
                    }
                    return fixMarketMaker;
                }
                catch(OBJECT_NOT_EXIST e)
                {
                    throw e;
                }
                catch (Exception e)
                {
                    if(userSession != null)
                    {
                        userSession.logout();
                    }
                    if(fixUserSession != null)
                    {
                        fixUserSession.logout();
                    }
                    throw ExceptionBuilder.systemException(e.toString(), 0);
                }
            }
            else
            {
                return null;
            }
        }
    }

    // todo...
    protected static void registerFIXClientListener(EventChannelListener clientListener)
    {
        if(clientListener != null)
        {
//            registerForFixLogoff(clientListener);
        }
    }

    protected static void unregisterFIXClientListener(EventChannelListener clientListener)
    {
        if(clientListener != null)
        {
            // todo
        }
    }

    // todo -- if the FIXTranslator publishes these events with the same ChannelType as the CMi messages, then these registrations won't be necessay
//    /**
//     * Register the event channel listener for the logoff events
//     * @usage can be used to subscribe for log off events
//     * @param clientListener the listener to subscribe
//     * @returns none
//     */
//    private static void registerForFIXLogoff(EventChannelListener clientListener)
//    {
//        if ( clientListener != null )
//        {
//            ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, new Integer(0));
//            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
//        }
//    }
//
//    /**
//     * Register the event channel listener for the text message events
//     * @usage can be used to subscribe for log off events
//     * @param clientListener the listener to subscribe
//     * @returns none
//     */
//    private static void registerForFIXTextMessage(EventChannelListener clientListener)
//    {
//        if ( clientListener != null )
//        {
//            ChannelKey key = new ChannelKey(ChannelType.CB_TEXT_MESSAGE, new Integer(0));
//            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
//        }
//    }
//
//    /**
//     * Register the event channel listener for authenticate events
//     * @usage can be used to subscribe for log off events
//     * @param clientListener the listener to subscribe
//     * @returns none
//     */
//    private static void registerForFIXAuthenticate(EventChannelListener clientListener)
//    {
//        if ( clientListener != null )
//        {
//            ChannelKey key = new ChannelKey(ChannelType.CB_AUTHENTICATION_NOTICE, new Integer(0));
//            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
//        }
//    }
//
//    /**
//     * Register the event channel listener for callback removal events
//     * @usage can be used to subscribe for callback removal events
//     * @param clientListener the listener to subscribe
//     * @returns none
//     */
//    private static void registerForFIXCallbackRemoval(EventChannelListener clientListener)
//    {
//        if(clientListener!= null)
//        {
//            ChannelKey key = new ChannelKey(ChannelType.CB_UNREGISTER_LISTENER, new Integer(0));
//            EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), clientListener, key);
//        }
//    }
}
