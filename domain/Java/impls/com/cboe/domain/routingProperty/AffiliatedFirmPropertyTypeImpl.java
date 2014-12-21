package com.cboe.domain.routingProperty;

import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKeyType;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class AffiliatedFirmPropertyTypeImpl extends BasePropertyTypeImpl
{
    /*
     * Property to Override FirmRegistration for DirectedAIM.
     */
    public static final BasePropertyType DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION_OVERRIDE =
        new AffiliatedFirmPropertyTypeImpl("DirectedAIMAffiliatedFirmRegistrationOverride", "Directed AIM Affiliated Firm Registration Override",
                                    BasePropertyKeyTypeImpl.DIRECTED_AIM_SESSION_AFFILIATED_FIRM_KEY,
                                    AffiliatedFirmPropertyMaskHelper.DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION_OVERRIDE_MASK);
    /*
     * Property to register for DirectedAIM.
     */
    public static final BasePropertyType DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION =
    	new AffiliatedFirmPropertyTypeImpl("DirectedAIMAffiliatedFirmRegistration", "Directed AIM Affiliated Firm Registration",
    			BasePropertyKeyTypeImpl.DIRECTED_AIM_SESSION_AFFILIATED_FIRM_CLASS_KEY,
    			AffiliatedFirmPropertyMaskHelper.DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION_MASK);
    
    /*
     * Property to define Affiliated[Target] Firm Partnership for DirectedAIM.
     */
    public static final BasePropertyType DIRECTED_AIM_AFFILIATED_FIRM_PARTNERSHIP =
        new AffiliatedFirmPropertyTypeImpl("DirectedAIMAffiliatedFirmPartnership", "Directed AIM Affiliated Firm Partnership",
                                    BasePropertyKeyTypeImpl.DIRECTED_AIM_AFFILIATED_FIRM_PARTNERSHIP_KEY,
                                    AffiliatedFirmPropertyMaskHelper.DIRECTED_AIM_AFFILIATED_FIRM_PARTNERSHIP_MASK);
    
    /**
     * Add the new param added to the list.
     * Modified by Cognizant Technology Solutions 
     */
    public static final BasePropertyType[] allAffiliatedFirmProperties =
    {
    	DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION_OVERRIDE,
        DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION,
        DIRECTED_AIM_AFFILIATED_FIRM_PARTNERSHIP
    };

    static
    {
        for(BasePropertyType propertyType : allAffiliatedFirmProperties)
        {
            allPropertyTypes.put(propertyType.getName(), propertyType);
        }
    }

    /**
     * Constructor that uses PropertyCategoryTypes.AFFILIATED_FIRM_PROPERTIES as the set propertyCategory.
     * @param name programmatic name
     * @param fullName full english name for display trading session
     */
    protected AffiliatedFirmPropertyTypeImpl(String name, String fullName, BasePropertyKeyType keyType, int[][] masks)
    {
        super(PropertyCategoryTypes.AFFILIATED_FIRM_PROPERTIES, name, fullName, keyType, masks);
    }
}
