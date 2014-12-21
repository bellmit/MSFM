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

public class MMTNotificationPreferenceGroup extends AbstractFirmPropertyGroup
{
    public static final BasePropertyType FIRM_PROPERTY_TYPE = FirmPropertyTypeImpl.MMTN_PREFERENCE;

    private static final String MMTN_PREFERENCE = "MMTNPreference";

    private BooleanBaseProperty mmtnPreference;

    public MMTNotificationPreferenceGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public MMTNotificationPreferenceGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public MMTNotificationPreferenceGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public MMTNotificationPreferenceGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public MMTNotificationPreferenceGroup(BasePropertyKey basePropertyKey,
                                   PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public MMTNotificationPreferenceGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
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
        mmtnPreference = new BooleanBasePropertyImpl(getPropertyCategoryType(),
                MMTN_PREFERENCE, getPropertyKey(), getType());
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(MMTN_PREFERENCE))
        {
            return mmtnPreference;
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
        properties[0] = mmtnPreference;

        return properties;
    }

    public boolean getMMTNPreference()
    {
        return mmtnPreference.getBooleanValue();
    }

    public void setMMTNPreference(boolean booleanValue)
    {
        mmtnPreference.setBooleanValue(booleanValue);
        firePropertyChange();
    }
}