package com.cboe.application.product;

import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.product.*;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.application.supplier.ProductStatusCollectorSupplier;
import com.cboe.application.supplier.ProductStatusCollectorSupplierFactory;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class ProductEventBaseListener implements InstrumentedEventChannelListener
{
    private ProductStatusCollectorSupplier productStatusSupplier;
    private static final Integer INT_0 = 0;

    public ProductEventBaseListener()
    {
        super();

        productStatusSupplier = ProductStatusCollectorSupplierFactory.find();
        subscribeForEvents();
    }

    public void shutdown()
    {
        unsubscribeForEvents();
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this);
        productStatusSupplier = null;
    }

    /**
     * Subscribing for all the Product related Events irrespective of any key
     */
    private void unsubscribeForEvents()
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE, INT_0);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, INT_0);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS, INT_0);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, INT_0);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, INT_0);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_REPORTING_CLASS, INT_0);
        InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);

    }


    /**
     * Subscribing for all the Product related Events irrespective of any key
     */
    private void subscribeForEvents()
    {
        ChannelKey channelKey;

        channelKey = new ChannelKey(ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE, INT_0);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, INT_0);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS, INT_0);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, INT_0);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, INT_0);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_REPORTING_CLASS, INT_0);
        InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

    }

    /**
     * IEC Events delivered to this method implmenting update of cache
     *
     * @param event Description of event
     */
    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();

        if (Log.isDebugOn())
        {
            Log.debug("ProductEventBaseListener.channelUpdate received event " + channelKey + ":" + event.getEventData());
        }

        switch (channelKey.channelType)
        {
            case ChannelType.PQS_UPDATE_PRODUCT_CLASS :
                dispatchClass((ProductClassStruct) event.getEventData());
            break;

            case ChannelType.PQS_UPDATE_PRODUCT :
            case ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS :
                dispatchProduct((ProductStruct) event.getEventData());
            break;

            case ChannelType.PQS_STRATEGY_UPDATE :
                dispatchProductStrategy((StrategyStruct) event.getEventData());
            break;

            case ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE :
                dispatchAllAdjustmentsApplied();
            break;

            case ChannelType.PQS_UPDATE_REPORTING_CLASS :
                dispatchReportingClass((ReportingClassStruct) event.getEventData());
            break;

            default:
                if (Log.isDebugOn())
                {
                    Log.debug("ProductEventBaseListener.channelUpdate does not handle this channel: " + channelKey.channelType);
                }
            break;
        }
    }

    public void queueInstrumentationInitiated()
    { }

    public String getName()
    {
        return "ProductEventBaseListener";
    }

    protected void dispatchClass(ProductClassStruct productClassStruct)
    {
        // Let CAS clients know about it
        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, Integer.valueOf(productClassStruct.info.classKey));
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, productClassStruct);
        productStatusSupplier.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, INT_0);
        event = productStatusSupplier.getChannelEvent(this, channelKey, productClassStruct);
        productStatusSupplier.dispatch(event);
    }

    protected void dispatchProduct(ProductStruct productStruct)
    {
        // Let CAS clients know about it
        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS, Integer.valueOf(productStruct.productKeys.classKey));
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, productStruct);
        productStatusSupplier.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, Integer.valueOf(productStruct.productKeys.productKey));
        event = productStatusSupplier.getChannelEvent(this, channelKey, productStruct);
        productStatusSupplier.dispatch(event);
    }

    protected void dispatchProductStrategy(StrategyStruct strategyStruct)
    {
        // Let CAS clients know about it
        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, Integer.valueOf(strategyStruct.product.productKeys.productKey));
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, strategyStruct);
        productStatusSupplier.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, Integer.valueOf(strategyStruct.product.productKeys.classKey));
        event = productStatusSupplier.getChannelEvent(this, channelKey, strategyStruct);
        productStatusSupplier.dispatch(event);
    }

//currently, this method is not used by ProductQueryManagerImpl
    protected void dispatchReportingClass(ReportingClassStruct reportingClassStruct)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_UPDATE_REPORTING_CLASS, Integer.valueOf(reportingClassStruct.classKey));
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, reportingClassStruct);
        productStatusSupplier.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_REPORTING_CLASS, INT_0);
        event = productStatusSupplier.getChannelEvent(this, channelKey, reportingClassStruct);
        productStatusSupplier.dispatch(event);
    }

    protected void dispatchAllAdjustmentsApplied()
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE, INT_0);
        ChannelEvent event = productStatusSupplier.getChannelEvent(this, channelKey, "");
        productStatusSupplier.dispatch(event);
    }
}