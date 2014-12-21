package com.cboe.domain.routingProperty.properties;

import java.util.List;

import com.cboe.domain.routingProperty.BasePropertyValidationFactoryHome;
import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class AcronymProperty extends StringBasePropertyImpl
{
	
    public AcronymProperty(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                            BasePropertyType type)
    {
        super(propertyCategory, propertyName, basePropertyKey, type);
    }
		
    public AcronymProperty(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
                            BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, basePropertyKey, type, validators);
    }
		
    public String getAcronym()
    {
        return getStringValue();
    }
		
    public void setAcronym(String acronym)
    {
        super.setStringValue(acronym);
    }
		
    @Override
    protected List<Validator> getDefaultValidators()
    {
        return BasePropertyValidationFactoryHome.find().createAcronymValidators(getPropertyName());
    }
}
