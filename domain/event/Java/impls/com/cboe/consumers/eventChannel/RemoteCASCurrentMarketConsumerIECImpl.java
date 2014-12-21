package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.RemoteCASCurrentMarketConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;


public class RemoteCASCurrentMarketConsumerIECImpl extends BObject implements RemoteCASCurrentMarketConsumer
{
    private EventChannelAdapter internalEventChannel;

    public RemoteCASCurrentMarketConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

	public void subscribeCurrentMarketForClassV3(
			RoutingParameterStruct routingParameters,
			String casOrigin,
			String userId,
			String userSessionIOR,
			com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener,
			short actionOnQueue)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Consume/IEC Publish subscribeCurrentMarketForClassV3" +
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
			ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_CURRENT_MARKET_BY_CLASS_V3, new SessionKeyContainer(routingParameters.sessionName,routingParameters.groups[i]));
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
			internalEventChannel.dispatch(event);
		}
	}

	public void subscribeCurrentMarketForProductV3(
			RoutingParameterStruct routingParameters,
			String casOrigin,
			String userId,
			String userSessionIOR,
			int productKey,
			com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener,
			short actionOnQueue)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Consume/IEC Publish subscribeCurrentMarketForProductV3" +
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
			ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V3, new SessionKeyContainer(routingParameters.sessionName,routingParameters.groups[i]));
			ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
			internalEventChannel.dispatch(event);
		}
	}

	public void unsubscribeCurrentMarketForClassV3(
			RoutingParameterStruct routingParameters,
			String casOrigin,
			String userId,
			String userSessionIOR,
			com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing unsubscribeCurrentMarketForClassV3" +
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
			ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS_V3, new SessionKeyContainer(routingParameters.sessionName,routingParameters.groups[i]));
			ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
			internalEventChannel.dispatch(event);
		}
	}

	public void unsubscribeCurrentMarketForProductV3(
			RoutingParameterStruct routingParameters,
			String casOrigin,
			String userId,
			String userSessionIOR,
			int productKey,
			com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer clientListener)
	{
		if(Log.isDebugOn())
		{
			Log.debug(this, "Publishing unsubscribeCurrentMarketForProductV3" +
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
			ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V3, new SessionKeyContainer(routingParameters.sessionName, routingParameters.groups[i]));
			ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
			internalEventChannel.dispatch(event);
		}
	}

    public void subscribeCurrentMarketForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeCurrentMarketForClassV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_CURRENT_MARKET_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeCurrentMarketForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener,
            short actionOnQueue)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeCurrentMarketForProductV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeCurrentMarketForClassV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeCurrentMarketForClassV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeCurrentMarketForProductV2(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeCurrentMarketForProductV2" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT_V2, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeCurrentMarketForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeCurrentMarketForClass" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_CURRENT_MARKET_BY_CLASS, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void subscribeCurrentMarketForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Consume/IEC Publish subscribeCurrentMarketForProduct" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.SUBSCRIBE_CURRENT_MARKET_BY_PRODUCT, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeCurrentMarketForClass(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeCurrentMarketForClass" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_CURRENT_MARKET_BY_CLASS, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }

    public void unsubscribeCurrentMarketForProduct(
            RoutingParameterStruct routingParameters,
            String casOrigin,
            String userId,
            String userSessionIOR,
            int productKey,
            com.cboe.idl.cmiCallback.CMICurrentMarketConsumer clientListener)
    {
        if(Log.isDebugOn())
        {
            Log.debug(this, "Publishing unsubscribeCurrentMarketForProduct" +
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
            ChannelKey channelKey = new ChannelKey(ChannelKey.UNSUBSCRIBE_CURRENT_MARKET_BY_PRODUCT, sessionGroupKey);
            ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventContainer);
            internalEventChannel.dispatch(event);
        }
    }
}
