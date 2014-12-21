package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractFirmPropertyGroup;
import com.cboe.domain.routingProperty.FirmPropertyTypeImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.util.ExceptionBuilder;

public class FirmClassAcronymPreferenceGroup extends AbstractFirmPropertyGroup
{
    public static final BasePropertyType FIRM_PROPERTY_TYPE = FirmPropertyTypeImpl.MMTN_MAPPING;

    private static final String MMTN_MAPPING_PREFERENCE = "FirmMMTNMapping";

    private AcronymProperty mmtnMappingPreference;

    public FirmClassAcronymPreferenceGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public FirmClassAcronymPreferenceGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public FirmClassAcronymPreferenceGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public FirmClassAcronymPreferenceGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public FirmClassAcronymPreferenceGroup(BasePropertyKey basePropertyKey,
                                   PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public FirmClassAcronymPreferenceGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
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
        mmtnMappingPreference = new AcronymProperty(getPropertyCategoryType(),
                MMTN_MAPPING_PREFERENCE, getPropertyKey(), getType());
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(MMTN_MAPPING_PREFERENCE))
        {
            return mmtnMappingPreference;
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
        properties[0] = mmtnMappingPreference;

        return properties;
    }

    public String getCmiUser()
    {
        return mmtnMappingPreference.getStringValue();
    }

    public void setParClearingPreference(String stringValue)
    {
        mmtnMappingPreference.setStringValue(stringValue);
        firePropertyChange();
    }
}