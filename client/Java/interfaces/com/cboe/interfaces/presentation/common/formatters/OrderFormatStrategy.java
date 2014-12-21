//
// -----------------------------------------------------------------------------------
// Source file: OrderFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.idl.cmiOrder.OrderFilledReportStruct;
import com.cboe.idl.cmiOrder.OrderStruct;

import com.cboe.interfaces.presentation.omt.OrderCancelMessageElement;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.order.OrderId;

/**
 * Defines a contract for a class that formats OrderStructs.
 *
 * @author Troy Wehrle
 */
public interface OrderFormatStrategy extends FormatStrategy
{
    String FULL_INFO_NAME = "Full Information/Single Column";
    String FULL_INFO_TWO_COLUMN_NAME = "Full Information/Two Column Page";

    String BRIEF_INFO_NAME = "Brief/One Line";
    String BRIEF_INFO_DESCRIPTION = "Brief information designed to fit on one line.";

    String BRIEFER_INFO_NAME = "Briefer/One Line";
    String BRIEFER_INFO_DESCRIPTION = "Briefer information designed to fit on one line.";

    String BRIEF_INFO_LEAVES_NAME = "Brief/One Line with leaves quantity";
    String BRIEF_INFO_LEAVES_DESCRIPTION = "Brief information designed to fit on one line using leaves quantity instead of original quantity.";

    String BRIEFER_INFO_LEAVES_NAME = "Briefer/One Line with leaves quantity";
    String BRIEFER_INFO_LEAVES_DESCRIPTION = "Briefer information designed to fit on one line using leaves quantity instead of original quantity.";

    String FULL_INFO_DESCRIPTION = "Full Struct information ran down one column on the left";
    String FULL_INFO_TWO_COLUMN_DESCRIPTION = "Full Struct information ran down two columns, tab separated with fields left justified.";

    String BRIEF_INFO_NAME_OMT = "Brief/One Line For OMT Messages";
    String BRIEF_INFO_DESCRIPTION_OMT = "Brief information for OMT message designed to fit on one line.";

    String BRIEF_INFO_FBSCID = "Brief/One Line For Gang of Five Id";
    String BRIEF_INFO_DESCRIPTION_FBSCID =
            "Gang of Five order identifier designed to fit on one line.";

    String HELP_DESK_INFO = "HelpDesk/One Line";
    String HELP_DESK_INFO_DESCRIPTION = "Information for Help Desk, designed to fit on one line.";

    /**
     * Defines a method for formatting order CancelReportStruct's.
     *
     * @param orderCancel to format.
     * @param styleName   to use for formatting
     * @return formatted string
     */
    String format(CancelReportStruct orderCancel, String styleName);

    /**
     * Defines a method for formatting order CancelReportStruct's.
     * @param orderCancel to format.
     * @param order (needed for remaining quantity.)
     * @param styleName to use for formatting
     * @return formatted string
     */
    String format(CancelReportStruct orderCancel, OrderStruct order, String styleName);

    /**
     * Defines a method for formatting Orders.
     *
     * @param order to format.
     * @return formatted string
     */
    String format(Order order);

    /**
     * Defines a method for formatting Orders.
     *
     * @param order     to format.
     * @param styleName to use for formatting
     * @return formatted string
     */
    String format(Order order, String styleName);

    /**
     * Defines a method for formatting Orders.
     *
     * @param order to format.
     * @return formatted string
     * @deprecated here for backwards compatibility only
     */
    String format(OrderStruct order);

    /**
     * Defines a method for formatting Orders.
     *
     * @param order     to format.
     * @param styleName to use for formatting
     * @return formatted string
     * @deprecated here for backwards compatibility only
     */
    String format(OrderStruct order, String styleName);

    /**
     * Defines a method for formatting OrderFilledReportStruct's.
     *
     * @param orderFilled to format.
     * @param reportIndex
     * @return formatted string
     */
    String format(OrderFilledReportStruct orderFilled, int reportIndex);

    /**
     * Defines a method for formatting OrderFilledReportStruct's.
     *
     * @param orderFilled to format.
     * @param styleName   to use for formatting
     * @param reportIndex
     * @return formatted string
     */
    String format(OrderFilledReportStruct orderFilled, String styleName, int reportIndex);

    String format(FilledReportStruct orderFill, OrderContingencyStruct contingency, String styleName);

    String format(OrderCancelMessageElement messageElement);

    /**
     * Defines a method for formatting Order Id.
     * @param OrderId to format.
     * @param styleName to use for formatting
     * @return formatted string
     */
    String format(OrderId id, String styleName);

}