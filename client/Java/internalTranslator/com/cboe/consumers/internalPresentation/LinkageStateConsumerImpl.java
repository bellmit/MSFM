//
// -----------------------------------------------------------------------------------
// Source file: LinkageStateConsumerImpl.java
//
// PACKAGE: com.cboe.consumers.internalPresentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.internalPresentation;

import com.cboe.idl.cmiProduct.PendingAdjustmentStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.consumers.ProductStatusConsumerPOA;
import com.cboe.idl.product.LinkageIndicatorResultStruct;
import com.cboe.idl.product.ProductClassStruct;
import com.cboe.idl.product.ProductClassStructV3;
import com.cboe.idl.product.ProductStructV4;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

public class LinkageStateConsumerImpl extends ProductStatusConsumerPOA
{
    protected EventChannelAdapter eventChannel = null;

    public LinkageStateConsumerImpl(EventChannelAdapter eventChannel)
    {
        this.eventChannel = eventChannel;
    }

    public void allAdjustmentsAppliedNotice()
    {
    }

    public void priceAdjustmentAppliedNotice(PendingAdjustmentStruct pendingAdjustmentStruct)
    {
    }

    public void priceAdjustmentUpdatedNotice(PendingAdjustmentStruct pendingAdjustmentStruct)
    {
    }

    public void updateLinkageIndicator(LinkageIndicatorResultStruct[] linkageIndicatorResultStructs)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE))
        {
            Object[] args = new Object[1];
            args[0] = linkageIndicatorResultStructs;

            GUILoggerHome.find().debug(
                    this.getClass().getName() + ":updateLinkageIndicator",
                    GUILoggerSABusinessProperty.PRODUCT_MAINTENANCE, args);
        }

        ChannelKey key = new ChannelKey(ChannelType.CB_UPDATE_LINKAGE_INDICATOR, new Integer(0));
        ChannelEvent event =
                EventChannelAdapterFactory.find().getChannelEvent(this, key,
                                                                  linkageIndicatorResultStructs);
        eventChannel.dispatch(event);
    }

    public void updateProduct(ProductStruct productStruct, com.cboe.idl.product.ProductInformationStruct productInformationStruct)
    {
    }

    public void updateProductClass(ProductClassStruct productClassStruct)
    {
    }

    public void updateProductClassV3(ProductClassStructV3 productClassStructV3)
    {
    }

    public void updateProductStrategy(StrategyStruct strategyStruct)
    {
    }

    public void updateProductV4(ProductStructV4 productStructV4)
    {
    }

    public void updateQPEIndicator(int i, boolean b)
    {
    }

    public void updateReportingClass(ReportingClassStruct reportingClassStruct)
    {
    }
    public void updateProductOpenInterest(int i,com.cboe.idl.product.ProductOpenInterestStruct[] openInterestStruct)
    {
    }
}
