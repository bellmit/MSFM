//
// -----------------------------------------------------------------------------------
// Source file: UserRoleFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.interfaces.presentation.user.Role;

public interface UserRoleFormatStrategy extends FormatStrategy
{
    String FULL_ROLE_NAME = "Full Role Name";
    String BRIEF_ROLE_NAME = "Brief Role Name";

    String FULL_ROLE_NAME_DESCRIPTION = "Full Role Name";
    String BRIEF_ROLE_NAME_DESCRIPTION = "Brief Role Name";

    String BROKER_DEALER_STRING = "Broker Dealer";
    String CUSTOMER_BROKER_DEALER_STRING = "Customer Broker Dealer";
    String FIRM_STRING = "Firm";
    String HELP_DESK_STRING = "Help Desk";
    String MARKET_MAKER_STRING = "Market Maker";
    String DPM_ROLE_STRING = "DPM";
    String CLASS_DISPLAY_STRING = "Class Display";
    String FIRM_DISPLAY_STRING = "Firm Display";
    String UNKNOWN_ROLE_STRING = "Unknown";
    String INVALID_ROLE_STRING = "Invalid Role";
    String EXCHANGE_BROKER_STRING = "Exchange Broker";
    String PRODUCT_MAINTENANCE_STRING = "Product Maintenance";
    String TFL_ROLE_STRING = "Trading Floor Liaison";
    String EXPECTED_OPENING_PRICE_ROLE_STRING = "Expected Opening Price Role";
    String HELPDESK_OMT_STRING = "Help Desk OMT";
    String BOOTH_OMT_STRING = "Booth OMT";
    String DISPLAY_OMT_STRING = "OMT Display";
    String CROWD_OMT_STRING = "Crowd OMT";
    String REPORTING_STRING = "Reporting";
    String OPRA_STRING = "OPRA";

    String BROKER_DEALER_BRIEF_STRING = "BD";
    String CUSTOMER_BROKER_DEALER_BRIEF_STRING = "CBD";
    String FIRM_BRIEF_STRING = "Firm";
    String HELP_DESK_BRIEF_STRING = "HD";
    String MARKET_MAKER_BRIEF_STRING = "MM";
    String DPM_ROLE_BRIEF_STRING = "DPM";
    String CLASS_DISPLAY_BRIEF_STRING = "CD";
    String FIRM_DISPLAY_BRIEF_STRING = "FD";
    String UNKNOWN_ROLE_BRIEF_STRING = "Unknown";
    String INVALID_ROLE_BRIEF_STRING = "Invalid Role";
    String EXCHANGE_BROKER_BRIEF_STRING = "EB";
    String PRODUCT_MAINTENANCE_BRIEF_STRING = "PM";
    String TFL_ROLE_BRIEF_STRING = "TFL";
    String EXPECTED_OPENING_PRICE_ROLE_BRIEF_STRING = "EOP";
    String HELPDESK_OMT_BRIEF_STRING = "HD-OMT";
    String BOOTH_OMT_BRIEF_STRING = "B-OMT";
    String DISPLAY_OMT_BRIEF_STRING = "OMT";
    String CROWD_OMT_BRIEF_STRING = "C-OMT";
    String REPORTING_BRIEF_STRING = "RPT";
    String OPRA_BRIEF_STRING = "OPRA";

    /**
     * Defines a method for formatting cmiConstant UserRole
     * @param role char to format
     * @return formatted string
     */
    String format(char role);

    /**
     * Defines a method for formatting cmiConstant UserRole
     * @param role char to format
     * @param style to use
     * @return formatted string
     */
    String format(char role, String style);

    String format(Role role);

    String format(Role role, String style);
}
