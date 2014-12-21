package com.cboe.application.inprocess.marketData;

import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.ORB.DelegateImpl;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.inprocess.session.InProcessSessionManagerImpl;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiCallbackV2.*;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.RemoteCASCallbackRemovalCollector;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.RemoteMarketQuery;
import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;

import java.util.HashMap;

public class RemoteMarketQueryImpl extends BObject implements RemoteMarketQuery,
        RemoteCASCallbackRemovalCollector,
        UserSessionLogoutCollector
        
{
    private InProcessSessionManager sessionManager;
    private String userId;
    private int marketDataCallbackTimeout;
    private String userIor=null;
    
    private HashMap subscriptionTrackerMap;
    private final long resubscribeInterval;
    private final int maxResubscribeAttempts;

    public RemoteMarketQueryImpl(InProcessSessionManager sessionManager, String userId, int marketDataCallbackTimeout, long resubscribeInterval, int maxResubscribeAttempts)
    {
        this.sessionManager = sessionManager;
        this.userId = userId;
        this.marketDataCallbackTimeout = marketDataCallbackTimeout;
        this.resubscribeInterval = resubscribeInterval;
        this.maxResubscribeAttempts = maxResubscribeAttempts;

        userIor = ((InProcessSessionManagerImpl)sessionManager).getUserSessionIor();

        subscriptionTrackerMap = new HashMap(11);
    }

    public void subscribeCurrentMarketForClassV2(String sessionName, int classKey, CMICurrentMarketConsumer cmiCurrentMarketConsumer, short actionOnQueue) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
       if (Log.isDebugOn())
       {
           Log.debug(this, "calling subscribeCurrentMarketForClassV2 for " + sessionName + " classkey: " + classKey);
       }
       
       if (cmiCurrentMarketConsumer == null) 
       {
           throw ExceptionBuilder.dataValidationException( "CMICurrentMarketConsumer null for session/class=" + sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
       }
       ServicesHelper.getUserMarketDataService(sessionManager).verifyUserCurrentMarketEnablement(sessionName, classKey);

       ChannelListener proxyListener = null;
       String callbackIor = getIorForCallback(cmiCurrentMarketConsumer);
       RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, true /* create if not exist */);

       synchronized(tracker)
       {
           proxyListener = tracker.getProxyListener();
           if(proxyListener == null)
           {
               com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer timeoutListener = null;
               timeoutListener = com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumerHelper.narrow(
                       (org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(cmiCurrentMarketConsumer, marketDataCallbackTimeout));
               proxyListener = ServicesHelper.getCurrentMarketV2ConsumerProxy(timeoutListener, sessionManager, actionOnQueue);
               tracker.setProxyListener(proxyListener);
               tracker.setMarketDataSubscriptionType(RemoteSubscriptionTracker.CURRENT_MARKET);
           }
           tracker.addSubscription(new SessionKeyContainer(sessionName, classKey));
           ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketSubscriptionV2(
                   this, userId, userIor, sessionName, classKey, 0, proxyListener, actionOnQueue);
       }
    }

    public void unsubscribeCurrentMarketForClassV2(String sessionName, int classKey, CMICurrentMarketConsumer cmiCurrentMarketConsumer)
        throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForClassV2 for " + sessionName + " classkey: " + classKey);
        }
        
        if (cmiCurrentMarketConsumer == null)
        {
            throw ExceptionBuilder.dataValidationException( "CMICurrentMarketConsumer null for session/class=" +
                sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
        }
        ServicesHelper.getUserMarketDataService(sessionManager).verifyUserCurrentMarketEnablement(sessionName, classKey);
        
        ChannelListener proxyListener = null;
        String callbackIor = getIorForCallback(cmiCurrentMarketConsumer);
        RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, false);

        if(tracker == null)
        {
            Log.alarm(this, "Cannot process unsubscribeCurrentMarketForClassV2 for " + sessionName + " classkey: " +
                    classKey + " - subscription information not available");
            throw ExceptionBuilder.systemException(
                    "unsubscribeCurrentMarketForClassV2 (" + sessionName + " classkey: " + classKey + 
                    ") subscription not found", 0);
        }
        
        synchronized(tracker)
        {
            tracker.deleteSubscription(new SessionKeyContainer(sessionName, classKey));
            if(tracker.getSubscriptionCount() == 0)
            {
                cleanupRemoteSubscriptionTracker(callbackIor);
            }
            proxyListener = tracker.getProxyListener();
            ServicesHelper.getMarketDataRequestPublisher().publishCurrentMarketUnSubscriptionV2(this, userId, userIor,
                    sessionName, classKey, 0, proxyListener);
        }
    }

    public void subscribeRecapForClassV2(String sessionName, int classKey, CMIRecapConsumer cmiRecapConsumer, short actionOnQueue) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
       if (Log.isDebugOn())
       {
           Log.debug(this, "calling subscribeRecapForClassV2 for " + sessionName + " classkey: " + classKey);
       }
       
       if (cmiRecapConsumer == null) 
       {
           throw ExceptionBuilder.dataValidationException( "CMIRecapConsumer null for session/class=" + sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
       }
       ServicesHelper.getUserMarketDataService(sessionManager).verifyUserRecapEnablement(sessionName, classKey);

       ChannelListener proxyListener = null;
       String callbackIor = getIorForCallback(cmiRecapConsumer);
       RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, true /* create if not exist */);

       synchronized(tracker)
       {
           proxyListener = tracker.getProxyListener();
           if(proxyListener == null)
           {
                com.cboe.idl.cmiCallbackV2.CMIRecapConsumer timeoutListener = null;
                timeoutListener = com.cboe.idl.cmiCallbackV2.CMIRecapConsumerHelper.narrow(
                   (org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(cmiRecapConsumer, marketDataCallbackTimeout));
                proxyListener = ServicesHelper.getRecapV2ConsumerProxy(timeoutListener, sessionManager, actionOnQueue);
                tracker.setProxyListener(proxyListener);
                tracker.setMarketDataSubscriptionType(RemoteSubscriptionTracker.RECAP);
           }
           tracker.addSubscription(new SessionKeyContainer(sessionName, classKey));
           ServicesHelper.getMarketDataRequestPublisher().publishRecapSubscriptionV2(
               this, userId, userIor, sessionName, classKey, 0, proxyListener, actionOnQueue);
       }
    }

    public void unsubscribeRecapForClassV2(String sessionName, int classKey, CMIRecapConsumer cmiRecapConsumer) 
        throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
       if (Log.isDebugOn())
       {
           Log.debug(this, "calling unsubscribeRecapForClassV2 for " + sessionName + " classkey: " + classKey);
       }
       if (cmiRecapConsumer == null) 
       {
            throw ExceptionBuilder.dataValidationException( "CMIRecapConsumer null for session/class=" + 
                sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
       }
       ServicesHelper.getUserMarketDataService(sessionManager).verifyUserRecapEnablement(sessionName, classKey);

       ChannelListener proxyListener = null;
       String callbackIor = getIorForCallback(cmiRecapConsumer);
       RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, false);

       if(tracker == null)
       {
            Log.alarm(this, "Cannot process unsubscribeRecapForClassV2 for " + sessionName + " classkey: " +
                    classKey + " - subscription information not available");
            throw ExceptionBuilder.systemException(
                    "unsubscribeRecapForClassV2 (" + sessionName + " classkey: " + classKey + 
                    ") subscription not found", 0);
       }
        
       synchronized(tracker)
       {
            tracker.deleteSubscription(new SessionKeyContainer(sessionName, classKey));
            if(tracker.getSubscriptionCount() == 0)
            {
                cleanupRemoteSubscriptionTracker(callbackIor);
            }
            proxyListener = tracker.getProxyListener();
            ServicesHelper.getMarketDataRequestPublisher().publishRecapUnSubscriptionV2(
               this, userId, userIor, sessionName, classKey, 0, proxyListener);
       }
    }

    public void subscribeNBBOForClassV2(String sessionName, int classKey, CMINBBOConsumer cmiNBBOConsumer, short actionOnQueue) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
       if (Log.isDebugOn())
       {
           Log.debug(this, "calling subscribeNBBOForClassV2 for " + sessionName + " classkey: " + classKey);
       }
       
       if (cmiNBBOConsumer == null) 
       {
           throw ExceptionBuilder.dataValidationException( "CMINBBOConsumer null for session/class=" + sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
       }
       ServicesHelper.getUserMarketDataService(sessionManager).verifyUserNBBOEnablement(sessionName, classKey);

       ChannelListener proxyListener = null;
       String callbackIor = getIorForCallback(cmiNBBOConsumer);
       RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, true /* create if not exist */);

       synchronized(tracker)
       {
           proxyListener = tracker.getProxyListener();
           if(proxyListener == null)
           {
               com.cboe.idl.cmiCallbackV2.CMINBBOConsumer timeoutListener = null;
               timeoutListener = com.cboe.idl.cmiCallbackV2.CMINBBOConsumerHelper.narrow(
                       (org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(cmiNBBOConsumer, marketDataCallbackTimeout));
               proxyListener = ServicesHelper.getNBBOV2ConsumerProxy(timeoutListener, sessionManager, actionOnQueue);
               tracker.setProxyListener(proxyListener);
               tracker.setMarketDataSubscriptionType(RemoteSubscriptionTracker.NBBO);
           }
           tracker.addSubscription(new SessionKeyContainer(sessionName, classKey));
           ServicesHelper.getMarketDataRequestPublisher().publishNBBOSubscriptionV2(
                   this, userId, userIor, sessionName, classKey, 0, proxyListener, actionOnQueue);
       }
    }

    public void unsubscribeNBBOForClassV2(String sessionName, int classKey, CMINBBOConsumer cmiNBBOConsumer) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForClassV2 for " + sessionName + " classkey: " + classKey);
        }
        
        if (cmiNBBOConsumer == null)
        {
            throw ExceptionBuilder.dataValidationException( "CMINBBOConsumer null for session/class=" +
                sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
        }
        ServicesHelper.getUserMarketDataService(sessionManager).verifyUserNBBOEnablement(sessionName, classKey);
        
        ChannelListener proxyListener = null;
        String callbackIor = getIorForCallback(cmiNBBOConsumer);
        RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, false);

        if(tracker == null)
        {
            Log.alarm(this, "Cannot process unsubscribeNBBOForClassV2 for " + sessionName + " classkey: " +
                    classKey + " - subscription information not available");
            throw ExceptionBuilder.systemException(
                    "unsubscribeNBBOForClassV2 (" + sessionName + " classkey: " + classKey + 
                    ") subscription not found", 0);
        }
        
        synchronized(tracker)
        {
            tracker.deleteSubscription(new SessionKeyContainer(sessionName, classKey));
            if(tracker.getSubscriptionCount() == 0)
            {
                cleanupRemoteSubscriptionTracker(callbackIor);
            }
            proxyListener = tracker.getProxyListener();
            ServicesHelper.getMarketDataRequestPublisher().publishNBBOUnSubscriptionV2(this, userId, userIor,
                    sessionName, classKey, 0, proxyListener);
        }
    }

    public void subscribeTickerForClassV2(String sessionName, int classKey, CMITickerConsumer cmiTickerConsumer, short actionOnQueue) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
       if (Log.isDebugOn())
       {
           Log.debug(this, "calling subscribeTickerForClassV2 for " + sessionName + " classkey: " + classKey);
       }
       
       if (cmiTickerConsumer == null) 
       {
           throw ExceptionBuilder.dataValidationException( "CMITickerConsumer null for session/class=" + sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
       }
       ServicesHelper.getUserMarketDataService(sessionManager).verifyUserTickerEnablement(sessionName, classKey);

       ChannelListener proxyListener = null;
       String callbackIor = getIorForCallback(cmiTickerConsumer);
       RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, true /* create if not exist */);

       synchronized(tracker)
       {
           proxyListener = tracker.getProxyListener();
           if(proxyListener == null)
           {
               com.cboe.idl.cmiCallbackV2.CMITickerConsumer timeoutListener = null;
               timeoutListener = com.cboe.idl.cmiCallbackV2.CMITickerConsumerHelper.narrow(
                       (org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(cmiTickerConsumer, marketDataCallbackTimeout));
               proxyListener = ServicesHelper.getTickerV2ConsumerProxy(timeoutListener, sessionManager, actionOnQueue);
               tracker.setProxyListener(proxyListener);
               tracker.setMarketDataSubscriptionType(RemoteSubscriptionTracker.TICKER);
           }
           tracker.addSubscription(new SessionKeyContainer(sessionName, classKey));
           ServicesHelper.getMarketDataRequestPublisher().publishTickerSubscriptionV2(
                   this, userId, userIor, sessionName, classKey, 0, proxyListener, actionOnQueue);
       }
    }

    public void unsubscribeTickerForClassV2(String sessionName, int classKey, CMITickerConsumer cmiTickerConsumer) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTickerForClassV2 for " + sessionName + " classkey: " + classKey);
        }
        
        if (cmiTickerConsumer == null)
        {
            throw ExceptionBuilder.dataValidationException( "CMITickerConsumer null for session/class=" +
                sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
        }
        ServicesHelper.getUserMarketDataService(sessionManager).verifyUserTickerEnablement(sessionName, classKey);
        
        ChannelListener proxyListener = null;
        String callbackIor = getIorForCallback(cmiTickerConsumer);
        RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, false);

        if(tracker == null)
        {
            Log.alarm(this, "Cannot process unsubscribeTickerForClassV2 for " + sessionName + " classkey: " +
                    classKey + " - subscription information not available");
            throw ExceptionBuilder.systemException(
                    "unsubscribeTickerForClassV2 (" + sessionName + " classkey: " + classKey + 
                    ") subscription not found", 0);
        }
        
        synchronized(tracker)
        {
            tracker.deleteSubscription(new SessionKeyContainer(sessionName, classKey));
            if(tracker.getSubscriptionCount() == 0)
            {
                cleanupRemoteSubscriptionTracker(callbackIor);
            }
            proxyListener = tracker.getProxyListener();
            ServicesHelper.getMarketDataRequestPublisher().publishTickerUnSubscriptionV2(this, userId, userIor,
                    sessionName, classKey, 0, proxyListener);
        }
    }

    public void subscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer, short actionOnQueue) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
       if (Log.isDebugOn())
       {
           Log.debug(this, "calling subscribeExpectedOpeningPriceForClassV2 for " + sessionName + " classkey: " + classKey);
       }
       
       if (cmiExpectedOpeningPriceConsumer == null) 
       {
           throw ExceptionBuilder.dataValidationException( "CMIExpectedOpeningPriceConsumer null for session/class=" + sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
       }
       ServicesHelper.getUserMarketDataService(sessionManager).verifyUserExpectdOpeningPriceEnablement(sessionName, classKey);

       ChannelListener proxyListener = null;
       String callbackIor = getIorForCallback(cmiExpectedOpeningPriceConsumer);
       RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, true /* create if not exist */);

       synchronized(tracker)
       {
           proxyListener = tracker.getProxyListener();
           if(proxyListener == null)
           {
               com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer timeoutListener = null;
               timeoutListener = com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumerHelper.narrow(
                       (org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(cmiExpectedOpeningPriceConsumer, marketDataCallbackTimeout));
               proxyListener = ServicesHelper.getExpectedOpeningPriceV2ConsumerProxy(timeoutListener, sessionManager, actionOnQueue);
               tracker.setProxyListener(proxyListener);
               tracker.setMarketDataSubscriptionType(RemoteSubscriptionTracker.EXPECTED_OPENING_PRICE);
           }
           tracker.addSubscription(new SessionKeyContainer(sessionName, classKey));
           ServicesHelper.getMarketDataRequestPublisher().publishExpectedOpeningPriceSubscriptionV2(
                   this, userId, userIor, sessionName, classKey, 0, proxyListener, actionOnQueue);
       }
    }

    public void unsubscribeExpectedOpeningPriceForClassV2(String sessionName, int classKey, CMIExpectedOpeningPriceConsumer cmiExpectedOpeningPriceConsumer) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeExpectedOpeningPriceForClassV2 for " + sessionName + " classkey: " + classKey);
        }
        
        if (cmiExpectedOpeningPriceConsumer == null)
        {
            throw ExceptionBuilder.dataValidationException( "CMIExpectedOpeningPriceConsumer null for session/class=" +
                sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
        }
        ServicesHelper.getUserMarketDataService(sessionManager).verifyUserExpectdOpeningPriceEnablement(sessionName, classKey);
        
        ChannelListener proxyListener = null;
        String callbackIor = getIorForCallback(cmiExpectedOpeningPriceConsumer);
        RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, false);

        if(tracker == null)
        {
            Log.alarm(this, "Cannot process unsubscribeExpectedOpeningPriceForClassV2 for " + sessionName + " classkey: " +
                    classKey + " - subscription information not available");
            throw ExceptionBuilder.systemException(
                    "unsubscribeExpectedOpeningPriceForClassV2 (" + sessionName + " classkey: " + classKey + 
                    ") subscription not found", 0);
        }
        
        synchronized(tracker)
        {
            tracker.deleteSubscription(new SessionKeyContainer(sessionName, classKey));
            if(tracker.getSubscriptionCount() == 0)
            {
                cleanupRemoteSubscriptionTracker(callbackIor);
            }
            proxyListener = tracker.getProxyListener();
            ServicesHelper.getMarketDataRequestPublisher().publishExpectedOpeningPriceUnSubscriptionV2(this, userId, userIor,
                    sessionName, classKey, 0, proxyListener);
        }
    }

    public void subscribeBookDepthForClassV2(String sessionName, int classKey, CMIOrderBookConsumer cmiOrderBookConsumer, short actionOnQueue) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
       if (Log.isDebugOn())
       {
           Log.debug(this, "calling subscribeBookDepthForClassV2 for " + sessionName + " classkey: " + classKey);
       }
       
       if (cmiOrderBookConsumer == null)
       {
           throw ExceptionBuilder.dataValidationException( "CMIBookDepthConsumer null for session/class=" + sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
       }
       ServicesHelper.getUserMarketDataService(sessionManager).verifyUserBookDepthEnablement(sessionName, classKey);

       ChannelListener proxyListener = null;
       String callbackIor = getIorForCallback(cmiOrderBookConsumer);
       RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, true /* create if not exist */);

       synchronized(tracker)
       {
           proxyListener = tracker.getProxyListener();
           if(proxyListener == null)
           {
               com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer timeoutListener = null;
               timeoutListener = com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumerHelper.narrow(
                       (org.omg.CORBA.Object) RemoteConnectionFactory.find().setRoundTripTimeout(cmiOrderBookConsumer, marketDataCallbackTimeout));
               proxyListener = ServicesHelper.getBookDepthV2ConsumerProxy(timeoutListener, sessionManager, actionOnQueue);
               tracker.setProxyListener(proxyListener);
               tracker.setMarketDataSubscriptionType(RemoteSubscriptionTracker.BOOK_DEPTH);
           }
           tracker.addSubscription(new SessionKeyContainer(sessionName, classKey));
           ServicesHelper.getMarketDataRequestPublisher().publishBookDepthSubscriptionV2(
                   this, userId, userIor, sessionName, classKey, 0, proxyListener, actionOnQueue);
       }
    }

    public void unsubscribeBookDepthForClassV2(String sessionName, int classKey, CMIOrderBookConsumer cmiOrderBookConsumer) throws SystemException, AuthorizationException, DataValidationException, CommunicationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthForClassV2 for " + sessionName + " classkey: " + classKey);
        }
        
        if (cmiOrderBookConsumer == null)
        {
            throw ExceptionBuilder.dataValidationException( "CMIBookDepthConsumer null for session/class=" +
                sessionName + "/" + classKey, DataValidationCodes.MISSING_LISTENER);
        }
        ServicesHelper.getUserMarketDataService(sessionManager).verifyUserBookDepthEnablement(sessionName, classKey);
        
        ChannelListener proxyListener = null;
        String callbackIor = getIorForCallback(cmiOrderBookConsumer);
        RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, false);

        if(tracker == null)
        {
            Log.alarm(this, "Cannot process unsubscribeBookDepthForClassV2 for " + sessionName + " classkey: " +
                    classKey + " - subscription information not available");
            throw ExceptionBuilder.systemException(
                    "unsubscribeBookDepthForClassV2 (" + sessionName + " classkey: " + classKey + 
                    ") subscription not found", 0);
        }
        
        synchronized(tracker)
        {
            tracker.deleteSubscription(new SessionKeyContainer(sessionName, classKey));
            if(tracker.getSubscriptionCount() == 0)
            {
                cleanupRemoteSubscriptionTracker(callbackIor);
            }
            proxyListener = tracker.getProxyListener();
            ServicesHelper.getMarketDataRequestPublisher().publishBookDepthUnSubscriptionV2(this, userId, userIor,
                    sessionName, classKey, 0, proxyListener);
        }
    }

    public void acceptRemoteCASCallbackRemoval(CallbackDeregistrationInfo eventData)
    {
        String callbackIor = eventData.getCallbackInformationStruct().ior;
        
        RemoteSubscriptionTracker tracker = getRemoteSubscriptionTracker(callbackIor, false);
        if(tracker == null)
        {
            Log.alarm(this, "Cannot process callback removal: subscription information is not available.");
            return;
        }
        
        synchronized(tracker)
        {
            short subscriptionType = tracker.getMarketDataSubscriptionType();
            ChannelListener proxyListener = tracker.getProxyListener();
            
            try
            {
                tracker.incrementResubscribeCount();
            }
            catch(Exception e)
            {
                Log.exception(this, e.getMessage(), e);
                cleanupRemoteSubscriptionTracker(callbackIor);
                removeSubscriptionsForCallback(proxyListener, subscriptionType);
                return;
            }
            
            StringBuilder received = new StringBuilder(userId.length()+90);
            received.append("Received callback removal notification for user=").append(userId)
                    .append(" - attempting to resubscribe callback");
            Log.alarm(this, received.toString());

            try {
                switch(subscriptionType)
                {
                    case RemoteSubscriptionTracker.BOOK_DEPTH:
                        ServicesHelper.getMarketDataRequestPublisherExt().republishBookDepthSubscriptionV2(proxyListener);
                        break;
                    case RemoteSubscriptionTracker.CURRENT_MARKET:
                        ServicesHelper.getMarketDataRequestPublisherExt().republishCurrentMarketSubscriptionV2(proxyListener);
                        break;
                    case RemoteSubscriptionTracker.EXPECTED_OPENING_PRICE:
                        ServicesHelper.getMarketDataRequestPublisherExt().republishExpectedOpeningPriceSubscriptionV2(proxyListener);
                        break;
                    case RemoteSubscriptionTracker.NBBO:
                        ServicesHelper.getMarketDataRequestPublisherExt().republishNBBOSubscriptionV2(proxyListener);
                        break;
                    case RemoteSubscriptionTracker.RECAP:
                        ServicesHelper.getMarketDataRequestPublisherExt().republishRecapSubscriptionV2( proxyListener);
                        break;
                    case RemoteSubscriptionTracker.TICKER:
                        ServicesHelper.getMarketDataRequestPublisherExt().republishTickerSubscriptionV2(proxyListener);
                        break;
                    default:
                        Log.alarm(this, "Invalid subscription type: " + subscriptionType);
                        break;
                }
            } catch (Exception e) {
                Log.exception(this, e.getMessage(), e);
                Log.alarm(this, "Callback removal notification for user=" + userId + " - failed to resubscribe callback");
            }
        }
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + userId);
        }
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        InstrumentedEventChannelAdapterFactory.find().removeListenerGroup(this);
        ServicesHelper.getMarketDataRequestPublisher().removeMarketDataRequestSource(this);        
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);
    }
    
    private String getIorForCallback(org.omg.CORBA.Object object)
    {
        IORImpl iorImpl = ((DelegateImpl)((org.omg.CORBA.portable.ObjectImpl)object)._get_delegate()).getIOR();
        return iorImpl.stringify();
    }
    
    private synchronized RemoteSubscriptionTracker getRemoteSubscriptionTracker(String callbackIor, boolean create)
    {
        RemoteSubscriptionTracker tracker = (RemoteSubscriptionTracker) subscriptionTrackerMap.get(callbackIor);
        if(create && tracker == null)
        {
            tracker = new RemoteSubscriptionTracker(resubscribeInterval, maxResubscribeAttempts);
            subscriptionTrackerMap.put(callbackIor, tracker);
        }
        
        return tracker;
    }

    private void removeSubscriptionsForCallback(ChannelListener proxyListener, short subscriptionType)
    { 
        switch(subscriptionType)
        {
            case RemoteSubscriptionTracker.BOOK_DEPTH:
                ServicesHelper.getMarketDataRequestPublisherExt().removeBookDepthSubscriptionsV2(this, proxyListener);
                break;
            case RemoteSubscriptionTracker.CURRENT_MARKET:
                ServicesHelper.getMarketDataRequestPublisherExt().removeCurrentMarketSubscriptionsV2(this, proxyListener);
                break;
            case RemoteSubscriptionTracker.EXPECTED_OPENING_PRICE:
                ServicesHelper.getMarketDataRequestPublisherExt().removeExpectedOpeningPriceSubscriptionsV2(this, proxyListener);
                break;
            case RemoteSubscriptionTracker.NBBO:
                ServicesHelper.getMarketDataRequestPublisherExt().removeNBBOSubscriptionsV2(this, proxyListener);
                break;
            case RemoteSubscriptionTracker.RECAP:
                ServicesHelper.getMarketDataRequestPublisherExt().removeRecapSubscriptionsV2(this, proxyListener);
                break;
            case RemoteSubscriptionTracker.TICKER:
                ServicesHelper.getMarketDataRequestPublisherExt().removeTickerSubscriptionsV2(this, proxyListener);
                break;
            default:
                Log.alarm(this, "Invalid subscription type: " + subscriptionType);
                break;
        }

    }
    
    private synchronized void cleanupRemoteSubscriptionTracker(String callbackIor)
    {
        subscriptionTrackerMap.remove(callbackIor);
    }
}
