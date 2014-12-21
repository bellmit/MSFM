package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractFirmPropertyGroup;
import com.cboe.domain.routingProperty.FirmPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;
import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;
import com.cboe.util.ExceptionBuilder;

public class EtnIndicatorGroup extends AbstractFirmPropertyGroup
{
    public static final BasePropertyType FIRM_PROPERTY_TYPE = FirmPropertyTypeImpl.FIRM_ETN_PARAM;
    
    public static final String ETN = "etnIndicator";

    private EtnIndicator etnIndicator;

    public EtnIndicatorGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public EtnIndicatorGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public EtnIndicatorGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public EtnIndicatorGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public EtnIndicatorGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                             List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public EtnIndicatorGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    /**
     * Gets the BasePropertyType for this group that identifies the type of this group.
     */
    public BasePropertyType getType()
    {
        return FIRM_PROPERTY_TYPE;
    }

    protected void initializeProperties()
    {
        etnIndicator = new EtnIndicator(getPropertyCategoryType(), ETN, getPropertyKey(), getType());
        try
        {
            etnIndicator.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + ETN + " category="
                          + getPropertyCategoryType() + "type=" + getType(), e);
        }
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(ETN))
        {
            return etnIndicator;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown FirmPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = etnIndicator;

        return properties;
    }

    public String getEtnIndicator()
    {
        return etnIndicator.getEtnIndicator();
    }

    public void setEtnIndicator(String value)
    {
        this.etnIndicator.setEtnIndicator(value);
    }
}
