package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractAffiliatedFirmPropertyGroup;
import com.cboe.domain.routingProperty.AffiliatedFirmPropertyTypeImpl;
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

public class DirectedAIMFirmRegistrationOverrideGroup extends AbstractAffiliatedFirmPropertyGroup 
{
	public static final BasePropertyType AFFILIATED_FIRM_PROPERTY_TYPE = AffiliatedFirmPropertyTypeImpl.DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION_OVERRIDE;

    public static final String AFFILIATED_FIRM_REGISTERED_FOR_DIRECTED_AIM = "DirectedAIMFirmRegistrationOverride";
    
    protected BooleanBaseProperty isFirmRegistrationForDirectedAIMOverrride;
    
    public DirectedAIMFirmRegistrationOverrideGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public DirectedAIMFirmRegistrationOverrideGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public DirectedAIMFirmRegistrationOverrideGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public DirectedAIMFirmRegistrationOverrideGroup(BasePropertyKey basePropertyKey,
            List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public DirectedAIMFirmRegistrationOverrideGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public DirectedAIMFirmRegistrationOverrideGroup(BasePropertyKey basePropertyKey, int versionNumber,
            List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

	@Override
	public BaseProperty[] getAllProperties()
	{
		BaseProperty[] properties = new BaseProperty[1];
        properties[0] = isFirmRegistrationForDirectedAIMOverrride;

        return properties;
	}

	@Override
	public BaseProperty getProperty(String name) throws DataValidationException 
	{
		if(name.equals(AFFILIATED_FIRM_REGISTERED_FOR_DIRECTED_AIM))
		{
			return isFirmRegistrationForDirectedAIMOverrride;
		}
		else
		{
			throw ExceptionBuilder.dataValidationException("Unknown AffiliatedFirmPropertyName: " + name +
                    ". Could not find class type to handle.", 0);
		}
	}

	@Override
	public BasePropertyType getType()
	{
		return AFFILIATED_FIRM_PROPERTY_TYPE;
	}

	@Override
	protected void initializeProperties() 
	{
		isFirmRegistrationForDirectedAIMOverrride = new BooleanBasePropertyImpl(getPropertyCategoryType(), AFFILIATED_FIRM_REGISTERED_FOR_DIRECTED_AIM, getPropertyKey(), getType());
	}
	
	public boolean isFirmRegistrationForDAIMOverride()
	{
		return isFirmRegistrationForDirectedAIMOverrride.getBooleanValue();
	}
	
	public void setFirmRegistrationForDAIMOverride(boolean value)
	{
		isFirmRegistrationForDirectedAIMOverrride.setBooleanValue(value);
		firePropertyChange();
	}
	
	public Object clone() throws CloneNotSupportedException
    {
		DirectedAIMFirmRegistrationOverrideGroup newGroup = (DirectedAIMFirmRegistrationOverrideGroup) super.clone();
        newGroup.isFirmRegistrationForDirectedAIMOverrride = (BooleanBaseProperty) isFirmRegistrationForDirectedAIMOverrride.clone();

        return newGroup;
    }

}
