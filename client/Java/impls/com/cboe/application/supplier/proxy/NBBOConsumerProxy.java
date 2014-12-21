package com.cboe.application.supplier.proxy;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.NBBOSupplierFactory;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtensionFactory;
import com.cboe.idl.cmiCallback.CMINBBOConsumer;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.ListenerProxyQueueControl;


/**
 * NBBOConsumerProxy serves as a SessionManager managed proxy to
 * the NBBOConsumer object on the presentation side in
 * com.cboe.presentation.consumer.  The NBBOSupplier on the CAS uses
 * this proxy object to communicate to the GUI callback object.  If a connection
 * to the presentation side consumer fails the <CODE>lostConnection</CODE> method
 * will be called letting the SessionManager this consumer reference is no longer
 * valid.
 *
 * @see com.cboe.consumers.internalPresentation.NBBOConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMINBBOConsumer
 *
 * @author Jimmy Wang
 * @version 06/07/2000
 */

public class NBBOConsumerProxy extends InstrumentedConsumerProxy
{

    /**
     * NBBOConsumerProxy constructor.
     *
     * @param consumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public NBBOConsumerProxy(CMINBBOConsumer consumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, NBBOSupplierFactory.find(sessionManager), consumer);
        interceptor = new NBBOConsumerInterceptor(consumer);
    }
    public CMINBBOConsumer getNBBOConsumer()
    {
        return ((NBBOConsumerInterceptor)interceptor).cmiObject;
    }

    /**
     * This method is called by ChannelThreadCommand.  It takes the passed
     * EventChannelEvent, parses out the relevant data for the proxied object,
     * and calls the proxied objects callback method passing in the appropriate
     * data.
     *
     * @param event the ChannelEvent containing the data to send the listener.
     */
    public final void channelUpdate(ChannelEvent event)
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
                    // Call the proxied method passing the extracted CurrentMarketStruct[] from the EventChannelEvent.
                    ((NBBOConsumerInterceptor)interceptor).acceptNBBO((NBBOStruct[])event.getEventData());
                }
            }
            catch (org.omg.CORBA.TIMEOUT toe)
            {
                Log.exception(this, "session:" + getSessionManager(), toe);
            }
            catch (Exception e)
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
        return "acceptNBBO";
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.NBBO;
    }

}