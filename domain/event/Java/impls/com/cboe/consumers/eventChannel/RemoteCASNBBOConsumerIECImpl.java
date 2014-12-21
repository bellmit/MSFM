package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASNBBOConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;


public class RemoteCASNBBOConsumerIECImpl extends BObject implements RemoteCASNBBOConsumer
{
    private EventChannelAdapter internalEventChannel;

    public RemoteCASNBBOConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void subscribeNBBOForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeNBBOForClassV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_NBBO_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeNBBOForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeNBBOForProductV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_NBBO_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeNBBOForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeNBBOForClassV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_NBBO_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeNBBOForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeNBBOForProductV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_NBBO_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeNBBOForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeNBBOForClass" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_NBBO_BY_CLASS, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeNBBOForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeNBBOForProduct" +
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
                     casOrigin, userId, userSessionIOR, clientListener, (short)0);

        for(int i = 0; i < routingParameters.groups.length; i++)
        {
            SessionKeyContainer sessionGroupKey = new SessionKeyContainer(routingParameters.sessionName,  routingParameters.groups[i]);
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_NBBO_BY_PRODUCT, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeNBBOForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeNBBOForClass" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_NBBO_BY_CLASS, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeNBBOForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMINBBOConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeNBBOForProduct" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_NBBO_BY_PRODUCT, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }
}
