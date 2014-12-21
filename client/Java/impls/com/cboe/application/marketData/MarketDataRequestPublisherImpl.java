package com.cboe.application.marketData;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.proxy.*;
import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.MarketDataRequestPublisher;
import com.cboe.interfaces.events.*;
import com.cboe.interfaces.internalBusinessServices.ProductConfigurationService;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jing Chen
 */
public class MarketDataRequestPublisherImpl extends BObject implements MarketDataRequestPublisher, EventChannelListener
{
    protected ProductConfigurationService productConfigurationService;
    protected String casOrigin;
    protected Map bookDepthV2SubscriptionsBySource;
    protected Map bookDepthV2SubscriptionsByGroup;
    protected Map bookDepthSubscriptionsBySource;
    protected Map bookDepthSubscriptionsByGroup;
    protected Map currentMarketV3SubscriptionsBySource;
    protected Map currentMarketV3SubscriptionsByGroup;
    protected Map currentMarketV2SubscriptionsBySource;
    protected Map currentMarketV2SubscriptionsByGroup;
    protected Map currentMarketSubscriptionsBySource;
    protected Map currentMarketSubscriptionsByGroup;
    protected Map recapV2SubscriptionsBySource;
    protected Map recapV2SubscriptionsByGroup;
    protected Map recapSubscriptionsBySource;
    protected Map recapSubscriptionsByGroup;
    protected Map largeTradeLastSaleSubscriptionsBySource;
    protected Map largeTradelastSaleSubscriptionsByGroup;
    protected Map tickerV2SubscriptionsBySource;
    protected Map tickerV2SubscriptionsByGroup;
    protected Map tickerSubscriptionsBySource;
    protected Map tickerSubscriptionsByGroup;
    protected Map nbboV2SubscriptionsBySource;
    protected Map nbboV2SubscriptionsByGroup;
    protected Map nbboSubscriptionsBySource;
    protected Map nbboSubscriptionsByGroup;
    protected Map eopV2SubscriptionsBySource;
    protected Map eopV2SubscriptionsByGroup;
    protected Map eopSubscriptionsBySource;
    protected Map eopSubscriptionsByGroup;

    protected IECRemoteCASRecoveryConsumerHome remoteCASRecoveryConsumerHome;
    public static final String className = "MarketDataRequestPublisherImpl";

