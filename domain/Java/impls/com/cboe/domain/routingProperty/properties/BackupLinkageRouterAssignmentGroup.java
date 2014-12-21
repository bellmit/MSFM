package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.common.StringListBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.common.StringListBaseProperty;
import com.cboe.util.ExceptionBuilder;

public class BackupLinkageRouterAssignmentGroup  extends LinkageRouterAssignmentGroup implements Cloneable
{
    
    public static BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.BACKUP_LINKAGE_ROUTER_ASSIGNMENT;
    
    public BackupLinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey)
    {
       super(basePropertyKey);
    }

    public BackupLinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
           throws DataValidationException, InvocationTargetException
    {
       super(basePropertyKey, propertyGroup);
    }

    public BackupLinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
       super(basePropertyKey, versionNumber);
    }

    public BackupLinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public BackupLinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                            List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public BackupLinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, int versionNumber,
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
