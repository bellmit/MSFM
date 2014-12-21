//
// -----------------------------------------------------------------------------------
// Source file: AdminProductStatusConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.internalPresentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiProduct.PendingAdjustmentStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.product.ProductClassStruct;
import com.cboe.idl.product.ProductStructV4;
import com.cboe.idl.product.ProductClassStructV3;
import com.cboe.idl.product.ProductOpenInterestStruct;
import com.cboe.idl.product.ProductInformationStruct;

import com.cboe.interfaces.events.ProductStatusConsumer;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.presentation.product.ProductClass;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.product.ProductFactoryHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductClassFactoryHome;

import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

/**
 * This is the implementation of the ProductStatusConsumer callback object which
 * receives  event state data from a callback supplier on the CAS and publishes it
 * on a designated event channel.
 */
public class AdminProductStatusConsumerImpl implements ProductStatusConsumer
{
    private EventChannelAdapter eventChannel = null;

    /**
     * @param eventChannel the event channel to publish to.
     */
    public AdminProductStatusConsumerImpl(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;
    }

    public void priceAdjustmentAppliedNotice(PendingAdjustmentStruct appliedAdjustment)
    {
        // NOT IMPLEMENTED CURRENTLY
    }

    public void priceAdjustmentUpdatedNotice(PendingAdjustmentStruct updateAdjustment)
    {
        // NOT IMPLEMENTED CURRENTLY
    }

    public void allAdjustmentsAppliedNotice()
    {
        // NOT IMPLEMENTED CURRENTLY
    }

    public void updateProduct(ProductStruct updatedProduct)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_QUERY))
        {
            Object[] args = new Object[1];
            args[0] = updatedProduct;

            GUILoggerHome.find().debug(this.getClass().getName() + ":updateProduct",
                                       GUILoggerSABusinessProperty.PRODUCT_QUERY,
                                       args);
        }

        ChannelKey key;
        ChannelEvent event;

        Product product = ProductFactoryHome.find().create(updatedProduct);

        key = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, new Integer(updatedProduct.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, product);
        eventChannel.dispatch(event);

        key = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, key, product);
        eventChannel.dispatch(event);
    }

    public void updateReportingClass(ReportingClassStruct reportingClass)
    {
        // NOT IMPLEMENTED CURRENTLY, NEED TO DISCUSS
    }

    public void updateProductClass(ProductClassStruct updatedProductClass)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_QUERY))
        {
            Object[] args = new Object[1];
            args[0] = updatedProductClass;

            GUILoggerHome.find().debug(this.getClass().getName() + ":updateProductClass",
                                       GUILoggerSABusinessProperty.PRODUCT_QUERY,
                                       args);
        }

        ChannelKey channelKey;
        ChannelEvent event;

        ProductClass productClass =  ProductClassFactoryHome.find().create(updatedProductClass.info);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, new Integer(productClass.getProductType()));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, productClass);
        eventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, productClass);
        eventChannel.dispatch(event);
    }

    public void updateProductStrategy(StrategyStruct updatedStrategy)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_QUERY))
        {
            Object[] args = new Object[1];
            args[0] = updatedStrategy;

            GUILoggerHome.find().debug(this.getClass().getName() + ":updateProductStrategy",
                                       GUILoggerSABusinessProperty.PRODUCT_QUERY,
                                       args);
        }

        ChannelKey channelKey;
        ChannelEvent event;

        Product strategy = ProductFactoryHome.find().create(updatedStrategy);

        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, new Integer(updatedStrategy.product.productKeys.classKey));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, strategy);
        eventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, new Integer(0));
        event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, strategy);
        eventChannel.dispatch(event);
    }

    public void updateQPEIndicator(int classKey, boolean indicator) {
       //not implemented here
    }

    public void updateLinkageIndicator(com.cboe.idl.product.LinkageIndicatorResultStruct[] linkageIndicatorResult) {
       //not implemented here
    }
    public void updateProductV4(ProductStructV4 productStructV4) {
        // Todo: inplement (ask Nyoman)
    }

    public void updateProductClassV3(ProductClassStructV3 productClassStructV3) {
        // Todo: implement (ask Nyoman)
    }
    /**
     * @param classKey
     * @param productOpenInterestStructSequence
     */
    public void updateProductOpenInterest(int classKey, ProductOpenInterestStruct[] productOpenInterestStructSequence)
    {
         // TODO To be implemented for Publishing the event on ProductStatus Channel.
    }

    public void updateProduct(ProductStruct productStruct, ProductInformationStruct productInformationStruct)
    {
        if(GUILoggerHome.find().isInformationOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_DEFINITION))
        {
            GUILoggerHome.find().information("AdminProductStatusConsumerImpl.updateProduct(ProductStruct, ProductInformationStruct)",
                                             GUILoggerSABusinessProperty.PRODUCT_DEFINITION, new Object[]{productStruct, productInformationStruct});
        }
        // the ProductInformationStruct isn't used in our caches, so just publishing the "old style" events on the IEC;
        // if anybody needs the CUSIP, closingPrice, closingSuffix, or restrictedProductIndicator they will have to query the ProductQueryService
        updateProduct(productStruct);
    }
}
