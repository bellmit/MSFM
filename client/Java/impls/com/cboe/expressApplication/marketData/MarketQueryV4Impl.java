//
// -----------------------------------------------------------------------------------
// Source file: MarketQueryV4Impl.java
//
// PACKAGE: com.cboe.expressApplication.marketData
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.expressApplication.marketData;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;
import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.expressApplication.ExpressMarketQuery;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.SessionManagerHome;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.SubscriptionFields;
import com.cboe.interfaces.application.UserMarketDataService;
import com.cboe.interfaces.application.UserEnablement;

import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;

// there is no "local" CurrentMarketV4 -- only "remote", so just have to publish the subscriptions; no need to implement CurrentMarketV4Collector or EventChannelListener
public class MarketQueryV4Impl extends BObject implements ExpressMarketQuery, UserSessionLogoutCollector, SubscriptionFields
{
    protected UserSessionLogoutProcessor logoutProcessor;
    protected String userId;

    private SessionManager currentSession;
    private String userIor;

    protected UserMarketDataService userMarketQuery;

    protected UserEnablement userEnablementService;

    public MarketQueryV4Impl()
    {
        super();
    }

    public void initialize() throws Exception
    {
    }

    /**
     * sets the V4 session manager for the market data class and creates a new helper
     * @param session SessionManagerV4
     */
    public void setSessionManager(SessionManager session)
    {
        currentSession = session;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);

        try
        {
            this.userId = currentSession.getValidSessionProfileUser().userId;
            // Since the instance of SessionManagerHome is a singleton in CAS process, 
            // replace SessionManagerV4Home with SessionManagerHome to break the dependence.
            // ServicesHelper.getSessionManagerHome() should always return the singleton, 
            // which is the latest version of SessionManagerHome 
            SessionManagerHome sessionManagerHome = ServicesHelper.getSessionManagerHome();
            if(sessionManagerHome != null)
            {
                this.userIor = sessionManagerHome.getUserSessionIor(currentSession);
            }
            /*SessionManagerV4Home sessionManagerV4Home = ServicesHelper.getSessionManagerV4Home();
            if(sessionManagerV4Home != null)
            {
                this.userIor = sessionManagerV4Home.getUserSessionIor(currentSession);
            }*/
        }
        catch(Exception e)
        {
            Log.exception(this, "session : " + currentSession, e);
        }
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + currentSession);
        }
        ServicesHelper.getV4MarketDataRequestPublisher().removeMarketDataRequestSource(this);

        // Do any individual service clean up needed for logout
        InstrumentedEventChannelAdapterFactory.find().removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        InstrumentedEventChannelAdapterFactory.find().removeChannel(this);
        EventChannelAdapterFactory.find().removeChannel(this);
        LogoutServiceFactory.find().logoutComplete(currentSession, this);

        logoutProcessor.setParent(null);
        logoutProcessor = null;
        currentSession = null;
        userId = null;
    }

