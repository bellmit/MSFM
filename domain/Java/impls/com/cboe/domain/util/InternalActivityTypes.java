package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.ActivityTypesOperations;

/**
 * This class is designed to provide the facility to define internal order
 * activity types
 */

public class InternalActivityTypes implements ActivityTypesOperations {
    // all internal types will be < 0
    public static final short HYBRID_PROCESSING_REQUESTED = (short) -1;
    public static final short HYBRID_REQUEST_RETURNED = (short) -2; 
    public static final short OMT_DISPLAY_ROUTED_AWAY = (short) -3;

    // CPS Split Order Event
    public static final short CPS_SPLIT_ORDER_TIMEOUT               = -101;
    public static final short CPS_SPLIT_ORDER_CANCEL_REQUEST_REJECT = -102;
    public static final short CPS_SPLIT_ORDER_CANCEL_REPLACE_REJECT = -103;
    public static final short CPS_SPLIT_DERIVED_ORDER_NEW           = -104;
    public static final short CPS_SPLIT_DERIVED_ORDER_FILL          = -105;
    public static final short CPS_SPLIT_DERIVED_ORDER_CANCEL        = -106;

    public InternalActivityTypes() {
    }
}