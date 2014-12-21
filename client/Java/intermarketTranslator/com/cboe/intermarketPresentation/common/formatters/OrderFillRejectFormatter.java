//
// ------------------------------------------------------------------------
// Source file: OrderFillRejectFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------

package com.cboe.intermarketPresentation.common.formatters;

import com.cboe.idl.cmiIntermarketMessages.OrderFillRejectStruct;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.FillReject;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.OrderFillReject;
import com.cboe.interfaces.presentation.common.formatters.OrderFillRejectFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.OrderFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.order.OrderDetail;

import com.cboe.presentation.common.formatters.*;
import com.cboe.presentation.common.formatters.FormatFactory;

import com.cboe.intermarketPresentation.intermarketMessages.OrderFillRejectFactory;

/**
 * @author torresl@cboe.com
 */
class OrderFillRejectFormatter extends AbstractCommonStylesFormatter implements OrderFillRejectFormatStrategy
{
    private final String Category = this.getClass().getName();

    public OrderFillRejectFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        setDefaultStyle(FULL_STYLE_NAME);
    }


    public String format(OrderFillRejectStruct orderFillRejectStruct)
    {
        return format(orderFillRejectStruct, getDefaultStyle());
    }

    public String format(OrderFillRejectStruct orderFillRejectStruct, String style)
    {
        return format(
                OrderFillRejectFactory.createOrderFillReject(orderFillRejectStruct),
                style
        );
    }

    public String format(OrderFillReject orderFillReject)
    {
        return format(orderFillReject, getDefaultStyle());
    }
    public String format(OrderFillReject orderFillReject, String style)
    {
        validateStyle(style);
        StringBuffer buffer = new StringBuffer(1000);
        boolean brief = isBrief(style);
        String delimiter = getDelimiterForStyle(style);

        if (! brief )
        {
            Utility.portWarningToBePorted(Category + ".format(OrderFillReject)");                                       // TODO: remove when Jasper port is completed
        }

        if (! brief )
        {
            buffer.append("Reason: ");
            buffer.append(StatusUpdateReasons.toString(orderFillReject.getOrderDetail().getStatusChange().shortValue()));
            buffer.append(" REJECT");
            buffer.append(delimiter);
            buffer.append("Product: ");
            OrderDetail orderDetail = orderFillReject.getOrderDetail();
            buffer.append(
                    CommonFormatFactory.getProductFormatStrategy().format(
                            orderDetail.getOrder().getSessionProduct(), ProductFormatStrategy.FULL_PRODUCT_NAME_WITH_SESSION_AND_TYPE));
            buffer.append(delimiter);
            buffer.append("Order Info: ");
            buffer.append(delimiter);
            buffer.append(
                    FormatFactory.getOrderFormatStrategy().format(
                            orderFillReject.getOrderDetail().getOrder(),
                            OrderFormatStrategy.FULL_INFO_TWO_COLUMN_NAME));
            FillReject[] fillRejects = orderFillReject.getFillRejectReports();
            buffer.append(delimiter).append(delimiter);
            for (int i = 0; i < fillRejects.length; i++)
            {
                FillReject fillReject = fillRejects[i];
                buffer.append("----").append(delimiter).append("Reject Detail").append(delimiter);
                buffer.append("Trade ID: ");
                buffer.append(fillReject.getTradeId().getHighId());
                buffer.append(":").append(fillReject.getTradeId().getLowId());
                buffer.append(delimiter);
                buffer.append("Reject Reason: ").append(ActivityTypes.toString(fillReject.getRejectReason().shortValue()));
                buffer.append(delimiter);
                buffer.append(FormatFactory.getOrderFormatStrategy().format(
                        fillReject.getOrder(),
                        OrderFormatStrategy.FULL_INFO_TWO_COLUMN_NAME));
                buffer.append(delimiter);
            }
        }
        else
        {
            buffer.append(
                    StatusUpdateReasons.toString(orderFillReject.getOrderDetail().getStatusChange().shortValue()));
            buffer.append(delimiter);
            buffer.append("REJECT");
            buffer.append(delimiter);
            buffer.append(
                    FormatFactory.getOrderFormatStrategy().format(
                            orderFillReject.getOrderDetail().getOrder(),
                            OrderFormatStrategy.BRIEF_INFO_LEAVES_NAME));
        }
        return buffer.toString();
    }
}
