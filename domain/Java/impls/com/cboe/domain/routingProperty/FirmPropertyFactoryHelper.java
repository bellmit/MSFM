//-----------------------------------------------------------------------------------
//Source file: FirmPropertyFactoryHelper
//
//PACKAGE: com.cboe.domain.routingProperty
//
//Created: 
//-----------------------------------------------------------------------------------
//Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

package com.cboe.domain.routingProperty;

import java.util.HashMap;
import java.util.Map;

import com.cboe.domain.routingProperty.properties.AuctionFirmInfoParametersGroup;
import com.cboe.domain.routingProperty.properties.DirectedAIMNotificationFirmInfoGroup;
import com.cboe.domain.routingProperty.properties.DropCopyPreferenceGroup;
import com.cboe.domain.routingProperty.properties.EnableNewBOBGroup;
import com.cboe.domain.routingProperty.properties.EtnIndicatorGroup;
import com.cboe.domain.routingProperty.properties.FirmClassAcronymPreferenceGroup;
import com.cboe.domain.routingProperty.properties.FirmPostStationAcronymPreferenceGroup;
import com.cboe.domain.routingProperty.properties.FirmTradingParameterGroup;
import com.cboe.domain.routingProperty.properties.MMTNotificationPreferenceGroup;
import com.cboe.domain.routingProperty.properties.ParClearingPreferenceGroup;
import com.cboe.domain.routingProperty.properties.PriceAdjustmentsCancelPreferenceGroup;
import com.cboe.domain.routingProperty.properties.ShortSaleMarkingGroup;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

class FirmPropertyFactoryHelper
{
    /**
     * Provides a mapping from the BaseProperty names to the Class that is responsible for that BaseProperty's
     * implementation.
     */
    static final Map<String, BasePropertyClassType> PROPERTY_NAME_CLASS_TYPE_MAP = new HashMap<String, BasePropertyClassType>(101);

    static
    {
        addRoutingPropertyClassType(FirmTradingParameterGroup.ROUTING_PROPERTY_TYPE, FirmTradingParameterGroup.class);
        addRoutingPropertyClassType(PriceAdjustmentsCancelPreferenceGroup.FIRM_PROPERTY_TYPE, PriceAdjustmentsCancelPreferenceGroup.class);
        addRoutingPropertyClassType(EtnIndicatorGroup.FIRM_PROPERTY_TYPE,EtnIndicatorGroup.class );
        addRoutingPropertyClassType(DropCopyPreferenceGroup.FIRM_PROPERTY_TYPE, DropCopyPreferenceGroup.class );
        addRoutingPropertyClassType(AuctionFirmInfoParametersGroup.FIRM_PROPERTY_TYPE, AuctionFirmInfoParametersGroup.class );
        addRoutingPropertyClassType(DirectedAIMNotificationFirmInfoGroup.FIRM_PROPERTY_TYPE, DirectedAIMNotificationFirmInfoGroup.class );
        addRoutingPropertyClassType(MMTNotificationPreferenceGroup.FIRM_PROPERTY_TYPE, MMTNotificationPreferenceGroup.class );
        addRoutingPropertyClassType(ParClearingPreferenceGroup.FIRM_PROPERTY_TYPE, ParClearingPreferenceGroup.class );
        addRoutingPropertyClassType(FirmClassAcronymPreferenceGroup.FIRM_PROPERTY_TYPE, FirmClassAcronymPreferenceGroup.class );
        addRoutingPropertyClassType(FirmPostStationAcronymPreferenceGroup.FIRM_PROPERTY_TYPE, FirmPostStationAcronymPreferenceGroup.class );
        addRoutingPropertyClassType(ShortSaleMarkingGroup.FIRM_PROPERTY_TYPE, ShortSaleMarkingGroup.class);
        addRoutingPropertyClassType(EnableNewBOBGroup.FIRM_PROPERTY_TYPE, EnableNewBOBGroup.class);
    }

    private static void addRoutingPropertyClassType(BasePropertyType type, Class propertyGroupClass)
    {
        BasePropertyClassType newClassType = new BasePropertyClassType(type, propertyGroupClass);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(type.getName(), newClassType);
    }
}
