package com.cboe.expressApplication.marketData;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.cmiCallbackV4.CMINBBOConsumer;
import com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer;
import com.cboe.idl.cmiCallbackV4.CMIRecapConsumer;
import com.cboe.idl.cmiCallbackV4.CMITickerConsumer;
import com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import com.cboe.interfaces.events.*;
import com.cboe.interfaces.internalBusinessServices.ProductConfigurationService;
import com.cboe.interfaces.expressApplication.MarketDataCallbackRequestPublisher;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MarketDataCallbackRequestPublisherImpl extends BObject implements MarketDataCallbackRequestPublisher, EventChannelListener
{
    protected ProductConfigurationService productConfigurationService;
    protected String casOrigin;
    protected Map currentMarketV4SubscriptionsBySource;
    protected Map currentMarketV4SubscriptionsByGroup;

    protected Map recapV4SubscriptionsBySource;
    protected Map recapV4SubscriptionsByGroup;

    protected Map tickerV4SubscriptionsBySource;
    protected Map tickerV4SubscriptionsByGroup;
    
    protected Map nbboV4SubscriptionsBySource;
    protected Map nbboV4SubscriptionsByGroup;

    protected IECRemoteCASRecoveryConsumerHome remoteCASRecoveryConsumerHome;

    public MarketDataCallbackRequestPublisherImpl()
    {
        super();
        currentMarketV4SubscriptionsBySource = new HashMap();
        currentMarketV4SubscriptionsByGroup = new HashMap();

        recapV4SubscriptionsBySource = new HashMap();
        recapV4SubscriptionsByGroup = new HashMap();

        tickerV4SubscriptionsBySource = new HashMap();
        tickerV4SubscriptionsByGroup = new HashMap();
        
        nbboV4SubscriptionsBySource = new HashMap();
        nbboV4SubscriptionsByGroup = new HashMap();
    }

    public void initialize()
        throws Exception
    {
        remoteCASRecoveryConsumerHome = ServicesHelper.getRemoteCASRecoveryConsumerHome();
        casOrigin = FoundationFramework.getInstance().getConfigService().getProperty("Process.name()");
        subscribeForEvents();
    }

    private void subscribeForEvents()
    {
        EventChannelAdapterFactory.find().setDynamicChannels(true);
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.EXPRESS_CAS_RECOVERY, Integer.valueOf(0));
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
    }

    protected ProductConfigurationService getProductConfigurationService()
    {
        if (productConfigurationService == null )
        {
            productConfigurationService = com.cboe.application.shared.ServicesHelper.getProductConfigurationService();
        }
        return productConfigurationService;
    }

    private Map getMap(Map keyTable, Object key)
    {
        Map lookupMap = (Map) keyTable.get(key);
        if (lookupMap == null)
        {
            lookupMap = new ConcurrentHashMap();
            keyTable.put(key, lookupMap);
        }
        return lookupMap;
    }

    private Map getCurrentMarketV4SubscriptionsBySource(Object source)
    {
        return getMap(currentMarketV4SubscriptionsBySource, source);
    }
    private Map getCurrentMarketV4SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(currentMarketV4SubscriptionsByGroup, Integer.valueOf(groupKey));
    }

    private Map getRecapV4SubscriptionsBySource(Object source)
    {
        return getMap(recapV4SubscriptionsBySource, source);
    }
    private Map getRecapV4SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(recapV4SubscriptionsByGroup, Integer.valueOf(groupKey));
    }

    private Map getTickerV4SubscriptionsBySource(Object source)
    {
        return getMap(tickerV4SubscriptionsBySource, source);
    }
    private Map getTickerV4SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(tickerV4SubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    
    private Map getNBBOV4SubscriptionsBySource(Object source)
    {
        return getMap(nbboV4SubscriptionsBySource, source);
    }
    private Map getNBBOSubscriptionsByGroupKey(int groupKey)
    {
        return getMap(nbboV4SubscriptionsByGroup, Integer.valueOf(groupKey));
    }

    public void publishCurrentMarketV4Subscription(Object source, String userId, String userIor, int classKey, Object clientListener, short actionOnQueue,
                                                   boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishCurrentMarketV4Subscription(source, userId, userIor, classKey, clientListener, actionOnQueue, disseminateExternalMarketData);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishCurrentMarketV4Subscription(Object source, String userId, String userIor, int classKey, Object clientListener, short actionOnQueue,
                                                                                                      boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishCurrentMarketV4Subscription(userId, userIor, classKey,
                                                             (CMICurrentMarketConsumer) clientListener,
                                                             actionOnQueue, disseminateExternalMarketData);
        // re-using RemoteMarketDataSubscriptionInfoContainer, but with no tradingSession or productKey
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
                                                                                                      classKey,
                                                                                                      0,
                                                                                                      "",
                                                                                                      userId,
                                                                                                      userIor,
                                                                                                      clientListener,
                                                                                                      actionOnQueue,
                                                                                                      disseminateExternalMarketData);
        for(int i = 0; i < groupKeys.length; i++)
        {
            getCurrentMarketV4SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getCurrentMarketV4SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishCurrentMarketV4Unsubscription(Object source, String userId, String userIor, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishCurrentMarketV4UnSubscription(source, userId, userIor, classKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishCurrentMarketV4UnSubscription(Object source, String userId, String userIor, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = internalPublishCurrentMarketV4Unsubscription(userId, userIor, classKey,
                                                               (CMICurrentMarketConsumer)clientListener);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
                                                                                                      classKey,
                                                                                                      0,
                                                                                                      "",
                                                                                                      userId,
                                                                                                      userIor,
                                                                                                      clientListener,
                                                                                                      (short) 0,
                                                                                                      false);
        for(int i = 0; i < groupKeys.length; i++)
        {
            getCurrentMarketV4SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if(getCurrentMarketV4SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                currentMarketV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getCurrentMarketV4SubscriptionsBySource(source).remove(subscriptionInfo);
        if(getCurrentMarketV4SubscriptionsBySource(source).isEmpty())
        {
            currentMarketV4SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishRecapV4Subscription(Object source, String userId, String userIor,
                                           int classKey, Object clientListener, short actionOnQueue, boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishRecapV4Subscription(source, userId, userIor, classKey, clientListener, actionOnQueue, disseminateExternalMarketData);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishRecapV4Subscription(Object source,
                                                                                              String userId,
                                                                                              String userIor,
                                                                                              int classKey,
                                                                                              Object clientListener,
                                                                                              short actionOnQueue,
                                                                                              boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishRecapV4Subscription(userId, userIor, classKey,
                                                     (CMIRecapConsumer) clientListener,
                                                     actionOnQueue, disseminateExternalMarketData);
        // re-using RemoteMarketDataSubscriptionInfoContainer, but with no tradingSession or productKey
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
                                                                                                      classKey,
                                                                                                      0,
                                                                                                      "",
                                                                                                      userId,
                                                                                                      userIor,
                                                                                                      clientListener,
                                                                                                      actionOnQueue,
                                                                                                      disseminateExternalMarketData);
        for(int i = 0; i < groupKeys.length; i++)
        {
            getRecapV4SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getRecapV4SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishRecapV4Unsubscription(Object source, String userId, String userIor,
                                             int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishRecapV4UnSubscription(source, userId, userIor, classKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishRecapV4UnSubscription(Object source,
                                                                                                String userId,
                                                                                                String userIor,
                                                                                                int classKey,
                                                                                                Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishRecapV4Unsubscription(userId, userIor, classKey,
                                                       (CMIRecapConsumer) clientListener);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
                                                                                                      classKey,
                                                                                                      0,
                                                                                                      "",
                                                                                                      userId,
                                                                                                      userIor,
                                                                                                      clientListener,
                                                                                                      (short) 0,
                                                                                                      false);
        for(int i = 0; i < groupKeys.length; i++)
        {
            getRecapV4SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if(getRecapV4SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                recapV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getRecapV4SubscriptionsBySource(source).remove(subscriptionInfo);
        if(getRecapV4SubscriptionsBySource(source).isEmpty())
        {
            recapV4SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishTickerV4Subscription(Object source, String userId, String userIor,
                                            int classKey, Object clientListener, short actionOnQueue, boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishTickerV4Subscription(source, userId, userIor, classKey, clientListener, actionOnQueue, disseminateExternalMarketData);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishTickerV4Subscription(Object source,
                                                                                               String userId,
                                                                                               String userIor,
                                                                                               int classKey,
                                                                                               Object clientListener,
                                                                                               short actionOnQueue,
                                                                                               boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishTickerV4Subscription(userId, userIor, classKey,
                                                      (CMITickerConsumer) clientListener,
                                                      actionOnQueue, disseminateExternalMarketData);
        // re-using RemoteMarketDataSubscriptionInfoContainer, but with no tradingSession or productKey
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
                                                                                                      classKey,
                                                                                                      0,
                                                                                                      "",
                                                                                                      userId,
                                                                                                      userIor,
                                                                                                      clientListener,
                                                                                                      actionOnQueue,
                                                                                                      disseminateExternalMarketData);
        for(int i = 0; i < groupKeys.length; i++)
        {
            getTickerV4SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getTickerV4SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishTickerV4Unsubscription(Object source, String userId, String userIor,
                                              int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishTickerV4UnSubscription(source, userId, userIor, classKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishTickerV4UnSubscription(Object source,
                                                                                                 String userId,
                                                                                                 String userIor,
                                                                                                 int classKey,
                                                                                                 Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishTickerV4Unsubscription(userId, userIor, classKey,
                                                        (CMITickerConsumer) clientListener);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
                                                                                                      classKey,
                                                                                                      0,
                                                                                                      "",
                                                                                                      userId,
                                                                                                      userIor,
                                                                                                      clientListener,
                                                                                                      (short) 0,
                                                                                                      false);
        for(int i = 0; i < groupKeys.length; i++)
        {
            getTickerV4SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if(getTickerV4SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                tickerV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getTickerV4SubscriptionsBySource(source).remove(subscriptionInfo);
        if(getTickerV4SubscriptionsBySource(source).isEmpty())
        {
            tickerV4SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }
    
    public void publishNBBOV4Subscription(Object source, String userId, String userIor,
            							int classKey, Object clientListener, short actionOnQueue)
    		throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
    	internalPublishNBBOV4Subscription(source, userId, userIor, classKey, clientListener, actionOnQueue);
	}

	synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishNBBOV4Subscription(Object source,
	                                                               						   String userId,
	                                                               						   String userIor,
	                                                               						   int classKey,
	                                                               						   Object clientListener,
	                                                               						   short actionOnQueue)
			throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = publishNBBOV4Subscription(userId, userIor, classKey,
		                      				      (CMINBBOConsumer) clientListener, 
		                      				      actionOnQueue);
		// re-using RemoteMarketDataSubscriptionInfoContainer, but with no tradingSession or productKey
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
		                                                                      						  classKey,
		                                                                      						  0,
		                                                                      						  "",
		                                                                      						  userId,
		                                                                      						  userIor,
		                                                                      						  clientListener,
		                                                                      						  actionOnQueue,
		                                                                      						  false);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getNBBOSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
		}
		getNBBOV4SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
		return subscriptionInfo;
	}
	
	public void publishNBBOV4Unsubscription(Object source, String userId, String userIor,
	              						  int classKey, Object clientListener)
			throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		internalPublishNBBOV4UnSubscription(source, userId, userIor, classKey, clientListener);
	}
	
	synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishNBBOV4UnSubscription(Object source,
	                                                                 				  String userId,
	                                                                 				  String userIor,
	                                                                 				  int classKey,
	                                                                 				  Object clientListener)
			throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = publishNBBOV4Unsubscription(userId, userIor, classKey,
	                        					    (CMINBBOConsumer) clientListener);
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
	                                                                      							  classKey,
	                                                                      							  0,
	                                                                      							  "",
	                                                                      							  userId,
	                                                                      							  userIor,
	                                                                      							  clientListener,
	                                                                      							  (short) 0,
	                                                                      							   false);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getNBBOSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
			if(getNBBOSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
			{
				nbboV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
			}
		}
		getNBBOV4SubscriptionsBySource(source).remove(subscriptionInfo);
		if(getNBBOV4SubscriptionsBySource(source).isEmpty())
		{
			nbboV4SubscriptionsBySource.remove(source);
		}
		return subscriptionInfo;
	}

    // would only need this if there were local V4 subscriptions
    public synchronized void removeMarketDataRequestSource(Object source)
    {
        removeCurrentMarketV4SubscriptionFromGroup(source);
        currentMarketV4SubscriptionsBySource.remove(source);

        removeRecapV4SubscriptionFromGroup(source);
        recapV4SubscriptionsBySource.remove(source);

        removeTickerV4SubscriptionFromGroup(source);
        tickerV4SubscriptionsBySource.remove(source);
        
        removeNBBOV4SubscriptionFromGroup(source);
        nbboV4SubscriptionsBySource.remove(source);
    }

    protected void removeCurrentMarketV4SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getCurrentMarketV4SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getCurrentMarketV4SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getCurrentMarketV4SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    currentMarketV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected void removeRecapV4SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getRecapV4SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer;
        for(int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer) subscriptions[i];
            int[] groupKeys = (int[]) subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j = 0; j < groupKeys.length; j++)
            {
                getRecapV4SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if(getRecapV4SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    recapV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected void removeTickerV4SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getTickerV4SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer;
        for(int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer) subscriptions[i];
            int[] groupKeys = (int[]) subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j = 0; j < groupKeys.length; j++)
            {
                getTickerV4SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if(getTickerV4SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    tickerV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }
    
    protected void removeNBBOV4SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getNBBOV4SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer;
        for(int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer) subscriptions[i];
            int[] groupKeys = (int[]) subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j = 0; j < groupKeys.length; j++)
            {
                getNBBOSubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if(getNBBOSubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    nbboV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishCurrentMarketV4Subscription(String userId, String userIor, int classKey,
                                                       com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer clientListener,
                                                       short actionOnQueue, boolean disseminateExternalMarketData)
    throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short)0);
        MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();

        publisher.subscribeCurrentMarket(params,
                                         casOrigin,
                                         userId,
                                         userIor,
                                         clientListener,
                                         actionOnQueue,
                                         disseminateExternalMarketData);

        return groupKeys;
    }
    
    protected synchronized int[] internalPublishCurrentMarketV4SubscriptionForProduct(String userId, String userIor, int classKey, int productKey, CurrentMarketManualQuoteConsumer consumer, short actionOnQueue, boolean disseminateExternalMarketData) 
    	throws SystemException, CommunicationException, DataValidationException, AuthorizationException {
    	int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short)0);
        MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();

        publisher.subscribeCurrentMarketForProduct(params,
                                         casOrigin,
                                         userId,
                                         userIor,
                                         productKey,
                                         consumer,
                                         actionOnQueue,
                                         disseminateExternalMarketData);

        return groupKeys;
	}

    protected int[] publishRecapV4Subscription(String userId, String userIor, int classKey,
                                               CMIRecapConsumer clientListener,
                                               short actionOnQueue, boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
        MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();

        publisher.subscribeRecap(params,
                                 casOrigin,
                                 userId,
                                 userIor,
                                 clientListener,
                                 actionOnQueue,
                                 disseminateExternalMarketData);

        return groupKeys;
    }
    
    protected synchronized int[] internalPublishRecapV4SubscriptionForProduct(String userId, String userIor, int classKey, int productKey,
            CMIRecapConsumer clientListener,
            short actionOnQueue, boolean disseminateExternalMarketData)
		throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();		
		publisher.subscribeRecapForProduct(params,
											casOrigin,
											userId,
											userIor,
											productKey,
											clientListener,
											actionOnQueue,
											disseminateExternalMarketData);
		
		return groupKeys;
	}

    protected int[] publishTickerV4Subscription(String userId, String userIor, int classKey,
                                                CMITickerConsumer clientListener,
                                                short actionOnQueue, boolean disseminateExternalMarketData)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
        MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();

        publisher.subscribeTicker(params,
                                  casOrigin,
                                  userId,
                                  userIor,
                                  clientListener,
                                  actionOnQueue,
                                  disseminateExternalMarketData);

        return groupKeys;
    }
    
    protected synchronized int[] internalPublishTickerV4SubscriptionForProduct(String userId, String userIor, int classKey, int productKey,
    		CMITickerConsumer clientListener, short actionOnQueue, boolean disseminateExternalMarketData)
		throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
		
		publisher.subscribeTickerForProduct(params,
											casOrigin,
											userId,
											userIor,
											productKey,
											clientListener,
											actionOnQueue,
											disseminateExternalMarketData);		
		return groupKeys;
	}
    
    protected int[] publishNBBOV4Subscription(String userId, String userIor, int classKey,
            								CMINBBOConsumer clientListener, short actionOnQueue)
    		throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
		
		publisher.subscribeNBBO(params,
								casOrigin,
								userId,
								userIor,
								clientListener,
								actionOnQueue);
		
		return groupKeys;
	}

    protected synchronized int[] internalPublishNBBOV4SubscriptionForProduct(String userId, String userIor, int classKey, int productKey,
			CMINBBOConsumer clientListener, short actionOnQueue)
	throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
		
		publisher.subscribeNBBOForProduct(params,
											casOrigin,
											userId,
											userIor,
											productKey,
											clientListener,
											actionOnQueue);
		
		return groupKeys;
	}
    
    protected int[] internalPublishCurrentMarketV4Unsubscription(String userId, String userIor, int classKey, com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short)0);
        MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();

        publisher.unsubscribeCurrentMarket(params,
                                           casOrigin,
                                           userId,
                                           userIor,
                                           clientListener);

        return groupKeys;
    }
    
    protected synchronized int[] internalPublishCurrentMarketV4UnsubscriptionForProduct(String userId, String userIor, int classKey, int productKey, CurrentMarketManualQuoteConsumer clientListener)
    	throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
	    int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
	    RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short)0);
	    MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
	
	    publisher.unsubscribeCurrentMarketForProduct(params,
	                                       casOrigin,
	                                       userId,                                       
	                                       userIor,
	                                       productKey,
	                                       clientListener);
	
	    return groupKeys;
	}

    protected int[] publishRecapV4Unsubscription(String userId, String userIor, int classKey,
                                                 CMIRecapConsumer clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
        MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();

        publisher.unsubscribeRecap(params,
                                   casOrigin,
                                   userId,
                                   userIor,
                                   clientListener);

        return groupKeys;
    }
    
    protected synchronized int[] internalPublishRecapV4UnsubscriptionForProduct(String userId, String userIor, 
    					int classKey, int productKey, CMIRecapConsumer clientListener)
	throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
		
		publisher.unsubscribeRecapForProduct(params,
												casOrigin,
												userId,
												userIor,
												productKey,
												clientListener);		
		return groupKeys;
	}

    protected int[] publishTickerV4Unsubscription(String userId, String userIor, int classKey,
                                                  CMITickerConsumer clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
        MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();

        publisher.unsubscribeTicker(params,
                                    casOrigin,
                                    userId,
                                    userIor,
                                    clientListener);

        return groupKeys;
    }
    
    protected synchronized int[] internalPublishTickerV4UnsubscriptionForProduct(String userId, String userIor, 
    		int classKey, int productKey,CMITickerConsumer clientListener)
	throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
		
		publisher.unsubscribeTickerForProduct(params,
												casOrigin,
												userId,
												userIor,
												productKey,
												clientListener);		
		return groupKeys;
	}
    
    protected int[] publishTickerV4Unsubscription(String userId, String userIor, 
    			int classKey,int productKey, CMITickerConsumer clientListener)
		throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
		
		publisher.unsubscribeTickerForProduct(params,
												casOrigin,
												userId,
												userIor,
												productKey,
												clientListener);
		return groupKeys;
	}
    
    protected int[] publishNBBOV4Unsubscription(String userId, String userIor, int classKey,
            								  CMINBBOConsumer clientListener)
    		throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
		
		publisher.unsubscribeNBBO(params,
								  casOrigin,
								  userId,
								  userIor,
								  clientListener);
		
		return groupKeys;
	}

    protected void republishV4MarketDataRequestsForGroup(int groupKey)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "republishing V4 market data requests for group:"+groupKey);
        }
        try
        {
            republishCurrentMarketV4Requests(groupKey);
            republishRecapV4Requests(groupKey);
            republishTickerV4Requests(groupKey);
            republishNBBOV4Requests(groupKey);
        }
        catch (Exception e)
        {
            Log.exception("error in V4 republishing market date requests to remote cas.",e);
        }
    }

    protected void republishCurrentMarketV4Requests(int groupKey)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        Map marketDataRequests = getCurrentMarketV4SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        MDXSubscriptionInfoContainer subscriptionInfoContainer;
        for(int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (MDXSubscriptionInfoContainer) requests[i];
            
            if(subscriptionInfoContainer.getProductKey() == 0) {
            	publishCurrentMarketV4Subscription(subscriptionInfoContainer.getUserId(),
                                               subscriptionInfoContainer.getUserSessionIOR(),
                                               subscriptionInfoContainer.getClassKey(),
                                               (com.cboe.idl.cmiCallbackV4.CMICurrentMarketConsumer)subscriptionInfoContainer.getCmiConsumer(),
                                               subscriptionInfoContainer.getActionOnQueue(),
                                               subscriptionInfoContainer.isExternalMarketDataEnabled());
            } else {
            	internalPublishCurrentMarketV4SubscriptionForProduct(subscriptionInfoContainer.getUserId(),
                                               subscriptionInfoContainer.getUserSessionIOR(),
                                               subscriptionInfoContainer.getClassKey(),
                                               subscriptionInfoContainer.getProductKey(),
                                               (com.cboe.idl.internalCallback.CurrentMarketManualQuoteConsumer)subscriptionInfoContainer.getCmiConsumer(),
                                               subscriptionInfoContainer.getActionOnQueue(),
                                               subscriptionInfoContainer.isExternalMarketDataEnabled());          	
            }
        }
    }

    protected void republishRecapV4Requests(int groupKey)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        Map marketDataRequests = getRecapV4SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        MDXSubscriptionInfoContainer subscriptionInfoContainer;
        for(int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (MDXSubscriptionInfoContainer) requests[i];
            
            if(subscriptionInfoContainer.getProductKey() == 0) {
	            publishRecapV4Subscription(subscriptionInfoContainer.getUserId(),
	                                       subscriptionInfoContainer.getUserSessionIOR(),
	                                       subscriptionInfoContainer.getClassKey(),
	                                       (CMIRecapConsumer)subscriptionInfoContainer.getCmiConsumer(),
	                                       subscriptionInfoContainer.getActionOnQueue(),
	                                       subscriptionInfoContainer.isExternalMarketDataEnabled());
	        } else {
	        	internalPublishRecapV4SubscriptionForProduct(subscriptionInfoContainer.getUserId(),
	                                       subscriptionInfoContainer.getUserSessionIOR(),
	                                       subscriptionInfoContainer.getClassKey(),
	                                       subscriptionInfoContainer.getProductKey(),
	                                       (CMIRecapConsumer)subscriptionInfoContainer.getCmiConsumer(),
	                                       subscriptionInfoContainer.getActionOnQueue(),
	                                       subscriptionInfoContainer.isExternalMarketDataEnabled());
	        }
	                                      
        }
    }

    protected void republishTickerV4Requests(int groupKey)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        Map marketDataRequests = getTickerV4SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        MDXSubscriptionInfoContainer subscriptionInfoContainer;
        for(int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (MDXSubscriptionInfoContainer) requests[i];
            
            if(subscriptionInfoContainer.getProductKey() == 0) {
	            publishTickerV4Subscription(subscriptionInfoContainer.getUserId(),
	                                        subscriptionInfoContainer.getUserSessionIOR(),
	                                        subscriptionInfoContainer.getClassKey(),
	                                        (CMITickerConsumer)subscriptionInfoContainer.getCmiConsumer(),
	                                        subscriptionInfoContainer.getActionOnQueue(),
	                                        subscriptionInfoContainer.isExternalMarketDataEnabled());
	        } else {
	        	internalPublishTickerV4SubscriptionForProduct(subscriptionInfoContainer.getUserId(),
	                                        subscriptionInfoContainer.getUserSessionIOR(),
	                                        subscriptionInfoContainer.getClassKey(),
	                                        subscriptionInfoContainer.getProductKey(),
	                                        (CMITickerConsumer)subscriptionInfoContainer.getCmiConsumer(),
	                                        subscriptionInfoContainer.getActionOnQueue(),
	                                        subscriptionInfoContainer.isExternalMarketDataEnabled());
	        }
        }
    }
    
    protected void republishNBBOV4Requests(int groupKey)
    		throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
		Map marketDataRequests = getNBBOSubscriptionsByGroupKey(groupKey);
		Object[] requests = marketDataRequests.values().toArray();
		MDXSubscriptionInfoContainer subscriptionInfoContainer;
		for(int i = 0; i < requests.length; i++)
		{
		    subscriptionInfoContainer = (MDXSubscriptionInfoContainer) requests[i];
		    
		    if(subscriptionInfoContainer.getProductKey() == 0) {
			    publishNBBOV4Subscription(subscriptionInfoContainer.getUserId(),
			                            subscriptionInfoContainer.getUserSessionIOR(),
			                            subscriptionInfoContainer.getClassKey(),
			                            (CMINBBOConsumer)subscriptionInfoContainer.getCmiConsumer(),
			                            subscriptionInfoContainer.getActionOnQueue());
			} else {
				internalPublishNBBOV4SubscriptionForProduct(subscriptionInfoContainer.getUserId(),
			                            subscriptionInfoContainer.getUserSessionIOR(),
			                            subscriptionInfoContainer.getClassKey(),
			                            subscriptionInfoContainer.getProductKey(),
			                            (CMINBBOConsumer)subscriptionInfoContainer.getCmiConsumer(),
			                            subscriptionInfoContainer.getActionOnQueue());
			}
		}
	}

    public synchronized void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (channelKey.channelType == ChannelType.EXPRESS_CAS_RECOVERY)
        {
            int groupKey = ((Integer)event.getEventData()).intValue();
            StringBuilder republishing = new StringBuilder(70);
            republishing.append("republishing V4 market data requests for market data group:").append(groupKey);
            Log.information(this, republishing.toString());
            republishV4MarketDataRequestsForGroup(groupKey);
        }
        else
        {
            Log.alarm("MarketDataCallbackRequestPublisherImpl -> Wrong Channel : " + channelKey.channelType);
        }
    }

	public void publishCurrentMarketV4SubscriptionForProduct(Object source, String userId, String userIor, int classKey, int productKey, Object clientListener, short actionOnQueue, boolean disseminateExternalMarketData) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
		int[] groupKeys = internalPublishCurrentMarketV4SubscriptionForProduct(userId, userIor, classKey, productKey,
                (CurrentMarketManualQuoteConsumer) clientListener,
                actionOnQueue, disseminateExternalMarketData);
		// re-using RemoteMarketDataSubscriptionInfoContainer, but with no tradingSession or productKey
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
		                                                         classKey,
		                                                         productKey,
		                                                         "",
		                                                         userId,
		                                                         userIor,
		                                                         clientListener,
		                                                         actionOnQueue,
		                                                         disseminateExternalMarketData);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getCurrentMarketV4SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
		}
		getCurrentMarketV4SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
	}

	

	public void publishCurrentMarketV4UnsubscriptionForProduct(Object source, String userId, String userIor, int classKey, int productKey, Object clientListener) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
		int[] groupKeys = internalPublishCurrentMarketV4UnsubscriptionForProduct(userId, userIor, classKey, productKey,
                (CurrentMarketManualQuoteConsumer)clientListener);
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
		                                                       classKey,
		                                                       productKey,
		                                                       "",
		                                                       userId,
		                                                       userIor,
		                                                       clientListener,
		                                                       (short) 0,
		                                                       false);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getCurrentMarketV4SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
			if(getCurrentMarketV4SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
			{
				currentMarketV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
			}
		}
		getCurrentMarketV4SubscriptionsBySource(source).remove(subscriptionInfo);
		if(getCurrentMarketV4SubscriptionsBySource(source).isEmpty())
		{
			currentMarketV4SubscriptionsBySource.remove(source);
		}
	}

	public void publishRecapV4SubscriptionForProduct(Object source, String userId, String userIor, int classKey, int productKey, Object clientListener, short actionOnQueue, boolean disseminateExternalMarketData) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
		int[] groupKeys = internalPublishRecapV4SubscriptionForProduct(userId, userIor, classKey, productKey,
                (CMIRecapConsumer) clientListener, actionOnQueue, disseminateExternalMarketData);
		// re-using RemoteMarketDataSubscriptionInfoContainer, but with no tradingSession or productKey
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
		                                                                 classKey,
		                                                                 productKey,
		                                                                 "",
		                                                                 userId,
		                                                                 userIor,
		                                                                 clientListener,
		                                                                 actionOnQueue,
		                                                                 disseminateExternalMarketData);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getRecapV4SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
		}
		getRecapV4SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);	
	}

	public void publishRecapV4UnsubscriptionForProduct(Object source, String userId, String userIor, int classKey, int productKey, Object clientListener) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {

		int[] groupKeys = internalPublishRecapV4UnsubscriptionForProduct(userId, userIor, classKey, productKey,
                (CMIRecapConsumer) clientListener);
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
		                                                               classKey,
		                                                               productKey,
		                                                               "",
		                                                               userId,
		                                                               userIor,
		                                                               clientListener,
		                                                               (short) 0,
		                                                               false);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getRecapV4SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
			if(getRecapV4SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
			{
				recapV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
			}
		}
		getRecapV4SubscriptionsBySource(source).remove(subscriptionInfo);
		if(getRecapV4SubscriptionsBySource(source).isEmpty())
		{
			recapV4SubscriptionsBySource.remove(source);
		}
		
	}

	public void publishTickerV4SubscriptionForProduct(Object source, String userId, String userIor, int classKey, int productKey, Object clientListener, short actionOnQueue, boolean disseminateExternalMarketData) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
		int[] groupKeys = internalPublishTickerV4SubscriptionForProduct(userId, userIor, classKey,productKey,
                (CMITickerConsumer) clientListener,
                actionOnQueue, disseminateExternalMarketData);
		// re-using RemoteMarketDataSubscriptionInfoContainer, but with no tradingSession or productKey
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
		                                                                classKey,
		                                                                productKey,
		                                                                "",
		                                                                userId,
		                                                                userIor,
		                                                                clientListener,
		                                                                actionOnQueue,
		                                                                disseminateExternalMarketData);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getTickerV4SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
		}
		getTickerV4SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);		
	}

	public void publishTickerV4UnsubscriptionForProduct(Object source, String userId, String userIor, int classKey, int productKey, Object clientListener) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
        int[] groupKeys = internalPublishTickerV4UnsubscriptionForProduct(userId, userIor, classKey,productKey,
                (CMITickerConsumer) clientListener);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
                                                              classKey,
                                                              productKey,
                                                              "",
                                                              userId,
                                                              userIor,
                                                              clientListener,
                                                              (short) 0,
                                                              false);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getTickerV4SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
			if(getTickerV4SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
			{
			tickerV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
			}
		}
		getTickerV4SubscriptionsBySource(source).remove(subscriptionInfo);
		if(getTickerV4SubscriptionsBySource(source).isEmpty())
		{
			tickerV4SubscriptionsBySource.remove(source);
		}
	}

	public void publishNBBOV4SubscriptionForProduct(Object source, String userId, String userIor, int classKey, int productKey, Object clientListener, short actionOnQueue) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
		int[] groupKeys = internalPublishNBBOV4SubscriptionForProduct(userId, userIor, classKey,productKey,
			      (CMINBBOConsumer) clientListener, 
			      actionOnQueue);
		// re-using RemoteMarketDataSubscriptionInfoContainer, but with no tradingSession or productKey
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
		                                            						  classKey,
		                                            						  productKey,
		                                            						  "",
		                                            						  userId,
		                                            						  userIor,
		                                            						  clientListener,
		                                            						  actionOnQueue,
		                                            						  false);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getNBBOSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
		}
			getNBBOV4SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
	}

	public void publishNBBOV4UnsubscriptionForProduct(Object source, String userId, String userIor, int classKey, int productKey, Object clientListener) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
		int[] groupKeys = internalPublishNBBOV4UnsubscriptionForProduct(userId, userIor, classKey, productKey,
                (CMINBBOConsumer) clientListener);
		RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new MDXSubscriptionInfoContainer("",
                                                              classKey,
                                                              productKey,
                                                              "",
                                                              userId,
                                                              userIor,
                                                              clientListener,
                                                              (short) 0,
                                                              false);
		for(int i = 0; i < groupKeys.length; i++)
		{
			getNBBOSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
			if(getNBBOSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
			{
				nbboV4SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
			}
		}
		getNBBOV4SubscriptionsBySource(source).remove(subscriptionInfo);
		if(getNBBOV4SubscriptionsBySource(source).isEmpty())
		{
			nbboV4SubscriptionsBySource.remove(source);
		}	
	}

	protected synchronized int[] internalPublishNBBOV4UnsubscriptionForProduct(String userId, String userIor, int classKey, int productKey, CMINBBOConsumer clientListener) throws SystemException, CommunicationException, DataValidationException, AuthorizationException {
		
		int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
		RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, "", classKey, (short) 0);
		MarketDataCallbackConsumer publisher = ServicesHelper.getMarketDataCallbackPublisher();
		
		publisher.unsubscribeNBBOForProduct(params,
			  casOrigin,
			  userId,
			  userIor,
			  productKey,
			  clientListener);
		return groupKeys;
	}
   

	
}
