package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.RFQSupplierFactory;
import com.cboe.idl.cmiCallback.CMIRFQConsumer;
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

public class RFQConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * RFQConsumerProxy constructor.
     *
     * @param rfqConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public RFQConsumerProxy(CMIRFQConsumer rfqConsumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, RFQSupplierFactory.find(), rfqConsumer);
        interceptor = new RFQConsumerInterceptor(rfqConsumer);
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
            try
            {
                // Call the proxied method passing the extracted RFQStruct from the EventChannelEvent.
                ((RFQConsumerInterceptor)interceptor).acceptRFQ((RFQStruct)event.getEventData());
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
        return "acceptRFQ";
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.RFQ;
    }
}
