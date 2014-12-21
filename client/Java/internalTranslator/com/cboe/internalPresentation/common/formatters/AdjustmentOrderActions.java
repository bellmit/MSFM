// -----------------------------------------------------------------------------------
// Source file: AdjustmentOrderActions.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

/**
 * Formats a adjustmentOrderAction
 */
public class AdjustmentOrderActions
{
    public static final short NORMAL_ADJUSTMENT = com.cboe.idl.constants.AdjustmentOrderActions.NORMAL_ADJUSTMENT;
    public static final short CANCEL_ALL_ORDERS = com.cboe.idl.constants.AdjustmentOrderActions.CANCEL_ALL_ORDERS;

    public static final String NORMAL_ADJUSTMENT_STRING = "Normal Adjustment";
    public static final String CANCEL_ALL_ORDERS_STRING = "Cancel all Orders";
    public static final String UNKNOWN_ADJUSTMENT_ORDER_ACTION_STRING = "Unknown Adjustment Order Action";

    private AdjustmentOrderActions()
    {
    }

    /**
     * Formats a adjustmentOrderAction
     * @param adjustmentOrderAction to format
     */
    public static String toString(short adjustmentOrderAction)
    {
        String retVal = "";
        switch(adjustmentOrderAction)
        {
            case NORMAL_ADJUSTMENT:
                retVal = NORMAL_ADJUSTMENT_STRING;
                break;
            case CANCEL_ALL_ORDERS:
                retVal = CANCEL_ALL_ORDERS_STRING;
                break;
            default:
                retVal = new StringBuffer(35).append(UNKNOWN_ADJUSTMENT_ORDER_ACTION_STRING).append(" ").append(adjustmentOrderAction).toString();
                break;
        }
        return retVal;
    }
}
