package com.cboe.remoteApplication.session;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.user.SessionProfileUserStructV2;

import com.cboe.application.shared.consumer.ForcedLogoutProcessor;
import com.cboe.application.shared.consumer.ForcedLogoutProcessorFactory;
import com.cboe.application.supplier.*;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ForcedLogoutCollector;
import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;
import com.cboe.interfaces.events.IECRemoteCASSessionManagerConsumerHome;
import com.cboe.interfaces.remoteApplication.RemoteCASMarketDataService;
import com.cboe.interfaces.remoteApplication.RemoteCASMarketDataServiceHome;
import com.cboe.interfaces.remoteApplication.RemoteCASSessionManager;
import com.cboe.remoteApplication.shared.RemoteServicesHelper;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

/**
 * @author Jing Chen
 */
public class RemoteCASSessionManagerImpl extends BObject implements RemoteCASSessionManager, EventChannelListener, ForcedLogoutCollector
{
    protected String userId;
    protected String casOrigin;
    protected String userSessionIor;
    protected RemoteCASMarketDataService marketDataService;
    protected IECRemoteCASSessionManagerConsumerHome remoteCASSessionManagerConsumerHome;
    protected ForcedLogoutProcessor forcedLogoutProcessor;
    protected String userSessionId;
    protected String instrumentorName;
    protected int sessionKey;
    protected SessionProfileUserStructV2 userInformation;

    public RemoteCASSessionManagerImpl(String ior)
    {
        this.userSessionIor = ior;
    }

    public synchronized void initialize(String userId, String casOrigin)
    {
        this.userId = userId;
        this.casOrigin = casOrigin;
        instrumentorName = InstrumentorNameHelper.createInstrumentorName(new String[]{casOrigin,userId}, this);
        listenForLogoutEvent();
        marketDataService = initRemoteCASMarketDataService();
    }

