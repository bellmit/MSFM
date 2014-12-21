package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.util.ProductStatusGroupUpdateProductContainer;
import com.cboe.interfaces.events.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.util.*;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.product.*;

public class ProductStatusConsumerIECImpl extends BObject implements ProductStatusConsumer{
    private InstrumentedEventChannelAdapter internalEventChannel = null;
    private EventChannelAdapter iec  = null;
    private static final Integer INT_0 = 0;
    /**
     * constructor comment.
     */
    public ProductStatusConsumerIECImpl() {
        super();
        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
        iec = EventChannelAdapterFactory.find();
    }

    public void priceAdjustmentAppliedNotice(PendingAdjustmentStruct appliedAdjustment) {
        if (Log.isDebugOn()) {
            Log.debug(this, "event received -> priceAdjustmentAppliedNotice : " + appliedAdjustment.classKey );
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_PRICE_ADJUSTMENT_APPLIED_NOTICE, Integer.valueOf(appliedAdjustment.classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, appliedAdjustment);
        internalEventChannel.dispatch(event);
    }

    public void priceAdjustmentUpdatedNotice(PendingAdjustmentStruct updateAdjustment) {
        if (Log.isDebugOn()) {
            Log.debug(this, "event received -> priceAdjustmentUpdateNotice : " + updateAdjustment.classKey);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_PRICE_ADJUSTMENT_UPDATED_NOTICE, Integer.valueOf(updateAdjustment.classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, updateAdjustment);
        internalEventChannel.dispatch(event);
    }

    public void allAdjustmentsAppliedNotice() {
        if (Log.isDebugOn()) {
            Log.debug(this, "event received -> allAdjustmentsAppliedNotice");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_ALL_ADJUSTMENTS_APPLIED_NOTICE, INT_0);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, channelKey);
        internalEventChannel.dispatch(event);
    }

    public void updateProduct(ProductStruct product, ProductInformationStruct productInformation) {
        if (Log.isDebugOn()) {
            Log.debug(this, "event received -> updateProduct : " + product.productKeys.productKey);
        }
        // Dispatch to CAS will continue with old way updateProduct(ProductStruct)
        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, Integer.valueOf(product.productKeys.productKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, product);
        internalEventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS, INT_0);
        event = internalEventChannel.getChannelEvent(this, channelKey, product);
        internalEventChannel.dispatch(event);

        // Dispatch to SACAS use new  updateProduct(ProductStruct,ProductInformationStruct)
        ProductStatusGroupUpdateProductContainer productContainer =
                new ProductStatusGroupUpdateProductContainer(product, productInformation);
        
        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT, Integer.valueOf(product.productKeys.productKey));
        event = iec.getChannelEvent(this, channelKey, productContainer);
        iec.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_BY_CLASS, INT_0);
        event = iec.getChannelEvent(this, channelKey, productContainer);
        iec.dispatch(event);

    }

    public void updateReportingClass(ReportingClassStruct reportingClass) {
        if (Log.isDebugOn()) {
            Log.debug(this, "event received -> updateReportingClass : " + reportingClass.productClassKey);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_UPDATE_REPORTING_CLASS, Integer.valueOf(reportingClass.productClassKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, reportingClass);
        internalEventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_REPORTING_CLASS, INT_0);
        event = internalEventChannel.getChannelEvent(this, channelKey, reportingClass);
        internalEventChannel.dispatch(event);
    }

    public void updateProductClass(ProductClassStruct productClass) {
        if (Log.isDebugOn()) {
            Log.debug(this, "event received -> updateProductClass : " + productClass.info.classKey);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, Integer.valueOf(productClass.info.classKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, productClass);
        internalEventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_UPDATE_PRODUCT_CLASS, INT_0);
        event = internalEventChannel.getChannelEvent(this, channelKey, productClass);
        internalEventChannel.dispatch(event);
    }

    public void updateProductStrategy(StrategyStruct updatedStrategy)
    {
        if (Log.isDebugOn()) {
            Log.information(this, "event received -> updateProductStrategy : " + updatedStrategy.product.productKeys.productKey);
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, Integer.valueOf(updatedStrategy.product.productKeys.productKey));
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, updatedStrategy);
        internalEventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelType.PQS_STRATEGY_UPDATE, INT_0);
        event = internalEventChannel.getChannelEvent(this, channelKey, updatedStrategy);
        internalEventChannel.dispatch(event);
    }


    public void updateQPEIndicator(int classKey, boolean indicator) {
      //internal server event
    }
    public void updateLinkageIndicator(LinkageIndicatorResultStruct[] linkageIndicatorResult) {
	    //internal server event
        if (Log.isDebugOn()) {
            Log.debug(this, "event received -> updateLinkageIndicator ");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.PS_UPDATE_LINKAGE_INDICATOR, INT_0);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, linkageIndicatorResult);
        iec.dispatch(event);

           /* channelKey = new ChannelKey(ChannelType.PS_UPDATE_LINKAGE_INDICATOR, INT_0);
            event = internalEventChannel.getChannelEvent(this, channelKey, linkageIndicatorResult);
            internalEventChannel.dispatch(event); */
    }


    /* (non-Javadoc)
     * @see com.cboe.idl.consumers.ProductUpdateConsumerOperations#updateProductClassV3(com.cboe.idl.product.ProductClassStructV3)
     */
    public void updateProductClassV3(ProductClassStructV3 arg0)
    {
        // TODO Auto-generated method stub
        // TODO To be implemented on OHS EOD/Reporting project

    }

    /* (non-Javadoc)
     * @see com.cboe.idl.consumers.ProductUpdateConsumerOperations#updateProductV4(com.cboe.idl.product.ProductStructV4)
     */
    public void updateProductV4(ProductStructV4 arg0)
    {
        // TODO Auto-generated method stub
        // TODO To be implemented on OHS EOD/Reporting project


    }
    /**
     * @param classKey
     * @param openInterestForProducts
     */
    public void updateProductOpenInterest(int classKey, ProductOpenInterestStruct[] openInterestForProducts)
    {
         // TODO To be implemented for Publishing the event on ProductStatus Channel.
    }


}
