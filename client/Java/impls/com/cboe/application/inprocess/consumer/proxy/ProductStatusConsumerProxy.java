package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.application.supplier.proxy.ProductStatusCollectorProxy;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.callback.ProductStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */

public class ProductStatusConsumerProxy extends ProductStatusCollectorProxy
{
    protected ProductStatusConsumer productStatusConsumer;

    public ProductStatusConsumerProxy(ProductStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        super(null, sessionManager, consumer);
        setHashKey(consumer);
        this.productStatusConsumer = consumer;
    }

    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"Got channel update " + event);
        }
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        switch (channelKey.channelType)
        {
            case ChannelType.SET_PRODUCT_STATE :
                ProductStateStruct[] productStates = (ProductStateStruct[]) event.getEventData();
                productStatusConsumer.acceptProductState(productStates);
                break;
            case ChannelType.UPDATE_PRODUCT :
                productStatusConsumer.updateProduct((SessionProductStruct) event.getEventData());
                break;
            case ChannelType.UPDATE_PRODUCT_BY_CLASS :
                productStatusConsumer.updateProduct((SessionProductStruct) event.getEventData());
                break;
            default :
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Wrong Channel : " + channelKey.channelType);
                }
        }
    }
}