    protected void listenForLogoutEvent()
    {
        remoteCASSessionManagerConsumerHome = RemoteServicesHelper.getRemoteCASSessionManagerConsumerHome();
        ChannelKey channelKey = new ChannelKey( ChannelType.CB_LOGOUT, userId );
        forcedLogoutProcessor = ForcedLogoutProcessorFactory.create( this );
        EventChannelAdapterFactory.find().addChannelListener(this, forcedLogoutProcessor, channelKey);
        channelKey = new ChannelKey( ChannelType.MDCAS_LOGOUT, userSessionIor);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
        try
        {
            remoteCASSessionManagerConsumerHome.addFilter(channelKey);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    public String getCasOrigin()
    {
        return casOrigin;
    }

    public String getUserId()
        throws SystemException, CommunicationException, AuthorizationException
    {
        return userId;
    }

    public SessionProfileUserStructV2 getValidSessionProfileUserV2()
            throws SystemException, CommunicationException, AuthorizationException
    {
        if(userInformation == null)
        {
            try
            {
                userInformation = RemoteServicesHelper.getUserService().getSessionProfileUserInformationV2(userId);
            }
            catch(DataValidationException dve)
            {
                throw ExceptionBuilder.systemException("Unable to find session profile information for user:" + userId, DataValidationCodes.INVALID_ACCOUNT);
            }
            catch(NotFoundException nfe)
            {
                throw ExceptionBuilder.systemException("Unable to find session profile information for user:" + userId, NotFoundCodes.RESOURCE_DOESNT_EXIST);
            }
        }
        return userInformation;
    }


    private RemoteCASMarketDataService initRemoteCASMarketDataService()
    {
        RemoteCASMarketDataServiceHome marketDataServiceHome = RemoteServicesHelper.getRemoteCASMarketDataServiceHome();
        marketDataService = marketDataServiceHome.create(this);
        return marketDataService;
    }

    public RemoteCASMarketDataService getRemoteCASMarketDataService()
    {
        if(marketDataService == null)
        {
            marketDataService = initRemoteCASMarketDataService();
        }
        return marketDataService;
    }

    protected void publishLogout()
    {
        String us = this.toString();
        StringBuilder calling = new StringBuilder(us.length()+41);
        calling.append("calling publishLogout for sessionManager:").append(us);
        Log.information(this, calling.toString());
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, this, this);
        EventChannelAdapterFactory.find().dispatch(event);
        try
        {
            ChannelKey channelKey = new ChannelKey( ChannelType.MDCAS_LOGOUT, userSessionIor);
            remoteCASSessionManagerConsumerHome.removeFilter(channelKey);
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    public void lostConnection(ChannelListener channelListener)
        throws SystemException, CommunicationException, AuthorizationException
    {
    // leave it empty for now for MD CAS.
    //In the future, if there is any consumer proxy connectionLocalFatal is true in remote CAS,
    //publishing logout to regular CAS should be added here.
    }

    public void unregisterNotification(CallbackDeregistrationInfo deregistrationInfo)
        throws SystemException, CommunicationException, AuthorizationException
    {
        RemoteServicesHelper.getRemoteCASCallbackRemovalPublisher().acceptCallbackRemoval(
                casOrigin, userId, userSessionIor, deregistrationInfo.getReason(),
                deregistrationInfo.getErrorCode(), deregistrationInfo.getCallbackInformationStruct());
    }

    public void acceptForcedLogout( int key, String message )
    {
        String us = this.toString();
        StringBuilder calling = new StringBuilder(us.length()+31);
        calling.append("calling acceptForcedLogout for ").append(us);
        Log.notification(this, calling.toString());
        logout();
    }

    public String toString()
    {
        String us = super.toString();
        StringBuilder sessionInfo = new StringBuilder(us.length()+14+(userId == null ? 4 : userId.length()));
        if ( userId != null)
        {
            sessionInfo.append("user:").append(userId).append(" session:").append(us);
        } else
        {
            sessionInfo.append("user:").append("null").append(" session: ").append(us);
        }
        return sessionInfo.toString();
    }

    public int getSessionKey()
            throws SystemException, CommunicationException, AuthorizationException
    {
        if(sessionKey == 0)
        {
            try
            {
                sessionKey = FoundationFramework.getInstance().getSessionManagementService().getSessionForUser(userId);
            }
            catch(Exception e)
            {
                Log.exception(this, e);
            }
        }
        return sessionKey;
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (Log.isDebugOn())
        {
            Log.debug("RemoteCASSessionManager:" + this + " -->  ChannelUpdate " + channelKey.channelType);
        }
        if(channelKey.channelType==ChannelType.MDCAS_LOGOUT)
        {
            logout();
        }
        else
        {
            if (Log.isDebugOn())
            {
                Log.debug("Wrong Channel : " + channelKey.channelType);
            }
        }
    }

    private synchronized void logout()
    {
        publishLogout();
        RemoteServicesHelper.getRemoteCASSessionManagerHome().remove(userSessionIor);
        cleanupUserSuppliers();
        RemoteServicesHelper.getUserSessionMarketDataThreadPoolHome().remove(this);
        if(forcedLogoutProcessor != null)
        {
            forcedLogoutProcessor.setParent(null);
            forcedLogoutProcessor = null;
        }
        
        
	// Add this back later when we get a chance to do this part correctly.
         EventChannelAdapterFactory.find().removeChannel(this);
////**** The above removeChannel may need to stay commented out; Just uncommenting for a testing.      
        EventChannelAdapterFactory.find().removeChannelListener(this);
    }

    private void cleanupUserSuppliers()
    {
        try {
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
        } catch (Exception e)
        {
            Log.exception(this, "session : " + this, e);
        }
    }

    public String getUserSessionId()
    {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId)
    {
        this.userSessionId = userSessionId;
    }

    public String getInstrumentorName()
        throws SystemException, CommunicationException, AuthorizationException
    {
        return instrumentorName;
    }
}
