//
// ------------------------------------------------------------------------
// FILE: CancelRequestFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiOrder.CancelRequestStruct;
import com.cboe.interfaces.presentation.common.formatters.CancelRequestFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ExchangeFirmFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;
import com.cboe.interfaces.presentation.order.CancelRequest;
import com.cboe.interfaces.presentation.order.OrderId;
import com.cboe.presentation.order.CancelRequestFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author torresl@cboe.com
 */
class CancelRequestFormatter extends Formatter implements CancelRequestFormatStrategy
{
    private final String Category = this.getClass().getName();

    public static final String BRIEF_STYLE_DELIMITER = " ";
    public static final String FULL_STYLE_DELIMITER = "\n";
    public CancelRequestFormatter()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        addStyle(FULL_STYLE_NAME, FULL_STYLE_DESCRIPTION);
        addStyle(BRIEF_STYLE_NAME, BRIEF_STYLE_DESCRIPTION);
        setDefaultStyle(FULL_STYLE_NAME);
    }

    public String format(CancelRequestStruct cancelRequestStruct)
    {
        return format(cancelRequestStruct, getDefaultStyle());
    }

    public String format(CancelRequestStruct cancelRequestStruct, String style)
    {
        return format(
                CancelRequestFactory.createCancelRequest(cancelRequestStruct),
                style);
    }

    public String format(CancelRequest cancelRequest)
    {
        return format(cancelRequest, getDefaultStyle());
    }

    public String format(CancelRequest cancelRequest, String style)
    {
        validateStyle(style);
        String delimiter = FULL_STYLE_DELIMITER;
        boolean full = true;
        if(BRIEF_STYLE_NAME.equals(style))
        {
            full = false;
            delimiter = BRIEF_STYLE_DELIMITER;
        }

        if (full)
        {
            Utility.portWarningToBePorted(Category + ".format(CancelRequest)");                                         // TODO: remove when Jasper port is completed
        }

        StringBuffer buffer = new StringBuffer();
        if(!full)
        {
            buffer.append(OrderCancelTypes.toString(cancelRequest.getCancelType().shortValue()));
            buffer.append(delimiter);
            buffer.append(cancelRequest.getQuantity().intValue());
        }
        else
        {
            buffer.append("Cancel Type: ");
            buffer.append(OrderCancelTypes.toString(cancelRequest.getCancelType().shortValue()));
            buffer.append(delimiter);
            buffer.append("Quantity: ");
            buffer.append(cancelRequest.getQuantity().intValue());
            buffer.append(delimiter);
            buffer.append("Session: ");
            buffer.append(cancelRequest.getSessionName());
            buffer.append(delimiter);
            buffer.append("User Assigned Cancel Id: ");
            buffer.append(cancelRequest.getUserAssignedCancelId());
            buffer.append(delimiter);
            buffer.append(delimiter);
            buffer.append("Original Order Information").append(delimiter);
            OrderId orderId = cancelRequest.getOrderId();
            buffer.append("Order ID: ").append(orderId.getCboeId().getHighId()).append(":");
            buffer.append(orderId.getCboeId().getLowId());
            buffer.append(delimiter);
            DateFormat dateFormat = FormatFactory.getDateFormatStrategy().getDateFormat(DateFormatStrategy.DATE_FORMAT_YEARMONTHDATE_STYLE);
            String dateString = orderId.getOrderDate();
            try
            {
                Date date = dateFormat.parse(dateString);
                dateString = FormatFactory.getDateFormatStrategy().format(date, DateFormatStrategy.DATE_FORMAT_SHORT_STYLE);
            }
            catch (ParseException e)
            {
                GUILoggerHome.find().exception(e);
            }
            buffer.append("Order Date: ").append(dateString);
            buffer.append(delimiter);
            buffer.append("Branch/Sequence: ").append(orderId.getFormattedBranchSequence());
            buffer.append(delimiter);
            buffer.append(FormatFactory.getExchangeFirmFormatStrategy().format(
                    orderId.getExecutingOrGiveUpFirm(), ExchangeFirmFormatStrategy.FULL));
            buffer.append(delimiter);
            buffer.append("Correspondent: ").append(orderId.getCorrespondentFirm());
            buffer.append(delimiter);
        }
       return buffer.toString();
    }
}
