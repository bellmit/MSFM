package com.cboe.domain.routingProperty;

public class RoutingPropertyMaskHelper
{
    public static final int[][] FIRM_VOLUME_LIMIT_MASK = {{0,0,0,1},{0,1,1,0},{0,1,1,1}};
    public static final int[][] FIRM_VOLUME_LIMIT_POST_STATION_MASK = {{0,0,0,0,1},{0,0,0,1,1},{0,1,1,1,1}};
    public static final int[][] ELIGIBLE_PDPM_MASK = {{0,0}};
    public static final int[][] PDPM_ASSIGNMENT_MASK = {{0,0,0,1,0,0},{0,0,0,0,0,1},{0,0,0,1,0,1}, {0,0,0,0,1,0},{0,0,0,1,1,0},{0,0,0,0,1,1}, {0,0,0,1,1,1}};
    public static final int[][] PDPM_ASSIGNMENT_POST_STATION_MASK = {{0,0,0,0,1,0,0},{0,0,0,1,1,0,0},{0,0,0,0,0,0,1},{0,0,0,0,1,0,1},{0,0,0,1,1,0,1},{0,0,0,0,0,1,0},{0,0,0,0,1,1,0},{0,0,0,1,1,1,0}, {0,0,0,0,0,1,1}, {0,0,0,0,1,1,1}, {0,0,0,1,1,1,1}};
    public static final int[][] RESTRICTED_SERIES_MASK = {{0,1}};
    public static final int[][] ELIGIBLE_SERIES_MASK = new int[0][0];
    public static final int[][] BOOTH_DIRECT_ROUTING_MASK = {{0,0,0,0}};
    public static final int[][] PAR_DIRECT_ROUTING_MASK = {{0,1,1,0,0}};
    public static final int[][] BOOTH_DEFAULT_DESTINATION_MASK = {{0,0,0,1},{0,1,1,0},{0,1,1,1}};
    public static final int[][] BOOTH_DEFAULT_DESTINATION_POST_STATION_MASK = {{0,0,0,0,1},{0,0,0,1,1},{0,1,1,0,0},{0,1,1,1,1}};
    public static final int[][] CROWD_DEFAULT_DESTINATION_MASK = {{0,0,0,1},{0,1,1,0},{0,1,1,1}};
    public static final int[][] CROWD_DEFAULT_DESTINATION_POST_STATION_MASK = {{0,0,0,0,1},{0,0,0,1,1},{0,1,1,0,0},{0,1,1,1,1}};
    public static final int[][] HELP_DESK_DEFAULT_DESTINATION_MASK = {{0,0,0,1},{0,1,1,0},{0,1,1,1}};
    public static final int[][] HELP_DESK_DEFAULT_DESTINATION_POST_STATION_MASK = {{0,0,0,0,1},{0,0,0,1,1},{0,1,1,0,0},{0,1,1,1,1}};
    public static final int[][] PAR_DEFAULT_DESTINATION_MASK = {{0,0,0,1},{0,1,1,0},{0,1,1,1}};
    public static final int[][] PAR_DEFAULT_DESTINATION_POST_STATION_MASK = {{0,0,0,0,1},{0,0,0,1,1},{0,1,1,0,0},{0,1,1,1,1}};
    public static final int[][] ALTERNATE_DESTINATIONS_MASK = {{0,0}};
    public static final int[][] PRINT_DESTINATIONS_MASK = new int[0][0];
    public static final int[][] SESSION_CLASS_PAR_ROUTING_MASK = {{0,0}};
    public static final int[][] SESSION_CLASS_PAR_ROUTING_POST_STATION_MASK = {{0,0,0}};
    public static final int[][] CROWD_OMT_MASK = new int[0][0];
    public static final int[][] FIRM_CORR_BRANCH_OMT_MASK = {{0,0,0,0,1},{0,1,1,0,0},{0,1,1,0,1}};
    public static final int[][] COA_ELIGIBILITY_MASK = {{0,0,0,0,1},{0,0,0,1,0},{0,0,0,1,1}, {0,1,1,0,0},{0,1,1,0,1}, {0,1,1,1,0},{0,1,1,1,1}};
    public static final int[][] COA_ELIGIBILITY_POST_STATION_MASK ={{0,0,0,0,0,1},{0,0,0,0,1,0},{0,0,0,0,1,1},{0,0,0,1,1,0},{0,0,0,1,1,1},{0,1,1,0,0,0},{0,1,1,0,0,1}, {0,1,1,0,1,0},{0,1,1,0,1,1}, {0,1,1,1,1,0},{0,1,1,1,1,1}};
    public static final int[][] REASONABILITY_MASK = {{0,0,0,1},{0,1,1,0},{0,1,1,1}};
    public static final int[][] REASONABILITY_POST_STATION_MASK = {{0,0,0,0,1},{0,0,0,1,1},{0,1,1,0,0},{0,1,1,1,1}};
    public static final int[][] ELIGIBLE_COA_MASK = new int[0][0];
    public static final int[][] SESSION_FIRM_CLASS_ORIGIN_LEVEL_MASK = {{0,0,0,0,1,0},{0,0,0,1,0,0},{0,0,0,1,1,0}, {0,1,1,0,0,0},{0,1,1,0,1,0}, {0,1,1,1,0,0},{0,1,1,1,1,0}};
    public static final int[][] SESSION_FIRM_POST_STATION_ORIGIN_LEVEL_MASK= {{0,0,0,0,0,1,0},{0,0,0,0,1,0,0},{0,0,0,0,1,1,0},{0,0,0,1,1,0,0},{0,0,0,1,1,1,0},{0,1,1,0,0,0,0},{0,1,1,0,0,1,0}, {0,1,1,0,1,0,0},{0,1,1,0,1,1,0}, {0,1,1,1,1,0,0},{0,1,1,1,1,1,0}};
    public static final int[][] BOOTH_WIRE_ORDER_PREFERENCE_MASK = {{0,0,0,0,1},{0,1,1,0,0},{0,1,1,0,1}};
    public static final int[][] ALL_ELECTRONIC_TRADING_CLASS_MASK = {{0,0}};
    public static final int[][] FIRM_CORR_CLASS_PAR_MASK = {{0,0,0,0,1},{0,0,0,1,0},{0,0,0,1,1},{0,1,1,0,0},{0,1,1,0,1},{0,1,1,1,0},{0,1,1,1,1}};
    public static final int[][] FIRM_CORR_PAR_POST_STATION_MASK = {{0,0,0,0,0,1},{0,0,0,0,1,1},{0,0,0,1,0,0},{0,1,1,0,0,0},{0,1,1,0,0,1},{0,1,1,0,1,1},{0,1,1,1,0,0},{0,1,1,1,0,1},{0,0,0,1,0,1},{0,0,0,1,1,1},{0,1,1,1,1,1}};
    public static final int[][] ALLOW_INCOMING_ISO_MASK = {{0,1}};
    public static final int[][] DISABLE_LINKAGE_ON_PAR_MASK = {{0,1}};
    public static final int[][] DEFAULT_LINKAGE_ROUTER_MASK = new int[0][0];
    public static final int[][] LINKAGE_ROUTER_ASSIGNMENT_MASK = {{0,0,0,0,0},{0,0,0,0,1},{0,1,0,0,0},{0,1,0,0,1},{0,0,1,1,0},{0,0,1,1,1},{0,1,1,1,0},{0,1,1,1,1}};
    public static final int[][] LINKAGE_ROUTER_ASSIGNMENT_POST_STATION_MASK = {{0,0,0,0,0,0},{0,0,0,0,0,1},{0,0,0,0,1,1},{0,1,0,0,0,0},{0,1,0,0,0,1},{0,1,0,0,1,1},{0,0,1,1,0,0},{0,0,1,1,0,1},{0,1,1,1,0,0},{0,1,1,1,0,1},{0,0,1,1,1,1},{0,1,1,1,1,1}};
    public static final int[][] ENABLE_BOOKING_FORCPS_MASK = {{0,0},{0,1}};
    public static final int[][] REASONABILITY_EDIT_CLASS_MASK = {{0,1}};
    public static final int[][] REASONABILITY_EDIT_POST_STATION_MASK = {{0,0,1},{0,1,1}};
    public static final int[][] NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING_MASK = {{0,0,0,0,0}};
    // public static final int[][] NEWBOB_ORIGINCODE_CONTINGENCYTYPE_MAPPING_MASK = {{0,0,0,0,1},{0,0,0,1,0},{0,0,0,1,1}, {0,1,1,0,0},{0,1,1,0,1}, {0,1,1,1,0},{0,1,1,1,1}};
    
    //PDPMComplexEligibilitly - Firm, classkey can be default
    public static final int[][] PDPM_COMPLEX_ELIGIBILITY_MASK = {{0,0,0,0},{0,0,0,1},{0,1,1,0}, {0,1,1,1}};

    public static String toString(int[][] masks)
    {
        int          bits = masks.length > 0 ? masks[0].length : 0;
        StringBuffer str  = new StringBuffer(512);
        str.append("mask count=").append(masks.length).append(", bits per mask=").append(bits).append(" masks:\n");
        for(int[] mask : masks)
        {
            str.append("  {");
            int cnt = 0;
            for(int m : mask)
            {
                if(cnt > 0)
                {
                    str.append(", ");
                }
                str.append(m);
                ++cnt;
            }
            str.append("}\n");
        }
        return str.toString();
    }
}
