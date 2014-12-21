//
// ------------------------------------------------------------------------
// FILE: HeldOrderFormatter.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.*;

public interface HeldOrderFormatStrategy extends FormatStrategy
{
    public static final String FULL_INFO_NAME = OrderFormatStrategy.FULL_INFO_NAME;
    public static final String FULL_INFO_DESCRIPTION = OrderFormatStrategy.FULL_INFO_DESCRIPTION;

    public static final String FULL_INFO_TWO_COLUMN_NAME = OrderFormatStrategy.FULL_INFO_TWO_COLUMN_NAME;
    public static final String FULL_INFO_TWO_COLUMN_DESCRIPTION = OrderFormatStrategy.FULL_INFO_TWO_COLUMN_DESCRIPTION;

    public static final String BRIEF_INFO_NAME = OrderFormatStrategy.BRIEF_INFO_NAME ;
    public static final String BRIEF_INFO_DESCRIPTION = OrderFormatStrategy.BRIEF_INFO_DESCRIPTION;

    public static final String BRIEF_INFO_LEAVES_NAME = OrderFormatStrategy.BRIEF_INFO_LEAVES_NAME;
    public static final String BRIEF_INFO_LEAVES_DESCRIPTION = OrderFormatStrategy.BRIEF_INFO_LEAVES_DESCRIPTION;


    /**
     * Defines a method for formatting Held Orders
     * @param heldOrderStruct to format
     * @return formatted string
     */
    public String format(HeldOrderStruct heldOrderStruct);

    /**
     * Defines a method for formatting Listing States
     * @param heldOrderStruct to format
     * @param style to use
     * @return formatted string
     */
    public String format(HeldOrderStruct heldOrderStruct, String style);

    /**
     * Defines a method for formatting Held Orders
     * @param heldOrder to format
     * @return formatted string
     */
    public String format(HeldOrder heldOrder);

    /**
     * Defines a method for formatting Listing States
     * @param heldOrder to format
     * @param style to use
     * @return formatted string
     */
    public String format(HeldOrder heldOrder, String style);

    public String format(HeldOrderCancelReportStruct heldOrderCancelReportStruct);
    public String format(HeldOrderCancelReportStruct heldOrderCancelReportStruct, String style);
    public String format(HeldOrderCancelReport heldOrderCancelReport);
    public String format(HeldOrderCancelReport heldOrderCancelReport, String style);

    public String format(HeldOrderFilledReportStruct heldOrderFilledReportStruct);
    public String format(HeldOrderFilledReportStruct heldOrderFilledReportStruct, String style);
    public String format(HeldOrderFilledReport heldOrderFilledReport);
    public String format(HeldOrderFilledReport heldOrderFilledReport, String style);
    public String format(HeldOrderFilledReport heldOrderFilledReport, int filledReportIndex);
    public String format(HeldOrderFilledReport heldOrderFilledReport, int filledReportIndex, String style);

    public String format(HeldOrderDetailStruct heldOrderDetailStruct);
    public String format(HeldOrderDetailStruct heldOrderDetailStruct, String style);
    public String format(HeldOrderDetail heldOrderDetail);
    public String format(HeldOrderDetail heldOrderDetail, String style);

    public String format(FillRejectStruct fillRejectStruct);
    public String format(FillRejectStruct fillRejectStruct, String style);
    public String format(FillReject fillReject);
    public String format(FillReject fillReject, String style);

    public String format(HeldOrderCancelRequestStruct heldOrderCancelRequestStruct);
    public String format(HeldOrderCancelRequestStruct heldOrderCancelRequestStruct, String style);
    public String format(HeldOrderCancelRequest heldOrderCancelRequest);
    public String format(HeldOrderCancelRequest heldOrderCancelRequest, String style);
}
