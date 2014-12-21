// $Workfile$ com.cboe.application.session.SessionManagerImpl.java
// $Revision$
// Last Modification on:  $Date$ $Modtime$// $Author$
/* $Log$
*   Initial Version         3/16/99      Derek T. Chambers-Boucher
*                           07/11/1999   Connie Feng
*/
//----------------------------------------------------------------------
// Copyright (c) 1999 CBOE. All Rights Reserved.

package com.cboe.application.session;

import static com.cboe.application.shared.LoggingUtil.SESSION_ID;
import static com.cboe.application.shared.LoggingUtil.SESSION_TYPE;
import static com.cboe.application.shared.LoggingUtil.TOKEN_DELIMITER;
import static com.cboe.application.shared.LoggingUtil.USER_ACRONYM;
import static com.cboe.application.shared.LoggingUtil.VALUE_DELIMITER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.omg.CORBA.UserException;

import com.cboe.application.heartBeatConsumer.HeartBeatConsumerFactory;
import com.cboe.application.order.OrderQueryCacheFactory;
import com.cboe.application.quote.QuoteCacheFactory;
import com.cboe.application.quote.common.QuoteSemaphoreHandler;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.AcceptTextMessageProcessor;
import com.cboe.application.shared.consumer.AcceptTextMessageProcessorFactory;
import com.cboe.application.shared.consumer.ForcedLogoutProcessor;
import com.cboe.application.shared.consumer.ForcedLogoutProcessorFactory;
import com.cboe.application.shared.consumer.HeartBeatProcessor;
import com.cboe.application.shared.consumer.HeartBeatProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.consumer.UserTimeoutWarningProcessor;
import com.cboe.application.shared.consumer.UserTimeoutWarningProcessorFactory;
import com.cboe.application.supplier.BookDepthSupplierFactory;
import com.cboe.application.supplier.BookDepthV2SupplierFactory;
import com.cboe.application.supplier.CurrentMarketSupplierFactory;
import com.cboe.application.supplier.CurrentMarketV2SupplierFactory;
import com.cboe.application.supplier.CurrentMarketV3SupplierFactory;
import com.cboe.application.supplier.ExpectedOpeningPriceSupplierFactory;
import com.cboe.application.supplier.ExpectedOpeningPriceV2SupplierFactory;
import com.cboe.application.supplier.LargeTradeLastSaleSupplierFactory;
import com.cboe.application.supplier.NBBOSupplierFactory;
import com.cboe.application.supplier.NBBOV2SupplierFactory;
import com.cboe.application.supplier.OrderRoutingSupplierFactory;
import com.cboe.application.supplier.OrderStatusSupplierFactory;
import com.cboe.application.supplier.OrderStatusV2SupplierFactory;
import com.cboe.application.supplier.QuoteStatusSupplierFactory;
import com.cboe.application.supplier.QuoteStatusV2SupplierFactory;
import com.cboe.application.supplier.RecapSupplierFactory;
import com.cboe.application.supplier.RecapV2SupplierFactory;
import com.cboe.application.supplier.TickerSupplierFactory;
import com.cboe.application.supplier.TickerV2SupplierFactory;
import com.cboe.application.supplier.UserSessionAdminSupplier;
import com.cboe.application.supplier.UserSessionAdminSupplierFactory;
import com.cboe.client.util.CollectionHelper;
import com.cboe.delegates.application.AdministratorDelegate;
import com.cboe.delegates.application.MarketQueryV3Delegate;
import com.cboe.delegates.application.OrderEntryV9Delegate;
import com.cboe.delegates.application.OrderQueryV6Delegate;
import com.cboe.delegates.application.ProductDefinitionDelegate;
import com.cboe.delegates.application.ProductQueryDelegate;
import com.cboe.delegates.application.QuoteV7Delegate;
import com.cboe.delegates.application.TradingSessionDelegate;
import com.cboe.delegates.application.UserHistoryDelegate;
import com.cboe.delegates.application.UserPreferenceQueryDelegate;
import com.cboe.delegates.application.UserSessionManagerV2Delegate;
import com.cboe.delegates.application.UserTradingParametersV5Delegate;
import com.cboe.domain.logout.LogoutQueue;
import com.cboe.domain.logout.LogoutQueueFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.startup.ClientRoutingBOHome;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.domain.util.SessionProfileHelper;
import com.cboe.domain.util.TimeServiceWrapper;
import com.cboe.domain.rateMonitor.RateMonitorHomeImpl;
import com.cboe.exceptions.AuthenticationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiAdmin.HeartBeatStruct;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiConstants.UserRoles;
import com.cboe.idl.cmiErrorCodes.AuthenticationCodes;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUser.SessionProfileUserStruct;
import com.cboe.idl.cmiUser.UserLogonStruct;
import com.cboe.idl.cmiUser.UserStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiV2.UserSessionManagerV2Helper;
import com.cboe.idl.cmiV6.OrderQuery;
import com.cboe.idl.constants.PropertyCategoryTypes;
import com.cboe.idl.floorApplication.ProductQueryV2Helper;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.infrastructureServices.sessionManagementService.UserCallbackDeregistrationModes;
import com.cboe.idl.order.OrderAcknowledgeStruct;
import com.cboe.idl.order.OrderAcknowledgeStructV3;
import com.cboe.idl.property.PropertyGroupStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStruct;
import com.cboe.idl.quote.QuoteAcknowledgeStructV3;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.user.UserEnablementStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.sessionManagementService.SessionManagementService;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.AggregatedMethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.interfaces.application.AcceptTextMessageCollector;
import com.cboe.interfaces.application.Administrator;
import com.cboe.interfaces.application.AdministratorHome;
import com.cboe.interfaces.application.ForcedLogoutCollector;
import com.cboe.interfaces.application.HeartBeatCollector;
import com.cboe.interfaces.application.MarketQueryHome;
import com.cboe.interfaces.application.MarketQueryV3;
import com.cboe.interfaces.application.OrderEntryV9;
import com.cboe.interfaces.application.OrderEntryV7;
import com.cboe.interfaces.application.OrderQueryV6;
import com.cboe.interfaces.application.ProductDefinition;
import com.cboe.interfaces.application.ProductDefinitionHome;
import com.cboe.interfaces.application.ProductQueryManager;
import com.cboe.interfaces.application.ProductQueryManagerHome;
import com.cboe.interfaces.application.QuoteV7;
import com.cboe.interfaces.application.RemoteCASCallbackRemovalCollector;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.TradingSession;
import com.cboe.interfaces.application.TradingSessionHome;
import com.cboe.interfaces.application.UserEnablement;
import com.cboe.interfaces.application.UserHistory;
import com.cboe.interfaces.application.UserHistoryHome;
import com.cboe.interfaces.application.UserOrderEntryHome;
import com.cboe.interfaces.application.UserOrderQueryHome;
import com.cboe.interfaces.application.UserPreferenceQuery;
import com.cboe.interfaces.application.UserPreferenceQueryHome;
import com.cboe.interfaces.application.UserQuoteHome;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.UserTimeoutWarningCollector;
import com.cboe.interfaces.application.UserTradingParametersHome;
import com.cboe.interfaces.application.UserTradingParametersV5;
import com.cboe.interfaces.callbackServices.HeartBeatCallbackService;
import com.cboe.interfaces.domain.LogoutMonitor;
import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;
import com.cboe.interfaces.internalBusinessServices.GroupService;
import com.cboe.interfaces.internalBusinessServices.GroupServiceHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * This is the implementation of the SessionManager interface that maintains all the
 * user specific API object connections for a client into the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @author Connie Feng
 * @author Mike Pyatetsky
 * @version 07/11/1999
 */

public class SessionManagerImpl extends BObject
    implements SessionManager, HeartBeatCollector, ForcedLogoutCollector, LogoutMonitor,
                UserTimeoutWarningCollector, AcceptTextMessageCollector, UserSessionLogoutCollector,
                RemoteCASCallbackRemovalCollector

