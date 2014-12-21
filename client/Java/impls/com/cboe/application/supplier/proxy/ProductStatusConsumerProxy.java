package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.ProductStatusSupplierFactory;
import com.cboe.idl.cmiCallback.CMIProductStatusConsumer;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/**
 * ProductStatusConsumerProxy serves as a proxy to the ProductStatusConsumer
 * object on the presentation side in com.cboe.presentation.consumer.  The
 * ProductStatusSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.internalPresentation.ProductStatusConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMIProductStatusConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version  06/25/1999
 */

public class ProductStatusConsumerProxy extends InstrumentedConsumerProxy
{

    /**
     * ProductStatusConsumerProxy constructor.
     *
     * @param productStatusConsumer a reference to the proxied implementation object.
     * @param sessionManager a reference to the SessionManager managing subscriptions for this proxy.
     */
    public ProductStatusConsumerProxy(CMIProductStatusConsumer productStatusConsumer, BaseSessionManager sessionManager)
    {
        super(sessionManager, ProductStatusSupplierFactory.find(), productStatusConsumer);
        interceptor = new ProductStatusConsumerInterceptor(productStatusConsumer);
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
                ChannelKey key = (ChannelKey) event.getChannel();
                switch(key.channelType)
                {
                    case ChannelType.CB_PRODUCT_UPDATE:
                        ((ProductStatusConsumerInterceptor)interceptor).updateProduct((SessionProductStruct)event.getEventData());
                        break;

                    case ChannelType.CB_PRODUCT_STATE:
                        ((ProductStatusConsumerInterceptor)interceptor).acceptProductState((ProductStateStruct[])event.getEventData());
                        break;
                    case ChannelType.CB_PRODUCT_UPDATE_BY_CLASS:
                        ((ProductStatusConsumerInterceptor)interceptor).updateProduct((SessionProductStruct)event.getEventData());
                        break;

                    case ChannelType.CB_PRODUCT_STATE_BY_CLASS:
                        ((ProductStatusConsumerInterceptor)interceptor).acceptProductState((ProductStateStruct[])event.getEventData());
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
        String method = "";

        ChannelKey key = (ChannelKey) event.getChannel();

        switch(key.channelType)
        {
            case ChannelType.CB_PRODUCT_UPDATE:
                method = "updateProduct";
                break;

            case ChannelType.CB_PRODUCT_STATE:
                method = "acceptProductState";
                break;
            case ChannelType.CB_PRODUCT_UPDATE_BY_CLASS:
                method = "updateProduct";
                break;

            case ChannelType.CB_PRODUCT_STATE_BY_CLASS:
                method = "acceptProductState";
                break;

            default:
                break;
        }

        return method;
    }

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.PRODUCT_STATUS;
    }
}
