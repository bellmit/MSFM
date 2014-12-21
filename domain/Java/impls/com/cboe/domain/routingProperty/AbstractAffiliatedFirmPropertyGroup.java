package com.cboe.domain.routingProperty;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.Validator;

public abstract class AbstractAffiliatedFirmPropertyGroup extends AbstractBasePropertyGroup
{
	   public static final String PROPERTY_GROUP_CHANGE_EVENT = "AffiliatedFirmPropertyGroup"; // "RoutingPropertyGroup"; // PropertyCategoryTypes.FIRM_PROPERTIES + "Group";
	   public static final String PROPERTY_CHANGE_EVENT       = "AffiliatedFirmProperty"; // "RoutingProperty"; // PropertyCategoryTypes.ROUTING_PROPERTIES; // PropertyCategoryTypes.FIRM_PROPERTIES;

	    protected AbstractAffiliatedFirmPropertyGroup(BasePropertyKey basePropertyKey)
	    {
	        super(basePropertyKey);
	    }

	    protected AbstractAffiliatedFirmPropertyGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
	            throws DataValidationException, InvocationTargetException
	    {
	        super(basePropertyKey, propertyGroup);
	    }

	    protected AbstractAffiliatedFirmPropertyGroup(BasePropertyKey basePropertyKey, int versionNumber)
	    {
	        super(basePropertyKey, versionNumber);
	    }

	    protected AbstractAffiliatedFirmPropertyGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
	    {
	        super(basePropertyKey, validators);
	    }

	    protected AbstractAffiliatedFirmPropertyGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
	                                        List<Validator> validators)
	            throws DataValidationException, InvocationTargetException
	    {
	        super(basePropertyKey, propertyGroup, validators);
	    }

	    protected AbstractAffiliatedFirmPropertyGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
	    {
	        super(basePropertyKey, versionNumber, validators);
	    }

	    @Override
	    protected String getPropertyCategoryType()
	    {
	        return PropertyCategoryTypes.AFFILIATED_FIRM_PROPERTIES;
	    }

	    @Override
	    protected BasePropertyFactory getPropertyFactory()
	    {
	        return AffiliatedFirmPropertyFactoryHome.find();
	    }

	    @Override
	    protected String getPropertyGroupChangeEvent()
	    {
	        return PROPERTY_GROUP_CHANGE_EVENT;
	    }

	    @Override
	    protected String getPropertyChangeEvent()
	    {
	        return PROPERTY_CHANGE_EVENT;
	    }

        protected List<Validator> getDefaultValidators()
        {
            List<Validator> validators = super.getDefaultValidators();
            for (Validator v : BasePropertyValidationFactoryHome.find().createAffiliatedFirmPropertyKeyValidators())
            {
                validators.add(v);
            }
            return validators;
        }
	
	
}
