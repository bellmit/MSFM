package com.cboe.remoteApplication.marketData;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRemoteCASRecapConsumerHome;
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
public class RecapRequestManager implements EventChannelListener {

    private Integer groupKey;
    private IECRemoteCASRecapConsumerHome recapConsumerHome;

    public RecapRequestManager(Integer groupKey)
    {
        this.groupKey = groupKey;
        recapConsumerHome = RemoteServicesHelper.getRemoteCASRecapConsumerHome();
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
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_RECAP_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                recapConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                recapConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                recapConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                recapConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_RECAP_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                recapConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                recapConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                recapConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                recapConsumerHome.addFilter(channelKey);
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
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_RECAP_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                recapConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                recapConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                recapConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                recapConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_RECAP_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                recapConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                recapConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                recapConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                recapConsumerHome.removeFilter(channelKey);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    private void subscribeRecapByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeRecapForClassV2(sessionName, classKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeRecapByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeRecapForProductV2(sessionName, classKey, productKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeRecapByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeRecapForClassV2(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeRecapByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIRecapConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeRecapForProductV2(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeRecapByClass(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeRecapForClass(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeRecapByProduct(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeRecapForProduct(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeRecapByClass(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeRecapForClass(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeRecapByProduct(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMIRecapConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeRecapForProduct(sessionName, classKey, productKey, clientListener);
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
            Log.debug("RecapRequestManager:" + this + " -->  ChannelUpdate " + channelKey.channelType + ": sessionName=" + sessionGroupKey.getSessionName());
        }
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) event.getEventData();
        switch (channelKey.channelType)
        {
            case ChannelType.SUBSCRIBE_RECAP_BY_CLASS_V2:
                subscribeRecapByClassV2(subscriptionInfo.getSessionName(),
                            subscriptionInfo.getClassKey(),
                            subscriptionInfo.getCasOrigin(),
                            subscriptionInfo.getUserId(),
                            subscriptionInfo.getUserSessionIOR(),
                            (com.cboe.idl.cmiCallbackV2.CMIRecapConsumer)subscriptionInfo.getCmiConsumer(),
                            subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT_V2:
                subscribeRecapByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIRecapConsumer)subscriptionInfo.getCmiConsumer(),
                        subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS_V2:
                unsubscribeRecapByClassV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIRecapConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT_V2:
                unsubscribeRecapByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIRecapConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.SUBSCRIBE_RECAP_BY_CLASS:
                subscribeRecapByClass(subscriptionInfo.getSessionName(),
                            subscriptionInfo.getClassKey(),
                            subscriptionInfo.getCasOrigin(),
                            subscriptionInfo.getUserId(),
                            subscriptionInfo.getUserSessionIOR(),
                            (com.cboe.idl.cmiCallback.CMIRecapConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.SUBSCRIBE_RECAP_BY_PRODUCT:
                subscribeRecapByProduct(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMIRecapConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_RECAP_BY_CLASS:
                unsubscribeRecapByClass(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMIRecapConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_RECAP_BY_PRODUCT:
                unsubscribeRecapByProduct(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMIRecapConsumer)subscriptionInfo.getCmiConsumer());
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
