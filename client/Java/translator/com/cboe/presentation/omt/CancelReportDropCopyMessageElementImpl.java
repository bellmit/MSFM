//
// -----------------------------------------------------------------------------------
// Source file: CancelReportDropCopyMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.OrderCancelReportStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.util.RouteReasonStruct;
import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.presentation.common.formatters.OrderFormatStrategy;
import com.cboe.interfaces.presentation.omt.CancelReportDropCopyMessageElement;
import com.cboe.interfaces.presentation.order.CancelReport;

import com.cboe.presentation.common.formatters.FormatFactory;
import com.cboe.presentation.order.CancelReportFactory;
import com.cboe.presentation.util.StructBuilder;

public class CancelReportDropCopyMessageElementImpl extends DropCopyMessageElementImpl
    implements CancelReportDropCopyMessageElement
{
    private CancelReportStruct cancelReportStruct;
    private CancelReport cancelReport;
    private OrderCancelReportStruct orderCancelReportStruct;

    protected CancelReportDropCopyMessageElementImpl (
            RoutingParameterV2Struct routingParameterV2Struct, RouteReasonStruct routeReasonStruct,
            OrderStruct orderStruct, CancelReportStruct cancelReportStruct)
    {
        super(MessageType.CANCEL_REPORT_DROP_COPY, routingParameterV2Struct);
        this.cancelReportStruct = StructBuilder.cloneCancelReportStruct(cancelReportStruct);
        setOrder(orderStruct);
        setClassPostStation(getProductClass(orderStruct.classKey));
        OrderFormatStrategy formatter = FormatFactory.getOrderFormatStrategy();
        setRestAsString(formatter.format(cancelReportStruct, orderStruct,
                                         OrderFormatStrategy.BRIEF_INFO_NAME_OMT));
        setRouteReasonStruct(routeReasonStruct);
    }

    public int getProductKeyValue()
    {
        return cancelReportStruct.productKey;
    }

    public String getSessionName()
    {
        return cancelReportStruct.sessionName;
    }

    public short getRouteReason()
    {
        return getRouteReasonStruct().routeReason;
    }

    public String getRouteDescription()
    {
        return getRouteReasonStruct().routeDescription;
    }

    public CancelReportStruct getCancelReportStruct()
    {
        return cancelReportStruct;
    }

    public CancelReport getCancelReport()
    {
        if (cancelReport == null && cancelReportStruct != null)
        {
            cancelReport = CancelReportFactory.createCancelReport(cancelReportStruct);
        }
        return cancelReport;
    }

    public OrderCancelReportStruct getOrderCancelReportStruct() throws CommunicationException,
            AuthorizationException, DataValidationException, NotFoundException, SystemException
    {
        if (orderCancelReportStruct == null)
        {
            CancelReportStruct[] cancelReports = new CancelReportStruct[1];
            cancelReports[0] = getCancelReportStruct();
            orderCancelReportStruct = new OrderCancelReportStruct(getOrderDetailStruct(), cancelReports);
        }
        return orderCancelReportStruct;
    }

    public boolean equals(Object o)
    {
        boolean isEqual = super.equals(o);

        if(isEqual)
        {
            if(o != null && o instanceof CancelReportDropCopyMessageElement)
            {
                CancelReportDropCopyMessageElement that = (CancelReportDropCopyMessageElement) o;

                isEqual = getOrderStruct().orderId.equals(that.getOrderStruct().orderId);
            }
        }

        return isEqual;
    }
}
