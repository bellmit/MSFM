package com.cboe.domain.util.fixUtil;


/**
 * Extends CmiErrorCodes to include additional FIX ErrorCodes
 * <br><br>
 * Copyright © 2000 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software program is made available to
 * CBOE members and member firms to enable them to develop software applications using
 * the CBOE Market Interface (CMi), and its use is subject to the terms and conditions
 * of a Software License Agreement that governs its use. This document is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 * @author Jim Northey
 */

public interface FixUtilCmiErrorCodes
{

    public static final int FIX_PREFIX = 130000;

    public interface FixAlreadyExistCodes
    {

        public static final int ALREADY_EXISTS_BASE = 7000;
        public static final int CONSUMER_ALREADY_EXISTS = FIX_PREFIX + ALREADY_EXISTS_BASE + 1;
    }

    public interface FixAuthenticationCodes extends com.cboe.idl.cmiErrorCodes.AuthenticationCodesOperations
    {

    }
    public interface FixCommunicationFailureCodes
            extends com.cboe.idl.cmiErrorCodes.CommunicationFailureCodesOperations
    {

    }

    public interface FixDataValidationCodes extends com.cboe.idl.cmiErrorCodes.DataValidationCodesOperations
    {
         public static final int DATA_VALIDATION_BASE = 1000;
         public static final int INVALID_PUTCALL
                 = FIX_PREFIX + FixUtilConstants.PutOrCall.TAGNUMBER;
         public static final int INVALID_EXPIRATIONDATE
                 = FIX_PREFIX + FixUtilConstants.MaturityMonthYear.TAGNUMBER;
         public static final int INVALID_MARKETDATA_EXCHANGECODE
                 = FIX_PREFIX + FixUtilConstants.MDMkt.TAGNUMBER;
         public static final int INVALID_EXDESTINATION_EXCHANGE_CODE
                 = FIX_PREFIX + FixUtilConstants.ExDestination.TAGNUMBER;
         public static final int INVALID_LOGIN_SESSION_MODE
                 = FIX_PREFIX + FixUtilConstants.LastPx.TAGNUMBER;
         public static final int INVALID_PRODUCT_STATE
                 = FIX_PREFIX + FixUtilConstants.SecurityType.TAGNUMBER;
         public static final int INVALID_TRADING_SESSION_STATE
                 = FIX_PREFIX + FixUtilConstants.TradingSessionID.TAGNUMBER;
         public static final int INVALID_QUOTE_ID
                 = FIX_PREFIX + FixUtilConstants.QuoteID.TAGNUMBER;
         public static final int INVALID_SUBSCRIPTION_REQUEST_TYPE
                 = FIX_PREFIX + FixUtilConstants.SubscriptionRequestType.TAGNUMBER;
         public static final int INVALID_SECURITY_ID
                 = FIX_PREFIX + FixUtilConstants.SecurityID.TAGNUMBER;
         public static final int INVALID_TAG_SPECIFIED
                 = FIX_PREFIX + DATA_VALIDATION_BASE + 1;
         public static final int INVALID_PRICE_PROTECTION_SCOPE
                 = FIX_PREFIX + FixUtilConstants.PriceProtectionScope.TAGNUMBER;

    }

    public interface FixNotAcceptedCodes extends com.cboe.idl.cmiErrorCodes.NotAcceptedCodesOperations
    {

    }

    public interface FixNotFoundCodes extends com.cboe.idl.cmiErrorCodes.NotFoundCodesOperations
    {

        /**
         * We need to make sure the error number is unique - so we add the FIX_PREFIX and then
         * use the first entry in the base cmiErrorCodes.NotFoundCodesOperations as a base - then
         * we add values to that - avoids conflicts within our own derived class and allows for
         * some consistency with the base class
         */
        public static final int CONSUMER_NOT_FOUND = FIX_PREFIX + RESOURCE_DOESNT_EXIST + 1;

    }

    public interface FixTransactionFailedCodes extends com.cboe.idl.cmiErrorCodes.TransactionFailedCodesOperations
    {

    }

    public interface FixSystemCodes extends com.cboe.idl.cmiErrorCodes.SystemCodesOperations
    {

    }

}
