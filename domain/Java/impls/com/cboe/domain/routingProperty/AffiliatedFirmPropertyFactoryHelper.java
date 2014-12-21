package com.cboe.domain.routingProperty;

import java.util.HashMap;
import java.util.Map;
import com.cboe.domain.routingProperty.properties.DirectedAIMAffiliatedFirmPartnershipGroup;
import com.cboe.domain.routingProperty.properties.DirectedAIMFirmRegistrationGroup;
import com.cboe.domain.routingProperty.properties.DirectedAIMFirmRegistrationOverrideGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class AffiliatedFirmPropertyFactoryHelper 
{  
	/**
     * Provides a mapping from the BaseProperty names to the Class that is responsible for that BaseProperty's
     * implementation.
     */
    static final Map<String, BasePropertyClassType> PROPERTY_NAME_CLASS_TYPE_MAP = new HashMap<String, BasePropertyClassType>(101);

    static
    {
        addRoutingPropertyClassType(DirectedAIMFirmRegistrationGroup.AFFILIATED_FIRM_PROPERTY_TYPE, DirectedAIMFirmRegistrationGroup.class );        
    	addRoutingPropertyClassType(DirectedAIMFirmRegistrationOverrideGroup.AFFILIATED_FIRM_PROPERTY_TYPE, DirectedAIMFirmRegistrationOverrideGroup.class );
    	addRoutingPropertyClassType(DirectedAIMAffiliatedFirmPartnershipGroup.AFFILIATED_FIRM_PROPERTY_TYPE, DirectedAIMAffiliatedFirmPartnershipGroup.class );
    }

    private static void addRoutingPropertyClassType(BasePropertyType type, Class propertyGroupClass)
    {
        BasePropertyClassType newClassType = new BasePropertyClassType(type, propertyGroupClass);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(type.getName(), newClassType);
    }
}
