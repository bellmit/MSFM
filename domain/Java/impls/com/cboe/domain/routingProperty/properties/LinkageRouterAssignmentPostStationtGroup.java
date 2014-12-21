package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class LinkageRouterAssignmentPostStationtGroup extends LinkageRouterAssignmentGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.LINKAGE_ROUTER_ASSIGNMENT_POST_STATION;

    public LinkageRouterAssignmentPostStationtGroup(BasePropertyKey basePropertyKey)
    {
       super(basePropertyKey);
    }

    public LinkageRouterAssignmentPostStationtGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
           throws DataValidationException, InvocationTargetException
    {
       super(basePropertyKey, propertyGroup);
    }

    public LinkageRouterAssignmentPostStationtGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
       super(basePropertyKey, versionNumber);
    }

    public LinkageRouterAssignmentPostStationtGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public LinkageRouterAssignmentPostStationtGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                            List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public LinkageRouterAssignmentPostStationtGroup(BasePropertyKey basePropertyKey, int versionNumber,
                                            List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    @Override
    public BasePropertyType getType()
    {
       return ROUTING_PROPERTY_TYPE;
    }

}
