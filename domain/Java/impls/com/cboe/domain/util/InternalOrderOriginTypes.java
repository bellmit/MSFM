package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.OrderOriginsOperations;

/**
 * This class is designed to provide the facility to define internal order
 * origin types
 */

public class InternalOrderOriginTypes implements OrderOriginsOperations {

    public static final char NBBO_MIN_SIZE_GUARANTEE = (char) '1';
    public static final char QUOTE_TRIGGER = (char) '2';
    public static final char SATISFACTION_ORDER_SATISFY = (char) '3';
    public static final char CROSS_PRODUCT_LEG_SYNTHETIC_ORDER = (char) '4';
  
    public InternalOrderOriginTypes() {
    }
}