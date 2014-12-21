package com.cboe.application.supplier.proxy;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.TickerV2SupplierFactory;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtensionFactory;
import com.cboe.idl.cmiCallbackV2.CMITickerConsumer;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListenerProxy;

/**
 * TickerV2ConsumerProxy serves as a proxy to the TickerConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * TickerSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.internalPresentation.TickerConsumerImpl
 * @see com.cboe.idl.cmiCallbackV2.CMITickerConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class TickerV2ConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * TickerV2ConsumerProxy constructor.
     *
     * @param tickerConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    protected TickerV2ConsumerProxy(CMITickerConsumer tickerConsumer, BaseSessionManager sessionManager, short queuePolicy)
    {
        super(sessionManager, TickerV2SupplierFactory.find(sessionManager), tickerConsumer, queuePolicy);
        interceptor = new TickerV2ConsumerInterceptor(tickerConsumer);
    }

    public CMITickerConsumer getTickerConsumer()
    {
        return ((TickerV2ConsumerInterceptor)interceptor).cmiObject;
    }

    public TickerV2ConsumerInterceptor getTickerConsumerInterceptor()
    {
        return (TickerV2ConsumerInterceptor)interceptor;
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
            short action = QueueActions.NO_ACTION;
            try
            {
                if (getProxyWrapper().getQueueSize() > this.getNoActionProxyQueueDepthLimit()) // NEED TO PUT THIS IN THE XML
                {
                    action = QueueActions.DISCONNECT_CONSUMER;
                    String us = this.toString();
                    StringBuilder discon = new StringBuilder(us.length()+40);
                    discon.append("Disconnection consumer for : ").append(us).append(" Q=").append(getProxyWrapper().getQueueSize());
                    Log.information(this, discon.toString());
                }
                ((TickerV2ConsumerInterceptor)interceptor).acceptTicker((TickerStruct[])event.getEventData(),
                        getProxyWrapper().getQueueSize(), action);
            }
            catch (org.omg.CORBA.TIMEOUT toe)
            {
                Log.exception(this, "session:" + getSessionManager(), toe);
            }
            catch (Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                lostConnection(event);
            }
            if (action == QueueActions.DISCONNECT_CONSUMER)
            {
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
        return "acceptTicker";
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.TICKER;
    }
}
