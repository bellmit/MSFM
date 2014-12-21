package com.cboe.remoteApplication.marketData;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRemoteCASExpectedOpeningPriceConsumerHome;
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
public class ExpectedOpeningPriceRequestManager implements EventChannelListener {

    private Integer groupKey;
    private IECRemoteCASExpectedOpeningPriceConsumerHome eopConsumerHome;

    public ExpectedOpeningPriceRequestManager(Integer groupKey)
    {
        this.groupKey = groupKey;
        eopConsumerHome = RemoteServicesHelper.getRemoteCASExpectedOpeningPriceConsumerHome();
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
                
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                eopConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                eopConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                eopConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                eopConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                eopConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                eopConsumerHome.addFilter(channelKey);
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

                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                eopConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                eopConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                eopConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                eopConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                eopConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                eopConsumerHome.removeFilter(channelKey);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    protected void subscribeExpectedOpeningPriceByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeExpectedOpeningPriceForClassV2(sessionName, classKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    protected void subscribeExpectedOpeningPriceByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeExpectedOpeningPriceForProductV2(sessionName, classKey, productKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    protected void unsubscribeExpectedOpeningPriceByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeExpectedOpeningPriceForClassV2(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    protected void unsubscribeExpectedOpeningPriceByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeExpectedOpeningPriceForProductV2(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    protected void subscribeExpectedOpeningPriceByClass(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeExpectedOpeningPriceForClass(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    protected void unsubscribeExpectedOpeningPriceByClass(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeExpectedOpeningPriceForClass(sessionName, classKey, clientListener);
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
            Log.debug("ExpectedOpeningPriceRequestManager:" + this + " -->  ChannelUpdate " + channelKey.channelType + ": sessionName= " + sessionGroupKey.getSessionName());
        }
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) event.getEventData();
        switch (channelKey.channelType)
        {
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2:
                subscribeExpectedOpeningPriceByClassV2(subscriptionInfo.getSessionName(),
                            subscriptionInfo.getClassKey(),
                            subscriptionInfo.getCasOrigin(),
                            subscriptionInfo.getUserId(),
                            subscriptionInfo.getUserSessionIOR(),
                            (com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer)subscriptionInfo.getCmiConsumer(),
                            subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2:
                subscribeExpectedOpeningPriceByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer)subscriptionInfo.getCmiConsumer(),
                        subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2:
                unsubscribeExpectedOpeningPriceByClassV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2:
                unsubscribeExpectedOpeningPriceByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS:
                subscribeExpectedOpeningPriceByClass(subscriptionInfo.getSessionName(),
                            subscriptionInfo.getClassKey(),
                            subscriptionInfo.getCasOrigin(),
                            subscriptionInfo.getUserId(),
                            subscriptionInfo.getUserSessionIOR(),
                            (com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS:
                unsubscribeExpectedOpeningPriceByClass(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer)subscriptionInfo.getCmiConsumer());
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
