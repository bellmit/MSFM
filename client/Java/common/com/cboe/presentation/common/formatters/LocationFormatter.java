//
// -----------------------------------------------------------------------------------
// Source file: LocationFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.LocationFormatStrategy;

/**
 * Format the location activityField by parsing the fields.
 * 
 * It exists different formats for the location activity type field.
 * The format that will be parsed by this class is a format that looks like: Server:User:Route.
 * The formatter will parse only the location containing 3 fields separated by a colon and
 * it will return a verbatim location value in the other cases.
 *
 * @author Eric Maheo
 * @since Dec 17, 2008
 * 
 */

public class LocationFormatter extends Formatter implements LocationFormatStrategy
{

    /**
     * Field location server.
     */
    public static final int SERVER_FIELD_ID = 0;
    /**
     * Field location user.
     */
    public static final int USER_FIELD_ID = 1;
    /**
     * Field route description.
     */
    public static final int ROUTE_FIELD_ID = 2;
    /**
     * Max fields to parse in location.
     */
    private static final int MAX_FIELD = 3;

    /**
     * Create a lcoation formatter Object.
     */
    public LocationFormatter(){
        addStyle(LOCATION_ROUTE_DESTINATION, LOCATION_ROUTE_DESTINATION_DESC);
        addStyle(LOCATION_SERVER, LOCATION_SERVER_DESC);
        addStyle(LOCATION_USER_FIELD, LOCATION_USER_FIELD_DESC);
        addStyle(LOCATION_DEFAULT, LOCATION_DEFAULT_DESC);
        setDefaultStyle(LOCATION_DEFAULT);
    }

    /**
     * {@inheritDoc}.
     */
    public String format(String location)
    {
       return format(location, getDefaultStyle());
    }

    /**
     * {@inheritDoc}.
     */
    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public String format(String location, String styleName)
    {
        if (LOCATION_ROUTE_DESTINATION.equals(styleName)){
           return getFieldLocation(location, ROUTE_FIELD_ID);
        }
        else if (LOCATION_SERVER.equals(styleName)){
            return getFieldLocation(location, SERVER_FIELD_ID);
        }
        else if (LOCATION_USER_FIELD.equals(styleName)){
            return getFieldLocation(location, USER_FIELD_ID);
        }
        else if (LOCATION_DEFAULT.equals(styleName)){
            return location;
        }
        else {
            return location;
        }
    }

    /**
     * Split the location string into 3 parts and return the part requested by fieldId.
     * 
     * @param location to parse.
     * @param fieldId to query.
     * @return the fieldId part or the location if no field found.
     */
    private String getFieldLocation(String location, int fieldId){
        if (location == null){
            throw new IllegalArgumentException("location can't be null");
        }
        if (fieldId <0 || fieldId >= MAX_FIELD){
            throw new IllegalArgumentException("Attempt to query a field out of range.");
        }
        String[] locationAry = location.split(":",MAX_FIELD);

        return locationAry.length > fieldId? locationAry[fieldId]:location;
    }

}
