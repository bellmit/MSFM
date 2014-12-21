package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;
import com.cboe.util.ExceptionBuilder;

public class SessionRouterVendorGroup extends AbstractRoutingPropertyGroup
{

    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.DEFAULT_LINKAGE_ROUTER;

    public static final String ROUTER_VENDOR_STATUS = "Router Status";

    private BooleanBaseProperty routerStatus;

    public SessionRouterVendorGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public SessionRouterVendorGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public SessionRouterVendorGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public SessionRouterVendorGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public SessionRouterVendorGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                             List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public SessionRouterVendorGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }


    protected void initializeProperties()
    {
        routerStatus = new BooleanBasePropertyImpl(getPropertyCategoryType(), ROUTER_VENDOR_STATUS, getPropertyKey(), getType());
        routerStatus.setBooleanValue(true);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(ROUTER_VENDOR_STATUS))
        {
            return routerStatus;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = routerStatus;

        return properties;
    }

    public boolean getRouterStatus()
    {
        return routerStatus.getBooleanValue();
    }

    public void setPMM(boolean flag)
    {
        routerStatus.setBooleanValue(flag);
        firePropertyChange();
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionRouterVendorGroup newGroup = (SessionRouterVendorGroup) super.clone();
        newGroup.routerStatus = (BooleanBaseProperty) routerStatus.clone();

        return newGroup;
    }

}
