//
//-----------------------------------------------------------------------------------
//Source file: LinkageOrderDestinationPostStationGroup.java
//
//PACKAGE: com.cboe.domain.routingProperty.properties
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

/**
 * This is a LinkageOrderDestinationPostStationGroup that has has the BasePropertyType
 * "LinkageOrderDestination".
 */
public class LinkageOrderDestinationPostStationGroup extends DefaultDestinationGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.SESSION_CLASS_PAR_ROUTING_POST_STATION;

    public LinkageOrderDestinationPostStationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public LinkageOrderDestinationPostStationGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public LinkageOrderDestinationPostStationGroup(BasePropertyKey basePropertyKey,
            int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public LinkageOrderDestinationPostStationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public LinkageOrderDestinationPostStationGroup(BasePropertyKey basePropertyKey,
                                                   PropertyServicePropertyGroup propertyGroup,
                                                   List<Validator> validators)
            throws DataValidationException,
                   InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public LinkageOrderDestinationPostStationGroup(BasePropertyKey basePropertyKey,
                                                   int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }
}
