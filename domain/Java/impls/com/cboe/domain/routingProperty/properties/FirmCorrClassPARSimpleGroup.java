package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class FirmCorrClassPARSimpleGroup extends DefaultDestinationGroup
{
    public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.FIRM_CORR_CLASS_PAR_SIMPLE;

    public FirmCorrClassPARSimpleGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public FirmCorrClassPARSimpleGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public FirmCorrClassPARSimpleGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

   public FirmCorrClassPARSimpleGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
   {
       super(basePropertyKey, validators);
   }

   public FirmCorrClassPARSimpleGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                  List<Validator> validators)
           throws DataValidationException, InvocationTargetException
   {
       super(basePropertyKey, propertyGroup, validators);
   }

   public FirmCorrClassPARSimpleGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
   {
       super(basePropertyKey, versionNumber, validators);
   }

     public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }
}