// CURRENT_MARKET
    public void subscribeCurrentMarket(int classKey, CMICurrentMarketConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarket for classKey " + classKey);
        }

        if(clientListener != null)
        {
            boolean externalMDXEnabled = isExternalMarketDataEnabled();

            ServicesHelper.getV4MarketDataRequestPublisher().publishCurrentMarketV4Subscription(this,
                                                                                                userId,
                                                                                                userIor,
                                                                                                classKey,
                                                                                                clientListener,
                                                                                                actionOnQueue,
                                                                                                externalMDXEnabled);

            String smgr = currentSession.toString();
            String listenerClass = clientListener.getClass().getName();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerClass.length()+45);
            // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
            suboid.append("Sub:oid for ").append(smgr)
                  .append(" ").append(listenerClass).append("@").append(Integer.toHexString(clientListener.hashCode()))
                  .append(" classKey:").append(classKey);
            Log.information(this, suboid.toString());
        }
    }

    public void unsubscribeCurrentMarket(int classKey, CMICurrentMarketConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarket for classKey " + classKey);
        }

        ServicesHelper.getV4MarketDataRequestPublisher().publishCurrentMarketV4Unsubscription(this,
                                                                                              userId,
                                                                                              userIor,
                                                                                              classKey,
                                                                                              clientListener);

        String smgr = currentSession.toString();
        String listenerClass = clientListener.getClass().getName();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerClass.length()+45);
        // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
        unsuboid.append("UnSub:oid for ").append(smgr)
                .append(" ").append(listenerClass).append("@").append(Integer.toHexString(clientListener.hashCode()))
                .append(" classKey:").append(classKey);
        Log.information(this, unsuboid.toString());
    }

    public void subscribeTicker(int classKey, CMITickerConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTicker for classKey " + classKey);
        }

        if(clientListener != null)
        {
            boolean externalMDXEnabled = isExternalMarketDataEnabled();

            ServicesHelper.getV4MarketDataRequestPublisher().publishTickerV4Subscription(this,
                                                                                         userId,
                                                                                         userIor,
                                                                                         classKey,
                                                                                         clientListener,
                                                                                         actionOnQueue,
                                                                                         externalMDXEnabled);

            String smgr = currentSession.toString();
            String listenerName = clientListener.getClass().getName();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+45);
            // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
            suboid.append("Sub:oid for ").append(smgr)
                  .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                  .append(" classKey:").append(classKey);
            Log.information(this, suboid.toString());
        }
    }

    public void unsubscribeTicker(int classKey, CMITickerConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTicker for classKey " + classKey);
        }

        ServicesHelper.getV4MarketDataRequestPublisher().publishTickerV4Unsubscription(this,
                                                                                       userId,
                                                                                       userIor,
                                                                                       classKey,
                                                                                       clientListener);

        String smgr = currentSession.toString();
        String listenerName = clientListener.getClass().getName();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+45);
        // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
        unsuboid.append("UnSub:oid for ").append(smgr)
                .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                .append(" classKey:").append(classKey);
        Log.information(this, unsuboid.toString());
    }

    public void subscribeRecap(int classKey, CMIRecapConsumer clientListener, short actionOnQueue)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecap for classKey " + classKey);
        }

        if(clientListener != null)
        {
            boolean externalMDXEnabled = isExternalMarketDataEnabled();

            ServicesHelper.getV4MarketDataRequestPublisher().publishRecapV4Subscription(this,
                                                                                        userId,
                                                                                        userIor,
                                                                                        classKey,
                                                                                        clientListener,
                                                                                        actionOnQueue,
                                                                                        externalMDXEnabled);

            String smgr = currentSession.toString();
            String listenerName = clientListener.getClass().getName();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+45);
            // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
            suboid.append("Sub:oid for ").append(smgr)
                  .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                  .append(" classKey:").append(classKey);
            Log.information(this, suboid.toString());
        }
    }

    public void unsubscribeRecap(int classKey, CMIRecapConsumer clientListener)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecap for classKey " + classKey);
        }

        ServicesHelper.getV4MarketDataRequestPublisher().publishRecapV4Unsubscription(this,
                                                                                      userId,
                                                                                      userIor,
                                                                                      classKey,
                                                                                      clientListener);

        String smgr = currentSession.toString();
        String listenerName = clientListener.getClass().getName();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+45);
        // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
        unsuboid.append("UnSub:oid for ").append(smgr)
                .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                .append(" classKey:").append(classKey);
        Log.information(this, unsuboid.toString());
    }
    
    public void subscribeNBBO(int classKey, CMINBBOConsumer clientListener, short actionOnQueue)
    		throws SystemException, CommunicationException, AuthorizationException, DataValidationException
	{
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBO for classKey " + classKey);
        }
		
		if(clientListener != null)
		{		
		    ServicesHelper.getV4MarketDataRequestPublisher().publishNBBOV4Subscription(this,
		                                                                                userId,
		                                                                                userIor,
		                                                                                classKey,
		                                                                                clientListener,
		                                                                                actionOnQueue);

            String smgr = currentSession.toString();
            String listenerName = clientListener.getClass().getName();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+45);
            // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
            suboid.append("Sub:oid for ").append(smgr)
                  .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                  .append(" classKey:").append(classKey);
            Log.information(this, suboid.toString());
		}
	}

	public void unsubscribeNBBO(int classKey, CMINBBOConsumer clientListener)
	    throws SystemException, CommunicationException, AuthorizationException, DataValidationException
	{
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBO for classKey " + classKey);
        }
		
		ServicesHelper.getV4MarketDataRequestPublisher().publishNBBOV4Unsubscription(this,
		                                                                              userId,
		                                                                              userIor,
		                                                                              classKey,
		                                                                              clientListener);

        String smgr = currentSession.toString();
        String listenerName = clientListener.getClass().getName();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+45);
        // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
        unsuboid.append("UnSub:oid for ").append(smgr)
                .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                .append(" classKey:").append(classKey);
        Log.information(this, unsuboid.toString());
	}
    protected UserEnablement getUserEnablementService()
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if(userEnablementService == null)
        {
            userEnablementService = ServicesHelper.getUserEnablementService(currentSession.getUserId(),
                                                                            currentSession.getValidSessionProfileUserV2().userInfo.userAcronym.exchange,
                                                                            currentSession.getValidSessionProfileUserV2().userInfo.userAcronym.acronym);
        }
        return userEnablementService;
    }

    // return true if the user is enabled for *external* MDX data
    protected boolean isExternalMarketDataEnabled()
    {
        boolean retVal;
        try
        {
            getUserEnablementService().verifyUserMDXEnabled();
            retVal = true;
        }
        catch(UserException e)
        {
            retVal = false;
        }
        return retVal;
    }

	public void subscribeCurrentMarketByProduct(int classKey, int productKey, CurrentMarketManualQuoteConsumer clientListener, short actionOnQueue) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketByProduct for classKey:productKey " + classKey + ":" + productKey);
        }

        if(clientListener != null)
        {
            boolean externalMDXEnabled = isExternalMarketDataEnabled();

            ServicesHelper.getV4MarketDataRequestPublisher().publishCurrentMarketV4SubscriptionForProduct(this,
                                                                                                userId,
                                                                                                userIor,
                                                                                                classKey,
                                                                                                productKey,
                                                                                                clientListener,
                                                                                                actionOnQueue,
                                                                                                externalMDXEnabled);

            String smgr = currentSession.toString();
            String listenerName = clientListener.getClass().getName();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+60);
            // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
            suboid.append("Sub:oid for ").append(smgr)
                  .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                  .append(" classKey:productKey ").append(classKey).append(":").append(productKey);
            Log.information(this, suboid.toString());
        }
		
	}
    
   

	public void unsubscribeCurrentMarketByProduct(int classKey, int productKey, CurrentMarketManualQuoteConsumer clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketByProduct for classKey:productKey " + classKey + ":" + productKey);
        }

        ServicesHelper.getV4MarketDataRequestPublisher().publishCurrentMarketV4UnsubscriptionForProduct(this,
                                                                                              userId,
                                                                                              userIor,
                                                                                              classKey,
                                                                                              productKey,
                                                                                              clientListener);

        String smgr = currentSession.toString();
        String listenerName = clientListener.getClass().getName();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+60);
        // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
        unsuboid.append("UnSub:oid for ").append(smgr)
                .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                .append(" classKey:productKey ").append(classKey).append(":").append(productKey);
        Log.information(this, unsuboid.toString());

	}

	public void subscribeTickerByProduct(int classKey, int productKey, CMITickerConsumer clientListener, short actionOnQueue) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTickerByProduct for classKey:productKey " + classKey + ":" + productKey);
        }

        if(clientListener != null)
        {
            boolean externalMDXEnabled = isExternalMarketDataEnabled();

            ServicesHelper.getV4MarketDataRequestPublisher().publishTickerV4SubscriptionForProduct(this,
                                                                                         userId,
                                                                                         userIor,
                                                                                         classKey,
                                                                                         productKey,
                                                                                         clientListener,
                                                                                         actionOnQueue,
                                                                                         externalMDXEnabled);

            String smgr = currentSession.toString();
            String listenerName = clientListener.getClass().getName();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+60);
            // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
            suboid.append("Sub:oid for ").append(smgr)
                  .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                  .append(" classKey:productKey ").append(classKey).append(":").append(productKey);
            Log.information(this, suboid.toString());
        }
		
	}

	public void unsubscribeTickerByProduct(int classKey, int productKey, CMITickerConsumer clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTickerByProduct for classKey:productKey " + classKey + ":" + productKey);
        }

        ServicesHelper.getV4MarketDataRequestPublisher().publishTickerV4UnsubscriptionForProduct(this,
                                                                                       userId,
                                                                                       userIor,
                                                                                       classKey,
                                                                                       productKey,
                                                                                       clientListener);

        String smgr = currentSession.toString();
        String listenerName = clientListener.getClass().getName();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+60);
        // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
        unsuboid.append("UnSub:oid for ").append(smgr)
                .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                .append(" classKey:productKey").append(classKey).append(":").append(productKey);
        Log.information(this, unsuboid.toString());
	}

	public void subscribeRecapByProduct(int classKey, int productKey, CMIRecapConsumer clientListener, short actionOnQueue) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecap for classKey:productKey " + classKey + ":" + productKey);
        }

        if(clientListener != null)
        {
            boolean externalMDXEnabled = isExternalMarketDataEnabled();

            ServicesHelper.getV4MarketDataRequestPublisher().publishRecapV4SubscriptionForProduct(this,
                                                                                        userId,
                                                                                        userIor,
                                                                                        classKey,
                                                                                        productKey,
                                                                                        clientListener,
                                                                                        actionOnQueue,
                                                                                        externalMDXEnabled);

            String smgr = currentSession.toString();
            String listenerName = clientListener.getClass().getName();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+45);
            // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
            suboid.append("Sub:oid for ").append(smgr)
                  .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                  .append(" classKey:").append(classKey);
            Log.information(this, suboid.toString());
        }
		
	}

	public void unsubscribeRecapByProduct(int classKey, int productKey, CMIRecapConsumer clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapByProduct for classKey:productKey " + classKey + ":" + productKey);
        }

        ServicesHelper.getV4MarketDataRequestPublisher().publishRecapV4UnsubscriptionForProduct(this,
                                                                                      userId,
                                                                                      userIor,
                                                                                      classKey,
                                                                                      productKey,
                                                                                      clientListener);

        String smgr = currentSession.toString();
        String listenerName = clientListener.getClass().getName();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+60);
        // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
        unsuboid.append("UnSub:oid for ").append(smgr)
                .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                .append(" classKey:productKey ").append(classKey).append(":").append(productKey);
        Log.information(this, unsuboid.toString());
	}

	public void subscribeNBBOByProduct(int classKey, int productKey, CMINBBOConsumer clientListener, short actionOnQueue) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBO for classKey:productKey " + classKey + ":" + productKey);
        }
		
		if(clientListener != null)
		{		
		    ServicesHelper.getV4MarketDataRequestPublisher().publishNBBOV4SubscriptionForProduct(this,
		                                                                                userId,
		                                                                                userIor,
		                                                                                classKey,
		                                                                                productKey,
		                                                                                clientListener,
		                                                                                actionOnQueue);
		
            String smgr = currentSession.toString();
            String listenerName = clientListener.getClass().getName();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+60);
            // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
            suboid.append("Sub:oid for ").append(smgr)
                  .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                  .append(" classKey:productKey ").append(classKey).append(":").append(productKey);
            Log.information(this, suboid.toString());
		}
		
	}

	public void unsubscribeNBBOByProduct(int classKey, int productKey, CMINBBOConsumer clientListener) throws SystemException, CommunicationException, AuthorizationException, DataValidationException {
		if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOByProduct for classKey:productKey " + classKey +  ":" + productKey);
        }
		
		ServicesHelper.getV4MarketDataRequestPublisher().publishNBBOV4UnsubscriptionForProduct(this,
		                                                                              userId,
		                                                                              userIor,
		                                                                              classKey,
		                                                                              productKey,
		                                                                              clientListener);
		
        String smgr = currentSession.toString();
        String listenerName = clientListener.getClass().getName();
        StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+60);
        // not using clientListener.toString() because it's too much info (multi-line string) for this simple logging
        unsuboid.append("UnSub:oid for ").append(smgr)
                .append(" ").append(listenerName).append("@").append(Integer.toHexString(clientListener.hashCode()))
                .append(" classKey:productKey ").append(classKey).append(":").append(productKey);
        Log.information(this, unsuboid.toString());
	}

    

	
}
