package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractAffiliatedFirmPropertyGroup;
import com.cboe.domain.routingProperty.AffiliatedFirmPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;
import com.cboe.util.ExceptionBuilder;

public class DirectedAIMAffiliatedFirmPartnershipGroup extends AbstractAffiliatedFirmPropertyGroup
{
	public static final BasePropertyType AFFILIATED_FIRM_PROPERTY_TYPE = AffiliatedFirmPropertyTypeImpl.DIRECTED_AIM_AFFILIATED_FIRM_PARTNERSHIP;
	protected static final String AFFILIATED_FIRM_PROPERTY = "AffiliatedFirmProperty";
	public static final String AFFILIATED_FIRM_PARTNER_REGISTERED_FOR_DIRECTED_AIM = "DirectedAIMAffiliatedFirmPartnership";
	
	protected BooleanBaseProperty isFirmPartner;
		
    
    public DirectedAIMAffiliatedFirmPartnershipGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public DirectedAIMAffiliatedFirmPartnershipGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public DirectedAIMAffiliatedFirmPartnershipGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public DirectedAIMAffiliatedFirmPartnershipGroup(BasePropertyKey basePropertyKey,
            List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public DirectedAIMAffiliatedFirmPartnershipGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public DirectedAIMAffiliatedFirmPartnershipGroup(BasePropertyKey basePropertyKey, int versionNumber,
            List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }
    
    @Override
	public BasePropertyType getType() 
    {
		return AFFILIATED_FIRM_PROPERTY_TYPE;
	}
    
    @Override
	protected void initializeProperties() 
	{
    	isFirmPartner = new BooleanBasePropertyImpl(getPropertyCategoryType(), AFFILIATED_FIRM_PARTNER_REGISTERED_FOR_DIRECTED_AIM, getPropertyKey(), getType());
	}
    
    public boolean isFirmPartner() {
		return isFirmPartner.getBooleanValue();
	}

	public void setIsFirmPartner(boolean value) 
	{
		isFirmPartner.setBooleanValue(value);
	    firePropertyChange();
	}

	@Override
	public BaseProperty getProperty(String name) throws DataValidationException 
	{
    	if(name.equals(AFFILIATED_FIRM_PARTNER_REGISTERED_FOR_DIRECTED_AIM))
		{
			return isFirmPartner;
		}
		else
		{
			throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                    ". Could not find class type to handle.", 0);
		}
	}

	@Override
	public BaseProperty[] getAllProperties() 
	{
		BaseProperty[] properties = new BaseProperty[1];
        properties[0] = isFirmPartner;
        return properties;
	}
	
	 public Object clone() throws CloneNotSupportedException
    {
    	DirectedAIMAffiliatedFirmPartnershipGroup newGroup = (DirectedAIMAffiliatedFirmPartnershipGroup) super.clone();
        newGroup.isFirmPartner = (BooleanBaseProperty )isFirmPartner.clone();
        return newGroup;
    }

	

	

}