{
    protected static int DEFAULT_SIZE = 50;

    protected com.cboe.idl.cmiV3.MarketQuery systemMarketCorba;
    protected com.cboe.idl.cmi.ProductQuery productQueryCorba;
    //protected com.cboe.idl.cmiV3.OrderQuery userOrderQueryCorba;
    protected com.cboe.idl.cmiV6.OrderQuery userOrderQueryCorba;
    protected com.cboe.idl.cmi.UserPreferenceQuery userPreferenceQueryCorba;
    protected com.cboe.idl.cmiV5.UserTradingParameters userTradingParametersCorba;
    // protected com.cboe.idl.cmi.Quote userQuoteQueryCorba;
    // protected com.cboe.idl.cmiV5.Quote userQuoteQueryCorba;
    protected com.cboe.idl.cmiV7.Quote userQuoteQueryCorba;
    protected com.cboe.idl.cmiV2.UserSessionManagerV2 userSessionManagerCorba;

    protected com.cboe.idl.cmi.TradingSession tradingSessionCorba;

    //protected com.cboe.idl.cmiV3.OrderEntry userOrderEntryCorba;
    //protected com.cboe.idl.cmiV5.OrderEntry userOrderEntryCorba;
    protected com.cboe.idl.cmiV9.OrderEntry userOrderEntryCorba;

    protected com.cboe.idl.cmi.Administrator administratorCorba;
    protected com.cboe.idl.cmi.ProductDefinition productDefinitionCorba;
    protected com.cboe.idl.cmi.UserHistory userHistoryCorba;
    protected com.cboe.idl.cmi.UserSessionManager sessionManager;

    protected UserEnablement userEnablement;

    protected HeartBeatProcessor heartBeatProcessor;
    protected UserTimeoutWarningProcessor userTimeoutWarningProcessor;
    protected AcceptTextMessageProcessor acceptTextMessageProcessor;
    protected ForcedLogoutProcessor forcedLogoutProcessor;
    protected UserSessionLogoutProcessor logoutProcessor;

    //protected UserStruct validUser;
    protected SessionProfileUserStructV2 validUser;
    protected String sessionId;
    protected int sessionKey;
    private Map<String,Integer> dependentSessions = new ConcurrentHashMap(10);
    protected short sessionType;
    protected CMIUserSessionAdmin sessionListener;

    protected UserSessionAdminSupplier adminSupplier;

    // indicator if the logging out is in process
    protected boolean ifLoggingOut = false;
    protected boolean lostConnection = false;
    protected boolean forcedLogoff = false;
    protected String instrumentorName;

    // List of users in a trading firm group, only valid for Firm Display User with
    // trading frim enablement
    protected List<String> tradingFirmGroup;

    // Cached value for toString() to return.
    protected String printName;
    private static final String PAR_PATTERN = "^W[0-9]{3,3}";


    /**
     * SessionManagerImpl constructor comment.
     *
     * member/firm information, priviledges and preferences.
     */
    public SessionManagerImpl() {
        super();

        systemMarketCorba = null;
        productQueryCorba = null;
        userOrderQueryCorba = null;
        userPreferenceQueryCorba = null;
        userTradingParametersCorba = null;
        userQuoteQueryCorba = null;
        tradingSessionCorba = null;
        userOrderEntryCorba = null;
        administratorCorba = null;
        productDefinitionCorba = null;
        userHistoryCorba = null;
        userEnablement = null;
        userSessionManagerCorba = null;
        calcPrintName();
    }

    public void setRemoteDelegate(Object remoteDelegate)
        throws SystemException, CommunicationException, AuthorizationException
    {
        sessionManager = (com.cboe.idl.cmi.UserSessionManager) remoteDelegate;

        // build collection of remote corba object
        ServicesHelper.getRemoteSessionManagerHome().addRemoteSession(this, sessionManager);

    }

    public SessionProfileUserStructV2 getValidSessionProfileUserV2()
            throws SystemException, CommunicationException, AuthorizationException
    {
        return validUser;
    }

    /**
     * Initializes the session manager.
     * @param validUser the ValidUserStruct containing the connecting users
     * member/firm information, priviledges and preferences.
     * @param ifLazyInitialization boolean indicator indicating if all
     * services should be initialized
     */
    protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey, boolean ifLazyInitialization,
                    CMIUserSessionAdmin clientListener, short sessionType, boolean gmdTextMessaging, boolean addUserInterest)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {

        this.validUser = validUser;
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        this.sessionType = sessionType;
        this.sessionListener = clientListener;
        calcPrintName();

        try{
        // init UserEnablement Service
        userEnablement = initUserEnablement();
        } catch (Exception e) {
            // Report this better somehow
            Log.exception(this, "session : " + this, e);
        }
        // name will have to be set before initializing everything else.
        instrumentorName = InstrumentorNameHelper.createInstrumentorName(new String[]{
            getUserId(),
            RemoteConnectionFactory.find().getHostname(clientListener)+":"+RemoteConnectionFactory.find().getPort(clientListener)
        }, this);

        adminSupplier = UserSessionAdminSupplierFactory.create(this);
        adminSupplier.setDynamicChannels(true);
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, this);
        LogoutServiceFactory.find().addLogoutListener(this, this);
        subscribeIECConsumers(this, validUser, clientListener, gmdTextMessaging);
        userTimeoutWarningProcessor = UserTimeoutWarningProcessorFactory.create(this);
        acceptTextMessageProcessor = AcceptTextMessageProcessorFactory.create(this);
        heartBeatProcessor = HeartBeatProcessorFactory.create( this );

        // get references to the CAS singletons.
        try {
            forcedLogoutProcessor = initForcedLogout();
            if (addUserInterest)
            {
                ServicesHelper.getSubscriptionService(this).addUserInterest(this);
            }
            productQueryCorba = initProductQuery();
            tradingSessionCorba = initTradingSession();
        } catch (Exception e) {
            // Report this better somehow
            Log.exception(this, "session : " + this, e);
            throw ExceptionBuilder.systemException("could not subscribe the User", 0);
        }

        try
        {
            subscribeCBOEConsumers(validUser);
        }
        catch( Exception e )
        {
            Log.exception(this, "session : " + this, e);
        }

        if ( !ifLazyInitialization )
        {
            try {
                // create a UserOrderQuery object.
                userOrderQueryCorba = initUserOrderQuery();


                // create a UserQuote object.
                userQuoteQueryCorba = initUserQuoteQuery();

                // create a UserPreferenceQuery object.
                userPreferenceQueryCorba = initUserPreferenceQuery();

                systemMarketCorba = initSystemMarketQuery();

                productDefinitionCorba = initProductDefinition();
                userOrderEntryCorba = initUserOrderEntry();
                userHistoryCorba = initUserHistory();

                //create administrator ( contains: TMS )
                administratorCorba = initAdministrator();
                // create a UserTradingParameters CORBA object.
                userTradingParametersCorba = initUserTradingParameters();
             } catch (Exception e) {
                //Report this better
                Log.exception(this, "session : " + this, e);
            }
        }

        if(validUser.userInfo.role == UserRoles.FIRM_DISPLAY && isTradingFirmEnabled())
        {
            try
            {
                tradingFirmGroup = initTradingFirmGroup();
            }
            catch (Exception e)
            {
                Log.exception(this, "session : " + this, e);
                throw ExceptionBuilder.systemException("could not init the trading firm group", 0);
            }
        }
        
        if(this.sessionType != LoginSessionTypes.PRIMARY && this.sessionType != LoginSessionTypes.SECONDARY)
        {
        	StringBuilder invalidSessionTypeMsg = new StringBuilder(100);
        	invalidSessionTypeMsg.append("session : ")
        		.append(this)
        		.append(" : Invalid LoginSessionType= ")
        		.append(this.sessionType)
        		.append(" sent by user: ")
                .append(validUser.userInfo.userId);
            Log.alarm(this, invalidSessionTypeMsg.toString());
        }
    }

    protected void subscribeIECConsumers(
            SessionManager sessionManager,
            SessionProfileUserStructV2 validUser,
            CMIUserSessionAdmin clientListener,
            boolean gmdTextMessaging)
        throws  DataValidationException,
                SystemException,
                CommunicationException,
                AuthorizationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.CB_LOGOUT, validUser.userInfo.userId);
        ChannelListener proxyListener =
            ServicesHelper.getSessionAdminConsumerProxy(
                clientListener, sessionManager, gmdTextMessaging);

        if (Log.isDebugOn())
        {
        	Log.debug(this, "Subscribe IEC using proxy listener = " + proxyListener + " GMD Status = " + gmdTextMessaging
                + " session = " + sessionManager);
        }

        adminSupplier.addChannelListener(this, proxyListener, channelKey, validUser.userInfo.userId);

        channelKey = new ChannelKey(ChannelType.CB_HEARTBEAT, validUser.userInfo.userId);
        adminSupplier.addChannelListener(this, proxyListener, channelKey, validUser.userInfo.userId);

        channelKey = new ChannelKey(ChannelType.CB_AUTHENTICATION_NOTICE, validUser.userInfo.userId);
        adminSupplier.addChannelListener(this, proxyListener, channelKey, validUser.userInfo.userId);

        channelKey = new ChannelKey(ChannelType.CB_TEXT_MESSAGE, validUser.userInfo.userId);
        adminSupplier.addChannelListener(this, proxyListener, channelKey, validUser.userInfo.userId);

        channelKey  = new ChannelKey( ChannelType.CB_UNREGISTER_LISTENER, validUser.userInfo.userId );
        adminSupplier.addChannelListener(this, proxyListener, channelKey, validUser.userInfo.userId);
    }

    protected void subscribeCBOEConsumers(SessionProfileUserStructV2 validUser)
    {

        try
        {
            subscribeHeartBeat();
            subscribeUserTimeoutWarning();
            // sign up for SACAS messages
            subscribeAcceptTextMessage();
            publishMessagesForUser( validUser.userInfo.userId );
        }
        catch(Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }



    public String getUserId()
        throws SystemException, CommunicationException, AuthorizationException
    {
        return validUser.userInfo.userId;
    }

    public String getExchange()
            throws SystemException, CommunicationException, AuthorizationException
    {
        return validUser.userInfo.userAcronym.exchange;
    }

    public String getAcronym()
            throws SystemException, CommunicationException, AuthorizationException
    {
        return validUser.userInfo.userAcronym.acronym;
    }

    public List<String> getTradingFirmGroup()
        throws SystemException, CommunicationException, AuthorizationException
    {
        return this.tradingFirmGroup;
    }

    protected void cleanUpUserCaches()
    {
        if(ServicesHelper.getRemoteSessionManagerHome().find(validUser.userInfo.userId).length == 0)
        {
            QuoteCacheFactory.remove(validUser.userInfo.userId);
            OrderQueryCacheFactory.remove(validUser.userInfo.userId);
            ServicesHelper.getUserEnablementHome().remove(validUser.userInfo.userId
                                                          , validUser.userInfo.userAcronym.exchange
                                                          , validUser.userInfo.userAcronym.acronym);
            ServicesHelper.getMarketQueryHome().removeSession(this);
            QuoteSemaphoreHandler.cleanupSemaphores(validUser.userInfo.userId);
            ((RateMonitorHomeImpl)ServicesHelper.getRateMonitorHome()).cleanupRateMonitors(validUser.userInfo.userId);
            
        }
    }

// submethod to here
    /**
     * getMarketQuery returns the global system market object.
     *
     * @return a reference to the global system market object.
     */
    public com.cboe.idl.cmi.MarketQuery getMarketQuery()
           throws SystemException, CommunicationException, AuthorizationException
    {
        return getMarketQueryV2();
    }

    /**
     * getOrderQuery returns the users order query object.
     *
     * @return the users order query object.
     */
    public com.cboe.idl.cmi.OrderQuery getOrderQuery()
           throws SystemException, CommunicationException, AuthorizationException
    {
        return getOrderQueryV2();
    }

    /**
     * getProductQuery returns a CAS global product query object.
     *
     * @return the product query object
     */
    public com.cboe.idl.cmi.ProductQuery getProductQuery()
           throws SystemException, CommunicationException, AuthorizationException
    {
//        Log.debug(this, "calling getProductQuery for " + this);
        try {
            if ( productQueryCorba == null )
            {
                productQueryCorba = initProductQuery();
            }
            return productQueryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get product query", e);
            throw ExceptionBuilder.systemException("Could not get product query " + e.toString(), 0);
        }
    }

    /**
     * getProductQuery returns a CAS global product query object.
     *
     * @return the product query object
     */
    public com.cboe.idl.cmi.TradingSession getTradingSession()
           throws SystemException, CommunicationException, AuthorizationException
    {
//        Log.debug(this, "calling getTradingSession for " + this);
        try {
            if ( tradingSessionCorba == null )
            {
                tradingSessionCorba = initTradingSession();
            }
            return tradingSessionCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get trading session", e);
            throw ExceptionBuilder.systemException("Could not get trading session " + e.toString(), 0);
        }
    }
    /**
     * getQuoteQuery returns the users quoteQuery object.
     *
     * @return the users quoteQuery object.
     */
    public com.cboe.idl.cmi.Quote getQuote()
           throws SystemException, CommunicationException, AuthorizationException
    {
        return getQuoteV3();
    }

    /**
     * Returns the ProductDefinition object.
     *
     * @return the ProductDefinition object.
     */
    public com.cboe.idl.cmi.ProductDefinition getProductDefinition()
           throws SystemException, CommunicationException, AuthorizationException
    {
//        Log.debug(this, "calling getProductDefinition for " + this);
        try {
            if ( productDefinitionCorba == null )
            {
                productDefinitionCorba = initProductDefinition();
            }
            return productDefinitionCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get product definition", e);
            throw ExceptionBuilder.systemException("Could not get product definition " + e.toString(), 0);
        }
    }

    /**
     * Returns the OrderEntry object.
     *
     * @return the OrderEntry object.
     */
    public com.cboe.idl.cmi.OrderEntry getOrderEntry()
           throws SystemException, CommunicationException, AuthorizationException
    {
        return getOrderEntryV3();
    }

    /**
     * Returns the Administrator object.
     *
     * @return the Administrator object.
     */
    public com.cboe.idl.cmi.UserHistory getUserHistory()
           throws SystemException, CommunicationException, AuthorizationException
    {
//        Log.debug(this, "calling getUserHistory for " + this);
        try {
            if ( userHistoryCorba == null )
            {
                userHistoryCorba = initUserHistory();
            }
            return userHistoryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get user history", e);
            throw ExceptionBuilder.systemException("Could not get user history " + e.toString(), 0);
        }
    }

    /**
     * Returns the Administrator object.
     *
     * @return the Administrator object.
     */

    public com.cboe.idl.cmi.Administrator getAdministrator()
           throws SystemException, CommunicationException, AuthorizationException
    {
//        Log.debug(this, "calling getAdministrator for " + this);
        try {
            if ( administratorCorba == null )
            {
                administratorCorba = initAdministrator();
            }
            return administratorCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get administrator", e);
            throw ExceptionBuilder.systemException("Could not get administrator " + e.toString(), 0);
        }
    }

    /**
     * Changes the user password.
     * @param oldPassword user's old password
     * @param newPassword the new password
     *
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     * @exception DataValidationException
     */
    public void changePassword(String oldPassword, String newPassword)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling changePassword for " + this);
        }
        FoundationFramework.getInstance().getSecurityService().changePassword(getSessionId(), oldPassword, newPassword);
    }

    /**
     * Returns the CMI Version.
     *
     * @return the version of the cmi API.
     */
    public String getVersion()
           throws SystemException, CommunicationException, AuthorizationException
    {
//        Log.debug(this, "calling getVersion for " + this);
        return Version.CMI_VERSION;
    }


    /**
     * getUserPreferenceQuery returns the users UserPreferenceQuery object.
     *
     * @return the users UserPreferenceQuery object.
     */
    public com.cboe.idl.cmi.UserPreferenceQuery getUserPreferenceQuery()
           throws SystemException, CommunicationException, AuthorizationException
    {
//        Log.debug(this, "calling getUserPreferenceQuery for " + this);
        try {
            if ( userPreferenceQueryCorba == null )
            {
                userPreferenceQueryCorba = initUserPreferenceQuery();
            }
            return userPreferenceQueryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get user preference", e);
            throw ExceptionBuilder.systemException("Could not get user prefrence " + e.toString(), 0);
        }
    }

    /**
     * getUserTradingParameters returns the users UserTradingParameters object.
     *
     * @return the users UserTradingParameters object.
     */
    public com.cboe.idl.cmi.UserTradingParameters getUserTradingParameters()
           throws SystemException, CommunicationException, AuthorizationException
    {
//        Log.debug(this, "calling getUserTradingParameters for " + this);
        try {
            if ( userTradingParametersCorba == null )
            {
                userTradingParametersCorba = initUserTradingParameters();
            }
            return userTradingParametersCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get user trading parameters", e);
            throw ExceptionBuilder.systemException("Could not get user trading parameters " + e.toString(), 0);
        }
    }


    /**
     * getSystemDateTime returns the current system time from the server.
     *
     * @return the current system date and time.
     * @exception SystemException
     * @exception CommunicationException
     * @exception AuthorizationException
     */
    public DateTimeStruct getSystemDateTime()
        throws SystemException, CommunicationException, AuthorizationException
    {
        return TimeServiceWrapper.toDateTimeStruct();
    }

    /**
     * getValidUser returns the connected users ValidUserStruct.
     *
     * @return the users ValidUserStruct
     */
    public UserStruct getValidUser()
           throws SystemException, CommunicationException, AuthorizationException
    {
        return SessionProfileHelper.toUserStruct(validUser.userInfo);
    }

	public SessionProfileUserStruct getValidSessionProfileUser()
			throws SystemException, CommunicationException, AuthorizationException
	{
        return validUser.userInfo;
	}

    protected void addToUserLogoutQueue()
    {
        LogoutQueue logoutQueue = LogoutQueueFactory.find(validUser.userInfo.userId);
        synchronized(logoutQueue)
        {
            logoutQueue.addLogout(this);
            if(logoutQueue.getQueueSize() >= ServicesHelper.getRemoteSessionManagerHome().find(validUser.userInfo.userId).length)
            {
                logoutQueue.setWaitForEmptyLogoutQueueFlag(true);
            }
        }
    }

    /**
    * Logs the user out
    * Call forceCloseSession if the session is being clossed due to a heartbeat failure. -- Gijo (5/22/06).
    */
    synchronized public void logout()
              throws SystemException, CommunicationException, AuthorizationException
    {
        //Log.notification(this, "calling logout for " + this + " : " + ifLoggingOut);
        StringBuilder sb = new StringBuilder(120);
        sb.append("calling logout for ").append(this).append(" loggingOut: ").append(ifLoggingOut)
               .append(" forcedLogoff: ").append(forcedLogoff).append(" lostConnection : ").append(lostConnection);
        Log.notification(this, sb.toString());

        // set the logging out flag so that no further use request will be processed
        if (!ifLoggingOut ) {
            try {
                ifLoggingOut = true;
            } finally {
                /*if (Log.isDebugOn())
                {
                Log.debug(this, "session : " + this + " : user logging out: " + validUser.userInfo.userId);
                }*/
            	sb.setLength(0);     
            	sb.append("session : ").append(this).append(" : user logging out: ").append(validUser.userInfo.userId);
            	Log.notification(this, sb.toString());
                // the following method is local _AND_ there's a version in SystemAdminSessionMAnagerImpl
                // that methog is essentially empty - possible refactoring task
                deregisterSPOWHeartBeatCallback();
                //Unregister Forced Logout Consumer and close SMS session
                FoundationFramework ff = FoundationFramework.getInstance();
                SessionManagementService sms = ff.getSessionManagementService();
                if(sms != null){
                    logOutDependentSessions();
                    if (!forcedLogoff) {
                        addToUserLogoutQueue();
                        try {
                            if ( sessionType == LoginSessionTypes.PRIMARY ) {
                            	if (lostConnection)
                            	{
                                    /*if (Log.isDebugOn())
                                    {
	                                Log.debug(this, "closing SMS session due to loss of connection for " + sessionKey + " : " + this);
                                    }*/  
                            		
                                    if (Pattern.matches(PAR_PATTERN, validUser.userInfo.userId))
                                    {
                                        if (Log.isDebugOn())
                                        {
                                        Log.debug(this, "Par closing SMS session due to loss of connection."+ validUser.userInfo.userId);
                                        }
                                        // If Par user get force log out call tell sms to closeSession per PAR2CD.
                                        sms.closeSession(sessionKey);
                                    }else
                                    {
                            		// forcing out due to heartbeat failure
	                                sms.forceCloseSession(sessionKey, "Forced logout due to loss of connection");
                                    }
	                                sb.setLength(0);     
	                            	sb.append("closing SMS session due to loss of connection for ").append(sessionKey).append(" : ").append(this);
	                            	Log.notification(this, sb.toString());
                            	}
                            	else
                            	{
                                    /*if (Log.isDebugOn())
                                    {
	                                Log.debug(this, "closing SMS session for " + sessionKey + " : " + this);
                                    }*/
                            		
	                                sms.closeSession(sessionKey);                            		
	                                
	                                sb.setLength(0);     
	                            	sb.append("closing SMS session for ").append(sessionKey).append(" : ").append(this);
	                            	Log.notification(this, sb.toString());
                            	}
                                sms = null;
                            } else {
                                /*if (Log.isDebugOn())
                                {
                                Log.debug(this, "leaving SMS session for " + sessionKey + " : " + this);
                                }*/
                                sms.leaveSession(sessionKey);
                                sms = null;
                                
                                sb.setLength(0);     
                            	sb.append("leaving SMS session for ").append(sessionKey).append(" : ").append(this);
                            	Log.notification(this, sb.toString());
                            }
                        } catch (Exception e){
                            Log.exception(this, "session : " + this, e);
                        }
                    }
                }
                publishLogout();
            }
        }
    }
    private void logOutDependentSessions() throws SystemException, AuthorizationException, CommunicationException
    {

        FoundationFramework ff = FoundationFramework.getInstance();
        SessionManagementService sms = ff.getSessionManagementService();
        for(Iterator<String> it=dependentSessions().keySet().iterator();it.hasNext();)
        {
            String userId= it.next();
            Log.information(this,new StringBuilder(100).append("Closing depenent SMS session:").append(userId).toString());
            try
            {
                sms.closeSession(dependentSessions.get(userId));

            }
            catch (Exception e)
            {
                Log.exception(this,e);
            }
            it.remove();
        }
    }
    protected void deregisterSPOWHeartBeatCallback()
    {
        // this method is local _AND_ there's a version in SystemAdminSessionManagerImpl
        // that method is essentially empty - possible refactoring
        if (Log.isDebugOn()) {
            Log.debug(this, "deregisterSPOWHeartBeatCallback() entry");
        }
        if (!ClientRoutingBOHome.clientIsRemote())
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "No heartbeat callback to deregister, since local client has no Frontend");
            }
            return;
        }
        HeartBeatCallbackService heartBeatCallbackService = ServicesHelper.getHeartBeatCallbackService();
        String orbName;
        try {
            orbName = FoundationFramework.getInstance().getConfigService().getProperty("Process.name()");
        } catch (com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException nsp) {
            Log.exception(this, "NoSuchPropertyException getting Process.name", nsp);
            orbName = System.getProperty("ORB." +
                    "OrbName");
            Log.alarm(this, "NoSuchPropertyException getting Process.name -substituting ORB.OrbName:" + orbName);
        }
        
        try {
            heartBeatCallbackService.deregisterHeartBeatCallback(orbName,HeartBeatConsumerFactory.getHeartBeatConsumerCallback() , orbName, UserCallbackDeregistrationModes.NORMAL ,"CAS logout");
            if (Log.isDebugOn()) {
                Log.debug(this, "deregisterSPOWHeartBeatCallback(" + orbName + ") complete");
            }
        }
        // exceptions set up a do-over next time
        catch (com.cboe.exceptions.SystemException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (com.cboe.exceptions.CommunicationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (com.cboe.exceptions.AuthorizationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
        catch (com.cboe.exceptions.DataValidationException ex) {
            Log.exception(this, ex.details.message, ex);
        }
    }



    public short getLoginType() {
//        Log.debug(this, "calling getLoginType for " + this);
        return sessionType;
    }

    //////////////// the interface methods for SessionManager in addition to UserSessionManager ////
    /**
    * Handles the channel listener lost of connection
    * @param channelListener the object that lost connection
    */
    synchronized public void lostConnection(ChannelListener channelListener)
         throws SystemException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling lostConnection for " + this);
        }
        if ((!ifLoggingOut) && (!lostConnection)) {

            lostConnection = true;
            try
            {
                ChannelKey key = new ChannelKey(ChannelType.CB_LOGOUT, validUser.userInfo.userId);

                ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, "Forced logout due to loss of connection.");
                adminSupplier.dispatch(event);

                StringBuilder force = new StringBuilder(120);
                force.append("session : ").append(this)
                     .append(" : Force user to logout due to the loss of connection for user: ")
                     .append(validUser.userInfo.userId);
                Log.alarm(this, force.toString());
            }
            catch(Exception e)
            {
                Log.exception(this, "session : " + this, e);
            }
            finally
            {
                try
                {
                    // now start to perform the logout processing
                    logout();
                }
                catch(Exception e)
                {
                    Log.exception(this, "session : " + this, e);
                    Log.alarm(this, "session : " + this + "logout failed in lostConnection: " + e.toString());
                }
            }
        }
    }

    /**
    * Post a message delivery with the TextMessagingService ( "guarateed deliver" )
    * @param userId     - user receiving the message
    * @param messageId  - TMS message ID
    */
    public void acceptMessageDelivery( String userId, int messageId )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling acceptMessageDelivery for " + this);
        }
        if ( !ifLoggingOut)
        {
            ServicesHelper.getTextMessagingService().acceptMessageDelivery(userId, messageId);
        }
    }

    /**
    * Post an order status acknowledgement with the OSSS ( "guarateed deliver" )
    * @param orderAcknowledge   - acknowledgement struct/info.
    */

    public void ackOrderStatus( OrderAcknowledgeStruct orderAcknowledge )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!ifLoggingOut)
        {
            // ServicesHelper.getOrderStatusConsumerHome().ackOrderStatus(orderAcknowledge);
            StringBuilder calling = new StringBuilder(100);
            calling.append("calling ackOrderStatus for ").append(this).append(getOrderIdString(orderAcknowledge.orderId));
            Log.information(this, calling.toString());
            ServicesHelper.getOrderStatusAdminPublisher().ackOrderStatus(CollectionHelper.EMPTY_int_ARRAY, orderAcknowledge);
        }
    }

    /**
    * Post an order status acknowledgement with the OSSS ( "guarateed deliver" ) on the class level
    */
    public void ackOrderStatusV3( OrderAcknowledgeStructV3 orderAcknowledge )
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!ifLoggingOut)
        {
            StringBuilder calling = new StringBuilder(100);
            calling.append("calling ackOrderStatusV3 for ").append(this).append(getOrderIdString(orderAcknowledge.orderId));
            Log.information(this, calling.toString());
            int[] groupKeys = ServicesHelper.getProductConfigurationService().getGroupKeysForProductClass(orderAcknowledge.classKey);
            RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", orderAcknowledge.classKey, (short)0);
            ServicesHelper.getOrderStatusAdminPublisher().ackOrderStatusV3(params, orderAcknowledge);
        }

    }


    /**
    * Post a quote status acknowledgement with the QSSS ( "guarateed deliver" )
    * @param quoteAcknowledge   - acknowledgement struct/info.
    */

    public void ackQuoteStatus( QuoteAcknowledgeStruct quoteAcknowledge )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!ifLoggingOut)
        {
            // ServicesHelper.getQuoteStatusConsumerHome().ackQuoteStatus(quoteAcknowledge);
            StringBuilder calling = new StringBuilder(100);
            calling.append("calling ackQuoteStatus for ").append(this).append(getQuoteIdString(quoteAcknowledge));
            Log.information(this, calling.toString());
            ServicesHelper.getQuoteStatusAdminPublisher().ackQuoteStatus(CollectionHelper.EMPTY_int_ARRAY, quoteAcknowledge);
        }
    }

    public void ackQuoteStatusV3( QuoteAcknowledgeStructV3 quoteAcknowledge )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (!ifLoggingOut)
        {
            // ServicesHelper.getQuoteStatusConsumerHome().ackQuoteStatus(quoteAcknowledge);
            StringBuilder calling = new StringBuilder(100);
            calling.append("calling ackQuoteStatus for ").append(this).append(getQuoteIdString(quoteAcknowledge));
            Log.information(this, calling.toString());
            int[] groupKeys = ServicesHelper.getProductConfigurationService().getGroupKeysForProductClass(quoteAcknowledge.classKey);
            RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", quoteAcknowledge.classKey, (short)0);
            ServicesHelper.getQuoteStatusAdminPublisher().ackQuoteStatusV3(params, quoteAcknowledge);
        }
    }

    /////////////////////// protected methods //////////////////////////////

    protected void unregisterRemoteObjects()
    {
        StringBuilder msg = new StringBuilder(70);
        msg.append("Unregister remote objects for session:").append(this);
        Log.information(this, msg.toString());
        try {
            unregisterSystemMarketQuery();
            unregisterProductQuery();
            unregisterUserOrderQuery();
            unregisterTradingSession();
            unregisterUserPreferenceQuery();
            unregisterUserQuoteQuery();
            unregisterUserOrderEntry();
            unregisterAdministrator();
            unregisterProductDefinition();
            unregisterUserTradingParameters();
            unregisterUserHistory();
            unregisterUserSessionManagerV2();
            unregisterSessionManager();
        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }
    /**
     * Initializes the trading session reference.
     */
    protected com.cboe.idl.cmi.TradingSession initTradingSession()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try{
            TradingSessionHome home = ServicesHelper.getTradingSessionHome();
            String poaName = getPOA((BOHome)home);
            TradingSession tradingSession = home.create(this);
            TradingSessionDelegate delegate = new TradingSessionDelegate(tradingSession);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            tradingSessionCorba = TradingSessionHelper.narrow(obj);

            return tradingSessionCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Trading Session", poae);
            throw ExceptionBuilder.systemException("Could not bind Trading Session", 1);
        }
    }

    protected void unregisterTradingSession()
    {
        try {
            if (tradingSessionCorba != null) {
                RemoteConnectionFactory.find().unregister_object(tradingSessionCorba);
                tradingSessionCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    protected void unregisterUserSessionManagerV2()
    {
        try {
            if (userSessionManagerCorba != null) {
                RemoteConnectionFactory.find().unregister_object(userSessionManagerCorba);
                userSessionManagerCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    protected com.cboe.idl.cmiV2.UserSessionManagerV2 initUserSessionManagerV2()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            String poaName = POANameHelper.getPOAName(getBOHome());
            UserSessionManagerV2Delegate delegate = new UserSessionManagerV2Delegate(this);

            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);

            userSessionManagerCorba = UserSessionManagerV2Helper.narrow(obj);
            return userSessionManagerCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind User Session V2", poae);
            throw ExceptionBuilder.systemException("Could not bind User Session V2", 1);
        }
    }


    /**
     * Initializes the user order query reference.
     */
   /* protected com.cboe.idl.cmiV3.OrderQuery initUserOrderQuery()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            UserOrderQueryHome home = ServicesHelper.getUserOrderQueryHome();
            String poaName = getPOA((BOHome)home);
            OrderQueryV3 userOrderQuery = home.create(this);
            OrderQueryV3Delegate delegate = new OrderQueryV3Delegate(userOrderQuery);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            userOrderQueryCorba = com.cboe.idl.cmiV3.OrderQueryHelper.narrow(obj);

            return userOrderQueryCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Order Query", poae);
            throw ExceptionBuilder.systemException("Could not bind Order Query", 1);
        }
    }*/

    protected com.cboe.idl.cmiV6.OrderQuery initUserOrderQuery()
    throws SystemException, CommunicationException, AuthorizationException
    {
	    try {
	        UserOrderQueryHome home = ServicesHelper.getUserOrderQueryHome();
	        String poaName = getPOA((BOHome)home);
	        OrderQueryV6 userOrderQuery = home.create(this);
	        OrderQueryV6Delegate delegate = new OrderQueryV6Delegate(userOrderQuery);
	        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
	                register_object(delegate, poaName);
	        userOrderQueryCorba = com.cboe.idl.cmiV6.OrderQueryHelper.narrow(obj);
	
	        return userOrderQueryCorba;
	    }
	    catch( Exception poae )
	    {
	        Log.exception(this, "Could not bind Order Query", poae);
	        throw ExceptionBuilder.systemException("Could not bind Order Query", 1);
	    }
    }
    /**
     * Initializes the (CAS) user enablementServie
     */
    protected UserEnablement initUserEnablement()
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling initUserEnablement for " + this);
        }

        UserEnablement userEnablement = ServicesHelper.getUserEnablementService(this.validUser.userInfo.userId
                                                                                , this.validUser.userInfo.userAcronym.exchange
                                                                                , this.validUser.userInfo.userAcronym.acronym);

        try
        {
            initUserEnablementByExchangeAcronym(userEnablement);

            // FIXME - KAK  after single acr rollout, this can be removed
            // if the user is a single acr user, the his TestClassesOnly update
            // will come ONLY from the property service
            // this flag prevents the "old" TestClassesOnly update path from being
            // used.
            userEnablement.setPropertyUpdatesOnly(true);
        }
        // FIXME - KAK  after full single acr rollout, this will no longer be needed
        catch( NotFoundException nfe )
        {
            initUserEnablementByUser(userEnablement);
        }

        return userEnablement;
    }

    // FIXME - KAK  after full single acr rollout, this will no longer be needed
    private void initUserEnablementByUser(UserEnablement userEnablement)
            throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "Getting user enablement settings for " + this.validUser.userInfo.userId + " from the user service");
        }

        UserEnablementStruct enablementStruct = ServicesHelper.getUserService().getUserEnablement(this.validUser.userInfo.userId);

        PropertyGroupStruct propertyGroupStruct = ServicesHelper.getPropertyService().getProperties(PropertyCategoryTypes.USER_ENABLEMENT, this.validUser.userInfo.userId);
        userEnablement.acceptUserEnablementUpdate(propertyGroupStruct, enablementStruct);
    }

    private void initUserEnablementByExchangeAcronym(UserEnablement userEnablement)
        throws SystemException, CommunicationException, AuthorizationException, NotFoundException, DataValidationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "Getting user enablement settings for "
                        + this.validUser.userInfo.userId
                        + ":" + this.validUser.userInfo.userAcronym.exchange
                        + ":" + this.validUser.userInfo.userAcronym.acronym
                        + " from the user service");
        }

        String enablementKey = userEnablement.getUserEnablementKey();
        PropertyGroupStruct propertyGroupStruct = ServicesHelper.getPropertyService().getProperties(PropertyCategoryTypes.USER_ENABLEMENT, enablementKey);
        userEnablement.acceptUserEnablementUpdate(propertyGroupStruct);

        String testClassesKey = userEnablement.getUserTestClassesKey();
        propertyGroupStruct = ServicesHelper.getPropertyService().getProperties(PropertyCategoryTypes.USER_ENABLEMENT, testClassesKey);
        userEnablement.acceptUserEnablementUpdate(propertyGroupStruct);

        String mdxKey = userEnablement.getUserMDXEnablmentKey();
        try
        {
            propertyGroupStruct = ServicesHelper.getPropertyService().getProperties(PropertyCategoryTypes.USER_ENABLEMENT, mdxKey);
            userEnablement.acceptUserEnablementUpdate(propertyGroupStruct);
        }
        catch(NotFoundException e)
        {
            Log.information(this, "Warning: No property was found for key '"+mdxKey+"'");
        }

        String tradingFirmKey = userEnablement.getUserTradingFirmEnablementKey();
        try
        {
            propertyGroupStruct =
                    ServicesHelper.getPropertyService().getProperties(PropertyCategoryTypes.USER_ENABLEMENT, tradingFirmKey);
            userEnablement.acceptUserEnablementUpdate(propertyGroupStruct);
        }
        catch(NotFoundException e)
        {
            Log.information(this, "Warning: No property was found for key '"+tradingFirmKey+"'");
        }
     }

    protected List<String> initTradingFirmGroup()
        throws SystemException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling initTradeFirmGroup for " + this);
        }

        // TODO: check Trading Firm enabled

        //userEnablement.verifyUserEnablementForSession
        // (sessionManager.getTradingSession().Name, OperationTypes.ISTRADINGFIRM);
        List<String> group = new ArrayList<String>();
        try
        {
            GroupServiceHome home = ServicesHelper.getGroupServiceHome();
            String poaName = getPOA((BOHome) home);
            GroupService service = home.find();
            if (Log.isDebugOn())
                Log.debug(this, "Querying GroupService for group users for user:" + getUserId() + ", " + this);
            ElementStruct[] groupElements = service.getAllLeafElementsForGroup(getUserId());
            for(ElementStruct element : groupElements)
            {
                String user = element.entryStruct.elementName;
                if (Log.isDebugOn())
                    Log.debug(this, "Adding user " + user + " to group for Firm User " + getUserId() + ", " + this);
                group.add(user);
            }
        }
        catch (Exception poae)
        {
            Log.exception("SessionManagerImpl.initGroupService() Exception occurred :", poae);
            throw ExceptionBuilder.systemException("Could not bind GroupService", 1);
        }


        // return an empty List if not a Trading Firm user
        return group;
    }

    protected void unregisterUserOrderQuery()
    {
        try {
            if (userOrderQueryCorba != null) {
                RemoteConnectionFactory.find().unregister_object(userOrderQueryCorba);
                userOrderQueryCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    /**
     * Initializes the user quote query reference.
     */
    protected com.cboe.idl.cmiV7.Quote initUserQuoteQuery()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            UserQuoteHome home = ServicesHelper.getUserQuoteHome();
            String poaName = getPOA((BOHome)home);
            QuoteV7 userQuoteQuery = home.create(this);

            if (Log.isDebugOn())
            {
            Log.debug(this, "Quote=" + userQuoteQuery + " poaName=" + poaName);
            }
            QuoteV7Delegate delegate = new QuoteV7Delegate(userQuoteQuery);
            if (Log.isDebugOn())
            {
            Log.debug(this, "delegate=" + delegate);
            }
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            userQuoteQueryCorba = com.cboe.idl.cmiV7.QuoteHelper.narrow(obj);

            return userQuoteQueryCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Quote", poae);
            throw ExceptionBuilder.systemException("Could not bind Quote", 1);
        }
    }

    protected void unregisterUserQuoteQuery()
    {
        try {
            if (userQuoteQueryCorba != null) {
                RemoteConnectionFactory.find().unregister_object(userQuoteQueryCorba);
                userQuoteQueryCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    protected void unregisterUserTradingParameters()
    {
        try {
            if (userTradingParametersCorba != null) {
                RemoteConnectionFactory.find().unregister_object(userTradingParametersCorba);
                userTradingParametersCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    /**
     * Initializes the user preference query reference.
     */
    protected com.cboe.idl.cmi.UserPreferenceQuery initUserPreferenceQuery()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            UserPreferenceQueryHome home = ServicesHelper.getUserPreferenceQueryHome();
            String poaName = getPOA((BOHome)home);
            UserPreferenceQuery userPreferenceQuery = home.create(this);
            UserPreferenceQueryDelegate delegate = new UserPreferenceQueryDelegate(userPreferenceQuery);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            userPreferenceQueryCorba = UserPreferenceQueryHelper.narrow(obj);

            return userPreferenceQueryCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind User Preference Query", poae);
            throw ExceptionBuilder.systemException("Could not bind User Preference Query", 1);
        }
    }

    protected void unregisterUserPreferenceQuery()
    {
        try {
            if (userPreferenceQueryCorba != null) {
                RemoteConnectionFactory.find().unregister_object(userPreferenceQueryCorba);
                userPreferenceQueryCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    /**
     * Initializes the user trading parameters  CORBA object reference.
     *
     * @return com.cboe.idl.cmiV5.UserTradingParameters Reference to corba object
     */
    protected com.cboe.idl.cmiV5.UserTradingParameters initUserTradingParameters()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            UserTradingParametersHome home = ServicesHelper.getUserTradingParametersHome();
            String poaName = getPOA((BOHome)home);
            UserTradingParametersV5 userTradingParameters = home.create(this);
            UserTradingParametersV5Delegate delegate = new UserTradingParametersV5Delegate(userTradingParameters);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            userTradingParametersCorba = com.cboe.idl.cmiV5.UserTradingParametersHelper.narrow(obj);

            return userTradingParametersCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind User Trading Parameters", poae);
            throw ExceptionBuilder.systemException("Could not bind User Trading Parameters", 1);
        }
    }

    /**
     * Initializes the product query reference
     */
    protected com.cboe.idl.cmi.ProductQuery initProductQuery()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            ProductQueryManagerHome home = ServicesHelper.getProductQueryManagerHome();
            String poaName = getPOA((BOHome)home);
            ProductQueryManager productQuery = home.create(this);
            ProductQueryDelegate delegate = new ProductQueryDelegate(productQuery);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
            productQueryCorba = ProductQueryV2Helper.narrow(obj);
            return productQueryCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, poae);
            throw ExceptionBuilder.systemException("Could not bind Product Query", 1);
        }
    }

    protected void unregisterProductQuery()
    {
        try {
            if (productQueryCorba != null) {
                RemoteConnectionFactory.find().unregister_object(productQueryCorba);
                productQueryCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    /**
     * Initializes the system market query reference.
     */
    protected com.cboe.idl.cmiV3.MarketQuery initSystemMarketQuery()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            MarketQueryHome home = ServicesHelper.getMarketQueryHome();
            String poaName = getPOA((BOHome)home);
            MarketQueryV3 systemMarket = home.createMarketQuery(this);
            MarketQueryV3Delegate delegate = new MarketQueryV3Delegate(systemMarket);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            systemMarketCorba = com.cboe.idl.cmiV3.MarketQueryHelper.narrow(obj);

            return systemMarketCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Market Query", poae);
            throw ExceptionBuilder.systemException("Could not bind Market Query", 1);
        }

    }

    protected void unregisterSystemMarketQuery()
    {
        try {
            if (systemMarketCorba != null) {
                RemoteConnectionFactory.find().unregister_object(systemMarketCorba);
                systemMarketCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }


    /*
    protected com.cboe.idl.cmiV3.OrderEntry initUserOrderEntry()
        throws SystemException, CommunicationException, AuthorizationException
    {

        try {
            UserOrderEntryHome home = ServicesHelper.getUserOrderEntryHome();
            String poaName = getPOA((BOHome)home);

            OrderEntryV3 userOrderEntry = home.create(this);
            OrderEntryV3Delegate delegate = new OrderEntryV3Delegate(userOrderEntry);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            userOrderEntryCorba = com.cboe.idl.cmiV3.OrderEntryHelper.narrow(obj);

            return userOrderEntryCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Order Entry", poae);
            throw ExceptionBuilder.systemException("Could not bind Order Entry", 1);
        }
    }
    */

    /*
    protected com.cboe.idl.cmiV5.OrderEntry initUserOrderEntry()
        throws SystemException, CommunicationException, AuthorizationException
    {

        try {
            UserOrderEntryHome home = ServicesHelper.getUserOrderEntryHome();
            String poaName = getPOA((BOHome)home);
            OrderEntryV5 userOrderEntry = home.create(this);
            OrderEntryV5Delegate delegate = new OrderEntryV5Delegate(userOrderEntry);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            userOrderEntryCorba = com.cboe.idl.cmiV5.OrderEntryHelper.narrow(obj);

            return userOrderEntryCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Order Entry ", poae);
            throw ExceptionBuilder.systemException("Could not bind Order Entry ", 1);
        }
    }
    */

    /**
     * Initializes the user order entry reference.
     */
    protected com.cboe.idl.cmiV9.OrderEntry initUserOrderEntry()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            UserOrderEntryHome home = ServicesHelper.getUserOrderEntryHome();
            String poaName = getPOA((BOHome)home);
            OrderEntryV9 userOrderEntry = home.create(this);
            OrderEntryV9Delegate delegate = new OrderEntryV9Delegate(userOrderEntry);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            userOrderEntryCorba = com.cboe.idl.cmiV9.OrderEntryHelper.narrow(obj);

            return userOrderEntryCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Order Entry ", poae);
            throw ExceptionBuilder.systemException("Could not bind Order Entry ", 1);
        }
    }

    protected void unregisterUserOrderEntry()
    {
        try {
            if (userOrderEntryCorba != null) {
                RemoteConnectionFactory.find().unregister_object(userOrderEntryCorba);
                userOrderEntryCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    /**
     * Initializes the product definition service reference.
     */
    protected com.cboe.idl.cmi.ProductDefinition initProductDefinition()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            ProductDefinitionHome home = ServicesHelper.getProductDefinitionHome();
            String poaName = getPOA((BOHome)home);
            ProductDefinition productDefinition = home.create(this);
            ProductDefinitionDelegate delegate = new ProductDefinitionDelegate(productDefinition);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            productDefinitionCorba = ProductDefinitionHelper.narrow(obj);

            return productDefinitionCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Product Definition", poae);
            throw ExceptionBuilder.systemException("Could not bind Product Definition", 1);
        }
    }

    protected void unregisterProductDefinition()
    {
        try {
            if (productDefinitionCorba != null) {
                RemoteConnectionFactory.find().unregister_object(productDefinitionCorba);
                productDefinitionCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    /**
     * Initializes the Administrator service reference.
     */
    protected com.cboe.idl.cmi.Administrator initAdministrator()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            AdministratorHome home = ServicesHelper.getAdministratorHome();
            String poaName = getPOA((BOHome)home);
            Administrator administrator = home.create(this);
            AdministratorDelegate delegate = new AdministratorDelegate(administrator);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            administratorCorba = AdministratorHelper.narrow(obj);

            return administratorCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind Administrator", poae);
            throw ExceptionBuilder.systemException("Could not bind Administrator", 1);
        }
    }

    protected void unregisterAdministrator()
    {
        try
        {
            if (administratorCorba != null) {
                RemoteConnectionFactory.find().unregister_object(administratorCorba);
                administratorCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }
    /**
     * Initializes the product query reference
     */
    protected com.cboe.idl.cmi.UserHistory initUserHistory()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            UserHistoryHome home = ServicesHelper.getUserHistoryHome();
            String poaName = getPOA((BOHome)home);
            UserHistory userHistory = home.create(this);
            UserHistoryDelegate delegate = new UserHistoryDelegate(userHistory);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            userHistoryCorba = UserHistoryHelper.narrow(obj);

            return userHistoryCorba;
        }
        catch( Exception poae )
        {
            Log.exception(this, "Could not bind User History", poae);
            throw ExceptionBuilder.systemException("Could not bind User History", 1);
        }
    }

    protected void unregisterUserHistory()
    {
        try {
            if (userHistoryCorba != null) {
                RemoteConnectionFactory.find().unregister_object(userHistoryCorba);
                userHistoryCorba = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    protected void unregisterSessionManager()
    {
        try {
            if (sessionManager != null) {
                RemoteConnectionFactory.find().unregister_object(sessionManager);
                sessionManager = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
    }

    protected void subscribeHeartBeat()
    {
        ChannelKey channelKey = new ChannelKey( ChannelType.CB_HEARTBEAT, Integer.valueOf(0) );
        EventChannelAdapterFactory.find().addChannelListener(this, heartBeatProcessor, channelKey);
    }

    /**
     * Initializes the Forced Logout message recieving by this object
     */
    protected ForcedLogoutProcessor initForcedLogout()
    {
        ForcedLogoutProcessor forcedLogoutProcessor = ForcedLogoutProcessorFactory.create( this );
        ChannelKey channelKey = new ChannelKey( ChannelType.CB_LOGOUT, validUser.userInfo.userId );

        EventChannelAdapterFactory.find().addChannelListener(this, forcedLogoutProcessor, channelKey);

        return( forcedLogoutProcessor );
    }

    //TextMessagingService
    //
    /**
     * Request any/all waiting messages be delivered via consumer
     */
    protected void publishMessagesForUser(String userId)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ServicesHelper.getTextMessagingService().publishMessagesForUser(userId);
    }

    protected void subscribeUserTimeoutWarning()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.USER_SECURITY_TIMEOUT, validUser.userInfo.userId);
        EventChannelAdapterFactory.find().addChannelListener(this, userTimeoutWarningProcessor, channelKey);
    }

    protected void subscribeAcceptTextMessage()
        throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, DataValidationException
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.TEXT_MESSAGE_BY_USER, validUser.userInfo.userId);
        EventChannelAdapterFactory.find().addChannelListener(this, acceptTextMessageProcessor, channelKey);
    }

    public void authenticate(UserLogonStruct logonStruct)
        throws SystemException, CommunicationException, AuthorizationException, AuthenticationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling authenticate for " + this);
        }
        sessionId = FoundationFramework.getInstance().getSecurityService().authenticateWithPassword(logonStruct.userId, logonStruct.password);
        if ( sessionId == null )
        {
            throw ExceptionBuilder.authenticationException("Authentication Failed!", AuthenticationCodes.INCORRECT_PASSWORD);
        }
    }

    public String getSessionId()
        throws SystemException, CommunicationException, AuthorizationException
    {
        return sessionId;
    }

    public void acceptForcedLogout( int key, String message )
    {
        StringBuilder calling = new StringBuilder(70);
        calling.append("calling acceptForcedLogout for ").append(this);
        Log.notification(this, calling.toString());
        if(key != sessionKey)
        {
            Log.alarm(this, "received logout event with invalid sessionKey: "+key);
            return;
        }
        // don't bother reporting logout or re-logging out if already in a logout process.
        if ((!ifLoggingOut) && (!lostConnection)) {
            forcedLogoff = true;
            addToUserLogoutQueue();
            try
            {
                sessionListener.acceptLogout(message);
            }
            catch (Exception e)
            {
                Log.exception(this, "session : " + this, e);
            }
            try
            {
                logout();

            } catch(Exception e){
                Log.exception(this, "session : " + this + " : Logout Failure", e);
            }
        }
    }

    public void acceptHeartBeat( HeartBeatStruct   heartBeatStruct )
    {
        ChannelKey     channelKey  = new ChannelKey( ChannelType.CB_HEARTBEAT, validUser.userInfo.userId );
        ChannelEvent   event       = adminSupplier.getChannelEvent( this, channelKey, heartBeatStruct );

        adminSupplier.dispatch( event );
    }

    public void acceptUserTimeoutWarning(String userName)
    {
        if ( userName.equals(validUser.userInfo.userId))
        {
            ChannelKey     channelKey  = new ChannelKey( ChannelType.CB_AUTHENTICATION_NOTICE, validUser.userInfo.userId );
            ChannelEvent   event       = adminSupplier.getChannelEvent( this, channelKey, "" );

            adminSupplier.dispatch( event );
        }
    }

    public void acceptTextMessage( MessageStruct message )
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling acceptTextMessage for " + this);
        }
        ChannelKey     channelKey  = new ChannelKey( ChannelType.CB_TEXT_MESSAGE, validUser.userInfo.userId );
        ChannelEvent   event       = adminSupplier.getChannelEvent( this, channelKey, message );

        adminSupplier.dispatch( event );
    }

    public void unregisterNotification(CallbackDeregistrationInfo deregistrationInfo)
        throws SystemException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
        Log.debug(this, "calling unregisterNotification for " + this);
        }
        ChannelKey     channelKey  = new ChannelKey( ChannelType.CB_UNREGISTER_LISTENER, validUser.userInfo.userId );
        ChannelEvent   event       = adminSupplier.getChannelEvent( this, channelKey, deregistrationInfo);

        adminSupplier.dispatch( event );
    }

    protected void remove(SessionProfileUserStructV2 validUser, SessionManager session)
    {
        try {
            ServicesHelper.getSessionManagerHome().remove(this, validUser.userInfo.userId);
        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    protected synchronized void publishLogout()
    {
        StringBuilder calling = new StringBuilder(90);
        calling.append("calling publishLogout for sessionManager:").append(this);
        Log.information(this, calling.toString());
        ServicesHelper.getRemoteCASSessionManagerPublisher().logout(
                ServicesHelper.getAppServerStatusManager().getProcessName(),
                ServicesHelper.getSessionManagerHome().getUserSessionIor(this),
                validUser.userInfo.userId);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, this, this);
        EventChannelAdapterFactory.find().dispatch(event);

    }

    protected void cleanupProcessors()
    {
        try {
            if (heartBeatProcessor != null)
            {
                heartBeatProcessor.setParent(null);
                heartBeatProcessor = null;
            }
            else
            {
                Log.information(this, "cleanupProcessors, heartBeatProcessor is null!");
            }
            if (userTimeoutWarningProcessor != null)
            {
                userTimeoutWarningProcessor.setParent(null);
                userTimeoutWarningProcessor = null;
            }
            else
            {
                Log.information(this, "cleanupProcessors, userTimeoutWarningProcess is null!");
            }
            if (acceptTextMessageProcessor != null)
            {
                acceptTextMessageProcessor.setParent(null);
                acceptTextMessageProcessor = null;
            }
            else
            {
                Log.information(this, "cleanupProcessors, acceptTextMessageProcessor is null!");
            }
            if (forcedLogoutProcessor != null)
            {
                forcedLogoutProcessor.setParent(null);
                forcedLogoutProcessor = null;
            }
            else
            {
                Log.information(this, "cleanupProcessors, forcedLogoutProcessor is null!");
            }
            if (logoutProcessor != null)
            {
                logoutProcessor.setParent(null);
                logoutProcessor = null;
            }
            else
            {
                Log.information(this, "cleanupProcessors, logoutProcessor is null!");
            }

        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    protected void cleanupUserSuppliers()
    {
        try {
            QuoteStatusSupplierFactory.remove(this);
            OrderStatusSupplierFactory.remove(this);
            UserSessionAdminSupplierFactory.remove(this);
            QuoteStatusV2SupplierFactory.remove(this);
            OrderStatusV2SupplierFactory.remove(this);
            CurrentMarketSupplierFactory.remove(this);
            CurrentMarketV2SupplierFactory.remove(this);
            CurrentMarketV3SupplierFactory.remove(this);
            NBBOSupplierFactory.remove(this);
            NBBOV2SupplierFactory.remove(this);
            RecapSupplierFactory.remove(this);
            RecapV2SupplierFactory.remove(this);
            TickerSupplierFactory.remove(this);
            TickerV2SupplierFactory.remove(this);
            BookDepthSupplierFactory.remove(this);
            BookDepthV2SupplierFactory.remove(this);
            ExpectedOpeningPriceSupplierFactory.remove(this);
            ExpectedOpeningPriceV2SupplierFactory.remove(this);
            LargeTradeLastSaleSupplierFactory.remove(this);
            OrderRoutingSupplierFactory.remove(this);
        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    protected void cleanupOrb()
    {
        RemoteConnectionFactory.find().cleanupConnection(sessionListener);
    }

    protected void cleanUpInstrumentors()
    {
        // clean up all instrumentors created in boInterceptors that belong to this session.
        MethodInstrumentorFactory aggregatedMethodInstrumentorFactory
                =  FoundationFramework.getInstance().getInstrumentationService().getAggregatedMethodInstrumentorFactory();
        MethodInstrumentor mi = aggregatedMethodInstrumentorFactory.find(getInstrumentorName());
        if(mi != null)
        {
            ((AggregatedMethodInstrumentor)mi).removeMembersFromFactories();
            aggregatedMethodInstrumentorFactory.unregister(mi);
        }
    }

    public void acceptUserSessionLogout()
    {
        try {
            if (Log.isDebugOn())
            {
            Log.debug(this, "calling acceptUserSessionLogout for " + this);
            }
            //remove collection of remote session

            cleanUpInstrumentors();
            EventChannelAdapterFactory.find().removeListenerGroup(this);
            adminSupplier.removeListenerGroup(this);
            cleanupUserSuppliers();
            if (sessionManager != null)
            {
                ServicesHelper.getRemoteSessionManagerHome().removeRemoteSession(sessionManager);
            }
            else
            {
                Log.information(this, "acceptUserSessionLogout, sessionManager is null!");
            }
            unregisterRemoteObjects();
            remove(validUser, this);
            cleanUpUserCaches();
            // Do any individual service clean up needed for logout
            cleanupProcessors();

            ServicesHelper.getUserSessionThreadPoolHome().remove(this);
            ServicesHelper.getUserSessionMarketDataThreadPoolHome().remove(this);
            cleanUpUserServices();
            ServicesHelper.getSubscriptionServiceHome().remove(this);
            // Remove itself since we are caching now in Home factory
            ServicesHelper.getMarketQueryHome().removeSession(this);

            sessionListener = null;
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
        finally
        {
            LogoutServiceFactory.find().logoutComplete(this, this);
        }

    }

    protected void cleanUpUserServices()
    {
        ServicesHelper.getUserOrderServiceHome().remove(this);
        ServicesHelper.getUserMarketDataServiceHome().remove(this);
        ServicesHelper.getUserQuoteServiceHome().remove(this);
        ServicesHelper.getUserTradingSessionServiceHome().remove(this);
        ServicesHelper.getTradingClassStatusQueryServiceHome().remove(this);
    }

    public void logoutCleanup()
    {
        try {
            cleanupOrb();
            EventChannelAdapterFactory.find().removeChannel(this);
            LogoutQueueFactory.find(validUser.userInfo.userId).removeLogout(this);
            StringBuilder msg = new StringBuilder(80);
            msg.append("Logout cleanup finished for ").append(this);
            Log.information(this, msg.toString());
        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    public String toString()
    {
        return printName;
    }

    /** Calculate the value that toString() will return. This depends on validUser and sessionType,
     * so we (and our subclasses) must call this method any time that validUser or sessionType changes.
     * @see #validUser, #sessionType, #printName
     */
    protected void calcPrintName()
    {
        StringBuilder sessionInfo = new StringBuilder(32);
        if (validUser != null)
        {
            sessionInfo.append(USER_ACRONYM).append(VALUE_DELIMITER);
            sessionInfo.append(validUser.userInfo.userId);
            sessionInfo.append(TOKEN_DELIMITER).append(SESSION_TYPE).append(VALUE_DELIMITER);
            sessionInfo.append(sessionType);
            sessionInfo.append(TOKEN_DELIMITER).append(SESSION_ID).append(VALUE_DELIMITER);
            sessionInfo.append(hashCode());
        }
        else
        {
            sessionInfo.append(USER_ACRONYM).append(VALUE_DELIMITER);
            sessionInfo.append(validUser);
            sessionInfo.append(TOKEN_DELIMITER).append(SESSION_ID).append(VALUE_DELIMITER);
            sessionInfo.append(hashCode());        
        }
        printName = sessionInfo.toString();
    }

    private String getOrderIdString(OrderIdStruct orderId)
    {
        StringBuilder oid = new StringBuilder(40);
        oid.append(":oid=").append(orderId.executingOrGiveUpFirm.exchange)
           .append(':').append(orderId.executingOrGiveUpFirm.firmNumber)
           .append(':').append(orderId.branch).append(':').append(orderId.branchSequenceNumber)
           .append(": h=").append(orderId.highCboeId)
           .append(": l=").append(orderId.lowCboeId);
        return oid.toString();
    }

    private String getQuoteIdString (QuoteAcknowledgeStruct quoteAck)
    {
        StringBuilder toStr = new StringBuilder(60);
        toStr.append(':');
        toStr.append(quoteAck.acknowledgingUserId).append(':');
        toStr.append(":pkey=").append(quoteAck.productKey).append(':');
        toStr.append(":qkey=").append(quoteAck.quoteKey);
        return toStr.toString();
    }

    private String getQuoteIdString (QuoteAcknowledgeStructV3 quoteAck)
    {
        StringBuilder toStr = new StringBuilder(60);
        toStr.append(':');
        toStr.append(quoteAck.acknowledgingUserId).append(':');
        toStr.append(":pkey=").append(quoteAck.productKey).append(':');
        toStr.append(":qkey=").append(quoteAck.quoteKey);
        return toStr.toString();
    }

    public short getSessionType()
    {
        return sessionType;
    }

    public int getSessionKey()
        throws SystemException, CommunicationException, AuthorizationException
    {
        return sessionKey;
    }

    protected String getPOA(BOHome home)
    {
        String poaName = "";
        try {
            FoundationFramework instance =FoundationFramework.getInstance();
            ConfigurationService config = instance.getConfigService();
            poaName = config.getProperty(home.getFrameworkFullName() + ".poaName", null);
            if(poaName == null)
            {
                poaName = config.getProperty(home.getContainer().getFullName() + ".poaName", null);
            }
            if(poaName == null)
            {
                poaName = config.getProperty(instance.getFullName() + ".poaName");
            }
        } catch (Exception e)
        {
            Log.exception (this, "Could not get POA Name for " + home, e);
        }
        return poaName;
    }

    public com.cboe.idl.cmiV2.OrderQuery getOrderQueryV2() throws SystemException, CommunicationException, AuthorizationException
    {
        return getOrderQueryV3();
    }

    public com.cboe.idl.cmiV2.MarketQuery getMarketQueryV2() throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            if ( systemMarketCorba == null )
            {
                systemMarketCorba = initSystemMarketQuery();
            }
            return systemMarketCorba;
        }

        catch (Exception e)
        {
            Log.exception(this, "Could not get market query V2", e);
            throw ExceptionBuilder.systemException("Could not get market query V2 " + e.toString(), 0);
        }
    }

    public com.cboe.idl.cmiV2.Quote getQuoteV2() throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            if ( userQuoteQueryCorba == null )
            {
                userQuoteQueryCorba = initUserQuoteQuery();
            }
            return userQuoteQueryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get quote", e);
            throw ExceptionBuilder.systemException("Could not get quote " + e.toString(), 0);
        }
    }

    public com.cboe.idl.cmiV2.UserSessionManagerV2 getUserSessionManagerV2() throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            if ( userSessionManagerCorba == null )
            {
                userSessionManagerCorba = initUserSessionManagerV2();
            }
            return userSessionManagerCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get userSession v2", e);
            throw ExceptionBuilder.systemException("Could not get userSession v2 " + e.toString(), 0);
        }
    }

    public com.cboe.idl.cmiV3.MarketQuery getMarketQueryV3() throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            if ( systemMarketCorba == null )
            {
                systemMarketCorba = initSystemMarketQuery();
            }
            return systemMarketCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get MArketQuery V3", e);
            throw ExceptionBuilder.systemException("Could not get MarketQuery V3 " + e.toString(), 0);
        }

        //throw ExceptionBuilder.authorizationException("Not Implemented MarketQuery V3 ", 1);
    }

    public com.cboe.idl.cmiV3.OrderEntry getOrderEntryV3() throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            if ( userOrderEntryCorba == null )
            {
                userOrderEntryCorba = initUserOrderEntry();
            }
            return userOrderEntryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get order entry", e);
            throw ExceptionBuilder.systemException("Could not get order entry " + e.toString(), 0);
        }

    }

    public OrderQuery getOrderQueryV3() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if(userOrderQueryCorba == null)
            {
                userOrderQueryCorba = initUserOrderQuery();
            }
            return userOrderQueryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get order query", e);
            throw ExceptionBuilder.systemException("Could not get order query " + e.toString(), 0);
        }
    }

    public com.cboe.idl.cmiV3.Quote getQuoteV3() throws SystemException, CommunicationException, AuthorizationException
    {
        try {
            if ( userQuoteQueryCorba == null )
            {
                userQuoteQueryCorba = initUserQuoteQuery();
            }
            return userQuoteQueryCorba;
        }
        catch (Exception e)
        {
            Log.exception(this, "Could not get quote V3", e);
            throw ExceptionBuilder.systemException("Could not get quote V3 " + e.toString(), 0);
        }
    }
    public void acceptRemoteCASCallbackRemoval(CallbackDeregistrationInfo eventData)
    {
        try
        {
            unregisterNotification(eventData);
        }
        catch(Exception e)
        {
            Log.exception(this, "Exception while processing callback removal.", e);
        }
    }

    public String getInstrumentorName()
    {
        return instrumentorName;
    }

    public boolean isTradingFirmEnabled()
        throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if(null != userEnablement)
            {
                userEnablement.verifyUserTradingFirmEnabled();
                return true;
            }
            else
                return false;
        }
        catch(UserException e)
        {
            return false;
        }
    }

    public Map<String,Integer> dependentSessions()  throws SystemException, CommunicationException, AuthorizationException
    {
        return dependentSessions;
    }

}// EOF
