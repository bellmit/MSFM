package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.application.supplier.proxy.ProductStatusCollectorProxy;
import com.cboe.idl.cmiSession.ClassStateStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.callback.ClassStatusConsumer;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */

public class ClassStatusConsumerProxy extends ProductStatusCollectorProxy
{
    protected ClassStatusConsumer classStatusConsumer;

    public ClassStatusConsumerProxy(ClassStatusConsumer consumer, BaseSessionManager sessionManager)
    {
        super(null, sessionManager, consumer);
        setHashKey(consumer);
        this.classStatusConsumer = consumer;
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
            case ChannelType.SET_CLASS_STATE :
                ClassStateStruct[] classStates = {(ClassStateStruct)event.getEventData()};
                classStatusConsumer.acceptClassState(classStates);
                break;
            case ChannelType.UPDATE_PRODUCT_CLASS :
                classStatusConsumer.updateProductClass((SessionClassStruct) event.getEventData());
                break;
            default :
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Wrong Channel : " + channelKey.channelType);
                }
        }
    }
}
