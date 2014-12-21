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

/**
 * @author sundar
 *
 * Group for the PDPMCOmplex Eligibility routing property. Has one boolean as the value.
 *
 */
public class PDPMComplexEligibilityGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
	public static final BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.PDPM_COMPLEX_ELIGIBILITY;
	public static final String PDPM_COMPLEX_ELIGIBILITY= "PDPMComplexEligibility";
	private BooleanBaseProperty isPDPMComplexEligible;

	public PDPMComplexEligibilityGroup(BasePropertyKey basePropertyKey)
	 {
	     super(basePropertyKey);
	 }

	 public PDPMComplexEligibilityGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
	         throws DataValidationException, InvocationTargetException
	 {
	     super(basePropertyKey, propertyGroup);
	 }

	 public PDPMComplexEligibilityGroup(BasePropertyKey basePropertyKey, int versionNumber)
	 {
	     super(basePropertyKey, versionNumber);
	 }

	public PDPMComplexEligibilityGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
	{
	    super(basePropertyKey, validators);
	}

	public PDPMComplexEligibilityGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
	                               List<Validator> validators)
	        throws DataValidationException, InvocationTargetException
	{
	    super(basePropertyKey, propertyGroup, validators);
	}

	public PDPMComplexEligibilityGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
	{
	    super(basePropertyKey, versionNumber, validators);
	}
	
	protected void initializeProperties()
    {
    	isPDPMComplexEligible = new BooleanBasePropertyImpl(getPropertyCategoryType(), PDPM_COMPLEX_ELIGIBILITY, getPropertyKey(), getType());
    }

	@Override
	public BasePropertyType getType()
	{
		return ROUTING_PROPERTY_TYPE;
	}
	
	public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(PDPM_COMPLEX_ELIGIBILITY))
        {
            return isPDPMComplexEligible;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException(" PDPMComplexEligibilityGroup >>>>Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }
	
	public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = isPDPMComplexEligible;

        return properties;
    }
	
	public boolean isPDPMComplexEligible()
	{
		return isPDPMComplexEligible.getBooleanValue();
	}
	
	public void setPDPMComplexEligible(boolean value)
	{
		isPDPMComplexEligible.setBooleanValue(value);
	}
	
	public Object clone() throws CloneNotSupportedException
    {
    	PDPMComplexEligibilityGroup newGroup = (PDPMComplexEligibilityGroup) super.clone();
        newGroup.isPDPMComplexEligible = (BooleanBaseProperty) isPDPMComplexEligible.clone();

        return newGroup;
    }

}
