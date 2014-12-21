package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASExpectedOpeningPriceConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

public class RemoteCASExpectedOpeningPriceConsumerIECImpl extends BObject implements RemoteCASExpectedOpeningPriceConsumer
{
    private EventChannelAdapter internalEventChannel;

    public RemoteCASExpectedOpeningPriceConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void subscribeExpectedOpeningPriceForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeExpectedOpeningPriceForClassV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeExpectedOpeningPriceForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeExpectedOpeningPriceForProductV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeExpectedOpeningPriceForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeExpectedOpeningPriceForClassV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeExpectedOpeningPriceForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMIExpectedOpeningPriceConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeExpectedOpeningPriceForProductV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeExpectedOpeningPriceForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeExpectedOpeningPriceForClass" +
                    "; casOrigin= " + casOrigin +
                    "; userId=" + userId +
                    "; session=" + routingParameters.sessionName +
                    "; classKey=" + routingParameters.classKey +
                    "; productType=" + routingParameters.productType + ".");
        }

        RemoteMarketDataSubscriptionInfoContainer eventContainer =
                new RemoteMarketDataSubscriptionInfoContainer(routingParameters.sessionName,
                     routingParameters.classKey, 0/*productKey - not applicable*/,
                     casOrigin, userId, userSessionIOR, clientListener, (short)0);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeExpectedOpeningPriceForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMIExpectedOpeningPriceConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeExpectedOpeningPriceForClass" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_EXPECTED_OPENING_PRICE_BY_CLASS, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

}
