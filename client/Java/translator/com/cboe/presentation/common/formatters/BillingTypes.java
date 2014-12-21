//
// -----------------------------------------------------------------------------------
// Source file: BillingTypes.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

// Inlined to avoid name conflict: com.cboe.idl.cmiConstants.BillingTypeIndicator;

/********************************************************************************
 * Represents Billing Types
 *
 * @see com.cboe.idl.cmiConstants.BillingTypeIndicators
 */
public class BillingTypes
{

//*** Public Attributes

    // Billing Events (mapping to com.cboe.idl.cmiConstants.BillingTypeIndicators)
    public static final char MAKER = com.cboe.idl.cmiConstants.BillingTypeIndicators.MAKER;
    public static final char TAKER = com.cboe.idl.cmiConstants.BillingTypeIndicators.TAKER;
    public static final char FLASH_RESPONSE = com.cboe.idl.cmiConstants.BillingTypeIndicators.FLASH_RESPONSE;
    public static final char FLASH = com.cboe.idl.cmiConstants.BillingTypeIndicators.FLASH;
    public static final char CROSS = com.cboe.idl.cmiConstants.BillingTypeIndicators.CROSS;
    public static final char LINKED_AWAY = com.cboe.idl.cmiConstants.BillingTypeIndicators.LINKED_AWAY;
    public static final char LINKED_AWAY_RESPONSE = com.cboe.idl.cmiConstants.BillingTypeIndicators.LINKED_AWAY_RESPONSE;
    public static final char OPENING = com.cboe.idl.cmiConstants.BillingTypeIndicators.OPENING;
    public static final char ODD_LOT_FLASH = com.cboe.idl.cmiConstants.BillingTypeIndicators.ODD_LOT_FLASH;
    public static final char ODD_LOT_RESPONSE = com.cboe.idl.cmiConstants.BillingTypeIndicators.ODD_LOT_RESPONSE;
    public static final char RESTING = com.cboe.idl.cmiConstants.BillingTypeIndicators.RESTING;
    public static final char CROSS_PRICE_IMP = com.cboe.idl.cmiConstants.BillingTypeIndicators.CROSS_PRICE_IMP;
    public static final char FLASH_PRICE_IMP = com.cboe.idl.cmiConstants.BillingTypeIndicators.FLASH_PRICE_IMP;
    public static final char FLASH_RESPONSE_PRICE_IMP = com.cboe.idl.cmiConstants.BillingTypeIndicators.FLASH_RESPONSE_PRICE_IMP;
    public static final char MAKER_TURNER = com.cboe.idl.cmiConstants.BillingTypeIndicators.MAKER_TURNER;
    public static final char RESTING_TURNER = com.cboe.idl.cmiConstants.BillingTypeIndicators.RESTING_TURNER;
    // Format constants
    public static final String TRADERS_FORMAT = "TRADERS_FORMAT";

    public static final String FORMAT_UNDEFINED = "ERROR: Format Not Defined For Type";
    public static final String INVALID_FORMAT = "ERROR: Invalid Format Specifier";
    public static final String INVALID_BILLING_TYPE = "ERROR: Invalid Billing Type Code";

//*** Private Attributes

    private static final String MAKER_STRING = "Maker";
    private static final String TAKER_STRING = "Taker";
    private static final String FLASH_RESPONSE_STRING = "Flash Response";
    private static final String FLASH_STRING = "Flash";
    private static final String CROSS_STRING = "Cross";
    private static final String LINKED_AWAY_STRING = "Linked Away";
    private static final String LINKED_AWAY_RESPONSE_STRING = "Linked Away Response";
    private static final String OPENING_STRING = "Opening";
    private static final String ODD_LOT_FLASH_STRING = "Odd Lot Flash";
    private static final String ODD_LOT_RESPONSE_STRING = "Odd Lot Response";
    private static final String RESTING_STRING = "Resting";
    private static final String CROSS_PRICE_IMP_STRING = "Cross Price Imp";
    private static final String FLASH_PRICE_IMP_STRING = "Flash Price Imp";
    private static final String FLASH_RESPONSE_PRICE_IMP_STRING = "Flash Response Price Imp";
    private static final String MAKER_TURNER_STRING = "Maker Turner";
    private static final String RESTING_TURNER_STRING = "Resting Turner";

//*** Public Methods

        /**
         * **************************************************************************
         * Returns a string representation of the object in TRADERS_FORMAT format
         * @param billingType - the billing type indicator to render (see defined constants)
         * @return a string representation of the billing type indicator
         * @see com.cboe.idl.cmiConstants.BillingTypeIndicators
         */
        public static String toString(char billingType)
        {
            return toString(billingType, TRADERS_FORMAT);
        }


        /**
         * ************************************************************************** Returns a
         * string representation of the object in the given format
         * @param billingType - the activity event code to render (see defined constants)
         * @param formatSpecifier - a string that specifies how the object should format itself.
         * @return a string representation of the billingType
         * @see com.cboe.idl.cmiConstants.BillingTypeIndicators
         */
        public static String toString(char billingType, String formatSpecifier)
        {
            if(formatSpecifier.equals(TRADERS_FORMAT))
            {
                switch(billingType)
                {
                    case MAKER:
                        return MAKER_STRING;
                    case TAKER:
                        return TAKER_STRING;
                    case FLASH_RESPONSE:
                        return FLASH_RESPONSE_STRING;
                    case FLASH:
                        return FLASH_STRING;
                    case CROSS:
                        return CROSS_STRING;
                    case LINKED_AWAY:
                        return LINKED_AWAY_STRING;
                    case LINKED_AWAY_RESPONSE:
                        return LINKED_AWAY_RESPONSE_STRING;
                    case OPENING:
                        return OPENING_STRING;
                    case ODD_LOT_FLASH:
                        return ODD_LOT_FLASH_STRING;
                    case ODD_LOT_RESPONSE:
                        return ODD_LOT_RESPONSE_STRING;
                    case RESTING:
                        return RESTING_STRING;
                    case CROSS_PRICE_IMP:
                        return CROSS_PRICE_IMP_STRING;
                    case FLASH_PRICE_IMP:
                        return FLASH_PRICE_IMP_STRING;
                    case FLASH_RESPONSE_PRICE_IMP:
                        return FLASH_RESPONSE_PRICE_IMP_STRING;
                    case MAKER_TURNER:
                        return MAKER_TURNER_STRING;
                    case RESTING_TURNER:
                        return RESTING_TURNER_STRING;
                    default:
                        return new StringBuffer(20).append(INVALID_BILLING_TYPE).append("[ ").append(billingType).append(" ]").toString();
                }
            }
            return INVALID_FORMAT;
        }

//*** Private Methods

        /**
         * **************************************************************************
         *  Hide the default constructor from the public interface
         */
        private BillingTypes()
        {
        }
    }
