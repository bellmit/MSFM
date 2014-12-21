//
// -----------------------------------------------------------------------------------
// Source file: LocationFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

/**
 * Defines a contract for a class that formats a Location.
 * @author Eric Maheo
 * @since Dec 17, 2008
 */
public interface LocationFormatStrategy extends FormatStrategy
{
    
    public static final String LOCATION_ROUTE_DESTINATION = "Location Route Destination";
    public static final String LOCATION_SERVER = "Location Server";
    public static final String LOCATION_USER_FIELD = "Location User Field";
    public static final String LOCATION_DEFAULT = "Location Default";

    public static final String LOCATION_ROUTE_DESTINATION_DESC = "Only the route destination of the location";
    public static final String LOCATION_SERVER_DESC = "Only the server field for the location";
    public static final String LOCATION_USER_FIELD_DESC = "Only the user field for the location";
    public static final String LOCATION_DEFAULT_DESC = "Verbatim location";


    //check the 3 fields of a location SVRS:ABC:W004

    /**
     * Default format for a location activityField.
     * @param location to format.
     * @return the default formatted output.
     */
    public String format(String location);

    /**
     * Format the location by applying a style.
     * @param location to format.
     * @param styleName to apply.
     * @return the styled formatted location.
     */
    public String format(String location, String styleName);

}