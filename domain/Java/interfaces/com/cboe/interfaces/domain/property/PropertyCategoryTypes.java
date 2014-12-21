package com.cboe.interfaces.domain.property;

//
// -----------------------------------------------------------------------------------
// Source file: PropertyServicePropertyGroup
//
// PACKAGE: com.cboe.interfaces.domain.property;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public interface PropertyCategoryTypes
{
    // existing
    String USER_ENABLEMENT = com.cboe.idl.constants.PropertyCategoryTypes.USER_ENABLEMENT;
    String TRADING_PROPERTIES = com.cboe.idl.constants.PropertyCategoryTypes.TRADING_PROPERTIES;

    // new
    String RATE_LIMITS = "RateLimits";
    String ROUTING_PROPERTIES = "RoutingProperty";
    String FIRM_PROPERTIES = "FirmProperty";
    String AFFILIATED_FIRM_PROPERTIES = "AffiliatedFirmProperty";
    String USER_PROPERTIES = "UserProperty";
    
    //manual quote property
    public static final String TCLOC = com.cboe.idl.constants.PropertyCategoryTypes.TCLOC;
 }
