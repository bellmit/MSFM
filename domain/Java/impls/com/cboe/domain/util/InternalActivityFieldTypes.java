package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.ActivityFieldTypesOperations;

/**
 * This class is designed to provide the facility to define internal order
 * activity types
 */

public class InternalActivityFieldTypes implements ActivityFieldTypesOperations {
    // all internal types will be < 0
    public static final short HANDLING_INSTRUCTION = (short) -1;
    public static final short RETURN_CODE = (short) -2;        

    public InternalActivityFieldTypes() {
    }
}