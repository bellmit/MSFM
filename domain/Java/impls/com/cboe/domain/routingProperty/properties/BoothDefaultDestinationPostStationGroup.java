package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

import com.cboe.domain.routingProperty.AbstractDropCopyPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;

public class BoothDefaultDestinationPostStationGroup extends AbstractDropCopyPropertyGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.BOOTH_DEFAULT_DESTINATION_POST_STATION;

    public BoothDefaultDestinationPostStationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public BoothDefaultDestinationPostStationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public BoothDefaultDestinationPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public BoothDefaultDestinationPostStationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public BoothDefaultDestinationPostStationGroup(BasePropertyKey basePropertyKey,
                                                   PropertyServicePropertyGroup propertyGroup,
                                                   List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public BoothDefaultDestinationPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber,
                                                   List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public Object clone()
            throws CloneNotSupportedException
    {
        BoothDefaultDestinationPostStationGroup newGroup = (BoothDefaultDestinationPostStationGroup) super.clone();
        return newGroup;
    }
}
