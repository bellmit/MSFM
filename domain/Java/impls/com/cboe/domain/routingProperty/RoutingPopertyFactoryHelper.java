//-----------------------------------------------------------------------------------
//Source file: RoutingPopertyFactoryHelper
//
//PACKAGE: com.cboe.domain.routingProperty.key
//
//Created: Aug 23, 2006 11:02:45 AM
//-----------------------------------------------------------------------------------
//Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------

package com.cboe.domain.routingProperty;

import java.util.HashMap;
import java.util.Map;

import com.cboe.domain.routingProperty.properties.*;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

class RoutingPopertyFactoryHelper
{
    
    /**
     * Provides a mapping from the BaseProperty names to the Class that is responsible for that BaseProperty's
     * implementation.
     */
    static final Map<String, BasePropertyClassType> PROPERTY_NAME_CLASS_TYPE_MAP = new HashMap<String, BasePropertyClassType>(101);

    static
    {             
        addRoutingPropertyClassType(FirmVolumeLimitSimpleGroup.ROUTING_PROPERTY_TYPE, FirmVolumeLimitSimpleGroup.class);
        addRoutingPropertyClassType(FirmVolumeSimplePostStationGroup.ROUTING_PROPERTY_TYPE, FirmVolumeSimplePostStationGroup.class);
        addRoutingPropertyClassType(FirmVolumeLimitComplexGroup.ROUTING_PROPERTY_TYPE, FirmVolumeLimitComplexGroup.class);
        addRoutingPropertyClassType(FirmVolumeComplexPostStationGroup.ROUTING_PROPERTY_TYPE, FirmVolumeComplexPostStationGroup.class);
        addRoutingPropertyClassType(EligiblePDPMGroup.ROUTING_PROPERTY_TYPE, EligiblePDPMGroup.class);
        addRoutingPropertyClassType(PDPMAssignmentGroup.ROUTING_PROPERTY_TYPE, PDPMAssignmentGroup.class);
        addRoutingPropertyClassType(PDPMAssignmentPostStationGroup.ROUTING_PROPERTY_TYPE, PDPMAssignmentPostStationGroup.class);
        addRoutingPropertyClassType(RestrictedSeriesGroup.ROUTING_PROPERTY_TYPE, RestrictedSeriesGroup.class);
        addRoutingPropertyClassType(BoothDefaultDestinationGroup.ROUTING_PROPERTY_TYPE, BoothDefaultDestinationGroup.class);
        addRoutingPropertyClassType(BoothDefaultDestinationPostStationGroup.ROUTING_PROPERTY_TYPE, BoothDefaultDestinationPostStationGroup.class);
        addRoutingPropertyClassType(CrowdDefaultDestinationGroup.ROUTING_PROPERTY_TYPE, CrowdDefaultDestinationGroup.class);
        addRoutingPropertyClassType(CrowdDefaultDestinationPostStationGroup.ROUTING_PROPERTY_TYPE, CrowdDefaultDestinationPostStationGroup.class);
        addRoutingPropertyClassType(HelpDeskDefaultDestinationGroup.ROUTING_PROPERTY_TYPE, HelpDeskDefaultDestinationGroup.class);
        addRoutingPropertyClassType(HelpDeskDefaultDestinationPostStationGroup.ROUTING_PROPERTY_TYPE, HelpDeskDefaultDestinationPostStationGroup.class);
        addRoutingPropertyClassType(ParDefaultDestinationGroup.ROUTING_PROPERTY_TYPE, ParDefaultDestinationGroup.class);
        addRoutingPropertyClassType(ParDefaultDestinationPostStationGroup.ROUTING_PROPERTY_TYPE, ParDefaultDestinationPostStationGroup.class);
        addRoutingPropertyClassType(BoothDirectRoutingGroup.ROUTING_PROPERTY_TYPE, BoothDirectRoutingGroup.class);
        addRoutingPropertyClassType(ParDirectRoutingGroup.ROUTING_PROPERTY_TYPE, ParDirectRoutingGroup.class);
        addRoutingPropertyClassType(AlternateDestinationsGroup.ROUTING_PROPERTY_TYPE, AlternateDestinationsGroup.class);        
        addRoutingPropertyClassType(LinkageOrderDestinationGroup.ROUTING_PROPERTY_TYPE, LinkageOrderDestinationGroup.class);
        addRoutingPropertyClassType(LinkageOrderDestinationPostStationGroup.ROUTING_PROPERTY_TYPE, LinkageOrderDestinationPostStationGroup.class);        
        addRoutingPropertyClassType(FirmCorrBranchOMTDestinationGroup.ROUTING_PROPERTY_TYPE, FirmCorrBranchOMTDestinationGroup.class);
        addRoutingPropertyClassType(EligibleCOAGroup.ROUTING_PROPERTY_TYPE, EligibleCOAGroup.class);
        addRoutingPropertyClassType(EligibleCOAPostStationGroup.ROUTING_PROPERTY_TYPE, EligibleCOAPostStationGroup.class);
        addRoutingPropertyClassType(ReasonabilityEditGroup.ROUTING_PROPERTY_TYPE, ReasonabilityEditGroup.class);
        addRoutingPropertyClassType(ReasonabilityEditPostStationGroup.ROUTING_PROPERTY_TYPE, ReasonabilityEditPostStationGroup.class);
        addRoutingPropertyClassType(SimpleOrderVolumeDeviationDestinationGroup.ROUTING_PROPERTY_TYPE, SimpleOrderVolumeDeviationDestinationGroup.class);
        addRoutingPropertyClassType(ComplexOrderVolumeDeviationDestinationGroup.ROUTING_PROPERTY_TYPE, ComplexOrderVolumeDeviationDestinationGroup.class);
        addRoutingPropertyClassType(BuyWriteOrderVolumeDeviationDestinationGroup.ROUTING_PROPERTY_TYPE, BuyWriteOrderVolumeDeviationDestinationGroup.class);
        addRoutingPropertyClassType(SimpleOrderVolumeDeviationDestinationPostStationGroup.ROUTING_PROPERTY_TYPE, SimpleOrderVolumeDeviationDestinationPostStationGroup.class);
        addRoutingPropertyClassType(ComplexOrderVolumeDeviationDestinationPostStationGroup.ROUTING_PROPERTY_TYPE, ComplexOrderVolumeDeviationDestinationPostStationGroup.class);
        addRoutingPropertyClassType(BuyWriteOrderVolumeDeviationDestinationPostStationGroup.ROUTING_PROPERTY_TYPE, BuyWriteOrderVolumeDeviationDestinationPostStationGroup.class);
        addRoutingPropertyClassType(AllElectronicTradingClassGroup.ROUTING_PROPERTY_TYPE, AllElectronicTradingClassGroup.class);
        addRoutingPropertyClassType(BoothWireOrderPreferenceGroup.ROUTING_PROPERTY_TYPE, BoothWireOrderPreferenceGroup.class);
        addRoutingPropertyClassType(FirmClassPARSimpleGroup.ROUTING_PROPERTY_TYPE, FirmClassPARSimpleGroup.class);
        addRoutingPropertyClassType(FirmClassPARSimplePostStationGroup.ROUTING_PROPERTY_TYPE, FirmClassPARSimplePostStationGroup.class);
        addRoutingPropertyClassType(FirmClassPARComplexGroup.ROUTING_PROPERTY_TYPE, FirmClassPARComplexGroup.class);
        addRoutingPropertyClassType(FirmClassPARComplexPostStationGroup.ROUTING_PROPERTY_TYPE, FirmClassPARComplexPostStationGroup.class);
        addRoutingPropertyClassType(FirmCorrClassPARSimpleGroup.ROUTING_PROPERTY_TYPE, FirmCorrClassPARSimpleGroup.class);
        addRoutingPropertyClassType(FirmCorrPARSimplePostStationGroup.ROUTING_PROPERTY_TYPE, FirmCorrPARSimplePostStationGroup.class);
        addRoutingPropertyClassType(FirmCorrClassPARComplexGroup.ROUTING_PROPERTY_TYPE, FirmCorrClassPARComplexGroup.class);
        addRoutingPropertyClassType(FirmCorrPARComplexPostStationGroup.ROUTING_PROPERTY_TYPE, FirmCorrPARComplexPostStationGroup.class);
        addRoutingPropertyClassType(AllowIncomingISOGroup.ROUTING_PROPERTY_TYPE, AllowIncomingISOGroup.class);
        addRoutingPropertyClassType(DisableLinkageOnParGroup.ROUTING_PROPERTY_TYPE, DisableLinkageOnParGroup.class);
        addRoutingPropertyClassType(SessionRouterVendorGroup.ROUTING_PROPERTY_TYPE, SessionRouterVendorGroup.class);
        addRoutingPropertyClassType(LinkageRouterAssignmentGroup.ROUTING_PROPERTY_TYPE, LinkageRouterAssignmentGroup.class);
        addRoutingPropertyClassType(LinkageRouterAssignmentPostStationtGroup.ROUTING_PROPERTY_TYPE, LinkageRouterAssignmentPostStationtGroup.class);       
        addRoutingPropertyClassType(BackupLinkageRouterAssignmentGroup.ROUTING_PROPERTY_TYPE, BackupLinkageRouterAssignmentGroup.class);
        addRoutingPropertyClassType(BackupLinkageRouterAssignmentPostStationGroup.ROUTING_PROPERTY_TYPE, BackupLinkageRouterAssignmentPostStationGroup.class);    
        addRoutingPropertyClassType(ReasonabilityEditBypassClassGroup.ROUTING_PROPERTY_TYPE, ReasonabilityEditBypassClassGroup.class);        
        addRoutingPropertyClassType(ReasonabilityEditBypassPostStationGroup.ROUTING_PROPERTY_TYPE, ReasonabilityEditBypassPostStationGroup.class);
        addRoutingPropertyClassType(EnableBookingForCPSGroup.ROUTING_PROPERTY_TYPE, EnableBookingForCPSGroup.class);
        addRoutingPropertyClassType(PDPMComplexEligibilityGroup.ROUTING_PROPERTY_TYPE, PDPMComplexEligibilityGroup.class);
        addRoutingPropertyClassType(NewBobOriginCodeContingencyTypeMappingGroup.ROUTING_PROPERTY_TYPE, NewBobOriginCodeContingencyTypeMappingGroup.class);
    }

    private static void addRoutingPropertyClassType(BasePropertyType type, Class routingPropertyGroupClass)
    {
        BasePropertyClassType newClassType = new BasePropertyClassType(type, routingPropertyGroupClass);
        PROPERTY_NAME_CLASS_TYPE_MAP.put(type.getName(), newClassType);
    }
}
