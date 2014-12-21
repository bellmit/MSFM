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

public class ShortSaleMarkingGroup extends AbstractFirmPropertyGroup
{
    public static final BasePropertyType FIRM_PROPERTY_TYPE = FirmPropertyTypeImpl.SHORT_SALE_MARKING;
    
    public static final String SSM = "ShortSaleMarking";

    private BooleanBaseProperty shortSaleMarking;

    public ShortSaleMarkingGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public ShortSaleMarkingGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public ShortSaleMarkingGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public ShortSaleMarkingGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public ShortSaleMarkingGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                             List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public ShortSaleMarkingGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
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
        shortSaleMarking = new BooleanBasePropertyImpl(getPropertyCategoryType(), SSM, getPropertyKey(), getType());
        try
        {
            shortSaleMarking.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + SSM + " category="
                          + getPropertyCategoryType() + "type=" + getType(), e);
        }
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(SSM))
        {
            return shortSaleMarking;
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
        properties[0] = shortSaleMarking;

        return properties;
    }

    public boolean getShortSaleMarking()
    {
        return shortSaleMarking.getBooleanValue();
    }

    public void setShortSaleMarking(boolean value)
    {
        this.shortSaleMarking.setBooleanValue(value);
    }
}
