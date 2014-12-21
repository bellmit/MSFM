package com.cboe.application.supplier.proxy;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiCallbackV2.CMIRecapConsumer;
import com.cboe.idl.cmiConstants.QueueActions;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.application.supplier.RecapV2SupplierFactory;
import com.cboe.util.channel.ChannelEvent;

/**
 * RecapV2ConsumerProxy serves as a proxy to the RecapConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * RecapSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.internalPresentation.RecapConsumerImpl
 * @see com.cboe.idl.cmiCallbackV2.CMIRecapConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class RecapV2ConsumerProxy extends InstrumentedConsumerProxy
{

    /**
     * RecapV2ConsumerProxy constructor.
     *
     * @param recapConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public RecapV2ConsumerProxy(CMIRecapConsumer recapConsumer, BaseSessionManager sessionManager, short queuePolicy)
    {
        super(sessionManager, RecapV2SupplierFactory.find(sessionManager), recapConsumer, queuePolicy);
        interceptor = new RecapV2ConsumerInterceptor(recapConsumer);
    }
    public CMIRecapConsumer getRecapConsumer()
    {
        return ((RecapV2ConsumerInterceptor)interceptor).cmiObject;
    }

    public RecapV2ConsumerInterceptor getRecapConsumerInterceptor()
    {
        return (RecapV2ConsumerInterceptor)interceptor;
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
                ((RecapV2ConsumerInterceptor)interceptor).acceptRecap((RecapStruct[])event.getEventData(),
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
        return "acceptRecap";
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.RECAP;
    }
}
