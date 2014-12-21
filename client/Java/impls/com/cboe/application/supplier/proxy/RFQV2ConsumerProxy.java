package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.RFQV2SupplierFactory;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;

/**
 * RFQConsumerProxy serves as a proxy to the RFQConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * RFQSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.internalPresentation.RFQConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMIRFQConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class RFQV2ConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * RFQV2ConsumerProxy constructor.
     *
     * @param rfqConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public RFQV2ConsumerProxy(com.cboe.idl.cmiCallbackV2.CMIRFQConsumer rfqConsumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, RFQV2SupplierFactory.find(), rfqConsumer);
        interceptor = new RFQV2ConsumerInterceptor(rfqConsumer);
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
                // Queue policy: disconnect if queue gets too large
                if (getProxyWrapper().getQueueSize() > this.getNoActionProxyQueueDepthLimit())
                {
                    action = QueueActions.DISCONNECT_CONSUMER;
                    String us = this.toString();
                    StringBuilder discon = new StringBuilder(us.length()+40);
                    discon.append("Disconnection consumer for : ").append(us).append(" Q=").append(getProxyWrapper().getQueueSize());
                    Log.information(this, discon.toString());
                }
                RFQStruct rfq = (RFQStruct)event.getEventData();
                RFQStruct[] rfqs = new RFQStruct[1];
                rfqs[0] = rfq;
                // Call the proxied method passing the extracted RFQStruct from the EventChannelEvent.
                ((RFQV2ConsumerInterceptor)interceptor).acceptRFQ(rfqs,
                        getProxyWrapper().getQueueSize(), com.cboe.idl.cmiConstants.QueueActions.NO_ACTION);
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
        return "acceptRFQ";
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.RFQ;
    }
}
