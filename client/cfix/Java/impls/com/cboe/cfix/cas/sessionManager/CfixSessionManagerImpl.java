/**
 * @author Jing Chen
 */
package com.cboe.cfix.cas.sessionManager;

import com.cboe.application.session.*;
import com.cboe.application.shared.*;
import com.cboe.application.shared.consumer.*;
import com.cboe.application.supplier.*;
import com.cboe.cfix.cas.shared.*;
import com.cboe.cfix.cas.supplier.*;
import com.cboe.domain.logout.*;
import com.cboe.exceptions.*;
import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.securityService.*;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.application.TradingSession;
import com.cboe.interfaces.application.TradingSessionHome;
import com.cboe.interfaces.cfix.*;
import com.cboe.interfaces.domain.session.*;
import com.cboe.util.*;
import com.cboe.util.channel.*;
import com.cboe.util.event.*;

public class CfixSessionManagerImpl extends SessionManagerImpl
    implements CfixSessionManager, HeartBeatCollector, ForcedLogoutCollector,
        UserTimeoutWarningCollector, AcceptTextMessageCollector, UserSessionLogoutCollector
{
    protected CfixMarketDataQueryIF cfixMarketDataQuery;
    protected CfixMDXMarketDataQueryIF cfixMDXMarketDataQuery;
    protected TradingSession cfixTradingSession;
    protected ProductQueryManager cfixProductQuery;
    protected CfixUserSessionAdminConsumer cfixSessionListener;
    protected CfixUserSessionAdminSupplier cfixAdminSupplier;
    protected boolean isMDXEnabled = false;

    public CfixSessionManagerImpl()
    {
        super();
        cfixMarketDataQuery = null;
    }


    /**
     * Initializes the session manager.
     * @param validUser the ValidUserStruct containing the connecting users
     * member/firm information, priviledges and preferences.
     * @param ifLazyInitialization boolean indicator indicating if all
     * services should be initialized
     */
    protected synchronized void initialize(SessionProfileUserStructV2 validUser, String sessionId, boolean ifLazyInitialization,
                    CfixUserSessionAdminConsumer clientListener, short sessionType, boolean gmdTextMessaging)
        throws DataValidationException, SystemException
    {
        this.isMDXEnabled = CfixServicesHelper.getMDXEnabled();
        if(Log.isDebugOn())
        {
            Log.debug(this, "In CfixSessionManagerImpl: After call to getMDXEnabled() property from ServicesHelper. CFIX MDX Enabled is : " + isMDXEnabled);
        }

        this.validUser = validUser;
        this.sessionId = sessionId;
        this.sessionType = sessionType;
        this.cfixSessionListener = clientListener;
        calcPrintName();
        instrumentorName = validUser.userInfo.userId;
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
        cfixAdminSupplier = CfixUserSessionAdminSupplierFactory.create(this);
        cfixAdminSupplier.setDynamicChannels(true);
        subscribeIECConsumersForCfixSession(this, validUser, clientListener, gmdTextMessaging);
        acceptTextMessageProcessor = AcceptTextMessageProcessorFactory.create(this);
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, this);
        LogoutServiceFactory.find().addLogoutListener(this, this);

        // get references to the CAS singletons.
        try
        {
            forcedLogoutProcessor = initForcedLogout();
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
        if ( !ifLazyInitialization )
        {
            try
            {
                if(isMDXEnabled)
                    cfixMDXMarketDataQuery = initCfixMDXMarketDataQuery();
                else
                    cfixMarketDataQuery = initCfixMarketDataQuery();

                cfixTradingSession = initCfixTradingSession();
               	cfixProductQuery = initCfixProductQuery();
            }
            catch (Exception e)
            {
                //Report this better
                Log.exception(this, "session : " + this, e);
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

    protected void subscribeIECConsumersForCfixSession(BaseSessionManager sessionManager, SessionProfileUserStructV2 validUser, CfixUserSessionAdminConsumer clientListener, boolean gmdTextMessaging)
        throws DataValidationException
    {
        ChannelListener proxyListener = CfixServicesHelper.getCfixSessionAdminConsumerProxy(clientListener, sessionManager, gmdTextMessaging);
        if (Log.isDebugOn())
        {
            Log.debug(this, "Subscribe IEC using proxy listener = " + proxyListener + " GMD Status = " + gmdTextMessaging
                    + " session = " + sessionManager);
        }
        ChannelKey channelKey = new ChannelKey(ChannelType.CB_LOGOUT, validUser.userInfo.userId);
        cfixAdminSupplier.addChannelListener(this, proxyListener, channelKey);

        channelKey = new ChannelKey(ChannelType.CB_TEXT_MESSAGE, validUser.userInfo.userId);
        cfixAdminSupplier.addChannelListener(this, proxyListener, channelKey);
    }

    public CfixMarketDataQueryIF initCfixMarketDataQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        if(isMDXEnabled)
            throw ExceptionBuilder.systemException("CFIX is not IEC enabled. Requesting the wrong Market Data Query", 1);
        try
        {
            CfixMarketDataQueryProxyHome home = CfixServicesHelper.getCfixMarketDataQueryHome();
            CfixMarketDataQueryIF marketDataQuery = home.create(this);
            return marketDataQuery;
        }
        catch( Exception poae )
        {
            throw ExceptionBuilder.systemException("Could not find Cfix Market Query", 1);
        }
    }

    public CfixMDXMarketDataQueryIF initCfixMDXMarketDataQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        if(!isMDXEnabled)
            throw ExceptionBuilder.systemException("CFIX is not MDX enabled. Requesting the wrong Market Data Query", 1);
        try
        {
            CfixMDXMarketDataQueryProxyHome home = CfixServicesHelper.getCfixMDXMarketDataQueryHome();
            CfixMDXMarketDataQueryIF marketDataQueryMDX = home.create(this);
            return marketDataQueryMDX;
        }
        catch( Exception poae )
        {
            throw ExceptionBuilder.systemException("Could not find Cfix MDX Market Query", 1);
        }
    }

    public ProductQueryManager initCfixProductQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            ProductQueryManagerHome home = ServicesHelper.getProductQueryManagerHome();
            ProductQueryManager productQuery = home.create(this);
            return productQuery;
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find Cfix Product Query", 1);
        }
    }

    public TradingSession initCfixTradingSession() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            TradingSessionHome home = ServicesHelper.getTradingSessionHome();
	        TradingSession tradingSession = home.create(this);
	        return tradingSession;
        }
        catch( Exception e)
        {
            throw ExceptionBuilder.systemException("Could not find Cfix Trading Session", 1);
        }
    }

    public ProductQueryManager getCfixProductQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( cfixProductQuery == null )
            {
                cfixProductQuery = initCfixProductQuery();
            }
            return cfixProductQuery;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get cfix product query " + e.toString(), 0);
        }
    }

    public TradingSession getCfixTradingSession() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( cfixTradingSession == null)
            {
                cfixTradingSession = initCfixTradingSession();
            }
            return cfixTradingSession;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get cfix trading session " + e.toString(), 0);
        }
    }

    public CfixMarketDataQueryIF getCfixMarketDataQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( cfixMarketDataQuery == null )
            {
                cfixMarketDataQuery = initCfixMarketDataQuery();
            }
            return cfixMarketDataQuery;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get cfix market query " + e.toString(), 0);
        }
    }

    public CfixMDXMarketDataQueryIF getCfixMDXMarketDataQuery() throws SystemException, CommunicationException, AuthorizationException
    {
        try
        {
            if ( cfixMDXMarketDataQuery == null )
            {
                cfixMDXMarketDataQuery = initCfixMDXMarketDataQuery();
            }
            return cfixMDXMarketDataQuery;
        }
        catch (Exception e)
        {
            throw ExceptionBuilder.systemException("Could not get cfix MDX market query " + e.toString(), 0);
        }
    }

    protected void remove(SessionProfileUserStructV2 validUser, SessionManager session)
    {
        try
        {
            CfixServicesHelper.getCfixSessionManagerHome().remove(this, validUser.userInfo.userId);
        }
        catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    protected void cleanupUserSuppliers()
    {
        try
        {
            CfixUserSessionAdminSupplierFactory.remove(this);
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
            try
            {
                cfixSessionListener.acceptLogout(message);
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

    public void acceptUserSessionLogout()
    {
        try {
            if (Log.isDebugOn())
            {
                Log.debug(this, "calling acceptUserSessionLogout for " + this);
            }
            LogoutServiceFactory.find().logoutComplete(this, this);
            remove(validUser, this);
            UserSessionAdminSupplierFactory.remove(this);
            ServicesHelper.getSubscriptionServiceHome().remove(this);
            EventChannelAdapterFactory.find().removeListenerGroup(this);
            cfixAdminSupplier.removeListenerGroup(this);
            cleanupUserSuppliers();
            cleanUpUserServices();
            cleanupProcessors();
            ServicesHelper.getUserEnablementHome().remove(validUser.userInfo.userId
                                                          ,validUser.userInfo.userAcronym.exchange
                                                          ,validUser.userInfo.userAcronym.acronym);
            ServicesHelper.getUserSessionThreadPoolHome().remove(this);
            cfixSessionListener = null;
        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    synchronized public void logout()
              throws SystemException, CommunicationException, AuthorizationException
    {
        StringBuilder calling = new StringBuilder(70);
        calling.append("calling logout for ").append(this).append(" : ").append(ifLoggingOut);
        Log.notification(this, calling.toString());
        // set the logging out flag so that no further use request will be processed
        if (!ifLoggingOut )
        {
            try
            {
                ifLoggingOut = true;
            }
            finally
            {
                try
                {
                    SecurityService security = FoundationFramework.getInstance().getSecurityService();
                    security.removeSessionFromCache(sessionId);
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Removed user session " + sessionKey + " from security (sec. sess id \"" + sessionId + "\")");
                    }
                }
                catch (Exception ex)
                {
                    Log.alarm(this, "Failed to remove user session " + sessionKey + " from security using security session id \"" + sessionId + "\")");
                }
                if (Log.isDebugOn())
                {
                    Log.debug(this, "session : " + this + " : user logging out: " + validUser.userInfo.userId);
                }
                publishLogout();
            }
        }
    }
}
