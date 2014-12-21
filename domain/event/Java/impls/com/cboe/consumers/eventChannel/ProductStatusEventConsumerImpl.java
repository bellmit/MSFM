package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.idl.product.*;
import com.cboe.idl.product.ProductInformationStruct;

public class ProductStatusEventConsumerImpl extends com.cboe.idl.events.POA_ProductStatusEventConsumer implements ProductStatusConsumer{
    private ProductStatusConsumer delegate;
    /**
     * constructor comment.
     */
    public ProductStatusEventConsumerImpl(ProductStatusConsumer productStatusConsumer) {
        super();
        delegate = productStatusConsumer;
    }

    public void priceAdjustmentUpdatedNotice(PendingAdjustmentStruct updatedAdjustment) {
        delegate.priceAdjustmentUpdatedNotice(updatedAdjustment);
    }

    public void priceAdjustmentAppliedNotice(PendingAdjustmentStruct appliedAdjustment) {
        delegate.priceAdjustmentAppliedNotice(appliedAdjustment);
    }

    public void allAdjustmentsAppliedNotice() {
        delegate.allAdjustmentsAppliedNotice();
    }

    public void updateProduct(ProductStruct updatedProduct,ProductInformationStruct productInformation) {
        delegate.updateProduct(updatedProduct, productInformation);
    }

    public void updateReportingClass(ReportingClassStruct updatedClass) {
        delegate.updateReportingClass(updatedClass);
    }

    public void updateProductClass(ProductClassStruct updatedClass) {
        delegate.updateProductClass(updatedClass);
    }

    public void updateProductStrategy(StrategyStruct updatedStrategy) {
        delegate.updateProductStrategy(updatedStrategy);
    }

    public void updateQPEIndicator(int classKey, boolean indicator) {
       delegate.updateQPEIndicator(classKey, indicator);
    }
    public void updateLinkageIndicator(com.cboe.idl.product.LinkageIndicatorResultStruct[] linkageIndicatorResult) {
       delegate.updateLinkageIndicator(linkageIndicatorResult);
    }

    public void updateProductClassV3(ProductClassStructV3 productClassStructV3)
    {
       delegate.updateProductClassV3(productClassStructV3);

    }

    public void updateProductV4(ProductStructV4 productStructV4)
    {
       delegate.updateProductV4(productStructV4);

    }

    /**
     * @author Jeff Illian
     */

    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }

    /* (non-Javadoc)
     * @see com.cboe.idl.consumers.ProductUpdateConsumerOperations#updateProductClassV3(com.cboe.idl.product.ProductClassStructV3)
     */

    /**
     * @param classKey
     * @param openInterestForProducts
     */
    public void updateProductOpenInterest(int classKey, ProductOpenInterestStruct[] openInterestForProducts)
    {
         // TODO To be implemented for Publishing the event on ProductStatus Channel.
    }


}
