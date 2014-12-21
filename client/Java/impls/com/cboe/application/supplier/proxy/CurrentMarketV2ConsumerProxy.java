package com.cboe.application.supplier.proxy;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.supplier.CurrentMarketV2SupplierFactory;
import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtensionFactory;
import com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.ListenerProxyQueueControl;

/**
 * CurrentMarketV2ConsumerProxy serves as a SessionManager managed proxy to
 * the UserCurrentMarketConsumer object on the presentation side in
 * com.cboe.presentation.consumer.  The CurrentMarketSupplier on the CAS uses
 * this proxy object to communicate to the GUI callback object.  If a connection
 * to the presentation side consumer fails the <CODE>lostConnection</CODE> method
 * will be called letting the SessionManager this consumer reference is no longer
 * valid.
 *
 * @see com.cboe.consumers.internalPresentation.CurrentMarketConsumerImpl
 * @see com.cboe.idl.cmiCallbackV2.CMICurrentMarketConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/27/1999
 */

public class CurrentMarketV2ConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * CurrentMarketV2ConsumerProxy constructor.
     *
     * @param currentMarketConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public CurrentMarketV2ConsumerProxy(CMICurrentMarketConsumer currentMarketConsumer, BaseSessionManager sessionManager, short queuePolicy)
    {
        super(sessionManager, CurrentMarketV2SupplierFactory.find(sessionManager), currentMarketConsumer, queuePolicy);
        interceptor = new CurrentMarketV2ConsumerInterceptor(currentMarketConsumer);
    }

    public CMICurrentMarketConsumer getCurrentMarketConsumer()
    {
        return ((CurrentMarketV2ConsumerInterceptor)interceptor).cmiObject;
    }

    public CurrentMarketV2ConsumerInterceptor getCurrentMarketConsumerInterceptor()
    {
        return (CurrentMarketV2ConsumerInterceptor)interceptor;
    }

    /**
     * This method is called by ChannelThreadCommand.  It takes the passed
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
                ((CurrentMarketV2ConsumerInterceptor)interceptor).acceptCurrentMarket((CurrentMarketStruct[])event.getEventData(),
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
        return "acceptCurrentMarket";
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.CURRENT_MARKET;
    }
}
