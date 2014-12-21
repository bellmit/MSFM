package com.cboe.remoteApplication.marketData;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRemoteCASNBBOConsumerHome;
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
public class NBBORequestManager implements EventChannelListener {

    private Integer groupKey;
    private IECRemoteCASNBBOConsumerHome nbboConsumerHome;

    public NBBORequestManager(Integer groupKey)
    {
        this.groupKey = groupKey;
        nbboConsumerHome = RemoteServicesHelper.getRemoteCASNBBOConsumerHome();
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
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_NBBO_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                nbboConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                nbboConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                nbboConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                nbboConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_NBBO_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                nbboConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                nbboConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                nbboConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                nbboConsumerHome.addFilter(channelKey);
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

                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_NBBO_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                nbboConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                nbboConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                nbboConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                nbboConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_NBBO_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                nbboConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                nbboConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                nbboConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                nbboConsumerHome.removeFilter(channelKey);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    private void subscribeNBBOByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeNBBOForClassV2(sessionName, classKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeNBBOByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeNBBOForProductV2(sessionName, classKey, productKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeNBBOByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeNBBOForClassV2(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeNBBOByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeNBBOForProductV2(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeNBBOByClass(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeNBBOForClass(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeNBBOByProduct(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeNBBOForProduct(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeNBBOByClass(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeNBBOForClass(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeNBBOByProduct(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeNBBOForProduct(sessionName, classKey, productKey, clientListener);
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
            Log.debug("NBBORequestManager:" + this + " -->  ChannelUpdate " + channelKey.channelType + ": sessionName=" + sessionGroupKey.getSessionName());
        }
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) event.getEventData();
        switch (channelKey.channelType)
        {
            case ChannelType.SUBSCRIBE_NBBO_BY_CLASS_V2:
                subscribeNBBOByClassV2(subscriptionInfo.getSessionName(),
                            subscriptionInfo.getClassKey(),
                            subscriptionInfo.getCasOrigin(),
                            subscriptionInfo.getUserId(),
                            subscriptionInfo.getUserSessionIOR(),
                            (com.cboe.idl.cmiCallbackV2.CMINBBOConsumer)subscriptionInfo.getCmiConsumer(),
                            subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT_V2:
                subscribeNBBOByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMINBBOConsumer)subscriptionInfo.getCmiConsumer(),
                        subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS_V2:
                unsubscribeNBBOByClassV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMINBBOConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT_V2:
                unsubscribeNBBOByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMINBBOConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.SUBSCRIBE_NBBO_BY_CLASS:
                subscribeNBBOByClass(subscriptionInfo.getSessionName(),
                            subscriptionInfo.getClassKey(),
                            subscriptionInfo.getCasOrigin(),
                            subscriptionInfo.getUserId(),
                            subscriptionInfo.getUserSessionIOR(),
                            (com.cboe.idl.cmiCallback.CMINBBOConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.SUBSCRIBE_NBBO_BY_PRODUCT:
                subscribeNBBOByProduct(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMINBBOConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_NBBO_BY_CLASS:
                unsubscribeNBBOByClass(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMINBBOConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_NBBO_BY_PRODUCT:
                unsubscribeNBBOByProduct(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMINBBOConsumer)subscriptionInfo.getCmiConsumer());
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
