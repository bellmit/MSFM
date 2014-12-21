package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractBasePropertyGroup;
import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;
import com.cboe.domain.routingProperty.common.StringListBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.StringListBaseProperty;
import com.cboe.util.ExceptionBuilder;

public class ReasonabilityEditBypassClassGroup extends AbstractRoutingPropertyGroup
{
    public static BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.REASONABILITY_EDIT_BYPASS_CLASS;
    public static final String ORIGIN_TYPES = "Origins";
    private StringBasePropertyImpl originTypes;

    public ReasonabilityEditBypassClassGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public ReasonabilityEditBypassClassGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public ReasonabilityEditBypassClassGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

   public ReasonabilityEditBypassClassGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
   {
       super(basePropertyKey, validators);
   }

   public ReasonabilityEditBypassClassGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                  List<Validator> validators)
           throws DataValidationException, InvocationTargetException
   {
       super(basePropertyKey, propertyGroup, validators);
   }

   public ReasonabilityEditBypassClassGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
   {
       super(basePropertyKey, versionNumber, validators);
   }

     public BasePropertyType getType()
    {
        return RoutingPropertyTypeImpl.REASONABILITY_EDIT_BYPASS_CLASS;
    }

    @Override
    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = originTypes;
        return properties;
    }

    @Override
    public BaseProperty getProperty(String p_name) throws DataValidationException
    {
        if(p_name.equals(ORIGIN_TYPES))
        {
            return originTypes;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + p_name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    @Override
    protected void initializeProperties()
    {
        originTypes = new StringBasePropertyImpl(getPropertyCategoryType(), ORIGIN_TYPES, getPropertyKey(), getType());
    }
 
    public String getOriginTypes()
    {
        return originTypes.getStringValue();
    }
    
    public void setOriginTypes(String value)
    {
        this.originTypes.setStringValue(value);
    }
    
}
