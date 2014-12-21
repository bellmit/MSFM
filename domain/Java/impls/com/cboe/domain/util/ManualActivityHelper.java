package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.ActivityTypes;
import com.cboe.idl.constants.OHSRoutingReasons;

public class ManualActivityHelper
{
    public static String getManualRouteDescription(short p_activityType)
    {
        String routeDesc = "";
        switch (p_activityType)
        {
            case ActivityTypes.MANUAL_ORDER_TA_TIMEOUT:
                routeDesc = "Manual Order TA Timeout";
                break;
            case ActivityTypes.MANUAL_ORDER_TB_TIMEOUT:
                routeDesc = "Manual Order TB Timeout";
                break;
            case ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT:
                routeDesc = "Manual Order BOOK Timeout";
                break;
            case ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT:
                routeDesc = "Manual Order AUCTION Timeout"; 
                break;
            case ActivityTypes.MANUAL_FILL_TIMEOUT:
                routeDesc = "Manual FILL Timeout"; 
                break;
            case ActivityTypes.MANUAL_FILL_LINKAGE_TIMEOUT:
                routeDesc = "Manual Fill Linkage Timeout"; 
                break;
            case ActivityTypes.MANUAL_ORDER_TA_TIMEOUT_FAILURE:
                routeDesc = "MANUAL Order TA Timeout Failure"; 
                break;
            case ActivityTypes.MANUAL_ORDER_TB_TIMEOUT_FAILURE:
                routeDesc = "Manual Order TB Timeout Failure"; 
                break;
            case ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT_FAILURE:
                routeDesc = "Manual Order BOOK Timeout Failure"; 
                break;
            case ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE:
                routeDesc = "Manual Order AUCTION Timeout Failure"; 
                break;
            case ActivityTypes.MANUAL_FILL_TIMEOUT_FAILURE:
                routeDesc = "Manual Fill Timeout FAILURE"; 
                break;
            case ActivityTypes.MANUAL_FILL_REJECT:
                routeDesc = "Manual Fill REJECT"; 
                break;
            case ActivityTypes.MANUAL_FILL_REJECT_FAILURE:
                routeDesc = "Manual Fill REJECT Failure"; 
                break;
            case ActivityTypes.FORCED_LOGOFF_PAR:
                routeDesc = "Forced Log-off PAR";
                break;
            case ActivityTypes.FORCED_LOGOFF_PAR_FAILURE:
                routeDesc = "Forced Log-off PAR failure";
                break;
            case ActivityTypes.MANUAL_ORDER_REROUTE_CROWD_REQUEST:
                routeDesc = "Manual Order Reroute Crowd Request";
                break;
            case ActivityTypes.MANUAL_ORDER_REROUTE_REQUEST:
                routeDesc = "Manual Order Reroute Request";
                break;
            case ActivityTypes.MANUAL_ORDER_SR_TIMEOUT:
                routeDesc = "Manual Order SR Timeout";
                break;
            case ActivityTypes.MANUAL_ORDER_SR_TIMEOUT_FAILURE:
                routeDesc = "MANUAL Order SR Timeout Failure"; 
                break;
            case ActivityTypes.MANUAL_ORDER_FR_TIMEOUT:
                routeDesc = "Manual Order FR Timeout";
                break;
            case ActivityTypes.MANUAL_ORDER_FR_TIMEOUT_FAILURE:
                routeDesc = "MANUAL Order FR Timeout Failure"; 
                break;
            default:
                break;
        }
        return routeDesc;
    }

    public static short getManualRouteReason(short p_activityType)
    {
        short routeReason = 0;
        switch (p_activityType)
        {
            case ActivityTypes.MANUAL_ORDER_TA_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_TB_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_SR_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_FR_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_TA_TIMEOUT_FAILURE:
            case ActivityTypes.MANUAL_ORDER_TB_TIMEOUT_FAILURE:
            case ActivityTypes.MANUAL_ORDER_SR_TIMEOUT_FAILURE:
            case ActivityTypes.MANUAL_ORDER_FR_TIMEOUT_FAILURE:
            case ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT_FAILURE:
            case ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE:
                routeReason = OHSRoutingReasons.MANUAL_ORDER_TIMEOUT;
                break;            
            case ActivityTypes.MANUAL_FILL_TIMEOUT:
            case ActivityTypes.MANUAL_FILL_TIMEOUT_FAILURE:
            case ActivityTypes.MANUAL_FILL_LINKAGE_TIMEOUT:                
            case ActivityTypes.MANUAL_FILL_LINKAGE_TIMEOUT_FAILURE:
                routeReason = OHSRoutingReasons.MANUAL_FILL_TIMEOUT;
                break;
            case ActivityTypes.MANUAL_ORDER_REROUTE_CROWD_REQUEST:
            case ActivityTypes.MANUAL_ORDER_REROUTE_REQUEST:
                routeReason = OHSRoutingReasons.DIRECT_ROUTE;
            default:
                break;
        }
        return routeReason;
    }
    
    public static short getFillRejectRouteReason(short p_activityType, boolean isStrategy)
    {
        short routeReason = 0;
        switch (p_activityType)
        {
            case ActivityTypes.MANUAL_FILL_REJECT:
            case ActivityTypes.MANUAL_FILL_REJECT_FAILURE:
                routeReason = isStrategy ? OHSRoutingReasons.COMPLEX_FILL_REJECT : OHSRoutingReasons.SIMPLE_FILL_REJECT;
                break;
            default:
            break;
        }
        return routeReason;
    }
    
