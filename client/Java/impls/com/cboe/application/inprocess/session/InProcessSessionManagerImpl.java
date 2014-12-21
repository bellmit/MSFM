package com.cboe.application.inprocess.session;

import org.omg.CORBA.UserException;

import com.cboe.application.inprocess.shared.InProcessServicesHelper;
import com.cboe.application.order.OrderQueryCacheFactory;
import com.cboe.application.order.FixOrderQueryCacheFactory;
import com.cboe.application.quote.QuoteCacheFactory;
import com.cboe.application.session.SessionManagerImpl;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.AcceptTextMessageProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.domain.logout.LogoutQueue;
import com.cboe.domain.logout.LogoutQueueFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.fixUtil.FixUtilConstants;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiAdmin.MessageStruct;
import com.cboe.idl.cmiConstants.UserRoles;
import com.cboe.idl.cmiTradeMaintenanceService.TradeMaintenanceService;
import com.cboe.idl.cmiV5.Quote;
import com.cboe.idl.cmiV6.FloorTradeMaintenanceService;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.Administrator;
import com.cboe.interfaces.application.AdministratorHome;
import com.cboe.interfaces.application.ProductDefinition;
import com.cboe.interfaces.application.ProductDefinitionHome;
import com.cboe.interfaces.application.ProductQueryManager;
import com.cboe.interfaces.application.ProductQueryManagerHome;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.UserTradingParametersHome;
import com.cboe.interfaces.application.UserTradingParametersV5;
import com.cboe.interfaces.application.inprocess.FloorTradeConsumer;
import com.cboe.interfaces.application.inprocess.FloorTradeConsumerHome;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.InProcessTradingSession;
import com.cboe.interfaces.application.inprocess.InProcessTradingSessionHome;
import com.cboe.interfaces.application.inprocess.MarketQuery;
import com.cboe.interfaces.application.inprocess.MarketQueryHome;
import com.cboe.interfaces.application.inprocess.OrderEntry;
import com.cboe.interfaces.application.inprocess.OrderEntryHome;
import com.cboe.interfaces.application.inprocess.QuoteEntry;
import com.cboe.interfaces.application.inprocess.QuoteEntryHome;
import com.cboe.interfaces.application.inprocess.QuoteQuery;
import com.cboe.interfaces.application.inprocess.QuoteQueryHome;
import com.cboe.interfaces.application.inprocess.RemoteMarketQuery;
import com.cboe.interfaces.application.inprocess.RemoteMarketQueryHome;
import com.cboe.interfaces.application.inprocess.UserOrderQuery;
import com.cboe.interfaces.application.inprocess.UserOrderQueryHome;
import com.cboe.interfaces.application.inprocess.UserSessionAdminConsumer;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * @author Jing Chen
 */

public class InProcessSessionManagerImpl extends SessionManagerImpl implements InProcessSessionManager
{
    protected com.cboe.interfaces.application.inprocess.QuoteEntry inProcessQuoteEntry;
    protected com.cboe.interfaces.application.inprocess.QuoteQuery inProcessQuoteQuery;
    protected OrderEntry inProcessOrderEntry;
    protected UserOrderQuery inProcessOrderQuery;
    protected InProcessTradingSession tradingSession;
    protected ProductQueryManager productQuery;
    protected UserSessionAdminConsumer fixSessionListener;
    protected Administrator administrator;
    protected ProductDefinition productDefinition;
    protected MarketQuery inProcessMarketQuery;
    protected UserTradingParametersV5 userTradingParameters;
    // MWM - new class - needs dealing with when IPD merges
    protected RemoteMarketQuery inProcessRemoteMarketQuery;
    protected FloorTradeConsumer inProcessFloorTrade;
    private String userIOR=null;

    public InProcessSessionManagerImpl()
    {
        super();
    }

    protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey, UserSessionAdminConsumer clientListener)
        throws DataValidationException, SystemException
    {
        this.validUser = validUser;
        this.sessionId = sessionId;
        this.sessionKey = sessionKey;
        instrumentorName = validUser.userInfo.userId;
        this.fixSessionListener = clientListener;
        calcPrintName();

        StringBuilder iorBuffer = new StringBuilder(120);

        String prefix = System.getProperty("prefixAdminServer");

        iorBuffer.append(prefix);
        iorBuffer.append(":");
        iorBuffer.append(validUser.userInfo.userId);
        iorBuffer.append(":");
        iorBuffer.append(getClass().getName());
        iorBuffer.append("@");
        iorBuffer.append(Integer.toHexString(hashCode()));

        userIOR = iorBuffer.toString();

        try
        {
            // init UserEnablement Service
            userEnablement = initUserEnablement();
        }
        catch (Exception e)
        {
            // Report this better somehow
            Log.exception(this, "session : " + this, e);
        }
        acceptTextMessageProcessor = AcceptTextMessageProcessorFactory.create(this);
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, this);
        LogoutServiceFactory.find().addLogoutListener(this, this);

        // get references to the CAS singletons.
        try
        {
            forcedLogoutProcessor = initForcedLogout();
            ServicesHelper.getSubscriptionService(this).addUserInterest(this);
        }
        catch (Exception e)
        {
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
        try
        {
            inProcessQuoteEntry = initInProcessQuoteEntry();
            inProcessQuoteQuery = initInProcessQuoteQuery();
              tradingSession = initInProcessTradingSession();
               productQuery = initInProcessProductQuery();
            inProcessOrderEntry = initInProcessOrderEntry();
            inProcessOrderQuery = initInProcessOrderQuery();
            administrator = initInProcessAdministrator();
            productDefinition = initInProcessProductDefinition();
            inProcessMarketQuery = initInProcessMarketQuery();
            userTradingParameters = initInProcessUserTradingParameters();
            // MWM - new - needs dealing with when IPD merges
            inProcessRemoteMarketQuery = initInProcessRemoteMarketQuery();
            inProcessFloorTrade = initInProcessFloorTrade();
        }
        catch (Exception e)
        {
            //Report this better
            Log.exception(this, "session : " + this, e);
        }

        boolean tradingFirmEnabled = false;
        try
        {
            tradingFirmEnabled = isTradingFirmEnabled();
        }
        catch(UserException e)
        {
            Log.exception(this, "Could not get trading firm enablement for user: " + validUser.userInfo.userId, e);
            tradingFirmEnabled = false;
        }

        if(validUser.userInfo.role == UserRoles.FIRM_DISPLAY && tradingFirmEnabled)
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
    }

    protected void subscribeCBOEConsumers(SessionProfileUserStructV2 validUser)
    {
        try
        {
             // sign up for SACAS messages
            subscribeAcceptTextMessage();
            publishMessagesForUser( validUser.userInfo.userId );
        }
        catch(Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    protected void addToUserLogoutQueue()
    {
        LogoutQueue logoutQueue = LogoutQueueFactory.find(validUser.userInfo.userId);
        synchronized(logoutQueue)
        {
            logoutQueue.addLogout(this);
            logoutQueue.setWaitForEmptyLogoutQueueFlag(true);
        }
    }

    public UserSessionAdminConsumer getUserSessionAdminConsumer()
    {
        return fixSessionListener;
    }

    public com.cboe.interfaces.application.inprocess.QuoteEntry initInProcessQuoteEntry() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            QuoteEntryHome home = InProcessServicesHelper.getQuoteEntryHome();
            com.cboe.interfaces.application.inprocess.QuoteEntry quoteEntry = home.create(this);
            return quoteEntry;
        }
        catch( Exception poae )
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Quote Entry", 1);
        }
    }

    public com.cboe.interfaces.application.inprocess.QuoteQuery initInProcessQuoteQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            QuoteQueryHome home = InProcessServicesHelper.getQuoteQueryHome();
            com.cboe.interfaces.application.inprocess.QuoteQuery quoteQuery = home.create(this);
            return quoteQuery;
        }
        catch( Exception poae )
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Quote Query", 1);
        }
    }

    public OrderEntry initInProcessOrderEntry() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            OrderEntryHome home = InProcessServicesHelper.getOrderEntryHome();
            OrderEntry inProcessOrder = home.create(this);
            return inProcessOrder;
        }
        catch( Exception poae )
        {
            throw ExceptionBuilder.systemException("Could not find InProcess OrderEntry", 1);
        }
    }

    public UserOrderQuery initInProcessOrderQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            UserOrderQueryHome home = InProcessServicesHelper.getOrderQueryHome();
            UserOrderQuery inProcessOrder = home.create(this);
            return inProcessOrder;
        }
        catch( Exception poae )
        {
            Log.exception(this, " session : " + this + " Will throw system exception :- ", poae);
            throw ExceptionBuilder.systemException("Could not find InProcess OrderQuery", 1);
        }
    }

    public ProductQueryManager initInProcessProductQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            ProductQueryManagerHome home = ServicesHelper.getProductQueryManagerHome();
            ProductQueryManager productQuery = home.create(this);
            return productQuery;
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Product Query", 1);
        }
    }

    public InProcessTradingSession initInProcessTradingSession() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            InProcessTradingSessionHome home = InProcessServicesHelper.getTradingSessionHome();
            InProcessTradingSession tradingSession = home.create(this, fixSessionListener);
            return tradingSession;
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Trading Session", 1);
        }
    }

    public Administrator initInProcessAdministrator() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            AdministratorHome home = ServicesHelper.getAdministratorHome();
            Administrator administrator = home.create(this);
            return administrator;
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Administrator", 1);
        }
    }

    public ProductDefinition initInProcessProductDefinition() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            ProductDefinitionHome home = ServicesHelper.getProductDefinitionHome();
            ProductDefinition productDefinition = home.create(this);
            return productDefinition;
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Product Definition", 1);
        }
    }

    // MWM - new - needs dealing with when IPD merges
    public String getUserSessionIor()
    {
        return userIOR;
    }

    public RemoteMarketQuery initInProcessRemoteMarketQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            RemoteMarketQueryHome home = InProcessServicesHelper.getRemoteMarketQueryHome();
            RemoteMarketQuery inProcessRemoteMarketQuery = home.create(this);
            return inProcessRemoteMarketQuery;
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Market Query", 1);
        }
    }

    public MarketQuery initInProcessMarketQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            MarketQueryHome home = InProcessServicesHelper.getMarketQueryHome();
            MarketQuery inProcessMarketQuery = home.create(this);
            return inProcessMarketQuery;
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Market Query", 1);
        }
    }

    private UserTradingParametersV5 initInProcessUserTradingParameters() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            UserTradingParametersHome home = ServicesHelper.getUserTradingParametersHome();
