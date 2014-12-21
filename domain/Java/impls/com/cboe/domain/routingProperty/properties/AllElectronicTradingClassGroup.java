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

public class AllElectronicTradingClassGroup extends AbstractRoutingPropertyGroup implements
        Cloneable
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.ALL_ELECTRONIC_TRADING_CLASS;   
    public static final String ALL_ELECTRONIC_TRADING_CLASS_STR = "AllElectronicTradingClass";
    private BooleanBaseProperty allElectronicTradingClass;


    public AllElectronicTradingClassGroup(BasePropertyKey p_basePropertyKey)
    {
        super(p_basePropertyKey);
    }
    
    public AllElectronicTradingClassGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
    throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public AllElectronicTradingClassGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public AllElectronicTradingClassGroup(BasePropertyKey p_basePropertyKey, List<Validator> validators)
    {
        super(p_basePropertyKey, validators);
    }

    public AllElectronicTradingClassGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                          List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public AllElectronicTradingClassGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(ALL_ELECTRONIC_TRADING_CLASS_STR))
        {
            return allElectronicTradingClass;
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
        properties[0] = allElectronicTradingClass;
        return properties;
    }

    protected void initializeProperties()
    {
        allElectronicTradingClass = new BooleanBasePropertyImpl(getPropertyCategoryType(), ALL_ELECTRONIC_TRADING_CLASS_STR, getPropertyKey(), getType());
    }

    public boolean isAllElectronicTradingEnabled()
    {
        return allElectronicTradingClass.getBooleanValue();
    }

    public void setAllElectronicTradingEnabled(boolean allElectronicTradingEnabled)
    {
        this.allElectronicTradingClass.setBooleanValue(allElectronicTradingEnabled);
        firePropertyChange();
    }
    
    public Object clone() throws CloneNotSupportedException
    {
        AllElectronicTradingClassGroup newGroup = (AllElectronicTradingClassGroup) super.clone();
        newGroup.allElectronicTradingClass = (BooleanBaseProperty) allElectronicTradingClass.clone();
        return newGroup;
    }
}