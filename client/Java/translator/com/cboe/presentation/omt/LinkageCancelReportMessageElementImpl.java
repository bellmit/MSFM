//
// -----------------------------------------------------------------------------------
// Source file: LinkageCancelReportMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.order.CancelReportRoutingStruct;
import com.cboe.idl.order.LinkageExtensionsStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.common.formatters.OrderFormatStrategy;
import com.cboe.interfaces.presentation.omt.LinkageCancelReportMessageElement;
import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.order.CancelReport;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.common.formatters.Utility;
import com.cboe.presentation.common.formatters.OrderFormatter;
import com.cboe.presentation.order.CancelReportFactory;

public class LinkageCancelReportMessageElementImpl
        extends InfoMessageElementImpl
        implements LinkageCancelReportMessageElement
{
    private CancelReport cancelReport;
    private LinkageExtensionsStruct linkageExtensions;
    private String displayOrsId;

    protected LinkageCancelReportMessageElementImpl(CancelReportRoutingStruct cancelReportRoutingStruct,
                                                    LinkageExtensionsStruct linkageExtensions,
                                                    RoutingParameterV2Struct routingParameterV2Struct)
    {
        super(MessageElement.MessageType.LINKAGE_CANCEL_REPORT, routingParameterV2Struct);
        cancelReport = CancelReportFactory.createCancelReport(cancelReportRoutingStruct.cancelReport);
        this.linkageExtensions = linkageExtensions;
        setRouteReasonStruct(cancelReportRoutingStruct.routeReason);
        setOrderId(cancelReport.getOrderId());
        setClassPostStation(getProductClass(getSessionName(), getProductKeyValue()));

        OrderFormatStrategy formatter = FormatFactory.getOrderFormatStrategy();
        setRestAsString(formatter.format(cancelReportRoutingStruct.cancelReport,
                                         OrderFormatStrategy.BRIEF_INFO_NAME_OMT));
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof LinkageCancelReportMessageElement)
            {
                LinkageCancelReportMessageElement that = (LinkageCancelReportMessageElement) o;

                isEqual = getCancelReport().getOrderId().equals(that.getCancelReport().getOrderId());
            }
        }

        return isEqual;
    }

    public CancelReport getCancelReport()
    {
        return cancelReport;
    }

    public LinkageExtensionsStruct getLinkageExtensions()
    {
        return linkageExtensions;
    }

    public int getProductKeyValue()
    {
        return cancelReport.getProductKey();
    }

    public String getSessionName()
    {
        return cancelReport.getSessionName();
    }

    public CBOEId getCboeId()
    {
        return cancelReport.getOrderId().getCboeId();
    }

    public String getProductName()
    {
        SessionProduct sp = Utility.getProductByKeyForSession(getSessionName(), getProductKeyValue());
        if(sp != null)
        {
            return FormatFactory.getFormattedProduct(sp);
        }
        else
        {
            return NOT_AVAILABLE;
        }
    }


    public String getOrsId()
    {
        return cancelReport.getOrsId();
    }

    public String getDisplayOrsId()
    {
        if(displayOrsId == null)
        {
            displayOrsId = OrderFormatter.formatOrsIdForDisplay(cancelReport.getOrsId());
        }
        return displayOrsId;
    }
}