package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASBookDepthUpdateConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;


public class RemoteCASBookDepthUpdateConsumerIECImpl extends BObject implements RemoteCASBookDepthUpdateConsumer
{
    private EventChannelAdapter internalEventChannel;

    public RemoteCASBookDepthUpdateConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void subscribeBookDepthUpdateForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeBookDepthUpdateForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, 0/*productKey - not applicable*/,
                     casOrigin, userId, userSessionIOR, clientListener, actionOnQueue);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeBookDepthUpdateForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeBookDepthUpdateForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +
                    "; actionOnQueue=" + actionOnQueue + ".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, productKey,
                     casOrigin, userId, userSessionIOR, clientListener, actionOnQueue);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeBookDepthUpdateForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthUpdateForClassV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, 0/*productKey - not applicable*/,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0 /*action on queue - not applicable*/);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeBookDepthUpdateForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIOrderBookUpdateConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthUpdateForProductV2" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, productKey,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0/* action on queue - not applicable */);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeBookDepthUpdateForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeBookDepthUpdateForProduct" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, productKey,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeBookDepthUpdateForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMIOrderBookUpdateConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeBookDepthUpdateForProduct" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productKey=" + productKey +
                    "; productType=" + routingParameters.productType +".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, productKey,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0/* action on queue - not applicable */);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_ORDER_BOOK_UPDATE_BY_PRODUCT, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

}
