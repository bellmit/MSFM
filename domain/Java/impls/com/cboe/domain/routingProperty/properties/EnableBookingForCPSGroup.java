package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;

public class EnableBookingForCPSGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.ENABLE_BOOKING_FORCPS;

    public static final String ENABLE_BOOKING_FORCPS= "EnableBookingForCPS";

    private BooleanBaseProperty enableBookingForCPS;

    public EnableBookingForCPSGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public EnableBookingForCPSGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public EnableBookingForCPSGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public EnableBookingForCPSGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public EnableBookingForCPSGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                            List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public EnableBookingForCPSGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }


    protected void initializeProperties()
    {
    	enableBookingForCPS = new BooleanBasePropertyImpl(getPropertyCategoryType(), ENABLE_BOOKING_FORCPS, getPropertyKey(), getType());
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(ENABLE_BOOKING_FORCPS))
        {
            return enableBookingForCPS;
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
        properties[0] = enableBookingForCPS;

        return properties;
    }

    public boolean isEnableBookingForCPS()
    {
        return enableBookingForCPS.getBooleanValue();
    }

    public void setRouteReasonCalulation(boolean flag)
    {
        enableBookingForCPS.setBooleanValue(flag);
        firePropertyChange();
    }

    public Object clone() throws CloneNotSupportedException
    {
    	EnableBookingForCPSGroup newGroup = (EnableBookingForCPSGroup) super.clone();
        newGroup.enableBookingForCPS = (BooleanBaseProperty) enableBookingForCPS.clone();

        return newGroup;
    }
}
