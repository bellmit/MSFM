package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: RoutingPropertyTypeImpl
//
// PACKAGE: com.cboe.domain.firmRoutingProperty2.test2
// 
// Created: Jul 21, 2006 1:27:28 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import com.cboe.interfaces.domain.property.PropertyCategoryTypes;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKeyType;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class RoutingPropertyTypeImpl extends BasePropertyTypeImpl
{
    /*
        NOTE: the name passed as the first argument of RoutingPropertyTypeImpl is significant in that there must be
        an entry in the handler map of com.cboe.ohs.domain.RoutingPropertyServiceImpl keyed by the name given here.
        It is also the name used in PropertyName field of the CSV file used to load enumerated values.
    */
    public static final BasePropertyType FIRM_VOLUME_LIMIT_SIMPLE =
        new RoutingPropertyTypeImpl("VolumeSimple", "Firm Volume Limit Simple Order", 
                                    BasePropertyKeyTypeImpl.FIRM_VOLUME_LIMIT_SIMPLE_KEY,
                                    RoutingPropertyMaskHelper.FIRM_VOLUME_LIMIT_MASK);
    
    public static final BasePropertyType FIRM_VOLUME_LIMIT_COMPLEX =
        new RoutingPropertyTypeImpl("VolumeComplex", "Firm Volume Limit Complex Order", 
                                    BasePropertyKeyTypeImpl.FIRM_VOLUME_LIMIT_COMPLEX_KEY,
                                    RoutingPropertyMaskHelper.FIRM_VOLUME_LIMIT_MASK);

    public static final BasePropertyType FIRM_VOLUME_LIMIT_SIMPLE_POST_STATION =
        new RoutingPropertyTypeImpl("VolumeSimplePostStation", "Firm Volume Limit Simple Order(Post/Station)", 
                                    BasePropertyKeyTypeImpl.FIRM_VOLUME_LIMIT_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.FIRM_VOLUME_LIMIT_POST_STATION_MASK);

    public static final BasePropertyType FIRM_VOLUME_LIMIT_COMPLEX_POST_STATION =
        new RoutingPropertyTypeImpl("VolumeComplexPostStation", "Firm Volume Limit Complex Order(Post/Station)", 
                                    BasePropertyKeyTypeImpl.FIRM_VOLUME_LIMIT_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.FIRM_VOLUME_LIMIT_POST_STATION_MASK);

    
    public static final BasePropertyType ELIGIBLE_PDPM =
            new RoutingPropertyTypeImpl("EligiblePDPM", "Eligible PDPM", 
                                        BasePropertyKeyTypeImpl.ELIGIBLE_PDPM_KEY,
                                        RoutingPropertyMaskHelper.ELIGIBLE_PDPM_MASK);
    


    public static final BasePropertyType PDPM_ASSIGNMENT =
            new RoutingPropertyTypeImpl("PDPMAssignment", "PDPM Assignment", 
                                        BasePropertyKeyTypeImpl.PDPM_ASSIGNMENT_KEY,
                                        RoutingPropertyMaskHelper.PDPM_ASSIGNMENT_MASK);

    public static final BasePropertyType PDPM_ASSIGNMENT_POST_STATION =
            new RoutingPropertyTypeImpl("PDPMAssignmentPostStation", "PDPM Assignment (Post/Station)",
                                        BasePropertyKeyTypeImpl.PDPM_ASSIGNMENT_POST_STATION_KEY,
                                        RoutingPropertyMaskHelper.PDPM_ASSIGNMENT_POST_STATION_MASK);
    
    
       

    public static final BasePropertyType COA_ELIGIBILITY =
        new RoutingPropertyTypeImpl("COAEligible", "COA Eligible", 
                                    BasePropertyKeyTypeImpl.COA_ELIGIBILITY_KEY,
                                    RoutingPropertyMaskHelper.COA_ELIGIBILITY_MASK);
   


    public static final BasePropertyType COA_ELIGIBILITY_POST_STATION =
        new RoutingPropertyTypeImpl("COAEligiblePostStation", "COA Eligible (Post/Station)",
                                    BasePropertyKeyTypeImpl.COA_ELIGIBILITY_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.COA_ELIGIBILITY_POST_STATION_MASK);
    
    public static final BasePropertyType REASONABILITY_EDIT =
        new RoutingPropertyTypeImpl("ReasonabilityEdit", "ReasonabilityEdit", 
                                    BasePropertyKeyTypeImpl.REASONABILITY_EDIT_KEY,
                                    RoutingPropertyMaskHelper.REASONABILITY_MASK);

    public static final BasePropertyType REASONABILITY_EDIT_POST_STATION =
        new RoutingPropertyTypeImpl("ReasonabilityEditPostStation", "ReasonabilityEdit (Post/Station)",
                                    BasePropertyKeyTypeImpl.REASONABILITY_EDIT_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.REASONABILITY_POST_STATION_MASK);
    

    public static final BasePropertyType RESTRICTED_SERIES =
            new RoutingPropertyTypeImpl("RestrictedSeries", "Restricted Series Eligibility",
                                        BasePropertyKeyTypeImpl.RESTRICTED_SERIES_KEY,
                                        RoutingPropertyMaskHelper.RESTRICTED_SERIES_MASK);

    public static final BasePropertyType BOOTH_DEFAULT_DESTINATION =
            new RoutingPropertyTypeImpl("BoothDefaultDestination", "Booth Default Destination",
                                        BasePropertyKeyTypeImpl.BOOTH_DEFAULT_DESTINATION_KEY,
                                        RoutingPropertyMaskHelper.BOOTH_DEFAULT_DESTINATION_MASK);

    public static final BasePropertyType BOOTH_DEFAULT_DESTINATION_POST_STATION =
        new RoutingPropertyTypeImpl("BoothDefaultDestinationPostStation", "Booth Default Destination (Post/Station)",
                                    BasePropertyKeyTypeImpl.BOOTH_DEFAULT_DESTINATION_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.BOOTH_DEFAULT_DESTINATION_POST_STATION_MASK);
    
    public static final BasePropertyType CROWD_DEFAULT_DESTINATION =
            new RoutingPropertyTypeImpl("CrowdDefaultDestination", "Crowd Default Destination",
                                        BasePropertyKeyTypeImpl.CROWD_DEFAULT_DESTINATION_KEY,
                                        RoutingPropertyMaskHelper.CROWD_DEFAULT_DESTINATION_MASK);
    
    public static final BasePropertyType CROWD_DEFAULT_DESTINATION_POST_STATION =
        new RoutingPropertyTypeImpl("CrowdDefaultDestinationPostStation", "Crowd Default Destination (Post/Station)",
                                    BasePropertyKeyTypeImpl.CROWD_DEFAULT_DESTINATION_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.CROWD_DEFAULT_DESTINATION_POST_STATION_MASK);

    public static final BasePropertyType HELP_DESK_DEFAULT_DESTINATION =
            new RoutingPropertyTypeImpl("HelpDeskDefaultDestination", "Help Desk Default Destination",
                                        BasePropertyKeyTypeImpl.HELP_DESK_DEFAULT_DESTINATION_KEY,
                                        RoutingPropertyMaskHelper.HELP_DESK_DEFAULT_DESTINATION_MASK);
    
    public static final BasePropertyType HELP_DESK_DEFAULT_DESTINATION_POST_STATION  =
        new RoutingPropertyTypeImpl("HelpDeskDefaultDestinationPostStation", "Help Desk Default Destination (Post/Station)",
                                    BasePropertyKeyTypeImpl.HELP_DESK_DEFAULT_DESTINATION_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.HELP_DESK_DEFAULT_DESTINATION_POST_STATION_MASK);

    public static final BasePropertyType PAR_DEFAULT_DESTINATION =
            new RoutingPropertyTypeImpl("ParDefaultDestination", "PAR Default Destination",
                                        BasePropertyKeyTypeImpl.PAR_DEFAULT_DESTINATION_KEY,
                                        RoutingPropertyMaskHelper.PAR_DEFAULT_DESTINATION_MASK);
    
    public static final BasePropertyType PAR_DEFAULT_DESTINATION_POST_STATION =
        new RoutingPropertyTypeImpl("ParDefaultDestinationPostStation", "PAR Default Destination (Post/Station)",
                                    BasePropertyKeyTypeImpl.PAR_DEFAULT_DESTINATION_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.PAR_DEFAULT_DESTINATION_POST_STATION_MASK);    

    public static final BasePropertyType FIRM_CLASS_PAR_SIMPLE =
            new RoutingPropertyTypeImpl("FirmClassParSimple", "Firm Class PAR Destination Simple",
                                        BasePropertyKeyTypeImpl.SIMPLE_PAR_DEFAULT_DESTINATION_KEY,
                                        RoutingPropertyMaskHelper.PAR_DEFAULT_DESTINATION_MASK);

    public static final BasePropertyType FIRM_CLASS_PAR_SIMPLE_POST_STATION =
        new RoutingPropertyTypeImpl("FirmClassParSimplePostStation", "Firm Class PAR Destination Simple (Post/Station)",
                                    BasePropertyKeyTypeImpl.PAR_DEFAULT_DESTINATION_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.PAR_DEFAULT_DESTINATION_POST_STATION_MASK);

    public static final BasePropertyType FIRM_CLASS_PAR_COMPLEX =
        new RoutingPropertyTypeImpl("FirmClassParComplex", "Firm Class PAR Destination Complex",
                                    BasePropertyKeyTypeImpl.COMPLEX_PAR_DEFAULT_DESTINATION_KEY,
                                    RoutingPropertyMaskHelper.PAR_DEFAULT_DESTINATION_MASK);

    public static final BasePropertyType FIRM_CLASS_PAR_COMPLEX_POST_STATION =
        new RoutingPropertyTypeImpl("FirmClassParComplexPostStation", "Firm Class PAR Destination Complex (Post/Station)",
                                    BasePropertyKeyTypeImpl.PAR_DEFAULT_DESTINATION_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.PAR_DEFAULT_DESTINATION_POST_STATION_MASK);

    public static final BasePropertyType BOOTH_DIRECT_ROUTING =
            new RoutingPropertyTypeImpl("BoothDirectRouting", "Booth Direct Routing",
                                        BasePropertyKeyTypeImpl.BOOTH_DIRECT_ROUTING_KEY,
                                        RoutingPropertyMaskHelper.BOOTH_DIRECT_ROUTING_MASK);

    public static final BasePropertyType PAR_DIRECT_ROUTING =
            new RoutingPropertyTypeImpl("ParDirectRouting", "PAR Direct Routing",
                                        BasePropertyKeyTypeImpl.PAR_DIRECT_ROUTING_KEY,
                                        RoutingPropertyMaskHelper.PAR_DIRECT_ROUTING_MASK);

    public static final BasePropertyType ALTERNATE_DESTINATIONS =
            new RoutingPropertyTypeImpl("AlternateDestinations", "Alternate Destinations",
                                        BasePropertyKeyTypeImpl.ALTERNATE_DESTINATIONS_KEY,
                                        RoutingPropertyMaskHelper.ALTERNATE_DESTINATIONS_MASK);

    public static final BasePropertyType PRINT_DESTINATIONS =
            new RoutingPropertyTypeImpl("PrintDestinations", "Print Destinations",
                                        BasePropertyKeyTypeImpl.PRINT_DESTINATIONS_KEY,
                                        RoutingPropertyMaskHelper.PRINT_DESTINATIONS_MASK);

    public static final BasePropertyType SESSION_CLASS_PAR_ROUTING =
            new RoutingPropertyTypeImpl("SessionClassParRouting", "Session Class PAR Routing",
                                        BasePropertyKeyTypeImpl.PAR_DIRECT_SESSION_CLASS_KEY,
                                        RoutingPropertyMaskHelper.SESSION_CLASS_PAR_ROUTING_MASK);

    public static final BasePropertyType SESSION_CLASS_PAR_ROUTING_POST_STATION =
        new RoutingPropertyTypeImpl("SessionClassParRoutingPostStation", "Session Class PAR Routing (Post/Station)",
                                    BasePropertyKeyTypeImpl.PAR_DIRECT_SESSION_CLASS_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.SESSION_CLASS_PAR_ROUTING_POST_STATION_MASK);

    public static final BasePropertyType FIRM_CORR_BRANCH_OMT =
            new RoutingPropertyTypeImpl("FirmCorrBranchOMT", "Firm Corr Branch OMT",
                                        BasePropertyKeyTypeImpl.FIRM_CORR_BRANCH_OMT_KEY,
                                        RoutingPropertyMaskHelper.FIRM_CORR_BRANCH_OMT_MASK);
    public static final BasePropertyType ALL_ELECTRONIC_TRADING_CLASS =
        new RoutingPropertyTypeImpl("AllElectronicTradingClass", "Class enabled for all electronic session",
                                    BasePropertyKeyTypeImpl.ALL_ELECTRONIC_TRADING_CLASS_KEY,
                                    RoutingPropertyMaskHelper.ALL_ELECTRONIC_TRADING_CLASS_MASK);

    public static final BasePropertyType VOLUME_DEV_DESINATION_CHECK_SIMPLE =
            new RoutingPropertyTypeImpl("VolumeDevCheckSimple",
                                        "Volume Deviation Check Simple Orders",
                                        BasePropertyKeyTypeImpl.SESSION_FIRM_SIMPLE_CLASS_ORIGIN_LEVEL_KEY,
                                        RoutingPropertyMaskHelper.SESSION_FIRM_CLASS_ORIGIN_LEVEL_MASK);

        public static final BasePropertyType VOLUME_DEV_DESINATION_CHECK_COMPLEX =
        new RoutingPropertyTypeImpl("VolumeDevCheckComplex", "Volume Deviation Check Complex Orders",
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_COMPLEX_CLASS_ORIGIN_LEVEL_KEY,
                                    RoutingPropertyMaskHelper.SESSION_FIRM_CLASS_ORIGIN_LEVEL_MASK);
    
    public static final BasePropertyType VOLUME_DEV_DESINATION_CHECK_BUYWRITE_DN =
        new RoutingPropertyTypeImpl("VolumeDevCheckBWDN", "Volume Deviation Check Buy Write Delta Neutral Orders", 
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_CLASS_ORIGIN_LEVEL_KEY,
                                    RoutingPropertyMaskHelper.SESSION_FIRM_CLASS_ORIGIN_LEVEL_MASK);
    
    public static final BasePropertyType VOLUME_DEV_DESINATION_CHECK_SIMPLE_POST_STATION =
        new RoutingPropertyTypeImpl("VolumeDevCheckSimplePostStation", "Volume Deviation Check Simple Orders (Post/Station)", 
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_KEY,
                                    RoutingPropertyMaskHelper.SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_MASK);
    
    public static final BasePropertyType VOLUME_DEV_DESINATION_CHECK_COMPLEX_POST_STATION =
        new RoutingPropertyTypeImpl("VolumeDevCheckComplexPostStation", "Volume Deviation Check Complex Orders (Post/Station)", 
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_KEY,
                                    RoutingPropertyMaskHelper.SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_MASK);
    
    public static final BasePropertyType VOLUME_DEV_DESINATION_CHECK_BUYWRITE_DN_POST_STATION =
        new RoutingPropertyTypeImpl("VolumeDevCheckBWDNPostStation", "Volume Deviation Check Buy Write Delta Neutral Orders (Post/Station)", 
                                    BasePropertyKeyTypeImpl.SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_KEY,
                                    RoutingPropertyMaskHelper.SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_MASK);

    public static final BasePropertyType BOOTH_WIRE_ORDER_PREFERENCE =
        new RoutingPropertyTypeImpl("BoothWireOrderPreference", "Booth or Wire Order Preference",
                                    BasePropertyKeyTypeImpl.BOOTH_WIRE_ORDER_PREFERENCE_KEY,
                                    RoutingPropertyMaskHelper.BOOTH_WIRE_ORDER_PREFERENCE_MASK);

    public static final BasePropertyType FIRM_CORR_CLASS_PAR_SIMPLE =
        new RoutingPropertyTypeImpl("FirmCorrClassPARSimple", "Firm Corr Class PAR Simple",
                                  BasePropertyKeyTypeImpl.FIRM_CORR_CLASS_PAR_KEY,
                                  RoutingPropertyMaskHelper.FIRM_CORR_CLASS_PAR_MASK);
    
    public static final BasePropertyType FIRM_CORR_PAR_SIMPLE_POST_STATION =
        new RoutingPropertyTypeImpl("FirmCorrClassPARSimplePostStation", "Firm Corr Class PAR Simple (Post/Station)",
                              BasePropertyKeyTypeImpl.FIRM_CORR_CLASS_POST_STATIOM_KEY,
                              RoutingPropertyMaskHelper.FIRM_CORR_PAR_POST_STATION_MASK);
    
    public static final BasePropertyType FIRM_CORR_CLASS_PAR_COMPLEX =
        new RoutingPropertyTypeImpl("FirmCorrClassPARComplex", "Firm Corr Class PAR Complex",
                              BasePropertyKeyTypeImpl.FIRM_CORR_CLASS_PAR_KEY,
                              RoutingPropertyMaskHelper.FIRM_CORR_CLASS_PAR_MASK);
    
    public static final BasePropertyType FIRM_CORR_PAR_COMPLEX_POST_STATION =
        new RoutingPropertyTypeImpl("FirmCorrClassPARComplexPostStation", "Firm Corr Class PAR Complex (Post/Station)",
                          BasePropertyKeyTypeImpl.FIRM_CORR_CLASS_POST_STATIOM_KEY,
                          RoutingPropertyMaskHelper.FIRM_CORR_PAR_POST_STATION_MASK);

    public static final BasePropertyType ALLOW_INCOMING_ISO =
        new RoutingPropertyTypeImpl("AllowIncomingISO", "Allow Incoming ISO Orders", 
                                    BasePropertyKeyTypeImpl.ALLOW_INCOMING_ISO_KEY,
                                    RoutingPropertyMaskHelper.ALLOW_INCOMING_ISO_MASK);

    public static final BasePropertyType DISABLE_LINKAGE_ON_PAR =
        new RoutingPropertyTypeImpl("DisableLinkageOnPar", "Disable Linkage On Par", 
                BasePropertyKeyTypeImpl.DISABLE_LINKAGE_ON_PAR_KEY,
                RoutingPropertyMaskHelper.DISABLE_LINKAGE_ON_PAR_MASK);
    
    
    public static final BasePropertyType DEFAULT_LINKAGE_ROUTER =
        new RoutingPropertyTypeImpl("DefaultLinkageRouter", "Default Linkage Router",
                                    BasePropertyKeyTypeImpl.DEFAULT_LINKAGE_ROUTER_KEY, 
                                    RoutingPropertyMaskHelper.DEFAULT_LINKAGE_ROUTER_MASK);
    
    public static final BasePropertyType LINKAGE_ROUTER_ASSIGNMENT =
        new RoutingPropertyTypeImpl("LinkageRouterVendorAssignment", "Linkage Router Vendor Assignment", 
                                    BasePropertyKeyTypeImpl.LINKAGE_ROUTER_ASSIGNMENT_KEY,
                                    RoutingPropertyMaskHelper.LINKAGE_ROUTER_ASSIGNMENT_MASK); 
    
    public static final BasePropertyType LINKAGE_ROUTER_ASSIGNMENT_POST_STATION =
        new RoutingPropertyTypeImpl("LinkageRouterAssignmentPostStation", "Linkage Router Vendor Assignment Post/Station", 
                                    BasePropertyKeyTypeImpl.LINKAGE_ROUTER_ASSIGNMENT_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.LINKAGE_ROUTER_ASSIGNMENT_POST_STATION_MASK);    
    
    public static final BasePropertyType BACKUP_LINKAGE_ROUTER_ASSIGNMENT =
        new RoutingPropertyTypeImpl("BackupLinkageRouterAssignment", "Backup Linkage Router Assignment", 
                                    BasePropertyKeyTypeImpl.LINKAGE_ROUTER_ASSIGNMENT_KEY,
                                    RoutingPropertyMaskHelper.LINKAGE_ROUTER_ASSIGNMENT_MASK); 
    
    public static final BasePropertyType BACKUP_LINKAGE_ROUTER_ASSIGNMENT_POST_STATION =
        new RoutingPropertyTypeImpl("BackupLinkageRouterAssignmentPostStation", "Backup Linkage Router Assignment Post/Station",
                                    BasePropertyKeyTypeImpl.LINKAGE_ROUTER_ASSIGNMENT_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.LINKAGE_ROUTER_ASSIGNMENT_POST_STATION_MASK);      
    
    public static final BasePropertyType ENABLE_BOOKING_FORCPS =
        new RoutingPropertyTypeImpl("EnableBookingForCPS", "Enable CPS Order Booking", 
                                    BasePropertyKeyTypeImpl.ENABLE_BOOKING_FORCPS_KEY,
                                    RoutingPropertyMaskHelper.ENABLE_BOOKING_FORCPS_MASK);

    public static final BasePropertyType REASONABILITY_EDIT_BYPASS_CLASS =
        new RoutingPropertyTypeImpl("ReasonabilityEditBypassClass", "Reasonability-edit Bypass By Class", 
                                    BasePropertyKeyTypeImpl.REASONABILITY_EDIT_BYPASS_CLASS_KEY,
                                    RoutingPropertyMaskHelper.REASONABILITY_EDIT_CLASS_MASK);
    
    public static final BasePropertyType REASONABILITY_EDIT_BYPASS_POST_STATION =
        new RoutingPropertyTypeImpl("ReasonabilityEditBypassPostStation", "Reasonability-edit Bypass By Post/Station", 
                                    BasePropertyKeyTypeImpl.REASONABILITY_EDIT_BYPASS_POST_STATION_KEY,
                                    RoutingPropertyMaskHelper.REASONABILITY_EDIT_POST_STATION_MASK);
    //PDPMComplexEligibility
    public static final BasePropertyType PDPM_COMPLEX_ELIGIBILITY = 
    	new RoutingPropertyTypeImpl("PDPMComplexEligibility", "PDPM Complex Eligibility",
    			                    BasePropertyKeyTypeImpl.PDPM_COMPLEX_ELIGIBILITY_KEY,
    			                    RoutingPropertyMaskHelper.PDPM_COMPLEX_ELIGIBILITY_MASK);
    
    public static final BasePropertyType NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING =
        new RoutingPropertyTypeImpl("NewBobOriginCodeContingencyTypeMapping", "New BOB Origin Code Contingency Type Mapping", 
                                    BasePropertyKeyTypeImpl.NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING_KEY,
                                    RoutingPropertyMaskHelper.NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING_MASK);
    
    public static final BasePropertyType[] allRoutngProperties =
    {               
                    FIRM_VOLUME_LIMIT_SIMPLE,
                    FIRM_VOLUME_LIMIT_SIMPLE_POST_STATION,
                    FIRM_VOLUME_LIMIT_COMPLEX,
                    FIRM_VOLUME_LIMIT_COMPLEX_POST_STATION,
                    ELIGIBLE_PDPM,
                    PDPM_ASSIGNMENT,
                    PDPM_ASSIGNMENT_POST_STATION,
                    RESTRICTED_SERIES,
                    BOOTH_DIRECT_ROUTING,
                    PAR_DIRECT_ROUTING,
                    BOOTH_DEFAULT_DESTINATION,
                    BOOTH_DEFAULT_DESTINATION_POST_STATION,
                    CROWD_DEFAULT_DESTINATION,
                    CROWD_DEFAULT_DESTINATION_POST_STATION,
                    HELP_DESK_DEFAULT_DESTINATION,
                    HELP_DESK_DEFAULT_DESTINATION_POST_STATION,                    
                    //FIRM_CLASS_PAR_SIMPLE,
                    //FIRM_CLASS_PAR_SIMPLE_POST_STATION,
                    //FIRM_CLASS_PAR_COMPLEX,
                    //FIRM_CLASS_PAR_COMPLEX_POST_STATION,
                    ALTERNATE_DESTINATIONS,                    
                    SESSION_CLASS_PAR_ROUTING,
                    SESSION_CLASS_PAR_ROUTING_POST_STATION,
                    FIRM_CORR_BRANCH_OMT,
                    COA_ELIGIBILITY,
                    COA_ELIGIBILITY_POST_STATION,
                    VOLUME_DEV_DESINATION_CHECK_SIMPLE,
                    VOLUME_DEV_DESINATION_CHECK_COMPLEX,
                    VOLUME_DEV_DESINATION_CHECK_BUYWRITE_DN,
                    VOLUME_DEV_DESINATION_CHECK_SIMPLE_POST_STATION,
                    VOLUME_DEV_DESINATION_CHECK_COMPLEX_POST_STATION,
                    VOLUME_DEV_DESINATION_CHECK_BUYWRITE_DN_POST_STATION,
                    REASONABILITY_EDIT,
                    REASONABILITY_EDIT_POST_STATION,
                    BOOTH_WIRE_ORDER_PREFERENCE,
                    ALL_ELECTRONIC_TRADING_CLASS,
                    FIRM_CORR_CLASS_PAR_SIMPLE,
                    FIRM_CORR_PAR_SIMPLE_POST_STATION,
                    FIRM_CORR_CLASS_PAR_COMPLEX,
                    FIRM_CORR_PAR_COMPLEX_POST_STATION,
                    ALLOW_INCOMING_ISO,
                    DISABLE_LINKAGE_ON_PAR,
                    DEFAULT_LINKAGE_ROUTER,
                    LINKAGE_ROUTER_ASSIGNMENT,
                    LINKAGE_ROUTER_ASSIGNMENT_POST_STATION,
                    BACKUP_LINKAGE_ROUTER_ASSIGNMENT,
                    ENABLE_BOOKING_FORCPS,
                    BACKUP_LINKAGE_ROUTER_ASSIGNMENT_POST_STATION,
                    REASONABILITY_EDIT_BYPASS_CLASS,
                    REASONABILITY_EDIT_BYPASS_POST_STATION,
                    PDPM_COMPLEX_ELIGIBILITY,
                    NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING
            };

    static
    {
        for (BasePropertyType propertyType : allRoutngProperties)
        {
            allPropertyTypes.put(propertyType.getName(), propertyType);
        }
    }

    /**
     * Constructor that uses PropertyCategoryTypes.TRADING_PROPERTIES as the set propertyCategory.
     * @param name programmatic name
     * @param fullName full english name for display trading session
     */
    protected RoutingPropertyTypeImpl(String name, String fullName, BasePropertyKeyType keyType,int[][] masks)
    {
        super(PropertyCategoryTypes.ROUTING_PROPERTIES, name, fullName, keyType,masks);
    }
}