    public MarketDataRequestPublisherImpl()
    {
        super();
        bookDepthV2SubscriptionsBySource = new HashMap();
        bookDepthV2SubscriptionsByGroup = new HashMap();
        currentMarketV3SubscriptionsBySource = new HashMap();
        currentMarketV3SubscriptionsByGroup = new HashMap();
        currentMarketV2SubscriptionsBySource = new HashMap();
        currentMarketV2SubscriptionsByGroup = new HashMap();
        nbboV2SubscriptionsBySource = new HashMap();
        nbboV2SubscriptionsByGroup = new HashMap();
        recapV2SubscriptionsBySource = new HashMap();
        recapV2SubscriptionsByGroup = new HashMap();
        tickerV2SubscriptionsBySource = new HashMap();
        tickerV2SubscriptionsByGroup = new HashMap();
        // VTATS
        largeTradeLastSaleSubscriptionsBySource = new HashMap();
        largeTradelastSaleSubscriptionsByGroup = new HashMap();
        eopV2SubscriptionsBySource = new HashMap();
        eopV2SubscriptionsByGroup = new HashMap();
        bookDepthSubscriptionsBySource = new HashMap();
        bookDepthSubscriptionsByGroup = new HashMap();
        currentMarketSubscriptionsBySource = new HashMap();
        currentMarketSubscriptionsByGroup = new HashMap();
        nbboSubscriptionsBySource = new HashMap();
        nbboSubscriptionsByGroup = new HashMap();
        recapSubscriptionsBySource = new HashMap();
        recapSubscriptionsByGroup = new HashMap();
        tickerSubscriptionsBySource = new HashMap();
        tickerSubscriptionsByGroup = new HashMap();
        eopSubscriptionsBySource = new HashMap();
        eopSubscriptionsByGroup = new HashMap();
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
        Integer zero = 0;
        EventChannelAdapterFactory.find().setDynamicChannels(true);
        ChannelKey channelKey;
        channelKey = new ChannelKey(ChannelType.MDCAS_RECOVERY, zero);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
        
        //for MDX recovery event
        ChannelKey channelKey2;
        channelKey2 = new ChannelKey(ChannelType.EXPRESS_CAS_RECOVERY, zero);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey2);
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
            lookupMap = new HashMap();
            keyTable.put(key, lookupMap);
        }
        return lookupMap;
    }

    private Map getBookDepthV2SubscriptionsBySource(Object source)
    {
        return getMap(bookDepthV2SubscriptionsBySource, source);
    }
    private Map getBookDepthV2SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(bookDepthV2SubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    private Map getCurrentMarketV2SubscriptionsBySource(Object source)
    {
        return getMap(currentMarketV2SubscriptionsBySource, source);
    }
    private Map getCurrentMarketV3SubscriptionsBySource(Object source)
    {
        return getMap(currentMarketV3SubscriptionsBySource, source);
    }
    private Map getCurrentMarketV2SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(currentMarketV2SubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    private Map getCurrentMarketV3SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(currentMarketV3SubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    private Map getNBBOV2SubscriptionsBySource(Object source)
    {
        return getMap(nbboV2SubscriptionsBySource, source);
    }
    private Map getNBBOV2SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(nbboV2SubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    private Map getRecapV2SubscriptionsBySource(Object source)
    {
        return getMap(recapV2SubscriptionsBySource, source);
    }
    private Map getRecapV2SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(recapV2SubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    private Map getTickerV2SubscriptionsBySource(Object source)
    {
        return getMap(tickerV2SubscriptionsBySource, source);
    }
    
    private Map getTickerV2SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(tickerV2SubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    
    private Map getLargeTradeLastSaleSubscriptionsBySource(Object source)
    {
        return getMap(largeTradeLastSaleSubscriptionsBySource, source);
    }
    private Map getLargeTradeLastSaleSubscriptionsByGroupKey(int groupKey)
    {
        return getMap(largeTradelastSaleSubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    
    private Map getEOPV2SubscriptionsBySource(Object source)
    {
        return getMap(eopV2SubscriptionsBySource, source);
    }
    private Map getEOPV2SubscriptionsByGroupKey(int groupKey)
    {
        return getMap(eopV2SubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    private Map getBookDepthSubscriptionsBySource(Object source)
    {
        return getMap(bookDepthSubscriptionsBySource, source);
    }

    private Map getBookDepthSubscriptionsByGroupKey(int groupKey)
    {
        return getMap(bookDepthSubscriptionsByGroup, Integer.valueOf(groupKey));
    }

    private Map getCurrentMarketSubscriptionsBySource(Object source)
    {
        return getMap(currentMarketSubscriptionsBySource, source);
    }

    private Map getCurrentMarketSubscriptionsByGroupKey(int groupKey)
    {
        return getMap(currentMarketSubscriptionsByGroup, Integer.valueOf(groupKey));
    }

    private Map getNBBOSubscriptionsBySource(Object source)
    {
        return getMap(nbboSubscriptionsBySource, source);
    }

    private Map getNBBOSubscriptionsByGroupKey(int groupKey)
    {
        return getMap(nbboSubscriptionsByGroup, Integer.valueOf(groupKey));
    }

    private Map getRecapSubscriptionsBySource(Object source)
    {
        return getMap(recapSubscriptionsBySource, source);
    }

    private Map getRecapSubscriptionsByGroupKey(int groupKey)
    {
        return getMap(recapSubscriptionsByGroup, Integer.valueOf(groupKey));
    }
    private Map getTickerSubscriptionsBySource(Object source)
    {
        return getMap(tickerSubscriptionsBySource, source);
    }

    private Map getTickerSubscriptionsByGroupKey(int groupKey)
    {
        return getMap(tickerSubscriptionsByGroup, Integer.valueOf(groupKey));
    }

    private Map getEOPSubscriptionsBySource(Object source)
    {
        return getMap(eopSubscriptionsBySource, source);
    }

    private Map getEOPSubscriptionsByGroupKey(int groupKey)
    {
        return getMap(eopSubscriptionsByGroup, Integer.valueOf(groupKey));
    }

    public void publishBookDepthSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishBookDepthSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
    }
        
    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishBookDepthSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishBookDepthSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((BookDepthV2ConsumerProxy)clientListener).getOrderBookConsumer(), actionOnQueue);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                actionOnQueue);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getBookDepthV2SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getBookDepthV2SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishBookDepthUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishBookDepthUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishBookDepthUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishBookDepthUnSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((BookDepthV2ConsumerProxy)clientListener).getOrderBookConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getBookDepthV2SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getBookDepthV2SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                bookDepthV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getBookDepthV2SubscriptionsBySource(source).remove(subscriptionInfo);
        if (getBookDepthV2SubscriptionsBySource(source).isEmpty())
        {
            bookDepthV2SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishBookDepthSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    { 
        internalPublishBookDepthSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishBookDepthSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishBookDepthSubscription(userId, userIor, sessionName, classKey, productKey, ((BookDepthConsumerProxy)clientListener).getOrderBookConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getBookDepthSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getBookDepthSubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishBookDepthUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishBookDepthUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishBookDepthUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishBookDepthUnSubscription(userId, userIor, sessionName, classKey, productKey, ((BookDepthConsumerProxy)clientListener).getOrderBookConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getBookDepthSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getBookDepthSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                bookDepthSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getBookDepthSubscriptionsBySource(source).remove(subscriptionInfo);
        if (getBookDepthSubscriptionsBySource(source).isEmpty())
        {
            bookDepthSubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishCurrentMarketSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishCurrentMarketSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishCurrentMarketSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishCurrentMarketSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((CurrentMarketV2ConsumerProxy)clientListener).getCurrentMarketConsumer(), actionOnQueue);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                actionOnQueue);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getCurrentMarketV2SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getCurrentMarketV2SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishCurrentMarketSubscriptionV3(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishCurrentMarketSubscriptionV3(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishCurrentMarketSubscriptionV3(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishCurrentMarketSubscriptionV3(userId, userIor, sessionName, classKey, productKey, ((CurrentMarketV3ConsumerProxy)clientListener).getCurrentMarketConsumer(), actionOnQueue);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                actionOnQueue);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getCurrentMarketV3SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getCurrentMarketV3SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishCurrentMarketUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishCurrentMarketUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishCurrentMarketUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishCurrentMarketUnSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((CurrentMarketV2ConsumerProxy)clientListener).getCurrentMarketConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getCurrentMarketV2SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getCurrentMarketV2SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                currentMarketV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getCurrentMarketV2SubscriptionsBySource(source).remove(subscriptionInfo);
        if (getCurrentMarketV2SubscriptionsBySource(source).isEmpty())
        {
            currentMarketV2SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishCurrentMarketUnSubscriptionV3(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishCurrentMarketUnSubscriptionV3(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishCurrentMarketUnSubscriptionV3(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishCurrentMarketUnSubscriptionV3(userId, userIor, sessionName, classKey, productKey, ((CurrentMarketV3ConsumerProxy)clientListener).getCurrentMarketConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getCurrentMarketV3SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getCurrentMarketV3SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                currentMarketV3SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getCurrentMarketV3SubscriptionsBySource(source).remove(subscriptionInfo);
        if (getCurrentMarketV3SubscriptionsBySource(source).isEmpty())
        {
            currentMarketV3SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishCurrentMarketSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishCurrentMarketSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishCurrentMarketSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishCurrentMarketSubscription(userId, userIor, sessionName, classKey, productKey, ((CurrentMarketConsumerProxy)clientListener).getCurrentMarketConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getCurrentMarketSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getCurrentMarketSubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishCurrentMarketUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishCurrentMarketUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishCurrentMarketUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishCurrentMarketUnSubscription(userId, userIor, sessionName, classKey, productKey, ((CurrentMarketConsumerProxy)clientListener).getCurrentMarketConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getCurrentMarketSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getCurrentMarketSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                currentMarketSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getCurrentMarketSubscriptionsBySource(source).remove(subscriptionInfo);
        if (getCurrentMarketSubscriptionsBySource(source).isEmpty())
        {
            currentMarketSubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishNBBOSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishNBBOSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishNBBOSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishNBBOSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((NBBOV2ConsumerProxy)clientListener).getNBBOConsumer(), actionOnQueue);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                actionOnQueue);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getNBBOV2SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getNBBOV2SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishNBBOUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishNBBOUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishNBBOUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishNBBOUnSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((NBBOV2ConsumerProxy)clientListener).getNBBOConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getNBBOV2SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getNBBOV2SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                nbboV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getNBBOV2SubscriptionsBySource(source).remove(subscriptionInfo);
        if (getNBBOV2SubscriptionsBySource(source).isEmpty())
        {
            nbboV2SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishNBBOSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishNBBOSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishNBBOSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishNBBOSubscription(userId, userIor, sessionName, classKey, productKey, ((NBBOConsumerProxy)clientListener).getNBBOConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getNBBOSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getNBBOSubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishNBBOUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishNBBOUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishNBBOUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishNBBOUnSubscription(userId, userIor, sessionName, classKey, productKey, ((NBBOConsumerProxy)clientListener).getNBBOConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getNBBOSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getNBBOSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                nbboSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getNBBOSubscriptionsBySource(source).remove(subscriptionInfo);
        if (getNBBOSubscriptionsBySource(source).isEmpty())
        {
            nbboSubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishRecapSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishRecapSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishRecapSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishRecapSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((RecapV2ConsumerProxy)clientListener).getRecapConsumer(), actionOnQueue);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                actionOnQueue);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getRecapV2SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getRecapV2SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishRecapUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishRecapUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishRecapUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishRecapUnSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((RecapV2ConsumerProxy)clientListener).getRecapConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getRecapV2SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getRecapV2SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                recapV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getRecapV2SubscriptionsBySource(source).remove(subscriptionInfo);
        if (getRecapV2SubscriptionsBySource(source).isEmpty())
        {
            recapV2SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishRecapSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishRecapSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishRecapSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishRecapSubscription(userId, userIor, sessionName, classKey, productKey, ((RecapConsumerProxy)clientListener).getRecapConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getRecapSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getRecapSubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishRecapUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishRecapUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishRecapUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishRecapUnSubscription(userId, userIor, sessionName, classKey, productKey, ((RecapConsumerProxy)clientListener).getRecapConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getRecapSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getRecapSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                recapSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getRecapSubscriptionsBySource(source).remove(subscriptionInfo);
        if (getRecapSubscriptionsBySource(source).isEmpty())
        {
            recapSubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishTickerSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishTickerSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishTickerSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishTickerSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((TickerV2ConsumerProxy)clientListener).getTickerConsumer(), actionOnQueue);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                actionOnQueue);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getTickerV2SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getTickerV2SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishTickerUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishTickerUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishTickerUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishTickerUnSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((TickerV2ConsumerProxy)clientListener).getTickerConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getTickerV2SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getTickerV2SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                tickerV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getTickerV2SubscriptionsBySource(source).remove(subscriptionInfo);
        if (getTickerV2SubscriptionsBySource(source).isEmpty())
        {
            tickerV2SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishTickerSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishTickerSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishTickerSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishTickerSubscription(userId, userIor, sessionName, classKey, productKey, ((TickerConsumerProxy)clientListener).getTickerConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                    sessionName,
                    classKey,
                    productKey,
                    "",
                    userId,
                    userIor,
                    clientListener,
                    (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getTickerSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getTickerSubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishTickerUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishTickerUnSubscription(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishTickerUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishTickerUnSubscription(userId, userIor, sessionName, classKey, productKey, ((TickerConsumerProxy)clientListener).getTickerConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                    sessionName,
                    classKey,
                    productKey,
                    "",
                    userId,
                    userIor,
                    clientListener,
                    (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getTickerSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getTickerSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                tickerSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getTickerSubscriptionsBySource(source).remove(subscriptionInfo);
        if (getTickerSubscriptionsBySource(source).isEmpty())
        {
            tickerSubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishExpectedOpeningPriceSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishExpectedOpeningPriceSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener, actionOnQueue);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishExpectedOpeningPriceSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener, short actionOnQueue)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishExpectedOpeningPriceSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((ExpectedOpeningPriceV2ConsumerProxy)clientListener).getExpectedOpeningPriceConsumer(), actionOnQueue);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                    sessionName,
                    classKey,
                    productKey,
                    "",
                    userId,
                    userIor,
                    clientListener,
                    actionOnQueue);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getEOPV2SubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getEOPV2SubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishExpectedOpeningPriceUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishExpectedOpeningPriceUnSubscriptionV2(source, userId, userIor, sessionName, classKey, productKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishExpectedOpeningPriceUnSubscriptionV2(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishExpectedOpeningPriceUnSubscriptionV2(userId, userIor, sessionName, classKey, productKey, ((ExpectedOpeningPriceV2ConsumerProxy)clientListener).getExpectedOpeningPriceConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                    sessionName,
                    classKey,
                    productKey,
                    "",
                    userId,
                    userIor,
                    clientListener,
                    (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getEOPV2SubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getEOPV2SubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                eopV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getEOPV2SubscriptionsBySource(source).remove(subscriptionInfo);
        if (getEOPV2SubscriptionsBySource(source).isEmpty())
        {
            eopV2SubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public void publishExpectedOpeningPriceSubscription(Object source, String userId, String userIor, String sessionName, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishExpectedOpeningPriceSubscription(source, userId, userIor, sessionName, classKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishExpectedOpeningPriceSubscription(Object source, String userId, String userIor, String sessionName, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishExpectedOpeningPriceSubscription(userId, userIor, sessionName, classKey, ((ExpectedOpeningPriceConsumerProxy)clientListener).getExpectedOpeningPriceConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                    sessionName,
                    classKey,
                    0,
                    "",
                    userId,
                    userIor,
                    clientListener,
                    (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getEOPSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getEOPSubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return subscriptionInfo;
    }

    public void publishExpectedOpeningPriceUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        internalPublishExpectedOpeningPriceUnSubscription(source, userId, userIor, sessionName, classKey, clientListener);
    }

    synchronized RemoteMarketDataSubscriptionInfoContainer internalPublishExpectedOpeningPriceUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, Object clientListener)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = publishExpectedOpeningPriceUnSubscription(userId, userIor, sessionName, classKey, ((ExpectedOpeningPriceConsumerProxy)clientListener).getExpectedOpeningPriceConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                    sessionName,
                    classKey,
                    0,
                    "",
                    userId,
                    userIor,
                    clientListener,
                    (short)0);
        for (int i = 0; i<groupKeys.length; i++)
        {
            getEOPSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getEOPSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
                eopSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        getEOPSubscriptionsBySource(source).remove(subscriptionInfo);
        if (getEOPSubscriptionsBySource(source).isEmpty())
        {
            eopSubscriptionsBySource.remove(source);
        }
        return subscriptionInfo;
    }

    public synchronized void removeMarketDataRequestSource(Object source)
    {
        removeBookDepthV2SubscriptionFromGroup(source);
        bookDepthV2SubscriptionsBySource.remove(source);
        removeCurrentMarketV2SubscriptionFromGroup(source);
        currentMarketV2SubscriptionsBySource.remove(source);
        removeCurrentMarketV3SubscriptionFromGroup(source);
        currentMarketV3SubscriptionsBySource.remove(source);
        removeNBBOV2SubscriptionFromGroup(source);
        nbboV2SubscriptionsBySource.remove(source);
        removeRecapV2SubscriptionFromGroup(source);
        recapV2SubscriptionsBySource.remove(source);
        removeTickerV2SubscriptionFromGroup(source);
        tickerV2SubscriptionsBySource.remove(source);
        // VTATS
        removeLTLSSubscriptionFromGroup(source);
        largeTradeLastSaleSubscriptionsBySource.remove(source);
        removeExpectedOpeningPriceV2SubscriptionFromGroup(source);
        eopV2SubscriptionsBySource.remove(source);
        removeBookDepthSubscriptionFromGroup(source);
        bookDepthSubscriptionsBySource.remove(source);
        removeCurrentMarketSubscriptionFromGroup(source);
        currentMarketSubscriptionsBySource.remove(source);
        removeNBBOSubscriptionFromGroup(source);
        nbboSubscriptionsBySource.remove(source);
        removeRecapSubscriptionFromGroup(source);
        recapSubscriptionsBySource.remove(source);
        removeTickerSubscriptionFromGroup(source);
        tickerSubscriptionsBySource.remove(source);
        removeExpectedOpeningPriceSubscriptionFromGroup(source);
        eopSubscriptionsBySource.remove(source);
    }

//BOOKDEPTH
    protected void removeBookDepthV2SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getBookDepthV2SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getBookDepthV2SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getBookDepthV2SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    bookDepthV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishBookDepthSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey , (short)0);
        RemoteCASBookDepthConsumer publisher = ServicesHelper.getRemoteCASBookDepthPublisher();
        if (productKey!= 0)
        {
            publisher.subscribeBookDepthForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener,
                                                 actionOnQueue);
        }
        else
        {
            publisher.subscribeBookDepthForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener,
                                                 actionOnQueue);
        }
        return groupKeys;
    }

    protected int[] publishBookDepthUnSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASBookDepthConsumer publisher = ServicesHelper.getRemoteCASBookDepthPublisher();
        if (productKey!= 0)
        {
            publisher.unsubscribeBookDepthForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeBookDepthForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

    protected void removeBookDepthSubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getBookDepthSubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getBookDepthSubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getBookDepthSubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    bookDepthSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishBookDepthSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASBookDepthConsumer publisher = ServicesHelper.getRemoteCASBookDepthPublisher();
        publisher.subscribeBookDepthForProduct(params,
                                             casOrigin,
                                             userId,
                                             userIor,
                                             productKey,
                                             clientListener);
        return groupKeys;
    }

    protected int[] publishBookDepthUnSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASBookDepthConsumer publisher = ServicesHelper.getRemoteCASBookDepthPublisher();
        publisher.unsubscribeBookDepthForProduct(params,
                                             casOrigin,
                                             userId,
                                             userIor,
                                             productKey,
                                             clientListener);
        return groupKeys;
    }
// CURRENTMARKET
    protected void removeCurrentMarketV2SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getCurrentMarketV2SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getCurrentMarketV2SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getCurrentMarketV2SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    currentMarketV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishCurrentMarketSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASCurrentMarketConsumer publisher = ServicesHelper.getRemoteCASCurrentMarketPublisher();
        if (productKey!= 0)
        {
            publisher.subscribeCurrentMarketForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener,
                                                 actionOnQueue);
        }
        else
        {
            publisher.subscribeCurrentMarketForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener,
                                                 actionOnQueue);
        }
        return groupKeys;
    }

    protected void removeCurrentMarketV3SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getCurrentMarketV3SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getCurrentMarketV3SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getCurrentMarketV3SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    currentMarketV3SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishCurrentMarketSubscriptionV3(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASCurrentMarketConsumer publisher = ServicesHelper.getRemoteCASCurrentMarketPublisher();
        if (productKey!= 0)
        {
            publisher.subscribeCurrentMarketForProductV3(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener,
                                                 actionOnQueue);
        }
        else
        {
            publisher.subscribeCurrentMarketForClassV3(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener,
                                                 actionOnQueue);
        }
        return groupKeys;
    }

    protected int[] publishCurrentMarketUnSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASCurrentMarketConsumer publisher = ServicesHelper.getRemoteCASCurrentMarketPublisher();
        if (productKey!= 0)
        {
            publisher.unsubscribeCurrentMarketForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeCurrentMarketForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

    protected int[] publishCurrentMarketUnSubscriptionV3(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASCurrentMarketConsumer publisher = ServicesHelper.getRemoteCASCurrentMarketPublisher();
        if (productKey!= 0)
        {
            publisher.unsubscribeCurrentMarketForProductV3(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeCurrentMarketForClassV3(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

    protected void removeCurrentMarketSubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getCurrentMarketSubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getCurrentMarketSubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getCurrentMarketSubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    currentMarketSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishCurrentMarketSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASCurrentMarketConsumer publisher = ServicesHelper.getRemoteCASCurrentMarketPublisher();
        if (productKey!= 0)
        {
            publisher.subscribeCurrentMarketForProduct(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.subscribeCurrentMarketForClass(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

    protected int[] publishCurrentMarketUnSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASCurrentMarketConsumer publisher = ServicesHelper.getRemoteCASCurrentMarketPublisher();
        if (productKey!= 0)
        {
            publisher.unsubscribeCurrentMarketForProduct(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeCurrentMarketForClass(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

//NBBO
    protected void removeNBBOV2SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getNBBOV2SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getNBBOV2SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getNBBOV2SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    nbboV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishNBBOSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASNBBOConsumer publisher = ServicesHelper.getRemoteCASNBBOPublisher();
        if (productKey != 0)
        {
            publisher.subscribeNBBOForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener,
                                                 actionOnQueue);
        }
        else
        {
            publisher.subscribeNBBOForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener,
                                                 actionOnQueue);
        }
        return groupKeys;
    }

    protected int[] publishNBBOUnSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASNBBOConsumer publisher = ServicesHelper.getRemoteCASNBBOPublisher();
        if (productKey != 0)
        {
            publisher.unsubscribeNBBOForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeNBBOForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

    protected void removeNBBOSubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getNBBOSubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getNBBOSubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getNBBOSubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    nbboSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishNBBOSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASNBBOConsumer publisher = ServicesHelper.getRemoteCASNBBOPublisher();
        if (productKey != 0)
        {
            publisher.subscribeNBBOForProduct(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.subscribeNBBOForClass(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

    protected int[] publishNBBOUnSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASNBBOConsumer publisher = ServicesHelper.getRemoteCASNBBOPublisher();
        if (productKey != 0)
        {
            publisher.unsubscribeNBBOForProduct(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeNBBOForClass(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

// RECAP
    protected void removeRecapV2SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getRecapV2SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getRecapV2SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getRecapV2SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    recapV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishRecapSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASRecapConsumer publisher = ServicesHelper.getRemoteCASRecapPublisher();
        if (productKey != 0)
        {
            publisher.subscribeRecapForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener,
                                                 actionOnQueue);
        }
        else
        {
            publisher.subscribeRecapForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener,
                                                 actionOnQueue);
        }
        return groupKeys;
    }

    protected int[] publishRecapUnSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASRecapConsumer publisher = ServicesHelper.getRemoteCASRecapPublisher();
        if (productKey != 0)
        {
            publisher.unsubscribeRecapForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeRecapForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }
    protected void removeRecapSubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getRecapSubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getRecapSubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getRecapSubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    recapSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishRecapSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASRecapConsumer publisher = ServicesHelper.getRemoteCASRecapPublisher();
        if (productKey != 0)
        {
            publisher.subscribeRecapForProduct(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.subscribeRecapForClass(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

    protected int[] publishRecapUnSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASRecapConsumer publisher = ServicesHelper.getRemoteCASRecapPublisher();
        if (productKey != 0)
        {
            publisher.unsubscribeRecapForProduct(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeRecapForClass(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

// TICKER
    protected void removeTickerV2SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getTickerV2SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getTickerV2SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getTickerV2SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    tickerV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }
    
    protected void removeLTLSSubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getLargeTradeLastSaleSubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                //getTickerV2SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                getLargeTradeLastSaleSubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
            	if (getLargeTradeLastSaleSubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    largeTradelastSaleSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }
    
    protected int[] publishLTLSSubscription(
    				String userId, 
    				String userIor, 
    				String sessionName, 
    				int classKey, 
    				int productKey, 
    				com.cboe.idl.consumers.TickerConsumer clientListener, 
    				short actionOnQueue)
    throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
	    int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
	    RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
	    RemoteCASTickerConsumer publisher = ServicesHelper.getRemoteCASTickerPublisher();
	    publisher.subscribeLargeTradeLastSaleForClass(params,
	                								  casOrigin,
	                								  userId,
	                								  userIor,
	                								  clientListener,
	                								  actionOnQueue);
	    return groupKeys;
	}
    
    protected int[] publishLTLSUnSubscription(String userId, 
    										  String userIor, 
    										  String sessionName, 
    										  int classKey, 
    										  int productKey, 
    										  com.cboe.idl.consumers.TickerConsumer  clientListener)
    throws DataValidationException, AuthorizationException, SystemException, CommunicationException
	{
	    int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
	    RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
	    RemoteCASTickerConsumer publisher = ServicesHelper.getRemoteCASTickerPublisher();
	    publisher.unsubscribeLargeTradeLastSaleForClass(params,
	                                            		casOrigin,
	                                            		userId,
	                                            		userIor,
	                                            		clientListener);
	    return groupKeys;
	}
    
    protected int[] publishTickerSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASTickerConsumer publisher = ServicesHelper.getRemoteCASTickerPublisher();
        if (productKey!= 0)
        {
            publisher.subscribeTickerForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener,
                                                 actionOnQueue);
        }
        else
        {
            publisher.subscribeTickerForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener,
                                                 actionOnQueue);
        }
        return groupKeys;
    }
    
    

    protected int[] publishTickerUnSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASTickerConsumer publisher = ServicesHelper.getRemoteCASTickerPublisher();
        if (productKey!= 0)
        {
            publisher.unsubscribeTickerForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeTickerForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }
    protected void removeTickerSubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getTickerSubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getTickerSubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getTickerSubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    tickerSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishTickerSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMITickerConsumer  clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASTickerConsumer publisher = ServicesHelper.getRemoteCASTickerPublisher();
        publisher.subscribeTickerForProduct(params,
                                            casOrigin,
                                            userId,
                                            userIor,
                                            productKey,
                                            clientListener);
        return groupKeys;
    }

    protected int[] publishTickerUnSubscription(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallback.CMITickerConsumer  clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASTickerConsumer publisher = ServicesHelper.getRemoteCASTickerPublisher();
        publisher.unsubscribeTickerForProduct(params,
                                             casOrigin,
                                             userId,
                                             userIor,
                                             productKey,
                                             clientListener);
        return groupKeys;
    }

//ExpectedOpeningPrice
    protected void removeExpectedOpeningPriceV2SubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getEOPV2SubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getEOPV2SubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getEOPV2SubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    eopV2SubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishExpectedOpeningPriceSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer  clientListener, short actionOnQueue)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASExpectedOpeningPriceConsumer publisher = ServicesHelper.getRemoteCASExpectedOpeningPricePublisher();
        if (productKey != 0)
        {
            publisher.subscribeExpectedOpeningPriceForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener,
                                                 actionOnQueue);
        }
        else
        {
            publisher.subscribeExpectedOpeningPriceForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener,
                                                 actionOnQueue);
        }
        return groupKeys;
    }

    protected int[] publishExpectedOpeningPriceUnSubscriptionV2(String userId, String userIor, String sessionName, int classKey, int productKey, com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer  clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASExpectedOpeningPriceConsumer publisher = ServicesHelper.getRemoteCASExpectedOpeningPricePublisher();
        if (productKey != 0)
        {
            publisher.unsubscribeExpectedOpeningPriceForProductV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 productKey,
                                                 clientListener);
        }
        else
        {
            publisher.unsubscribeExpectedOpeningPriceForClassV2(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        }
        return groupKeys;
    }

    protected void removeExpectedOpeningPriceSubscriptionFromGroup(Object source)
    {
        Map subscriptionsForSource = getEOPSubscriptionsBySource(source);
        Object[] subscriptions = subscriptionsForSource.keySet().toArray();
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer = null;
        for (int i = 0; i < subscriptions.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)subscriptions[i];
            int[] groupKeys = (int[])subscriptionsForSource.get(subscriptionInfoContainer);
            for(int j=0; j<groupKeys.length; j++)
            {
                getEOPSubscriptionsByGroupKey(groupKeys[j]).remove(subscriptionInfoContainer);
                if (getEOPSubscriptionsByGroupKey(groupKeys[j]).isEmpty())
                {
                    eopSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[j]));
                }
            }
        }
    }

    protected int[] publishExpectedOpeningPriceSubscription(String userId, String userIor, String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASExpectedOpeningPriceConsumer publisher = ServicesHelper.getRemoteCASExpectedOpeningPricePublisher();
        publisher.subscribeExpectedOpeningPriceForClass(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        return groupKeys;
    }

    protected int[] publishExpectedOpeningPriceUnSubscription(String userId, String userIor, String sessionName, int classKey, com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer  clientListener)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        int[] groupKeys = getProductConfigurationService().getGroupKeysForProductClass(classKey);
        RoutingParameterStruct params = new RoutingParameterStruct(groupKeys, sessionName, classKey, (short)0);
        RemoteCASExpectedOpeningPriceConsumer publisher = ServicesHelper.getRemoteCASExpectedOpeningPricePublisher();
        publisher.unsubscribeExpectedOpeningPriceForClass(params,
                                                 casOrigin,
                                                 userId,
                                                 userIor,
                                                 clientListener);
        return groupKeys;
    }

    protected void republishMarketDataRequestsForGroup(int groupKey)
    {
    	if(Log.isDebugOn()){
    		Log.debug(this, className + ":republishing market data requests for group:"+groupKey);
    	}
        try
        {
            republishBookDepthRequestsForGroup(groupKey);
            republishCurrentMarketRequestsForGroup(groupKey);
            republishNBBORequestsForGroup(groupKey);
            republishRecapRequestsForGroup(groupKey);
            republishTickerRequestsForGroup(groupKey);
            republishLargeTradeLastSaleRequestsForGroup(groupKey);
            republishExpectedOpeningPriceRequestsForGroup(groupKey);
        }
        catch (Exception e)
        {
            Log.exception(this, className + ":error in republishing market date requests to remote cas.",e);
        }
    }
    
    protected void republishLargeTradeLastSaleRecoveryRequestsForGroup(int groupKey)
    {
        if(Log.isDebugOn()){
        	Log.debug(this, className + ":republishing LargeTradeLastSale recovery requests for group:"+groupKey);
        }
        try
        {
            republishLargeTradeLastSaleRequestsForGroup(groupKey);
        }
        catch (Exception e)
        {
            Log.exception(this,className + ":error in republishing LargeTradeLastSale requests to remote cas.",e);
        }
    }

    protected void republishBookDepthRequestsForGroup(int groupKey)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        Map marketDataRequests = getBookDepthV2SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        RemoteMarketDataSubscriptionInfoContainer  subscriptionInfoContainer = null;
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishBookDepthSubscriptionV2(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((BookDepthV2ConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getOrderBookConsumer(),
                    subscriptionInfoContainer.getActionOnQueue()
            );
        }
        marketDataRequests = getBookDepthSubscriptionsByGroupKey(groupKey);
        requests = marketDataRequests.values().toArray();
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishBookDepthSubscription(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((BookDepthConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getOrderBookConsumer()
            );
        }
    }

    protected void republishCurrentMarketRequestsForGroup(int groupKey)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        republishV3CurrentMarketRequests(groupKey);
        republishV2CurrentMarketRequests(groupKey);
        republishCurrentMarketRequests(groupKey);
    }

    protected void republishCurrentMarketRequests(int groupKey) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
        Map marketDataRequests;
        Object[] requests;
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer;
        marketDataRequests = getCurrentMarketSubscriptionsByGroupKey(groupKey);
        requests = marketDataRequests.values().toArray();
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishCurrentMarketSubscription(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((CurrentMarketConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getCurrentMarketConsumer()
            );
        }
    }

    protected void republishV2CurrentMarketRequests(int groupKey) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
        Map marketDataRequests;
        Object[] requests;
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfoContainer;
        marketDataRequests = getCurrentMarketV2SubscriptionsByGroupKey(groupKey);
        requests = marketDataRequests.values().toArray();
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishCurrentMarketSubscriptionV2(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((CurrentMarketV2ConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getCurrentMarketConsumer(),
                    subscriptionInfoContainer.getActionOnQueue()
            );
        }
    }

    protected void republishV3CurrentMarketRequests(int groupKey) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
        Map marketDataRequests = getCurrentMarketV3SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        RemoteMarketDataSubscriptionInfoContainer  subscriptionInfoContainer = null;
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishCurrentMarketSubscriptionV3(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((CurrentMarketV3ConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getCurrentMarketConsumer(),
                    subscriptionInfoContainer.getActionOnQueue()
            );
        }
    }

    protected void republishNBBORequestsForGroup(int groupKey)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        Map marketDataRequests = getNBBOV2SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        RemoteMarketDataSubscriptionInfoContainer  subscriptionInfoContainer = null;
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishNBBOSubscriptionV2(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((NBBOV2ConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getNBBOConsumer(),
                    subscriptionInfoContainer.getActionOnQueue()
            );
        }
        marketDataRequests = getNBBOSubscriptionsByGroupKey(groupKey);
        requests = marketDataRequests.values().toArray();
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishNBBOSubscription(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((NBBOConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getNBBOConsumer()
            );
        }
    }

    protected void republishRecapRequestsForGroup(int groupKey)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        Map marketDataRequests = getRecapV2SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        RemoteMarketDataSubscriptionInfoContainer  subscriptionInfoContainer = null;
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishRecapSubscriptionV2(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((RecapV2ConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getRecapConsumer(),
                    subscriptionInfoContainer.getActionOnQueue()
            );
        }
        marketDataRequests = getRecapSubscriptionsByGroupKey(groupKey);
        requests = marketDataRequests.values().toArray();
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishRecapSubscription(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((RecapConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getRecapConsumer()
            );
        }
    }

    protected void republishTickerRequestsForGroup(int groupKey)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        Map marketDataRequests = getTickerV2SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        RemoteMarketDataSubscriptionInfoContainer  subscriptionInfoContainer = null;
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishTickerSubscriptionV2(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((TickerV2ConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getTickerConsumer(),
                    subscriptionInfoContainer.getActionOnQueue()
            );
        }
        marketDataRequests = getTickerSubscriptionsByGroupKey(groupKey);
        requests = marketDataRequests.values().toArray();
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishTickerSubscription(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((TickerConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getTickerConsumer()
            );
        }
    }
    
    protected void republishLargeTradeLastSaleRequestsForGroup(int groupKey)
    	throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
    	Map marketDataRequests = getLargeTradeLastSaleSubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        RemoteMarketDataSubscriptionInfoContainer  subscriptionInfoContainer = null;
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishLTLSSubscription(
            		subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((LargeTradeLastSaleConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getTickerConsumer(),
                    subscriptionInfoContainer.getActionOnQueue()
            );
        }
    }
    
    protected void republishExpectedOpeningPriceRequestsForGroup(int groupKey)
        throws DataValidationException, AuthorizationException, SystemException, CommunicationException
    {
        Map marketDataRequests = getEOPV2SubscriptionsByGroupKey(groupKey);
        Object[] requests = marketDataRequests.values().toArray();
        RemoteMarketDataSubscriptionInfoContainer  subscriptionInfoContainer = null;
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishExpectedOpeningPriceSubscriptionV2(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    subscriptionInfoContainer.getProductKey(),
                    ((ExpectedOpeningPriceV2ConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getExpectedOpeningPriceConsumer(),
                    subscriptionInfoContainer.getActionOnQueue()
            );
        }
        marketDataRequests = getEOPSubscriptionsByGroupKey(groupKey);
        requests = marketDataRequests.values().toArray();
        for (int i = 0; i < requests.length; i++)
        {
            subscriptionInfoContainer = (RemoteMarketDataSubscriptionInfoContainer)requests[i];
            publishExpectedOpeningPriceSubscription(subscriptionInfoContainer.getUserId(),
                    subscriptionInfoContainer.getUserSessionIOR(),
                    subscriptionInfoContainer.getSessionName(),
                    subscriptionInfoContainer.getClassKey(),
                    ((ExpectedOpeningPriceConsumerProxy)subscriptionInfoContainer.getCmiConsumer()).getExpectedOpeningPriceConsumer()
            );
        }
    }

    public void publishLargeTradeLastSaleSubscription(
    			Object source, 
    			String userId, 
    			String userIor, 
    			String sessionName, 
    			int classKey, 
    			int productKey, 
    			Object clientListener, 
    			short actionOnQueue) 
    	throws DataValidationException, AuthorizationException, SystemException, CommunicationException {
    	int[] groupKeys = publishLTLSSubscription(userId, 
    											  userIor, 
    											  sessionName, 
    											  classKey, 
    											  productKey, 
    											  ((LargeTradeLastSaleConsumerProxy)clientListener).getTickerConsumer(), 
    											  actionOnQueue);
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                actionOnQueue);
       
        for (int i = 0; i< groupKeys.length; i++)
        {
            getLargeTradeLastSaleSubscriptionsByGroupKey(groupKeys[i]).put(subscriptionInfo, subscriptionInfo);
        }
        getLargeTradeLastSaleSubscriptionsBySource(source).put(subscriptionInfo, groupKeys);
        return;
	}
    
    public void publishLargeTradeLastSaleUnSubscription(Object source, String userId, String userIor, String sessionName, int classKey, int productKey, Object clientListener) throws DataValidationException, AuthorizationException, SystemException, CommunicationException {

    	int[] groupKeys = publishLTLSUnSubscription(userId, 
    												userIor, 
    												sessionName, 
    												classKey, 
    												productKey, 
    												((LargeTradeLastSaleConsumerProxy)clientListener).getTickerConsumer());
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = new RemoteMarketDataSubscriptionInfoContainer(
                sessionName,
                classKey,
                productKey,
                "",
                userId,
                userIor,
                clientListener,
                (short)0);
        
        for (int i = 0; i< groupKeys.length; i++)
        {
            getLargeTradeLastSaleSubscriptionsByGroupKey(groupKeys[i]).remove(subscriptionInfo);
            if (getLargeTradeLastSaleSubscriptionsByGroupKey(groupKeys[i]).isEmpty())
            {
            	largeTradelastSaleSubscriptionsByGroup.remove(Integer.valueOf(groupKeys[i]));
            }
        }
        
        getLargeTradeLastSaleSubscriptionsBySource(source).remove(subscriptionInfo);
        if (getLargeTradeLastSaleSubscriptionsBySource(source).isEmpty())
        {
        	largeTradeLastSaleSubscriptionsBySource.remove(source);
        }
        return;
	}
	
	
    public synchronized void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (channelKey.channelType == ChannelType.MDCAS_RECOVERY)
        {
            int groupKey = ((Integer)event.getEventData()).intValue();
            StringBuilder republishing = new StringBuilder(className.length()+70);
            republishing.append(className).append(":republishing market data requests for market data group:").append(groupKey);
            Log.information(this, republishing.toString());
            republishMarketDataRequestsForGroup(groupKey);
        }
        else if (channelKey.channelType == ChannelType.EXPRESS_CAS_RECOVERY)
        {
            int groupKey = ((Integer)event.getEventData()).intValue();
            StringBuilder republishing = new StringBuilder(className.length()+85);
            republishing.append(className).append(":republishing LargeTradeLastSale requests for LargeTradeLastSale group:").append(groupKey);
            Log.information(this, republishing.toString());
            republishLargeTradeLastSaleRecoveryRequestsForGroup(groupKey);
        }
        else
        {
            Log.alarm("MarketDataRequestPublisherImpl -> Wrong Channel : " + channelKey.channelType);
        }
    }
	

}
