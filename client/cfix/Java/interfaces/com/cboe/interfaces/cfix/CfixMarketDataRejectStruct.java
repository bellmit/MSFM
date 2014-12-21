package com.cboe.interfaces.cfix;

/**
 * CfixMarketDataRejectStruct.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface CfixMarketDataRejectStruct
{
    public static final char UnknownSymbol                         = '0';  // taken from FixMDReqRejReasonField
    public static final char DuplicateMdReqId                      = '1';  // taken from FixMDReqRejReasonField
    public static final char InsufficientBandwidth                 = '2';  // taken from FixMDReqRejReasonField
    public static final char InsufficientPermissions               = '3';  // taken from FixMDReqRejReasonField
    public static final char UnsupportedMarketDepth                = '5';  // taken from FixMDReqRejReasonField
    public static final char UnsupportedAggregatedBook             = '7';  // taken from FixMDReqRejReasonField
    public static final char UseSpecifiedEngine                    = 'D';  // taken from FixMDReqRejReasonField
    public static final char AlreadySubscribed                     = 'E';  // taken from FixMDReqRejReasonField
    public static final char SubscriptionReplaced                  = 'F';  // taken from FixMDReqRejReasonField
    public static final char ForcedUnsubscribe                     = 'G';  // taken from FixMDReqRejReasonField

    public String  getMdReqID();
    public char    getRejectReason();
    public String  getText();
    public String  getTargetCompID();
}
