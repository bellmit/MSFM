package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.StrategyStatusSupplierFactory;
import com.cboe.idl.cmiCallback.CMIStrategyStatusConsumer;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/**
 * StrategyStatusConsumerProxy serves as a proxy to the StrategyStatusConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * StrategyStatusSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.internalPresentation.StrategyStatusConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMIStrategyStatusConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version  06/25/1999
 */

public class StrategyStatusConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * StrategyStatusConsumerProxy constructor.
     *
     * @param strategyStatusConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    protected StrategyStatusConsumerProxy(CMIStrategyStatusConsumer strategyStatusConsumer, BaseSessionManager sessionManager )
    {
        super(sessionManager, StrategyStatusSupplierFactory.find(), strategyStatusConsumer);
        interceptor = new StrategyStatusConsumerInterceptor(strategyStatusConsumer);
    }

    /**
     * This method is called by ChannelThreadCommand object.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public void channelUpdate(ChannelEvent event)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this,"calling channelUpdate for " + getSessionManager());
        }
        if (event != null)
        {
            ChannelKey key = (ChannelKey) event.getChannel();
            try
            {
                switch(key.channelType)
                {
                    case ChannelType.CB_STRATEGY_UPDATE:
                        ((StrategyStatusConsumerInterceptor)interceptor).updateProductStrategy((SessionStrategyStruct[])event.getEventData());
                        break;
                    default:
                        break;
                }
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                lostConnection(event);
            }
        }
        else
        {
            Log.information(this, "Null event");
        }
    }

    public String getMethodName(ChannelEvent event)
    {
        return "updateProductStrategy";
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.STRATEGY_STATUS;
    }
}
