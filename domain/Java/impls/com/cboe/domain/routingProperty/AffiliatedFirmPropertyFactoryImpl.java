package com.cboe.domain.routingProperty;

import java.util.Map;

import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;

public class AffiliatedFirmPropertyFactoryImpl extends BasePropertyFactoryImpl
{
	protected String getPropertyCategoryType()
	{
	    return PropertyCategoryTypes.AFFILIATED_FIRM_PROPERTIES;
	}
	
	protected Map<String, BasePropertyClassType> getPropertyMap()
	{
	    return AffiliatedFirmPropertyFactoryHelper.PROPERTY_NAME_CLASS_TYPE_MAP;
	}
	
	protected BasePropertyFactory getPropertyFactoryHome()
	{
	    return AffiliatedFirmPropertyFactoryHome.find();
	}
}