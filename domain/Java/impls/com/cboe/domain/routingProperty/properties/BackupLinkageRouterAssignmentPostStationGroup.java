package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class BackupLinkageRouterAssignmentPostStationGroup extends LinkageRouterAssignmentGroup implements Cloneable
{
    
    public static BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.BACKUP_LINKAGE_ROUTER_ASSIGNMENT_POST_STATION;
    
    public BackupLinkageRouterAssignmentPostStationGroup(BasePropertyKey basePropertyKey)
    {
       super(basePropertyKey);
    }

    public BackupLinkageRouterAssignmentPostStationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
           throws DataValidationException, InvocationTargetException
    {
       super(basePropertyKey, propertyGroup);
    }

    public BackupLinkageRouterAssignmentPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
       super(basePropertyKey, versionNumber);
    }

    public BackupLinkageRouterAssignmentPostStationGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public BackupLinkageRouterAssignmentPostStationGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                            List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public BackupLinkageRouterAssignmentPostStationGroup(BasePropertyKey basePropertyKey, int versionNumber,
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