//            UserTradingParameters inProcessUserTradingParameters = home.create(this);
            return home.create(this);
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find InProcess Trading Parameters", 1);
        }
    }

    public UserTradingParametersV5 getInProcessUserTradingParameters() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if (userTradingParameters == null)
            {
                userTradingParameters = initInProcessUserTradingParameters();
            }
            return userTradingParameters;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get InProcess user trading parameters " + e.toString(), 0);
        }
    }

    public ProductQueryManager getInProcessProductQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( productQuery == null )
            {
                productQuery = initInProcessProductQuery();
            }
            return productQuery;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get InProcess product query " + e.toString(), 0);
        }
    }

    public InProcessTradingSession getInProcessTradingSession() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( tradingSession == null)
            {
                tradingSession = initInProcessTradingSession();
            }
            return tradingSession;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get fix trading session " + e.toString(), 0);
        }
    }

    public QuoteEntry getInProcessQuoteEntry() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( inProcessQuoteEntry == null )
            {
                inProcessQuoteEntry = initInProcessQuoteEntry();
            }
            return inProcessQuoteEntry;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get inProcessQuoteEntry " + e.toString(), 0);
        }
    }

    public QuoteQuery getInProcessQuoteQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( inProcessQuoteQuery == null )
            {
                inProcessQuoteQuery = initInProcessQuoteQuery();
            }
            return inProcessQuoteQuery;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get inProcessQuoteQuery " + e.toString(), 0);
        }
    }

    public OrderEntry getInProcessOrderEntry() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( inProcessOrderEntry == null )
            {
                inProcessOrderEntry = initInProcessOrderEntry();
            }
            return inProcessOrderEntry;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get inProcessOrderEntry" + e.toString(), 0);
        }
    }

    public UserOrderQuery getInProcessOrderQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( inProcessOrderQuery == null )
            {
                inProcessOrderQuery = initInProcessOrderQuery();
            }
            return inProcessOrderQuery;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get inProcessOrderQuery" + e.toString(), 0);
        }
    }

    public Administrator getInProcessAdministrator() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( administrator == null )
            {
                administrator = initInProcessAdministrator();
            }
            return administrator;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get fix adminitrator " + e.toString(), 0);
        }
    }

    public ProductDefinition getInProcessProductDefinition() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( productDefinition == null )
            {
                productDefinition = initInProcessProductDefinition();
            }
            return productDefinition;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get fix product definition " + e.toString(), 0);
        }
    }

    // MWM - new
    public RemoteMarketQuery getInProcessRemoteMarketQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( inProcessRemoteMarketQuery == null )
            {
                inProcessRemoteMarketQuery = initInProcessRemoteMarketQuery();
            }
            return inProcessRemoteMarketQuery;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get inProcess remote market query " + e.toString(), 0);
        }
    }

    public FloorTradeConsumer getInProcessFloorTrade() throws SystemException, CommunicationException, AuthorizationException {
        try { 
            if ( inProcessFloorTrade == null ) {
                inProcessFloorTrade = initInProcessFloorTrade();
            }
            return inProcessFloorTrade;
        }
        catch (Exception e) {
            throw ExceptionBuilder.systemException("Could not get inProcess floor trade " + e.toString(), 0);
        }
    }

    public FloorTradeConsumer initInProcessFloorTrade() throws SystemException, CommunicationException, AuthorizationException {
        try {

            FloorTradeConsumerHome home = InProcessServicesHelper.getFloorTradeMaintenanceServiceHome();

            FloorTradeConsumer floorTrade = home.create(this);
            return floorTrade;
        }
        catch( Exception e ) {
            throw ExceptionBuilder.systemException("Could not find InProcess Floor Trade", 1);
        }
    }

    public FloorTradeMaintenanceService getFloorTradeMaintenanceService() 
        throws SystemException, CommunicationException, AuthorizationException { 
        try { 
            if ( inProcessFloorTrade == null ) {
                inProcessFloorTrade = initInProcessFloorTrade();
            }
            return (FloorTradeMaintenanceService) inProcessFloorTrade;
        }
        catch (Exception e) {
            throw ExceptionBuilder.systemException("Could not get inProcess floor trade " + e.toString(), 0);
        }
    }

    public MarketQuery getInProcessMarketQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( inProcessMarketQuery == null )
            {
                inProcessMarketQuery = initInProcessMarketQuery();
            }
            return inProcessMarketQuery;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get inProcess market query " + e.toString(), 0);
        }
    }

    protected void remove(SessionProfileUserStructV2 validUser, SessionManager session)
    {
        try
        {
            InProcessServicesHelper.getSessionManagerHome().remove(this, validUser.userInfo.userId);
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    public void acceptForcedLogout( int key, String message )
    {
        StringBuilder calling = new StringBuilder(60);
        calling.append("calling acceptForcedLogout for ").append(this);
        Log.notification(this, calling.toString());
        // don't bother reporting logout or re-logging out if already in a logout process.
        if ((!ifLoggingOut) && (!lostConnection))
        {

            forcedLogoff = true;
            addToUserLogoutQueue();
            try
            {
                fixSessionListener.acceptLogout(message);
            }
            catch (Exception e)
            {
                Log.exception(this, "session : " + this, e);
            }
            try
            {
                logout();
            }
            catch(Exception e)
            {
                Log.exception(this, "session : " + this + " : Logout Failure", e);
            }
        }
    }

    protected void cleanupProcessors()
    {
        try
        {
            acceptTextMessageProcessor.setParent(null);
            acceptTextMessageProcessor = null;
            forcedLogoutProcessor.setParent(null);
            forcedLogoutProcessor = null;
            logoutProcessor.setParent(null);
            logoutProcessor = null;
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }


    protected void cleanUpUserCaches()
    {
        QuoteCacheFactory.remove(validUser.userInfo.userId);
        if (FixUtilConstants.CLIENT_TYPES.FIXCAS.equals(System.getenv(FixUtilConstants.CLIENT_TYPES.CLIENT_TYPE))){
            FixOrderQueryCacheFactory.remove(validUser.userInfo.userId);
        } else {
            OrderQueryCacheFactory.remove(validUser.userInfo.userId);
        }
        ServicesHelper.getUserEnablementHome().remove(validUser.userInfo.userId
                                                      , validUser.userInfo.userAcronym.exchange
                                                      , validUser.userInfo.userAcronym.acronym);
    }

    public void acceptTextMessage( MessageStruct message )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptTextMessage for " + this);
        }
        fixSessionListener.acceptTextMessage(message);
    }

    synchronized public void lostConnection(ChannelListener channelListener)
         throws SystemException, CommunicationException, AuthorizationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling lostConnection for " + this);
        }
        if ((!ifLoggingOut) && (!lostConnection))
        {
            lostConnection = true;
            try
            {
                fixSessionListener.acceptLogout("Forced logout due to loss of connection.");
                StringBuilder force = new StringBuilder(140);
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
                    Log.alarm(this, "session : " + this + "logout failed in lostConnection: " + e.toString());
                }
            }
        }
    }

    public void publishLogout()
    {
        StringBuilder calling = new StringBuilder(60);
        calling.append("calling publishLogout for sessionManager:").append(this);
        Log.information(this, calling.toString());
        ServicesHelper.getRemoteCASSessionManagerPublisher().logout(
            ServicesHelper.getAppServerStatusManager().getProcessName(),
            userIOR,
            validUser.userInfo.userId);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, this, this);
        EventChannelAdapterFactory.find().dispatch(event);
    }

    public void acceptUserSessionLogout()
    {
        try
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "calling acceptUserSessionLogout for " + this);
            }
            LogoutServiceFactory.find().logoutComplete(this, this);
            remove(validUser, this);
            ServicesHelper.getSubscriptionServiceHome().remove(this);
            EventChannelAdapterFactory.find().removeListenerGroup(this);
            cleanupUserSuppliers();
            cleanUpUserCaches();
            cleanUpUserServices();
            cleanupProcessors();
            fixSessionListener = null;
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    /* -------------------------------------------------------------------
     * The following null implementations were added to satisfy changes to the SessionManager interface
     * -------------------------------------------------------------------
     */

   
    public com.cboe.idl.cmiV6.OrderQuery getOrderQueryV6() throws SystemException, CommunicationException, AuthorizationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public com.cboe.idl.cmiV5.OrderEntry getOrderEntryV5() throws SystemException, CommunicationException, AuthorizationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public com.cboe.idl.cmiV5.UserTradingParameters getUserTradingParametersV5() throws SystemException, CommunicationException, AuthorizationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public Quote getQuoteV5() throws SystemException, CommunicationException, AuthorizationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public TradeMaintenanceService getTradeMaintenanceService() throws SystemException, CommunicationException, AuthorizationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public com.cboe.idl.cmiV4.MarketQuery getMarketQueryV4() throws SystemException, CommunicationException, AuthorizationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    public String toString() {
    	return printName;
    }
}
