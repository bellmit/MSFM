package com.cboe.domain.routingProperty;
// -----------------------------------------------------------------------------------
// Source file: BasePropertyKeyTypeImpl
//
// PACKAGE: com.cboe.domain.firmRoutingProperty2.test2.key
// 
// Created: Jul 24, 2006 10:38:12 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.lang.reflect.Constructor;

import com.cboe.domain.routingProperty.key.ExternalExchangeFirmClassKey;
import com.cboe.domain.routingProperty.key.ExternalExchangeFirmPostStationKey;
import com.cboe.domain.routingProperty.key.SessionAffiliatedFirmClassKey;
import com.cboe.domain.routingProperty.key.SessionAffiliatedFirmExecutingFirmCorrBranchKey;
import com.cboe.domain.routingProperty.key.SessionAffiliatedFirmKey;
import com.cboe.domain.routingProperty.key.SessionClassKey;
import com.cboe.domain.routingProperty.key.SessionClassOriginKey;
import com.cboe.domain.routingProperty.key.SessionDefaultFirmClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmBoothKey;
import com.cboe.domain.routingProperty.key.SessionFirmClassAcronymKey;
import com.cboe.domain.routingProperty.key.SessionFirmClassCorrBranchKey;
import com.cboe.domain.routingProperty.key.SessionFirmClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmClassOriginKey;
import com.cboe.domain.routingProperty.key.SessionFirmClassOriginLevelKey;
import com.cboe.domain.routingProperty.key.SessionFirmComplexClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmComplexClassOriginLevelKey;
import com.cboe.domain.routingProperty.key.SessionFirmCorrBranchKey;
import com.cboe.domain.routingProperty.key.SessionFirmCorrClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmCorrParKey;
import com.cboe.domain.routingProperty.key.SessionFirmCorrPostStationKey;
import com.cboe.domain.routingProperty.key.SessionFirmKey;
import com.cboe.domain.routingProperty.key.SessionFirmKeyMarketMaker;
import com.cboe.domain.routingProperty.key.SessionFirmPostStationAcronymKey;
import com.cboe.domain.routingProperty.key.SessionFirmPostStationCorrBranchKey;
import com.cboe.domain.routingProperty.key.SessionFirmPostStationKey;
import com.cboe.domain.routingProperty.key.SessionFirmPostStationOriginKey;
import com.cboe.domain.routingProperty.key.SessionFirmPostStationOriginLevelKey;
import com.cboe.domain.routingProperty.key.SessionFirmSimpleClassKey;
import com.cboe.domain.routingProperty.key.SessionFirmSimpleClassOriginLevelKey;
import com.cboe.domain.routingProperty.key.SessionOriginKey;
import com.cboe.domain.routingProperty.key.SessionPostStationKey;
import com.cboe.domain.routingProperty.key.SessionReasonabilityEditBypassPostStationKey;
import com.cboe.domain.routingProperty.key.SessionRouterVendorKey;
import com.cboe.domain.routingProperty.key.SessionWorkstationNameKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKeyType;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class BasePropertyKeyTypeImpl implements BasePropertyKeyType 
{
    
    private static final Class[] PROPERTY_KEY_CONSTRUCTOR  = {String.class};
    private static final Class[] PROPERTY_TYPE_CONSTRUCTOR = {BasePropertyType.class};
    private static final Class[] DEFAULT_CONSTRUCTOR = {BasePropertyType.class, Object[].class};

    private static final Class[] SESSIONKEY_CONSTRUCTOR = {String.class, String.class};
    private static final Class[] SESSIONFIRMKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class};
    private static final Class[] FIRMSESSIONBOOTH_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class};
    private static final Class[] FIRMSESSIONCLASSKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class};
    private static final Class[] FIRMSESSIONSIMPLECLASSKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class};
    private static final Class[] EXTERNALEXCHANGEFIRMSESSIONCLASSKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, String.class, int.class};
    private static final Class[] FIRMSESSIONCLASSORIGINKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, char.class};
    private static final Class[] FIRMSESSIONCLASSORIGINCORRBRANCHKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, char.class, String.class, String.class};
    private static final Class[] SESSIONFIRMCLASSORIGIN_CONSTRUCTOR = {String.class, String.class, String.class,String.class, int.class, char.class};
    private static final Class[] FIRMSESSIONCLASSCORRBRANCHKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, String.class, String.class};
    private static final Class[] FIRMSESSIONCORRPAR_CONSTRUCTOR = {String.class, String.class, String.class, String.class, String.class, char.class};
    private static final Class[] FIRMSESSIONCLASSCORRKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, String.class, int.class};
    private static final Class[] FIRMSESSIONPOSTSTATIONCORRBRANCHKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, int.class, String.class, String.class};
    private static final Class[] SESSIONFIRMPOSTSTATIONORIGIN_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, int.class, char.class};
    private static final Class[] SESSIONCLASSORIGINKEY_CONSTRUCTOR = {String.class, String.class, int.class, char.class};
    private static final Class[] SESSIONCLASSPRODUCTKEY_CONSTRUCTOR = {String.class, String.class, int.class, int.class};
    private static final Class[] SESSIONWORKSTATION_CONSTRUCTOR = {String.class, String.class, String.class};
    private static final Class[] FIRMSESSIONCORRBRANCH_CONSTRUCTOR = {String.class, String.class, String.class, String.class, String.class, String.class};
    private static final Class[] SESSIONCLASSKEY_CONSTRUCTOR = {String.class, String.class, int.class};
    private static final Class[] FIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, int.class};
    private static final Class[] EXTERNAEXCHANGEFIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, String.class, int.class, int.class};
    private static final Class[] SESSIONPOSTSTATIONKEY_CONSTRUCTOR = {String.class, String.class, int.class, int.class};
    private static final Class[] SESSIONORIGINKEY_CONSTRUCTOR = {String.class, String.class, char.class};
    private static final Class[] SESSIONFIRMCLASSORIGINLEVELKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, char.class, int.class};
    private static final Class[] SESSIONFIRMPOSTSTATIONORIGINLEVELKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, int.class, char.class, int.class};
    private static final Class[] FIRMCORRMARKETMAKER_CONSTRUCTOR =  {String.class, String.class, String.class, String.class,String.class};
    private static final Class[] SESSIONFIRMCORRCLASSKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, String.class, int.class};
    private static final Class[] SESSIONFIRMCORRPOSTSTATIONKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, String.class, int.class, int.class};
    
    private static final Class[] SESSIONROUTERKEY_CONSTRUCTOR = {String.class, String.class, String.class};
    private static final Class[] SESSIONFIRMCLASSACRONYM_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, String.class};
    private static final Class[] SESSIONFIRMPOSTSTATIONACRONYM_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class, int.class, String.class};
    private static final Class[] SESSIONAFFILIATEDFIRMKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class};
    private static final Class[] SESSIONAFFILIATEDFIRMCLASSKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class};
    private static final Class[] SESSIONAFFILIATEDFIRMEXECUTINGFIRMCORRBRANCH_CONSTRUCTOR = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class};

    private static final Class[] SESSIONDEFAULTFIRMCLASSKEY_CONSTRUCTOR = {String.class, String.class, String.class, String.class, int.class};
    
    public static final BasePropertyKeyType SESSION_FIRM_KEY =
        new BasePropertyKeyTypeImpl("SessionFirmKey", SessionFirmKey.class, SESSIONFIRMKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType FIRM_VOLUME_LIMIT_KEY =
            new BasePropertyKeyTypeImpl("FirmVolumeLimitKey", SessionFirmClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType FIRM_VOLUME_LIMIT_SIMPLE_KEY = new BasePropertyKeyTypeImpl(
            "FirmVolumeLimitSimpleKey", SessionFirmSimpleClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType FIRM_VOLUME_LIMIT_COMPLEX_KEY = new BasePropertyKeyTypeImpl(
            "FirmVolumeLimitComplexKey", SessionFirmComplexClassKey.class,FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType FIRM_VOLUME_LIMIT_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("FirmVolumeLimitPostStationKey", SessionFirmPostStationKey.class, FIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType ELIGIBLE_PDPM_KEY =
            new BasePropertyKeyTypeImpl("EligiblePDPM", SessionOriginKey.class, SESSIONORIGINKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType PDPM_ASSIGNMENT_KEY =
            new BasePropertyKeyTypeImpl("PDPMAssignmentKey", SessionFirmClassCorrBranchKey.class, FIRMSESSIONCLASSCORRBRANCHKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType PDPM_ASSIGNMENT_POST_STATION_KEY =
            new BasePropertyKeyTypeImpl("PDPMAssignmentPostStaitonKey", SessionFirmPostStationCorrBranchKey.class, FIRMSESSIONPOSTSTATIONCORRBRANCHKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType COA_ELIGIBILITY_KEY =
        new BasePropertyKeyTypeImpl("COAEligibleKey", SessionFirmClassOriginKey.class, SESSIONFIRMCLASSORIGIN_CONSTRUCTOR);

    public static final BasePropertyKeyType COA_ELIGIBILITY_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("COAEligiblePostStationKey", SessionFirmPostStationOriginKey.class, SESSIONFIRMPOSTSTATIONORIGIN_CONSTRUCTOR);
    
    public static final BasePropertyKeyType REASONABILITY_EDIT_KEY =
        new BasePropertyKeyTypeImpl("ReasonabilityEditKey", SessionFirmClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType REASONABILITY_EDIT_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("ReasonabilityEditPostStationKey", SessionFirmPostStationKey.class, FIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType RESTRICTED_SERIES_KEY =
            new BasePropertyKeyTypeImpl("RestrictedSeriesKey", SessionOriginKey.class, SESSIONORIGINKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType BOOTH_DEFAULT_DESTINATION_KEY =
            new BasePropertyKeyTypeImpl("BoothDefaultDestinationKey", SessionFirmClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType BOOTH_DEFAULT_DESTINATION_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("BoothDefaultDestinationPostStationKey", SessionFirmPostStationKey.class, FIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType CROWD_DEFAULT_DESTINATION_KEY =
        new BasePropertyKeyTypeImpl("CrowdDefaultDestinationKey", SessionFirmClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType CROWD_DEFAULT_DESTINATION_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("CrowdDefaultDestinationPostStationKey", SessionFirmPostStationKey.class, FIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType HELP_DESK_DEFAULT_DESTINATION_KEY =
        new BasePropertyKeyTypeImpl("HelpDeskDefaultDestinationKey", SessionFirmClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType HELP_DESK_DEFAULT_DESTINATION_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("HelpDeskDefaultDestinationPostStationKey", SessionFirmPostStationKey.class, FIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType PAR_DEFAULT_DESTINATION_KEY =
            new BasePropertyKeyTypeImpl("ParDefaultDestinationKey", SessionFirmClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType SIMPLE_PAR_DEFAULT_DESTINATION_KEY =
            new BasePropertyKeyTypeImpl("SimpleParDefaultDestinationKey", SessionFirmSimpleClassKey.class,
                                        FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType COMPLEX_PAR_DEFAULT_DESTINATION_KEY =
            new BasePropertyKeyTypeImpl("ComplexParDefaultDestinationKey", SessionFirmComplexClassKey.class,
                                        FIRMSESSIONCLASSKEY_CONSTRUCTOR);


    public static final BasePropertyKeyType PAR_DEFAULT_DESTINATION_POST_STATION_KEY=
        new BasePropertyKeyTypeImpl("ParDefaultDestinationPostStationKey", SessionFirmPostStationKey.class, FIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType BOOTH_DIRECT_ROUTING_KEY =
            new BasePropertyKeyTypeImpl("BoothDirectRoutingKey", SessionFirmBoothKey.class, FIRMSESSIONBOOTH_CONSTRUCTOR);

    public static final BasePropertyKeyType PAR_DIRECT_ROUTING_KEY =
            new BasePropertyKeyTypeImpl("ParDirectRoutingKey", SessionFirmCorrParKey.class, FIRMSESSIONCORRPAR_CONSTRUCTOR);

    public static final BasePropertyKeyType ALTERNATE_DESTINATIONS_KEY =
            new BasePropertyKeyTypeImpl("AlternateDestinationsKey", SessionWorkstationNameKey.class, SESSIONWORKSTATION_CONSTRUCTOR);
    
    public static final BasePropertyKeyType PRINT_DESTINATIONS_KEY =
        new BasePropertyKeyTypeImpl("PrintDestinationsKey", SessionFirmClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType FIRM_TRADING_PARAM_KEY =
        new BasePropertyKeyTypeImpl("FirmTradingParameterKey", SessionFirmCorrBranchKey.class, FIRMSESSIONCORRBRANCH_CONSTRUCTOR);

    public static final BasePropertyKeyType PAR_DIRECT_SESSION_CLASS_KEY =
        new BasePropertyKeyTypeImpl("SessionClassParRoutingKey", SessionClassKey.class, SESSIONCLASSKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType PAR_DIRECT_SESSION_CLASS_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("SessionClassParRoutingPostStationKey", SessionPostStationKey.class, SESSIONPOSTSTATIONKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType CROWD_OMT_KEY =
            new BasePropertyKeyTypeImpl("CrowdOMTKey", SessionClassKey.class, SESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType FIRM_CORR_BRANCH_OMT_KEY =
        new BasePropertyKeyTypeImpl("FirmCorrBranchOMTKey", SessionFirmCorrBranchKey.class, FIRMSESSIONCORRBRANCH_CONSTRUCTOR);
    
    public static final BasePropertyKeyType FIRM_MARKET_MAKER_KEY =
        new BasePropertyKeyTypeImpl("FirmMarketMakerKey", SessionFirmKeyMarketMaker.class, FIRMCORRMARKETMAKER_CONSTRUCTOR);
    
    public static final BasePropertyKeyType SESSION_FIRM_CLASS_ORIGIN_LEVEL_KEY =
        new BasePropertyKeyTypeImpl("SessionFirmClassOriginLevelKey", SessionFirmClassOriginLevelKey.class, SESSIONFIRMCLASSORIGINLEVELKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType SESSION_FIRM_SIMPLE_CLASS_ORIGIN_LEVEL_KEY =
            new BasePropertyKeyTypeImpl("SessionFirmSimpleClassOriginLevelKey", SessionFirmSimpleClassOriginLevelKey.class,SESSIONFIRMCLASSORIGINLEVELKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType SESSION_FIRM_COMPLEX_CLASS_ORIGIN_LEVEL_KEY =
            new BasePropertyKeyTypeImpl("SessionFirmComplexClassOriginLevelKey",
                                        SessionFirmComplexClassOriginLevelKey.class,
                                        SESSIONFIRMCLASSORIGINLEVELKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_KEY =
        new BasePropertyKeyTypeImpl("SessionFirmPostStationOriginLevelKey", SessionFirmPostStationOriginLevelKey.class, SESSIONFIRMPOSTSTATIONORIGINLEVELKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType ALL_ELECTRONIC_TRADING_CLASS_KEY =
        new BasePropertyKeyTypeImpl("ClassEnabledAllElectronic", SessionClassKey.class, SESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType BOOTH_WIRE_ORDER_PREFERENCE_KEY =
            new BasePropertyKeyTypeImpl("BoothWireOrderPreferenceKey", SessionFirmCorrBranchKey.class, FIRMSESSIONCORRBRANCH_CONSTRUCTOR);
    
    public static final BasePropertyKeyType FIRM_CORR_CLASS_PAR_KEY =
        new BasePropertyKeyTypeImpl("FirmCorrClassPARKey", SessionFirmCorrClassKey.class, SESSIONFIRMCORRCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType FIRM_CORR_CLASS_POST_STATIOM_KEY =
        new BasePropertyKeyTypeImpl("FirmCorrClassPostStationKey", SessionFirmCorrPostStationKey.class, SESSIONFIRMCORRPOSTSTATIONKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType DIRECTED_AIM_SESSION_AFFILIATED_FIRM_CLASS_KEY =
        new BasePropertyKeyTypeImpl("DirectedAIMSessionAffiliatedFirmClassKey", SessionAffiliatedFirmClassKey.class, SESSIONAFFILIATEDFIRMCLASSKEY_CONSTRUCTOR);
   
    public static final BasePropertyKeyType DIRECTED_AIM_SESSION_AFFILIATED_FIRM_KEY =
        new BasePropertyKeyTypeImpl("DirectedAIMSessionAffiliatedFirmClassKey", SessionAffiliatedFirmKey.class, SESSIONAFFILIATEDFIRMKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType DIRECTED_AIM_AFFILIATED_FIRM_PARTNERSHIP_KEY =
        new BasePropertyKeyTypeImpl("DirectedAIMAffiliatedFirmPartnershipKey", SessionAffiliatedFirmExecutingFirmCorrBranchKey.class, SESSIONAFFILIATEDFIRMEXECUTINGFIRMCORRBRANCH_CONSTRUCTOR);
    
    public static final BasePropertyKeyType ALLOW_INCOMING_ISO_KEY =
        new BasePropertyKeyTypeImpl("AllowIncomingISOKey", SessionClassKey.class, SESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType DISABLE_LINKAGE_ON_PAR_KEY =
        new BasePropertyKeyTypeImpl("DisableLinkageOnParKey", SessionClassKey.class, SESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType DEFAULT_LINKAGE_ROUTER_KEY =
        new BasePropertyKeyTypeImpl("DefaultLinkageRouterKey", SessionRouterVendorKey.class, SESSIONROUTERKEY_CONSTRUCTOR);
    
    public static final BasePropertyKeyType LINKAGE_ROUTER_ASSIGNMENT_KEY =
        new BasePropertyKeyTypeImpl("LinkageRouterAssignmentKey", ExternalExchangeFirmClassKey.class, EXTERNALEXCHANGEFIRMSESSIONCLASSKEY_CONSTRUCTOR);    
    
    public static final BasePropertyKeyType LINKAGE_ROUTER_ASSIGNMENT_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("LinkageRouterAssignmentPostStationKey", ExternalExchangeFirmPostStationKey.class, EXTERNAEXCHANGEFIRMSESSIONPOSTSTATIONKEY_CONSTRUCTOR);    
   
    public static final BasePropertyKeyType SESSION_FIRM_CLASS_ACRONYM_KEY =
        new BasePropertyKeyTypeImpl("SessionFirmClassAcronymKey", SessionFirmClassAcronymKey.class, SESSIONFIRMCLASSACRONYM_CONSTRUCTOR);
    
    public static final BasePropertyKeyType SESSION_FIRM_POST_STATION_ACRONYM_KEY =
        new BasePropertyKeyTypeImpl("SessionFirmPostStationAcronymKey", SessionFirmPostStationAcronymKey.class, SESSIONFIRMPOSTSTATIONACRONYM_CONSTRUCTOR);
   
    public static final BasePropertyKeyType ENABLE_BOOKING_FORCPS_KEY =
        new BasePropertyKeyTypeImpl("EnableBookingForCPS", SessionClassKey.class, SESSIONCLASSKEY_CONSTRUCTOR);
   
    public static final BasePropertyKeyType REASONABILITY_EDIT_BYPASS_CLASS_KEY =
        new BasePropertyKeyTypeImpl("SessionClassKey", SessionClassKey.class, SESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType REASONABILITY_EDIT_BYPASS_POST_STATION_KEY =
        new BasePropertyKeyTypeImpl("SessionReasonabilityEditBypassPostStationKey", SessionReasonabilityEditBypassPostStationKey.class, SESSIONPOSTSTATIONKEY_CONSTRUCTOR);
    public static final BasePropertyKeyType PDPM_COMPLEX_ELIGIBILITY_KEY =
        new BasePropertyKeyTypeImpl("PDPMComplexEligibilityKey", SessionFirmClassKey.class, FIRMSESSIONCLASSKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING_KEY =
        new BasePropertyKeyTypeImpl("NewBobOriginCodeContingencyTypeMappingKey", SessionClassOriginKey.class, SESSIONCLASSORIGINKEY_CONSTRUCTOR);

    public static final BasePropertyKeyType SESSION_DEFAULT_FIRM_CLASS_KEY =
        new BasePropertyKeyTypeImpl("SessionFirmClassKey", SessionFirmClassKey.class, SESSIONDEFAULTFIRMCLASSKEY_CONSTRUCTOR);     

    public static final BasePropertyKeyType[] allFirmRoutngKeyTypes =
            {
                    FIRM_VOLUME_LIMIT_KEY,
                    FIRM_VOLUME_LIMIT_POST_STATION_KEY,
                    ELIGIBLE_PDPM_KEY,
                    PDPM_ASSIGNMENT_KEY,
                    PDPM_ASSIGNMENT_POST_STATION_KEY,
                    RESTRICTED_SERIES_KEY,
                    BOOTH_DIRECT_ROUTING_KEY,
                    PAR_DIRECT_ROUTING_KEY,
                    BOOTH_DEFAULT_DESTINATION_KEY,
                    BOOTH_DEFAULT_DESTINATION_POST_STATION_KEY,
                    CROWD_DEFAULT_DESTINATION_KEY,
                    CROWD_DEFAULT_DESTINATION_POST_STATION_KEY,
                    HELP_DESK_DEFAULT_DESTINATION_KEY,
                    HELP_DESK_DEFAULT_DESTINATION_POST_STATION_KEY,
                    PAR_DEFAULT_DESTINATION_KEY,
                    PAR_DEFAULT_DESTINATION_POST_STATION_KEY,
                    ALTERNATE_DESTINATIONS_KEY,                                   
                    FIRM_TRADING_PARAM_KEY,
                    PAR_DIRECT_SESSION_CLASS_KEY,
                    PAR_DIRECT_SESSION_CLASS_POST_STATION_KEY,
                    CROWD_OMT_KEY,
                    FIRM_CORR_BRANCH_OMT_KEY,
                    SESSION_FIRM_KEY,
                    FIRM_MARKET_MAKER_KEY,
                    COA_ELIGIBILITY_KEY,
                    COA_ELIGIBILITY_POST_STATION_KEY,                    
                    SESSION_FIRM_CLASS_ORIGIN_LEVEL_KEY,
                    SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_KEY,
                    REASONABILITY_EDIT_KEY,
                    REASONABILITY_EDIT_POST_STATION_KEY,
                    ALL_ELECTRONIC_TRADING_CLASS_KEY,                    
                    BOOTH_WIRE_ORDER_PREFERENCE_KEY,
                    FIRM_CORR_CLASS_PAR_KEY,
                    FIRM_CORR_CLASS_POST_STATIOM_KEY,
                    DIRECTED_AIM_SESSION_AFFILIATED_FIRM_CLASS_KEY,
                    DIRECTED_AIM_SESSION_AFFILIATED_FIRM_KEY,
                    DIRECTED_AIM_AFFILIATED_FIRM_PARTNERSHIP_KEY,
                    ALLOW_INCOMING_ISO_KEY,
                    DISABLE_LINKAGE_ON_PAR_KEY,
                    DEFAULT_LINKAGE_ROUTER_KEY,
                    LINKAGE_ROUTER_ASSIGNMENT_KEY,
                    ENABLE_BOOKING_FORCPS_KEY,
                    LINKAGE_ROUTER_ASSIGNMENT_POST_STATION_KEY,
                    REASONABILITY_EDIT_BYPASS_CLASS_KEY,
                    REASONABILITY_EDIT_BYPASS_POST_STATION_KEY,
                    PDPM_COMPLEX_ELIGIBILITY_KEY,
                    NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING_KEY,
					SESSION_FIRM_CLASS_ACRONYM_KEY,
                    SESSION_FIRM_POST_STATION_ACRONYM_KEY, 
                    SESSION_DEFAULT_FIRM_CLASS_KEY
            };

    private String name;
    private Class classType;

    private Constructor defaultKeyConstructor;
    private Constructor propertyKeyContructor;
    private Constructor propertyTypeContructor;

    protected BasePropertyKeyTypeImpl(String name, Class classType, Class[] defaultContructor)
    {
        this.name = name;
        this.classType = classType;

        //doing it here caches it for later instantiation so that reflection does not impact creation of keys.
        try
        {
            defaultKeyConstructor = classType.getConstructor(defaultContructor);
            propertyKeyContructor = classType.getConstructor(PROPERTY_KEY_CONSTRUCTOR);
            propertyTypeContructor = classType.getConstructor(PROPERTY_TYPE_CONSTRUCTOR);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Missing BasePropertyKey Constructor: " + e.getMessage());
        }
    }

    public String getName()
    {
        return name;
    }

    public Class getClassType()
    {
        return classType;
    }

    public boolean equals(Object o)
    {
        if (this == o) { return true; }

        if (o instanceof com.cboe.interfaces.domain.routingProperty.BasePropertyKeyType)
        {
            final BasePropertyKeyType keyType = (BasePropertyKeyType) o;

            if ((getName().equals(keyType.getName())) && (getClassType() == keyType.getClassType()))
            {
                return true;
            }
        }

        return false;
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public String toString()
    {
        return name;
    }

    public Constructor getDefaultKeyConstructor()
    {
        return defaultKeyConstructor;
    }

    public Constructor getPropertyKeyContructor()
    {
        return propertyKeyContructor;
    }

    public Constructor getPropertyTypeContructor()
    {
        return propertyTypeContructor;
    }
}
