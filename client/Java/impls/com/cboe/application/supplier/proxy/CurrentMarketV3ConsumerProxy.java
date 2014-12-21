package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.CurrentMarketV3SupplierFactory;
import com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.CurrentMarketContainer;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelEvent;

/**
 * CurrentMarketV3ConsumerProxy serves as a SessionManager managed proxy to
 * the UserCurrentMarketConsumer object on the presentation side in
 * com.cboe.presentation.consumer.  The CurrentMarketSupplier on the CAS uses
 * this proxy object to communicate to the GUI callback object.  If a connection
 * to the presentation side consumer fails the <CODE>lostConnection</CODE> method
 * will be called letting the SessionManager this consumer reference is no longer
 * valid.
 *
 * @see com.cboe.idl.cmiCallbackV3.CMICurrentMarketConsumer
 */

public class CurrentMarketV3ConsumerProxy extends InstrumentedConsumerProxy
{
    /**
     * CurrentMarketV3ConsumerProxy constructor.
     *
     * @param currentMarketConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public CurrentMarketV3ConsumerProxy(CMICurrentMarketConsumer currentMarketConsumer, BaseSessionManager sessionManager, short queuePolicy)
    {
        super(sessionManager, CurrentMarketV3SupplierFactory.find(sessionManager), currentMarketConsumer, queuePolicy);
        interceptor = new CurrentMarketV3ConsumerInterceptor(currentMarketConsumer);
    }

    public CMICurrentMarketConsumer getCurrentMarketConsumer()
    {
        return ((CurrentMarketV3ConsumerInterceptor)interceptor).cmiObject;
    }

    public CurrentMarketV3ConsumerInterceptor getCurrentMarketConsumerInterceptor()
    {
        return ((CurrentMarketV3ConsumerInterceptor)interceptor);
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
            CurrentMarketStruct[] bestPublicMarketsAtTop = null;
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
                CurrentMarketContainer currentMarketContainer = (CurrentMarketContainer)event.getEventData();
                if (currentMarketContainer.getBestPublicMarketsAtTop() == null)
                {
                    bestPublicMarketsAtTop = com.cboe.client.util.CollectionHelper.EMPTY_CurrentMarketStruct_ARRAY;
                }
                else
                {
                    bestPublicMarketsAtTop = currentMarketContainer.getBestPublicMarketsAtTop();
                }

                ((CurrentMarketV3ConsumerInterceptor)interceptor).acceptCurrentMarket(currentMarketContainer.getBestMarkets(),bestPublicMarketsAtTop,
                                                            getProxyWrapper().getQueueSize(), action);
                /**
                currentMarketConsumer.acceptCurrentMarket((CurrentMarketV2Container[])event.getEventData(),
                        getProxyWrapper().getQueueSize(), action);
                **/

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
