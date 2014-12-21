package com.cboe.domain.routingProperty.properties;

import java.util.List;

import com.cboe.domain.routingProperty.BasePropertyValidationFactoryHome;
import com.cboe.domain.routingProperty.common.StringListBasePropertyImpl;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class DirectedAIMPartnerFirmsListProperty extends
		StringListBasePropertyImpl {
	
		public DirectedAIMPartnerFirmsListProperty(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
		              BasePropertyType type)
		{
			super(propertyCategory, propertyName, basePropertyKey, type);
		}
		
		public DirectedAIMPartnerFirmsListProperty(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey,
		              BasePropertyType type, List<Validator> validators)
		{
			super(propertyCategory, propertyName, basePropertyKey, type, validators);
		}
		
		public String[] getDirectedAIMFirmPartners()
		{
			return getStringListValue();
		}
		
		public void setDirectedAIMFirmPartners(String[] firmPartners)
		{
			setStringListValue(firmPartners);
		}
		
		@Override
		protected List<Validator> getDefaultValidators()
		{
            return BasePropertyValidationFactoryHome.find().createPartnershipFirmsValidators(getPropertyName());
        }

}
