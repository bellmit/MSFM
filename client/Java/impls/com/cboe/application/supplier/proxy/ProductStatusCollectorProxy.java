package com.cboe.application.supplier.proxy;

import com.cboe.application.supplier.ProductStatusCollectorSupplierFactory;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.idl.product.LinkageIndicatorResultStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductStatusCollector;
import com.cboe.interfaces.domain.SupplierProxyMessageTypes;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

/**
 * QuoteStatusConsumerProxy serves as a proxy to the QuoteStatusConsumer
 * object on the presentation side in com.cboe.consumers.callback.  The
 * QuoteStatusSupplier on the CAS uses this proxy object to communicate to
 * the GUI callback object.
 *
 * @see com.cboe.consumers.callback.QuoteStatusConsumerImpl
 * @see com.cboe.idl.cmiCallback.CMIQuoteStatusConsumer
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class ProductStatusCollectorProxy extends InstrumentedCollectorProxy
{
    // the CORBA callback object.
    private ProductStatusCollector productStatusConsumer;

    /**
     * ProductStatusCollectorProxy constructor.
     *
     * @param productStatusConsumer a reference to the proxied implementation object.
     * @param hashKey - object to supply hash code for BaseSupplierProxy hash table usage
     */
    public ProductStatusCollectorProxy(ProductStatusCollector productStatusConsumer, BaseSessionManager sessionManager, Object hashKey )
    {
        super( sessionManager, ProductStatusCollectorSupplierFactory.find(), hashKey );
        setHashKey(productStatusConsumer);
        this.productStatusConsumer = productStatusConsumer;
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
            Log.debug(this,"Got channel update " + event);
        }
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        if (productStatusConsumer != null)
        {
            switch (channelKey.channelType)
            {
                case ChannelType.SET_PRODUCT_STATE :
                    productStatusConsumer.setProductState((ProductStateStruct[])event.getEventData());
                    break;
                case ChannelType.SET_CLASS_STATE :
                    productStatusConsumer.setClassState((ClassStateStruct)event.getEventData());
                    break;
                case ChannelType.UPDATE_PRODUCT :
                    productStatusConsumer.updateProduct((SessionProductStruct) event.getEventData());
                    break;
                case ChannelType.UPDATE_PRODUCT_BY_CLASS :
                    productStatusConsumer.updateProduct((SessionProductStruct) event.getEventData());
                    break;
                case ChannelType.UPDATE_PRODUCT_CLASS :
                    productStatusConsumer.updateProductClass((SessionClassStruct) event.getEventData());
                    break;
                case ChannelType.STRATEGY_UPDATE:
                    productStatusConsumer.updateProductStrategy((SessionStrategyStruct) event.getEventData());
                    break;
                default :
                    if (Log.isDebugOn())
                    {
                        Log.debug(this, "Wrong Channel : " + channelKey.channelType);
                    }
            }
        }
    }

    public CallbackInformationStruct getCallbackInformationStruct(ChannelEvent event)
    {
        return null;
    }
    public void startMethodInstrumentation(boolean privateOnly){};
    public void stopMethodInstrumentation(){};

    public String getMessageType()
    {
        return SupplierProxyMessageTypes.PRODUCT_STATUS;
    }
}
