package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.TickerSupplierFactory;
import com.cboe.idl.cmiCallback.CMITickerConsumer;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.util.channel.ChannelEvent;

/**
 * TickerConsumerProxy serves as a proxy to the TickerConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * TickerSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.internalPresentation.TickerConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMITickerConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class TickerConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * TickerConsumerProxy constructor.
     *
     * @param tickerConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    protected TickerConsumerProxy(CMITickerConsumer tickerConsumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, TickerSupplierFactory.find(sessionManager), tickerConsumer);
        interceptor = new TickerConsumerInterceptor(tickerConsumer);
    }

    public CMITickerConsumer getTickerConsumer()
    {
        return ((TickerConsumerInterceptor)interceptor).cmiObject;
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
            boolean disconnect = false;
            try
            {
                if (getProxyWrapper().getQueueSize() > this.getNoActionProxyQueueDepthLimit()) // NEED TO PUT THIS IN THE XML
                {
                    String us = this.toString();
                    StringBuilder discon = new StringBuilder(us.length()+40);
                    discon.append("Disconnection consumer for : ").append(us).append(" Q=").append(getProxyWrapper().getQueueSize());
                    Log.information(this, discon.toString());
                    disconnect = true;
                }
                else
                {
                    // Call the proxied method passing the extracted TickerStruct[] from the EventChannelEvent.
                    ((TickerConsumerInterceptor)interceptor).acceptTicker((TickerStruct[])event.getEventData());
                }
            }
            catch (org.omg.CORBA.TIMEOUT toe)
            {
                Log.exception(this, "session:" + getSessionManager(), toe);
            }
            catch(Exception e)
            {
                Log.exception(this, "session:" + getSessionManager(), e);
                disconnect = true;
            }
            if (disconnect)
            {
                // End connection to client, throw exception
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