    public static short getManualLegActivityType(short p_activityType)
    {
        short type = 0;
        switch(p_activityType) 
        {
            case ActivityTypes.MANUAL_ORDER_TA_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_TA_TIMEOUT_FAILURE:
                type = ActivityTypes.MANUAL_TA_TIMEOUT_STRATEGY_LEG;
                break;
            case ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE:
                type = ActivityTypes.MANUAL_AUCTION_TIMEOUT_STRATEGY_LEG;
                break;
            case ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT:
            case ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT_FAILURE:
                type = ActivityTypes.MANUAL_BOOK_TIMEOUT_STRATEGY_LEG;
                break;
            case ActivityTypes.MANUAL_FILL_TIMEOUT:
            case ActivityTypes.MANUAL_FILL_TIMEOUT_FAILURE:
                type = ActivityTypes.MANUAL_FILL_TIMEOUT_STRATEGY_LEG;
                break;
            case ActivityTypes.MANUAL_FILL_REJECT:
            case ActivityTypes.MANUAL_FILL_REJECT_FAILURE:
                type = ActivityTypes.MANUAL_FILL_REJECT_STRATEGY_LEG;
                break;
            default:
                break;
        }
        return type;
    }
    
    public static String getFillRejectMessage(Throwable ex, boolean isDuplicateReport)
    {
        if(ex != null || isDuplicateReport) {
            return "Manual Fill Reject - System Error";
        }
        return "Manual Fill Reject - Invalid Volume";
    }

    public static short deduceFailedActivityType(short p_activityType)
    {
        short failedType = 0; // NONE
        switch (p_activityType)
        {
            case ActivityTypes.MANUAL_ORDER_TA_TIMEOUT:
                failedType = ActivityTypes.MANUAL_ORDER_TA_TIMEOUT_FAILURE;
                break;
            case ActivityTypes.MANUAL_ORDER_TB_TIMEOUT:
                failedType = ActivityTypes.MANUAL_ORDER_TB_TIMEOUT_FAILURE;
                break;
            case ActivityTypes.MANUAL_ORDER_SR_TIMEOUT:
                failedType = ActivityTypes.MANUAL_ORDER_SR_TIMEOUT_FAILURE;
                break;
            case ActivityTypes.MANUAL_ORDER_FR_TIMEOUT:
                failedType = ActivityTypes.MANUAL_ORDER_FR_TIMEOUT_FAILURE;
                break;
            case ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT:
                failedType = ActivityTypes.MANUAL_ORDER_BOOK_TIMEOUT_FAILURE;
                break;
            case ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT:
                failedType = ActivityTypes.MANUAL_ORDER_AUCTION_TIMEOUT_FAILURE;
                break;
            case ActivityTypes.MANUAL_FILL_TIMEOUT:
                failedType = ActivityTypes.MANUAL_FILL_TIMEOUT_FAILURE;
                break;
            case ActivityTypes.MANUAL_FILL_LINKAGE_TIMEOUT:
                failedType = ActivityTypes.MANUAL_FILL_LINKAGE_TIMEOUT_FAILURE;
                break;
            case ActivityTypes.MANUAL_FILL_REJECT:
                failedType = ActivityTypes.MANUAL_FILL_REJECT_FAILURE;
                break;
            case ActivityTypes.FORCED_LOGOFF_PAR:
                failedType = ActivityTypes.FORCED_LOGOFF_PAR_FAILURE;
                break;
            default:
                break;
        }
        return failedType;
    }
    
    public static String getActivityString(short p_activityType)
    {
        String activityString = "";
        switch (p_activityType)
        {
            case ActivityTypes.MANUAL_ORDER_TA:
                activityString = "ManualOrder TA";
                break;
            case ActivityTypes.AUTO_ORDER_TA:
                activityString = "AutoOrder TA";
                break;
            case ActivityTypes.MANUAL_ORDER_TB:
                activityString = "ManualOrder TB";
                break;
            case ActivityTypes.MANUAL_ORDER_BOOK:
                activityString = "ManualOrder BOOK";
                break;
            case ActivityTypes.AUTO_ORDER_BOOK:
                activityString = "AutoOrder BOOK";
                break;
            case ActivityTypes.MANUAL_ORDER_AUCTION:
                activityString = "ManualOrder Auction";
                break;
            case ActivityTypes.AUTO_ORDER_AUCTION:
                activityString = "AutoOrder Auction";
                break;
            case ActivityTypes.MANUAL_ORDER_SR:
                activityString = "ManualOrder SR";
                break;
            case ActivityTypes.MANUAL_ORDER_FR:
                activityString = "ManualOrder FR";
                break;
            default:
                break;
        }
        return activityString;
    }
    
    public static short convertActivityType(short p_activityType)
    {
        switch (p_activityType)
        {
            case ActivityTypes.AUTO_ORDER_TA:
                return ActivityTypes.MANUAL_ORDER_TA;
            case ActivityTypes.AUTO_ORDER_BOOK:
                return ActivityTypes.MANUAL_ORDER_BOOK;
            case ActivityTypes.AUTO_ORDER_AUCTION:
                return ActivityTypes.MANUAL_ORDER_AUCTION;
                
            default:
                break;
        }
        return p_activityType;
    }  
}
