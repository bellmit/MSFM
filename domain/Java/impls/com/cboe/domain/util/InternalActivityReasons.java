package com.cboe.domain.util;
import com.cboe.idl.cmiConstants.ActivityReasonsOperations;
/**
 * Define constants for internal operations.
 */
public class InternalActivityReasons implements ActivityReasonsOperations {
    public static final short INTERNAL_USER_LOGON  = (short) -1;
    public static final short INTERNAL_USER_LOGOUT = (short) -2;
    public static final short INTERNAL_PROD_HALTED = (short) -3;
    public static final short INTERNAL_PROD_CLOSED = (short) -4;
    public static final short INTERNAL_PROD_SUSPENDED = (short) -5;
    public static final short INTERNAL_PROD_HALTED_BYCLASS = (short) -6;
    public static final short INTERNAL_PROD_CLOSED_BYCLASS = (short) -7;
    public static final short INTERNAL_PROD_SUSPENDED_BYCLASS = (short) -8;
    public static final short INTERNAL_UNSPECIFIED = (short) -100;

    public InternalActivityReasons()
    {
    }

    /**
     * Check if quote cancellation is allowed give the cancel reason
     * Logout will follow acceptCancel to a pending cancel request and will be
     * processed after trigger expires.
     */
    public static boolean allowCancelInQuoteTrigger(short aCancelReason) {
        return ((aCancelReason == InternalActivityReasons.INTERNAL_PROD_CLOSED_BYCLASS)
                || (aCancelReason == InternalActivityReasons.INTERNAL_PROD_HALTED_BYCLASS) 
                || (aCancelReason == InternalActivityReasons.INTERNAL_PROD_SUSPENDED_BYCLASS)
                || (aCancelReason == InternalActivityReasons.INTERNAL_PROD_CLOSED)
                || (aCancelReason == InternalActivityReasons.INTERNAL_PROD_HALTED) 
                || (aCancelReason == InternalActivityReasons.INTERNAL_PROD_SUSPENDED)         
                );
    }
    
    /**
     * Check if QuoteDelete report should be published for the cancel reason. Some cancel reasons 
     * indicate that we should not publish the quotedelete report.
     * @param aCancelReason
     * 
     */
    public static boolean shouldPublishQuoteDelete(short aCancelReason) {
        return !((aCancelReason == InternalActivityReasons.INTERNAL_PROD_CLOSED_BYCLASS)
                || (aCancelReason == InternalActivityReasons.INTERNAL_PROD_HALTED_BYCLASS) 
                || (aCancelReason == InternalActivityReasons.NO_USER_ACTIVITY) 
                || (aCancelReason == InternalActivityReasons.INTERNAL_PROD_SUSPENDED_BYCLASS) );
    }

    public static short getMappedExternalReason(short aInternalCancelReason)
    {
        switch (aInternalCancelReason)
        {
            case InternalActivityReasons.INTERNAL_USER_LOGON:             return USER;
            case InternalActivityReasons.INTERNAL_USER_LOGOUT:            return USER;
            case InternalActivityReasons.INTERNAL_PROD_HALTED:            return PRODUCT_HALTED;
            case InternalActivityReasons.INTERNAL_PROD_SUSPENDED:         return PRODUCT_SUSPENDED;
            case InternalActivityReasons.INTERNAL_PROD_CLOSED:            return SYSTEM;
            case InternalActivityReasons.INTERNAL_PROD_HALTED_BYCLASS:    return PRODUCT_HALTED;
            case InternalActivityReasons.INTERNAL_PROD_SUSPENDED_BYCLASS: return PRODUCT_SUSPENDED;
            case InternalActivityReasons.INTERNAL_PROD_CLOSED_BYCLASS:    return SYSTEM;
            case InternalActivityReasons.INTERNAL_UNSPECIFIED:            return OTHER;
        }
            return aInternalCancelReason;
    }
}