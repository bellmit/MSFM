//
// -----------------------------------------------------------------------------------
// Source file: ManualQuoteDetail.java
//
// PACKAGE: com.cboe.interfaces.presentation.manualReporting
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.manualReporting;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.idl.quote.ManualQuoteDetailStruct;

/**
 * Defines the contract a ManualQuoteDetail wrapper for a ManualQuoteDetailStruct
 */
public interface ManualQuoteDetail extends MutableBusinessModel {
    public static final String PROPERTY_LOCATION_ID = "PROPERTY_LOCATION_ID";
    public static final String PROPERTY_PAR_ID = "PROPERTY_PAR_ID";
    public static final String PROPERTY_IP_ADDRESS = "PROPERTY_IP_ADDRESS";

    // helper methods to struct attributes
    public String getLocationId();
    public String getParId();
    public String getIpAddress();

    public void setLocationId(String locationId);
    public void setParId(String parId);
    public void setIpAddress(String ipAddress);

    public ManualQuoteDetailStruct getStruct();
}
