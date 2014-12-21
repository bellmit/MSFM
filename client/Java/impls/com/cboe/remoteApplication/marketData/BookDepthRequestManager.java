package com.cboe.remoteApplication.marketData;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRemoteCASBookDepthConsumerHome;
import com.cboe.remoteApplication.shared.RemoteServicesHelper;
import com.cboe.remoteApplication.shared.RemoteMarketDataSessionNameHelper;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

/**
 * @author Jing Chen
 */
public class BookDepthRequestManager implements EventChannelListener {

    Integer groupKey;
    private IECRemoteCASBookDepthConsumerHome bookDepthConsumerHome;
    public BookDepthRequestManager(Integer groupKey)
    {
        this.groupKey = groupKey;
        bookDepthConsumerHome = RemoteServicesHelper.getRemoteCASBookDepthConsumerHome();
        subscribeForEvents();
    }

    private void subscribeForEvents()
    {
        String[] sessionNames = RemoteMarketDataSessionNameHelper.getLocalFilterOnlySessions();
        try
        {
            ChannelKey channelKey;
            
            for(int i = 0; i < sessionNames.length; i++)
            {
                SessionKeyContainer sessionGroupKey = new SessionKeyContainer(sessionNames[i], groupKey.intValue());
                
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                bookDepthConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                bookDepthConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                bookDepthConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                bookDepthConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                bookDepthConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                bookDepthConsumerHome.addFilter(channelKey);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    public void cleanUp()
    {
        String[] sessionNames = RemoteMarketDataSessionNameHelper.getLocalFilterOnlySessions();        
        try
        {
            ChannelKey channelKey;
            for(int i = 0; i < sessionNames.length; i++)
            {
                SessionKeyContainer sessionGroupKey = new SessionKeyContainer(sessionNames[i], groupKey.intValue());

                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                bookDepthConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                bookDepthConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                bookDepthConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                bookDepthConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                bookDepthConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                bookDepthConsumerHome.removeFilter(channelKey);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    private void subscribeBookDepthByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeBookDepthForClassV2(sessionName, classKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeBookDepthByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeBookDepthForProductV2(sessionName, classKey, productKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeBookDepthByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeBookDepthForClassV2(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeBookDepthByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeBookDepthForProductV2(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeBookDepthByProduct(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeBookDepthForProduct(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeBookDepthByProduct(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMIOrderBookConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeBookDepthForProduct(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        SessionKeyContainer sessionGroupKey = (SessionKeyContainer) channelKey.key;
        if (Log.isDebugOn())
        {
            Log.debug("BookDepthRequestManager:" + this + " -->  ChannelUpdate " + channelKey.channelType + ": sessionName=" + sessionGroupKey.getSessionName());
        }
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) event.getEventData();
        switch (channelKey.channelType)
        {
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2:
                subscribeBookDepthByClassV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer)subscriptionInfo.getCmiConsumer(),
                        subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2:
                subscribeBookDepthByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer)subscriptionInfo.getCmiConsumer(),
                        subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_CLASS_V2:
                unsubscribeBookDepthByClassV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT_V2:
                unsubscribeBookDepthByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIOrderBookConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.SUBSCRIBE_BOOK_DEPTH_BY_PRODUCT:
                subscribeBookDepthByProduct(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMIOrderBookConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_BOOK_DEPTH_BY_PRODUCT:
                unsubscribeBookDepthByProduct(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMIOrderBookConsumer)subscriptionInfo.getCmiConsumer());
                break;
            default :
                if (Log.isDebugOn())
                {
                    Log.debug("Wrong Channel : " + channelKey.channelType);
                }
            break;
        }
    }
}
