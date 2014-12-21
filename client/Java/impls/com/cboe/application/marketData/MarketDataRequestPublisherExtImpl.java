package com.cboe.application.marketData;

import com.cboe.application.supplier.proxy.*;
import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.MarketDataRequestPublisherExt;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;

import java.util.*;

/**
 * @author Jing Chen
 */
public class MarketDataRequestPublisherExtImpl extends MarketDataRequestPublisherImpl implements MarketDataRequestPublisherExt
{

    // MWM - add support for remoteResubscription
    protected Map marketDataSubscriptionsByListener;

    public MarketDataRequestPublisherExtImpl()
    {
        super();
        // MWM - add support for remoteResubscription
        marketDataSubscriptionsByListener = new HashMap();
    }

    // MWM - add support for remoteResubscription
    private void putMarketDataSubscriptionByListener(Object key, RemoteMarketDataSubscriptionInfoContainer subscriptionInfo)
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.get(key);
        if ( subscriptions == null ) {
            subscriptions = new HashSet();
            marketDataSubscriptionsByListener.put(key, subscriptions);
        }
        subscriptions.add(subscriptionInfo);
    }

    // MWM - add support for remoteResubscription
    private void removeMarketDataSubscriptionByListener (Object key, RemoteMarketDataSubscriptionInfoContainer subscriptionInfo)
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.get(key);
        if ( subscriptions != null ) {
            subscriptions.remove(subscriptionInfo);
            if (subscriptions.isEmpty()) {
                marketDataSubscriptionsByListener.remove(key);
            }
        }
    }

    // MWM - add support for remoteResubscription
    private void removeAllMarketDataSubscriptionsForListener (Object key)
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.remove(key);
        if ( subscriptions != null ) {
                subscriptions.clear();
        }
    }

    public synchronized void publishBookDepthSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishBookDepthSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishBookDepthUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishBookDepthUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishBookDepthSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishBookDepthSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishBookDepthUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishBookDepthUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishCurrentMarketSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishCurrentMarketSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishCurrentMarketSubscriptionV3(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue) 
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishCurrentMarketSubscriptionV3(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue); 
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishCurrentMarketUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener) 
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishCurrentMarketUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishCurrentMarketUnSubscriptionV3(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishCurrentMarketUnSubscriptionV3(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishCurrentMarketSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishCurrentMarketSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishCurrentMarketUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishCurrentMarketUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishNBBOSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishNBBOSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishNBBOUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishNBBOUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishNBBOSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishNBBOSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishNBBOUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishNBBOUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishRecapSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishRecapSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishRecapUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishRecapUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishRecapSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishRecapSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishRecapUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishRecapUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishTickerSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishTickerSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishTickerUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishTickerUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishTickerSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishTickerSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishTickerUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishTickerUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishExpectedOpeningPriceSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishExpectedOpeningPriceSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishExpectedOpeningPriceUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishExpectedOpeningPriceUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishExpectedOpeningPriceSubscription(Object source, String userId, String userIor, String sessionName, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishExpectedOpeningPriceSubscription(source, userId, userIor, sessionName, classKey, clientListener);
        // MWM - add support for remoteResubscription
        putMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    public synchronized void publishExpectedOpeningPriceUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = 
        super.internalPublishExpectedOpeningPriceUnSubscription(source, userId, userIor, sessionName, classKey, clientListener);
        // MWM - add support for remoteResubscription
        removeMarketDataSubscriptionByListener(clientListener, subscriptionInfo);
    }

    // MWM - add support for remoteResubscription
    public synchronized void republishExpectedOpeningPriceSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException 
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.get(clientListener);
        if ( subscriptions != null ) {
            for (Iterator iter = subscriptions.iterator(); iter.hasNext(); ) {
                RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) iter.next();
                publishExpectedOpeningPriceSubscriptionV2(
                         subscriptionInfo.getUserId(),
                         subscriptionInfo.getUserSessionIOR(),
                         subscriptionInfo.getSessionName(),
                         subscriptionInfo.getClassKey(),
                         subscriptionInfo.getProductKey(),
                         ((ExpectedOpeningPriceV2ConsumerProxy) subscriptionInfo.getCmiConsumer()).getExpectedOpeningPriceConsumer(),
                         subscriptionInfo.getActionOnQueue()
                         );
            }
        }
    }

    // MWM - add support for remoteResubscription
    public synchronized void republishNBBOSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException 
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.get(clientListener);
        if ( subscriptions != null ) {
            for (Iterator iter = subscriptions.iterator(); iter.hasNext(); ) {
                RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) iter.next();
                 publishNBBOSubscriptionV2(
                         subscriptionInfo.getUserId(),
                         subscriptionInfo.getUserSessionIOR(),
                         subscriptionInfo.getSessionName(),
                         subscriptionInfo.getClassKey(),
                         subscriptionInfo.getProductKey(),
                         ((NBBOV2ConsumerProxy) subscriptionInfo.getCmiConsumer()).getNBBOConsumer(),
                         subscriptionInfo.getActionOnQueue()
                         );
            }
        }
    }

    // MWM - add support for remoteResubscription
    public synchronized void republishTickerSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException 
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.get(clientListener);
        if ( subscriptions != null ) {
            for (Iterator iter = subscriptions.iterator(); iter.hasNext(); ) {
                RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) iter.next();
                 publishTickerSubscriptionV2(
                         subscriptionInfo.getUserId(),
                         subscriptionInfo.getUserSessionIOR(),
                         subscriptionInfo.getSessionName(),
                         subscriptionInfo.getClassKey(),
                         subscriptionInfo.getProductKey(),
                         ((TickerV2ConsumerProxy) subscriptionInfo.getCmiConsumer()).getTickerConsumer(),
                         subscriptionInfo.getActionOnQueue()
                         );
            }
        }
    }

    // MWM - add support for remoteResubscription
    public synchronized void republishRecapSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException 
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.get(clientListener);
        if ( subscriptions != null ) {
            for (Iterator iter = subscriptions.iterator(); iter.hasNext(); ) {
                RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) iter.next();
                 publishRecapSubscriptionV2(
                         subscriptionInfo.getUserId(),
                         subscriptionInfo.getUserSessionIOR(),
                         subscriptionInfo.getSessionName(),
                         subscriptionInfo.getClassKey(),
                         subscriptionInfo.getProductKey(),
                         ((RecapV2ConsumerProxy) subscriptionInfo.getCmiConsumer()).getRecapConsumer(),
                         subscriptionInfo.getActionOnQueue()
                         );
            }
        }
    }

    // MWM - add support for remoteResubscription
    public synchronized void republishBookDepthSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException 
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.get(clientListener);
        if ( subscriptions != null ) {
            for (Iterator iter = subscriptions.iterator(); iter.hasNext(); ) {
                RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) iter.next();
                 publishBookDepthSubscriptionV2(
                         subscriptionInfo.getUserId(),
                         subscriptionInfo.getUserSessionIOR(),
                         subscriptionInfo.getSessionName(),
                         subscriptionInfo.getClassKey(),
                         subscriptionInfo.getProductKey(),
                         ((BookDepthV2ConsumerProxy) subscriptionInfo.getCmiConsumer()).getOrderBookConsumer(),
                         subscriptionInfo.getActionOnQueue()
                         );
            }
        }
    }

    // MWM - add support for remoteResubscription
    public synchronized void republishCurrentMarketSubscriptionV2(ChannelListener clientListener)
        throws SystemException, DataValidationException, CommunicationException, AuthorizationException 
    {
        Set subscriptions = (Set) marketDataSubscriptionsByListener.get(clientListener);
        if ( subscriptions != null ) {
            for (Iterator iter = subscriptions.iterator(); iter.hasNext(); ) {
                RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) iter.next();
                 publishCurrentMarketSubscriptionV2(
                         subscriptionInfo.getUserId(),
                         subscriptionInfo.getUserSessionIOR(),
                         subscriptionInfo.getSessionName(),
                         subscriptionInfo.getClassKey(),
                         subscriptionInfo.getProductKey(),
                         ((CurrentMarketV2ConsumerProxy) subscriptionInfo.getCmiConsumer()).getCurrentMarketConsumer(),
                         subscriptionInfo.getActionOnQueue()
                         );
            }
        }
    }

    public synchronized void removeMarketDataRequestSource(Object source)
    {
        Map map=null;

        removeBookDepthV2SubscriptionFromGroup(source);
        map=(Map)bookDepthV2SubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);

        removeCurrentMarketV2SubscriptionFromGroup(source);
        map=(Map)currentMarketV2SubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeCurrentMarketV3SubscriptionFromGroup(source);
        map=(Map)currentMarketV3SubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeNBBOV2SubscriptionFromGroup(source);
        map=(Map)nbboV2SubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeRecapV2SubscriptionFromGroup(source);
        map=(Map)recapV2SubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeTickerV2SubscriptionFromGroup(source);
        map=(Map)tickerV2SubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeExpectedOpeningPriceV2SubscriptionFromGroup(source);
        map=(Map)eopV2SubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeBookDepthSubscriptionFromGroup(source);
        map=(Map)bookDepthSubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeCurrentMarketSubscriptionFromGroup(source);
        map=(Map)currentMarketSubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeNBBOSubscriptionFromGroup(source);
        map=(Map)nbboSubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeRecapSubscriptionFromGroup(source);
        map=(Map)recapSubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeTickerSubscriptionFromGroup(source);
        map=(Map)tickerSubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
        
        removeExpectedOpeningPriceSubscriptionFromGroup(source);
        map=(Map)eopSubscriptionsBySource.remove(source);
        removeSubscriptionMap(map);
    }

    // MWM - helper method for subscription removal
    private void removeSubscriptionMap(Map subscriptionMap)
    {
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo=null;
        
        Set mapSet = (Set) subscriptionMap.keySet();
        Iterator setIter = mapSet.iterator();
        while (setIter.hasNext()) {
            subscriptionInfo=(RemoteMarketDataSubscriptionInfoContainer) setIter.next();
            removeAllMarketDataSubscriptionsForListener(subscriptionInfo.getCmiConsumer());
        }        
    }

    // MWM - add support for remoteResubscription
    public synchronized void removeCurrentMarketSubscriptionsV2(Object source, Object clientListener)
    {
        removeMarketDataSubscriptions(source, clientListener, currentMarketV2SubscriptionsByGroup , currentMarketV2SubscriptionsBySource );
    }

    // MWM - add support for remoteResubscription
    public synchronized void removeBookDepthSubscriptionsV2(Object source, Object clientListener)
    {
        removeMarketDataSubscriptions(source, clientListener, bookDepthV2SubscriptionsByGroup , bookDepthV2SubscriptionsBySource );
    }

    // MWM - add support for remoteResubscription
    public synchronized void removeRecapSubscriptionsV2(Object source, Object clientListener)
    {
        removeMarketDataSubscriptions(source, clientListener, recapV2SubscriptionsByGroup , recapV2SubscriptionsBySource );
    }

    // MWM - add support for remoteResubscription
    public synchronized void removeTickerSubscriptionsV2(Object source, Object clientListener)
    {
        removeMarketDataSubscriptions(source, clientListener, tickerV2SubscriptionsByGroup , tickerV2SubscriptionsBySource );
    }

    // MWM - add support for remoteResubscription
    public synchronized void removeNBBOSubscriptionsV2(Object source, Object clientListener)
    {
        removeMarketDataSubscriptions(source, clientListener, nbboV2SubscriptionsByGroup , nbboV2SubscriptionsBySource );
    }

    // MWM - add support for remoteResubscription
    public synchronized void removeExpectedOpeningPriceSubscriptionsV2(Object source, Object clientListener)
    {
        removeMarketDataSubscriptions(source, clientListener, eopV2SubscriptionsByGroup , eopV2SubscriptionsBySource );
    }

    // MWM - add support for remoteResubscription
    private void removeMarketDataSubscriptions(Object source, Object clientListener, Map subscriptionsByGroupKey, Map subscriptionsBySource)
    {
        Set listenerSubscriptionSet = (Set) marketDataSubscriptionsByListener.remove(clientListener);
        Map groupKeysMap = (Map) subscriptionsBySource.get(source);

        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = null;
        
        Iterator subscriptionIterator = listenerSubscriptionSet.iterator();
        while ( subscriptionIterator.hasNext() ) { 
            subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) subscriptionIterator.next();
            int[] groupKeys = (int[]) groupKeysMap.remove(subscriptionInfo);
            for (int i=0; i < groupKeys.length ; i++) {
                Integer key = groupKeys[i];
                Map subscriptionInfoMap = (Map) subscriptionsByGroupKey.get(key);
                subscriptionInfoMap.remove(subscriptionInfo);
                if (subscriptionInfoMap.isEmpty()) {
                    subscriptionsByGroupKey.remove(key);
                }
            }
        }
        if (groupKeysMap.isEmpty()) { 
            subscriptionsBySource.remove(source); 
        }
    }

    public synchronized void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (channelKey.channelType == ChannelType.MDCAS_RECOVERY)
        {
            int groupKey = ((Integer)event.getEventData()).intValue();
            StringBuilder republishing = new StringBuilder(70);
            republishing.append("republishing market data requests for market data group:").append(groupKey);
            Log.information(this, republishing.toString());
            republishMarketDataRequestsForGroup(groupKey);
        }
        else
        {
            Log.alarm("MarketDataRequestPublisherImpl -> Wrong Channel : " + channelKey.channelType);
        }
    }
}
