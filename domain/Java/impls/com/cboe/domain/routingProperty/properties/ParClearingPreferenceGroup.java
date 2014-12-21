package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractFirmPropertyGroup;
import com.cboe.domain.routingProperty.FirmPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;
import com.cboe.util.ExceptionBuilder;

public class ParClearingPreferenceGroup extends AbstractFirmPropertyGroup
{
    public static final BasePropertyType FIRM_PROPERTY_TYPE = FirmPropertyTypeImpl.PAR_CLEARING_PREFERENCE;

    private static final String PAR_CLEARING_PREFERENCE = "ParClearingPreference";

    private BooleanBaseProperty parClearingPreference;

    public ParClearingPreferenceGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public ParClearingPreferenceGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public ParClearingPreferenceGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public ParClearingPreferenceGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public ParClearingPreferenceGroup(BasePropertyKey basePropertyKey,
                                   PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public ParClearingPreferenceGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
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
        parClearingPreference = new BooleanBasePropertyImpl(getPropertyCategoryType(),
                PAR_CLEARING_PREFERENCE, getPropertyKey(), getType());
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(PAR_CLEARING_PREFERENCE))
        {
            return parClearingPreference;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown FirmPropertyName: " + name
                    + ". Could not find class type to handle.", 0);
        }
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = parClearingPreference;

        return properties;
    }

    public boolean getParClearingPreference()
    {
        return parClearingPreference.getBooleanValue();
    }

    public void setParClearingPreference(boolean booleanValue)
    {
        parClearingPreference.setBooleanValue(booleanValue);
        firePropertyChange();
    }
}